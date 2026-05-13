/**
 * @(#)ASlabJspSeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      박판열연 Slab 야드 화면 관리 Session EJB 
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 *        2019/11/20
 */
package com.inisteel.cim.yf.aslab.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yf.common.YfCommUtils;
import com.inisteel.cim.yf.common.YfConstant;
import com.inisteel.cim.yf.common.YfQueryIF;
import com.inisteel.cim.yf.common.YfQueryIF2;
import com.inisteel.cim.yf.common.dao.YfCommDAO;
import com.inisteel.cim.yf.common.session.YfComm;
import com.inisteel.cim.yf.common.session.YfCommCarMvSeEJBSBean;

/**
 *      [A] 클래스명 : 박판열연 Slab 야드 화면관리 Session EJB 
 *
 * @ejb.bean name="ASlabJspSeEJB" jndi-name="ASlabJspSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class ASlabJspSeEJBSBean extends BaseSessionBean implements YfQueryIF, YfQueryIF2
{
	private YfCommUtils	commUtils		= new YfCommUtils();
	private YfCommDAO	commDao			= new YfCommDAO();
	private YfComm      comm			= new YfComm();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException 
	{
		
	}
	
	/**
	 * GridData - 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData getSelectData(GridData gdReq) throws DAOException 
	{
		String methodNm = "조회[ASlabJspSeEJB.getSelectData] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try 
		{
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);	//Grid date 를 JDTORecord data 로 변환
			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			commDao.jspSelect(inRecord, outRecSet, inRecord.getFieldString("QUERY_ID"), logId, methodNm);	
			
			//UI로 반환 할 Grid data 를 생성 
			//GridData gdRet = CmUtil.genGridData(gdReq, outRecSet); -- old version
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			GridData gdRet = commUtils.jdtoRecordToGridData(gdRtn, outRecSet.toList(), gdReq);
			
			commUtils.printLog(logId, methodNm, "S-", gdReq);
			
			return gdRet;
		} 
		catch(DAOException e) 
		{
			throw e;
		} 
		catch(Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}		

	/**
	 * 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSelectData(JDTORecord recPara) throws DAOException 
	{
		String methodNm = "조회[ASlabJspSeEJB.getSelectData] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();
		
		try 
		{	
			commUtils.printLog(logId, methodNm, "S+", recPara);
			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			commDao.jspSelect(recPara, outRecSet, recPara.getFieldString("QUERY_ID"), logId, methodNm);	
			
			commUtils.printLog(logId, methodNm, "S-", recPara);
			
			return outRecSet;	
		} 
		catch(DAOException e) 
		{
			throw e;
		} 
		catch(Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : 박판Slab 크레인스케줄취소 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord cancelSlabSchInfo(JDTORecord rcvMsg) throws DAOException 
	{
		String methodNm = "크레인 스케줄취소[ASlabJspSeEJB.cancelSlabSchInfo] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try
		{ 
			commUtils.printLog(logId, methodNm, "S+");

			String sYD_CRN_SCH_ID     = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")), "");		//야드크레인스케쥴ID
			String sYD_WBOOK_ID       = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID")), "");			//야드작업예약ID
			String sYD_L2_RETURN_FLAG = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("YD_L2_RETURN_FLAG")), "");	
			String sIS_SCH_MTL        = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("IS_SCH_MTL")), ""); 
			String sWRK_CNCL_YN       = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("WRK_CNCL_YN")), "N");		//작업취소여부

			if ("".equals(sYD_CRN_SCH_ID)) 
			{
				throw new Exception("크레인스케쥴ID가 없습니다.");
			} 
			else if ("".equals(sYD_WBOOK_ID)) 
			{
				throw new Exception("작업예약ID가 없습니다.");
			}
 
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));
			jrParam.setField("YD_CRN_SCH_ID",	sYD_CRN_SCH_ID);
			jrParam.setField("YD_WBOOK_ID",		sYD_WBOOK_ID);
			
			/**********************************************************
			* 1. 크레인스케쥴 정보 Check
			**********************************************************/
			JDTORecordSet jsCrnSch = commDao.select(jrParam, getCrnWrkMgtSCSch, logId, methodNm, "크레인작업지시read");
			
			if (jsCrnSch == null || jsCrnSch.size() <= 0) 
			{
				throw new Exception("크레인스케쥴ID[" + sYD_CRN_SCH_ID + "]의 크레인스케줄 정보가 존재하지 않습니다.");
		    }
			
			JDTORecord jrCrnSch = jsCrnSch.getRecord(0);
			
		    String ydWrkProgStat = commUtils.trim(jrCrnSch.getFieldString("YD_WRK_PROG_STAT"));	//야드작업진행상태
		    String eqpUpdYn      = commUtils.trim(jrCrnSch.getFieldString("EQP_UPD_YN"));		//설비상태수정여부
		    String ydEqpId       = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));		//야드설비ID
		    
		    commUtils.printLog(logId, "삭제대상크레인스케줄 YD_CRN_SCH_ID ["+sYD_CRN_SCH_ID+"]", "[INFO]");
		    commUtils.printLog(logId, "야드작업진행상태 YD_WRK_PROG_STAT ["+ydWrkProgStat+"]", "[INFO]");
		    
			if("2".equals(ydWrkProgStat)) 
			{
				throw new Exception("크레인스케줄 [" + sYD_CRN_SCH_ID + "]의 작업진행상태가 [2:권상완료]이므로 취소하실 수 없습니다.");
			} 
			else if("3".equals(ydWrkProgStat)) 
			{
				throw new Exception("크레인스케줄 [" + sYD_CRN_SCH_ID + "]의 작업진행상태가 [3:권하지시]이므로 취소하실 수 없습니다.");
			} 
			else if("4".equals(ydWrkProgStat)) 
			{
				throw new Exception("크레인스케줄 [" + sYD_CRN_SCH_ID + "]의 작업진행상태가 [4:권하완료]이므로 취소하실 수 없습니다.");
			}
			
			/**********************************************************
			* 2. 작업진행상태가 [1:권상지시] 이면 작업지시취소 전문 전송
			**********************************************************/
			if("1".equals(ydWrkProgStat) && !"Y".equals(sYD_L2_RETURN_FLAG)) 
			{
				if("Y".equals(sWRK_CNCL_YN)) 
				{
					// 작업대기상태 update : 작업취소이므로 X
					jrParam.setField("YD_L2_REQUEST_STAT",	YfConstant.YD_L2_REQUEST_STAT_X);
					jrParam.setField("YD_CRN_SCH_ID",		sYD_CRN_SCH_ID);
					
					commDao.update(jrParam, updYdCrnSchProgStat, logId, methodNm, "작업대기상태 스케줄 취소(X) UPDATE");
				}
				
				jrParam.setField("YD_CRN_SCH_ID",	sYD_CRN_SCH_ID);	//야드크레인스케쥴ID
				jrParam.setField("MSG_GP",			"D");				//전문구분(취소)

				//jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF0L004", jrParam));	//A열연 박판Slab는 L2가 없음...주석처리
			}
 
			/**********************************************************
			* 3. 권상, 권하위치 원복 - 적치단, 적치Bed
			**********************************************************/
			//적치단 수정 - 권상위치(U -> C), 권하위치(D -> E)
			String sQueryId = getCrnWrkMgtSCStkLyr;
			
			if ("Y".equals(sIS_SCH_MTL))
			{
				sQueryId = getCrnWrkMgtSCStkLyrMtl;
			}
			JDTORecordSet rsResult = commDao.select(jrParam, sQueryId, logId, methodNm, "TB_YF_STKLYR");
			
			for (int i = 0; i < rsResult.size(); ++i)
			{	
				jrParam.setField("STL_NO",			rsResult.getRecord(i).getFieldString("STL_NO"));
				jrParam.setField("YD_STK_LYR_STAT",	rsResult.getRecord(i).getFieldString("YD_STK_LYR_STAT"));
				jrParam.setField("YD_STK_COL_GP",	rsResult.getRecord(i).getFieldString("YD_STK_COL_GP"));
				jrParam.setField("YD_STK_BED_NO",	rsResult.getRecord(i).getFieldString("YD_STK_BED_NO"));
				jrParam.setField("YD_STK_LYR_NO",	rsResult.getRecord(i).getFieldString("YD_STK_LYR_NO"));
				
				commDao.update(jrParam, updYdStkLyrYdStkColBedGp, logId, methodNm, "TB_YF_STKLYR");
			}
			
			/**********************************************************
			* 4. 크레인스케줄 삭제
			**********************************************************/
			sQueryId = updCrnWrkMgtSCCrnMtl;
			
			if ("Y".equals(sIS_SCH_MTL))
			{
				sQueryId = updCrnWrkMgtSCCrnMtlUnitMtl;
			}
			commDao.update(jrParam, sQueryId, logId, methodNm, "TB_YF_CRNWRKMTL");				
			
			sQueryId = updCrnWrkMgtSCCrnSch;
			
			if ("Y".equals(sIS_SCH_MTL)) 
			{
				sQueryId = updCrnWrkMgtSCCrnSchUnitMtl;
			}
			commDao.update(jrParam, sQueryId, logId, methodNm, "TB_YM_CRNSCH");				

			/**********************************************************
			* 5. 설비상태 수정 - 크레인이 고장 또는 Off-Line이 아니고 상태가 다르면
			**********************************************************/
			if ("Y".equals(eqpUpdYn)) 
			{
				JDTORecordSet jsEqpStat = commDao.select(jrParam, getWrkListByEqpId, logId, methodNm, "[INFO]");
				
				if (jsEqpStat.size() == 0) 
				{
					jrParam.setField("YD_EQP_PROG_STAT", "W"); //야드설비상태
				} 
				else 
				{
					jrParam.setField("YD_EQP_PROG_STAT", jsEqpStat.getRecord(0).getFieldString("YD_WRK_PROG_STAT")); //야드설비상태
				}
				
				jrParam.setField("YD_EQP_ID",	ydEqpId); //야드설비ID
				
				commDao.update(jrParam, updStatEqp, logId, methodNm, "설비상태 수정");				
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} 
		catch(DAOException e) 
		{
			throw e;
		}
		catch(Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * [A] 오퍼레이션명 : 박판Slab 작업예약 취소처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord cancelSlabwbookInfo(JDTORecord rcvMsg) throws DAOException 
	{
		String methodNm = "작업예약 취소처리[ASlabJspSeEJB.cancelSlabwbookInfo] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			String ydWbookId = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"));	//야드작업예약ID
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));		//수정자
			
			if ("".equals(ydWbookId)) 
			{
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
			JDTORecordSet jsCrnSch = commDao.select(jrParam, getCommWbCrnSch, logId, methodNm, "크레인작업지시read");
			
			if (jsCrnSch != null && jsCrnSch.size() > 0) 
			{				
				throw new Exception("작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 " + jsCrnSch.size() + " 건 존재합니다.");
		    }
			
			/**********************************************************
			* 3. 차량/대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			**********************************************************/
			//차량스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			commDao.update(jrParam, updCommCarSchWbDel, logId, methodNm, "TB_YD_CARSCH");				
		
			//대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			commDao.update(jrParam, updCommTcarSchWbDel, logId, methodNm, "TB_YM_TCARSCH");				

		    /**********************************************************
			* 4. 작업예약/재료 삭제
			**********************************************************/
			//작업예약재료 삭제
			commDao.update(jrParam, updDelYnWrkBookMtl, logId, methodNm, "작업예약/재료 삭제");				

			//작업예약 삭제
			commDao.update(jrParam, updDelYnWrkBook, logId, methodNm, "작업예약 삭제");				
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} 
		catch(DAOException e) 
		{
			throw e;
		} 
		catch(Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * 적치활성상태 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updActiveStat(GridData gdReq) throws DAOException {
		String methodNm = "적치활성상태 변경[ASlabJspSeEJB.updActiveStat] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			String pYD_STK_LYR_ACTIVE_STAT = gdReq.getParam("YD_STK_LYR_ACTIVE_STAT");
			String pYD_STK_COL_GP = gdReq.getParam("YD_STK_COL_GP");
			
			if(pYD_STK_COL_GP.length()<5){
				throw new Exception("적치열 값을 확인해주세요.");
			}
			
			if(YfConstant.STACK_LAYER_ACTIVE_STAT_O.equals(pYD_STK_LYR_ACTIVE_STAT)){	
				//선택대상 상태변경
				jrParam.setField("YD_STK_LYR_ACTIVE_STAT"	,YfConstant.STACK_LAYER_ACTIVE_STAT_C); 
				jrParam.setField("YD_STK_LYR_STAT"	,YfConstant.STACK_LAYER_STAT_E); 
				jrParam.setField("YD_STK_COL_GP"	,gdReq.getParam("YD_STK_COL_GP")); 
				jrParam.setField("YD_STK_BED_NO"	,gdReq.getParam("YD_STK_BED_NO")); 
				jrParam.setField("YD_STK_LYR_NO"	,gdReq.getParam("YD_STK_LYR_NO"));  
				commDao.update(jrParam, updateEqualUpperlayerStat, logId, methodNm, "상위 적치활성상태 변경");		
			}else{
				//선택대상 상태변경
				jrParam.setField("YD_STK_LYR_ACTIVE_STAT"	,YfConstant.STACK_LAYER_ACTIVE_STAT_O); 
				jrParam.setField("YD_STK_LYR_STAT"	,YfConstant.STACK_LAYER_STAT_C); 
				jrParam.setField("YD_STK_COL_GP"	,gdReq.getParam("YD_STK_COL_GP")); 
				jrParam.setField("YD_STK_BED_NO"	,gdReq.getParam("YD_STK_BED_NO")); 
				jrParam.setField("YD_STK_LYR_NO"	,gdReq.getParam("YD_STK_LYR_NO")); 
				commDao.update(jrParam, updateEqualLowerlayerStat, logId, methodNm, "하위 적치활성상태 변경");	
			}
			
			if(YfConstant.EQUIP_KIND_PT.equals(pYD_STK_COL_GP.substring(2,4))){
				//대상 갯수 UPDATE
				jrParam.setField("YD_STK_COL_GP",pYD_STK_COL_GP);
				jrParam.setField("YD_EQP_ID"	,pYD_STK_COL_GP);
				commDao.update(jrParam, UpdateStackMaxQnty, logId, methodNm, "차량 적치활성상태 개수변경");	
					
				commUtils.printLog(logId, methodNm, "S-");	
			}
			
			
			return jrParam;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updEqpTrblReg
	
	/**
	 * 박판열연 SLAB 벤딩표시,해제,보급 설정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updBendingStat(GridData gdReq) throws DAOException {
		String methodNm = "박판열연 SLAB 벤딩표시,해제,보급 설정 - LAYER활성상태 변경[ASlabJspSeEJB.updBendingStat] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet rsQuery;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			String sYD_RULE_PL_RS_GP = commUtils.trim(gdReq.getParam("YD_RULE_PL_RS_GP")			); //1:벤딩표시,2:벤딩해제
			String sBENDING_GP 	= commUtils.nvl( gdReq.getParam("BENDING_AXIS"),"+"	); //BENDING량(mm)
			String []sARR_STOCK_ID 	= commUtils.trim(gdReq.getParam("ARR_STL_NO")).split(","); //대상 SLAB 리스트
			
			String sSTL_NO;
			String sSTOCK_MOVE_TERM = "";				
			String sCURR_PROG_CD;
			String sWO_MSLAB_RPR_MTD;
			
			/*
			 * Bending 수치 미 입력으로 인한 주석처리
			 */
//			int axisNo = Integer.parseInt(sBENDING_AXIS);
//			if(axisNo >= 0){
//				sBENDING_GP = "+";
//				sBENDING_AXIS = String.valueOf(Math.abs(axisNo));
//			}else{
//				sBENDING_GP = "-";
//				sBENDING_AXIS = String.valueOf(Math.abs(axisNo));
//			}
			
			for(int i = 0; i < sARR_STOCK_ID.length; i++) {
				sSTL_NO = sARR_STOCK_ID[i];
				
				jrParam.setField("SLAB_NO",			sSTL_NO);
				rsQuery = commDao.select(jrParam, getInitSlabInfo, logId, methodNm, "소재차량도착(영차)시 SLAB공통정보 조회");
				
				if(rsQuery.size() > 0) 
				{
					sCURR_PROG_CD 		= commUtils.trim(rsQuery.getRecord(0).getFieldString("CURR_PROG_CD"));		//현재진도코드
					sWO_MSLAB_RPR_MTD	= commUtils.trim(rsQuery.getRecord(0).getFieldString("WO_MSLAB_RPR_MTD"));	//Scarfing Pattern
					
					sSTOCK_MOVE_TERM	= comm.getStockMoveTerm(sCURR_PROG_CD, sWO_MSLAB_RPR_MTD);
				}
			
				jrParam.setField("STOCK_MOVE_TERM"	, sSTOCK_MOVE_TERM);
				jrParam.setField("BENDING_GP"		, sBENDING_GP); 
				jrParam.setField("BENDING_AXIS"		, "0"); 
				jrParam.setField("YD_RULE_PL_RS_GP"	, sYD_RULE_PL_RS_GP); 
				jrParam.setField("STL_NO"			, sSTL_NO); 

				commDao.update(jrParam, updStockBendingStat, logId, methodNm, "저장품 Bending상태 변경");
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
	 * 박판열연 SLAB 마킹표시,해제,보급 설정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updMarkingStat(GridData gdReq) throws DAOException {
		String methodNm = "박판열연 SLAB 마킹표시,해제 설정[ASlabJspSeEJB.updMarkingStat] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		/*
		 * 마킹 사용안함
		 */
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			String pMKNG_GP = commUtils.trim(gdReq.getParam("MKNG_GP")); 
			String []pARR_STOCK_ID 	= commUtils.trim(gdReq.getParam("ARR_STL_NO")).split(","); //대상 SLAB 리스트
			
			String pSTL_NO;
			
			for(int i = 0; i < pARR_STOCK_ID.length; i++) {
				pSTL_NO = pARR_STOCK_ID[i];
				
				jrParam.setField("STL_NO",			pSTL_NO);
				jrParam.setField("MKNG_GP",			pMKNG_GP);

				commDao.update(jrParam, updStockMarkingStat, logId, methodNm, "저장품 마킹상태 변경");
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updMarkingStat	
	
	/**
	 * [A] 오퍼레이션명 : 박판Slab 위치 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updSlabLoc(GridData gdReq) throws DAOException {
		String methodNm = "박판Slab 위치 변경[ASlabJspSeEJB.updSlabLoc] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		JDTORecord jrRtn = null;
		JDTORecord jrRtnItem = null;
		
		try{
			commUtils.printLog(logId, methodNm, "S+");			

			String pStlList = StringHelper.evl(commUtils.trim(gdReq.getParam("STL_LIST")), "");	//권상목록
			String toLoc = StringHelper.evl(commUtils.trim(gdReq.getParam("TO_LOC")), "");	//권하위치
			String fromLoc = "";
			String pBtcGp = StringHelper.evl(commUtils.trim(gdReq.getParam("BTC_GP")), YfConstant.CRANE_FUNC_V); //크레인구분
			String pYdStkColGp = "";	
		    String pYdStkBedNo   = "";		
			String modifier  = commUtils.trim(gdReq.getParam("MODIFIER"));		//수정자
			String pYdStkCode = "";

			String sSTL_NO = "";
			String sYD_STK_COL_GP = "";
			String sYD_STK_BED_NO = "";
			String sYD_STK_LYR_NO = "";
			
			List lyrNoList = new ArrayList();
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);
			JDTORecordSet rsStkLyrQuery;
			JDTORecordSet rsQuery;
			
			if(toLoc.length()> 0){
				pYdStkColGp = toLoc.substring(0,6);
				pYdStkBedNo = toLoc.substring(6,8);
				
				/*
				 * 1.TO 위치 조회
				 */
				jrParam.setField("YD_STK_COL_GP", pYdStkColGp);
				jrParam.setField("YD_STK_BED_NO", pYdStkBedNo);
				rsStkLyrQuery = commDao.select(jrParam, getAvalableLyr, logId, methodNm, "Slab 적치 가능목록");
				
				if (rsStkLyrQuery.size() > 0){ //권상개수 vs 적치가능개수
					if(rsStkLyrQuery.size() < pStlList.split(",").length){
						throw new Exception("저장품이 권상위치보다 많아 작업을 수행 할 수 없습니다.");
					}
				}
			
				for (int i = 0; i < rsStkLyrQuery.size(); i++){	
					sYD_STK_COL_GP = StringHelper.evl(rsStkLyrQuery.getRecord(i).getFieldString("YD_STK_COL_GP"),"");
					sYD_STK_BED_NO = StringHelper.evl(rsStkLyrQuery.getRecord(i).getFieldString("YD_STK_BED_NO"),"");
					sYD_STK_LYR_NO = StringHelper.evl(rsStkLyrQuery.getRecord(i).getFieldString("YD_STK_LYR_NO"),"");
					pYdStkCode = sYD_STK_COL_GP + sYD_STK_BED_NO + sYD_STK_LYR_NO;
					
					lyrNoList.add(pYdStkCode); 
				}
				
				/*
				 * 2.대상 SLAB 조회
				 */
				jrParam.setField("STL_NO", pStlList);
				
				if(YfConstant.CRANE_FUNC_V.equals(pBtcGp)){//크레인 순서(역순)
					rsQuery = commDao.select(jrParam, getSlabListInfo, logId, methodNm, "크레인수정 정렬순서");
				}else{//산적위치 순서(정순)
					rsQuery = commDao.select(jrParam, getSlabListInfo2, logId, methodNm, "산적위치수정 정렬순서");
				}
				
				if (rsQuery.size() == 0){ 
					throw new Exception("저장품이 존재하지 않습니다.");
				}
				
				/*
				 * 3.위치변경
				 */
				for(int i=0; i<rsQuery.size();i++){					
					sYD_STK_COL_GP = StringHelper.evl(rsQuery.getRecord(i).getFieldString("YD_STK_COL_GP"),"");
					sYD_STK_BED_NO = StringHelper.evl(rsQuery.getRecord(i).getFieldString("YD_STK_BED_NO"),"");
					sYD_STK_LYR_NO = StringHelper.evl(rsQuery.getRecord(i).getFieldString("YD_STK_LYR_NO"),"");
					fromLoc = sYD_STK_COL_GP + sYD_STK_BED_NO + sYD_STK_LYR_NO;
					sSTL_NO = StringHelper.evl(rsQuery.getRecord(i).getFieldString("STL_NO"),"");
					
					gdReq.addParam("STL_NO", sSTL_NO);
					gdReq.addParam("FROM_LOC", fromLoc);
					gdReq.addParam("TO_LOC", (String)lyrNoList.get(i));
					gdReq.addParam("BTC_GP", pBtcGp);
					gdReq.addParam("YD_SCH_CD", "");		
					jrRtnItem = changeSlabLocationInfo(gdReq);
					jrRtn 	= commUtils.addSndData(jrRtn, jrRtnItem);
				}	
			}

			return jrRtn;
		} 
		catch(DAOException e) 
		{
			throw e;
		} 
		catch(Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * [A] 오퍼레이션명 : 박판Slab 끼워넣기
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord inLaySlab(GridData gdReq) throws DAOException {
		String methodNm = "박판Slab 끼워넣기[ASlabJspSeEJB.inLaySlab] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrRtnItem = null;
		
		try{
			commUtils.printLog(logId, methodNm, "S+");			

			String pStlNo = StringHelper.evl(commUtils.trim(gdReq.getParam("STL_LIST")), "");
			String toLoc = StringHelper.evl(commUtils.trim(gdReq.getParam("TO_LOC")), "");	//권하위치
			String fromLoc = "";
			String pBtcGp = YfConstant.CRANE_FUNC_S; //크레인구분
			String pYdStkColGp = "";	
		    String pYdStkBedNo   = "";		
		    String pYdStkLyrNo   = "";	
			
			String sYdStkCode = "";
			String sYdStkColGp = "";	
		    String sYdStkBedNo   = "";		
		    String sYdStkLyrNo   = "";	
			String toStlNo = "";
			String modifier  = commUtils.trim(gdReq.getParam("MODIFIER"));		//수정자

			List stlNoList = new ArrayList();
			List lyrNoList = new ArrayList();
			String sStlNo = "";
			String sYD_STK_COL_GP = "";
			String sYD_STK_BED_NO = "";
			String sYD_STK_LYR_NO = "";
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);
			JDTORecordSet rsStkLyrQuery;
			JDTORecordSet rsQuery;
			
			if(toLoc.length()> 0){
				pYdStkColGp = toLoc.substring(0,6);
				pYdStkBedNo = toLoc.substring(6,8);
				pYdStkLyrNo = toLoc.substring(8);
				
				/*
				 * 1.To 위치의 Slab 조회
				 */
				jrParam.setField("YD_STK_COL_GP", pYdStkColGp);
				jrParam.setField("YD_STK_BED_NO", pYdStkBedNo);
				jrParam.setField("YD_STK_LYR_NO", pYdStkLyrNo);
				rsStkLyrQuery = commDao.select(jrParam, getStkLyr, logId, methodNm, "Slab 적치가능위치 조회");
				
				if(rsStkLyrQuery.size()>0){
					toStlNo = StringHelper.evl(rsStkLyrQuery.getRecord(0).getFieldString("STL_NO"),"");
				}else{
					throw new Exception("해당 적치단 정보가 존재하지 않습니다.");
				}
				
				/*
				 * 2.To 적치가능 적치단 조회
				 */
				jrParam.setField("YD_STK_COL_GP", pYdStkColGp);
				jrParam.setField("YD_STK_BED_NO", pYdStkBedNo);
				jrParam.setField("YD_STK_LYR_ACTIVE_STAT", YfConstant.STACK_LAYER_ACTIVE_STAT_O);
				jrParam.setField("YD_STK_LYR_STAT", YfConstant.STACK_LAYER_STAT_E);
				rsStkLyrQuery = commDao.select(jrParam, getLyrPossible, logId, methodNm, "Slab 적치가능위치 조회");
					
				/*
				 * 3.TO 위치항목 조회
				 */
				if("".equals(toStlNo)){
					lyrNoList.add(toLoc);
				}else{
					if(rsStkLyrQuery.size()>0){
						sYdStkColGp = StringHelper.evl(rsStkLyrQuery.getRecord(0).getFieldString("YD_STK_COL_GP"),"");
						sYdStkBedNo = StringHelper.evl(rsStkLyrQuery.getRecord(0).getFieldString("YD_STK_BED_NO"),"");
						sYdStkLyrNo = StringHelper.evl(rsStkLyrQuery.getRecord(0).getFieldString("YD_STK_LYR_NO"),"");
						sYdStkCode = sYdStkColGp + sYdStkBedNo + sYdStkLyrNo;
						
						lyrNoList.add(sYdStkCode);
						
						jrParam.setField("STL_NO", toStlNo);
						rsStkLyrQuery = commDao.select(jrParam, getLyrUpperSlab, logId, methodNm, "상위 Slab 조회");
					
						for (int i = 0; i < rsStkLyrQuery.size(); i++){	//TO 열 체크
							sYdStkColGp = StringHelper.evl(rsStkLyrQuery.getRecord(i).getFieldString("YD_STK_COL_GP"),"");
							sYdStkBedNo = StringHelper.evl(rsStkLyrQuery.getRecord(i).getFieldString("YD_STK_BED_NO"),"");
							sYdStkLyrNo = StringHelper.evl(rsStkLyrQuery.getRecord(i).getFieldString("YD_STK_LYR_NO"),"");
							sStlNo = StringHelper.evl(rsStkLyrQuery.getRecord(i).getFieldString("STL_NO"),"");
							sYdStkCode = sYdStkColGp + sYdStkBedNo + sYdStkLyrNo;
							
							if(stlNoList.indexOf(sStlNo) == -1){
								stlNoList.add(sStlNo);
							}
							
							if(lyrNoList.indexOf(sYdStkCode) == -1){
								lyrNoList.add(sYdStkCode);
							}
						}
					}else{
						throw new Exception("해당 적치열에 넣을 공간이 존재하지 않습니다.");
					}
				}
				
				/*
				 * 4.From 위치항목 조회 
				 */
				jrParam.setField("STL_NO", pStlNo);
				rsStkLyrQuery = commDao.select(jrParam, getLyrUpperSlab2, logId, methodNm, "From절 Update 항목");
				
				for (int i = 0; i < rsStkLyrQuery.size(); i++){	//TO 열 체크
					
					sYdStkColGp = StringHelper.evl(rsStkLyrQuery.getRecord(i).getFieldString("YD_STK_COL_GP"),"");
					sYdStkBedNo = StringHelper.evl(rsStkLyrQuery.getRecord(i).getFieldString("YD_STK_BED_NO"),"");
					sYdStkLyrNo = StringHelper.evl(rsStkLyrQuery.getRecord(i).getFieldString("YD_STK_LYR_NO"),"");
					sYdStkCode = sYdStkColGp + sYdStkBedNo + sYdStkLyrNo;
					sStlNo = StringHelper.evl(rsStkLyrQuery.getRecord(i).getFieldString("STL_NO"),"");
					
					if(stlNoList.indexOf(sStlNo) == -1){
						stlNoList.add(sStlNo);
					}
					
					if(lyrNoList.indexOf(sYdStkCode) == -1){
						lyrNoList.add(sYdStkCode);
					}
				}
				
				/*
				 * 5.TO 위치 체크 및 TO위치 수정항목들 조회
				 */					
				for(int i=0; i<stlNoList.size();i++){
					pStlNo = (String)stlNoList.get(i);
					
					jrParam.setField("STL_NO", pStlNo);
					rsQuery = commDao.select(jrParam, getStlNoLocInfo2, logId, methodNm, "적치단 정보");
	
					if(rsQuery.size() == 0 || rsQuery == null){
						throw new Exception(pStlNo + "를 가진 적치단이 존재하지 않습니다.");
					}
						
					for (int ii = 0; ii < rsQuery.size(); ii++){	//권하위치 확인			
						sYD_STK_COL_GP = StringHelper.evl(rsQuery.getRecord(ii).getFieldString("YD_STK_COL_GP"),"");
						sYD_STK_BED_NO = StringHelper.evl(rsQuery.getRecord(ii).getFieldString("YD_STK_BED_NO"),"");
						sYD_STK_LYR_NO = StringHelper.evl(rsQuery.getRecord(ii).getFieldString("YD_STK_LYR_NO"),"");
						fromLoc = sYD_STK_COL_GP + sYD_STK_BED_NO + sYD_STK_LYR_NO;
						
						gdReq.addParam("STL_NO", pStlNo);
						gdReq.addParam("FROM_LOC", fromLoc);
						gdReq.addParam("TO_LOC", (String)lyrNoList.get(i));
						gdReq.addParam("BTC_GP", pBtcGp);
						gdReq.addParam("YD_SCH_CD", "");
						jrRtnItem = changeSlabLocationInfo(gdReq);
						jrRtn 	= commUtils.addSndData(jrRtn, jrRtnItem);
					}
				}
			}

			return jrRtn;
		} 
		catch(DAOException e) 
		{
			throw e;
		} 
		catch(Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * [A] 오퍼레이션명 : 박판Slab 덮어쓰기
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord coverSlab(GridData gdReq) throws DAOException {
		String methodNm = "박판Slab 덮어쓰기[ASlabJspSeEJB.coverSlab] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrRtnItem = null;
		
		try{
			commUtils.printLog(logId, methodNm, "S+");			

			String pStlList =StringHelper.evl(commUtils.trim(gdReq.getParam("STL_LIST")), "");	//권상목록
			String toLoc = StringHelper.evl(commUtils.trim(gdReq.getParam("TO_LOC")), "");	//권하위치
			String fromLoc = "";
			String pBtcGp = YfConstant.CRANE_FUNC_S; //크레인구분
			String modifier  = commUtils.trim(gdReq.getParam("userid"));		//수정자
			
			JDTORecord jrResetParam = commUtils.getParam(logId, methodNm, modifier);
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);
			JDTORecordSet rsQuery;
			
			String sSTL_NO = "";
			String sYD_STK_COL_GP = toLoc.substring(0,6);
			String sYD_STK_BED_NO = toLoc.substring(6,8);
			String sYD_STK_LYR_NO = toLoc.substring(8);
			String sYdStkCode = "";
			
			List stlNoList = new ArrayList();
			List lyrNoList = new ArrayList();
			
			/*
			 * 1.덮여서 초기화 될 Slab 조회
			 */
			jrResetParam.setField("YD_STK_COL_GP", sYD_STK_COL_GP);
			jrResetParam.setField("YD_STK_BED_NO", sYD_STK_BED_NO);
			jrResetParam.setField("YD_STK_LYR_NO", sYD_STK_LYR_NO);
			JDTORecordSet rsSlabQuery = commDao.select(jrResetParam, getStkLyr, logId, methodNm, "초기화 할 Slab항목 조회");
			
			/*
			 * 2.TO위치의 대상 Slab 초기화
			 */
			if(rsSlabQuery.size()>0){
				sSTL_NO = rsSlabQuery.getRecord(0).getFieldString("STL_NO");
				
				if(!"".equals(sSTL_NO)){
					jrResetParam.setField("STL_NO", "");
					jrResetParam.setField("YD_STK_LYR_STAT", YfConstant.STACK_LAYER_STAT_E);
					jrResetParam.setField("YD_STK_COL_GP", sYD_STK_COL_GP);
					jrResetParam.setField("YD_STK_BED_NO", sYD_STK_BED_NO);
					jrResetParam.setField("YD_STK_LYR_NO", sYD_STK_LYR_NO);
					commDao.update(jrResetParam, updateStackLayerStat, logId, methodNm, "TB_YF_STKLYR SLAB초기화");
					
					gdReq.addParam("STL_NO", sSTL_NO);
					gdReq.addParam("FROM_LOC", toLoc);
					gdReq.addParam("TO_LOC", "");
					gdReq.addParam("BTC_GP", pBtcGp);
					updSlabHistory(gdReq); //초기화 이력 등록
				}
			}
			
			lyrNoList.add(toLoc);
			
			/*
			 * 3.From위치 수정항목 조회 ( 옮기는 대상 + 옮기는 대상의 상위 항목 )
			 */
			jrParam.setField("STL_NO", pStlList);
			JDTORecordSet rsStkLyrQuery = commDao.select(jrParam, getLyrUpperSlab2, logId, methodNm, "From절 Update 항목");
			
			for (int i = 0; i < rsStkLyrQuery.size(); i++){	//TO 열 체크
					
				sYD_STK_COL_GP = StringHelper.evl(rsStkLyrQuery.getRecord(i).getFieldString("YD_STK_COL_GP"),"");
				sYD_STK_BED_NO = StringHelper.evl(rsStkLyrQuery.getRecord(i).getFieldString("YD_STK_BED_NO"),"");
				sYD_STK_LYR_NO = StringHelper.evl(rsStkLyrQuery.getRecord(i).getFieldString("YD_STK_LYR_NO"),"");
				sYdStkCode = sYD_STK_COL_GP + sYD_STK_BED_NO + sYD_STK_LYR_NO;
				sSTL_NO = StringHelper.evl(rsStkLyrQuery.getRecord(i).getFieldString("STL_NO"),"");
				
				if(stlNoList.indexOf(sSTL_NO) == -1){
					stlNoList.add(sSTL_NO);
				}
				
				if(lyrNoList.indexOf(sYdStkCode) == -1){
					lyrNoList.add(sYdStkCode);
				}
			}
			
			/*
			 * 4.이동 대상제들 이동 ( 옮기는 대상은 덮어쓰기 + 옮기는 대상 상위 항목은 아래로 한칸씩 내리기 )
			 */					
			for(int i=0; i<stlNoList.size();i++){
				sSTL_NO = (String)stlNoList.get(i);
				
				jrParam.setField("STL_NO", sSTL_NO);
				rsQuery = commDao.select(jrParam, getStlNoLocInfo2, logId, methodNm, "적치단 정보");

				if(rsQuery.size() == 0 || rsQuery == null){
					throw new Exception(sSTL_NO + "를 가진 적치단이 존재하지 않습니다.");
				}
					
				if (rsQuery.size()>0){	//권하위치 확인			
					sYD_STK_COL_GP = StringHelper.evl(rsQuery.getRecord(0).getFieldString("YD_STK_COL_GP"),"");
					sYD_STK_BED_NO = StringHelper.evl(rsQuery.getRecord(0).getFieldString("YD_STK_BED_NO"),"");
					sYD_STK_LYR_NO = StringHelper.evl(rsQuery.getRecord(0).getFieldString("YD_STK_LYR_NO"),"");
					fromLoc = sYD_STK_COL_GP + sYD_STK_BED_NO + sYD_STK_LYR_NO;
					
					gdReq.addParam("STL_NO", sSTL_NO);
					gdReq.addParam("FROM_LOC", fromLoc);
					gdReq.addParam("TO_LOC", (String)lyrNoList.get(i));
					gdReq.addParam("BTC_GP", pBtcGp);
					gdReq.addParam("YD_SCH_CD", "");	
					jrRtnItem = changeSlabLocationInfo(gdReq);
					jrRtn 	= commUtils.addSndData(jrRtn, jrRtnItem);
				}
			}

			return jrRtn;
		} 
		catch(DAOException e) 
		{
			throw e;
		} 
		catch(Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 *
	 * SLAB 위치 수정 메소드
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @throws Exception  
	 * @param 
	 * @return
	 */                     
	public JDTORecord changeSlabLocationInfo(GridData gdReq) throws Exception {		 //상단(U)/교체(R) 구분
		
		String sStlNo = StringHelper.evl(commUtils.trim(gdReq.getParam("STL_NO")), "");		//저장품ID 
		String sUpLoc = StringHelper.evl(commUtils.trim(gdReq.getParam("FROM_LOC")), "");		//FROM LOC
		String sPutLoc = StringHelper.evl(commUtils.trim(gdReq.getParam("TO_LOC")), "");		//TO LOC 
		String sBtcGp = StringHelper.evl(commUtils.trim(gdReq.getParam("BTC_GP")), ""); //비상조업처리구분(U:L2시스템,V:L2산적위치)
		String modifier  = commUtils.trim(gdReq.getParam("userid"));		//수정자
		
		String methodNm = "박판Slab["+sUpLoc+" >> "+sPutLoc+" :"+ sStlNo +"] SLAB 이동[ASlabJspSeEJB.changeSlabLocationInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrRst 		= null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");	
			
			gdReq.addParam("TC_CD", "");
			gdReq.addParam("STL_NO", sStlNo);

			String sCURR_PROG_CD = ""; 

			String sYD_CAR_SCH_ID = "";
			String sTRN_EQP_CD = "";
			String sYD_CAR_PROG_STAT = "";
			String sHCR_GP = "";

			if(sUpLoc.length()>0 && sPutLoc.length()>0){

				//Return Value
				JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);	
				JDTORecordSet rsColQuery;

				int yfCnt = 0; //차량재료개수
				int rtnCnt = 0; //회송차량개수
	
				String fromYdStkColGp = sUpLoc.substring(0,6);
				String fromYdStkBedNo = sUpLoc.substring(6,8);		
				String fromStkLyrNo   = sUpLoc.substring(8);
				String fromYdGp = fromYdStkColGp.substring(0,1);
				String fromBayGp = fromYdStkColGp.substring(1,2);
				String fromSectGp = fromYdStkColGp.substring(2,4);
				String fromColGp = fromYdStkColGp.substring(4,6);

				String toYdStkColGp   = sPutLoc.substring(0,6);
				String toYdStkBedNo   = sPutLoc.substring(6,8);		
				String toYdStkLyrNo   = sPutLoc.substring(8);	
				String toYdGp = toYdStkColGp.substring(0,1);
				String toBayGp = toYdStkColGp.substring(1,2);
				String toSectGp = toYdStkColGp.substring(2,4);
				String toColGp = toYdStkColGp.substring(4,6);
				
				/* 재료 데이터 조회*/
				jrParam.setField("SLAB_NO", sStlNo);
				rsColQuery = commDao.select(jrParam, selectSlabMatirialInfo, logId, methodNm, "STKCOL 조회");
				
				if(rsColQuery.size()>0){//SLAB조회
					sHCR_GP = StringHelper.evl(rsColQuery.getRecord(0).getFieldString("HCR_GP"),"");
					sCURR_PROG_CD = StringHelper.evl(rsColQuery.getRecord(0).getFieldString("CURR_PROG_CD"),"");
				}else{
					throw new EJBServiceException( sStlNo + " 정보가 존재하지 않습니다.");
				}
				
				/* 차량데이터 조회*/
				if(YfConstant.EQUIP_KIND_PT.equals(fromSectGp)){
					jrParam.setField("YD_GP", fromYdGp);
					jrParam.setField("BAY_GP", fromBayGp);
					jrParam.setField("SECT_GP", fromSectGp);
					jrParam.setField("COL_GP", fromColGp);
					rsColQuery = commDao.select(jrParam, getAxYML008CarSchLd2, logId, methodNm, "STKCOL 조회");
					
					if(rsColQuery.size()>0){//권하차량 정보
						sYD_CAR_PROG_STAT = StringHelper.evl(rsColQuery.getRecord(0).getFieldString("YD_CAR_PROG_STAT"),"");
						sTRN_EQP_CD = StringHelper.evl(rsColQuery.getRecord(0).getFieldString("TRN_EQP_CD"),"");
						sYD_CAR_SCH_ID = StringHelper.evl(rsColQuery.getRecord(0).getFieldString("YD_CAR_SCH_ID"),"");
						yfCnt = Integer.parseInt(StringHelper.evl(rsColQuery.getRecord(0).getFieldString("ITEM_CNT"),"0"));
						rtnCnt = Integer.parseInt(StringHelper.evl(rsColQuery.getRecord(0).getFieldString("RETURN_CNT"),"0"));
					}
				}else if(YfConstant.EQUIP_KIND_PT.equals(toSectGp)){
					jrParam.setField("YD_GP", toYdGp);
					jrParam.setField("BAY_GP", toBayGp);
					jrParam.setField("SECT_GP", toSectGp);
					jrParam.setField("COL_GP", toColGp);
					rsColQuery = commDao.select(jrParam, getAxYML008CarSchLd2, logId, methodNm, "STKCOL 조회");
					
					if(rsColQuery.size()>0){//권상차량 정보
						sYD_CAR_PROG_STAT = StringHelper.evl(rsColQuery.getRecord(0).getFieldString("YD_CAR_PROG_STAT"),"");
						sTRN_EQP_CD = StringHelper.evl(rsColQuery.getRecord(0).getFieldString("TRN_EQP_CD"),"");
						sYD_CAR_SCH_ID = StringHelper.evl(rsColQuery.getRecord(0).getFieldString("YD_CAR_SCH_ID"),"");
						yfCnt = Integer.parseInt(StringHelper.evl(rsColQuery.getRecord(0).getFieldString("ITEM_CNT"),"0"));
						rtnCnt = Integer.parseInt(StringHelper.evl(rsColQuery.getRecord(0).getFieldString("RETURN_CNT"),"0"));
					}				
				}
				
				/* true : 상차관련 / false : 하차관련 */
				boolean upDownFlag = commUtils.isNumber(sYD_CAR_PROG_STAT);
				
				/**
				 * 1. 첫 상차 시 차량 도착지 갱신
				 */
				if(upDownFlag == true && yfCnt == 0){//상차 첫번째 SLAB
					jrParam.setField("TRN_EQP_CD", sTRN_EQP_CD);
					jrParam.setField("STL_NO", sStlNo);
					commDao.update(jrParam, updateCarArrWlocCd, logId, methodNm, "TB_YD_CARSCH 도착지 수정");
				}
				
				/**
				 * 2. FROM 및 TO 위치 수정
				 */
				//FROM 위치 수정
				jrParam.setField("STL_NO", "");
				jrParam.setField("YD_STK_LYR_STAT", YfConstant.STACK_LAYER_STAT_E);
				jrParam.setField("YD_STK_COL_GP", fromYdStkColGp);
				jrParam.setField("YD_STK_BED_NO", fromYdStkBedNo);
				jrParam.setField("YD_STK_LYR_NO", fromStkLyrNo);
				commDao.update(jrParam, updateStackLayerStat, logId, methodNm, "TB_YF_STKLYR FROM 수정");
					
				//TO 위치 수정
				jrParam.setField("STL_NO", sStlNo);
				jrParam.setField("YD_STK_LYR_STAT", YfConstant.STACK_LAYER_STAT_C);
				jrParam.setField("YD_STK_COL_GP", toYdStkColGp);
				jrParam.setField("YD_STK_BED_NO", toYdStkBedNo);
				jrParam.setField("YD_STK_LYR_NO", toYdStkLyrNo);
				commDao.update(jrParam, updateStackLayerStat, logId, methodNm, "TB_YF_STKLYR TO 수정");

				/**
				 * 3. Crane 작업 실적 등록
				 */			     
				gdReq.addParam("STL_NO", sStlNo);
				gdReq.addParam("FROM_LOC", sUpLoc);
				gdReq.addParam("TO_LOC", sPutLoc);
				gdReq.addParam("BTC_GP", sBtcGp);
				updSlabHistory(gdReq); 
				
				/**
				 * 4. SLAB 공통 TABLE 수정
				 */
				jrRst = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
				
				jrRst.setField("YD_GP"			, toYdGp);
				jrRst.setField("BAY_GP"			, toBayGp);
				jrRst.setField("YD_EQP_GP"		, toSectGp);
				jrRst.setField("YD_STK_COL_NO"	, toColGp);
				jrRst.setField("YD_STK_BED_NO"	, toYdStkBedNo);
				jrRst.setField("YD_STK_LYR_NO"	, toYdStkLyrNo);
				jrRst.setField("SLAB_NO"		, sStlNo);
				
				EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
				sndConn.trx("updateSlabCommonLocInfo", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				
				/**
				 * 5. 차량에서 SLAB하차 시 차량에서 해당 재료 제거 및 회송차량일 경우 회송완료처리
				 */
				if(YfConstant.EQUIP_KIND_PT.equals(fromSectGp)){
					jrParam.setField("YD_STK_COL_GP", fromYdStkColGp);
					jrParam.setField("STL_NO", sStlNo);
					commDao.update(jrParam, deleteCarMtlInfo2, logId, methodNm, "TB_YD_CARFTMVMTL 차량하차");
					
					jrParam.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);
					jrParam.setField("STL_NO", sStlNo);
					commDao.update(jrParam, uptRetHtHistCmplDt, logId, methodNm, "TB_YD_RETHTHIST 회송완료코드로 수정");
				}
				
				/**
				 * 6. 차량에 SLAB상차 시 차량에 해당 재료추가
				 */
				if(YfConstant.EQUIP_KIND_PT.equals(toSectGp)){
					jrParam.setField("YD_CAR_SCH_ID"       	, sYD_CAR_SCH_ID);
					jrParam.setField("STL_NO"       		, sStlNo); //재료   
					jrParam.setField("DEL_YN"       		, "N"); //삭제여부
					jrParam.setField("YD_STK_BED_NO"       	, toYdStkBedNo); //BED NO
					jrParam.setField("YD_STK_LYR_NO"       	, "0" + toYdStkLyrNo); //적치단
					jrParam.setField("YD_CAR_UPP_LOC_CD"    , toBayGp + "1"); //차상위치
					jrParam.setField("HCR_GP"       		, sHCR_GP); 
					jrParam.setField("STL_PROG_CD"    		, sCURR_PROG_CD); //차상위치
					jrParam.setField("YD_MTL_ITEM"    		, "SM"); //SLAB
					
					commDao.update(jrParam, MergeCarftmvmtl, logId, methodNm, "차량이송재료 갱신");
				}
				
				
				/**
				 * 7. SLAB 공통 하차처리(회송차량 제외)
				 */	
				if(YfConstant.EQUIP_KIND_PT.equals(fromSectGp)
				&& upDownFlag == false
				&& rtnCnt == 0){
					jrRst = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
					jrRst.setField("STL_NO", sStlNo);
					
					sndConn = new EJBConnector("default", "YfCommSeEJB", this);
					JDTORecord jrPTrst = (JDTORecord)sndConn.trx("updateFtmvCmtl_Slab", new Class[] { JDTORecord.class }, new Object[] { jrRst });
					jrRtn 	= commUtils.addSndData(jrRtn, jrPTrst);
				}

				
				/**
				 * 8. 수행 할 전문생성 및 차량스케줄 정보 갱신
				 */
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));	
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
				
				if(YfConstant.EQUIP_KIND_PT.equals(fromSectGp)
				&& upDownFlag == false){ //하차관련전문
					
					if(YfConstant.YD_CAR_PROG_STAT_B.equals(sYD_CAR_PROG_STAT)){ // 하차개시
						jrParam.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);
						jrParam.setField("YD_CARUD_ST_DT", "SYSDATE");
						commDao.update(jrParam, updateCarSchTime, logId, methodNm, "TB_YD_CARSCH 상태변경시간 수정");
						
						//하차개시 전문
						jrYdMsg.setField("WR_DT" 		, commUtils.getDateTime14()  ); 
						jrYdMsg.setField("YD_STK_COL_GP"	, fromYdStkColGp     );
						jrRst 	= commUtils.addSndData(commDao.getMsgL3("YDTSJ009", jrYdMsg));
						
						//하차개시로 상태변경
						jrParam.setField("YD_CAR_PROG_STAT", YfConstant.YD_CAR_PROG_STAT_D);
						jrParam.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);
						commDao.update(jrParam, updateCarSchStats, logId, methodNm, "TB_YD_CARSCH 상태변경 수정");
					}
				}else if(YfConstant.EQUIP_KIND_PT.equals(toSectGp)
					&& upDownFlag == true){ //상차관련
					
					if(YfConstant.YD_CAR_PROG_STAT_2.equals(sYD_CAR_PROG_STAT)){ // 상차개시
						
						//상차개시 전문
						jrYdMsg.setField("YD_STK_COL_GP"	, toYdStkColGp     );
						jrRst 	= commUtils.addSndData(commDao.getMsgL3("YDTSJ007", jrYdMsg));	
						
						//상차개시로 상태변경
						jrParam.setField("YD_CAR_PROG_STAT", YfConstant.YD_CAR_PROG_STAT_4);
						jrParam.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);
						commDao.update(jrParam, updateCarSchStats, logId, methodNm, "TB_YD_CARSCH 상태변경 수정");
					}
				}
			}
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRtn 	= commUtils.addSndData(jrRtn, jrRst);
			}
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			
			throw new EJBServiceException(e);
		}
		return jrRtn;
	}
	
	/**
	 * 박판열연 차량 사용여부
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updUseCarManage(GridData gdReq) throws DAOException {
		String methodNm = "박판열연 SLAB 차량진입 가능여부수정[ASlabJspSeEJB.updUseCarManage] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));			
			JDTORecord sndRecord = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));			
			JDTORecord jDrd = JDTORecordFactory.getInstance().create();
			
			String pCAR_CARD_NO = gdReq.getParam("CAR_CARD_NO");
			String pYD_STK_COL_GP = gdReq.getParam("YD_STK_COL_GP");
			
			if(!"".equals(pYD_STK_COL_GP)){	
				/*
				 *	1.	(임의번호)차량점유
				 */
				jrParam.setField("CAR_CARD_NO"		,pCAR_CARD_NO); 
				jrParam.setField("YD_STK_COL_GP"	,pYD_STK_COL_GP); 
				commDao.update(jrParam, updateListWlocSLAB_02, logId, methodNm, "TB_YF_STKCOL - CAR_CARD_NO 수정");			
				
				/*
				 *	2.	차량 포인트 상태변경
				 */
				jrParam.setField("CAR_CARD_NO"		,pCAR_CARD_NO); 
				jrParam.setField("YD_STK_COL_GP"	,pYD_STK_COL_GP); 
				commDao.update(jrParam, updCarPointStat, logId, methodNm, "TB_YD_CARPOINT - YD_STK_COL_ACT_STAT 수정");	
				
				JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
				
				/*
				 *	3.	포인트 개폐 송신(보류)
				 */
				jrParam.setField("JMS_TC_CD", "YDTSJ012");
				jrParam.setField("JMS_TC_CREATE_DDTT", 		new String(YfCommUtils.getTcDate("yyyyMMddHHmmss")));
				jrParam.setField("PRSNT_LOC_WLOC_CD", 		inRecord.getFieldString("WLOC_CD"));
				jrParam.setField("YD_PNT_CD", 				inRecord.getFieldString("YD_PNT_CD2"));
				jrParam.setField("PNT_UNIT_CL_GP", 			("Y".equals(pCAR_CARD_NO) ? "O" : "C") );
				jrParam.setField("YD_PNT_OP_CL_TT", 		new String(YfCommUtils.getTcDate("yyyyMMddHHmmss")));
	
				sndRecord = commUtils.addSndData(sndRecord,jDrd);		
				commUtils.printLog(logId, methodNm, "S-", gdReq);
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return sndRecord;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updUseCarManage
	
	/**
	 * [A] 오퍼레이션명 : 박판Slab 보급
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updSlabSupply(GridData gdReq) throws DAOException {
		String methodNm = "박판Slab 보급[ASlabJspSeEJB.updSlabSupply] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrRtnItem = null;
		try{
			commUtils.printLog(logId, methodNm, "S+");			

			String pStlList = StringHelper.evl(commUtils.trim(gdReq.getParam("STL_LIST")), "");	//권상목록
			String pSupplyType = StringHelper.evl(commUtils.trim(gdReq.getParam("SUPPLY_TYPE")), "1");	//보급방식
			String fromLoc = "";
			String pBtcGp = YfConstant.CRANE_FUNC_V; //크레인구분
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("MODIFIER")));
			JDTORecordSet rsStkLyrQuery;
			JDTORecordSet rsStlNoListQuery;
			JDTORecordSet rsQuery;
			
			/*
			 *	1.	보급대상 Slab 조회
			 */
			jrParam.setField("STL_NO", pStlList);
			rsStlNoListQuery = commDao.select(jrParam, getSlabListInfo, logId, methodNm, "Slab항목들 정보 조회");
			
			String flag = "";
			String sYD_STK_COL_GP = "";
			String sYD_STK_BED_NO = "";
			String sYD_STK_LYR_NO = "";
			String sSTL_NO = "";
			String sBAY_GP = "";
			String toLoc = "";
			
			Map fromMap = new HashMap();
			Map toMap = new HashMap();
			int seq = 0;
			int loop = 0;

			
			for(int i=0;i<rsStlNoListQuery.size();i++){				
				sYD_STK_COL_GP = rsStlNoListQuery.getRecord(i).getFieldString("YD_STK_COL_GP");
				sYD_STK_BED_NO = rsStlNoListQuery.getRecord(i).getFieldString("YD_STK_BED_NO");
				sYD_STK_LYR_NO = rsStlNoListQuery.getRecord(i).getFieldString("YD_STK_LYR_NO");
				sBAY_GP = sYD_STK_COL_GP.substring(1,2);
				
				if(fromMap.containsKey(sBAY_GP)){
					List locList = (List)fromMap.get(sBAY_GP);
					locList.add(sYD_STK_COL_GP + sYD_STK_BED_NO + sYD_STK_LYR_NO);
					fromMap.put(sBAY_GP, locList);
				}else{
					List locList = new ArrayList();
					locList.add(sYD_STK_COL_GP + sYD_STK_BED_NO + sYD_STK_LYR_NO);
					fromMap.put(sBAY_GP, locList);
				}
			}
			
			/*
			 *	2.	보급대상 위치 조회
			 */
			rsStkLyrQuery = commDao.select(jrParam, getSupplyLyrInfo, logId, methodNm, "공급가능 한 Lyr정보 조회");
			
			for(int i=0;i<rsStkLyrQuery.size();i++){				
				sYD_STK_COL_GP = rsStkLyrQuery.getRecord(i).getFieldString("YD_STK_COL_GP");
				sYD_STK_BED_NO = rsStkLyrQuery.getRecord(i).getFieldString("YD_STK_BED_NO");
				sYD_STK_LYR_NO = rsStkLyrQuery.getRecord(i).getFieldString("YD_STK_LYR_NO");
				sBAY_GP = sYD_STK_COL_GP.substring(1,2);
				
				if(toMap.containsKey(sBAY_GP)){
					List toList = (List)toMap.get(sBAY_GP);
					toList.add(sYD_STK_COL_GP + sYD_STK_BED_NO + sYD_STK_LYR_NO);
					toMap.put(sBAY_GP, toList);
				}else{
					List toList = new ArrayList();
					toList.add(sYD_STK_COL_GP + sYD_STK_BED_NO + sYD_STK_LYR_NO);
					toMap.put(sBAY_GP, toList);
				}
			}
			
			/*
			 *	3.	보급대상개수 부족 시 에러
			 */
			if(fromMap.containsKey("A")){
				if(((List)fromMap.get("A")).size() > ((List)toMap.get("A")).size()){
					throw new Exception("A동의 보급가능한 1라인이 부족합니다.");
				}
			}
			if(fromMap.containsKey("B")){
				if(((List)fromMap.get("B")).size() > ((List)toMap.get("B")).size()){
					throw new Exception("B동의 보급가능한 1라인이 부족합니다.");
				}
			}
				
			/*
			 *	4.	보급대상 SLAB 확인
			 */
			for(int i=0;i<rsStlNoListQuery.size();i++){				
				sSTL_NO = rsStlNoListQuery.getRecord(i).getFieldString("STL_NO");
				
				
				/*
				 *	5.	보급대상 위/아래 항목들 조회
				 */
				jrParam.setField("STL_NO", sSTL_NO);
				rsQuery = commDao.select(jrParam, getStlNoLocInfo, logId, methodNm, "STL_NO의 적치단의 위,아래,자신 정보");
				
				if(rsQuery.size() == 0 || rsQuery == null){ //STL_NO이 존재하는 적치단 없음
					throw new Exception(sSTL_NO + "를 가진 적치단이 존재하지 않습니다.");
				}
				
				/*
				 *	6.	보급대상 위항목이 있을 경우 에러 / 항목이없을 경우 이동
				 */
				for (int ii = 0; ii < rsQuery.size(); ii++){//TO 열 체크
					flag = StringHelper.evl(rsQuery.getRecord(ii).getFieldString("LOC_FLAG"),"");
					sSTL_NO = StringHelper.evl(rsQuery.getRecord(ii).getFieldString("STL_NO"),"");
					sYD_STK_COL_GP = StringHelper.evl(rsQuery.getRecord(ii).getFieldString("YD_STK_COL_GP"),"");
					sYD_STK_BED_NO = StringHelper.evl(rsQuery.getRecord(ii).getFieldString("YD_STK_BED_NO"),"");
					sYD_STK_LYR_NO = StringHelper.evl(rsQuery.getRecord(ii).getFieldString("YD_STK_LYR_NO"),"");
					sBAY_GP = sYD_STK_COL_GP.substring(1,2);
					fromLoc = sYD_STK_COL_GP + sYD_STK_BED_NO + sYD_STK_LYR_NO;
					
					if("UP".equals(flag)){
						if(!"".equals(sSTL_NO)){ //권상대상의 상단이 비어있지 않을 경우							
							throw new Exception(sBAY_GP + "동 " + sYD_STK_BED_NO + "열 " + sYD_STK_LYR_NO + "행에 " + sSTL_NO + "가 존재합니다.");
						}
					}else{	
						List fromList = (List)fromMap.get(sBAY_GP);	
						List toList = (List)toMap.get(sBAY_GP);
						
						if("2".equals(pSupplyType)){//역방향
							seq = toList.size() - 1 - loop;
						}else{//정방향
							seq = loop;
						}
						
						toLoc = (String)toList.get(seq);
						
						if(fromList.size()-1 == loop){
							loop = 0;
						}else{
							loop++;							
						}
						
						gdReq.addParam("STL_NO", sSTL_NO);
						gdReq.addParam("FROM_LOC", fromLoc);
						gdReq.addParam("TO_LOC", toLoc);
						gdReq.addParam("BTC_GP", pBtcGp);
						gdReq.addParam("YD_SCH_CD", "");
						jrRtnItem = changeSlabLocationInfo(gdReq);
						jrRtn 	= commUtils.addSndData(jrRtn, jrRtnItem);
					}
				}
			}
			
			return jrRtn;
		} 
		catch(DAOException e) 
		{
			throw e;
		} 
		catch(Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * 박판열연 차량 회송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord runTsRetHt(GridData gdReq) throws DAOException {
		String methodNm = "박판열연 SLAB 차량 회송[ASlabJspSeEJB.runTsRetHt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));			
			
			String []pYD_STK_COL_GP_LIST = gdReq.getParam("YD_STK_COL_GP").split(",");
			String pYD_STK_COL_GP = "";
			String pYD_CAR_SCH_ID = "";
			
			String sTRN_EQP_CD = "";
			String sSTL_NO = "";
			String sYD_STK_LYR_NO = "";
			String sARR_WLOC_CD = "";
			String sSPOS_WLOC_CD = "";
			String sYD_PNT_CD3 = "";
			String sYD_CAR_SCH_ID = "";
			String sDEL_YN = "";
			String sHCR_GP = "";
			String sRetHt_ID = commDao.getSeqId(logId, methodNm, "RetHt");
			
			int totCnt = 0;
			int totWT = 0;
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			JDTORecordSet rsQuery = null;
			
			for(int i=0;i<pYD_STK_COL_GP_LIST.length;i++){
				pYD_STK_COL_GP = pYD_STK_COL_GP_LIST[i];
				pYD_CAR_SCH_ID = commDao.getSeqId(logId, methodNm, "CarSch");
				
				/*
				 * 1.적치열에 정지한 차량정보 조회
				 */
				jrParam.setField("YD_STK_COL_GP",pYD_STK_COL_GP);
				rsQuery = commDao.select(jrParam, getCarInfoByStkColGp, logId, methodNm, "적치열의 적치단에 적치된 저장품 조회");
				
				/*
				 * 2.회송이력 입력
				 */
				for(int i2=0; i2<rsQuery.size();i2++){ 
					sDEL_YN = commUtils.trim(rsQuery.getRecord(i2).getFieldString("DEL_YN"));	
					sTRN_EQP_CD = commUtils.trim(rsQuery.getRecord(i2).getFieldString("TRN_EQP_CD"));
					sSTL_NO = StringHelper.evl(rsQuery.getRecord(i2).getFieldString("STL_NO"),"");
					sYD_STK_LYR_NO = StringHelper.evl(rsQuery.getRecord(i2).getFieldString("YD_STK_LYR_NO"),"");
					sYD_CAR_SCH_ID = commUtils.trim(rsQuery.getRecord(i2).getFieldString("YD_CAR_SCH_ID"));
					sSPOS_WLOC_CD = commUtils.trim(rsQuery.getRecord(i2).getFieldString("SPOS_WLOC_CD"));
					sARR_WLOC_CD = commUtils.trim(rsQuery.getRecord(i2).getFieldString("ARR_WLOC_CD"));
					sYD_PNT_CD3 = StringHelper.evl(rsQuery.getRecord(i2).getFieldString("YD_PNT_CD3"),"");
					
					if("N".equals(sDEL_YN)){
						totCnt++;
						totWT += Integer.parseInt(StringHelper.evl(rsQuery.getRecord(i2).getFieldString("SLAB_WT"),"0"));
						
						jrParam.setField("YD_RETHT_HIST_ID"		, sRetHt_ID);
						jrParam.setField("STL_NO"				, sSTL_NO);
						jrParam.setField("YD_RETHT_EMPNO"		, gdReq.getParam("userid"));
						jrParam.setField("YD_RETHT_REQ_DT"		, commUtils.getDateTime14());
						jrParam.setField("YD_RETHT_RSN_CD"		, commUtils.trim(gdReq.getParam("RTNHT_RSN_CD")));
						jrParam.setField("YD_RETHT_RSN_CNTS"	, commUtils.trim(gdReq.getParam("RTNHT_RSN_MSG")));
						jrParam.setField("YD_RETHT_STAT_CD"		, "1");
						jrParam.setField("SPOS_WLOC_CD"			, sSPOS_WLOC_CD); 
						jrParam.setField("ARR_WLOC_CD"			, sARR_WLOC_CD); 
						jrParam.setField("TRN_EQP_CD"			, sTRN_EQP_CD); 
						jrParam.setField("YD_CAR_SCH_ID"		, pYD_CAR_SCH_ID ); 
						
						commDao.insert(jrParam, insRetHtHist, logId, methodNm, "회송이력 테이블 INSERT");				
					}
				}				
				
				
				/*
				 * 3.차량 스케줄, 차량재료 리셋
				 */
				jrParam.setField("TRN_EQP_CD", sTRN_EQP_CD);
				
				EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
				sndConn.trx("delCarSchInfo", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
				/*
				 * 4.차량 스케줄 생성
				 */
				jrParam.setField("SPOS_WLOC_CD"     , sARR_WLOC_CD); //발지개소코드      
				jrParam.setField("ARR_WLOC_CD"      , sSPOS_WLOC_CD); //착지개소코드      
				jrParam.setField("YD_PNT_CD1"       , sYD_PNT_CD3); //야드포인트코드1      
				jrParam.setField("YD_PNT_CD3"       , ""); //야드포인트코드3      
				jrParam.setField("YD_CARLD_STOP_LOC", pYD_STK_COL_GP);     
				jrParam.setField("YD_CARUD_STOP_LOC", "");  
				jrParam.setField("YD_CARLD_LEV_DT", "SYSDATE");     
				jrParam.setField("YD_CARUD_LEV_DT", ""); 
				jrParam.setField("YD_CARLD_ARR_DT", "SYSDATE");     
				jrParam.setField("YD_CARUD_ARR_DT", ""); 
				jrParam.setField("YD_CAR_PROG_STAT" , YfConstant.YD_CAR_PROG_STAT_5); //야드차량진행상태      
				
				jrParam.setField("OLD_YD_CAR_SCH_ID"	, sYD_CAR_SCH_ID); //기존 차량스케줄ID
				jrParam.setField("YD_CAR_SCH_ID"       	, pYD_CAR_SCH_ID);
				jrParam.setField("DEL_YN"       , "N"); //삭제유무      
				jrParam.setField("YD_EQP_ID"    , "XXPT01"); //야드설비ID      
				jrParam.setField("YD_CAR_USE_GP", "L"); //야드차량사용구분      
				jrParam.setField("TRN_EQP_CD"       , sTRN_EQP_CD); //운송장비코드      
				jrParam.setField("YD_EQP_WRK_STAT"  , "L"); //야드설비작업상태      
				jrParam.setField("YD_EQP_WRK_SH"    , String.valueOf(totCnt)); //작업매수      
				jrParam.setField("YD_EQP_WRK_WT"    , String.valueOf(totWT)); //작업중량      
				jrParam.setField("YD_CARLD_WRK_BOOK_ID"       , ""); 
				jrParam.setField("REGISTER"       , "runTsRetHt");    
				
				commDao.update(jrParam, MergeCarSch, logId, methodNm, "차량 스케줄 등록");
				
				/*
				 * 5.차량이송대상제 입력
				 */
				for(int i2=0; i2<rsQuery.size();i2++){ 
					sDEL_YN = commUtils.trim(rsQuery.getRecord(i2).getFieldString("DEL_YN"));	
					sSTL_NO = StringHelper.evl(rsQuery.getRecord(i2).getFieldString("STL_NO"),"");
					sYD_STK_LYR_NO = StringHelper.evl(rsQuery.getRecord(i2).getFieldString("YD_STK_LYR_NO"),"");
					sYD_PNT_CD3 = StringHelper.evl(rsQuery.getRecord(i2).getFieldString("YD_PNT_CD3"),"");
					sHCR_GP = StringHelper.evl(rsQuery.getRecord(i2).getFieldString("HCR_GP"),"");
					
					if("N".equals(sDEL_YN)){
						jrParam.setField("YD_CAR_SCH_ID"       	, pYD_CAR_SCH_ID);
						jrParam.setField("STL_NO"       		, sSTL_NO); //재료   
						jrParam.setField("DEL_YN"       		, "N"); //삭제여부
						jrParam.setField("YD_STK_BED_NO"       	, "01"); //BED NO
						jrParam.setField("YD_STK_LYR_NO"       	, sYD_STK_LYR_NO); //적치단
						jrParam.setField("HCR_GP"       		, sHCR_GP); 
						
						commDao.update(jrParam, MergeCarftmvmtl, logId, methodNm, "차량이송재료 갱신");
					}
				}
				
				
				/*
				 * 6.구내운송 회송하차 완료실적 전문 편집
				 */
				JDTORecord jrYDTSJ016 = JDTORecordFactory.getInstance().create();
			
				for(int i2=0; i2<rsQuery.size();i2++){ 
					sDEL_YN = commUtils.trim(rsQuery.getRecord(i2).getFieldString("DEL_YN"));	
					sSTL_NO = StringHelper.evl(rsQuery.getRecord(i2).getFieldString("STL_NO"),"");
					sYD_STK_LYR_NO = StringHelper.evl(rsQuery.getRecord(i2).getFieldString("YD_STK_LYR_NO"),"");
					
					if(i2 == 0){
						jrYDTSJ016.setField("JMS_TC_CD", "YDTSJ016"); //야드작업예약ID
						jrYDTSJ016.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시	
						jrYDTSJ016.setField("TRN_EQP_CD", sTRN_EQP_CD ); //운송장비코드
						jrYDTSJ016.setField("ARR_WLOC_CD", sARR_WLOC_CD ); //착지개소코드
						jrYDTSJ016.setField("ARR_YD_PNT_CD", sYD_PNT_CD3); //착지야드포인트코드
						jrYDTSJ016.setField("CARUD_CMPL_DT", commUtils.getDateTime14()); //하차완료일시
						jrYDTSJ016.setField("CARLD_SH", String.valueOf(rsQuery.size()) ); //상차매수
					}
					
					jrYDTSJ016.setField("STL_NO" + (i2+1), sSTL_NO ); //재료번호n
					jrYDTSJ016.setField("RETHT_CARUD_CMPL_GP" + (i2+1), ("N".equals(sDEL_YN) ? "HS" : "HW") ); //회송하차완료구분n	
				}
				jrRtn = commUtils.addSndData(jrRtn,jrYDTSJ016);
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of runTsRetHt
	
	/**
	 * 박판열연 차량 상차완료
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updPhaseComplete(GridData gdReq) throws DAOException {
		String methodNm = "박판열연 SLAB 차량 상차완료[ASlabJspSeEJB.updPhaseComplete] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));		
			JDTORecord ydStlRecord = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));	
			
			String []pYD_STK_COL_GP_LIST = gdReq.getParam("YD_STK_COL_GP").split(",");
			
			for(int i=0;i<pYD_STK_COL_GP_LIST.length;i++){
				if(!"".equals(pYD_STK_COL_GP_LIST[i])){
					
					/*
					 *	1.	차량,차량적치단 정보조회
					 */
					
					jrParam.setField("YD_STK_COL_GP", pYD_STK_COL_GP_LIST[i]);
					JDTORecordSet rsQuery = commDao.select(jrParam, getLoadendLayer, logId, methodNm, "차량 적치단 정보 조회");
					JDTORecordSet rsQuery2;
					
					String sTRN_EQP_CD;
					String sSTL_NO;
					String sRECORD_PROG_STAT;
					String szPT_TB_COMM = "";
					String sYD_CAR_SCH_ID = "";
					String sYD_STK_BED_NO = "";
					String sYD_STK_LYR_NO = "";
					String sHCR_GP = "";
					
					if(rsQuery.size() > 0){
						sTRN_EQP_CD = StringHelper.evl(rsQuery.getRecord(0).getFieldString("TRN_EQP_CD"),"");						
						
						
						/*
						 *	2.	차량정보조회
						 */
						jrParam.setField("TRN_EQP_CD"	,sTRN_EQP_CD); 
						rsQuery = commDao.select(jrParam, getTrnEqpCdSch, logId, methodNm, "차량 조회");
						
						if(rsQuery.size()>0){
							sYD_CAR_SCH_ID = StringHelper.evl(rsQuery.getRecord(0).getFieldString("YD_CAR_SCH_ID"),"");
						}
						
						/*
						 *	3.	적치대상제 조회
						 */
						jrParam.setField("TRN_EQP_CD"	,sTRN_EQP_CD); 
						rsQuery = commDao.select(jrParam, getListFrtostlList_loadEnd, logId, methodNm, "적치대상제 조회");
						
						if(rsQuery.size() == 0){
							throw new Exception( sTRN_EQP_CD + "차량에 Slab가 존재하지 않습니다.");
						}
						
						/*
						 *	4.	차량이송대상제 리셋
						 */
						jrParam.setField("YD_CAR_SCH_ID"	,sYD_CAR_SCH_ID); 
						jrParam.setField("YD_STK_BED_NO"	,"01"); 
						jrParam.setField("YD_STK_LYR_NO"	,"001"); 
						commDao.update(jrParam, deleteCarMtrlEqualUpperReset, logId, methodNm, "CARFTMVMTL 리셋");	
						
						/*
						 *	5.	차량이송대상제 추가
						 */
						for(int i2=0;i2<rsQuery.size();i2++){
							sSTL_NO = StringHelper.evl(rsQuery.getRecord(i2).getFieldString("STL_NO"),"");
							sYD_STK_BED_NO = StringHelper.evl(rsQuery.getRecord(i2).getFieldString("YD_STK_BED_NO"),"");
							sYD_STK_LYR_NO = StringHelper.evl(rsQuery.getRecord(i2).getFieldString("YD_STK_LYR_NO"),"");
							sHCR_GP = StringHelper.evl(rsQuery.getRecord(i2).getFieldString("HCR_GP"),"");
							
							jrParam.setField("YD_CAR_SCH_ID"       	, sYD_CAR_SCH_ID);
							jrParam.setField("STL_NO"       		, sSTL_NO); //재료   
							jrParam.setField("DEL_YN"       		, "N"); //삭제여부
							jrParam.setField("YD_STK_BED_NO"       	, sYD_STK_BED_NO); //BED NO
							jrParam.setField("YD_STK_LYR_NO"       	, "0" + sYD_STK_LYR_NO); //적치단
							jrParam.setField("HCR_GP"       		, sHCR_GP); 
							
							commDao.update(jrParam, MergeCarftmvmtl, logId, methodNm, "차량이송재료 갱신");
							
							//차량 이송대상재 삭제	
							jrParam.setField("STL_NO"	,sSTL_NO); 
							commDao.update(jrParam, updateLoadTimeToPT, logId, methodNm, "TB_PT_STLFRTOMOVE UPDATE");
						
							//STOCK정보 삭제
							jrParam.setField("DEL_YN"	,"Y"); 
							jrParam.setField("STL_NO"	,sSTL_NO); 
							commDao.update(jrParam, updateStockYN, logId, methodNm, "TB_YF_STOCK UPDATE");
							
							//주편공통에서 현재 종료상태 확인		
							jrParam.setField("STL_NO"	,sSTL_NO); 
							rsQuery2 = commDao.select(jrParam, getMSlabComm, logId, methodNm, "차량 적치단 정보 조회"); 
							
							/*
							 *	6.	공통테이블 소재이송일자 갱신
							 */
							for(int i3=0;i3<rsQuery2.size();i3++){
								sRECORD_PROG_STAT = StringHelper.evl(rsQuery2.getRecord(i3).getFieldString("RECORD_PROG_STAT"),"");
								
								if("3".equals(sRECORD_PROG_STAT)){
									jrParam.setField("SLAB_NO"	,sSTL_NO); 
									commDao.update(jrParam, updateMatlFtmvTimeSlab, logId, methodNm, "TB_PT_SLABCOMM UPDATE");
									szPT_TB_COMM = "S";
								}else{
									jrParam.setField("MSLAB_NO"	,sSTL_NO); 
									commDao.update(jrParam, updateMatlFtmvTimeMSlab, logId, methodNm, "TB_PT_MSLABCOMM UPDATE");
									szPT_TB_COMM = "B";
								}
							}
							
							/*
							 *	7.	적치단 상태 갱신
							 */
							if(i == rsQuery.size()-1){//상위 단정보 업데이트	
								jrParam.setField("YD_STK_LYR_ACTIVE_STAT"	,YfConstant.STACK_LAYER_ACTIVE_STAT_C); 
								jrParam.setField("YD_STK_LYR_STAT"	,YfConstant.STACK_LAYER_STAT_V); 
								jrParam.setField("YD_STK_COL_GP"	,StringHelper.evl(rsQuery.getRecord(i2).getFieldString("YD_STK_COL_GP"), "")); 
								jrParam.setField("YD_STK_BED_NO"	,StringHelper.evl(rsQuery.getRecord(i2).getFieldString("YD_STK_BED_NO"), "")); 
								jrParam.setField("YD_STK_LYR_NO"	,StringHelper.evl(rsQuery.getRecord(i2).getFieldString("YD_STK_LYR_NO"), "")); 
								commDao.update(jrParam, updateUpperlayerStat, logId, methodNm, "TB_YF_STKLYR UPDATE");
							}
										
					        ydStlRecord.setField("PT_TB_COMM",szPT_TB_COMM);
					        ydStlRecord.setField("STL_NO", sSTL_NO);							        
					        ydStlRecord.setField("SLAB_WO_RT_CD", StringHelper.evl(rsQuery.getRecord(i2).getFieldString("SLAB_WO_RT_CD"), ""));
					        ydStlRecord.setField("ORD_YEOJAE_GP", StringHelper.evl(rsQuery.getRecord(i2).getFieldString("ORD_YEOJAE_GP"), ""));
					        ydStlRecord.setField("SCARFING_YN", StringHelper.evl(rsQuery.getRecord(i2).getFieldString("SCARFING_YN"), ""));
					        ydStlRecord.setField("SCARFING_DONE_YN", StringHelper.evl(rsQuery.getRecord(i2).getFieldString("SCARFING_DONE_YN"), ""));
					        ydStlRecord.setField("MILL_WO_EXN", StringHelper.evl(rsQuery.getRecord(i2).getFieldString("MILL_WO_EXN"), ""));
					        ydStlRecord.setField("YD_GP", StringHelper.evl(gdReq.getParam("YD_GP"),""));					        
					        ydStlRecord.setField("STL_APPEAR_GP", StringHelper.evl(rsQuery.getRecord(i2).getFieldString("STL_APPEAR_GP"), ""));
					        ydStlRecord.setField("HCR_GP", StringHelper.evl(rsQuery.getRecord(i2).getFieldString("HCR_GP"), ""));
					        
					        YdCommonUtils.uptStockCodeMapping(ydStlRecord);
						}
						
						/*
						 *	8.	차량스케줄 갱신
						 */
						if(!"".equals(sYD_CAR_SCH_ID)){
							//차량스케줄 상차완료일시 갱신
							jrParam.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);
							jrParam.setField("YD_CARLD_CMPL_DT", "SYSDATE");
							commDao.update(jrParam, updateCarSchTime, logId, methodNm, "TB_YD_CARSCH 상태변경시간 수정");
							
							jrParam.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID); 
							jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL3("YDTSJ008", jrParam));	
							
							//차량스케줄상태 상차완료갱신
							jrParam.setField("YD_CAR_PROG_STAT", YfConstant.YD_CAR_PROG_STAT_5);
							jrParam.setField("YD_EQP_WRK_STAT", YfConstant.YD_EQP_WRK_STAT_L);
							jrParam.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);
							commDao.update(jrParam, updateCarSchStats, logId, methodNm, "TB_YD_CARSCH 상태변경 수정");
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
	} // end of updPhaseComplete
	
	/**
	 * 박판열연 차량 하차완료
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updPhaseOffComplete(GridData gdReq) throws DAOException {
		String methodNm = "박판열연 SLAB 차량 하차완료[ASlabJspSeEJB.updPhaseOffComplete] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));			
			
			String []pYD_STK_COL_GP_LIST = gdReq.getParam("YD_STK_COL_GP").split(",");
			String pYD_STK_COL_GP = "";
			String sYD_CAR_SCH_ID = "";
			String sTRN_EQP_CD = "";
			int itemCnt = 0;
			
			JDTORecordSet rsQuery;
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			
			for(int i=0;i<pYD_STK_COL_GP_LIST.length;i++){
				pYD_STK_COL_GP = pYD_STK_COL_GP_LIST[i];
				
				if(!"".equals(pYD_STK_COL_GP)){
						
					/*
					 *	1.	하차차량정보 조회
					 */
					jrParam.setField("YD_GP", pYD_STK_COL_GP.substring(0,1));
					jrParam.setField("BAY_GP", pYD_STK_COL_GP.substring(1,2));
					jrParam.setField("SECT_GP", pYD_STK_COL_GP.substring(2,4));
					jrParam.setField("COL_GP", pYD_STK_COL_GP.substring(4,6));
					rsQuery = commDao.select(jrParam, getAxYML008CarSchLd2, logId, methodNm, "차량정보 조회");
					
					/*
					 *	2.	하차완료처리
					 */
					if(rsQuery.size()>0){//권하 시 차량위치 조회
						sYD_CAR_SCH_ID = StringHelper.evl(rsQuery.getRecord(0).getFieldString("YD_CAR_SCH_ID"),"");
						itemCnt = Integer.parseInt(StringHelper.evl(rsQuery.getRecord(0).getFieldString("ITEM_CNT"),"0"));
						sTRN_EQP_CD = StringHelper.evl(rsQuery.getRecord(0).getFieldString("TRN_EQP_CD"),"");
						
						if(itemCnt>0){
							throw new Exception( sTRN_EQP_CD + "차량에 Slab가 남아있습니다.");
						}
						
						//하차완료시간 입력
						jrParam.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);
						jrParam.setField("YD_CARUD_CMPL_DT", "SYSDATE");
						commDao.update(jrParam, updateCarSchTime, logId, methodNm, "TB_YD_CARSCH 상태변경시간 수정");
						
						jrYdMsg.setField("WR_DT"	        , commUtils.getDateTime14());
						jrYdMsg.setField("YD_CAR_SCH_ID"	, sYD_CAR_SCH_ID);
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDTSJ010", jrYdMsg));	
						
						//하차완료로 상태변경
						jrParam.setField("YD_CAR_PROG_STAT", YfConstant.YD_CAR_PROG_STAT_E);
						jrParam.setField("YD_EQP_WRK_STAT", YfConstant.YD_EQP_WRK_STAT_U);
						jrParam.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);
						commDao.update(jrParam, updateCarSchStats, logId, methodNm, "TB_YD_CARSCH 상태변경 수정");
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
	} // end of updPhaseOffComplete
	
	/**
	 * 박판열연 차량 상차취소
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updPhaseCancel(GridData gdReq) throws DAOException {
		String methodNm = "박판열연 SLAB 차량상차취소[ASlabJspSeEJB.updPhaseCancel] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		/*
		 *	사용안함
		 */
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));	
			
			String []pYD_STK_COL_GP_LIST = gdReq.getParam("YD_STK_COL_GP").split(",");
			
			/*
			 *	1.차량스케줄 상차개시로 변경
			 */
			for(int i=0;i<pYD_STK_COL_GP_LIST.length;i++){
				if(!"".equals(pYD_STK_COL_GP_LIST[i])){
			
					jrParam.setField("YD_STK_COL_GP", pYD_STK_COL_GP_LIST[i]);
					commDao.update(jrParam, updateLoadenddelete, logId, methodNm, "TB_YD_CARSCH UPDATE");
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrParam;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updPhaseCancel
	
	/**
	 * 박판열연 차량 백업
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updCarArrive(GridData gdReq) throws DAOException {
		String methodNm = "박판열연 SLAB 차량도착[ASlabJspSeEJB.updCarArrive] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRtn = null;
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			int totWT = 0;
			int totCnt = 0;
			
			String pTRN_EQP_CD = commUtils.trim(gdReq.getParam("TRN_EQP_CD"));
			String pYD_STK_COL_GP = commUtils.trim(gdReq.getParam("YD_STK_COL_GP"));
			String pYD_CAR_SCH_ID = "";
			String pSTL_NO = "";
			String pBAY_GP = pYD_STK_COL_GP.substring(1,2);
			
			String sYD_CAR_SCH_ID = "";
			String sARR_WLOC_CD = "";
			String sSPOS_WLOC_CD = "";
			String sYD_PNT_CD = "";
			String sHCR_GP = "";
			String sCURR_PROG_CD = "";
			String sSLAB_WT = "";
			
			if(!"".equals(pTRN_EQP_CD)){
				
				jrParam.setField("YD_STK_COL_GP", pYD_STK_COL_GP);
				
				JDTORecordSet rsQuery = commDao.select(jrParam, getCarPoint2, logId, methodNm, "차량포인트 조회");
				JDTORecordSet rsSlabInfoQuery = null;
				JDTORecordSet rsPtSlabQuery = null;
				JDTORecordSet rsSlabQuery = null;
				
				if(rsQuery.size()>0){
					sYD_CAR_SCH_ID = StringHelper.evl(rsQuery.getRecord(0).getFieldString("YD_CAR_SCH_ID"),"");
					sARR_WLOC_CD = StringHelper.evl(rsQuery.getRecord(0).getFieldString("WLOC_CD"),"");
					sYD_PNT_CD = StringHelper.evl(rsQuery.getRecord(0).getFieldString("YD_PNT_CD"),"");
				
					/*
					 * 1.차량 초기화
					 */
					gdReq.addParam("YD_STK_COL_GP", pYD_STK_COL_GP);
					gdReq.addParam("TRN_EQP_CD", pTRN_EQP_CD);
					delCarInfo(gdReq);
					
					/*
					 * 2.차량이송대상제 입력
					 */
					pYD_CAR_SCH_ID = commDao.getSeqId(logId, methodNm, "CarSch");
					
					for (int i = 0; i < rowCnt; i++) {
						pSTL_NO = commUtils.getValue(gdReq, "STL_NO"       , i);	
						
						jrParam.setField("SLAB_NO"       		, pSTL_NO); 
						rsSlabInfoQuery = commDao.select(jrParam, getSlabInfo, logId, methodNm, "PT Slab정보 조회");						
						
						if(rsSlabInfoQuery.size()>0){
							
							if(!"".equals(pSTL_NO)){
								jrParam.setField("SLAB_NO", pSTL_NO);
								rsSlabQuery = commDao.select(jrParam, selectSlabMatirialInfo, logId, methodNm, "STKCOL 조회");
								
								sHCR_GP = StringHelper.evl(rsSlabQuery.getRecord(0).getFieldString("HCR_GP"),"");
								sCURR_PROG_CD = StringHelper.evl(rsSlabQuery.getRecord(0).getFieldString("CURR_PROG_CD"),"");
								sSLAB_WT = StringHelper.evl(rsSlabQuery.getRecord(0).getFieldString("SLAB_WT"),"");
								
								totCnt++;
								totWT += Integer.parseInt(sSLAB_WT);
								
								jrParam.setField("YD_CAR_SCH_ID"       	, pYD_CAR_SCH_ID);
								jrParam.setField("STL_NO"       		, pSTL_NO); //재료   
								jrParam.setField("DEL_YN"       		, "N"); //삭제여부
								jrParam.setField("YD_STK_BED_NO"       	, commUtils.getValue(gdReq, "YD_STK_BED_NO"       , i)); //BED NO
								jrParam.setField("YD_STK_LYR_NO"       	, "0" + commUtils.getValue(gdReq, "YD_STK_LYR_NO"       , i)); //적치단
								jrParam.setField("YD_CAR_UPP_LOC_CD"    , pBAY_GP + "1"); //차상위치
								jrParam.setField("HCR_GP"       		, sHCR_GP); 
								jrParam.setField("STL_PROG_CD"    		, sCURR_PROG_CD); //진도코드
								jrParam.setField("YD_MTL_ITEM"    		, "SM"); //SLAB
								
								commDao.update(jrParam, MergeCarftmvmtl, logId, methodNm, "차량이송재료 갱신");
								
								if(i == 0){
									jrParam.setField("STL_NO"       		, pSTL_NO); 
									rsPtSlabQuery = commDao.select(jrParam, getPtSlabInfo, logId, methodNm, "PT Slab정보 조회");
									
									if(rsPtSlabQuery.size()>0){
										sARR_WLOC_CD = StringHelper.evl(rsPtSlabQuery.getRecord(0).getFieldString("ARR_WLOC_CD"),"");
										sSPOS_WLOC_CD = StringHelper.evl(rsPtSlabQuery.getRecord(0).getFieldString("SPOS_WLOC_CD"),"");
									}
								}
							}
						}
					}
					
					/*
					 * 3.차량스케쥴 생성
					 */
					if(totCnt==0){//공차
						jrParam.setField("SPOS_WLOC_CD"     , sARR_WLOC_CD); //발지개소코드      
						jrParam.setField("ARR_WLOC_CD"      , ""); //착지개소코드      
						jrParam.setField("YD_PNT_CD1"       , sYD_PNT_CD); //야드포인트코드1      
						jrParam.setField("YD_PNT_CD3"       , ""); //야드포인트코드3      
						jrParam.setField("YD_CARLD_STOP_LOC", pYD_STK_COL_GP);     
						jrParam.setField("YD_CARUD_STOP_LOC", "");  
						jrParam.setField("YD_CARLD_LEV_DT", "SYSDATE");     
						jrParam.setField("YD_CARUD_LEV_DT", ""); 
						jrParam.setField("YD_CARLD_ARR_DT", "SYSDATE");     
						jrParam.setField("YD_CARUD_ARR_DT", ""); 
						jrParam.setField("YD_CAR_PROG_STAT" , YfConstant.YD_CAR_PROG_STAT_1); //야드차량진행상태      
					}else{//영차
						jrParam.setField("SPOS_WLOC_CD"     , sSPOS_WLOC_CD); //발지개소코드      
						jrParam.setField("ARR_WLOC_CD"      , sARR_WLOC_CD); //착지개소코드      
						jrParam.setField("YD_PNT_CD1"       , ""); //야드포인트코드1      
						jrParam.setField("YD_PNT_CD3"       , sYD_PNT_CD); //야드포인트코드3      
						jrParam.setField("YD_CARLD_STOP_LOC", "");     
						jrParam.setField("YD_CARUD_STOP_LOC", pYD_STK_COL_GP);  
						jrParam.setField("YD_CARLD_LEV_DT", "");     
						jrParam.setField("YD_CARUD_LEV_DT", "SYSDATE"); 
						jrParam.setField("YD_CARLD_ARR_DT", "");     
						jrParam.setField("YD_CARUD_ARR_DT", "SYSDATE"); 
						jrParam.setField("YD_CAR_PROG_STAT" , YfConstant.YD_CAR_PROG_STAT_A); //야드차량진행상태      
					}
					
					jrParam.setField("OLD_YD_CAR_SCH_ID"	, sYD_CAR_SCH_ID); //기존 차량스케줄ID
					jrParam.setField("YD_CAR_SCH_ID"       	, pYD_CAR_SCH_ID);
					jrParam.setField("DEL_YN"       , "N"); //삭제유무      
					jrParam.setField("YD_EQP_ID"    , "XXPT01"); //야드설비ID      
					jrParam.setField("YD_CAR_USE_GP", "L"); //야드차량사용구분      
					jrParam.setField("TRN_EQP_CD"       , pTRN_EQP_CD); //운송장비코드      
					jrParam.setField("YD_EQP_WRK_STAT"  , "L"); //야드설비작업상태      
					jrParam.setField("YD_EQP_WRK_SH"    , String.valueOf(totCnt)); //작업매수      
					jrParam.setField("YD_EQP_WRK_WT"    , String.valueOf(totWT)); //작업중량      
					jrParam.setField("YD_CARLD_WRK_BOOK_ID"       , "");    
					
					commDao.update(jrParam, MergeCarSch, logId, methodNm, "차량 스케줄 등록");
							
					/*
					 * 4.차량 포인트 수정
					 */
					jrParam.setField("WLOC_CD"       	, sARR_WLOC_CD);
					jrParam.setField("YD_PNT_CD"       , sYD_PNT_CD);   
					jrParam.setField("TRN_EQP_CD"       , pTRN_EQP_CD);    
						
					YfCommCarMvSeEJBSBean carSession = new YfCommCarMvSeEJBSBean();
					carSession.YfCarPointinforeg("4", "", pTRN_EQP_CD, "", sARR_WLOC_CD, sYD_PNT_CD, "R", logId, methodNm); 
					
					/*
					 * 5.차량도착 전문
					 */
					jrParam.setField("TC_CODE", "TSYDJ003");
					jrParam.setField("TRN_EQP_CD", pTRN_EQP_CD);
					jrParam.setField("TRN_WRK_FULLVOID_GP", totCnt > 0 ? "F" : "E");
					jrParam.setField("ARR_WLOC_CD", sARR_WLOC_CD);
					jrParam.setField("ARR_YD_PNT_CD", sYD_PNT_CD);
					jrParam.setField("TRN_EQP_STK_CAPA", "");
					
					EJBConnector ejbConn = new EJBConnector("default", "YfCommCarMvSeEJB", this);
					jrRtn = (JDTORecord)ejbConn.trx("rcvTSYDJ003", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updCarArrive
	
	/**
	 * 박판열연 차량정보삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delCarInfo(GridData gdReq) throws DAOException {
		String methodNm = "박판열연 SLAB 차량정보 DEL_YN -> 'Y' [ASlabJspSeEJB.delCarInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			String pYD_STK_COL_GP = commUtils.trim(gdReq.getParam("YD_STK_COL_GP"));
			String pTRN_EQP_CD = commUtils.trim(gdReq.getParam("TRN_EQP_CD"));
			
			jrParam.setField("TRN_EQP_CD", pTRN_EQP_CD);
			
			/*
			 * 1.차량 스케줄, 차량재료 리셋
			 */
			EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
			sndConn.trx("delCarSchInfo", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			/*
			 * 2.차량 포인트 리셋	
			 */
			jrParam.setField("YD_STK_COL_GP"	,pYD_STK_COL_GP); 
			commDao.update(jrParam, CarPointReset, logId, methodNm, "CARPOINT 공백처리");	
			
			commUtils.printLog(logId, methodNm, "S-");
			return jrParam;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delCarInfo
	
	/**
	 * 박판열연 SLAB 도착
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updSlabArrive(GridData gdReq) throws DAOException {
		String methodNm = "박판열연 SLAB 추가[ASlabJspSeEJB.updSlabArrive] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String pSTL_NO = commUtils.trim(gdReq.getParam("STL_NO"));
			String pYD_STK_COL_GP = commUtils.trim(gdReq.getParam("YD_STK_COL_GP"));
			String pYD_STK_BED_NO = commUtils.trim(gdReq.getParam("YD_STK_BED_NO"));
			String pYD_STK_LYR_NO = commUtils.trim(gdReq.getParam("YD_STK_LYR_NO"));
			String sBtcGp = YfConstant.CRANE_FUNC_S;
			String toLoc = pYD_STK_COL_GP + pYD_STK_BED_NO + pYD_STK_LYR_NO;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));	
			
			jrParam.setField("STL_NO", pSTL_NO);
			JDTORecordSet rsResult = commDao.select(jrParam, getSlabComm, logId, methodNm, "슬라브 공통에서 조회"); 
			
			String sCURR_PROG_CD = "";
			String sWO_MSLAB_RPR_MTD = "";
			String sSTOCK_MOVE_TERM = "";
			
			if(rsResult.size() > 0){
				sCURR_PROG_CD 		= commUtils.trim(rsResult.getRecord(0).getFieldString("CURR_PROG_CD"));		
				sWO_MSLAB_RPR_MTD	= commUtils.trim(rsResult.getRecord(0).getFieldString("WO_MSLAB_RPR_MTD"));	
				
				sSTOCK_MOVE_TERM	= comm.getStockMoveTerm(sCURR_PROG_CD, sWO_MSLAB_RPR_MTD);
			}
			
			/*
			 * 1.저장품 입력
			 */
			jrParam.setField("STL_NO",			pSTL_NO);
			jrParam.setField("STOCK_ITEM",		"SM");		//SLAB소재
			jrParam.setField("STOCK_MOVE_TERM",	sSTOCK_MOVE_TERM);
			commDao.update(jrParam, mergeStockInfo, logId, methodNm, "TB_YF_STOCK MERGE");	
			
			/*
			 * 2.적치단 입력
			 */
			jrParam.setField("STL_NO", pSTL_NO);
			jrParam.setField("YD_STK_LYR_STAT", YfConstant.STACK_LAYER_STAT_C);
			jrParam.setField("YD_STK_COL_GP", pYD_STK_COL_GP);
			jrParam.setField("YD_STK_BED_NO", pYD_STK_BED_NO);
			jrParam.setField("YD_STK_LYR_NO", pYD_STK_LYR_NO);
			commDao.update(jrParam, updateStackLayerStat, logId, methodNm, "TB_YF_STKLYR 수정");
			
			/*
			 * 3.이력 생성
			 */
			gdReq.addParam("STL_NO", pSTL_NO);
			gdReq.addParam("FROM_LOC", "");
			gdReq.addParam("TO_LOC", toLoc);
			gdReq.addParam("BTC_GP", sBtcGp);
			updSlabHistory(gdReq); 
			
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrParam;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updSlabArrive
	
	/**
	 * 박판열연 SLAB 자재 초기화
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updSlabReset(GridData gdReq) throws DAOException {
		String methodNm = "박판열연 SLAB 자재 초기화[ASlabJspSeEJB.updSlabReset] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrRtnItem = null;
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String pStlList = commUtils.trim(gdReq.getParam("STL_LIST"));
			String sSTL_NO = "";
			String refreshCd = "";
			String sYD_STK_COL_GP = "";
			String sYD_STK_BED_NO = "";
			String sYD_STK_LYR_NO = "";
			String fromLoc = "";
			String toLoc ="";
			String sBtcGp = YfConstant.CRANE_FUNC_S;
			
			Set refreshSet = new HashSet();
			Iterator setIt;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));	
			
			jrParam.setField("STL_NO", pStlList);
			JDTORecordSet rsSlabQuery = commDao.select(jrParam, getSlabListInfo, logId, methodNm, "Slab항목들 정보 조회");
			JDTORecordSet rsLyrQuery ;
			
			for(int i=0;i<rsSlabQuery.size();i++){//항목들 초기화
				sSTL_NO = rsSlabQuery.getRecord(i).getFieldString("STL_NO");
				sYD_STK_COL_GP = rsSlabQuery.getRecord(i).getFieldString("YD_STK_COL_GP");
				sYD_STK_BED_NO = rsSlabQuery.getRecord(i).getFieldString("YD_STK_BED_NO");		
				sYD_STK_LYR_NO = rsSlabQuery.getRecord(i).getFieldString("YD_STK_LYR_NO");		
				fromLoc = sYD_STK_COL_GP + sYD_STK_BED_NO + sYD_STK_LYR_NO;
				
				/*
				 * 1.대상 적치단 SLAB 초기화
				 */
				jrParam.setField("STL_NO", "");
				jrParam.setField("YD_STK_LYR_STAT", YfConstant.STACK_LAYER_STAT_E);
				jrParam.setField("YD_STK_COL_GP", sYD_STK_COL_GP);
				jrParam.setField("YD_STK_BED_NO", sYD_STK_BED_NO);
				jrParam.setField("YD_STK_LYR_NO", sYD_STK_LYR_NO);
				commDao.update(jrParam, updateStackLayerStat, logId, methodNm, "TB_YF_STKLYR SLAB초기화");
				
				
				/*
				 * 2.초기화 이력 추가
				 */
				gdReq.addParam("STL_NO", sSTL_NO);
				gdReq.addParam("FROM_LOC", fromLoc);
				gdReq.addParam("TO_LOC", "");
				gdReq.addParam("BTC_GP", sBtcGp);
				updSlabHistory(gdReq); //초기화 이력 등록
				
				refreshSet.add(sYD_STK_COL_GP+sYD_STK_BED_NO);
			}
			
			setIt = refreshSet.iterator();
			
			/*
			 * 3.초기화 된 Slab의 상위 항목들 아래로 내리기
			 */
			while(setIt.hasNext()){
				refreshCd = (String)setIt.next();
				jrParam.setField("YD_STK_COL_GP",refreshCd.substring(0,6));
				jrParam.setField("YD_STK_BED_NO",refreshCd.substring(6)); 
				rsSlabQuery = commDao.select(jrParam, getFloatSlab, logId, methodNm, "단 별로 떠있는 SLAB 조회");
				
				for(int i=0;i<rsSlabQuery.size();i++){//항목들 초기화
					sSTL_NO = rsSlabQuery.getRecord(i).getFieldString("STL_NO");
					sYD_STK_COL_GP = rsSlabQuery.getRecord(i).getFieldString("YD_STK_COL_GP");
					sYD_STK_BED_NO = rsSlabQuery.getRecord(i).getFieldString("YD_STK_BED_NO");	
					sYD_STK_LYR_NO = rsSlabQuery.getRecord(i).getFieldString("YD_STK_LYR_NO");	
					fromLoc = sYD_STK_COL_GP + sYD_STK_BED_NO + sYD_STK_LYR_NO;
					
					jrParam.setField("YD_STK_COL_GP", sYD_STK_COL_GP);
					jrParam.setField("YD_STK_BED_NO", sYD_STK_BED_NO);
					rsLyrQuery = commDao.select(jrParam, getBlankLyr, logId, methodNm, "Slab 옮길 장소 조회");
					
					if(rsLyrQuery.size() > 0){
						sYD_STK_LYR_NO = rsLyrQuery.getRecord(0).getFieldString("YD_STK_LYR_NO");	
						toLoc = sYD_STK_COL_GP + sYD_STK_BED_NO + sYD_STK_LYR_NO;
					}
					
					gdReq.addParam("STL_NO", sSTL_NO);
					gdReq.addParam("FROM_LOC", fromLoc);
					gdReq.addParam("TO_LOC", toLoc);
					gdReq.addParam("BTC_GP", sBtcGp);
					
					jrRtnItem = changeSlabLocationInfo(gdReq);
					jrRtn 	= commUtils.addSndData(jrRtn, jrRtnItem);
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updSlabReset
	
	/**
	 * 박판열연 SLAB 이력정보 추가
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updSlabHistory(GridData gdReq) throws DAOException {
		String methodNm = "박판열연 SLAB 이력정보 추가[ASlabJspSeEJB.updSlabHistory] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			String fromLoc = commUtils.trim(gdReq.getParam("FROM_LOC"));
			String toLoc = commUtils.trim(gdReq.getParam("TO_LOC"));
			String sBtcGp = commUtils.trim(gdReq.getParam("BTC_GP"));
			String sStlNo = commUtils.trim(gdReq.getParam("STL_NO"));
			
			String sYdSchCd = "";
			String sYdStkColGp = ""; 
			String sYdGp = "";
			String sBayGp = "";
			String sCraneNo = "";
			
			String fromColBedGp = "";
			String fromBayGp = "";
			String fromSectGp = "";
			String fromYdStkColGp = "";
			String fromYdStkBedNo = "";
			String fromYdStkLyrNo = "";
			
			String toColBedGp = "";
			String toBayGp = "";
			String toSectGp = "";
			String toYdStkColGp = "";
			String toYdStkBedNo = "";
			String toYdStkLyrNo = "";
			
			/*
			 * 1.From 위치 값 분류
			 */
			if(!"".equals(fromLoc)){
				 fromYdStkColGp = fromLoc.substring(0,6);
				 fromYdStkBedNo = fromLoc.substring(6,8);
				 fromYdStkLyrNo = fromLoc.substring(8);
				 fromSectGp = fromYdStkColGp.substring(2,4);
				 fromBayGp = fromYdStkColGp.substring(1,2);;
				 sYdGp = fromYdStkColGp.substring(0,1);
				 fromColBedGp = fromYdStkColGp + fromYdStkBedNo;
				 sBayGp = fromBayGp;
				 
				 if(YfConstant.EQUIP_KIND_PT.equals(fromSectGp)){
					 sYdStkColGp = fromYdStkColGp;
				 }
			}
			
			/*
			 * 2.To 위치 값 분류
			 */
			if(!"".equals(toLoc)){
				 toYdStkColGp = toLoc.substring(0,6);
				 toYdStkBedNo = toLoc.substring(6,8);
				 toYdStkLyrNo = toLoc.substring(8);
				 toSectGp = toYdStkColGp.substring(2,4);
				 toBayGp = toYdStkColGp.substring(1,2);
				 sYdGp = toYdStkColGp.substring(0,1);
				 toColBedGp = toYdStkColGp + toYdStkBedNo;
				 sBayGp = toBayGp;
				 
				 if(YfConstant.EQUIP_KIND_PT.equals(toSectGp)){
					 sYdStkColGp = toYdStkColGp;
				 }
			}
			
			/*
			 * 3.크레인 번호 세팅
			 */
			if(YfConstant.CRANE_FUNC_V.equals(sBtcGp)){
				sCraneNo = sYdGp + sBayGp + YfConstant.EQUIP_KIND_CR + sBayGp + "1";
			}else{
				sCraneNo = sYdGp + sBayGp + YfConstant.EQUIP_KIND_CR + "00";
			}
			
			/*
			 * 4.스케줄 번호 세팅
			 */
			if("".equals(toLoc)){//Slab 초기화
				if("A".equals(fromBayGp)){
					sYdSchCd = YfConstant.SCH_WORK_KIND_0AYD01DM;
				}else{
					sYdSchCd = YfConstant.SCH_WORK_KIND_0BYD01DM;
				}
			}else if("".equals(fromBayGp)){//Slab 추가
				if("A".equals(toBayGp)){
					sYdSchCd = YfConstant.SCH_WORK_KIND_0AYD01RM;
				}else{
					sYdSchCd = YfConstant.SCH_WORK_KIND_0BYD01RM;
				}
			}else if(YfConstant.EQUIP_KIND_PT.equals(toSectGp)){//이송상차
				if("A".equals(toBayGp)){
					sYdSchCd = YfConstant.SCH_WORK_KIND_0APT01UM;
				}else{
					sYdSchCd = YfConstant.SCH_WORK_KIND_0BPT01UM;
				}
			}else if(YfConstant.EQUIP_KIND_PT.equals(fromSectGp)){//이송하차
				if("A".equals(toBayGp)){
					sYdSchCd = YfConstant.SCH_WORK_KIND_0APT01LM;
				}else{
					sYdSchCd = YfConstant.SCH_WORK_KIND_0BPT01LM;
				}
			}else if(fromBayGp.equals(toBayGp)){//동내이적
				if("A".equals(toBayGp)){
					sYdSchCd = YfConstant.SCH_WORK_KIND_0AYD01MM;
				}else{
					sYdSchCd = YfConstant.SCH_WORK_KIND_0BYD01MM;
				}
			}else{//동간이적
				if("A".equals(toBayGp)){
					sYdSchCd = YfConstant.SCH_WORK_KIND_0AYD01BM;
				}else{
					sYdSchCd = YfConstant.SCH_WORK_KIND_0BYD01BM;
				}
			}
			
			/*
			 * 5.Slab 정보 조회
			 */
			jrParam.setField("STL_NO", sStlNo);
			JDTORecordSet slabInfo = commDao.select(jrParam, getSlabComm, logId, methodNm, "슬라브정보 조회"); 
			
			/*
			 * 6.차량 정보 조회
			 */
			jrParam.setField("YD_STK_COL_GP", sYdStkColGp);
			JDTORecordSet carInfo = commDao.select(jrParam, getCarInfoByStkCol, logId, methodNm, "차량정보 조회"); 
			
			jrParam.setField("STL_NO", sStlNo);
			jrParam.setField("YD_SCH_PRIOR", "1");
			jrParam.setField("YD_TO_LOC_DCSN_MTD", "F");
			jrParam.setField("YD_UP_WRK_MODE2", "M");
			jrParam.setField("YD_DN_WRK_MODE2", "M");
			
			jrParam.setField("YD_EQP_ID", sCraneNo);
			jrParam.setField("YD_GP", sYdGp);
			jrParam.setField("YD_SCH_CD", sYdSchCd);
			
			if(!"".equals(fromLoc)){
				jrParam.setField("YD_UP_CMPL_DT", "SYSDATE");
				jrParam.setField("YD_UP_WR_FUNC", sBtcGp);
				jrParam.setField("YD_UP_WO_LOC", fromColBedGp);
				jrParam.setField("YD_UP_WR_LOC", fromColBedGp);
				jrParam.setField("YD_UP_WO_LYR", "0" + fromYdStkLyrNo);
				jrParam.setField("YD_UP_WR_LYR", "0" + fromYdStkLyrNo);
			}
			
			if(!"".equals(toLoc)){
				jrParam.setField("YD_DN_CMPL_DT", "SYSDATE");
				jrParam.setField("YD_DN_WR_FUNC", sBtcGp);
				jrParam.setField("YD_DN_WO_LOC", toColBedGp);
				jrParam.setField("YD_DN_WR_LOC", toColBedGp);
				jrParam.setField("YD_DN_WO_LYR", "0" + toYdStkLyrNo);
				jrParam.setField("YD_DN_WR_LYR", "0" + toYdStkLyrNo);
			}
			
			if(slabInfo.size()>0){
				jrParam.setField("SCARFING_YN", slabInfo.getRecord(0).getFieldString("SCARFING_YN"));
				jrParam.setField("SCARFING_DONE_YN", slabInfo.getRecord(0).getFieldString("SCARFING_DONE_YN"));
				jrParam.setField("ORD_NO", slabInfo.getRecord(0).getFieldString("ORD_NO"));
				jrParam.setField("ORD_DTL", slabInfo.getRecord(0).getFieldString("ORD_DTL"));
				jrParam.setField("ORD_GP", slabInfo.getRecord(0).getFieldString("ORD_GP"));
				jrParam.setField("STL_PROG_CD", slabInfo.getRecord(0).getFieldString("CURR_PROG_CD"));
				jrParam.setField("CUST_CD", slabInfo.getRecord(0).getFieldString("CUST_CD"));
				jrParam.setField("DEST_CD", slabInfo.getRecord(0).getFieldString("DEST_CD"));
				jrParam.setField("STL_APPEAR_GP", slabInfo.getRecord(0).getFieldString("STL_APPEAR_GP"));
				jrParam.setField("ITEMNAME_CD", slabInfo.getRecord(0).getFieldString("ITEMNAME_CD"));
				jrParam.setField("ORD_YEOJAE_GP", slabInfo.getRecord(0).getFieldString("ORD_YEOJAE_GP"));
				jrParam.setField("SPEC_ABBSYM", slabInfo.getRecord(0).getFieldString("SPEC_ABBSYM"));
				jrParam.setField("DEMANDER_CD", slabInfo.getRecord(0).getFieldString("DEMANDER_CD"));
				jrParam.setField("SLAB_WO_RT_CD", slabInfo.getRecord(0).getFieldString("SLAB_WO_RT_CD"));
				jrParam.setField("ORD_HCR_GP", slabInfo.getRecord(0).getFieldString("ORD_HCR_GP"));
				jrParam.setField("HCR_GP", slabInfo.getRecord(0).getFieldString("HCR_GP"));
				jrParam.setField("YD_MTL_T", slabInfo.getRecord(0).getFieldString("SLAB_T"));
				jrParam.setField("YD_MTL_W", slabInfo.getRecord(0).getFieldString("SLAB_W"));
				jrParam.setField("YD_MTL_L", slabInfo.getRecord(0).getFieldString("SLAB_LEN"));
				jrParam.setField("YD_MTL_WT", slabInfo.getRecord(0).getFieldString("SLAB_WT"));
			}
			
			if(carInfo.size()>0){
				jrParam.setField("TRN_EQP_CD", carInfo.getRecord(0).getFieldString("TRN_EQP_CD"));
				jrParam.setField("SPOS_WLOC_CD", carInfo.getRecord(0).getFieldString("SPOS_WLOC_CD"));
				jrParam.setField("ARR_WLOC_CD", carInfo.getRecord(0).getFieldString("ARR_WLOC_CD"));
				jrParam.setField("CAR_NO", carInfo.getRecord(0).getFieldString("CAR_NO"));
				jrParam.setField("CAR_KIND", carInfo.getRecord(0).getFieldString("CAR_KIND"));
				jrParam.setField("CARD_NO", carInfo.getRecord(0).getFieldString("CARD_NO"));
				jrParam.setField("YD_CAR_SCH_ID", carInfo.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
			}
			
			commDao.update(jrParam, MergeWrkHist, logId, methodNm, "TB_YF_WRKHIST");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrParam;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updSlabHistory
	
	/**
	 * 박판열연 차량 초기화
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updCarReset(GridData gdReq) throws DAOException {
		String methodNm = "박판열연 SLAB 차량 초기화[ASlabJspSeEJB.updCarReset] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String []pYD_STK_COL_GP_LIST = gdReq.getParam("YD_STK_COL_GP").split(",");
			String pYdStkColGp = "";
			String pSTL_LIST = "";
			String sStlNo = "";
			String pYdGp = "";
			String pYdBayGp = "";
			String pSectGp = "";
			String pColGp = "";
			String pTrnEqpCd = "";
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));	
			JDTORecordSet rsQuery = null;
			
			for(int i1=0;i1<pYD_STK_COL_GP_LIST.length;i1++){
				pYdStkColGp = pYD_STK_COL_GP_LIST[i1];
				pYdGp = pYdStkColGp.substring(0,1);
				pYdBayGp = pYdStkColGp.substring(1,2);
				pSectGp = pYdStkColGp.substring(2,4);
				pColGp= pYdStkColGp.substring(4);
				
				pSTL_LIST = "";
								
				jrParam.setField("YD_GP",pYdGp);
				jrParam.setField("BAY_GP",pYdBayGp);
				jrParam.setField("SECT_GP",pSectGp);
				jrParam.setField("COL_GP",pColGp);
				rsQuery = commDao.select(jrParam, getStkCol, logId, methodNm, "적치열 정보 조회");
				
				for(int i2=0; i2<rsQuery.size();i2++){ 
					pTrnEqpCd = StringHelper.evl(rsQuery.getRecord(i2).getFieldString("TRN_EQP_CD"),"");
				}
				
				/*
				 * 1.차량 초기화
				 */
				gdReq.addParam("TRN_EQP_CD", pTrnEqpCd);
				delCarInfo(gdReq);
				
				/*
				 * 2.적치열 초기화
				 */
				jrParam.setField("YD_STK_COL_GP"	,pYdStkColGp); 
				commDao.update(jrParam, CarpoiontReset_Col, logId, methodNm, "TB_YF_STKCOL RESET");
				
				/*
				 * 3.초기화 차량의 적재된 SLAB 조회
				 */
				jrParam.setField("YD_STK_COL_GP",pYdStkColGp);
				rsQuery = commDao.select(jrParam, getLyrInStlList, logId, methodNm, "삭제 할 STL_NO 조회");
				
				for(int i2=0; i2<rsQuery.size();i2++){ 
					sStlNo = StringHelper.evl(rsQuery.getRecord(i2).getFieldString("STL_NO"),"");
					if(!"".equals(sStlNo)){
						pSTL_LIST += sStlNo + ",";
					}
				}
				
				/*
				 * 4.차량적재 SLAB 초기화
				 */
				if(!"".equals(pSTL_LIST)){
					gdReq.addParam("STL_LIST", pSTL_LIST);
					updSlabReset(gdReq);
				}
			}
			

			commUtils.printLog(logId, methodNm, "S-");

			return jrParam;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updCarReset
	
	/**
	 * 박판열연 차량 동변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updCarLocChange(GridData gdReq) throws DAOException {
		String methodNm = "박판열연 SLAB 차량 동변경[ASlabJspSeEJB.updCarLocChange] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			JDTORecord jrRst = null;
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));	
			
			/*
			 * 1.발지 및 착지 정보 조회
			 */
			jrParam.setField("YD_GP", commUtils.trim(gdReq.getParam("YD_GP")));
			jrParam.setField("BAY_GP", "%");
			jrParam.setField("SECT_GP", YfConstant.EQUIP_KIND_PT);
			jrParam.setField("COL_GP", "01");
			JDTORecordSet rsQuery = commDao.select(jrParam, getStkCol, logId, methodNm, "발지 및 착지 정보 조회"); 
			
			if(rsQuery.size()>0){
				String pTrnEqpCd = commUtils.trim(gdReq.getParam("TRN_EQP_CD"));
				String sTRN_EQP_CD = "";
				
				String sToWloocCd = "";
				String sToYdPntCd = "";
				String sToStkColGp = "";
				String sToCarCardNo = "";
				String sFromWloocCd = "";
				String sFromYdPntCd = "";
				String sFromStkColGp = "";
				
				/*
				 * 2.착지동 동변경 가능여부 체크
				 */
				for(int i=0;i<rsQuery.size();i++){
					sTRN_EQP_CD = StringHelper.evl(rsQuery.getRecord(i).getFieldString("TRN_EQP_CD"), "");
					
					if(sTRN_EQP_CD.equals(pTrnEqpCd)){
						sFromWloocCd = StringHelper.evl(rsQuery.getRecord(i).getFieldString("WLOC_CD"), "");
						sFromYdPntCd = StringHelper.evl(rsQuery.getRecord(i).getFieldString("YD_PNT_CD"), "");
						sFromStkColGp = StringHelper.evl(rsQuery.getRecord(i).getFieldString("YD_STK_COL_GP"), "");
					}else{
						sToWloocCd = StringHelper.evl(rsQuery.getRecord(i).getFieldString("WLOC_CD"), "");
						sToYdPntCd = StringHelper.evl(rsQuery.getRecord(i).getFieldString("YD_PNT_CD"), "");
						sToStkColGp = StringHelper.evl(rsQuery.getRecord(i).getFieldString("YD_STK_COL_GP"), "");
						sToCarCardNo = StringHelper.evl(rsQuery.getRecord(i).getFieldString("CAR_CARD_NO"), "");
						
						if(!"".equals(sTRN_EQP_CD)){
							throw new Exception(sTRN_EQP_CD + "차량이 목적동에 존재합니다.");
						}else if("9999".equals(sToCarCardNo)){
							throw new Exception("목적동이 사용안함 되어 변경 할 수 없습니다.");
						}
					}
				}
				
				/*
				 * 3.동변경
				 */
				gdReq.addParam("FROM_LOC", sFromStkColGp);
				gdReq.addParam("TO_LOC", sToStkColGp);
				gdReq.addParam("TRN_EQP_CD", pTrnEqpCd);
				gdReq.addParam("WLOC_CD", sFromWloocCd);
				gdReq.addParam("MOD_WLOC_CD", sToWloocCd);
				gdReq.addParam("YD_PNT_CD", sFromYdPntCd);
				gdReq.addParam("MOD_YD_PNT_CD", sToYdPntCd);
				
				
				EJBConnector ejbConn = new EJBConnector("default", "YfCommCarMvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("procChangeUdLoc", new Class[] { GridData.class }, new Object[] { gdReq });
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRst;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updCarLocChange
	
	/////////////////////////////////
	// 김광철 작업시작
	////////////////////////////////
	
	/**
	 *      [A] 오퍼레이션명 : SLAB 진도코드 변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */
	public JDTORecord updateSlabProgStat(GridData gdReq) throws DAOException {
		
		String methodNm = "SLAB 진도코드 변경[ASlabJspSeEJB.updateSlabProgStat] < " + gdReq.getNavigateValue();
		String logId 	= gdReq.getIPAddress();
		JDTORecord jrRtn = null;
		JDTORecord jrParam = null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			jrRtn = JDTORecordFactory.getInstance().create();
			jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String sStlNo = gdReq.getParam("STL_NO"); 
			jrParam.setField("STL_NO", sStlNo);  
			
			commUtils.printLog("", "SLAB 진도코드 변경 처리 START :: "+sStlNo, "[INFO]+");
			
			EJBConnector ejbConn = new EJBConnector("default", "YfCommSeEJB", this);
			jrRtn = (JDTORecord)ejbConn.trx("updateFtmvCmtl_Slab", new Class[] { JDTORecord.class }, new Object[] { jrParam });

		    commUtils.printLog("", "SLAB 진도코드 변경 END", "[INFO]-");
        	
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 * 야드및설비 열정보수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecord updSlabYdStkPosSet(GridData gdReq) throws DAOException {
		String methodNm = "야드및설비 열정보수정[ASlabJspSeEJB.updSlabYdStkPosSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			
			inRecord.setField("MODIFIER"	            	,commUtils.trim(gdReq.getParam("userid")));
			inRecord.setField("STACK_COL_GP"	        	,commUtils.getValue(gdReq, "STACK_COL_GP", 0)); 
//			2019.10.01 박판열연 슬라브는 L2가 없기때문에 주석처리
//			JDTORecord jDrd = JDTORecordFactory.getInstance().create();
//			
//			jDrd.setField("MSG_ID"				, "YMA8L001");
//			jDrd.setField("DATE"				, commUtils.getDate10());
//			jDrd.setField("TIME"				, commUtils.getTime8());
//			jDrd.setField("MSG_GP"				, "");
//			jDrd.setField("MSG_LEN"				, "0089");
//			jDrd.setField("YD_INFO_SYNC_CD"		, "3");						//1:동,2:SPAN,3:열,4:BED
//			jDrd.setField("YD_GP"				, inRecord.getFieldString("STACK_COL_GP").substring(0, 1));
//			jDrd.setField("COL_GP"				, inRecord.getFieldString("STACK_COL_GP").substring(5, 6));
//			jDrd.setField("STACK_COL_GP"		, inRecord.getFieldString("STACK_COL_GP"));
//
//			sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA8L001", jDrd));
			
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
			
//			JDTORecord jDrd = JDTORecordFactory.getInstance().create();
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				inRecord.setField("MODIFIER"	            	,commUtils.trim(gdReq.getParam("userid")));
				inRecord.setField("YD_STK_LYR_X_AXIS"	        ,commUtils.getValue(gdReq, "YD_STK_LYR_X_AXIS", ii));
				inRecord.setField("YD_STK_LYR_Y_AXIS"	        ,commUtils.getValue(gdReq, "YD_STK_LYR_Y_AXIS", ii));
				inRecord.setField("YD_STK_LYR_Z_AXIS"			,commUtils.getValue(gdReq, "YD_STK_LYR_Z_AXIS", ii));
				inRecord.setField("YD_STK_COL_GP"	        	,commUtils.getValue(gdReq, "YD_STK_COL_GP", ii));
				inRecord.setField("YD_STK_BED_NO"	        	,commUtils.getValue(gdReq, "YD_STK_BED_NO", ii));
				
				inRecord.setField("YD_STK_BED_XAXIS_TOL"	    ,commUtils.getValue(gdReq, "YD_STK_BED_XAXIS_TOL", ii));
				inRecord.setField("YD_STK_BED_YAXIS_TOL"	    ,commUtils.getValue(gdReq, "YD_STK_BED_YAXIS_TOL", ii));
				inRecord.setField("YD_STK_BED_ZAXIS_TOL"	    ,commUtils.getValue(gdReq, "YD_STK_BED_ZAXIS_TOL", ii));
				
				//적치 단 정보의  UPDATE 
				commDao.update(inRecord, updYdStklyrDan, logId, methodNm, "TB_YF_STKLYR UPDATE");
				
				//적치 베드 정보의  UPDATE 
				commDao.update(inRecord, updYdStklyrTol, logId, methodNm, "TB_YF_STKBED UPDATE");
//				2019.10.01 박판열연 슬라브는 l2가 없기때문에 주석처리
//				jDrd.setField("MSG_ID"				, "YMA8L001");
//				jDrd.setField("DATE"				, commUtils.getDate10());
//				jDrd.setField("TIME"				, commUtils.getTime8());
//				jDrd.setField("MSG_GP"				, "");
//				jDrd.setField("MSG_LEN"				, "0089");
//				jDrd.setField("YD_INFO_SYNC_CD"		, "4");						//1:동,2:SPAN,3:열,4:BED
//				jDrd.setField("YD_GP"				, "2");
//				jDrd.setField("STACK_BED_GP"		, commUtils.getValue(gdReq, "STACK_BED_GP", ii));
//				jDrd.setField("STACK_COL_GP"		, commUtils.getValue(gdReq, "STACK_COL_GP", ii));
//				
//				sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA8L001", jDrd));
				sndRecord = null;
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
	 * 적치기준 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updStockRule(GridData gdReq) throws DAOException {
		String methodNm = "적치기준 변경[ASlabJspFaEJB.updStockRule] < " + gdReq.getNavigateValue();
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
				
				jrParam.setField("DTL_ITEM1"	,commUtils.getValue(gdReq, "DTL_ITEM1", ii)); 
				jrParam.setField("DTL_ITEM2"	,commUtils.getValue(gdReq, "DTL_ITEM2", ii)); 	
				jrParam.setField("DTL_ITEM3"	,commUtils.getValue(gdReq, "DTL_ITEM3", ii)); 	
				jrParam.setField("DEL_YN"	,commUtils.getValue(gdReq, "DEL_YN", ii)); 				
				jrParam.setField("CD_GP"	,commUtils.getValue(gdReq, "CD_GP", ii)); 
				jrParam.setField("ITEM"	    ,commUtils.getValue(gdReq, "ITEM", ii)); 
				commDao.update(jrParam, updStockRule, logId, methodNm, "적치기준 변경");
								
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updStockRule
	/////////////////////////////////
	// 김광철 작업종료
	////////////////////////////////
}
