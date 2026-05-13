/**
 * @(#)YsCommCarMvSeEJBBean.java
 * 
 * @version			1.0
 * @author 			현대제철
 * @date			2015/11/14
 * 
 * @description		이클래스는 차량이동처리 Session EJB클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.01  2013/04/03   송정현     
 * 차량이동처리 Session EJB
 * rcvTSYSJ002 					: 소재차량도착Point요구(TSYSJ002)
 * rcvTSYSJ003 					: 소재차량도착(TSYSJ003)
 * rcvTSYSJ004 					: 소재차량출발(TSYSJ004)
 * rcvTSYSJ005 					: 서문스크랩상차실적(TSYSJ005)
 * procLDMatlCarArr 			: 소재차량 공차도착 실적
 * procUDMatlCarArr 			: 소재차량 영차도착 실적
 * procCallCrnSch 				: 크레인스케줄호출
 * procInsWrkBookCarUd 			: 하차작업예약생성
 * procInsCarSch 			    : 차량스케쥴 편성 및 상차Lot편성 호출 
 * procCarPosActiveOrInActive 	: 차량정지위치활성/비활성처리
 * procOutCarLevWr 				: 출하차량출발실적 처리 
 * YsCarPointinforeg2 : 차량 포인트 통합관리 YD 와 동일
 */	

package com.inisteel.cim.ys.common.session;


import com.inisteel.cim.cm.message.MessageSenderAuto;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ys.message.MessageSenderTalk;
import com.inisteel.cim.ysPI.dao.YsPiDAO;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.common.util.YsConstant;
import com.inisteel.cim.ys.gds.session.GdsYsComm; 

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;


/**
 * 차량이동처리 Session EJB
 *
 * @ejb.bean name="YsCommCarMvSeEJB" jndi-name="YsCommCarMvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
 */
public class YsCommCarMvSeEJBBean extends BaseSessionBean { 
	private static final long serialVersionUID = 1L;
	private YsCommUtils commUtils = new YsCommUtils();
	private YsCommDAO commDao = new YsCommDAO();
	private GdsYsComm gdsYsComm = new GdsYsComm();
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 *      [A] 오퍼레이션명 : 소재차량도착Point요구(TSYSJ002) YSYSJ901 호출시 기동
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYSJ002(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "소재차량도착Point요구[YsCommCarMvSeEJB.rcvTSYSJ002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		JDTORecordSet 	rsResult    = null;
		JDTORecord	  	sndRecord	= JDTORecordFactory.getInstance().create();
		JDTORecord    	recInTemp   = null;
		JDTORecord    	recInTemp1  = null;
		JDTORecord    	recOutTemp  = null;
		JDTORecord    	recOutTemp1 = null;
		JDTORecord    	recSndPnt   = null;


	    int intRtnVal 				= 0 ;
	    
	    String szMsg           		= "";
	    String szOperationName		= "소재차량도착Point요구";
	    String szMethodName     	= "procMatlCarLev";
	    
	    String szYS_STK_COL_GP 		= "";
	    String szTRN_EQP_CD    		= "";						//운송장비코드
	    String szWLOC_CD       		= "";						//개소코드
	    String szYD_CAR_SCH_ID 		= "";
	    String szYD_PNT_CD     		= "";
	    String szYD_CAR_PROG_STAT	= "";
	    String szTRN_WRK_FULLVOID_GP= "";					//운송작업영공구분
	    String szYD_STK_COL_ACT_STAT= "";
	    String szLAST_TRN_EQP_CD_LOC= "";
	    String szCOL_WLOC_CD 		= "";		    
	    String szSPOS_WLOC_CD 		= ""; // 각강체크를 위함 - WC
	    
        String msgId = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
        
        if(msgId==null || msgId.equals("")){
        	return sndRecord;
        }

        szMsg="["+szOperationName+"] 전문수신 : TCCODE=" +msgId ;
        
	    try{

			commUtils.printLog(logId, methodNm, "S+");
			
			commUtils.printParam(logId + "소재차량도착Point요구 수신 ", rcvMsg);
			
	    	//운송장비코드, 개소코드, 상하차구분코드, 포인트요구일시
			szTRN_EQP_CD      		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"));
			szWLOC_CD      			= commUtils.trim(rcvMsg.getFieldString("WLOC_CD")); 
			szTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); 
  
			//포인트지시 메세지 전송 준비
			//포인트 결정 하면 됨
			recSndPnt = JDTORecordFactory.getInstance().create();

			recSndPnt.setResultCode(logId);	//Log ID
			recSndPnt.setResultMsg(methodNm);	//Log Method Name
			recSndPnt.setField("JMS_TC_CD"			, "YSTSJ011");
			recSndPnt.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
			recSndPnt.setField("TRN_EQP_CD"			, szTRN_EQP_CD);
			recSndPnt.setField("WLOC_CD"			, szWLOC_CD);
			recSndPnt.setField("PNT_WO_GP"			, "A");
			recSndPnt.setField("PNT_WO_DT"			, commUtils.getDateTime14());
/*			
			recSndPnt.setField("YD_PNT_CD"			, "0000");               
			sndRecord = commUtils.addSndData(sndRecord,recSndPnt);
*/			
			String szYD_GP = commUtils.getWlocToYdGp(szWLOC_CD);
			
			/**********************************************************
			* 1. 운송장비코드로 차량스케줄을 조회한다.
			**********************************************************/			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("TRN_EQP_CD", szTRN_EQP_CD);
	    	
	    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschDaoTrnEqpCd 
	    	SELECT *
	    	  FROM (
	    	        SELECT YD_CAR_SCH_ID        AS YD_CAR_SCH_ID                          
	    	             , REGISTER             AS REGISTER
	    	             , TO_CHAR(REG_DDTT,'YYYYMMDDHH24MISS') AS REG_DDTT
	    	             , MODIFIER             AS MODIFIER
	    	             , TO_CHAR(MOD_DDTT,'YYYYMMDDHH24MISS') AS MOD_DDTT
	    	             , DEL_YN               AS DEL_YN
	    	             , YD_EQP_ID            AS YD_EQP_ID
	    	             , YD_CAR_USE_GP        AS YD_CAR_USE_GP
	    	             , CAR_NO               AS CAR_NO
	    	             , TRN_EQP_CD           AS TRN_EQP_CD
	    	             , CAR_KIND             AS CAR_KIND
	    	             , TRANS_EQUIPMENT_TYPE AS TRANS_EQUIPMENT_TYPE
	    	             , YD_EQP_WRK_STAT      AS YD_EQP_WRK_STAT
	    	             , YD_WRK_PROG_STAT     AS YD_WRK_PROG_STAT
	    	             , YD_EQP_WRK_SH        AS YD_EQP_WRK_SH
	    	             , YD_EQP_WRK_WT        AS YD_EQP_WRK_WT
	    	             , YS_STK_BED_TP        AS YS_STK_BED_TP
	    	             , SPOS_WLOC_CD         AS SPOS_WLOC_CD
	    	             , ARR_WLOC_CD          AS ARR_WLOC_CD
	    	             , YD_CARLD_LEV_LOC     AS YD_CARLD_LEV_LOC
	    	             , TO_CHAR(YD_CARLD_LEV_DT,'YYYYMMDDHH24MISS') AS YD_CARLD_LEV_DT
	    	             , TO_CHAR(YD_CARLD_PNT_WO_DT,'YYYYMMDDHH24MISS') AS YD_CARLD_PNT_WO_DT
	    	             , NVL(YD_PNT_CD1,'0000') AS YD_PNT_CD1
	    	             , YD_PNT_CD2           AS YD_PNT_CD2
	    	             , YD_CARLD_WRK_BOOK_ID AS YD_CARLD_WRK_BOOK_ID
	    	             , YD_CARLD_SCH_REQ_GP  AS YD_CARLD_SCH_REQ_GP
	    	             , YD_CARLD_STOP_LOC    AS YD_CARLD_STOP_LOC
	    	             , TO_CHAR(YD_CARLD_ARR_DT,'YYYYMMDDHH24MISS') AS YD_CARLD_ARR_DT
	    	             , TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT
	    	             , TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS') AS YD_CARLD_CMPL_DT
	    	             , YD_CARLD_WRK_ACT_GP  AS YD_CARLD_WRK_ACT_GP
	    	             , TO_CHAR(YD_CARLD_CHK_DT,'YYYYMMDDHH24MISS') AS YD_CARLD_CHK_DT
	    	             , TO_CHAR(YD_CARUD_LEV_DT,'YYYYMMDDHH24MISS') AS YD_CARUD_LEV_DT
	    	             , TO_CHAR(YD_CARUD_PNT_WO_DT,'YYYYMMDDHH24MISS') AS YD_CARUD_PNT_WO_DT
	    	             , NVL(YD_PNT_CD3,'0000') AS YD_PNT_CD3
	    	             , YD_PNT_CD4           AS YD_PNT_CD4
	    	             , YD_CARUD_WRK_BOOK_ID AS YD_CARUD_WRK_BOOK_ID
	    	             , YD_CARUD_STOP_LOC    AS YD_CARUD_STOP_LOC
	    	             , YD_CARUD_SCH_REQ_GP  AS YD_CARUD_SCH_REQ_GP
	    	             , TO_CHAR(YD_CARUD_ARR_DT,'YYYYMMDDHH24MISS') AS YD_CARUD_ARR_DT
	    	             , TO_CHAR(YD_CARUD_CHK_DT,'YYYYMMDDHH24MISS') AS YD_CARUD_CHK_DT
	    	             , TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS') AS YD_CARUD_ST_DT
	    	             , TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS') AS YD_CARUD_CMPL_DT
	    	             , YD_CARUD_WRK_ACT_GP  AS YD_CARUD_WRK_ACT_GP
	    	             , YD_TRN_WRK_DELY_CD   AS YD_TRN_WRK_DELY_CD
	    	             , CARD_NO              AS CARD_NO
	    	             , YD_CAR_PROG_STAT     AS YD_CAR_PROG_STAT
	    	             , FRTOMOVE_PLANT_GP    AS FRTOMOVE_PLANT_GP      
	    	             , PROC_TO              AS PROC_TO                
	    	             , RENTPROC_CD          AS RENTPROC_CD            
	    	             , YD_FRTOMOVE_YD_GP    AS YD_FRTOMOVE_YD_GP      
	    	             , YD_FRTOMOVE_BAY_GP   AS YD_FRTOMOVE_BAY_GP     
	    	             , URGENT_FRTOMOVE_WORD_GP AS URGENT_FRTOMOVE_WORD_GP
	    	             , DEST_TEL_NO          AS DEST_TEL_NO            
	    	             , YD_DLVRDD_RULE_DD    AS YD_DLVRDD_RULE_DD      
	    	             , SHIPASSIGN_WORD_DATE AS SHIPASSIGN_WORD_DATE      
	    	             , SHIPASSIGN_WORD_SEQNO AS SHIPASSIGN_WORD_SEQNO   
	    	             , SHIP_CD              AS SHIP_CD                
	    	             , SHIP_NAME            AS SHIP_NAME              
	    	             , RSHP_HOLD_NO         AS RSHP_HOLD_NO           
	    	             , BERTH_NO             AS BERTH_NO               
	    	             , SAILNO               AS SAILNO                 
	    	             , YD_CAR_WRK_GP        AS YD_CAR_WRK_GP          
	    	             , TRANS_ORD_DATE       AS TRANS_ORD_DATE         
	    	             , TRANS_ORD_SEQNO      AS TRANS_ORD_SEQNO   
	    	             , (SELECT YD_STKBED_USG_CD 
	    	                  FROM TB_YS_STKCOL B 
	    	                 WHERE B.YS_STK_COL_GP=A.YD_CARLD_STOP_LOC 
	    	                   AND B.YD_STKBED_USG_CD IN ('A','D','E')
	    	               ) AS NEW_DEST_BAY
	    	             , (SELECT YD_CARUD_STOP_LOC
	    	                  FROM TB_YS_CARSCH C
	    	                 WHERE C.TRN_EQP_CD = A.TRN_EQP_CD 
	    	                   AND C.YD_CAR_SCH_ID = (SELECT MAX(YD_CAR_SCH_ID)
	    	                                            FROM TB_YS_CARSCH 
	    	                                           WHERE YD_CAR_PROG_STAT = 'E' 
	    	                                             AND DEL_YN = 'Y'
	    	                                             AND TRN_EQP_CD = C.TRN_EQP_CD 
	    	                                         )    
	    	               ) AS LAST_TRN_EQP_CD_LOC            
	    	          FROM TB_YS_CARSCH A                                  
	    	         WHERE TRN_EQP_CD = :V_TRN_EQP_CD            
	    	           AND DEL_YN='N'
	    	         ORDER BY YD_CAR_SCH_ID DESC , YD_CARUD_CMPL_DT DESC
	    	         ) A
	    	 WHERE ROWNUM<=1
	    	 */	    	 
			rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschDaoTrnEqpCd", logId, methodNm, "차량스케줄을 조회"); 

			if (rsResult == null || rsResult.size() <= 0) {
				szMsg="["+methodNm+"] 운송장비코드["+szTRN_EQP_CD+"]로 차량스케줄 없음 ---> '0000' 포인트 송신";
				commUtils.printLog(logId, szMsg, "SL");
				
				recSndPnt.setField("YD_PNT_CD"			, "0000");               
				return sndRecord = commUtils.addSndData(sndRecord,recSndPnt);
				
			} else if (rsResult.size() > 1) {
				szMsg= "[" + methodNm + "] 차량스케줄 조회 시 오류발생 - 운송장비코드로 차량스케줄이 여러건["+rsResult.size()+"]이 존재합니다.---> '0000' 포인트 송신";
				commUtils.printLog(logId, szMsg, "SL");

				recSndPnt.setField("YD_PNT_CD"			, "0000");               
				return sndRecord = commUtils.addSndData(sndRecord,recSndPnt);
				
			}

			/**********************************************************
			* 2. 상차출발 / 하차출발 검사
			**********************************************************/		
			rsResult.absolute(1);			
			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(rsResult.getRecord());
			
			szYD_CAR_SCH_ID      	= commUtils.trim(recOutTemp.getFieldString("YD_CAR_SCH_ID")); 
			szYD_CAR_PROG_STAT    	= commUtils.trim(recOutTemp.getFieldString("YD_CAR_PROG_STAT")); 
			szLAST_TRN_EQP_CD_LOC	= commUtils.trim(recOutTemp.getFieldString("LAST_TRN_EQP_CD_LOC")); 
	    	
			// 각강체크를 위함 - WC
	        szSPOS_WLOC_CD          = commUtils.trim(recOutTemp.getFieldString("SPOS_WLOC_CD")); 
	        
	    	if( szYD_CAR_PROG_STAT.equals(YsConstant.YD_CARLD_LEV) ) {
				//상차출발
	    		szYS_STK_COL_GP     = commUtils.trim(recOutTemp.getFieldString("YD_CARLD_STOP_LOC")); 
	    		szYD_PNT_CD      	= commUtils.trim(recOutTemp.getFieldString("YD_PNT_CD1")); 

	    	} else if( szYD_CAR_PROG_STAT.equals(YsConstant.YD_CARUD_LEV) ) {
	    		//하차출발
	    		szYS_STK_COL_GP     = commUtils.trim(recOutTemp.getFieldString("YD_CARUD_STOP_LOC")); 
	    		szYD_PNT_CD      	= commUtils.trim(recOutTemp.getFieldString("YD_PNT_CD3")); 

	    	}  else {
	    		//상차출발/하차출발이 아니경우 ----> 대기
				commUtils.printLog(logId, szMsg, "SL");
				
				recSndPnt.setField("YD_PNT_CD"			, "0000");               
				return sndRecord = commUtils.addSndData(sndRecord,recSndPnt);	    		
	    	}
	    	
	    	szMsg="["+methodNm+"] 운송장비코드["+szTRN_EQP_CD+"]로 차량스케줄["+szYD_CAR_SCH_ID+"], 메세지msgId["+msgId+"] 차량스케줄포인트 ["+ szYD_PNT_CD + "] 차량진행상태 ["+szYD_CAR_PROG_STAT + "]";   	
			commUtils.printLog(logId, szMsg, "SL");

			/**********************************************************
			* 3. 가용포인트 조회 ( YSYSJ901 / TSYSJ002:0000 (대기장)
			*  - 특수강 이송은 LOT편성된 정보가 없으면 무조건 대기장으로 처리 함
			**********************************************************/		

	    	szMsg="["+methodNm+"] 개소코드["+szWLOC_CD+"] szYS_STK_COL_GP [" + szYS_STK_COL_GP+ "]";
			commUtils.printLog(logId, szMsg, "SL");
	    	  	
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			
	    	
			/**********************************************************
			* 4. 가용포인트 조회
			**********************************************************/
	    	boolean isReqCheck = false; // 각강입고/중복 포인트요구 체크를 위함 - WC
			    
	    	if (szSPOS_WLOC_CD.equals("S4Y20")&&!"S5Y10".equals(szWLOC_CD)) {
				//각광입고는 무조건  해당포인트
				szYD_PNT_CD 	= "2B01";
				szYS_STK_COL_GP = "KBTR21";
			} else {
				
				/**********************************************************
				* 4-1. 야드구분+동구분+설비구분+% ---> 적치열 검색
				**********************************************************/	
				//스케줄코드의 야드구분 동구분 설비구분으로 적치열을 LIKE 검색한다.
		    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		    	recInTemp1 = JDTORecordFactory.getInstance().create();
		    	recOutTemp1 = JDTORecordFactory.getInstance().create();
		    
		    	if(szYD_GP.equals("B") &&(szTRN_WRK_FULLVOID_GP.equals("E")) ) {
			    	
			    	recInTemp1.setField("WLOC_CD"		, szWLOC_CD);
			    	recInTemp1.setField("YD_GP"			, szYD_GP);
			    	recInTemp1.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
			    	recInTemp1.setField("LAST_TRN_EQP_CD_LOC", szLAST_TRN_EQP_CD_LOC);
			    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdGpFtmvLotNoPrior 
			    	SELECT AA.YS_STK_COL_GP
			    	     , AA.TRN_EQP_CD
			    	     , AA.WLOC_CD 
			    	     , AA.YD_PNT_CD
			    	     , AA.YD_CAR_USE_GP
			    	     , AA.YD_STK_COL_ACT_STAT
			    	     , BB.YD_PREP_SCH_ID
			    	     , BB.YD_SCH_CD
			    	     , BB.YD_GP
			    	     , SUBSTR(BB.YD_SCH_CD,2,1) AS YD_BAY_GP
			    	     , BB.YD_PREP_WK_ST
			    	     , BB.YD_AIM_BAY_GP
			    	     , BB.YD_CARASGN_SEQ
			    	  FROM
			    	       (
			    	        SELECT A.YS_STK_COL_GP
			    	             , A.YD_GP
			    	             , A.YD_BAY_GP
			    	             , A.YD_CAR_USE_GP
			    	             , A.TRN_EQP_CD
			    	             , A.CAR_NO
			    	             , A.CARD_NO
			    	             , A.WLOC_CD
			    	             , A.YD_PNT_CD
			    	             , A.YD_STK_COL_ACT_STAT
			    	          FROM TB_YS_STKCOL A
			    	         WHERE A.WLOC_CD LIKE NVL(SUBSTR(:V_WLOC_CD,1,3),'*') || '%'
			    	           AND A.DEL_YN='N'
			    	           AND A.YD_STK_COL_ACT_STAT <> 'N'
			    	           AND A.YD_CAR_USE_GP IS NULL
			    	           AND NOT EXISTS ( SELECT YD_CARLD_STOP_LOC 
			    	                              FROM TB_YS_CARSCH  
			    	                             WHERE DEL_YN='N'
			    	                               AND YD_CAR_PROG_STAT IN ('1','2','3','4','5')
			    	                               AND YD_CARLD_STOP_LOC=A.YS_STK_COL_GP)
			    	        UNION ALL     
			    	        SELECT A.YS_STK_COL_GP
			    	             , A.YD_GP
			    	             , A.YD_BAY_GP
			    	             , A.YD_CAR_USE_GP
			    	             , A.TRN_EQP_CD
			    	             , A.CAR_NO
			    	             , A.CARD_NO
			    	             , A.WLOC_CD
			    	             , A.YD_PNT_CD
			    	             , A.YD_STK_COL_ACT_STAT
			    	          FROM TB_YS_STKCOL A
			    	         WHERE A.WLOC_CD LIKE NVL(SUBSTR(:V_WLOC_CD,1,3),'*') || '%'
			    	           AND A.DEL_YN='N'
			    	           AND A.TRN_EQP_CD = :V_TRN_EQP_CD      --동일차량 수신시 동일정보 송신을 위해
			    	          
			    	       ) AA
			    	     , (
			    	         SELECT A.YD_PREP_SCH_ID
			    	              , A.YD_SCH_CD
			    	              , A.YD_GP
			    	              , SUBSTR(A.YD_SCH_CD,2,1) AS YD_BAY_GP
			    	              , A.YD_PREP_WK_ST
			    	              , A.YD_AIM_BAY_GP
			    	              , A.YD_CARASGN_SEQ
			    	           FROM USRYSA.TB_YS_PREPSCH A
			    	          WHERE A.YD_GP = :V_YD_GP
			    	            AND A.YD_SCH_CD LIKE  '%'||'TR02UM'|| '%'  -- 이송SCH만
			    	            AND A.DEL_YN = 'N'
			    	            AND A.CAR_GP IS NULL
			    	       ) BB
			    	 WHERE AA.YD_GP = BB.YD_GP
			    	   AND AA.YD_BAY_GP = BB.YD_BAY_GP
			    	 ORDER BY BB.YD_CARASGN_SEQ
			    	        , DECODE(SUBSTR(:V_LAST_TRN_EQP_CD_LOC,1,2), SUBSTR(AA.YS_STK_COL_GP,1,2),'1','2')
			    	        , BB.YD_PREP_SCH_ID, AA.YS_STK_COL_GP
			    	 */
			    	rsResult = commDao.select(recInTemp1, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdGpFtmvLotNoPrior", logId, methodNm, "적치열 조회");
			    	
		    	} else if(szYD_GP.equals("C") &&(szTRN_WRK_FULLVOID_GP.equals("E")) ) {
		    		
		    		recInTemp1.setField("WLOC_CD"		, szWLOC_CD);
			    	recInTemp1.setField("YD_GP"			, szYD_GP);
			    	recInTemp1.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
 
			    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdGpFtmvLotNoPriorBt 
						WITH DATA_TABLE AS 
						(
						        SELECT AA.YS_STK_COL_GP
						             , AA.TRN_EQP_CD
						             , AA.WLOC_CD 
						             , AA.YD_PNT_CD
						             , AA.YD_CAR_USE_GP
						             , AA.YD_STK_COL_ACT_STAT
						             , BB.YD_PREP_SCH_ID
						             , BB.YD_SCH_CD
						             , BB.YD_GP
						             , SUBSTR(BB.YD_SCH_CD,2,1) AS YD_BAY_GP
						             , BB.YD_PREP_WK_ST
						             , BB.YD_AIM_BAY_GP
						             , BB.YD_CARASGN_SEQ
						             , (SELECT DECODE(ITEM,'C',0,'A',DECODE(SUBSTR(AA.YS_STK_COL_GP,2,1),'A',1,2),'B',DECODE(SUBSTR(AA.YS_STK_COL_GP,2,1),'B',1,2)) 
						                  FROM TB_YS_RULE 
						                 WHERE REPR_CD_GP = 'CXTR01' 
						                   AND ROWNUM = 1
						               ) AS RULE_BAY
						          FROM
						               (
						                SELECT A.YS_STK_COL_GP
						                     , A.YD_GP
						                     , A.YD_BAY_GP
						                     , A.YD_CAR_USE_GP
						                     , A.TRN_EQP_CD
						                     , A.CAR_NO
						                     , A.CARD_NO
						                     , A.WLOC_CD
						                     , A.YD_PNT_CD
						                     , A.YD_STK_COL_ACT_STAT
						                  FROM TB_YS_STKCOL A
						                 WHERE A.WLOC_CD = :V_WLOC_CD
						                   AND A.DEL_YN  = 'N'
						                   AND A.YD_STK_COL_ACT_STAT <> 'N'
						                   AND A.TRN_EQP_CD IS NULL
						                   AND NOT EXISTS ( SELECT YD_CARLD_STOP_LOC 
						                                      FROM TB_YS_CARSCH  
						                                     WHERE DEL_YN = 'N'
						                                       AND YD_CAR_PROG_STAT IN ('1','2','3','4','5')
						                                       AND YD_CARLD_STOP_LOC = A.YS_STK_COL_GP)
						                UNION ALL     
						                SELECT A.YS_STK_COL_GP
						                     , A.YD_GP
						                     , A.YD_BAY_GP
						                     , A.YD_CAR_USE_GP
						                     , A.TRN_EQP_CD
						                     , A.CAR_NO
						                     , A.CARD_NO
						                     , A.WLOC_CD
						                     , A.YD_PNT_CD
						                     , A.YD_STK_COL_ACT_STAT
						                  FROM TB_YS_STKCOL A
						                 WHERE A.WLOC_CD    = :V_WLOC_CD
						                   AND A.DEL_YN     = 'N'
						                   AND A.TRN_EQP_CD = :V_TRN_EQP_CD      --동일차량 수신시 동일정보 송신을 위해
						                  
						               ) AA
						             , (
						                 SELECT A.YD_PREP_SCH_ID
						                      , A.YD_SCH_CD
						                      , A.YD_GP
						                      , SUBSTR(A.YD_SCH_CD,2,1) AS YD_BAY_GP
						                      , A.YD_PREP_WK_ST
						                      , A.YD_AIM_BAY_GP
						                      , A.YD_CARASGN_SEQ
						                   FROM USRYSA.TB_YS_PREPSCH A
						                  WHERE A.YD_GP     = :V_YD_GP
						                    AND A.YD_SCH_CD LIKE  '%'||'TR02UM'|| '%'  -- 이송SCH만
						                    AND A.DEL_YN    = 'N'
						                    AND A.CAR_GP IS NULL
						               ) BB
						         WHERE AA.YD_GP     = BB.YD_GP(+)
						           AND AA.YD_BAY_GP = BB.YD_BAY_GP(+)
						 )
						 SELECT * 
						   FROM DATA_TABLE DA
						 ORDER BY DECODE(TRN_EQP_CD,:V_TRN_EQP_CD,1,2)
						        , RULE_BAY
						        , YD_CARASGN_SEQ
						        , YD_PREP_SCH_ID
						        , YS_STK_COL_GP
			    	 */
			    	rsResult = commDao.select(recInTemp1, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdGpFtmvLotNoPriorBt", logId, methodNm, "적치열 조회");
			    	
		    	} else {
		    	
			    	recInTemp1.setField("WLOC_CD"		, szWLOC_CD);
			    	recInTemp1.setField("YS_STK_COL_GP"	, szYS_STK_COL_GP);
			    	
			    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolColGpLike
			    	SELECT YS_STK_COL_GP
			    	      ,REG_DDTT
			    	      ,REGISTER
			    	      ,MOD_DDTT
			    	      ,MODIFIER
			    	      ,DEL_YN
			    	      ,YD_GP
			    	      ,YD_BAY_GP
			    	      ,YD_EQP_GP
			    	      ,YD_STK_COL_NO
			    	      ,YD_STK_COL_ACT_STAT
			    	      ,YD_STK_COL_RULE_YAXIS
			    	      ,YD_STK_COL_W
			    	      ,YD_STK_COL_L
			    	      ,YD_CAR_USE_GP
			    	      ,TRN_EQP_CD
			    	      ,CAR_NO
			    	      ,CARD_NO
			    	      ,WLOC_CD
			    	      ,YD_PNT_CD
			    	  FROM TB_YS_STKCOL A
			    	  WHERE WLOC_CD LIKE NVL(:V_WLOC_CD,'*') || '%'
			    	   AND YS_STK_COL_GP LIKE NVL(:V_YS_STK_COL_GP,'') || '%' 
			    	   AND DEL_YN='N'
			    	   AND YD_STK_COL_ACT_STAT<>'N'
			    	 ORDER BY YS_STK_COL_GP
			    	 */
			    	rsResult = commDao.select(recInTemp1, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolColGpLike", logId, methodNm, "적치열 조회"); 
		    	}
		    	
		    	String szCOL_TRN_EQP_CD = "";						//적치열에 이미 등록된 운송장비코드
		    	String szYD_PREP_SCH_ID = "";						
		    	
		    	if (rsResult == null || rsResult.size() <= 0) {
		    		szMsg="["+methodNm+"] 적치열 조회 getYdStkcol data not found";
					commUtils.printLog(logId, szMsg, "SL");
					recSndPnt.setField("YD_PNT_CD"			, "0000");
					return sndRecord = commUtils.addSndData(sndRecord,recSndPnt);
				} else {
	
					/**********************************************************
					* 4-1-2. 중복 포인트 요구 체크
					*        --->   기존 차량정보로 송신 처리 함
					**********************************************************/	
					for (int i = 1; i <= rsResult.size(); i++) {
						rsResult.absolute(i);
						recOutTemp1	= rsResult.getRecord();
						
						szCOL_TRN_EQP_CD = commUtils.trim(recOutTemp1.getFieldString("TRN_EQP_CD"));
						szCOL_WLOC_CD    = commUtils.trim(recOutTemp1.getFieldString("WLOC_CD"));
					
						if (szCOL_TRN_EQP_CD.equals(szTRN_EQP_CD)){
							isReqCheck = true;		
							szYD_PNT_CD    	  	= commUtils.trim(recOutTemp1.getFieldString("YD_PNT_CD")); 
					    	szYS_STK_COL_GP   	= commUtils.trim(recOutTemp1.getFieldString("YS_STK_COL_GP"));
							break;
						}
					}
	
					/**********************************************************
					* 4-1-3. 적치열 검색 성공 시 / 적치가능여부 체크
					**********************************************************/	
					if(!isReqCheck) { // 중복포인트요구가 아닐 경우					
			    		for(int i = 1; i <= rsResult.size(); i++ ) {
			    			rsResult.absolute(i);
			    			recOutTemp1			= rsResult.getRecord();
			    			
					    	szYD_PNT_CD    		  	= commUtils.trim(recOutTemp1.getFieldString("YD_PNT_CD")); 
					    	szYS_STK_COL_GP 	  	= commUtils.trim(recOutTemp1.getFieldString("YS_STK_COL_GP"));
					    	szYD_STK_COL_ACT_STAT 	= commUtils.trim(recOutTemp1.getFieldString("YD_STK_COL_ACT_STAT"));
					    	szCOL_TRN_EQP_CD		= commUtils.trim(recOutTemp1.getFieldString("TRN_EQP_CD")); 
					    	szYD_PREP_SCH_ID		= commUtils.trim(recOutTemp1.getFieldString("YD_PREP_SCH_ID")); 
					    	szCOL_WLOC_CD   		= commUtils.trim(recOutTemp1.getFieldString("WLOC_CD"));
					    	
					    	if(	szYD_STK_COL_ACT_STAT.equals("C")) {
					    		if( szCOL_TRN_EQP_CD.equals("") ) {
				    			
					    			recInTemp1 = JDTORecordFactory.getInstance().create();
					    			recInTemp1.setField("YS_STK_COL_GP"	, szYS_STK_COL_GP);
					    	    	recInTemp1.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
					    	    	recInTemp1.setField("YD_CAR_USE_GP"	, "L");
					    	    	recInTemp1.setField("MODIFIER"		, szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
					    	    	recInTemp1.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
					    	    	recInTemp1.setField("YD_STK_COL_ACT_STAT"	, szYD_STK_COL_ACT_STAT);
					    	    	
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
							    	intRtnVal = commDao.update(recInTemp1, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStat", logId, methodNm, "TB_YS_STKCOL 등록");				    	    	
					    	    	
					    	    	szMsg="["+szOperationName+"] 적치열구분["+szYS_STK_COL_GP+"] - 운송장비코드["+szTRN_EQP_CD+"] 예약 성공 - 포인트 지시 : ["+szYD_PNT_CD+"]";
					    	    	commUtils.printLog(logId, szMsg, "SL");
					    	    	
					    	    	if(szYD_GP.equals("B") &&(szTRN_WRK_FULLVOID_GP.equals("E")) ) {
						    	    	/* 준비스케줄 포인트지시 여부 -- com.inisteel.cim.ys.common.dao.YsCommDAO.updPrepSchPntWord 
	
						    	    	UPDATE TB_YS_PREPSCH
						    	    	   SET MODIFIER       = :V_MODIFIER
						    	    	     , MOD_DDTT       = SYSDATE
						    	    	     , CAR_GP         = '1'       -- 포인트 지시 송신
						    	    	 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
						    	    	   AND DEL_YN         = 'N'
						    	    	*/	   
								    	intRtnVal = commDao.update(recInTemp1, "com.inisteel.cim.ys.common.dao.YsCommDAO.updPrepSchPntWord", logId, methodNm, "TB_YS_STKCOL 등록");				    	    	
						    	    	
						    	    	szMsg="["+szOperationName+"] 적치열구분["+szYS_STK_COL_GP+"] - 운송장비코드["+szTRN_EQP_CD+"] 예약 성공 - 포인트 지시 : ["+szYD_PNT_CD+"]";
						    	    	commUtils.printLog(logId, szMsg, "SL");
					    	    	}	
					    	    	//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					    	    	YsCarPointinforeg2("3","",szTRN_EQP_CD,szYS_STK_COL_GP,"","","R",logId,methodNm);
					    			
					    			break;
					    		}else{
					    			szMsg="["+szOperationName+"] 적치열구분["+szYS_STK_COL_GP+"] - 운송장비코드["+szCOL_TRN_EQP_CD+"]로 예약되어 있음";
					    			commUtils.printLog(logId, szMsg, "SL");
					    		}
					    	}else{
					    		szMsg="["+szOperationName+"] 적치열구분["+szYS_STK_COL_GP+"] - 운송장비코드["+szTRN_EQP_CD+"], 적치열의 활성상태["+szYD_STK_COL_ACT_STAT+"]가 비활성화[C]가 아니므로 사용불가";
					    		commUtils.printLog(logId, szMsg, "SL");
					    	}
					    	 
					    	
					    	if(i == rsResult.size()) {
					    		szYS_STK_COL_GP = "";
				    			szYD_PNT_CD = "0000";
				    			
				    			szMsg="["+szOperationName+"] 차량도착위치 기본동에 가용한 차량도착Point가 존재하지 않으므로 대기장으로 지시";
				    			commUtils.printLog(logId, szMsg, "SL");
					    	}
			    		}
					}
				}
			}	
			szMsg="=========검색된 szCOL_WLOC_CD : " + szCOL_WLOC_CD + "===포인트 : "+ szYD_PNT_CD + "======";
			commUtils.printLog(logId, szMsg, "SL");
			
			recInTemp.setField("TRN_WRK_FULLVOID_GP"		, szTRN_WRK_FULLVOID_GP); 		//영공구분
			/**********************************************************
			* 4-1-3. 차량스케줄에 포인트코드를 등록
			**********************************************************/	
			 // 차량스케줄에 포인트코드를 등록하고 소재차량Point지시 전문 전송
			if(szTRN_WRK_FULLVOID_GP.equals("E") ) {
				//공차인경우
				recInTemp.setField("SPOS_WLOC_CD"		, szCOL_WLOC_CD); 				//발지개소코드
				recInTemp.setField("YD_CARLD_STOP_LOC"	, szYS_STK_COL_GP); 		//상차정지위치
				recInTemp.setField("YD_CARLD_PNT_WO_DT"	, commUtils.getDateTime14()); 	//상차point지시일시
				recInTemp.setField("YD_PNT_CD1"			, szYD_PNT_CD);
				szWLOC_CD = szCOL_WLOC_CD;                                             //포인트지시 메세지 전송시 필요 

			} else if(szTRN_WRK_FULLVOID_GP.equals("F") ) {
				//영차인경우
				recInTemp.setField("ARR_WLOC_CD"		, szWLOC_CD);  		     	//착지개소코드
				recInTemp.setField("YD_CARUD_STOP_LOC"	, szYS_STK_COL_GP); 		//하차정지위치
				recInTemp.setField("YD_CARUD_PNT_WO_DT"	, commUtils.getDateTime14()); 	//하차point지시일시
				recInTemp.setField("YD_PNT_CD3"			, szYD_PNT_CD);
			}
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarschByWloc 

			UPDATE TB_YS_CARSCH
			   SET MODIFIER     = :V_MODIFIER
			     , MOD_DDTT     = SYSDATE
			     , SPOS_WLOC_CD         = DECODE(:V_TRN_WRK_FULLVOID_GP,'E',:V_SPOS_WLOC_CD,SPOS_WLOC_CD)
			     , YD_CARLD_STOP_LOC    = DECODE(:V_TRN_WRK_FULLVOID_GP,'E',:V_YD_CARLD_STOP_LOC,YD_CARLD_STOP_LOC)
			     , YD_CARLD_PNT_WO_DT   = DECODE(:V_TRN_WRK_FULLVOID_GP,'E',TO_DATE(:V_YD_CARLD_PNT_WO_DT,'YYYYMMDDHH24MISS'),YD_CARLD_PNT_WO_DT)
			     , YD_PNT_CD1           = DECODE(:V_TRN_WRK_FULLVOID_GP,'E',:V_YD_PNT_CD1,YD_PNT_CD1)
			     , ARR_WLOC_CD          = DECODE(:V_TRN_WRK_FULLVOID_GP,'F',:V_ARR_WLOC_CD,ARR_WLOC_CD)
			     , YD_CARUD_STOP_LOC    = DECODE(:V_TRN_WRK_FULLVOID_GP,'F',:V_YD_CARUD_STOP_LOC,YD_CARUD_STOP_LOC)
			     , YD_CARUD_PNT_WO_DT   = DECODE(:V_TRN_WRK_FULLVOID_GP,'F',TO_DATE(:V_YD_CARUD_PNT_WO_DT,'YYYYMMDDHH24MISS'),YD_CARUD_PNT_WO_DT)
			     , YD_PNT_CD3           = DECODE(:V_TRN_WRK_FULLVOID_GP,'F',:V_YD_PNT_CD3,YD_PNT_CD3)
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
*/			 
			intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarschByWloc", logId, methodNm, "TB_YS_CARSCH 등록");
			if(intRtnVal <= 0) {
				recSndPnt.setField("YD_PNT_CD"			, "0000");               
				return sndRecord = commUtils.addSndData(sndRecord,recSndPnt);
			}			
			szMsg="=========검색된 적치열 : " + szWLOC_CD + "===포인트 : "+ szYD_PNT_CD + "======";
			commUtils.printLog(logId, szMsg, "SL");

			/**********************************************************
			* 4-1-4. 소재차량Point지시 전문 전송
			**********************************************************/	
			
			//포인트지시 메세지 전송
			recInTemp = JDTORecordFactory.getInstance().create();

			recInTemp.setResultCode(logId);	//Log ID
			recInTemp.setResultMsg(methodNm);	//Log Method Name
			recInTemp.setField("JMS_TC_CD"			, "YSTSJ011");
			recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
			recInTemp.setField("TRN_EQP_CD"			, szTRN_EQP_CD);
			recInTemp.setField("WLOC_CD"			, szWLOC_CD);
			recInTemp.setField("YD_PNT_CD"			, szYD_PNT_CD);
			recInTemp.setField("PNT_WO_GP"			, "A");
			recInTemp.setField("PNT_WO_DT"			, commUtils.getDateTime14());

			//전송 Data 생성
			sndRecord = commUtils.addSndData(sndRecord,recInTemp);	
		
			
		} catch(Exception e){
			
			szMsg="소재차량 도착 Point요구 처리 Error:" +e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
			
			m_ctx.setRollbackOnly();
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
		commUtils.printLog(logId, methodNm, "S-");
		return sndRecord;
	} //end of rcvTSYSJ002()

	/**
	 *      [A] 오퍼레이션명 : 소재차량도착(TSYSJ003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYSJ003(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "소재차량도착[YsCommCarMvSeEJB.rcvTSYSJ003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		JDTORecordSet rsResult          = null;
		JDTORecordSet rsWbookMtl        = null;
		JDTORecord	  sndRecord			= JDTORecordFactory.getInstance().create();
		JDTORecord recCarSch        	= null;
		JDTORecord recOutTemp        	= null;
		JDTORecord recInTemp         	= null;		
	    int intRtnVal 		   			= 0 ;
	    
	    String szMsg           			= "";
	    String szMSG_GP					= "";
	    String szOperationName			= "소재차량도착";
	    String szTRN_EQP_CD    			= "";
	    String szYD_WBOOK_ID   			= "";
	    String szYD_SCH_CD				= "";
	    String szARR_WLOC_CD   			= "";
	    String szARR_YD_PNT_CD   		= "";
	    String szYD_CAR_SCH_ID 			= "";
	    String szTRN_EQP_STK_CAPA 		= "";
	    String szYD_CAR_PROG_STAT		= "";
	    String szTRN_WRK_FULLVOID_GP	= "";
	    	    
        String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID        
        if(msgId==null || msgId.equals("")){
        	return sndRecord;
        }
        
	    try{
			commUtils.printLog(logId, methodNm, "S+");
	        szMsg="["+szOperationName+"] 전문수신 : TCCODE=" +msgId ;

	        commUtils.printParam(logId + szMsg, rcvMsg);
			
			szMSG_GP      			= commUtils.trim(rcvMsg.getFieldString("MSG_GP"));
			szARR_WLOC_CD     		= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 
			szARR_YD_PNT_CD   		= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); 
			szTRN_EQP_CD   			= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 
			szTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); 

			if( szARR_YD_PNT_CD.equals("1Z99") ) {
	    		szMsg="[" + methodNm + "] 착지개소코드["+szARR_WLOC_CD+"], 착지포인트코드["+szARR_YD_PNT_CD+"]가 대기장이므로 업무처리 종료";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
	    	}
	    	
	    	if( szARR_WLOC_CD.equals("DMY1P"))	{
	    		szMsg="[" + methodNm + "] 착지개소코드["+szARR_WLOC_CD+"]가 중장비수리고이므로 업무처리 종료";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
	    	}
			
	    	szMsg="[" + methodNm + "] 운송장비코드["+szTRN_EQP_CD+"]로 차량스케줄 조회 시작";
			commUtils.printLog(logId, szMsg, "SL");
	    	
	    	//운송장비코드로 차량스케줄 조회	    	
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("TRN_EQP_CD", szTRN_EQP_CD);
	    	
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschDaoTrnEqpCd 
	    	SELECT *
	    	 FROM (
	    	SELECT YD_CAR_SCH_ID        AS YD_CAR_SCH_ID                          
	    	      ,REGISTER AS REGISTER
	    	      ,TO_CHAR(REG_DDTT,'YYYYMMDDHH24MISS') AS REG_DDTT
	    	      ,MODIFIER  AS MODIFIER
	    	      ,TO_CHAR(MOD_DDTT,'YYYYMMDDHH24MISS') AS MOD_DDTT
	    	      ,DEL_YN AS DEL_YN
	    	      ,YD_EQP_ID AS YD_EQP_ID
	    	      ,YD_CAR_USE_GP AS YD_CAR_USE_GP
	    	      ,CAR_NO AS CAR_NO
	    	      ,TRN_EQP_CD AS TRN_EQP_CD
	    	      ,CAR_KIND AS CAR_KIND
	    	 
	    	                  :
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
	    	      ,(SELECT YD_STKBED_USG_CD FROM TB_YS_STKCOL B WHERE B.YS_STK_COL_GP=A.YD_CARLD_STOP_LOC 
	    	                        AND B.YD_STKBED_USG_CD IN ('A','D','E')
	    	                        ) AS NEW_DEST_BAY
	    	  FROM TB_YS_CARSCH A                                  
	    	 WHERE TRN_EQP_CD = :V_TRN_EQP_CD            
	    	   AND DEL_YN='N'
	    	 ORDER BY YD_CAR_SCH_ID DESC , YD_CARUD_CMPL_DT DESC
	    	 ) A
	    	 WHERE ROWNUM<=1
			*/	    			
	    	rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschDaoTrnEqpCd", logId, methodNm, "차량스케줄을 조회"); 	
	    	
			if (rsResult == null || rsResult.size() < 0) {
				szMsg="["+methodNm+"] 운송장비코드["+szTRN_EQP_CD+"]로 차량스케줄 조회 시 : parameter error";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			} else if (rsResult.size() > 1) {
				szMsg= "[" + methodNm + "] 차량스케줄 조회 시 오류발생 - 운송장비코드로 차량스케줄이 여러건["+rsResult.size()+"]이 존재합니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			}
	    	
	    	rsResult.first();
	    	recCarSch = rsResult.getRecord(); 
	    	
			szYD_CAR_SCH_ID     = commUtils.trim(recCarSch.getFieldString("YD_CAR_SCH_ID")); 
			szYD_CAR_PROG_STAT	= commUtils.trim(recCarSch.getFieldString("YD_CAR_PROG_STAT")); 
			
			String isUnMatched = "N";
			if(szTRN_WRK_FULLVOID_GP.equals("F")){
	    		//지시포인트와 도착포인트가 틀리면 ERROR 처리
	    		if(!commUtils.trim(recCarSch.getFieldString("YD_PNT_CD3")).equals(szARR_YD_PNT_CD)) {
					szMsg="["+methodNm+"] 지시포인트와 도착된 포인트가 틀립니다.";
		    		commUtils.printLog(logId, szMsg, "SL");
		    		isUnMatched = "Y";
					//throw new Exception(szMsg);
		    	}
			} else {
	    		//지시포인트와 도착포인트가 틀리면 ERROR 처리
	    		if(!commUtils.trim(recCarSch.getFieldString("YD_PNT_CD1")).equals(szARR_YD_PNT_CD)) {
					szMsg="["+methodNm+"] 지시포인트와 도착된 포인트가 틀립니다.";
		    		commUtils.printLog(logId, szMsg, "SL");
					throw new Exception(szMsg);
		    	}
			}	
			szMsg="["+methodNm+"] 운송장비코드["+szTRN_EQP_CD+"]로 차량스케줄["+szYD_CAR_SCH_ID+"], 차량진행상태["+szYD_CAR_PROG_STAT+"] ";
			commUtils.printLog(logId, szMsg, "SL");
			
			if( !szMSG_GP.equals("U") ) {
				//도착전문이 수정이 아니고 상차도착/하차도착인 경우에는 업무종료처리
				if( szYD_CAR_PROG_STAT.equals(YsConstant.YD_CARLD_ARR) || szYD_CAR_PROG_STAT.equals(YsConstant.YD_CARUD_ARR) ) {
					szMsg= "[" + methodNm + "] 운송장비코드["+szTRN_EQP_CD+"]로 차량스케줄["+szYD_CAR_SCH_ID+"]이 이미 도착처리된 상태입니다. - 차량진행상태["+szYD_CAR_PROG_STAT+"]" + "종료함";
					commUtils.printLog(logId, szMsg, "SL");
					throw new Exception(szMsg);
				}
			} 
    	
			/**********************************************************
		     * 0. 각강 체크
		     *    - 임고 검수 완료 여부 CEHCK
		     **********************************************************/   
			String szSPOS_WLOC_CD = commUtils.trim(recCarSch.getFieldString("SPOS_WLOC_CD"));
			if((szSPOS_WLOC_CD.equals("S4Y20")) && ( szTRN_WRK_FULLVOID_GP.equals("F"))){
				JDTORecordSet resSet      = null;;
				JDTORecord    recIn       = JDTORecordFactory.getInstance().create();
				JDTORecord    recOut      = JDTORecordFactory.getInstance().create();
			    
				resSet = JDTORecordFactory.getInstance().createRecordSet("");
				recIn  = JDTORecordFactory.getInstance().create();
				recIn.setField("TRN_EQP_CD", szTRN_EQP_CD);
				resSet = commDao.select(recInTemp, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.getSbStkChkOKPda", logId, methodNm, "각강 입고검수 완료"); 
			  
			    if (resSet == null || resSet.size() < 0 ) {
			    	szMsg="["+methodNm+"] 운송장비코드["+szTRN_EQP_CD+"]로 입고검수완료 조회 시 : parameter error";
			    	commUtils.printLog(logId, szMsg, "SL");
			    	throw new Exception(szMsg);
			    } else {
			    	resSet.absolute(1);
			  
			    	recOut = JDTORecordFactory.getInstance().create();
			    	recOut.setRecord(resSet.getRecord());
			  
			    	String sbComlCount  = commUtils.trim(recOut.getFieldString("COUNT")); 
			    	if (Integer.parseInt(sbComlCount) != resSet.size()) {
			    		szMsg="["+methodNm+"] 운송장비코드["+szTRN_EQP_CD+"] 입고 검수 미완료";
			    		commUtils.printLog(logId, szMsg, "SL");
			    		throw new Exception(szMsg);
			    	}
			    }           
			}
			
	    	//작업예약정보에서
	    	//운송장비코드 , 야드차량사용구분으로  조회 
	    	//해당된 작업예약 재료 정보를 가지고 온다
			rsWbookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	recInTemp  = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("TRN_EQP_CD",    szTRN_EQP_CD);
	    
	    	 /* com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookMtlbyCarUsrGpTrnEqpCd 
			SELECT B.YD_WBOOK_ID    AS YD_WBOOK_ID
			      ,B.YD_SCH_CD
			      ,A.SSTL_NO         AS SSTL_NO
			--      ,A.YD_UP_COLL_SEQ AS YD_UP_COLL_SEQ
			      ,A.YS_STK_COL_GP  AS YS_STK_COL_GP
			      ,A.YS_STK_BED_NO  AS YS_STK_BED_NO
			      ,A.YS_STK_LYR_NO  AS YS_STK_LYR_NO
			  FROM TB_YS_WRKBOOKMTL A
			      ,(SELECT *
			          FROM (SELECT YD_WBOOK_ID
			                     , YD_SCH_CD
			                  FROM TB_YS_WRKBOOK
			                 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
			                     AND DEL_YN='N'
			                 order by YD_WBOOK_ID desc
			                ) C
			          WHERE ROWNUM<=1
			        ) B
			 WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
			 ORDER BY YS_STK_COL_GP,YS_STK_BED_NO,YS_STK_LYR_NO
			 */
	    	rsWbookMtl = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookMtlbyCarUsrGpTrnEqpCd", logId, methodNm, "작업예약을 조회"); 
			
	    	if (rsWbookMtl == null || rsWbookMtl.size() < 0 ) {
				szMsg="["+methodNm+"] 운송장비코드["+szTRN_EQP_CD+"]로 작업예약 조회 시 : parameter error";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			} else if (rsWbookMtl.size() == 0 ){
				
			} else {
		    	rsWbookMtl.absolute(1);
				
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsWbookMtl.getRecord());
				
				szYD_WBOOK_ID	= commUtils.trim(recOutTemp.getFieldString("YD_WBOOK_ID")); 
				szYD_SCH_CD    	= commUtils.trim(recOutTemp.getFieldString("YD_SCH_CD")); 
			}

			/**********************************************************
			* 1. 공차도착  및 영차도착 실적
			**********************************************************/			

	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("WLOC_CD",   szARR_WLOC_CD);
	    	recInTemp.setField("YD_PNT_CD", szARR_YD_PNT_CD);

	    	//착지개소코드와 착지야드포인트코드로 적치열을 조회한다.
	    	szMsg="["+methodNm+"] 수신된 착지개소코드["+szARR_WLOC_CD+"]와 수신된 착지야드포인트코드["+szARR_YD_PNT_CD+"]로 적치열을 조회 시작";
			commUtils.printLog(logId, szMsg, "SL");

	    	/*
	    	SELECT A.YS_STK_COL_GP                         AS YS_STK_COL_GP
	    	      ,TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
	    	      ,A.REGISTER                              AS REGISTER
	    	      ,TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
	    	      ,A.MODIFIER                              AS MODIFIER
	    	      ,A.DEL_YN                                AS DEL_YN
	    	      ,A.YD_GP                                 AS YD_GP
	    	      ,A.YD_BAY_GP                             AS YD_BAY_GP
	    	      ,A.YD_EQP_GP                             AS YD_EQP_GP
	    	      ,A.YD_STK_COL_NO                         AS YD_STK_COL_NO
	    	      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.YD_STK_COL_ACT_STAT   ELSE A.YD_STK_COL_ACT_STAT  END ) AS YD_STK_COL_ACT_STAT     
	    	      ,A.YD_STK_COL_RULE_XAXIS                 AS YD_STK_COL_RULE_XAXIS 
	    	      ,A.YD_STK_COL_RULE_YAXIS                 AS YD_STK_COL_RULE_YAXIS 
	    	      ,A.YD_STK_COL_W                          AS YD_STK_COL_W                   
	    	      ,A.YD_STK_COL_L                          AS YD_STK_COL_L                   
	    	      ,A.YD_CAR_USE_GP                         AS YD_CAR_USE_GP                 
	    	      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.TRN_EQP_CD ELSE A.TRN_EQP_CD END)    AS TRN_EQP_CD                       
	    	      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.CAR_NO  ELSE  A.CAR_NO END)                 AS CAR_NO                               
	    	      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.CARD_NO   ELSE A.CARD_NO END)               AS CARD_NO                             
	    	      ,A.WLOC_CD                               AS WLOC_CD                             
	    	      ,A.YD_PNT_CD                             AS YD_PNT_CD   
	    	      ,B.YD_CARPNT_CD AS YD_CARPNT_CD
	    	  FROM TB_YS_STKCOL A   
	    	     , TB_YS_CARPOINT B
	    	 WHERE B.YS_STK_COL_GP=A.YS_STK_COL_GP
	    	   AND A.WLOC_CD =  :V_WLOC_CD
	    	   AND A.YD_PNT_CD = :V_YD_PNT_CD  	
	    	 */	    	
	    	rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolWLocCdandPntCd", logId, methodNm, "적치열 조회"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
	    		szMsg="["+methodNm+"] 수신된 착지개소코드["+szARR_WLOC_CD+"]와 수신된 착지야드포인트코드["+szARR_YD_PNT_CD+"] 적치열 조회 시 적치열이 존재하지 않습니다.";
	    		commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
	    	}
	    	
	    	rsResult.absolute(1);
	    	JDTORecord rcvMsgCol = JDTORecordFactory.getInstance().create();
	    	rcvMsgCol.setRecord(rsResult.getRecord());
	    	
    		//공차도착실적
	    	//재송신 된 경우는 SKIP
	    	if(!commUtils.trim(rcvMsgCol.getFieldString("TRN_EQP_CD")).equals(szTRN_EQP_CD)) {
	    		if(commUtils.trim(rcvMsgCol.getFieldString("YD_STK_COL_ACT_STAT")).equals("L")) {
					szMsg="["+methodNm+"] 차량정지위치가 이미 활성상태입니다.";
		    		commUtils.printLog(logId, szMsg, "SL");
					throw new Exception(szMsg);
		    	}
	    	}	
    		if(commUtils.trim(rcvMsgCol.getFieldString("YD_STK_COL_ACT_STAT")).equals("N")) {
    			szMsg="["+methodNm+"] 차량정지위치가 사용 불가상태입니다.";
	    		commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
	    	}
	    	
	    	if(szTRN_WRK_FULLVOID_GP.equals("E")){
	    		rcvMsg.setField("YD_CARLD_WRK_BOOK_ID"	, szYD_WBOOK_ID);
	    		rcvMsg.setField("YD_SCH_CD"				, szYD_SCH_CD);
	    		
	    		/**********************************************************
			     * 1.작업예약 생성
			     * 2.준비스케줄 삭제
			     * 3.차량스케줄 갱신 (실적등록된 상차위치로)
			     * 4.차량 도착열(COL) 등록
			     * 5.차량포인트 등록
				 * 6.적치베드 활성화
				 * 7.적치단 활성화
				 * 8.저장위치 제원 야드L2로 전송
				 * 9.크레인 스케줄 호출
			     **********************************************************/   
	    		sndRecord = this.procLDMatlCarArr(logId, rcvMsg, recCarSch , rcvMsgCol);
	    		intRtnVal = Integer.parseInt(sndRecord.getTaskCode());
	    		
	    		szMsg="["+methodNm+"]  -----> intRtnVal";
   				commUtils.printLog(logId, szMsg, "SL");
   				
   				if(intRtnVal == -100) {							
	    			return sndRecord;
	    		} else if( intRtnVal <= -1 ) {
	    			return sndRecord;
	    		}

	    	}else{
	    		//영차도착실적

	    		rcvMsg.setField("YD_CARUD_WRK_BOOK_ID"	, szYD_WBOOK_ID);
	    		rcvMsg.setField("TRN_EQP_STK_CAPA"		, szTRN_EQP_STK_CAPA);
	    		rcvMsg.setField("IS_UNMATCHED"			, isUnMatched);
	    		
	    		/**********************************************************
			     * 1.하차 작업예약 생성(하차동이 변경된 경우만 )
			     * 2.차량스케줄 갱신
			     * 3.작업예약상세 갱신
			     * 4.차량 도착열(COL) 등록
			     * 5.차량포인트 등록
				 * 6.적치베드 활성화
				 * 7.적치단 활성화
				 * 8.저장위치 제원 야드L2로 전송
				 * 9.저장품 제원 야드L2로 전송
				 * 9.크레인 스케줄 호출
			     **********************************************************/   	    		
	    		sndRecord = this.procUDMatlCarArr(logId, rcvMsg, rsWbookMtl, recCarSch, rcvMsgCol);
	    		
	    		intRtnVal = Integer.parseInt(sndRecord.getTaskCode());
	    		
	    		if(intRtnVal == -100) {							//하차정지위치가 이미 활성상태입니다.
	    			return sndRecord;
	    		} else if( intRtnVal <= -1 ) {
	    			return sndRecord;
	    		}
	    	}
	    	
			
		} catch(Exception e){
			
			szMsg="소재차량 도착처리 Error:" +e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
			m_ctx.setRollbackOnly();
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
		commUtils.printLog(logId, methodNm, "S-");
		return sndRecord;
	} //end of rcvTSYSJ003()	
	
	/**
	 *      [A] 오퍼레이션명 : 소재차량출발(TSYSJ004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYSJ004(JDTORecord rcvMsg)throws DAOException  {
		/*
		 * 전문내용 : TSYDJ004(소재차량출발)
		 *TRN_EQP_CD                    	운송장비코드			CHAR	8		구내운송 차량 제원 등록 NO.	
		 *SPOS_WLOC_CD                  	발지개소코드			CHAR	5		영차출발 시 개소코드	
		 *SPOS_YD_PNT_CD                	발지야드포인트코드		CHAR	4		영차출발 시 포인트코드	
		 *ARR_WLOC_CD                   	착지개소코드			CHAR	5		공차출발 시 개소코드	
		 *ARR_YD_PNT_CD                 	착지야드포인트코드		CHAR	4		공차출발 시 포인트코드	
		 *TRN_WRK_FULLVOID_GP				운송작업영공구분		CHAR	1			
		 *TRN_EQP_STK_CAPA              	운송장비적재능력		NUMBER	22,2			
		 *CARUD_PAP_LEV_TT              	하차지출발시각			DATE
		 *YD_WO_CNCL_YN						야드지시취소여부		CHAR	1
		 *CARLD_SH							상차매수				NUMBER	22,2
		 *SSTL_NO1~10						특수강재료번호1~10		CHAR	12
		 */
		
		String methodNm = "소재차량출발[YsCommCarMvSeEJB.rcvTSYSJ004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		JDTORecordSet	rsResult				= null;
		JDTORecord		sndRecord				= JDTORecordFactory.getInstance().create();
		JDTORecord		recOutTemp				= null;
		JDTORecord		recInTemp				= null;	
		JDTORecord		recPara					= null;	
		
	    int intRtnVal 		   					= 0 ;
	    
	    String 			szMsg					= "";
		JDTORecordSet	rsStkCol	   			= null;
		JDTORecord    	recInPara				= null;
	    int				intLevLocGp     	    = 0;
	    String			szMethodName    		= "procMatlCarLev";
	    String 			szYD_CARLD_STOP_LOC		= "";
	    String 			szYD_EQP_ID				= "";
	    String			szYD_CARLD_WRK_BOOK_ID	= "";
	    String			szYD_CARUD_WRK_BOOK_ID	= "";
	    
	    String 			szYD_CAR_SCH_ID			= "";
	    String 			szYD_CAR_PROG_STAT		= "";
		JDTORecordSet 	rsWrkBookMtl			= null;
		String 			szMSG_ID				= "";
		String 			szCARSCH_SPOS_WLOC_CD	= "";
		String 			szYD_GP					= null;
		
		// WC 추가 변수
		String				szCARLD_SH			= null;
		
		//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
		String msgId = commUtils.getMsgId(rcvMsg); 
        if(msgId==null || msgId.equals("")){
        	return sndRecord;
        }
        
	    try{
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "소재차량출발 수신 ", rcvMsg);
			
	    	/**********************************************************
	    	 * 1. 파라미터 확인
	    	 **********************************************************/	    	
	    	//운송장비코드, 발지개소코드, 발지야드포인트코드, 착지개소코드, 착지야드포인트코드
			String szTRN_EQP_CD     		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 
			String szSPOS_WLOC_CD     		= commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD")); 
			String szSPOS_YD_PNT_CD        	= commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"));
			String szARR_WLOC_CD   			= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 
			String szARR_YD_PNT_CD        	= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD"));
			String szTRN_WRK_FULLVOID_GP  	= commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")); 
			String szTRN_EQP_STK_CAPA      	= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_STK_CAPA"));
			
		   	//화면에서 넘어오는 직상차 구분 - BACKUP 실적처리시 포함
			String szYD_DIRECT_CARLD_GP 	= commUtils.trim(rcvMsg.getFieldString("YD_DIRECT_CARLD_GP")); 
			String szIS_EJB_CALL 			= commUtils.trim(rcvMsg.getFieldString("IS_EJB_CALL"));
			String szYD_AUTO_LOT 			= commUtils.trim(rcvMsg.getFieldString("YD_AUTO_LOT")); //자동LOT/수동LOT 편성 판단 변수
	    	
			// 출발실적 수신시 발지개소 코드 및 착지개소 코드 체크
			if( szSPOS_WLOC_CD.equals("") || szARR_WLOC_CD.equals("") ) {
				szMsg = "[JSP Facade] 소재차량출발실적 수신 시 발지개소코드["+szSPOS_WLOC_CD+"]나 착지개소코드["+szARR_WLOC_CD+"]가 없습니다." ;
				commUtils.printLog(logId, szMsg, "SL");
	    		return sndRecord;
			}
			else {
		    	szMsg = "수신발지개소코드["+szSPOS_WLOC_CD+"], 수신발지개소POINT코드["+szSPOS_YD_PNT_CD+"]로 야드에서 관리되는 적치열구분 조회 시작";
		    	commUtils.printLog(logId, szMsg, "SL");
			}
			
			/**********************************************************
	    	 * 2.운송장비코드로 차량스케줄 조회
	    	 **********************************************************/
			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp 	= JDTORecordFactory.getInstance().create();			
			recInTemp.setField("TRN_EQP_CD", szTRN_EQP_CD);
			
			/* 
			com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschDaoTrnEqpCd 
	    	SELECT *
	    	 FROM (
	    	SELECT YD_CAR_SCH_ID        AS YD_CAR_SCH_ID                          
	    	      ,REGISTER AS REGISTER
	    	      ,TO_CHAR(REG_DDTT,'YYYYMMDDHH24MISS') AS REG_DDTT
	    	      ,MODIFIER  AS MODIFIER
	    	      ,TO_CHAR(MOD_DDTT,'YYYYMMDDHH24MISS') AS MOD_DDTT
	    	      ,DEL_YN AS DEL_YN
	    	      ,YD_EQP_ID AS YD_EQP_ID
	    	      ,YD_CAR_USE_GP AS YD_CAR_USE_GP
						:	    	      
				  ,YD_CAR_WRK_GP AS YD_CAR_WRK_GP          
	    	      ,TRANS_ORD_DATE AS TRANS_ORD_DATE         
	    	      ,TRANS_ORD_SEQNO AS TRANS_ORD_SEQNO   
	    	      ,(SELECT YD_STKBED_USG_CD FROM TB_YS_STKCOL B WHERE B.YS_STK_COL_GP=A.YD_CARLD_STOP_LOC 
	    	                        AND B.YD_STKBED_USG_CD IN ('A','D','E')
	    	                        ) AS NEW_DEST_BAY
	    	  FROM TB_YS_CARSCH A                                  
	    	 WHERE TRN_EQP_CD = :V_TRN_EQP_CD            
	    	   AND DEL_YN='N'
	    	 ORDER BY YD_CAR_SCH_ID DESC , YD_CARUD_CMPL_DT DESC
	    	 ) A
	    	 WHERE ROWNUM<=1
			*/	    	 
			rsResult = commDao.select(recInTemp,"com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschDaoTrnEqpCd", logId, methodNm, "차량스케줄을 조회"); 
			if (rsResult == null || rsResult.size() < 0) {
				szMsg="["+methodNm+"] 운송장비코드["+szTRN_EQP_CD+"]로 차량스케줄 조회 시 : parameter error";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			} else if (rsResult.size() > 1) {
				szMsg= "[" + methodNm + "] 차량스케줄 조회 시 오류발생 - 운송장비코드로 차량스케줄이 여러건["+rsResult.size()+"]이 존재합니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			}
			
			// 설비 ID 기본 설비ID(구내운송차량)로 설정
			szYD_EQP_ID     = YsConstant.YD_TS_CAR_EQP_ID;
			
			/**********************************************************
	    	 * 3.운송장비코드로 차량스케줄 조회시 차량 SCH 있는 경우
	    	 **********************************************************/
			if( rsResult.size() == 1 ) {			

				rsResult.absolute(1);
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsResult.getRecord());
				
				szTRN_EQP_CD     		= commUtils.trim(recOutTemp.getFieldString("TRN_EQP_CD"       )); 
				szYD_CAR_PROG_STAT 		= commUtils.trim(recOutTemp.getFieldString("YD_CAR_PROG_STAT" ));  		// 차량진행상태
				szYD_CAR_SCH_ID 		= commUtils.trim(recOutTemp.getFieldString("YD_CAR_SCH_ID" )); 			// 차량스케줄ID
				szCARSCH_SPOS_WLOC_CD 	= commUtils.trim(recOutTemp.getFieldString("SPOS_WLOC_CD" )); 			// 발지개소코드
				szYD_CARLD_WRK_BOOK_ID	= commUtils.trim(recOutTemp.getFieldString("YD_CARLD_WRK_BOOK_ID" )); 	// 상차작업예약
				szYD_CARUD_WRK_BOOK_ID	= commUtils.trim(recOutTemp.getFieldString("YD_CARUD_WRK_BOOK_ID" )); 	// 하차작업예약
				szYD_GP         		= commUtils.trim(recOutTemp.getFieldString("YD_AIM_YD_GP" ));			// 목적지 야드구분
				
				szMsg = "[" + methodNm + "] 차량스케줄["+szYD_CAR_SCH_ID+"] - 차량진행상태["+szYD_CAR_PROG_STAT+"], ";
				szMsg += "발지개소코드["+szCARSCH_SPOS_WLOC_CD+"], 상차작업예약["+szYD_CARLD_WRK_BOOK_ID+"], 하차작업예약["+szYD_CARUD_WRK_BOOK_ID+"]";
				commUtils.printLog(logId, szMsg, "SL");

				/**********************************************************
		    	 * 2-1.수신 DATE CHECK
				 *  
		    	 **********************************************************/
				
				 if( szTRN_WRK_FULLVOID_GP.equals("E")  ) {  //
					 
					
					 if(szYD_CAR_PROG_STAT.equals("1")||szYD_CAR_PROG_STAT.equals("E")) {
					 }
					 
					 else {
						szMsg= "[" + methodNm + "] 상차도착 및 작업중인 상태입니다..";
						commUtils.printLog(logId, szMsg, "SL");
						throw new Exception(szMsg); 
					 }	 
				} else if( szTRN_WRK_FULLVOID_GP.equals("F")  ) {  //
					if(szYD_CAR_PROG_STAT.equals("5")||szYD_CAR_PROG_STAT.equals("A")) {
					} else {
						szMsg= "[" + methodNm + "] 상차완료 및 하차출발 상태가 아닙니다...";
						commUtils.printLog(logId, szMsg, "SL");
						throw new Exception(szMsg);
					}
				}
				
				/**********************************************************
		    	 * 3-1.야드차량진행상태(YD_CAR_PROG_STAT)이 하차완료인 경우
				 *  차량이송소재 및 차량스케줄 삭제)
		    	 **********************************************************/
				if(szYD_CAR_PROG_STAT.equals(YsConstant.YD_CARUD_CMPL)){
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					recInTemp.setField("DEL_YN", "Y");
					recInTemp.setField("MODIFIER", szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
					
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
					
					commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarFtmvMtl", logId, methodNm, "TB_YS_CARFTMVMTL 종료");
		            
		        	//차량스케줄 종료
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch  
					UPDATE TB_YS_CARSCH
					   SET MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					     , DEL_YN = 'Y'
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
 				       AND DEL_YN = 'N'

					 */
					intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch", logId, methodNm, "TB_YS_CARSCH 종료");
					
					
					szMsg= "[" + methodNm + "] 하차완료된 차량스케줄["+szYD_CAR_SCH_ID+"] 종료 성공 ";
					commUtils.printLog(logId, szMsg, "SL");
						
				}
				/**********************************************************
		    	 * 3-2.상차출발이고 공차인 경우
		    	 **********************************************************/
				else if( szYD_CAR_PROG_STAT.equals(YsConstant.YD_CARLD_LEV) && szTRN_WRK_FULLVOID_GP.equals("E") ) {  //1
					/**********************************************************
			    	 * 3-2-1.착지개소코드 및 발지개소코드가 동일 && 발지개소POINT = 1Z99(대기장) -> 중복 상차 출발 -> 업무종료
			    	 **********************************************************/
					if( szARR_WLOC_CD.equals(szCARSCH_SPOS_WLOC_CD) ) {
						if( szSPOS_YD_PNT_CD.equals("1Z99") || szSPOS_YD_PNT_CD.equals("0000") || szSPOS_YD_PNT_CD.equals("")){ // 발지개소POINT = 1Z99(대기장)
							
							szMsg="["+methodNm+"] 대기장[1Z99]에서 공차출발 시 소재차량도착Point요구 모듈 호출 시작";
							commUtils.printLog(logId, szMsg, "SL");

							//소재차량도착Point요구 호출
							recPara = JDTORecordFactory.getInstance().create();
							recPara.setResultCode(logId);	//Log ID
							recPara.setResultMsg(methodNm);	//Log Method Name
							recPara.setField("JMS_TC_CD",               YsConstant.YSYSJ901);
							recPara.setField("JMS_TC_CREATE_DDTT",      commUtils.getDateTime14()); //JMSTC생성일시
							recPara.setField("TRN_EQP_CD",              szTRN_EQP_CD);
							recPara.setField("WLOC_CD",                 szCARSCH_SPOS_WLOC_CD);
							recPara.setField("TRN_WRK_FULLVOID_GP",     szTRN_WRK_FULLVOID_GP);
							recPara.setField("PNT_DMD_DT",              commUtils.getDateTime14());
							
							//소재차량도착Point요구 호출
							sndRecord = commUtils.addSndData(sndRecord, recPara);	
							
							return sndRecord;
						} 
					
					} 
					/**********************************************************
			    	 * 3-2-2.정보 이상 -> Data Clear(예약POINT제거/차량스케줄 삭제/자업예약삭제/준비스케줄복구) : 발생-상차출발 재 수신 
			    	 **********************************************************/
					else {		
						/**********************************************************
				    	 * 3-2-2-1. 예약된 차량 도착 위치 POINT 삭제 처리
				    	 **********************************************************/
						szMsg = "[" + methodNm + "] 운송장비코드로 예약된 차량정비Point 삭제 시작";
						commUtils.printLog(logId, szMsg, "SL");
						 						
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("TRN_EQP_CD",	szTRN_EQP_CD); // 운송장비코드
						/* 
						com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolTrnEqpCdToNull 
						UPDATE TB_YS_STKCOL
						   SET TRN_EQP_CD = NULL
						   , YD_CAR_USE_GP = NULL
						WHERE TRN_EQP_CD = :V_TRN_EQP_CD
						*/						
						intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolTrnEqpCdToNull", logId, methodNm, "TB_YS_STKCOL 등록");
						
						szMsg = "[" + methodNm + "] 운송장비코드로 예약된 차량정비Point 삭제 완료 - 반환값 : " + intRtnVal;
						commUtils.printLog(logId, szMsg, "SL");
						
						/**********************************************************
				    	 * 3-2-2-2. 차량 스케줄 삭제 처리
				    	 **********************************************************/

						szMsg = "[" + methodNm + "] 차량스케줄["+szYD_CAR_SCH_ID+"]이 존재하므로 삭제 시작";
						commUtils.printLog(logId, szMsg, "SL");
						
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
						recInTemp.setField("DEL_YN", 		"Y");
						recInTemp.setField("MODIFIER",		szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
						/* 
						com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch  
						UPDATE TB_YS_CARSCH
						   SET MODIFIER = :V_MODIFIER
						     , MOD_DDTT = SYSDATE
						     , DEL_YN = 'Y'
						WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	 				       AND DEL_YN = 'N'
						*/						
						intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch", logId, methodNm, "TB_YS_CARSCH 삭제");
						
						szMsg = "[" + methodNm + "] 차량스케줄["+szYD_CAR_SCH_ID+"]이 존재하므로 삭제 완료 - 메세지 : " + intRtnVal;
						commUtils.printLog(logId, szMsg, "SL");
						
						/**********************************************************
				    	 * 3-2-2-3. 작업예약 및 작업예약 재료 삭제 처리--상차정보
				    	 **********************************************************/
						if( !szYD_CARLD_WRK_BOOK_ID.equals(""))	{	
							
							/**********************************************************
					    	 * 3-2-2-3-1. 작업예약재료 삭제 처리
					    	 **********************************************************/
							szMsg = "[" + methodNm + "] 상차작업예약["+szYD_CARLD_WRK_BOOK_ID+"]이 존재하므로 삭제 시작";
							commUtils.printLog(logId, szMsg, "SL");
							
							//szRtnMsg	= YdCommonUtils.delYdWrkbookNMtl(szYD_CARLD_WRK_BOOK_ID, szMethodName);
							recInTemp = JDTORecordFactory.getInstance().create();
			    	    	recInTemp.setField("YD_WBOOK_ID", 	   	szYD_CARLD_WRK_BOOK_ID);
			    	    	recInTemp.setField("MODIFIER", 	   		szMethodName.substring(0, 10));
			    	    	recInTemp.setField("DEL_YN", 	   		"Y");			    	    	
							/* 
							com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBookMtl  
							UPDATE TB_YS_WRKBOOKMTL
							   SET MODIFIER    = :V_MODIFIER
							      ,MOD_DDTT    = SYSDATE
							      ,DEL_YN      = 'Y'
							WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
							   AND DEL_YN      = 'N'
		    	    	    */
							commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL 삭제");

							/**********************************************************
					    	 * 2-2-2-3-2. 작업예약 삭제 처리
					    	 **********************************************************/
							/* com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBook 
							UPDATE TB_YS_WRKBOOK
							   SET MODIFIER    = :V_MODIFIER
							      ,MOD_DDTT    = SYSDATE
							      ,DEL_YN      = 'Y'
							 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
							   AND DEL_YN      = 'N'
			    	    	 */
							commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBook", logId, methodNm, "TB_YS_WRKBOOK 삭제");

							/**********************************************************
					    	 * 2-2-2-3-3. 준비스케줄 복구
					    	 **********************************************************/							
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
							commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepMtlRcvr", logId, methodNm, "TB_YS_PREPMTL");	

							/* com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepSchRcvr 
							--준비스케줄 복원 - 
							UPDATE TB_YS_PREPSCH
							   SET MODIFIER    = :V_MODIFIER
							      ,MOD_DDTT    = SYSDATE
							      ,DEL_YN      = 'N'
							      ,YD_WBOOK_ID = NULL
							 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
							*/ 
							commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepSchRcvr", logId, methodNm, "TB_YS_PREPSCH");	

							
							szMsg = "[" + methodNm + "] 상차작업예약["+szYD_CARLD_WRK_BOOK_ID+"]이 존재하므로 삭제 완료";
							commUtils.printLog(logId, szMsg, "SL");
						}
					}		
				}
			}

			/**********************************************************
	    	 * 4.각강 입고 체크/CARLD_SH > 0(구내운송 -> 하차출발/영차) - 저장품 정보 존재하지 않음 - WC
	    	 **********************************************************/			
			// 전문으로부터 CARLD_SH 가져오기
			szCARLD_SH = commUtils.trim(rcvMsg.getFieldString("CARLD_SH"));	
			
			szMsg = "[" + methodNm + "] 각강/빌렛 입고 수량 CARLD_SH : ["+szCARLD_SH+"]";
			commUtils.printLog(logId, szMsg, "SL");
			
			if (!"".equals(szCARLD_SH) && Integer.parseInt(szCARLD_SH) > 0) { // CARLD_SH 가 0 이상인 것(각강)
				/**********************************************************
		    	 * 4-1.발지개소코드(S4Y10(빌렛정정),S3Y20(대형봉강),S3Y30(옥외)) 및 운송작업영공구분(영차)
		    	 *     1) 차량스케줄 생성
		    	 *     2) 차량이송재료 생성
		    	 **********************************************************/			
				if((szSPOS_WLOC_CD.equals("S4Y10")||szSPOS_WLOC_CD.equals("S3Y20")||szSPOS_WLOC_CD.equals("S3Y30")) && ( szTRN_WRK_FULLVOID_GP.equals("F"))){	
					
					if("S5Y10".equals(szARR_WLOC_CD)) szYD_GP = "C"; 
					else szYD_GP = "K";
					
					/**********************************************************
			    	 * 4-1-1.차량스케줄 생성
			    	 **********************************************************/	
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("REGISTER",         szMethodName.length() > 10 ? szMethodName.subSequence(0, 10) : szMethodName);
				    recPara.setField("YD_EQP_WRK_STAT",  "L");                    			//야드설비작업상태
				    recPara.setField("YD_EQP_ID",        YsConstant.YD_TS_CAR_EQP_ID);		//야드설비ID
				    recPara.setField("TRN_EQP_CD",       szTRN_EQP_CD);               		//운송장비코드
				    recPara.setField("YD_CAR_USE_GP",    "L");                              //차량사용구분(L:구내,G:출하)
				    recPara.setField("SPOS_WLOC_CD",     szSPOS_WLOC_CD);                	//발지개소코드
				    recPara.setField("YD_CARLD_LEV_LOC", "");          						//야드상차출발위치
				    recPara.setField("YD_CARLD_LEV_DT",  commUtils.getDateTime14());   		//상차출발일시
				    recPara.setField("YD_BAYIN_WO_SEQ",  "99");                    			//입동지시순번 - WC 수정 : 각강 입고 차량의 경우 입고검수완료 전까지 99로 설정함, 검수 이후 9로 셋팅
				    recPara.setField("YD_CAR_PROG_STAT", YsConstant.YD_CARUD_LEV);          //상차출발상태
				    recPara.setField("YD_CAR_SCH_ID",    commDao.getSeqId(logId, methodNm, "CarSch"));
				    /* 
				    com.inisteel.cim.ys.common.dao.YsCommDAO.insYdCarsch
			        INSERT INTO TB_YS_CARSCH
			        (    YD_CAR_SCH_ID
			             , REGISTER
			             , REG_DDTT   
			             , YD_EQP_ID
			             , TRN_EQP_CD
			             , YD_CAR_USE_GP
			             , SPOS_WLOC_CD
			             , YD_CARLD_LEV_LOC
			             , YD_CARLD_LEV_DT
			             , YD_BAYIN_WO_SEQ
			             , YD_CAR_PROG_STAT
			             )
  			        VALUES (
  			             :V_YD_CAR_SCH_ID
	  		             , :V_REGISTER
		  	             , SYSDATE
			             , :V_YD_EQP_ID
			             , :V_TRN_EQP_CD
			             , :V_YD_CAR_USE_GP
			             , :V_SPOS_WLOC_CD
			             , :V_YD_CARLD_LEV_LOC
			             ,  TO_DATE(:V_YD_CARLD_LEV_DT,'YYYYMMDDHH24MISS')
			             , :V_YD_BAYIN_WO_SEQ
			             , :V_YD_CAR_PROG_STAT
			             )
				    */
				    intRtnVal = commDao.insert(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.insYdCarsch", logId, methodNm, "차량스케줄 등록");				      
			        if( intRtnVal <= 0 ){
			        	szMsg= szMethodName + "개소코드["+szSPOS_WLOC_CD+"] : 차량스케줄["+szYD_CAR_SCH_ID+"] 생성 시 오류발생 - 반환값 : " + intRtnVal;
			        	commUtils.printLog(logId, szMsg, "SL");
			        	sndRecord.setTaskCode("-1");
			        	return sndRecord;
			        }
			        szYD_CAR_SCH_ID = commUtils.trim(recPara.getFieldString("YD_CAR_SCH_ID"));
			        szMsg= methodNm + "개소코드["+szSPOS_WLOC_CD+"] : 차량스케줄["+szYD_CAR_SCH_ID+"] 생성 완료 - 반환값 : " + intRtnVal;
			        commUtils.printLog(logId, szMsg, "SL");
			        
					/**********************************************************
			    	 * 4-1-2.저장품 등록
			    	 **********************************************************/
					/**********************************************************
			    	 * 4-1-3.차량 이송재료 등록
			    	 **********************************************************/	
					String StmpSstlNo = "";
					String StmpSstlLoc = "";
					JDTORecord jrParam = null;
					for(int Loop_i = 1; Loop_i <= Integer.parseInt(szCARLD_SH); Loop_i++) {						
						StmpSstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+Loop_i));
						StmpSstlLoc= commUtils.trim(rcvMsg.getFieldString("SSTL_LOC"+Loop_i));
						commUtils.printLog(logId, StmpSstlNo, "SL");		
						jrParam = JDTORecordFactory.getInstance().create();
						jrParam.setResultCode(logId);	//Log ID
						jrParam.setResultMsg(methodNm);	//Log Method Name
						jrParam.setField("SSTL_NO", StmpSstlNo);
						jrParam.setField("LOC"	  , StmpSstlLoc); 
						jrParam.setField("CARRY_OUT", "N");
						jrParam.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
						if (!StmpSstlNo.equals("")) {
							// 저장품 등록
							if("C".equals(szYD_GP))
							commDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.insBtYdStock", logId, methodNm, "빌렛야드 저장품 등록");
							else
							commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insBdlYdStock", logId, methodNm, "제품야드 저장품 등록");
							// 차량 이송 재료 등록
							commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insSbCarmvmtl", logId, methodNm, "차량 이송재료 등록");
						}	
					}		    
				}
			}
			
			/**********************************************************
	    	 * 5.출발지 적치열 베드/단 정보 체크
	    	 **********************************************************/			
			rsStkCol 	= JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp 	= JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("WLOC_CD",   szSPOS_WLOC_CD);
	    	recInTemp.setField("YD_PNT_CD", szSPOS_YD_PNT_CD);

	    	//intLevLocGp = ydStkColDao.getYdStkcol(recInTemp, rsStkCol, 4);
	    	/* 
	    	com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolWLocCdandPntCd
	    	SELECT A.YS_STK_COL_GP                         AS YS_STK_COL_GP
	    	      ,TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
	    	      ,A.REGISTER                              AS REGISTER
	    	      ,TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
	    	      ,A.MODIFIER                              AS MODIFIER
	    	      ,A.DEL_YN                                AS DEL_YN
	    	      ,A.YD_GP                                 AS YD_GP
	    	      ,A.YD_BAY_GP                             AS YD_BAY_GP
	    	      ,A.YD_EQP_GP                             AS YD_EQP_GP
	    	      ,A.YD_STK_COL_NO                         AS YD_STK_COL_NO
	    	      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.YD_STK_COL_ACT_STAT   ELSE A.YD_STK_COL_ACT_STAT  END ) AS YD_STK_COL_ACT_STAT     
	    	      ,A.YD_STK_COL_RULE_XAXIS                 AS YD_STK_COL_RULE_XAXIS 
	    	      ,A.YD_STK_COL_RULE_YAXIS                 AS YD_STK_COL_RULE_YAXIS 
	    	      ,A.YD_STK_COL_W                          AS YD_STK_COL_W                   
	    	      ,A.YD_STK_COL_L                          AS YD_STK_COL_L                   
	    	      ,A.YD_CAR_USE_GP                         AS YD_CAR_USE_GP                 
	    	      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.TRN_EQP_CD ELSE A.TRN_EQP_CD END)    AS TRN_EQP_CD                       
	    	      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.CAR_NO  ELSE  A.CAR_NO END)                 AS CAR_NO                               
	    	      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.CARD_NO   ELSE A.CARD_NO END)               AS CARD_NO                             
	    	      ,A.WLOC_CD                               AS WLOC_CD                             
	    	      ,A.YD_PNT_CD                             AS YD_PNT_CD   
	    	      ,B.YD_CARPNT_CD AS YD_CARPNT_CD
	    	  FROM TB_YS_STKCOL A   
	    	     , TB_YS_CARPOINT B
	    	 WHERE B.YS_STK_COL_GP=A.YS_STK_COL_GP
	    	   AND A.WLOC_CD =  :V_WLOC_CD
	    	   AND A.YD_PNT_CD = :V_YD_PNT_CD  	
	    	*/	    	
	    	rsStkCol = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolWLocCdandPntCd", logId, methodNm, "적치열 조회"); 
	    	intLevLocGp = rsStkCol.size();	    	
	    	if (rsStkCol == null || intLevLocGp == 0) {
	    		szMsg= "[" + methodNm + "] 발지개소["+szSPOS_WLOC_CD+"] 및 포인트 코드["+szSPOS_YD_PNT_CD+"]가 타공정코드가 아니고 대기장입니다.";
	    		commUtils.printLog(logId, szMsg, "SL");
	    	}
	    	
			/**********************************************************
	    	 * 6.출발지 정보 CLEAR / 비활성화 상태(YD_STK_COL_ACT_STAT = C)로 업데이트
	    	 **********************************************************/
	    	if(intLevLocGp > 0) {
	    		
	    		rsStkCol.absolute(1);
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsStkCol.getRecord());		    	
		    	szYD_CARLD_STOP_LOC     	= commUtils.trim(recOutTemp.getFieldString("YS_STK_COL_GP")); 
		    	String szCOL_TRN_EQP_CD   	= commUtils.trim(recOutTemp.getFieldString("TRN_EQP_CD")); 		    	
		    	szMsg = "[" + methodNm + "] 발지개소코드["+szSPOS_WLOC_CD+"], " +
		    			"발지개소POINT코드["+szSPOS_YD_PNT_CD+"]로 야드에서 관리되는 적치열구분[출발지:"+szYD_CARLD_STOP_LOC+"]이 존재합니다.";
		    	commUtils.printLog(logId, szMsg, "SL");
				
				/**********************************************************
		    	 * 6-1.(적치열의 운송코드 = 전문 운송코드) -> 맵 Clear
		    	 **********************************************************/
				if( szCOL_TRN_EQP_CD.equals(szTRN_EQP_CD))	{					
					szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CARLD_STOP_LOC+"]의 운송장비코드["+szCOL_TRN_EQP_CD+"]와 전문의 운송장비코드["+szTRN_EQP_CD+"]가 같으므로 맵 Clear 시작 ";
					commUtils.printLog(logId, szMsg, "SL");
					
					/**********************************************************
			    	 * 6-1-1. 출발야드 적치열 -> 비활성상태(C) 로 업데이트
			    	 **********************************************************/
					szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CARLD_STOP_LOC+"]을 비활성상태로 변경처리 시작 ";
					commUtils.printLog(logId, szMsg, "SL");
					
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YS_STK_COL_GP",        szYD_CARLD_STOP_LOC);
			    	recInTemp.setField("YD_STK_COL_ACT_STAT",  "C");
			    	recInTemp.setField("YD_CAR_USE_GP",        "");
			    	recInTemp.setField("TRN_EQP_CD",           "");
			    	recInTemp.setField("CAR_NO",               "");
			    	recInTemp.setField("CARD_NO",              "");
			    	recInTemp.setField("MODIFIER", 				szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);			    	
			    	/* 
			    	com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStatClear
			    	UPDATE TB_YS_STKCOL
			    	   SET MOD_DDTT     = SYSDATE             
			    		 , MODIFIER     = :V_MODIFIER             
			    		 , YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
			    		 , TRN_EQP_CD   = null
			    		 , CAR_NO       = null
			    		 , CARD_NO      = null
			    	     , YD_CAR_USE_GP = null
			    	WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
			    	*/
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
					YsCarPointinforeg2("2","","",szYD_CARLD_STOP_LOC,"","","C",logId,methodNm);
					
					 // 적치베드 비활성상태로 변경
					/**********************************************************
			    	 * 6-1-3. 출발야드 적치베드 -> 야드적치베드활성상태(=C(비활성상태), YD_STK_BED_ACT_STAT) 
			    	 *                         및 BED중량MAX(=기본값, YD_STK_BED_WT_MAX) 으로 업데이트
			    	 **********************************************************/
					szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CARLD_STOP_LOC+"]의 베드를 비활성상태와 BED중량MAX기본값으로 변경처리 시작 ";
					commUtils.printLog(logId, szMsg, "SL");
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_STK_BED_WT_MAX", YsConstant.YD_STK_BED_WT_MAX_DEFAULT);
					recInTemp.setField("YS_STK_COL_GP", szYD_CARLD_STOP_LOC);
					recInTemp.setField("YD_STK_BED_ACT_STAT", "C");					
					/* 
					com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedYdStkColGp  
					UPDATE TB_YS_STKBED
					   SET MOD_DDTT     = SYSDATE             
						 , MODIFIER     = :V_MODIFIER             
						 , YD_STK_BED_ACT_STAT = NVL(:V_YD_STK_BED_ACT_STAT,YD_STK_BED_ACT_STAT)
					     , YD_STK_BED_WT_MAX = NVL(:V_YD_STK_BED_WT_MAX,YD_STK_BED_WT_MAX )
					   WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
					*/
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
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);	//Log ID
					recInTemp.setResultMsg(methodNm);	//Log Method Name
					recInTemp.setField("YS_STK_COL_GP", szYD_CARLD_STOP_LOC);
					recInTemp.setField("YD_STK_LYR_ACT_STAT", "C");
					recInTemp.setField("SSTL_NO", "");
					recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear  
					UPDATE TB_YS_STKLYR            
					   SET MOD_DDTT     = SYSDATE             
						 , MODIFIER     = :V_MODIFIER             
						 , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
					     , SSTL_NO = null
					     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
					 WHERE YS_STK_COL_GP  = :V_YS_STK_COL_GP
					 */
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
					recInTemp = JDTORecordFactory.getInstance().create();
			    	String szYdGp = szYD_CARLD_STOP_LOC.substring(0,2);
					String szJMS_TC_CD = "";
					
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
					
		    		recInTemp.setField("MSG_ID"			, szJMS_TC_CD);
					recInTemp.setField("YD_INFO_SYNC_CD", "3");						//1:동,2:SPAN,3:열,4:BED
					recInTemp.setField("YD_GP"			, szYD_CARLD_STOP_LOC.substring(0, 1));
					recInTemp.setField("YS_STK_COL_GP"	, szYD_CARLD_STOP_LOC);
					
					if(szTRN_WRK_FULLVOID_GP.equals("E")) {									// 공차출발
						recInTemp.setField("YD_CAR_PROG_STAT", "1");
						recInTemp.setField("YD_EQP_WRK_STAT" , "U");
						szMsg = "[" + methodNm + "] 공차출발시 시 저장위치 제원 야드L2로 전송";
					}else{																	// 영차출발
						recInTemp.setField("YD_CAR_PROG_STAT", "A");
						recInTemp.setField("YD_EQP_WRK_STAT" , "L");
						szMsg = "[" + methodNm + "] 영차출발시 시 저장위치 제원 야드L2로 전송";
					}
					
					//전송 Data 생성
					sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2(szJMS_TC_CD, recInTemp));

					szMsg="["+methodNm+"] 차량출발시 시 저장위치 제원 야드L2로 전송";
					commUtils.printLog(logId, szMsg, "SL");					
					
				}
				/**********************************************************
		    	 * 6-2.(적치열의 운송코드 != 전문 운송코드) -> 맵 Clear 하지 않음
		    	 **********************************************************/
				else{
					szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CARLD_STOP_LOC+"]의 운송장비코드["+szCOL_TRN_EQP_CD+"]와 전문의 운송장비코드["+szTRN_EQP_CD+"]가 다르므로 맵 Clear하지 않음 ";
					commUtils.printLog(logId, szMsg, "SL");
				}

	    	} 
	    	/***************************  출발개소코드 비활성처리 / 하차완료 차량스케줄 삭제 처리 / 각강 저장품 등록 완료  ***************************

	    	/***************************  하차개소코드 처리                                                                                                    ***************************/

	    	recInPara = JDTORecordFactory.getInstance().create();
	    	recInPara.setField("YD_CAR_USE_GP",			"L");
	    	recInPara.setField("YD_EQP_ID",             szYD_EQP_ID);
	    	recInPara.setField("TRN_EQP_CD",            szTRN_EQP_CD);
	    	recInPara.setField("WLOC_CD",               szARR_WLOC_CD);
	    	recInPara.setField("PNT_DMD_DT",            commUtils.getDateTime14());
	    	recInPara.setField("SPOS_YD_PNT_CD",        szSPOS_YD_PNT_CD);
	    	recInPara.setField("SPOS_WLOC_CD",          szSPOS_WLOC_CD);
	    	recInPara.setField("TRN_WRK_FULLVOID_GP",	szTRN_WRK_FULLVOID_GP);

	    	if( !szYD_DIRECT_CARLD_GP.equals("")) {
	    		recInPara.setField("YD_DIRECT_CARLD_GP",szYD_DIRECT_CARLD_GP);
	    	}else{
	    		recInPara.setField("YD_DIRECT_CARLD_GP","");
	    	}
	    	
	    	recInPara.setField("IS_EJB_CALL", szIS_EJB_CALL);//하위 모듈에서 EJB Call or JMS Call 유무 설정
	    	recInPara.setField("YD_AUTO_LOT", szYD_AUTO_LOT);//자동LOT/수동LOT 편성 판단 변수
	    	
	    	/**********************************************************
	    	 * 8. 공차 (차량스케줄등록/차량도착POINT요구모듈 호출)
	    	 **********************************************************/	
	    	if(szTRN_WRK_FULLVOID_GP.equals("E")) {
	    		
	    		//야드상차출발위치
	    		recInPara.setField("YD_CARLD_LEV_LOC", szYD_CARLD_STOP_LOC);
	    		recInPara.setField("TRN_EQP_STK_CAPA", szTRN_EQP_STK_CAPA);
	    		
	    		if(szARR_WLOC_CD.substring(0, 1).equals("S")) {   // 다른 특수강 야드로 갈 경우만 생성처리함 
	    			if(("S3Y30".equals(szARR_WLOC_CD)||"S3Y20".equals(szARR_WLOC_CD))&&szARR_YD_PNT_CD.equals("XXXX")){
	    				/* 
	    				 * 빌렛옥외야드(L2야드)로 상차출발하는 대상은 상차스케쥴을 만들지 않는다.
	    				 * 조건 : 개소코드 - S3Y30,S3Y20 착지포인트 - XXXX
	    				 */
	    			}else{
	    				/**********************************************************
			    		* 하차지 정보로 
			    		* 1.차량스케줄 생성
			    		* 2.차량도착POINT요구모듈을 호출
			    		**********************************************************/	    			
		    			sndRecord = this.procInsCarSch(logId, methodNm,recInPara,sndRecord);
		    			
			    		commUtils.printLog(logId, sndRecord.getTaskCode(), "SL");			
			    		intRtnVal =   Integer.parseInt(sndRecord.getTaskCode());
			    		if(intRtnVal == -1) {
							szMsg=" LOT편성 호출 중 Error";
							commUtils.printLog(logId, szMsg, "SL");					
							m_ctx.setRollbackOnly();
							throw new DAOException(szMsg);
			    		}
	    			}
	    		}	
	    	}
	    	/**********************************************************
	    	 * 9. 영차 
	    	 **********************************************************/
	    	else if(szTRN_WRK_FULLVOID_GP.equals("F")) {
	    		
	    		if(("S4Y10".equals(szARR_WLOC_CD)||"S3Y30".equals(szARR_WLOC_CD)||"S3Y20".equals(szARR_WLOC_CD))&&szARR_YD_PNT_CD.equals("XXXX")){//빌렛옥외(L2야드)로 하차출발시
	    			/* 
    				 * 빌렛옥외야드(L2야드)로 하차출발하는 대상은 상차스케쥴정보를 삭제한다.
    				 * 조건 : 개소코드 - S4Y10,S3Y30,S3Y20, 착지포인트 - XXXX, 개소코드 : BSY04(철분말)
    				 */
    				recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					recInTemp.setField("DEL_YN", "Y");
					recInTemp.setField("MODIFIER", szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
					
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
					
					commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarFtmvMtl", logId, methodNm, "TB_YS_CARFTMVMTL 종료");
		            
		        	//차량스케줄 종료
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch  
					UPDATE TB_YS_CARSCH
					   SET MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					     , DEL_YN = 'Y'
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
 				       AND DEL_YN = 'N'

					 */
					intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch", logId, methodNm, "TB_YS_CARSCH 종료");
					
					szMsg= "[" + methodNm + "] 빌렛옥외(L2야드)로 하차출발시 차량스케줄["+szYD_CAR_SCH_ID+"] 종료 성공 ";
					commUtils.printLog(logId, szMsg, "SL");
					
	    		}
	    		else if("BSY04".equals(szARR_WLOC_CD)){
	    			//철분말 일경우 
	    			 recInTemp = JDTORecordFactory.getInstance().create();
					 recInTemp.setField("MODIFIER", szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);	
					 recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					
					 /* 빌렛공통 철분말 하차 위치 동기화
					  * UPDATE TB_PB_BILLETCOMM
						SET    MODIFIER = :V_MODIFIER
						      ,MOD_DDTT = SYSDATE
						      ,YD_GP = 'G'
						      ,YD_BAY_GP = 'H'
						      ,YD_EQP_GP = 'A0'
						      ,YS_STK_COL_NO = '01'
						      ,YS_STK_BED_NO = '01'
						      ,YS_STK_LYR_NO = '01'
						      ,YS_STK_SEQ_NO = '1'
						      ,YS_STR_LOC = 'GHA00101011'
						      ,YS_STR_LOC_HIS1 = YS_STR_LOC
						      ,YS_STR_LOC_HIS2 = YS_STR_LOC_HIS1
						WHERE  SSTL_NO IN (SELECT SSTL_NO FROM TB_YS_CARFTMVMTL WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID)
					  */
					 commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updTSYSJ004Bltcomm", logId, methodNm, "철분말 하차 BLTCOMM 위치 변경");
					 
					 
					 //wrkhist 실적 입력 
					 
					 	recInTemp = JDTORecordFactory.getInstance().create();
					 	recInTemp.setResultCode(logId);	//Log ID
					 	recInTemp.setResultMsg(methodNm);	//Log Method Name
					 	recInTemp.setField("MODIFIER", szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
					 	recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
						commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkHistByPda", logId, methodNm, "이송 작업실적 등록");
						
					 
					 
					 //L2 저장품제원 전문 
					 rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
					 
					 	recInPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				    	rsWrkBookMtl = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarftmvmtlID", logId, methodNm, "차량재료 조회"); 		    	
				    	if (rsWrkBookMtl == null || rsWrkBookMtl.size() <= 0) {
							szMsg = "["+methodNm+"] 차량재료 조회 data not found";
				    		commUtils.printLog(logId, szMsg, "SL");
				    		m_ctx.setRollbackOnly();
							throw new DAOException(szMsg);
						}
				    	
				    	for(int Loop_i = 1; Loop_i <= rsWrkBookMtl.size(); Loop_i++) {
				    		//L2 저장위치 전송 전문 쿼리 생성 
				    		rsWrkBookMtl.absolute(Loop_i);
				    		
				    		
				    		recOutTemp = JDTORecordFactory.getInstance().create();
				    		recOutTemp.setRecord(rsWrkBookMtl.getRecord());
				    		
				    		String szSSTL_NO = commUtils.trim(recOutTemp.getFieldString("SSTL_NO"));
				    		recInTemp  = JDTORecordFactory.getInstance().create();
				    		recInTemp.setField("TC_CD"		, "YSN4L002"); 
					    	recInTemp.setField("SSTL_NO"		, szSSTL_NO); 
					    	recInTemp.setField("YS_STR_LOC"	, "GHA00101011");
					    	recInTemp.setField("YD_INFO_SYNC_CD", "6_3");
					    	sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YSN4L002", recInTemp));
					    	
					    	szMsg="저장품 제원 야드L2[ YSN4L002 ]로 전송";
							commUtils.printLog(logId, szMsg, "SL");
							
							recInTemp  = JDTORecordFactory.getInstance().create();
							recInTemp.setField("MODIFIER"		, szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName); 
					    	recInTemp.setField("SSTL_NO"		, szSSTL_NO); 
					    	
							commDao.update(recInTemp, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updgdsWhsPlnInfojlByStkLyr", logId, methodNm, "TB_YS_STKLYR");
				    		
							//야드저장품 위치정보 수정하기
							recInTemp  = JDTORecordFactory.getInstance().create();
							recInTemp.setField("YS_STK_COL_GP"		, "GHA001" );
							recInTemp.setField("YS_STK_BED_NO"		, "01" );
							recInTemp.setField("YS_STK_LYR_NO"		, "01");
							recInTemp.setField("YS_STK_SEQ_NO"		, "1" );
							recInTemp.setField("YS_STR_LOC"			, "GHA00101011" );
							recInTemp.setField("SSTL_NO"				, szSSTL_NO );
							
							commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
							
				    	
				    		}
				    	
				    	//carsch 수정
				    		String currDt = commUtils.getDateTime14(); //현재시각
				    		//하차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
						 	recInTemp = JDTORecordFactory.getInstance().create();
						 	recInTemp.setResultCode(logId);	//Log ID
						 	recInTemp.setResultMsg(methodNm);	//Log Method Name
						 	recInTemp.setField("MODIFIER", szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
						 	recInTemp.setField("WR_DT" 			, currDt ); 
						 	recInTemp.setField("YD_CAR_SCH_ID" 	, szYD_CAR_SCH_ID ); 
						 	/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchUd 
							--크레인권하실적 하차 차량스케줄 수정 
							UPDATE TB_YS_CARSCH
							   SET MODIFIER         = :V_MODIFIER
								  ,MOD_DDTT         = SYSDATE
							      ,YD_EQP_WRK_STAT  = 'U' --공차
							      ,YD_CAR_PROG_STAT = 'E' --하차완료
							      ,YD_CARUD_CMPL_DT = NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
							 WHERE YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID
							   AND DEL_YN           = 'N'
							*/     
						 	
							commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchUd", logId, methodNm, "차량스케줄 수정");
							
	    		}
	    		else{
	    			
	    			/**********************************************************
			    	 * 9-1. 차량재료 조회 (YD_CAR_SCH_ID 이용)
			    	 **********************************************************/		
		    		recInPara.setField("YD_CAR_SCH_ID"	, szYD_CAR_SCH_ID);	    		
			    	
			    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarftmvmtlID 
			    	SELECT A.YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
			    	     , A.SSTL_NO  AS SSTL_NO
			    	     , A.REGISTER  AS REGISTER
			    	     , TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
			    	     , A.MODIFIER  AS MODIFIER
			    	     , TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
			    	     , A.DEL_YN  AS DEL_YN
			    	     , A.YS_STK_BED_NO  AS YS_STK_BED_NO
			    	     , A.YS_STK_LYR_NO  AS YS_STK_LYR_NO
			    	     , A.YS_STK_SEQ_NO  AS YS_STK_SEQ_NO
			    	     , A.HCR_GP  AS HCR_GP
			    	     , A.STL_PROG_CD  AS STL_PROG_CD
			    	     , A.YS_MTL_ITEM  AS YS_MTL_ITEM
			    	     , B.YD_RCPT_PLN_STR_LOC
			    	     , (SELECT ARR_YD_PNT_CD FROM USRTSA.TB_TS_MATL_FTMV_WO C
			    	         WHERE C.TRANSWORD_SEQNO=(SELECT MAX(TRANSWORD_SEQNO) FROM TB_TS_MATL_FTMV_WO D
			    	                                   WHERE D.STL_NO=C.STL_NO
			    	                                   )
			    	           AND C.STL_NO=A.SSTL_NO ) AS ARR_YD_PNT_CD
			    	     , B.CUST_CD     
			    	     , B.DETAIL_ARR_CD     
			    	     , B.HEAT_NO     
			    	     , B.YD_MTL_L_GP             
			    	     , B.CUST_CD || B.DETAIL_ARR_CD || B.HEAT_NO || B.YD_MTL_L_GP AS GROUP_CHK_ID            
			    	  FROM TB_YS_CARFTMVMTL A
			    	     , TB_YS_STOCK B
			    	 WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			    	   AND A.SSTL_NO = B.SSTL_NO(+)
			    	   AND A.DEL_YN='N'
			    	 ORDER BY YS_STK_BED_NO, YS_STK_LYR_NO DESC
			    	*/		    	

		    		rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    	rsWrkBookMtl = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarftmvmtlID", logId, methodNm, "차량재료 조회"); 		    	
			    	if (rsWrkBookMtl == null || rsWrkBookMtl.size() <= 0) {
						szMsg = "["+methodNm+"] 차량재료 조회 data not found";
			    		commUtils.printLog(logId, szMsg, "SL");
			    		m_ctx.setRollbackOnly();
						throw new DAOException(szMsg);
					}	    	
					
		    		/**********************************************************
			    	 * 9-3. 하차작업예약 생성 작업(중복으로 영차 출발 실적 들어오는 경우 제외
			    	 **********************************************************/
			    	if(!szYD_CARUD_WRK_BOOK_ID.equals("")) {
			    		JDTORecord recInTemp1 = JDTORecordFactory.getInstance().create();
				    	recInTemp1.setRecord(rcvMsg);
				    	recInTemp1.setField("YD_WBOOK_ID"	, szYD_CARUD_WRK_BOOK_ID);
				    	
				    	JDTORecordSet rsWrkBookMtl1 = commDao.select(recInTemp1, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdWrkbookmtlId", logId, methodNm, "작업예약재료 조회"); 		    	
				    	if (rsWrkBookMtl1 == null || rsWrkBookMtl1.size() <= 0) {
							szMsg = "["+methodNm+"] 기편성된 작업 예약 재료 가 없습니다. 재편성";
				    		commUtils.printLog(logId, szMsg, "SL");
				    		
					    	this.procInsWrkBookCarUd(logId,recInPara);
						}
			    	} else{
			    		/**********************************************************
			    		* 작업예약 생성
			    		* 차량스케줄 차량 상차출발 상태로 변경
			    		**********************************************************/
			    		this.procInsWrkBookCarUd(logId,recInPara);
			    	}		    	

		    		/**********************************************************
			    	 * 9-4. 차량 도착 POINT 요구 (TSYDJ002)
			    	 **********************************************************/	
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setRecord(rcvMsg);
			    	recInTemp.setField("JMS_TC_CD"			, "TSYSJ002"); 
			    	recInTemp.setField("WLOC_CD"			, szARR_WLOC_CD);
			    	recInTemp.setField("TRN_WRK_FULLVOID_GP", "F"); 	    	
			    	
			    	sndRecord = this.rcvTSYSJ002(recInTemp);
		    		
					/**********************************************************
			    	 * 9-5. 차량 영차 출발 시 저장품 제원 -> 야드 L2 로 전송
			    	 *        야드BLOOM:N1,BILLET:N2,선재:N3,봉강:N4,선재자동화:N5,봉강자동화:N6
			    	 **********************************************************/
					if(szYD_GP.startsWith("B") ){
						szMSG_ID =  "YSN1L002";
			    	}else if(szYD_GP.startsWith("C")){
			    		szMSG_ID =  "YSN2L002";
			    	}else if(szYD_GP.startsWith("KA")){
			    		szMSG_ID =  "YSN6L002";
		          	}else if(szYD_GP.startsWith("KB")){
		          		szMSG_ID =  "YSN4L002";
			    	}else if(szYD_GP.startsWith("KD")){
			    		szMSG_ID =  "YSN5L002";
			    	}else if(szYD_GP.startsWith("KE")){
			    		szMSG_ID =  "YSN3L002";
			    	}	
		    		
			    	for(int Loop_i = 1; Loop_i <= rsWrkBookMtl.size(); Loop_i++) {
			    		rsWrkBookMtl.absolute(Loop_i);
			    		recInTemp  = JDTORecordFactory.getInstance().create();
			    		recOutTemp = JDTORecordFactory.getInstance().create();
			    		recOutTemp.setRecord(rsWrkBookMtl.getRecord());
			    		
						if( !szMSG_ID.equals("") ) {
							recInTemp  = JDTORecordFactory.getInstance().create();
							recInTemp.setResultCode(logId);	//Log ID
							recInTemp.setResultMsg(methodNm);	//Log Method Name
							recInTemp.setField("MSG_ID"			, szMSG_ID);
							recInTemp.setField("YD_INFO_SYNC_CD", "B");	//차량입고
					    	recInTemp.setField("SSTL_NO"		, commUtils.trim(recOutTemp.getFieldString("SSTL_NO"       ))); 
					    	recInTemp.setField("YS_STK_COL_GP"	, szYD_GP);
					    	sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2(szMSG_ID, recInTemp));
					    	
					    	szMsg=" 영차 출발 시 저장품 제원 야드L2[" + szMSG_ID + "]로 전송";
							commUtils.printLog(logId, szMsg, "SL");
						}		    		
			    	}
	    		}
	    	}
	    	else{	    		
				szMsg="운송작업영공구분코드 Error";
				commUtils.printLog(logId, szMsg, "SL");
				m_ctx.setRollbackOnly();
				throw new DAOException(szMsg);				
	    	}		    	
	    	szMsg="[" + methodNm + "] 소재차량 출발 실적 처리 메소드 성공적으로 실행 완료 ]";
			commUtils.printLog(logId, szMsg, "SL");	  
			
		} catch(Exception e){
			szMsg="소재차량 도착처리 Error:" +e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
			m_ctx.setRollbackOnly();
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		commUtils.printLog(logId, methodNm, "S-");
		return sndRecord;
	} //end of rcvTSYSJ004()	

	
	/**
	 *      [A] 오퍼레이션명 : 서문스크랩상차실적(TSYSJ005)  
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYSJ005(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "서문스크랩상차실적[YsCommCarMvSeEJB.rcvTSYSJ005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
 
		JDTORecord	  	sndRecord	= JDTORecordFactory.getInstance().create();
		JDTORecord    	recInTemp   = null;
 


	    int intRtnVal 				= 0 ;
	    int szStkLyrNo				= 0 ;
	    String szMsg           		= "";
	    String szOperationName		= "서문스크랩상차실적";
	    String szMethodName     	= "rcvTSYSJ005";
	    
	    
	    String szTRN_EQP_CD    		= "";						//운송장비코드
	    String szWLOC_CD       		= "";						//개소코드
	    String szARR_YD_PNT_CD		= "";
	    String szCARLD_CMPL_DT 		= "";
	    String szCARLD_SH 			= "";
	 
        String msgId = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
        
        if(msgId==null || msgId.equals("")){
        	return sndRecord;
        }

        szMsg="["+szOperationName+"] 전문수신 : TCCODE=" +msgId ;
        
	    try{

			commUtils.printLog(logId, methodNm, "S+");
			
			commUtils.printParam(logId + "서문스크랩상차실적 수신 ", rcvMsg);
			
	    	//운송장비코드, 개소코드, 상하차구분코드, 포인트요구일시
			szTRN_EQP_CD      		= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"));
			szWLOC_CD      			= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 
			szARR_YD_PNT_CD			= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); 
			szCARLD_CMPL_DT		    = commUtils.trim(rcvMsg.getFieldString("CARLD_CMPL_DT")); 
			szCARLD_SH			    = commUtils.trim(rcvMsg.getFieldString("CARLD_SH")); 
			
			
			String StmpStlNo = "";
			JDTORecord jrParam = null;
			
			if(!"SMY20".equals(szWLOC_CD)){
				szMsg=" 서문스크랩상차실적 발지개소[" + szWLOC_CD + "]가 잘 못 되었습니다. 구내운송 확인 필요";
				commUtils.printLog(logId, szMsg, "SL");
				throw new DAOException(szMsg);
			}
			
			if("".equals(szCARLD_SH)||"0".equals(szCARLD_SH)){
				szMsg=" 서문스크랩상차실적 총 재료건수[" + szCARLD_SH + "]가 잘 못 되었습니다. 구내운송 확인 필요";
				commUtils.printLog(logId, szMsg, "SL");
				throw new DAOException(szMsg);
			}
			
		/*	//MAX저장위치 조회
			rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getBundleCommydloc", logId, methodNm, "TB_PB_BUNDLECOMM 조회"); 
			if(rsResult.size() > 1) {
				
				rsResult.absolute(1);			
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsResult.getRecord());
				
				szYS_STR_LOC      	= commUtils.trim(recOutTemp.getFieldString("YS_STR_LOC")); 
				szYS_STK_COL_NO		=szYS_STR_LOC.substring(4 , 6);
				szYS_STK_BED_NO		=szYS_STR_LOC.substring(6 , 8);
				szYS_STK_LYR_NO		=szYS_STR_LOC.substring(8 , 10);
				
				szMsg= "[" + methodNm + "] 최단 적치위치["+szYS_STR_LOC+"]가 있습니다.";
				commUtils.printLog(logId, szMsg, "SL");  
				
				if(Integer.parseInt(szYS_STK_LYR_NO) == 99 ){
					
					if(Integer.parseInt(szYS_STK_BED_NO) == 99 ){
						szYS_STK_COL_NO = ""+(Integer.parseInt(szYS_STK_COL_NO) + 1);
						szYS_STK_BED_NO = "01";
						szYS_STK_LYR_NO = "01";
					}else{					
						szYS_STK_BED_NO = ""+(Integer.parseInt(szYS_STK_BED_NO) + 1); 
						szYS_STK_LYR_NO = "01";
					}
				} 
					
				szStkLyrNo = Integer.parseInt(szYS_STK_LYR_NO); 				 
					
			}
			*/
			
			
			if(Integer.parseInt(szCARLD_SH) > 0 ){
				for(int Loop_i = 1; Loop_i <= Integer.parseInt(szCARLD_SH); Loop_i++) {						
					StmpStlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+Loop_i));
					commUtils.printLog(logId, StmpStlNo, "SL");		
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("SSTL_NO", StmpStlNo);  
					jrParam.setField("CARLD_CMPL_DT", szCARLD_CMPL_DT); 
					jrParam.setField("ARR_YD_PNT_CD", szARR_YD_PNT_CD); 
					
					if ("SMY20".equals(szWLOC_CD)) {
						//번들공통 저장위치 등록(업무기준:YSB006 강종별 저장위치 결정)
						intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBundleCommydloc", logId, methodNm, "TB_PB_BUNDLECOMM 저장위치수정");
					}	
				}
			}
			
			 
		} catch(Exception e){
			
			szMsg="서문스크랩상차실적 Error:" +e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
			
			m_ctx.setRollbackOnly();
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
		commUtils.printLog(logId, methodNm, "S-");
		return sndRecord;
	} //end of rcvTSYSJ005()
	
	/**
	 * 오퍼레이션명 : 소재차량 공차도착 실적
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procLDMatlCarArr(String logId, JDTORecord rcvMsg, JDTORecord recCarSch, JDTORecord rcvMsgCol)throws JDTOException  {
		String methodNm = "소재차량 공차도착 실적[YsCommCarMvSeEJB.procLDMatlCarArr] < " + rcvMsg.getResultMsg();
		
		JDTORecordSet rsResult          = null;
		JDTORecord    recInTemp         = null;
//		JDTORecord    recOutTemp        = null;
		JDTORecord    sndRecord         = JDTORecordFactory.getInstance().create();
		
	    int intRtnVal 		   	= 0 ;
	    
	    String szMsg           	= "";
	    String szMethodName    	= "procLDMatlCarArr";
	    String szREG_MOD_USER   = "MtlCarArr";

	    String szTRN_EQP_CD    	= "";
	    String szARR_WLOC_CD   	= "";
	    String szARR_YD_PNT_CD 	= "";
	    String szYS_STK_COL_GP 	= "";
	    String szYD_WBOOK_ID   	= "";
	    String szYD_SCH_CD		= null;
	    String szYD_CAR_SCH_ID 	= "";
	    String szCOL_TRN_EQP_CD	= "";
	    String szCARD_NO        = "";
	    String szMSG_GP			= null;
	    String szYD_PNT_CD		= null;
	    String szYD_CARLD_STOP_LOC = null;
	    String szYD_CAR_PROG_STAT  = null;
	    String szYD_STK_COL_ACT_STAT = "";

	    boolean bIsReplacable	= false;

	    
	    try{

	    	commUtils.printLog(logId, methodNm, "S+");
	    	
	    	szMSG_GP    			= commUtils.trim(rcvMsg.getFieldString("MSG_GP")); 
	    	szARR_WLOC_CD   		= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 
	    	szARR_YD_PNT_CD 		= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); 
	    	szYD_WBOOK_ID   		= commUtils.trim(rcvMsg.getFieldString("YD_CARLD_WRK_BOOK_ID")); // 작업예약TABLE의 작업예약ID
	    	szYD_SCH_CD    			= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));            // 작업예약TABLE의 스케쥴 코드
	    	szTRN_EQP_CD   			= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 

	    	szMsg="["+methodNm+"] 수신정보 : MSG_GP["+szMSG_GP+"],착지개소코드["+szARR_WLOC_CD+"],포인트["+szARR_YD_PNT_CD+"],상차작업예약ID["+szYD_WBOOK_ID+"],스케쥴코드["+szYD_SCH_CD+"],차량번호["+szTRN_EQP_CD+"]";
			commUtils.printLog(logId, szMsg, "SL");

			//수신된 차량 정지위치(COL) 정보
	    	szYS_STK_COL_GP   		= commUtils.trim(rcvMsgCol.getFieldString("YS_STK_COL_GP")); 
	    	szYD_STK_COL_ACT_STAT	= commUtils.trim(rcvMsgCol.getFieldString("YD_STK_COL_ACT_STAT")); 
	    	szCOL_TRN_EQP_CD    	= commUtils.trim(rcvMsgCol.getFieldString("TRN_EQP_CD")); 
	    	szCARD_NO    			= commUtils.trim(rcvMsgCol.getFieldString("CARD_NO")); 

	    	szMsg="["+methodNm+"] COL 정보 : 적치열["+szYS_STK_COL_GP+"],적치상태["+szYD_STK_COL_ACT_STAT+"],적치열 차량["+szCOL_TRN_EQP_CD+"],적치열 카드["+szCARD_NO+"]";
			commUtils.printLog(logId, szMsg, "SL");
	    	
	    	//차량 SCH 정보
			szYD_CAR_SCH_ID   		= commUtils.trim(recCarSch.getFieldString("YD_CAR_SCH_ID")); 
			szYD_CAR_PROG_STAT  	= commUtils.trim(recCarSch.getFieldString("YD_CAR_PROG_STAT")); 
			szYD_CARLD_STOP_LOC 	= commUtils.trim(recCarSch.getFieldString("YD_CARLD_STOP_LOC")); 
			szYD_PNT_CD   			= commUtils.trim(recCarSch.getFieldString("YD_PNT_CD1" )); 

	    	szMsg="["+methodNm+"] 차량 SCH 정보 : 차량스케쥴["+szYD_CAR_SCH_ID+"],야드적치열활성상태["+szYD_CAR_PROG_STAT+"],도착위치["+szYD_CARLD_STOP_LOC+"],도착포인드["+szYD_PNT_CD+"]";
			commUtils.printLog(logId, szMsg, "SL");

	    	
			if( !szYD_WBOOK_ID.equals("") ) { 
				//작업예약이 존재하는 경우
				if(szYD_SCH_CD.substring(0,2).equals(szYS_STK_COL_GP.substring(0,2)) ) {			
	    			//신규 작업예약 생성하지 않기
	    			bIsReplacable = false;
	    		}else{	
	    			//기존작업예약이 등록된 동과 차량이 도착한 현재동이 다른 경우
	    			//기존작업예약을 삭제처리하고 이미 등록된 차량이송준비스케줄을 조회		    		  			
					recInTemp = JDTORecordFactory.getInstance().create();
	    	    	recInTemp.setField("YD_WBOOK_ID", 	   	szYD_WBOOK_ID);
	    	    	recInTemp.setField("MODIFIER", 	   		szMethodName.substring(0, 10));
	    	    	recInTemp.setField("DEL_YN", 	   		"Y");
	    	    	
	    	    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBookMtl  
						UPDATE TB_YS_WRKBOOKMTL
						   SET MODIFIER    = :V_MODIFIER
						      ,MOD_DDTT    = SYSDATE
						      ,DEL_YN      = 'Y'
						 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
						   AND DEL_YN      = 'N'
	    	    	 */
	    	    	commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL 삭제");
	    			
					szMsg="["+methodNm+"]  기존에 등록된 작업예약이 존재하는 동과 차량이 도착한 현재동이 다르므로 기존작업예약["+szYD_WBOOK_ID+"]재료을 삭제 완료";
					commUtils.printLog(logId, szMsg, "SL");
					
	    	    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBook

	    	    	UPDATE TB_YS_WRKBOOK
					   SET MODIFIER    = :V_MODIFIER
					      ,MOD_DDTT    = SYSDATE
					      ,DEL_YN      = 'Y'
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND DEL_YN      = 'N'
	    	    	 */
	    	    	commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBook", logId, methodNm, "TB_YS_WRKBOOK 삭제");
	    			
	    	    	szMsg="["+methodNm+"]  기존에 등록된 작업예약이 존재하는 동과 차량이 도착한 현재동이 다르므로 기존작업예약["+szYD_WBOOK_ID+"]을 삭제 완료";
	    			commUtils.printLog(logId, szMsg, "SL");
					
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					//신규 작업예약 생성
	    			bIsReplacable = true;
	    		}
    		}else{	 
    			//신규 작업예약 생성하기
    			bIsReplacable = true;
    		}
    		
			
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			//신규 작업예약 생성
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			JDTORecord recOutPara = JDTORecordFactory.getInstance().create();
    		if( bIsReplacable ) {
    			
       			// LOT 편성된 정보가 없기 때문에 SCH_CD편성
    			if (szYS_STK_COL_GP.substring(0, 1).equals("B")||szYS_STK_COL_GP.substring(0, 1).equals("C")) {  //블룸/빌렛
    				szYD_SCH_CD = szYS_STK_COL_GP.substring(0, 2)+"TR02UM";
    			} else if (szYS_STK_COL_GP.substring(0, 1).equals("K")){  //제품창고
    				szYD_SCH_CD = szYS_STK_COL_GP.substring(0, 2)+"TR10SM";
    			}
 
    			//이미 등록된 차량이송준비스케줄을 조회
    			szMsg="["+methodNm+"] 차량이송준비스케줄을 조회 후 작업예약 등록 시작";
    			commUtils.printLog(logId, szMsg, "SL");
				recInTemp = JDTORecordFactory.getInstance().create();
				
//				recInTemp = JDTORecordFactory.getInstance().create();
//				JDTORecord recInTemp2 = JDTORecordFactory.getInstance().create();
//				recInTemp2.setField("YD_CAR_SCH_ID"	, szYD_CAR_SCH_ID);
//				recInTemp2.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
//				recInTemp2.setField("YD_PNT_CD"		, szARR_YD_PNT_CD);
//				recInTemp2.setField("YD_SCH_CD"		, szYD_SCH_CD);
//				
//				recInTemp = this.procYdWbookForCarLd(recInTemp2);
				
				//-------------------------------------------------------------------------------------------------
				//	도착포인트에 해당하는 준비스케줄이 존재 하는 경우 조회
				//-------------------------------------------------------------------------------------------------
				szMsg = "["+methodNm+"] ★★★도착포인트에 해당하는 준비스케줄이 존재 하는 경우 조회 시작★★★";
				commUtils.printLog(logId, szMsg, "SL");
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				JDTORecord recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_GP"			, szYD_SCH_CD.substring(0, 1));
				recPara.setField("YD_SCH_CD"		, szYD_SCH_CD.substring(0, 1) + "_TR");
				recPara.setField("YD_WRK_PLAN_CRN"	, "");
				recPara.setField("YD_PREP_WK_ST"	, "L");
				recPara.setField("YD_PNT_CD"		, szYD_PNT_CD);
	 			recPara.setField("CAR_GP"			, szTRN_EQP_CD.substring(1, 2));
				
				 /* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStockNPrepSchByYdCrnCarGp
				 -- 가장빠른 상차 LOT
				 WITH TEMP_TABLE1 AS (
				    SELECT *
				      FROM USRYSA.TB_YS_PREPSCH A
				     WHERE YD_GP = :V_YD_GP
				       AND YD_SCH_CD LIKE :V_YD_SCH_CD || '%'
				       AND YD_PREP_WK_ST LIKE :V_YD_PREP_WK_ST || '%'
				       AND NVL(YD_WRK_PLAN_CRN, '*') LIKE :V_YD_WRK_PLAN_CRN || '%'
				       AND NVL(CAR_GP, '*') LIKE :V_CAR_GP || '%'
				       AND SUBSTR(YD_SCH_CD,2,1) =SUBSTR(:V_YD_PNT_CD,2,1)
				       AND A.DEL_YN = 'N'
				     ORDER BY YD_CARASGN_SEQ, A.YD_PREP_SCH_ID
				), TEMP_TABLE2 AS (
				SELECT A.SSTL_NO       AS SSTL_NO
				     -- ,A.YD_AIM_RT_GP  AS YD_AIM_RT_GP
				      ,A.YS_MTL_ITEM   AS YS_MTL_ITEM
				      ,A.YD_MTL_L      AS YD_MTL_L
				      ,A.YD_MTL_W      AS YD_MTL_W
				      ,A.YD_MTL_WT     AS YD_MTL_WT
				      ,B.YD_PREP_SCH_ID
				      ,B.YD_SCH_CD
				      ,B.YD_GP 
				      ,B.YD_PREP_WK_ST
				      ,B.YD_TO_LOC_DCSN_MTD
				      ,B.YD_TO_LOC_GUIDE
				      ,B.ARR_WLOC_CD
				      ,B.YD_AIM_YD_GP
				      ,B.YD_AIM_BAY_GP
				      ,B.YD_CARASGN_SEQ
				      ,B.YD_EQP_WRK_SH
				      ,B.YD_WRK_PLAN_CRN
				      ,B.YS_STK_COL_GP AS YS_STK_COL_GP
				      ,B.YS_STK_BED_NO AS YS_STK_BED_NO
				      ,B.YS_STK_LYR_NO AS YS_STK_LYR_NO
				      ,B.YS_STK_SEQ_NO AS YS_STK_SEQ_NO
				  FROM USRYSA.TB_YS_STOCK  A
				      , (
				      SELECT A.YD_PREP_SCH_ID
				            ,A.YD_SCH_CD
				            ,A.YD_GP 
				            ,A.YD_PREP_WK_ST
				            ,A.YD_TO_LOC_DCSN_MTD
				            ,A.YD_TO_LOC_GUIDE
				            ,A.ARR_WLOC_CD
				            ,A.YD_AIM_YD_GP
				            ,A.YD_AIM_BAY_GP
				            ,A.YD_CARASGN_SEQ
				            ,A.YD_EQP_WRK_SH
				            ,A.YD_WRK_PLAN_CRN
				            ,B.SSTL_NO
				            ,B.YS_STK_COL_GP AS YS_STK_COL_GP
				            ,B.YS_STK_BED_NO AS YS_STK_BED_NO
				            ,B.YS_STK_LYR_NO AS YS_STK_LYR_NO
				            ,B.YS_STK_SEQ_NO AS YS_STK_SEQ_NO
				        FROM TEMP_TABLE1 A
				            ,TB_YS_PREPMTL B
				            ,TB_YS_STKLYR C
				            ,TB_YS_CARPOINT D
				        WHERE A.YD_PREP_SCH_ID = B.YD_PREP_SCH_ID
				         AND B.SSTL_NO=C.SSTL_NO
				         AND C.YD_STK_LYR_MTL_STAT='C'
				         AND B.DEL_YN = 'N'
				         AND D.YD_GP ='A'
				         AND D.YD_PNT_CD=:V_YD_PNT_CD
				--         AND SUBSTR(C.YS_STK_COL_GP,3,2) BETWEEN  D.YD_SPAN_FROM AND D.YD_SPAN_TO
				         ORDER BY YD_CARASGN_SEQ ASC, YD_PREP_SCH_ID ASC
				      ) B
				 WHERE A.SSTL_NO = B.SSTL_NO
				   AND A.DEL_YN = 'N'
				 )
				 SELECT *
				   FROM TEMP_TABLE2 B
				  WHERE YD_PREP_SCH_ID=(SELECT *
				                          FROM 
				                               (SELECT YD_PREP_SCH_ID 
				                                  FROM TEMP_TABLE2 
				                                 ORDER BY YD_CARASGN_SEQ ASC, YD_PREP_SCH_ID ASC)
				                         WHERE ROWNUM<=1)
				ORDER BY B.YS_STK_COL_GP ASC,B.YS_STK_BED_NO DESC,B.YS_STK_LYR_NO DESC
				*/
	 			//열 작은것부터, 베드 높은것부터, 단 높은것부터
				
				rsResult = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStockNPrepSchByYdCrnCarGp", logId, methodNm, "적치열 조회"); 
		    	
		    	if (rsResult == null || rsResult.size() <= 0) {
		    		szMsg = "["+methodNm+"] 배차순서가 빠르고 준비스케줄ID가 빠른 차량 이송 대상재를 준비스케줄에서 조회 시 대상 없음 ";
					commUtils.printLog(logId, szMsg, "SL");
		    		szMsg = "["+methodNm+"] 직상차를 위한 작업 대기 ";
					commUtils.printLog(logId, szMsg, "SL");
//					throw new DAOException(szMsg);
		    	} else {			
					//-------------------------------------------------------------------------------------------------
					//	작업예약/작업예약재료 등록
					//-------------------------------------------------------------------------------------------------
			    	String szYD_PREP_SCH_ID = "";
			    	String szYD_SCH_PRIOR = "";
			    	
					for(int i = 1; i <= rsResult.size(); i++ ) {
						rsResult.absolute(i);
						recPara			= rsResult.getRecord();
						String szSSTL_NO		= commUtils.trim(recPara.getFieldString("SSTL_NO" ));				
						String szYD_GP			= szYD_SCH_CD.substring(0, 1);
						String szYD_BAY_GP		= szYD_SCH_CD.substring(1, 2);
						String szYD_AIM_YD_GP	= commUtils.trim(recPara.getFieldString("YD_AIM_YD_GP" ));
						String szYD_AIM_BAY_GP	= commUtils.trim(recPara.getFieldString("YD_AIM_BAY_GP" ));
						//String szYS_STK_COL_GP	= commUtils.trim(recPara.getFieldString("YS_STK_COL_GP" ));
						String szYS_STK_BED_NO	= commUtils.trim(recPara.getFieldString("YS_STK_BED_NO" ));
						String szYS_STK_LYR_NO	= commUtils.trim(recPara.getFieldString("YS_STK_LYR_NO" ));
						String szYS_STK_SEQ_NO	= commUtils.trim(recPara.getFieldString("YS_STK_SEQ_NO" ));
						
						if( i == 1 ) {
							szYD_PREP_SCH_ID	= commUtils.trim(recPara.getFieldString("YD_PREP_SCH_ID" ));
							//-------------------------------------------------------------------------------------------------
							//	스케줄코드 조회
							//-------------------------------------------------------------------------------------------------
					    	//스케줄코드로 스케줄기준Table조회
							szMsg="스케줄코드로 스케줄기준Table조회";
							commUtils.printLog(logId, szMsg, "SL");
							
							recInTemp = JDTORecordFactory.getInstance().create();
					    	recInTemp.setField("YD_SCH_CD", szYD_SCH_CD);
					    	
					    	//스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.
							szMsg="스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.";
							commUtils.printLog(logId, szMsg, "SL");
							
							// rsResult 지역변수가 For문 반복시마다  초기화 됨 변수명 변경 rsResult -> rsResult2(아래 참조하는 부분 포함) - wc
							JDTORecordSet rsResult2 = JDTORecordFactory.getInstance().createRecordSet("Temp");
					    	/*  com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule  */
					    	
							rsResult2 = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
					    	
					    	if (rsResult2 == null || rsResult2.size() <= 0) {
					    		szMsg = "["+methodNm+"] 스케줄 기준 조회 data not found";
								commUtils.printLog(logId, methodNm, "SL");
								throw new DAOException(szMsg);
							} else {
							
								//레코드 추출
								rsResult2.first();
								recPara = rsResult2.getRecord();
								szYD_SCH_PRIOR = commUtils.trim(recPara.getFieldString("YD_WRK_CRN_PRIOR"       ));
							
							}
							//-------------------------------------------------------------------------------------------------
							
							//-------------------------------------------------------------------------------------------------
							//	작업예약 등록
							//-------------------------------------------------------------------------------------------------
							
							//com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdWrkBook
							szYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
							
							if ("".equals(szYD_WBOOK_ID)) {
								commUtils.printLog(logId, "작업예약ID 생성 실패", "SL");
								throw new DAOException(szMsg);
							}
	
							//작업예약항목SETTING
							recOutPara = JDTORecordFactory.getInstance().create();
							
							recOutPara.setField("YD_WBOOK_ID", 			szYD_WBOOK_ID);
							recOutPara.setField("REGISTER", 			szREG_MOD_USER);
							recOutPara.setField("YD_GP", 				szYD_GP);
							recOutPara.setField("YD_BAY_GP", 			szYD_BAY_GP);
							recOutPara.setField("YD_SCH_CD", 			szYD_SCH_CD);
							recOutPara.setField("YD_SCH_PRIOR", 		szYD_SCH_PRIOR);
							recOutPara.setField("YD_AIM_YD_GP", 		szYD_AIM_YD_GP);
							recOutPara.setField("YD_AIM_BAY_GP", 		szYD_AIM_BAY_GP);
							recOutPara.setField("YD_CAR_USE_GP", 		"L");  //구내운송
							recOutPara.setField("TRN_EQP_CD", 			szTRN_EQP_CD);
							
							szMsg = "["+methodNm+"] 작업예약["+szYD_WBOOK_ID+"]등록 시작";
							commUtils.printLog(logId, szMsg, "SL");
	
	
							//com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook
							commDao.insert(recOutPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
							
							szMsg ="["+methodNm+"] 작업예약["+szYD_WBOOK_ID+"]등록 완료";
							commUtils.printLog(logId, szMsg, "SL");
							
							//-------------------------------------------------------------------------------------------------
						}
						
						//-------------------------------------------------------------------------------------------------
						//	작업예약재료 등록
						//-------------------------------------------------------------------------------------------------
						
						recOutPara = JDTORecordFactory.getInstance().create();
						
						recOutPara.setField("YD_WBOOK_ID", 				szYD_WBOOK_ID);
						recOutPara.setField("REGISTER", 				szREG_MOD_USER);
						recOutPara.setField("SSTL_NO", 					szSSTL_NO);
						recOutPara.setField("YS_STK_COL_GP", 			szYS_STK_COL_GP);
						recOutPara.setField("YS_STK_BED_NO", 			szYS_STK_BED_NO);
						recOutPara.setField("YS_STK_LYR_NO", 			szYS_STK_LYR_NO);
						recOutPara.setField("YS_STK_SEQ_NO", 			szYS_STK_SEQ_NO);
						recOutPara.setField("YD_UP_COLL_SEQ", 			String.valueOf(i));
					
						intRtnVal = commDao.insert(recOutPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
			    		if(intRtnVal <= 0) {
			    			commUtils.printLog(logId, "작업예약ID 생성 실패", "SL");
							throw new DAOException(szMsg);
			    		}
						
						
						szMsg = "["+methodNm+"] ["+i+"] 작업예약["+szYD_WBOOK_ID+"]의 작업재료["+szSSTL_NO+"] 등록 완료";
						commUtils.printLog(logId, szMsg, "SL");
						//-------------------------------------------------------------------------------------------------
					}
					
					//-------------------------------------------------------------------------------------------------
					//	준비스케줄 삭제
					//-------------------------------------------------------------------------------------------------
					
					szMsg = "["+methodNm+"]  준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 시작";
					commUtils.printLog(logId, szMsg, "SL");
					recOutPara = JDTORecordFactory.getInstance().create();
					
					recOutPara.setField("YD_PREP_SCH_ID", 	szYD_PREP_SCH_ID);
					recOutPara.setField("MODIFIER", 		szREG_MOD_USER);
					recOutPara.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
					recOutPara.setField("DEL_YN", 			"Y");
					
					/*com.inisteel.cim.ys.common.dao.YsCommDAO.delYdPrepmtlByPrepSchIdYN
					UPDATE USRYSA.TB_YS_PREPMTL
					   SET MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					     , DEL_YN = :V_DEL_YN
					WHERE YD_PREP_SCH_ID = REPLACE(:V_YD_PREP_SCH_ID,' ','')
					*/
					
					commDao.update(recOutPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.delYdPrepmtlByPrepSchIdYN", logId, methodNm, "준비작업 재료 삭제");
					
					// WC - 준비스케줄 삭제
					commDao.update(recOutPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.delYdPrepSch", logId, methodNm, "준비스케줄 삭제");
	
					szMsg = "["+methodNm+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 완료 - 메세지 : " ;
					commUtils.printLog(logId, szMsg, "SL");
					
	//				//-------------------------------------------------------------------------------------------------
	//				//	차량스케줄에 상차작업예약 등록
	//				//-------------------------------------------------------------------------------------------------
	//				recOutPara = JDTORecordFactory.getInstance().create();
	//				
	//				recOutPara.setField("YD_CAR_SCH_ID", 				szYD_CAR_SCH_ID);
	//				recOutPara.setField("YD_CARLD_WRK_BOOK_ID", 		szYD_WBOOK_ID);
	//				recOutPara.setField("MODIFIER", 					szREG_MOD_USER);
	//				
	//				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarSchLDWrkBook
	//				UPDATE USRYSA.TB_YS_CARSCH
	//				   SET MODIFIER = :V_MODIFIER
	//				     , MOD_DDTT = SYSDATE
	//				     , YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID
	//				WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	//				*/
	//				
	//		    	intRtnVal = commDao.update(recOutPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarSchLDWrkBook", logId, methodNm, "TB_YS_CARSCH 등록");
	//				if(intRtnVal <= 0) {
	//					szMsg="["+methodNm+"] 차량스케줄 업데이트 시 이상.";
	//					commUtils.printLog(logId, methodNm, "SL");
	//				}	
	    		}
    		}
			/**********************************************************
			* 1. 차량스케줄 갱신 (실적등록된 상차위치로)
			* 1.1 상차스케쥴 및 포인트
			**********************************************************/			
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_CAR_SCH_ID", 	   	szYD_CAR_SCH_ID);
	    	recInTemp.setField("YD_CARLD_WRK_BOOK_ID", 	szYD_WBOOK_ID);
	    	recInTemp.setField("YD_STK_COL_ACT_STAT",  	"L");                 //적치가능
	    	recInTemp.setField("YD_PNT_CD1",  			szARR_YD_PNT_CD);
	    	recInTemp.setField("YD_CAR_PROG_STAT",     	YsConstant.YD_CARLD_ARR);
	    	recInTemp.setField("YD_CARLD_STOP_LOC", 	szYS_STK_COL_GP);	 // 실제차량이 들어온 상차정지위치를 재등록한다.    	
	    	recInTemp.setField("YD_CARLD_ARR_DT", 		commUtils.getDateTime14());//상차도착
	    	recInTemp.setField("ARR_WLOC_CD",  			szARR_WLOC_CD);
	    	                           
	    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarSchCarWrkBook  
			UPDATE TB_YS_CARSCH
			   SET MODIFIER = :V_MODIFIER
			     , MOD_DDTT = SYSDATE
			     , YD_PNT_CD1 = :V_YD_PNT_CD1
			     , YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID
			     , YD_CARLD_STOP_LOC = :V_YD_CARLD_STOP_LOC
			     , YD_CARLD_ARR_DT = TO_DATE(:V_YD_CARLD_ARR_DT,'YYYYMMDDHH24MISS')
			     , YD_CAR_PROG_STAT = :V_YD_CAR_PROG_STAT
			     , ARR_WLOC_CD = :V_ARR_WLOC_CD  -- WC추가
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	    	*/
	    	
	    	intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarSchCarWrkBook", logId, methodNm, "TB_YS_CARSCH 등록");
			if(intRtnVal <= 0) {
				szMsg="["+methodNm+"] 차량스케줄 업데이트 시 존재하지 않습니다.";
				commUtils.printLog(logId, methodNm, "SL");
				throw new DAOException(szMsg);
			}	

			/**********************************************************
			* 1. 차량 도착열(COL) 등록
			* 1.1 해당장비로 CLEAR
			* 1.2 해당열 등록
			**********************************************************/			
			recInTemp = JDTORecordFactory.getInstance().create();			 
			recInTemp.setField("TRN_EQP_CD",     szTRN_EQP_CD);
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolTrnEqpCdToNull 
			UPDATE TB_YS_STKCOL
			   SET TRN_EQP_CD = NULL
			     , YD_CAR_USE_GP = NULL
			 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
			 */
			
			intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolTrnEqpCdToNull", logId, methodNm, "TB_YS_STKCOL 등록");
			if(intRtnVal < 0) {
				szMsg = "["+methodNm+"] 운송장비코드["+szTRN_EQP_CD+"] - 예약된 차량도착point 삭제 시 오류발생 - 반환값 : " + intRtnVal;
				commUtils.printLog(logId, szMsg, "SL");
			} else {			
				szMsg = "["+methodNm+"] 운송장비코드["+szTRN_EQP_CD+"] - 예약된 차량도착point 삭제 성공";
				commUtils.printLog(logId, szMsg, "SL");
			}
			
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
	    	recInTemp.setField("TRN_EQP_CD"			, szTRN_EQP_CD);
	    	recInTemp.setField("YD_STK_COL_ACT_STAT", "L");
	    	recInTemp.setField("YD_CAR_USE_GP"		, "L");			// WC 추가
			recInTemp.setField("MODIFIER"			, szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStat 
			UPDATE TB_YS_STKCOL
			   SET MOD_DDTT     = SYSDATE             
			   , MODIFIER     = :V_MODIFIER             
			   , YD_STK_COL_ACT_STAT = NVL(:V_YD_STK_COL_ACT_STAT,YD_STK_COL_ACT_STAT)
			   , TRN_EQP_CD   = NVL(:V_TRN_EQP_CD, TRN_EQP_CD)       
			   , CAR_NO       = NVL(:V_CAR_NO, CAR_NO)           
			   , CARD_NO      = NVL(:V_CARD_NO, CARD_NO)              
			     , YD_STKBED_USG_CD = NVL(:V_STKBED_USG_CD,YD_STKBED_USG_CD)
			     , YD_CAR_USE_GP = NVL(:V_YD_CAR_USE_GP,YD_CAR_USE_GP)
			WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
			*/ // Query 수정
			
	    	intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStat", logId, methodNm, "TB_YS_STKCOL 등록");
			if(intRtnVal <= 0) {
				szMsg="[" + methodNm + "] 적치열[" + szYS_STK_COL_GP + "]활성화중 ERROR 발생.";
				commUtils.printLog(logId, methodNm, "SL");
	    		sndRecord.setTaskCode("-1");
	    		return sndRecord;

			}
			/**********************************************************
			* 1. 차량포인트 등록
			* 1.1 해당장비로 CLEAR
			* 1.2 해당차량포인트 등록
			**********************************************************/			
			//설비코드로 초기화 
		    this.YsCarPointinforeg2("1","",szTRN_EQP_CD,"","","","C",logId,methodNm);
		    
		    //저장위치로 차량 포인트 예약
		    this.YsCarPointinforeg2("3","",szTRN_EQP_CD,szYS_STK_COL_GP,"","","L",logId,methodNm);
			
			/**********************************************************
			* 1. 적치베드 활성화
			**********************************************************/			
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
			recInTemp.setField("YD_STK_BED_ACT_STAT", "L");
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedYdStkColGp  

			UPDATE TB_YS_STKBED
			   SET MOD_DDTT     = SYSDATE             
				 , MODIFIER     = :V_MODIFIER             
				 , YD_STK_BED_ACT_STAT = NVL(:V_YD_STK_BED_ACT_STAT,YD_STK_BED_ACT_STAT)
			     , YD_STK_BED_WT_MAX = NVL(:V_YD_STK_BED_WT_MAX,YD_STK_BED_WT_MAX )
			  WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
			  */
			intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedYdStkColGp", logId, methodNm, "TB_YS_STKBED 등록");
			if(intRtnVal <= 0) {
				szMsg="[" + methodNm + "] 적치BED[" + szYS_STK_COL_GP + "]활성화중 ERROR 발생.";
				commUtils.printLog(logId, methodNm, "SL");
				throw new DAOException(szMsg);
			}
			/**********************************************************
			* 1. 적치단 활성화
			**********************************************************/			
	    	recInTemp  = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
	    	recInTemp.setField("SSTL_NO"			, "");
	    	recInTemp.setField("YD_STK_LYR_ACT_STAT", "E");
	    	recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
	    	        
	    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear  

			UPDATE TB_YS_STKLYR            
			   SET MOD_DDTT     = SYSDATE             
				 , MODIFIER     = :V_MODIFIER             
				 , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
			     ,      , SSTL_NO = null
			     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
			 WHERE YS_STK_COL_GP  = :V_YS_STK_COL_GP
			 
			 */
			intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "TB_YS_STKLYR 등록");
			
			if(intRtnVal <= 0) {
				szMsg="[" + methodNm + "] 적치단[" + szYS_STK_COL_GP + "]활성화중 ERROR 발생.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new DAOException(szMsg);

			}

			/**********************************************************
			* 1. 공차 도착 시 저장위치 제원 야드L2로 전송
			* 야드BLOOM:N1,BILLET:N2,선재:N3,봉강:N4,선재자동화:N5,봉강자동화:N6 	
			**********************************************************/			

			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setResultCode(logId);	//Log ID
	    	recInTemp.setResultMsg(methodNm);	//Log Method Name
	    	String szYdGp = szYS_STK_COL_GP.substring(0,2);
			String szJMS_TC_CD = "";
			
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
    		recInTemp.setField("MSG_ID"				, szJMS_TC_CD);
			recInTemp.setField("YD_INFO_SYNC_CD"	, "3");						//1:동,2:SPAN,3:열,4:BED
			recInTemp.setField("YD_GP"				, szYS_STK_COL_GP.substring(0, 1));
			recInTemp.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
			recInTemp.setField("YD_CAR_PROG_STAT"	, "2");
			recInTemp.setField("YD_EQP_WRK_STAT"	, "U");
			
			//전송 Data 생성
			sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2(szJMS_TC_CD, recInTemp));

			szMsg="["+methodNm+"] 공차도착 시 저장위치 제원 야드L2로 전송";
			commUtils.printLog(logId, szMsg, "SL");
			
			/**********************************************************
			* 1. 크레인 스케줄 호출 작업
			**********************************************************/			
			
			//작업예약이 존재하는 경우 - 
	    	if( !szYD_WBOOK_ID.equals("") ) {
		    	//작업예약을 조회한다.
				recInTemp = JDTORecordFactory.getInstance().create();
				
				recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
		    	//크레인 스케줄 호출
				sndRecord = this.procCallCrnSch(logId,recInTemp,sndRecord);

//	    		intRtnVal =   Integer.parseInt(sndRecord.getTaskCode());
//	    		
//	    		szMsg="["+methodNm+"]  -----> intRtnVal";
//	    		
//				if(intRtnVal == -1) {
//		    		return sndRecord;
//				}

				
				szMsg="["+methodNm+"] 크레인 스케줄 호출";
				commUtils.printLog(logId, szMsg, "SL");
	    	}
	    	/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
			commUtils.printParam(logId + "확인1 ", sndRecord);
			
		}catch(Exception e){
			
			szMsg="["+methodNm+"] Error:" +e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
	
		szMsg="["+methodNm+"] 메소드 완료";
		commUtils.printLog(logId, szMsg, "S-");
		sndRecord.setTaskCode("1");
 		return sndRecord;
	} //end of procLDMatlCarArr()
	
	
	/**
	 * 오퍼레이션명 : 소재차량 영차도착 실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procUDMatlCarArr(String logId, JDTORecord rcvMsg, JDTORecordSet rsWbookMtl, JDTORecord recCarSch, JDTORecord rcvMsgCol)throws JDTOException  {
		String methodNm = "소재차량 영차도착 실적[YsCommCarMvSeEJB.procUDMatlCarArr] < " + rcvMsg.getResultMsg();
		
		JDTORecordSet rsResult          = null;
		JDTORecord	  sndRecord			= JDTORecordFactory.getInstance().create();
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord	  recInPara			= null;
		
		
	    int intRtnVal 		   = 0 ;
	    
	    String szMsg           = "";
	    String szMethodName    = "procUDMatlCarArr";
	    String szOperationName = "소재차량 영차도착 실적";
	    
	    String szTRN_EQP_CD    = "";
	    String szARR_WLOC_CD   = "";
	    String szARR_YD_PNT_CD = "";
	    String szYS_STK_COL_GP = "";
	    String szYD_WBOOK_ID   = "";
	    String isUnMatched     = "";
	    String szYD_CAR_SCH_ID = "";
	    String szSSTL_NO        = "";
	    String szYD_STK_COL_ACT_STAT = "";
	    String szTRN_WRK_FULLVOID_GP= "";
	    try{
	    	//----------------------------------------------------------------------------------------------------------
	    	//	파라미터 값 확인
	    	//----------------------------------------------------------------------------------------------------------
	    	
	    	szARR_WLOC_CD   		= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 
	    	szARR_YD_PNT_CD 		= commUtils.trim(rcvMsg.getFieldString("ARR_YD_PNT_CD")); 
	    	szYD_WBOOK_ID   		= commUtils.trim(rcvMsg.getFieldString("YD_CARUD_WRK_BOOK_ID")); 
	    	isUnMatched    			= commUtils.trim(rcvMsg.getFieldString("IS_UNMATCHED")); 
	    	szTRN_EQP_CD   			= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"));
	    	
	    	szMsg="["+szOperationName+"] 수신정보 : 착지개소코드["+szARR_WLOC_CD+"],포인트["+szARR_YD_PNT_CD+"],상차작업예약ID["+szYD_WBOOK_ID+"],차량번호["+szTRN_EQP_CD+"]";
			commUtils.printLog(logId, szMsg, "SL");
	    	
	    	//수신된 차량 정지위치(COL) 정보
	    	szYS_STK_COL_GP  		= commUtils.trim(rcvMsgCol.getFieldString("YS_STK_COL_GP")); 
	    	szYD_STK_COL_ACT_STAT	= commUtils.trim(rcvMsgCol.getFieldString("YD_STK_COL_ACT_STAT")); 
	    	
	    	//차량 SCH 정보
	    	szYD_CAR_SCH_ID   		= commUtils.trim(recCarSch.getFieldString("YD_CAR_SCH_ID")); 
	    	
	    	szTRN_WRK_FULLVOID_GP   = "F";
	    	
	    	szMsg="["+szOperationName+"] COL 정보 : 적치열["+szYS_STK_COL_GP+"],적치상태["+szYD_STK_COL_ACT_STAT+"]";
			commUtils.printLog(logId, szMsg, "SL");
 
	    	
	    	szMsg="[" + szOperationName + "] 운송장비코드["+szTRN_EQP_CD+"]로 차량스케줄을 조회 완료 - 차량스케줄ID["+szYD_CAR_SCH_ID+"]";
	    	commUtils.printLog(logId, szMsg, "SL");
			
	    	
			//----------------------------------------------------------------------------------------------------------
			//	하차 작업예약 생성(하차동이 변경된 경우 )
			//----------------------------------------------------------------------------------------------------------
	    	if("Y".equals(isUnMatched) && szTRN_WRK_FULLVOID_GP.equals("F")){ 
	    		//영차이고 하차동이 변경될 경우 작업예약 스케쥴코드만 변경해준다.
	    		
	    		recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("MODIFIER"		, "YJK");
		    	recInTemp.setField("YD_BAY_GP"		, szYS_STK_COL_GP.substring(1 , 2));//실제 차량이 들어온 하차정지위치를 재등록한다.
		    	recInTemp.setField("YD_SCH_CD"		, szYS_STK_COL_GP.substring(0 , 2)+"TR02LM");
		    	recInTemp.setField("YD_WBOOK_ID"	, szYD_WBOOK_ID);
		    	
		    	intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdWrkbook", logId, methodNm, "TB_YS_WBOOK 수정");
	    		
	    	}else if("".equals(szYD_WBOOK_ID) && szTRN_WRK_FULLVOID_GP.equals("F")){ 
		    	
		    	recInPara = JDTORecordFactory.getInstance().create();
		    	recInPara.setResultCode(logId);	//Log ID
		    	recInPara.setResultMsg(methodNm);	//Log Method Name
		    	recInPara.setField("YD_CAR_SCH_ID",            szYD_CAR_SCH_ID);
		    	recInPara.setField("TRN_EQP_CD",               szTRN_EQP_CD);
		    	recInPara.setField("WLOC_CD",                  szARR_WLOC_CD);
		    	
//				//트렌젝션 분리 작업 
	    		EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
	    		ejbConn.trx("procInsWrkBookCarUd", new Class[] { String.class,JDTORecord.class }, new Object[] { logId, recInPara });
	    		
	    		rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    		
	    		/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch */
	    		rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch", logId, methodNm, "차량스케줄조회"); 
		    	
		    	if (rsResult == null || rsResult.size() <= 0) {
					szMsg="[" + szOperationName + "] 차량스케줄조회 중 오류 ["+intRtnVal+"]" ;
					commUtils.printLog(logId, szMsg, "SL");	
		    		sndRecord.setTaskCode("0");
		    		return sndRecord;
				}
				
				rsResult.absolute(1);
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsResult.getRecord());
		    	
		    	//작업예약
		    	szYD_WBOOK_ID   	= commUtils.trim(recOutTemp.getFieldString("YD_CARUD_WRK_BOOK_ID")); 
 			}

			/**********************************************************
			* 1. 차량스케줄 갱신 (실적등록된  하차위치로)
			* 1.1 차량스케줄에 하차작업예약id, 차량진행상태(하차도착) 등록
			**********************************************************/				
	    	szMsg="[" + szOperationName + "] 차량스케줄["+szYD_CAR_SCH_ID+"]에 하차작업예약["+szYD_WBOOK_ID+"], 착지개소코드["+szARR_WLOC_CD+"], 차량진행상태[B], 하차정지위치["+szYS_STK_COL_GP+"] 등록 시작";
	    	commUtils.printLog(logId, szMsg, "SL");	
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_CAR_SCH_ID"			, szYD_CAR_SCH_ID);
	    	recInTemp.setField("YD_CARUD_WRK_BOOK_ID"	, szYD_WBOOK_ID);
	    	recInTemp.setField("ARR_WLOC_CD"			, szARR_WLOC_CD);
	    	recInTemp.setField("YD_STK_COL_ACT_STAT"	, szYD_STK_COL_ACT_STAT);
	    	recInTemp.setField("YD_CAR_PROG_STAT"		, "B");
	    	recInTemp.setField("YD_CARUD_STOP_LOC"		, szYS_STK_COL_GP);//실제 차량이 들어온 하차정지위치를 재등록한다.
	    	recInTemp.setField("YD_CARUD_ARR_DT"		, commUtils.getDateTime14());//하차도착
	    	
	    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarSchCarWrkUD  
	    	UPDATE TB_YS_CARSCH
	    	   SET MODIFIER = :V_MODIFIER
	    	     , MOD_DDTT = SYSDATE
	    	     , YD_CARUD_WRK_BOOK_ID = :V_YD_CARUD_WRK_BOOK_ID
	    	     , YD_CARUD_LEV_DT = TO_DATE(:V_YD_CARUD_LEV_DT,'YYYYMMDDHH24MISS')
	    	     , YD_CAR_PROG_STAT = :V_YD_CAR_PROG_STAT
	    	     , ARR_WLOC_CD      = NVL(:V_ARR_WLOC_CD,ARR_WLOC_CD)
	    	     , YD_STK_COL_ACT_STAT = NVL(:V_YD_STK_COL_ACT_STAT,YD_STK_COL_ACT_STAT)
	    	     , YD_CARUD_STOP_LOC   = NVL(:V_YD_CARUD_STOP_LOC,YD_CARUD_STOP_LOC)
	    	 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	    	*/ 
	    	intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarSchCarWrkUD", logId, methodNm, "TB_YS_CARSCH 등록");
			if(intRtnVal <= 0) {
				szMsg="["+szOperationName+"] 차량스케줄 업데이트 시 존재하지 않습니다.";
				commUtils.printLog(logId, methodNm, "SL");
	    		sndRecord.setTaskCode("-1");
	    		return sndRecord;

			}	
			/**********************************************************
			* 1.작업예약재료에 적치열정보를 등록한다
			* (하차작업예약생성시에는 정지위치를 알수없기에 도착처리시에 등록.)
			**********************************************************/				

			szMsg="[" + szOperationName + "] 하차작업예약["+szYD_WBOOK_ID+"]의 작업재료에 차량도착위치["+szYS_STK_COL_GP+"]를 등록 시작";
			commUtils.printLog(logId, szMsg, "SL");	
			
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp.setField("YD_WBOOK_ID", 	   szYD_WBOOK_ID);
	    	
	    	 /* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdWrkbookmtlId 
				SELECT YD_WBOOK_ID  AS YD_WBOOK_ID
				      ,SSTL_NO  AS SSTL_NO
				      ,REGISTER  AS REGISTER
				      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
				      ,MODIFIER  AS MODIFIER
				      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
				      ,DEL_YN  AS DEL_YN
				      ,YS_STK_COL_GP  AS YS_STK_COL_GP
				      ,YS_STK_BED_NO  AS YS_STK_BED_NO
				      ,YS_STK_LYR_NO  AS YS_STK_LYR_NO
				      ,YS_STK_SEQ_NO  AS YS_STK_SEQ_NO
				--      ,YD_UP_COLL_SEQ  AS YD_UP_COLL_SEQ
				   FROM TB_YS_WRKBOOKMTL A
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND DEL_YN='N'
	    	 */
	    	rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdWrkbookmtlId", logId, methodNm, "작업예약상세 조회"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
	    		szMsg="작업예약 id로 작업예약 조회 중 Error code : " + intRtnVal;
				commUtils.printLog(logId, szMsg, "SL");	
			}
	    	
	    	for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
	    		rsResult.absolute(Loop_i);
				
	    		recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsResult.getRecord());

		    	szSSTL_NO = commUtils.trim(recOutTemp.getFieldString("SSTL_NO")); 
		    	
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_WBOOK_ID"	, szYD_WBOOK_ID);
		    	recInTemp.setField("SSTL_NO"		, szSSTL_NO);
		    	recInTemp.setField("YS_STK_COL_GP"	, szYS_STK_COL_GP);
		    	recInTemp.setField("YS_STK_BED_NO"	, recOutTemp.getFieldString("YS_STK_BED_NO")); // WC
		    	recInTemp.setField("YS_STK_LYR_NO"	, recOutTemp.getFieldString("YS_STK_LYR_NO")); // WC
		    	recInTemp.setField("YS_STK_SEQ_NO"	, recOutTemp.getFieldString("YS_STK_SEQ_NO")); // WC
		    	
		    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdWrkbookmtl 
		    	UPDATE TB_YS_WRKBOOKMTL
				  SET MODIFIER = :V_MODIFIER
				    , MOD_DDTT = SYSDATE
				    , YS_STK_COL_GP = :V_YS_STK_COL_GP
				    , YS_STK_BED_NO = NVL(:V_YS_STK_BED_NO,YS_STK_BED_NO)
				    , YS_STK_LYR_NO = NVL(:V_YS_STK_LYR_NO,YS_STK_LYR_NO)
				    , YS_STK_SEQ_NO = NVL(:V_YS_STK_SEQ_NO,YS_STK_SEQ_NO)
				  WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				    AND SSTL_NO = :V_SSTL_NO
		    	*/    
		    	intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdWrkbookmtl", logId, methodNm, "TB_YS_WRKBOOKMTL 등록");
				if(intRtnVal <= 0) {
					szMsg="["+szOperationName+"] 작업 예약 등록 시 ERROR 발생.";
					commUtils.printLog(logId, methodNm, "SL");
		    		sndRecord.setTaskCode("-1");
		    		return sndRecord;
				}	
				szMsg="[" + szOperationName + "] 하차작업예약["+szYD_WBOOK_ID+"]의 작업재료["+szSSTL_NO+"]에 차량도착위치["+szYS_STK_COL_GP+"]를 등록 성공";
				commUtils.printLog(logId, szMsg, "SL");
				
				
			}
	    	
	    	szMsg="[" + szOperationName + "] 하차작업예약["+szYD_WBOOK_ID+"]의 작업재료에 차량도착위치["+szYS_STK_COL_GP+"]를 등록 완료";
	    	commUtils.printLog(logId, szMsg, "SL");
			
	    	//////////////////////  왜 ? 이게 있는지 모름 //////////////////////////////////////////////////////////////////
	    	
//	    	//실제 차량이 도착한 위치로 스케줄 코드로 새로 만들어서 등록한다. (생성때 등록한 스케줄코드와 틀릴 수 있기때문에...)
//	    	recInTemp = JDTORecordFactory.getInstance().create();
//	    	recInTemp.setField("YD_WBOOK_ID", 	   szYD_WBOOK_ID);
//	    	
//	    	// 스케줄코드 - WC
//	        if (szYS_STK_COL_GP.substring(0,1).equals("C")||szYS_STK_COL_GP.substring(0,1).equals("B")){
//	        	recInTemp.setField("YD_SCH_CD",	szYS_STK_COL_GP.substring(0, 2)+"TR02LM");         // WC 수정
//	        } else if (szYS_STK_COL_GP.substring(0,1).equals("K")) {
//	        	recInTemp.setField("YD_SCH_CD",	szYS_STK_COL_GP.substring(0, 2)+"TR20LM");         // WC 수정  
//	        }
//			recInTemp.setField("YD_BAY_GP", 	szYS_STK_COL_GP.substring(1, 2));//실제 도착동으로 작업예약셋팅
//			
//			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdWrkbook  
//			UPDATE TB_YS_WRKBOOK
//			   SET MODIFIER = :V_MODIFIER
//			     , MOD_DDTT = SYSDATE
//			     , YD_BAY_GP = :V_YD_BAY_GP
//			     , YD_SCH_CD = :V_YD_SCH_CD
//			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
//			*/ 
//			 
//			intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdWrkbook", logId, methodNm, "TB_YS_WRKBOOK 등록");
//			if(intRtnVal <= 0) {
//				szMsg="작업예약에 스케줄코드 등록 중 Error code : " + intRtnVal;
//				commUtils.printLog(logId, methodNm, "SL");
//	    		sndRecord.setTaskCode("-1");
//	    		return sndRecord;
//			}	
			
			/**********************************************************
			* 1. 차량 도착열(COL) 등록
			* 1.1 해당장비로 CLEAR
			* 1.2 해당열 등록
			**********************************************************/	
			recInTemp = JDTORecordFactory.getInstance().create();			 
			recInTemp.setField("TRN_EQP_CD",     szTRN_EQP_CD);
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolTrnEqpCdToNull 
			UPDATE TB_YS_STKCOL
			   SET TRN_EQP_CD = NULL
			     , YD_CAR_USE_GP = NULL
			 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
			 */
			intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolTrnEqpCdToNull", logId, methodNm, "TB_YS_STKCOL 등록");
			if(intRtnVal <= 0) {
				szMsg = "["+methodNm+"] 운송장비코드["+szTRN_EQP_CD+"] - 예약된 차량도착point 삭제 시 오류발생 - 반환값 : " + intRtnVal;
				commUtils.printLog(logId, methodNm, "SL");
			} else {			
				szMsg = "["+szOperationName+"] 운송장비코드["+szTRN_EQP_CD+"] - 예약된 차량도착point 삭제 성공";
				commUtils.printLog(logId, szMsg, "SL");
			}	    	
			
			//----------------------------------------------------------------------------------------------------------
	    	//하차정지위치 Map Open(적치열, 적치베드, 적치단 활성화)
			//----------------------------------------------------------------------------------------------------------
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YS_STK_COL_GP",        szYS_STK_COL_GP);
	    	recInTemp.setField("TRN_EQP_CD",           szTRN_EQP_CD);
	    	recInTemp.setField("YD_STK_COL_ACT_STAT",  "L");
	    	recInTemp.setField("YD_CAR_USE_GP",        "L");
			recInTemp.setField("MODIFIER", 		szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
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
	    	intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStat", logId, methodNm, "TB_YS_STKCOL 등록");
			if(intRtnVal <= 0) {
				szMsg="[" + methodNm + "] 적치열[" + szYS_STK_COL_GP + "]활성화중 ERROR 발생.";
				commUtils.printLog(logId, methodNm, "SL");
	    		sndRecord.setTaskCode("-1");
	    		return sndRecord;
			}
			
			//----------------------------------------------------------------------------------------------------------
			/**********************************************************
			* 1. 차량포인트 등록
			**********************************************************/	
			
			//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
		    this.YsCarPointinforeg2("1","",szTRN_EQP_CD,"","","","C",logId,methodNm);
		    
			//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
		    this.YsCarPointinforeg2("3","",szTRN_EQP_CD,szYS_STK_COL_GP,"","","L",logId,methodNm);
			
			/**********************************************************
			* 1. 적치베드 활성화
			**********************************************************/	
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YS_STK_COL_GP", szYS_STK_COL_GP);
			recInTemp.setField("YD_STK_BED_ACT_STAT", "L");
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedYdStkColGp  

			UPDATE TB_YS_STKBED
			   SET MOD_DDTT     = SYSDATE             
				 , MODIFIER     = :V_MODIFIER             
				 , YD_STK_BED_ACT_STAT = NVL(:V_YD_STK_BED_ACT_STAT,YD_STK_BED_ACT_STAT)
			     , YD_STK_BED_WT_MAX = NVL(:V_YD_STK_BED_WT_MAX,YD_STK_BED_WT_MAX )
			  WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
			  */
			intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedYdStkColGp", logId, methodNm, "TB_YS_STKBED 등록");
			if(intRtnVal <= 0) {
				szMsg="[" + methodNm + "] 적치BED[" + szYS_STK_COL_GP + "]활성화중 ERROR 발생.";
				commUtils.printLog(logId, methodNm, "SL");
				throw new DAOException(szMsg);
			}
			
			//----------------------------------------------------------------------------------------------------------
//			/////////////////////////SJH /////////////////////////////
//			//출발시 생성된 작업예약이 삭제된 경우 신규 작업예약에서 재료정보를 호출 함
//			if(rsWbookMtl.size()==0){
//				//작업예약을 조회한다. (차량사용구분과 운송장비코드로 작업예약을 조회한다.)
//				rsWbookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
//		    	recInTemp = JDTORecordFactory.getInstance().create();
//		    	recInTemp.setField("TRN_EQP_CD",    szTRN_EQP_CD);
//		    	/* COM.INISTEEL.CIM.YS.COMMON.DAO.YSCOMMDAO.GETWORKBOOKMTLBYCARUSRGPTRNEQPCD 
//		    	SELECT B.YD_WBOOK_ID    AS YD_WBOOK_ID
//		    	      ,B.YD_SCH_CD
//		    	      ,A.SSTL_NO         AS SSTL_NO
//		    	--      ,A.YD_UP_COLL_SEQ AS YD_UP_COLL_SEQ
//		    	      ,A.YS_STK_COL_GP  AS YS_STK_COL_GP
//		    	      ,A.YS_STK_BED_NO  AS YS_STK_BED_NO
//		    	      ,A.YS_STK_LYR_NO  AS YS_STK_LYR_NO
//		    	      ,A.YS_STK_SEQ_NO AS YS_STK_SEQ_NO
//		    	  FROM TB_YS_WRKBOOKMTL A
//		    	      ,(SELECT *
//		    	          FROM (SELECT YD_WBOOK_ID
//		    	                     , YD_SCH_CD
//		    	                  FROM TB_YS_WRKBOOK
//		    	                 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
//		    	                     AND DEL_YN='N'
//		    	                 ORDER BY YD_WBOOK_ID DESC
//		    	                ) C
//		    	          WHERE ROWNUM<=1
//		    	        ) B
//		    	 WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
//		    	 ORDER BY YS_STK_COL_GP,YS_STK_BED_NO,YS_STK_LYR_NO
//		    	*/ 
//		    	rsWbookMtl = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookMtlbyCarUsrGpTrnEqpCd", logId, methodNm, "작업예약을 조회"); 
//				
//		    	if (rsWbookMtl == null || rsWbookMtl.size() < 0 ) {
//					szMsg="["+methodNm+"] 운송장비코드["+szTRN_EQP_CD+"]로 작업예약 조회 시 : parameter error";
//					commUtils.printLog(logId, szMsg, "SL");
//					throw new Exception(szMsg);
//				} else if(intRtnVal == 0) {
//					szMsg="["+methodNm+"] 작업예약 정보에 하차재료 정보가 존재 안함";
//					commUtils.printLog(logId, szMsg, "SL");
//				}	
//			}
//			
			/**********************************************************
			* 1. 적치단 활성화
			**********************************************************/	
			for(int Loop_i = 1; Loop_i <= rsWbookMtl.size(); Loop_i++) {
				rsWbookMtl.absolute(Loop_i);
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsWbookMtl.getRecord());
		    	
		    	szSSTL_NO = commUtils.trim(recOutTemp.getFieldString("SSTL_NO"       ));
		    	
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YS_STK_COL_GP", szYS_STK_COL_GP);
		    	recInTemp.setField("SSTL_NO",        szSSTL_NO);
		    	recInTemp.setField("YD_STK_LYR_MTL_STAT", "C");		    	
		    	recInTemp.setField("YD_STK_LYR_ACT_STAT", "E");		    	

		    	
		    	// WC 추가
		    	recInTemp.setField("YS_STK_BED_NO", recOutTemp.getFieldString("YS_STK_BED_NO")); // WC
		    	recInTemp.setField("YS_STK_LYR_NO", recOutTemp.getFieldString("YS_STK_LYR_NO")); // WC
		    	recInTemp.setField("YS_STK_SEQ_NO", recOutTemp.getFieldString("YS_STK_SEQ_NO")); // WC
		    	
		    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGp2

				UPDATE TB_YS_STKLYR            
				   SET MOD_DDTT     = SYSDATE             
					 , MODIFIER     = :V_MODIFIER             
					 , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
				     , SSTL_NO = :V_SSTL_NO
				     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
				 WHERE YS_STK_COL_GP  = :V_YS_STK_COL_GP
				 AND YS_STK_BED_NO = :V_YS_STK_BED_NO
				 AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO
				 AND YS_STK_SEQ_NO = :V_YS_STK_SEQ_NO
				 */
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGp2", logId, methodNm, "TB_YS_STKLYR 등록");
				
				if(intRtnVal <= 0) {
					szMsg="[" + methodNm + "] 적치단[" + szYS_STK_COL_GP + "]활성화중 ERROR 발생.";
					commUtils.printLog(logId, szMsg, "SL");
		    		sndRecord.setTaskCode("-1");
		    		return sndRecord;
				}
			}
			//----------------------------------------------------------------------------------------------------------
			
			
			/**********************************************************
			* 1. 영차 도착 시 저장위치 제원 야드L2로 전송
			* 야드BLOOM:N1,BILLET:N2,선재:N3,봉강:N4,선재자동화:N5,봉강자동화:N6 	
			**********************************************************/	
			
			String szYdGp =  szYS_STK_COL_GP.substring(0, 2);
			String szJMS_TC_CD1  = "";
			String szJMS_TC_CD2  = "";
			
			if(szYdGp.startsWith("B") ){
				szJMS_TC_CD1 =  "YSN1L001";
				szJMS_TC_CD2 =  "YSN1L002";
	    	}else if(szYdGp.startsWith("C")){
				szJMS_TC_CD1 =  "YSN2L001";
				szJMS_TC_CD2 =  "YSN2L002";
	    	}else if(szYdGp.startsWith("KA")){
				szJMS_TC_CD1 =  "YSN6L001";
				szJMS_TC_CD2 =  "YSN6L002";
          	}else if(szYdGp.startsWith("KB")){
				szJMS_TC_CD1 =  "YSN4L001";
				szJMS_TC_CD2 =  "YSN4L002";
	    	}else if(szYdGp.startsWith("KD")){
				szJMS_TC_CD1 =  "YSN5L001";
				szJMS_TC_CD2 =  "YSN5L002";
	    	}else if(szYdGp.startsWith("KE")){
				szJMS_TC_CD1 =  "YSN3L001";
				szJMS_TC_CD2 =  "YSN3L002";
	    	}	
				
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setResultCode(logId);	//Log ID
			recInTemp.setResultMsg(methodNm);	//Log Method Name
			recInTemp.setField("MSG_ID"				, szJMS_TC_CD1);
			recInTemp.setField("YD_INFO_SYNC_CD"	, "3");
			recInTemp.setField("YD_GP"				, szYS_STK_COL_GP.substring(0, 1));
			recInTemp.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
			recInTemp.setField("YD_CAR_PROG_STAT"	, "B");
			recInTemp.setField("YD_EQP_WRK_STAT"	, "L");

			//전송 Data 생성
			sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2(szJMS_TC_CD1, recInTemp));

			szMsg="["+szOperationName+"] 영차도착 시 저장위치 제원 야드L2로 전송";
			commUtils.printLog(logId, szMsg, "SL");
			
			/**********************************************************
			* 1. 영차 도착 시 저장품 제원 야드L2로 전송
			* 야드BLOOM:N1,BILLET:N2,선재:N3,봉강:N4,선재자동화:N5,봉강자동화:N6 	
			**********************************************************/	
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setResultCode(logId);	//Log ID
			recInTemp.setResultMsg(methodNm);	//Log Method Name
			recInTemp.setField("MSG_ID"			, szJMS_TC_CD2);
			recInTemp.setField("YD_INFO_SYNC_CD", "1");
			recInTemp.setField("YS_STK_COL_GP"	, szYS_STK_COL_GP);
			recInTemp.setField("YS_STK_BED_NO"	, "");

			//전송 Data 생성
			sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2(szJMS_TC_CD2, recInTemp));

			szMsg="["+szOperationName+"] 영차도착 시 저장품제원 제원 야드L2로 전송";
			commUtils.printLog(logId, szMsg, "SL");

			/**********************************************************
			* 1. 크레인 스케줄 호출 작업
			**********************************************************/	
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			
			sndRecord = this.procCallCrnSch(logId,recInTemp,sndRecord);
			
			intRtnVal = Integer.parseInt(sndRecord.getTaskCode());
    		
			if(intRtnVal == -1) {
	    		return sndRecord;
	    	}
			//----------------------------------------------------------------------------------------------------------
			
		}catch(Exception e){
			
			szMsg="소재차량 영차도착 처리 Error:" +e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
    		sndRecord.setTaskCode("-1");
    		return sndRecord;
		}
	
		szMsg="소재차량 영차도착 처리 ("+szMethodName+") 완료";
		commUtils.printLog(logId, szMsg, "SL");
		sndRecord.setTaskCode("1");
		return sndRecord;
		
	} //end of procUDMatlCarArr()

	/**
	 * 오퍼레이션명 : 크레인스케줄호출
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public JDTORecord procCallCrnSch(String logId,  JDTORecord rcvMsg, JDTORecord sndRecord)throws JDTOException  {
		String methodNm = "크레인스케줄호출[YsCommCarMvSeEJB.procCallCrnSch] < " + rcvMsg.getResultMsg();
		
		JDTORecordSet rsResult          = null;
		JDTORecord    recInTemp         = null;
		
	    String szMsg              		= "";
//	    String szMethodName       		= "procCallCrnSch";
	    String szWbookId                = "";
	    String szSchCd                  = "";
	    String szEqpId                  = "";
	    
	    try{
	    	commUtils.printLog(logId, methodNm, "S+");
	    	//작업예약ID
	    	szWbookId  = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"       ));
	    	
	    	//작업예약 id로 작업예약Table를 조회한다.
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");

	    	rsResult = commDao.select(rcvMsg, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdWrkbook", logId, methodNm, "작업예약 조회"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
				szMsg = "["+methodNm+"] getYdWrkbook data not found";
	    		commUtils.printLog(logId, szMsg, "SL");
	    		sndRecord.setTaskCode("-1");
	    		return sndRecord;
			}
	    	
	    	rsResult.absolute(1);
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setRecord(rsResult.getRecord());
	    	
	    	//스케줄코드
	    	szSchCd   	= commUtils.trim(recInTemp.getFieldString("YD_SCH_CD"       ));
	   
	    	//스케줄기준Table조회
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	   	
	    	/*  com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule
	    	SELECT A.YD_GP
	    	      ,A.YD_BAY_GP
	    	      ,YD_SCH_CD
	    	      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN1
	    	            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN2
	    	        END AS YD_WRK_CRN
	    	      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN_PRIOR1
	    	            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN_PRIOR2
	    	        END AS YD_WRK_CRN_PRIOR
	    	      ,YD_SCH_CD_NM
	    	      ,YD_SCH_CONTENTS
	    	      ,YD_SCH_PROH_EXN 
	    	    FROM TB_YS_SCHRULE A
	    	        ,(
	    	            SELECT YD_GP
	    	                  ,YD_BAY_GP
	    	                  ,YD_SCH_GP
	    	                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 1, YD_EQP_STAT, '') ELSE 'O' END), 'X') AS STAT1 
	    	                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 2, YD_EQP_STAT, '') ELSE 'X' END), 'X') AS STAT2 
	    	            FROM   (
	    	                        SELECT YD_EQP_ID
	    	                              ,YD_GP
	    	                              ,YD_BAY_GP
	    	                              ,DECODE(YD_EQP_STAT,'B','C','O') AS YD_EQP_STAT
	    	                              ,SUBSTR(YD_EQP_NO,-1) AS CRN_NO 
	    	                              ,YD_EQP_GP AS YD_SCH_GP
	    	                        FROM   TB_YS_EQP
	    	                        WHERE  YD_EQP_GP IN ('CR','SC')
	    	                   )
	    	            GROUP BY YD_GP, YD_BAY_GP , YD_SCH_GP             
	    	         ) B
	    	    WHERE A.YD_SCH_CD LIKE :V_YD_SCH_CD || '%'
	    	    AND   A.YD_DATA_GP = 'M'
	    	    AND   A.YD_SCH_GP = B.YD_SCH_GP
	    	    AND   A.YD_GP = B.YD_GP
	    	    AND   A.YD_BAY_GP = B.YD_BAY_GP
	    	    AND   A.YD_CRN_STAT1 = B.STAT1
	    	    AND   A.YD_CRN_STAT2 = B.STAT2
	    	    ORDER BY A.YD_GP, A.YD_BAY_GP, A.YD_SCH_CD
	    	 */
	    	
	    	rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule", logId, methodNm, "스케줄기준 조회"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
				szMsg = "["+methodNm+"] getYdSchrule data not found";
	    		commUtils.printLog(logId, szMsg, "SL");
	    		sndRecord.setTaskCode("-1");
	    		return sndRecord;
			}
	    	
	    	rsResult.absolute(1);
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setRecord(rsResult.getRecord());
	    	recInTemp.setResultCode(logId);	//Log ID
	    	recInTemp.setResultMsg(methodNm);	//Log Method Name
	    	
	    	//크레인설비ID
	    	szEqpId   	= commUtils.trim(recInTemp.getFieldString("YD_WRK_CRN"       ));
	    	
	    	//크레인스케줄MAIN호출 TC :/** 1:블름,2:빌렛,3:봉강,4:선재	 */
	    	String szJMS_TC_CD = "";
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	String szYdGpBay = szSchCd.substring(0, 2);

	    	if(szYdGpBay.startsWith("B") ){	    		
	    		szJMS_TC_CD = "YSYSJ102";
	    	}else if(szYdGpBay.startsWith("C")){
	    		szJMS_TC_CD = "YSYSJ202";
	    	}else if(szYdGpBay.startsWith("KA")||szYdGpBay.startsWith("KB")){
	    		szJMS_TC_CD = "YSYSJ302";
	    	}else if(szYdGpBay.startsWith("KD")||szYdGpBay.startsWith("KE")){
	    		szJMS_TC_CD = "YSYSJ402";
	    	}
	    	
	    	/*
	    	 * 차량도착 시 크레인스케줄메인을 호출할 때 작업예약ID를 명시적을 전달처리한다.
	    	 */
	    	
	    	recInTemp.setField("JMS_TC_CD"	, szJMS_TC_CD);
	    	recInTemp.setField("JMS_TC_CREATE_DDTT"	,  commUtils.getDateTime14());
	    	recInTemp.setField("YD_SCH_CD"	, szSchCd);
	    	recInTemp.setField("YD_EQP_ID"	, szEqpId);
	    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
	    	
	    	//차량 하차작업 여부 
	    	if(szSchCd.substring(2, 4).equals("PT") && szSchCd.substring(6).equals("LM")) {
	    		recInTemp.setField("CRN_SCH_INS_TYPE", "U");
	    	}
	    	if("CATR02LM".equals(szSchCd)||"CBTR02LM".equals(szSchCd)){
	    		//빌렛 이송하차인 경우 스케쥴 호출하지 않는다.
	    	}else{
				//내부 전송 Data 생성
				sndRecord = commUtils.addSndData(sndRecord, recInTemp);
	    	}

 		}catch(Exception e){
	
			szMsg="크레인스케줄 호출 Error:" +e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
    		sndRecord.setTaskCode("-1");
    		return sndRecord;
		}
	
 		commUtils.printLog(logId, methodNm, "S-");

		sndRecord.setTaskCode("1");
		return sndRecord;
	} //end of C3CallCrnSch()
	
	
	
	/**
	 * 오퍼레이션명 : 소재차량 하차작업예약생성( makeCarUdWrkBook )
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public int procInsWrkBookCarUd(String logId, JDTORecord rcvMsg)throws JDTOException  {
		String methodNm = "소재차량 하차작업예약생성[YsCommCarMvSeEJB.procInsWrkBookCarUd] < " + rcvMsg.getResultMsg();
		
		JDTORecordSet rsResult          = null;
		JDTORecordSet rsCarBookMtl      = null;
		JDTORecord    recPara           = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
	    int     intRtnVal 	        	= 0 ;
	    
	    String szMsg           = "";
	    String szMethodName    = "procInsWrkBookCarUd";
	    String szOperationName = "소재차량하차작업예약생성";
	    String szYD_CAR_SCH_ID = "";
	    String szYD_WBOOK_ID   = "";
	    String szYD_GP         = "";
	    String szREGISTER      = "SYSTEM";
	    String szYD_BAY_GP     = "";
	    String szCUR_SCH_CD    = "";
	    String szSCH_PRIOR     = "";
	    String szTRN_EQP_CD    = "";
	    String szYD_CAR_USE_GP = "";
	    String szARR_WLOC_CD   = "";
	    
	    try{
	    	commUtils.printLog(logId, methodNm, "S+");
			szMsg="["+szOperationName+"] 하차 작업예약 생성 START!!";
			commUtils.printLog(logId, szMsg, "SL");
	    	
	    	
	    	szYD_CAR_SCH_ID	= commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"));
	    	szTRN_EQP_CD   	= commUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD"));
	    	szARR_WLOC_CD   = commUtils.trim(rcvMsg.getFieldString("WLOC_CD"));
	    	
	    	//하차지 개소로  TO 행선 결정
    		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	
	    	recInTemp.setField("YS_STK_COL_GP"	, "" );
	    	recInTemp.setField("ARR_WLOC_CD"	, szARR_WLOC_CD);
	    	recInTemp.setField("YD_CAR_SCH_ID"	, szYD_CAR_SCH_ID);
	    	
	    	String sQuery = "";
	    	
	    	if("S5Y10".equals(szARR_WLOC_CD)){//빌렛소재야드 포인트
	    		sQuery = "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolColGpLike3";
	    	}else{
	    		sQuery = "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolColGpLike2";
	    	}
	    	
	    	rsResult = commDao.select(recInTemp, sQuery, logId, methodNm, "적치열 조회"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
				szMsg="["+methodNm+"] 하차지 개소 이상 발생 data not found";
				throw new Exception(szMsg);
			}
	    		    	
	    	rsResult.absolute(1);
    		recOutTemp = JDTORecordFactory.getInstance().create();
    		recOutTemp.setRecord(rsResult.getRecord());
	    	
	    	szYD_GP   			= commUtils.trim(recOutTemp.getFieldString("YD_GP"));
	    	szYD_BAY_GP			= commUtils.trim(recOutTemp.getFieldString("YD_BAY_GP"));
	    	
	    	//차량이송재료 조회
			szMsg="차량이송재료와 저장품 join조회";
			commUtils.printLog(logId, szMsg, "SL");
			
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
	    	
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarftmvmtlID 
	    	SELECT A.YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
	    	     , A.SSTL_NO  AS SSTL_NO
	    	     , A.REGISTER  AS REGISTER
	    	     , TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
	    	     , A.MODIFIER  AS MODIFIER
	    	     , TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
	    	     , A.DEL_YN  AS DEL_YN
	    	     , A.YS_STK_BED_NO  AS YS_STK_BED_NO
	    	     , A.YS_STK_LYR_NO  AS YS_STK_LYR_NO
	    	     , A.YS_STK_SEQ_NO  AS YS_STK_SEQ_NO
	    	     , A.HCR_GP  AS HCR_GP
	    	     , A.STL_PROG_CD  AS STL_PROG_CD
	    	     , A.YS_MTL_ITEM  AS YS_MTL_ITEM
	    	     , B.YD_RCPT_PLN_STR_LOC
	    	     , (SELECT ARR_YD_PNT_CD FROM USRTSA.TB_TS_MATL_FTMV_WO C
	    	         WHERE C.TRANSWORD_SEQNO=(SELECT MAX(TRANSWORD_SEQNO) FROM TB_TS_MATL_FTMV_WO D
	    	                                   WHERE D.STL_NO=C.STL_NO
	    	                                   )
	    	           AND C.STL_NO=A.SSTL_NO ) AS ARR_YD_PNT_CD
	    	     , B.CUST_CD     
	    	     , B.DETAIL_ARR_CD     
	    	     , B.HEAT_NO     
	    	     , B.YD_MTL_L_GP             
	    	     , B.CUST_CD || B.DETAIL_ARR_CD || B.HEAT_NO || B.YD_MTL_L_GP AS GROUP_CHK_ID            
	    	  FROM TB_YS_CARFTMVMTL A
	    	     , TB_YS_STOCK B
	    	 WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	    	   AND A.SSTL_NO = B.SSTL_NO(+)
	    	   AND A.DEL_YN='N'
	    	 ORDER BY YS_STK_BED_NO, YS_STK_LYR_NO DESC
	    	 */
	    	rsCarBookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	rsCarBookMtl = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarftmvmtlID", logId, methodNm, "차량재료 조회"); 
	    	
	    	if (rsCarBookMtl == null || rsCarBookMtl.size() <= 0) {
				szMsg = "["+methodNm+"] 차량재료 조회 data not found";
	    		commUtils.printLog(logId, szMsg, "SL");
	    		return intRtnVal = -1;
			}
			
		    // 스케줄코드 - WC
		    if (szYD_GP.equals("C")||szYD_GP.equals("B"))
		    	szCUR_SCH_CD = szYD_GP + szYD_BAY_GP + "TR02LM";  // 이송입고
		    else if (szYD_GP.equals("K"))
		    	szCUR_SCH_CD = szYD_GP + szYD_BAY_GP + "TR20LM";  // 차량입고
			
	    	szMsg="["+szOperationName+"] 스케줄코드["+szCUR_SCH_CD+"]";
	    	commUtils.printLog(logId, szMsg, "SL");    	
	    	//-------------------------------------------------------------------------------------------------------------
	    	
	    	//스케줄코드로 스케줄기준Table조회
			szMsg="스케줄코드로 스케줄기준Table조회";
			commUtils.printLog(logId, szMsg, "SL");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_SCH_CD", szCUR_SCH_CD);
	    	
	    	
	    	//스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.
			szMsg="스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.";
			commUtils.printLog(logId, szMsg, "SL");
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	/*  com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule 
	    	SELECT A.YD_GP
	    	      ,A.YD_BAY_GP
	    	      ,YD_SCH_CD
	    	      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN1
	    	            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN2
	    	        END AS YD_WRK_CRN
	    	      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN_PRIOR1
	    	            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN_PRIOR2
	    	        END AS YD_WRK_CRN_PRIOR
	    	      ,YD_SCH_CD_NM
	    	      ,YD_SCH_CONTENTS
	    	      ,YD_SCH_PROH_EXN 
	    	    FROM TB_YS_SCHRULE A
	    	        ,(
	    	            SELECT YD_GP
	    	                  ,YD_BAY_GP
	    	                  ,YD_SCH_GP
	    	                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 1, YD_EQP_STAT, '') ELSE 'O' END), 'X') AS STAT1 
	    	                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 2, YD_EQP_STAT, '') ELSE 'X' END), 'X') AS STAT2 
	    	            FROM   (
	    	                        SELECT YD_EQP_ID
	    	                              ,YD_GP
	    	                              ,YD_BAY_GP
	    	                              ,DECODE(YD_EQP_STAT,'B','C','O') AS YD_EQP_STAT
	    	                              ,SUBSTR(YD_EQP_NO,-1) AS CRN_NO 
	    	                              ,YD_EQP_GP AS YD_SCH_GP
	    	                        FROM   TB_YS_EQP
	    	                        WHERE  YD_EQP_GP IN ('CR','SC')
	    	                   )
	    	            GROUP BY YD_GP, YD_BAY_GP , YD_SCH_GP             
	    	         ) B
	    	    WHERE A.YD_SCH_CD LIKE :V_YD_SCH_CD || '%'
	    	    AND   A.YD_DATA_GP = 'M'
	    	    AND   A.YD_SCH_GP = B.YD_SCH_GP
	    	    AND   A.YD_GP = B.YD_GP
	    	    AND   A.YD_BAY_GP = B.YD_BAY_GP
	    	    AND   A.YD_CRN_STAT1 = B.STAT1
	    	    AND   A.YD_CRN_STAT2 = B.STAT2
	    	    ORDER BY A.YD_GP, A.YD_BAY_GP, A.YD_SCH_CD
	    	*/	    	 
	    	rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
	    		szMsg = "["+methodNm+"] 스케줄 기준 조회 data not found";
				commUtils.printLog(logId, szMsg, "SL");
				return intRtnVal = -1;
			}
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			szSCH_PRIOR = commUtils.trim(recPara.getFieldString("YD_WRK_CRN_PRIOR"));	

			//생성한 작업예약ID
			//com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdWrkBook
			szYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
			
			if ("".equals(szYD_WBOOK_ID)) {
				throw new Exception("작업예약ID 생성 실패");
			}

			//작업예약항목SETTING
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	
	    	recInTemp.setField("YD_WBOOK_ID",      szYD_WBOOK_ID);
	    	recInTemp.setField("REGISTER",         szREGISTER);
	    	recInTemp.setField("YD_GP",            szYD_GP);
	    	recInTemp.setField("YD_BAY_GP",        szYD_BAY_GP);
	    	recInTemp.setField("YD_AIM_YD_GP",     szYD_GP);
	    	recInTemp.setField("YD_AIM_BAY_GP",    szYD_BAY_GP);
	    	recInTemp.setField("YD_SCH_PRIOR", 	   szSCH_PRIOR);
	    	recInTemp.setField("YD_SCH_CD",        szCUR_SCH_CD);
	    	recInTemp.setField("TRN_EQP_CD",       szTRN_EQP_CD);
	    	recInTemp.setField("YD_CAR_USE_GP",    szYD_CAR_USE_GP);
	    	
			//com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook
			commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
			
	    	//작업예약재료 등록
	    	for(int Loop_i = 1; Loop_i <= rsCarBookMtl.size(); Loop_i++) {
	    		rsCarBookMtl.absolute(Loop_i);
	    		recInTemp  = JDTORecordFactory.getInstance().create();
	    		recOutTemp = JDTORecordFactory.getInstance().create();
	    		recOutTemp.setRecord(rsCarBookMtl.getRecord());
	    		
	    		recInTemp.setField("YD_WBOOK_ID",    szYD_WBOOK_ID);
	    		recInTemp.setField("REGISTER",       szREGISTER);
	    		recInTemp.setField("YS_STK_BED_NO",  commUtils.trim(recOutTemp.getFieldString("YS_STK_BED_NO"       )));
	    		recInTemp.setField("YS_STK_LYR_NO",  commUtils.trim(recOutTemp.getFieldString("YS_STK_LYR_NO"       )));
	    		recInTemp.setField("SSTL_NO",        commUtils.trim(recOutTemp.getFieldString("SSTL_NO"       )));
	    		recInTemp.setField("YD_UP_COLL_SEQ", "" + Loop_i);
	    		
	    		recInTemp.setField("YS_STK_SEQ_NO",  commUtils.trim(recOutTemp.getFieldString("YS_STK_SEQ_NO"       ))); // WC 추가
	    		
	    		commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
	    		if(intRtnVal == -2) {
	    			szMsg = "["+methodNm+"] insYdWrkbookmtl parameter error";
					commUtils.printLog(logId, szMsg, "SL");
					return intRtnVal = -1;
	    		}
	    	}
	    		    	
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_CAR_SCH_ID"			, szYD_CAR_SCH_ID);
	    	recInTemp.setField("YD_CARUD_WRK_BOOK_ID"	, szYD_WBOOK_ID);
	    	recInTemp.setField("YD_CAR_PROG_STAT"		, "A");
			recInTemp.setField("YD_CARUD_LEV_DT"		, commUtils.getDateTime14());
			recInTemp.setField("MODIFIER"				, szREGISTER);
			recInTemp.setField("YD_CARUD_STOP_LOC"		, szYD_GP + szYD_BAY_GP);
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarSchCarWrkUD  
			UPDATE TB_YS_CARSCH
			   SET MODIFIER = :V_MODIFIER
			     , MOD_DDTT = SYSDATE
			     , YD_CARUD_WRK_BOOK_ID = :V_YD_CARUD_WRK_BOOK_ID
			     , YD_CARUD_LEV_DT = TO_DATE(:V_YD_CARUD_LEV_DT,'YYYYMMDDHH24MISS')
			     , YD_CAR_PROG_STAT = :V_YD_CAR_PROG_STAT
			     , ARR_WLOC_CD      = NVL(:V_ARR_WLOC_CD,ARR_WLOC_CD)
			     , YD_CARUD_STOP_LOC   = NVL(:V_YD_CARUD_STOP_LOC,YD_CARUD_STOP_LOC)
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			 */
	    	intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarSchCarWrkUD", logId, methodNm, "TB_YS_CARSCH 등록");
			if(intRtnVal <= 0) {
				szMsg="["+szOperationName+"] 차량스케줄 업데이트 시 존재하지 않습니다.";
				commUtils.printLog(logId, methodNm, "SL");
				throw new DAOException(szMsg);
			}	
			
		}catch(Exception e){
			
			szMsg="차량하차작업예약생성 처리 Error:" +e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
			return intRtnVal = -1;
		}
	
	
		szMsg="차량하차작업예약생성 처리 ("+szMethodName+") 완료";
		commUtils.printLog(logId, szMsg, "SL");
		commUtils.printLog(logId, methodNm, "S-");
		return intRtnVal = 1;
	} //end of procInsWrkBookCarUd()
	
	/**
	 * 오퍼레이션명 : 소재차량 개소코드구분 및 상차Lot편성 호출 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procInsCarSch(String logId, String rcvMsg,JDTORecord msgRecord,JDTORecord sndRecord)throws JDTOException  {
		String methodNm = "소재차량 개소코드구분 및 상차Lot편성 호출[YsCommCarMvSeEJB.procInsCarSch] < " + rcvMsg ;
		JDTORecord recPara 		= null;

		int intRtnVal 		   	= 0 ;
	    String szMsg           	= "";
	    String szMethodName    	= "procInsCarSch";
	    String szTRN_EQP_CD		= null;
	    String szWLOC_CD       	= "";
	    String szYD_CARLD_LEV_LOC	= null;
	    String szYD_CAR_SCH_ID	= null;
	    String szTRN_WRK_FULLVOID_GP 	= null;

	    
	    try{
	    	commUtils.printLog(logId, methodNm, "S+"); 
	    	szMsg="["+methodNm+"] 메소드 시작";
	    	commUtils.printLog(logId, szMsg, "SL");
	    	
	    	//하위모듈에서 EJB Call or JMS Call 유무
	    	szTRN_EQP_CD 			= commUtils.trim(msgRecord.getFieldString("TRN_EQP_CD"       ));
	    	szWLOC_CD 				= commUtils.trim(msgRecord.getFieldString("WLOC_CD"));
	    	szYD_CARLD_LEV_LOC 		= commUtils.trim(msgRecord.getFieldString("YD_CARLD_LEV_LOC"));
	    	szTRN_WRK_FULLVOID_GP 	= commUtils.trim(msgRecord.getFieldString("TRN_WRK_FULLVOID_GP"));
	    	
	    	//----------------------------------------------------------------------------------------
	    	//	차량스케줄을 생성
	    	//----------------------------------------------------------------------------------------
	    	
	    	recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("REGISTER",         szMethodName.length() > 10 ? szMethodName.subSequence(0, 10) : szMethodName);
			recPara.setField("YD_EQP_WRK_STAT",  "U");										//야드설비작업상태
			recPara.setField("YD_EQP_ID",        YsConstant.YD_TS_CAR_EQP_ID);				//야드설비ID
			recPara.setField("TRN_EQP_CD",       szTRN_EQP_CD);								//운송장비코드
			recPara.setField("YD_CAR_USE_GP",    "L");				                        //차량사용구분(L:구내,G:출하)
			recPara.setField("SPOS_WLOC_CD",     szWLOC_CD);								//발지개소코드
			recPara.setField("YD_CARLD_LEV_LOC", szYD_CARLD_LEV_LOC);						//야드상차출발위치
			recPara.setField("YD_CARLD_LEV_DT",  commUtils.getDateTime14());				//상차출발일시
			recPara.setField("YD_BAYIN_WO_SEQ",  "9");										//입동지시순번 - 기본값으로 설정(9)
			recPara.setField("YD_CAR_PROG_STAT", YsConstant.YD_CARLD_LEV);					//상차출발상태
			//recPara.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);					//야드상차정지위치 (직상차 제외)
			
            //차량SCH ID 생성
			recPara.setField("YD_CAR_SCH_ID",    commDao.getSeqId(logId, methodNm, "CarSch"));
			//차량스케줄 등록
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.insYdCarsch
			INSERT INTO TB_YS_CARSCH
			(	   YD_CAR_SCH_ID
			     , REGISTER
			     , REG_DDTT		
			     , YD_EQP_ID
			     , TRN_EQP_CD
			     , YD_CAR_USE_GP
			     , SPOS_WLOC_CD
			     , YD_CARLD_LEV_LOC
			     , YD_CARLD_LEV_DT
			     , YD_BAYIN_WO_SEQ
			     , YD_CAR_PROG_STAT
			       )
			VALUES (
			      :V_YD_CAR_SCH_ID
			     , :V_REGISTER
			     , SYSDATE
			     , :V_YD_EQP_ID
			     , :V_TRN_EQP_CD
			     , :V_YD_CAR_USE_GP
			     , :V_SPOS_WLOC_CD
			     , :V_YD_CARLD_LEV_LOC
			     ,  TO_DATE(:V_YD_CARLD_LEV_DT,'YYYYMMDDHH24MISS')
			     , :V_YD_BAYIN_WO_SEQ
			     , :V_YD_CAR_PROG_STAT
			       )
			       */
			intRtnVal = commDao.insert(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.insYdCarsch", logId, methodNm, "차량스케줄 등록");
			
			if( intRtnVal <= 0 ){
				szMsg= szMethodName + "개소코드["+szWLOC_CD+"] : 차량스케줄["+szYD_CAR_SCH_ID+"] 생성 시 오류발생 - 반환값 : " + intRtnVal;
				commUtils.printLog(logId, szMsg, "SL");

				sndRecord.setTaskCode("-1");
				return sndRecord;
			}
			
			szYD_CAR_SCH_ID = commUtils.trim(recPara.getFieldString("YD_CAR_SCH_ID"));
			
    		szMsg= methodNm + "개소코드["+szWLOC_CD+"] : 차량스케줄["+szYD_CAR_SCH_ID+"] 생성 완료 - 반환값 : " + intRtnVal;
    		commUtils.printLog(logId, szMsg, "SL");
			
			//----------------------------------------------------------------------------------------
	    	//	자동/수동LOT편성 판단 후 자동LOT편성이면 상차LOT편성 모듈 호출하고
	    	//	수동LOT편성이면 차량도착POINT요구모듈을 호출
	    	//----------------------------------------------------------------------------------------
				
			//소재차량도착Point요구 호출
			//record 생성
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setResultCode(logId);	//Log ID
			recPara.setResultMsg(methodNm);	//Log Method Name
			recPara.setField("JMS_TC_CD",               YsConstant.YSYSJ901); //"YDYDJ630"); // rcvTSYSJ002
			recPara.setField("JMS_TC_CREATE_DDTT",      commUtils.getDateTime14()); //JMSTC생성일시
			recPara.setField("TRN_EQP_CD",              szTRN_EQP_CD);
			recPara.setField("WLOC_CD",                 szWLOC_CD);
			recPara.setField("TRN_WRK_FULLVOID_GP",     szTRN_WRK_FULLVOID_GP);
			recPara.setField("PNT_DMD_DT",              commUtils.getDateTime14());
			
			//소재차량도착Point요구 호출
			sndRecord = commUtils.addSndData(sndRecord, recPara);	
			
			szMsg=methodNm + " 소재차량도착Point요구 모듈 호출 완료";
			

	    	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    	
		}catch(Exception e){
			
			szMsg=methodNm + "개소코드구분 및 상차Lot편성 호출처리 Error:" +e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
			sndRecord.setTaskCode("-1");
			return sndRecord;
		}
	
	
		szMsg= methodNm + "개소코드구분 및 상차Lot편성 호출처리 완료 - 메소드 끝";
		commUtils.printLog(logId, szMsg, "SL");
		sndRecord.setTaskCode("1");
		commUtils.printLog(logId, methodNm, "S-");
		return sndRecord;
	} //end of procInsCarSch()
	
	/**
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procCarPosActiveOrInActive(JDTORecord rcvMsg) throws DAOException {	
		String methodNm = "비활성처리[YsCommCarMvSeEJB..procCarPosActiveOrInActive] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		int intRtnVal = -100;
		String szMsg = null;
		String szRtnMsg = YsConstant.RETN_CD_SUCCESS;
		String szYD_STK_BED_ACT_STAT = null;					//야드적치베드활성상태
		String szYD_STK_LYR_ACT_STAT = null;					//야드적치단활성상태
		
		JDTORecord recInTemp = null;
		/*
		 * 파라미터 확인
		 */
		String szYS_STK_COL_GP 		 = StringHelper.evl(rcvMsg.getFieldString("YS_STK_COL_GP"), "");
		String szYD_CAR_USE_GP 		 = StringHelper.evl(rcvMsg.getFieldString("YD_CAR_USE_GP"), "");
		String szTRN_EQP_CD 		 = StringHelper.evl(rcvMsg.getFieldString("TRN_EQP_CD"), "");
		String szCAR_NO 			 = StringHelper.evl(rcvMsg.getFieldString("CAR_NO"), "");
		String szCARD_NO 			 = StringHelper.evl(rcvMsg.getFieldString("CARD_NO"), "");
		String szTRN_EQP_STK_CAPA 	 = StringHelper.evl(rcvMsg.getFieldString("TRN_EQP_STK_CAPA"), "");
		String szYD_STK_COL_ACT_STAT = StringHelper.evl(rcvMsg.getFieldString("YD_STK_COL_ACT_STAT"), "");
		String szMODIFIER			 = StringHelper.evl(rcvMsg.getFieldString("MODIFIER"), "");
		
		
		/*
		 * 값 검증
		 */
		commUtils.printLog(logId, methodNm, "S+");
		
		if( szYS_STK_COL_GP.equals("") ) {
			szMsg="[" + methodNm + "] 적치열이 존재하지 않습니다.";
			commUtils.printLog(logId, szMsg, "SL");
			return "NOPARAM";
		}
		
		if( szYD_CAR_USE_GP.equals("L") ) {			//구내운송
			if( szYD_STK_COL_ACT_STAT.equals("L") && szTRN_EQP_CD.equals("") ) {   //적치가능
				szMsg="[" + methodNm + "] 차량사용구분["+szYD_CAR_USE_GP+"]이므로 운송장비코드가 존재해야합니다.";
				commUtils.printLog(logId, szMsg, "S-");
				return YsConstant.RETN_CD_FAILURE;
			}
		}else if( szYD_CAR_USE_GP.equals("G") ) {	//출하차량
			if( szYD_STK_COL_ACT_STAT.equals("L") ) {
				if( szCAR_NO.equals("") ) {
					szMsg="[" + methodNm + "] 차량사용구분["+szYD_CAR_USE_GP+"]이므로 차량번호가 존재해야합니다.";
					commUtils.printLog(logId, szMsg, "SL");
					return YsConstant.RETN_CD_FAILURE;
				}
			}
		}

		if( szYD_STK_COL_ACT_STAT.equals("C") ) {		//비활성화
			szYD_STK_BED_ACT_STAT 		= "C";
			szYD_STK_LYR_ACT_STAT 		= "C";
			szTRN_EQP_STK_CAPA 			= YsConstant.YD_STK_BED_WT_MAX_DEFAULT;
			szYD_CAR_USE_GP				= "";
		
		}else{
			szMsg="[" + methodNm + "] 사용가능값[활성화L,비활성화C: - 사용할 수 없는 값["+szYD_STK_COL_ACT_STAT+"]입니다.";
			commUtils.printLog(logId, methodNm, "SL");
			return YsConstant.RETN_CD_FAILURE;
		}
		
		szMsg="[" + methodNm + "] 적치열 활성/비활성 처리.";
		commUtils.printLog(logId, methodNm, "SL");

		try {
			/*
			 * 적치열 활성/비활성 처리
			 */
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YS_STK_COL_GP",        	szYS_STK_COL_GP);
	    	recInTemp.setField("YD_CAR_USE_GP",        	szYD_CAR_USE_GP);
	    	recInTemp.setField("TRN_EQP_CD",           	szTRN_EQP_CD);
	    	recInTemp.setField("CAR_NO",           		szCAR_NO);
	    	recInTemp.setField("CARD_NO",           	szCARD_NO);
	    	recInTemp.setField("YD_STK_COL_ACT_STAT",   szYD_STK_COL_ACT_STAT);
	    	recInTemp.setField("MODIFIER",				szMODIFIER);					//수정자
	    	/* 
	    	com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStatClear
	    	UPDATE TB_YS_STKCOL
	    	   SET MOD_DDTT     = SYSDATE             
	    		 , MODIFIER     = :V_MODIFIER             
	    		 , YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
	    		 , TRN_EQP_CD   = null
	    		 , CAR_NO       = null
	    		 , CARD_NO      = null
	    	     , YD_CAR_USE_GP = null
	    	WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
	    	*/
	    	intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStatClear", logId, methodNm, "TB_YS_STKCOL 등록");
			if(intRtnVal <= 0) {
				szMsg="[" + methodNm + "] 적치열[" + szYS_STK_COL_GP + "]활성화중 ERROR 발생.";
				commUtils.printLog(logId, methodNm, "SL");
				m_ctx.setRollbackOnly();
				throw new DAOException(szMsg);
			}

			/*
			 * 차량포인트 비활성 처리
			 */
			
	    	YdStockDAO ydStockDAO = new YdStockDAO();
			
    		//저장위치로 차량 포인트 예약 하는 경우(출하)
	    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.carpointtrneqpcdupdateC2
	    	UPDATE TB_YD_CARPOINT
	    	   SET YD_STK_COL_ACT_STAT=:V_STAT
	    	     , CAR_NO  =:V_CAR_NO
	    	     , CARD_NO =:V_TRN_EQP_CD
	    	     , MOD_DDTT=sysdate
	    	     , MODIFIER='CarPointC'
	    	 WHERE YD_STK_COL_GP=:V_YS_STK_COL_GP
    		*/ 
			String stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointtrneqpcdupdateC2";
			ydStockDAO.requestupdateData(stkQueryId, new Object[]{ szYD_STK_COL_ACT_STAT,szCAR_NO ,szCARD_NO,szYS_STK_COL_GP});

			/*
			 * 적치베드 상태 비활성 처리
			 */
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_STK_BED_WT_MAX"	, szTRN_EQP_STK_CAPA);
			recInTemp.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
			recInTemp.setField("YD_STK_BED_ACT_STAT", szYD_STK_BED_ACT_STAT);
			recInTemp.setField("MODIFIER",				szMODIFIER);					//수정자
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedYdStkColGp  

			UPDATE TB_YS_STKBED
			   SET MOD_DDTT     = SYSDATE             
				 , MODIFIER     = :V_MODIFIER             
				 , YD_STK_BED_ACT_STAT = NVL(:V_YD_STK_BED_ACT_STAT,YD_STK_BED_ACT_STAT)
			     , YD_STK_BED_WT_MAX = NVL(:V_YD_STK_BED_WT_MAX,YD_STK_BED_WT_MAX )
			  WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
			  */
			intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedYdStkColGp", logId, methodNm, "TB_YS_STKBED 등록");
			if(intRtnVal <= 0) {
				szMsg="[" + methodNm + "] 적치열[" + szYS_STK_COL_GP + "]수정 시 존재하지 않습니다.";
				commUtils.printLog(logId, methodNm, "SL");
				throw new DAOException(szMsg);
			}
			
			
			/*
			 * 적치단 비활성 처리
			 */
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YS_STK_COL_GP", szYS_STK_COL_GP);
			recInTemp.setField("YD_STK_LYR_ACT_STAT", szYD_STK_LYR_ACT_STAT);
			recInTemp.setField("SSTL_NO", "");
			recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
			recInTemp.setField("MODIFIER",				szMODIFIER);					//수정자
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear  

			UPDATE TB_YS_STKLYR            
			   SET MOD_DDTT     = SYSDATE             
				 , MODIFIER     = :V_MODIFIER             
				 , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
			     , SSTL_NO = null
			     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
			 WHERE YS_STK_COL_GP  = :V_YS_STK_COL_GP
			 */
			intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "TB_YS_STKLYR 등록");
			
			if(intRtnVal <= 0) {
				szMsg = "[" + methodNm + "] 적치열[" + szYS_STK_COL_GP + "]의 적치베드를 수정 시 오류발생 - 반환값 : " + intRtnVal;
				commUtils.printLog(logId, szMsg, "SL");
				return YsConstant.RETN_CD_FAILURE;
			}

			
			commUtils.printLog(logId, methodNm, "S-");	
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
		return szRtnMsg;
	}

	/**
	 *      [A] 오퍼레이션명 : 출하차량출발실적 처리 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procOutCarLevWr(JDTORecord rcvMsg)throws JDTOException  {
		
		String methodNm = "출하차량-차량출발처리[YsCommCarMvSeEJB.procOutCarLevWr] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		String szMsg                   = "";
	    String szMethodName      	   = "procOutCarLevWr";
	    String szOperationName			= "출하차량-차량출발처리";
	    
		JDTORecordSet rsResult         = null;
		JDTORecordSet rsStkCol         = null;
		JDTORecord    recInTemp        = null;
		JDTORecord    recOutTemp       = null;
		JDTORecord    recInPara        = null;
		JDTORecord    recGetVal        = null;
		JDTORecord	  sndRecord			= JDTORecordFactory.getInstance().create();
		
	    // 발지개소코드
	    String szSPOS_WLOC_CD     = "";

	    // 발지야드포인트코드
	    String szSPOS_YD_PNT_CD   = "";    
	    String szYD_CARLD_LEV_LOC = "";
	    String szTRANS_ORD_DATE     = "";    
	    String szTRANS_ORD_SEQNO  = "";    
	    String szCAR_NO           = "";    
	    String szCARD_NO          = "";
	    String szCarSchId         = "";
	    String szYD_CARPNT_CD	  = "";
	    String szCAR_NO_CHK		  = "";
	    String szCHK_YN		  	  = "N";
	    
	    int intRtnVal 			  = 0;
	    int intLevLocGp           = 0;
	    int nRet                  = 0;
		
		try{
			commUtils.printLog(logId, methodNm, "S+");	
			commUtils.printParam(logId + "출하차량출발실적 처리 수신 ", rcvMsg);
			
	    	//운송지시일자
			szTRANS_ORD_DATE    = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DATE"     )); 
	    	if(szTRANS_ORD_DATE.equals("")) {
				szMsg="운송지시일자가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new DAOException("TRANS_ORD_DATE Error");
	    	}
	    	//운송지시순번
	    	szTRANS_ORD_SEQNO = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"     ));
	    	if(szTRANS_ORD_SEQNO.equals("")) {
				szMsg="운송지시순번이 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new DAOException("TRANS_ORD_SEQNO Error");
	    	}
	    	//차량번호
	    	szCAR_NO          = commUtils.trim(rcvMsg.getFieldString("CAR_NO"     ));
	    	if(szCAR_NO.equals("")) {
				szMsg="차량번호가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new DAOException("CAR_NO Error");
	    	}
	    	//카드번호
	    	szCARD_NO         = commUtils.trim(rcvMsg.getFieldString("CARD_NO"     ));

	    	//발지개소코드
	    	szSPOS_WLOC_CD    = commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"     ));
	    	if(szSPOS_WLOC_CD.equals("")) {
				szMsg="발지개소코드가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
	    	}
	    	//발지포인트코드
	    	szSPOS_YD_PNT_CD  = commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"     ));
	    	if(szSPOS_YD_PNT_CD.equals("")) {
				szMsg="발지포인트코드가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new DAOException("SPOS_YD_PNT_CD Error");
	    	}

	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("TRANS_ORD_DATE",  szTRANS_ORD_DATE);
	    	recInTemp.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
	    	recInTemp.setField("CAR_NO",          szCAR_NO);
	    	recInTemp.setField("CARD_NO",         szCARD_NO);
	    	recInTemp.setField("SPOS_WLOC_CD",    szSPOS_WLOC_CD);
	    	recInTemp.setField("SPOS_YD_PNT_CD",  szSPOS_YD_PNT_CD);
			
	    	//발지위치정보로 출발위치 Clear
	    	//열정보 Clear 업데이트 후 리턴값이 1이상이면 베드 단정보도 Clear
	    	//업데이트값이 없다면 그냥 종료
	    	//발지개소코드를 변환, 발지개소Point를 변환(출발지 위치)
	    	rsStkCol = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("WLOC_CD",   szSPOS_WLOC_CD);
	    	recInTemp.setField("YD_PNT_CD", szSPOS_YD_PNT_CD);
	    	
	    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolWLocCdandPntCd */
	    	//intLevLocGp = ydStkColDao.getYdStkcol(recInTemp, rsStkCol, 4);
	    	/*
	    	SELECT A.YS_STK_COL_GP                         AS YS_STK_COL_GP
	    	      ,TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
	    	      ,A.REGISTER                              AS REGISTER
	    	      ,TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
	    	      ,A.MODIFIER                              AS MODIFIER
	    	      ,A.DEL_YN                                AS DEL_YN
	    	      ,A.YD_GP                                 AS YD_GP
	    	      ,A.YD_BAY_GP                             AS YD_BAY_GP
	    	      ,A.YD_EQP_GP                             AS YD_EQP_GP
	    	      ,A.YD_STK_COL_NO                         AS YD_STK_COL_NO
	    	      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.YD_STK_COL_ACT_STAT   ELSE A.YD_STK_COL_ACT_STAT  END ) AS YD_STK_COL_ACT_STAT     
	    	      ,A.YD_STK_COL_RULE_XAXIS                 AS YD_STK_COL_RULE_XAXIS 
	    	      ,A.YD_STK_COL_RULE_YAXIS                 AS YD_STK_COL_RULE_YAXIS 
	    	      ,A.YD_STK_COL_W                          AS YD_STK_COL_W                   
	    	      ,A.YD_STK_COL_L                          AS YD_STK_COL_L                   
	    	      ,A.YD_CAR_USE_GP                         AS YD_CAR_USE_GP                 
	    	      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.TRN_EQP_CD ELSE A.TRN_EQP_CD END)    AS TRN_EQP_CD                       
	    	      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.CAR_NO  ELSE  A.CAR_NO END)                 AS CAR_NO                               
	    	      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.CARD_NO   ELSE A.CARD_NO END)               AS CARD_NO                             
	    	      ,A.WLOC_CD                               AS WLOC_CD                             
	    	      ,A.YD_PNT_CD                             AS YD_PNT_CD   
	    	      ,B.YD_CARPNT_CD AS YD_CARPNT_CD
	    	  FROM TB_YS_STKCOL A   
	    	     , TB_YS_CARPOINT B
	    	 WHERE B.YS_STK_COL_GP=A.YS_STK_COL_GP
	    	   AND A.WLOC_CD =  :V_WLOC_CD
	    	   AND A.YD_PNT_CD = :V_YD_PNT_CD  	
	    	 */	    	
	    	rsStkCol = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolWLocCdandPntCd", logId, methodNm, "적치열 조회"); 
	    	intLevLocGp = rsStkCol.size();
	    	
	    	if (rsStkCol == null || intLevLocGp == 0) {
	    		szMsg= "[" + szOperationName + "] 발지개소["+szSPOS_WLOC_CD+"] 및 포인트 코드["+szSPOS_YD_PNT_CD+"]가 타공정코드가 아니고 대기장입니다.";
	    		commUtils.printLog(logId, szMsg, "SL");
	    	}
	    								
	    	if(intLevLocGp > 0) {
	    		
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsStkCol.getRecord(0));

		    	//열구분을 조회(도착지)
		    	szYD_CARLD_LEV_LOC = commUtils.trim(recOutTemp.getFieldString("YS_STK_COL_GP"     ));
		    	szYD_CARPNT_CD	   = commUtils.trim(recOutTemp.getFieldString("YD_CARPNT_CD"     ));
		    	szCAR_NO_CHK  	   = commUtils.trim(recOutTemp.getFieldString("CAR_NO"     ));
		    	
		    	//다른 차량이 존재 하는 경우 
		    	if(!szCAR_NO_CHK.equals(szCAR_NO)){
		    		szCHK_YN ="Y";
		    		szMsg="<procOutCarLevWr> 해당 위치에 다른 차량이 존재 합니다. 포인트 차량:"+szCAR_NO_CHK + "  취소대상 차량:"+ szCAR_NO;
					commUtils.printLog(logId, szMsg, "SL");
		    	}
		    	
		    	if(szCHK_YN.equals("N")){
		    		
		    		/*
					 * 적치열 비활성상태로 변경
					 */
					szMsg= "[" + szOperationName + "] 출발야드의 적치열["+szYD_CARLD_LEV_LOC+"]을 비활성상태로 변경처리 시작 ";
					commUtils.printLog(logId, szMsg, "SL");
					
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YS_STK_COL_GP",        szYD_CARLD_LEV_LOC);
			    	recInTemp.setField("YD_STK_COL_ACT_STAT",  "C");
			    	recInTemp.setField("YD_CAR_USE_GP",        "");
			    	recInTemp.setField("TRN_EQP_CD",           "");
			    	recInTemp.setField("CAR_NO",               "");
			    	recInTemp.setField("CARD_NO",              "");
			    	recInTemp.setField("MODIFIER", 				szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
			    	
			    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStatClear 
					UPDATE TB_YS_STKCOL
					   SET MOD_DDTT     = SYSDATE             
						 , MODIFIER     = :V_MODIFIER             
						 , YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
						 , TRN_EQP_CD   = null
						 , CAR_NO       = null
						 , CARD_NO      = null
					     , YD_CAR_USE_GP = null

					WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
				*/
			    	intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStatClear", logId, methodNm, "TB_YS_STKCOL 등록");
					if(intRtnVal <= 0) {
						szMsg="[" + methodNm + "] 적치열[" + szYD_CARLD_LEV_LOC + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, methodNm, "SL");
						m_ctx.setRollbackOnly();
						throw new DAOException(szMsg);
					}
				

					//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					YsCarPointinforeg2("B","","",szYD_CARLD_LEV_LOC,"","","C",logId,methodNm);
					
					/*
					 * 적치베드 상태 비활성화등록
					 */
					szMsg= "[" + szOperationName + "] 출발야드의 적치열["+szYD_CARLD_LEV_LOC+"]의 베드를 비활성상태와 BED중량MAX기본값으로 변경처리 시작 ";
					commUtils.printLog(logId, szMsg, "SL");
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_STK_BED_WT_MAX", YsConstant.YD_STK_BED_WT_MAX_DEFAULT);
					recInTemp.setField("YS_STK_COL_GP", szYD_CARLD_LEV_LOC);
					recInTemp.setField("YD_STK_BED_ACT_STAT", "C");
					
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedYdStkColGp  

					UPDATE TB_YS_STKBED
					   SET MOD_DDTT     = SYSDATE             
						 , MODIFIER     = :V_MODIFIER             
						 , YD_STK_BED_ACT_STAT = NVL(:V_YD_STK_BED_ACT_STAT,YD_STK_BED_ACT_STAT)
					     , YD_STK_BED_WT_MAX = NVL(:V_YD_STK_BED_WT_MAX,YD_STK_BED_WT_MAX )
					  WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
					  */
					intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedYdStkColGp", logId, methodNm, "TB_YS_STKBED 등록");
					if(intRtnVal <= 0) {
						szMsg="[" + methodNm + "] 적치BED[" + szYD_CARLD_LEV_LOC + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, methodNm, "SL");
						throw new DAOException(szMsg);
					}
					
					/*
					 * 적치단 비활성화
					 */
					szMsg= "[" + szOperationName + "] 출발야드의 적치열["+szYD_CARLD_LEV_LOC+"]의 적치단을 비활성상태로 변경처리 시작 ";
					commUtils.printLog(logId, methodNm, "SL");
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YS_STK_COL_GP", szYD_CARLD_LEV_LOC);
					recInTemp.setField("YD_STK_LYR_ACT_STAT", "C");
					recInTemp.setField("SSTL_NO", "");
					recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
			    	
					//intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGp(recInTemp);
			    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGp  

					UPDATE TB_YS_STKLYR            
					   SET MOD_DDTT     = SYSDATE             
						 , MODIFIER     = :V_MODIFIER             
						 , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
					     , SSTL_NO = null
					     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
					 WHERE YS_STK_COL_GP  = :V_YS_STK_COL_GP
					 */
					intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGp", logId, methodNm, "TB_YS_STKLYR 등록");
					
					if(intRtnVal <= 0) {
						szMsg="[" + methodNm + "] 적치단[" + szYD_CARLD_LEV_LOC + "]활성화중 ERROR 발생.";
						commUtils.printLog(logId, szMsg, "SL");
						throw new DAOException(szMsg);

					}
					
			    	//=======================================================================
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);	//Log ID
					recInTemp.setResultMsg(methodNm);	//Log Method Name
			    	String szYdGp = szYD_CARLD_LEV_LOC.substring(0,2);
					String szJMS_TC_CD = "";
					
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
		    		recInTemp.setField("MSG_ID",    szJMS_TC_CD);
					recInTemp.setField("YD_INFO_SYNC_CD", "3");						//1:동,2:SPAN,3:열,4:BED
					recInTemp.setField("YD_GP", szYD_CARLD_LEV_LOC.substring(0, 1));
					recInTemp.setField("YS_STK_COL_GP", szYD_CARLD_LEV_LOC);
					recInTemp.setField("YD_CAR_PROG_STAT", "A");
					recInTemp.setField("YD_EQP_WRK_STAT" , "L");
					szMsg = "[" + szOperationName + "] 영차출발시 시 저장위치 제원 야드L2로 전송";
					
					//전송 Data 생성
					sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2(szJMS_TC_CD, recInTemp));

					szMsg="["+methodNm+"] 차량출발시 시 저장위치 제원 야드L2로 전송";
					commUtils.printLog(logId, szMsg, "SL");		
					
					
	
					/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
		    	}
		    	
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				// 차량스케줄 삭제
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				
				
				recInTemp = JDTORecordFactory.getInstance().create();
				
				recInTemp.setField("CAR_NO" , szCAR_NO);
				recInTemp.setField("CARD_NO", szCARD_NO);
				recInTemp.setField("TRANS_ORD_DATE",  szTRANS_ORD_DATE);
		    	recInTemp.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);

		    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschTransDTSeq2
		    	SELECT *
		    	 FROM (
		    	SELECT 
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
		    	    ,YD_EQP_WRK_SH  AS YD_EQP_WRK_SH
		    	    ,YD_EQP_WRK_WT  AS YD_EQP_WRK_WT
		    	    ,YS_STK_BED_TP  AS YS_STK_BED_TP
		    	    ,SPOS_WLOC_CD  AS SPOS_WLOC_CD
		    	    ,ARR_WLOC_CD  AS ARR_WLOC_CD
		    	    ,YD_CARLD_LEV_LOC  AS YD_CARLD_LEV_LOC
		    	    ,TO_CHAR(YD_CARLD_LEV_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_LEV_DT
		    	    ,TO_CHAR(YD_CARLD_PNT_WO_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_PNT_WO_DT
		    	    ,(CASE WHEN YD_CAR_PROG_STAT IN('A','B','C','D','E') THEN YD_PNT_CD3 ELSE YD_PNT_CD1 END) AS YD_PNT_CD1
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
		    	    ,CMBN_CARLD_YN
		    	FROM TB_YS_CARSCH
		    	WHERE CAR_NO LIKE :V_CAR_NO||'%'
		    	  AND CARD_NO = :V_CARD_NO
		    	  AND TRANS_ORD_DATE = :V_TRANS_ORD_DATE
		    	  AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
		    	--AND DEL_YN='N'
		    	ORDER BY YD_CAR_SCH_ID DESC
		    	) A
		    	WHERE ROWNUM<=1
		    	*/
		    	rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschTransDTSeq2", logId, methodNm, "차량스케줄  조회"); 
		    	intLevLocGp = rsResult.size();
		    	
		    	if (rsResult == null || rsResult.size() <= 0) {
					szMsg = "차량스케쥴 조회 오류 + (" + szCAR_NO + ", " + szCARD_NO + ", 'G')";
		    		commUtils.printLog(logId, szMsg, "SL");
		    		return sndRecord;
		    	}
		    
				rsResult.first();
				recGetVal = rsResult.getRecord();
				szCarSchId      = commUtils.trim(recGetVal.getFieldString("YD_CAR_SCH_ID"     )); 
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID" , szCarSchId);
				
				/*com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch 
				UPDATE TB_YS_CARSCH
				   SET MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				     , DEL_YN = 'Y'
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				     AND DEL_YN = 'N'
                */  
				nRet = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch", logId, methodNm, "차량스케줄 삭제");
				
				if(nRet <= 0) {
					szMsg = "차량 스케쥴 업데이트 갱신 오류 + (" + szCarSchId + ")";
					commUtils.printLog(logId, szMsg, "SL");
					throw new DAOException(szMsg);

				}
				
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				// 차량 이송재료 삭제
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				recInTemp = JDTORecordFactory.getInstance().create();

				recInTemp.setField("YD_CAR_SCH_ID" , szCarSchId);
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");

				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarftmvmtlBySchId 
				SELECT 
				       YD_CAR_SCH_ID 
				     , SSTL_NO 
				     , YS_STK_BED_NO 
				     , YS_STK_LYR_NO
				     , STL_PROG_CD 
				     , YS_MTL_ITEM
				     , YS_ROUTE_GP
				     , SUBSTR(YS_MTL_ITEM, 1, 1) AS YD_MTL_GP 
				  FROM TB_YS_CARFTMVMTL
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 ORDER BY YS_STK_BED_NO, YS_STK_LYR_NO
				 */
				rsResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarftmvmtlBySchId", logId, methodNm, "차량이송 조회"); 
		    	intLevLocGp = rsResult.size();
		    	
		    	if (rsResult == null || rsResult.size() <= 0) {
		    		szMsg = "차량재료정보가 존재 안합니다. + (" + szTRANS_ORD_DATE + ", " + szTRANS_ORD_SEQNO + ", " + szCAR_NO + ", " + szCARD_NO + ")";
		    		commUtils.printLog(logId, szMsg, "SL");
		    	} else {	
				
			    	recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID" , szCarSchId);
					
					
					/*com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSchMtl  
					UPDATE TB_YS_CARFTMVMTL
					   SET MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					     , DEL_YN = 'Y'
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					   AND DEL_YN = 'N'
	                */
					nRet = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSchMtl", logId, methodNm, "차량스케줄재료 삭제");
					
					if(nRet <= 0) {
						szMsg = "차량 이송재료 업데이트 갱신 오류 + (" + szCarSchId + ")";
						commUtils.printLog(logId, szMsg, "SL");
						throw new DAOException(szMsg);
	
					}
		    	}
		    	
				if(szCHK_YN.equals("N")){
					
					/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			         * 구내운송 소재차량Point개폐 전송  - YSTSJ012
			         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
					recInPara = JDTORecordFactory.getInstance().create();
					recInPara.setResultCode(logId);	//Log ID
					recInPara.setResultMsg(methodNm);	//Log Method Name
					recInPara.setField("JMS_TC_CD"       	, "YSTSJ012");
					recInPara.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
					recInPara.setField("YS_STK_COL_GP"		, szYD_CARLD_LEV_LOC);			//적치열구분
					recInPara.setField("PNT_UNIT_CL_GP"		, "O");//포인트개폐구분 'O':OPEN 'C':CLOSE
					
					sndRecord = commUtils.addSndData(sndRecord, recInPara);	
					
					szMsg="[구내내운송 소재차량Point개폐 전송 - 차량입동지시요구 모듈을 호출 성공"; 
					commUtils.printLog(logId, szMsg, "SL");
			
					/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
					/*
					 * 차량도착위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
					 */
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);	//Log ID
					recInTemp.setResultMsg(methodNm);	//Log Method Name
					recInTemp.setField("JMS_TC_CD"			, "YSYSJ801");          //차량입동지시 요구 기존:YDYDJ662
					recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
					recInTemp.setField("YD_CARPNT_CD"		, szYD_CARPNT_CD);		//입동포인트
					recInTemp.setField("YD_CAR_SCH_ID"		, szCarSchId);	        //차량스케줄ID
					recInTemp.setField("CAR_NO" 			, szCAR_NO);
					recInTemp.setField("CARD_NO"			, szCARD_NO);
					recInTemp.setField("YD_CAR_STOP_LOC"	, szYD_CARLD_LEV_LOC);
					
					sndRecord = commUtils.addSndData(sndRecord, recInTemp);	
								
								
					szMsg="[" + szOperationName + "] 차량도착위치[" + szYD_CARLD_LEV_LOC + "] - 차량입동지시요구 모듈을 호출 성공";
					commUtils.printLog(logId, szMsg, "SL");
			
		    	}//end of if
	    	}
    	
		}catch(Exception e){
			szMsg="출하차량출발실적 처리 Error:" +e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
			throw new DAOException(e);
		}

		szMsg="출하차량출발실적 처리("+szMethodName+") 완료";
		commUtils.printLog(logId, methodNm +szMsg, "S-");	
		return sndRecord;
	}// end of procOutCarLevWr()	
	
	
	
	/**
	 * 오퍼레이션명 : 차량포인트 통합관리 (기존형태 유지)
 	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */     	
	public boolean YsCarPointinforeg2(String chk 
									, String s_CAR_NO
									, String s_TRN_EQP_CD
									, String s_STACK_COL_GP
									, String szARR_WLOC_CD 
									, String szARR_YD_PNT_CD
									, String s_STAT
									, String logId
									, String mthdNm
									)throws DAOException {
		
		boolean isSuccess = false;
		int iSeq=0;
		String stkQueryId ="";
		String szMsg ="";
		YsPiDAO ysPiDAO = new YsPiDAO();
		String methodNm =  "[YsCommCarMvSeEJB.YsCarPointinforeg2] < " + mthdNm;
		
		try{

			commUtils.printLog(logId, methodNm, "S+");
			szMsg = "▣▣▣▣차량포인트 통합관리(START):"+chk+","+s_CAR_NO+","+s_TRN_EQP_CD+","+s_STACK_COL_GP+","+szARR_WLOC_CD+","+szARR_YD_PNT_CD+","+s_STAT+"▣▣▣▣▣" ;
	  		
	    		
    		szMsg =  "▣▣▣▣차량포인트 통합관리(START):통합작업 시작 ▣▣▣▣▣";
			commUtils.printLog(logId, szMsg, "ST");
			if(chk.equals("1")){
				//설비코드로 초기화 하는 경우(구내운송)			 			
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.carpointtrneqpcdupdate
				UPDATE USRYSA.TB_YS_CARPOINT
				   SET TRN_EQP_CD=null
				     , YD_STK_COL_ACT_STAT=DECODE(CARD_NO,NULL,:V_STAT,YD_STK_COL_ACT_STAT)
				     , MOD_DDTT=sysdate
				     , MODIFIER='CarPointin'
				 WHERE TRN_EQP_CD=:V_TRN_EQP_CD
				   AND MOD_DDTT<>sysdate
				*/
				stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointtrneqpcdupdate";
				iSeq = ysPiDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_TRN_EQP_CD});
			}else if(chk.equals("2")){
				//저장위치로 초기화 하는 경우(구내운송)
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.carpointstackcolgpupdateCT
				UPDATE USRYSA.TB_YS_CARPOINT
				   SET TRN_EQP_CD=null
				     , YD_STK_COL_ACT_STAT=DECODE(CARD_NO,NULL,:V_STAT,YD_STK_COL_ACT_STAT)
				     , MOD_DDTT=sysdate
				     , MODIFIER='CarPointCT'
				 WHERE YD_STK_COL_GP=:V_YS_STK_COL_GP 
				*/ 
				stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointstackcolgpupdateCT";
				 iSeq = ysPiDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_STACK_COL_GP});
			}else if(chk.equals("3")){
				//저장위치로 차량 포인트 예약 하는 경우(구내운송)
				 /* com.inisteel.cim.ys.common.dao.YsCommDAO.carpointtrneqpcdupdateC
				UPDATE USRYSA.TB_YS_CARPOINT
				   SET YD_STK_COL_ACT_STAT=:V_STAT
				     , TRN_EQP_CD =:V_TRN_EQP_CD
				     , MOD_DDTT=sysdate
				     , MODIFIER='CarPointCP'
				 WHERE YD_STK_COL_GP=:V_YS_STK_COL_GP
				*/ 
				stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointtrneqpcdupdateC";
				 iSeq = ysPiDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_TRN_EQP_CD,s_STACK_COL_GP});
			} else if(chk.equals("4")){
				//개소코드,포인트로 차량 포인트 예약 하는 경우(구내운송)
				 /* com.inisteel.cim.ys.common.dao.YsCommDAO.carpointWlocpntupdate
				UPDATE USRYSA.TB_YS_CARPOINT
				   SET YD_STK_COL_ACT_STAT=:V_STAT
				     , TRN_EQP_CD = :V_TRN_EQP_CD
				     , MOD_DDTT=sysdate
				     , MODIFIER='CarPointin'
				 WHERE WLOC_CD=:V_WLOC_CD
				   AND YD_PNT_CD=:V_YD_PNT_CD
				*/   
				stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointWlocpntupdate";
				 iSeq = ysPiDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_TRN_EQP_CD,szARR_WLOC_CD,szARR_YD_PNT_CD});
			}else if(chk.equals("A")){
				//설비코드로 초기화 하는 경우(출하)			 		
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.carpointtrneqpcdupdatePT
				UPDATE USRYSA.TB_YS_CARPOINT
				  SET   CARD_NO=NULL
				    ,   CAR_NO=NULL
				    ,   YD_STK_COL_ACT_STAT=:V_STAT
				    ,   MOD_DDTT=sysdate
				    ,   MODIFIER='CarPointPT'
				 WHERE CARD_NO=:V_TRN_EQP_CD   
				*/   
				stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointtrneqpcdupdatePT";
				 iSeq = ysPiDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_TRN_EQP_CD});
			}else if(chk.equals("B")){
				//저장위치로 초기화 하는 경우(출하)
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.carpointstackcolgpupdateC
				UPDATE USRYSA.TB_YS_CARPOINT
				   SET CARD_NO=null
				     , CAR_NO=NULL
				     , YD_STK_COL_ACT_STAT=DECODE(TRN_EQP_CD,NULL,:V_STAT,YD_STK_COL_ACT_STAT)
				     , MOD_DDTT=sysdate
				     , MODIFIER='CarPointC'
				 WHERE YD_STK_COL_GP=:V_YS_STK_COL_GP 
				*/ 
				stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointstackcolgpupdateC";
				 iSeq = ysPiDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_STACK_COL_GP});
			}else if(chk.equals("C")){
				//저장위치로 차량 포인트 예약 하는 경우(출하)
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.carpointtrneqpcdupdateC2
	    		UPDATE USRYSA.TB_YS_CARPOINT
	    		   SET YD_STK_COL_ACT_STAT=:V_STAT
	    		     , CAR_NO  =:V_CAR_NO
	    		     , CARD_NO =:V_TRN_EQP_CD
	    		     , MOD_DDTT=sysdate
	    		     , MODIFIER='CarPointC'
	    		 WHERE YD_STK_COL_GP=:V_YS_STK_COL_GP
	    		*/ 
				stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointtrneqpcdupdateC2";
				 iSeq = ysPiDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_CAR_NO ,s_TRN_EQP_CD,s_STACK_COL_GP});
			} else if(chk.equals("D")){
				 /* com.inisteel.cim.ys.common.dao.YsCommDAO.carpointWlocpntupdatePT
				UPDATE USRYSA.TB_YS_CARPOINT
				   SET YD_STK_COL_ACT_STAT=:V_STAT
				     , CAR_NO  =:V_CAR_NO
				     , CARD_NO = :V_TRN_EQP_CD
				     , MOD_DDTT=sysdate
				     , MODIFIER='CarPointPT'
				 WHERE WLOC_CD=:V_WLOC_CD
				   AND YD_PNT_CD=:V_YD_PNT_CD
				 */  
				
				
				//개소코드,포인트로 차량 포인트 예약 하는 경우(출하)
				stkQueryId = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointWlocpntupdatePT";
				 iSeq = ysPiDAO.requestupdateData(stkQueryId, new Object[]{ s_STAT,s_CAR_NO, s_TRN_EQP_CD,szARR_WLOC_CD,szARR_YD_PNT_CD});
			}  
	 
	    	szMsg =  "▣▣▣▣차량포인트 통합관리(END)COUNT:"+iSeq+"▣▣▣▣▣";
			isSuccess = true;
			commUtils.printLog(logId, methodNm, "S-");	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}		
	/**
	 * 오퍼레이션명 : 소재차량상차작업등록: procYdWbookForCarLdC--> 삭제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */	
	public JDTORecord procYdWbookForCarLd(JDTORecord msgRecord) throws JDTOException {
		String methodNm = "차량상차작업등록[YsCommCarMvSeEJB.procYdWbookForCarLd] < " + msgRecord.getResultMsg();
		String logId = msgRecord.getResultCode();

		int intRtnVal			= -100;
		String szMsg			= null;
		String szOperationName  = " ";
		//레코드 선언
		JDTORecord recPara     			= null;
		JDTORecord recOutPara  			=  JDTORecordFactory.getInstance().create();
		JDTORecord rtnOutPara  			=  JDTORecordFactory.getInstance().create();
		
		//레코드셋 선언
		JDTORecordSet rsResult 			= null;
		String szREG_MOD_USER			= "ForCarLdC";
		String szTRN_EQP_CD        		= null;
		String szYD_CAR_SCH_ID			= null;
		String szYD_SCH_CD         		= null;
		
		String szYD_PREP_SCH_ID			= null;
		String szYD_WBOOK_ID			= null;
		String szSSTL_NO 				= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYS_STK_COL_GP     		= null;
		String szYS_STK_BED_NO			= null;
		String szYS_STK_LYR_NO			= null;
		String szYS_STK_SEQ_NO			= null;
		String szYD_AIM_YD_GP			= null;
		String szYD_AIM_BAY_GP			= null;
		String szYD_SCH_PRIOR			= null;
		String szYD_PNT_CD				= null;
		
		//리턴값
		
		try {
			
			commUtils.printLog(logId, methodNm+szMsg, "S+");
			
			
			szTRN_EQP_CD = commUtils.trim(msgRecord.getFieldString("TRN_EQP_CD"       )); //운송장비코드
			if(szTRN_EQP_CD.equals("")){
				szMsg = "["+szOperationName+"] 운송장비코드가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				return rtnOutPara;
			}
			szYD_PNT_CD = commUtils.trim(msgRecord.getFieldString("YD_PNT_CD"       )); 
			if(szYD_PNT_CD.equals("")){
				szMsg = "["+szOperationName+"] 도착포인트코드가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				return recOutPara;
			}
			szYD_CAR_SCH_ID = commUtils.trim(msgRecord.getFieldString("YD_CAR_SCH_ID" )); 
			if(szYD_CAR_SCH_ID.equals("")){
				szMsg = "["+szOperationName+"] 차량스케줄ID가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				return rtnOutPara;
			}
			
			szYD_SCH_CD = commUtils.trim(msgRecord.getFieldString("YD_SCH_CD"       )); 
			if(szYD_SCH_CD.equals("")){
				szMsg = "["+szOperationName+"] 스케줄코드가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				return rtnOutPara;
			}
			
			//-------------------------------------------------------------------------------------------------
			
			
			//-------------------------------------------------------------------------------------------------
			//	도착포인트에 해당하는 준비스케줄이 존재 하는 경우 조회
			//-------------------------------------------------------------------------------------------------
			szMsg = "["+szOperationName+"] ★★★도착포인트에 해당하는 준비스케줄이 존재 하는 경우 조회 시작★★★";
			commUtils.printLog(logId, szMsg, "SL");
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_GP", 			szYD_SCH_CD.substring(0, 1));
			recPara.setField("YD_SCH_CD", 		szYD_SCH_CD.substring(0, 1) + "_TR");
			recPara.setField("YD_WRK_PLAN_CRN", "");
			recPara.setField("YD_PREP_WK_ST", 	"L");
			recPara.setField("YD_PNT_CD", 	szYD_PNT_CD);
 			recPara.setField("CAR_GP", 	szTRN_EQP_CD.substring(1, 2));
			
			 /* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStockNPrepSchByYdCrnCarGp
			 -- 가장빠른 상차 LOT
			 WITH TEMP_TABLE1 AS (
			    SELECT *
			      FROM USRYSA.TB_YS_PREPSCH A
			     WHERE YD_GP = :V_YD_GP
			       AND YD_SCH_CD LIKE :V_YD_SCH_CD || '%'
			       AND YD_PREP_WK_ST LIKE :V_YD_PREP_WK_ST || '%'
			       AND NVL(YD_WRK_PLAN_CRN, '*') LIKE :V_YD_WRK_PLAN_CRN || '%'
			       AND NVL(CAR_GP, '*') LIKE :V_CAR_GP || '%'
			       AND SUBSTR(YD_SCH_CD,2,1) =SUBSTR(:V_YD_PNT_CD,2,1)
			       AND A.DEL_YN = 'N'
			     ORDER BY YD_CARASGN_SEQ, A.YD_PREP_SCH_ID
			), TEMP_TABLE2 AS (
			SELECT A.SSTL_NO       AS SSTL_NO
			     -- ,A.YD_AIM_RT_GP  AS YD_AIM_RT_GP
			      ,A.YS_MTL_ITEM   AS YS_MTL_ITEM
			      ,A.YD_MTL_L      AS YD_MTL_L
			      ,A.YD_MTL_W      AS YD_MTL_W
			      ,A.YD_MTL_WT     AS YD_MTL_WT
			      ,B.YD_PREP_SCH_ID
			      ,B.YD_SCH_CD
			      ,B.YD_GP 
			      ,B.YD_PREP_WK_ST
			      ,B.YD_TO_LOC_DCSN_MTD
			      ,B.YD_TO_LOC_GUIDE
			      ,B.ARR_WLOC_CD
			      ,B.YD_AIM_YD_GP
			      ,B.YD_AIM_BAY_GP
			      ,B.YD_CARASGN_SEQ
			      ,B.YD_EQP_WRK_SH
			      ,B.YD_WRK_PLAN_CRN
			      ,B.YS_STK_COL_GP AS YS_STK_COL_GP
			      ,B.YS_STK_BED_NO AS YS_STK_BED_NO
			      ,B.YS_STK_LYR_NO AS YS_STK_LYR_NO
			      ,B.YS_STK_SEQ_NO AS YS_STK_SEQ_NO
			  FROM USRYSA.TB_YS_STOCK  A
			      , (
			      SELECT A.YD_PREP_SCH_ID
			            ,A.YD_SCH_CD
			            ,A.YD_GP 
			            ,A.YD_PREP_WK_ST
			            ,A.YD_TO_LOC_DCSN_MTD
			            ,A.YD_TO_LOC_GUIDE
			            ,A.ARR_WLOC_CD
			            ,A.YD_AIM_YD_GP
			            ,A.YD_AIM_BAY_GP
			            ,A.YD_CARASGN_SEQ
			            ,A.YD_EQP_WRK_SH
			            ,A.YD_WRK_PLAN_CRN
			            ,B.SSTL_NO
			            ,B.YS_STK_COL_GP AS YS_STK_COL_GP
			            ,B.YS_STK_BED_NO AS YS_STK_BED_NO
			            ,B.YS_STK_LYR_NO AS YS_STK_LYR_NO
			            ,B.YS_STK_SEQ_NO AS YS_STK_SEQ_NO
			        FROM TEMP_TABLE1 A
			            ,TB_YS_PREPMTL B
			            ,TB_YS_STKLYR C
			            ,TB_YS_CARPOINT D
			        WHERE A.YD_PREP_SCH_ID = B.YD_PREP_SCH_ID
			         AND B.SSTL_NO=C.SSTL_NO
			         AND C.YD_STK_LYR_MTL_STAT='C'
			         AND B.DEL_YN = 'N'
			         AND D.YD_GP ='A'
			         AND D.YD_PNT_CD=:V_YD_PNT_CD
			--         AND SUBSTR(C.YS_STK_COL_GP,3,2) BETWEEN  D.YD_SPAN_FROM AND D.YD_SPAN_TO
			         ORDER BY YD_CARASGN_SEQ ASC, YD_PREP_SCH_ID ASC
			      ) B
			 WHERE A.SSTL_NO = B.SSTL_NO
			   AND A.DEL_YN = 'N'
			 )
			 SELECT *
			   FROM TEMP_TABLE2 B
			  WHERE YD_PREP_SCH_ID=(SELECT *
			                          FROM 
			                               (SELECT YD_PREP_SCH_ID 
			                                  FROM TEMP_TABLE2 
			                                 ORDER BY YD_CARASGN_SEQ ASC, YD_PREP_SCH_ID ASC)
			                         WHERE ROWNUM<=1)
			ORDER BY B.YS_STK_COL_GP ASC,B.YS_STK_BED_NO DESC,B.YS_STK_LYR_NO DESC
			*/
			
			rsResult = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStockNPrepSchByYdCrnCarGp", logId, methodNm, "적치열 조회"); 
	    	
	    	if (rsResult == null || rsResult.size() <= 0) {
	    		szMsg = "["+szOperationName+"] 배차순서가 빠르고 준비스케줄ID가 빠른 차량 이송 대상재를 준비스케줄에서 조회 시 오류발생 - 메세지 : " + "-1";
	    		commUtils.printLog(logId, szMsg, "SL");
	    		return rtnOutPara;
	    	}			
			//-------------------------------------------------------------------------------------------------
			//	작업예약/작업예약재료 등록
			//-------------------------------------------------------------------------------------------------
			for(int i = 1; i <= rsResult.size(); i++ ) {
				rsResult.absolute(i);
				recPara			= rsResult.getRecord();
				szSSTL_NO				= commUtils.trim(recPara.getFieldString("SSTL_NO" ));				
				szYD_GP					= szYD_SCH_CD.substring(0, 1);
				szYD_BAY_GP				= szYD_SCH_CD.substring(1, 2);
				szYD_AIM_YD_GP			= commUtils.trim(recPara.getFieldString("YD_AIM_YD_GP" ));
				szYD_AIM_BAY_GP			= commUtils.trim(recPara.getFieldString("YD_AIM_BAY_GP" ));
				szYS_STK_COL_GP			= commUtils.trim(recPara.getFieldString("YS_STK_COL_GP" ));
				szYS_STK_BED_NO			= commUtils.trim(recPara.getFieldString("YS_STK_BED_NO" ));
				szYS_STK_LYR_NO			= commUtils.trim(recPara.getFieldString("YS_STK_LYR_NO" ));
				szYS_STK_SEQ_NO			= commUtils.trim(recPara.getFieldString("YS_STK_SEQ_NO" ));
				
				if( i == 1 ) {
					szYD_PREP_SCH_ID	= commUtils.trim(recPara.getFieldString("YD_PREP_SCH_ID" ));
					//-------------------------------------------------------------------------------------------------
					//	스케줄코드 조회
					//-------------------------------------------------------------------------------------------------
			    	//스케줄코드로 스케줄기준Table조회
					szMsg="스케줄코드로 스케줄기준Table조회";
					commUtils.printLog(logId, szMsg, "SL");
					JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YD_SCH_CD", szYD_SCH_CD);
			    	
			    	//스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.
					szMsg="스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.";
					commUtils.printLog(logId, szMsg, "SL");
					
					// rsResult 지역변수가 For문 반복시마다  초기화 됨 변수명 변경 rsResult -> rsResult2(아래 참조하는 부분 포함) - wc
					JDTORecordSet rsResult2 = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    	/*  com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule 
			    	SELECT A.YD_GP
			    	      ,A.YD_BAY_GP
			    	      ,YD_SCH_CD
			    	      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN1
			    	            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN2
			    	        END AS YD_WRK_CRN
			    	      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN_PRIOR1
			    	            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN_PRIOR2
			    	        END AS YD_WRK_CRN_PRIOR
			    	      ,YD_SCH_CD_NM
			    	      ,YD_SCH_CONTENTS
			    	      ,YD_SCH_PROH_EXN 
			    	    FROM TB_YS_SCHRULE A
			    	        ,(
			    	            SELECT YD_GP
			    	                  ,YD_BAY_GP
			    	                  ,YD_SCH_GP
			    	                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 1, YD_EQP_STAT, '') ELSE 'O' END), 'X') AS STAT1 
			    	                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 2, YD_EQP_STAT, '') ELSE 'X' END), 'X') AS STAT2 
			    	            FROM   (
			    	                        SELECT YD_EQP_ID
			    	                              ,YD_GP
			    	                              ,YD_BAY_GP
			    	                              ,DECODE(YD_EQP_STAT,'B','C','O') AS YD_EQP_STAT
			    	                              ,SUBSTR(YD_EQP_NO,-1) AS CRN_NO 
			    	                              ,YD_EQP_GP AS YD_SCH_GP
			    	                        FROM   TB_YS_EQP
			    	                        WHERE  YD_EQP_GP IN ('CR','SC')
			    	                   )
			    	            GROUP BY YD_GP, YD_BAY_GP , YD_SCH_GP             
			    	         ) B
			    	    WHERE A.YD_SCH_CD LIKE :V_YD_SCH_CD || '%'
			    	    AND   A.YD_DATA_GP = 'M'
			    	    AND   A.YD_SCH_GP = B.YD_SCH_GP
			    	    AND   A.YD_GP = B.YD_GP
			    	    AND   A.YD_BAY_GP = B.YD_BAY_GP
			    	    AND   A.YD_CRN_STAT1 = B.STAT1
			    	    AND   A.YD_CRN_STAT2 = B.STAT2
			    	    ORDER BY A.YD_GP, A.YD_BAY_GP, A.YD_SCH_CD
			    	*/	    	 
					rsResult2 = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
			    	
			    	if (rsResult2 == null || rsResult2.size() <= 0) {
			    		szMsg = "["+methodNm+"] 스케줄 기준 조회 data not found";
						commUtils.printLog(logId, szMsg, "SL");
						return rtnOutPara;
					} else {
					
						//레코드 추출
						rsResult2.first();
						recPara = rsResult2.getRecord();
						szYD_SCH_PRIOR = commUtils.trim(recPara.getFieldString("YD_WRK_CRN_PRIOR"       ));
					
					}
					//-------------------------------------------------------------------------------------------------
					
					//-------------------------------------------------------------------------------------------------
					//	작업예약 등록
					//-------------------------------------------------------------------------------------------------
					
					//com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdWrkBook
					szYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
					
					if ("".equals(szYD_WBOOK_ID)) {
						commUtils.printLog(logId, "작업예약ID 생성 실패", "SL");
						return recOutPara;
					}

					//작업예약항목SETTING
					recOutPara = JDTORecordFactory.getInstance().create();
					
					recOutPara.setField("YD_WBOOK_ID", 			szYD_WBOOK_ID);
					recOutPara.setField("REGISTER", 			szREG_MOD_USER);
					recOutPara.setField("YD_GP", 				szYD_GP);
					recOutPara.setField("YD_BAY_GP", 			szYD_BAY_GP);
					recOutPara.setField("YD_SCH_CD", 			szYD_SCH_CD);
					recOutPara.setField("YD_SCH_PRIOR", 		szYD_SCH_PRIOR);
					recOutPara.setField("YD_AIM_YD_GP", 		szYD_AIM_YD_GP);
					recOutPara.setField("YD_AIM_BAY_GP", 		szYD_AIM_BAY_GP);
					recOutPara.setField("YD_CAR_USE_GP", 		"L");  //구내운송
					recOutPara.setField("TRN_EQP_CD", 			szTRN_EQP_CD);
					
					szMsg = "["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"]등록 시작";
					commUtils.printLog(logId, szMsg, "SL");


					//com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook
					commDao.insert(recOutPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
					
					szMsg = "["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"]등록 완료";
					commUtils.printLog(logId, szMsg, "SL");
					
					//-------------------------------------------------------------------------------------------------
				}
				
				//-------------------------------------------------------------------------------------------------
				//	작업예약재료 등록
				//-------------------------------------------------------------------------------------------------
				
				recOutPara = JDTORecordFactory.getInstance().create();
				
				recOutPara.setField("YD_WBOOK_ID", 				szYD_WBOOK_ID);
				recOutPara.setField("REGISTER", 				szREG_MOD_USER);
				recOutPara.setField("SSTL_NO", 					szSSTL_NO);
				recOutPara.setField("YS_STK_COL_GP", 			szYS_STK_COL_GP);
				recOutPara.setField("YS_STK_BED_NO", 			szYS_STK_BED_NO);
				recOutPara.setField("YS_STK_LYR_NO", 			szYS_STK_LYR_NO);
				recOutPara.setField("YS_STK_SEQ_NO", 			szYS_STK_SEQ_NO);
				recOutPara.setField("YD_UP_COLL_SEQ", 			String.valueOf(i));
			
		   		commDao.insert(recOutPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
	    		if(intRtnVal == -2) {
	    			szMsg = "["+methodNm+"] insYdWrkbookmtl parameter error";
					commUtils.printLog(logId, szMsg, "SL");
					return rtnOutPara;
	    		}
				
				
				szMsg = "["+szOperationName+"] ["+i+"] 작업예약["+szYD_WBOOK_ID+"]의 작업재료["+szSSTL_NO+"] 등록 완료";
				commUtils.printLog(logId, szMsg, "SL");
				//-------------------------------------------------------------------------------------------------
			}
			
			//-------------------------------------------------------------------------------------------------
			//	준비스케줄 삭제
			//-------------------------------------------------------------------------------------------------
			
			szMsg = "["+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 시작";
			commUtils.printLog(logId, szMsg, "SL");
			recOutPara = JDTORecordFactory.getInstance().create();
			
			recOutPara.setField("YD_PREP_SCH_ID", 	szYD_PREP_SCH_ID);
			recOutPara.setField("MODIFIER", 		szREG_MOD_USER);
			recOutPara.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
			recOutPara.setField("DEL_YN", 			"Y");
			
			/*com.inisteel.cim.ys.common.dao.YsCommDAO.delYdPrepmtlByPrepSchIdYN
			UPDATE USRYSA.TB_YS_PREPMTL
			   SET MODIFIER = :V_MODIFIER
			     , MOD_DDTT = SYSDATE
			     , DEL_YN = :V_DEL_YN
			WHERE YD_PREP_SCH_ID = REPLACE(:V_YD_PREP_SCH_ID,' ','')
			*/
			
			commDao.update(recOutPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.delYdPrepmtlByPrepSchIdYN", logId, methodNm, "준비작업 재료 삭제");
			
			// WC - 준비스케줄 삭제
			commDao.update(recOutPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.delYdPrepSch", logId, methodNm, "준비스케줄 삭제");

			szMsg = "["+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 완료 - 메세지 : " ;
			commUtils.printLog(logId, szMsg, "SL");
			
			//-------------------------------------------------------------------------------------------------
			//	차량스케줄에 상차작업예약 등록
			//-------------------------------------------------------------------------------------------------
			recOutPara = JDTORecordFactory.getInstance().create();
			
			recOutPara.setField("YD_CAR_SCH_ID", 				szYD_CAR_SCH_ID);
			recOutPara.setField("YD_CARLD_WRK_BOOK_ID", 		szYD_WBOOK_ID);
			recOutPara.setField("MODIFIER", 					szREG_MOD_USER);
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarSchLDWrkBook
			UPDATE USRYSA.TB_YS_CARSCH
			   SET MODIFIER = :V_MODIFIER
			     , MOD_DDTT = SYSDATE
			     , YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID
			WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			*/
			
	    	intRtnVal = commDao.update(recOutPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarSchLDWrkBook", logId, methodNm, "TB_YS_CARSCH 등록");
			if(intRtnVal <= 0) {
				szMsg="["+methodNm+"] 차량스케줄 업데이트 시 이상.";
				commUtils.printLog(logId, methodNm, "SL");
			}	

			rtnOutPara.setField("YD_WBOOK_ID", 				szYD_WBOOK_ID);
			rtnOutPara.setField("YD_SCH_CD", 				szYD_SCH_CD);

			commUtils.printLog(logId, methodNm, "S-");
			return rtnOutPara;

		} catch(Exception e){
			
			szMsg="소재차량 도착 Point요구 처리 Error:" +e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
			m_ctx.setRollbackOnly();
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
		
	/**
	 * 오퍼레이션명 : 차량입동지시요구(YSYSJ801)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procCarBayInOrdReq(JDTORecord rcvMsg)throws JDTOException  {
		/*
		 * 업무기준 : 1. 해당하는 차량정지point[파라미터로 전달됨-필수]가 사용가능한 지를 확인한다.
		 * 		  2. 해당하는 차량정지point[파라미터로 전달됨-필수]가 사용가능하면 대기중인 차량스케줄들중에서 입동순서가 가장빠른 차량스케줄을 하나 조회하고
		 * 		 	   해당 차량스케줄이 구내운송용이면 출하관리로 입동지시를 보내지 않고 출하차량이면 입동지시전문에 Y로 설정해서 전송한다.
		 * 		  3. 조회된 차량스케줄ID와 파라미터로 전달된 차량스케줄ID[파라미터로전달됨 - 선택]가 다른 경우에는 전달된 차량스케줄ID로 조회를 해서
		 * 			  입동지시전문에 N으로 설정해서 전송한다.
		 * 모듈호출경로 : 1. 상차지시 실적 처리 후
		 * 		    2. 출하차량 출발 처리 후
		 * 			3. 구내운송차량 출발 처리 후
		 * 			4. 화면에서 백업용으로 호출
		 */
		//기본변수 정의
		
		String methodNms = "차량입동지시요구 수신[YsCommCarMvSeEJB.procCarBayInOrdReq] < " + rcvMsg.getResultMsg();
		String methodNm = "차량입동지시요구 수신[YsCommCarMvSeEJB.procCarBayInOrdReq]";
		String logId = rcvMsg.getResultCode();
		String szMsg = "";
		
		JDTORecord 	sndRecord		= JDTORecordFactory.getInstance().create();
		String szYD_CAR_STOP_LOC 	= StringHelper.evl(rcvMsg.getFieldString("YD_CAR_STOP_LOC"), "");				//차량정지위치[적치열] - 필수항목
		String szYD_CAR_SCH_ID 		= StringHelper.evl(rcvMsg.getFieldString("YD_CAR_SCH_ID"), "");					//차량스케줄ID - 선택항목 
		String szCALL_PGM 			= StringHelper.evl(rcvMsg.getFieldString("CALL_PGM"), "SANGCHA"); 				//상차지시시 전문인지 CHECK 
		
		boolean isCarArrAble 		= false;
		
		JDTORecordSet 	rsCarSch 	= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord 		recCarSch 	= JDTORecordFactory.getInstance().create();
		JDTORecord 		recPara 	= JDTORecordFactory.getInstance().create();
		
		try {
			
			commUtils.printLog(logId, methodNms, "S+");
			
			commUtils.printParam(logId + "차량입동지시요구 수신 PARA ", rcvMsg);

			commUtils.printLog(logId, "["+ methodNm +"] 메소드 시작 - 파라미터 확인 : 차량정지위치[" + szYD_CAR_STOP_LOC + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "]", "SL");
			
			if( szYD_CAR_STOP_LOC.equals("") ) {
				szMsg="["+ methodNm +"] 파라미터 확인 : 차량정지위치가 존재하지 않습니다.";
				commUtils.printLog(logId, szMsg, "SL");	
				sndRecord.setTaskCode("-1");
				return sndRecord;
			}

			// PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "K", "*");
			
			//-----------------------------------------------------------------------------------------------------------------------
			// 1. 해당하는 차량정지point[파라미터로 전달됨-필수]가 사용가능한 지를 확인한다.
			//-----------------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, "["+ methodNm +"] 차량정지위치[" + szYD_CAR_STOP_LOC + "]의 사용가능 여부를 확인 시작", "SL");
			
			JDTORecordSet rsStkCol = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord   recInTemp = JDTORecordFactory.getInstance().create();
			JDTORecord   recStkCol = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YS_STK_COL_GP", szYD_CAR_STOP_LOC);
			
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
    		rsStkCol = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkCol1", logId, methodNm, "적치열 조회"); 
	    	
	    	if (rsStkCol == null || rsStkCol.size() <= 0) {
	    		szMsg= "["+ methodNm +"] 차량정지위치[" + szYD_CAR_STOP_LOC + "]의 사용가능 여부를 확인 시 적치열이 존재하지 않습니다.";
				commUtils.printLog(logId, szMsg, "SL");			 
				sndRecord.setTaskCode("-1");
				return sndRecord;
			}
	     
	    	rsStkCol.first(); 
	    	recStkCol					= rsStkCol.getRecord();
			szYD_CAR_STOP_LOC			= StringHelper.evl(recStkCol.getFieldString("YS_STK_COL_GP"), "");
			String szYD_STK_COL_ACT_STAT= StringHelper.evl(recStkCol.getFieldString("YD_STK_COL_ACT_STAT"), ""); 			//야드적치열활성상태
			String szWLOC_CD 			= StringHelper.evl(recStkCol.getFieldString("WLOC_CD"), ""); 
			String szYD_PNT_CD 			= StringHelper.evl(recStkCol.getFieldString("YD_PNT_CD"), ""); 
			String szYD_STKBED_USG_CD	= StringHelper.evl(recStkCol.getFieldString("YD_STKBED_USG_CD"), "TR"); 
	    	
			String szCAR_NO2 			= "";
			String szCARD_NO2 			= "";
			String szTRANS_ORD_DATE2 	= "";
			String szTRANS_ORD_SEQNO2 	= "";
			String szYD_CAR_PROG_STAT2	= "";
			
			szMsg= "["+ methodNm +"] 차량정지위치(적치열)[" + szYD_CAR_STOP_LOC +"포인트" + szYD_PNT_CD + " 용도:"+szYD_STKBED_USG_CD+ "]의 야드적치열활성상태값 [" + szYD_STK_COL_ACT_STAT + "]";
			commUtils.printLog(logId, szMsg, "SL");			 
			
			//구내운송 상차완료 이후에는 차량입동처리를 안한다.
			if(szYD_STKBED_USG_CD.equals("GT")){
				sndRecord.setTaskCode("1");
				return sndRecord;
			}
			//-----------------------------------------------------------------------------------------------------------------------
			
			//-----------------------------------------------------------------------------------------------------------------------
			//비활성상태이면 입동지시를 전송한다.
			//-----------------------------------------------------------------------------------------------------------------------
			if( "TR".equals(szYD_STKBED_USG_CD) && "C".equals(szYD_STK_COL_ACT_STAT)) {
	 
				//------------------------------------------------------------------------------------------------------------
				
				//-----------------------------------------------------------------------------------------------------------------------
				// 2. 입동지시순서 목록 조회
				//-----------------------------------------------------------------------------------------------------------------------
				szMsg= "["+ methodNm +"] 입동지시순서 목록 조회 시작 - 차량정지위치[" + szYD_CAR_STOP_LOC + "]";
				commUtils.printLog(logId, szMsg, "SL");	
				
				rsCarSch = JDTORecordFactory.getInstance().createRecordSet("");
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CAR_STOP_LOC", szYD_CAR_STOP_LOC);
 				
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchByBayInSeq
				SELECT YD_CAR_SCH_ID
				    , YD_CAR_USE_GP
				    , CAR_NO
				    , CARD_NO
				    , TRN_EQP_CD
				    , CAR_KIND
				    , SPOS_WLOC_CD
				    , ARR_WLOC_CD
				    , YD_PNT_CD1
				    , YD_PNT_CD2
				    , YD_CARLD_WRK_BOOK_ID
				    , YD_CARLD_STOP_LOC
				    , YD_CAR_PROG_STAT
				    , TRANS_ORD_DATE
				    , TRANS_ORD_SEQNO
				    , CASE WHEN YD_CAR_PROG_STAT = '1' THEN YD_CARLD_STOP_LOC WHEN YD_CAR_PROG_STAT = 'A' THEN YD_CARUD_STOP_LOC END AS YD_STOP_LOC
				    , CASE WHEN YD_CAR_PROG_STAT = '1' THEN YD_CARLD_LEV_DT   WHEN YD_CAR_PROG_STAT = 'A' THEN YD_CARUD_LEV_DT END AS YD_CAR_LEV_DT
				    , NVL(YD_BAYIN_WO_SEQ, 9) AS YD_BAYIN_WO_SEQ
				    ,(SELECT YD_PNT_CD 
				       FROM USRYSA.TB_YS_STKCOL 
				      WHERE YS_STK_COL_GP=( CASE WHEN A.YD_CAR_PROG_STAT = '1' THEN A.YD_CARLD_STOP_LOC 
				                                 WHEN A.YD_CAR_PROG_STAT = 'A' THEN A.YD_CARUD_STOP_LOC 
				                            END )
				      ) AS YD_PNT_CD
				      ,(SELECT YD_CARPNT_CD
				         FROM USRYSA.TB_YS_CARPOINT B
				        WHERE B.YD_STK_COL_GP=A.YD_CARLD_STOP_LOC) AS YD_CARPNT_CD
				        
				FROM USRYSA.TB_YS_CARSCH A
				WHERE DEL_YN='N'
				AND ( CASE WHEN CAR.YD_CAR_PROG_STAT = '1' THEN CAR.YD_CARLD_STOP_LOC WHEN CAR.YD_CAR_PROG_STAT = 'A' THEN CAR.YD_CARUD_STOP_LOC END )
      				LIKE SUBSTR(:V_YD_CAR_STOP_LOC,1,5) || '%'
				ORDER BY YD_STOP_LOC ASC, YD_BAYIN_WO_SEQ ASC, YD_CAR_SCH_ID ASC
				*/
				rsCarSch = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchByBayInSeq", logId, methodNm, "차량포인트 조회"); 
				
				if(rsCarSch == null || rsCarSch.size() <= 0){
					szMsg= "["+ methodNm +"] 입동지시순서 목록 조회 시작 - 해당 포인트 ["+szYD_CAR_STOP_LOC+"] 입동대상 차량스케줄이 없습니다.]";
					commUtils.printLog(logId, szMsg, "SL");	
			 		sndRecord.setTaskCode("-1");
					return sndRecord;
				}
				
				String szNEW_YD_CAR_SCH_ID1		= "";
				String szNEW_YD_CAR_USE_GP 		= "";
				String szNEW_CAR_NO 			= "";
				String szNEW_CARD_NO 			= "";
				String szNEW_TRANS_ORD_DATE 	= "";
				String szNEW_TRANS_ORD_SEQNO 	= "";
				String szNEW_TRN_EQP_CD 		= "";
				String szNEW_CAR_KIND_GP		= "";
				String szNEW_YD_CARPNT_CD2		= "";
				String szNEW_YD_CAR_PROG_STAT	= "";
				String szNEW_CMBN_CARLD_YN		= "";
				String szTEL_NO					= "";
				
				// 입동 가능 차량 조회 (가장 빠른 순서, 각강/반입 검수 완료되지 않는 차량 제외)
				for (int ii = 0; ii < rsCarSch.size(); ii++) {
					recCarSch = rsCarSch.getRecord(ii);					
					
					// 반입/각강 검수 완료 체크
					JDTORecordSet resSet      = null;;
					JDTORecord    recIn       = JDTORecordFactory.getInstance().create();
					
					resSet = JDTORecordFactory.getInstance().createRecordSet("");
					recIn = JDTORecordFactory.getInstance().create();
					recIn.setField("TRN_EQP_CD", StringHelper.evl(recCarSch.getFieldString("CAR_NO"), ""));
					resSet = commDao.select(recIn, "com.inisteel.cim.ys.common.dao.YsCommDAO.getRtnStkOrSbStkCmpl"
							, logId, methodNm, "반입/각강 검수 완료 체크"); 
					if (resSet != null && resSet.size() > 0 ) {
						continue;
					}
					 
					// 각강 검수 완료 체크
					/*
					String szSPOS_WLOC_CD = StringHelper.evl(recCarSch.getFieldString("SPOS_WLOC_CD"), "");
		    		String szTRN_EQP_CD = StringHelper.evl(recCarSch.getFieldString("TRN_EQP_CD"), "");
		    		if((szSPOS_WLOC_CD.equals("S4Y20"))){
		    			JDTORecordSet resSet      = null;;
						JDTORecord    recIn       = JDTORecordFactory.getInstance().create();
						JDTORecord    recOut      = JDTORecordFactory.getInstance().create();
						
						resSet = JDTORecordFactory.getInstance().createRecordSet("");
						recIn = JDTORecordFactory.getInstance().create();
						recIn.setField("TRN_EQP_CD", szTRN_EQP_CD);
						resSet = commDao.select(recInTemp, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.getSbStkChkOKPda"
														, logId, methodNm, "각강 입고검수 완료"); 
						if (resSet != null && resSet.size() > 0 ) {
							resSet.absolute(1);
							  
					    	recOut = JDTORecordFactory.getInstance().create();
					    	recOut.setRecord(resSet.getRecord());
					  
					    	String sbComlCount  = commUtils.trim(recOut.getFieldString("COUNT")); 
					    	if (Integer.parseInt(sbComlCount) != resSet.size()) {
					    		// 각강 검수 미완료 차량 - 선택 X
					    		continue;
					    	}
						}
		    		}
		    		*/
					
					// 가장 빠른 순서 차량 선택
		    		szNEW_YD_CAR_SCH_ID1	= StringHelper.evl(recCarSch.getFieldString("YD_CAR_SCH_ID"), "");
					szNEW_YD_CAR_USE_GP 	= StringHelper.evl(recCarSch.getFieldString("YD_CAR_USE_GP"), "");
					szNEW_CAR_NO 			= StringHelper.evl(recCarSch.getFieldString("CAR_NO"), "");
					szNEW_CARD_NO 			= StringHelper.evl(recCarSch.getFieldString("CARD_NO"), "");
					szNEW_TRANS_ORD_DATE 	= StringHelper.evl(recCarSch.getFieldString("TRANS_ORD_DATE"), "");
					szNEW_TRANS_ORD_SEQNO 	= StringHelper.evl(recCarSch.getFieldString("TRANS_ORD_SEQNO"), "");
					szNEW_TRN_EQP_CD 		= StringHelper.evl(recCarSch.getFieldString("TRN_EQP_CD"), "");
					szNEW_CAR_KIND_GP		= StringHelper.evl(recCarSch.getFieldString("CAR_KIND_GP"), "");
					szNEW_YD_CARPNT_CD2		= StringHelper.evl(recCarSch.getFieldString("YD_CARPNT_CD"), "");
					szNEW_YD_CAR_PROG_STAT	= StringHelper.evl(recCarSch.getFieldString("YD_CAR_PROG_STAT"), "");
					szNEW_CMBN_CARLD_YN		= StringHelper.evl(recCarSch.getFieldString("CMBN_CARLD_YN"), "");
					szTEL_NO				= StringHelper.evl(recCarSch.getFieldString("DEST_TEL_NO"), "");
					break;
				}
				
				//-----------------------------------------------------------------------------------------------------------------------
				//입동순서가 가장빠른 차량이 구내운송인 경우에는 입동지시 전문을 전송하지 않는다.
				//입동순서가 가장빠른 출하차량인 경우에만 입동지시 전문을 전송한다.
				//-----------------------------------------------------------------------------------------------------------------------
				if( szNEW_YD_CAR_USE_GP.equals("G")) {
					
					szMsg= "입동순서가 가장빠른 차량이 출하차량[차량번호:" + szNEW_CAR_NO + ", 카드번호:" + szNEW_CARD_NO + ", 운송지시일자:" +szNEW_TRANS_ORD_DATE + ", 운송지시순번:" + szNEW_TRANS_ORD_SEQNO + "]이므로 입동지시 전문을 전송예정.";
					commUtils.printLog(logId, szMsg, "SL");	
					
					if(szCALL_PGM.equals("SANGCHA")) {                   // 상차지시 일경우에는 송신처리 함
						if("A".equals(szNEW_YD_CAR_PROG_STAT) ||"PT".equals(szYD_STKBED_USG_CD)) {
							// 입고차량인 경우 출하로 입동지시전문(YSDSJ005)을 전송하지 않는다
							szMsg="입고차량 입동지시 송신 안함" ;
							commUtils.printLog(logId, szMsg, "SL");										
						} else {
							recInTemp = JDTORecordFactory.getInstance().create();
							recInTemp.setResultCode(logId);	//Log ID
							recInTemp.setResultMsg(methodNm);	//Log Method Name
							
							// PIDEV
//							if("Y".equals(sApplyYnPI)) {
								recInTemp = JDTORecordFactory.getInstance().create();
								recInTemp.setField("MQ_TC_CD"				, "M10YDLMJ1064");
								recInTemp.setField("MQ_TC_CREATE_DDTT" 		, commUtils.getDateTime14());
								recInTemp.setField("TRN_REQ_DATE"			, szNEW_TRANS_ORD_DATE);
								recInTemp.setField("TRN_REQ_SEQ" 			, szNEW_TRANS_ORD_SEQNO);
								// recInTemp.setField("CARD_NO"				, szNEW_CARD_NO);
								recInTemp.setField("CAR_NO"					, szNEW_CAR_NO);
								recInTemp.setField("YD_GP"					, "K");
								recInTemp.setField("DIST_GOODS_GP"			, "R");
								recInTemp.setField("SCH_YN"					, "N");
								recInTemp.setField("BAYIN_DATE"				, commUtils.getDate8());
								recInTemp.setField("BAYIN_TIME"				, commUtils.getTime6()); 
								recInTemp.setField("YD_CARPNT_CD"			, szNEW_YD_CARPNT_CD2);	
								recInTemp.setField("LOAN_PULLOUT_ABLE_YN"  	, "Y");

								recInTemp.setField("SPST_FRTOMOVE_GP"  		, "1" ); //특수강 이송구분
								recInTemp.setField("BAYIN_WAIT_CNT"  		, "0" ); //입동대기카운트  
								/*
								 * 입동지시 대상차량에 입동지시 송신여부 셋팅
								 */
								recInTemp.setField("YD_CAR_RCPT_CHK_YN"   ,"C");	
								/*
								 * 입동지시 대상차량에 입동지시 송신여부 셋팅
								 */
								recInTemp.setField("YD_CAR_RCPT_CHK_YN"   ,"C");							

								/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarschYdCarRcptChkYn_PIDEV
				    			UPDATE TB_YS_CARSCH 
				    			   SET YD_CAR_RCPT_CHK_YN = :V_YD_CAR_RCPT_CHK_YN
				    			WHERE CAR_NO            = :V_CAR_NO
				    			  AND TRANS_ORD_DATE    = :V_TRN_REQ_DATE
				    			  AND TRANS_ORD_SEQNO   = :V_TRN_REQ_SEQ
				                */
								int nRet = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarschYdCarRcptChkYn_PIDEV", logId, methodNm, "차량스케줄 갱신");
								
								if(nRet <= 0) {
									throw new DAOException("차량스케줄 갱신오류");
								}	
								
//							} else {
//								recInTemp.setField("JMS_TC_CD"				, "YSDSJ005");
//								recInTemp.setField("JMS_TC_CREATE_DDTT" 	, commUtils.getDateTime14());
//								recInTemp.setField("TRANS_WORD_DATE"		, szNEW_TRANS_ORD_DATE);
//								recInTemp.setField("TRANS_WORD_SEQNO" 		, szNEW_TRANS_ORD_SEQNO);
//								recInTemp.setField("CARD_NO"				, szNEW_CARD_NO);
//								recInTemp.setField("CAR_NO"					, szNEW_CAR_NO);
//								recInTemp.setField("WLOC_CD"				, szWLOC_CD);
//								recInTemp.setField("YD_PNT_CD"				, szYD_PNT_CD);	
//								recInTemp.setField("YD_CARPNT_CD"			, szNEW_YD_CARPNT_CD2);	
//								recInTemp.setField("LOAN_PULLOUT_ABLE_YN"  	, "Y");
//								recInTemp.setField("SPST_FRTOMOVE_GP"  		, "1" ); //특수강 이송구분
//								recInTemp.setField("BAYIN_WAIT_CNT"  		, "0" ); //입동대기카운트  
//								/*
//								 * 입동지시 대상차량에 입동지시 송신여부 셋팅
//								 */
//								recInTemp.setField("YD_CAR_RCPT_CHK_YN"   ,"C");							
//
//								/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarschYdCarRcptChkYn_PIDEV
//				    			UPDATE TB_YS_CARSCH 
//				    			   SET YD_CAR_RCPT_CHK_YN = :V_YD_CAR_RCPT_CHK_YN
//				    			WHERE CAR_NO            = :V_CAR_NO
//				    			  AND TRANS_ORD_DATE    = :V_TRANS_WORD_DATE
//				    			  AND TRANS_ORD_SEQNO   = :V_TRANS_WORD_SEQNO
//				                */
//								int nRet = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarschYdCarRcptChkYn_PIDEV", logId, methodNm, "차량스케줄 갱신");
//								
//								if(nRet <= 0) {
//									throw new DAOException("차량스케줄 갱신오류");
//								}	
//							}
			    			
							//복수동 다음 작업 인 경우 야드에서 SMS문자를 보낸다.
							if("E".equals(szNEW_CMBN_CARLD_YN)){ //K1B2
								String sSmsMsg = szNEW_YD_CARPNT_CD2.substring(2,3)+"동 "+szNEW_YD_CARPNT_CD2.substring(1,2)+"통로 입동하세요.";
								
								szMsg= " 입동 SMS 문자내용:"+sSmsMsg;
								commUtils.printLog(logId, szMsg, "SL");								 
								/*
								JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
								recPara1.setResultCode(logId);	//Log ID
								recPara1.setResultMsg(methodNm);	//Log Method Name
								recPara1.setField("FROM_PHONE_NO", "0416806678");	
								recPara1.setField("TO_PHONE_NO"  , szTEL_NO);	
								recPara1.setField("TO_CONTENT"   , sSmsMsg);								

								this.updSmsMsgSend(recPara1); // SMS 송신
								*/
								MessageSenderTalk    sender = new MessageSenderTalk();
								
				 		    	JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
				 		    	recPara1.setField("PHONE_NUM"	, new String(szTEL_NO));
				 		    	recPara1.setField("TMPL_CD"		, new String("CM1"));
				 		    	recPara1.setField("SND_MSG"		, new String("[현대제철 공지사항]\n" + sSmsMsg));
				 		    	recPara1.setField("SUBJECT"		, new String("입동 알림"));
				 		    	recPara1.setField("SMS_SND_NUM"	, new String("0416806678"));
				 		    	recPara1.setField("RECV_ID"		,"1522110");
				 		    	recPara1.setField("GROUP_ID"	,"KaKao");
				 		    	recPara1.setField("PROGRAM_ID"	,"udttalk");
								sender.sendTalk(recPara1);
								/*
								MessageSenderTalk    sender2 = new MessageSenderTalk();
								
								JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
								recPara2.setField("PHONE_NUM"	, new String("01038433916"));
								recPara2.setField("TMPL_CD"		, new String("CM1"));
				 		    	recPara2.setField("SND_MSG"		, new String("[현대제철 공지사항]\n" + sSmsMsg+"["+szNEW_CAR_NO+"]" ));
				 		    	recPara2.setField("SUBJECT"		, new String("입동 알림"));
				 		    	recPara2.setField("SMS_SND_NUM"	, new String("0416806678"));
				 		    	recPara2.setField("RECV_ID"		,"1522110");
				 		    	recPara2.setField("GROUP_ID"	,"KaKao");
				 		    	recPara2.setField("PROGRAM_ID"	,"udttalk");
								sender2.sendTalk(recPara2);
								*/
								szMsg= "입동지시수신전화[" + szTEL_NO + "]에 대한 입동대기지시 SMS 전송";
								commUtils.printLog(logId, szMsg, "SL");	
								
							}else{
								sndRecord = commUtils.addSndData(sndRecord,recInTemp);	
							}
							
			    			szMsg= " 입동순서가 가장빠른 차량이 출하차량[차량번호:" + szNEW_CAR_NO + ", 카드번호:" + szNEW_CARD_NO + ", 운송지시일자:" + szNEW_TRANS_ORD_DATE + ", 운송지시순번:" + szNEW_TRANS_ORD_SEQNO + "]이므로 입동지시 전문전송 완료.";
							commUtils.printLog(logId, szMsg, "SL");										
							
						}
					} else{	
						commUtils.printLog(logId, "상차지시 가  아니므로 입동지시 송신 안함", "SL");										
					}					
					isCarArrAble = true; 
					
				}else{
					szMsg= "["+ methodNm +"] 입동순서가 가장빠른 차량이 구내운송차량[운송장비코드:" + szNEW_TRN_EQP_CD + "]이므로 입동지시 전문을 전송하지 않는다.";
					commUtils.printLog(logId, szMsg, "SL");										
					
				}
				//-----------------------------------------------------------------------------------------------------------------------
				
				
				//-----------------------------------------------------------------------------------------------------------------------
				//파라미터로 넘겨진 차량스케줄인 경우에만 확인해서 전송한다.
				//파라미터로 넘겨진 차량스케줄 입동지시 'N'
				//-----------------------------------------------------------------------------------------------------------------------
				if( !szYD_CAR_SCH_ID.equals("") ) {
					if( !szNEW_YD_CAR_SCH_ID1.equals(szYD_CAR_SCH_ID) ) {

						szMsg= "["+ methodNm +"] 파라미터로 넘겨진 차량스케줄ID[" + szYD_CAR_SCH_ID + "]로 차량스케줄을 조회해서 입동지시 전문을 전송한다.";
						commUtils.printLog(logId, szMsg, "SL");										
						
						
						rsCarSch = JDTORecordFactory.getInstance().createRecordSet("");
						
						recCarSch = JDTORecordFactory.getInstance().create();
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
						
						/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch
						SELECT * 
						FROM TB_YS_CARSCH C
						WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
						*/
						//차량스케쥴 조회
						rsCarSch = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch", logId, methodNm, "차량스케쥴 조회");
						
						if (rsCarSch.size() <= 0) {
							commUtils.printLog(logId, "차량스케줄이 없습니다. SKIP", "SL");
							sndRecord.setTaskCode("-1");
							return sndRecord;
						}							
						 
						
						rsCarSch.first();
						recCarSch = rsCarSch.getRecord();
						
						szCAR_NO2 			= StringHelper.evl(recCarSch.getFieldString("CAR_NO"), "");
						szCARD_NO2 			= StringHelper.evl(recCarSch.getFieldString("CARD_NO"), "");
						szTRANS_ORD_DATE2 	= StringHelper.evl(recCarSch.getFieldString("TRANS_ORD_DATE"), "");
						szTRANS_ORD_SEQNO2 	= StringHelper.evl(recCarSch.getFieldString("TRANS_ORD_SEQNO"), "");
						szYD_CAR_PROG_STAT2	= StringHelper.evl(recCarSch.getFieldString("YD_CAR_PROG_STAT"), "");
						
						//CHITO 2018.07.10 이재현 차장님 요청 : 상차완료 후 입동TC가 다시 나가는 경우에 대한 보완 
						if("A".equals(szYD_CAR_PROG_STAT2) || "5".equals(szYD_CAR_PROG_STAT2)) {
							// 입고차량인 경우 출하로 입동지시전문(YSDSJ005)을 전송하지 않는다. 
			    			szMsg= "파라미터로 넘겨진 차량스케줄[현재차량스케줄:" + szYD_CAR_SCH_ID + "]의 입고차량[차량번호:" + szCAR_NO2 + ", 카드번호:" + szCARD_NO2 + ", 운송지시일자:" + szTRANS_ORD_DATE2 + ", 운송지시순번:" + szTRANS_ORD_SEQNO2 + "]에 대해 입고차량임으로 입동지시 전문전송 안함.";
							commUtils.printLog(logId, szMsg, "SL");										
						} else {
							//입동지시전문전문(YSDSJ005) 전송
			    			szMsg= "파라미터로 넘겨진 차량스케줄[현재차량스케줄:" + szYD_CAR_SCH_ID + "]의 출하차량[차량번호:" + szCAR_NO2 + ", 카드번호:" + szCARD_NO2 + ", 운송지시일자:" + szTRANS_ORD_DATE2 + ", 운송지시순번:" + szTRANS_ORD_SEQNO2 + "]에 대해 출고차량임으로 입동지시 전문전송.";
							commUtils.printLog(logId, szMsg, "SL");										
							
							recInTemp = JDTORecordFactory.getInstance().create();
							recInTemp.setResultCode(logId);	//Log ID
							recInTemp.setResultMsg(methodNm);	//Log Method Name
							
							// PIDEV
//							if("Y".equals(sApplyYnPI)) {
								recInTemp = JDTORecordFactory.getInstance().create();
								recInTemp.setField("MQ_TC_CD"				, "M10YDLMJ1064");
								recInTemp.setField("MQ_TC_CREATE_DDTT" 		, commUtils.getDateTime14());
								recInTemp.setField("TRN_REQ_DATE"			, szTRANS_ORD_DATE2);
								recInTemp.setField("TRN_REQ_SEQ" 			, szTRANS_ORD_SEQNO2);
								// recInTemp.setField("CARD_NO"				, szCARD_NO2);
								recInTemp.setField("CAR_NO"					, szCAR_NO2);								
								recInTemp.setField("YD_GP"					, "K");
								recInTemp.setField("DIST_GOODS_GP"			, "R");
								recInTemp.setField("SCH_YN"					, "N");
								recInTemp.setField("WLOC_CD"				, "");
								recInTemp.setField("YD_PNT_CD"				, "");	
								recInTemp.setField("LOAN_PULLOUT_ABLE_YN"	, "N");
								recInTemp.setField("SPST_FRTOMOVE_GP"  		, "1" ); //특수강 이송구분
								
//							} else {
//								recInTemp.setField("JMS_TC_CD"			,"YSDSJ005");
//								recInTemp.setField("JMS_TC_CREATE_DDTT" ,commUtils.getDateTime14());
//								recInTemp.setField("TRANS_WORD_DATE"	,szTRANS_ORD_DATE2);
//								recInTemp.setField("TRANS_WORD_SEQNO" 	,szTRANS_ORD_SEQNO2);
//								recInTemp.setField("CARD_NO"			,szCARD_NO2);
//								recInTemp.setField("CAR_NO"				,szCAR_NO2);
//								recInTemp.setField("WLOC_CD"			,"");
//								recInTemp.setField("YD_PNT_CD"			,"");	
//								recInTemp.setField("LOAN_PULLOUT_ABLE_YN","N");
//								recInTemp.setField("SPST_FRTOMOVE_GP"  , "1" ); //특수강 이송구분								
//							}
							

							
							/////////////////////////입고대기 카운트 계산로직 추가 시작///////////////////////////////////////////
							String sBAYIN_WAIT_CNT = "0";
							{
								JDTORecordSet rsRslt 	= JDTORecordFactory.getInstance().createRecordSet("");
								JDTORecord   recInParam = JDTORecordFactory.getInstance().create();
								JDTORecord   recRslt 	= JDTORecordFactory.getInstance().create();
								
								recInParam.setField("TRANS_WORD_DATE"	,szTRANS_ORD_DATE2);
								recInParam.setField("TRANS_WORD_SEQNO" 	,szTRANS_ORD_SEQNO2);
								recInParam.setField("CAR_NO"			,szCAR_NO2);
								
								rsRslt = commDao.select(recInParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschCnt", logId, methodNm, "대기차량정보 조회"); 
						    	
						    	if (rsRslt == null || rsRslt.size() <= 0) {
						    		
								}else{
									rsRslt.first(); 
									recRslt				= rsRslt.getRecord();
									sBAYIN_WAIT_CNT		= StringHelper.evl(recRslt.getFieldString("BAYIN_WAIT_CNT"), "0");
								}
						    }
							recInTemp.setField("BAYIN_WAIT_CNT"    , sBAYIN_WAIT_CNT); //입동대기카운트
							/////////////////////////입고대기 카운트 계산로직 추가 시작///////////////////////////////////////////
							
							sndRecord = commUtils.addSndData(sndRecord,recInTemp);	
							
			    			szMsg= "["+ methodNm +"] 파라미터로 넘겨진 차량스케줄[현재차량스케줄:" + szYD_CAR_SCH_ID + "]의 출하차량[차량번호:" + szCAR_NO2 + ", 카드번호:" + szCARD_NO2 + ", 운송지시일자:" + szTRANS_ORD_DATE2 + ", 운송지시순번:" + szTRANS_ORD_SEQNO2 + "]에 대해  입동지시 전문전송 .";
							commUtils.printLog(logId, szMsg, "SL");										
						}
					}
				}
				
				//-----------------------------------------------------------------------------------------------------------------------
				//차량도착 처리 호출
				//-----------------------------------------------------------------------------------------------------------------------
				if( isCarArrAble ) {
					
					//-----------------------------------------------------------------------------------
					//-----------------------------------------------------------------------------------
					// transaction 을 분리하여 적치열의 할성상태를 'L'로 업데이트 한다. - 2017.10.26 
					try {
						recInTemp = JDTORecordFactory.getInstance().create();
				    	recInTemp.setField("YS_STK_COL_GP", szYD_CAR_STOP_LOC);
				    	recInTemp.setField("YD_STK_COL_ACT_STAT", "L");

				    	EJBConnector 	ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);			
					    ejbConn.trx("updYdStkColTX", new Class[] { JDTORecord.class }, new Object[] { recInTemp });	 
					    
					} catch (Exception e) {
						e.printStackTrace();
					}
					//-----------------------------------------------------------------------------------
					//-----------------------------------------------------------------------------------
					
					// PIDEV					
//					if( "N".equals(sApplyYnPI) ) {
//						
//						if(szNEW_CARD_NO.equals("")){
//							szNEW_CARD_NO = "XXXX";
//						}
//						
//					}

					szMsg= "["+ methodNm +"] 차량스케줄ID[" + szNEW_YD_CAR_SCH_ID1 + "]의 출하차량[차량번호:" + szNEW_CAR_NO + ", 포인트:" + szYD_PNT_CD + ", 운송지시일자:" + szNEW_TRANS_ORD_DATE + ", 운송지시순번:" + szNEW_TRANS_ORD_SEQNO + "]에 대해 차량도착 전문전송 시작.";
					commUtils.printLog(logId, szMsg, "SL");										
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);	//Log ID
					recInTemp.setResultMsg(methodNm);	//Log Method Name
					recInTemp.setField("JMS_TC_CD"			,"YSYSJ906");
					recInTemp.setField("JMS_TC_CREATE_DDTT" ,commUtils.getDateTime14());
					recInTemp.setField("TRANS_ORD_DT"		,szNEW_TRANS_ORD_DATE);
					recInTemp.setField("TRANS_ORD_SEQNO" 	,szNEW_TRANS_ORD_SEQNO);
					recInTemp.setField("CAR_NO"				,szNEW_CAR_NO);
					recInTemp.setField("CARD_NO"			,szNEW_CARD_NO);
					recInTemp.setField("SPOS_WLOC_CD"		,szWLOC_CD);	
					recInTemp.setField("SPOS_YD_PNT_CD"		,szYD_PNT_CD);
					recInTemp.setField("WORK_GP"			,szNEW_CAR_KIND_GP);
					recInTemp.setField("IS_EJB_CALL"		,"N");

					if("A".equals(szNEW_YD_CAR_PROG_STAT)) { 
						recInTemp.setField("JMS_TC_CREATE_DDTT" ,commUtils.getDateTime14());
						recInTemp.setField("YD_CAR_STOP_LOC"	,szYD_CAR_STOP_LOC); 	//입고차량일경우 차량정지위치를 넘겨준다.
						recInTemp.setField("YD_CAR_SCH_ID"		,szNEW_YD_CAR_SCH_ID1); //입고차량일경우 차량스케줄ID를 넘겨준다.
						
						sndRecord = commUtils.addSndData(sndRecord,this.RcptCarArrWr(recInTemp));	

						szMsg= "["+ methodNm +"] 차량스케줄ID[" + szNEW_YD_CAR_SCH_ID1 + "]의 입고차량[차량번호:" + szNEW_CAR_NO + ", 카드번호:" + szNEW_CARD_NO + ", 운송지시일자:" + szNEW_TRANS_ORD_DATE + ", 운송지시순번:" + szNEW_TRANS_ORD_SEQNO + "]에 대해 입고차량도착 전문전송 완료";
						commUtils.printLog(logId, szMsg, "SL");		
						
					} else {

						sndRecord = commUtils.addSndData(sndRecord,this.rcvYSYSJ906(recInTemp));	
						
						commUtils.printParam(logId, sndRecord);
						
						szMsg= "["+ methodNm +"] 차량스케줄ID[" + szNEW_YD_CAR_SCH_ID1 + "]의 출하차량[차량번호:" + szNEW_CAR_NO + ", 카드번호:" + szNEW_CARD_NO + ", 운송지시일자:" + szNEW_TRANS_ORD_DATE + ", 운송지시순번:" + szNEW_TRANS_ORD_SEQNO + "]에 대해 차량도착 전문전송 완료";
						commUtils.printLog(logId, szMsg, "SL");		
					}
					
				}
				
				//-----------------------------------------------------------------------------------------------------------------------
			} else {
				
				//-----------------------------------------------------------------------------------------------------------------------
				// 차량정지위치가 사용불가능인 경우
				//-----------------------------------------------------------------------------------------------------------------------
				szMsg= "["+ methodNm +"] 차량정지위치(적치열)[" + szYD_CAR_STOP_LOC + "]의 야드적치열활성상태값 [" + szYD_STK_COL_ACT_STAT + "]이므로 사용불가능합니다.";
				commUtils.printLog(logId, szMsg, "SL");
				
				if( !szYD_CAR_SCH_ID.equals("") ) {
					
					szMsg= "["+ methodNm +"] 차량정지위치가 사용불가능하므로 파라미터로 넘겨진 차량스케줄ID[" + szYD_CAR_SCH_ID + "]로 차량스케줄을 조회해서 입동지시 전문을 N으로 전송한다.";
					commUtils.printLog(logId, szMsg, "SL");
					
					rsCarSch = JDTORecordFactory.getInstance().createRecordSet("");
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch
					SELECT *
					FROM TB_YS_CARSCH C
					WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					*/
					//차량스케쥴 조회
					rsCarSch = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch", logId, methodNm, "차량스케쥴 조회");
					
					if (rsCarSch.size() <= 0) {
						commUtils.printLog(logId, "차량스케줄이 없습니다. SKIP", "SL");
						sndRecord.setTaskCode("-1");
						return sndRecord;
					}							
					
					if (rsCarSch.size() <= 0) {
						commUtils.printLog(logId, "차량스케줄이 없습니다. SKIP", "SL");
						sndRecord.setTaskCode("-1");
						return sndRecord;
					}							
				
					rsCarSch.first();
					recCarSch = rsCarSch.getRecord();
					
					szCAR_NO2 			= StringHelper.evl(recCarSch.getFieldString("CAR_NO"), "");
					szCARD_NO2 			= StringHelper.evl(recCarSch.getFieldString("CARD_NO"), "");
					szTRANS_ORD_DATE2 	= StringHelper.evl(recCarSch.getFieldString("TRANS_ORD_DATE"), "");
					szTRANS_ORD_SEQNO2 	= StringHelper.evl(recCarSch.getFieldString("TRANS_ORD_SEQNO"), "");
					szYD_CAR_PROG_STAT2	= StringHelper.evl(recCarSch.getFieldString("YD_CAR_PROG_STAT"), "");
					
					//CHITO 2018.07.10 이재현 차장님 요청 : 상차완료 후 입동TC가 다시 나가는 경우에 대한 보완 
					if("A".equals(szYD_CAR_PROG_STAT2)|| "5".equals(szYD_CAR_PROG_STAT2)) {
						// 입고차량인 경우 출하로 입동지시전문(YSDSJ005)을 전송하지 않는다. 
		    			szMsg= "["+ methodNm +"] 파라미터로 넘겨진 차량스케줄[현재차량스케줄:" + szYD_CAR_SCH_ID + "]의 입고차량[차량번호:" + szCAR_NO2 + ", 카드번호:" + szCARD_NO2 + ", 운송지시일자:" + szTRANS_ORD_DATE2 + ", 운송지시순번:" + szTRANS_ORD_SEQNO2 + "]에 대해 입고차량임으로 입동지시 전문전송 안함.";
						commUtils.printLog(logId, szMsg, "SL");										
					} else {
						//입동지시전문전문(YSDSJ005) 전송
						
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setResultCode(logId);	//Log ID
						recInTemp.setResultMsg(methodNm);	//Log Method Name
						
						// PIDEV
//						if("Y".equals(sApplyYnPI)) {
							recInTemp = JDTORecordFactory.getInstance().create();
							recInTemp.setField("MQ_TC_CD"				, "M10YDLMJ1064");
							recInTemp.setField("MQ_TC_CREATE_DDTT" 		, commUtils.getDateTime14());
							recInTemp.setField("TRN_REQ_DATE"			, szTRANS_ORD_DATE2);
							recInTemp.setField("TRN_REQ_SEQ" 			, szTRANS_ORD_SEQNO2);
							recInTemp.setField("CAR_NO"					, szCAR_NO2);
							recInTemp.setField("YD_GP"					, "K");
							recInTemp.setField("DIST_GOODS_GP"			, "R");
							recInTemp.setField("SCH_YN"					, "N");								
							recInTemp.setField("WLOC_CD"				, "");
							recInTemp.setField("YD_PNT_CD"				, "");	
							recInTemp.setField("LOAN_PULLOUT_ABLE_YN"	, "N");
							recInTemp.setField("SPST_FRTOMOVE_GP"  		, "1" ); //특수강 이송구분
							
//						} else {
//							recInTemp.setField("JMS_TC_CD"				,"YSDSJ005");
//							recInTemp.setField("JMS_TC_CREATE_DDTT" 	,commUtils.getDateTime14());
//							recInTemp.setField("TRANS_WORD_DATE"		,szTRANS_ORD_DATE2);
//							recInTemp.setField("TRANS_WORD_SEQNO" 		,szTRANS_ORD_SEQNO2);
//							recInTemp.setField("CARD_NO"				,szCARD_NO2);
//							recInTemp.setField("CAR_NO"					,szCAR_NO2);
//							recInTemp.setField("WLOC_CD"				,"");
//							recInTemp.setField("YD_PNT_CD"				,"");	
//							recInTemp.setField("LOAN_PULLOUT_ABLE_YN"	,"N");
//							recInTemp.setField("SPST_FRTOMOVE_GP"  		, "1" ); //특수강 이송구분
//						}
						/////////////////////////입고대기 카운트 계산로직 추가 시작///////////////////////////////////////////
						String sBAYIN_WAIT_CNT = "0";
						{
							JDTORecordSet rsRslt 	= JDTORecordFactory.getInstance().createRecordSet("");
							JDTORecord   recInParam = JDTORecordFactory.getInstance().create();
							JDTORecord   recRslt 	= JDTORecordFactory.getInstance().create();
							
							recInParam.setField("TRANS_WORD_DATE"	,szTRANS_ORD_DATE2);
							recInParam.setField("TRANS_WORD_SEQNO" 	,szTRANS_ORD_SEQNO2);
							recInParam.setField("CAR_NO"			,szCAR_NO2);
							
							rsRslt = commDao.select(recInParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschCnt", logId, methodNm, "대기차량정보 조회"); 
					    	
					    	if (rsRslt == null || rsRslt.size() <= 0) {
					    		
							}else{
								rsRslt.first(); 
								recRslt				= rsRslt.getRecord();
								sBAYIN_WAIT_CNT		= StringHelper.evl(recRslt.getFieldString("BAYIN_WAIT_CNT"), "0");
							}
					    }
						recInTemp.setField("BAYIN_WAIT_CNT"    , sBAYIN_WAIT_CNT); //입동대기카운트
						/////////////////////////입고대기 카운트 계산로직 추가 시작///////////////////////////////////////////
	
						sndRecord = commUtils.addSndData(sndRecord,recInTemp);	
						
		    			szMsg= "["+ methodNm +"] 파라미터로 넘겨진 차량스케줄[현재차량스케줄:" + szYD_CAR_SCH_ID + "]의 입고차량[차량번호:" + szCAR_NO2 + ", 카드번호:" + szCARD_NO2 + ", 운송지시일자:" + szTRANS_ORD_DATE2 + ", 운송지시순번:" + szTRANS_ORD_SEQNO2 + "]에 대해  입동지시 전문전송 .";
						commUtils.printLog(logId, szMsg, "SL");	
					}	
				}
			}
			
		}catch(Exception e){
			szMsg="[ 차량입동지시요구수신 ] Error:" +e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");	
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}
			
		szMsg="[ 차량입동지시요구수신 ] 처리(procCarBayInOrdReq) 완료";
	 	commUtils.printLog(logId, methodNms+szMsg, "S-");
		return sndRecord;
		
	}// end of procCarBayInOrdReq()
	
	/**
	 * [A] 오퍼레이션명 : 야드적치열맵활성화 UPDATE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord inRec parameter record
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */		
	public void updYdStkColTX(JDTORecord recInTemp) throws DAOException  {
		
		String methodNm = "차량포인트 트랜잭션 분리 활성화[YsCommCarMvSeEJB.updYdStkColTX]";
		String logId = recInTemp.getResultCode();
		
		try {
			int intRtnVal       		= 0;

			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStat 
			UPDATE TB_YS_STKCOL
			   SET YD_STK_COL_ACT_STAT = NVL(:V_YD_STK_COL_ACT_STAT,YD_STK_COL_ACT_STAT)
			WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
	    	*/
	    	intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStat_01", logId, methodNm, "TB_YS_STKCOL 등록");
	    	
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(e.getMessage(), e);			
		}
		
		return;
	};

	/**
	 * [A] 오퍼레이션명 : 제품출하차량도착실적처리 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return String
	 * @throws JDTOException
	 */
	public JDTORecord rcvYSYSJ906(JDTORecord msgRecord)throws JDTOException  {
		String methodNm = "제품출하차량도착실적처리[YsCommCarMvSeEJB.rcvYSYSJ906] < " + msgRecord.getResultMsg();
		String logId = msgRecord.getResultCode();
			/*
		 * 업무기준 : 1. 차량정지위치의 맵을 활성화
		 * 			2. 제품출하상차LOT 편성 모듈 호출
		 * 
		 */
		JDTORecordSet rsStkCol 		= null;
		JDTORecord 	sndRecord	        = JDTORecordFactory.getInstance().create();
	    String szMsg                = "";
	    String szMethodName      	= "rcvYSYSJ906";
	    String szOperationName		= "제품출하차량도착실적처리";
		
	    // TC CODE
	    /*
	    TRANS_ORD_DT	운송지시일자		DATE				
	    TRANS_ORD_SEQNO	운송지시순번		CHAR	4			
	    CAR_NO			차량번호			CHAR	15			
	    CARD_NO			카드번호			CHAR	4		출하차량 ID카드번호	
	    SPOS_WLOC_CD   	발지개소코드		CHAR	6		차량도착 개소코드	
	    SPOS_YD_PNT_CD 	발지야드포인트코드	CHAR	4		차량도착  포인트코드
	    */
		
		
	    try{

			commUtils.printLog(logId, methodNm+szMsg, "S+");
			
			
			String szTRANS_ORD_DT = commUtils.trim(msgRecord.getFieldString("TRANS_ORD_DT")); //운송장비코드  ydDaoUtils.paraRecChkNull(msgRecord, "TRANS_ORD_DT");
			if(szTRANS_ORD_DT.equals("")) {
				szMsg = "[전문 이상] 운송지시일자가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			}	    	
	    	
			String szTRANS_ORD_SEQNO = commUtils.trim(msgRecord.getFieldString("TRANS_ORD_SEQNO"));
			if(szTRANS_ORD_SEQNO.equals("")) {
				szMsg = "[전문 이상] 운송지시순번이 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			}	    	

			String szCAR_NO = commUtils.trim(msgRecord.getFieldString("CAR_NO"));
			if(szCAR_NO.equals("")) {
				szMsg = "[전문 이상] 차량번호가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			}	    	
			
			String szSPOS_WLOC_CD = commUtils.trim(msgRecord.getFieldString("SPOS_WLOC_CD"));
			if(szSPOS_WLOC_CD.equals("")) {
				szMsg = "[전문 이상] 발지개소코드가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			}	    	
			
			String szSPOS_YD_PNT_CD = commUtils.trim(msgRecord.getFieldString("SPOS_YD_PNT_CD")); 
			if(szSPOS_YD_PNT_CD.equals("")) {
				szMsg = "[전문 이상] 발지개소포인트 코드가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			}	    	
			
	    	
	    	rsStkCol = JDTORecordFactory.getInstance().createRecordSet("Temp");

	    	//출하차량도착실적처리 - 맵활성화
			if( this.procOutCarArrWr(msgRecord, rsStkCol) > 0 ) {
				
				rsStkCol.absolute(1);
				String szYD_CARLD_STOP_LOC = commUtils.trim(msgRecord.getFieldString("YD_CARLD_STOP_LOC"));
				
				// 전문 생성 및 전송
				JDTORecord recSndQue = JDTORecordFactory.getInstance().create();
			
		    	recSndQue.setField("YD_CARLD_STOP_LOC"	  , szYD_CARLD_STOP_LOC);							//차량상차정지위치
				recSndQue.setField("YD_CAR_USE_GP"    	  , "G");	//차량사용구분(G:출하차량, L:구내운송) - 출하차량만 적용
				recSndQue.setField("YD_GP"           	  , szYD_CARLD_STOP_LOC.substring(0, 1));			//야드
				recSndQue.setField("YD_BAY_GP"        	  , szYD_CARLD_STOP_LOC.substring(1, 2)); 			//동
				recSndQue.setField("TRANS_ORD_DATE"   	  , commUtils.trim(msgRecord.getFieldString("TRANS_ORD_DT")));		//운송지시일자
				recSndQue.setField("TRANS_ORD_SEQNO" 	  , commUtils.trim(msgRecord.getFieldString("TRANS_ORD_SEQNO")));	//운송지시순번
				recSndQue.setField("CAR_NO"           	  , commUtils.trim(msgRecord.getFieldString("CAR_NO")));			//차량번호
				recSndQue.setField("CARD_NO"          	  , commUtils.trim(msgRecord.getFieldString("CARD_NO")));			//카드번호
				recSndQue.setField("SPOS_WLOC_CD"         , commUtils.trim(msgRecord.getFieldString("SPOS_WLOC_CD")));		//발지개소코드
				recSndQue.setField("SPOS_YD_PNT_CD"       , commUtils.trim(msgRecord.getFieldString("SPOS_YD_PNT_CD")));	//발지야드포인트코드
				
				
				//제품출하상차LOT편성
				JDTORecord jrRtn =  this.procGdsDistCarLdLotComp(recSndQue);
				
				sndRecord = commUtils.addSndData(sndRecord,jrRtn);	

				commUtils.printParam(logId, sndRecord);
				/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	    		 * 업무기준 : 제품출하 공차 도착 시 저장위치 제원 야드L2로 전송
	    		 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
				
				String szYdGp = szYD_CARLD_STOP_LOC.substring(0,2);
				String szJMS_TC_CD = "";
				/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	    		 * 업무기준 : 제품출하 공차 도착 시 저장위치 제원 야드L2로 전송
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
				JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setResultCode(logId);	//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
	    		recInTemp.setField("MSG_ID",    szJMS_TC_CD);
				recInTemp.setField("YD_INFO_SYNC_CD", "3");						//1:동,2:SPAN,3:열,4:BED
				recInTemp.setField("YD_GP", szYD_CARLD_STOP_LOC.substring(0, 1));
				recInTemp.setField("YS_STK_COL_GP", szYD_CARLD_STOP_LOC);
				recInTemp.setField("YD_CAR_PROG_STAT", "2");
				recInTemp.setField("YD_EQP_WRK_STAT" , "U");

				//전송 Data 생성
				sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2(szJMS_TC_CD, recInTemp));

				szMsg="["+methodNm+"] 차량출발시 시 저장위치 제원 야드L2로 전송";
				commUtils.printLog(logId, szMsg, "SL");	
				
				
				
				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	    	}else{
	    		return sndRecord;
	    	}//end of if
		}catch(Exception e){
			szMsg="["+ szOperationName +"] Error:" +e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");	
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}

		szMsg= szOperationName + "(" + szMethodName + ") 완료";
		commUtils.printLog(logId, methodNm+szMsg, "S-");
		
		return sndRecord;
	}// end of procPlGdsDistCarArrWr()	

	

	/**
	 * 오퍼레이션명 : 제품출하상차LOT편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	
	public JDTORecord procGdsDistCarLdLotComp(JDTORecord msgRecord) throws JDTOException  {
		String methodNm = "제품출하상차LOT편성[YsCommCarMvSeEJB.procGdsDistCarLdLotComp] < " + msgRecord.getResultMsg();
		String logId = msgRecord.getResultCode();

		
		// 레코드 선언
		JDTORecord recPara      = null;
		JDTORecord recInTemp    = null;
		
		
		// 레코드셋 선언
		JDTORecordSet rsResult  = null;
		JDTORecordSet rsResult1	= null;
		JDTORecordSet recTEMP   = JDTORecordFactory.getInstance().createRecordSet("Temp");
		JDTORecord    recResult = null;
		JDTORecord	  sndRecord	= null;
		// 메세지
		String szMsg            = "";
		
		// 메소드명
		String szMethodName    	= "procGdsDistCarLdLotComp";
		String szOperationName	= "제품출하상차LOT편성";
		
		try {

			commUtils.printLog(logId, methodNm, "S+");
			// 상차정지위치
			String szYD_CARLD_STOP_LOC = commUtils.trim(msgRecord.getFieldString("YD_CARLD_STOP_LOC")); 
			if(szYD_CARLD_STOP_LOC.equals("")){
				szMsg = "[전문 이상] 상차정지위치가 없습니다.";
				commUtils.printLog(logId, methodNm, "SL");
				return sndRecord;
			}

			// 차량사용구분
			String szYD_CAR_USE_GP = commUtils.trim(msgRecord.getFieldString("YD_CAR_USE_GP"       )); 
			if(szYD_CAR_USE_GP.equals("") || !szYD_CAR_USE_GP.equals("G")){
				szMsg = "[전문 이상] 차량사용구분(" + szYD_CAR_USE_GP + ")";
				commUtils.printLog(logId, methodNm, "SL");
				return sndRecord;
			}
			
			// 차량번호
			String szCAR_NO = commUtils.trim(msgRecord.getFieldString("CAR_NO"       ));
			if(szCAR_NO.equals("")){
				szMsg = "[전문 이상] 차량번호가 없습니다.";
				commUtils.printLog(logId, methodNm, "SL");
				return sndRecord;
			}
			
			
			// 운송지시 일자
			String szTRANS_ORD_DATE = commUtils.trim(msgRecord.getFieldString("TRANS_ORD_DATE"       ));  
			if(szTRANS_ORD_DATE.equals("")){
				szMsg = "[전문 이상] 운송지시 일자가 없습니다.";
				commUtils.printLog(logId, methodNm, "SL");
				return sndRecord;
			}
			
			// 운송지시 순번
			String szTRANS_ORD_SEQNO = commUtils.trim(msgRecord.getFieldString("TRANS_ORD_SEQNO"       )); 
			if(szTRANS_ORD_SEQNO.equals("")){
				szMsg = "[전문 이상] 운송지시 순번이 없습니다.";
				commUtils.printLog(logId, methodNm, "SL");
				return sndRecord;
			}
			
			// 야드
			String szYdGp = commUtils.trim(msgRecord.getFieldString("YD_GP")); 
			if(szYdGp.equals("")){
				szMsg = "[전문 이상] 야드구분이 없습니다.";
				commUtils.printLog(logId, methodNm, "SL");
				return sndRecord;
			}

			// 동
			String szBayRGp = commUtils.trim(msgRecord.getFieldString("YD_BAY_GP")); 
			if(szBayRGp.equals("")){
				szMsg = "[전문 이상] 동구분이 없습니다.";
				commUtils.printLog(logId, methodNm, "SL");
				return sndRecord;
			}
			
			// 발지개소코드 
			String szSPOS_WLOC_CD = commUtils.trim(msgRecord.getFieldString("SPOS_WLOC_CD")); 
			if(szSPOS_WLOC_CD.equals("")){
				szMsg = "[전문 이상] 발지개소코드가 없습니다.";
				commUtils.printLog(logId, methodNm, "SL");
				return sndRecord;
			}
			
			// 발지포인트 코드 
			String szSPOS_YD_PNT_CD = commUtils.trim(msgRecord.getFieldString("SPOS_YD_PNT_CD")); 
			if(szSPOS_YD_PNT_CD.equals("")){
				szMsg = "[전문 이상] 발지개소코드가 없습니다.";
				commUtils.printLog(logId, methodNm, "SL");
				return sndRecord;
			}		
			
			// RecordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			
			//-------------------------------------------------------------
			// 크레인 작업 예약 생성 저장품과 적치단 테이블 조회 
			//-------------------------------------------------------------
			szMsg = "["+szOperationName+"] 운송지시일자["+szTRANS_ORD_DATE+"], 운송지시순번["+szTRANS_ORD_SEQNO+"], 차량번호["+szCAR_NO+"]에 해당하는 대상재를 조회 시작";
			commUtils.printLog(logId, szMsg, "SL");
			
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("CAR_NO", 				szCAR_NO);
			recPara.setField("TRANS_ORD_DATE",  	szTRANS_ORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO", 	szTRANS_ORD_SEQNO);
			recPara.setField("YD_CARLD_STOP_LOC", 	szYD_CARLD_STOP_LOC);
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkTransOrd 
			SELECT SSTL_NO              
			     , ROUND(YD_MTL_W) AS YD_MTL_W
			     , YD_MTL_WT
			     , YS_STK_COL_GP       
			     , YS_STK_BED_NO       
			     , YS_STK_LYR_NO       
			     , YS_STK_SEQ_NO       
			     , DONG
			     , SC_NO
			     , SPST_FRTOMOVE_GP
			     , ITEMNAME_CD
			     , 'Y' AS FLAG_GP
			     , COUNT(SSTL_NO) OVER (PARTITION BY SC_NO,ITEMNAME_CD) AS SEQ_NUM
			     , (SELECT ITEM
			          FROM USRYSA.TB_YS_RULE
			         WHERE REPR_CD_GP = 'K00005'
			           AND CD_GP = '0002'
			           AND ROWNUM = 1 ) AS SKID_W
			      , (SELECT ITEM
			          FROM USRYSA.TB_YS_RULE
			         WHERE REPR_CD_GP = 'K00005'
			           AND CD_GP = '0003'
			           AND ROWNUM = 1 ) AS SKID_WT      
			      , (SELECT ITEM
			          FROM USRYSA.TB_YS_RULE
			         WHERE REPR_CD_GP = 'K00005'
			           AND CD_GP = '0001'
			           AND ROWNUM = 1 ) AS SKID_CNT      
			  FROM
			(
			SELECT ROW_NUMBER() OVER (PARTITION BY  B.YS_STK_COL_GP ORDER BY B.YS_STK_COL_GP, B.YS_STK_BED_NO ,B.YS_STK_LYR_NO ) AS SEQ_CHK
			     , A.SSTL_NO              
			     , A.YD_MTL_W
			     , A.YD_MTL_WT
			     , A.SPST_FRTOMOVE_GP
			     , A.ITEMNAME_CD
			     , B.YS_STK_COL_GP       
			     , B.YS_STK_BED_NO       
			     , B.YS_STK_LYR_NO       
			     , B.YS_STK_SEQ_NO       
			     , SUBSTR(B.YS_STK_COL_GP,2,1) AS DONG
			     , ( SELECT CD_GP 
			           FROM USRYSA.TB_YS_RULE
			          WHERE REPR_CD_GP = 'K00007'
			            AND ITEM =B.YS_STK_COL_GP ) SC_NO
			  FROM TB_YS_STOCK A,
			       TB_YS_STKLYR B
			 WHERE A.SSTL_NO  = B.SSTL_NO
			   AND A.CAR_NO          = :V_CAR_NO
			   AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
			   AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			   AND SUBSTR(B.YS_STK_COL_GP,1,2) LIKE SUBSTR(NVL(:V_YD_CARLD_STOP_LOC,'KA'),1,2) ||'%'
			   AND B.YD_STK_LYR_MTL_STAT IN ('C','U')
			   AND (B.YS_STK_COL_GP LIKE '__0___%'  OR B.YS_STK_COL_GP LIKE '__CR__%' OR B.YS_STK_COL_GP LIKE '__7___%' ) 
			   AND B.SSTL_NO NOT IN (SELECT WT.SSTL_NO 
			                           FROM TB_YS_WRKBOOK WB
			                              , TB_YS_WRKBOOKMTL WT
			                          WHERE WB.YD_WBOOK_ID = WT.YD_WBOOK_ID
			                              AND WB.YD_GP = 'K'
			                              AND SUBSTR(WB.YD_SCH_CD,7,2) IN ('UM','BM') -- 출하 작업예약 제외
			                              AND WB.DEL_YN = 'N'
			                              AND WT.DEL_YN = 'N')
			)
			ORDER BY (CASE WHEN DONG IN('D','E') THEN SEQ_CHK ELSE 1 END), SEQ_NUM DESC, ITEMNAME_CD, SC_NO
			  */    
			rsResult = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkTransOrd", logId, methodNm, "작업대상 조회"); 
	
			String szYD_SCH_CD  = "";   
			String szSC_NO = "";
			String szOLD_YD_SCH_CD = "";
			String szYD_WBOOK_ID = "";
			String szREPR_YD_WBOOK_ID = "";
			
			String szSCH_PRIOR 	= "";	
			String szCurrCrn 	="";	
			String[][] bedMtl = new String[20][2];	//KDSCXX재료정보
			String szMtlIns ="";	
			String szSPST_FRTOMOVE_GP ="";	
			String sITEMNAME_CD = "";
			String sOLD_ITEMNAME_CD = "";
			String sFLAG_YN = "";
			
			int iYD_MTL_W = 0;
			int iYD_MTL_WT = 0;
			int iSKID_W = 0;
			int iSKID_WT = 0;
			int iSKID_CNT = 0;
			int iSumYD_MTL_W = 0;
			int iSumYD_MTL_WT = 0;
			int iSumYD_MTL_CNT = 0;
			int iChoiceCnt   = 0;
			
			if( rsResult.size() > 0 ) {
				szMsg = "["+szOperationName+"] 운송지시일자["+szTRANS_ORD_DATE+"], 운송지시순번["+szTRANS_ORD_SEQNO+"],차량번호["+szCAR_NO+"]에 해당하는 대상재 존재 - 건수 : " + rsResult.size();
				commUtils.printLog(logId, szMsg, "SL");

				// 자동화 창고 인 경우 SC 작업 예약을 생성 해 놔야 한다.
				for(int i = 1; i <= rsResult.size(); i++) {
		
					rsResult.absolute(i);
					recResult = rsResult.getRecord();
					szSC_NO 		= commUtils.trim(recResult.getFieldString("SC_NO"));	
					sITEMNAME_CD	= commUtils.trim(recResult.getFieldString("ITEMNAME_CD"));
					sFLAG_YN		= commUtils.trim(recResult.getFieldString("FLAG_YN"));
					iYD_MTL_W 		= Integer.parseInt(commUtils.nvl(recResult.getFieldString("YD_MTL_W"),"0"));
					iYD_MTL_WT 		= Integer.parseInt(commUtils.nvl(recResult.getFieldString("YD_MTL_WT"),"0"));
					szSPST_FRTOMOVE_GP		= commUtils.trim(recResult.getFieldString("SPST_FRTOMOVE_GP"));	
					iSKID_W		    = Integer.parseInt(commUtils.trim(recResult.getFieldString("SKID_W")));
					iSKID_WT	    = Integer.parseInt(commUtils.trim(recResult.getFieldString("SKID_WT")));
					iSKID_CNT	    = Integer.parseInt(commUtils.trim(recResult.getFieldString("SKID_CNT")));
					
					if(!szSC_NO.equals("")) {
						/**********************************************************
						* SC 크레인 출하 작업 예약 편성 
						**********************************************************/
						szYD_SCH_CD  = 	szSC_NO + "HSUM" ;
						//반납
						if(szSPST_FRTOMOVE_GP.equals("21")) {
							szYD_SCH_CD  = 	szSC_NO + "HSBM";
						} 
						commUtils.printLog(logId, "스케쥴 코드 :"+ szYD_SCH_CD, "SL");
						

						if((szBayRGp.equals("A") || szBayRGp.equals("D")) &&(szREPR_YD_WBOOK_ID.equals(""))) {
							// 대표 ID 편성
							//  스택거 크레인 작업예약 : CAR_YD_WBOOK_ID 에 등록 됨 
							szREPR_YD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
						}	
						
						if(szSC_NO.substring(1,2).equals("A")) {
							
							/**********************************************************
							* 봉강자동화 창고 SC 크레인 작업 예약 편성 
							**********************************************************/
							if(!szOLD_YD_SCH_CD.equals(szYD_SCH_CD)){

								szMsg="스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.";
								commUtils.printLog(logId, szMsg, "SL");
						    	
								rsResult1 = JDTORecordFactory.getInstance().createRecordSet("Temp");
								recInTemp = JDTORecordFactory.getInstance().create();
								recInTemp.setField("YD_SCH_CD", 		szYD_SCH_CD);
						    	rsResult1 = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
						    	
						    	if (rsResult1 == null || rsResult1.size() <= 0) {
						    		szMsg = "["+methodNm+"] 스케줄 기준 조회 data not found";
									commUtils.printLog(logId, szMsg, "SL");
									throw new Exception("스케줄 기준 조회 실패");
								}
								
								//레코드 추출
								rsResult1.first();
								recPara = rsResult1.getRecord();
								
								szSCH_PRIOR = commUtils.trim(recPara.getFieldString("YD_WRK_CRN_PRIOR"       ));	
								szCurrCrn 	= commUtils.trim(recPara.getFieldString("YD_WRK_CRN"       ));							

								//생성한 작업예약ID
								//com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdWrkBook
								szYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
								
								if ("".equals(szYD_WBOOK_ID)) {
									throw new Exception("작업예약ID 생성 실패");
								}
			
								//작업예약항목SETTING
						    	recInTemp = JDTORecordFactory.getInstance().create();
						    	
						    	recInTemp.setField("YD_WBOOK_ID",      szYD_WBOOK_ID);
						    	recInTemp.setField("REGISTER",         "");
						    	recInTemp.setField("YD_GP",            szYD_SCH_CD.substring(0, 1));
						    	recInTemp.setField("YD_BAY_GP",        szYD_SCH_CD.substring(1, 2));
						    	recInTemp.setField("YD_AIM_YD_GP",     szYD_SCH_CD.substring(0, 1));
						    	recInTemp.setField("YD_AIM_BAY_GP",    szYD_SCH_CD.substring(1, 2));
						    	recInTemp.setField("YD_SCH_PRIOR", 	   szSCH_PRIOR);
						    	recInTemp.setField("YD_SCH_CD",        szYD_SCH_CD);
						    	recInTemp.setField("CAR_NO",       	   szCAR_NO);
						    	recInTemp.setField("YD_CAR_USE_GP",    szYD_CAR_USE_GP);
						    	
						    	recInTemp.setField("YD_EQP_ID", 		szCurrCrn);
						    	recInTemp.setField("CAR_YD_WBOOK_ID"   ,szREPR_YD_WBOOK_ID);
						    	recTEMP.addRecord(recInTemp);
								
						    	//com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook
								commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
								
							}
							
							//작업예약재료 등록
							
				    		JDTORecord recOutTemp = JDTORecordFactory.getInstance().create();
				    		recInTemp  = JDTORecordFactory.getInstance().create();
				    		recOutTemp.setRecord(rsResult.getRecord());
				    		
				    		recInTemp.setField("YD_WBOOK_ID",    szYD_WBOOK_ID);
				    		recInTemp.setField("REGISTER",       "");
				    		recInTemp.setField("YS_STK_COL_GP",  commUtils.trim(recResult.getFieldString("YS_STK_COL_GP")));
				    		recInTemp.setField("YS_STK_BED_NO",  commUtils.trim(recResult.getFieldString("YS_STK_BED_NO")));
				    		recInTemp.setField("YS_STK_LYR_NO",  commUtils.trim(recResult.getFieldString("YS_STK_LYR_NO")));
				    		recInTemp.setField("YS_STK_SEQ_NO",  commUtils.trim(recResult.getFieldString("YS_STK_SEQ_NO")));
				    		recInTemp.setField("SSTL_NO",        commUtils.trim(recResult.getFieldString("SSTL_NO")));
				    		recInTemp.setField("YD_UP_COLL_SEQ", "" + i);
				    		
				    		commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
				    		
				    		szOLD_YD_SCH_CD 	= szYD_SCH_CD;

				    		
						} else {
							/**********************************************************
							* 선재자동화 창고 SC 크레인 작업 예약 편성 
							* 권하 위치('KDCSxx' 를 확인해서 분할 처리 함 
							* 출고위치는 5M < 합계(제품폭+100)*제품수
							* 로직 : 먼저 5000 단위 및 크레인스케쥬별로 작업예약분리후 비워 있는 위치( KDCSxx') 만큼 스케쥴 기동
							* ------------------------------------------------------------
							* 로직 변경시 권하 처리 로직도 수정해야 함
							**********************************************************/
							JDTORecordSet jsChk1  = JDTORecordFactory.getInstance().createRecordSet("Temp");
							
							if (i == 1){
								
								for (int ii = 0; ii < 20; ii++) {
									for (int jj = 0; jj < 2; jj++) {
										bedMtl[ii][jj] = "";
									}
								}							
								
								recInTemp = JDTORecordFactory.getInstance().create();
								JDTORecord outParam1 = JDTORecordFactory.getInstance().create();
								recInTemp.setField("YS_STK_COL_GP",      "KDCS");
								/* com.inisteel.cim.ys.gds.dao.GdsYsDAO.getStkLyrCntWrD 
								SELECT YS_STK_COL_GP
								     , SSTL_CNT
								     , YD_EQP_STAT
								  FROM
								(
								SELECT A.YS_STK_COL_GP
								     , COUNT(SSTL_NO) AS SSTL_CNT
								     , (SELECT YD_EQP_STAT FROM TB_YS_EQP WHERE YD_EQP_ID = 'KDCC3'||SUBSTR(A.YS_STK_COL_GP,6,1)) AS YD_EQP_STAT
								  FROM TB_YS_STKCOL A
								     , TB_YS_STKLYR B 
								 WHERE A.YS_STK_COL_GP = B.YS_STK_COL_GP
								   AND A.YS_STK_COL_GP LIKE :V_YS_STK_COL_GP || '%'
								   AND A.YD_STK_COL_ACT_STAT = 'L'
								   AND NVL(B.SSTL_NO,'1') NOT IN (SELECT WT.SSTL_NO 
								                                    FROM TB_YS_WRKBOOK WB
								                                       , TB_YS_WRKBOOKMTL WT
								                                   WHERE WB.YD_WBOOK_ID = WT.YD_WBOOK_ID
								                                     AND WB.YD_SCH_CD LIKE '____HS_M'  -- 출하 작업예약 제외
								                                     AND WB.DEL_YN = 'N'
								                                     AND WT.DEL_YN = 'N')
								   AND A.YS_STK_COL_GP NOT IN (SELECT NVL(WB.YD_TO_LOC_GUIDE,'*') 
								                                    FROM TB_YS_WRKBOOK WB
								                                   WHERE WB.YD_SCH_CD LIKE '____HS_M'  -- 출하 작업예약 제외
								                                     AND WB.DEL_YN = 'N')                                     
								   AND A.YS_STK_COL_GP NOT IN ( SELECT 'KDCS0'||SUBSTR(YS_STK_COL_GP,5,1) 
								                                  FROM TB_YS_STKLYR  
								                                 WHERE YS_STK_COL_GP LIKE 'KDCS%'
								                                   AND SUBSTR(YS_STK_COL_GP,5,1) <>'0'
								                                   AND SSTL_NO IS NOT NULL
								                                UNION ALL
								                                SELECT 'KDCS0'||SUBSTR(YS_STK_COL_GP,6,1) 
								                                  FROM TB_YS_STKLYR  
								                                 WHERE YS_STK_COL_GP LIKE 'KDCS%'
								                                   AND SUBSTR(YS_STK_COL_GP,5,1) <>'0'
								                                   AND SSTL_NO IS NOT NULL)
								 GROUP BY A.YS_STK_COL_GP       
								 HAVING COUNT(SSTL_NO) = 0 AND COUNT(*) = 5
								 ) WHERE YD_EQP_STAT <> 'B'
								 ORDER BY SSTL_CNT --, MOD(SUBSTR(YS_STK_COL_GP,6,1),2) DESC 
								        , YS_STK_COL_GP
	
								 */
								jsChk1 = commDao.select(recInTemp, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.getStkLyrCntWrD", logId, methodNm, "적치열 구분 조회");
	
								
								for (int ii = 0; ii < jsChk1.size() ; ii++) {  
									jsChk1.absolute(ii+1);
									outParam1  = jsChk1.getRecord();
									bedMtl[ii][0] = commUtils.trim(outParam1.getFieldString("YS_STK_COL_GP"));
									bedMtl[ii][1] = commUtils.trim(outParam1.getFieldString("SSTL_CNT"     )); // 추후 필요 예정
								} 							
								
							}	
							
							commUtils.printParam( logId, bedMtl);
														
							iSumYD_MTL_W 	= iSumYD_MTL_W + (iYD_MTL_W + 100);
							iSumYD_MTL_WT 	= iSumYD_MTL_WT + iYD_MTL_WT ;
							iSumYD_MTL_CNT 	= iSumYD_MTL_CNT + 1 ;
							
							szMsg="재료 SUM 폭[" + iSumYD_MTL_W + " ] 재료 폭[" + iYD_MTL_W + " ]" + "재료 SUM 중량 [" + iSumYD_MTL_WT + " ] 재료중량[" + iYD_MTL_WT + " ]" + "재료 SUM CNT [" + iSumYD_MTL_CNT + " ]";
							commUtils.printLog(logId, szMsg, "SL");
							
				    		//재료 폭/중량 / 갯수  CLEAR
							if(( iSumYD_MTL_W > iSKID_W ) || ( iSumYD_MTL_WT > iSKID_WT ) || ( iSumYD_MTL_CNT > iSKID_CNT )){
								iSumYD_MTL_W 	= iYD_MTL_W + 100;
								iSumYD_MTL_WT 	= iYD_MTL_WT;
								iSumYD_MTL_CNT 	= 1; 
								
								iChoiceCnt ++;
								szMtlIns = "Y";
							}else {
								szMtlIns = "N";
								
								/*
								 * 2019.11.13 윤재광
								 * - 선재/BIC 출하SKID 구분기능 추가
								 * - 운영계 반영 구분자 'Y':반영, 그외 : 미반영
								 */
								if("Y".equals(sFLAG_YN)){
									
									if(i != 1 && !sOLD_ITEMNAME_CD.equals(sITEMNAME_CD)){
										iSumYD_MTL_W 	= iYD_MTL_W + 100;
										iSumYD_MTL_WT 	= iYD_MTL_WT;
										iSumYD_MTL_CNT 	= 1; 
										
										iChoiceCnt ++;
										szMtlIns = "Y";
									}
								}
							}
							
							sOLD_ITEMNAME_CD = sITEMNAME_CD;
							
							szMsg="기준 폭[" + iSKID_W + " ] 기준 중량 [" + iSKID_WT + " ] 기준 CNT [" + iSKID_CNT + " ]"  ;
							commUtils.printLog(logId, szMsg, "SL");
							
							szMsg="szOLD_YD_SCH_CD[" + szOLD_YD_SCH_CD + " ] szYD_SCH_CD [" + szYD_SCH_CD + " ]szMtlIns [" + szMtlIns + " ] " ;
							commUtils.printLog(logId, szMsg, "SL");
							
							if(!szOLD_YD_SCH_CD.equals(szYD_SCH_CD) || szMtlIns.equals("Y") ){  //크레인별 sch_cd가 틀림  
								szMsg="스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.";
								commUtils.printLog(logId, szMsg, "SL");
								if(!szOLD_YD_SCH_CD.equals(szYD_SCH_CD)) {
									
									rsResult1 = JDTORecordFactory.getInstance().createRecordSet("Temp");
									recInTemp = JDTORecordFactory.getInstance().create();
									recInTemp.setField("YD_SCH_CD", 		szYD_SCH_CD);
							    	rsResult1 = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
							    	
							    	if (rsResult1 == null || rsResult1.size() <= 0) {
							    		szMsg = "["+methodNm+"] 스케줄 기준 조회 data not found";
										commUtils.printLog(logId, szMsg, "SL");
										throw new Exception("스케줄 기준 조회 실패");
									}
									
									//레코드 추출
									rsResult1.first();
									recPara = rsResult1.getRecord();
									
									szSCH_PRIOR = commUtils.trim(recPara.getFieldString("YD_WRK_CRN_PRIOR"       ));	
									szCurrCrn 	= commUtils.trim(recPara.getFieldString("YD_WRK_CRN"       ));							
								}	
									
								//생성한 작업예약ID
								//com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdWrkBook
								szYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
								
								if ("".equals(szYD_WBOOK_ID)) {
									throw new Exception("작업예약ID 생성 실패");
								}
								//작업예약항목SETTING
						    	recInTemp = JDTORecordFactory.getInstance().create();
						    	
						    	recInTemp.setField("YD_WBOOK_ID",      szYD_WBOOK_ID);
						    	recInTemp.setField("REGISTER",         "");
						    	recInTemp.setField("YD_GP",            szYD_SCH_CD.substring(0, 1));
						    	recInTemp.setField("YD_BAY_GP",        szYD_SCH_CD.substring(1, 2));
						    	recInTemp.setField("YD_AIM_YD_GP",     szYD_SCH_CD.substring(0, 1));
						    	recInTemp.setField("YD_AIM_BAY_GP",    szYD_SCH_CD.substring(1, 2));
						    	recInTemp.setField("YD_SCH_PRIOR", 	   szSCH_PRIOR);
						    	recInTemp.setField("YD_SCH_CD",        szYD_SCH_CD);
						    	recInTemp.setField("CAR_NO",       	   szCAR_NO);
						    	recInTemp.setField("YD_CAR_USE_GP",    szYD_CAR_USE_GP);
						    	recInTemp.setField("YD_TO_LOC_GUIDE"   ,bedMtl[iChoiceCnt][0]);
						    	recInTemp.setField("CARD_NO"           ,""+iChoiceCnt);
						    	
						    	recInTemp.setField("CAR_YD_WBOOK_ID"   ,szREPR_YD_WBOOK_ID);

						    	//com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook
								commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
								recInTemp.setField("YD_EQP_ID", 	szCurrCrn);
								recTEMP.addRecord(recInTemp);
							
							}
	
							//작업예약재료 등록
							
				    		JDTORecord recOutTemp = JDTORecordFactory.getInstance().create();
				    		recInTemp  = JDTORecordFactory.getInstance().create();
				    		recOutTemp.setRecord(rsResult.getRecord());
				    		
				    		recInTemp.setField("YD_WBOOK_ID",    szYD_WBOOK_ID);
				    		recInTemp.setField("REGISTER",       "");
				    		recInTemp.setField("YS_STK_COL_GP",  commUtils.trim(recResult.getFieldString("YS_STK_COL_GP"       )));
				    		recInTemp.setField("YS_STK_BED_NO",  commUtils.trim(recResult.getFieldString("YS_STK_BED_NO"       )));
				    		recInTemp.setField("YS_STK_LYR_NO",  commUtils.trim(recResult.getFieldString("YS_STK_LYR_NO"       )));
				    		recInTemp.setField("YS_STK_SEQ_NO",  commUtils.trim(recResult.getFieldString("YS_STK_SEQ_NO"       )));
				    		recInTemp.setField("SSTL_NO",        commUtils.trim(recResult.getFieldString("SSTL_NO"       )));
				    		recInTemp.setField("YD_UP_COLL_SEQ", "" + i);
				    		
				    		commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
				    		
				    		szOLD_YD_SCH_CD 	= szYD_SCH_CD;
						}	
					}
				} //for
					
				commUtils.printParam(logId, recTEMP);
				for(int i = 1; i <= recTEMP.size(); i++) {
					recTEMP.absolute(i);
					recResult = recTEMP.getRecord();
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setResultCode(logId);	//Log ID
			    	recInTemp.setResultMsg(methodNm);	//Log Method Name
			    	if(commUtils.trim(recResult.getFieldString("YD_BAY_GP")).equals("A")) {
						recInTemp.setField("JMS_TC_CD",    	"YSYSJ302");
			    		recInTemp.setField("JMS_TC_CREATE_DDTT"	,  commUtils.getDateTime14());
				    	recInTemp.setField("YD_SCH_CD", 	commUtils.trim(recResult.getFieldString("YD_SCH_CD")));
				    	recInTemp.setField("YD_EQP_ID", 	commUtils.trim(recResult.getFieldString("YD_EQP_ID")));
				    	recInTemp.setField("YD_WBOOK_ID", 	commUtils.trim(recResult.getFieldString("YD_WBOOK_ID")));
				    	
						sndRecord = commUtils.addSndData(sndRecord, recInTemp);
						
			    	} else if(commUtils.trim(recResult.getFieldString("YD_BAY_GP")).equals("D")) {
			    		if(!commUtils.trim(recResult.getFieldString("YD_TO_LOC_GUIDE")).equals("")) {
				    		recInTemp.setField("JMS_TC_CD",    	"YSYSJ402");
				    		recInTemp.setField("JMS_TC_CREATE_DDTT"	,  commUtils.getDateTime14());
					    	recInTemp.setField("YD_SCH_CD", 	commUtils.trim(recResult.getFieldString("YD_SCH_CD")));
					    	recInTemp.setField("YD_EQP_ID", 	commUtils.trim(recResult.getFieldString("YD_EQP_ID")));
					    	recInTemp.setField("YD_WBOOK_ID", 	commUtils.trim(recResult.getFieldString("YD_WBOOK_ID")));
					    	
							sndRecord = commUtils.addSndData(sndRecord, recInTemp);
			    		}	
			    	}
				}
				
				commUtils.printParam(logId, sndRecord);
						
				szYD_SCH_CD  = szYdGp + szBayRGp + "TR10UM" ;   
				if(szSPST_FRTOMOVE_GP.equals("21")) {
					szYD_SCH_CD  = 	szYdGp + szBayRGp + "TR10BM" ;   
				} 

				if(szBayRGp.equals("A") || szBayRGp.equals("D")) {
					// 차량스케쥴에 상차 작업예약 ID 만 생성 
                    //  봉강트레버서 나 선재 출고SKID에서 작업예약 생성시권하 
					//  스택거 크레인 작업예약 : CAR_YD_WBOOK_ID 에 등록 됨 
					szYD_WBOOK_ID = szREPR_YD_WBOOK_ID;
					
				} else{
					
			    	
			    	//스케줄코드로 스케줄기준Table조회
					szMsg="스케줄코드로 스케줄기준Table조회";
					commUtils.printLog(logId, szMsg, "SL");
					recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YD_SCH_CD", szYD_SCH_CD);
			    	
			    	
			    	//스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.
					szMsg="스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.";
					commUtils.printLog(logId, szMsg, "SL");
			    	rsResult1 = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    	/*  com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule 
			    	SELECT A.YD_GP
					      ,A.YD_BAY_GP
					      ,YD_SCH_CD
					      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN1
					            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN2
					        END AS WRK_CRN
					      ,CASE WHEN A.YD_CRN_PRIOR1 > 0 THEN YD_CRN_PRIOR1
					            WHEN A.YD_CRN_PRIOR2 > 0 THEN YD_CRN_PRIOR2
					        END AS YD_WRK_CRN_PRIOR
					      ,YD_SCH_CD_NM
					      ,YD_SCH_CONTENTS
					    FROM TB_YS_SCHRULE A
					        ,(
					            SELECT YD_GP
					                  ,YD_BAY_GP
					                  ,YD_SCH_GP
					                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 1, YD_EQP_STAT, '') ELSE 'O' END), 'X') AS STAT1 
					                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CRN_NO, 2, YD_EQP_STAT, '') ELSE 'X' END), 'X') AS STAT2 
					            FROM   (
					                        SELECT YD_EQP_ID
					                              ,YD_GP
					                              ,YD_BAY_GP
					                              ,DECODE(YD_EQP_STAT,'B','C','O') AS YD_EQP_STAT
					                              ,SUBSTR(YD_EQP_NO,-1) AS CRN_NO 
					                              ,DECODE(YD_EQP_GP,'CR','CR','S'||SUBSTR(YD_EQP_ID,-1)) AS YD_SCH_GP
					                        FROM   TB_YS_EQP
					                        WHERE  YD_EQP_GP IN ('CR','SC')
					                   )
					            GROUP BY YD_GP, YD_BAY_GP , YD_SCH_GP             
					         ) B
					    WHERE A.YD_SCH_CD LIKE :V_YD_SCH_CD || '%'
					    AND   A.YD_DATA_GP = 'M'
					    AND   A.YD_SCH_GP = B.YD_SCH_GP
					    AND   A.YD_GP = B.YD_GP
					    AND   A.YD_BAY_GP = B.YD_BAY_GP
					    AND   A.YD_CRN_STAT1 = B.STAT1
					    AND   A.YD_CRN_STAT2 = B.STAT2
					    ORDER BY A.YD_GP, A.YD_BAY_GP, A.YD_SCH_CD
							    	*/	    	 
			    	rsResult1 = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
			    	
			    	if (rsResult1 == null || rsResult1.size() <= 0) {
			    		szMsg = "["+methodNm+"] 스케줄 기준 조회 data not found";
						commUtils.printLog(logId, szMsg, "SL");
						throw new Exception("스케줄 기준 조회 실패");
					}
					
					//레코드 추출
					rsResult1.first();
					recPara = rsResult1.getRecord();
					
					szSCH_PRIOR = commUtils.trim(recPara.getFieldString("YD_WRK_CRN_PRIOR"       ));	
					szCurrCrn 	= commUtils.trim(recPara.getFieldString("YD_WRK_CRN"       ));	
	
					//생성한 작업예약ID
					//com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdWrkBook
					szYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
					
					if ("".equals(szYD_WBOOK_ID)) {
						throw new Exception("작업예약ID 생성 실패");
					}
	
					//작업예약항목SETTING
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	
			    	recInTemp.setField("YD_WBOOK_ID",      szYD_WBOOK_ID);
			    	recInTemp.setField("REGISTER",         "");
			    	recInTemp.setField("YD_GP",            szYD_SCH_CD.substring(0, 1));
			    	recInTemp.setField("YD_BAY_GP",        szYD_SCH_CD.substring(1, 2));
			    	recInTemp.setField("YD_AIM_YD_GP",     szYD_SCH_CD.substring(0, 1));
			    	recInTemp.setField("YD_AIM_BAY_GP",    szYD_SCH_CD.substring(1, 2));
			    	recInTemp.setField("YD_SCH_PRIOR", 	   szSCH_PRIOR);
			    	recInTemp.setField("YD_SCH_CD",        szYD_SCH_CD);
			    	recInTemp.setField("CAR_NO",       	   szCAR_NO);
			    	recInTemp.setField("YD_CAR_USE_GP",    szYD_CAR_USE_GP);
	
					//com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook
					commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
					
			    	//작업예약재료 등록
					
		    		JDTORecord recOutTemp = JDTORecordFactory.getInstance().create();
			    	for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
			    		rsResult.absolute(Loop_i);
			    		recInTemp  = JDTORecordFactory.getInstance().create();
			    		recOutTemp.setRecord(rsResult.getRecord());
			    		
			    		recInTemp.setField("YD_WBOOK_ID",    szYD_WBOOK_ID);
			    		recInTemp.setField("REGISTER",       "");
			    		recInTemp.setField("YS_STK_BED_NO",  commUtils.trim(recOutTemp.getFieldString("YS_STK_BED_NO"       )));
			    		recInTemp.setField("YS_STK_LYR_NO",  commUtils.trim(recOutTemp.getFieldString("YS_STK_LYR_NO"       )));
			    		recInTemp.setField("YS_STK_SEQ_NO",  commUtils.trim(recOutTemp.getFieldString("YS_STK_SEQ_NO"       )));
			    		recInTemp.setField("SSTL_NO",        commUtils.trim(recOutTemp.getFieldString("SSTL_NO"       )));
			    		recInTemp.setField("YD_UP_COLL_SEQ", "" + Loop_i);
			    		
			    		commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
			    		
			    	}
				}		    	
				
				
				
				
				szMsg = "제품 출하상차LOT편성 후 차량상차 작업요구  시작";
				commUtils.printLog(logId, szMsg, "SL");
					
				
				recInTemp = JDTORecordFactory.getInstance().create();
				JDTORecord recCarSchPara = JDTORecordFactory.getInstance().create();
				JDTORecordSet rsCarSchResult = JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp.setField("CAR_NO" , szCAR_NO);
				
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschCarNoCarNo
				SELECT *
				 FROM(
				        SELECT 
				            ARR_WLOC_CD
				            ,YD_PNT_CD3
				            ,YD_PNT_CD4
				            ,CARD_NO             
				            ,CAR_NO
				            ,TRANS_ORD_DATE
				            ,TRANS_ORD_SEQNO
				            ,DEL_YN              
				            ,MODIFIER            
				            ,MOD_DDTT            
				            ,REGISTER            
				            ,REG_DDTT            
				            ,SPOS_WLOC_CD
				            ,YD_PNT_CD1
				            ,YD_PNT_CD2
				            ,TRN_EQP_CD          
				            ,YD_CARLD_ARR_DT     
				            ,YD_CARLD_CHK_DT     
				            ,YD_CARLD_CMPL_DT    
				            ,YD_CARLD_LEV_DT     
				            ,YD_CARLD_LEV_LOC    
				            ,YD_CARLD_PNT_WO_DT  
				            ,YD_CARLD_SCH_REQ_GP 
				            ,YD_CARLD_STOP_LOC   
				            ,YD_CARLD_ST_DT      
				            ,YD_CARLD_WRK_ACT_GP 
				            ,YD_CARLD_WRK_BOOK_ID
				            ,YD_CARUD_ARR_DT     
				            ,YD_CARUD_CHK_DT     
				            ,YD_CARUD_CMPL_DT    
				            ,YD_CARUD_LEV_DT     
				            ,YD_CARUD_PNT_WO_DT  
				            ,YD_CARUD_SCH_REQ_GP 
				            ,YD_CARUD_STOP_LOC   
				            ,YD_CARUD_ST_DT      
				            ,YD_CARUD_WRK_ACT_GP 
				            ,YD_CARUD_WRK_BOOK_ID
				            ,YD_CAR_PROG_STAT    
				            ,YD_CAR_SCH_ID       
				            ,YD_CAR_USE_GP       
				            ,YD_EQP_ID           
				            ,YD_EQP_WRK_SH       
				            ,YD_EQP_WRK_STAT     
				            ,YD_EQP_WRK_WT       
				        --    ,YD_STK_BED_TP       
				            ,YD_TRN_WRK_DELY_CD  
				            ,YD_WRK_PROG_STAT    
				        FROM TB_YS_CARSCH A
				        WHERE CAR_NO = :V_CAR_NO 
				        AND DEL_YN = 'N'
				        ORDER BY YD_CAR_SCH_ID DESC
				        )
				WHERE ROWNUM<=1 
				*/
				
				rsCarSchResult = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschCarNoCarNo", logId, methodNm, "스케줄 기준 조회"); 
		    	
		    	if (rsCarSchResult == null || rsCarSchResult.size() <= 0) {
		    		szMsg = "["+methodNm+"] 스케줄 기준 조회 data not found";
					commUtils.printLog(logId, szMsg, "SL");
					throw new Exception("스케줄 기준 조회 실패");
				}
				/* 
				 * 차량스케줄을 조회해서 상차작업예약ID와 차량진행상태 등의 상태값을 변경한다.
				 */
				rsCarSchResult.first();
				recCarSchPara = rsCarSchResult.getRecord();
				String szYD_CAR_SCH_ID 		= StringHelper.evl(recCarSchPara.getFieldString("YD_CAR_SCH_ID"), "");
				String szYD_EQP_WRK_STAT	= StringHelper.evl(recCarSchPara.getFieldString("YD_EQP_WRK_STAT"), ""); 
				szMsg = "["+szOperationName+"] 차량스케쥴["+szYD_CAR_SCH_ID+"] 조회 성공 후 차량도착상태로 변경 시작";
				commUtils.printLog(logId, szMsg, "SL");
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				recInTemp.setField("MODIFIER", szMethodName.substring(0, 10));
				
				
			 
				if(szYD_EQP_WRK_STAT.equals("L")){
					recInTemp.setField("YD_CAR_PROG_STAT"		, "B");									//하차도착상태 
					recInTemp.setField("YD_CARUD_WRK_BOOK_ID"	, szYD_WBOOK_ID);
					recInTemp.setField("YD_CARUD_STOP_LOC"	 	, szYD_CARLD_STOP_LOC);
					recInTemp.setField("YD_CARUD_ARR_DT"	 	, commUtils.getDateTime14());
					recInTemp.setField("YD_PNT_CD3"          	, szSPOS_YD_PNT_CD);
				}else{
					recInTemp.setField("YD_CAR_PROG_STAT"		, "2");									//상차도착상태
					recInTemp.setField("YD_CARLD_WRK_BOOK_ID"	, szYD_WBOOK_ID);
					recInTemp.setField("YD_CARLD_STOP_LOC"		, szYD_CARLD_STOP_LOC);
					recInTemp.setField("YD_CARLD_ARR_DT"		, commUtils.getDateTime14());
					recInTemp.setField("YD_PNT_CD1"         	, szSPOS_YD_PNT_CD);
				}
			
				
				if(!"".equals(szSPOS_WLOC_CD)) {
					recInTemp.setField("SPOS_WLOC_CD"         , szSPOS_WLOC_CD); //2014.02.18 cho 발지개소코드를 파라메터 받은 값으로 설정한다.
				}
				
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarschByCarschid
				UPDATE TB_YS_CARSCH
				   SET MODIFIER     = :V_MODIFIER
				     , MOD_DDTT     = SYSDATE
				     , YD_CARUD_ARR_DT  = NVL(TO_DATE(:V_YD_CARUD_ARR_DT,'YYYYMMDDHH24MISS'),YD_CARUD_ARR_DT)
				     , YD_CARLD_ARR_DT  = NVL(TO_DATE(:V_YD_CARLD_ARR_DT,'YYYYMMDDHH24MISS'),YD_CARLD_ARR_DT)
				     , YD_CAR_PROG_STAT = NVL(:V_YD_CAR_PROG_STAT,YD_CAR_PROG_STAT)
				     , SPOS_WLOC_CD     = NVL(:V_SPOS_WLOC_CD,SPOS_WLOC_CD)
				     , YD_PNT_CD1       = NVL(:V_YD_PNT_CD1,YD_PNT_CD1)
				     , YD_CARLD_STOP_LOC= NVL(:V_YD_CARLD_STOP_LOC,YD_CARLD_STOP_LOC)
				     , YD_CARUD_WRK_BOOK_ID= NVL(:V_YD_CARUD_WRK_BOOK_ID,YD_CARUD_WRK_BOOK_ID)
				     , YD_YD_CARUD_STOP_LOC= NVL(:V_YD_CARUD_STOP_LOC,YD_CARUD_STOP_LOC)
				     , YD_PNT_CD3       = NVL(:V_YD_PNT_CD3,YD_PNT_CD3)
				     , YD_CARLD_WRK_BOOK_ID = NVL(:V_YD_CARLD_WRK_BOOK_ID,YD_CARLD_WRK_BOOK_ID)
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				 */
				int intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarschByCarschid", logId, methodNm, "차량스케줄 갱신");

				
				if( intRtnVal == 0 ) {
					szMsg = "["+szOperationName+"] 차량스케쥴["+szYD_CAR_SCH_ID+"] 조회 성공 후 차량도착상태로 변경 시 차량스케줄이 존재하지 않습니다.";
					commUtils.printLog(logId, szMsg, "SL");
					throw new DAOException(szMsg);
				}else if( intRtnVal < 0 ) {
					szMsg = "["+szOperationName+"] 차량스케쥴["+szYD_CAR_SCH_ID+"] 조회 성공 후 차량도착상태로 변경 시 차량스케줄이 존재하지 않습니다.";
					commUtils.printLog(logId, szMsg, "SL");
					throw new DAOException(szMsg);
				}
				
				szMsg = "["+szOperationName+"] 차량스케쥴["+szYD_CAR_SCH_ID+"] 조회 성공 후 차량도착상태로 변경 완료";
				commUtils.printLog(logId, szMsg, "SL");
				
				/* 
				 * 크레인스케줄Main을 호출한다.
				 */
				//크레인스케줄MAIN호출 TC :/** 1:블름,2:빌렛,3:봉강,4:선재	 */
		    	String szJMS_TC_CD = "";
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setResultCode(logId);	//Log ID
		    	recInTemp.setResultMsg(methodNm);	//Log Method Name
		    	String szYdGpBay = szYD_SCH_CD.substring(0, 2);

		    	if(szYdGpBay.startsWith("B") ){	    		
		    		szJMS_TC_CD = "YSYSJ102";
		    	}else if(szYdGpBay.startsWith("C")){
		    		szJMS_TC_CD = "YSYSJ202";
		    	}else if(szYdGpBay.startsWith("KB")){
		    		szJMS_TC_CD = "YSYSJ302";
		    	}else if(szYdGpBay.startsWith("KE")){
		    		szJMS_TC_CD = "YSYSJ402";
		    	}
		    	if (!szJMS_TC_CD.equals("")) {
		    		recInTemp.setField("JMS_TC_CD",    	szJMS_TC_CD);
		    		recInTemp.setField("JMS_TC_CREATE_DDTT"	,  commUtils.getDateTime14());
			    	recInTemp.setField("YD_SCH_CD", 	szYD_SCH_CD);
			    	recInTemp.setField("YD_EQP_ID", 	szCurrCrn);
			    	recInTemp.setField("YD_WBOOK_ID", 	szYD_WBOOK_ID);
			    	
			    	szMsg="> 크레인스케줄메인을 호출 전 전문내용";
			    	commUtils.printLog(logId, szMsg, "SL");
					
					sndRecord = commUtils.addSndData(sndRecord, recInTemp);						
				
		    	}	
				szMsg="["+szOperationName+"] 출하차량스케줄 수정 전문 전송 성공";
				commUtils.printLog(logId, szMsg, "SL");	
				
			}	
			szMsg = "제품 출하상차LOT편성 후 차량상차 작업요구 송신 완료! [" +  szBayRGp + "]";
			commUtils.printLog(logId, methodNm, "S-");
		
		} catch(Exception e){
			szMsg = "출하상차LOT편성  Error : " + e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
			return sndRecord;
		}
		return sndRecord;
	} //end of procPlGdsDistCarLdLotComp
	
	/**
	 * 오퍼레이션명 : 출하차량도착실적처리 - 맵활성화
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param szSPOS_WLOC_CD
	 * @param szSPOS_YD_PNT_CD
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */
	public int procOutCarArrWr(JDTORecord msgRecord, JDTORecordSet rsStkCol) throws DAOException {
		String methodNm = " 출하차량도착실적처리 - 맵활성화[YsCommCarMvSeEJB.procOutCarArrWr] < " + msgRecord.getResultMsg();
		String logId = msgRecord.getResultCode();
		String szMsg                = "";
		String szMethodName			= "procOutCarArrWr";
		JDTORecord 		recInTemp 	= null;
		JDTORecordSet 	rsTemp 		= null;
		
		int intRtnVal = 0;
		int intLevLocGp = 0;
		// 쿼리
		String szYD_CARLD_STOP_LOC = "";
	    
	    try {
	    	commUtils.printLog(logId, methodNm, "S+");
	    	
	    	String szSPOS_WLOC_CD 	= commUtils.trim(msgRecord.getFieldString("SPOS_WLOC_CD"       ));
	    	String szSPOS_YD_PNT_CD = commUtils.trim(msgRecord.getFieldString("SPOS_YD_PNT_CD"       ));
	    	String szCAR_NO 		= commUtils.trim(msgRecord.getFieldString("CAR_NO"       ));
			String szCARD_NO 		= commUtils.trim(msgRecord.getFieldString("CARD_NO"       )); 
			
			szMsg="["+szMethodName+"] 차량도착 개소코드[" + szSPOS_WLOC_CD + "], 차량도착포인트코드[" + szSPOS_YD_PNT_CD + "], 차량번호[" + szCAR_NO + "], 카드번호[" + szCARD_NO + "]";
			commUtils.printLog(logId, szMsg, "SL");
			
	    	//착지개소코드와 착지야드포인트코드로 적치열을 조회한다.
	    	//rsStkCol = JDTORecordFactory.getInstance().createRecordSet("Temp");
			recInTemp 	= JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("WLOC_CD",   szSPOS_WLOC_CD);
	    	recInTemp.setField("YD_PNT_CD", szSPOS_YD_PNT_CD);
	    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolWLocCdandPntCd */
	    	//intLevLocGp = ydStkColDao.getYdStkcol(recInTemp, rsStkCol, 4);
	    	/*
	    	SELECT A.YS_STK_COL_GP                         AS YS_STK_COL_GP
	    	      ,TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
	    	      ,A.REGISTER                              AS REGISTER
	    	      ,TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
	    	      ,A.MODIFIER                              AS MODIFIER
	    	      ,A.DEL_YN                                AS DEL_YN
	    	      ,A.YD_GP                                 AS YD_GP
	    	      ,A.YD_BAY_GP                             AS YD_BAY_GP
	    	      ,A.YD_EQP_GP                             AS YD_EQP_GP
	    	      ,A.YD_STK_COL_NO                         AS YD_STK_COL_NO
	    	      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.YD_STK_COL_ACT_STAT   ELSE A.YD_STK_COL_ACT_STAT  END ) AS YD_STK_COL_ACT_STAT     
	    	      ,A.YD_STK_COL_RULE_XAXIS                 AS YD_STK_COL_RULE_XAXIS 
	    	      ,A.YD_STK_COL_RULE_YAXIS                 AS YD_STK_COL_RULE_YAXIS 
	    	      ,A.YD_STK_COL_W                          AS YD_STK_COL_W                   
	    	      ,A.YD_STK_COL_L                          AS YD_STK_COL_L                   
	    	      ,A.YD_CAR_USE_GP                         AS YD_CAR_USE_GP                 
	    	      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.TRN_EQP_CD ELSE A.TRN_EQP_CD END)    AS TRN_EQP_CD                       
	    	      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.CAR_NO  ELSE  A.CAR_NO END)                 AS CAR_NO                               
	    	      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.CARD_NO   ELSE A.CARD_NO END)               AS CARD_NO                             
	    	      ,A.WLOC_CD                               AS WLOC_CD                             
	    	      ,A.YD_PNT_CD                             AS YD_PNT_CD   
	    	      ,B.YD_CARPNT_CD AS YD_CARPNT_CD
	    	  FROM TB_YS_STKCOL A   
	    	     , TB_YS_CARPOINT B
	    	 WHERE B.YS_STK_COL_GP=A.YS_STK_COL_GP
	    	   AND A.WLOC_CD =  :V_WLOC_CD
	    	   AND A.YD_PNT_CD = :V_YD_PNT_CD  	
	    	 */	    	
	    	rsTemp = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolWLocCdandPntCd", logId, methodNm, "적치열 조회"); 
	    	intLevLocGp = rsTemp.size();
	    	
	    	if (rsTemp == null || intLevLocGp == 0) {
	    		szMsg= "[" + methodNm + "] 발지개소["+szSPOS_WLOC_CD+"] 및 포인트 코드["+szSPOS_YD_PNT_CD+"]가 타공정코드가 아니고 대기장입니다.";
	    		commUtils.printLog(logId, szMsg, "SL");
	    		return -1;
	    	}
	    	
	    	rsStkCol.addAll(rsTemp);
	    	
	    	if(intLevLocGp > 0) {
	    		szMsg= "["+szMethodName+"] 개소코드와 야드포인트코드로 적치열 조회 성공";
	    		commUtils.printLog(logId, szMsg, "SL");
	    		JDTORecord recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsTemp.getRecord(0));
	    	
		    	// 적치열구분을 조회 (도착지)
		    	szYD_CARLD_STOP_LOC = commUtils.trim(recOutTemp.getFieldString("YS_STK_COL_GP"       )); 
		    	if( szYD_CARLD_STOP_LOC.trim().equals("") ) {
		    		szMsg = "["+szMethodName+"]  적치열구분(상차정지위치)이 존재하지 않음";
		    		throw new DAOException(szMsg );
		    	}
		    	// 적치열 테이블에 활성상태 처리 
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YS_STK_COL_GP"      , szYD_CARLD_STOP_LOC);
		    	recInTemp.setField("YD_STK_COL_ACT_STAT", "L");
		    	recInTemp.setField("YD_CAR_USE_GP"      , "G");
		    	recInTemp.setField("TRN_EQP_CD"         , "");
		    	recInTemp.setField("CAR_NO"             , szCAR_NO);
		    	recInTemp.setField("CARD_NO"             , szCARD_NO);
		    	
		    	
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
		    	intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStat", logId, methodNm, "TB_YS_STKCOL 등록");
				if(intRtnVal <= 0) {
					szMsg="[" + methodNm + "] 적치열[" + szYD_CARLD_STOP_LOC + "]활성화중 ERROR 발생.";
					commUtils.printLog(logId, methodNm, "SL");
					throw new DAOException(szMsg);
				}
			
		    	
				//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			    this.YsCarPointinforeg2("C",szCAR_NO,szCARD_NO,szYD_CARLD_STOP_LOC,"","","L",logId,methodNm);
				
				 // 적치베드 비활성상태로 변경
				szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CARLD_STOP_LOC+"]의 베드를 비활성상태와 BED중량MAX기본값으로 변경처리 시작 ";
				commUtils.printLog(logId, szMsg, "SL");
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_BED_WT_MAX"		, YsConstant.YD_STK_BED_WT_MAX_DEFAULT);
				recInTemp.setField("YS_STK_COL_GP"			, szYD_CARLD_STOP_LOC);
				recInTemp.setField("YD_STK_BED_ACT_STAT"	, "L");
				
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedYdStkColGp  

				UPDATE TB_YS_STKBED
				   SET MOD_DDTT     = SYSDATE             
					 , MODIFIER     = :V_MODIFIER             
					 , YD_STK_BED_ACT_STAT = NVL(:V_YD_STK_BED_ACT_STAT,YD_STK_BED_ACT_STAT)
				     , YD_STK_BED_WT_MAX = NVL(:V_YD_STK_BED_WT_MAX,YD_STK_BED_WT_MAX )
				  WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
				  */
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedYdStkColGp", logId, methodNm, "TB_YS_STKBED 등록");
				if(intRtnVal <= 0) {
					szMsg="[" + methodNm + "] 적치BED[" + szYD_CARLD_STOP_LOC + "]활성화중 ERROR 발생.";
					commUtils.printLog(logId, methodNm, "SL");
					throw new DAOException(szMsg);
				}
			    // 적치단 비활성상태로 변경
				szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CARLD_STOP_LOC+"]의 적치단을 활성상태로 변경처리 시작 ";
				commUtils.printLog(logId, methodNm, "SL");
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YS_STK_COL_GP"		, szYD_CARLD_STOP_LOC);
				recInTemp.setField("YD_STK_LYR_ACT_STAT", "E");
				recInTemp.setField("SSTL_NO"			, "");
				recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
		    	
				//intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGp(recInTemp);
		    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear  

				UPDATE TB_YS_STKLYR            
				   SET MOD_DDTT     = SYSDATE             
					 , MODIFIER     = :V_MODIFIER             
					 , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
				     , SSTL_NO = null
				     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
				 WHERE YS_STK_COL_GP  = :V_YS_STK_COL_GP
				 */
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "TB_YS_STKLYR 등록");
				
				if(intRtnVal <= 0) {
					szMsg="[" + methodNm + "] 적치단[" + szYD_CARLD_STOP_LOC + "]활성화중 ERROR 발생.";
					commUtils.printLog(logId, szMsg, "SL");
					throw new DAOException(szMsg);

				}	
				
				msgRecord.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
				
				commUtils.printLog(logId, methodNm, "S-");
	    	}else{
	    		szMsg= "["+szMethodName+"]  getYdStkcol 개소코드와 야드포인트코드로 적치열 조회 실패";
	    		commUtils.printLog(logId, methodNm, "SL");
				throw new DAOException(szMsg);
	    	}
	    }catch(JDTOException e) {
	    	szMsg= szMethodName + " Error:" +e.getMessage();
	    	commUtils.printLog(logId, methodNm, "SL");
			throw new DAOException(e);
	    }
	    return intRtnVal;
	}

		
	/**
	 * [A] 오퍼레이션명 : 제품반입차량도착실적처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param szSPOS_WLOC_CD
	 * @param szSPOS_YD_PNT_CD
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord RcptCarArrWr(JDTORecord rcvMsg)throws DAOException  {
		String methodNm = "제품반입차량도착실적처리[YsCommCarMvSeEJB.RcptCarArrWr] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		String szMsg                   = "";
	    JDTORecord 	sndRecord	        = JDTORecordFactory.getInstance().create();

	    try{

	    	commUtils.printLog(logId, methodNm, "S+");

	    	String szTRANS_ORD_DT = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"));
			if(szTRANS_ORD_DT.equals("")) {
				szMsg = "["+methodNm+"][전문 이상] 운송지시일자가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			}	    	
	    	
			String szTRANS_ORD_SEQNO = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			if(szTRANS_ORD_SEQNO.equals("")) {
				szMsg = "["+methodNm+"][전문 이상] 운송지시순번이 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			}	    	

			String szCAR_NO = commUtils.trim(rcvMsg.getFieldString("CAR_NO"));
			if(szCAR_NO.equals("")) {
				szMsg = "["+methodNm+"][전문 이상] 차량번호가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			}	    	
			
			String szSPOS_WLOC_CD = commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"));
			if(szSPOS_WLOC_CD.equals("")) {
				szMsg = "["+methodNm+"][전문 이상] 발지개소코드가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			}	    	
			
			String szYD_PNT_CD = commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD")); 
			if(szYD_PNT_CD.equals("")) {
				szMsg = "["+methodNm+"][전문 이상] 발지개소포인트 코드가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			}	    	
			String szYD_CAR_STOP_LOC = commUtils.trim(rcvMsg.getFieldString("YD_CAR_STOP_LOC")); 
			if(szYD_CAR_STOP_LOC.equals("")) {
				szMsg = "["+methodNm+"][전문 이상] 차량정지위치가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			}	    	
			String szYD_CAR_SCH_ID = commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID")); 
			if(szYD_CAR_SCH_ID.equals("")) {
				szMsg = "["+methodNm+"][전문 이상] 차량SCH_ID가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new Exception(szMsg);
			}	    	
			
	    	
			JDTORecordSet rsStkCol = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	//반입차량도착실적처리 - 맵활성화
			if( this.procOutCarArrWr(rcvMsg, rsStkCol) > 0 ) {
				
				String szYD_CARLD_STOP_LOC = commUtils.trim(rcvMsg.getFieldString("YD_CARLD_STOP_LOC"));
				
				// 전문 생성 및 전송
				JDTORecord recSndQue = JDTORecordFactory.getInstance().create();
				recSndQue.setResultCode(logId);	//Log ID
				recSndQue.setResultMsg(methodNm);	//Log Method Name
		    	recSndQue.setField("YD_CARLD_STOP_LOC"	  , szYD_CARLD_STOP_LOC);							//차량상차정지위치
				recSndQue.setField("YD_CAR_USE_GP"    	  , "G");	//차량사용구분(G:출하차량, L:구내운송) - 출하차량만 적용
				recSndQue.setField("YD_GP"           	  , szYD_CARLD_STOP_LOC.substring(0, 1));			//야드
				recSndQue.setField("YD_BAY_GP"        	  , szYD_CARLD_STOP_LOC.substring(1, 2)); 			//동
				recSndQue.setField("TRANS_ORD_DATE"   	  , commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT")));		//운송지시일자
				recSndQue.setField("TRANS_ORD_SEQNO" 	  , commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")));	//운송지시순번
				recSndQue.setField("CAR_NO"           	  , commUtils.trim(rcvMsg.getFieldString("CAR_NO")));			//차량번호
				recSndQue.setField("CARD_NO"          	  , commUtils.trim(rcvMsg.getFieldString("CARD_NO")));			//카드번호
				recSndQue.setField("SPOS_WLOC_CD"         , commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD")));		//발지개소코드
				recSndQue.setField("SPOS_YD_PNT_CD"       , commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD")));	//발지야드포인트코드
				recSndQue.setField("YD_CAR_SCH_ID"        , szYD_CAR_SCH_ID);	
				
				
				//제품출하하차LOT편성
				JDTORecord jrRtn =  this.procGdsDistCarUdLotComp(recSndQue);
				
				sndRecord = commUtils.addSndData(sndRecord,jrRtn);	
				
				
				/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				 * 업무기준 : 제품입고  영차 도착 시 저장위치 제원 야드L2로 전송
	    		 *** 야드BLOOM:N1,BILLET:N2,선재:N3,봉강:N4,선재자동화:N5,봉강자동화:N6 	
		             YSN1L001 저장위치제원
	    		 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
		    	JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setResultCode(logId);	//Log ID
		    	recInTemp.setResultMsg(methodNm);	//Log Method Name
		    	String szYdGpBay = szYD_CARLD_STOP_LOC.substring(0, 2);
		    	String szJMS_TC_CD = "";
				if(szYdGpBay.startsWith("KA")){
					szJMS_TC_CD =  "YSN6L001";
	          	}else if(szYdGpBay.startsWith("KB")){
					szJMS_TC_CD =  "YSN4L001";
		    	}else if(szYdGpBay.startsWith("KD")){
					szJMS_TC_CD =  "YSN5L001";
		    	}else if(szYdGpBay.startsWith("KE")){
					szJMS_TC_CD =  "YSN3L001";
		    	}
				recInTemp = JDTORecordFactory.getInstance().create();
	    		recInTemp.setField("MSG_ID",    szJMS_TC_CD);
				recInTemp.setField("YD_INFO_SYNC_CD", "3");						//1:동,2:SPAN,3:열,4:BED
				recInTemp.setField("YD_GP", "K");
				recInTemp.setField("YS_STK_COL_GP", szYD_CAR_STOP_LOC);
				recInTemp.setField("YD_CAR_PROG_STAT", "2");
				recInTemp.setField("YD_EQP_WRK_STAT" , "U");

				//전송 Data 생성
				sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2(szJMS_TC_CD, recInTemp));
				
				commUtils.printParam(logId, sndRecord);
				
				szMsg="["+methodNm+"] 반입 도착 시 저장위치 제원 야드L2로 전송";
				commUtils.printLog(logId, szMsg, "SL");	
				
	    		commUtils.printLog(logId, methodNm, "S-");  		
				
				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	    	}else{
	    		commUtils.printLog(logId, methodNm, "S-");  		
	    		return sndRecord;
	    	}//end of if			
			
		}catch(Exception e){
			szMsg="["+methodNm+"][ 반입 도착 처리 ] Error:" +e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");	
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}
			
		szMsg="[ 영차 도착 처리 ] 처리(procCarBayInOrdReq) 완료";
	 	commUtils.printLog(logId, methodNm+szMsg, "S-");
		return sndRecord;
		
	}// end of procCarBayInOrdReq()

	/**
	 * 오퍼레이션명 : 제품반입하차LOT편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	
	public JDTORecord procGdsDistCarUdLotComp(JDTORecord rcvMsg) throws JDTOException  {
		String methodNm = "제품반입하차LOT편성[YsCommCarMvSeEJB.procGdsDistCarUdLotComp] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		JDTORecord recInTemp	= null;
		JDTORecord sndRecord	= null;
		String szMsg            = "";
		String szMethodName    	= "procGdsDistCarUdLotComp";
		String szOperationName	= "제품반입하차LOT편성";
		
		try {

			commUtils.printLog(logId, methodNm, "S+");
			
			String szYD_CARLD_STOP_LOC = commUtils.trim(rcvMsg.getFieldString("YD_CARLD_STOP_LOC")); // 상차정지위치
			if(szYD_CARLD_STOP_LOC.equals("")){
				szMsg = "["+methodNm+"][전문 이상] 상차정지위치가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				return sndRecord;
			}
			String szYD_CAR_USE_GP = commUtils.trim(rcvMsg.getFieldString("YD_CAR_USE_GP"       ));  // 차량사용구분
			if(szYD_CAR_USE_GP.equals("") || !szYD_CAR_USE_GP.equals("G")){
				szMsg = "["+methodNm+"][전문 이상] 차량사용구분(" + szYD_CAR_USE_GP + ")";
				commUtils.printLog(logId, szMsg, "SL");
				return sndRecord;
			}
			String szCAR_NO = commUtils.trim(rcvMsg.getFieldString("CAR_NO"       )); // 차량번호
			if(szCAR_NO.equals("")){
				szMsg = "["+methodNm+"][전문 이상] 차량번호가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				return sndRecord;
			}
			String szTRANS_ORD_DATE = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DATE"       ));   // 운송지시 일자
			if(szTRANS_ORD_DATE.equals("")){
				szMsg = "["+methodNm+"][전문 이상] 운송지시 일자가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				return sndRecord;
			}
			String szTRANS_ORD_SEQNO = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"       )); // 운송지시 순번
			if(szTRANS_ORD_SEQNO.equals("")){
				szMsg = "["+methodNm+"][전문 이상] 운송지시 순번이 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				return sndRecord;
			}
			String szYdGp = commUtils.trim(rcvMsg.getFieldString("YD_GP"));  // 야드
			if(szYdGp.equals("")){
				szMsg = "["+methodNm+"][전문 이상] 야드구분이 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				return sndRecord;
			}
			String szBayRGp = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"));  // 동
			if(szBayRGp.equals("")){
				szMsg = "["+methodNm+"][전문 이상] 동구분이 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				return sndRecord;
			}
			String szSPOS_WLOC_CD = commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD")); // 발지개소코드 
			if(szSPOS_WLOC_CD.equals("")){
				szMsg = "["+methodNm+"][전문 이상] 발지개소코드가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				return sndRecord;
			}
			String szSPOS_YD_PNT_CD = commUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD"));  // 발지포인트 코드 
			if(szSPOS_YD_PNT_CD.equals("")){
				szMsg = "["+methodNm+"][전문 이상] 발지개소코드가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				return sndRecord;
			}		
			String szYD_CAR_SCH_ID = commUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"));  //차량스케쥴 ID
			if(szYD_CAR_SCH_ID.equals("")){
				szMsg = "["+methodNm+"][전문 이상] 차량스케쥴 ID 가 없습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				return sndRecord;
			}		
			
			// 스케쥴코드  생성

			String szYD_SCH_CD = ""; 
			if(szYD_CARLD_STOP_LOC.substring(1,2).equals("B")) {
				szYD_SCH_CD = "KBTR10CM";
			} else if(szYD_CARLD_STOP_LOC.substring(1,2).equals("D")) {	
				szYD_SCH_CD = "KDTR10CM";
			} else {
				szYD_SCH_CD = "KETR10CM";
			}
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_SCH_CD"             , szYD_SCH_CD);
			JDTORecord jrSchCd = gdsYsComm.chkSchCdEqp(recInTemp);
			
			String szYD_SCH_PRIOR	= commUtils.trim(jrSchCd.getFieldString("YD_SCH_PRIOR"));	//야드스케쥴우선순위
			String szYD_EQP_ID 	    = commUtils.trim(jrSchCd.getFieldString("YD_EQP_ID"));		//설비ID

			JDTORecord recOutTemp = null;
			JDTORecord recOutPara = null;
			String szYD_WBOOK_ID = "";
			String szFIR_YD_WBOOK_ID = "";
			String szMSG_ID = "";
			String szYdGpBay = szYD_CARLD_STOP_LOC.substring(0,2);
			
			JDTORecord recInPara = JDTORecordFactory.getInstance().create();
			recInPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarftmvmtlID 
			SELECT A.YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
			     , A.SSTL_NO  AS SSTL_NO
			     , A.REGISTER  AS REGISTER
			     , TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
			     , A.MODIFIER  AS MODIFIER
			     , TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
			     , A.DEL_YN  AS DEL_YN
			     , A.YS_STK_BED_NO  AS YS_STK_BED_NO
			     , A.YS_STK_LYR_NO  AS YS_STK_LYR_NO
			     , A.YS_STK_SEQ_NO  AS YS_STK_SEQ_NO
			     , A.HCR_GP  AS HCR_GP
			     , A.STL_PROG_CD  AS STL_PROG_CD
			     , A.YS_MTL_ITEM  AS YS_MTL_ITEM
			     , B.YD_RCPT_PLN_STR_LOC
			     , (SELECT ARR_YD_PNT_CD FROM USRTSA.TB_TS_MATL_FTMV_WO C
			         WHERE C.TRANSWORD_SEQNO=(SELECT MAX(TRANSWORD_SEQNO) FROM TB_TS_MATL_FTMV_WO D
			                                   WHERE D.STL_NO=C.STL_NO
			                                   )
			           AND C.STL_NO=A.SSTL_NO ) AS ARR_YD_PNT_CD
			     , B.CUST_CD     
			     , B.DETAIL_ARR_CD     
			     , B.HEAT_NO     
			     , B.YD_MTL_L_GP             
			     , B.CUST_CD || B.DETAIL_ARR_CD || B.HEAT_NO || B.YD_MTL_L_GP AS GROUP_CHK_ID            
			  FROM TB_YS_CARFTMVMTL A
			     , TB_YS_STOCK B
			 WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			   AND A.SSTL_NO = B.SSTL_NO(+)
			   AND A.DEL_YN='N'
			 ORDER BY YS_STK_BED_NO, YS_STK_LYR_NO DESC
			 */
			
			JDTORecordSet rsCol = commDao.select(recInPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarftmvmtlID", logId, methodNm, "재료정보 조회"); 
	    	
			
			szMsg = "["+methodNm+"]반입대상재 수 : " + rsCol.size();
			commUtils.printLog(logId, szMsg, "SL");
						
			//-------------------------------------------------------------------------------------------------
			//	작업예약 등록
			//-------------------------------------------------------------------------------------------------
			if(szYdGpBay.equals("KD") || szYdGpBay.equals("KE")) {
				// D 동은 1매씩 처리함 ,E 동은 1매씩 처리함 

				for(int Loop_i = 1; Loop_i <= rsCol.size(); Loop_i++) {
		    		rsCol.absolute(Loop_i);
		    		
		    		recOutTemp = rsCol.getRecord();
		    		
		    		szYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
		    		
					if ("".equals(szYD_WBOOK_ID)) {
						commUtils.printLog(logId, "["+methodNm+"] 작업예약ID 생성 실패", "SL");
						throw new Exception("작업예약ID 생성 실패");
					}
					
					if(Loop_i == 1){
						szFIR_YD_WBOOK_ID = szYD_WBOOK_ID;
					}
						
					
		    		recOutPara = JDTORecordFactory.getInstance().create();
		    		recOutPara.setField("YD_WBOOK_ID", 			szYD_WBOOK_ID);
					recOutPara.setField("REGISTER", 			"CarUdLot");
					recOutPara.setField("YD_GP", 				"K");
					recOutPara.setField("YD_BAY_GP", 			szYD_CARLD_STOP_LOC.substring(1,2));
					recOutPara.setField("YD_SCH_CD", 			szYD_SCH_CD);
					recOutPara.setField("YD_SCH_PRIOR", 		szYD_SCH_PRIOR);
					recOutPara.setField("YD_AIM_YD_GP", 		"K");
					recOutPara.setField("YD_AIM_BAY_GP", 		szYD_CARLD_STOP_LOC.substring(1,2));
					recOutPara.setField("YD_CAR_USE_GP", 		"G");  
					recOutPara.setField("CAR_NO", 				szCAR_NO);
					szMsg = "["+methodNm+"] 작업예약["+szYD_WBOOK_ID+"]등록 시작";
					commUtils.printLog(logId, szMsg, "SL");
	
	
					//com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook
					commDao.insert(recOutPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
					
					szMsg = "["+methodNm+"] 작업예약["+szYD_WBOOK_ID+"]등록 완료";
					commUtils.printLog(logId, szMsg, "SL");
					
					recInTemp  = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_WBOOK_ID",    szYD_WBOOK_ID);
		    		recInTemp.setField("REGISTER",       "CarUdLot");
		    		recInTemp.setField("YS_STK_COL_GP",  szYD_CARLD_STOP_LOC);
		    		recInTemp.setField("YS_STK_BED_NO",  commUtils.trim(recOutTemp.getFieldString("YS_STK_BED_NO"       )));
		    		recInTemp.setField("YS_STK_LYR_NO",  commUtils.trim(recOutTemp.getFieldString("YS_STK_LYR_NO"       )));
		    		recInTemp.setField("YS_STK_SEQ_NO",  commUtils.trim(recOutTemp.getFieldString("YS_STK_SEQ_NO"       )));
		    		recInTemp.setField("SSTL_NO",        commUtils.trim(recOutTemp.getFieldString("SSTL_NO"       )));
		    		recInTemp.setField("YD_UP_COLL_SEQ", "" + Loop_i);
		    		
		    		commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
	// 적재위치 등록
		    		
		    		
		    		recInTemp.setField("YD_STK_LYR_MTL_STAT", "C");		    	
		    		recInTemp.setField("YD_STK_LYR_ACT_STAT", "E");		    	
			    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGp2
	
					UPDATE TB_YS_STKLYR            
					   SET MOD_DDTT     = SYSDATE             
						 , MODIFIER     = :V_MODIFIER             
						 , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
					     , SSTL_NO = :V_SSTL_NO
					     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
					 WHERE YS_STK_COL_GP  = :V_YS_STK_COL_GP
					 AND YS_STK_BED_NO = :V_YS_STK_BED_NO
					 AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO
					 AND YS_STK_SEQ_NO = :V_YS_STK_SEQ_NO
					 */
					commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGp2", logId, methodNm, "TB_YS_STKLYR 등록");
					
		    		if(szYdGpBay.startsWith("KB")){
		          		szMSG_ID =  "YSN4L002";
			    	}else if(szYdGpBay.startsWith("KD")){
			    		szMSG_ID =  "YSN5L002";
			    	}	
		    		recInTemp  = JDTORecordFactory.getInstance().create();
		    		recInTemp.setResultCode(logId);	//Log ID
		    		recInTemp.setResultMsg(methodNm);	//Log Method Name
					recInTemp.setField("MSG_ID"				, szMSG_ID);
					recInTemp.setField("YD_INFO_SYNC_CD"	, "B");	//차량입고
			    	recInTemp.setField("SSTL_NO"			, commUtils.trim(recOutTemp.getFieldString("SSTL_NO"       ))); 
			    	recInTemp.setField("YS_STK_COL_GP"		, "K");
			    	//recInTemp.setField("YS_STK_BED_NO", ydDaoUtils.paraRecChkNull(recOutTemp, "YS_STK_BED_NO"));
			    	sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2(szMSG_ID, recInTemp));
			    	
			    	szMsg="["+methodNm+"] 반입도착시 저장품 제원 야드L2[" + szMSG_ID + "]로 전송";
					commUtils.printLog(logId, szMsg, "SL");				
					
		    	}	
			} else {
				// B동 봉강은   고객사 ,상세착지,HEAT 단위로 그룹핑 함
				String szGroupChkId = "";
				String szbefGroupChkId = "";
				int ydMtl_cnt = 0;
				for(int Loop_i = 1; Loop_i <= rsCol.size(); Loop_i++) {

					rsCol.absolute(Loop_i);
		    		recOutTemp = rsCol.getRecord();
		    		
					szGroupChkId = commUtils.trim(recOutTemp.getFieldString("GROUP_CHK_ID"));
					
						
					if(!szbefGroupChkId.equals(szGroupChkId)|| ydMtl_cnt >= 3 ) {
					
			    		szYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");

			    		if(Loop_i == 1){
							szFIR_YD_WBOOK_ID = szYD_WBOOK_ID;
						} 

						recOutPara = JDTORecordFactory.getInstance().create();
			    		recOutPara.setField("YD_WBOOK_ID", 			szYD_WBOOK_ID);
						recOutPara.setField("REGISTER", 			"CarUdLot");
						recOutPara.setField("YD_GP", 				"K");
						recOutPara.setField("YD_BAY_GP", 			szYD_CARLD_STOP_LOC.substring(1,2));
						recOutPara.setField("YD_SCH_CD", 			szYD_SCH_CD);
						recOutPara.setField("YD_SCH_PRIOR", 		szYD_SCH_PRIOR);
						recOutPara.setField("YD_AIM_YD_GP", 		"K");
						recOutPara.setField("YD_AIM_BAY_GP", 		szYD_CARLD_STOP_LOC.substring(1,2));
						recOutPara.setField("YD_CAR_USE_GP", 		"G");  
						recOutPara.setField("CAR_NO", 				szCAR_NO);
						szMsg = "["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"]등록 시작";
						commUtils.printLog(logId, szMsg, "SL");
		
		
						//com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook
						commDao.insert(recOutPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
						
						szMsg ="["+methodNm+"] 작업예약["+szYD_WBOOK_ID+"]등록 완료";
						commUtils.printLog(logId, szMsg, "SL");
						szbefGroupChkId = szGroupChkId;
						ydMtl_cnt = 0;
					}
					
					ydMtl_cnt ++;
					recInTemp  = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_WBOOK_ID",    szYD_WBOOK_ID);
		    		recInTemp.setField("REGISTER",       "CarUdLot");
		    		recInTemp.setField("YS_STK_COL_GP",  szYD_CARLD_STOP_LOC);
		    		recInTemp.setField("YS_STK_BED_NO",  commUtils.trim(recOutTemp.getFieldString("YS_STK_BED_NO")));
		    		recInTemp.setField("YS_STK_LYR_NO",  commUtils.trim(recOutTemp.getFieldString("YS_STK_LYR_NO")));
		    		recInTemp.setField("YS_STK_SEQ_NO",  commUtils.trim(recOutTemp.getFieldString("YS_STK_SEQ_NO")));
		    		recInTemp.setField("SSTL_NO",        commUtils.trim(recOutTemp.getFieldString("SSTL_NO")));
		    		recInTemp.setField("YD_UP_COLL_SEQ", "" + Loop_i);
		    		
		    		commDao.insert(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
	// 적재위치 등록
		    		
		    		
			    	recInTemp.setField("YD_STK_LYR_MTL_STAT", "C");		    	
			    	recInTemp.setField("YD_STK_LYR_ACT_STAT", "E");		    	
			    	
			    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGp2
	
					UPDATE TB_YS_STKLYR            
					   SET MOD_DDTT     = SYSDATE             
						 , MODIFIER     = :V_MODIFIER             
						 , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
					     , SSTL_NO = :V_SSTL_NO
					     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
					 WHERE YS_STK_COL_GP  = :V_YS_STK_COL_GP
					 AND YS_STK_BED_NO = :V_YS_STK_BED_NO
					 AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO
					 AND YS_STK_SEQ_NO = :V_YS_STK_SEQ_NO
					 */
					commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGp2", logId, methodNm, "TB_YS_STKLYR 등록");
					
		    		if(szYdGpBay.startsWith("KB")){
		          		szMSG_ID =  "YSN4L002";
			    	}else if(szYdGpBay.startsWith("KD")){
			    		szMSG_ID =  "YSN5L002";
			    	}	
		    		recInTemp  = JDTORecordFactory.getInstance().create();
		    		recInTemp.setResultCode(logId);	//Log ID
		    		recInTemp.setResultMsg(methodNm);	//Log Method Name
					recInTemp.setField("MSG_ID"				, szMSG_ID);
					recInTemp.setField("YD_INFO_SYNC_CD"	, "B");	//차량입고
			    	recInTemp.setField("SSTL_NO"			, commUtils.trim(recOutTemp.getFieldString("SSTL_NO"       ))); 
			    	recInTemp.setField("YS_STK_COL_GP"		, "K");
			    	//recInTemp.setField("YS_STK_BED_NO", ydDaoUtils.paraRecChkNull(recOutTemp, "YS_STK_BED_NO"));
			    	sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2(szMSG_ID, recInTemp));
			    	
			    	szMsg = "["+methodNm+"] 반입도착시 저장품 제원 야드L2[" + szMSG_ID + "]로 전송";
					commUtils.printLog(logId, szMsg, "SL");				
					
		    	}				
			}
			//---------------------------------------------------------------------------------------------
	    	
			szMsg = "["+methodNm+"] 반입차량스케쥴["+szYD_CAR_SCH_ID+"] 입고차량도착상태[B]로 변경 시작";
			commUtils.printLog(logId, szMsg, "SL");
			
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID"			, szYD_CAR_SCH_ID);
			recInTemp.setField("MODIFIER"				, szMethodName.substring(0, 10));
			recInTemp.setField("YD_CARUD_WRK_BOOK_ID"	, szYD_WBOOK_ID);
			recInTemp.setField("YD_CARLD_WRK_BOOK_ID"	, szYD_WBOOK_ID);  
			recInTemp.setField("YD_CARUD_STOP_LOC"		, szYD_CARLD_STOP_LOC);
			recInTemp.setField("YD_CARUD_ARR_DT"		, commUtils.getDateTime14());
			recInTemp.setField("YD_PNT_CD1"         	, szSPOS_YD_PNT_CD);
			recInTemp.setField("YD_CAR_PROG_STAT"		, "B");									//대기장 도착
			
			int intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarschByCarschid", logId, methodNm, "차량스케줄 갱신");

			if( intRtnVal == 0 ) {
				szMsg = "["+methodNm+"] 차량스케쥴["+szYD_CAR_SCH_ID+"] 조회 성공 후 차량도착상태로 변경 시 차량스케줄이 존재하지 않습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new DAOException(szMsg);
			}else if( intRtnVal < 0 ) {
				szMsg = "["+methodNm+"] 차량스케쥴["+szYD_CAR_SCH_ID+"] 조회 성공 후 차량도착상태로 변경 시 차량스케줄이 존재하지 않습니다.";
				commUtils.printLog(logId, szMsg, "SL");
				throw new DAOException(szMsg);
			}
			
			szMsg = "["+methodNm+"] 차량스케쥴["+szYD_CAR_SCH_ID+"] 조회 성공 후 차량도착상태로 변경 완료";
			commUtils.printLog(logId, szMsg, "SL");
			
			/* 
			 * 크레인스케줄Main을 호출한다.
			 */
			//크레인스케줄MAIN호출 TC :/** 1:블름,2:빌렛,3:봉강,4:선재	 */
	    	String szJMS_TC_CD = "";
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setResultCode(logId);	//Log ID
	    	recInTemp.setResultMsg(methodNm);	//Log Method Name

	    	if(szYdGpBay.startsWith("KA")||szYdGpBay.startsWith("KB")){
	    		szJMS_TC_CD = "YSYSJ302";
	    	}else if(szYdGpBay.startsWith("KD")||szYdGpBay.startsWith("KE")){
	    		szJMS_TC_CD = "YSYSJ402";
	    	}
	    	if (!szJMS_TC_CD.equals("")) {
	    		recInTemp.setField("JMS_TC_CD",    	szJMS_TC_CD);
	    		recInTemp.setField("JMS_TC_CREATE_DDTT"	,  commUtils.getDateTime14());
		    	recInTemp.setField("YD_SCH_CD", 	szYD_SCH_CD);
		    	recInTemp.setField("YD_EQP_ID", 	szYD_EQP_ID);
		    	recInTemp.setField("YD_WBOOK_ID", 	szFIR_YD_WBOOK_ID);
		    	
		    	szMsg="["+methodNm+"] 반입 크레인스케줄메인을 호출 전 전문내용 : 기동 YD_WBOOK_ID" + szFIR_YD_WBOOK_ID;
		    	commUtils.printLog(logId, szMsg, "SL");
				
				sndRecord = commUtils.addSndData(sndRecord, recInTemp);						
			
	    	}	
			szMsg= "["+methodNm+"]출하차량스케줄 수정 전문 전송 성공";
			commUtils.printLog(logId, szMsg, "SL");	
			
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch(Exception e){
			szMsg = "["+methodNm+"] procGdsDistCarUdLotComp ->출하하차LOT편성  Error : " + e.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
			return sndRecord;
		}
		return sndRecord;
	} //end of procGdsDistCarUdLotComp
		
	/**
	 * SMS SENDER
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
    public String updSmsMsgSend(JDTORecord recInPara) throws DAOException {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName ="" ;
		String logId 	= recInPara.getResultCode();
		szMsg        				= "";
		szMethodName 				= "updSmsMsgSend < "+ recInPara.getResultMsg();
		String szOperationName 		= "SMS SENDER";
		
		JDTORecord	inRecord 		= null;
		try {
			
			szMsg = "SMS SENDER 시작";
			commUtils.printLog(logId, szMsg, "SL");	
			
			// JDTORecord 생성
			inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("FROM_SENDER_NAME", new String("야드관리"));	// SMS 보내는 사람 성명
			
			inRecord.setField("FROM_PHONE_NO", recInPara.getFieldString("FROM_PHONE_NO"));	// SMS 보내는 사람 핸드펀번호
			inRecord.setField("TO_PHONE_NO"  , recInPara.getFieldString("TO_PHONE_NO"));	// SMS 받는 사람 핸드펀번호
			inRecord.setField("TO_CONTENT"   , recInPara.getFieldString("TO_CONTENT"));		// SMS 전송 내용
			inRecord.setField("TO_SEND_TIME" , new String(""));								// SMS 전송시간
			
			
			//---------------------------------------------------------------------
//			// SMS전송 객체
//			SmsSender	sender			= null;	
//			// 객체생성
//		    sender = new SmsSender();
//		    // 객체초기화
//		    sender.initService();
//		
//		    sender.send(inRecord);
			//---------------------------------------------------------------------
			
			//---------------------------------------------------------------------
			MessageSenderAuto    sender = new MessageSenderAuto();
		    inRecord.setField("RECV_ID", "YS00001");
    		inRecord.setField("GROUP_ID", "MMS1");
		    inRecord.setField("PROGRAM_ID", "updSmsMsgSendYS");

		    sender.sendAutoSMS(inRecord);
		    //---------------------------------------------------------------------
		    
			szMsg = "SMS SENDER 끝";
			commUtils.printLog(logId, szMsg, "SL");	
			
			commUtils.printLog(logId, szMethodName, "S-");
			
		}catch(Exception ex) {
			szMsg = "["+szOperationName+"] SMS 송신 ERROR - 메세지 : " + ex.getMessage();
			commUtils.printLog(logId, szMsg, "SL");
		}
		return YdConstant.RETN_CD_SUCCESS;
		
		
	}	// end of updSmsMsgSend
    
} // end of class YsCommCarMvSeEJB
