/**
 * @(#)GdsYsL3RcvSeEJBBean
 *
 * @version          V1.00
 * @author           조병기
 * @date             2014/12/22
 *
 * @description      제품(봉강,선재) 야드 L3 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2014/12/22   윤재광      조병기      최초 등록
 */
package com.inisteel.cim.ys.gds.session;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.gds.dao.GdsYsDAO;
import com.inisteel.cim.ys.common.util.YsConstant;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

/**
 *      [A] 클래스명 : 제품(봉강,선재) 야드 L3수신 처리
 *
 * @ejb.bean name="GdsYsL3RcvSeEJB" jndi-name="GdsYsL3RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class GdsYsL3RcvSeEJBBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	private YsCommUtils commUtils = new YsCommUtils();
	private YsCommDAO commDao = new YsCommDAO();
	private GdsYsDAO GdsYsDao = new GdsYsDAO();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 특수강제품 출하지시대기(DSYSJ001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvDSYSJ001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "특수강제품 출하지시대기[GdsYsL3RcvSeEJB.rcvDSYSJ001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp			= commUtils.trim(rcvMsg.getFieldString("YD_GP")); //야드구분
			String stlNo    	= commUtils.trim(rcvMsg.getFieldString("SSTL_NO")); //재료번호
			String currProgCd	= commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD")); //현재진도코드
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (!"K".equals(ydGp) ) {
				//throw new Exception("야드구분이 특수강제품창고(K)가 아닙니다!");
				commUtils.printLog(logId, "야드구분이 특수강제품창고(K)가 아닙니다!", "S+");
				return jrReturn;
			}
			if ("".equals(stlNo) ) {
				throw new Exception("잘못된 재료번호가 빈 값 입니다.");
			}
			if ("".equals(currProgCd) ) {
				throw new Exception("잘못된 현재진도코드가 빈 값 입니다.");
			}

			/**********************************************************
			* 2. 저장품 등록
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STL_APPEAR_GP"		,commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"))); 	//재료외형구분
			jrParam.setField("SSTL_NO"				,commUtils.trim(rcvMsg.getFieldString("SSTL_NO"))); 			//재료번호
			jrParam.setField("STL_PROG_CD"			,commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD")));	//현재진도코드
			jrParam.setField("ORD_YEOJAE_GP"		,commUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP")));   //주문여재구분
			jrParam.setField("ORD_NO"				,commUtils.trim(rcvMsg.getFieldString("ORD_NO")));			//주문번호
			jrParam.setField("ORD_DTL"				,commUtils.trim(rcvMsg.getFieldString("ORD_DTL")));			//주문행번
			jrParam.setField("ORD_GP"				,commUtils.trim(rcvMsg.getFieldString("ORD_GP")));			//수주구분
			jrParam.setField("CUST_CD"				,commUtils.trim(rcvMsg.getFieldString("CUST_CD")));			//고객코드
			jrParam.setField("DEST_CD"				,commUtils.trim(rcvMsg.getFieldString("DEST_CD")));			//목적지코드
			jrParam.setField("YD_DLVRDD_RULE_DD"	,commUtils.trim(rcvMsg.getFieldString("DLVRDD_RULE_DD")));	//야드납기기준일
			jrParam.setField("DEST_TEL_NO"			,commUtils.trim(rcvMsg.getFieldString("DEST_TEL_NO")));		//목적지전화번호
			jrParam.setField("DIST_SHIPASSIGN_GP"	,commUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP")));//출하배선지시구분
			jrParam.setField("MODIFIER"				,modifier); //수정자
			
			//야드목표행선지구분
			String ydAimRtGp = "";
			if(currProgCd.equals("Y")){
				ydAimRtGp =currProgCd+"C";	//재공충당대기(A후판plate)
			}else if(currProgCd.equals("G")){
				ydAimRtGp =currProgCd+"3";	//종합판정대기
			}else if(currProgCd.equals("I")){
				ydAimRtGp =currProgCd+"3";	//반송대기
			}else if(currProgCd.equals("H")){
				ydAimRtGp =currProgCd+"3";	//입고대기
			}else if(currProgCd.equals("J")){
				ydAimRtGp =currProgCd+"3";	//반납대기
			}else if(currProgCd.equals("Z")){
				ydAimRtGp =currProgCd+"3";	//제품충당대기
			}else if(currProgCd.equals("X")){
				ydAimRtGp =currProgCd+"3";	//경매대상선정
			}else if(currProgCd.equals("K")){		
				ydAimRtGp =currProgCd+"3";	//출하지시대기		
			}else if(currProgCd.equals("N")){
				ydAimRtGp =currProgCd+"3";	//운송지시대기
			}else if(currProgCd.equals("M")){
				ydAimRtGp =currProgCd+"3";	//출하완료				
			}
						
			jrParam.setField("YD_AIM_RT_GP", ydAimRtGp); //야드목표행선지구분
			jrParam.setField("PRE_AR_STAT_CD", ""); //보관매출발생상태코드 초기화
			
			//저장품 갱신
			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updDSYSJ001", logId, methodNm, "저장품 갱신");

			/**********************************************************
			* 3. 저장품제원 전문을 전송
			**********************************************************/
			jrParam.setField("YD_GP"          , ydGp); //야드구분
			jrParam.setField("YD_INFO_SYNC_CD", "5"); //야드정보동기화코드(지정저장품)

			//저장품제원 전송 Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgYSL2L002(jrParam));

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 특수강제품 운송지시대기(DSYSJ002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvDSYSJ002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "특수강제품 운송지시대기[GdsYsL3RcvSeEJB.rcvDSYSJ002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp			= commUtils.trim(rcvMsg.getFieldString("YD_GP")); //야드구분
			String stlNo    	= commUtils.trim(rcvMsg.getFieldString("SSTL_NO")); //재료번호
			String currProgCd	= commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD")); //현재진도코드
			String cancelYn		= commUtils.trim(rcvMsg.getFieldString("CANCEL_YN")); //취소유무
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (!"K".equals(ydGp) ) {
				//throw new Exception("야드구분이 특수강제품창고(K)가 아닙니다!");
				commUtils.printLog(logId, "야드구분이 특수강제품창고(K)가 아닙니다!", "S+");
				return jrReturn;
			}
			if ("".equals(stlNo) ) {
				throw new Exception("잘못된 재료번호가 빈 값 입니다.");
			}
			if ("".equals(currProgCd) ) {
				throw new Exception("잘못된 현재진도코드가 빈 값 입니다.");
			}
			if ("".equals(cancelYn) ) {
				throw new Exception("취소 유무가 빈 값 입니다.");
			}
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			if("Y".equals(cancelYn)) {
				//CANCEL_YN : Y 취소 처리
				jrParam.setField("SSTL_NO"				,commUtils.trim(rcvMsg.getFieldString("SSTL_NO"))); 			//재료번호
				
			} else {
				//CANCEL_YN : N 지시 처리

				/**********************************************************
				* 2. 저장품 등록
				**********************************************************/				
				jrParam.setField("STL_APPEAR_GP"		,commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"))); 	//재료외형구분
				jrParam.setField("SSTL_NO"				,commUtils.trim(rcvMsg.getFieldString("SSTL_NO"))); 			//재료번호
				jrParam.setField("STL_PROG_CD"			,commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD")));	//현재진도코드
				jrParam.setField("ORD_YEOJAE_GP"		,commUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP")));   //주문여재구분
				jrParam.setField("ORD_NO"				,commUtils.trim(rcvMsg.getFieldString("ORD_NO")));			//주문번호
				jrParam.setField("ORD_DTL"				,commUtils.trim(rcvMsg.getFieldString("ORD_DTL")));			//주문행번
				jrParam.setField("ORD_GP"				,commUtils.trim(rcvMsg.getFieldString("ORD_GP")));			//수주구분
				jrParam.setField("CUST_CD"				,commUtils.trim(rcvMsg.getFieldString("CUST_CD")));			//고객코드
				jrParam.setField("DEST_CD"				,commUtils.trim(rcvMsg.getFieldString("DEST_CD")));			//목적지코드
				jrParam.setField("YD_DLVRDD_RULE_DD"	,commUtils.trim(rcvMsg.getFieldString("DLVRDD_RULE_DD")));	//야드납기기준일
				jrParam.setField("DEST_TEL_NO"			,commUtils.trim(rcvMsg.getFieldString("DEST_TEL_NO")));		//목적지전화번호
				jrParam.setField("DIST_SHIPASSIGN_GP"	,commUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP")));//출하배선지시구분
				jrParam.setField("SHIPASSIGN_WORD_DATE"	,commUtils.trim(rcvMsg.getFieldString("SHIPASSIGN_WORD_DATE")));//배선작업지시일자
				jrParam.setField("SHIPASSIGN_WORD_SEQNO",commUtils.trim(rcvMsg.getFieldString("SHIPASSIGN_WORD_SEQNO")));//배선작업지시순번
				jrParam.setField("SHIP_CD"				,commUtils.trim(rcvMsg.getFieldString("SHIP_CD")));			//선박코드
				jrParam.setField("SHIP_NAME"			,commUtils.trim(rcvMsg.getFieldString("SHIP_NAME")));		//선박명
				jrParam.setField("SAILNO"				,commUtils.trim(rcvMsg.getFieldString("SAILNO")));			//선박항차
				jrParam.setField("DETAIL_ARR_CD"		,commUtils.trim(rcvMsg.getFieldString("DETAIL_ARR_CD")));			//상세착지
				jrParam.setField("YD_AIM_RT_GP"			,"N3"); //야드목표행선지구분
				jrParam.setField("MODIFIER"				,modifier); //수정자
				
				//저장품 갱신
				GdsYsDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updDSYSJ002", logId, methodNm, "저장품 갱신");
				
			}

			/**********************************************************
			* 3. 저장품제원 전문을 전송
			**********************************************************/
			jrParam.setField("YD_GP"          , ydGp); //야드구분
			jrParam.setField("YD_INFO_SYNC_CD", "5"); //야드정보동기화코드(지정저장품)

			//저장품제원 전송 Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgYSL2L002(jrParam));

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 특수강제품 선별LOT편성정보(DSYSJ003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvDSYSJ003(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "특수강제품 선별LOT편성정보[GdsYsL3RcvSeEJB.rcvDSYSJ003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			JDTORecord jrRtn 	= null;
			String sSstlNo		= "";

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp			= commUtils.trim(rcvMsg.getFieldString("YD_GP")); //야드구분
			String transOrdDate	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT")); //운송지시일자
			int itransOrdSeqNo  = Integer.parseInt(commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"))); //운송지시순번
			int iydEqpWrkSh		= Integer.parseInt(commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_SH"))); //야드설비작업매수
			
			String carLotId		= commUtils.trim(rcvMsg.getFieldString("LOT_NO")); 	//선별LOT번호
			String carLotGbn	= commUtils.trim(rcvMsg.getFieldString("LOT_GBN")); //작업구분(1:수동선별LOT편성, 2:취소)
			
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (!"K".equals(ydGp) ) {
				//throw new Exception("야드구분이 특수강제품창고(K)가 아닙니다!");
				commUtils.printLog(logId, "야드구분이 특수강제품창고(K)가 아닙니다!", "S+");
				return jrReturn;
			}
			if ("".equals(transOrdDate) ) {
				throw new Exception("운송지시일자가 빈 값 입니다.");
			}
			if (0 == itransOrdSeqNo ) {
				throw new Exception("운송지시순번이 빈 값 입니다.");
			}
			if (0 == iydEqpWrkSh ) {
				throw new Exception("재료매수가 빈 값 입니다.");
			}
			if ("".equals(carLotId) ) {
				throw new Exception("선별LOT번호가 빈 값 입니다.");
			}
			if ("".equals(carLotGbn) ) {
				throw new Exception("작업구분이 빈 값 입니다.");
			}

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
    		for(int index = 0; index < iydEqpWrkSh; index++){
    			
    			sSstlNo =  commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+(index+1))); 

    			if("".equals(sSstlNo)) break;
    			
    			/**********************************************************
    			* 2. 저장품 등록
    			**********************************************************/
    			jrParam.setResultCode(logId);							//Log ID
    			jrParam.setResultMsg(methodNm);							//Log Method Name
    			jrParam.setField("SSTL_NO"			,sSstlNo); 			//재료번호
    			jrParam.setField("TRANS_ORD_DATE"	,transOrdDate);		//운송지시일자
    			jrParam.setField("TRANS_ORD_SEQNO"	,""+itransOrdSeqNo);//운송지시순번
    			jrParam.setField("CAR_LOTID"		,carLotId);			//차량LotID
    			jrParam.setField("MODIFIER"			,modifier); 		//수정자
    			
    			if("2".equals(carLotGbn)) { //취소시 CAR_LODID 에 빈 값을 설정한다.
        			jrParam.setField("CAR_LOTID"		,"");			
    			}
    			
    			//저장품 선별Lot정보 갱신
    			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updDSYSJ003", logId, methodNm, "선별Lot정보 갱신");
    		}	
			
			/**********************************************************
			* 3. 저장품제원 전문을 전송
			**********************************************************/
			//jrParam.setField("YD_GP"          , ydGp); //야드구분
			//jrParam.setField("YD_INFO_SYNC_CD", "5"); //야드정보동기화코드(지정저장품)

			//저장품제원 전송 Data 생성
			//JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgYSL2L002(jrParam));

			if (commUtils.isEmpty(jrRtn)) {
				jrRtn = JDTORecordFactory.getInstance().create();
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
	 *      [A] 오퍼레이션명 : 특수강제품 운송상차지시DSYSJ004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvDSYSJ004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "특수강제품 운송상차지시[GdsYsL3RcvSeEJB.rcvDSYSJ004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecordSet rsChkPara	= null;
		JDTORecord recChkPara	= JDTORecordFactory.getInstance().create();
		JDTORecord jrParam	= null;
		JDTORecord recInTemp	= null;
		JDTORecord jrRtn= null;
		String sChkStlNo		= "";
		String szMsg		= "";
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String cancelYn		= commUtils.trim(rcvMsg.getFieldString("CANCEL_YN")); //취소유무
//			String cmbnCarldYn	= commUtils.trim(rcvMsg.getFieldString("CMBN_CARLD_YN")); //조합상차유무
			String stlAppearGp	= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP")); //재료외형구분
			String transOrdDate	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT")); //운송지시일자
//			String workGp		= commUtils.trim(rcvMsg.getFieldString("WORK_GP")); //작업구분
			String carNo		= commUtils.trim(rcvMsg.getFieldString("CAR_NO")); //차량번호
//			String cardNo		= commUtils.trim(rcvMsg.getFieldString("CARD_NO")); //카드번호
			
//			String telNo		= commUtils.trim(rcvMsg.getFieldString("TEL_NO")); //전화번호
			String lotNo    	= commUtils.trim(rcvMsg.getFieldString("LOT_NO")); //LOT번호
			String ydGp1		= commUtils.trim(rcvMsg.getFieldString("YD_GP1")); //야드구분
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			String returnYn		= commUtils.trim(rcvMsg.getFieldString("RETRURN_YN")); //반납여무(N이 일반출하 Y가 차량 반납)

			int itransOrdSeqNo  = Integer.parseInt(commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"))); //운송지시순번
			int iydEqpWrkSh		= Integer.parseInt(commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_SH"))); //야드설비작업매수

			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (!"K".equals(ydGp1) ) {
				//throw new Exception("야드구분이 특수강제품창고(K)가 아닙니다!");
				commUtils.printLog(logId, "야드구분이 특수강제품창고(K)가 아닙니다!", "S+");
				return jrReturn;
			}
			if ("".equals(transOrdDate) ) {
				throw new Exception("운송지시일자가 빈 값 입니다.");
			}
			if (0 == itransOrdSeqNo ) {
				throw new Exception("운송지시순번이 빈 값 입니다.");
			}
			if ("".equals(carNo) ) {
				throw new Exception("차량번호가 빈 값 입니다.");
			}
			if ("".equals(transOrdDate) ) {
				throw new Exception("운송지시일자가 빈 값 입니다.");
			}
			if (0 == iydEqpWrkSh ) {
				throw new Exception("재료매수가 빈 값 입니다.");
			}
			if ("".equals(cancelYn) ) {
				throw new Exception("취소 유무가 빈 값 입니다.");
			}

			if("Y".equals(cancelYn)) { //취소일 경우
				

	    		
	    		for(int index = 0; index < iydEqpWrkSh; index++){
	    			
	    			sChkStlNo =  commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+(index+1))); 

	    			if("".equals(sChkStlNo)) break;
	    			
	    			recChkPara.setField("SSTL_NO" , sChkStlNo);
	    			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdWrkbookmtlSTLNO 
	    			SELECT A.YD_WBOOK_ID         AS YD_WBOOK_ID
	    			      ,A.DEL_YN              AS DEL_YN
	    			      ,A.YD_GP               AS YD_GP
	    			      ,A.YD_BAY_GP           AS YD_BAY_GP
	    			      ,A.YD_SCH_CD           AS YD_SCH_CD
	    			      ,A.YD_SCH_PRIOR        AS YD_SCH_PRIOR
	    			      ,A.YD_SCH_PROG_STAT    AS YD_SCH_PROG_STAT
	    			      ,A.YD_SCH_ST_GP        AS YD_SCH_ST_GP
	    			      ,A.YD_SCH_REQ_GP       AS YD_SCH_REQ_GP
	    			      ,A.YD_AIM_YD_GP        AS YD_AIM_YD_GP
	    			      ,A.YD_AIM_BAY_GP       AS YD_AIM_BAY_GP
	    			      ,A.YD_CTS_RELAY_YN     AS YD_CTS_RELAY_YN
	    			      ,A.YD_CTS_RELAY_BAY_GP AS YD_CTS_RELAY_BAY_GP
	    			      ,A.YD_TO_LOC_DCSN_MTD  AS YD_TO_LOC_DCSN_MTD
	    			      ,A.YD_TO_LOC_GUIDE     AS YD_TO_LOC_GUIDE
	    			      ,B.SSTL_NO             AS SSTL_NO
	    			      ,B.REGISTER            AS REGISTER
	    			      ,TO_CHAR(B.REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
	    			      ,B.MODIFIER            AS MODIFIER
	    			      ,TO_CHAR(B.MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
	    			      ,B.YS_STK_COL_GP       AS YS_STK_COL_GP
	    			      ,B.YS_STK_BED_NO       AS YS_STK_BED_NO
	    			      ,B.YS_STK_LYR_NO       AS YS_STK_LYR_NO
	    			      ,B.YS_STK_SEQ_NO       AS YS_STK_SEQ_NO
	    			      ,B.YD_UP_COLL_SEQ      AS YD_UP_COLL_SEQ
	    			      ,to_char(SYSDATE,'yyyymmddhh24miss')  AS DB_DATE
	    			  FROM TB_YS_WRKBOOK    A
	    			      ,TB_YS_WRKBOOKMTL B
	    			 WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
	    			   AND B.SSTL_NO = :V_SSTL_NO
	    			   AND (A.DEL_YN <> 'Y' OR A.DEL_YN IS NULL)
	    			   AND (B.DEL_YN <> 'Y' OR B.DEL_YN IS NULL)
 	    			
	    			
	    			*/
	    			rsChkPara = commDao.select(recChkPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdWrkbookmtlSTLNO", logId, methodNm, "재료 정보 조회");
	    			if (rsChkPara.size() > 0) {    
						throw new Exception("상차지시 취소시 이미 작업예약에 재료 존재함");
					}
	    		}
				 
	    		/*
	    		 * 취소처리 : 운송지시,순번,차량번호가 일치하는 것만 취소처리 한다.
	    		 */
	    		JDTORecord recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("TRANS_ORD_DATE"	, transOrdDate);
				recPara.setField("TRANS_ORD_SEQNO"	, ""+itransOrdSeqNo);
				recPara.setField("CAR_NO"	, carNo);
	    		
	    		for(int index = 0; index < iydEqpWrkSh; index++){
	    			
	    			sChkStlNo =  commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+(index+1))); 
					recPara.setField("SSTL_NO"	, sChkStlNo);
					
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStockDSYSJ004CNL 
					 UPDATE TB_YS_STOCK
					    SET TRANS_ORD_DATE   = ''
					      , TRANS_ORD_SEQNO  = ''
					      , YD_CAR_UPP_LOC_CD= ''
					      , MODIFIER         = 'DSYSJ004'
					      , MOD_DDTT         = SYSDATE
					      , CAR_NO           = ''
					      , CARD_NO          = ''
					      , CAR_LOTID        = ''
					      , CAR_LOTID_REG_DDTT =  NULL 
					      , YS_STK_BED_NO    = 'TR'  
					    WHERE STL_NO    = :V_STL_NO
					    AND   TRANS_ORD_DATE = :V_TRANS_ORD_DATE
					    AND   TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
					    AND   NVL(CAR_NO,'-') LIKE NVL(:V_CAR_NO,'-')||'%'
					*/
					
	    			commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStockDSYSJ004CNL", logId, methodNm, "저장품 갱신");
	    			
	    			/**********************************************************
	    		     * 선별LOT편성정보 취소
	    		     *
	    		     * 출하에서  선별Lot편성취소  정보가 수신되면  해당 저장위치 상태변경
	    		     *	-- LOT정보가 있으면 그대로 나둠.
						-- LOT정보가 없을경우
    						-- 야드베드입출고상태  : 가적베드 : 가적베드(G) , 일반베드 : 입출고가능베드(E)
    						-- 선별상태            : 가적베드 : 선별완료(F) , 일반베드 : 미선별출하(N)
	    		     **********************************************************************/
	    			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedStsCd_02 
	    			UPDATE  /*+ BYPASS_UJVC 
	    			(
	    			SELECT A.YS_STK_COL_GP,
	    			       A.YS_STK_BED_NO,
	    			       A.YD_STK_BED_WHIO_STAT,
	    			       A.YD_STK_BED_SEL_GP,
	    			       B.LOT_CNT
	    			FROM TB_YS_STKBED A, 
	    			    (
	    			    SELECT A.YS_STK_COL_GP,
	    			           A.YS_STK_BED_NO,
	    			           SUM(DECODE(C.CAR_LOTID,NULL,0,1)) AS LOT_CNT
	    			    FROM TB_YS_STKLYR A,
	    			         TB_YS_STOCK C
	    			    WHERE A.SSTL_NO = C.SSTL_NO
	    			      AND (A.YS_STK_COL_GP,A.YS_STK_BED_NO) 
	    			           IN 
	    			          (
	    			           SELECT DISTINCT YS_STK_COL_GP,YS_STK_BED_NO 
	    			             FROM TB_YS_STKLYR 
	    			            WHERE SSTL_NO = :V_SSTL_NO
	    			              AND YD_STK_LYR_MTL_STAT IN ('C','U')
	    			          )
	    			    GROUP BY A.YS_STK_COL_GP,  
	    			             A.YS_STK_BED_NO
	    			    )B
	    			WHERE A.YS_STK_COL_GP = B.YS_STK_COL_GP
	    			  AND A.YS_STK_BED_NO = B.YS_STK_BED_NO   
	    			)A
	    			-- LOT정보가 있으면 그대로 나둠.
	    			-- LOT정보가 없을경우
	    			    -- 야드베드입출고상태  : 가적베드 : 가적베드(G) , 일반베드 : 입출고가능베드(E)
	    			    -- 선별상태            : 가적베드 : 선별완료(F) , 일반베드 : 미선별출하(N)
	    			SET A.YD_STK_BED_WHIO_STAT = DECODE(A.LOT_CNT,0,DECODE(A.YD_STK_BED_WHIO_STAT,'G',A.YD_STK_BED_WHIO_STAT,'E'),A.YD_STK_BED_WHIO_STAT),
	    			    A.YD_STK_BED_SEL_GP    = DECODE(A.LOT_CNT,0,DECODE(A.YD_STK_BED_WHIO_STAT,'G','F','N'),A.YD_STK_BED_SEL_GP)
	    			   */ 
	    			
	    			commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedStsCd_02", logId, methodNm, "적치BED 갱신");
	    		}
//sjh 2015.08.24
//취소시 입동대기면 carsch 삭제처리 추가 	    		
				
				//---------------------------------------------------------------------------------
				//	출하운송지시 상차정보가 중복해서 수신될경우 SKIP처리한다. 
				//---------------------------------------------------------------------------------
				recChkPara 	= JDTORecordFactory.getInstance().create();
				recChkPara.setField("TRANS_ORD_DATE"	, transOrdDate);
				recChkPara.setField("TRANS_ORD_SEQNO"	, ""+itransOrdSeqNo);
				recChkPara.setField("CAR_NO"			, carNo );
				recChkPara.setField("DEL_YN"			, "N" );
				
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchDelYn 
				SELECT YD_CAR_SCH_ID ,YD_CAR_PROG_STAT
				  FROM TB_YS_CARSCH
				 WHERE TRANS_ORD_DATE = :V_TRANS_ORD_DATE
				   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				   AND CAR_NO  = :V_CAR_NO
				   AND DEL_YN   = :V_DEL_YN
				ORDER BY YD_CAR_SCH_ID DESC   
				*/
				rsChkPara = commDao.select(recChkPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchDelYn", logId, methodNm, "차량스케줄 조회");
				if (rsChkPara.size() > 0) {
					
					rsChkPara.first();
					JDTORecord recTemp		= rsChkPara.getRecord();
					
					String szYD_CAR_PROG_STAT	= commUtils.trim(recTemp.getFieldString("YD_CAR_PROG_STAT"));			
					String szYD_CAR_SCH_ID		= commUtils.trim(recTemp.getFieldString("YD_CAR_SCH_ID"));			
					
					if(szYD_CAR_PROG_STAT.equals("")||szYD_CAR_PROG_STAT.equals("0")||szYD_CAR_PROG_STAT.equals("1")) {

						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("YD_CAR_SCH_ID" , szYD_CAR_SCH_ID);
						recInTemp.setField("MODIFIER"      , "DSYSJ004");
						
						/*com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch 
						UPDATE TB_YS_CARSCH
						   SET MODIFIER = :V_MODIFIER
						     , MOD_DDTT = SYSDATE
						     , DEL_YN = 'Y'
						 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
						     AND DEL_YN = 'N'
		                */  
						commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch", logId, methodNm, "차량스케줄 삭제");						
					}
				}
	    		
			} else {
				//CANCEL_YN : N 지시 처리	
			
			
				//---------------------------------------------------------------------------------
				//	출하운송지시 상차정보가 중복해서 수신될경우 SKIP처리한다. 
				//---------------------------------------------------------------------------------
				recChkPara 	= JDTORecordFactory.getInstance().create();
				recChkPara.setField("TRANS_ORD_DATE"	, transOrdDate);
				recChkPara.setField("TRANS_ORD_SEQNO"	, ""+itransOrdSeqNo);
				recChkPara.setField("CAR_NO"			, carNo );
				recChkPara.setField("DEL_YN"			, "N" );
				
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchDelYn 
				SELECT YD_CAR_SCH_ID , YD_CAR_PROG_STAT
				  FROM TB_YS_CARSCH
				 WHERE TRANS_ORD_DATE = :V_TRANS_ORD_DATE
				   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				   AND CAR_NO  = :V_CAR_NO
				   AND DEL_YN   = :V_DEL_YN
				ORDER BY YD_CAR_SCH_ID DESC   
				*/
				rsChkPara = commDao.select(recChkPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchDelYn", logId, methodNm, "차량스케줄 조회");
				if (rsChkPara.size() > 0) {
					throw new Exception("차량스케줄이 편성되어 있습니다");
				}
	
				/**********************************************************
				* 2. 저장품 등록
				**********************************************************/
				jrParam	= JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("STL_APPEAR_GP", 		stlAppearGp);
				jrParam.setField("TRANS_ORD_DATE", 		transOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO", 	""+itransOrdSeqNo);
				jrParam.setField("CAR_NO", 				carNo);
				
				if(!"".equals(lotNo)) {
					jrParam.setField("CAR_LOTID", 			lotNo);
				}
				jrParam.setField("MODIFIER", 				msgId);
				
				for(int i = 1 ; i<= iydEqpWrkSh; i++){
	
					sChkStlNo =  commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+i));
					
					if(sChkStlNo.equals("")){
						break;
					}
					jrParam.setField("SSTL_NO",	sChkStlNo);
					jrParam.setField("STL_PROG_CD",  "L");  //운송대기	
	
//					rsChkPara = JDTORecordFactory.getInstance().createRecordSet("Temp");
//					rsChkPara = commDao.select(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.getYdStkSpstFrToMoveGp_PIDEV", logId, methodNm, "반납반송구분");
//					if (rsChkPara.size() > 0) {
//						jrParam.setField("SPST_FRTOMOVE_GP",  rsChkPara.getRecord(0).getFieldString("SPST_FRTOMOVE_GP"));
//					} else {
//						jrParam.setField("SPST_FRTOMOVE_GP",  "");
//					}
					
					//반납출하 여부
					if("Y".equals(returnYn)){
						jrParam.setField("SPST_FRTOMOVE_GP",  "21");
					}else{
						jrParam.setField("SPST_FRTOMOVE_GP",  "");
					}
					
					/* 저장품 갱신 -- com.inisteel.cim.ys.gds.dao.GdsYsDAO.updDSYSJ004 

					UPDATE TB_YS_STOCK
					SET    STL_APPEAR_GP  = NVL(:V_STL_APPEAR_GP ,STL_APPEAR_GP)
					      ,STL_PROG_CD    = NVL(:V_STL_PROG_CD ,STL_PROG_CD)
					      ,TRANS_ORD_DATE = NVL(:V_TRANS_ORD_DATE ,TRANS_ORD_DATE)
					      ,TRANS_ORD_SEQNO = NVL(:V_TRANS_ORD_SEQNO ,TRANS_ORD_SEQNO)
					      ,CAR_NO = NVL(:V_CAR_NO ,CAR_NO)
					      ,CAR_LOTID = NVL(:V_CAR_LOTID ,CAR_LOTID)
					      ,MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE 
					      ,SPST_FRTOMOVE_GP =  :V_SPST_FRTOMOVE_GP 
					WHERE  SSTL_NO = :V_SSTL_NO   
					*/
					GdsYsDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updDSYSJ004", logId, methodNm, "저장품 갱신");
	
				}
				
				
				/**********************************************************
				* 2. 야드핸들링정보 송신
				**********************************************************/
				
				rsChkPara = JDTORecordFactory.getInstance().createRecordSet("Temp");
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getHandlingCnt 
				SELECT A.YS_STK_COL_GP
				     , A.YS_STK_BED_NO
				     , A.YS_STK_LYR_NO
				     , A.HANDLING_CNT 
				     , A.GATE
				     , B.YD_CARPNT_CD
				  FROM (
				        SELECT C.YS_STK_COL_GP
				             , C.YS_STK_BED_NO
				             , C.YS_STK_LYR_NO
				             , COUNT(*) OVER () HANDLING_CNT
				             , SUBSTR(C.YS_STK_COL_GP,1,2)||'TR'||( CASE WHEN SUBSTR(C.YS_STK_COL_GP , 3, 2) IN ('01','02','03') THEN '2' ELSE '4' END )||'1'  AS GATE
				          FROM TB_YS_STKLYR C
				              ,(
				                    SELECT BB.YS_STK_COL_GP
				                          ,BB.YS_STK_BED_NO
				                          ,MIN(BB.YS_STK_LYR_NO) AS  YS_STK_LYR_NO
				                          ,MAX(AA.TRANS_ORD_DATE) AS TRANS_ORD_DATE
				                          ,MAX(AA.TRANS_ORD_SEQNO) AS TRANS_ORD_SEQNO
				                    FROM   TB_YS_STOCK AA
				                          ,TB_YS_STKLYR BB
				                    WHERE  AA.TRANS_ORD_DATE = :V_TRANS_ORD_DATE
				                    AND    AA.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				                    AND    AA.SSTL_NO = BB.SSTL_NO
				                    AND    BB.YD_STK_LYR_MTL_STAT IN ('C','D')
				                    GROUP BY BB.YS_STK_COL_GP, BB.YS_STK_BED_NO
				               ) D
				              ,TB_YS_STOCK E
				        WHERE  C.YS_STK_COL_GP = D.YS_STK_COL_GP     
				        AND    C.YS_STK_BED_NO = D.YS_STK_BED_NO
				        AND    C.YS_STK_LYR_NO >= D.YS_STK_LYR_NO
				        AND    C.YD_STK_LYR_MTL_STAT IN ('C','D')
				        AND    C.SSTL_NO = E.SSTL_NO
				        GROUP BY C.YS_STK_COL_GP, C.YS_STK_BED_NO ,C.YS_STK_LYR_NO
				       
				     ) A 
				     , TB_YS_CARPOINT B
				WHERE  A.GATE = B.YD_STK_COL_GP(+)  
				ORDER BY A.YS_STK_COL_GP, A.YS_STK_BED_NO, A.YS_STK_LYR_NO DESC
				*/
				rsChkPara = commDao.select(recChkPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getHandlingCnt", logId, methodNm, "차량스케줄 조회");
				if (rsChkPara.size() > 0) {
	
					szMsg="출하제품핸들링횟수[반환값 : " + rsChkPara.size() + "]";
					commUtils.printLog(logId, szMsg, "S+");
					
					recInTemp = JDTORecordFactory.getInstance().create();
					
					for( int i = 1; i <= rsChkPara.size(); i++ ) {
						jrParam = JDTORecordFactory.getInstance().create();
						rsChkPara.absolute(i);					
						jrParam = rsChkPara.getRecord();
	
						sChkStlNo =  commUtils.trim(jrParam.getFieldString("HANDLING_CNT"+i));
						
						
						recInTemp.setResultCode(logId);	//Log ID
						recInTemp.setResultMsg(methodNm);	//Log Method Name
						
						
						// PIDEV
//						String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "K", "*");
						
//						if("Y".equals(sApplyYnPI)) {

							recInTemp.setField("MQ_TC_CD"			, "M10YDLMJ1054"); 	//특수강야드핸들링정보
							recInTemp.setField("MQ_TC_CREATE_DDTT"	, commUtils.getDateTime14()); 	// TC생성일시
							recInTemp.setField("YD_GP"				, ydGp1); 	// 야드구분
							recInTemp.setField("DIST_GOODS_GP"		, "R"); 	// 출하제품구분
							recInTemp.setField("CAR_NO"				, carNo);	//차량번호							
							recInTemp.setField("TRN_REQ_DATE"		, transOrdDate); 			// 운송의뢰일자
							recInTemp.setField("TRN_REQ_SEQ"		, ""+itransOrdSeqNo);	// 운송의뢰순번														
							recInTemp.setField("YD_CARPNT_CD"		, commUtils.trim(jrParam.getFieldString("YD_CARPNT_CD")));  //야드차량포인트코드
							recInTemp.setField("HANDLING_CNT"		, commUtils.trim(jrParam.getFieldString("HANDLING_CNT")));	//핸들링횟수							
							
//						} else {
//						
//							recInTemp.setField("JMS_TC_CD"			,"YSDSJ010"); 	//특수강야드핸들링정보
//							recInTemp.setField("YD_GP"				,ydGp1); 	//재료외형구분
//							recInTemp.setField("TRANS_WORD_DATE"	,transOrdDate); 			//재료번호
//							recInTemp.setField("TRANS_WORD_SEQNO"	,""+itransOrdSeqNo);	//현재진도코드
//							recInTemp.setField("YD_CARPNT_CD"		,commUtils.trim(jrParam.getFieldString("YD_CARPNT_CD")));   //주문여재구분
//							recInTemp.setField("CAR_NO"				,carNo);			//주문번호
//							recInTemp.setField("HANDLING_CNT"		,commUtils.trim(jrParam.getFieldString("HANDLING_CNT")));			//주문행번
//						
//						}
						
						jrRtn = commUtils.addSndData(recInTemp);
			 
						//동일TC 여러번 보내기
						EJBConnector rcvConn = new EJBConnector("default", "YsCommEJB", this);						
						JDTORecord jrRst = (JDTORecord)rcvConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
					}
				}
			}	
			commUtils.printLog(logId, methodNm, "S-");

			jrRtn = JDTORecordFactory.getInstance().create();
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 특수강제품 2냉연야드 상차완료정보(DSYSJ012)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvDSYSJ012(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "특수강제품 2냉연야드 상차완료정보[GdsYsL3RcvSeEJB.rcvDSYSJ012] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecordSet rsChkPara	= null;
		JDTORecord recChkPara	= JDTORecordFactory.getInstance().create();
		JDTORecord jrParam	= null;
		JDTORecord recInTemp	= null;
		JDTORecord jrRtn= null;
		String sChkStlNo		= "";
		String szMsg		= "";
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String transOrdDate	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT")); //운송지시일자
			String carNo		= commUtils.trim(rcvMsg.getFieldString("CAR_NO")); //차량번호
			String ydGp1		= commUtils.trim(rcvMsg.getFieldString("YD_GP1")); //야드구분
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			
			int itransOrdSeqNo  = Integer.parseInt(commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"))); //운송지시순번
			int iydEqpWrkSh		= Integer.parseInt(commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_SH"))); //야드설비작업매수

			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (!"G".equals(ydGp1) ) {
				commUtils.printLog(logId, "야드구분이 특수강제품창고 2냉연야드(G)가 아닙니다!", "S+");
				return jrReturn;
			}
			if ("".equals(transOrdDate) ) {
				throw new Exception("운송지시일자가 빈 값 입니다.");
			}
			if (0 == itransOrdSeqNo ) {
				throw new Exception("운송지시순번이 빈 값 입니다.");
			}
			if ("".equals(carNo) ) {
				throw new Exception("차량번호가 빈 값 입니다.");
			}
			if (0 == iydEqpWrkSh ) {
				throw new Exception("재료매수가 빈 값 입니다.");
			}
			
			/**********************************************************
			* 2. 저장품 등록
			**********************************************************/
			jrParam	= JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("TRANS_ORD_DATE", 		transOrdDate);
			jrParam.setField("TRANS_ORD_SEQNO", 	""+itransOrdSeqNo);
			jrParam.setField("YD_GP", 				ydGp1);
			jrParam.setField("CAR_NO", 				carNo);
			jrParam.setField("REGISTER", 			msgId);
			
			for(int i = 1 ; i<= iydEqpWrkSh; i++){

				sChkStlNo =  commUtils.trim(rcvMsg.getFieldString("STL_NO"+i));
				
				if(sChkStlNo.equals("")){
					break;
				}
				jrParam.setField("SSTL_NO",	sChkStlNo);
				
				GdsYsDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updDSYSJ012", logId, methodNm, "저장품 등록");

			}
			
			commUtils.printLog(logId, methodNm, "S-");

			jrRtn = JDTORecordFactory.getInstance().create();
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 특수강제품 대기장도착실적(DSYSJ005)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvDSYSJ005(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "특수강제품 대기장도착실적[GdsYsL3RcvSeEJB.rcvDSYSJ005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			String szYD_CAR_SCH_ID = null;
			/*
			 * WORK_GP  : 1-내수,2-수출,3-연안해송,6-HYSCO대차,7-철도운송,4-제품이송,5-소재이송
			 * ISSUE_GP : 0-일반,4-고객출하,5-SAMPLE,6-사급재,9-사외이송,G-가공이송,F-차량반송 
			 *            Y-당진사외이송(YD_CAR_WRK_GP항목에 관리)
 			 */
			//수신 항목 값
			String msgId    		= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp				= commUtils.trim(rcvMsg.getFieldString("YD_GP")); 			//야드구분
			String cmbnCarldYn 		= commUtils.trim(rcvMsg.getFieldString("CMBN_CARLD_YN")); 	//조합상차유무
			String workGp    		= commUtils.trim(rcvMsg.getFieldString("WORK_GP")); 		//작업구분
			String issueGp    		= commUtils.trim(rcvMsg.getFieldString("ISSUE_GP")); 		//작업구분
			String telNo    		= commUtils.trim(rcvMsg.getFieldString("TEL_NO")); 			//전화번호
			String transOrdDt  		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT")); 	//운송지시일자
			String transOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")); //운송지시순번
			String carNo  			= commUtils.trim(rcvMsg.getFieldString("CAR_NO")); 			//차량번호
			String cardNo  			= commUtils.trim(rcvMsg.getFieldString("CARD_NO"));			//카드번호
			String waitArrDdtt		= commUtils.trim(rcvMsg.getFieldString("WAIT_ARR_DDTT"));	//도착시간
			String waitArrGp		= commUtils.trim(rcvMsg.getFieldString("WAIT_ARR_GP"));		//도착구분
			String modifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			
			/*
			 *  복수동 출하시 나머지 출하작업에 대한 상태 = 'Y'
			 *  -- 권하처리시 선 복수동 작업일때 해당항목을 셋팅
			 */
			String szDOUBLEDONG_CHECK  			= commUtils.nvl(rcvMsg.getFieldString("DOUBLEDONG_CHECK"),"N");
			String szDOUBLEDONG_YD_CAR_SCH_ID 	= commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"));
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (!"K".equals(ydGp) ) {
				//throw new Exception("야드구분이 특수강제품창고(K)가 아닙니다!");
				commUtils.printLog(logId, "야드구분이 특수강제품창고(K)가 아닙니다!", "S+");
				return jrReturn;
			}
			if ("".equals(transOrdDt) ) {
				throw new Exception("운송지시일자가 빈 값 입니다.");
			}
			if ("".equals(transOrdSeqNo) ) {
				throw new Exception("운송지시순번이 빈 값 입니다.");
			}
			
			/**********************************************************
			* 2. 차량스케줄 편성 여부 Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("TRANS_ORD_DATE"   , transOrdDt  ); 	//운송지시일자
			jrParam.setField("TRANS_ORD_SEQNO"  , transOrdSeqNo  ); //운송지시순번
			jrParam.setField("MODIFIER"         , modifier       ); //수정자

			/* 차량스케줄ID 조회 -- com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchId 
			SELECT YD_CAR_SCH_ID 
			  FROM TB_YS_CARSCH
			 WHERE TRANS_ORD_DATE = :V_TRANS_ORD_DATE
			   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			   AND DEL_YN   = 'N'
			ORDER BY YD_CAR_SCH_ID DESC   
			*/
			
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchId", logId, methodNm, "차량스케줄ID 조회");

			if (jsChk.size() > 0) {
				throw new Exception("차량스케줄이 이미 편성되어 있습니다!!");
			}

			/*************************************************************
			* 3. 운송지시일자 순번으로 대상 제품의 위치를 조회하여 입동포인트를 가져온다.
			*************************************************************/
			jrParam.setField("TRANS_ORD_DATE"   , transOrdDt  ); 	//운송지시일자
			jrParam.setField("TRANS_ORD_SEQNO"  , transOrdSeqNo  ); //운송지시순번
			jrParam.setField("YD_GP"         	, ydGp       ); 	//야드구분

			
			/* 도착가능 차량포인트 조회 -- com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarPntCd  
			SELECT CP.YD_STK_COL_GP AS YS_STK_COL_GP
			      ,CP.YD_CARPNT_CD
			      ,CP.YD_PNT_CD
			      ,CP.WLOC_CD
			      ,DECODE(CP.YD_STK_COL_ACT_STAT,'C',1,'N',3,2) AS STAT_RANK
			      ,(SELECT COUNT(*)
			          FROM TB_YS_CARSCH 
			         WHERE DEL_YN='N'
			           AND YD_CARLD_STOP_LOC=CP.YD_STK_COL_GP) AS CARLD_RANK
			      ,DD.CNT    
			      ,DD.YD_BAY_GP
			      ,DD.AUTO_SUP_YN
			      ,CASE WHEN DD.AUTO_SUP_YN = 'C' THEN '1'
			            ELSE DECODE(DD.AUTO_SUP_YN,DD.YD_BAY_GP,'1','2')
			       END AS RULE_SEQ     
			FROM   TB_YD_CARPOINT CP
			      ,(
			      
			            SELECT YD_GP
			                  ,YD_BAY_GP
			                  ,COUNT(*) AS CNT
			                  ,(SELECT ITEM FROM TB_YS_RULE WHERE REPR_CD_GP = 'KXTR01') AS AUTO_SUP_YN
			            FROM   (
			                        SELECT  ST.TRANS_ORD_DATE
			                               ,ST.TRANS_ORD_SEQNO
			                               ,ST.SSTL_NO
			                               ,LY.YD_GP
			                               ,SUBSTR(LY.YS_STK_COL_GP,2,1) AS YD_BAY_GP
			                               ,LY.YS_STK_COL_GP
			                        FROM    TB_YS_STOCK ST
			                               ,TB_YS_STKLYR LY
			                        WHERE   ST.TRANS_ORD_DATE = :V_TRANS_ORD_DATE
			                        AND     ST.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			                        AND     ST.SSTL_NO = LY.SSTL_NO
			                        AND     LY.YS_STK_COL_GP LIKE :V_YD_GP || '%'
			
			                   ) 
			                   
			            GROUP BY TRANS_ORD_DATE
			                    ,TRANS_ORD_SEQNO
			                    ,YD_GP
			                    ,YD_BAY_GP      
			       ) DD
			WHERE  CP.YD_GP     = DD.YD_GP
			AND    CP.YD_BAY_GP = DD.YD_BAY_GP
			AND    CP.YD_CAR_USETYPE_GP IN('TR', 'RT','RA','TO') --트레일러
			AND    CP.DEL_YN    ='N'
			--AND    CP.YD_STK_COL_ACT_STAT <>'N'
			AND    CP.YD_STK_COL_GP <> 'KBTR21'  -- 'KBTR21:봉강 입고 전용'
			ORDER BY RULE_SEQ
			        ,SUBSTR(YD_CARPNT_CD,3,1) 
			        ,STAT_RANK
			        ,CARLD_RANK
			        ,CNT 
			        ,YS_STK_COL_GP

			*/        
			JDTORecordSet jsPnt = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarPntCd", logId, methodNm, "도착가능 차량포인트 조회");
			
			if (jsPnt.size() <= 0) {
				throw new Exception("도착가능 차량포인트가 없습니다.");
			}
			
			jsPnt.first();
			JDTORecord jrCarPnt = jsPnt.getRecord();
			
			String szYS_STK_COL_GP 	= commUtils.trim(jrCarPnt.getFieldString("YS_STK_COL_GP" ));
			String szYD_CARPNT_CD 	= commUtils.trim(jrCarPnt.getFieldString("YD_CARPNT_CD" ));
			String szYD_PNT_CD 		= commUtils.trim(jrCarPnt.getFieldString("YD_PNT_CD" ));
			String szWLOC_CD 		= commUtils.trim(jrCarPnt.getFieldString("WLOC_CD" ));
			
			
			/**********************************************************
			* 4. 차량스케줄을 생성(수정) 한다.
			**********************************************************/
			if("Y".equals(szDOUBLEDONG_CHECK)) { //복수동일 경우 
				
				/*
				 * 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 초기화 수정한다.
				 */
				jrParam.setField("YD_CAR_SCH_ID"	, szDOUBLEDONG_YD_CAR_SCH_ID); 
				jrParam.setField("YD_EQP_WRK_STAT"	, "U");									//야드설비작업상태
				jrParam.setField("YD_EQP_ID"		, "XXPT02");			//야드설비ID
				jrParam.setField("YD_CAR_USE_GP"	, "G");			//차량사용구분
				jrParam.setField("SPOS_WLOC_CD"		, szWLOC_CD);							//발지개소코드
				jrParam.setField("YD_PNT_CD1"		, szYD_PNT_CD);							//야드포인트코드1
				jrParam.setField("CAR_NO"			, carNo);								//차량번호
				jrParam.setField("CARD_NO"			, cardNo);								//카드번호
				jrParam.setField("TRANS_ORD_DATE"   , transOrdDt  ); 						//운송지시일자
				jrParam.setField("TRANS_ORD_SEQNO"  , transOrdSeqNo  ); 					//운송지시순번
				//jrParam.setField("YD_CARLD_LEV_DT"	, YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
				jrParam.setField("YD_CARLD_STOP_LOC", szYS_STK_COL_GP);						//차량상차정지위치
				jrParam.setField("YD_CAR_PROG_STAT"	, YsConstant.YD_CARLD_LEV);				//상차출발상태
				jrParam.setField("YD_BAYIN_WO_SEQ"	, "2");									//입동지시순번  (2013.06.20 cho 1순위 --> 2순위 변경)
				jrParam.setField("DEL_YN"			, "N");					
	    		jrParam.setField("DEST_TEL_NO"		, telNo);								//기사핸드폰번호
	    		jrParam.setField("CMBN_CARLD_YN"	, cmbnCarldYn);							//첫번째 도착창고 : S 두번째 도착창고 : E
	    		jrParam.setField("WAIT_ARR_DDTT"	, waitArrDdtt);							//대기장도착시간
	    		jrParam.setField("WAIT_ARR_GP"		, waitArrGp);							//대기장도착구분  - B:BACKUP , S:SMARTPHONE		
	    		jrParam.setField("YD_CAR_WRK_GP"	, issueGp);								//당진사외이송구분(Y)
	    		 
				//차량스케줄수정
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDSYSJ005", logId, methodNm, "차량스케줄수정");
		    	
			} else { //복수동이 아닐 경우  
				
				/*
				 * 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
				 */
				szYD_CAR_SCH_ID = commDao.getSeqId(logId, methodNm, "CarSch");
				jrParam.setField("YD_CAR_SCH_ID"	, szYD_CAR_SCH_ID);
				jrParam.setField("YD_EQP_WRK_STAT"	, "U");									//야드설비작업상태
				jrParam.setField("YD_EQP_ID"		, "XXPT02");							//야드설비ID
				jrParam.setField("YD_CAR_USE_GP"	, "G");									//차량사용구분
				jrParam.setField("CAR_KIND"			, "TR");
				jrParam.setField("SPOS_WLOC_CD"		, szWLOC_CD);							//발지개소코드
				jrParam.setField("YD_PNT_CD1"		, szYD_PNT_CD);							//야드포인트코드1
				jrParam.setField("CAR_NO"			, carNo);								//차량번호
				jrParam.setField("CARD_NO"			, cardNo);								//카드번호
				jrParam.setField("TRANS_ORD_DATE"   , transOrdDt  ); 						//운송지시일자
				jrParam.setField("TRANS_ORD_SEQNO"  , transOrdSeqNo  ); 					//운송지시순번
				jrParam.setField("YD_CARLD_STOP_LOC", szYS_STK_COL_GP);						//차량상차정지위치
		    	jrParam.setField("YD_BAYIN_WO_SEQ"	, "9");	//입동지시순번 --기본값으로 설정(9) **
	    		jrParam.setField("YD_CAR_PROG_STAT"	, YsConstant.YD_CARLD_LEV);				//상차출발상태
				
	    		jrParam.setField("DEST_TEL_NO"		, telNo);								//기사핸드폰번호
	    		jrParam.setField("CMBN_CARLD_YN"	, cmbnCarldYn);							//첫번째 도착창고 : S 두번째 도착창고 : E
	    		jrParam.setField("WAIT_ARR_DDTT"	, waitArrDdtt);							//대기장도착시간
	    		jrParam.setField("WAIT_ARR_GP"		, waitArrGp);							//대기장도착구분  - B:BACKUP , S:SMARTPHONE
	    		jrParam.setField("YD_CAR_WRK_GP"	, issueGp);								//당진사외이송구분(Y)
				
	    		//차량스케줄 등록
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insDSYSJ005", logId, methodNm, "차량스케줄등록");
				
			}

			/**********************************************************
			* 5. 차량입동지시요구를 호출한다.
			**********************************************************/
			JDTORecord jrRtn = null;
			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("JMS_TC_CD"			, "YSYSJ801");          		//차량입동지시 요구 기존:YDYDJ662
			jrParam.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); 	//JMSTC생성일시
			jrParam.setField("YD_CARPNT_CD"			, szYD_CARPNT_CD);				//입동포인트
			jrParam.setField("YD_CAR_SCH_ID"		, szYD_CAR_SCH_ID);	        	//차량스케줄ID (선택항목)
			jrParam.setField("CAR_NO" 				, carNo);
			jrParam.setField("CARD_NO"				, cardNo);
			jrParam.setField("YD_CAR_STOP_LOC"		, szYS_STK_COL_GP);
			
			//----------------------------------------------------------------------------------
			//	동기화 문제로 인하여 JMS --> EJB Call로 변경
			//	수정자 : 윤재광
			//	수정일 : 1) 2017.10.26 - 최초등록
			//----------------------------------------------------------------------------------
			//jrRtn = commUtils.addSndData(jrRtn, jrParam);	
			
			EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
    		jrRtn = (JDTORecord)ejbConn.trx("procCarBayInOrdReq", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	

	/**
	 *      [A] 오퍼레이션명 : 특수강제품 출하완료(DSYSJ006)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvDSYSJ006(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "특수강제품 출하완료[GdsYsL3RcvSeEJB.rcvDSYSJ006] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp			= commUtils.trim(rcvMsg.getFieldString("YD_GP")); //야드구분
			String stlAppearGp 	= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP")); //재료외형
			String stlNo    	= commUtils.trim(rcvMsg.getFieldString("SSTL_NO")); //재료번호
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (!"K".equals(ydGp) ) {
				//throw new Exception("야드구분이 특수강제품창고(K)가 아닙니다!");
				commUtils.printLog(logId, "야드구분이 특수강제품창고(K)가 아닙니다!", "S+");
				return jrReturn;
			}
			if ("".equals(stlNo) ) {
				throw new Exception("잘못된 재료번호가 빈 값 입니다.");
			}
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			JDTORecord recStock = JDTORecordFactory.getInstance().create();
			JDTORecord recCrnwrkmtl = JDTORecordFactory.getInstance().create();
			recCrnwrkmtl.setField("SSTL_NO" , stlNo); 
			
			/* COM.INISTEEL.CIM.YS.COMMON.DAO.YSCOMMDAO.GETYDSTOCK 
			SELECT SSTL_NO                 --재료번호
			    , PTOP_PLNT_GP                 --조업공장구분
			    , YS_MTL_ITEM                 --특수강야드재료품목
			    , ITEMNAME_CD                 --품명코드
			    , YD_MTL_STAT                 --야드재료상태
			    , STL_PROG_CD                 --재료진도코드
			    , ORD_YEOJAE_GP                 --주문여재구분
			    , ORD_NO                 --주문번호
			    , ORD_DTL                 --주문행번
			    , YD_STK_LOT_TP                 --야드산적LOTTYPE
			    , YD_STK_LOT_CD                 --야드산적LOT코드
			    , FRTOMOVE_ORD_DATE                 --이송지시일자
			    , FRTOMOVE_PLANT_GP                 --이송공장구분
			    , URGENT_FRTOMOVE_WORD_GP                 --긴급이송작업지시구분
			    , STL_APPEAR_GP                 --재료외형구분
			    , PLNT_PROC_CD                 --공장공정코드
			    , YD_MTL_T                 --야드재료두께
			    , YD_MTL_W                 --야드재료폭
			    , YD_MTL_L                 --야드재료길이
			    , YD_MTL_WT                 --야드재료중량
			    , YD_MTL_OUTDIA                 --야드재료외경
			    , YD_MTL_W_GP                 --야드재료폭구분
			    , YD_MTL_T_GP                 --야드재료두께구분
			    , YD_MTL_L_GP                 --야드재료길이구분
			    , YS_OUTDIA_GRP_GP                 --특수강야드외경군구분
			    , HCR_GP                 --HCR구분
			    , ROLL_UNIT_GP                 --ROLL단위구분
			    , ROLL_UNIT_NAME                 --ROLL단위명
			    , REFUR_CHG_LOT_NO                 --가열로장입LOT번호
			    , REFUR_CHG_PLN_SERNO                 --가열로장입예정일련번호
			    , ORD_GP                 --수주구분
			    , CUST_CD                 --고객코드
			    , DEST_CD                 --목적지코드
			    , DEMANDER_CD                 --수요가코드
			    , DEST_TEL_NO                 --목적지전화번호
			    , TRANS_ORD_DATE                 --운송지시일자
			    , TRANS_ORD_SEQNO                 --운송지시순번
			    , HOLD_TREAT_GP                 --보류처리구분
			    , CAR_NO                 --차량번호
			    , CARD_NO                 --카드번호
			    , YS_STK_COL_GP                 --특수강야드적치열구분
			    , YS_STK_BED_NO                 --특수강야드적치BED번호
			    , YS_STK_LYR_NO                 --특수강야드적치단번호
			    , YS_STK_SEQ_NO                 --특수강야드적치SEQ번호
			    , STLKIND_CD                 --강종코드
			    , SPEC_ABBSYM                 --규격약호
			    , RENTPROC_CD                 --임가공사코드
			    , SPOS_WLOC_CD                 --발지개소코드
			    , ARR_WLOC_CD                 --착지개소코드
			    , APPEAR_GRADE                 --외관종합판정등급
			    , OVERALL_STAMP_GRADE                 --종합판정등급
			    , GOODS_GRADE                 --제품등급
			    , YD_CAR_UPP_LOC_CD                 --야드차상위치코드
			    , YS_STR_LOC                 --특수강야드저장위치
			    , YD_RCPT_DATE                 --야드입고일자
			    , DIST_DUE_DATE                 --출하기한일
			    , DIST_SHIPASSIGN_GP                 --출하배선지시구분
			    , EXPORT_SHIP_SET_NO                 --수출재배선번호
			    , SHIPASSIGN_WORD_DATE                 --배선작업지시일자
			    , SHIPASSIGN_WORD_SEQNO                 --배선작업지시순번
			    , SHIP_CD                 --선박코드
			    , SHIP_NAME                 --선박명
			    , BERTH_NO                 --선석번호
			    , SAILNO                 --선박항차
			    , SNDBK_RSN_CD                 --반송원인코드
			    , SNDBK_GP                 --반송요청구분
			    , SNDBK_REGISTER                 --반송요청자
			    , SNDBK_REG_DDTT                 --반송요청일자
			    , SNDBK_GP_ETC                 --반납구분기타
			    , DELIVER_TERM_CD                 --인도조건코드
			    , PRE_AR_STAT_CD                 --보관매출상태코드
			    , CAR_LOTID                 --차량LOTID
			    , CAR_LOTID_REG_DDTT                 --차량LOTID등록일자
			    , DETAIL_ARR_CD                 --상세착지코드
			    , URGENT_DIST_YN                 --긴급출하유무
			    , YD_RCPT_ARR_DT                 --입고존도착일시
			    , YD_RCPT_LEV_DT                 --입고존출발일시
			    , YD_RCPT_PLN_STR_LOC                 --야드입고예정저장위치
			    , YD_RCPT_STR_LOC_RSN                 --입고동위치변경사유
			    , REGISTER                 --등록자
			    , REG_DDTT                 --등록일시
			    , MODIFIER                 --수정자
			    , MOD_DDTT                 --수정일시
			    , DEL_YN                 --삭제유무
			    , YD_CHG_NO                 --야드장입순위
			    , SPST_FRTOMOVE_GP                 --특수강이송구분
			    , HEAT_NO
			 FROM TB_YS_STOCK B
			WHERE SSTL_NO = :V_SSTL_NO  
			*/
			JDTORecordSet rsStock = commDao.select(recCrnwrkmtl, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStock", logId, methodNm, "저장품 조회");
			if (rsStock.size() <= 0) {
				throw new Exception("재료번호가 없습니다.");
			}
			
			rsStock.first();
			recStock = rsStock.getRecord();
			
			String StlProgCd 	= commUtils.trim(recStock.getFieldString("STL_PROG_CD" ));
			String carNo 		= commUtils.trim(recStock.getFieldString("CAR_NO" ));
			String TransOrdDate = commUtils.trim(recStock.getFieldString("TRANS_ORD_DATE" ));
			String TransOrdSeqNo = commUtils.trim(recStock.getFieldString("TRANS_ORD_SEQNO" ));
			
			/**********************************************************
			* 2. 저장품 등록
			**********************************************************/
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("STL_APPEAR_GP"	,stlAppearGp); 	//재료외형구분
			jrParam.setField("SSTL_NO"			,stlNo); 			//재료번호
			jrParam.setField("CAR_LOTID"		,"");
			jrParam.setField("STL_PROG_CD"		,"L".equals(StlProgCd)? "L":"M");
			jrParam.setField("MODIFIER"			,modifier); //수정자
			
			/* 저장품 갱신 -- com.inisteel.cim.ys.gds.dao.GdsYsDAO.updDSYSJ006 
			UPDATE TB_YS_STOCK
			SET    STL_APPEAR_GP = NVL(:V_STL_APPEAR_GP ,STL_APPEAR_GP)
			      ,STL_PROG_CD = NVL(:V_STL_PROG_CD ,STL_PROG_CD)
			      ,CAR_LOTID = null
			      ,MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE 
			WHERE  SSTL_NO = :V_SSTL_NO      
			*/
			//저장품 갱신
			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updDSYSJ006", logId, methodNm, "저장품 갱신");

			//----------------------------------------------------------------------------------------------------
			//	재료외형구분에 *로 설정되어 오는 경우에는 마지막재료의 출하완료 처리이므로 차량출발 처리 모듈 호출
			//----------------------------------------------------------------------------------------------------
// 차량출발처리는 권하완료시 함
//			if( stlAppearGp.equals("*")) {
//				
//
//				JDTORecord recInTemp  = JDTORecordFactory.getInstance().create();
//				recInTemp.setField("CAR_NO" , carNo);
//				recInTemp.setField("TRANS_ORD_DATE", 	TransOrdDate);
//				recInTemp.setField("TRANS_ORD_SEQNO", 	TransOrdSeqNo);
//
//				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschTransDTSeq2
//				SELECT *
//				 FROM (
//				SELECT 
//				    YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
//				    ,REGISTER AS REGISTER
//				    ,TO_CHAR(REG_DDTT,'YYYYMMDDHH24MISS') AS REG_DDTT
//				    ,MODIFIER AS MODIFIER
//				    ,TO_CHAR(MOD_DDTT,'YYYYMMDDHH24MISS') AS MOD_DDTT
//				    ,DEL_YN AS DEL_YN
//				    ,YD_EQP_ID AS YD_EQP_ID
//				    ,YD_CAR_USE_GP AS YD_CAR_USE_GP
//				    ,CAR_NO AS CAR_NO
//				    ,TRN_EQP_CD AS TRN_EQP_CD
//				    ,CAR_KIND AS CAR_KIND
//				    ,TRANS_EQUIPMENT_TYPE AS TRANS_EQUIPMENT_TYPE 
//				    ,YD_EQP_WRK_STAT  AS YD_EQP_WRK_STAT
//				    ,YD_WRK_PROG_STAT  AS YD_WRK_PROG_STAT
//				    ,YD_EQP_WRK_SH  AS YD_EQP_WRK_SH
//				    ,YD_EQP_WRK_WT  AS YD_EQP_WRK_WT
//				    ,YS_STK_BED_TP  AS YS_STK_BED_TP
//				    ,SPOS_WLOC_CD  AS SPOS_WLOC_CD
//				    ,ARR_WLOC_CD  AS ARR_WLOC_CD
//				    ,YD_CARLD_LEV_LOC  AS YD_CARLD_LEV_LOC
//				    ,TO_CHAR(YD_CARLD_LEV_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_LEV_DT
//				    ,TO_CHAR(YD_CARLD_PNT_WO_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_PNT_WO_DT
//				    ,(CASE WHEN YD_CAR_PROG_STAT IN('A','B','C','D','E') THEN YD_PNT_CD3 ELSE YD_PNT_CD1 END) AS YD_PNT_CD1
//				    ,YD_PNT_CD2 AS YD_PNT_CD2
//				    ,YD_CARLD_WRK_BOOK_ID  AS YD_CARLD_WRK_BOOK_ID
//				    ,YD_CARLD_SCH_REQ_GP  AS YD_CARLD_SCH_REQ_GP
//				    ,YD_CARLD_STOP_LOC  AS YD_CARLD_STOP_LOC
//				    ,TO_CHAR(YD_CARLD_ARR_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_ARR_DT
//				    ,TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_ST_DT
//				    ,TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_CMPL_DT
//				    ,YD_CARLD_WRK_ACT_GP  AS YD_CARLD_WRK_ACT_GP
//				    ,TO_CHAR(YD_CARLD_CHK_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_CHK_DT
//				    ,TO_CHAR(YD_CARUD_LEV_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_LEV_DT
//				    ,TO_CHAR(YD_CARUD_PNT_WO_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_PNT_WO_DT
//				    ,YD_PNT_CD3 AS YD_PNT_CD3
//				    ,YD_PNT_CD4 AS YD_PNT_CD4
//				    ,YD_CARUD_WRK_BOOK_ID  AS YD_CARUD_WRK_BOOK_ID
//				    ,YD_CARUD_STOP_LOC  AS YD_CARUD_STOP_LOC
//				    ,YD_CARUD_SCH_REQ_GP  AS YD_CARUD_SCH_REQ_GP
//				    ,TO_CHAR(YD_CARUD_ARR_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_ARR_DT
//				    ,TO_CHAR(YD_CARUD_CHK_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_CHK_DT
//				    ,TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_ST_DT
//				    ,TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_CMPL_DT
//				    ,YD_CARUD_WRK_ACT_GP  AS YD_CARUD_WRK_ACT_GP
//				    ,YD_TRN_WRK_DELY_CD  AS YD_TRN_WRK_DELY_CD
//				    ,CARD_NO  AS CARD_NO
//				    ,YD_CAR_PROG_STAT AS YD_CAR_PROG_STAT
//				    ,FRTOMOVE_PLANT_GP AS FRTOMOVE_PLANT_GP
//				    ,PROC_TO AS PROC_TO
//				    ,RENTPROC_CD AS RENTPROC_CD
//				    ,YD_FRTOMOVE_YD_GP AS YD_FRTOMOVE_YD_GP
//				    ,YD_FRTOMOVE_BAY_GP AS YD_FRTOMOVE_BAY_GP
//				    ,URGENT_FRTOMOVE_WORD_GP AS URGENT_FRTOMOVE_WORD_GP
//				    ,DEST_TEL_NO AS DEST_TEL_NO
//				    ,YD_DLVRDD_RULE_DD AS YD_DLVRDD_RULE_DD
//				    ,SHIPASSIGN_WORD_DATE AS SHIPASSIGN_WORD_DATE
//				    ,SHIPASSIGN_WORD_SEQNO AS SHIPASSIGN_WORD_SEQNO
//				    ,SHIP_CD AS SHIP_CD
//				    ,SHIP_NAME AS SHIP_NAME
//				    ,RSHP_HOLD_NO AS RSHP_HOLD_NO
//				    ,BERTH_NO AS BERTH_NO
//				    ,SAILNO AS SAILNO
//				    ,YD_CAR_WRK_GP AS YD_CAR_WRK_GP
//				    ,TRANS_ORD_DATE AS TRANS_ORD_DATE
//				    ,TRANS_ORD_SEQNO AS TRANS_ORD_SEQNO
//				    ,YD_BAYIN_WO_SEQ
//				    ,YD_CAR_RCPT_CHK_YN
//				    ,YD_CAR_ISSUE_CHK_YN
//				    ,YD_CAR_RCPT_CHECKER
//				    ,YD_CAR_ISSUE_CHECKER 
//				    ,CMBN_CARLD_YN
//				FROM TB_YS_CARSCH
//				WHERE CAR_NO LIKE :V_CAR_NO||'%'
//				  AND TRANS_ORD_DATE = :V_TRANS_ORD_DATE
//				  AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
//				--AND DEL_YN='N'
//				ORDER BY YD_CAR_SCH_ID DESC
//				) A
//				WHERE ROWNUM<=1
//				*/
//				JDTORecordSet rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschTransDTSeq2", logId, methodNm, "차량스케줄  조회"); 
//				
//				jrParam = JDTORecordFactory.getInstance().create();
//				jrParam.setResultCode(logId);	//Log ID
//				jrParam.setResultMsg(methodNm);	//Log Method Name
//				
//				if (rsResult == null || rsResult.size() <= 0) {
//		    		throw new Exception("차량스케쥴이 없습니다..");
//		    	}
//		    
//				rsResult.first();
//				jrParam = rsResult.getRecord();
//
//				recInTemp = JDTORecordFactory.getInstance().create();
//				recInTemp.setResultCode(logId);	//Log ID
//				recInTemp.setResultMsg(methodNm);	//Log Method Name				
//				recInTemp.setField("YD_GP",					ydGp);		//야드구분
//				
//				recInTemp.setField("CAR_NO", 				commUtils.trim(jrParam.getFieldString("CAR_NO"     )));			
//				recInTemp.setField("SPOS_WLOC_CD", 			commUtils.trim(jrParam.getFieldString("SPOS_WLOC_CD")));
//				recInTemp.setField("SPOS_YD_PNT_CD", 		commUtils.trim(jrParam.getFieldString("YD_PNT_CD1"  )));
//				recInTemp.setField("TRANS_ORD_DATE", 		commUtils.trim(jrParam.getFieldString("TRANS_ORD_DATE")));
//				recInTemp.setField("TRANS_ORD_SEQNO", 		commUtils.trim(jrParam.getFieldString("TRANS_ORD_SEQNO")));
//
//				
//				// 차량출발처리 
//				EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
//				ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
//				
//			}
			
			
			
 	

			/**********************************************************
			* 3. 저장품제원 전문을 전송
			**********************************************************/
			jrParam.setField("YD_GP"          	, ydGp); //야드구분
			jrParam.setField("YD_INFO_SYNC_CD"	, "5"); //야드정보동기화코드(지정저장품)
			jrParam.setField("SSTL_NO"			, stlNo);		//재료번호
			
			//저장품제원 전송 Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgYSL2L002(jrParam));
			
			//특수강재공야드L2로 전문 전송(반납대상)
			if("K".equals(ydGp) && ("H".equals(stlNo.substring(0 , 1)) || "Q".equals(stlNo.substring(0 , 1)))){
					   jrRtn = commUtils.addSndData(jrRtn,(commDao.getMsgL2("YSN7L003", jrParam)));
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
	 *      [A] 오퍼레이션명 : 특수강제품 반납대기(DSYSJ007)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvDSYSJ007(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "특수강제품 반납대기[GdsYsL3RcvSeEJB.rcvDSYSJ007] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp			= commUtils.trim(rcvMsg.getFieldString("YD_GP")); //야드구분
			String stlNo    	= commUtils.trim(rcvMsg.getFieldString("SSTL_NO")); //재료번호
			String currProgCd	= commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD")); //현재진도코드
			String cancelYn		= commUtils.trim(rcvMsg.getFieldString("CANCEL_YN")); //취소유무
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (!"K".equals(ydGp) ) {
				//throw new Exception("야드구분이 특수강제품창고(K)가 아닙니다!");
				commUtils.printLog(logId, "야드구분이 특수강제품창고(K)가 아닙니다!", "S+");
				return jrReturn;
			}
			if ("".equals(stlNo) ) {
				throw new Exception("잘못된 재료번호가 빈 값 입니다.");
			}
			if ("".equals(currProgCd) ) {
				throw new Exception("잘못된 현재진도코드가 빈 값 입니다.");
			}
			//if ("".equals(cancelYn) ) {
			//	throw new Exception("취소 유무가 빈 값 입니다.");
			//}
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			if("Y".equals(cancelYn)) {
				//CANCEL_YN : Y 취소 처리
				
				
			} else {
				//CANCEL_YN : N 지시 처리

				/**********************************************************
				* 2. 저장품 등록
				**********************************************************/
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("STL_APPEAR_GP"			,commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"))); 	//재료외형구분
				jrParam.setField("SSTL_NO"					,commUtils.trim(rcvMsg.getFieldString("SSTL_NO"))); 			//재료번호
				jrParam.setField("STL_PROG_CD"				,commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD")));	//현재진도코드
				jrParam.setField("URGENT_FRTOMOVE_WORD_GP"	,commUtils.trim(rcvMsg.getFieldString("URGENT_FRTOMOVE_WORD_GP")));   //긴급이송작업지시구분
				jrParam.setField("YD_AIM_RT_GP"				,"J3"); //야드목표행선지구분
				jrParam.setField("MODIFIER"					,modifier); //수정자
				
				//저장품 갱신
				GdsYsDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updDSYSJ007", logId, methodNm, "저장품 갱신");
				
			}

			/**********************************************************
			* 3. 저장품제원 전문을 전송
			**********************************************************/
			jrParam.setField("YD_GP"          , ydGp); //야드구분
			jrParam.setField("YD_INFO_SYNC_CD", "5"); //야드정보동기화코드(지정저장품) 
			
			//저장품제원 전송 Data 생성
			//JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgYSL2L002(jrParam));
			 
		 
			//특수강재공야드L2로 전문 전송(반납대상)
			if("K".equals(ydGp) && ("H".equals(stlNo.substring(0 , 1)) || "Q".equals(stlNo.substring(0 , 1)))){
					   jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN7L003", jrParam));
			}
			
			if (commUtils.isEmpty(jrRtn)) {
				jrRtn = JDTORecordFactory.getInstance().create();
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
	 *      [A] 오퍼레이션명 : 특수강제품 목전(DSYSJ008)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvDSYSJ008(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "특수강제품 목전[GdsYsL3RcvSeEJB.rcvDSYSJ008] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = null;
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp			= commUtils.trim(rcvMsg.getFieldString("YD_GP")); //야드구분
			String stlNo    	= commUtils.trim(rcvMsg.getFieldString("SSTL_NO")); //재료번호
			String currProgCd	= commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD")); //현재진도코드
			String cancelYn		= commUtils.trim(rcvMsg.getFieldString("CANCEL_YN")); //취소유무
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (!"K".equals(ydGp) ) {
				//throw new Exception("야드구분이 특수강제품창고(K)가 아닙니다!");
				commUtils.printLog(logId, "야드구분이 특수강제품창고(K)가 아닙니다!", "S+");
				return jrReturn;
			}
			if ("".equals(stlNo) ) {
				throw new Exception("잘못된 재료번호가 빈 값 입니다.");
			}
			if ("".equals(currProgCd) ) {
				throw new Exception("잘못된 현재진도코드가 빈 값 입니다.");
			}
			if ("".equals(cancelYn) ) {
				throw new Exception("취소 유무가 빈 값 입니다.");
			}
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			if("Y".equals(cancelYn)) {
				//CANCEL_YN : Y 취소 처리
				
				
			} else {
				//CANCEL_YN : N 지시 처리

				/**********************************************************
				* 2. 저장품 등록
				**********************************************************/
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("STL_APPEAR_GP"		,commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"))); 	//재료외형구분
				jrParam.setField("SSTL_NO"				,commUtils.trim(rcvMsg.getFieldString("SSTL_NO"))); 			//재료번호
				jrParam.setField("STL_PROG_CD"			,commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD")));	//현재진도코드
				jrParam.setField("ORD_YEOJAE_GP"		,commUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP")));   //주문여재구분
				jrParam.setField("ORD_NO"				,commUtils.trim(rcvMsg.getFieldString("ORD_NO")));			//주문번호
				jrParam.setField("ORD_DTL"				,commUtils.trim(rcvMsg.getFieldString("ORD_DTL")));			//주문행번
				jrParam.setField("ORD_GP"				,commUtils.trim(rcvMsg.getFieldString("ORD_GP")));			//수주구분
				jrParam.setField("CUST_CD"				,commUtils.trim(rcvMsg.getFieldString("CUST_CD")));			//고객코드
				jrParam.setField("DEST_CD"				,commUtils.trim(rcvMsg.getFieldString("DEST_CD")));			//목적지코드
				jrParam.setField("YD_DLVRDD_RULE_DD"	,commUtils.trim(rcvMsg.getFieldString("DLVRDD_RULE_DD")));	//야드납기기준일
				jrParam.setField("DEST_TEL_NO"			,commUtils.trim(rcvMsg.getFieldString("DEST_TEL_NO")));		//목적지전화번호
				jrParam.setField("DIST_SHIPASSIGN_GP"	,commUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP")));//출하배선지시구분
				jrParam.setField("YD_AIM_RT_GP"			,"K3"); //야드목표행선지구분
				jrParam.setField("MODIFIER"				,modifier); //수정자
				
				//저장품 갱신
				GdsYsDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updDSYSJ001", logId, methodNm, "저장품 갱신");
				
			}

			/**********************************************************
			* 3. 저장품제원 전문을 전송
			**********************************************************/
			jrParam.setField("YD_GP"          , ydGp); //야드구분
			jrParam.setField("YD_INFO_SYNC_CD", "5"); //야드정보동기화코드(지정저장품)

			//저장품제원 전송 Data 생성
			//JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgYSL2L002(jrParam));
			if (commUtils.isEmpty(jrRtn)) {
				jrRtn = JDTORecordFactory.getInstance().create();
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
	 *      [A] 오퍼레이션명 : 특수강제품 보관지시(DSYSJ009)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvDSYSJ009(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "특수강제품 보관지시[GdsYsL3RcvSeEJB.rcvDSYSJ009] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp			= commUtils.trim(rcvMsg.getFieldString("YD_GP")); //야드구분
			String stlNo    	= commUtils.trim(rcvMsg.getFieldString("SSTL_NO")); //재료번호
			String currProgCd	= commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD")); //현재진도코드
			String cancelYn		= commUtils.trim(rcvMsg.getFieldString("CANCEL_YN")); //취소유무
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (!"K".equals(ydGp) ) {
				//throw new Exception("야드구분이 특수강제품창고(K)가 아닙니다!");
				commUtils.printLog(logId, "야드구분이 특수강제품창고(K)가 아닙니다!", "S+");
				return jrReturn;
			}
			if ("".equals(stlNo) ) {
				throw new Exception("잘못된 재료번호가 빈 값 입니다.");
			}
			if ("".equals(currProgCd) ) {
				throw new Exception("잘못된 현재진도코드가 빈 값 입니다.");
			}
			if ("".equals(cancelYn) ) {
				throw new Exception("취소 유무가 빈 값 입니다.");
			}
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			if("Y".equals(cancelYn)) {
				//CANCEL_YN : Y 취소 처리
				
				
			} else {
				//CANCEL_YN : N 지시 처리

				/**********************************************************
				* 2. 저장품 등록
				**********************************************************/
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("STL_APPEAR_GP"		,commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"))); 	//재료외형구분
				jrParam.setField("SSTL_NO"				,commUtils.trim(rcvMsg.getFieldString("SSTL_NO"))); 			//재료번호
				jrParam.setField("STL_PROG_CD"			,commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD")));	//현재진도코드
				jrParam.setField("ORD_YEOJAE_GP"		,commUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP")));   //주문여재구분
				jrParam.setField("ORD_NO"				,commUtils.trim(rcvMsg.getFieldString("ORD_NO")));			//주문번호
				jrParam.setField("ORD_DTL"				,commUtils.trim(rcvMsg.getFieldString("ORD_DTL")));			//주문행번
				jrParam.setField("ORD_GP"				,commUtils.trim(rcvMsg.getFieldString("ORD_GP")));			//수주구분
				jrParam.setField("CUST_CD"				,commUtils.trim(rcvMsg.getFieldString("CUST_CD")));			//고객코드
				jrParam.setField("DEST_CD"				,commUtils.trim(rcvMsg.getFieldString("DEST_CD")));			//목적지코드
				jrParam.setField("YD_DLVRDD_RULE_DD"	,commUtils.trim(rcvMsg.getFieldString("DLVRDD_RULE_DD")));	//야드납기기준일
				jrParam.setField("DEST_TEL_NO"			,commUtils.trim(rcvMsg.getFieldString("DEST_TEL_NO")));		//목적지전화번호
				jrParam.setField("DIST_SHIPASSIGN_GP"	,commUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP")));//출하배선지시구분
				jrParam.setField("YD_AIM_RT_GP"			,"K3"); //야드목표행선지구분
				jrParam.setField("MODIFIER"				,modifier); //수정자
				
				//저장품 갱신
				GdsYsDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updDSYSJ001", logId, methodNm, "저장품 갱신");
				
			}

			/**********************************************************
			* 3. 저장품제원 전문을 전송
			**********************************************************/
			jrParam.setField("YD_GP"          , ydGp); //야드구분
			jrParam.setField("YD_INFO_SYNC_CD", "5"); //야드정보동기화코드(지정저장품)

			//저장품제원 전송 Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgYSL2L002(jrParam));

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	

	/**
	 *      [A] 오퍼레이션명 : 특수강제품 긴급보류재처리실적(DSYSJ010)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvDSYSJ010(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "특수강제품 긴급보류재처리실적[GdsYsL3RcvSeEJB.rcvDSYSJ010] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn =null;
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp			= commUtils.trim(rcvMsg.getFieldString("YD_GP")); //야드구분
			String stlNo    	= commUtils.trim(rcvMsg.getFieldString("SSTL_NO")); //재료번호
			String urgentDistYn	= commUtils.trim(rcvMsg.getFieldString("URGENT_DIST_YN")); //긴급보류재처리유무
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (!"K".equals(ydGp) ) {
				//throw new Exception("야드구분이 특수강제품창고(K)가 아닙니다!");
				commUtils.printLog(logId, "야드구분이 특수강제품창고(K)가 아닙니다!", "S+");
				return jrReturn;
			}
			if ("".equals(stlNo) ) {
				throw new Exception("재료번호가 빈 값 입니다.");
			}
			if ("".equals(urgentDistYn) ) {
				throw new Exception("긴급보류재처리유무가 빈 값 입니다.");
			}
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			/**********************************************************
			* 2. 저장품 등록
			**********************************************************/
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("URGENT_DIST_YN"		,commUtils.trim(rcvMsg.getFieldString("URGENT_DIST_YN"))); 	//긴급출하유무
			jrParam.setField("SSTL_NO"				,commUtils.trim(rcvMsg.getFieldString("SSTL_NO"))); 			//재료번호
			jrParam.setField("MODIFIER"				,modifier); //수정자
			
			//저장품 긴급출하유무 갱신
			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updDSYSJ010", logId, methodNm, "저장품 긴급출하유무 갱신");


			/**********************************************************
			* 3. 저장품제원 전문을 전송
			**********************************************************/
			jrParam.setField("YD_GP"          , ydGp); //야드구분
			jrParam.setField("YD_INFO_SYNC_CD", "5"); //야드정보동기화코드(지정저장품)

			//저장품제원 전송 Data 생성
			//JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgYSL2L002(jrParam));
			if (commUtils.isEmpty(jrRtn)) {
				jrRtn = JDTORecordFactory.getInstance().create();
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
	 *      [A] 오퍼레이션명 : 특수강제품 반품/반입 대상차량정보(DSYSJ011)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvDSYSJ011(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "특수강제품 반품대상차량정보[GdsYsL3RcvSeEJB.rcvDSYSJ011] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			JDTORecord jrRtn 	= null;
			String sSstlNo		= "";

			//수신 항목 값
			String msgId    		= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp				= commUtils.trim(rcvMsg.getFieldString("YD_GP")); //야드구분
			String transOrdDate		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT")); //운송지시일자
			String carNo			= commUtils.trim(rcvMsg.getFieldString("CAR_NO")); //차량번호
			String cancelYn			= commUtils.trim(rcvMsg.getFieldString("CANCEL_YN")); //취소유무
			String spstFrtomoveGp	= commUtils.trim(rcvMsg.getFieldString("SPST_FRTOMOVE_GP")); //14:반품(반입), 21:반납, 26:반송
			int itransOrdSeqNo  	= Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("TRANS_ORD_SEQNO"),"0")); //운송지시순번
			int iydEqpWrkSh			= Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0")); //야드설비작업매수
			
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (!"K".equals(ydGp) ) {
				//throw new Exception("야드구분이 특수강제품창고(K)가 아닙니다!");
				commUtils.printLog(logId, "야드구분이 특수강제품창고(K)가 아닙니다!", "S+");
				return jrReturn;
			}
			if ("".equals(carNo) ) {
				throw new Exception("차량번호가 빈 값 입니다.");
			}
			if (0 == iydEqpWrkSh ) {
				throw new Exception("재료매수가 빈 값 입니다.");
			}

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			if("Y".equals(cancelYn)) {
				
		  		for(int index = 0; index < iydEqpWrkSh; index++){
	    			
	    			sSstlNo =  commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+(index+1))); 
	
	    			if("".equals(sSstlNo)) break;
	    			
	    			/**********************************************************
	    			* 2. 저장품 등록
	    			**********************************************************/
	    			jrParam.setResultCode(logId);							//Log ID
	    			jrParam.setResultMsg(methodNm);							//Log Method Name
	    			jrParam.setField("SSTL_NO"			,sSstlNo); 			//재료번호
        			jrParam.setField("CAR_NO"			,"");	//차량번호
        			jrParam.setField("TRANS_ORD_DATE"	,"");	//운송지시일자
        			jrParam.setField("TRANS_ORD_SEQNO"	,"");	//운송지시순번
	    			jrParam.setField("SPST_FRTOMOVE_GP"	,spstFrtomoveGp);   //이송구분
	    			jrParam.setField("MODIFIER"			,modifier); 		//수정자
	    			
	    			//저장품 반품정보 갱신
	    			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updDSYSJ011", logId, methodNm, "반품정보 갱신");
	    		}	
				
				/**********************************************************
				* 3. 차량스케쥴 삭제
				**********************************************************/
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("TRANS_ORD_DATE"   , transOrdDate  ); 	//운송지시일자
				jrParam.setField("TRANS_ORD_SEQNO"  , ""+itransOrdSeqNo  ); //운송지시순번
				jrParam.setField("CAR_NO"			, carNo );
				jrParam.setField("DEL_YN"			, "N" );
				jrParam.setField("MODIFIER"         , modifier       ); //수정자

				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchDelYn 
				SELECT YD_CAR_SCH_ID ,YD_CAR_PROG_STAT
				  FROM TB_YS_CARSCH
				 WHERE TRANS_ORD_DATE = :V_TRANS_ORD_DATE
				   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				   AND CAR_NO  = :V_CAR_NO
				   AND DEL_YN   = :V_DEL_YN
				ORDER BY YD_CAR_SCH_ID DESC   
				*/
				
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchDelYn", logId, methodNm, "차량스케줄ID 조회");

				if (jsChk.size() > 0) {
					
					jsChk.first();
					JDTORecord recTemp		= jsChk.getRecord();
					
					String szYD_CAR_PROG_STAT	= commUtils.trim(recTemp.getFieldString("YD_CAR_PROG_STAT"));			
					String szYD_CAR_SCH_ID		= commUtils.trim(recTemp.getFieldString("YD_CAR_SCH_ID"));			
					
					if(szYD_CAR_PROG_STAT.equals("")||szYD_CAR_PROG_STAT.equals("0")||szYD_CAR_PROG_STAT.equals("1")) {

						JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("YD_CAR_SCH_ID" , szYD_CAR_SCH_ID);
						recInTemp.setField("MODIFIER"      , "DSYSJ004");
						
						/*com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch 
						UPDATE TB_YS_CARSCH
						   SET MODIFIER = :V_MODIFIER 
						     , MOD_DDTT = SYSDATE
						     , DEL_YN = 'Y'
						 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
						     AND DEL_YN = 'N'
		                */  
						commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch", logId, methodNm, "차량스케줄 삭제");						
					}
				}
				
			} else {

				for(int index = 0; index < iydEqpWrkSh; index++){
	    			
	    			sSstlNo =  commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+(index+1))); 
	
	    			if("".equals(sSstlNo)) break;
	    			
	    			/**********************************************************
	    			* 2. 저장품 등록
	    			**********************************************************/
	    			jrParam.setResultCode(logId);							//Log ID
	    			jrParam.setResultMsg(methodNm);							//Log Method Name
	    			jrParam.setField("SSTL_NO"			,sSstlNo); 			//재료번호
	    			jrParam.setField("CAR_NO"			,carNo);			//차량번호
	    			jrParam.setField("TRANS_ORD_DATE"	,transOrdDate);		//운송지시일자
	    			jrParam.setField("TRANS_ORD_SEQNO"	,""+itransOrdSeqNo);//운송지시순번
	    			jrParam.setField("SPST_FRTOMOVE_GP"	,spstFrtomoveGp);   //이송구분
	    			jrParam.setField("MODIFIER"			,modifier); 		//수정자
	    			
	    			//저장품 반품정보 갱신
	    			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updDSYSJ011", logId, methodNm, "반품정보 갱신");
	    		}	
				
				/**********************************************************
				* 3. 대기장 도착 처리
				**********************************************************/
	
				if (commUtils.isEmpty(jrRtn)) {
					jrRtn = JDTORecordFactory.getInstance().create();
				}
				jrRtn = JDTORecordFactory.getInstance().create();
				jrParam= JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);							//Log ID
				jrParam.setResultMsg(methodNm);							//Log Method Name
				jrParam.setField("YD_GP"			,ydGp); 			//재료번호
				jrParam.setField("CAR_NO"			,carNo);			//차량번호
				jrParam.setField("TRANS_ORD_DT"	,transOrdDate);		//운송지시일자
				jrParam.setField("TRANS_ORD_SEQNO"	,""+itransOrdSeqNo);//운송지시순번
				jrParam.setField("MODIFIER"			,modifier); 		//수정자			
				jrParam.setField("SPST_FRTOMOVE_GP"	,spstFrtomoveGp); 		//특수강이송운송구분	
				jrParam.setField("DEL_YN"	,cancelYn); 		//삭제 여부	
						
				jrRtn = commUtils.addSndData(jrRtn, this.rcvYARD_DSYSJ005(jrParam));
				
				
					
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
	 *      [A] 오퍼레이션명 : 특수강반입/반품제품 대기장도착실적
     *                    : 내부 로직임 (출하에서 차량도착시 처리 함
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYARD_DSYSJ005(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "특수강 반입/반품제품 대기장도착[GdsYsL3RcvSeEJB.rcvYARD_DSYSJ005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			String szYD_CAR_SCH_ID = null;
			
			//수신 항목 값
			String msgId    		= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp				= commUtils.trim(rcvMsg.getFieldString("YD_GP")); 			//야드구분
			String transOrdDt  		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT")); 	//운송지시일자
			String transOrdSeqNo	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")); //운송지시순번
			String carNo  			= commUtils.trim(rcvMsg.getFieldString("CAR_NO")); 			//차량번호
			String transFrtomoveGp	= commUtils.trim(rcvMsg.getFieldString("SPST_FRTOMOVE_GP"));//14:반품(반입), 21:반납, 26:반송
			String modifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)

			if ("".equals(modifier)) { modifier = msgId; }
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (!"K".equals(ydGp) ) {
				//throw new Exception("야드구분이 특수강제품창고(K)가 아닙니다!");
				commUtils.printLog(logId, "야드구분이 특수강제품창고(K)가 아닙니다!", "S+");
				return jrReturn;
			}
			if ("".equals(transOrdDt) ) {
				throw new Exception("운송지시일자가 빈 값 입니다.");
			}
			if ("".equals(transOrdSeqNo) ) {
				throw new Exception("운송지시순번이 빈 값 입니다.");
			}
	
			/**********************************************************
			* 2. 차량스케줄 편성 여부 Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("TRANS_ORD_DATE"   , transOrdDt  ); 	//운송지시일자
			jrParam.setField("TRANS_ORD_SEQNO"  , transOrdSeqNo  ); //운송지시순번
			jrParam.setField("MODIFIER"         , modifier       ); //수정자

			/* 차량스케줄ID 조회 -- com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchId 
			SELECT YD_CAR_SCH_ID 
			  FROM TB_YS_CARSCH
			 WHERE TRANS_ORD_DATE = :V_TRANS_ORD_DATE
			   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			   AND DEL_YN   = 'N'
			ORDER BY YD_CAR_SCH_ID DESC   
			*/
			
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchId", logId, methodNm, "차량스케줄ID 조회");

			if (jsChk.size() > 0) {
				throw new Exception("차량스케줄이 이미 편성되어 있습니다!!");
			}
			
			String szYS_STK_COL_GP 	= "";
			String szYD_PNT_CD 		= "";
			String szWLOC_CD 		= "";
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
			if(transFrtomoveGp.equals("21")||transFrtomoveGp.equals("26")) {  //21:반납, 26:반송
			
				throw new Exception("정보 이상.");
			} else{
				commUtils.printLog(logId, transFrtomoveGp, "SL");
				/*************************************************************
				* 3. 반입 운송지시일자 순번으로 대상 제품의 위치를 조회하여 입동포인트를 가져온다.
				*************************************************************/
				jrParam.setField("TRANS_ORD_DATE"   , transOrdDt  ); 	//운송지시일자
				jrParam.setField("TRANS_ORD_SEQNO"  , transOrdSeqNo  ); //운송지시순번
				jrParam.setField("YD_GP"         	, ydGp       ); 	//야드구분
	
				/* 반입도착가능 차량포인트 조회 -- com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarPntCdReturn 
				SELECT CP.YD_STK_COL_GP AS YS_STK_COL_GP
				      ,CP.YD_CARPNT_CD
				      ,CP.YD_PNT_CD
				      ,CP.WLOC_CD
				      ,DECODE(CP.YD_STK_COL_ACT_STAT,'C',1,2) AS STAT_RANK
				      , (SELECT COUNT(*)
				           FROM TB_YS_CARSCH 
				          WHERE DEL_YN='N'
				            AND YD_CARLD_STOP_LOC=CP.YD_STK_COL_GP) CARLD_RANK
				      ,DD.CNT           

				FROM   TB_YD_CARPOINT CP
				      ,(
				      
				            SELECT YD_GP
				                  ,YD_BAY_GP
				                  ,COUNT(*) AS CNT
				            FROM   (
				                        SELECT  ST.TRANS_ORD_DATE
				                               ,ST.TRANS_ORD_SEQNO
				                               ,ST.SSTL_NO
				                               ,'K'  AS YD_GP
				                               ,CASE WHEN ITEMNAME_CD IN ('SRR','SRQ') THEN 'B'
				                                     WHEN ITEMNAME_CD IN ('SRI','SRW') THEN 'D'
				                                     END  AS YD_BAY_GP
				                        FROM    TB_YS_STOCK ST
				                        WHERE   ST.TRANS_ORD_DATE = :V_TRANS_ORD_DATE
				                        AND     ST.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				                   ) 
				                   
				            GROUP BY TRANS_ORD_DATE
				                    ,TRANS_ORD_SEQNO
				                    ,YD_GP
				                    ,YD_BAY_GP      
				       ) DD
				WHERE  CP.YD_GP = DD.YD_GP
				AND    CP.YD_BAY_GP = DD.YD_BAY_GP
				AND    CP.YD_CAR_USETYPE_GP IN('TR', 'RT','RA','TO') --트레일러
				AND    CP.DEL_YN='N'
				AND    CP.YD_STK_COL_ACT_STAT <>'N'
				ORDER BY STAT_RANK
				        ,CARLD_RANK
				        ,CNT 
	
				*/        
				JDTORecordSet jsPnt = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarPntCdReturn", logId, methodNm, "반입도착가능 차량포인트 조회");
				
				if (jsPnt.size() <= 0) {
					throw new Exception("도착가능 차량포인트가 없습니다.");
				}
				
				jsPnt.first();
				JDTORecord jrCarPnt = jsPnt.getRecord();
				
				szYS_STK_COL_GP	= commUtils.trim(jrCarPnt.getFieldString("YS_STK_COL_GP" ));
				szYD_PNT_CD 	= commUtils.trim(jrCarPnt.getFieldString("YD_PNT_CD" ));
				szWLOC_CD 		= commUtils.trim(jrCarPnt.getFieldString("WLOC_CD" ));
				
				/**********************************************************
				* 4. 차량스케줄을 생성(수정) 한다.
				**********************************************************/

				/*
				 * 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
				 */
				szYD_CAR_SCH_ID = commDao.getSeqId(logId, methodNm, "CarSch");
				
				//14:반입
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setField("YD_CAR_SCH_ID"	, szYD_CAR_SCH_ID);
				jrParam.setField("YD_EQP_WRK_STAT"	, "L");									//야드설비작업상태
				jrParam.setField("YD_EQP_ID"		, "XXPT02");							//야드설비ID
				jrParam.setField("YD_CAR_USE_GP"	, "G");									//차량사용구분
				jrParam.setField("CAR_KIND"			, "TR");
				jrParam.setField("ARR_WLOC_CD"		, szWLOC_CD);							//발지개소코드
				jrParam.setField("YD_PNT_CD3"		, szYD_PNT_CD);							//야드포인트코드1
				jrParam.setField("CAR_NO"			, carNo);								//차량번호
				jrParam.setField("TRANS_ORD_DATE"   , transOrdDt  ); 						//운송지시일자
				jrParam.setField("TRANS_ORD_SEQNO"  , transOrdSeqNo  ); 					//운송지시순번
				jrParam.setField("YD_CARUD_STOP_LOC", szYS_STK_COL_GP);						//차량상차정지위치
		    	jrParam.setField("YD_BAYIN_WO_SEQ"	, "99");	//입동지시순번 --기본값으로 설정(9) ** -- WC 수정 : 반입 차량의 경우 입고검수완료 전까지 99로 설정함, 검수 이후 9로 셋팅
	    		jrParam.setField("YD_CAR_PROG_STAT"	, ""); //하차출발
	    		jrParam.setField("WAIT_ARR_DDTT"	, commUtils.getDateTime14());	//대기장도착시간
	      		//차량스케줄 등록
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insDSYSJ005", logId, methodNm, "차량스케줄등록");
				

				/**********************************************************
				* 4. 반입이송차량재료정보 등록
				**********************************************************/
				
				/* com.inisteel.cim.ys.gds.dao.GdsYsDAO.getYdStockTrnord 
				SELECT SSTL_NO 
				  FROM TB_YS_STOCK
				 WHERE TRANS_ORD_DATE = :V_TRANS_ORD_DATE
				   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				   AND CAR_NO = :V_CAR_NO
				*/   
				JDTORecordSet jsChk1 = commDao.select(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.getYdStockTrnord", logId, methodNm, "재료정보 조회");
				JDTORecord outParam  = JDTORecordFactory.getInstance().create();
				
				for (int ii = 0; ii < jsChk1.size() ; ii++) {  
					jsChk1.absolute(ii+1);
					outParam  = jsChk1.getRecord();
					jrParam.setField("SSTL_NO"		,  commUtils.trim(outParam.getFieldString("SSTL_NO"))); 
					//차량이송재료 등록
					/* com.inisteel.cim.ys.gds.dao.GdsYsDAO.insSbCarmvmtl 
					MERGE INTO TB_YS_CARFTMVMTL TM USING (
					    SELECT * 
					    FROM TB_YS_STOCK ST
					    WHERE ST.SSTL_NO = :V_SSTL_NO
					) DD ON (TM.SSTL_NO = DD.SSTL_NO AND DD.SSTL_NO = 'N')
					WHEN NOT MATCHED THEN
					INSERT (TM.YD_CAR_SCH_ID, TM.SSTL_NO, TM.REGISTER, TM.REG_DDTT,
					            TM.MODIFIER, TM.MOD_DDTT, TM.DEL_YN, TM.YS_STK_BED_NO,
					            TM.YS_STK_LYR_NO, TM.HCR_GP, TM.STL_PROG_CD)
					VALUES (:V_YD_CAR_SCH_ID, DD.SSTL_NO, DD.MODIFIER, DD.MOD_DDTT,
					            DD.MODIFIER, DD.MOD_DDTT, DD.DEL_YN, DD.YS_STK_BED_NO,
					            DD.YS_STK_LYR_NO, DD.HCR_GP, DD.STL_PROG_CD)
				   */	            
					commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insSbCarmvmtl", logId, methodNm, "차량 이송재료 등록");
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
	 *      [A] 오퍼레이션명 : 봉강제품야드 Banding실적 (SBYSJ002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvSBYSJ002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강제품야드 Banding실적[GdsYsL3RcvSeEJB.rcvSBYSJ002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlNo    = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"    )); //재료번호
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(stlNo) ) {
				throw new Exception("잘못된 재료번호[" + stlNo + "] 입니다.");
			}

			/**********************************************************
			* 2. 저장품 등록
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("SSTL_NO"  , stlNo   ); //재료번호
			jrParam.setField("MODIFIER" , modifier); //수정자
			jrParam.setField("CARRY_OUT", "N"     ); //CARRY-OUT 여부 N:생산실적
			
			
			JDTORecordSet rsResult			= null;
			JDTORecord recTemp				= null;
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.getBdlYdStock", logId, methodNm, "재료정보조회");
			if(rsResult.size()>0){
				rsResult.first();
				recTemp=rsResult.getRecord();
				
				String szYS_STK_COL_GP=commUtils.trim(recTemp.getFieldString("YS_STK_COL_GP"));
				String szBNDL_NO=commUtils.trim(recTemp.getFieldString("BNDL_NO"));
				String szREAL_MEASURE_BUNDLE_WT=commUtils.trim(recTemp.getFieldString("REAL_MEASURE_BUNDLE_WT"));
				String szREAL_MEASURE_BUNDLE_LEN=commUtils.trim(recTemp.getFieldString("REAL_MEASURE_BUNDLE_LEN"));
				String szREAL_MEASURE_BUNDLE_PIECE_CNT=commUtils.trim(recTemp.getFieldString("REAL_MEASURE_BUNDLE_PIECE_CNT"));
				String szREAL_MEASURE_BUNDLE_T=commUtils.trim(recTemp.getFieldString("REAL_MEASURE_BUNDLE_T"));
				String szR_YD_MTL_MIN_WT=commUtils.trim(recTemp.getFieldString("R_YD_MTL_MIN_WT"));
				String szR_YD_MTL_MAX_WT=commUtils.trim(recTemp.getFieldString("R_YD_MTL_MAX_WT"));
				String szR_YD_MTL_MIN_L=commUtils.trim(recTemp.getFieldString("R_YD_MTL_MIN_L"));
				String szR_YD_MTL_MAX_L=commUtils.trim(recTemp.getFieldString("R_YD_MTL_MAX_L"));
				String szR_GONG_BED_CNT=commUtils.trim(recTemp.getFieldString("R_GONG_BED_CNT"));
				String szR_NET_BED_CNT=commUtils.trim(recTemp.getFieldString("R_NET_BED_CNT"));
				String szR_YD_SIZE=commUtils.trim(recTemp.getFieldString("R_YD_SIZE"));
				
				String szMsg="STK_COL_GP:"+szYS_STK_COL_GP+" BNDL_NO:"+szBNDL_NO+" BUNDLE_WT:"+szREAL_MEASURE_BUNDLE_WT
						+" BUNDLE_LEN:"+szREAL_MEASURE_BUNDLE_LEN+" BUNDLE_PIECE_CNT:"+szREAL_MEASURE_BUNDLE_PIECE_CNT+" BUNDLE_T:"+szREAL_MEASURE_BUNDLE_T
						+" R_MIN_WT:"+szR_YD_MTL_MIN_WT+" R_MAX_WT:"+szR_YD_MTL_MAX_WT+" R_MIN_L:"+szR_YD_MTL_MIN_L+" R_MAX_L:"+szR_YD_MTL_MAX_L
						+" GONG_BED_CNT:"+szR_GONG_BED_CNT+" R_NET_BED_CNT:"+szR_NET_BED_CNT+" R_YD_SIZE:"+szR_YD_SIZE;
				commUtils.printLog(logId, szMsg, "S+");
			}
			//등록전 번들공통의 REAL_MEASURE_BUNDLE_WT, REAL_MEASURE_BUNDLE_LEN, REAL_MEASURE_BUNDLE_PIECE_CNT,REAL_MEASURE_BUNDLE_T
			//    RULE의 R_YD_MTL_MIN_WT, R_YD_MTL_MAX_WT, R_YD_MTL_MIN_L, R_GONG_BED_CNT, R_YD_SIZE, RULE.R_NET_BED_CNT 
			//    등의 값 확인 로그 필요. 로그로 찍을지? INSERT로 히스토리에 담을지 결정하고..
			//저장품 등록
			GdsYsDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insBdlYdStock", logId, methodNm, "저장품 등록");
			

			/**********************************************************
			* 3. 저장품제원 전문을 전송
			**********************************************************/
			jrParam.setField("YD_GP"          , "K"); //야드구분
			jrParam.setField("YD_INFO_SYNC_CD", "A"); //야드정보동기화코드(생산실적)

			jrParam.setField("YD_L2_GP", "N4"); //L2 위치
			
			//저장품제원 전송 Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgYSL2L002(jrParam));

			jrParam.setField("YD_L2_GP", "N6"); //L2 위치

			//저장품제원 전송 Data 생성
			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgYSL2L002(jrParam));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 선재제품야드 평량실적 (SBYSJ003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvSBYSJ003(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재제품야드 평량실적[GdsYsL3RcvSeEJB.rcvSBYSJ003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlNo    = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"    )); //재료번호
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(stlNo) ) {
				throw new Exception("잘못된 재료번호[" + stlNo + "] 입니다.");
			}

			/**********************************************************
			* 2. 저장품 등록
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("SSTL_NO"  , stlNo   ); //재료번호
			jrParam.setField("MODIFIER", modifier); //수정자
			jrParam.setField("CARRY_OUT", "N"     ); //CARRY-OUT 여부 N:생산실적
			
			//저장품 등록
			GdsYsDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insBdlYdStock", logId, methodNm, "저장품 등록");
			
			/**********************************************************
			* 3. 저장품제원 전문을 전송
			**********************************************************/
			jrParam.setField("YD_GP"          , "K"); //야드구분
			jrParam.setField("YD_INFO_SYNC_CD", "A"); //야드정보동기화코드(생산실적)
			
			jrParam.setField("YD_L2_GP", "N3"); //L2 위치
			//저장품제원 전송 Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgYSL2L002(jrParam));
			
			jrParam.setField("YD_L2_GP", "N5"); //L2 위치
			//저장품제원  전송 Data 생성
			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgYSL2L002(jrParam));
			
			/**********************************************************
			* 4. 입고라우팅 전문을 전송
			*  4.1 기준테이블 값으로 판단 로직 추가할것
			**********************************************************/
			//jrParam.setField("INFO_GP"        , "1"  ); //정보구분 : 입고(1),반납(2),이적(3),출고(5)
			jrParam.setField("SSTL_NO"        , stlNo); //특수강재료번호
			jrParam.setField("YD_BAY_TO"      , "E"  ); //목적 동

			//입고라우팅 전문 전송 Data 생성
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L101", jrParam));
			

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 봉강제품야드 봉강정정지시 (SBYSJ004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvSBYSJ004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강제품야드 봉강정정지시[GdsYsL3RcvSeEJB.rcvSBYSJ004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
//			String modGp    	= commUtils.trim(rcvMsg.getFieldString("MOD_GP"       )); //수정구분
//			String ptopPlntGp 	= commUtils.trim(rcvMsg.getFieldString("PTOP_PLNT_GP" )); //조업공장구분
			String wordUnitName	= commUtils.trim(rcvMsg.getFieldString("WORD_UNIT_NAME" )); //작업단위
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(wordUnitName) ) {
				throw new Exception("잘못된 작업단위[" + wordUnitName + "] 입니다.");
			}

			/**********************************************************
			* 2. 저장품 등록
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("WORD_UNIT_NAME"  	, wordUnitName   ); //작업단위
			jrParam.setField("MODIFIER"			, modifier); //수정자

			//봉강 생산예정 저장품 등록
			GdsYsDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insPlnSRRStock", logId, methodNm, "봉강 생산예정 저장품 등록");
			
			return null;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 봉강제품야드 각강번들실적 (SBYSJ005)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvSBYSJ005(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강제품야드 각강번들실적[GdsYsL3RcvSeEJB.rcvSBYSJ005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlNo    = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"    )); //재료번호
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(stlNo) ) {
				throw new Exception("잘못된 재료번호[" + stlNo + "] 입니다.");
			}

			/**********************************************************
			* 2. 저장품 등록
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("SSTL_NO"  , stlNo   ); //재료번호
			jrParam.setField("MODIFIER" , modifier); //수정자
			jrParam.setField("CARRY_OUT", "N"     ); //CARRY-OUT 여부 N:생산실적

			//각강 생산예정 저장품 등록
			//GdsYsDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insPlnSRQStock", logId, methodNm, "각강 생산예정 저장품 등록");
			//저장품 등록
			GdsYsDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insBdlYdStock", logId, methodNm, "저장품 등록");
			
			/**********************************************************
			* 3. 저장품제원 전문을 전송
			**********************************************************/
			jrParam.setField("YD_GP"          , "K"); //야드구분
			jrParam.setField("YD_INFO_SYNC_CD", "A"); //야드정보동기화코드(생산실적)

			jrParam.setField("YD_L2_GP", "N4"); //L2 위치
			
			//저장품제원 전송 Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgYSL2L002(jrParam));

			jrParam.setField("YD_L2_GP", "N6"); //L2 위치

			//저장품제원 전송 Data 생성
			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgYSL2L002(jrParam));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 선재제품 스크랩실적 (SBYSJ006)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvSBYSJ006(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재제품 스크랩실적[GdsYsL3RcvSeEJB.rcvSBYSJ006] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try {
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlNo    = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"    )); //재료번호
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(stlNo) ) {
				throw new Exception("잘못된 재료번호[" + stlNo + "] 입니다.");
			}

			/**********************************************************
			* 2. 저장품 등록
			**********************************************************/
			JDTORecordSet rsResult			= null;
			JDTORecord recPara				= null;
			JDTORecord recTemp				= null;

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("SSTL_NO", stlNo);
			recPara.setField("YD_STK_LYR_MTL_STAT", "C");
			
			rsResult = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrSTLNO", logId, methodNm, "적재위치조회 조회");
			if (rsResult.size() <= 0) {

				return jrRtn;
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			String szYsStkColGp 		= commUtils.trim(recTemp.getFieldString("YS_STK_COL_GP"));
			String szYsStkBedNo 		= commUtils.trim(recTemp.getFieldString("YS_STK_BED_NO"));
			String szYsStkLyrNo 		= commUtils.trim(recTemp.getFieldString("YS_STK_LYR_NO"));
			String szYsStkSeqNo 		= commUtils.trim(recTemp.getFieldString("YS_STK_SEQ_NO"));
			
			if(szYsStkColGp.startsWith("KX")){
				
				JDTORecord jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setField("FNL_REG_PGM"			, "WrScrapDelete" );
				jrParam.setField("YD_GP"				, "_" );
				jrParam.setField("YD_BAY_GP"			, szYsStkColGp.substring(1,2) );
				jrParam.setField("YD_EQP_GP"			, szYsStkColGp.substring(2,4) );
				jrParam.setField("YS_STK_COL_NO"		, szYsStkColGp.substring(4,6) );
				jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
				jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
				jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
				
				jrParam.setField("YS_STR_LOC"			, "_" + szYsStkColGp.substring(1,6) + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
				jrParam.setField("SSTL_NO"				, stlNo ); //삭제된 번호
				jrParam.setField("MODIFIER"				, modifier );
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
				
				// 저장위치 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.updYdStockStlClear", logId, methodNm, "저장위치 삭제");
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
	 *      [A] 오퍼레이션명 : 특수강포항이송재 이송지시(PBYSJ001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPBYSJ001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "특수강포항이송재 이송지시[GdsYsL3RcvSeEJB.rcvPBYSJ001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn		= null;
		String sChkStlNo		= "";

// 2026.03.19 포항 이송재(TB_YS_POHANG_MOVE_REQ) 테이블 등록 추가
		String stlNos = "";
		
		jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String cancelYn		= commUtils.trim(rcvMsg.getFieldString("TC_GP")); //취소유무(I:신규, D:삭제(취소))
			int iydEqpWrkSh		= Integer.parseInt(commUtils.trim(rcvMsg.getFieldString("BNDL_CNT"))); //야드설비작업매수

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (0 == iydEqpWrkSh ) {
				throw new Exception("재료매수가 빈 값 입니다.");
			}
			if ("".equals(cancelYn) ) {
				throw new Exception("취소 유무가 빈 값 입니다.");
			}
			
			if("D".equals(cancelYn)){
				cancelYn = "Y";
			}else{
				cancelYn = "N";
			}
			
			//상차개시 전문
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
	    		
    		for(int index = 0; index < iydEqpWrkSh; index++){
    			
    			sChkStlNo =  commUtils.trim(rcvMsg.getFieldString("BNDL_NO"+(index+1))); 

    			if("".equals(sChkStlNo)) break;

// 2026.03.19 포항 이송재(TB_YS_POHANG_MOVE_REQ) 테이블 등록 추가
				stlNos += sChkStlNo + ",";	// 재료번호
    			
    			jrYdMsg.setField("DEL_YN"  , cancelYn);
				jrYdMsg.setField("SSTL_NO" , sChkStlNo);
			
				//특수강재공야드L2로 전문 전송
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L004", jrYdMsg));
    		}

//////////////////////////////////////////////////////////////////////////////////////////////
// 2026.03.19 포항 이송재(TB_YS_POHANG_MOVE_REQ) 테이블 등록 추가
			commUtils.printLog(logId, "포항 이송재 재료 번호들 [" + stlNos + "]", "");
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			jrParam.setResultCode(logId);						// Log ID
			jrParam.setResultMsg(methodNm);						// Log Method Name
			
			jrParam.setField("SSTL_NO"		, stlNos		);	// 재료번호
			jrParam.setField("MODIFIER"		, "PBYSJ001"	); 	// 수정자
			jrParam.setField("DEL_YN"		, cancelYn		); 	// 삭제유무

			commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.insPohangMoveReq", logId, methodNm, "포항 이송재 재료 정보 등록 및 갱신");
//////////////////////////////////////////////////////////////////////////////////////////////
    		
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 특수강시편채취 전송지시(QSYSJ301)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvQSYSJ301(JDTORecord rcvMsg) throws DAOException {  
		String methodNm = "특수강시편채취 전송지시[GdsYsL3RcvSeEJB.rcvQSYSJ301] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn		= null;
		String sChkStlNo		= "";
		
		jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			String HEAT_NO= commUtils.trim(rcvMsg.getFieldString("HEAT_NO"));
			String MATL_LENGTH= commUtils.trim(rcvMsg.getFieldString("MATL_LENGTH"));
			String MATL_NO= commUtils.trim(rcvMsg.getFieldString("MATL_NO"));
			String MATL_SIZE= commUtils.trim(rcvMsg.getFieldString("MATL_SIZE"));
			String MATL_WEIGHT= commUtils.trim(rcvMsg.getFieldString("MATL_WEIGHT"));
			String REAGENT_NO= commUtils.trim(rcvMsg.getFieldString("REAGENT_NO"));
			String REAGENT_PICK_LENGTH= commUtils.trim(rcvMsg.getFieldString("REAGENT_PICK_LENGTH"));
			String REAGENT_PICK_TARGET_YN= commUtils.trim(rcvMsg.getFieldString("REAGENT_PICK_TARGET_YN"));
			String REMARK_MSG= commUtils.trim(rcvMsg.getFieldString("REMARK_MSG"));
			String STL_APPEAR_GP= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));
			
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setField("HEAT_NO"  , HEAT_NO);
			jrYdMsg.setField("MATL_LENGTH"  , MATL_LENGTH);
			jrYdMsg.setField("MATL_NO"  , MATL_NO);
			jrYdMsg.setField("MATL_SIZE"  , MATL_SIZE);
			jrYdMsg.setField("MATL_WEIGHT"  , MATL_WEIGHT);
			jrYdMsg.setField("REAGENT_NO"  , REAGENT_NO);
			jrYdMsg.setField("REAGENT_PICK_LENGTH"  , REAGENT_PICK_LENGTH);
			jrYdMsg.setField("REAGENT_PICK_TARGET_YN"  , REAGENT_PICK_TARGET_YN);
			jrYdMsg.setField("REMARK_MSG"  , REMARK_MSG);
			jrYdMsg.setField("STL_APPEAR_GP"  , STL_APPEAR_GP); 
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L005", jrYdMsg));
			//특수강재공야드L2로 전문 전송
			
			//String cancelYn		= commUtils.trim(rcvMsg.getFieldString("TC_GP")); //취소유무(I:신규, D:삭제(취소))
			//int iydEqpWrkSh		= Integer.parseInt(commUtils.trim(rcvMsg.getFieldString("BNDL_CNT"))); //야드설비작업매수

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			//if (0 == iydEqpWrkSh ) {
		//		throw new Exception("재료매수가 빈 값 입니다.");
		//	}
	//		if ("".equals(cancelYn) ) {
	//			throw new Exception("취소 유무가 빈 값 입니다.");
	//		}
			
		//	if("D".equals(cancelYn)){
		//		cancelYn = "Y";
		//	}else{
		//		cancelYn = "N";
		//	}
			
			//상차개시 전문
		//	JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
	    		
    	//	for(int index = 0; index < iydEqpWrkSh; index++){
    			
    	//		sChkStlNo =  commUtils.trim(rcvMsg.getFieldString("BNDL_NO"+(index+1))); 

    	//		if("".equals(sChkStlNo)) break;
    			
    	//		jrYdMsg.setField("DEL_YN"  , cancelYn);
		//		jrYdMsg.setField("SSTL_NO" , sChkStlNo);
			
				//특수강재공야드L2로 전문 전송
		//		jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L004", jrYdMsg));
    	//	}
    		
				 
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}		
	
}