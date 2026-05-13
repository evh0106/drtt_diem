/**
 * @(#)YmCommSeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      YM야드 공통 관리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.ym.bcommon.session;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.ydStock.StockSpecReg.CoilSpecRegSeEJBBean;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.bslab.session.BSlabComm;
import com.inisteel.cim.ym.common.YmCommonUtil;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
/**
 *      [A] 클래스명 : 야드공통관리 Session EJB 
 * @ejb.bean name="YmCommSeEJB" jndi-name="YmCommSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class YmCommSeEJBSBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YmCommUtils commUtils = new YmCommUtils();
	private YmCommDAO commDao = new YmCommDAO();
	private BSlabComm bSlabComm = new BSlabComm();
	
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
	public GridData getYmCode(GridData gdReq) throws DAOException {
		String methodNm = "YM야드코드조회[YmCommSeEJB.getYmCode]";
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = commDao.getYmCode(gdReq);
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
		String methodNm = "예외처리이력 테이블 Log 등록[YmCommSeEJB.insExcptHist] < " + jrParam.getResultMsg();
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
	 *      [A] 오퍼레이션명 : 이송완료처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */
	public JDTORecord procFtmvCmtl(JDTORecord rcvMsg) throws DAOException {
		
		String methodNm = "이송완료처리[YmCommSeEJB.procFtmvCmtl] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
//			String sFTMV_BKUP = "N"; //이송백업 유무
			
			String sSTOCK_ID = rcvMsg.getFieldString("STOCK_ID");
			
//			/************************************************
//			 ** 이송완료
//			 ************************************************/
//			/*
//			SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END AS IS_FTMV_BKUP
//			  FROM USRPTA.TB_PT_STLFRTOMOVE A
//			 WHERE FRTOMOVE_STAT_CD = '3'
//			   AND ARR_WLOC_CD ||'' IN ('D3Y41','D3Y42')
//			   AND SPOS_WLOC_CD||'' NOT IN ('D3Y41','D3Y42')
//			   AND TRANSWORD_SEQNO = (SELECT MAX(TRANSWORD_SEQNO)
//			                            FROM USRPTA.TB_PT_STLFRTOMOVE B
//			                           WHERE B.STL_NO = A.STL_NO
//			                             AND ROWNUM <= 1)
//			   AND A.STL_NO = :V_STOCK_ID
//			 */
//			jrParam.setField("STOCK_ID"  , sSTOCK_ID);
//			JDTORecord rs = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getIsFtmvBkup").getRecord(0);
//			sFTMV_BKUP = rs.getFieldString("IS_FTMV_BKUP");
//
//			
//			if ("Y".equals(sFTMV_BKUP)) { 
				commUtils.printLog("", "이송백업 실적처리 START", "[INFO]+");
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
			    EJBConnector ejbConnPT = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				ejbConnPT.trx("UpdCoilComProg", new Class[] { JDTORecord.class }, new Object[] { tcRecord });
				
				
				//Coil공통 테이블 조회
				jrParam.setField("COIL_NO", sSTOCK_ID);
			    JDTORecord stlRecord  = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCOILCOMM").getRecord(0);
			    String sSTL_APPEAR_GP = commUtils.nvl(stlRecord.getFieldString("STL_APPEAR_GP"), "");
			    
			    
			    if (!sSTL_APPEAR_GP.equals("Y")) {
					
		        	//TB_PT_STLFRTOMOVE update			
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
			    	jrParam.setField("STL_NO", sSTOCK_ID);
//					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateunLoadTimeToPT"); 
			    	
			    	//TB_PT_STLFRTOMOVE 테이블 업데이트
				    EJBConnector ejbConnPT2 = new EJBConnector("default", "YmCommSeEJB", this);
					ejbConnPT2.trx("updProcStlFrToMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					
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
				    
				    commUtils.printLog(logId, "YDPTJ002 코일소재 이송완료실적BACKUP처리", "[INFO]");
				}
			    
			    commUtils.printLog("", "이송백업 실적처리 END", "[INFO]-");
//			} // end if ("Y".equals(sFTMV_BKUP))
        	
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 이송완료처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */
	public boolean updProcStlFrToMove(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "이송완료처리[YmCommSeEJB.updProcStlFrToMove] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//TB_PT_STLFRTOMOVE update			
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
			commDao.update(rcvMsg, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateunLoadTimeToPT"); 
			
		} catch(Exception e) {
			
		}
		return true;

	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 차량입동 ERROR LOG처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */
	public boolean updCarErrorLog(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "차량입동 ERROR LOG처리[YmCommSeEJB.updCarErrorLog] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarYdMsgNm 
			INSERT INTO TB_YM_CARSCHLOG ( 
			       YM_CARSCHLOG_SEQ
			     , YD_CAR_SCH_ID    
			     , YD_MSG_NM    
			     , REGISTER    
			     , REG_DDTT    
			     , DEL_YN    
			)
			SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI')||YM_CARSCHLOG_SEQ.NEXTVAL   --CRANE_WRSLT_ID
			     , :V_YD_CAR_SCH_ID 
			     , :V_YD_MSG_NM 
			     , :V_MODIFIER 
			     , SYSDATE
			     , 'N' 
			  FROM DUAL 

			*/   
			commDao.update(rcvMsg, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarYdMsgNm", logId, methodNm, "차량 ERROR 메세지");
			
			commUtils.printLog(logId, methodNm, "S-");
		} catch(Exception e) {
			
		}
		return true;

	}	
	
	/**
	 *      [A] 오퍼레이션명 : 차량입동 ERROR LOG처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
     */
	public boolean updCarErrorLogNew(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "차량입동 ERROR LOG처리[YmCommSeEJB.updCarErrorLogNew] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarYdMsgNm 
			INSERT INTO TB_YM_CARSCHLOG ( 
			       YM_CARSCHLOG_SEQ
			     , YD_CAR_SCH_ID    
			     , YD_MSG_NM    
			     , REGISTER    
			     , REG_DDTT    
			     , DEL_YN    
			)
			SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI')||YM_CARSCHLOG_SEQ.NEXTVAL   --CRANE_WRSLT_ID
			     , :V_YD_CAR_SCH_ID 
			     , :V_YD_MSG_NM 
			     , :V_MODIFIER 
			     , SYSDATE
			     , 'N' 
			  FROM DUAL 
			*/   
			commDao.update(rcvMsg, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarYdMsgNm", logId, methodNm, "차량 ERROR 메세지");
			
			commUtils.printLog(logId, methodNm, "S-");
		} catch(Exception e) {
			
		}
		return true;

	}	
	
	/**
	 *      [A] 오퍼레이션명 : 차량입동 ERROR LOG Clear 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */
	public boolean updCarErrorLogClear(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "차량입동 ERROR LOG처리 Clear[YmCommSeEJB.updCarErrorLogClear] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarYdMsgNmClear 
			UPDATE TB_YM_CARSCHLOG
			   SET DEL_YN = 'Y'
			     , MODIFIER = :V_MODIFIER
			     , MOD_DDTT = SYSDATE
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  
			*/   
			commDao.update(rcvMsg, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarYdMsgNmClear", logId, methodNm, "차량 ERROR 메세지");
			
			commUtils.printLog(logId, methodNm, "S-");
		} catch(Exception e) {
			
		}
		return true;

	}		
	
	/**
	 *      [A] 오퍼레이션명 : 이송상차완료처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
     */
	public JDTORecord runLdCmplProc(JDTORecord rcvMsg) throws DAOException {
		
		String methodNm = "이송상차완료처리[YmCommSeEJB.runLdCmplProc] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			CoilSpecRegSeEJBBean CoilSpecRegSeEJBBean = new CoilSpecRegSeEJBBean();
			
			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			JDTORecord tcRecord = null;
			JDTORecordSet rsResult 	= null;
			JDTORecordSet rsResult2 = null;
			int nIdx = 0;
			
			String sTRN_EQP_CD 		= rcvMsg.getFieldString("TRN_EQP_CD");
			String sYD_CAR_SCH_ID 	= rcvMsg.getFieldString("YD_CAR_SCH_ID");
			String currDate			= commUtils.getDateTime14();
			String szPT_TB_COMM     = "";
			String s_YD_GP			= "";
	
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));
			
			/**********************************************************
			* 1. 운송장비코드로 상차완료 대상 조회
			**********************************************************/
			jrParam.setField("TRN_EQP_CD"		, sTRN_EQP_CD		);  //운송장비코드
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getListFrtostlList_loadEnd_PIDEV", logId, methodNm, "이송대상재료 조회 "); 
			
			if(rsResult.size() <= 0) {
				//운송장비코드로 상차완료 대상 이 조회되지 않았습니다.
				return jrRtn;
			} 

			String sSTORE_LOC_CD = rsResult.getRecord(0).getFieldString("STORE_LOC_CD");
			String sARR_WLOC_CD  = rsResult.getRecord(0).getFieldString("ARR_WLOC_CD");
			
			/**********************************************************
			* 2. TB_YD_CARSCH 변경
			**********************************************************/
			jrParam.setField("YD_CAR_PROG_STAT"	, "5"); //야드차량진행상태(상차완료)
			jrParam.setField("YD_WBOOK_ID" 		, "" ); //null 이면 이전 값 그대로 설정 
			jrParam.setField("STACK_COL_GP" 	, sSTORE_LOC_CD.substring(0, 6) ); 
			jrParam.setField("WR_DT" 			, currDate );
			jrParam.setField("YD_CAR_SCH_ID" 	, sYD_CAR_SCH_ID );
			jrParam.setField("ARR_WLOC_CD" 		, sARR_WLOC_CD ); 
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updA8YML009CarSchLd
			--B열연 SLAB 크레인권하실적 상차 차량스케줄 수정
			UPDATE USRYDA.TB_YD_CARSCH TS
			   SET TS.MODIFIER             = :V_MODIFIER
			     , TS.MOD_DDTT             = SYSDATE
			     , TS.YD_EQP_WRK_STAT      = 'L' --영차
			     , TS.YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT   
			     , TS.YD_EQP_WRK_SH        = (SELECT COUNT(*) 
			                                    FROM TB_YD_CARFTMVMTL 
			                                   WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID)
			     , TS.YD_EQP_WRK_WT        = (SELECT SUM(SLAB_WT) 
			                                    FROM TB_YD_CARFTMVMTL A
			                                       , VW_YD_SLABCOMM   B
			                                   WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			                                     AND A.STL_NO        = B.SLAB_NO
			                                   
			                                   )
			     , TS.YD_PNT_CD3           = '0000'
			     , TS.YD_CARLD_WRK_BOOK_ID = NVL(:V_YD_WBOOK_ID,TS.YD_CARLD_WRK_BOOK_ID)
			     , TS.YD_CARLD_STOP_LOC    = :V_STACK_COL_GP
			     , TS.YD_CARLD_ST_DT       = NVL(TS.YD_CARLD_ST_DT,NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE))
			     , TS.YD_CARLD_CMPL_DT     = DECODE(:V_YD_CAR_PROG_STAT,'5',NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE),NULL)
			     , TS.ARR_WLOC_CD          = NVL(:V_ARR_WLOC_CD,TS.ARR_WLOC_CD)
			   
			WHERE TS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID   */
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updA8YML009CarSchLd", logId, methodNm, "상차 차량스케줄 수정");
			
			String sSTL_NO;
			int iSeqCount 	= rsResult.size();
			
			for(int ii= 0; ii < rsResult.size() ; ii++) {
				
				sSTL_NO = rsResult.getRecord(ii).getFieldString("STL_NO");
				
				/**********************************************************
				* 3. TB_PT_STLFRTOMOVE 변경
				**********************************************************/
				jrParam.setField("STL_NO"	, sSTL_NO); 
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateLoadTimeToPT
				UPDATE TB_PT_STLFRTOMOVE
				   SET FRTOMOVE_CARLOAD_DATE = TO_CHAR(sysdate,'YYYYMMDD')
				 WHERE STL_NO = :V_STL_NO
				   AND TRANSWORD_SEQNO = ( select max(TRANSWORD_SEQNO)
				                             from TB_PT_STLFRTOMOVE
				                            where STL_NO = :V_STL_NO
				                              and FRTOMOVE_STAT_CD NOT IN ('Z','C')
				                         )                        */
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateLoadTimeToPT", logId, methodNm, "TB_PT_STLFRTOMOVE 변경");
				
				
				/**********************************************************
				* 4. 주편공통 진도에 따라 주편공통, SLAB공통 UPDATE
				**********************************************************/
				//주편공통 진행 상태가 진행중(3)인 경우 SLAB공통을 update 
				jrParam.setField("RECORD_PROG_STAT"	, "3"); 
				jrParam.setField("MSLAB_NO"			, sSTL_NO); 
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getMSlabByRecordProgStat 
				SELECT MSLAB_NO
				  FROM TB_PT_MSLABCOMM
				 WHERE RECORD_PROG_STAT = :V_RECORD_PROG_STAT 
				   AND MSLAB_NO= :V_MSLAB_NO */
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getMSlabByRecordProgStat", logId, methodNm, "주편공통에 레코드상태가 진행중(3)인지 확인 "); 
				if(rsResult2.size() > 0) {
					
					//SLAB공통 UPDATE
					jrParam.setField("SLAB_NO"	, sSTL_NO); 
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateMatlFtmvTimeSlab
					UPDATE TB_PT_SLABCOMM  
					   set MATL_FTMV_DT = sysdate
					WHERE SLAB_NO = :V_SLAB_NO       */
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateMatlFtmvTimeSlab", logId, methodNm, "TB_PT_SLABCOMM 의 MATL_FTMV_DT UPDATE");
					szPT_TB_COMM = "S";
				} else {
					
					//주편공통 UPDATE
					jrParam.setField("MSLAB_NO"	, sSTL_NO); 
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateMatlFtmvTimeMSlab
					UPDATE TB_PT_MSLABCOMM  
					   set MATL_FTMV_DT = sysdate
					WHERE MSLAB_NO = :V_MSLAB_NO      */
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateMatlFtmvTimeMSlab", logId, methodNm, "주편공통의 MATL_FTMV_DT 변경");
					szPT_TB_COMM = "B";
				}
				
				
				if(ii == 0){
	    			   //상차완료실적 송신
	                   //소재차량상차완료
	    			   tcRecord = JDTORecordFactory.getInstance().create(); 
					   tcRecord.setField("JMS_TC_CD"			, "YDTSJ008");
					   tcRecord.setField("JMS_TC_CREATE_DDTT"	, currDate);					
					   tcRecord.setField("TRN_EQP_CD"			, sTRN_EQP_CD);
					   tcRecord.setField("SPOS_WLOC_CD"			, rsResult.getRecord(ii).getFieldString("SPOS_WLOC_CD"));
					   tcRecord.setField("SPOS_YD_PNT_CD"		, rsResult.getRecord(ii).getFieldString("YD_PNT_CD"));
					   tcRecord.setField("ARR_WLOC_CD"			, rsResult.getRecord(ii).getFieldString("ARR_WLOC_CD"));
					   tcRecord.setField("TRN_WRK_MTL_GP"		, rsResult.getRecord(ii).getFieldString("TRN_WRK_MTL_GP"));
					   tcRecord.setField("MTL_UGNT_GP"			, rsResult.getRecord(ii).getFieldString("MTL_UGNT_GP"));
					   tcRecord.setField("HCR_GP"				, rsResult.getRecord(ii).getFieldString("HCR_GP"));
					   tcRecord.setField("CARLD_SH"				, Integer.toString(iSeqCount));
				}
				tcRecord.setField("STL_NO"  + (1+nIdx), sSTL_NO);
				tcRecord.setField("STL_WT"  + (1+nIdx), rsResult.getRecord(ii).getFieldString("STL_WT"));
				tcRecord.setField("STL_LOC" + (1+nIdx), commUtils.format(rsResult.getRecord(ii).getFieldInt("STACK_LAYER_GP"),3));
				nIdx ++;
				
				
				
				s_YD_GP = getYdFromWlocCd(rsResult.getRecord(ii).getFieldString("ARR_WLOC_CD"));
				
				if(!"H".equals(s_YD_GP)) {
			        JDTORecord ydStlRecord = null;
			        ydStlRecord = JDTORecordFactory.getInstance().create();
			        ydStlRecord.setField("PT_TB_COMM"		, szPT_TB_COMM);
			        ydStlRecord.setField("STL_NO"			, sSTL_NO);							        
			        ydStlRecord.setField("SLAB_WO_RT_CD"	, rsResult.getRecord(ii).getFieldString("SLAB_WO_RT_CD"));
			        ydStlRecord.setField("ORD_YEOJAE_GP"	, rsResult.getRecord(ii).getFieldString("ORD_YEOJAE_GP"));
			        ydStlRecord.setField("SCARFING_YN"		, rsResult.getRecord(ii).getFieldString("SCARFING_YN"));
			        ydStlRecord.setField("SCARFING_DONE_YN"	, rsResult.getRecord(ii).getFieldString("SCARFING_DONE_YN"));
			        ydStlRecord.setField("MILL_WO_EXN"		, rsResult.getRecord(ii).getFieldString("MILL_WO_EXN"));
			        ydStlRecord.setField("YD_GP"			, s_YD_GP);					        
			        ydStlRecord.setField("STL_APPEAR_GP"	, rsResult.getRecord(ii).getFieldString("STL_APPEAR_GP"));
			        ydStlRecord.setField("HCR_GP"			, rsResult.getRecord(ii).getFieldString("HCR_GP"));
			        
			        YdCommonUtils.uptStockCodeMapping(ydStlRecord);
				}
			}
			
			jrRtn = commUtils.addSndData(jrRtn, tcRecord);	
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	} // end of runLdCmplProc
	
	/**
	 * 관리되는 개소코드를 야드구분으로 변환하는 메소드
	 * @param szWLOC_CD
	 * @return
	 */
	public static String getYdFromWlocCd(String szWLOC_CD) {
		String szYD_GP = "";
		if(szWLOC_CD.equals("DHY21") || szWLOC_CD.equals("DHY22")) {				//C연주슬라브
			szYD_GP = YdConstant.YD_GP_C_SLAB_YARD;
		}else if(szWLOC_CD.equals("DJY21") || szWLOC_CD.equals("DJY22")) {			//C열연소재
			szYD_GP = YdConstant.YD_GP_C_HR_COIL_MATL_YARD;
		}else if(szWLOC_CD.equals("DJY15") 
				|| szWLOC_CD.equals("DJY16") 
				|| szWLOC_CD.equals("DJY17") 
    	    	|| szWLOC_CD.equals("DJY18") 
    	    	|| szWLOC_CD.equals("DJY19") 
    	    	|| szWLOC_CD.equals("DJY30")) {										//C열연 코일제품창고
			szYD_GP = YdConstant.YD_GP_C_HR_COIL_GDS_YARD;
    	}else if(szWLOC_CD.equals("DKY21")||szWLOC_CD.equals("DWY22")){	 									//A후판 소재
    		szYD_GP = YdConstant.YD_GP_A_PLATE_SLAB_YARD;
    	}else if(szWLOC_CD.equals("DKY30")) {										//A후판 제품창고
    		szYD_GP = YdConstant.YD_GP_PLATE_GDS_YARD;
    	}else if("DJY25".equals(szWLOC_CD)||"DYY15".equals(szWLOC_CD)||"BSY01".equals(szWLOC_CD)||"BSY02".equals(szWLOC_CD)||"BSY03".equals(szWLOC_CD)) { //(비상야드추가)
    		szYD_GP = YdConstant.YD_GP_INTGR_YARD;
    	}else if( YdConstant.WLOC_CD_A_PLATE_PLANT.equals(szWLOC_CD)||
    			  YdConstant.WLOC_CD_B_PLATE_PLANT.equals(szWLOC_CD)){
    		szYD_GP = YdConstant.YD_GP_A_PLATE_PLANT;								//A후판조업
    	}else if( YdConstant.WLOC_CD_C_HR_PLANT.equals(szWLOC_CD) ) {
    		szYD_GP = YdConstant.YD_GP_C_HR_PLANT;									//C열연조업
		}else if( YdConstant.WLOC_CD_B_HR_PLANT.equals(szWLOC_CD) ) {
			szYD_GP = YdConstant.YD_GP_B_HR_SLAB_YARD;								//B열연
		}
		return szYD_GP;
	}
		
	/**
	 *      [A] 오퍼레이션명 : 이송완료처리_SLAB
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */
	public JDTORecord procFtmvCmtl_SLAB(JDTORecord rcvMsg) throws DAOException {
		
		String methodNm = "이송완료처리_SLAB[YmCommSeEJB.procFtmvCmtl_SLAB] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			JDTORecordSet rsResult = null;
			String szCurrProgCd = "";
    	 	String sScarfingYn 		= "";
    	 	String sOrdYeojaeGp 	= "";
    	 	String sSlabCreateGp 	= "";
    	 	String currDate			= commUtils.getDateTime14();
    	 	String sRECORD_PROG_STAT = "3"; // 2018.02.28 주편공통에 존재하지 않을 경우 레코드진행상태를 '3'으로 본다.
    	 	
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			String sSTOCK_ID = rcvMsg.getFieldString("STOCK_ID");
			
			commUtils.printLog("", "이송백업 실적처리 START", "[INFO]+");
			JDTORecord tcRecord = JDTORecordFactory.getInstance().create(); 

			/**********************************************************
			* 4. 주편공통 진도에 따라 주편공통, SLAB공통 UPDATE
			**********************************************************/
			//주편공통 진행 상태가 진행중(3)인 경우 SLAB공통을 update 
			//jrParam.setField("RECORD_PROG_STAT"	, "3"); 
			//jrParam.setField("MSLAB_NO"			, sSTOCK_ID); 
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getMSlabByRecordProgStat
			SELECT MSLAB_NO
			      ,SCARFING_YN
			      ,ORD_YEOJAE_GP
			      ,SLAB_CREATE_GP
			  FROM TB_PT_MSLABCOMM
			 WHERE RECORD_PROG_STAT = :V_RECORD_PROG_STAT --진행중:2
			   AND MSLAB_NO= :V_MSLAB_NO */
			//JDTORecordSet rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getMSlabByRecordProgStat", logId, methodNm, "주편공통에 레코드상태가 진행중(3)인지 확인 ");
			
			
			// 주편 공통에서 조회
			jrParam.setField("STOCK_ID"		, sSTOCK_ID		);  
			JDTORecordSet rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.bslabCommDtlInqjl.getMSlabComm", logId, methodNm, "주편 공통에서 조회"); 
			
			if(rsResult2.size() > 0) {
				
				sRECORD_PROG_STAT = rsResult2.getRecord(0).getFieldString("RECORD_PROG_STAT");
				
	    	 	sScarfingYn 	= rsResult2.getRecord(0).getFieldString("SCARFING_YN");
	    	 	sOrdYeojaeGp 	= rsResult2.getRecord(0).getFieldString("ORD_YEOJAE_GP");
	    	 	sSlabCreateGp 	= rsResult2.getRecord(0).getFieldString("SLAB_CREATE_GP");
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
				
	    	 	commUtils.printLog(logId, methodNm,"▶▶정정작업 sScarfingYn ◀◀"+sScarfingYn);
	    	 	commUtils.printLog(logId, methodNm,"▶▶정정작업 sOrdYeojaeGp◀◀"+sOrdYeojaeGp);
	    	 	commUtils.printLog(logId, methodNm,"▶▶정정작업 sSlabCreateGp◀◀"+sSlabCreateGp);
	    	 	
	    	 	if(!"M".equals(sSTOCK_ID.substring(0,1))) { //슬라브 생산공장구분이 "M"이 아니고
	    	    	if("N".equals(sScarfingYn)){ //Non Scarfing 대상재 중
	    	    		if("G".equals(sSlabCreateGp)&&"2".equals(sOrdYeojaeGp)){
	    	    			//구입재이면서 여재인것은 제외
	    	    		} else {
			    			JDTORecord tEndRecord = null;
			    			tEndRecord = JDTORecordFactory.getInstance().create(); 
			    			tEndRecord.setField("JMS_TC_CD", "YMCSJ001");
			    			tEndRecord.setField("JMS_TC_CREATE_DDTT", currDate);						
			    			tEndRecord.setField("MSLAB_NO",sSTOCK_ID);
	    	    			
			    			jrRtn = commUtils.addSndData(jrRtn, tEndRecord);
	    	    		}
	    	    	}
	    	 	}
			}
		    
        	//TB_PT_STLFRTOMOVE update			
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
	    	jrParam.setField("STL_NO", sSTOCK_ID);
	    	
	    	//TB_PT_STLFRTOMOVE 테이블 업데이트
		    EJBConnector ejbConnPT2 = new EJBConnector("default", "YmCommSeEJB", this);
			ejbConnPT2.trx("updProcStlFrToMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			
			
			//공통에 전문 전송(YDCTJ032, YDPTJ001)
			if("3".equals(sRECORD_PROG_STAT))  { //RECORD_PROG_STAT == '3'
				
				// YDPTJ001 송신 추가(슬라브소재이송완료실적)- 슬라브인경우 슬라브 공통에서 항목 조회해서 전문편집
				// 슬라브 공통에서 조회
				jrParam.setField("STOCK_ID"		, sSTOCK_ID		);  
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.bslabCommDtlInqjl.getSlabComm", logId, methodNm, "슬라브 공통에서 조회"); 
				
				if(rsResult.size() > 0) {
					JDTORecord FrtoendRecord = null;
					FrtoendRecord = JDTORecordFactory.getInstance().create();
					FrtoendRecord.setField("JMS_TC_CD" , "YDPTJ001");
					FrtoendRecord.setField("JMS_TC_CREATE_DDTT" , currDate);
					FrtoendRecord.setField("STL_NO" , rsResult.getRecord(0).getFieldString("SLAB_NO"));// 재료번호
					FrtoendRecord.setField("ORD_NO" , rsResult.getRecord(0).getFieldString("ORD_NO")); // 주문번호
					FrtoendRecord.setField("ORD_DTL" , rsResult.getRecord(0).getFieldString("ORD_DTL")); // 주문행번
					FrtoendRecord.setField("PLNT_PROC_CD" , rsResult.getRecord(0).getFieldString("PLNT_PROC_CD"));// 공장공정코드
					FrtoendRecord.setField("STL_APPEAR_GP" , rsResult.getRecord(0).getFieldString("STL_APPEAR_GP"));// 재료외형구분
					FrtoendRecord.setField("CURR_PROG_CD" , rsResult.getRecord(0).getFieldString("CURR_PROG_CD"));// 현재진도코드
					FrtoendRecord.setField("ORD_YEOJAE_GP" , rsResult.getRecord(0).getFieldString("ORD_YEOJAE_GP")); // 주문여재구분
					FrtoendRecord.setField("STL_WT" , rsResult.getRecord(0).getFieldString("SLAB_WT"));// 재료중량 (SLAB중량)
					FrtoendRecord.setField("DS_MTL_WT" , ""); // 설계재료중량
					FrtoendRecord.setField("MTL_STAT_GP" , rsResult.getRecord(0).getFieldString("RECORD_PROG_STAT")); // 재료상태구분
					FrtoendRecord.setField("RECORD_END_GP" , rsResult.getRecord(0).getFieldString("RECORD_END_GP"));// Record 종료구분
					FrtoendRecord.setField("RECORD_END_GP1" , "");
					FrtoendRecord.setField("BEFO_PROG_CD" , rsResult.getRecord(0).getFieldString("BEFO_PROG_CD"));// 전진도 코드
					FrtoendRecord.setField("BEF_ORD_NO" , rsResult.getRecord(0).getFieldString("BEF_ORD_NO"));// 전주문 번호
					FrtoendRecord.setField("BEF_ORD_DTL" , rsResult.getRecord(0).getFieldString("BEF_ORD_DTL"));// 전주문 행번
					FrtoendRecord.setField("MMATL_FEE_NO" , "");// 모재료번호
					FrtoendRecord.setField("ORDERTRANS_MATCH_GP" , rsResult.getRecord(0).getFieldString("MATCH_ORDERTRANS_GP"));// 목전충당구분
					
					jrRtn = commUtils.addSndData(jrRtn, FrtoendRecord);
				}
				
				// 이송완료 후 YDCTJ032전문 송신
				JDTORecord FrtoendRecord2 = null;
				FrtoendRecord2 = JDTORecordFactory.getInstance().create();
				FrtoendRecord2.setField("JMS_TC_CD" , "YDCTJ032");
				FrtoendRecord2.setField("JMS_TC_CREATE_DDTT" , currDate);
				FrtoendRecord2.setField("PTOP_PLNT_GP" , "HB");
				FrtoendRecord2.setField("STL_APPEAR_GP" , "C");
				FrtoendRecord2.setField("CHG_SUP_PROG_STAT" , "09");
				FrtoendRecord2.setField("WR_OCCR_DT" , currDate);
				FrtoendRecord2.setField("YD_EQP_WR_CNT" , "1");
				FrtoendRecord2.setField("STL_NO1" , sSTOCK_ID);
				
				jrRtn = commUtils.addSndData(jrRtn, FrtoendRecord2);
				
				
			} else { //RECORD_PROG_STAT != '3'

				// YDPTJ001 송신 추가(슬라브소재이송완료실적)- 주편인경우 주편공통에서 항목 조회해서 전문편집
				// 주편 공통에서 조회
				jrParam.setField("STOCK_ID"		, sSTOCK_ID		);  
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.bslabCommDtlInqjl.getMSlabComm", logId, methodNm, "주편 공통에서 조회"); 
				
				if(rsResult.size() > 0) {
					JDTORecord FrtoendRecord = null;
					FrtoendRecord = JDTORecordFactory.getInstance().create();
					FrtoendRecord.setField("JMS_TC_CD" , "YDPTJ001");
					FrtoendRecord.setField("JMS_TC_CREATE_DDTT" , currDate);
					FrtoendRecord.setField("STL_NO" , rsResult.getRecord(0).getFieldString("MSLAB_NO"));// 재료번호
					FrtoendRecord.setField("ORD_NO" , rsResult.getRecord(0).getFieldString("ORD_NO")); // 주문번호
					FrtoendRecord.setField("ORD_DTL" , rsResult.getRecord(0).getFieldString("ORD_DTL")); // 주문행번
					FrtoendRecord.setField("PLNT_PROC_CD" , rsResult.getRecord(0).getFieldString("PLNT_PROC_CD"));// 공장공정코드
					FrtoendRecord.setField("STL_APPEAR_GP" , rsResult.getRecord(0).getFieldString("STL_APPEAR_GP"));// 재료외형구분
					FrtoendRecord.setField("CURR_PROG_CD" , rsResult.getRecord(0).getFieldString("CURR_PROG_CD"));// 현재진도코드
					FrtoendRecord.setField("ORD_YEOJAE_GP" , rsResult.getRecord(0).getFieldString("ORD_YEOJAE_GP")); // 주문여재구분
					FrtoendRecord.setField("STL_WT" , rsResult.getRecord(0).getFieldString("MSLAB_WT"));// 재료중량 (SLAB중량)
					FrtoendRecord.setField("DS_MTL_WT" , ""); // 설계재료중량
					FrtoendRecord.setField("MTL_STAT_GP" , rsResult.getRecord(0).getFieldString("RECORD_PROG_STAT")); // 재료상태구분
					FrtoendRecord.setField("RECORD_END_GP" , rsResult.getRecord(0).getFieldString("RECORD_END_GP"));// Record 종료구분
					FrtoendRecord.setField("RECORD_END_GP1" , "");
					FrtoendRecord.setField("BEFO_PROG_CD" , rsResult.getRecord(0).getFieldString("BEFO_PROG_CD"));// 전진도 코드
					FrtoendRecord.setField("BEF_ORD_NO" , "");// 전주문 번호
					FrtoendRecord.setField("BEF_ORD_DTL" , "");// 전주문 행번
					FrtoendRecord.setField("MMATL_FEE_NO" , "");// 모재료번호
					FrtoendRecord.setField("ORDERTRANS_MATCH_GP" , "");// 목전충당구분
					
					jrRtn = commUtils.addSndData(jrRtn, FrtoendRecord);
				}
				
				// 이송완료 후 YDCTJ032전문 송신
				JDTORecord FrtoendRecord2 = null;
				FrtoendRecord2 = JDTORecordFactory.getInstance().create();
				FrtoendRecord2.setField("JMS_TC_CD" , "YDCTJ032");
				FrtoendRecord2.setField("JMS_TC_CREATE_DDTT" , currDate);
				FrtoendRecord2.setField("PTOP_PLNT_GP" , "HB");
				FrtoendRecord2.setField("STL_APPEAR_GP" , "C");
				FrtoendRecord2.setField("CHG_SUP_PROG_STAT" , "09");
				FrtoendRecord2.setField("WR_OCCR_DT" , currDate);
				FrtoendRecord2.setField("YD_EQP_WR_CNT" , "1");
				FrtoendRecord2.setField("STL_NO1" , sSTOCK_ID);
				
				jrRtn = commUtils.addSndData(jrRtn, FrtoendRecord2);
			}
			
		    commUtils.printLog("", "이송백업 실적처리 END", "[INFO]-");
        	
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}		
	
	
	/**
	 *      [A] 오퍼레이션명 : INSERT,UPDATE (Transaction 분리)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public boolean execQueryIdTx(JDTORecord rcvMsg, String queryId) throws DAOException {
		String methodNm = "Transaction 분리 수행 [YmCommSeEJB.execQueryIdTx] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			commDao.update(rcvMsg, queryId, logId, methodNm, "Transaction 분리 수행");
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	} 	
	
	
	/**
	 * 화면 도움말 - 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord setPageHelpInfo(GridData gdReq) throws DAOException {
		String methodNm = "화면 도움말 등록[YmCommSeEJB.setPageHelpInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("PAGE_ID"		,gdReq.getParam("PAGE_ID"	)); 
			jrParam.setField("PAGE_PT"		,gdReq.getParam("PAGE_PT"	)); 
			jrParam.setField("SCR_REMARK"	,gdReq.getParam("SCR_REMARK")); 
			jrParam.setField("DEL_YN"		,gdReq.getParam("DEL_YN"	)); 
			jrParam.setField("REGISTER"		,gdReq.getParam("REGISTER"	)); 
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.setPageHelpInfo", logId, methodNm, "화면도움말등록");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updPageHelpInfo
	
	
	/**
	 * 화면 도움말 - 버튼등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord setPageHelpBtnInfo(GridData gdReq) throws DAOException {
		String methodNm = "화면 도움말 - 버튼등록[YmCommSeEJB.setPageHelpBtnInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
				/* 이전 버전 버튼 미사용 처리
				-- com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updPageHelpBtnRvsInit
				-- 1열연 YD 화면 도움말 - 이전 버전 버튼 미사용 처리(신규버전 버튼정보 입력 전 실행)
				UPDATE USRYMA.TB_YM_HELP_BTN B
				   SET  B.DEL_YN  = 'Y'    -- 삭제여부   
				WHERE   B.PAGE_ID = :V_PAGE_ID   -- 화면ID
				*/
				jrParam.setField("PAGE_ID", gdReq.getParam("PAGE_ID"));
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updPageHelpBtnRvsInit", logId, methodNm, "이전 버전 버튼 미사용 처리");
				
				/* 다음 버전 조회
				-- com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getPageHelpBtnNextRvsNo
				-- 1열연 YD 화면 도움말 - 버튼 다음 버전 조회
				SELECT NVL(MAX(RVS_NO), 0) +1 AS RVS_NO
				  FROM USRYMA.TB_YM_HELP_BTN B
				 WHERE B.PAGE_ID = :V_PAGE_ID   -- 화면ID
				*/
				String nextRvsNo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getPageHelpBtnNextRvsNo", logId, methodNm, "다음 버전 조회").getRecord(0).getFieldString("RVS_NO");
				
			for (int ii = 0; ii < rowCnt; ii++) {
				
				// 파라미터 Set.
				jrParam.setField("PAGE_ID"	,commUtils.getValue(gdReq, "PAGE_ID"	, ii)	); 
				jrParam.setField("BTN_ID"	,commUtils.getValue(gdReq, "BTN_ID"		, ii)	);
				jrParam.setField("BTN_NM"	,commUtils.getValue(gdReq, "BTN_NM"		, ii)	);
				jrParam.setField("RVS_NO"	,nextRvsNo 										);
				jrParam.setField("BTN_IMG_PATH"	,commUtils.getValue(gdReq, "BTN_IMG_PATH"		, ii)	);
				jrParam.setField("BTN_DISC"	,commUtils.getValue(gdReq, "BTN_DISC"	, ii)	);
				jrParam.setField("REGISTER"	,commUtils.getValue(gdReq, "REGISTER"	, ii)	);
				jrParam.setField("MODIFIER"	,commUtils.getValue(gdReq, "MODIFIER"	, ii)	);
				
				/*
					-- com.inisteel.cim.ym.bcommon.dao.YmCommDAO.setPageHelpBtnInfo
					-- 1열연 YD 화면 도움말 - 버튼등록
					INSERT INTO USRYMA.TB_YM_HELP_BTN B(
					      B.PAGE_ID   -- 화면ID     
					    , B.BTN_ID    -- 버튼ID
					    , B.BTN_NM    -- 버튼명
					    , B.RVS_NO    -- Revision
					    , B.BTN_IMG_PATH -- 버튼 이미지 경로
					    , B.BTN_DISC  -- Discription
					    , B.BTN_SEQ   -- 순번       
					    , B.DEL_YN    -- 삭제여부   
					    , B.REGISTER  -- 등록자     
					    , B.REG_DDTT  -- 등록 일시  
					    , B.MODIFIER  -- 수정자     
					    , B.MOD_DDTT  -- 수정 일시  
					) VALUES (
					      :V_PAGE_ID   -- 화면ID     
					    , :V_BTN_ID    -- 버튼ID
					    , :V_BTN_NM    -- 버튼명
					    , :V_RVS_NO    -- 버전       
					    , :V_BTN_IMG_PATH -- 버튼 이미지 경로
					    , :V_BTN_DISC  -- Discription
					    , (SELECT NVL(MAX(BTN_SEQ)+1, 1) FROM USRYMA.TB_YM_HELP_BTN WHERE PAGE_ID = :V_PAGE_ID)   -- 순번       
					    , 'N'          -- 삭제여부   
					    , :V_REGISTER  -- 등록자     
					    , SYSDATE      -- 등록 일시  
					    , :V_MODIFIER  -- 수정자     
					    , SYSDATE      -- 수정 일시  
					)
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.setPageHelpBtnInfo", logId, methodNm, "1열연 YD 화면 도움말 - 버튼등록");
								
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of setPageHelpBtnInfo
	
	
	/**
	 * 화면 도움말 - 작업방법(버튼상세) 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord setPageHelpBtnDtlInfo(GridData gdReq) throws DAOException {
		String methodNm = "화면 도움말 - 작업방법(버튼상세) 등록[YmCommSeEJB.setPageHelpBtnDtlInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
				/* 이전 버전 data 삭제
				-- com.inisteel.cim.ym.bcommon.dao.YmCommDAO.delPageHelpBtnDtlInfo
				-- 화면 도움말 - 작업방법(버튼상세) 삭제
				DELETE USRYMA.TB_YM_HELP_BTNDTL B
				 WHERE B.PAGE_ID = :V_PAGE_ID
				   AND B.BTN_ID  = :V_BTN_ID
				*/
				jrParam.setField("PAGE_ID"	, gdReq.getParam("PAGE_ID"	));
				jrParam.setField("BTN_ID"	, gdReq.getParam("BTN_ID"	));
				commDao.delete(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.delPageHelpBtnDtlInfo", logId, methodNm, "이전 버전 data 삭제");

			for (int ii = 0; ii < rowCnt; ii++) {
				
				// 파라미터 Set.
				jrParam.setField("PAGE_ID"	,commUtils.getValue(gdReq, "PAGE_ID"	, ii)	); 
				jrParam.setField("BTN_ID"	,commUtils.getValue(gdReq, "BTN_ID"		, ii)	);
				jrParam.setField("BTN_SEQ"	,commUtils.getValue(gdReq, "BTN_SEQ"	, ii)	);
				jrParam.setField("BTN_CMNT"	,commUtils.getValue(gdReq, "BTN_CMNT"	, ii)	);
				jrParam.setField("BTN_DISC"	,commUtils.getValue(gdReq, "BTN_DISC"	, ii)	);
				jrParam.setField("REGISTER"	,commUtils.getValue(gdReq, "REGISTER"	, ii)	);
				jrParam.setField("MODIFIER"	,commUtils.getValue(gdReq, "MODIFIER"	, ii)	);
				
				/*
					-- com.inisteel.cim.ym.bcommon.dao.YmCommDAO.setPageHelpBtnDtlInfo
					-- 1열연 YD 화면 도움말 - 작업방법(버튼상세)등록
					INSERT INTO USRYMA.TB_YM_HELP_BTNDTL B(
					      B.PAGE_ID   -- 화면ID   
					    , B.BTN_ID    -- 버튼ID   
					    , B.BTN_SEQ   -- 순번     
					    , B.BTN_CMNT  -- 버튼설명 
					    , B.DEL_YN    -- 삭제여부 
					    , B.REGISTER  -- 등록자   
					    , B.REG_DDTT  -- 등록 일시
					    , B.MODIFIER  -- 수정자   
					    , B.MOD_DDTT  -- 수정 일시
					) VALUES (
					      :V_PAGE_ID   -- 화면ID   
					    , :V_BTN_ID    -- 버튼ID   
					    , :V_BTN_SEQ   -- 순번
					    , :V_BTN_CMNT  -- 버튼설명
					    , 'N'          -- 삭제여부 
					    , :V_REGISTER  -- 등록자   
					    , SYSDATE      -- 등록 일시
					    , :V_MODIFIER  -- 수정자   
					    , SYSDATE      -- 수정 일시 
					)
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.setPageHelpBtnDtlInfo", logId, methodNm, "작업방법(버튼상세)등록");
								
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of setPageHelpBtnInfo
	
	
	/**
	 * 화면 도움말 - 신규 문서번호 채번
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord getPageHelpDocMaxDocSeq(JDTORecord inDto) throws DAOException {
		String methodNm = "화면 도움말 - 신규 문서번호 채번[YmCommSeEJB.getPageHelpDocMaxDocSeq ] < ";
		String logId = "x";

		try {

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	// 처리결과 건수 담아서 리턴

			int rtn = 0;	// 결과 처리건수
			
			/* 
				-- com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getPageHelpDocMaxDocSeq
				-- 화면 도움말 - 신규 문서번호 조회
				SELECT NVL(MAX(DOC_SEQ), 0)+1 AS DOC_SEQ FROM USRYMA.TB_YM_HELP_DOC WHERE PAGE_ID = :V_PAGE_ID
			*/
			
			String nextSeq = commDao.select(inDto, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getPageHelpDocMaxDocSeq").getRecord(0).getFieldString("DOC_SEQ");
			
			jrRtn.addField("DOC_SEQ", nextSeq); // 처리 결과 수 return;
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of getPageHelpDocMaxDocSeq
	
	
	/**
	 * 화면 도움말 - 첨부문서 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord setPageHelpDoc(JDTORecord inDto) throws DAOException {
		String methodNm = "화면 도움말 - 첨부문서 등록[YmCommSeEJB.setPageHelpDoc] < ";
		String logId = "x";

		try {

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	// 처리결과 건수 담아서 리턴


			int rtn = 0;	// 결과 처리건수
			/* 
			-- com.inisteel.cim.ym.bcommon.dao.YmCommDAO.setPageHelpDoc
			-- 화면 도움말 - 첨부문서 등록
			INSERT INTO USRYMA.TB_YM_HELP_DOC (
			      PAGE_ID   -- 화면ID   
			    , DOC_SEQ   -- 문서번호 
			    , DOC_NM    -- 문서명   
			    , DOC_PATH  -- 문서경로 
			    , REGISTER  -- 등록자   
			    , REG_DDTT  -- 등록 일시
			    , MODIFIER  -- 수정자   
			    , MOD_DDTT  -- 수정 일시
			    , DEL_YN    -- 삭제여부 
			) VALUES (
			      :V_PAGE_ID   -- 화면ID   
			    , (SELECT NVL(MAX(DOC_SEQ), 0)+1 FROM USRYMA.TB_YM_HELP_DOC WHERE PAGE_ID = :V_PAGE_ID)  -- 문서번호 
			    , :V_DOC_NM    -- 문서명
			    , :V_DOC_PATH  -- 문서경로 
			    , :V_REGISTER  -- 등록자   
			    , SYSDATE      -- 등록 일시
			    , :V_MODIFIER  -- 수정자   
			    , SYSDATE      -- 수정 일시
			    , 'N'          -- 삭제여부
			)
			*/
			
			rtn = commDao.insert(inDto, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.setPageHelpDoc");
			
			jrRtn.addField("rtn", "" + rtn); // 처리 결과 수 return;
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of setPageHelpDoc
	
	
	/**
	 * 화면 도움말 - 첨부문서 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delPageHelpDoc(JDTORecord inDto) throws DAOException {
		String methodNm = "화면 도움말 - 첨부문서 삭제[YmCommSeEJB.updPageHelpDoc] < ";
		String logId = "x";

		try {

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	// 처리결과 건수 담아서 리턴
			int rtn = 0;	// 결과 처리건수
			/* 
			-- com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updPageHelpDocDelYn
			-- 화면 도움말 - 문서삭제처리
			UPDATE USRYMA.TB_YM_HELP_DOC D
			   SET D.MODIFIER = :V_MODIFIER
			     , D.MOD_DDTT = SYSDATE
			     , D.DEL_YN   = 'Y'
			 WHERE D.PAGE_ID  = :V_PAGE_ID
			   AND D.DOC_SEQ  = :V_DOC_SEQ
			*/
			
			rtn = commDao.update(inDto, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updPageHelpDocDelYn");
			
			jrRtn.addField("rtn", "" + rtn); // 처리 결과 수 return;
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updPageHelpDoc
}	
