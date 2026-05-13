/**
 * @(#)ACoilJspBakSeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      박판열연 COIL 야드 화면 관리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 *  
 */
package com.inisteel.cim.yf.acoilBak.session;

import java.sql.Types;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;
import xlib.cmc.GridHeader;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.pp.common.commonUtil;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yf.acoilBak.YFUserException;
import com.inisteel.cim.yf.acoilBak.YfCommUtils;
import com.inisteel.cim.yf.acoilBak.YfConstant;
import com.inisteel.cim.yf.acoilBak.YfQueryIFOld;
import com.inisteel.cim.yf.acoilBak.YfQueryIFOld2;
import com.inisteel.cim.yf.acoilBak.dao.YfCommDAO;
import com.inisteel.cim.yf.acoilBak.session.YfComm;

/**
 *      [A] 클래스명 : 박판열연 COIL 야드 화면관리 Session EJB 
 *
 * @ejb.bean name="ACoilJspBakSeEJB" jndi-name="ACoilJspBakSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class ACoilJspSeEJBSBean extends BaseSessionBean implements YfQueryIFOld, YfQueryIFOld2
{
	private YfCommUtils	commUtils		= new YfCommUtils();
	private YfCommDAO	commDao			= new YfCommDAO();
	private YfComm      comm			= new YfComm();
	private String		szSessionName	= getClass().getName();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
		
	}
	
	/**
	 * GridData - 단순 조회
	 * : 빌드6
	 * 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData getSelectData(GridData gdReq) throws DAOException{
		String methodNm = "조회[ACoilJspBakSeEJB.getSelectData(GridData)] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
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
		catch(DAOException e) {
			throw e;
		} 
		catch(Exception e){
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
		String methodNm = "조회[ACoilJspBakSeEJB.getSelectData(JDTORecord)] < " + recPara.getResultMsg();
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
	 * <pre>
	 * [A] 오퍼레이션명 : 크레인 스케줄취소처리
	 * 
	 *  크레인스케쥴의 취소 공통 모듈이다.
	 *  
	 *  1. 대표 파라메터 정리
	 *     YD_CRN_SCH_ID : 크레인스케쥴 ID
	 *     YD_WBOOK_ID : 작업예약 ID
	 *     YD_L2_RETURN_FLAG : Y or N
	 *     IS_SCH_MTL : 스케줄 단위 취소 여부 : Y or N
	 *     WRK_CNCL_YN : 작업취소 여부 Y or N
	 * 
	 *  2. 크레인스케쥴 취소 불가능 상태
	 *    권상완료, 권하지시, 권하완료 "2".equals(ydWrkProgStat) || "3".equals(ydWrkProgStat) || "4".equals(ydWrkProgStat)
	 *  
	 *  3. 만약 권하지시가 내려간 상태라면 작업지시 취소 전문을 발송하는데
	 *   YD_L2_RETURN_FLAG, Y여야만 가능하다.
	 *     -  YFF1L004, MSG_GP :: D로 전송
	 *  
	 *  4. 적치단을 원복한다.
	 *    - 권상, 권하
	 * 
	 *  5. 크레인스케쥴, 재료 삭제처리
	 *  
	 *  6. 설비상태 수정 - 크레인이 고장 또는 Off-Line이 아니고 상태가 다르면
	 *       CASE WHEN EQ.YD_EQP_PROG_STAT IN ('B', CS.YD_EQP_STAT) OR EQ.YD_EQP_WRK_MODE != '1' THEN 'N'
             ELSE 'Y' END AS EQP_UPD_YN, --설비상태수정여부
          [조건]
				SELECT
				    *
				FROM
				    TB_YF_CRNSCH
				WHERE 1=1
				AND YD_EQP_ID       = :V_YD_EQP_ID
				AND YD_CRN_SCH_ID  != :V_YD_CRN_SCH_ID
				AND DEL_YN          = 'N'
				ORDER BY DECODE(YD_WRK_PROG_STAT, 'W', 1, 'S', 2, '1', 3, '2', 4) DESC
				
		  ㅁ 크레인스케쥴이 존재하면 DECODE(YD_WRK_PROG_STAT, 'W', 1, 'S', 2, '1', 3, '2', 4) DESC 의 첫번째
		      
		  ㅁ 크레인스케쥴이 존재하지 않으면 W상태 없데이트 	
				
      </pre>      
	 *  
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord trtCrnSchCncl(JDTORecord rcvMsg) throws DAOException 
	{
		String methodNm = "크레인 스케줄취소[ACoilJspBakSeEJB.trtCrnSchCncl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try
		{ 
			commUtils.printLog(logId, methodNm, "S+");

			String sYD_CRN_SCH_ID     = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")),""); //야드크레인스케쥴ID
			String sYD_WBOOK_ID       = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"  )),""); //야드작업예약ID
			String sYD_L2_RETURN_FLAG = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("YD_L2_RETURN_FLAG")),""); //
			String sIS_SCH_MTL        = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("IS_SCH_MTL"   )),""); 
			String sWRK_CNCL_YN       = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("WRK_CNCL_YN"  )),"N"); //작업취소여부

			if ("".equals(sYD_CRN_SCH_ID)) 
			{
				throw new YFUserException("크레인스케쥴ID가 없습니다.");
			} 
			else if ("".equals(sYD_WBOOK_ID)) 
			{
				throw new YFUserException("작업예약ID가 없습니다.");
			}
 
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));
			
			jrParam.setField("YD_CRN_SCH_ID",	sYD_CRN_SCH_ID);
			jrParam.setField("YD_WBOOK_ID",		sYD_WBOOK_ID );
			
			/**********************************************************
			* 1. 크레인스케쥴 정보 Check
			**********************************************************/
			JDTORecordSet jsCrnSch = commDao.select(jrParam, getCrnWrkMgtSCSch, logId, methodNm, "크레인작업지시read");
			
			if (jsCrnSch == null || jsCrnSch.size() <= 0) 
			{
				throw new YFUserException("크레인스케쥴ID[" + sYD_CRN_SCH_ID + "]의 크레인스케줄 정보가 존재하지 않습니다.");
		    }
			
			JDTORecord jrCrnSch = jsCrnSch.getRecord(0);
			
		    String ydWrkProgStat = commUtils.trim(jrCrnSch.getFieldString("YD_WRK_PROG_STAT"));	//야드작업진행상태
		    String eqpUpdYn      = commUtils.trim(jrCrnSch.getFieldString("EQP_UPD_YN"));		//설비상태수정여부
		    String ydEqpId       = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));		//야드설비ID
		    String ydEqpStat     = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_STAT"));		//야드설비상태
		    
		    commUtils.printLog(logId, "삭제대상크레인스케줄 YD_CRN_SCH_ID ["+sYD_CRN_SCH_ID+"]", "[INFO]");
		    commUtils.printLog(logId, "야드작업진행상태 YD_WRK_PROG_STAT ["+ydWrkProgStat+"]", "[INFO]");
		    
			if("2".equals(ydWrkProgStat))
			{
				throw new YFUserException("크레인스케줄 [" + sYD_CRN_SCH_ID + "]의 작업진행상태가 [2:권상완료]이므로 취소하실 수 없습니다.");
			}
			else if ("3".equals(ydWrkProgStat))
			{
				throw new YFUserException("크레인스케줄 [" + sYD_CRN_SCH_ID + "]의 작업진행상태가 [3:권하지시]이므로 취소하실 수 없습니다.");
			}
			else if ("4".equals(ydWrkProgStat))
			{
				throw new YFUserException("크레인스케줄 [" + sYD_CRN_SCH_ID + "]의 작업진행상태가 [4:권하완료]이므로 취소하실 수 없습니다.");
			}
			
			/**********************************************************
			* 2. 작업진행상태가 [1:권상지시] 이면 작업지시취소 전문 전송
			**********************************************************/
			if ("1".equals(ydWrkProgStat) && !"Y".equals(sYD_L2_RETURN_FLAG))
			{
				if ("Y".equals(sWRK_CNCL_YN))
				{
					// 작업대기상태 update : 작업취소이므로 X
					jrParam.setField("YD_L2_REQUEST_STAT",	YfConstant.YD_L2_REQUEST_STAT_X);
					jrParam.setField("YD_CRN_SCH_ID",		sYD_CRN_SCH_ID);
					
					commDao.update(jrParam, updYdCrnSchProgStat, logId, methodNm, "작업대기상태 스케줄 취소(X) UPDATE");
				}
				
				jrParam.setField("YD_CRN_SCH_ID",	sYD_CRN_SCH_ID);	//야드크레인스케쥴ID
				jrParam.setField("MSG_GP",			"D");				//전문구분(취소)

				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L004", jrParam));
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
				
				commDao.update(jrParam, updYdStkLyrYdStkColBedGp, logId, methodNm, "TB_YF_STKLYR 적치단상태, 재료번호 변경");
			}
			
			/**********************************************************
			* 4. 크레인스케줄 삭제
			**********************************************************/
			//크레인작업재료 삭제
			sQueryId = updCrnWrkMgtSCCrnMtl;
			
			if ("Y".equals(sIS_SCH_MTL))
			{
				sQueryId = updCrnWrkMgtSCCrnMtlUnitMtl;
			}
			
			commDao.update(jrParam, sQueryId, logId, methodNm, "TB_YF_CRNWRKMTL 크레인스케줄취소");				
			
			//크레인스케줄 삭제
			sQueryId = updCrnWrkMgtSCCrnSch;
			
			if ("Y".equals(sIS_SCH_MTL))
			{
				sQueryId = updCrnWrkMgtSCCrnSchUnitMtl;
			}
			
			commDao.update(jrParam, sQueryId, logId, methodNm, "TB_YF_CRNSCH 크레인스케줄취소");				

			/**********************************************************
			* 5. 설비상태 수정 - 크레인이 고장 또는 Off-Line이 아니고 상태가 다르면
			**********************************************************/
			if ("Y".equals(eqpUpdYn)) 
			{
				JDTORecordSet jsEqpStat = commDao.select(jrParam, getWrkListByEqpId, logId, methodNm, "크레인설비상태 조회");
				
				if (jsEqpStat.size() == 0) 
				{
					jrParam.setField("YD_EQP_PROG_STAT", "W"); //야드설비상태
				}
				else 
				{
					jrParam.setField("YD_EQP_PROG_STAT", jsEqpStat.getRecord(0).getFieldString("YD_WRK_PROG_STAT")); //야드설비상태
				}
				
				jrParam.setField("YD_EQP_ID"  , ydEqpId  ); //야드설비ID
				
				//설비 상태 수정
				commDao.update(jrParam, updStatEqp, logId, methodNm, "설비상태 수정");				
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}catch(DAOException e){
			throw e;
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
			
		}catch(Exception e){
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * [A] 오퍼레이션명 : 작업예약 취소처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord trtWrkBookCncl(JDTORecord rcvMsg) throws DAOException 
	{
		String methodNm = "작업예약 취소처리[ACoilJspBakSeEJB.trtWrkBookCncl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try
		{
			commUtils.printLog(logId, methodNm, "S+");

			String ydWbookId	= commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"));		//야드작업예약ID
			String modifier		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));		//수정자
			
			if ("".equals(ydWbookId)) 
			{
				throw new YFUserException("작업예약ID가 없습니다.");
			}

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);

			jrParam.setField("YD_WBOOK_ID", ydWbookId);
			
			/**********************************************************
			* 1. 크레인스케줄 존재여부 Check 
			* 
			**********************************************************/
			JDTORecordSet jsCrnSch = commDao.select(jrParam, getCommWbCrnSch, logId, methodNm, "크레인작업지시read");
			
			if (jsCrnSch != null && jsCrnSch.size() > 0) 
			{				
				throw new YFUserException("작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 " + jsCrnSch.size() + " 건 존재합니다.");
		    }
			
			/**********************************************************
			* 3. 차량/대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			**********************************************************/
			//차량스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제 - USRYDA.TB_YD_CARSCH
			commDao.update(jrParam, updCommCarSchWbDel, logId, methodNm, "TB_YD_CARSCH 차량스케줄 삭제");				
		
			//대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제 - USRYFA.TB_YF_TCARSCH
			commDao.update(jrParam, updCommTcarSchWbDel, logId, methodNm, "TB_YF_TCARSCH 대차스케줄 삭제");				

		    /**********************************************************
			* 4. 작업예약/재료 삭제
			**********************************************************/
			//작업예약재료 삭제 - TB_YF_WRKBOOKMTL
			commDao.update(jrParam, updDelYnWrkBookMtl, logId, methodNm, "작업예약/재료 삭제");				

			//작업예약 삭제 - TB_YF_WRKBOOK
			commDao.update(jrParam, updDelYnWrkBook, logId, methodNm, "작업예약 삭제");				
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} 
		catch(DAOException e)
		{
			throw e;
		}
		catch(YFUserException e)
		{
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		}
		catch(Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	
	/**
	 * 저장영역별검색순서조회 - 저장
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updStrAreaSrchSeq(GridData gdReq) throws DAOException {
		String methodNm = "저장영역별검색순서조회 저장[ACoilJspBakSeEJB.updStrAreaSrchSeq] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			
			String sEQP_PRIOR  = gdReq.getParam("EQP_PRIOR");
			String sYARD_PRIOR = gdReq.getParam("YARD_PRIOR");
			String sCAR_PRIOR  = gdReq.getParam("CAR_PRIOR");
			
			String sARR_RT     = gdReq.getParam("ARR_RT"); //복사대상 행선
			String[] sARR_RT_List = sARR_RT.split(",");
			String sYD_AIM_BAY_GP = "";
			
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("YD_SCH_CD"  , gdReq.getParam("YD_SCH_CD"  )); //스케줄코드
			jrParam.setField("YD_ROUTE_GP", gdReq.getParam("YD_ROUTE_GP")); //행선
						
			//수정할 레코드 수(전체로 넘어옴)
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			 
			// 행선별 복사
			for (int i = 0; i < sARR_RT_List.length; ++i) {
				commUtils.printLog(logId, "○○○["+i+"]"+ sARR_RT_List[i] , "[info]");
				jrParam.setField("YD_ROUTE_GP", sARR_RT_List[i]); //행선
				
				String sSPAN_CD = "";
	
				int nMaxEQP  = 0;
				int nMaxYARD = 0;
				int nMaxCAR  = 0;
				boolean bSortGp = false;
				 
					
				// 우선순위별 개수 조회
				for (int idx = 0; idx < rowCnt; ++idx) {
					String spanCdStr =  commUtils.getValue(gdReq, "YD_STK_COL_GP", idx);
					commUtils.printLog(logId, "○○○spanCdStr["+idx+"]"+ spanCdStr , "[info]");
					sSPAN_CD = commUtils.getValue(gdReq, "YD_STK_COL_GP", idx).substring(2, 4);
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
				
				//설비SEQ
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
				
				//야드SEQ
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
				
				//차량SEQ
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
			
				//우선순위 수정
				jrParam.setField("YD_SCH_PRFR_PRIOR" , sYARD_PRIOR+sEQP_PRIOR+sCAR_PRIOR);
				commDao.update(jrParam, updSchPrfrPrior, logId, methodNm, "스케줄우선순위 수정");
				
				
				commUtils.printLog(logId, "[EQP ] : "+sEQP_PRIOR +" MAX:"+nMaxEQP +" START:"+nStartEQP , "[info]");
				commUtils.printLog(logId, "[YARD] : "+sYARD_PRIOR+" MAX:"+nMaxYARD+" START:"+nStartYARD, "[info]");
				commUtils.printLog(logId, "[CAR ] : "+sCAR_PRIOR +" MAX:"+nMaxCAR +" START:"+nStartCAR , "[info]");
				
				
				//전체 삭제
				commDao.update(jrParam, delSchLocSrch, logId, methodNm, "저장위치 삭제");
				
				for (int ii = 0; ii < rowCnt; ii++) {
					sSPAN_CD = commUtils.getValue(gdReq, "YD_STK_COL_GP", ii).substring(2, 4);
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

					jrParam.setField("YD_STK_COL_GP"        , commUtils.getValue(gdReq, "YD_STK_COL_GP"        , ii)); //
					
					commDao.update(jrParam, updSchLocSrch, logId, methodNm, "야드위치검색범위순서 저장");
				}
			
			} // end for
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updStrAreaSrchSeq
	
	/**
	 * ZONE별검색순서조회 - 저장 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updStrZoneList(GridData gdReq) throws DAOException {
		String methodNm = "ZONE별검색순서조회 - 저장[ACoilJspBakSeEJB.updStrZoneList] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecordSet jsSeq = null;
			//추가 레코드
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			int bayRank = 1;
			String crudFlag = "";
			String chkFlag = "";
			String ydStkColGp = "";
			String compareBayGp = "";
			String sYD_LOC_SRCH_RNG_SEQ = "";
			String ydZoneGp = "";
			
			for (int i = 0; i < rowCnt; i++) {	
				
				ydZoneGp = commUtils.getValue(gdReq, "YD_ZONE_GP"        , i);
				ydStkColGp = commUtils.getValue(gdReq, "YD_STK_COL_GP"        , i);
				crudFlag = commUtils.getValue(gdReq, "CRUD"        , i);
				chkFlag = commUtils.getValue(gdReq, "CHECK"        , i);
				
				if(ydStkColGp.substring(1, 2).equals(compareBayGp)){
					bayRank++;
				}else{
					bayRank = 1;
				}
				
				compareBayGp = ydStkColGp.substring(1, 2);
				
				if("1".equals(chkFlag)){
					if("D".equals(crudFlag)){ //행삭제
						/*
						 * 1.적치열 ZONE 수정
						 */
						jrParam.setField("YD_ZONE_GP"  			, ydZoneGp);
						jrParam.setField("YD_STK_COL_GP"        , ydStkColGp); 
						commDao.update(jrParam, updZoneCd, logId, methodNm, "STKCOL Zone코드 수정");
						
						/*
						 * 2.ZONE 순서정보 삭제
						 */
						jrParam.setField("YD_STK_COL_GP"        , ydStkColGp); //
						commDao.delete(jrParam, delSchLocSrchZone, logId, methodNm, "ZONE 검색범위순서 삭제");
						
						
						
					}else if("C".equals(crudFlag) || "U".equals(crudFlag)){ //행추가
						/*
						 * 1.적치열 ZONE 수정
						 */
						jrParam.setField("YD_ZONE_GP"  			, ydZoneGp);
						jrParam.setField("YD_STK_COL_GP"        , ydStkColGp); 
						commDao.update(jrParam, updZoneCd, logId, methodNm, "STKCOL Zone코드 수정");
						
						/*
						 * 2.ZONE 순위 조회
						 */
						jrParam.setField("YD_ZONE_GP"  			, ydZoneGp);
						jrParam.setField("YD_STK_COL_GP"        , ydStkColGp); 
						jrParam.setField("RANKING" 	, bayRank + ""); 
						jsSeq = commDao.select(jrParam, getZoneSeq, logId, methodNm, "Zone 순위 조회");
						
						if(jsSeq.size()>0){
							/*
							 * 3.ZONE 검색순서 저장
							 */
							sYD_LOC_SRCH_RNG_SEQ = jsSeq.getRecord(0).getFieldString("YD_LOC_SRCH_RNG_SEQ");
							
							jrParam.setField("YD_ZONE_GP"  			, ydZoneGp);
							jrParam.setField("YD_STK_COL_GP"        , ydStkColGp); 
							jrParam.setField("YD_LOC_SRCH_RNG_SEQ" 	, sYD_LOC_SRCH_RNG_SEQ); 
							commDao.update(jrParam, updSchLocSrchZone, logId, methodNm, "ZONE 검색범위순서 저장");	
							
							/*
							 * 4.ZONE 순서 재정의
							 */
							jrParam.setField("YD_ZONE_GP"  			, ydZoneGp);
							jrParam.setField("YD_STK_COL_GP"        , ydStkColGp); 
							jrParam.setField("YD_LOC_SRCH_RNG_SEQ" 	, sYD_LOC_SRCH_RNG_SEQ); 
							commDao.update(jrParam, updSchLocSrchZoneSeq, logId, methodNm, "ZONE 순서재정의 저장");
						}	
					}					
					
					/*
					 * 5.YFF1L021 전문 송신
					 */				
					JDTORecord sndParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
					sndParam.setField("YD_STK_COL_GP", ydStkColGp);					
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L021",sndParam));
				}
			} // end for
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updStrZoneList
	
	/**
	 * ZONE별버전관리순서조회 - 저장 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updStrZoneList1(GridData gdReq) throws DAOException {
		String methodNm = "ZONE별버전관리조회 - 저장[ACoilJspBakSeEJB.updStrZoneList1] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecordSet jsSeq = null;
			
			//추가 레코드
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			int bayRank = 1;
			String crudFlag = "";
			String chkFlag = "";
			String ydStkColGp = "";
			String compareBayGp = "";
			String sYD_LOC_SRCH_RNG_SEQ = "";
			
			for (int i = 0; i < rowCnt; i++) {	
				
				ydStkColGp = commUtils.getValue(gdReq, "YD_STK_COL_GP"        , i);
				crudFlag = commUtils.getValue(gdReq, "CRUD"        , i);
				chkFlag = commUtils.getValue(gdReq, "CHECK"        , i);
				
				if(ydStkColGp.substring(1, 2).equals(compareBayGp)){
					bayRank++;
				}else{
					bayRank = 1;
				}
				
				compareBayGp = ydStkColGp.substring(1, 2);
				
				if("1".equals(chkFlag)){
					if("D".equals(crudFlag)){ //행삭제
						/*
						 * 1.ZONE 적치열 삭제
						 */
						jrParam.setField("VER_ID"  				, gdReq.getParam("VER_ID"));
						jrParam.setField("YD_STK_COL_GP"        , commUtils.getValue(gdReq, "YD_STK_COL_GP"        , i)); //
						commDao.delete(jrParam, delSchLocSrchZone1, logId, methodNm, "ZONE 검색범위순서 삭제");
						
					}else if("C".equals(crudFlag) || "U".equals(crudFlag)){ //행추가
						
						/*
						 * 1.ZONE 순위 조회
						 */
						jrParam.setField("VER_ID"  				, gdReq.getParam("VER_ID"));
						jrParam.setField("YD_ZONE_GP"  			, gdReq.getParam("YD_ZONE_GP"));
						jrParam.setField("YD_STK_COL_GP"        , ydStkColGp); 
						jrParam.setField("RANKING" 	, bayRank + ""); 
						jsSeq = commDao.select(jrParam, getZoneVerSeq, logId, methodNm, "Zone 순위 조회");
						
						if(jsSeq.size()>0){
							
							sYD_LOC_SRCH_RNG_SEQ = jsSeq.getRecord(0).getFieldString("YD_LOC_SRCH_RNG_SEQ");
							
							/*
							 * 2.ZONE 검색순서 저장
							 */
							jrParam.setField("VER_ID"  				, gdReq.getParam("VER_ID"));
							jrParam.setField("YD_ZONE_GP"  			, gdReq.getParam("YD_ZONE_GP"));
							jrParam.setField("YD_STK_COL_GP"        , commUtils.getValue(gdReq, "YD_STK_COL_GP"        , i)); 
							jrParam.setField("YD_LOC_SRCH_RNG_SEQ" 	, sYD_LOC_SRCH_RNG_SEQ); 
							commDao.update(jrParam, updZoneCd1, logId, methodNm, "ZONE 검색범위순서 저장");
							
							/*
							 * 3.ZONE 순서 재정의
							 */
							jrParam.setField("VER_ID"  				, gdReq.getParam("VER_ID"));
							jrParam.setField("YD_ZONE_GP"  			, gdReq.getParam("YD_ZONE_GP"));
							jrParam.setField("YD_STK_COL_GP"        , commUtils.getValue(gdReq, "YD_STK_COL_GP"        , i)); 
							jrParam.setField("YD_LOC_SRCH_RNG_SEQ" 	, sYD_LOC_SRCH_RNG_SEQ); 
							commDao.update(jrParam, updSchLocSrchZoneSeq1, logId, methodNm, "ZONE 순서재정의 저장");	
						}
					}
				}			
			} // end for
			
			jrParam.setField("REPR_CD_CONTENTS"     , gdReq.getParam("REPR_CD_CONTENTS"));
			jrParam.setField("VER_ID"  				, gdReq.getParam("VER_ID"));
			commDao.update(jrParam, updSchLocSrchZoneTitle, logId, methodNm, "ZONE 버전이름 변경");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updStrZoneList
	
	
	
	/**
	 * ZONE별검색순서조회 - 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delZoneList(GridData gdReq) throws DAOException {
		String methodNm = "ZONE별검색순서조회 - 삭제[ACoilJspBakSeEJB.delZoneList] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet rsResult = null;	
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
						
			//수정할 레코드 수(전체로 넘어옴)
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int i = 0; i < rowCnt; i++) {
				/*
				 * 1.적치열 ZONE 수정
				 */
				jrParam.setField("YD_ZONE_GP"  			, "");
				jrParam.setField("YD_STK_COL_GP"        , commUtils.getValue(gdReq, "YD_STK_COL_GP"        , i)); //
				commDao.update(jrParam, updZoneCd, logId, methodNm, "STKCOL Zone코드 수정");
				
				/*
				 * 2.ZONE 순서정보 삭제
				 */
				jrParam.setField("YD_STK_COL_GP"        , commUtils.getValue(gdReq, "YD_STK_COL_GP"        , i)); //
				commDao.delete(jrParam, delSchLocSrchZone, logId, methodNm, "ZONE 검색범위순서 삭제");
			
			} // end for
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delZoneList
	
	
	/**
	 * ZONE별버전삭제 - 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delZoneVerId(GridData gdReq) throws DAOException {
		String methodNm = "ZONE별검색순서조회 - 저장[ACoilJspBakSeEJB.delZoneVerId] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet rsResult = null;	
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("VER_ID"  			, gdReq.getParam("VER_ID"));
			
			/*
			 * 1.YF_RULE DEL_YN N->Y
			 */
			commDao.delete(jrParam, delZoneVerIdRule, logId, methodNm, "버전 타이틀 삭제");			
			/*
			 * 2.YF_ZONE_V 삭제
			 */
			commDao.update(jrParam, delZoneVerId, logId, methodNm, "버전 데이터삭제");
		
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delZoneList
	
	/**
	 * 선택 존 적용 - 삭제후저장 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord applyZoneVer(GridData gdReq) throws DAOException {
		String methodNm = "ZONE 버전관리 - 현재버전에 적용 [ACoilJspBakSeEJB.applyZoneVer] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet rsResult = null;	
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));			
			commDao.update(jrParam, DeleteUseZoneData, logId, methodNm, "버전 ID 데이터삭제");
			    
		    jrParam.setField("YD_GP"  			, gdReq.getParam("YD_GP"));
			jrParam.setField("VER_ID"  			, gdReq.getParam("VER_ID"));
			commDao.update(jrParam, ApplyZoneVer1, logId, methodNm, "현재버전으로 업데이트");
			commDao.update(jrParam, updYdZoneGp, logId, methodNm, "적치열존정보갱신");
			
			JDTORecord sndParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			sndParam.setField("VER_ID"  			, gdReq.getParam("VER_ID"));
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L021BackUp",sndParam));
  
							
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delZoneList
	
	/**
	 * 현재 ZONE 복사  - 복사
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord zoneCopy(GridData gdReq) throws DAOException {
		String methodNm = "현재 ZONE 복사 - 복사[ACoilJspBakSeEJB.zoneCopy] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet rsResult = null;	
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			String from_VER_ID = gdReq.getParam("VER_ID");//selectBox
			String to_VER_ID = "";//GridData
			String contents="";
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int i = 0; i < rowCnt; i++) {
				to_VER_ID = commUtils.getValue(gdReq, "VER_ID"        , i);
				contents =  commUtils.getValue(gdReq, "REPR_CD_CONTENTS"        , i );
			}
			jrParam.setField("VER_ID"  			, to_VER_ID);
			jrParam.setField("REPR_CD_CONTENTS"  			, contents);
			commDao.update(jrParam, DeleteBackUpZoneData, logId, methodNm, "버전 ID 데이터삭제");
			
			jrParam.setField("VER_ID"  			, to_VER_ID);
			jrParam.setField("REPR_CD_CONTENTS"  			, contents);
			commDao.insert(jrParam, updNewReprCdContents, logId, methodNm, "버전추가 타이틀 업데이트");
			
			 if("YF000710".equals(from_VER_ID)){//신규버전				
				jrParam.setField("VER_ID"  			, to_VER_ID);
				jrParam.setField("REPR_CD_CONTENTS"  			, contents);
				commDao.insert(jrParam, insertBackUpZoneData, logId, methodNm, "현재 ZONE 복사");
			}else{//백업버전
				jrParam.setField("VER_ID"  			, to_VER_ID);
				
				jrParam.setField("FR_VER_ID"  			, from_VER_ID);
				jrParam.setField("TO_VER_ID"  			, to_VER_ID);
				commDao.update(jrParam, ApplyBackUpZoneVer, logId, methodNm, "현재버전으로 업데이트");
			}
			
				commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delZoneList
	
	/**
	 * Trun Table 고장처리,복구처리- 업데이트
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord trunTableBR(GridData gdReq) throws DAOException {
		String methodNm = "분기컨베이어 턴테이블 고장,복구 - RULE업데이트 [ACoilJspBakSeEJB.trunTableBR] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		//Return Value
		JDTORecord jrRtn = null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String stlNo = gdReq.getParam("STL_NO");
			String sBizGp  = gdReq.getParam("BIZ_GP");
			String ydGp  = gdReq.getParam("YD_GP");
			String repr  = gdReq.getParam("REPR_CD_GP");
			String item  = gdReq.getParam("ITEM");
			String yn    = gdReq.getParam("YN");
			jrRtn		= JDTORecordFactory.getInstance().create();
			
			// 고장
		    if("B".equals(sBizGp)){
		    	
		    	jrParam.setField("YN"  			, yn);
		    	jrParam.setField("STL_NO"  			, stlNo);
		    	jrParam.setField("YD_GP"  			, ydGp);
		    	jrParam.setField("REPR_CD_GP"  		, repr);
		    	jrParam.setField("ITEM"  			, item);
		    	commDao.update(jrParam, updsteTurnTableY, logId, methodNm, "고장 Y로 업데이트");
		    	
		    	JDTORecord intfaceDto = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
		    	
		    	if(!"".equals(stlNo)){
		    		//분기 컨베이어 Line Off
			    	intfaceDto.setField("JMS_TC_CD"	, "F1YFL041" );
					intfaceDto.setField("STL_NO"		, stlNo );
					
					// LINE OFF시 동까지만 전달해준다. Take In은 무조건 C동, 고장처리도 동일하게 C동
					intfaceDto.setField("LOCATION"		, "C" );

					// BIZ_GP ( I: Take In, T : Take Out, L : Line Off, H : Hot Coil Line Off
					intfaceDto.setField("BIZ_GP"		, "L" );
					intfaceDto.setField("BACK_YN"		, "Y" );   //백업화면 기동 여부

					EJBConnector ejbConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
					jrRtn = (JDTORecord)ejbConn.trx("rcvF1YFL041", new Class[] { JDTORecord.class }, new Object[] { intfaceDto });	
		    	}
					
		    }
		    // 턴테이블 복구	
		    else if("R".equals(sBizGp)){
		    	jrParam.setField("YN"  			, yn);
		    	jrParam.setField("STL_NO"  			, "");
		    	jrParam.setField("YD_GP"  			, ydGp);
		    	jrParam.setField("REPR_CD_GP"  		, repr);
		    	jrParam.setField("ITEM"  			, item);
		        commDao.update(jrParam, updsteTurnTableY, logId, methodNm, "정상 N로 업데이트");
		    }
		
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		
		}
	}
		/**
		 * B동LineOff영순위변경- 업데이트
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param GridData gdReq
		 * @return JDTORecord
		 * @throws DAOException
		*/
		public JDTORecord updLineOffSeq(GridData gdReq) throws DAOException {
			String methodNm = "B동LineOff영순위변경 -  [ACoilJspBakSeEJB.updLineOffSeq] < " + gdReq.getNavigateValue();
			String logId = gdReq.getIPAddress();
			JDTORecord jrRtn = null; 
			JDTORecord jrParam = null;
			try {
				commUtils.printLog(logId, methodNm, "S+", gdReq);
				//DAO Parameter - Log ID, Method, 수정자 Set
				jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
				
				//수정할 레코드 수
				int rowCnt = gdReq.getHeader("CHECK").getRowCount();
				for (int i = 0; i < rowCnt; i++) {
					jrParam.setField("STL_NO"       , commUtils.getValue(gdReq, "STL_NO"       , i)); //스케줄범위코드 
			    	commDao.update(jrParam, updBLineOffLank9, logId, methodNm, "이전 Dc라인오프 초기화 순서를 9로 조정");
			    	commDao.update(jrParam, updBLineOffLank, logId, methodNm, "B동LineOff영순위변경");

				}
		    	commUtils.printLog(logId, methodNm, "S-");
				return jrRtn;
			}catch(DAOException e) {
				throw e;
			} catch(Exception e) {
				throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			}	
		} // end of delZoneList	}
		
		
		/**
		 * 버전 추가- 저장
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param GridData gdReq
		 * @return JDTORecord
		 * @throws DAOException
		*/
		public JDTORecord insAddVerion(GridData gdReq) throws DAOException {
			String methodNm = "버전 추가 - 저장 [ACoilJspBakSeEJB.insAddVerion] < " + gdReq.getNavigateValue();
			String logId = gdReq.getIPAddress();
			JDTORecord jrRtn = null; 
			JDTORecord jrParam = null;
			JDTORecordSet jsSeq = null;
			try {
				commUtils.printLog(logId, methodNm, "S+", gdReq);
				//DAO Parameter - Log ID, Method, 수정자 Set
				jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
				
				String sITEM = null;
				jsSeq = commDao.select(jrParam, getZoneItem, logId, methodNm, "RULE 테이블 ITEM 채번");
				if(jsSeq.size()>0){
					sITEM = jsSeq.getRecord(0).getFieldString("ITEM");
				}
				jrParam.setField("ITEM"  				, sITEM);
				//수정할 레코드 수
				int rowCnt = gdReq.getHeader("CHECK").getRowCount();
				for (int i = 0; i < rowCnt; i++) {
					jrParam.setField("REPR_CD_CONTENTS"       , commUtils.getValue(gdReq, "REPR_CD_CONTENTS"       , i)); 
			    	commDao.insert(jrParam, insAddVersion, logId, methodNm, "버전추가시 RULE테이블 insert");
				}
				
		    	commUtils.printLog(logId, methodNm, "S-");
				return jrRtn;
			}catch(DAOException e) {
				throw e;
			} catch(Exception e) {
				throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			}	
		} // end of delZoneList	}
		
		/**
		 * 버전 타이틀- 저장
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param GridData gdReq
		 * @return JDTORecord
		 * @throws DAOException
		*/
		public JDTORecord updNewTitle(GridData gdReq) throws DAOException {
			String methodNm = "버전 추가 - 저장 [ACoilJspBakSeEJB.updNewTitle] < " + gdReq.getNavigateValue();
			String logId = gdReq.getIPAddress();
			JDTORecord jrRtn = null; 
			JDTORecord jrParam = null;
			JDTORecordSet jsSeq = null;
			try {
				commUtils.printLog(logId, methodNm, "S+", gdReq);
			
				jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
				
				int rowCnt = gdReq.getHeader("CHECK").getRowCount();
				for (int i = 0; i < rowCnt; i++) {
				jrParam.setField("REPR_CD_CONTENTS"       , commUtils.getValue(gdReq, "REPR_CD_CONTENTS"       , i)); 
				jrParam.setField("VER_ID"       , commUtils.getValue(gdReq, "VER_ID"       , i)); 
				commDao.insert(jrParam, updNewReprCdContents, logId, methodNm, "버전타이틀 RULE테이블 저장");
				}
				commUtils.printLog(logId, methodNm, "S-");
				return jrRtn;
			}catch(DAOException e) {
				throw e;
			} catch(Exception e) {
				throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			}
		}
	
		/**
		 * 그리드의 선택된 행에 대해서 단순 업데이틀 수행
		 * 
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return JDTORecordSet
		 * @throws DAOException
		 */
		public JDTORecord updateGridData(GridData gdReq) throws DAOException{
			String methodNm = "업데이트[AcoilJspSeEJB.updateGridData(GridData)] < " + gdReq.getNavigateValue();
			
			String logId = gdReq.getIPAddress();
			JDTORecord jrRtn = null;
			
			try{
				commUtils.printLog(logId, methodNm, "S+", gdReq);
				
				
				String funcNm =  commUtils.trim(gdReq.getParam("jsp_page_func_nm"));
				String sUsrId = commUtils.trim(gdReq.getParam("userid"));
				
				
				
				JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
				JDTORecord jrParam = null;
				
				int rowCnt = gdReq.getHeader("CHECK").getRowCount();
				if (rowCnt < 0) 
				{
					throw new YFUserException("체크된 행이 없습니다.");
				}
				int headerCnt = gdReq.getHeaderCount();
				if (headerCnt < 0) 
				{
					throw new YFUserException("컬럼이 존재 하지 않습니다");
				}
				
				GridHeader[] header = gdReq.getHeaders();//해더의 요소들
				GridHeader hd = null;
				
				for(int i = 0 ; i<rowCnt ; i++){ //체크수
					jrParam = commUtils.getParam(logId, methodNm, sUsrId);
					jrParam.addRecord(inRecord);
					for(int j=0; j< headerCnt ; j++){//컬럼해더 수
						hd = header[j];
						jrParam.setField( hd.getID()      , commUtils.getValue(gdReq,  hd.getID()     , i));
					}
					commDao.update(jrParam, inRecord.getFieldString("QUERY_ID"), logId, methodNm , inRecord.getFieldString("JSP_PAGE_FUNC_NM"));
				}
				commUtils.printLog(logId, methodNm, "S-", gdReq);
				return jrRtn;
			}catch(DAOException e){
				throw e;
			}catch(YFUserException e){
				throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
			}catch(Exception e){
				throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			}
		}
	
	/**
	 * 난방코일 처리- 업데이트
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord upheatingCoilYN(GridData gdReq) throws DAOException {
		String methodNm = "난방코일처리 - STOCK업데이트 [ACoilJspBakSeEJB.upheatingCoilYN] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			for (int i = 0; i < rowCnt; i++) {
				jrParam.setField("STL_NO", commUtils.getValue(gdReq, "STL_NO", i)); 
				jrParam.setField("HEATING_COIL_YN", commUtils.getValue(gdReq, "HEATING_COIL_YN", i)); 
		    	commDao.update(jrParam, upheatingCoilYN, logId, methodNm, "난방코일 STOCK.HEATING_COIL_Y로 업데이트");
			}
     		commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delZoneList
	
	/**
	 * 크레인스케줄 기준 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updSchRuleMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인스케줄 기준 변경[ACoilJspBakSeEJB.updSchRuleMgt] < " + gdReq.getNavigateValue();
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
				
				commDao.update(jrParam, updSchRuleInfo, logId, methodNm, "크레인스케줄 기준 수정");
								
				/** 
				 * 해당 작업크레인으로 지정되어 있는 작업예약 크레인변경
				 */
				sYD_WRK_CRN     = commUtils.getValue(gdReq, "YD_WRK_CRN"    , ii);
				sYD_WRK_CRN_OLD = commUtils.getValue(gdReq, "YD_WRK_CRN_OLD", ii);
				
				//작업 크레인이 변경되었을 때
				if (!sYD_WRK_CRN.equals(sYD_WRK_CRN_OLD)) {
					jrParam.setField("YD_WRK_PLAN_CRN", sYD_WRK_CRN); //작업크레인
					commDao.update(jrParam, updWrkCrnByChgSchCrn, logId, methodNm, "지정크레인변경"); 		 	
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
	 * 크레인스케줄 기준 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updSchColor(GridData gdReq) throws DAOException {
		String methodNm = "스케줄 색상변경[ACoilJspBakSeEJB.updSchColor] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
				
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("YD_GP"       	, commUtils.trim(gdReq.getParam("YD_GP")));
			jrParam.setField("YD_SCH_CD"   	, commUtils.trim(gdReq.getParam("YD_SCH_CD"))); //스케줄범위코드      
			jrParam.setField("DTL_ITEM1"  	, commUtils.trim(gdReq.getParam("DTL_ITEM1"))); //스케줄범위코드      
			
			commDao.update(jrParam, updSchColor, logId, methodNm, "스케줄 색상 지정");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updSchColor
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업요구현황조회 크레인변경
	 *
	 *
	 *
	 *
	 *
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updCraneChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업요구현황조회 크레인변경[ACoilJspBakSeEJB.updCraneChange] < " + gdReq.getNavigateValue();
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
			String sOLD_YD_EQP_WRK_MODE  = ""; //이전 크레인의 on off-line 상태
			String sOLD_YD_EQP_PROG_STAT = ""; //이전 크레인 설비상태
			
			String sOLD_YD_EQP_AUTO_CRN_MODE = ""; //이전 크레인 자동화 상태
			
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
				JDTORecordSet jsCrn = commDao.select(jrParam, getCraneChange1, logId, methodNm, "크레인변경 조회");

				// 2020.03.04
				// 대체크레인이 없을 경우 메시치저리이므로 
				// 메시지를 변경처리한다.
			    if (jsCrn == null || jsCrn.size() <= 0) {
//			    	throw new YFUserException("크레인스케쥴ID[" + ydCrnSchId + "]의 크레인스케줄 정보가 존재하지 않습니다.");
			    	throw new YFUserException(
			    			gdReq.getHeader("YD_EQP_NM").getValue(ii) 
			    			+"["+gdReq.getHeader("YD_EQP_ID").getValue(ii) + "]"
			    			+"크레인의 대체가능 크레인이 없습니다."
			    			+"[크레인스케쥴 기준조회]에서 해당 크레인 대체가능 유무를 확인하세요"
			    	);
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
				sOLD_YD_EQP_WRK_MODE    = commUtils.trim(jrCrn.getFieldString("OLD_YD_EQP_WRK_MODE"    ));
				sOLD_YD_EQP_PROG_STAT   = commUtils.trim(jrCrn.getFieldString("OLD_YD_EQP_PROG_STAT"   ));
				sOLD_YD_EQP_AUTO_CRN_MODE = commUtils.trim(jrCrn.getFieldString("OLD_YD_EQP_AUTO_CRN_MODE"   ));
			
				// 2020. 04. 27
				// 자동화 크레인의 경우 ( 일시정지 + 고장 ), 대기상태 일 경우에만 변경 가능함
				// 확인했음 :: L2에서 일시정지 + 고장 처리 할 경우 ( 바로 대체크레인으로 스케쥴이 넘어감 )
				if (YfConstant.YD_EQP_WRK_MODE2_A.equals(sYD_EQP_WRK_MODE2)){
					if( !( 
							( "4".equals(sOLD_YD_EQP_AUTO_CRN_MODE) && "B".equals(sOLD_YD_EQP_PROG_STAT))
							|| "W".equals(ydWrkProgStat)
						)
					){
						throw new YFUserException("자동화크레인의 경우 [일시정지:Crance Mode(4) - 고장(B)] 또는 [대기(W)] 경우에만 변경 가능합니다.");
					}
				}
								
				if ("2".equals(ydWrkProgStat)) {
					throw new YFUserException("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [2:권상완료]이므로 변경하실 수 없습니다.");
				} else if ("3".equals(ydWrkProgStat)) {
					throw new YFUserException("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [3:권하지시]이므로 변경하실 수 없습니다.");
				} else if ("4".equals(ydWrkProgStat)) {
					throw new YFUserException("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [4:권하완료]이므로 변경하실 수 없습니다.");
				} else if ("".equals(chgYdEqpId)) {
					throw new YFUserException("변경 크레인 [" + chgYdEqpId + "]의 정보가 존재하지 않습니다.");
				} else if ("B".equals(chgYdEqpStat)) {
					throw new YFUserException("변경 크레인 [" + chgYdEqpId + "]의 설비상태가 [B:고장]이므로 변경하실 수 없습니다.");
				} else if (! YfConstant.YD_EQP_WRK_MODE_1_ONLINE.equals(chgYdEqpWrkMode)) {
					throw new YFUserException("변경 크레인 [" + chgYdEqpId + "]의 설비작업Mode가 [Off-Line]이므로 변경하실 수 없습니다.");
				} else if ("1".equals(chgYdEqpStat) || "2".equals(chgYdEqpStat) || "3".equals(chgYdEqpStat)) {
					throw new YFUserException("변경 크레인 [" + chgYdEqpId + "]의 작업지시가 이미 내려진 상태이므로 변경하실 수 없습니다.");
				} else if (ydEqpId.equals(chgYdEqpId)) {
					throw new YFUserException("변경 크레인 [" + chgYdEqpId + "]과 현재 크레인과 같습니다. ");
				}

				commUtils.printLog(logId, "크레인변경 [ " + ydWbookId + " : " + ydEqpId + " >> " + chgYdEqpId + " ]", "SL");

				/**********************************************************
				* 2. 작업예약 및 크레인스케줄 Table에  대체 크레인ID와 우선순위를 Update
				**********************************************************/
				jrParam.setField("MODIFIER"		, modifier);
				jrParam.setField("YD_SCH_PRIOR"	, chgYdSchPrior);
				jrParam.setField("YD_EQP_ID"   	, chgYdEqpId   );
				
				//작업예약 Table 우선순위 Update
				commDao.update(jrParam, updWrkBookPrior, logId, methodNm, "TB_YF_WRKBOOK");				
				
				if ("1".equals(ydWrkProgStat) || "S".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.1  이전 크레인의 작업지시 취소 전문 송신
					**********************************************************/
					jrParam.setField("MSG_GP"       , "D"       ); //전문구분(취소)
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L004", jrParam));
				}
				
				//크레인스케줄 Table 크레인ID, 우선순위 Update
				commDao.update(jrParam, updCrnWrkMgtW, logId, methodNm,  "TB_YF_CRNSCH");				
				
			
				/**********************************************************
				* 3. 현 작업상태가 권상지시[1], 명령선택기동[S] 인 경우
				**********************************************************/
				if ("1".equals(ydWrkProgStat) || "S".equals(ydWrkProgStat)) {
					/**********************************************************
					* 3.1 변경 크레인의 설비 Table 상태정보를 Update
					**********************************************************/
					jrParam.setField("MODIFIER"			, modifier);
					jrParam.setField("YD_EQP_PROG_STAT"   , YfConstant.YD_EQP_PROG_STAT_1); // "1" 야드설비상태 : 권상작업지시
					jrParam.setField("YD_EQP_ID"   		, chgYdEqpId);
					commDao.update(jrParam, updCrnYsEqp, logId, methodNm,  "TB_YF_EQP");				
					
					// 2020.03. 23
					// 대체크레인 상태값을 동기화 해준다.
					//  - 상태변경의 변수 값이 변경되지 않았을 경우 F1YFL007 작업지시 2번 발생
					chgYdEqpStat = YfConstant.YD_EQP_PROG_STAT_1;
					 
					/**********************************************************
					* 3.2 변경 크레인의 크레인작업지시요구 처리
					**********************************************************/
					//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
					JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
					jrYdMsg.setField("JMS_TC_CD"       , "F1YFL007");	//크레인작업지시요구
					jrYdMsg.setField("YD_EQP_ID"       , chgYdEqpId);	//야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT", YfConstant.YD_WRK_PROG_STAT_1       );	//야드작업진행상태(권상작업지시)
					jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   );	//야드스케쥴코드
					jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID

					EJBConnector sndConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
					JDTORecord jrSnd = (JDTORecord)sndConn.trx("rcvF1YFL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

					jrRtn = commUtils.addSndData(jrRtn, jrSnd);

					/**********************************************************
					* 3.3 이전 크레인의 설비 Table 상태정보를 Update
					**********************************************************/
					jrParam.setField("MODIFIER"		, modifier);
					jrParam.setField("YD_EQP_PROG_STAT"	, "W"    ); //야드설비상태 : 권상작업지시
					jrParam.setField("YD_EQP_ID"  	, ydEqpId);
					commDao.update(jrParam, updCrnYsEqp, logId, methodNm,  "TB_YF_EQP");				
					
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

					jrRtn = commUtils.addSndData(jrRtn, comm.getYFF1L005(resMsg));
					
				}
				
				jrParam.setField("YD_EQP_ID", ydEqpId);
				JDTORecordSet rst = commDao.select(jrParam, getCrnSchId, logId, methodNm,  "크레인장비상태조회");
				if (rst.size() > 0) {
					if (!"B".equals(sOLD_YD_EQP_PROG_STAT) 
							&& YfConstant.YD_EQP_WRK_MODE_1_ONLINE.equals(sOLD_YD_EQP_WRK_MODE)) { // 고장이 아니고 on-line일때 명령선택 기동
						
						if ("W".equals(rst.getRecord(0).getFieldString("YD_EQP_WRK_STAT"))) {
							/*********************************************
							 * 이전 크레인의 다음 스케줄 명령 선택 기동 
							 ********************************************/
							JDTORecord jrF1YFL007 = JDTORecordFactory.getInstance().create();
							jrF1YFL007.setField("JMS_TC_CD", "F1YFL007");
							jrF1YFL007.setField("YD_EQP_ID", ydEqpId);
							EJBConnector sndConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
							JDTORecord jrSnd = (JDTORecord)sndConn.trx("rcvF1YFL007", new Class[] { JDTORecord.class }, new Object[] { jrF1YFL007 });
			
							jrRtn = commUtils.addSndData(jrRtn, jrSnd);
						}
					}
				}
				
				//변경된 크레인 상태 w이면 명령선택기동 EQP
				if ("W".equals(chgYdEqpStat)) {
					/*********************************************
					 * 변경 크레인의 다음 스케줄 명령 선택 기동 
					 ********************************************/
					JDTORecord jrF1YFL007a = JDTORecordFactory.getInstance().create();
					jrF1YFL007a.setField("JMS_TC_CD", "F1YFL007");
					jrF1YFL007a.setField("YD_EQP_ID", chgYdEqpId);
					EJBConnector sndConn1 = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
					JDTORecord jrSnd1 = (JDTORecord)sndConn1.trx("rcvF1YFL007", new Class[] { JDTORecord.class }, new Object[] { jrF1YFL007a });

					jrRtn = commUtils.addSndData(jrRtn, jrSnd1);
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
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
		String methodNm = "크레인작업요구현황조회 순위변경[ACoilJspBakSeEJB.updPriorChange] < " + gdReq.getNavigateValue();
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
				commDao.update(jrParam, updCrnWrkMgt, logId, methodNm,  "크레인스케줄 Table 크레인ID, 우선순위 Update");					
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
	 * <pre>
	 *      [A] 오퍼레이션명 : 크레인작업요구현황조회 긴급작업
	 *      * 기존작업지시가 존재 했을 경우 
	 *        - YD_WRK_PROG_STAT IN ('1', 'S') 명령선택, 권상지시
	 *         ㅁ 무인(자동)
	 *           1. 우선순위 변경
	 *           2. 긴급작업재에 대한 APP030 기준값에 의한 일시정지 후 긴깁작업 여부
	 *            : YFF1L004(크레인작업지시)
	 *              - YD_CRN_SCH_RMD_CNT", "S1"  //S1 일시정지 후 긴급작업
	 *              - 신규크레인스케쥴ID
	 *            Return 문으로 빠져나간다.
	 *         
	 *         ㅁ 유인(수동)
	 *           1. 기존 크레인작업운선수위변경
	 *           2. YD_WRK_PROG_STAT = 'W' 대기상태로 변경처리
	 *       
	 *        - 신규크레인ID기준으로 YFF1L004(크레인작업지시) 내림
	 *       
	 *       * 기존작업지시가 없을 경우
	 *        - 우선순위만 변경처리 
	 * </pre>
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updPriorWrkChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 긴급작업[ACoilJspBakSeEJB.updPriorWrkChange] < " + gdReq.getNavigateValue();
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
				
				// 크레인스케쥴 및 크레인작업 상태를 확인하자
				JDTORecordSet jsCrnSch = commDao.select(jrParam, getCrnsch, logId, methodNm, "크레인작업지시read");
				if (jsCrnSch.size() < 1) {
					throw new YFUserException("작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 존재하지 않습니다.");
				}
				else{
					if( !YfConstant.YD_WRK_PROG_STAT_W.equals( jsCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT")) ){
						throw new YFUserException("크레인작업 진행상태가 대기[W]일 경우에만 변경가능합니다.");
					}
				}
				
                // 기존작업지시
				JDTORecordSet jsCrn = commDao.select(jrParam, getCrnWrkMgtPriorWrk1, logId, methodNm, "기존크레인 조회");
				if (jsCrn.size() == 0) {
					commDao.update(jrParam, updCrnWrkMgt0, logId, methodNm,  "긴급작업 우선순위 변경");
					
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
			    	JDTORecordSet rsResult = commDao.select(jrParam, ChkCrnMode2,logId, methodNm,  "무인크레인여부");
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
							commDao.update(jrParam, updCrnWrkMgt, logId, methodNm,  "TB_YF_CRNSCH");

							/**********************************************************
							* 크레인작업지시요구 전문 조회 - 일시정지 긴급작업
							**********************************************************/
							String sAPP030 = comm.ACoilApplyYn("APP030","1","S1");
							
							if ("Y".equals(sAPP030)) {
								//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
								JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
				                
				               	jrYdMsg.setField("JMS_TC_CD"         , "YFF1L004"); //크레인작업지시요구
								jrYdMsg.setField("YD_CRN_SCH_ID"     , ydCrnSchId         ); //야드크레인스케쥴ID
								jrYdMsg.setField("MSG_GP"            , "I"   ); //전문구분
								jrYdMsg.setField("YD_CRN_SCH_RMD_CNT", "S1"  ); //S1 일시정지 후 긴급작업

								jrRtn = commUtils.addSndData(commDao.getMsgL2("YFF1L004", jrYdMsg));
							}
							
			    			return jrRtn;
			    			
			    		} 

		    			/******************************************************
		    			 * 유인 긴급작업일 경우 명령선택 기동(기존작업)
		    			 ******************************************************/
					    jrParam.setField("YD_CRN_SCH_ID" 	, ydCrnSchIdWrk );
					    
					    
					    /**** 기존 작업 지시 정리 ***********/
						//크레인스케줄 Table 크레인ID, 우선순위 Update, 
					    jrParam.setField("YD_CRN_SCH_ID" , ydCrnSchIdWrk);
						commDao.update(jrParam, updCrnWrkMgtPriorWrkNext1, logId, methodNm,  "TB_YF_CRNSCH");						    
						    
			    	}
							
					//신규 작업 우선순위 변경
					commDao.update(jrParam, updCrnWrkMgt, logId, methodNm,  "TB_YF_CRNSCH");

					commDao.update(jrParam, updCrnWrkMgtS, logId, methodNm,  "TB_YF_CRNSCH");
						
					/**********************************************************
					* 3.2 신  크레인작업지시 요구 처리
					**********************************************************/

					//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();

					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					jrYdMsg.setField("JMS_TC_CD"       , "YFF1L004");	//크레인작업지시요구
					jrYdMsg.setField("MSG_GP"          , "I");	//야드설비ID
					jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID

					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L004", jrYdMsg));	

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
		String methodNm = "크레인작업관리 권하위치변경[ACoilJspBakSeEJB.updDownLocChange] < " + gdReq.getNavigateValue();
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
				
				jrYdMsg.setField("STL_NO"          , commUtils.trim(gdReq.getHeader("STL_NO"        ).getValue(ii))); //저장품
				jrYdMsg.setField("YD_EQP_ID"       , commUtils.trim(gdReq.getHeader("YD_EQP_ID"       ).getValue(ii))); //야드설비ID(크레인)
				jrYdMsg.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD"       ).getValue(ii))); //야드스케쥴코드
				jrYdMsg.setField("YD_CRN_SCH_ID"   , commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"   ).getValue(ii))); //야드크레인스케쥴ID
				jrYdMsg.setField("YD_WBOOK_ID"     , commUtils.trim(gdReq.getHeader("YD_WBOOK_ID"     ).getValue(ii))); //야드작업예약ID
				jrYdMsg.setField("YD_DN_WO_LOC"    , commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC"    ).getValue(ii))); //야드권하지시위치(신규)
				jrYdMsg.setField("YD_WRK_PROG_STAT", commUtils.trim(gdReq.getHeader("YD_WRK_PROG_STAT").getValue(ii))); //야드작업진행상태
				jrYdMsg.setField("YD_DN_WO_LOC_ORG", commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_ORG").getValue(ii))); //야드권하지시위치(기존)
				jrYdMsg.setField("BIZ_GP"    	   , gdReq.getParam("BIZ_GP")); //1:주작업 2:더미 3:사용자 4:차공정 5:진도, 7
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
	 * 
	 * <pre>
	 *      [A] 오퍼레이션명 : 크레인작업요구현황조회 작업취소
	 *      
	 *       중요 :: 크레인상태가 무인이라도 다음과 같은 조건을 만족하면
	 *       수동모드 작동
	 *        - W 상태 
	 *      
	 *       ㅁ 일반
	 *        1. 무인
	 *          : APP030의 기준값에 의한 상태체크로직
	 *           ( 크레인자동모드상태가 : 4, 크레인장비상태가 고장B)
	 *              일시정지이거나 고장상태가 아니면 권하위치를 변경할 수 없습니다
	 *          : TB_YF_CRNSCH 취소정보로 변경
	 *            (YD_WRK_PROG_STAT : S,YD_L2_REQUEST_STAT: X(취소요청))
	 *          : YFF1L004 전문전송
	 *          Return   문으로 빠져나온다.
	 *              
	 *        2. 유인 
	 *         : trtCrnSchCncl, trtWrkBookCncl 모듈 호출하여 크레인, 작업예약삭제한다.
	 *         : 크레인작업진행상태가 아래와 같을 경우엔 크레인작업지시요구 Java Methed Call rcvF1YFL007 처리 
	 *           ** 설비가 offline, 고장이 아니고 대기 일때 **
	 *           !"B".equals(sYD_WRK_PROG_STAT) && !"2".equals(sYD_EQP_WRK_MODE) && "W".equals(sYD_WRK_PROG_STAT)
	 *           YD_WRK_PROG_STAT : 4(권하완료)
	 * </pre>
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updCraneWrkCancel(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업요구현황조회 작업취소[ACoilJspBakSeEJB.updCraneWrkCancel] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String ydCrnSchId = ""; //야드크레인스케쥴ID
			String ydWbookId  = ""; //야드작업예약ID
		    String ydEqpId    = ""; //야드설비ID
		    String ydSchCd    = ""; //야드스케쥴코드
			String sYD_EQP_WRK_MODE = "";
			
		    boolean autoFlag = false;
		    String sYD_WRK_PROG_STAT     = "";
		    String sYD_EQP_AUTO_CRN_MODE = "";
		    String sYD_EQP_WRK_MODE2     = "";
		    String sYD_WRK_PROG_STAT2    = "";
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
			    sYD_WRK_PROG_STAT2 = commUtils.trim(gdReq.getHeader("YD_WRK_PROG_STAT").getValue(ii));
			    
			    /*****************************************
			     * 무인크레인일 때는 작업취소가 되면 안됨
			     *****************************************/
				jrParam.setField("YD_EQP_ID" , ydEqpId);
				JDTORecordSet rsResult = commDao.select(jrParam, getYfEqp,logId, methodNm,  "크레인설비정보조회");
				JDTORecord jrEqpInfo = null;
				
				if (rsResult.size() > 0) {
					rsResult.first();
					jrEqpInfo = rsResult.getRecord();
					
					sYD_WRK_PROG_STAT          = jrEqpInfo.getFieldString("YD_WRK_PROG_STAT");          // 설비 상태
					sYD_EQP_AUTO_CRN_MODE = jrEqpInfo.getFieldString("YD_EQP_AUTO_CRN_MODE");// AutoCrn 상태
					sYD_EQP_WRK_MODE2     = jrEqpInfo.getFieldString("YD_EQP_WRK_MODE2"); 	// AutoCrn 여부
					sYD_EQP_WRK_MODE            = jrEqpInfo.getFieldString("sYD_EQP_WRK_MODE");
					
					if ("A".equals(sYD_EQP_WRK_MODE2)) {// A:무인 R:리모컨
						autoFlag = true; 
					}
				}
				
				//W:명령선택대기 S:스케줄작성중
				if ("W".equals(sYD_WRK_PROG_STAT2)) {
					autoFlag = false;
				}
				
				if (autoFlag){ 
					
					//설비상태 체크하여 Auto일경우 일시정지 상태에서만 가능하게 수정
					if (!"4".equals(sYD_EQP_AUTO_CRN_MODE) && !"B".equals(sYD_WRK_PROG_STAT)) { //4: 일시정지 B:고장
						//m_ctx.setRollbackOnly();
						throw new YFUserException("무인크레인 [" + ydEqpId + "]이 일시정지이거나 고장상태가 아니면 취소할 수 없습니다.");
						
					}
				}

				/**************************************
				 * 무인크레인
				 **************************************/
				if (autoFlag) {
					
					// 작업대기상태 update
					jrParam.setField("YD_WRK_PROG_STAT"  , "S");
					jrParam.setField("YD_L2_REQUEST_STAT", YfConstant.YD_L2_REQUEST_STAT_X);
					jrParam.setField("YD_CRN_SCH_ID"     , ydCrnSchId);
					commDao.update(jrParam, updYdCrnSchProgStat, logId, methodNm, "크레인 작업대기상태 변경");
					
					jrParam.setField("MSG_GP"       , "D"       ); //전문구분(취소)
					
					if (YfConstant.SCH_CODE_WFB_COIL_TAKEINOUT.equals(ydSchCd)) { //분동코일
						JDTORecordSet jsInfo = commDao.select(jrParam, getStockIdByCrnSchId, logId, methodNm, "분동코일ID 조회");
						if (jsInfo.size() > 0) {
							jrParam.setField("STL_NO", jsInfo.getRecord(0).getFieldString("STL_NO"));
							jrRtn = commUtils.addSndData(commDao.getMsgL2("YFF1L004WC", jrParam));
						}
					} else {
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L004", jrParam));
					}
					
					return jrRtn;
				}
				
			    
			    //작업할  야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if (commUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }
				arrYdWbookId[ii] = ydWbookId;

				//기본정보조회
				jrParam.setField("YD_WBOOK_ID", ydWbookId);

				JDTORecordSet jsCrnSch = commDao.select(jrParam, getCrnsch, logId, methodNm, "크레인작업지시read");
				if (jsCrnSch == null || jsCrnSch.size() <= 0) {
					throw new YFUserException("작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 존재하지 않습니다.");
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
			if (!"B".equals(sYD_WRK_PROG_STAT) && !"2".equals(sYD_EQP_WRK_MODE) && "W".equals(sYD_WRK_PROG_STAT)) { 
	
				/**********************************************************
				* 5. 크레인작업지시요구 전문 조회
				**********************************************************/
				//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, "F1YFL007");
	
				jrYdMsg.setField("JMS_TC_CD"       , "F1YFL007");	//크레인작업지시요구
				jrYdMsg.setField("YD_EQP_ID"       , ydEqpId   );	//야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT", "4"       );	//야드작업진행상태(권하완료)
				jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   );	//야드스케쥴코드
				jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID
	
				EJBConnector sndConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
				JDTORecord jrRtn1 = (JDTORecord)sndConn.trx("rcvF1YFL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				
				
				jrRtn = commUtils.addSndData(jrRtn,jrRtn1);
			}
		
			commUtils.printParam(logId, jrRtn);
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * <pre>
	 *      [A] 오퍼레이션명 : 크레인스케줄 권하지시위치 변경
	 *       * 권하위치변경은  sYD_WRK_PROG_STAT : W S 1 2 상태 진행가능하다.
	 *       크레인 모드에 따른 권하위치 변경 로직구분은 다음과 같다
	 *       
	 *       중요 :: 크레인상태가 무인이라도 다음과 같은 조건을 만족하면
	 *       수동모드 작동
	 *        - W 상태
	 *        - XX 검색위치
	 *        
	 *       * 공통사항
	 *        1. ACoilSchBakSeEJB.procStockIdBaseCheckNew 모듈을 호출하여 권하위치 변경로직 재 확인
	 *        2. 권하위치 정합성
	 *         - To위치 정보가 6, 8일 경우 적치 1단 2단에 대해서 체크하여 적치단 정보가 변경될 소지가 있음
	 *         
	 *       ㅁ 스크랩
	 *       
	 *       ㅁ 일반
	 *        1. 무인
	 *          : APP030의 기준값에 의한 상태체크로직
	 *           ( 크레인자동모드상태가 : 4, 크레인장비상태가 고장B)
	 *              일시정지이거나 고장상태가 아니면 권하위치를 변경할 수 없습니다
	 *          : APP030의 기준값에 의한 크레인작업지시요구 YFF1L004
	 *            - MSG_GP : U, YD_CRN_SCH_RMD_CNT : S5 (일시정지 후 권하위치 변경)
	 *            Return으로 빠져나감
	 *            
	 *          : APP030의 기준값에 N일 경우
	 *            - 크레인작업지시요구 Java Methed Call rcvF1YFL007 처리 
	 *              ( YD_WRK_PROG_STAT:  "2".equals(sYD_WRK_PROG_STAT) ? "5" : sYD_WRK_PROG_STAT )
	 *             Return으로 빠져나감 
	 *          
	 *        2. 유인
	 *          : 기존적치단 상태 : E적치가능으로 변경
	 *          : 신규적치단 활성화 ( YD_STK_LYR_ACTIVE_STAT : E, YD_STK_LYR_STAT : D(권하)
	 *          : 크레인스케줄 수정 - 권상, 권하지시위치
	 *          : 기존 대차, 차량 권하위치에서 일반야드로 변경 시 대차 or 차량스케줄 작업예약ID 삭제
	 *          : 크레인작업지시요구 Java Methed Call rcvF1YFL007 처리 
	 *
	 * </pre>
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updCrnSchDnWoLoc(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인스케줄 권하지시위치 변경[ACoilJspBakSeEJB.updCrnSchDnWoLoc] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String sSTL_NO         = commUtils.trim(rcvMsg.getFieldString("STL_NO"        )); //저장품
			String sYD_EQP_ID        = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID(크레인)
			String sYD_SCH_CD        = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String sYD_CRN_SCH_ID    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String sYD_WBOOK_ID      = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
			String sYD_DN_WO_LOC     = commUtils.trim(rcvMsg.getFieldString("YD_DN_WO_LOC"    )); //야드권하지시위치(신규)
			String sYD_WRK_PROG_STAT = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String sMODIFIER         = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자
			String sYD_DN_WO_LOC_ORG = commUtils.trim(rcvMsg.getFieldString("YD_DN_WO_LOC_ORG")); //야드권하지시위치(기존)
			String sBIZ_GP = commUtils.trim(rcvMsg.getFieldString("BIZ_GP")); //야드권하지시위치(기존)
			
			
			if ("".equals(sYD_CRN_SCH_ID)) {
				throw new YFUserException("크레인스케쥴ID가 없습니다.");
			} else if ("".equals(sYD_DN_WO_LOC)) {
				throw new YFUserException("변경할 권하지시위치가 없습니다.");
			} 
			
			
			if( "".equals(sBIZ_GP)){
				commUtils.printLog(logId, methodNm, "[ TO위치 배드를 찾기 위한 BIZ_GP이 존재하지 않습니다. ]" + rcvMsg );
				throw new YFUserException("TO위치 배드를 찾기 위한 [BIZ_GP]값이 존재하지 않습니다.");
			}

			//Return Value
			JDTORecord sndRecord = JDTORecordFactory.getInstance().create();

			String sYD_STK_COL_GP   = sYD_DN_WO_LOC.substring(0,  6); //야드적치열구분
			String sYD_STK_BED_NO   = sYD_DN_WO_LOC.substring(6,  8); //야드권하지시위치
			String sYD_STK_LYR_NO = sYD_DN_WO_LOC.substring(8, 10); //야드권하지시위치
			String ydDnWoLocOld   = ""; //야드권하지시위치(기존)
			String ydDnWoLayerOld = ""; //야드권하지시위치(기존)
			String ydDnWoLayer    = ""; //야드권하지시단(신규)
			String ydDnWoLocXaxis = ""; //야드권하지시X축(신규)
			String ydDnWoLocYaxis = ""; //야드권하지시Y축(신규)
			String ydDnWoLocZaxis = ""; //야드권하지시Z축(신규)
			
			JDTORecordSet jsCrnSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, sMODIFIER);
			
			
			/*********************************************
			 * 야드작업진행상태  JAVA단에서 한번 더 체크
			 *********************************************/ 
			jrParam.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID);
			JDTORecordSet rstCrnSch = commDao.select(jrParam, getYfCrnsch, logId, methodNm, "크레인스케쥴정보조회");
			
			if (rstCrnSch == null || rstCrnSch.size() <= 0) {
				throw new YFUserException("크레인스케줄이 없습니다.");
			} else {
				commUtils.printLog(logId, ">>> 화면에서 받은 YD_WRK_PROG_STAT 값 : " + sYD_WRK_PROG_STAT + " <<<", "SL");
				
				sYD_WRK_PROG_STAT = rstCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT");
				
				commUtils.printLog(logId, ">>> DB에서 읽은 YD_WRK_PROG_STAT 값 : " + sYD_WRK_PROG_STAT + " <<<", "SL");
			}
			
			
			jrParam.setField("YD_EQP_ID"            , sYD_EQP_ID);
			jrParam.setField("YD_SCH_CD"            , sYD_SCH_CD);
			jrParam.setField("YD_CRN_SCH_ID"       	, sYD_CRN_SCH_ID);
			jrParam.setField("YD_CARLD_WRK_BOOK_ID"	, sYD_WBOOK_ID );	//야드상차작업예약ID
			jrParam.setField("YD_STK_COL_GP"       	, sYD_STK_COL_GP);
			
			/*********************************
			 * 무인화 관련 위치변경 조건 체크
			 ********************************/
			boolean autoFlag       = false;
			JDTORecordSet rsResult = null;
			JDTORecord jrEqpInfo   = JDTORecordFactory.getInstance().create();
			
			String sYD_EQP_PROG_STAT    = "";
			String sYD_EQP_AUTO_CRN_MODE = "";
			String sYD_EQP_WRK_MODE2     = "";
			String sYD_EQP_WRK_MODE            = "";//online/off

			jrParam.setField("YD_EQP_ID" , sYD_EQP_ID);
			rsResult = commDao.select(jrParam, getYfEqp, logId, methodNm, "크레인장비정보조회");
			
			if (rsResult.size() > 0) {
				rsResult.first();
				jrEqpInfo = rsResult.getRecord();
				
				sYD_EQP_PROG_STAT           = jrEqpInfo.getFieldString("YD_EQP_PROG_STAT");          // 설비 상태
				sYD_EQP_AUTO_CRN_MODE = jrEqpInfo.getFieldString("YD_EQP_AUTO_CRN_MODE");// AutoCrn 상태
				sYD_EQP_WRK_MODE2     = jrEqpInfo.getFieldString("YD_EQP_WRK_MODE2"); 	 // AutoCrn 여부
				sYD_EQP_WRK_MODE            = jrEqpInfo.getFieldString("YD_EQP_WRK_MODE");
				
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

			/*****************************************************************************
			 * 스크랩 위치검색실패일경우 스크랩 정보조회후 정보 없으면 권하위치변경 못함
			 ****************************************************************************/
			if (sSTL_NO.startsWith("S") && "XX010101".equals(sYD_DN_WO_LOC_ORG)) { 
				JDTORecordSet jsScrInfo = commDao.select(jrParam, getScrInfo, logId, methodNm, "스크랩 정보 조회");
				if (jsScrInfo.size() == 0) {
					throw new YFUserException("스크랩["+sSTL_NO+"] 정보가 없으므로 권하위치를 변경할 수 없습니다. 조업L2에 문의하세요");
				}
			}
			
			// 일시정지-권하위치변경 적용여부
			String sAPP030 = comm.ACoilApplyYn("APP030","1","S5");
			if (autoFlag && !"Y".equals(sAPP030)) { 
				//설비상태 체크하여 Auto일경우 일시정지 상태에서만 가능하게 수정
				if (!"4".equals(sYD_EQP_AUTO_CRN_MODE) && !"B".equals(sYD_EQP_PROG_STAT)) { //4: 일시정지 B:고장
					throw new YFUserException("무인크레인 [" + sYD_EQP_ID + "]이 일시정지이거나 고장상태가 아니면 권하위치를 변경할 수 없습니다.");
				}
			}
			
			/********************************************
			 * 적치위치 변경에 따른 위치 정합성 체크
			 *******************************************/
			jrParam.setField("STL_NO"         , sSTL_NO);
			jrParam.setField("YD_STK_COL_GP"  , sYD_STK_COL_GP);
			jrParam.setField("YD_STK_BED_NO"  , sYD_STK_BED_NO);
			jrParam.setField("YD_STK_LYR_NO"  , sYD_STK_LYR_NO);
			jrParam.setField("BIZ_GP"		  , sBIZ_GP);
			//2단적치 기울기 공식 적용 여부
//			String sAPP024 = ymComm.BCoilApplyYn("APP024","3","1");
			
			commUtils.printLog(logId, "[STL_NO : "+sSTL_NO+"] [YD_DN_WO_LOC : "+sYD_DN_WO_LOC+"] 권하위치변경 >> 적치기준 조회", "[INFO]");
			
			//권하변경위치가 스크랩이 아니고 야드일 때만 적치기준 확인
			if (!sSTL_NO.startsWith("S") && sYD_STK_COL_GP.matches("[1][A-H]\\d\\d\\d\\d")) {
				
				EJBConnector ejbConn = new EJBConnector("default", "ACoilSchBakSeEJB", this);
				String isSuc = "";
//				if ("Y".equals(sAPP024)) {
					isSuc = (String)ejbConn.trx("procStockIdBaseCheckNew", new Class[] { String.class, String.class, JDTORecord.class }, new Object[] { logId, methodNm, jrParam });
//				} else {
//					isSuc = (String)ejbConn.trx("procStockIdBaseCheck", new Class[] { String.class, String.class, JDTORecord.class }, new Object[] { logId, methodNm, jrParam });	
//				}
 
				if (YfConstant.RETN_CD_FAILURE.equals(isSuc)) {
					if ("01".equals(sYD_STK_LYR_NO)) {				
						throw new YFUserException("좌우 코일 적치기준불가(두께, 폭, 중량) 및 권하위치 검증에 실패 하였습니다.");
					} else {
						throw new YFUserException("하단 코일 적치기준불가(두께, 폭, 중량) 및 권하위치 검증에 실패 하였습니다.");
					}
				}
			}
			

			if (sYD_DN_WO_LOC.length() == 6) {
			
				/**********************************************************
				* 1. 신규 권하지시위치 Bed정보 조회
				**********************************************************/
				jsCrnSch = commDao.select(jrParam, getCrnSchDnWoLocBt, logId, methodNm, "신규권하위치 조회");
				
			} else if (sYD_DN_WO_LOC.length() == 8) {
				
				jrParam.setField("YD_STK_BED_NO"       	, sYD_STK_BED_NO);
				jsCrnSch = commDao.select(jrParam, getCrnSchDnWoLocCurBed, logId, methodNm, "신규권하위치 조회");
				
			} else {
				
				jrParam.setField("YD_STK_BED_NO"       	, sYD_STK_BED_NO);
				jrParam.setField("YD_STK_LYR_NO"      	, sYD_STK_LYR_NO);
				jsCrnSch = commDao.select(jrParam, getCrnSchDnWoLocCurLyr, logId, methodNm, "신규권하위치 조회");
			}
			
			
			if (jsCrnSch == null || jsCrnSch.size() <= 0) {
				throw new YFUserException("신규 권하지시위치[" + sYD_DN_WO_LOC + "] 정보가 없습니다.");
			} else {
			
		    	JDTORecord jrCrnSch = jsCrnSch.getRecord(0);

		    	ydDnWoLocOld   		= commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_OLD"  ));
		    	ydDnWoLayerOld 		= commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LYR_OLD"));
		    	sYD_STK_BED_NO       = commUtils.trim(jrCrnSch.getFieldString("YD_STK_BED_NO"      )); 
		    	ydDnWoLayer         = commUtils.trim(jrCrnSch.getFieldString("YD_STK_LYR_NO"    )); 
		    	ydDnWoLocXaxis 		= commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_XAXIS"));
		    	ydDnWoLocYaxis 		= commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_YAXIS"));
		    	ydDnWoLocZaxis 		= commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_ZAXIS"));
			    String dlLocChkRst 	= commUtils.trim(jrCrnSch.getFieldString("DL_LOC_CHK_RST"    ));
	

			    if ("UP".equals(dlLocChkRst)) {
					throw new YFUserException("권상/권하대기(U) 재료가 적치되어 있습니다.");
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
				jrParam.setField("YD_DN_WO_LOC_TO"   , sYD_STK_COL_GP+sYD_STK_BED_NO);
				jrParam.setField("STL_NO_TEMP"       , sSTL_NO);
				jrParam.setField("STK_LYR_NO_TEMP"   , sYD_STK_LYR_NO);
				jrParam.setField("YD_L2_REQUEST_STAT", YfConstant.YD_L2_REQUEST_STAT_5);
				jrParam.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID);
				commDao.update(jrParam, upYfCrnSchLocStat, logId, methodNm, "무인크레인 적치위치 임시저장");
				/**********************************************************
				* 크레인작업지시요구 전문 조회
				**********************************************************/
				// 일시정지-권하위치변경 적용여부
				if ("Y".equals(sAPP030)) {
					JDTORecord jrS5Msg = commUtils.getParam(logId, methodNm, sMODIFIER);
					
					jrS5Msg.setField("JMS_TC_CD"         , "YFF1L004"); //크레인작업지시요구
					jrS5Msg.setField("YD_CRN_SCH_ID"     , sYD_CRN_SCH_ID     ); //야드크레인스케쥴ID
					jrS5Msg.setField("MSG_GP"            , "U"   ); //전문구분 - 재지시
					jrS5Msg.setField("YD_CRN_SCH_RMD_CNT", "S5"  ); //S5 일시정지 후 권하위치 변경
	
					sndRecord = commUtils.addSndData(commDao.getMsgL2("YFF1L004", jrS5Msg));
					
					return sndRecord;
				}
				
				//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, sMODIFIER);

				jrYdMsg.setField("JMS_TC_CD"       , "F1YFL007");	//크레인작업지시요구
				jrYdMsg.setField("YD_EQP_ID"       , sYD_EQP_ID       );	//야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT", "2".equals(sYD_WRK_PROG_STAT) ? "5" : sYD_WRK_PROG_STAT);	//야드작업진행상태(권하위치변경 요구상태)
				jrYdMsg.setField("YD_SCH_CD"       , sYD_SCH_CD       );	//야드스케쥴코드
				jrYdMsg.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID   );	//야드크레인스케쥴ID
				jrYdMsg.setField("MODIFIER"        , sMODIFIER        );	//수정자

				EJBConnector sndConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
				JDTORecord jrRtn = (JDTORecord)sndConn.trx("rcvF1YFL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

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
			jrParam.setField("YD_STK_COL_GP_OLD"   , ydDnWoLocOld.substring(0, 6));
			jrParam.setField("YD_STK_BED_NO_OLD"   , ydDnWoLocOld.substring(6, 8));
			jrParam.setField("YD_STK_LYR_NO_OLD" , ydDnWoLayerOld);
			jrParam.setField("YD_STK_COL_GP_NEW"   , sYD_STK_COL_GP    );
			jrParam.setField("YD_STK_BED_NO_NEW"   , sYD_STK_BED_NO    );
			if (sYD_DN_WO_LOC.length() == 6) {
				jrParam.setField("YD_DN_WO_LOC"      , sYD_DN_WO_LOC+sYD_STK_BED_NO     );
			} else {
				jrParam.setField("YD_DN_WO_LOC"      , sYD_DN_WO_LOC.substring(0, 8));
			}

			jrParam.setField("YD_DN_WO_LYR"    , ydDnWoLayer   );
			jrParam.setField("YD_STK_BED_NO"      , sYD_STK_BED_NO );
			jrParam.setField("YD_STK_LYR_NO"    , ydDnWoLayer   );
			jrParam.setField("YD_DN_WO_LOC_XAXIS", ydDnWoLocXaxis);
			jrParam.setField("YD_DN_WO_LOC_YAXIS", ydDnWoLocYaxis);
			jrParam.setField("YD_DN_WO_LOC_ZAXIS", ydDnWoLocZaxis);


			//적치단 수정 - 기존
			commDao.update(jrParam, updYdStkLyrYdByCrnSchId, logId, methodNm, "기존권하위치 CLEAR");	
			
			
			//신규 적치단 재료정보READ
			JDTORecordSet jsCrnSchMtl = commDao.select(jrParam, getCrnWekMtlByschid, logId, methodNm, "기존권하위치 조회");
			

			JDTORecord recOutTemp = null;
			JDTORecord recInTemp = null;
			
			String szSTL_NO = null; 
			 
			int intRtnVal = 0; 
			
			//----------------------------------------------------------------------------------------------------------
			//신규적치단 활성화
			//----------------------------------------------------------------------------------------------------------
			for(int Loop_i = 1; Loop_i <= jsCrnSchMtl.size(); Loop_i++) {
				jsCrnSchMtl.absolute(Loop_i);
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(jsCrnSchMtl.getRecord());
		    	
		    	szSTL_NO   = commUtils.trim(recOutTemp.getFieldString("STL_NO"     ));
		    	
		    	recInTemp  = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP"   , sYD_STK_COL_GP);
		    	recInTemp.setField("YD_STK_BED_NO"   , sYD_STK_BED_NO);
		    	recInTemp.setField("YD_STK_LYR_NO" , ydDnWoLayer);
		    	recInTemp.setField("STL_NO"       , szSTL_NO);
		    	recInTemp.setField("YD_STK_LYR_ACTIVE_STAT", "E");
		    	recInTemp.setField("YD_STK_LYR_STAT"       , "D");
		    	recInTemp.setField("MODIFIER"      , sMODIFIER);
		    	
				intRtnVal = commDao.update(recInTemp, updYdStkLyrYdStkColBedGp, logId, methodNm, "TB_YF_STKLYR 등록");
				
				if (intRtnVal <= 0) {
					commUtils.printLog(logId, "[" + methodNm + "] 적치단[" + sYD_STK_COL_GP + "]활성화중 ERROR 발생", "SL");
					throw new YFUserException("적치단변경시 오류 발생.");
				}
			}
			

			//크레인스케줄 수정 - 권상, 권하지시위치
			commDao.update(jrParam, updCrnSchDnWoLocCrnSch, logId, methodNm, "TB_YF_CRNSCH 크레인스케줄 수정 - 권상, 권하지시위치");				
																			
			//기존 대차, 차량 권하위치에서 일반야드로 변경 시 대차 or 차량스케줄 작업예약ID 삭제
			// 2020.02.28 CTS는 아직 생성되기 전이므로 삭제안해도 됨
			ydDnWoLocOld = ydDnWoLocOld.substring(2, 4);
			if ("TC".equals(ydDnWoLocOld)
					&& !ydDnWoLocOld.equals(sYD_DN_WO_LOC.substring(2, 4))) {
				if ("TC".equals(ydDnWoLocOld)) {
					//대차스케줄 수정 - 상차작업예약ID 삭제
					//작업예약 Table 우선순위 Update					
					commDao.update(jrParam, updCrnSchDnWoLocTCarSch, logId, methodNm, "TB_YF_TCARSCH 우선순위 Update");				
				} else {
					//차량스케줄 수정 - 상차작업예약ID 삭제
					//작업예약 Table 우선순위 Update
					commDao.update(jrParam, updCrnSchDnWoLocCarSch, logId, methodNm, "TB_YD_CARSCH 우선순위 Update");				
				}
			}
			
			/**********************************************************
			* 3. 크레인작업지시요구 전문 조회
			**********************************************************/
			//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, sMODIFIER);

			jrYdMsg.setField("JMS_TC_CD"       , "F1YFL007");	//크레인작업지시요구
			jrYdMsg.setField("YD_EQP_ID"       , sYD_EQP_ID      );	//야드설비ID
			jrYdMsg.setField("YD_WRK_PROG_STAT", sYD_WRK_PROG_STAT);	//야드작업진행상태
			jrYdMsg.setField("YD_SCH_CD"       , sYD_SCH_CD      );	//야드스케쥴코드
			jrYdMsg.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID   );	//야드크레인스케쥴ID
			jrYdMsg.setField("MODIFIER"        , sMODIFIER     );	//수정자

			EJBConnector sndConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)sndConn.trx("rcvF1YFL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

			sndRecord = commUtils.addSndData(sndRecord, jrRtn);
			
			commUtils.printLog(logId, methodNm, "S-");

			return sndRecord;

		} catch(DAOException e) {
			throw e;
		} catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 
	 * <pre>
	 * [A] 오퍼레이션명 : 크레인스케쥴현황조회-스케줄취소
	 *  
	 *  중요 ::
	 *     1. 권하지시(권상)상태에서는 스케줄취소 스킵
	 *     2. 권상 전에만 스케줄 취소가 가능함
	 *     3. 무인 모드라도 아래와 같은 상태이면 수동모드 로직으로 작동한다. 
	 *        W:명령선택대기 - L2에 작업지시가 내려가지 않은 상태
	 *  
	 *   스캐쥴 재기동 구분자(RS) :: TRT_DTL_GP 
	 *   (화면에서 :: To위치재기동, 스케쥴취소 버튼)
	 *  
	 *  취소처리 
	 *   1. 스케줄 재기동의 경우 동일 작업예약 스케줄상태 확인후 재기동여부 판단
	 *    
	 *   2. 동일작업예약을 확인(더미재 같은 경우)하여 W상태가 아니면 오류처리
	 *  
	 *  
	 *   ㅁ 무인 
	 *   	- 일시정지-스케줄취소 적용여부(APP030)에 따른 상태값 체크하여 진행여부 판단
	 *        !"4".equals(szEqpAutoCrnMode) && !"B".equals(szydEqpStat)  
	 *        무인크레인이 일시정지이거나 고장상태가 아니면 취소할 수 없습니다.
	 *     
	 *     - 일시정지-스케줄취소 적용여부(APP030) 
	 *       일때 따른 크레인작업지시요구(YFF1L004)
	 *       : MSG_GP - D,  YD_CRN_SCH_RMD_CNT - SD(일시정지 후 스케줄취소)
	 *       아닐때
	 *       : MSG_GP - D
	 *      
	 *     - TB_YF_CRNSCH 취소관련 정보 업데이트
	 *       (YD_L2_REQUEST_STAT :: D(스케쥴취소요청:응답대기중))
	 *     - TB_YF_WRKBOOK :: SCH_CNCL_YN 스케쥴취소 컬럼 업데이트
	 *     - TB_YF_SCHRULE :: YD_SCH_AUTO_ST_YN 스케쥴 자동기동 금지 상태변경(N)
	 *     **** 무인일 경우 L2응답에 따라 스케줄 취소여부가 결정되므로 명령선택 기동않고 종료
	 *     
	 *   ㅁ 유인 
	 *     - 크레인 스케쥴 취소 모듈 호출 trtCrnSchCncl
	 *     - TB_YF_WRKBOOK :: SCH_CNCL_YN 스케쥴취소 컬럼 업데이트
	 *     - TB_YF_SCHRULE :: YD_SCH_AUTO_ST_YN 스케쥴 자동기동 금지 상태변경(N)
	 *     - ACoilRcvL2BakSeEJB rcvF1YFL007 크레인작업지시 요구 아래의 조건을 만족해야함
	 *       TB_YF_EQP테이블의 설비상태(YD_WRK_PROG_STAT)
	 *       !"B".equals(szydEqpStat) && !"2".equals(sYD_EQP_WRK_MODE) && "W".equals(szydEqpStat)
	 * </pre>
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */	
	public JDTORecord updCraneSchCancel(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업요구현황조회 스케줄취소[ACoilJspBakSeEJB.updCraneSchCancel] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sTRT_DTL_GP = commUtils.trim(gdReq.getParam("TRT_DTL_GP"));	//처리상세구분 RS스케줄재기동
			String sIS_SCH_MTL = StringHelper.evl(commUtils.trim(gdReq.getParam("IS_SCH_MTL")), "N"); // 재료단위 스케줄 취소여부
			String sYD_GP      = StringHelper.evl(commUtils.trim(gdReq.getParam("YD_GP")), "1");
			
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
			String sSCH_CANCLE_MENT = commUtils.trim(gdReq.getParam("V_SCH_CANCLE_MENT"));
			String sYD_EQP_WRK_MODE        = "";//online/off
			
			String sYD_STK_COL_GP = "";
			String sYD_BED_GP    = "";
			String sYD_LYR_GP    = "";
			String sYD_DN_WO_LOC_ORG = "";
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int i = 0; i < rowCnt; i++) {
				
				sYD_STK_COL_GP     = commUtils.getValue(gdReq, "YD_UP_WO_LOC_LYR", i);

				// 권상 지시가 없다면
				if("".equals(sYD_STK_COL_GP) || sYD_STK_COL_GP.length() <= 9 ){
					throw new YFUserException("권상위치 정보를 확인하세요.!"
								+"[ YD_UP_WO_LOC_LYR :: " + sYD_STK_COL_GP + " ]"
					);
				}

				sYD_STK_COL_GP = sYD_STK_COL_GP.substring(0, 6);
				sYD_BED_GP        = commUtils.getValue(gdReq, "YD_UP_WO_LOC_LYR", i).substring(6, 8);
				sYD_LYR_GP        = commUtils.getValue(gdReq, "YD_UP_WO_LYR"    , i); //단
				sYD_DN_WO_LOC_ORG = commUtils.getValue(gdReq, "YD_DN_WO_LOC_ORG"  , i); //기존권하지시위치
				sYD_WRK_PROG_STAT = commUtils.getValue(gdReq, "YD_WRK_PROG_STAT", i);
				sYD_EQP_ID        = commUtils.getValue(gdReq, "YD_EQP_ID"       , i);
				sYD_CRN_SCH_ID    = commUtils.getValue(gdReq, "YD_CRN_SCH_ID"   , i);
				sYD_SCH_CD        = commUtils.getValue(gdReq, "YD_SCH_CD"       , i);
				sYD_WBOOK_ID      = commUtils.getValue(gdReq, "YD_WBOOK_ID"     , i);
				 
				jrParam.setField("YD_STK_COL_GP", sYD_STK_COL_GP);
				jrParam.setField("YD_STK_BED_NO", sYD_BED_GP);
				
				/*********************************
				 * 무인크레인 관련 위치변경 조건 체크
				 ********************************/
				jrParam.setField("YD_EQP_ID" , sYD_EQP_ID);
				rsResult = commDao.select(jrParam, getYfEqp, logId, methodNm, "무인크레인 관련 위치변경 조건 체크");
				
				if (rsResult.size() > 0) {
					rsResult.first();
					jrEqpInfo = rsResult.getRecord();
					
					szydEqpStat      = jrEqpInfo.getFieldString("YD_EQP_PROG_STAT");          // 설비 상태
					szEqpAutoCrnMode = jrEqpInfo.getFieldString("YD_EQP_AUTO_CRN_MODE");// AutoCrn 상태
					szEqpAutoCrnYN   = jrEqpInfo.getFieldString("YD_EQP_WRK_MODE2"); 	// AutoCrn 여부
					sYD_EQP_WRK_MODE       = jrEqpInfo.getFieldString("YD_EQP_WRK_MODE");
					
					if ("A".equals(szEqpAutoCrnYN)) {// A:무인
						autoFlag = true; 
						mainFlag = true;
					}
				}
				
				/*********************************************
				 * 야드작업진행상태  JAVA단에서 한번 더 체크
				 *********************************************/
				jrParam.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID);
				JDTORecordSet rstCrnSch = commDao.select(jrParam, getYfCrnsch, logId, methodNm, "야드작업진행상태  JAVA단에서 한번 더 체크");
				
				if (rstCrnSch == null || rstCrnSch.size() <= 0) {
					throw new YFUserException("크레인스케줄이 없습니다.");
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
					JDTORecordSet jsSchList = commDao.select(jrParam, getCrnSchSameWrkBook, logId, methodNm, "동일 작업예약 스케줄 조회");
					if (jsSchList.size() > 0) {
						for (int ii = 0; ii < jsSchList.size(); ++ii) {
							if (!"W".equals(jsSchList.getRecord(ii).getFieldString("YD_WRK_PROG_STAT"))) {
								throw new YFUserException("동일 작업예약의 다른 스케줄이 대기상태가 아니므로 스케줄 재기동을 할 수 없습니다.");
							}
						}
					}
				}
				
				/*********************************
				 * 무인 크레인 작업일 경우 
				 *********************************/
				if (autoFlag) { 
					
					//일시정지-스케줄취소 적용여부
					String sAPP030 = comm.ACoilApplyYn("APP030","1","SD");
					
					//설비상태 체크하여 Auto일경우 일시정지 상태에서만 가능하게 수정
					if ("Y".equals(sAPP030)) {
						//일시정지-스케줄취소
					} else if (!"4".equals(szEqpAutoCrnMode) && !"B".equals(szydEqpStat)) { //4: 일시정지 B:고장
						throw new YFUserException("무인크레인 [" + sYD_EQP_ID + "]이 일시정지이거나 고장상태가 아니면 취소할 수 없습니다.");
					}
					
					/**********************************************************
					* 크레인작업지시요구 전문 조회 - 일시정지-스케줄취소
					**********************************************************/
					if ("Y".equals(sAPP030)) {
						//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
						JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
		                
		               	jrYdMsg.setField("JMS_TC_CD"         , "YFF1L004"); //크레인작업지시요구
						jrYdMsg.setField("YD_CRN_SCH_ID"     , sYD_CRN_SCH_ID         ); //야드크레인스케쥴ID
						jrYdMsg.setField("MSG_GP"            , "D"   ); //전문구분
						jrYdMsg.setField("YD_CRN_SCH_RMD_CNT", "SD"  ); //S1 일시정지 후 스케줄취소

						//jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L004", jrYdMsg));
						
						if (YfConstant.SCH_CODE_WFB_COIL_TAKEINOUT.equals(sYD_SCH_CD)) { //분동코일
							JDTORecordSet jsInfo = commDao.select(jrParam, getStockIdByCrnSchId, logId, methodNm, "분동코일ID 조회");
							if (jsInfo.size() > 0) {
								jrYdMsg.setField("STL_NO", jsInfo.getRecord(0).getFieldString("STL_NO"));
								jrRtn = commUtils.addSndData(commDao.getMsgL2("YFF1L004WC", jrYdMsg));
							}
						} else {
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L004", jrYdMsg));
						}
						
					} else {
						//크레인 스케줄의 취소 전문 전송
						JDTORecord tcRecord = JDTORecordFactory.getInstance().create();
						tcRecord.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID);
						tcRecord.setField("MSG_GP"          , "D");
	
						if (YfConstant.SCH_CODE_WFB_COIL_TAKEINOUT.equals(sYD_SCH_CD)) { //분동코일
							JDTORecordSet jsInfo = commDao.select(jrParam, getStockIdByCrnSchId, logId, methodNm, "분동코일ID 조회");
							if (jsInfo.size() > 0) {
								tcRecord.setField("STL_NO", jsInfo.getRecord(0).getFieldString("STL_NO"));
								jrRtn = commUtils.addSndData(commDao.getMsgL2("YFF1L004WC", tcRecord));
							}
						} else {
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L004", tcRecord));
						}
					}
					
					commUtils.printLog(logId, "["+szSessionName+"] 스케줄["+sYD_CRN_SCH_ID+"]을 스케쥴 취소 요청 전송", "S+");
		        	
					// 작업대기상태 update : 작업취소와 구분되게 D 로 상태 없데이트 함...
					jrParam.setField("YD_L2_REQUEST_STAT", YfConstant.YD_L2_REQUEST_STAT_D);
					jrParam.setField("YD_CRN_SCH_ID"     , sYD_CRN_SCH_ID);
					commDao.update(jrParam, updYdCrnSchProgStat, logId, methodNm, "작업대기상태 스케줄 취소(D) UPDATE");
					
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
				jrParam.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
				jrParam.setField("SCH_CANCLE_MENT", sSCH_CANCLE_MENT);
				commDao.update(jrParam, updSchCnclYn, logId, methodNm, "TB_YF_WRKBOOK >> SCH_CNCL_YN");
				
				
				/**********************************************************
				* 스케줄 취소시 해당 스케줄 자동기동여부 N
				**********************************************************/
				// 2020. 02. 19 확인사항 :: 크레인스케쥴 자동기동을 N으로 변경처리하는지 확인
				if (!"RS".equals(sTRT_DTL_GP)) {
					jrParam.setField("YD_SCH_AUTO_ST_YN", "N"); // 'N' 스케줄 자동기동 금지
					jrParam.setField("YD_SCH_CD"        , sYD_SCH_CD);
					commDao.update(jrParam, updSchAutoStYn, logId, methodNm, "TB_YF_SCHRULE >> YD_SCH_AUTO_ST_YN");
				}
				
				/****************************
				 * 스케줄 재기동
				 ****************************/
				if ("RS".equals(sTRT_DTL_GP)) {
					
					commUtils.printLog(logId, "○○○ 스케줄 재기동", "[info]");
					JDTORecord jParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
					jParam.setResultCode(logId);	//Log ID
					jParam.setResultMsg(methodNm);	//Log Method Name

					jParam.setField("JMS_TC_CD", "YFYFJ302"); //야드작업예약ID
					jParam.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시				
					
					jParam.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
					jParam.setField("YD_SCH_CD"  , ""); //야드스케쥴코드
					jParam.setField("YD_EQP_ID"  , ""); //야드설비ID
					
					//크레인스케줄기동 전문
					EJBConnector sndConnD = new EJBConnector("default", "ACoilSchBakSeEJB", this);
					JDTORecord jrRtnD = (JDTORecord)sndConnD.trx("procYFYFJ302", new Class[] { JDTORecord.class }, new Object[] { jParam });
					jrRtn = commUtils.addSndData(jrRtn, jrRtnD);
					
				} //end RS
			} // end for
			
			/**************************************************
			 * 무인일 경우 L2응답에 따라 스케줄 취소여부가 결정되므로 명령선택 기동않고 종료
			 *************************************************/
			if (mainFlag) {
				commUtils.printLog(logId, "자동화크레인 스케줄 취소 종료(무인일 경우 L2응답에 따라 스케줄 취소여부가 결정되므로 명령선택 기동않고 종료)", "[info]");
				return jrRtn;
			}
			
			/**************************************************
			 * 명령선택 재기동
			 **************************************************/
			commUtils.printLog(logId, "설비상태 재조회 - 명령선택 조건 재설정", "[info]");
			jrParam.setField("YD_EQP_ID" , sYD_EQP_ID);
			rsResult = commDao.select(jrParam, getYfEqp, logId, methodNm, "설비상태 조회");
			
			if (rsResult.size() > 0) {
				rsResult.first();
				jrEqpInfo   = rsResult.getRecord();
				szydEqpStat = jrEqpInfo.getFieldString("YD_EQP_PROG_STAT");          // 설비 상태
			}
			
			commUtils.printLog(logId, "스케줄취소시 설비가 offline, 고장이 아니고 대기 일때  명령 선택", "[info]");
			commUtils.printLog(logId,  szydEqpStat + sYD_EQP_WRK_MODE + szydEqpStat, "[info]");
			
			if (!"B".equals(szydEqpStat) 
					&& !YfConstant.YD_EQP_WRK_MODE_2_OFFLINE.equals(sYD_EQP_WRK_MODE) 
					&& "W".equals(szydEqpStat)) { 

				commUtils.printLog(logId, "명령선택 기동", "[info]");
				/**********************************************************
				* 스케줄취소시 명령선택
				**********************************************************/
				//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, "F1YFL007");

				jrYdMsg.setField("JMS_TC_CD"       , "F1YFL007");	//크레인작업지시요구
				jrYdMsg.setField("YD_EQP_ID"       , sYD_EQP_ID    );	//야드설비ID

				EJBConnector sndConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
				JDTORecord jrRtn1 = (JDTORecord)sndConn.trx("rcvF1YFL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				
				jrRtn = commUtils.addSndData(jrRtn,jrRtn1);
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}	// end of updCraneSchCancel	
	
	
	/**
	 * <pre>
	 *      [A] 오퍼레이션명 : 크레인작업요구현황조회 스케줄재전송
	 *       1. 크레인에 작업지시 전문을 다시 보낸다.
	 *        YFF1L004 : MSG_GP -> R
	 * <pre>
	 *		
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord reSndCrnSch(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업요구현황조회 스케줄재전송[ACoilJspBakSeEJB.reSndCrnSch] < " + gdReq.getNavigateValue();
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

				rsResult = commDao.select(jrParam, getYfCrnsch, logId, methodNm, "크레인스케쥴조회");
				
				if (rsResult.size() <= 0) {
					throw new YFUserException("해당 스케줄크레인스케줄 ID  정보: ["+ commUtils.getValue(gdReq, "YD_CRN_SCH_ID", i) + "] 가 존재하지않습니다");
				}
				else{
					// 스캐쥴지시
					if( !"Y".equals(rsResult.getRecord(0).getFieldString("RE_SCH_ABLE_YN")) ){
						throw new YFUserException("다른 스케쥴에 지시가 내려간 상태이므로 스케쥴 재전송을 할 수 없습니다.");
					}
				}
				/*****************************************************
				 **  크레인스케줄 재전송
				 *****************************************************/
				JDTORecord tcRecord = JDTORecordFactory.getInstance().create();
				tcRecord.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID);
				tcRecord.setField("MSG_GP"          , "R");

				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L004", tcRecord));
				
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}// reSndCrnSch

	/**
	 * 		<pre>
	 * 			[A] 오퍼레이션명 : 크레인백업처리
	 *             1. 설비상태변경
	 *               - rcvF1YFL004
	 *             2. 작업모드변경
	 *               - rcvF1YFL003
	 *             3. 응답백업
	 *               - 작업실적응답전송
	 *                : 권상
						jrParam.setField("YD_L2_WR_GP"     , "U"); //야드L2실적구분(지시요구)
						jrParam.setField("YD_L3_HD_RS_CD"  , "0000"); //야드L3처리결과코드(Error)                
	 *                : 권하
						jrParam.setField("YD_L2_WR_GP"     , "D"    ); //야드L2실적구분(지시요구)
						jrParam.setField("YD_L3_HD_RS_CD"  , "0000"); //야드L3처리결과코드(Error)
	 *               - YFF1L005 전문전송
	 *             4. 실적처리
	 *                - 권상 : rcvF1YFL008 (권상완료처리 YD_WRK_PROG_STAT:2, YD_EQP_WRK_MODE: 9(백업))
	 *                - 권하 : rcvF1YFL009 (권하완료처리 YD_WRK_PROG_STAT:4, YD_EQP_WRK_MODE: 9(백업))
	 *             5. 권하위치변경
	 *               * updCrnSchDnWoLoc 모듈 내용 확인
	 *                  0. 권하위치 검증
	 *                   ACoilSchSeJsp.procStockIdBaseCheckNew
				        1. 무인
				          : APP030의 기준값에 의한 상태체크로직
				           ( 크레인자동모드상태가 : 4, 크레인장비상태가 고장B)
				              일시정지이거나 고장상태가 아니면 권하위치를 변경할 수 없습니다
				          : APP030의 기준값에 의한 크레인작업지시요구 YFF1L004
				            - MSG_GP : U, YD_CRN_SCH_RMD_CNT : S5 (일시정지 후 권하위치 변경)
				            Return으로 빠져나감
				            
				          : APP030의 기준값에 N일 경우
				            - 크레인작업지시요구 Java Methed Call rcvF1YFL007 처리 
				              ( YD_WRK_PROG_STAT:  "2".equals(sYD_WRK_PROG_STAT) ? "5" : sYD_WRK_PROG_STAT )
				             Return으로 빠져나감 
				          
				        2. 유인
				          : 기존적치단 상태 : E적치가능으로 변경
				          : 신규적치단 활성화 ( YD_STK_LYR_ACTIVE_STAT : E, YD_STK_LYR_STAT : D(권하)
				          : 크레인스케줄 수정 - 권상, 권하지시위치
				          : 기존 대차, 차량 권하위치에서 일반야드로 변경 시 대차 or 차량스케줄 작업예약ID 삭제
				          : 크레인작업지시요구 Java Methed Call rcvF1YFL007 처리 

	 *             6. 명령선택기동	
	 *                -rcvF1YFL007
	 * 
	 * 		</pre> 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updbtCrnStsSetPp(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 상태 설정변경[ACoilJspBakSeEJB.updbtCrnStsSetPp] < " + gdReq.getNavigateValue();
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
			
			String sYD_CRN_ANSWER    = StringHelper.evl(commUtils.getValue(gdReq, "YD_CRN_ANSWER", 0), ""); //작업실적응답
			String sYD_SCH_CD        = StringHelper.evl(commUtils.getValue(gdReq, "YD_SCH_CD"    , 0), ""); 
			String sYD_CRN_SCH_ID    = commUtils.nvl(commUtils.getValue(gdReq, "YD_CRN_SCH_ID", 0), "");
			
			if ("".equals(ydEqpId)) {
				throw new YFUserException("크레인설비ID가 없습니다.");
			}
			
			jrParam.setField("YD_EQP_ID"    , ydEqpId); //야드설비ID
			jrParam.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID); // 크레인스케줄ID
			
			/********************************************
			 * 야드작업진행상태  JAVA단에서 한번 더 체크
			 *********************************************/
			JDTORecordSet rstCrnSch = commDao.select(jrParam, getYfCrnsch, logId, methodNm,  "야드작업진행상태 확인");
			String sYD_WRK_PROG_STAT = "";
			
			if (rstCrnSch == null || rstCrnSch.size() <= 0) {
				if ("WU".equals(trtDtlGp) || "WD".equals(trtDtlGp) || "DL".equals(trtDtlGp) || "XX".equals(trtDtlGp)) {
					throw new YFUserException("크레인스케줄이 없습니다.");
				}
			} else {
				sYD_WRK_PROG_STAT = rstCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT");
			}
			
			commUtils.printLog(logId, "야드작업진행상태 YD_WRK_PROG_STAT [ " + sYD_WRK_PROG_STAT + " ]", "[INFO]");
			
			
			if ("ST".equals(trtDtlGp)) {
				//설비상태 변경
				jrParam.setField("JMS_TC_CD"          , "F1YFL004"); //설비고장복구실적
				jrParam.setField("YD_EQP_STAT"        , commUtils.trim(gdReq.getParam("YD_EQP_STAT"))); //야드설비상태(B:고장, N:정상)
				jrParam.setField("YD_EQP_PAUSE_CODE"  , "0000"    ); //야드설비휴지코드
				jrParam.setField("YD_EQP_TRBL_RCVR_DT", currDate  ); //야드설비고장복구일시

				EJBConnector sndConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvF1YFL004", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
			} else if ("MD".equals(trtDtlGp)) {
				//작업Mode 변경
				jrParam.setField("JMS_TC_CD"      , "F1YFL003"); //설비운전모드전환
				jrParam.setField("YD_EQP_WRK_MODE", commUtils.trim(gdReq.getParam("YD_EQP_WRK_MODE"))); //야드설비작업Mode(1:On-Line, 0:Off-Line)

				EJBConnector sndConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvF1YFL003", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
			} else if ("WO".equals(trtDtlGp)) {
				//명령선택기동
				jrParam.setField("JMS_TC_CD"       , "F1YFL007"); //크레인작업지시요구
				jrParam.setField("YD_WRK_PROG_STAT", "W"       ); //야드작업진행상태(명령선택대기)
				jrParam.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD"     ).getValue(0))); //야드스케쥴코드
				jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID" ).getValue(0))); //야드크레인스케쥴ID

				EJBConnector sndConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvF1YFL007", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			} else if ("WU".equals(trtDtlGp)) {
				if (!"1".equals(sYD_WRK_PROG_STAT)) {
					throw new YFUserException("["+sYD_WRK_PROG_STAT+"]해당 작업진행상태는 권상지시 상태가 아닙니다.");
				}
				//권상실적처리
				jrParam.setField("JMS_TC_CD"       , "F1YFL008"); //크레인권상실적
				jrParam.setField("YD_EQP_WRK_MODE" , "9"       ); //야드설비작업Mode(Backup)
				jrParam.setField("YD_WRK_PROG_STAT", "2"       ); //야드작업진행상태(권상완료)
				jrParam.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD"         ).getValue(0))); //야드스케쥴코드
				jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"     ).getValue(0))); //야드크레인스케쥴ID
				jrParam.setField("YD_UP_WR_LOC"    , commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC"      ).getValue(0))); //야드권상실적위치
				jrParam.setField("YD_UP_WR_LYR"    , commUtils.trim(gdReq.getHeader("YD_UP_WO_LYR"      ).getValue(0))); //야드권상실적단
				jrParam.setField("YD_CRN_XAXIS"    , commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_XAXIS").getValue(0))); //야드크레인X축
				jrParam.setField("YD_CRN_YAXIS"    , commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_YAXIS").getValue(0))); //야드크레인Y축
				jrParam.setField("YD_CRN_ZAXIS"    , commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_ZAXIS").getValue(0))); //야드크레인Z축
				
				EJBConnector sndConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvF1YFL008", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			} else if ("WD".equals(trtDtlGp)) {
				if (!"2".equals(sYD_WRK_PROG_STAT)) {
					throw new YFUserException("["+sYD_WRK_PROG_STAT+"]해당 작업진행상태는 권상완료 상태가 아닙니다.");
				}
				//권하실적처리
				jrParam.setField("JMS_TC_CD"       , "F1YFL009"); //크레인권하실적
				jrParam.setField("YD_EQP_WRK_MODE" , "9"       ); //야드설비작업Mode(Backup)
				jrParam.setField("YD_WRK_PROG_STAT", "4"       ); //야드작업진행상태(권하완료)
				jrParam.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD"         ).getValue(0))); //야드스케쥴코드
				jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"     ).getValue(0))); //야드크레인스케쥴ID
				jrParam.setField("YD_DN_WR_LOC"    , commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC"      ).getValue(0))); //야드권하실적위치
				jrParam.setField("YD_DN_WR_LYR"    , commUtils.trim(gdReq.getHeader("YD_DN_WO_LYR"      ).getValue(0)));   //야드권하실적단
				jrParam.setField("YD_CRN_XAXIS"    , commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_XAXIS").getValue(0))); //야드크레인X축
				jrParam.setField("YD_CRN_YAXIS"    , commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_YAXIS").getValue(0))); //야드크레인Y축
				jrParam.setField("YD_CRN_ZAXIS"    , commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_ZAXIS").getValue(0))); //야드크레인Z축
				EJBConnector sndConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
				JDTORecord rst = (JDTORecord)sndConn.trx("rcvF1YFL009", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				jrRtn = commUtils.addSndData(jrRtn, rst);
				
			} else if ("DL".equals(trtDtlGp)) {
				if ("4".equals(sYD_WRK_PROG_STAT)) {
					throw new YFUserException("["+sYD_WRK_PROG_STAT+"]해당 작업진행상태는 권하위치변경을 할 수 없습니다.");
				}
				//권하위치변경
				jrParam.setField("YD_WRK_PROG_STAT", commUtils.trim(gdReq.getHeader("YD_WRK_PROG_STAT").getValue(0))); //야드작업진행상태
				jrParam.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD"       ).getValue(0))); //야드스케쥴코드
				jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"   ).getValue(0))); //야드크레인스케쥴ID
				jrParam.setField("YD_WBOOK_ID"     , commUtils.trim(gdReq.getHeader("YD_WBOOK_ID"     ).getValue(0))); //야드작업예약ID
				jrParam.setField("YD_DN_WO_LOC"    , commUtils.trim(gdReq.getParam("YD_DN_WO_LOC"))); //야드권하지시위치(신규)
				jrParam.setField("BIZ_GP"    	   , "3"); //1:주작업 2:더미 3:사용자 4:차공정 5:진도

				jrRtn = this.updCrnSchDnWoLoc(jrParam);
				
			} else if ("WM".equals(trtDtlGp)) {
				//운전모드 변경
				jrParam.setField("JMS_TC_CD"      , "F1YFL003"); //운전모드전환
				jrParam.setField("YD_EQP_WRK_MODE" , commUtils.trim(gdReq.getParam("YD_EQP_WRK_MODE"))); //야드설비작업Mode(1:On-Line, 2:Off-Line)
				jrParam.setField("YD_EQP_WRK_MODE2", commUtils.trim(gdReq.getParam("YD_EQP_WRK_MODE2"))); //

				EJBConnector sndConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvF1YFL003", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			} else if ("WR".equals(trtDtlGp)) {
				
				String sStlNo = commUtils.trim(gdReq.getParam("STL_NO"));
				if(!"".equals(sStlNo)){
					JDTORecord crSearchParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
					crSearchParam.setField("STL_NO", sStlNo);
					crSearchParam.setField("YD_EQP_ID"       , ydEqpId); //야드설비ID
					
					JDTORecordSet rstCrnSchByStlNo = commDao.select(crSearchParam, getYfCrnschByStlNo, logId, methodNm,  "재료번호의 가장 최근의 스케쥴을 조회한다.");
					if( rstCrnSchByStlNo == null || rstCrnSchByStlNo.size() < 1){
						throw new YFUserException("재료번호[" + sStlNo +"]에 해당하는 크레인스케쥴이 존재하지 않습니다.");
					}else{
						JDTORecord dto = rstCrnSchByStlNo.getRecord(0);
						sYD_CRN_SCH_ID = dto.getFieldString("YD_CRN_SCH_ID");
						sYD_SCH_CD = dto.getFieldString("YD_SCH_CD");
					} 
				}
				
				//작업실적응답
				if (YfConstant.CRN_WRK_RE_LD_WR.equals(sYD_CRN_ANSWER)) { //권상
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("JMS_TC_CD"       , "YFF1L005"); //작업실적응답
					jrParam.setField("YD_EQP_ID"       , ydEqpId); //야드설비ID
					jrParam.setField("YD_WRK_PROG_STAT", YfConstant.YD_EQP_STAT_UP_CMPL);
					jrParam.setField("YD_SCH_CD"       , sYD_SCH_CD);
					jrParam.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID);
					jrParam.setField("YD_L2_WR_GP"     , YfConstant.CRN_WRK_RE_LD_WR); //야드L2실적구분(지시요구)
					jrParam.setField("YD_L3_HD_RS_CD"  , YfConstant.CRN_WRK_RE_CD_NORMAL_HD); //야드L3처리결과코드(Error)
					jrParam.setField("YD_L3_MSG"       , "작업실적응답" ); //야드L3MESSAGE

					jrRtn = commUtils.addSndData(jrRtn, comm.getYFF1L005(jrParam));
				} else {
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("JMS_TC_CD"       , "YFF1L005"); //작업실적응답
					jrParam.setField("YD_EQP_ID"       , ydEqpId); //야드설비ID
					jrParam.setField("YD_WRK_PROG_STAT", YfConstant.YD_EQP_STAT_DN_CMPL);
					jrParam.setField("YD_SCH_CD"       , sYD_SCH_CD);
					jrParam.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID);
					jrParam.setField("YD_L2_WR_GP"     , YfConstant.CRN_WRK_RE_DN_WR    ); //야드L2실적구분(지시요구)
					jrParam.setField("YD_L3_HD_RS_CD"  , YfConstant.CRN_WRK_RE_CD_NORMAL_HD); //야드L3처리결과코드(Error)
					jrParam.setField("YD_L3_MSG"       , "작업실적응답" ); //야드L3MESSAGE

					jrRtn = commUtils.addSndData(jrRtn, comm.getYFF1L005(jrParam));
				}
			} else if ("XX".equals(trtDtlGp)) {
				if (!"S".equals(sYD_WRK_PROG_STAT)) {
					throw new YFUserException("["+sYD_WRK_PROG_STAT+"]해당 작업진행상태는 응답대기 상태가 아닙니다.");
				}
				//응답 백업(개발용)
				jrParam.setField("MSG_ID"	        , "F1YFL015" );
				jrParam.setField("MSG_GP"		    , "I" );
				jrParam.setField("YD_EQP_ID"		, ydEqpId );
				jrParam.setField("YD_WRK_PROG_STAT"	, "1" );
				jrParam.setField("YD_SCH_CD"		, sYD_SCH_CD );
				jrParam.setField("YD_CRN_SCH_ID"	, sYD_CRN_SCH_ID );
				jrParam.setField("REQ_YN"	        , "Y" );
				
				EJBConnector ejbConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
				jrRtn = (JDTORecord)ejbConn.trx("rcvF1YFL015", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			} else {
				throw new YFUserException("정의되지 않은 처리구분[" + trtDtlGp + "] 입니다.");
			}
			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	/**
	 * 		<pre>
	 * 			[A] 오퍼레이션명 : 변경가능한 권하위치 조회
	 * 				1. 권하위치 찾기
	 *                - TO위치결정:사용자지정작업[ACoilSchBakSeEJB.procToLocUser]으로 대상위치 Get
	 * 
	 * 		</pre>
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData getDownLocChange(GridData gdReq) throws DAOException {
		String methodNm = "조회[ACoilJspBakSeEJB.getDownLocChange] < " + gdReq.getNavigateValue();
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
			
			JDTORecordSet jsCrnSch = commDao.select(jrParam, getYfCrnSchDel, logId, methodNm,  "크레인 스케쥴 read");
			
			if(jsCrnSch.size()> 0 ) {
				JDTORecord jrCrnSch = jsCrnSch.getRecord(0);
				EJBConnector ejbConn = new EJBConnector("default", "ACoilSchBakSeEJB", this);
				outRecSet = (JDTORecordSet)ejbConn.trx("procReSch"
																	, new Class[] { String.class, String.class, JDTORecord.class }
																	, new Object[] { logId, methodNm, jrCrnSch });

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
	} 
	
	/**
	 * 차량작업 포인트 현황-입동순서변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procCoilYdGdsBayInWoSeqChangCoil(GridData gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-입동순서변경[ACoilJspBakSeEJBSBean.procCoilYdGdsBayInWoSeqChangCoil] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String syd_car_sch_id ="";		
		String szYD_CAR_SCH_ID		= "";
		String szYD_BAYIN_WO_SEQ= "";
		
		int intRtnVal = 0;		

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
					
					if(!"".equals(syd_car_sch_id)){
						szYD_CAR_SCH_ID =  commUtils.getValue(gdReq, "YD_CAR_SCH_ID"+i, ii) ;
						szYD_BAYIN_WO_SEQ = commUtils.getValue(gdReq, "YD_BAYIN_WO_SEQ"+i, ii) ;
						jrParam.setField("YD_CAR_SCH_ID"	,szYD_CAR_SCH_ID );
						jrParam.setField("YD_BAYIN_WO_SEQ"	,szYD_BAYIN_WO_SEQ );

						intRtnVal=commDao.update(jrParam, updYdCarschYdBayinWoSeq, logId, methodNm, "박판열연차량스케줄수정");
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
	 *  차량작업 포인트 현황-포인트 사용 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecord procCoilYdGdsPntUnitCLCoil(GridData gdReq) throws DAOException
	{
		String methodNm					= "차량작업 포인트 현황-포인트 사용 등록[ACoilJspBakSeEJBSBean.procCoilYdGdsPntUnitCLCoil] < " + gdReq.getNavigateValue();
		String logId					= gdReq.getIPAddress();
		String szMsg					= null;
		String szYD_STK_COL_GP			= null;
		String szYD_STK_COL_ACT_STAT	= null;
 
		String szTRN_EQP_CD				= null;
		String szYD_STKBED_USG_CD_PARAM	= null;
		String szYD_STK_COL_ACT_STAT_PARAM	= null;		
 
		String szJMS_TC_CD				= null; 
		String szCAR_NO          		= "";
		String szCARD_NO          		= "";	
		String szYD_CAR_USE_GP          = "";
		String szYD_FRM_YN          = "";
		
		JDTORecord recInTemp = null;
		int       	intRtnVal    		= 0;		
		//JDTORecord recInTemp1 = null;
		boolean isSendable				= true;
		
		String szYD_GP					= "";
		String szTO_YD_STK_COL_GP		="";
		
		try
		{
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			JDTORecord recOutTemp = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecordSet rsStkCol = JDTORecordFactory.getInstance().createRecordSet("");	
			JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				szYD_STK_COL_GP 		    = commUtils.getValue(gdReq, "YD_STK_COL_GP", ii);
				szYD_STK_COL_ACT_STAT_PARAM	= commUtils.getValue(gdReq, "YD_STK_COL_ACT_STAT", ii);//사용구분
				szYD_STKBED_USG_CD_PARAM    = commUtils.getValue(gdReq, "YD_STKBED_USG_CD",ii);    //전용
				szTRN_EQP_CD			    = commUtils.getValue(gdReq, "TRN_EQP_CD", ii);         //차량번호
				szYD_FRM_YN                 = commUtils.getValue(gdReq, "YD_FRM_YN", ii);         //차량형상유무
    			recOutTemp = JDTORecordFactory.getInstance().create();
    			jrParam.setField("YD_STK_COL_GP"		, szYD_STK_COL_GP);
    			
    			szYD_GP = szYD_STK_COL_GP.substring(0 , 1);
    			
				rsStkCol = commDao.select(jrParam, getYfStkcol, logId, methodNm, "열연코일적치열 조회"); 
		    	
		    	if (rsStkCol == null || rsStkCol.size() <= 0) {
					szMsg="["+methodNm+"] 적치열 조회 getYfstackcol data not found";
					commUtils.printLog(logId, szMsg, "SL");	
					continue;
				}

		    	rsStkCol.absolute(1);
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsStkCol.getRecord());

				szYD_STK_COL_ACT_STAT 	    = commUtils.trim(recOutTemp.getFieldString("YD_STK_COL_ACT_STAT"));
				szCAR_NO 				    = commUtils.trim(recOutTemp.getFieldString("CAR_NO"));
				szCARD_NO 				    = commUtils.trim(recOutTemp.getFieldString("CARD_NO"));
				szYD_CAR_USE_GP 			= commUtils.trim(recOutTemp.getFieldString("YD_CAR_USE_GP")); 
				if("".equals(szTRN_EQP_CD)){ 
					//포인트 차량이 존재 안하는 경우 
					szTRN_EQP_CD  	= "";
					szYD_CAR_USE_GP = "";
					szCAR_NO 		= "";
					szCARD_NO 		= "";
				}else{				
					if(("G".equals(szTRN_EQP_CD.substring(0 , 1))||"K".equals(szTRN_EQP_CD.substring(0 , 1))) && !"TT".equals(szYD_STKBED_USG_CD_PARAM)){			 
						szCAR_NO 		= "";
						szCARD_NO 		= "";
					}else {
						szTRN_EQP_CD  ="";
					}
				}
		    	
				jrParam.setField("YD_STK_COL_ACTIVE_STAT",	"L");
				jrParam.setField("YD_CAR_USE_GP"      , szYD_CAR_USE_GP);
				jrParam.setField("TRN_EQP_CD"         , szTRN_EQP_CD);
				jrParam.setField("CAR_NO"             , szCAR_NO);
				jrParam.setField("CARD_NO"            , szCARD_NO);
				jrParam.setField("YD_STK_COL_GP"      , szYD_STK_COL_GP);
					
				intRtnVal= commDao.update(jrParam, updYfStkcol2, logId, methodNm, "박판열연적치열수정");
					
				if (intRtnVal <= 0) {
					szMsg = "["+methodNm+"] 적치열 수정, ErrorCode:" + intRtnVal;
					commUtils.printLog(logId, szMsg, "SL");	
				}  
					
				jrParam.setField("YD_STK_COL_ACT_STAT"	, szYD_STK_COL_ACT_STAT_PARAM);
				jrParam.setField("YD_STKBED_USG_CD"		, szYD_STKBED_USG_CD_PARAM);
				jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD);
				jrParam.setField("YD_CAR_USE_GP"		, szYD_CAR_USE_GP);
				jrParam.setField("CAR_NO"				, szCAR_NO);
				jrParam.setField("CARD_NO"				, szCARD_NO);
	    		jrParam.setField("YD_STK_COL_GP"		, szYD_STK_COL_GP);
	    		jrParam.setField("YD_FRM_YN"			, szYD_FRM_YN);		
				intRtnVal=commDao.update(jrParam, updydcarpoint, logId, methodNm, "차량포인트수정");
					
				if (intRtnVal <= 0) {
					szMsg = "["+methodNm+"] 차량포인트 수정, ErrorCode:" + intRtnVal;
					commUtils.printLog(logId, szMsg, "SL");	
				}  
    	    	
				//사용불가 인경우 대기차량을 다른 포인트로 변경 한다. 2020.01.04
				if("N".equals(szYD_STK_COL_ACT_STAT_PARAM) && "1".equals(szYD_GP))
				{
					if("1".equals(szYD_STK_COL_GP.substring(5 , 6)) || "2".equals(szYD_STK_COL_GP.substring(5 , 6)))
					{
						//1통로 인경우
						if("1".equals(szYD_STK_COL_GP.substring(5 , 6)))
						{
							szTO_YD_STK_COL_GP = szYD_STK_COL_GP.substring(0 , 5) + "2";
						}
						else
						{
							szTO_YD_STK_COL_GP = szYD_STK_COL_GP.substring(0 , 5) + "1";
						}
					}
					else if("3".equals(szYD_STK_COL_GP.substring(5 , 6)) || "4".equals(szYD_STK_COL_GP.substring(5 , 6)))
					{
						//2통로 인경우
						if("4".equals(szYD_STK_COL_GP.substring(5 , 6)))
						{ 
							szTO_YD_STK_COL_GP = szYD_STK_COL_GP.substring(0 , 5) + "3";
						}
						else
						{
							szTO_YD_STK_COL_GP = szYD_STK_COL_GP.substring(0 , 5) + "4";
						}
					}
					else if("5".equals(szYD_STK_COL_GP.substring(5 , 6)) || "6".equals(szYD_STK_COL_GP.substring(5 , 6)))
					{
						//3통로 인경우? 박판COIL H동은  가운데 통로 하나 더 있음...5,6
						if("5".equals(szYD_STK_COL_GP.substring(5 , 6)))
						{ 
							szTO_YD_STK_COL_GP = szYD_STK_COL_GP.substring(0 , 5) + "6";
						}
						else
						{
							szTO_YD_STK_COL_GP = szYD_STK_COL_GP.substring(0 , 5) + "5";
						}
					}
					
					recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YD_STK_COL_GP",		szYD_STK_COL_GP);		//FROM 차량위치
			    	recInTemp.setField("TO_YD_STK_COL_GP",	szTO_YD_STK_COL_GP);	//TO 차량위치
		 
			    	intRtnVal = commDao.update(recInTemp, CarschUpdatePoint);
					
					szMsg= "차량포인트 변경 완료 :" +intRtnVal ;
					commUtils.putLog(szSessionName, methodNm, szMsg, YfConstant.INFO);		
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
				
		    	
				if ("C".equals(szYD_STK_COL_ACT_STAT_PARAM) 
						|| "L".equals(szYD_STK_COL_ACT_STAT_PARAM)
						|| "R".equals(szYD_STK_COL_ACT_STAT_PARAM)){
					if ( "N".equals(szYD_STK_COL_ACT_STAT) ) {			//사용불가
						szMsg = " 적치열["+szYD_STK_COL_GP+"] - 변경된 야드적치열활성상태["+szYD_STK_COL_ACT_STAT+"]에 대한 포인트 OPEN 처리 전문 송신 ";
						commUtils.printLog(logId, szMsg, "SL");	
						
						recInTemp.setField("PNT_UNIT_CL_GP",	"Y");
					}else{
						isSendable = false;
					}
				}else if ("N".equals (szYD_STK_COL_ACT_STAT_PARAM)){
					
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
					recInTemp.setField("MSG_GP"			, "I"                         ); //전문구분
					recInTemp.setField("YD_GP", szYD_STK_COL_GP.substring(0, 1));
					recInTemp.setField("YD_INFO_SYNC_CD", "3");						//1:동,2:SPAN,3:열,4:BED
					recInTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
					recInTemp.setField("YD_STK_BED_NO"  , "");
					
					//전송 Data 생성
 
					sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YFF1L001", recInTemp));
					szMsg="["+methodNm+"] 포인트 개패시 시 저장위치 제원 야드L2로 전송";
					commUtils.printLog(logId, szMsg, "SL");			    		
		    	}
			}

			szMsg="[구내내운송 소재차량Point개폐 전송  성공";
			commUtils.printLog(logId, szMsg, "SL");
	    	
	    	szMsg = "["+methodNm+"] YD_STK_COL_GP["+szYD_STK_COL_GP+"]의 진행상태["+szYD_STK_COL_ACT_STAT+"] 변경처리함";
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
		String methodNm = "차량입동위치변경[ACoilJspBakSeEJBSBean.changeCarLoc] < " + gdReq.getNavigateValue();
		JDTORecord 		jrRtn  		= null;
		
		String logId = gdReq.getIPAddress();
		
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("CAR_NO"      			, gdReq.getParam("CAR_NO"));
			jrParam.setField("YD_STK_COL_GP"      	, gdReq.getParam("YD_STK_COL_GP"));
				
			commDao.update(jrParam, updCarStopLoc, logId, methodNm, "차량입동위치 수정");
	    	
			jrParam.setField("CAR_NO"      			, gdReq.getParam("CAR_NO"));
			JDTORecordSet jsCarSch = commDao.select(jrParam, getYfCarInfoByCarNo, logId, methodNm,  "차량 스케쥴 read");
			
			if(jsCarSch.size()>0){
				JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setResultCode(logId);	//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				recInTemp.setField("JMS_TC_CD"			, "YFYFJ662");          //차량입동지시 요구 기존:YDYDJ662
				recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
				recInTemp.setField("YD_CARPNT_CD"		, jsCarSch.getRecord(0).getFieldString("YD_CARPNT_CD"));		//입동포인트
				recInTemp.setField("YD_CAR_SCH_ID"		, jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID"));	//차량스케줄ID
				
				EJBConnector ejbConn9 = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);	
				JDTORecord jrRtn9 = (JDTORecord)ejbConn9.trx("rcvYFYFJ662", new Class[] { JDTORecord.class }, new Object[] { recInTemp });		
				jrRtn = commUtils.addSndData(jrRtn, jrRtn9);
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
	 * "차량작업 포인트 현황-출하차량도착
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew" 
	*/
	public JDTORecord CarArrivalNEW(GridData gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-출하차량도착[ACoilJspBakSeEJBSBean.CarArrivalNEW] < " + gdReq.getNavigateValue();
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
				
				for(int i=1;i<=20;i++){
					szYD_CAR_SCH_ID 		= commUtils.getValue(gdReq, "YD_CAR_SCH_ID"+i, ii);
					if(!"".equals(szYD_CAR_SCH_ID)){
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setResultCode(logId);	//Log ID
						recInTemp.setResultMsg(methodNm);	//Log Method Name
						recInTemp.setField("JMS_TC_CD"			, "YFYFJ662");          //차량입동지시 요구 기존:YDYDJ662
						recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
						recInTemp.setField("YD_CARPNT_CD"		, szYD_CARPNT_CD);		//입동포인트
						recInTemp.setField("YD_CAR_SCH_ID"		, szYD_CAR_SCH_ID);	//차량스케줄ID
						
						EJBConnector ejbConn9 = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);	
						JDTORecord jrRtn9 = (JDTORecord)ejbConn9.trx("rcvYFYFJ662", new Class[] { JDTORecord.class }, new Object[] { recInTemp });		
						jrRtn = commUtils.addSndData(jrRtn, jrRtn9);	
						
						szMsg="[" + methodNm + "] 차량입동포인트[" + szYD_CARPNT_CD + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 성공";
						commUtils.printLog(logId, szMsg, "SL");							
					}
				}
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
		String methodNm			= "updCarStart";
		String logId 			= gdReq.getIPAddress();
		JDTORecord jrRst 		= JDTORecordFactory.getInstance().create();
		EJBConnector ejbConn 	= null;
 
		JDTORecord inRecord2  	= JDTORecordFactory.getInstance().create();	
		try{
			 
			
			szMsg = "["+methodNm+"] 배차내역 출발처리  전송처리 시작  ==>";
			commUtils.printLog(logId, szMsg, "SL");	
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {				
		    	JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		    	String sydcarschid =commUtils.getValue(gdReq, "YD_CAR_SCH_ID", ii);
		    	jrParam.setField("YD_CAR_SCH_ID", sydcarschid);
				JDTORecordSet loadCarSch = commDao.select(jrParam, getYdCarsch, logId, methodNm, "TB_YD_CARSCH 조회");
				if(loadCarSch.size() <= 0 ){
					szMsg="["+methodNm+"]  TB_YD_CARSCH >YD_CAR_SCH_ID"+"["+sydcarschid+"] SELECT Error ::  DO NOT EXIST"  ;
					commUtils.printLog(logId, szMsg, "SL");	
					//return gdRes ;
				}else{
					
					inRecord2 	= JDTORecordFactory.getInstance().create();
					inRecord2.setResultCode(logId);	//Log ID
					inRecord2.setResultMsg(methodNm);	//Log Method Name
					inRecord2.setField("TRANS_ORD_DATE"		, commUtils.getValue(gdReq, "TRANS_ORD_DT", ii));
					inRecord2.setField("TRANS_ORD_SEQNO"	, commUtils.getValue(gdReq, "TRANS_ORD_SEQNO", ii));
					inRecord2.setField("CAR_NO"				, commUtils.getValue(gdReq, "CAR_NO", ii));
					inRecord2.setField("CARD_NO"			, commUtils.getValue(gdReq, "CARD_NO", ii));
					inRecord2.setField("SPOS_WLOC_CD"		, commUtils.getValue(gdReq, "ARR_WLOC_CD", ii));
					inRecord2.setField("SPOS_YD_PNT_CD"		, commUtils.getValue(gdReq, "YD_PNT_CD", ii));
					inRecord2.setField("YD_CARPNT_CD"		, commUtils.getValue(gdReq, "YD_CARPNT_CD", ii));
					
					ejbConn = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
					ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });
				}				
			}
			
			szMsg = "["+methodNm+"]  배차내역 출발처리  전송처리 끝 ===>";
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
	 *  제품이송우선순위 변경 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */	
	public JDTORecord getCoilCarMovYn(GridData gdReq) throws DAOException {
		 
		String methodNm			= "차량Point작업현황 - 제품이송우선순위 변경 [ACoilJspBakSeEJB.updCarStart]";
		JDTORecord recEditColumn	= JDTORecordFactory.getInstance().create();
		JDTORecord jrRtn 			= JDTORecordFactory.getInstance().create();
		YdPlateCommDAO	commDao 		= new YdPlateCommDAO();
		
		String logId = gdReq.getIPAddress();
		String szLogMsg = ""; 

		int intRtnVal 	= 0;
		
		try {
			szLogMsg = "JSP-FACADE [ " + methodNm +"] 시작";
			commUtils.printLog(logId, szLogMsg, "SL");	
			
			String szMOV_SUP_YN = gdReq.getParam("MOV_SUP_YN").toUpperCase();
			String szYD_GP = gdReq.getParam("YD_GP");
	 
			recEditColumn.setField("MOV_SUP_YN",szMOV_SUP_YN);
			recEditColumn.setField("YD_GP" , szYD_GP);
			
			/*
			 * 2020. 01. 23 관련정보를 YF에서 관리하지 않아 부득이하게 쿼리 표현함
			 * -- com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updCoilCarMovYnReg
			 *		UPDATE USRYDA.TB_YD_RULE
					SET ITEM1 =:V_MOV_SUP_YN
					 , MOD_DDTT=SYSDATE 
					WHERE REPR_CD_GP='J00005'
					  AND CD_GP=:V_YD_GP
					  AND ITEM='*'
			 * 
			 */
			intRtnVal = commDao.update(recEditColumn, "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updCoilCarMovYnReg");
			if (intRtnVal < 1) {
				
				szLogMsg = "제품이송우선순위UPDATE 실패! ErrorCode:" + intRtnVal;
	 

				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", szLogMsg);
				return jrRtn;
			}
 
			
			jrRtn.setField("RTN_CD" , "1");	
			jrRtn.setField("RTN_MSG", "정상적으로 등록하였습니다.");	
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} //getCoilCarMovYn
	
	/**
	 * 출고검수등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData jrecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updateCarExamination(JDTORecord jrecord) throws DAOException {
		String methodNm = "출고검수등록[ACoilJspBakSeEJB.updateCarExamination] < " + jrecord.getResultMsg();
		String logId = jrecord.getResultCode();
		//Return Value
		JDTORecord jrRtn = null;
		try {	
			int 					count 		= 0;
			//JDTORecord 			jrecrd	 	= JDTORecordFactory.getInstance().create();
			JDTORecord 				tcRecordDM 	=JDTORecordFactory.getInstance().create(); 
			String 					cnt			= "";
			String 					chk			= "";
			String 					trans_ord_no = "";
			String 					sTRANS_ORD_DATE = "";
			String 					sTRANS_ORD_SEQNO = "";
			String					szTRANS_EQUIPMENT_TYPE= "";
			String					szMsg = "";
			String					msgId = "";
	 
			//JDTORecord recPara = null;
			String szCR_FRTOMOVE_GP = "";
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(jrecord.getFieldString("userid")));

				//운송지시 번호 단위로 검수 완료 처리 작업
				trans_ord_no=commUtils.trim(jrecord.getFieldString("TRANS_ORD_NO"));

				// 2020. 01. 30 이송지시번호가 없는 Case는 확인해야 할 듯
				if("".equals(trans_ord_no)){
					
					//count += dao.updateCoilloadLotRankingJip(queryID, inData);
					jrParam.setField("YD_CAR_UPP_LOC_CD"	,jrecord.getFieldString("YD_CAR_UPP_LOC_CD")); // 차상위
					jrParam.setField("YD_AB_CD"	            ,jrecord.getFieldString("EXAM_CD"));
					jrParam.setField("YD_AB_CD2"	        ,jrecord.getFieldString("EXAM_CD2") );
					jrParam.setField("LABEL_YN"	            ,jrecord.getFieldString("LABEL_CD"));
					jrParam.setField("TRANS_ORD_DATE"	    ,jrecord.getFieldString("TRANS_ORD_DATE"));
					jrParam.setField("TRANS_ORD_SEQNO"	    ,jrecord.getFieldString("TRANS_ORD_SEQNO"));
					jrParam.setField("STL_NO"	            ,jrecord.getFieldString("STL_NO"));
					//jrParam.setField("MODIFIER"			,commUtils.trim(jrecord.getFieldString("userid")));
					
					//검수 완료 처리
					count=commDao.update(jrParam, updateCarExaminationGoodsDetjlNEW2, logId, methodNm, "박판열연검수완료처리");
				 
					//검수 완료 TC 전송 가능 유무 체크 
					sTRANS_ORD_DATE =jrecord.getFieldString("TRANS_WORD_NO");
					sTRANS_ORD_SEQNO =jrecord.getFieldString("TRANS_WORD_SEQNO");
					
					return jrRtn;
				
				
				}else{
					//검수 완료 TC 전송 가능 유무 체크 
					String[] OrdArr = null;
					OrdArr = trans_ord_no.split("-");
					sTRANS_ORD_DATE =OrdArr[0];
					sTRANS_ORD_SEQNO =OrdArr[1];
					
					jrParam.setField("TRANS_ORD_DATE"	    ,sTRANS_ORD_DATE);
					jrParam.setField("TRANS_ORD_SEQNO"	    ,sTRANS_ORD_SEQNO);
					jrParam.setField("YD_CAR_UPP_LOC_CD"	,jrecord.getFieldString("YD_CAR_UPP_LOC_CD")); // 차상위
					jrParam.setField("LABEL_YN"	            ,jrecord.getFieldString("LABEL_YN"));
					jrParam.setField("STL_NO"	    		,jrecord.getFieldString("STL_NO"));
					jrParam.setField("YD_AB_CD"	    		,jrecord.getFieldString("YD_AB_CD"));
					jrParam.setField("YD_AB_CD2"	    	,jrecord.getFieldString("YD_AB_CD2"));
					
					//운송지시 단위 검수 완료 처리
					count=commDao.update(jrParam, updateCarExaminationGoodsDetjl2, logId, methodNm, "박판열연검수완료처리");
	 
				}
				JDTORecordSet jsCarExamin = commDao.select(jrParam, CarExaminationChk, logId, methodNm, "차량스케쥴 조회");
				
				if(jsCarExamin.size()>0){
					//jrecrd = (JDTORecord)StockList.get(0);
					cnt =  commUtils.trim(jsCarExamin.getRecord(0).getFieldString("CNT")) ; 
					chk =  commUtils.trim(jsCarExamin.getRecord(0).getFieldString("CHK")) ; 
					szTRANS_EQUIPMENT_TYPE = commUtils.trim(jsCarExamin.getRecord(0).getFieldString("TRANS_EQUIPMENT_TYPE")) ;
					
					if("0".equals(chk) && !"0".equals(cnt) ){
						
						szMsg = "=============검수완료 전문 전송 시작 ========";
						commUtils.printLog(logId, szMsg, "SL");
						// 레코드생성-----------------------------------------------------------------
						//rsResult = JDTORecordFactory.getInstance().createRecordSet("");
						//recPara = JDTORecordFactory.getInstance().create();
						//recPara.setField("TRANS_ORD_DATE",  sTRANS_ORD_DATE);
						//recPara.setField("TRANS_ORD_SEQNO", sTRANS_ORD_SEQNO);
						//recPara.setField("CAR_NO", commUtils.trim(jsCarExamin.getRecord(0).getFieldString("CAR_NO")));
						
						//PDA출하 인경우 
						if("P".equals(szTRANS_EQUIPMENT_TYPE)){
							tcRecordDM.setField("JMS_TC_CD"             , new String("YDDMR074"));
							msgId = "YDDMR074";
						}else{
							tcRecordDM.setField("JMS_TC_CD"             , new String("YDDMR036"));
							msgId = "YDDMR036";
						}
						tcRecordDM.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14());
						tcRecordDM.setField("TRANS_WORD_DATE"		, sTRANS_ORD_DATE);
	                    tcRecordDM.setField("TRANS_WORD_SEQNO"		, sTRANS_ORD_SEQNO);
 	                    tcRecordDM.setField("CARLD_CHK_DONE_DATE"	, YfCommUtils.getCurDate("yyyyMMdd"));
	                    tcRecordDM.setField("CARLD_CHK_DONE_TIME"	, YfCommUtils.getCurDate("HHmmss"));	
	                    tcRecordDM.setField("CAR_NO", commUtils.trim(jsCarExamin.getRecord(0).getFieldString("CAR_NO")));
						//검수완료 TC대상 조회
				    	JDTORecordSet rst = commDao.select(jrParam, CarExaminationTCListNEW, logId, methodNm, "검수완료 TC대상 조회");
						
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
						}
						
						if(!"".equals(szCR_FRTOMOVE_GP)){
							tcRecordDM.setField("CR_FRTOMOVE_GP"           , szCR_FRTOMOVE_GP);
						} else {
							tcRecordDM.setField("CR_FRTOMOVE_GP"           , "");
						}
						
//						tcRecordDM.setField("TRANS_ORD_DATE", sTRANS_ORD_DATE);
//	                    tcRecordDM.setField("TRANS_ORD_SEQNO", sTRANS_ORD_SEQNO);
	         			//인터페이스 전문 호출
						
					   //EJBConnector ejbConn1 = new EJBConnector("default","YfCommDAO",this);
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
	                    count=commDao.update(jrParam, updateCarExaminationGoodsEnd, logId, methodNm, "박판열연검수완료처리");
 
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
	 *      [A] 오퍼레이션명 : YF-RULE 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord modiYfRule(GridData gdReq) throws DAOException {
		String methodNm = "YF-RULE 수정[ACoilJspBakSeEJB.modiYfRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("DTL_ITEM1" , commUtils.trim(gdReq.getParam("DTL_ITEM1" )));
			jrParam.setField("DTL_ITEM2" , commUtils.trim(gdReq.getParam("DTL_ITEM2" )));
			jrParam.setField("DTL_ITEM3" , commUtils.trim(gdReq.getParam("DTL_ITEM3" )));
			jrParam.setField("DTL_ITEM4" , commUtils.trim(gdReq.getParam("DTL_ITEM4" )));
			jrParam.setField("DTL_ITEM5" , commUtils.trim(gdReq.getParam("DTL_ITEM5" )));
			jrParam.setField("DTL_ITEM6" , commUtils.trim(gdReq.getParam("DTL_ITEM6" )));
			jrParam.setField("DTL_ITEM7" , commUtils.trim(gdReq.getParam("DTL_ITEM7" )));
			jrParam.setField("DTL_ITEM8" , commUtils.trim(gdReq.getParam("DTL_ITEM8" )));
			jrParam.setField("DTL_ITEM9" , commUtils.trim(gdReq.getParam("DTL_ITEM9" )));
			jrParam.setField("DTL_ITEM10", commUtils.trim(gdReq.getParam("DTL_ITEM10")));
			
			jrParam.setField("REPR_CD_GP", commUtils.trim(gdReq.getParam("REPR_CD_GP")));
			jrParam.setField("CD_GP"     , commUtils.trim(gdReq.getParam("CD_GP")));
			jrParam.setField("ITEM"      , commUtils.trim(gdReq.getParam("ITEM")));
			
			commDao.update(jrParam, updYfRule, logId, methodNm, "YF_RULE수정");
			

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	/**
	 *  하차작업등록 (반품,회송,부분하차)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */	
	public JDTORecord regCarUdWrk(GridData gdReq) throws DAOException {
		
		String methodNm = "하차작업등록[ACoilJspBakSeEJB.regCarUdWrk]s < " + gdReq.getNavigateValue();
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
			
			String sRETN_TRGT_YN = ""; //반품회송대상 여부 (Y : 대상)
			
			/******************************************************
			 * 화면으로 부터 전달 받은 정보로 중복 등록 불가 체크
			 ******************************************************/
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				
				szStlNo       = commUtils.trim(gdReq.getHeader("STL_NO"    ).getValue(ii));
				sRETN_TRGT_YN = commUtils.trim(gdReq.getHeader("RETN_TRGT_YN").getValue(ii));
			
				// 전달받은 재료번호와 차량 번호로 TB_YD_CARSCH 조회
				recTemp = JDTORecordFactory.getInstance().create();
				recTemp.setField("CAR_NO",         szCAR_NO);
				recTemp.setField("STL_NO",         szStlNo);
			    rsResult = commDao.select(recTemp, getYdCarYdCarMtlDEL_YN, logId, methodNm, "차량스케줄조회");
			    
			    if (rsResult.size() > 0) {
			    	szMsg="["+methodNm+"] 해당재료로 등록된 차량 스케줄이 있습니다. ";
					return jrRtn;
							
			    }
			}
			
			//CARD_NO가 NULL인경우 차량번호 뒤에 4자리로 셋팅
			if("".equals(szCARD_NO) && szCAR_NO.length() >= 4)
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
			rsResult = commDao.select(recParam, getYdCarPoint, logId, methodNm, "차량포인트조회");
			
			if (rsResult.size() < 1) {
				szMsg="["+methodNm+"] 차량포인트 조회 실패!! - intRtnVal : " + intRtnVal;
				throw new YFUserException(szMsg);
			}
			
			rsResult.first();
			recTemp	= rsResult.getRecord();
			
			szWLOC_CD    		= StringHelper.evl(recTemp.getFieldString("WLOC_CD"), "");
			szYD_STK_COL_GP    	= StringHelper.evl(recTemp.getFieldString("YD_STK_COL_GP"), "");		
			szYD_PNT_CD	    	= StringHelper.evl(recTemp.getFieldString("YD_PNT_CD"), "");	
			
			//------------------------------------------------------------------
			//운송지시일자, 순번 생성  (999001 처럼 앞에  999를 붙인다.)
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			rsResult = commDao.select(recParam, getRetnTransOrdNo, logId, methodNm, "운송지시 일자 생성");
			
			if (rsResult.size() < 1) {
				szMsg="["+methodNm+"] 운송지시일자,순번  생성시  오류발생 실패!! - intRtnVal : " + intRtnVal;
				throw new YFUserException(szMsg);
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
			jrParam.setField("TRANS_ORD_DATE" 	, szTRANS_ORD_DATE);
			jrParam.setField("TRANS_ORD_SEQNO" 	, szTRANS_ORD_SEQNO);
			jrParam.setField("CAR_CARD_NO" 		, szCARD_NO);
			jrParam.setField("CAR_NO" 			, szCAR_NO);
//			jrParam.setField("CR_FRTOMOVE_GP" 	, transFrtoMoveGp);
			jrParam.setField("YD_CARPNT_CD"		, szYD_CARPNT_CD);
			jrParam.setField("TRANS_WORD_NO" 	, szTRANS_ORD_SEQNO+szTRANS_ORD_SEQNO);
			String StockId  = "";
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				StockId 		= commUtils.trim(gdReq.getHeader("STL_NO"     ).getValue(ii));
				szYD_STK_BED_NO = commUtils.trim(gdReq.getHeader("YD_STK_BED_NO").getValue(ii));
				szMsgContents 	= commUtils.trim(gdReq.getHeader("MSG_CONTENTS" ).getValue(ii));
				szCoilWt 		= commUtils.trim(gdReq.getHeader("COIL_WT"      ).getValue(ii));

				if ("".equals(StockId)) {
					break;
				}
				jrParam.setField("STL_NO",				StockId);
				jrParam.setField("YD_CAR_UPP_LOC_CD",	szYD_STK_BED_NO); // 차상위치
				jrParam.setField("SNBK_WT",				szCoilWt);        // 반송중량
				jrParam.setField("YD_ABMTL_REM",		szMsgContents);   // 반품 메시지

	    		commDao.update(jrParam, insStockTransInfoSNBK, logId, methodNm, "TB_YF_STOCK 등록");
			}
			
		    //--------------------------------------------------------------------------
			//2. 차량스케줄 생성
			String ydCarSchId = commDao.getSeqId(logId, methodNm, "CarSch");
    		JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID"		, ydCarSchId);
			recInTemp.setField("REGISTER"			, szUser);
			recInTemp.setField("YD_EQP_ID"			, YfConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
			recInTemp.setField("YD_CAR_USE_GP"		, YfConstant.YD_CAR_USE_GP_DM);			//차량사용구분 
			recInTemp.setField("CAR_NO"				, szCAR_NO);							//차량번호
			recInTemp.setField("CAR_KIND"			, szCAR_KIND);							//차량종류
			recInTemp.setField("YD_EQP_WRK_STAT"	, "L");									//야드설비작업상태
			recInTemp.setField("SPOS_WLOC_CD"		, szWLOC_CD);							//발지개소코드
			recInTemp.setField("ARR_WLOC_CD"		, szWLOC_CD);							//착지개소코드
			recInTemp.setField("YD_CARUD_LEV_DT"	, commUtils.getDateTime14());			//하차출발일시
			recInTemp.setField("YD_PNT_CD3"			, szYD_PNT_CD);							//야드포인트코드3
			recInTemp.setField("YD_CARUD_STOP_LOC"	, szYD_STK_COL_GP);						//야드하차차정지위치 
			recInTemp.setField("CARD_NO"			, szCARD_NO);							//카드번호
			recInTemp.setField("YD_CAR_PROG_STAT"	, "A");									//하차출발상태
			recInTemp.setField("TRANS_ORD_DATE"		, szTRANS_ORD_DATE);					//운송지시일자
			recInTemp.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);					//운송지시순번 
			recInTemp.setField("YD_BAYIN_WO_SEQ"	, YfConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 - 기본값으로 설정(9)
			recInTemp.setField("YD_CAR_WRK_GP"		, szRETN_WK_GP);
			if ("TT".equals(szCAR_KIND)) {
				recInTemp.setField("CAR_KIND",          "TT");								//차량종류
			} else {
				recInTemp.setField("CAR_KIND",          "TR");								//차량종류
			}				
    		//차량스케줄 등록
			commDao.insert(recInTemp, insYdCarsch, logId, methodNm, "TB_YD_CARSCH 등록");
			
		    //--------------------------------------------------------------------------
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create(); 
			//3.차량스케줄재료 생성
			for (int ii = 0; ii < rowCnt; ii++) {
				szStlNo       = commUtils.trim(gdReq.getHeader("STL_NO"    ).getValue(ii));
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
				commDao.insert(recInTemp, insCarFtMvMtl, logId, methodNm, "차량재료 스케쥴 INSERT ");	
				
				if ("Y".equals(sRETN_TRGT_YN)){
					sndL2Msg = JDTORecordFactory.getInstance().create();
					sndL2Msg.setField("TC_CD"          , "YFF1L002");
					sndL2Msg.setField("MSG_GP"         , "I");
					sndL2Msg.setField("YD_INFO_SYNC_CD", "R");
					sndL2Msg.setField("STL_NO"       , szStlNo);
					
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L002", sndL2Msg));	 //전송 Data 생성	
				}
			}
			
			//입동지시요구모듈 호출(trailer인 경우)
			if ("T".equals(szCAR_KIND) || "TR".equals(szCAR_KIND)) {
				/*
				 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
				 */
				recInTemp = JDTORecordFactory.getInstance().create();			 
				recInTemp.setResultCode(logId);	//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				recInTemp.setField("JMS_TC_CD"			, "YFYFJ662");          //차량입동지시 요구 기존:YDYDJ662
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
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} //regCarUdWrk
	
	/**
	 * 차량예정정보 전송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regCarUdExplainInfo(GridData gdReq) throws DAOException {
		String methodNm = "차량예정정보 전송[ACoilJspBakSeEJB.regCarUdExplainInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

	    String szMsg = "";
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("PT_LOAD_LOC"		, gdReq.getParam("YD_CARPNT_CD"));
			JDTORecordSet rsResult = commDao.select(jrParam, getYdGetCarNoByLoc, logId, methodNm, "차량위치조회"); 
			
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
				jrParam.setField("STL_NO_"+ii			,commUtils.getValue(gdReq, "YD_STL_NO"			, ii)); //재료번호
				jrParam.setField("LOAD_LOC_CD_"+ii		,commUtils.getValue(gdReq, "YD_CAR_UPP_LOC_CD"	, ii)); //차량적재위치
				jrParam.setField("WORK_STATE_"+ii		,commUtils.getValue(gdReq, "YD_WORK_STATE"		, ii)); //작업상태
			}
			
			//차량예정정보 백업 송신
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L008BackUp", jrParam));
			
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
		String methodNm = "크레인작업예약관리 스케줄기동[ACoilJspBakSeEJB.procCrnWrkBookStart] < " + gdReq.getNavigateValue();
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

			jrParam.setField("JMS_TC_CD", "YFYFJ303"); //야드작업예약ID
			jrParam.setField("JMS_TC_CREATE_DDTT", currDate); //JMSTC생성일시
			jrParam.setField("YD_SCH_CD"  , ""); //야드스케쥴코드
			jrParam.setField("YD_EQP_ID"  , ""); //야드설비ID

			for (int ii = 0; ii < rowCnt; ii++) {
				jrParam.setField("YD_WBOOK_ID"+(++wrkBookCnt), commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii))); //야드작업예약ID
			}
			
			jrParam.setField("SCH_CNT" , Integer.toString(wrkBookCnt)); //작업예약 개수
			
			//크레인스케줄기동 전문
			EJBConnector sndConn = new EJBConnector("default", "ACoilSchBakSeEJB", this);
			jrRtn = (JDTORecord)sndConn.trx("rcvYFYFJ303", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
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

				jrParam.setField("YD_SCH_AUTO_ST_YN", "Y"); // 'Y' 스케줄 자동기동
				jrParam.setField("YD_SCH_CD"        , sYD_SCH_CD);
				commDao.update(jrParam, updSchAutoStYn, logId, methodNm, "TB_YF_SCHEDULERULE >> YD_SCH_AUTO_ST_YN");
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
	 * 	<pre>
	 *     [A] 오퍼레이션명 : 작업예약관리-삭제
	 *      1. 작업예약 삭제
	 *      2. 작업예약 삭제 후 다음 작업재료가 있을 경우 크레인스케쥴 생성
	 *         TB_YF_WRKBOOK.SCH_CNCL_YN : "Y" 인것들이 없다라는 전재
	 *  </pre>
	 *      
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord delWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "작업예약관리-삭제[ACoilJspBakSeEJB.delWrkBook] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecord jrParam = null;
				
			String ydWbookId  = ""; //야드작업예약ID
			String ydEqpId    = ""; //야드설비ID
		    String ydSchCd    = ""; //야드스케쥴코드
			String schCnclYn = "";
			
			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			// 스케쥴 코드 
			java.util.Map aMap = new java.util.Hashtable(); 
			int nYD_SCH_AUTO_ST_YN = 0;
			for (int i = 0; i < rowCnt; i++) {
				//DAO Parameter - Log ID, Method, 수정자 Set
				jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
				
				ydWbookId = commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(i));
				ydEqpId   = commUtils.trim(gdReq.getHeader("YD_WRK_CRN"  ).getValue(i));
			    ydSchCd   = commUtils.trim(gdReq.getHeader("YD_SCH_CD"  ).getValue(i));
			    schCnclYn   = commUtils.trim(gdReq.getHeader("SCH_CNCL_YN"  ).getValue(i));
			    
				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
				jrParam.setField("YD_EQP_ID"    , ydEqpId   );
				jrParam.setField("YD_SCH_CD"    , ydSchCd   );
				
				this.trtWrkBookCncl(jrParam);
				if("Y".equals(schCnclYn)){
					if(!aMap.containsValue(ydSchCd)){
						aMap.put("YD_SCH_CD"+nYD_SCH_AUTO_ST_YN, ydSchCd);	
						nYD_SCH_AUTO_ST_YN++;
					}					
				}
			}
			
			
			// 취소처리 이후 
			// 더이상 크레인취소건이 없다면 작업예약을 정상적으로 재기동 처리해줘야 한다.
			jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("YD_SCH_CD"    , "");
			String sYD_SCH_CD_AUTO_ST_Y = "";
			for( int i = 0; i < nYD_SCH_AUTO_ST_YN; i++){
				if(i > 0){
					sYD_SCH_CD_AUTO_ST_Y += ",";
				}
				
				sYD_SCH_CD_AUTO_ST_Y += (String)aMap.get("YD_SCH_CD"+i);
			}
			if(!"".equals(sYD_SCH_CD_AUTO_ST_Y)){
				jrParam.setField("YD_SCH_CD"    , sYD_SCH_CD_AUTO_ST_Y);
				jrParam.setField("YD_SCH_AUTO_ST_YN", "Y"); // 'Y' 스케줄 자동기동
				commDao.update(jrParam, updSchAutoStYn2, logId, methodNm, "스케줄 자동기동처리 Y TB_YF_SCHEDULERULE >> YD_SCH_AUTO_ST_YN");
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
	 * CTS작업
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procYfCTSModify(GridData gdReq) throws DAOException {
		String methodNm = "CTS작업[ACoilJspBakSeEJB.procYfCTSModify] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String YD_STK_COL_GP = "";
		String STL_NO = "";
		String YD_EQP_ID = "";
		String sCtsRelaySaddle = "";
		JDTORecord jrRtn = null;
		
//		CraneSchDAO crnSchDao	= new CraneSchDAO();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
			
			String pFlag = gdReq.getParam("pFlag");
			
//			if("1".equals(pFlag) || "2".equals(pFlag)){
//				if("1".equals(pFlag)){
//					methodNm = "CTS작업 - 용도변경[ACoilJspBakSeEJB.procYfCTSModify] < " + gdReq.getNavigateValue();
//					commUtils.printLog(logId, methodNm, "S+");
//					
//					//수정할 레코드 수
//					int rowCnt = gdReq.getHeader("CHECK").getRowCount();
//					
//					for (int i = 0; i < rowCnt; i++) {
//						YD_STK_COL_GP = commUtils.getValue(gdReq,"YD_EQP_ID"			,i);
//						
//						jrParam.setField("YD_STK_COL_USAGE_CD"	,commUtils.getValue(gdReq,"YD_STK_COL_USAGE_CD"	,i)); 
//						jrParam.setField("YD_STK_COL_GP"		,YD_STK_COL_GP); 
//						
//						commDao.update(jrParam, updateStackColUsageInfo, logId, methodNm, "TB_YF_STKCOL 변경");
//						
//						commDao.delete(jrParam, deleteLocSearchInfo, logId, methodNm, "TB_YF_SCHLOCSRCH 삭제");
//					}
//				}else 
			if( YfConstant.CTS_CHG_SADDLE_STATUS_2.equals(pFlag)){
					methodNm = "CTS작업 - 상태변경[ACoilJspBakSeEJB.procYfCTSModify] < " + gdReq.getNavigateValue();
					commUtils.printLog(logId, methodNm, "S+");
					
					//수정할 레코드 수
					int rowCnt = gdReq.getHeader("CHECK").getRowCount();
					
					for (int i = 0; i < rowCnt; i++) {
						YD_STK_COL_GP = commUtils.getValue(gdReq,"YD_EQP_ID"			,i);
						
						jrParam.setField("YD_STK_LYR_ACTIVE_STAT"	,commUtils.getValue(gdReq,"YD_STK_LYR_ACTIVE_STAT"	,i)); 
						jrParam.setField("YD_EQP_STAT"				,commUtils.getValue(gdReq,"YD_STK_LYR_ACTIVE_STAT"	,i)); 
						jrParam.setField("YD_STK_COL_GP"			,commUtils.getValue(gdReq,"YD_EQP_ID"				,i)); 
						jrParam.setField("YD_EQP_ID"				,commUtils.getValue(gdReq,"YD_EQP_ID"				,i)); 
						jrParam.setField("YD_STK_BED_NO"			,"01"); 
						jrParam.setField("YD_STK_LYR_NO"			,"01");
						
						commDao.update(jrParam, updateCraneStackLayerActivStat, logId, methodNm, "TB_YF_STKLYR 적치상태 변경");
						commDao.update(jrParam, UpdateEquipStatInfo, logId, methodNm, "TB_YF_EQP 설비상태 변경");
					} 
				
				/**
				 * Level2  전문전송
				 *  - YFF1L001 (저장품제원위치)
				 */
				if(!"".equals(YD_STK_COL_GP)){
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setField("TC_CD"          , "YFF1L001");
					jrYdMsg.setField("MSG_GP"         , "I");
					jrYdMsg.setField("YD_INFO_SYNC_CD", "4");
					jrYdMsg.setField("YD_STK_COL_GP"   , YD_STK_COL_GP);
					jrYdMsg.setField("YD_STK_BED_NO"   , "01");
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L001", jrYdMsg));		
				}	
			}else if(YfConstant.CTS_CHG_AIM_DONG_3.equals(pFlag)){
				methodNm = "CTS작업 - 목적동 변경(신버전)[ACoilJspBakSeEJB.procYfCTSModify] < " + gdReq.getNavigateValue();
				commUtils.printLog(logId, methodNm, "S+");
				
				//수정할 레코드 수
				int rowCnt = gdReq.getHeader("CHECK").getRowCount();
				JDTORecordSet ctsRecordSet = null;
				
				for (int i1 = 0; i1 < rowCnt; i1++) {
					YD_EQP_ID = commUtils.getValue(gdReq,"YD_EQP_ID"		,i1);
					
					/* STOCK의 SADDLE 변경
					jrParam.setField("CTS_RELAY_SADDLE"	,commUtils.getValue(gdReq,"CTS_RELAY_SADDLE"	,i)); 
					jrParam.setField("STL_NO"			,commUtils.getValue(gdReq,"STL_NO"	,i)); 
					
					commDao.update(jrParam, updateCarunLoadPutLoc, logId, methodNm, "STOCK 하차위치 변경");
					*/
						
					//크레인 스케줄 삭제
					jrParam.setField("STL_NO"			,commUtils.getValue(gdReq,"STL_NO"	,i1));
					jrRtn = commUtils.addSndData(jrRtn, comm.delCraneSchCTS(jrParam));	// CTS 대차 출발 지시
						
					//SADDLE 적치단 권하 예약위치 초기화
					commDao.update(jrParam, updStackLayerByStockId4, logId, methodNm, "SADDLE 적치단 권하 예약위치 초기화");
							
					//CTS 스케줄삭제
					commDao.update(jrParam, deleteCTSSch2, logId, methodNm, "STOCK 하차위치 변경");
						
					//CTS 하차지시위치 변경된 스케줄 등록
					jrParam.setField("YD_AIM_BAY_GP"	,commUtils.getValue(gdReq,"YD_AIM_BAY_GP"	,i1)); 
					jrParam.setField("STL_NO"			,commUtils.getValue(gdReq,"STL_NO"	,i1)); 
					commDao.update(jrParam, insCTSSchBackUp, logId, methodNm, "CTS_SCH 변경 된 값으로 신규등록");	
					
					jrParam.setField("STL_NO"					,commUtils.getValue(gdReq,"STL_NO"			,i1)); 
					ctsRecordSet = commDao.select(jrParam, selectCTSSch , logId, methodNm, "생성한 CTS_SCH 정보 조회");
					
					if(ctsRecordSet.size()>0){
						JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
						jrYdMsg.setField("YD_EQP_ID" , ctsRecordSet.getRecord(0).getFieldString("YD_EQP_ID")             ); // 설비ID
						comm.getCTSToLoc(jrYdMsg);
					}
				}
					

			}else if(YfConstant.CTS_CHG_WORK_ORDER_4.equals(pFlag)){
				methodNm = "CTS작업 - 작업지시[ACoilJspBakSeEJB.procYfCTSModify] < " + gdReq.getNavigateValue();
				commUtils.printLog(logId, methodNm, "S+");
				
				int rowCnt = gdReq.getHeader("CHECK").getRowCount();
				
				JDTORecordSet ctsRecordSet = null;
					
				for (int i1 = 0; i1 < rowCnt; i1++) {
					YD_EQP_ID = commUtils.getValue(gdReq,"YD_EQP_ID"		,i1);
					
					jrParam.setField("YD_EQP_ID"				,YD_EQP_ID); 
					jrParam.setField("YD_CTS_WRK_SEQ"			,commUtils.getValue(gdReq,"YD_CTS_WRK_SEQ"	,i1)); 
					jrParam.setField("STL_NO"					,commUtils.getValue(gdReq,"STL_NO"			,i1)); 
					
					ctsRecordSet = commDao.select(jrParam, selectCTSSchRank , logId, methodNm, "CTS 수정한 순위보다 상위 순위 조회");
					
					for(int i2=0;i2<ctsRecordSet.size();i2++){ //랭킹 갱신
						
						jrParam.setField("YD_CTS_WRK_SEQ",ctsRecordSet.getRecord(i2).getFieldString("YD_CTS_WRK_SEQ"));
						jrParam.setField("STL_NO",ctsRecordSet.getRecord(i2).getFieldString("STL_NO"));

						commDao.update(jrParam, updateCTSSchRank, logId, methodNm, "우선순위 수정");	
					}
					
					jrParam.setField("STL_NO"					,commUtils.getValue(gdReq,"STL_NO"			,i1)); 
					ctsRecordSet = commDao.select(jrParam, selectCTSSch , logId, methodNm, "생성한 CTS_SCH 정보 조회");
					
					if(ctsRecordSet.size()>0){
						JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
						jrYdMsg.setField("YD_EQP_ID" , ctsRecordSet.getRecord(0).getFieldString("YD_EQP_ID")             ); // 설비ID
						comm.getCTSToLoc(jrYdMsg);
					}
				}
			}else if(YfConstant.CTS_REQ_WORK_RESULT_6.equals(pFlag)){
				methodNm = "CTS작업 - 작업실적[ACoilJspBakSeEJB.procYfCTSModify] < " + gdReq.getNavigateValue();
				commUtils.printLog(logId, methodNm, "S+");
				
				int rowCnt = gdReq.getHeader("CHECK").getRowCount();
				String upDownGp = commonUtil.trim(gdReq.getParam("TC_UP_DN_GP"));
				String toLoc = "";
				String upLoc = "";
				String sYD_WRK_PROG_STAT = "";
				
				JDTORecordSet ctsRecordSet = null;
				
				
				for (int i = 0; i < rowCnt; i++) {
					STL_NO = commUtils.getValue(gdReq,"STL_NO"			,i);
					
					jrParam.setField("STL_NO"				,STL_NO); 
					ctsRecordSet = commDao.select(jrParam, selectCTSSch , logId, methodNm, "CTS장비정보조회");
					
					if(ctsRecordSet.size()>0){
						sYD_WRK_PROG_STAT = ctsRecordSet.getRecord(0).getFieldString("YD_WRK_PROG_STAT");
						
						// To 새들
						toLoc = ctsRecordSet.getRecord(0).getFieldString("YD_CARUD_WO_LOC");
						
						// From 새등
						upLoc = ctsRecordSet.getRecord(0).getFieldString("YD_CARLD_WO_LOC");
						
						// CTS작업실적(F1YFL043)
						jrParam.setField("JMS_TC_CD"	,"F1YFL043");
						jrParam.setField("STL_NO",STL_NO);
						jrParam.setField("YD_EQP_ID",ctsRecordSet.getRecord(0).getFieldString("YD_EQP_ID"));
						jrParam.setField("TC_UP_DN_GP",upDownGp);
						
						if("U".equals(upDownGp)){
							
							if("".equals(upLoc)) {
								throw new YFUserException("상차 지시정보[YD_CARUD_WO_LOC]가 존재하지 않습니다. CTS작업지시 내역을 확인하세요");
							}
							
							if( !"S".equals(sYD_WRK_PROG_STAT) ){
								throw new YFUserException("CTS작업진행 정보가 YD_WRK_PROG_STAT[작업지시:S]일 경우에만 가능합니다.");
							}
							
							jrParam.setField("YD_DN_WR_LOC"	,upLoc);						
						}else{
							
							if("".equals(toLoc)){
								throw new YFUserException("하차 지시정보[YD_CARLD_WO_LOC]가 존재하지 않습니다. CTS작업지시 내역을 확인하세요");
							}
							
							if(!"2".equals(sYD_WRK_PROG_STAT)){
								throw new YFUserException("상차가 완료된 재료에 대해서만 하차실적처리가 가능합니다.");
							}
							
							jrParam.setField("YD_DN_WR_LOC"	,toLoc);							
						}

						EJBConnector ejbCon = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
						jrRtn = (JDTORecord)ejbCon.trx("rcvF1YFL043", new Class[]{ JDTORecord.class }, new Object[]{ jrParam });
						jrRtn = commUtils.addSndData(jrRtn);	
					}else{
						 throw new YFUserException("해당 저장품과 관련된 CTS스케줄 정보가 존재하지 않습니다.");
					}
				}
			}else if(YfConstant.CTS_CHG_RESET_8.equals(pFlag)){
				methodNm = "CTS작업 - 설비초기화[ACoilJspBakSeEJB.procYfCTSModify] < " + gdReq.getNavigateValue();
				commUtils.printLog(logId, methodNm, "S+");
				
				jrRtn = commUtils.addSndData(comm.reCTSSchLevWo(jrParam));
			}
			
			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;
		} catch(DAOException e) {
			throw e;
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of procYfCTSModify
	
	/**
	 * 야드설비정비상태 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecordSet destinationSaddle(GridData gdReq) throws DAOException {
		String methodNm = "SADDLE 작업[ACoilJspBakSeEJB.destinationSaddle] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			String pStlNo = commUtils.trim(gdReq.getParam("STL_NO"));
			String pSaddle = commUtils.trim(gdReq.getParam("CTS_RELAY_SADDLE"));
			String pPos = pSaddle +  "0101";
			
			jrParam.setField("STL_NO"				, pStlNo);
			jrParam.setField("YD_STK_LYR_STAT"		, YfConstant.STACK_LAYER_STAT_C);
			jrParam.setField("YD_STK_COL_GP"		, pSaddle);
			jrParam.setField("CTS_RELAY_SADDLE"		, pSaddle);
			jrParam.setField("POS"					, pPos);
			
			commDao.update(jrParam, updateStockStatOfLayer1, logId, methodNm, "'적치단 상태' 항목 UPDATE");
			
			commDao.update(jrParam, updateRelayOfStock, logId, methodNm, "SADDLE 정보를 UPDATE");

			commDao.updateCoilCommonLocInfo(pStlNo, pPos); //코일저장위치 수정
			
			JDTORecordSet rsResult = commDao.select(jrParam, selectCoilInfo, logId, methodNm, "코일정보조회"); 
			
			if (rsResult != null || rsResult.size() > 0) {
				String sYdSchCd = "";
				String sKeepStlYn = "";
				String sStockItem = "";
				
	    		for(int i=0;i<rsResult.size();i++){
	    			sYdSchCd = rsResult.getRecord(i).getFieldString("YD_SCH_CD");
	    			sKeepStlYn = rsResult.getRecord(i).getFieldString("KEEP_STL_YN");
	    			sStockItem = rsResult.getRecord(i).getFieldString("STOCK_ITEM");
	    		}
	    		
	    		//COIL동간이적하차 작업예약
    			if(!("1DKE01UM".equals(sYdSchCd) || "1BFE01UM".equals(sYdSchCd))){
    				gdReq.addParam("KEEP_STL_YN", sKeepStlYn);
    				gdReq.addParam("STOCK_ITEM", sStockItem);
    				jrParam = createWBook(gdReq);
    			}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return rsResult;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of destinationSaddle
	
	/**
	 * CTS 목적동 SADDLE에 올려진 저장품에 대해 하차 작업을 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord createWBook(GridData gdReq) throws DAOException {
		String methodNm = "CTS 목적동 SADDLE 위에 저장품 하차[ACoilJspBakSeEJB.createWBook] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String sStlNo = gdReq.getParam("STL_NO");
			
			jrParam.setField("COIL_NO"		, sStlNo);
			JDTORecordSet rsResult = commDao.select(jrParam, getCoilCommonInfo2, logId, methodNm, "코일정보조회"); 
			
			String sYdZoneGp = "";
			String sYdGateGp = "";
			String sYdBayGp ="";
			String sSchCd ="";
			
	    	if (rsResult != null || rsResult.size() > 0) {
	    		for(int i=0;i<rsResult.size();i++){
	    			sYdZoneGp = rsResult.getRecord(i).getFieldString("YD_ZONE_GP");
	    			sYdGateGp = rsResult.getRecord(i).getFieldString("YD_GATE_GP");	
	    			sYdBayGp  = rsResult.getRecord(i).getFieldString("YD_BAY_GP");	
	    		}
	    		
	    		if("2".equals(sYdGateGp)){//CTS To(L)
	    				    			
	    			if(YfConstant.BAY_GP_A.equals(sYdBayGp)){
	    				sSchCd = "1ACT01LM";
	    			}else if(YfConstant.BAY_GP_B.equals(sYdBayGp)){
	    				sSchCd = "1BCT01LM";
	    			}else if(YfConstant.BAY_GP_C.equals(sYdBayGp)){
	    				sSchCd = "1CCT01LM";
	    			}else if(YfConstant.BAY_GP_D.equals(sYdBayGp)){
	    				sSchCd = "1DCT01LM";
	    			}else if(YfConstant.BAY_GP_E.equals(sYdBayGp)){
	    				sSchCd = "1ECT01LM";
	    			}else if(YfConstant.BAY_GP_F.equals(sYdBayGp)){
	    				sSchCd = "1FCT01LM";
	    			}else if(YfConstant.BAY_GP_G.equals(sYdBayGp)){
	    				sSchCd = "1GCT01LM";
	    			}else if(YfConstant.BAY_GP_H.equals(sYdBayGp)){
	    				sSchCd = "1HCT01LM";
	    			}
	    		}else {//CTS To(R)
	    			if(YfConstant.BAY_GP_B.equals(sYdBayGp)){
	    				sSchCd = "1BCT11LM";
	    			}else if(YfConstant.BAY_GP_C.equals(sYdBayGp)){
	    				sSchCd = "1CCT11LM";
	    			}else if(YfConstant.BAY_GP_E.equals(sYdBayGp)){
	    				sSchCd = "1ECT11LM";
	    			}else if(YfConstant.BAY_GP_F.equals(sYdBayGp)){
	    				sSchCd = "1FCT11LM";
	    			}else if(YfConstant.BAY_GP_G.equals(sYdBayGp)){
	    				sSchCd = "1GCT11LM";
	    			}else if(YfConstant.BAY_GP_H.equals(sYdBayGp)){
	    				sSchCd = "1HCT11LM";
	    			}
	    		}
	    		
	    		rsResult = commDao.select(jrParam, getWrkHistSeq, logId, methodNm, "YF_WRKHIST_SEQ PK조회");
	    		
	    		String sYdWbookId = "";
	    		if (rsResult != null || rsResult.size() > 0) {
		    		for(int i=0;i<rsResult.size();i++){
		    			sYdWbookId = rsResult.getRecord(i).getFieldString("YD_WBOOK_ID");
		    		}
		    		
		    		jrParam.setField("YD_WBOOK_ID"         ,sYdWbookId         );
			    	jrParam.setField("YD_GP"               ,sSchCd.substring(0, 1));
			    	jrParam.setField("YD_BAY_GP"           ,sSchCd.substring(1, 2));
			    	jrParam.setField("YD_SCH_CD"           ,sSchCd           );
			    	jrParam.setField("SCH_WORK_LOC_DECISION_METHOD"  ,"S"        );
			    	commDao.update(jrParam, MergeWrkBook, logId, methodNm, "TB_YF_WRKBOOK 등록");

			    	jrParam.setField("STL_NO"		, sStlNo);
			    	jrParam.setField("YD_STK_COL_GP", gdReq.getParam("CTS_RELAY_SADDLE"));
			    	jrParam.setField("YD_STK_BED_NO", "01");
			    	jrParam.setField("YD_STK_LYR_NO", "01");
			    	commDao.update(jrParam, MergeWrkBookMtl, logId, methodNm, "TB_YF_WRKBOOKMTL 등록");
			    	
			    	String[] unload = commUtils.getCoilCurrProgCd(sStlNo,"");
			    	String keepYn = gdReq.getParam("KEEP_STL_YN");
			    	String sStockMoveTerm = "";
			    	
			    	if(YfConstant.CURR_PROG_CD_COIL_M.equals(unload[0])|| YfConstant.CURR_PROG_CD_COIL_P.equals(unload[0])) {
			            if("Y".equals(keepYn)) {
			                if(YfConstant.ITEM_CG.equals(gdReq.getParam("STOCK_ITEM"))) {
			                	sStockMoveTerm = YfConstant.NEW_STOCK_MOVE_TERM_M2;
			                }else {
			                	sStockMoveTerm = YfConstant.NEW_STOCK_MOVE_TERM_M1;
			                }                
			            }
			        }else {
		            	sStockMoveTerm = unload[1];
		            }
			    	
			    	jrParam.setField("STOCK_MOVE_TERM", sStockMoveTerm);
			    	jrParam.setField("STL_NO"		, sStlNo);
			    	commDao.update(jrParam, updateWrkbookIdOfStock1, logId, methodNm, "TB_YF_STOCK - STOCK_MOVE_TERM 갱신");
		    	
			    	jrParam.setField("YD_STK_LYR_STAT", YfConstant.STACK_LAYER_STAT_S);
			    	jrParam.setField("YD_STK_COL_GP", gdReq.getParam("CTS_RELAY_SADDLE"));
			    	jrParam.setField("STL_NO"		, sStlNo);
			    	commDao.update(jrParam, updateLayerState1, logId, methodNm, "TB_YF_STKLYR - YD_STK_LYR_STAT 갱신");			    	
	    		
			    	EJBConnector ejbConn2 = new EJBConnector("default","JNDICraneSchReg",this);			
			 		Boolean isTrue2		  = (Boolean)ejbConn2.trx( "callCraneSchInfo",new  Class[]{String.class},new Object[]{sYdWbookId});
	    		}
	    	}
	    	
			commUtils.printLog(logId, methodNm, "S-");

			return jrParam;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updEqpTrblReg
	
	/**
	 *      [A] 오퍼레이션명 : 스크랩현황조회- 스크랩비우기
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procClearScrap(GridData gdReq) throws DAOException {
		String methodNm = "스크랩현황조회-스크랩비우기[ACoilJspBakSeEJB.procClearScrap] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String sSCRAP_CLEAR_GP = commUtils.trim(gdReq.getParam("SCRAP_CLEAR_GP")); //삭제할 열 %: 전체삭제
			String sCOL_NO         = commUtils.trim(gdReq.getParam("COL_NO"        )); //삭제할 열 %: 전체삭제
			String sAREA_GP        = commUtils.trim(gdReq.getParam("AREA_GP"       )); //삭제할 지역
			String sARR_STL_NO     = commUtils.trim(gdReq.getParam("ARR_STL_NO"  )); //재료번호들
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("ARR_STL_NO" , sARR_STL_NO); //재료번호들
			jrParam.setField("COL_NO"       , sCOL_NO      ); //열
			jrParam.setField("AREA_GP"      , sAREA_GP     ); //지역구분
			
			/*****************************************************
			 * 저장품제원 : 코일야드L2로 송신(YMA7L002)
			 ******************************************************/
			commUtils.printLog(logId, "YFF1L002 JMS전송", "[INFO]");
			
			JDTORecordSet arrStock = null;
			if ("COL".equals(sSCRAP_CLEAR_GP)) {
				arrStock = commDao.select(jrParam, getScrapStockByCol, logId, methodNm, "적치단의재료번호조회");
			} else {
				arrStock = commDao.select(jrParam, getStockListByArrList, logId, methodNm, "적치단의재료번호조회");
			}
			
			for (int idx = 0; idx < arrStock.size(); ++idx) {
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("TC_CD"          , "YFF1L002");
				jrYdMsg.setField("MSG_GP"         , "D"); 
				jrYdMsg.setField("YD_INFO_SYNC_CD", "5");
				jrYdMsg.setField("STL_NO"       , arrStock.getRecord(idx).getFieldString("STL_NO"));

//				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L002_SCRAP", jrYdMsg));
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L002_SCRAP", jrYdMsg));
			}
			
			/***************************
			 * 스크랩 비우기
			 ***************************/
			// 열단위 스크랩 삭제
			if ("COL".equals(sSCRAP_CLEAR_GP)) {
				commDao.update(jrParam, updClearScrapStockByCol, logId, methodNm, "열단위 스크랩 저장품내역삭제");
				commDao.update(jrParam, updClearScrapLyrByCol, logId, methodNm, "열단위 스크랩 적치단재료삭제");
			}
			
			
			// 코일 단위 스크랩 삭제
			if ("STOCK".equals(sSCRAP_CLEAR_GP)) {
				commDao.update(jrParam, updClearScrapLyrByArrStockId, logId, methodNm, "재료단위 스크랩 저장품내역삭제");
				commDao.update(jrParam, updClearScrapStockByArrStockId, logId, methodNm, "재료단위 스크랩 적치단재료삭제");
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
		String methodNm = "스크랩현황조회-스크랩생성[ACoilJspBakSeEJB.procCreateScrap] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String sARR_STL_NO   = commUtils.trim(gdReq.getParam("ARR_STL_NO"  )); //재료번호들
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("ARR_STL_NO" , sARR_STL_NO); //재료번호들
			
			/***************************
			 * 스크랩 생성
			 ***************************/
			 
			commDao.update(jrParam, insCrScrLyrByLoc, logId, methodNm, "TB_YF_STKLYR 스크랩 생성");
			 
			JDTORecordSet jsCrList = commDao.select(jrParam, getCrScrList, logId, methodNm, "YF - 생성스크랩 목록 조회");
			
			for (int ii = 0; ii < jsCrList.size(); ++ii) {
				 
				jrParam.setField("STL_NO"  , jsCrList.getRecord(ii).getFieldString("STL_NO"));
				commDao.update(jrParam, insertStockTransInfo, logId, methodNm, "TB_YF_STOCK 스크랩 생성");
			}
			
			/*****************************************************
			 * 저장품제원 : 코일야드L2로 송신(YMA7L002)
			 ******************************************************/
			commUtils.printLog(logId, "YFF1L002 JMS전송", "[INFO]");
			
			for (int idx = 0; idx < jsCrList.size(); ++idx) {
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("TC_CD"          , "YFF1L002");
				jrYdMsg.setField("MSG_GP"         , "I"); 
				jrYdMsg.setField("YD_INFO_SYNC_CD", "5");
				jrYdMsg.setField("STL_NO"       , jsCrList.getRecord(idx).getFieldString("STL_NO"));
				
//				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L002_SCRAP", jrYdMsg));
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L002_SCRAP", jrYdMsg));
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
	 *      [A] 오퍼레이션명 : 이적작업예약등록
	 *       - 이적방법
	 *        1. To위치 지정
	 *          : 사용자가 선택 한 위치에 적치
	 *          : 대차(CTS, 확장대차)는 사용자 선택
	 *            (From -> To의 동위치가 다를 경우에만 처리함)
	 *          : 스크랩은 동내이적으로 처리
	 *        2. 위치검색기준적용
	 *          : 소재
	 *            - 목적동을 선택 했을 경우 해당 동으로 이적처리
	 *            - 전체를 선택했을 경우 기준에 의한 목적동 배분처리
	 *          : 제품
	 *            - 목적동 선택했을 경우 해당 동으로 이적처리
	 *            - 전체를 선택했을 경우 각각 목적존으로 이동처리      
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updblMvStkWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "이적작업예약등록[ACoilJspBakSeEJB.updblMvStkWrkBook] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecordSet jsMsg = JDTORecordFactory.getInstance().createRecordSet("");	//크레인스케줄 정리를 위함
			
			/*****************************************************
			 * 1. 파라메터 정보 셋팅
			 ******************************************************/
			String stlNos        		= commUtils.trim(gdReq.getParam("ARR_STL_NO")); //재료번호들
			String sCHK_TO_LOC 			= commUtils.trim(gdReq.getParam("CHK_TO_LOC")); //이적방법(화면에서선택된 값) 
			String ydWrkPlanTcar 		= commUtils.trim(gdReq.getParam("YD_WRK_PLAN_TCAR")); //야드작업계획대차
			String ydWrkPlanCrn  		= commUtils.trim(gdReq.getParam("YD_WRK_PLAN_CRN" )); //야드지정크레인
			String sFrom_YD_STK_COL_GP  = commUtils.trim(gdReq.getParam("YD_STK_COL_GP"    ));
			String isScrapYn = "N";
			
			// 대차를 이용하는 재료 최대 3매 설정
			List aListTcarMove = new java.util.ArrayList();
			
			// 
			String sAIM_BAY_GP = commUtils.trim(gdReq.getParam("AIM_BAY_GP"));
			String sIS_GOODS_YN = commUtils.trim(gdReq.getParam("IS_GOODS_YN"));
			String sYD_ZONE_GP = commUtils.trim(gdReq.getParam("YD_ZONE_GP"));
			
			boolean bCTS_GP_1XTC03_CALL = false;
			
			String from_bayGp = sFrom_YD_STK_COL_GP.substring(1, 2);
			
			/*****************************************************
			 * 2. 스크랩팝업에서 호출되었는지 확인
			 ******************************************************/
			// 이적대상재가 스크랩인지 아닌지 확인 
			if("SCRAP".equals(sCHK_TO_LOC))
			{
				isScrapYn = "Y";
			}
			else
			{
				isScrapYn = "N";
			}

			if ("".equals(stlNos)) {
				throw new YFUserException("이적 재료번호가 없습니다.");
			}
			
			
			/******************************************************
			 * 3. 현재 전달받은 재료번호로 다시 DB에서 조회처리한다.
			 ******************************************************/
			JDTORecordSet jsWbMtl = null;
			
			jrParam.addField("SELECT_ZONE", sYD_ZONE_GP);
			jrParam.addField("SELECT_BAY_GP", sAIM_BAY_GP);
			jrParam.addField("ARR_STL_NO", stlNos);
			jrParam.addField("YD_STK_COL_GP", sFrom_YD_STK_COL_GP);
			
			if ("Y".equals(isScrapYn)) {
				jsWbMtl = commDao.select(jrParam, getblMvStkWrkBookMtlScPp, logId, methodNm, "스크랩 조회");				
			} else { 
				jsWbMtl = commDao.select(jrParam, getMvStkWrkBookMtl, logId, methodNm, "재료번호로 조회");//이전정렬
			}
			
			if( jsWbMtl == null || jsWbMtl.size() == 0 ){
				throw new YFUserException("이적대상재료["+stlNos+"]의 적치단 정보를 찾을 수 없습니다. DATA 0");
			}
			
			// 스케쥴코드 생성 및 
			/******************************************************
			 * 4. 이적처리( 작업예약생성 및 크레인스케쥴 호출 )
			 *  - RULE_BASE : 저장영역 OR 존별 검색순서 기준
			 *  - USER_BASE : 화면에서 TO위치 지정
			 *  - SCRAP : 스크랩이적(동내이적)
			 ******************************************************/
			if( "SCRAP".equals(sCHK_TO_LOC) ){
				
				String ydToLocGuide  = commUtils.trim(gdReq.getParam("YD_TO_LOC_GUIDE" )); //야드To위치Guide
				String ydAimBayGp = ydToLocGuide.substring(1, 2);
				String ydSchCd = "";
				
				// from과 To가 동일하면 동내이적으로 간주한다.
				ydSchCd = sFrom_YD_STK_COL_GP.substring(0, 2) + "YD01MM"; 
				
				// 작업예약 생성
				jrParam.setField("YD_SCH_CD"          , ydSchCd      ); //야드스케쥴코드
				jrParam.setField("YD_AIM_BAY_GP"      , ydAimBayGp   ); //야드목표동구분
				jrParam.setField("YD_TO_LOC_GUIDE"    , ydToLocGuide ); //야드To위치Guide
				jrParam.setField("YD_WRK_PLAN_TCAR"   , ""); //야드작업계획대차
				jrParam.setField("YD_WRK_PLAN_CRN"    , ""); //야드작업계획크레인
				jrParam.setField("YD_TO_LOC_GUIDE_FNL", ""); //야드To위치GuideFinal(A->E)
				
				//작업예약등록
				jsMsg.addRecord(this.insMvstkWrkBook(jrParam, jsWbMtl));
				
			}
			else if( "RULE_BASE".equals(sCHK_TO_LOC) ){
				
				// 소재이적이다.
				if( !"Y".equals(sIS_GOODS_YN) ){
					// 소재 및 제품 분기 실시
					JDTORecord nRow = null;
					String sNextProc = "", sCoilT = "", sCoilW = "", sStlNo = "", ydSchCd = "", targetByGp = "", sYdStkColGp = "";
					
					JDTORecordSet rSet = null;
					for(int i = 0 ; i < jsWbMtl.size() ; i++){
						nRow = jsWbMtl.getRecord(i);
						sNextProc  	= commUtils.nvl(nRow.getFieldString("NEXT_PROC"),"");
						sCoilT  	= commUtils.nvl(nRow.getFieldString("COIL_T"),"0");
						sCoilW  	= commUtils.nvl(nRow.getFieldString("COIL_W"),"0");
						sStlNo      = commUtils.nvl(nRow.getFieldString("STL_NO"), "");
						sYdStkColGp = commUtils.nvl(nRow.getFieldString("YD_STK_COL_GP"), "");
						// 만약 to위치가 전체일 경우 자동으로 목적동을 배분한다.
						// 이땐 무조건 CTS이다. 계획대차쪽에 CTS를 넣어주자
						if("".equals(sAIM_BAY_GP)){
							
							if("1K".equals(sNextProc)){
								targetByGp ="D";
								
							}else if("1H".equals(sNextProc)){
								targetByGp ="B";
								
							}else if("1Q".equals(sNextProc)){
								
								if( ("1.6".equals(sCoilT) && "1224".equals(sCoilW)) || ("2".equals(sCoilT) && "1530".equals(sCoilW)) ){
									targetByGp ="F";
								}else{
									targetByGp ="E";
								}
								
							}else {
								throw new YFUserException("목적동 찾기를 실패하였습니다. 재료번호["+sStlNo+"] __소재이적처리 -- 목적동전체");
							}
							
							// 목적동이 각각 다르기 때문에 작업예약을 각각 생성처리하자
							// 만약 화면에서 확장대차를 선택하였다면
							// CTS로 바꾸자
							if(YfConstant.CTS_GP_1XTC03.equals(ydWrkPlanTcar)){
								ydWrkPlanTcar  = YfConstant.CTS_1XTC01;
							}
							
							sAIM_BAY_GP = targetByGp;
							targetByGp = "";
						}
						else{
							
							if(!bCTS_GP_1XTC03_CALL && YfConstant.CTS_GP_1XTC03.equals(ydWrkPlanTcar)){
								bCTS_GP_1XTC03_CALL = true;
							}

						}
						
						ydSchCd = this.getMoveStockSchCode(sYdStkColGp, sAIM_BAY_GP, logId, methodNm);
						
						// 작업예약 생성
						jrParam.setField("YD_AIM_BAY_GP"      , sAIM_BAY_GP   ); //야드목표동구분
						jrParam.setField("YD_WRK_PLAN_TCAR"   , ydWrkPlanTcar); //CTS1번 대차로
						jrParam.setField("YD_SCH_CD"          , ydSchCd      ); //야드스케쥴코드
						jrParam.setField("YD_TO_LOC_GUIDE"    , "" ); //야드To위치Guide
						jrParam.setField("YD_TO_LOC_GUIDE_FNL", ""); //야드To위치GuideFinal(A->E)
						
						
						rSet = JDTORecordFactory.getInstance().createRecordSet("");
						rSet.addRecord(nRow);
						
						//크레인스케쥴 호출은 1번만
						if(jsMsg.size()<1)
							jsMsg.addRecord(this.insMvstkWrkBook(jrParam, rSet));
						else 
							this.insMvstkWrkBook(jrParam, rSet);
						
					}
					
				}
				// 제품의 경우 목적존을 구하자.
				else{
					
					String sTargetZone = "", sStlNo = "", ydSchCd = "", sTargetBayGp = "", sYdStkColGp = "";

					JDTORecordSet rSet = null;
					JDTORecord nRow = null;
					
					// 존을 전체로 했을 경우
					// 1. 목적존 찾기 
					// 2. 목적존의 목적동 찾기
					for(int i = 0 ; i < jsWbMtl.size() ; i++){
						
						nRow = jsWbMtl.getRecord(i);
						
						sTargetZone = commUtils.nvl(nRow.getFieldString("TARGET_ZONE"), "");
						sStlNo = commUtils.nvl(nRow.getFieldString("STL_NO"), "");
						sTargetBayGp = commUtils.nvl(nRow.getFieldString("TARGET_BAY_GP"), "");
						sYdStkColGp = commUtils.nvl(nRow.getFieldString("YD_STK_COL_GP"), "");
						
						if("".equals(sTargetBayGp)){
							throw new YFUserException("목적동을 찾을 수 없습니다.. 재료번호["+sStlNo+"] __제품이적처리 -- 존구분 전체");
						}

						ydSchCd = this.getMoveStockSchCode(sYdStkColGp, sTargetBayGp, logId, methodNm);
						
						// 현재위치와 To위치가 동일한 동일 경우 CTS는 사용하지 않음
						if(sTargetBayGp.equals(from_bayGp)){
							ydWrkPlanTcar = "";	
							commUtils.printLog(logId, methodNm, "[제품이적-목적동찾기-목적동과현재동이 동일]"
										+ "재료번호 :: " + sStlNo
										+ ", 목적존 :: " + sTargetZone 
										+ ", 현재동 :: " + from_bayGp 
										+ ", 목적동 :: " + sTargetBayGp, nRow);
						}
						else{

							// 계획대차가 없을 경우
							if("".equals(ydWrkPlanTcar)){
								ydWrkPlanTcar = YfConstant.CTS_1XTC01;
							}	
						}

						// 작업예약 생성
						jrParam.setField("YD_SCH_CD"          , ydSchCd      ); //야드스케쥴코드
						jrParam.setField("YD_AIM_BAY_GP"      , sTargetBayGp   ); //야드목표동구분
						jrParam.setField("YD_TO_LOC_GUIDE"    , "" ); //야드To위치Guide
						jrParam.setField("YD_WRK_PLAN_TCAR"   , ydWrkPlanTcar); //야드작업계획대차
						jrParam.setField("YD_WRK_PLAN_CRN"    , ydWrkPlanCrn ); //야드작업계획크레인
						jrParam.setField("YD_TO_LOC_GUIDE_FNL", ""); //야드To위치GuideFinal(A->E)
						
						// 작업예약등록
						rSet = JDTORecordFactory.getInstance().createRecordSet("");
						rSet.addRecord(nRow);
						//크레인스케쥴 호출은 1번만
						if(jsMsg.size()<1)
							jsMsg.addRecord(this.insMvstkWrkBook(jrParam, rSet));
						else
							this.insMvstkWrkBook(jrParam, rSet);

						// 저장품정보중 존정보 update 처리
						jrParam.setField("ARR_STL_NO", sStlNo ); //재료번호
						jrParam.setField("YD_ZONE_GP", sTargetZone ); //쿼리조회 목적존
						commDao.update(jrParam, updateStockZoneGp, logId, methodNm, "저장품정보(TB_YF_STOCK) 변경 : 야드존구분");
						
					}
				}
			}
			else if( "USER_BASE".equals(sCHK_TO_LOC) ){
				
				String ydToLocGuide  = commUtils.trim(gdReq.getParam("YD_TO_LOC_GUIDE" )); //야드To위치Guide
				String ydAimBayGp = ydToLocGuide.substring(1, 2);
				String ydSchCd = "", sYdStkColGp = "", sCallTcarWbookId="";
				
				
				int nTacrSTK_MAX_QNTY = 0; //대차 최대 적치수량
				int nTacrSTK_MAX_WT = 0; //  대차 최대 적치중량
				int nCOIL_W = 0;
				JDTORecordSet rSet = null;
				JDTORecord nRow = null;
				
				
				for(int i = 0 ; i < jsWbMtl.size() ; i++){
					
					nRow = jsWbMtl.getRecord(i);
					sYdStkColGp = commUtils.nvl(nRow.getFieldString("YD_STK_COL_GP"), "");
					nTacrSTK_MAX_QNTY = Integer.valueOf(commUtils.nvl(nRow.getFieldString("STK_MAX_QNTY"), "0")).intValue();
					nTacrSTK_MAX_WT = Integer.valueOf(commUtils.nvl(nRow.getFieldString("STK_MAX_WT"), "0")).intValue();
					nCOIL_W += Integer.valueOf(commUtils.nvl(nRow.getFieldString("COIL_W"), "0")).intValue();
					
					// from과 To가 동일하면 동내이적으로 간주한다.
					ydSchCd = this.getMoveStockSchCode(sYdStkColGp, ydAimBayGp, logId, methodNm);
					
					if(!bCTS_GP_1XTC03_CALL && YfConstant.CTS_GP_1XTC03.equals(ydWrkPlanTcar)){
						bCTS_GP_1XTC03_CALL = true;
					}
					
					// 작업예약 생성
					jrParam.setField("YD_SCH_CD"          , ydSchCd      ); //야드스케쥴코드
					jrParam.setField("YD_AIM_BAY_GP"      , ydAimBayGp   ); //야드목표동구분
					jrParam.setField("YD_TO_LOC_GUIDE"    , ydToLocGuide ); //야드To위치Guide
					jrParam.setField("YD_WRK_PLAN_TCAR"   , ydWrkPlanTcar); //야드작업계획대차
					jrParam.setField("YD_WRK_PLAN_CRN"    , ydWrkPlanCrn ); //야드작업계획크레인
					jrParam.setField("YD_TO_LOC_GUIDE_FNL", ""); //야드To위치GuideFinal(A->E)
					
					// 작업예약등록
					rSet = JDTORecordFactory.getInstance().createRecordSet("");
					rSet.addRecord(nRow);
					//크레인스케쥴 호출은 1번만
					if(jsMsg.size()<1){
						jsMsg.addRecord(this.insMvstkWrkBook(jrParam, rSet));
						sCallTcarWbookId = jsMsg.getRecord(0).getFieldString("YD_WBOOK_ID");
					}						
					else
					{
						sCallTcarWbookId = (this.insMvstkWrkBook(jrParam, rSet)).getFieldString("YD_WBOOK_ID");
					}
					 
					// 최대 3개만 넣어준다.
					// 최대 적치중량까지 확인하자!
//					if(bCTS_GP_1XTC03_CALL 
//							&& !( aListTcarMove.size() > nTacrSTK_MAX_QNTY  || nCOIL_W > nTacrSTK_MAX_WT ) // 최대적치수량보다 작거나, 중량미달이거나 둘 다 만족해야함
//							&& !"".equals(sCallTcarWbookId)
//					){
//						aListTcarMove.add(sCallTcarWbookId);	
//					}
				}
			}
			
			/**********************************************************
			* 00. 대차작업이 있으면 공대차출발지시 처리
			**********************************************************/
			
			commUtils.printLog(logId, methodNm, "[이적편성 완료 후 확장대차 공대차 출발지시 bCTS_GP_1XTC03_CALL :: ]" + bCTS_GP_1XTC03_CALL );
			/**
			 * 
			 */
			if (bCTS_GP_1XTC03_CALL) {
				//공대차출발지시 처리시 Exception을 발생시키지 않기위해 미리 Check
				String msgTcar = ""; //공대차출발지시 처리 메세지
			 			
				//대차스케쥴정보(공대차출발지시) 조회
				jrParam.setField("YD_EQP_ID", ydWrkPlanTcar);	//야드설비ID(대차)
				
				 
				JDTORecordSet jsChk = commDao.select(jrParam, getTcarSchLevWo, logId, methodNm, "공대차출발지시 조회");
				
				if (jsChk != null && jsChk.size() > 0) {
					JDTORecord jrChk = jsChk.getRecord(0);

					String ydTcarSchId   = commUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID"  ));
					String ydWbookIdCurr = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID_CURR"));

					if ("B".equals(commUtils.trim(jrChk.getFieldString("YD_EQP_PROG_STAT")))) {
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
				
				//공대차출발지시 처리 및 현재동에 대차가 있을 경우 크레인스케쥴 호출
//				if ("".equals(msgTcar)) {
					
					jrParam.setField("YD_EQP_ID", ydWrkPlanTcar); //야드설비ID(대차)
					jrParam.setField("YD_BAY_GP", from_bayGp      ); //야드동구분(상차동)

					commUtils.printLog(logId, methodNm, "[ 공대차 출발지시 ]" + ydWrkPlanTcar + " :: 야드동구분(상차동) " +  from_bayGp);
					
					//확장대차 작업예약 ID
					//동일 그룹처리
					jrParam.setField("ADD_MV_WBOOK", aListTcarMove);
					jrRtn = comm.trtTcarSchLevWo(jrParam);
					
//				} else {
//					commUtils.printLog(logId, "대차[" + ydWrkPlanTcar + "] 공대차출발지시 불가 : " + msgTcar, "SL");
//				}
				
				
			}
			
			/**********************************************************
			* 00. 크레인 스케줄 전송
			**********************************************************/
			if(jsMsg != null && jsMsg.size() > 0 ){
				commUtils.printLog(logId, methodNm, "[이적편성 완료 후 전문전송 Start]"
						+ "\n 전문전송 건 수:: " + jsMsg.size()
						+ "\n 전문전송 내역 :: " + jsMsg.toList().toString()
				);
				jrRtn = commUtils.addSndData(jrRtn, this.setCrnSchMsg(jsMsg, logId, methodNm));
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	

	/**
	 * <pre>
	 * 	[A] 오퍼레이션명 : 스케쥴 코드 조합
	 *      - 좌우구분 : YF_RULE의 SCH001의 LTRT_RULE의 기준에 의함
		    - 관련스케쥴 코드
				1AYD01MM	동내이적                  1ATC03UM	동간이적대차상차(L)
				1BYD01MM	동내이적(L)               1BTC03UM	동간이적대차상차(L)
				1BYD02MM	동내이적(R)               1BTC13UM	동간이적대차상차(R)
				1CYD01MM	동내이적(L)               1CTC03UM	동간이적대차상차(L)
				1CYD02MM	동내이적(R)               1CTC13UM	동간이적대차상차(R)
				1DYD01MM	동내이적                  1DTC03UM	동간이적대차상차(R)
				1EYD01MM	동내이적(L)               1ETC03UM	동간이적대차상차(L)
				1EYD02MM	동내이적(R)               1ETC13UM	동간이적대차상차(R)
				1FYD01MM	동내이적(L)               1FTC03UM	동간이적대차상차(L)
				1FYD02MM	동내이적(R)               1FTC13UM	동간이적대차상차(R)
				1GYD01MM	동내이적(L)               1GTC03UM	동간이적대차상차(L)
				1GYD02MM	동내이적(R)               1GTC13UM	동간이적대차상차(R)
				1HYD01MM	동내이적(L)               1HTC03UM	동간이적대차상차(L)
				1HYD02MM	동내이적(R)               1HTC13UM	동간이적대차상차(R)
			 ** 동내이적 :: YD01MM	
			 
	 * </pre>
	 * @param sStkYdSdkColGp
	 * @param ydAimBayGp
	 * @param sSECT_GP
	 * @param isScrap
	 * @param logId
	 * @param methodNm
	 * @return
	 * @throws Exception
	 */
	private String getMoveStockSchCode(String sFrom_STK_COL_GP, String sYD_AIM_BAY_GP, String logId, String methodNm) throws DAOException{
		
		methodNm += " > 이적작업 스케쥴 코드 생성 getMoveStockSchCode";
		commUtils.printLog(logId, methodNm, "SL+");
		
		String sSchCode = "";
		String ydBayGp    = sFrom_STK_COL_GP.substring(1, 2); //동구분
		String schDef     = sFrom_STK_COL_GP.substring(0, 2); //야드동구분
		String sSECT_GP  = sFrom_STK_COL_GP.substring(2, 4); //SPAN
		try{
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, "");
			jrParam.setField("REPR_CD_GP", "SCH001");
			jrParam.setField("CD_GP"     , "LTRT_RULE");
			jrParam.setField("ITEM"      , ydBayGp);
			JDTORecordSet rsResult = commDao.select(jrParam, getYfRule, logId, methodNm, "스케줄코드 좌우기준 조회");
			
			if (rsResult.size() <= 0 ) {
				throw new YFUserException("해당 동의 스케줄코드 좌우 구분이 없습니다.");
			}
			
			
			int nLtRtRule   = Integer.parseInt(rsResult.getRecord(0).getFieldString("DTL_ITEM1"));
			int nSECT_GP    = 0; //Integer.parseInt(sYD_STK_COL_GP.substring(2, 4));
			
			/*
			 *  Left, Right 특이사항 
			 *   - 대차일 경우  :: 1ATC -> 뒤에 오는 값(0,1)
			 *   - 동내이적일경우  ::  1AYD0 -> 뒤에 오는 값(0,1)
			 * 
			1AYD01MM	동내이적                  1ATC03UM	동간이적대차상차(L)
			1BYD01MM	동내이적(L)               1BTC03UM	동간이적대차상차(L)
			1BYD02MM	동내이적(R)               1BTC13UM	동간이적대차상차(R)
			1CYD01MM	동내이적(L)               1CTC03UM	동간이적대차상차(L)
			1CYD02MM	동내이적(R)               1CTC13UM	동간이적대차상차(R)
			1DYD01MM	동내이적                  1DTC03UM	동간이적대차상차(R)
			1EYD01MM	동내이적(L)               1ETC03UM	동간이적대차상차(L)
			1EYD02MM	동내이적(R)               1ETC13UM	동간이적대차상차(R)
			1FYD01MM	동내이적(L)               1FTC03UM	동간이적대차상차(L)
			1FYD02MM	동내이적(R)               1FTC13UM	동간이적대차상차(R)
			1GYD01MM	동내이적(L)               1GTC03UM	동간이적대차상차(L)
			1GYD02MM	동내이적(R)               1GTC13UM	동간이적대차상차(R)
			1HYD01MM	동내이적(L)               1HTC03UM	동간이적대차상차(L)
			1HYD02MM	동내이적(R)               1HTC13UM	동간이적대차상차(R)
			 */
			
			boolean bMoveSameBayGp = false;
			nSECT_GP = Integer.parseInt(sSECT_GP);
			
			// 야드와 설비가 달라서 배열선언
			String sCrPosition[] = {"1","0"}; // 배열 0번지 :: 야드 1:Left, 2: Right | 1번지 :: 대차 : 0(Left), 1(Right)
			
			// 야드좌우 기준 셋팅
			if (nSECT_GP < nLtRtRule) {
				sCrPosition[0] = "1";
				sCrPosition[1] = "0";
			} else if (nSECT_GP >= nLtRtRule) {
				sCrPosition[0] = "2";
				sCrPosition[1] = "1";
			}
			
			// 동내 동간이적 여부
			if (ydBayGp.equals(sYD_AIM_BAY_GP)) { 
				bMoveSameBayGp = true;
			}
			
			// 초기 코드 셋팅(1A ~ 1H )
			sSchCode += schDef;
			if(bMoveSameBayGp){
				// A, D동은 Left만 존재함.
				if(YfConstant.BAY_GP_A.equals(ydBayGp) || YfConstant.BAY_GP_D.equals(ydBayGp)){
					sSchCode += "YD01MM";
				}
				else{
					sSchCode += "YD0"+sCrPosition[0]+"MM";
				}
			}else{
				// A, D동은 Left만 존재함.
				if(YfConstant.BAY_GP_A.equals(ydBayGp) || YfConstant.BAY_GP_D.equals(ydBayGp)){
					sSchCode += "TC03UM";
				}
				else{
					sSchCode += "TC"+sCrPosition[1]+"3UM";
				}	
			}   
			
			commUtils.printLog(logId, methodNm, "S-");
			
			return sSchCode;
		} catch(DAOException e) {
			throw e;
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * 	<pre>
	 *      [A] 오퍼레이션명 : 이적작업예약등록
	 *        복수의 재료번호를 받아 작업예약을 생성한다.
	 *        1. 작업예약메인, 재료
	 *        
	 *      중요: 만약 재료 10건이 존재한다면 
	 *        첫번째만 전문발송대상으로 포함하여 Return처리한다.
	 *        (크레인스케쥴 호출대상을 첫번째 재료로)      
	 *      
	 * 	</pre>
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord insMvstkWrkBook(JDTORecord jrParam, JDTORecordSet jsWbMtl) throws DAOException {
		String methodNm = "이적작업예약등록[ACoilJspBakSeEJB.insMvstkWrkBook] < " + jrParam.getResultMsg();
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
 
			JDTORecordSet jsChk = commDao.select(jrParam, getStatSchCd, logId, methodNm, "야드스케쥴기준코드 Check");
			String sYD_WRK_CRN = "";
			if (jsChk.size() > 0) {
				sYD_WRK_CRN = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_WRK_CRN"));
			}
			
			if ("".equals(sYD_WRK_CRN)) {
				throw new YFUserException("스케쥴코드[" + ydSchCd + "] 작업가능 크레인 없음");
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
					throw new YFUserException("작업예약ID 생성 실패");
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

				 
				commDao.insert(jrParam, insWrkBook, logId, methodNm, "TB_YF_WRKBOOK");

				//작업예약재료 등록
				
				for (int jj = 0; jj < lotMtlSh; jj++) {
					jrRow = jsLotMtl.getRecord(jj);
					
					JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();
					
					jrRtn1.setField("YD_WBOOK_ID"   , ydWbookId     ); //야드작업예약ID
					jrRtn1.setField("STL_NO"      , commUtils.trim(jrRow.getFieldString("STL_NO"       )));	//재료번호
					jrRtn1.setField("YD_STK_COL_GP"  , commUtils.trim(jrRow.getFieldString("YD_STK_COL_GP")));	//야드적치열구분
					jrRtn1.setField("YD_STK_BED_NO"  , commUtils.trim(jrRow.getFieldString("YD_STK_BED_NO")));	//야드적치Bed번호
					jrRtn1.setField("YD_STK_LYR_NO", commUtils.trim(jrRow.getFieldString("YD_STK_LYR_NO")));	//야드적치단번호
					jrRtn1.setField("MODIFIER"     	, modifier      );												//등록자
					 
					commDao.insert(jrRtn1, insWrkBookMtl, logId, methodNm, "TB_YF_WRKBOOKMTL");
				}
			}
			
			/**********************************************************
			* 4. 크레인스케줄(YFYFJ302) 전송용 기초 전문 생성
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
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
//	/**	
//	 * 	<pre>
//	 *      [A] 오퍼레이션명 : 이적작업예약등록(스크랩)
//	 *       - 2020. 02. 10 사용하지 않음
//	 *        ( 일반 이적과 동일하게 사용함 )
//	 *        insMvstkWrkBook
//	 *  </pre>
//	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 *      @param JDTORecord rcvMsg
//	 *      @return JDTORecord
//	 *      @throws DAOException
//	*/
//	public JDTORecord insMvstkWrkBookScrap(JDTORecord jrParam, JDTORecordSet jsWbMtl) throws DAOException {
//		String methodNm = "스크랩 이적작업[ACoilJspBakSeEJB.insMvstkWrkBookScrap] < " + jrParam.getResultMsg();
//		String logId = jrParam.getResultCode();
//		JDTORecord jrRtn  = null;	//전문 Return
//
//		try {
//			commUtils.printLog(logId, methodNm, "S+");
//
//			String ydSchCd       = commUtils.trim(jrParam.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
//			String ydAimBayGp    = commUtils.trim(jrParam.getFieldString("YD_AIM_BAY_GP"   )); //야드목표동구분
//			String ydToLocGuide  = commUtils.trim(jrParam.getFieldString("YD_TO_LOC_GUIDE" )); //야드To위치Guide
//			String modifier      = commUtils.trim(jrParam.getFieldString("MODIFIER"        )); //수정자
//			String ydWrkPlanCrn  = commUtils.trim(jrParam.getFieldString("YD_WRK_PLAN_CRN" )); //야드작업계획대차
//			
//			//DAO Parameter - Log ID, Method, 수정자 Set
//			JDTORecord jrYFYFJ303 = commUtils.getParam(logId, methodNm, modifier);
//			int wrkBookCnt = 0;
//			
//			/**********************************************************
//			* 1. 야드스케쥴코드 Check
//			**********************************************************/
//
//			JDTORecordSet jsChk = commDao.select(jrParam, getStatSchCd, logId, methodNm, "야드스케쥴기준코드 Check");
//			String sYD_WRK_CRN = "";
//			if (jsChk.size() > 0) {
//				sYD_WRK_CRN = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_WRK_CRN"));
//			}
//			
//			if ("".equals(sYD_WRK_CRN)) {
//				throw new YFUserException("스케쥴코드[" + ydSchCd + "] 작업가능 크레인 없음");
//			}
//			
//			JDTORecord jrCrnSpec = jsChk.getRecord(0);
//			
//			String ydGp       = ydSchCd.substring(0, 1);	//야드구분
//			String ydBayGp    = ydSchCd.substring(1, 2);	//야드동구분
//			String ydEqpId    = commUtils.trim(jrCrnSpec.getFieldString("YD_WRK_CRN"   ));	//야드설비ID(크레인)
//			String ydSchPrior = commUtils.trim(jrCrnSpec.getFieldString("YD_WRK_CRN_PRIOR"));	//야드스케쥴우선순위
//			String ydToLocDcsnMtd = "S"; //야드TO위치결정방법(스케줄지정)
//
//			// 지정크레인일 경우 지정크레인으로 스케줄 기동
//			if (!"".equals(ydWrkPlanCrn)) {
//				ydEqpId = ydWrkPlanCrn;
//			}
//			
//			if ("".equals(ydAimBayGp)) {
//				ydAimBayGp = ydBayGp;
//			}
//
//			if (!"".equals(ydToLocGuide)) {
//				ydToLocDcsnMtd = "F";	//야드TO위치결정방법(지정위치)
//			}
//			
//			/**********************************************************
//			* 2. 크레인사양 분리
//			**********************************************************/
//			jrCrnSpec.setResultCode(logId);  	//Log ID
//			jrCrnSpec.setResultMsg(methodNm);	//Log Method Name
//			
//			Vector vcLot = this.setCrnSpecSpr(jrCrnSpec, jsWbMtl);
//
//			JDTORecordSet jsLotMtl = null;
//			int lotCnt   = vcLot.size();	//크레인사양 분리 작업예약수
//			int lotMtlSh = 0;				//작업예약재료매수
//			String ydWbookId = "";			//야드작업예약ID
//			JDTORecord jrRow = null;
//			commUtils.printLog(logId, "lotCnt:"+lotCnt, "SL");
//			
//			/**********************************************************
//			* 3. 작업예약 등록
//			**********************************************************/
//			for (int ii = 0; ii < lotCnt; ii++) {
//				//작업예약재료
//				jsLotMtl = (JDTORecordSet)vcLot.get(ii);
//				lotMtlSh = jsLotMtl.size();
//
//				if (lotMtlSh <= 0) {
//					continue;
//				}
//
//				//작업예약ID 조회
//				ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
//
//				if ("".equals(ydWbookId)) {
//					throw new YFUserException("작업예약ID 생성 실패");
//				}
//				
//				//작업예약 등록
//				jrParam.setField("YD_WBOOK_ID"       , ydWbookId     ); //야드작업예약ID
//				jrParam.setField("MODIFIER"          , modifier      ); //수정자
//				jrParam.setField("YD_GP"             , ydGp          ); //야드구분
//				jrParam.setField("YD_BAY_GP"         , ydBayGp       ); //야드동구분
//				jrParam.setField("YD_SCH_CD"         , ydSchCd       ); //야드스케쥴코드
//				jrParam.setField("YD_SCH_PRIOR"      , ydSchPrior    ); //야드스케쥴우선순위
//				jrParam.setField("YD_SCH_PROG_STAT"  , "W"           ); //야드스케쥴진행상태(스케줄수행대기)
//				jrParam.setField("YD_SCH_ST_GP"      , "M"           ); //야드스케쥴기동구분(Manual)
//				jrParam.setField("YD_SCH_REQ_GP"     , "M"           ); //야드스케쥴요청구분(이적)
//				jrParam.setField("YD_AIM_YD_GP"      , ydGp          ); //야드목표야드구분
//				jrParam.setField("YD_AIM_BAY_GP"     , ydAimBayGp    ); //야드목표동구분
//				jrParam.setField("YD_TO_LOC_DCSN_MTD", ydToLocDcsnMtd); //야드TO위치결정방법
//				jrParam.setField("YD_TO_LOC_GUIDE"   , ydToLocGuide  ); //야드To위치Guide
//				jrParam.setField("YD_WRK_PLAN_CRN"   , ydWrkPlanCrn ); //야드작업계획크레인
//
//				commDao.insert(jrParam, insWrkBook, logId, methodNm, "TB_YF_WRKBOOK");
//
//				//작업예약재료 등록
//				for (int jj = 0; jj < lotMtlSh; jj++) {
//					jrRow = jsLotMtl.getRecord(jj);
//					
//					JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();
//					
//					jrRtn1.setField("YD_WBOOK_ID"   , ydWbookId     ); //야드작업예약ID
//					jrRtn1.setField("STL_NO"      , commUtils.trim(jrRow.getFieldString("STL_NO"       )));	//재료번호
//					jrRtn1.setField("YD_STK_COL_GP"  , commUtils.trim(jrRow.getFieldString("YD_STK_COL_GP")));	//야드적치열구분
//					jrRtn1.setField("YD_STK_BED_NO"  , commUtils.trim(jrRow.getFieldString("YD_STK_BED_NO")));	//야드적치Bed번호
//					jrRtn1.setField("YD_STK_LYR_NO", commUtils.trim(jrRow.getFieldString("YD_STK_LYR_NO")));	//야드적치단번호
//					jrRtn1.setField("MODIFIER"     	, modifier      );												//등록자
//					
//					commDao.insert(jrRtn1, insWrkBookMtl, logId, methodNm, "TB_YF_WRKBOOKMTL");
//				}
//				
//				
//				// 스케줄 기동
////				JDTORecord jrYmMsg = commUtils.getParam(logId, methodNm, modifier);
////				jrYmMsg.setResultCode(logId);	//Log ID
////				jrYmMsg.setResultMsg(methodNm);	//Log Method Name
////
////				jrYmMsg.setField("JMS_TC_CD", "YMYMJ302"); //야드작업예약ID
////				jrYmMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시				
////				
////				jrYmMsg.setField("YD_WBOOK_ID", ydWbookId);
////				jrYmMsg.setField("YD_SCH_CD"  , ""); //야드스케쥴코드
////				jrYmMsg.setField("YD_EQP_ID"  , ""); //야드설비ID
////				
////				//크레인스케줄기동 전문
////				EJBConnector sndConnD = new EJBConnector("default", "BCoilSchSeEJB", this);
////				JDTORecord jrRtnD = (JDTORecord)sndConnD.trx("rcvYMYMJ302", new Class[] { JDTORecord.class }, new Object[] { jrYmMsg });
////				jrRtn = commUtils.addSndData(jrRtn, jrRtnD);
//				
//				jrYFYFJ303.setField("YD_WBOOK_ID"+(++wrkBookCnt), ydWbookId); //야드작업예약ID
//			}
//
//			if (!"".equals(ydWbookId)) {
//				jrYFYFJ303.setField("JMS_TC_CD"         , "YFYFJ303"); //야드작업예약ID
//				jrYFYFJ303.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
//				jrYFYFJ303.setField("YD_SCH_CD"         , ""); //야드스케쥴코드
//				jrYFYFJ303.setField("YD_EQP_ID"         , ""); //야드설비ID
//				jrYFYFJ303.setField("SCH_CNT"           , Integer.toString(wrkBookCnt)); //작업예약 개수
//				
//				jrRtn = commUtils.addSndData(jrRtn, jrYFYFJ303);
//			}
//			
//			//크레인스케줄기동 전문
////			EJBConnector sndConn = new EJBConnector("default", "BCoilSchSeEJB", this);
////			jrRtn = (JDTORecord)sndConn.trx("rcvYMYMJ303", new Class[] { JDTORecord.class }, new Object[] { jrYMYM303 });
//			
//			commUtils.printLog(logId, methodNm, "S-");
//
//			return jrRtn;
//		} catch(DAOException e) {
//			throw e;
//		} catch(Exception e) {
//			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
//		}	
//	}
	
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
		String methodNm = "크레인사양분리[ACoilJspBakSeEJB.setCrnSpecSpr] < " + jrCrnSpec.getResultMsg();
		String logId = jrCrnSpec.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			Vector vcLot = new Vector();	//크레인사양분리결과
			JDTORecord    jrRow = null;		//현재 Row
			JDTORecordSet jsLot = JDTORecordFactory.getInstance().createRecordSet("");	//Lot
			String sYD_STK_COL_GP    = "";	
			String sYD_STK_BED_NO    = "";	
			String sYD_STK_LYR_NO  = "";	
			String szCHK_YD_STK_COL_GP   = "";
			String szCHK_YD_STK_BED_NO   = "";			
			String szCHK_YD_STK_LYR_NO = "";			

			int rowCnt = jsWrkMtl.size();

			for (int ii = 0; ii < rowCnt; ii++) {
				jrRow = jsWrkMtl.getRecord(ii);
				
				sYD_STK_COL_GP   = commUtils.trim(jrRow.getFieldString("YD_STK_COL_GP"));
				sYD_STK_BED_NO   = commUtils.trim(jrRow.getFieldString("YD_STK_BED_NO"));
				sYD_STK_LYR_NO = commUtils.trim(jrRow.getFieldString("YD_STK_LYR_NO"));
				
				if (ii > 0) {

					if (!(szCHK_YD_STK_COL_GP+szCHK_YD_STK_BED_NO+szCHK_YD_STK_LYR_NO).equals(sYD_STK_COL_GP+sYD_STK_BED_NO+sYD_STK_LYR_NO)) {
						//이전 Lot 추가
						vcLot.add(jsLot);

						jsLot = JDTORecordFactory.getInstance().createRecordSet("");
						szCHK_YD_STK_COL_GP   = sYD_STK_COL_GP;
						szCHK_YD_STK_BED_NO   = sYD_STK_BED_NO;
						szCHK_YD_STK_LYR_NO = sYD_STK_LYR_NO;
					}
				} else {
					szCHK_YD_STK_COL_GP   = sYD_STK_COL_GP;
					szCHK_YD_STK_BED_NO   = sYD_STK_BED_NO;
					szCHK_YD_STK_LYR_NO = sYD_STK_LYR_NO;
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
		String methodNm = "크레인스케줄전문정리[ACoilJspBakSeEJB.setCrnSchMsg] < " + mthdNm;

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
					
					if (!"1XTC03".equals(commUtils.trim(jrRow.getFieldString("YD_WRK_PLAN_TCAR")))) {
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
							jsChk = commDao.select(jrParam, getStatEqp, logId, methodNm, "설비상태조회");
							
							ydEqpStat = "";

							if (jsChk.size() > 0) {
								ydEqpStat = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT"));
							}

							jrParam.setField("YD_SCH_CD", sYD_SCH_CD);
							JDTORecordSet rst = commDao.select(jrParam, getIsSchKind,logId, methodNm, "크레인스케쥴확인");
							commUtils.printLog(logId, "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■", "[info]");
							commUtils.printLog(logId, "■■■■■"+ sYD_WBOOK_ID + " " + ydEqpId + " "+sYD_SCH_CD, "[info]");
							commUtils.printLog(logId, "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■", "[info]");
							
							/**
							 * 스케줄 자동기동 여부 체크 
							 */
							JDTORecordSet jsSchInfo = commDao.select(jrParam, getSchCdInfo, logId, methodNm, "자동기동여부 조회");
							if (jsSchInfo.size() > 0) {
								String sYD_SCH_AUTO_ST_YN = jsSchInfo.getRecord(0).getFieldString("YD_SCH_AUTO_ST_YN");
								commUtils.printLog(logId, "■■■■■■ 스케쥴 자동기부 여부 확인 ■■■■■■■", "[info]");
								commUtils.printLog(logId, "■■■■■ YD_SCH_AUTO_ST_YN :: "+ sYD_SCH_AUTO_ST_YN, "[info]");
								commUtils.printLog(logId, "■■■■■ 크레인 && 동일스케쥴로 작업진행건 확인(Y/N) :: "+ rst.getRecord(0).getFieldString("IS_SCH"), "[info]");
								commUtils.printLog(logId, "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■", "[info]");
								//스케줄 자동기동여부 Y , 스케줄 기동된 것이 없으면 기동시킴
								if ("Y".equals(sYD_SCH_AUTO_ST_YN) && "N".equals(rst.getRecord(0).getFieldString("IS_SCH"))) {
									commUtils.printLog(logId, "■■■■■■ 크레인스케쥴 기동 YFYFJ302 [실제 전송되었는지 전문확인 필] ■■■■■■■", "[info]");
									
									//크레인스케줄 전송YMYMJ302
									jrRtn = commUtils.addSndData(jrRtn, comm.getCrnSchMsg(jrRow));
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
	 * 차량동간이적 (이적지시 )
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updColUnitCarMvstkRegNew(GridData gdReq) throws DAOException
	{
		String methodNm = "차량이적등록[ACoilJspBakSeEJB.updColUnitCarMvstkRegNew] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try
		{
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet rsResult = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String sSTL_NO			= null;
			String sYD_STK_COL_GP 	= ""; //적치 열 구분
			String sYD_STK_BED_NO 	= ""; //적치 BED 구분
			String sYD_STK_LYR_NO 	= ""; //적치 단 구분
			String sYD_SCH_PRIOR 	= ""; //야드스케쥴우선순위
			String sYD_WBOOK_ID 	= ""; //작업예약ID
			String sYD_SCH_REQ_GP 	= ""; //야드스케줄요청구분
			String ydFrmYn          = "";
			String ydStkColGp       = "";	
			
			//화면에서 전달 받은 파레메터 저장
			String sCAR_CARD_NO 	= commUtils.trim(gdReq.getParam("CAR_NO"));			//차량번호(차량카드번호)
			String sYD_GP	  		= commUtils.trim(gdReq.getParam("YD_GP"));			//야드구분 		
			String sYD_BAY_GP 		= commUtils.trim(gdReq.getParam("BAY_GP"));			//동 정보
			String sTO_YD_BAY_GP 	= commUtils.trim(gdReq.getParam("TO_YD_BAY_GP"));	//목표동 정보
			String sT_CNT			= commUtils.trim(gdReq.getParam("T_CNT"));			//작업매수
			String sPT_LOC			= commUtils.trim(gdReq.getParam("PT_LOC"));			//위치구분(L,R) 1 좌(L)->우(R), 2 좌(L)->좌(L), 3 우(R)->좌(L), 4 우(R)->우(R)
			String sYD_SCH_CD		= commUtils.trim(gdReq.getParam("YD_SCH_CD"));		//스케줄코드
			int	   vT_CNT			= 1;												//차상위치
			
			String subMsg			= "";
			
			if("1".equals(sPT_LOC))
			{
				subMsg = "좌(L)->우(R)";
			}
			else if("2".equals(sPT_LOC))
			{
				subMsg = "좌(L)->좌(L)";
			}
			else if("3".equals(sPT_LOC))
			{
				subMsg = "우(R)->좌(L)";
			}
			else if("4".equals(sPT_LOC))
			{
				subMsg = "우(R)->우(R)";
			}
			
			//파라메터 체크
			if("".equals(sYD_GP)) 
			{
				throw new YFUserException("야드구분을 입력하지 않았습니다!.");
			}
			
			if("".equals(sYD_BAY_GP)) 
			{
				throw new YFUserException("야드동을 입력하지 않았습니다!.");
			}
			
			if("".equals(sSTL_NO))
			{
				throw new YFUserException("COIL_NO를 입력하지 않았습니다!.");
			}
			
			if(sTO_YD_BAY_GP.equals(sYD_BAY_GP)){
				throw new YFUserException("차량동간이적시 동일동으로 이적을 할 수 없습니다. [이적동]을 확인하세요");
			}
			
			jrParam.setField("YD_GP",			sYD_GP); 	
			jrParam.setField("YD_BAY_GP",		sYD_BAY_GP); 
			jrParam.setField("PT_LOC",			sPT_LOC); 
			jrParam.setField("TO_YD_BAY_GP",	sTO_YD_BAY_GP); 
			
			// TO 위치 차량이송 포인트 CHECK
		    JDTORecordSet jsCarPntFrmYnToChk = commDao.select(jrParam, getCarPntFrmYnToChk, logId, methodNm, "TB_YD_CARPOINT 에 포인트(MT)  Check");
		    
			if(jsCarPntFrmYnToChk.size() == 0)
			{ 
				throw new YFUserException(sTO_YD_BAY_GP+ "동 차량동간이적 포인트["+subMsg+"]가 설정되어 있지 않습니다.");	
			}

			jrParam.setField("CAR_CARD_NO",		sCAR_CARD_NO);
			JDTORecordSet jsMvCarSch = commDao.select(jrParam, getDongMvCarSch, logId, methodNm, "TB_YD_CARPOINT 에 스케쥴  Check");

			if(jsMvCarSch.size() == 0)
			{	
				/**********************************************************
				//화면에서 전달 받은 파레메터 저장
				* Crane스케줄 호출
				*  - CarPoint 테이블에서 차량형상 시스템 사용 여부 확인
				*  - 사용여부가 N 일 경우 생성된 작업예약 모두를 스케줄 기동
				**********************************************************/
				JDTORecordSet jsPntFrm = commDao.select(jrParam, getCarPntFrmYnByPtLoc, logId, methodNm, "TB_YD_CARPOINT 테이블에서 차량형상 시스템 사용 여부 확인 ");
				
				if(jsPntFrm.size() > 0)
				{
					ydFrmYn		= commUtils.nvl(jsPntFrm.getRecord(0).getFieldString("YD_FRM_YN"), "N");
					ydStkColGp	= commUtils.trim(jsPntFrm.getRecord(0).getFieldString("YD_STK_COL_GP"));
				}
				else
				{
					throw new YFUserException(sYD_BAY_GP+ "동 차량동간이적 포인트[" + subMsg + "]가 설정되어 있지 않습니다.");	
				}
			}
			
			/**********************************************************
			* 야드스케쥴 우선순위 검색
			**********************************************************/
			//스케줄코드로 스케줄기준Table조회
			jrParam.setField("YD_SCH_CD", sYD_SCH_CD);

			rsResult = commDao.select(jrParam, getYdSchrule, logId, methodNm, "스케줄 기준 조회"); 
	    	
			if (rsResult != null && rsResult.size() > 0)
			{
				sYD_SCH_PRIOR = rsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR");	//야드스케쥴우선순위
			}
			else
			{
				throw new YFUserException("박판열연 코일 스케쥴 코드 이상 : [" + sYD_SCH_CD + "]");
			}			

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for(int jj = 2; jj > 0; jj--)
			{
				for(int ii = 0; ii < rowCnt; ii++)
				{
					sYD_STK_LYR_NO = commUtils.getValue(gdReq, "YD_STK_LYR_NO", ii);	//Grid에서 선택한 코일의 적치단 '01','02'
					
					if(sYD_STK_LYR_NO.equals("0"+jj)) 
					{
						//2단 먼저 처리 후 1단 처리..
						sSTL_NO = commUtils.getValue(gdReq, "STL_NO", ii);		//Grid에서 선택한 코일 번호
						
						/**********************************************************
						* 1. TB_YF_STOCK 에 정보가 존재하는지 Check
						*    - 존재한다면 작업예약에 걸려있는지 Check 
						**********************************************************/
						jrParam.setField("STL_NO", sSTL_NO);
						rsResult = commDao.select(jrParam, selectStockIdNoDel, logId, methodNm, "TB_YF_STOCK 에 정보가 존재하는지 Check");
						
						if (rsResult == null || rsResult.size() <= 0)
						{
							throw new YFUserException("TB_YF_STOCK 에 존재하지 않는 COIL_NO 입니다!");
						}
						else
						{
							sYD_WBOOK_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_WBOOK_ID"));
							
							if(!"".equals(sYD_WBOOK_ID))
							{
								throw new YFUserException("작업예약 ID : " + sYD_WBOOK_ID + " 에 등록된 COIL 입니다!");
							}
						}
	
						/**********************************************************
						* 2. TB_YF_STKLYR 에 적치상태  Check
						*    - 적치열,bed,단 정보를 가져온다.
						**********************************************************/
						sYD_STK_COL_GP = ""; //적치 열 구분
						sYD_STK_BED_NO = ""; //적치 BED 구분
						sYD_STK_LYR_NO = ""; //적치 단 구분
						
						jrParam.setField("STL_NO", sSTL_NO);
						  
						rsResult = commDao.select(jrParam, getselectYdStkColGp, logId, methodNm, "TB_YF_STKLYR 에 적치상태  Check");
						
						if (rsResult == null || rsResult.size() <= 0)
						{
							throw new YFUserException("TB_YF_STKLYR 에 존재하지 않는 COIL_NO 입니다!");
						}
						else
						{
							String vYD_STK_LYR_STAT = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_LYR_STAT"));
							
							if(!"C".equals(vYD_STK_LYR_STAT) && !"L".equals(vYD_STK_LYR_STAT))
							{
								throw new YFUserException("적치단 상태가 '적치중(C)' 이 아닙니다! 현재 상태 : " + vYD_STK_LYR_STAT);
							}
							
							sYD_STK_COL_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP")); //적치 열 구분
							sYD_STK_BED_NO 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_BED_NO")); //적치 BED 구분
							sYD_STK_LYR_NO	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_LYR_NO")); //적치 단 구분
						}

						/**********************************************************
						* 3. TB_YF_STOCK 에 차량 카드번호 등록
						*    - 저장품 이동 조건 지정
						*    - CAR_CARD_NO 에 화면에서 선택한 번호 설정 (9990,9991,..,9994)
						*    - TRANS_WORD_NO 설정
						**********************************************************/
						jrParam.setField("STOCK_MOVE_TERM",		YfConstant.NEW_STOCK_MOVE_TERM_CS);	//이송대기
						jrParam.setField("CAR_CARD_NO",			sCAR_CARD_NO);
						jrParam.setField("CARUNLOAD_YD",		sYD_GP);
						jrParam.setField("CARUNLOAD_BAY",		sTO_YD_BAY_GP);
						jrParam.setField("CARUNLOAD_PUT_LOC",	sYD_GP + sTO_YD_BAY_GP);
						jrParam.setField("CTS_RELAY_BAY",		sT_CNT);
						jrParam.setField("CTS_RELAY_SADDLE",	sPT_LOC);
						jrParam.setField("STL_NO",				sSTL_NO);
						jrParam.setField("YD_CAR_UPP_LOC_CD",	vT_CNT + "");
						commDao.update(jrParam, updateStockTransInfo_01, logId, methodNm, "TB_YF_STOCK 에 차량 카드번호 등록");
						
						/**********************************************************
						* 4. 작업예약 ID 생성
						**********************************************************/
						sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook"); 
						
						/**********************************************************
						* 5. 작업예약(TB_YM_WRKBOOK) 생성
						**********************************************************/
						jrParam.setField("YD_WBOOK_ID",			sYD_WBOOK_ID);
						jrParam.setField("YD_GP",				sYD_GP);
						jrParam.setField("YD_BAY_GP",			sYD_BAY_GP);
						jrParam.setField("YD_SCH_CD",			sYD_SCH_CD);		//야드스케쥴코드
						jrParam.setField("YD_SCH_PRIOR",		sYD_SCH_PRIOR);		//야드스케쥴우선순위
						jrParam.setField("YD_SCH_PROG_STAT",	"W");				//야드스케쥴진행상태(W:스케줄수행대기)
						jrParam.setField("YD_SCH_ST_GP",		"M");				//야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
						jrParam.setField("YD_SCH_REQ_GP",		sYD_SCH_REQ_GP);	//야드스케쥴요청구분
						jrParam.setField("CARD_NO",				sCAR_CARD_NO); 
						jrParam.setField("CAR_NO",				sCAR_CARD_NO); 
						jrParam.setField("YD_AIM_YD_GP",		sYD_GP); 
						jrParam.setField("YD_AIM_BAY_GP",		sTO_YD_BAY_GP); 
						jrParam.setField("DIST_SHIPASSIGN_GP",	sPT_LOC); 
						jrParam.setField("YD_CAR_USE_GP",		YfConstant.YD_CAR_USE_GP_DM);	//G:출하차량 (차량이적시 G로 설정한다 C열연 참조)
						jrParam.setField("YD_TO_LOC_DCSN_MTD",	"S");				//TO위치결정방법 S:스케줄기준적용
						commDao.insert(jrParam, insWrkBook, logId, methodNm, "작업예약(TB_YF_WRKBOOK) 생성");
	
						/**********************************************************
						* 6. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
						**********************************************************/
						jrParam.setField("YD_WBOOK_ID",			sYD_WBOOK_ID);
						jrParam.setField("STL_NO",				sSTL_NO);
						jrParam.setField("YD_STK_COL_GP",		sYD_STK_COL_GP);
						jrParam.setField("YD_STK_BED_NO",		sYD_STK_BED_NO);
						jrParam.setField("YD_STK_LYR_NO",		sYD_STK_LYR_NO);
						commDao.insert(jrParam, insYfWrkBookMtl, logId, methodNm, "작업예약재료(TB_YF_WRKBOOKMTL) 생성");
						
						//작업매수와 차상위치번호가 같으면 차상위치 초기화(1) 아니면 차상위치 증가(++)
						if(sT_CNT.equals(vT_CNT + ""))
						{
							vT_CNT	= 1;
						}
						else
						{
							vT_CNT++;
						}
						
					}
				}
			}
			
			// 형상여부
			if("N".equals(ydFrmYn))
			{
				// 형상이 없는 경우 도착 미리 기동처리 함	
				jrParam.setField("JMS_TC_CD"		, "F1YFL018" );
				jrParam.setField("PT_LOAD_LOC"	    , ydStkColGp);
				jrParam.setField("CAR_NO"			, sCAR_CARD_NO );
				jrParam.setField("CAR_UPDN_GP"		, "1");  //상차
				jrParam.setField("MODIFIER"			, commUtils.trim(gdReq.getParam("userid")) );
				
				EJBConnector sndConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
				JDTORecord jrRtn1 = (JDTORecord)sndConn.trx("procF1YFL018", new Class[] { JDTORecord.class }, new Object[] { jrParam });

				jrRtn = commUtils.addSndData(jrRtn, jrRtn1);				
			}

			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
			
		}
		catch(DAOException e)
		{
			throw e;
		}
		catch(YFUserException e)
		{
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		}
		catch(Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	} // end of updColUnitCarMvstkRegNew
	
	/**
	 *      [A] 오퍼레이션명 : 코일공통상세조회-정정검사메시지 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updShearInspectMsg(GridData gdReq) throws DAOException{
		String methodNm = "코일공통상세조회-정정검사메시지 수정[ACoilJspBakSeEJB.updShearInspectMsg] < " + gdReq.getNavigateValue();
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
			
			commDao.update(jrParam, insHrShrMsgLog, logId, methodNm, "TB_HR_C_SHEARWOWR_MSG_LOG 열연정정작업메세지이력관리 등록");
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}	//updShearInspectMsg
	
	/**
	 * 	<pre>
	 * 		[A] 오퍼레이션명 : 위치별 적치현황조회 - Bed상태 수정
	 * 			1. 적치단(TB_YF_STKLYR)의 활성화 상태를 변경한다.
	 *  </pre>
	 * 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updBedActStat(GridData gdReq) throws DAOException {
		String methodNm = "위치별 적치현황조회 - Bed상태 수정[ACoilJspBakSeEJB.updBedActStat] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = null;
			JDTORecord jrYdMsg = null;
				
			//대차정보
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String sYdStkLoc = "";
			String sSendF1Loc = "";
			for (int i = 0; i < rowCnt; i++) {
				
				jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
				
				sYdStkLoc = commUtils.trim(gdReq.getHeader("YD_STK_LOC").getValue(i));
				if(!"".equals(sYdStkLoc)){
					jrParam.setField("YD_STK_COL_GP"    , sYdStkLoc.substring(0, 6));
					jrParam.setField("YD_STK_BED_NO"    , sYdStkLoc.substring(6, 8));
					jrParam.setField("YD_STK_LYR_NO"    , sYdStkLoc.substring(8, 10));
					jrParam.setField("YD_STK_LYR_ACTIVE_STAT", commUtils.trim(gdReq.getHeader("YD_STK_LYR_ACTIVE_STAT").getValue(i)) );
					jrParam.setField("YD_STK_LYR_YD_STK_LOT_NO2", commUtils.trim(gdReq.getHeader("YD_STK_LYR_YD_STK_LOT_NO2").getValue(i)) );
					
					commDao.update(jrParam, updYdStkLyrActiveStat, logId, methodNm, "TB_YF_STKLYR - 적치활성화 상태 수정");
					
					// Bed가 다를 경우에만 전송
					if( !sSendF1Loc.equals(jrParam.getFieldString("YD_STK_COL_GP")+jrParam.getFieldString("YD_STK_BED_NO") )) {
						/**
						 * Level2  전문전송
						 *  - YFF1L001 (저장품제원위치)
						 */
						jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setField("TC_CD"          , "YFF1L001");
						jrYdMsg.setField("MSG_GP"         , "I");
						jrYdMsg.setField("YD_INFO_SYNC_CD", "4");
						jrYdMsg.setField("YD_STK_COL_GP"   , jrParam.getFieldString("YD_STK_COL_GP"));
						jrYdMsg.setField("YD_STK_BED_NO"   , jrParam.getFieldString("YD_STK_BED_NO"));
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L001", jrYdMsg));
						
						sSendF1Loc = jrParam.getFieldString("YD_STK_COL_GP")+jrParam.getFieldString("YD_STK_BED_NO");
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
	} // end of updBedActStat
	
	/**
	 * 대차스케줄관리 - 대차초기화
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord initTcarSchMgt(GridData gdReq) throws DAOException {
		String methodNm = "대차스케줄관리 대차초기화[ACoilJspBakSeEJB.initTcarSchMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String ydEqpId     = ""; //야드설비ID(대차)
			String ydCurrBayGp = ""; //야드현재동구분(신규)

			String sUserId = commUtils.trim(gdReq.getParam("userid"));
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, sUserId);

			jrParam.setField("YD_EQP_WRK_STAT"    , "U"); //야드설비작업상태(공차)
			jrParam.setField("YD_CARLD_SCH_REQ_GP", "6"); //야드상차스케쥴요청구분(공대차도착)
			jrParam.setField("YD_CARUD_SCH_REQ_GP", "3"); //야드하차스케쥴요청구분(영대차도착)
			jrParam.setField("YD_CAR_PROG_STAT"   , "0"); //야드차량진행상태(상차대기)

			//대차정보
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			commUtils.printLog(logId, methodNm, "userid :: {}"+gdReq.getParam("userid"));
			commUtils.printLog(logId, methodNm, "jrParam :: {}"+jrParam.toString());
			
			JDTORecord sedRtn = null;
			for (int ii = 0; ii < rowCnt; ii++) {
 
				ydEqpId     = commUtils.getValue(gdReq,"YD_EQP_ID"	,ii) ;
				ydCurrBayGp = commUtils.getValue(gdReq,"YD_CURR_BAY_GP"	,ii) ;
				if ("".equals(ydEqpId)) {
					throw new YFUserException("설비ID가 없습니다.");
				} else if ("".equals(ydCurrBayGp)) {
					throw new YFUserException("변경할 현재동이 없습니다.");
				}
				
				sedRtn = commUtils.getParam(logId, methodNm, sUserId);
				sedRtn.setField("YD_EQP_ID", ydEqpId);
				sedRtn.setField("YD_CURR_BAY_GP", ydCurrBayGp);
				sedRtn.setField("userid",sUserId);
				jrRtn = this.initTcarSchMgt(sedRtn); 
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of initTcarSchMgt
	
	/**
	 * 
	 * 	<pre>
 		[A] 오퍼레이션명 : 대차스케줄관리 - 대차초기화
 		 1. APP051(대차초기화시 작업예약삭제)의 값(Y/N)에 의한 작업예약삭제 진행여부 판단
	 *   2. 대차초기화
	 *     - 대차스케쥴, 재료 삭제
	 *     * 신규대차스케쥴 생성
	 *   3. 현재동 설정
	 *     
	 * 
	 *  <pre>
	 * 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @param booelan  bSendFlag 
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord initTcarSchMgt(JDTORecord rcvMsg) throws DAOException {
		
		String methodNm = "대차스케줄관리 대차초기화[ACoilJspBakSeEJB.initTcarSchMgt] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			String ydEqpId     = ""; //야드설비ID(대차)
			String ydCurrBayGp = ""; //야드현재동구분(신규)
			String sUserId = commUtils.trim(rcvMsg.getFieldString("userid"));
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, sUserId);

			jrParam.setField("YD_EQP_WRK_STAT"    , "U"); //야드설비작업상태(공차)
			jrParam.setField("YD_CARLD_SCH_REQ_GP", "6"); //야드상차스케쥴요청구분(공대차도착)
			jrParam.setField("YD_CARUD_SCH_REQ_GP", "3"); //야드하차스케쥴요청구분(영대차도착)
			jrParam.setField("YD_CAR_PROG_STAT"   , "0"); //야드차량진행상태(상차대기)

			//대차정보
			ydEqpId     = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));
			ydCurrBayGp = commUtils.trim(rcvMsg.getFieldString("YD_CURR_BAY_GP"));
			jrParam.setField("YD_EQP_ID", ydEqpId);
			
			
			if ("".equals(ydEqpId)) {
				throw new YFUserException("설비ID가 없습니다.");
			} else if ("".equals(ydCurrBayGp)) {
				throw new YFUserException("변경할 현재동이 없습니다.");
			}
			
			/**********************************************************
			* 1. 작업예약 삭제
			**********************************************************/
			String sAPP051 = comm.ACoilApplyYn("APP051","1","1");
			if ("Y".equals(sAPP051)) {
				JDTORecordSet jsList = commDao.select(jrParam, getInitTrgtWrkList, logId, methodNm, "삭제대상 작업예약 조회");
				JDTORecord tmpJdto = null;
				for (int ii = 0; ii < jsList.size(); ++ii) {
					tmpJdto = commUtils.getParam(logId, methodNm, sUserId);
					tmpJdto.setField("YD_WBOOK_ID", jsList.getRecord(ii).getFieldString("YD_WBOOK_ID"));
					// 작업예약-재료 
					commDao.update(tmpJdto, updDelYnWrkBookMtl, logId, methodNm, "TB_YM_WRKBOOKMTL 삭제");
					// 작업예약
					commDao.update(tmpJdto, updDelYnWrkBook, logId, methodNm, "TB_YM_WRKBOOK 삭제");
				}
			}
			
			/**********************************************************
			* 2. 기존 대차스케줄/재료 삭제
			**********************************************************/
		 
			//대차이송재료 초기화
			commDao.update(jrParam, updTcarSchInitMtl, logId, methodNm, "대차이송재료 초기화");
 
			//대차스케줄 초기화
			commDao.update(jrParam, updTcarSchInitSch, logId, methodNm, "대차스케줄 초기화");
			
			/**********************************************************
			* 3. 신규 대차스케줄 등록
			**********************************************************/
			//야드대차스케쥴ID 생성
			String ydTcarSchId = commDao.getSeqId(logId, methodNm, "TcarSch");

			if ("".equals(ydTcarSchId)) {
				throw new YFUserException( "대차스케줄ID 생성 중 오류가 발생하였습니다.");
			}
			
			//대차스케줄 등록
			jrParam.setField("YD_TCAR_SCH_ID"   , ydTcarSchId); //야드대차스케쥴ID
			jrParam.setField("YD_CAR_PROG_STAT" , "0"        ); //야드차량진행상태(상차대기)
			jrParam.setField("YD_CARLD_STOP_LOC", ydEqpId.substring(0, 1) + ydCurrBayGp + ydEqpId.substring(2)); //야드상차정지위치
			commDao.update(jrParam, mrgTcarSchInsSch, logId, methodNm, "대차스케줄 등록");

			/**********************************************************
			* 4. 대차 현재동 변경
			**********************************************************/
			jrParam.setField("YD_EQP_ID"      , ydEqpId    );
			jrParam.setField("YD_CURR_BAY_GP", ydCurrBayGp);

			// 기존 동일 내용의 method를 한개로 통합
			// 멀티건의 경우 jrParam의 값만 리턴하여 
			// 호출되는 Main Method에서 구현(전문전송)
	 
			jrRtn = commUtils.addSndData(jrRtn, this.updTcarCurrBay(jrParam));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
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
		String methodNm = "대차 현재동 변경[ACoilJspBakSeEJB.updTcarCurrBay] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		//Return Value
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydEqpId 		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID(대차)
			String ydBayGpNew 	= commUtils.trim(rcvMsg.getFieldString("YD_CURR_BAY_GP")); //야드신규동구분

			if ("".equals(ydEqpId)) {
				throw new YFUserException("설비ID가 없습니다.");
			} else if ("".equals(ydBayGpNew)) {
				throw new YFUserException("변경할 현재동이 없습니다.");
			}

	
			String ydBayGpCurr  	= ""; //야드현재동구분(현재)
			String ydStkColGpNew    = ydEqpId.substring(0, 1) + ydBayGpNew + ydEqpId.substring(2); //야드적치열(신규)

			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));

			jrParam.setField("YD_EQP_ID"         , ydEqpId       );
			jrParam.setField("YD_CURR_BAY_GP_NEW", ydBayGpNew);
			
			/**********************************************************
			* 1. 대차Bed상태 조회
			**********************************************************/
			JDTORecordSet jsTcar = commDao.select(jrParam, getStatEqp, logId, methodNm, "대차Bed상태 조회");
			if (jsTcar != null && jsTcar.size() > 0) {
		    	JDTORecord jrTcar = jsTcar.getRecord(0);
	
		    	ydBayGpCurr     = commUtils.trim(jrTcar.getFieldString("YD_CURR_BAY_GP"));  //대차현재동
	
			    if ("".equals(ydStkColGpNew)) {
					throw new YFUserException("변경할 적치열이 없습니다.");
				}
		    } else {
				throw new YFUserException("대차 Bed상태 정보가 없습니다.");
		    }

			/**********************************************************
			* 2. 대차 저장위치 전체 비 활성화
			**********************************************************/
			jrParam.setField("YD_STK_COL_GP", ydEqpId.substring(0, 1) + "_" + ydEqpId.substring(2)); //야드적치열구분(대차전체Bed)

			//적치Bed(전체) 비활성화
			commDao.update(jrParam, updStatStkBedActCA, logId, methodNm, "적치Bed(전체) 비활성화");

			//적치단(전체) 재료 삭제
			commDao.update(jrParam, updYdStkLyrClr1, logId, methodNm, "적치단 재료 삭제");

			/**********************************************************
			* 3. 현재동 변경 및 저장위치제원 전문 조회
			**********************************************************/
			if (!ydBayGpCurr.equals(ydBayGpNew)) {
				//설비 현재동 수정
				jrParam.setField("YD_STK_COL_GP", ydStkColGpNew);
				commDao.update(jrParam, updYdEqpCurrBay, logId, methodNm, "설비 현재동 수정");

				//기존 Bed의 상태가 변경되었으면 저장위치제원(YDY1L001, YDY3L001) 전문 조회
				jrParam.setField("YD_INFO_SYNC_CD", "4"          ); //야드정보동기화코드(Bed)
				jrParam.setField("YD_STK_COL_GP"   , ydStkColGpNew); //야드적치열구분
				jrParam.setField("YD_STK_BED_NO"   , "01"         ); //야드적치Bed번호

				//전송Data 조회
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L001", jrParam));
			}
			
			/**********************************************************
			* 4. 신규 저장위치  활성화 및 저장위치제원 전문 조회
			**********************************************************/
			//신규 적치Bed Close 상태이면 활성화
			jrParam.setField("YD_STK_COL_GP"      	, ydStkColGpNew); //야드적치열구분
			jrParam.setField("YD_STK_BED_ACTIVE_STAT", "L"          ); //야드적치Bed활성상태(적치가능)
			
			//적치Bed 수정
			commDao.update(jrParam, updStatStkBedActByCol, logId, methodNm, "신규 적치Bed Close 상태이면 활성화");

			//적치단 수정 
			commDao.update(jrParam, updYdStkLyrActiveTC, logId, methodNm, "신규 적치Bed Close 상태이면 활성화");
			

			//신규 Bed의 상태가 변경되었으면 저장위치제원(YDY1L001, YDY3L001) 전문 전송
			jrParam.setField("YD_INFO_SYNC_CD", "3"); //야드정보동기화코드(Bed)
			jrParam.setField("YD_STK_BED_NO"   , "");  
			//전송Data 조회
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L001", jrParam));

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
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
		String methodNm = "대차상태설정 등록처리[ACoilJspBakSeEJB.trtTcarStatSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sOPRN    = commUtils.nvl(commUtils.trim(gdReq.getParam("OPRN")), "N");	//영대차여부
			String trtDtlGp = commUtils.trim(gdReq.getParam("TRT_DTL_GP"));	//처리상세구분
			String ydEqpId  = commUtils.trim(gdReq.getParam("YD_EQP_ID" ));	//야드설비ID(대차)
			String modifier = commUtils.trim(gdReq.getParam("userid"      ));	//수정자
			String currDate = commUtils.getDateTime14();						//현재시각

			if ("".equals(ydEqpId)) {
				throw new YFUserException("대차설비ID가 없습니다.");
			}

			//Return Value
			JDTORecord jrRtn = null;

			//EJB Call을 위한 Message 생성용 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);

			jrYdMsg.setField("YD_EQP_ID", ydEqpId); //야드설비ID
			jrYdMsg.setField("YD_EQP_ID" , ydEqpId); //야드설비ID

			if ("ST".equals(trtDtlGp)) {
				//설비상태 변경
				jrYdMsg.setField("JMS_TC_CD"          , "F1YFL004"); //설비고장복구실적
				jrYdMsg.setField("YD_EQP_STAT"        , commUtils.trim(gdReq.getParam("YD_EQP_STAT"))); //야드설비상태(B:고장, N:정상)
				jrYdMsg.setField("YD_EQP_PAUSE_CODE"  , "0000"    ); //야드설비휴지코드
				jrYdMsg.setField("YD_EQP_TRBL_RCVR_DT", currDate  ); //야드설비고장복구일시

				EJBConnector sndConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvF1YFL004", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("MD".equals(trtDtlGp)) {
				//작업Mode 변경
				jrYdMsg.setField("JMS_TC_CD"      , "F1YFL003"); //설비운전모드전환
				jrYdMsg.setField("YD_EQP_WRK_MODE", commUtils.trim(gdReq.getParam("YD_EQP_WRK_MODE"))); //야드설비작업Mode(1:On-Line, 0:Off-Line)

				EJBConnector sndConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvF1YFL003", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("HB".equals(trtDtlGp)) {
				//Home동 변경 - Log ID, Method, 수정자 Set
				JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);

				jrParam.setField("YD_EQP_ID"   , ydEqpId); //야드설비ID
				
				// 홈위치정보 사용안함 2019.10.22
//				jrParam.setField("YD_HOME_LOC", ydEqpId.substring(0, 1) + commUtils.trim(gdReq.getParam("YD_HOME_BAY_GP")) + ydEqpId.substring(2));			
				
				jrParam.setField("YD_HOME_BAY_GP", commUtils.trim(gdReq.getParam("YD_HOME_BAY_GP")));
				// 설비 홈동 수정 
				commDao.update(jrParam, updEqpHomeBay, logId, methodNm, "Home동 변경");
				
			} else if ("CB".equals(trtDtlGp)) {
				//현재동 변경
				jrYdMsg.setField("YD_CURR_BAY_GP", commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP")));
				jrRtn = this.updTcarCurrBay(jrYdMsg);
			} else if ("TS".equals(trtDtlGp)) {
				//공대차출발지시 등록
				jrYdMsg.setField("YD_BAY_GP", commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TS"))); //야드동구분(상차동)
				jrYdMsg.setField("OPRN"     , sOPRN); //영대차여부
				jrRtn = comm.trtTcarSchLevWo(jrYdMsg);
			} else if ("TL".equals(trtDtlGp)) {
				//출발실적처리
				jrYdMsg.setField("JMS_TC_CD"      	, "F1YFL011"); //대차이동실적
				jrYdMsg.setField("YD_MOVE_GP"		, "S"       ); //야드대차이동구분(출발)
				jrYdMsg.setField("YD_TCAR_CURR_BAY" , commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TL"))); //야드동구분1(현재동)

				EJBConnector sndConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvF1YFL011", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("TA".equals(trtDtlGp)) {
				//도착실적처리
				jrYdMsg.setField("JMS_TC_CD"      	, "F1YFL011"); //대차이동실적
				jrYdMsg.setField("YD_MOVE_GP"		, "E"       ); //야드대차이동구분(도착)
				jrYdMsg.setField("YD_TCAR_CURR_BAY" , commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TA"))); //야드동구분1(현재동)

				EJBConnector sndConn = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvF1YFL011", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("TC".equals(trtDtlGp)) {
				//완료실적처리
				String ydCarProgStat = commUtils.trim(gdReq.getHeader("YD_CAR_PROG_STAT").getValue(0));	//야드차량진행상태
				
				if ("4".equals(ydCarProgStat)) {
				} else if ("D".equals(ydCarProgStat)) {
				}
			} else {
				throw new YFUserException("정의되지 않은 처리구분[" + trtDtlGp + "] 입니다.");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of trtTcarStatSet
	
	
	
	/**
	 * 대차이동구간변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updTCarYdGpMgt(GridData gdReq) throws DAOException {
		String methodNm = "대차이동구간변경[ACoilJspBakSeEJB.updTCarYdGpMgt] < " + gdReq.getNavigateValue();
		String logId  = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sUserId = commUtils.trim(gdReq.getParam("userid"));
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = null;

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			for (int ii = 0; ii < rowCnt; ii++) {
				jrParam = commUtils.getParam(logId, methodNm, sUserId);
				jrParam.setField("YD_EQP_ID"				,commUtils.getValue(gdReq, "YD_EQP_ID", ii));
				jrParam.setField("YD_TCAR_WRK_ABLE_BAY1"	,commUtils.getValue(gdReq, "YD_TCAR_WRK_ABLE_BAY1", ii));
				jrParam.setField("YD_TCAR_WRK_ABLE_BAY2"	,commUtils.getValue(gdReq, "YD_TCAR_WRK_ABLE_BAY2", ii));
				jrParam.setField("YD_TCAR_WRK_ABLE_BAY3"	,commUtils.getValue(gdReq, "YD_TCAR_WRK_ABLE_BAY3", ii));
				jrParam.setField("YD_TCAR_WRK_ABLE_BAY4"	,commUtils.getValue(gdReq, "YD_TCAR_WRK_ABLE_BAY4", ii));
				jrParam.setField("YD_TCAR_WRK_ABLE_BAY5"	,commUtils.getValue(gdReq, "YD_TCAR_WRK_ABLE_BAY5", ii));
				jrParam.setField("YD_TCAR_WRK_ABLE_BAY6"	,commUtils.getValue(gdReq, "YD_TCAR_WRK_ABLE_BAY6", ii));
				
				commDao.update(jrParam, updTCarYdGpMgt, logId, methodNm, "대차이동구간변경");
								
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
	 *      [A] 오퍼레이션명 : 대차작업현황조회 - 최대적치매수 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updStackMaxQnty(GridData gdReq) throws DAOException {
		String methodNm = "대차작업현황조회 - 최대적치매수 수정[ACoilJspBakSeEJB.updStackMaxQnty] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			JDTORecord jrParam = null;
			for (int ii = 0; ii < rowCnt; ii++) {
				//DAO Parameter - Log ID, Method, 수정자 Set
				jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
				 
				jrParam.setField("YD_EQP_ID"    , commUtils.trim(gdReq.getHeader("YD_EQP_ID").getValue(ii)) );
				jrParam.setField("STK_MAX_QNTY" , commUtils.trim(gdReq.getHeader("STK_MAX_QNTY").getValue(ii)) );
				jrParam.setField("STK_MAX_WT" , commUtils.trim(gdReq.getHeader("STK_MAX_WT").getValue(ii)) );
//				사용안함 2019.10.22
//				sEQP_DIR_TO_LOC = commUtils.trim(gdReq.getHeader("EQP_DIR_TO_LOC").getValue(ii));
//				jrParam.setField("EQP_DIR_TO_LOC" , sEQP_DIR_TO_LOC);				
				commDao.update(jrParam, updStackMaxQnty, logId, methodNm, "최대적치매수 수정");
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
	 *      [A] 오퍼레이션명 : 대차작업현황조회- 우선순위변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord modPriorChange(GridData gdReq) throws DAOException {
		String methodNm = "대차작업현황조회 순위변경[ACoilJspBakSeEJB.modPriorChange] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = null; 
			String sUserId = commUtils.trim(gdReq.getParam("userid"));
			
			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				jrParam = commUtils.getParam(logId, methodNm, sUserId);
				/**********************************************************
				* 1. 작업예약ID Check
				**********************************************************/ 
			    
				jrParam.setField("YD_SCH_PRIOR"    , commUtils.trim(gdReq.getHeader("YD_SCH_PRIOR").getValue(ii)));			    
				jrParam.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii)));
				jrParam.setField("YD_WRK_PLAN_TCAR", commUtils.trim(gdReq.getHeader("YD_WRK_PLAN_TCAR").getValue(ii)));
				jrParam.setField("YD_BAY_GP"       , commUtils.trim(gdReq.getHeader("LD_BAY").getValue(ii)));
				jrParam.setField("YD_AIM_BAY_GP"   , commUtils.trim(gdReq.getHeader("UD_BAY").getValue(ii)));
			    
				commUtils.printLog(logId, "우선순위변경 [ " 
							+ gdReq.getHeader("YD_SCH_CD").getValue(ii) + " >> " 
							+ gdReq.getHeader("YD_SCH_PRIOR").getValue(ii) + " ]", "SL");

				/**********************************************************
				* 2. 작업예약 및 크레인스케줄 Table에  우선순위를 Update
				**********************************************************/
				commDao.update(jrParam, updSchPriorModWrkBook, logId, methodNm, "TB_YF_WRKBOOK");				
				
				// 크레인스케줄 우선순위 수정
				commDao.update(jrParam, updSchPriorModCrnSch, logId, methodNm,  "TB_YF_CRNSCH");				
				
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
		String methodNm = "-작업예약삭제[ACoilJspBakSeEJB.delWrkBookDel] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			String sUserId = commUtils.trim(gdReq.getParam("userid"));
			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet rst = null;
			JDTORecord jrParam = null;
			
			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				//DAO Parameter - Log ID, Method, 수정자 Set
				jrParam = commUtils.getParam(logId, methodNm, sUserId);
				
				jrParam.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii)));
				jrParam.setField("YD_WRK_PLAN_TCAR", commUtils.trim(gdReq.getHeader("YD_WRK_PLAN_TCAR").getValue(ii)));
				jrParam.setField("YD_BAY_GP"       , commUtils.trim(gdReq.getHeader("LD_BAY").getValue(ii)));
				jrParam.setField("YD_AIM_BAY_GP"   , commUtils.trim(gdReq.getHeader("UD_BAY").getValue(ii)));
				 
				rst = commDao.select(jrParam, getDelWrkBookList, logId, methodNm, "작업예약삭제대상조회");
				
				if (rst.size() == 0) {
					return jrRtn;
				}
				
				for (int jj = 0; jj < rst.size(); ++jj) {
					jrParam.setField("YD_WBOOK_ID"  , rst.getRecord(jj).getFieldString("YD_WBOOK_ID"));
					
					/**********************************************************
					* 작업예약 취소
					**********************************************************/
					this.trtWrkBookCncl(jrParam);
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
	 *      [A] 오퍼레이션명 : 이송지시 취소
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updFtmvWrkCancel(GridData gdReq) throws DAOException {
		String methodNm = "이송지시 취소[ACoilJspBakSeEJB.updFtmvWrkCancel] < " + gdReq.getNavigateValue();
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
				
				stlNo 		 = commUtils.getValue(gdReq, "STL_NO", ii);
				sposWlocCd   = commUtils.getValue(gdReq, "SPOS_WLOC_CD", ii);
				arrWlocCd 	 = commUtils.getValue(gdReq, "ARR_WLOC_CD", ii);
				ordYeojaeGp  = commUtils.getValue(gdReq, "ORD_YEOJAE_GP", ii);
				reWoLmtRsnCd = commUtils.getValue(gdReq, "RE_WO_LMT_RSN_CD", ii);
				
				/**********************************************************
				* 3. 재료단위 이송지시 취소 작업 전문
				**********************************************************/
				if("X".equals(reWoLmtRsnCd)){
					reWoLmtRsnCd = "";
					reWoLmtYn = "N";
				}else{
					reWoLmtYn = "Y";
				}
				
				//재료단위 이송지시 취소 작업 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

				jrYdMsg.setField("JMS_TC_CD"       , YfConstant.YDPTJ007);	//재료단위 이송지시 취소 작업
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
				jrYdMsg.setField("STL_NO", stlNo);
				jrYdMsg.setField("SPOS_WLOC_CD", sposWlocCd);
				jrYdMsg.setField("ARR_WLOC_CD", arrWlocCd);
				jrYdMsg.setField("ORD_YEOJAE_GP", ordYeojaeGp);
				jrYdMsg.setField("RE_WO_LMT_RSN_CD", reWoLmtRsnCd); //그리드 콤보 값
				jrYdMsg.setField("RE_WO_LMT_YN", reWoLmtYn); //기본값 Y - RE_WO_LMT_RSN_CD X값이면 N
				jrYdMsg.setField("CANCEL_DATE", commUtils.getDate8());

				// 진행관리 {call SP_PTG_001(:v_JMS_TC_CD,:v_CURR_PROG_REG_DDTT,:v_STL_NO,:v_REWO_LMT_RSN_CD,:v_REWO_LMT_YN,:v_CANCEL_DATE)}
				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL3(YfConstant.YDPTJ007, jrYdMsg));
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
	 *      [A] 오퍼레이션명 : 이송지시 취소후 후처리작업 (스케줄 취소, 작업예약 취소)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updFtmvWrkCancel2(GridData gdReq) throws DAOException {
		String methodNm = "이송지시 취소후 후처리작업[ACoilJspBakSeEJB.updFtmvWrkCancel2] < " + gdReq.getNavigateValue();
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
		    String sUserId = commUtils.trim(gdReq.getParam("userid"));
		    
		    JDTORecordSet rsResult = null;
		    
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = null;
			
			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[] arrYdWbookId = new String[rowCnt];
			
			//크레인 스케줄 취소 ------------------------------------------------------------------------------
			for (int ii = 0; ii < rowCnt; ii++) {
				
				jrParam = commUtils.getParam(logId, methodNm, sUserId );
				stlNo 		 = commUtils.getValue(gdReq, "STL_NO", ii);
				jrParam.setField("STL_NO", stlNo);
				rsResult = commDao.select(jrParam, getSchInfo, logId, methodNm, "스케줄조회");
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
						
						if( "PT11UM".equals(ydSchCd.substring(2,8)) //소재이송상차(L)
							|| "PT12UM".equals(ydSchCd.substring(2,8)) //소재이송상차(R)
//								|| "PT21UM".equals(ydSchCd.substring(2,8)) //제품이송상차(L)
//								|| "PT22UM".equals(ydSchCd.substring(2,8)) //제품이송상차(R)
						){
//						if("PT02UM".equals(ydSchCd.substring(2,8))||"PT06UM".equals(ydSchCd.substring(2,8))) {
							//이송상차 스케줄만 취소처리한다.
						
							commUtils.printLog(logId, "작업취소 [ STL_NO:"+ stlNo +" ,작업예약ID:"+ ydWbookId + " 관련 크레인 스케줄 ]", "SL");
			
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

				jrParam = commUtils.getParam(logId, methodNm, sUserId );
				jrParam.setField("STL_NO", stlNo);
				rsResult = commDao.select(jrParam, getWrkBookInfo, logId, methodNm, "작업예약 조회");
				if (rsResult == null || rsResult.size() <= 0) {
					 
			    }else{
					ydWbookId  = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_WBOOK_ID")); //야드크레인스케쥴ID
					ydSchCd    = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_SCH_CD")); //야드크레인스케쥴ID
					ydCrnSchId = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID")); //야드크레인스케쥴ID
					
					if(ydSchCd.length() >= 8 && "".equals(ydCrnSchId)) {
						// 제품까지 포함할것인지? 
						if(
								"PT11UM".equals(ydSchCd.substring(2,8)) //소재이송상차(L)
								|| "PT12UM".equals(ydSchCd.substring(2,8)) //소재이송상차(R)
//								|| "PT21UM".equals(ydSchCd.substring(2,8)) //제품이송상차(L)
//								|| "PT22UM".equals(ydSchCd.substring(2,8)) //제품이송상차(R)
						
						) {
							//이송상차 스케줄만 취소처리한다.
						
							commUtils.printLog(logId, "작업취소 [ STL_NO:"+ stlNo +" ,작업예약ID:"+ ydWbookId + " ]", "SL");
			
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
		String methodNm = "긴급작업[ACoilJspBakSeEJB.updUgntWrk] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

		    String stlNo = "";
		    String sUserId = commUtils.trim(gdReq.getParam("userid"));
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = null;
			
			jrParam = commUtils.getParam(logId, methodNm, sUserId);
			commDao.update(jrParam, updTSmatlftmvwoUgntgp, logId, methodNm, "기존 긴급재 삭제");
			
			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrParam = commUtils.getParam(logId, methodNm, sUserId);
				stlNo 	= commUtils.getValue(gdReq, "STL_NO", ii);

				jrParam.setField("STL_NO", stlNo); 
				commDao.update(jrParam, updTSmatlftmvwoUgntCHK, logId, methodNm, "새로운 긴급재 편성");
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
		String methodNm = "상차대상순위별조회 - SCH기동[ACoilJspBakSeEJB.reqCarldSchSt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
	
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet rsResult = null;
			
			String sSTL_NO 				= "";
			String szTRN_EQP_CD 		= "";
			String szYD_CAR_SCH_ID 		= "";
			String szYD_CAR_PROG_STAT 	= "";
			String szYD_CARLD_STOP_LOC	= "";
			String szYD_GP				= "";
			String szYD_BAY_GP			= "";
			String sYD_STK_COL_GP 		= ""; //적치 열 구분
			String sYD_STK_BED_NO 		= ""; //적치 BED 구분
			String sYD_STK_LYR_NO 		= ""; //적치 단 구분
			String sYD_SCH_CD 			= "";
			String sYD_WBOOK_ID 		= "";
			String sYD_SCH_PRIOR 		= "";	//야드스케쥴우선순위
			String sYD_SCH_REQ_GP 		= "";	//야드스케줄요청구분
			String sPoint				= "";
			String sGP					= ""; //소재,제품 구분
			String sGPYdCode            = "";
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				sSTL_NO = commUtils.getValue(gdReq, "STL_NO", ii);
				
				/**********************************************************
				* 1. 이전 작업예약 존재 유무 Check
				**********************************************************/
				jrParam.setField("STL_NO", sSTL_NO); 
				rsResult = commDao.select(jrParam, getWBookID3, logId, methodNm, " 이전 작업예약 존재 유무 Check");
				if(rsResult.size() > 0) {
					throw new YFUserException("COIL번호 '"+sSTL_NO+"' 는 작업예약 ID:"+commUtils.trim(rsResult.getRecord(0).getFieldString("YD_WBOOK_ID"))+" 에 등록된 COIL 입니다!");
				}
				
				szTRN_EQP_CD = commUtils.trim(gdReq.getParam("TRN_EQP_CD"));
				/**********************************************************
				* 2. 차량정보 검색
				**********************************************************/
				jrParam.setField("YD_CAR_USE_GP", "L");
				jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
				jrParam.setField("STL_NO"		, sSTL_NO); 
				rsResult = commDao.select(jrParam, getListSposYNchk3, logId, methodNm, " 운송장비 코드와 재료번호로 이송대상 차량스케줄 조회");
				if(rsResult.size() > 0) {
					szYD_CAR_SCH_ID 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
					szYD_CAR_PROG_STAT	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_PROG_STAT"));
					szYD_CARLD_STOP_LOC = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CARLD_STOP_LOC"));
					szYD_GP 			= szYD_CARLD_STOP_LOC.substring(0, 1);
					szYD_BAY_GP		= szYD_CARLD_STOP_LOC.substring(1, 2);
				} else {
					throw new YFUserException("COIL번호: '"+sSTL_NO+"', 운송장비코드: " + szTRN_EQP_CD + " 로 차량스케줄을 찾지 못했습니다!!");
				}
				
				if("2".equals(szYD_CAR_PROG_STAT) || "3".equals(szYD_CAR_PROG_STAT) || "4".equals(szYD_CAR_PROG_STAT)) {
					//2:상차도착 ,3:상차검수, 4:상차개시
					
					/**********************************************************
					* 3. TB_YM_STACKLAYER 에 적치상태  Check
					*    - 적치열,bed,단 정보를 가져온다.
					**********************************************************/
					jrParam.setField("STL_NO", sSTL_NO);
					rsResult = commDao.select(jrParam, getselectYdStkColGp, logId, methodNm, "TB_YF_STKLYR 에 적치상태  Check");
					if (rsResult == null || rsResult.size() <= 0) {
						throw new YFUserException("TB_YF_STKLYR(YF_적치단) 에 존재하지 않는 COIL_NO 입니다!");
					} else {
						String sSTACK_LAYER_STAT = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_LYR_STAT"));
						if(!"C".equals(sSTACK_LAYER_STAT) && !"L".equals(sSTACK_LAYER_STAT)) {
							throw new YFUserException("적치단 상태가 '적치중(C)' 이 아닙니다! 현재 상태 : " + sSTACK_LAYER_STAT);
						}
						
						sYD_STK_COL_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP")); //적치 열 구분
						sYD_STK_BED_NO 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_BED_NO")); //적치 BED 구분
						sYD_STK_LYR_NO	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_LYR_NO")); //적치 단 구분
						
					}
					
					/**********************************************************
					* 3. 스케줄 코드, 작업예약 ID 생성
					*    - 저장품 이동 조건 지정
					*    - 야드스케쥴 우선순위 검색
					**********************************************************/
					sPoint = szYD_CARLD_STOP_LOC.substring(5,6);
					
					sYD_SCH_CD 	= szYD_GP + szYD_BAY_GP;
					sYD_SCH_REQ_GP = "";
					
					// 소재(CM) : 1, 제품(CG) : 2
					sGPYdCode = ("CM".equals(sGP))?"1":"2";
					
					// 스케쥴 코드 생성
					if("1".equals(sPoint)||"2".equals(sPoint)) {
						sYD_SCH_CD += "PT"+sGPYdCode+"1UM"; // 이송상차(L)
					}else{
						sYD_SCH_CD += "PT"+sGPYdCode+"2UM"; // 이송상차(R)
					}
					
					sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook"); 
					
					//스케줄코드로 스케줄기준Table조회
					jrParam.setField("YD_SCH_CD", sYD_SCH_CD); 
					rsResult = commDao.select(jrParam, getYdSchrule, logId, methodNm, "스케줄 기준 조회"); 
			    	
					if (rsResult != null && rsResult.size() > 0) {
						sYD_SCH_PRIOR = rsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
					} else {
						throw new YFUserException("코일["+sSTL_NO+"] 스케쥴 코드 이상 : [" + sYD_SCH_CD + "]");
					}			
					
					/**********************************************************
					* 4. 작업예약(TB_YM_WRKBOOK) 생성
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
					jrParam.setField("YD_GP"			, szYD_GP);
					jrParam.setField("YD_BAY_GP"		, szYD_BAY_GP);
					jrParam.setField("YD_SCH_CD"		, sYD_SCH_CD); //야드스케쥴코드
					jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
					jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
					jrParam.setField("YD_SCH_ST_GP"		, "M"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
					jrParam.setField("YD_SCH_REQ_GP"	, sYD_SCH_REQ_GP); //야드스케쥴요청구분
					jrParam.setField("YD_CAR_USE_GP"	, "L"); //L:구내운송
					jrParam.setField("TRN_EQP_CD"		, szTRN_EQP_CD); //운송장비코드
					
					commDao.insert(jrParam, insWrkBook, logId, methodNm, "작업예약(TB_YF_WRKBOOK) 생성");
		
					/**********************************************************
					* 5. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
					jrParam.setField("STL_NO"			, sSTL_NO);
					jrParam.setField("YD_STK_COL_GP"	, sYD_STK_COL_GP);
					jrParam.setField("YD_STK_BED_NO"	, sYD_STK_BED_NO);
					jrParam.setField("YD_STK_LYR_NO"	, sYD_STK_LYR_NO);
					 
					commDao.insert(jrParam, insYfWrkBookMtl, logId, methodNm, "작업예약재료(TB_YF_WRKBOOKMTL) 생성");
					
					/**********************************************************
					* 6. 크레인스케줄 전문 호출
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID"  	, sYD_WBOOK_ID); //야드작업예약ID
					jrParam.setField("YD_SCH_CD"    	, sYD_SCH_CD  ); //야드스케쥴코드
					jrParam.setField("YD_SCH_ST_GP" 	, "M"      	); //야드스케쥴기동구분
					jrParam.setField("YD_SCH_REQ_GP"	, sYD_SCH_REQ_GP); //야드스케쥴요청구분
					
					jrRtn = commUtils.addSndData(jrRtn, comm.getCrnSchMsg(jrParam));
					
					
				} else {
					throw new YFUserException("차량스케줄ID:" + szYD_CAR_SCH_ID + " 의 차량진행상태가 '" + szYD_CAR_PROG_STAT + "' 로 상차상태(2,3,4)가 아닙니다!!");
				}
				
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of reqCarldSchSt
	
	/**
	 * 하차백업생성
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord mkUdCarSch(GridData gdReq) throws DAOException {
		String methodNm = "하차백업생성[ACoilJspBakSeEJB.mkUdCarSch] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			/*
			 * 1.차량 스케줄, 차량재료 리셋
			 */
			jrParam.setField("TRN_EQP_CD", gdReq.getParam("TRN_EQP_CD"));
			
			EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
			sndConn.trx("delCarSchInfo", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			/*
			 * 2.하차출발 생성
			 */
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
			
			commDao.insert(jrParam, insCarSchLd, logId, methodNm, "차량스케쥴 상차출발(5)로 INSERT ");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of mkUdCarSch
	
	/**
	 *      [A] 오퍼레이션명 : 이송차량 실적처리 팝업 - 등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtMvCarStatSet2(GridData gdReq) throws DAOException {
		String methodNm = "이송차량 실적처리 팝업 - 등록 [ACoilJspBakSeEJB.trtMvCarStatSet2] < " + gdReq.getNavigateValue();
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
				throw new YFUserException("운송장비코드가 없습니다.");
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

				EJBConnector sndConn = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvTSYDJ003", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				
			} else if("TSYDJ004".equals(sJMS_TC_CD)) { //소재차량출발
				
				jrYdMsg.setField("TRN_EQP_CD"   	  		, sTRN_EQP_CD);
				jrYdMsg.setField("TRN_WRK_FULLVOID_GP"		, sTRN_WRK_FULLVOID_GP);
				jrYdMsg.setField("TRN_EQP_STK_CAPA"		    , YfConstant.TRN_EQP_STK_CAPA);
				
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

				EJBConnector sndConn = new EJBConnector("default", "YfCommCarMvBakSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvTSYDJ004", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

			} else if("YDTSJ007".equals(sJMS_TC_CD)) { //소재차량상차개시
				
				//차량진행상태를 하차완료로 설정
				jrParam.setField("YD_CAR_PROG_STAT"		, "4"		);  //상차개시
				jrParam.setField("YD_CARLD_ST_DT"		, currDate	);  //상차개시일시 ""이면 이전값 유지
				jrParam.setField("YD_CARLD_CMPL_DT"		, ""		);  //상차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_ST_DT"		, ""		);  //하차개시일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_CMPL_DT"		, ""		);  //하차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CAR_SCH_ID"		, sYD_CAR_SCH_ID); 
				
				commDao.update(jrParam, updMvCarSchCmpl, logId, methodNm, "이송차량스케줄 상차개시로 수정");
				
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
				
				commDao.update(jrParam, updMvCarSchCmpl, logId, methodNm, "이송차량스케줄 상차완료로 수정");
				
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
				
				commDao.update(jrParam, updMvCarSchCmpl, logId, methodNm, "이송차량스케줄 하차개시로 수정");
				
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
				
				commDao.update(jrParam, updMvCarSchCmpl, logId, methodNm, "이송차량스케줄 하차완료로 수정");
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
				JDTORecordSet jsCol = commDao.select(jrParam, getYdPntByStkColGp, logId, methodNm, "야드적치열구분으로 차량포인트 정보 조회");
				
				if(jsCol != null && jsCol.size() > 0) {
					sWLOC_CD	= commUtils.trim(jsCol.getRecord(0).getFieldString("WLOC_CD"));
					sYD_PNT_CD	= commUtils.trim(jsCol.getRecord(0).getFieldString("YD_PNT_CD"));
					
					if("".equals(sWLOC_CD) || "".equals(sYD_PNT_CD)) {
						
						throw new YFUserException(sTO_LOC + " 의 개소코드 또는 야드포인트에 NULL 값이 있습니다.");
					}
					
					if(!"".equals(commUtils.trim(jsCol.getRecord(0).getFieldString("TRN_EQP_CD")))) {
						
						throw new YFUserException(sTO_LOC + " 에 이미 " + commUtils.trim(jsCol.getRecord(0).getFieldString("TRN_EQP_CD")) + " 운송장비가 점유하고  있습니다.");
					}
					
					if(!"".equals(commUtils.trim(jsCol.getRecord(0).getFieldString("CAR_NO")))) {
						
						throw new YFUserException(sTO_LOC + " 에 이미 " + commUtils.trim(jsCol.getRecord(0).getFieldString("CAR_NO")) + " 차량이 점유하고  있습니다.");
					}
					
				} else {
					throw new YFUserException(sTO_LOC + " 의 개소코드와 야드포인트를 TB_YD_CARPOINT 에서 찾지 못했습니다.");
				}
				
				//차량위치 예정정보 삭제 정리 -예약으로 잡혀있는정보 Clear
				jrParam.setField("CAR_CARD_NO",	sTRN_EQP_CD);	//운송장비코드
				commDao.update(jrParam, updStackStatByTrnEqpCd, logId, methodNm, "TB_YF_STKCOL 차량위치 예정정보 삭제 정리 -예약으로 잡혀있는정보 Clear ");

				//차량 포인트 예약으로 잡혀있는정보 Clear
				jrParam.setField("TRN_EQP_CD",	sTRN_EQP_CD);	//운송장비코드
				commDao.update(jrParam, updPlnInfoReSet, logId, methodNm, "TB_YD_CARPOINT 차량 포인트 예약으로 잡혀있는정보 Clear ");
				
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
				commDao.update(jrParam, updMvCarSchPntWo, logId, methodNm, "차량포이트 지시 수정");
				
				//TB_YF_STKCOL 예약정보등록 
				jrParam.setField("STACK_STAT"	, "L"); 
				jrParam.setField("CAR_CARD_NO"	, sTRN_EQP_CD);
				jrParam.setField("YD_STK_COL_GP"	, sTO_LOC);
				commDao.update(jrParam, updateEquipcolStat, logId, methodNm, "TB_YF_STKCOL 예약정보등록");
				
				//TB_YD_CARPOINT 포인트지시 예약하기
		        EJBConnector ejbConn1 = new EJBConnector("default","YfCommCarMvBakSeEJB",this);
				ejbConn1.trx("YfCarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class},
			  	             new Object[]{"3","",sTRN_EQP_CD,sTO_LOC,"","","R",logId,methodNm});
				
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
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
		String methodNm = "이송작업재료등록[ACoilJspBakSeEJB.updCarFtMvMtl] < " + gdReq.getNavigateValue();
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
				
				//TB_YF_STOCK 에 존재 하는지 확인
				jrParam.setField("STL_NO" , sCoilNo); 
				rsResult = commDao.select(jrParam, getYfStockchk, logId, methodNm, "TB_YF_STOCK 에 존재 하는지 확인");
				
				if(rsResult.size()<=0) {
					
					//코일공통의 차공정코드로 이동조건 설정, 리턴 값이 ""이면 코일공통에 존재하지 않는 코일 번호로 Error 처리 한다.
					sStockMv	= comm.getStockMv(logId, methodNm, sCoilNo);
					
					if("".equals(sStockMv)) {
						throw new YFUserException("코일공통에 존재하지 않는 COIL_NO : " + sCoilNo);
					}
					
					//TB_YM_STOCK에 존재 한지 않으면 생성한다.
					jrParam.setField("STL_NO" 			, sCoilNo);
					jrParam.setField("STOCK_ITEM" 		, YfConstant.ITEM_CM);
					jrParam.setField("STOCK_MOVE_TERM" 	, sStockMv);
					commDao.insert(jrParam, insStock, logId, methodNm, "TB_YF_STOCK 생성");
				}
				
				//이송작업재료삭제
				jrParam.setField("YD_CAR_SCH_ID"	, commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
				jrParam.setField("STL_NO"			, commUtils.getValue(gdReq, "OLD_SSTL_NO", ii)); 
 
				commDao.insert(jrParam, delCarFtMvMtl, logId, methodNm, "이송작업재료삭제");
				
				//이송작업재료등록
				jrParam.setField("STL_NO"			, sCoilNo); 
				jrParam.setField("YD_CAR_SCH_ID"	, commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
				jrParam.setField("YD_STK_BED_NO"	, commUtils.getValue(gdReq, "YD_STK_BED_NO", ii)); 
				jrParam.setField("YD_STK_LYR_NO"	, commUtils.getValue(gdReq, "YD_STK_LYR_NO",ii)); 
				jrParam.setField("MODIFIER"	        , commUtils.trim(gdReq.getParam("userid"))); 
				commDao.insert(jrParam, updCarFtMvMtl, logId, methodNm, "이송작업재료등록");
			}
			
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
		String methodNm = "이송작업재료삭제[ACoilJspBakSeEJB.delCarFtMvMtl] < " + gdReq.getNavigateValue();
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
				commDao.insert(jrParam, delCarFtMvMtl, logId, methodNm, "이송작업재료삭제");
			}
			
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
		String methodNm = "이송작업재료위치변경[ACoilJspBakSeEJB.chgCarFtMvMtl] < " + gdReq.getNavigateValue();
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
				commDao.insert(jrParam, delCarFtMvMtl, logId, methodNm, "이송작업재료위치변경");
			}
			
			for (int ii = 0; ii < rowCnt; ii++) {
				//이송작업재료등록
				jrParam.setField("STL_NO"			, commUtils.getValue(gdReq, "OLD_SSTL_NO", ii)); 
				jrParam.setField("YD_CAR_SCH_ID"	, commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
				jrParam.setField("YD_STK_BED_NO"  	, commUtils.getValue(gdReq, "YD_STK_BED_NO", ii)); 
				jrParam.setField("YD_STK_LYR_NO"	, commUtils.getValue(gdReq, "YD_STK_LYR_NO",ii)); 
				jrParam.setField("MODIFIER"	        , commUtils.trim(gdReq.getParam("userid"))); 
				commDao.insert(jrParam, updCarFtMvMtl, logId, methodNm, "이송작업재료등록");
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
	 * 산적위치수정 - 저장품 생성
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord insStockInfo(JDTORecord rcvMsg) throws DAOException {
		
		String methodNm = "산적위치수정-저장품생성[ACoilJspBakSeEJB.insStockInfo] <" + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			String sYD_GP       = rcvMsg.getFieldString("YD_GP");
			String sSTL_NO    = rcvMsg.getFieldString("STL_NO");
			
			String sMODIFIER = commUtils.trim(rcvMsg.getFieldString("userid"));
			if("".equals(sMODIFIER)){
				sMODIFIER = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));
			}
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, sMODIFIER);
			jrParam.setField("YD_GP"	 , sYD_GP   );
			jrParam.setField("STL_NO"  , sSTL_NO);		
			 
			commDao.update(jrParam, insertStockTransInfo, logId, methodNm, "TB_YF_STOCK 저장품 생성");
			
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
		
		String methodNm = "산적위치수정-수정[ACoilJspBakSeEJB.updStkLoc] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		JDTORecordSet dmRc = null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sMODIFIER = StringHelper.evl(commUtils.trim(gdReq.getParam("userid")), "SYSTEM");
			
			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			String sYD_GP       = gdReq.getParam("YD_GP");

			String sSTL_NO    = gdReq.getParam("STL_NO");
			String sFROM_ADDR   = gdReq.getParam("FROM_ADDR");  //from 위치
			String sYD_STR_LOC  = gdReq.getParam("YD_STR_LOC"); //TO 위치
			String sFTMV_BKUP   = ""; //gdReq.getParam("FTMV_BKUP");  //이송유무
			String sYD_BED_GP  = StringHelper.evl(gdReq.getParam("YD_BED_GP") , sYD_STR_LOC.substring(6,  8));
			String sYD_LYR_GP  = StringHelper.evl(gdReq.getParam("YD_LYR_GP") , sYD_STR_LOC.substring(8, 10));
			String sYD_YD_STK_COL_GP = sYD_STR_LOC.substring(0,  6);
			String sYD_STOCK_YN      = gdReq.getParam("YD_STOCK_YN"     );     
			String sTO_YD_GP3 = commUtils.nvl(gdReq.getParam("TO_YD_GP3"), "N");//타야드 여부

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("YD_GP"	 , sYD_GP   );
			jrParam.setField("STL_NO"  , sSTL_NO);
			
			/************************
			 * 저장품 생성
			 ************************/
			if ("N".equals(sYD_STOCK_YN)) {
				
//				트랜지션 분리
				EJBConnector ejbConnS = new EJBConnector("default", "ACoilJspBakSeEJB", this);
				jrRtn = (JDTORecord)ejbConnS.trx("insStockInfo", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			}
			
			/**************************************************
			 * FROM위치와 TO위치가 같은 경우 종료 
			 * (저장품이 사라졌을 경우 생성후 종료)
			 **************************************************/
			if (sFROM_ADDR.equals(sYD_STR_LOC)) {
				return jrRtn;
			} 
			jrParam.setField("YD_STK_COL_GP"   	,sYD_YD_STK_COL_GP);
			jrParam.setField("YD_STK_BED_NO"   	,sYD_BED_GP);
			jrParam.setField("YD_STK_LYR_NO"  	,sYD_LYR_GP);
			JDTORecordSet jsLyrInfo = commDao.select(jrParam, getStackLayerInfo, logId, methodNm, "신규저장위치 조회");
			
			if (jsLyrInfo.size() == 0) {
				throw new YFUserException("동일한 산적위치로 수정을 할 수 엇습니다.  산적위치 이상 [" + sYD_YD_STK_COL_GP + sYD_BED_GP + sYD_LYR_GP + "]");
			}
			
			/************************************************
			 *  COIL 산적위치 수정
			 ************************************************/
			JDTORecord jparam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jparam.setResultCode(logId);	//Log ID
			jparam.setResultMsg(methodNm);	//Log Method Name
			jparam.setField("STL_NO"  , sSTL_NO);  //저장품
			jparam.setField("FROM_ADDR" , sFROM_ADDR); //from위치
			jparam.setField("YD_STR_LOC", sYD_STR_LOC);//to위치
			jparam.setField("FTMV_BKUP" , sFTMV_BKUP); //이송백업여부
			jparam.setField("TO_YD_GP3" , sTO_YD_GP3); //타야드 이적 여부
			

			// COIL 산적위치 수정 메소드(트랜젝션 분리 작업)
			commUtils.printLog(logId, "COIL 산적위치 수정 START", "[INFO]+");
			
//			트랜지션 분리
			EJBConnector ejbConn1 = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			jrRtn = (JDTORecord)ejbConn1.trx("changeCoilLocationInfo", new Class[] { JDTORecord.class }, new Object[] { jparam });
//			
			commUtils.printLog(logId, "COIL 산적위치 수정 END", "[INFO]-");
			
			
			/************************************************
			 * 이송백업 START
			 ************************************************/
			jrParam.setField("STL_NO"  , sSTL_NO);
			JDTORecord rs = commDao.select(jrParam, getIsFtmvBkup, logId, methodNm, "이송 백업대상여부").getRecord(0);
			sFTMV_BKUP = rs.getFieldString("IS_FTMV_BKUP");

			
			commUtils.printLog(logId, "○○○이송백업 실적처리 START", "[INFO]+");
			commUtils.printLog(logId, "○○○sFTMV_BKUP = "+sFTMV_BKUP, "[INFO]+");
			if ("Y".equals(sFTMV_BKUP)) { 
				
				JDTORecord tcRecord = JDTORecordFactory.getInstance().create(); 
	     
			    /*********************
			     * 실적BACKUP처리 CALL
			     *********************/ 
			    tcRecord.setField("COIL_NO", sSTL_NO);
			    
				//Coil공통 테이블 업데이트
			    commUtils.printLog(logId, "□□□TB_PT_COILCOMM UPDATE START", "[INFO]+");
			    
			    EJBConnector ejbConnPT = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
				ejbConnPT.trx("UpdCoilComProg", new Class[] { JDTORecord.class }, new Object[] { tcRecord });
				
				commUtils.printLog(logId, "□□□TB_PT_COILCOMM UPDATE START", "[INFO]+");
				
				
				/***********************************************
				 * 저장품제원 : 코일야드L2로 송신(YFF1L002)
				 ***********************************************/
				commUtils.printLog(logId, "YFF1L002 JMS전송", "[INFO]");
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("TC_CD"          , "YFF1L002");
				jrYdMsg.setField("YD_INFO_SYNC_CD", "5");
				jrYdMsg.setField("STL_NO"       , sSTL_NO);
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L002", jrYdMsg));

				
				/*************************************************
				 * 저장품 이동조건 업데이트 
				 *************************************************/
			    JDTORecord rVal = this.getCoilCurrProgCd(sSTL_NO, logId, methodNm);
			    String sSTOCK_MOVE_TERM  = rVal.getFieldString("STOCK_MOVE_TERM");
			    tcRecord.setField("STOCK_MOVE_TERM", sSTOCK_MOVE_TERM);
			    tcRecord.setField("MODIFIER"       , sMODIFIER);
			    tcRecord.setField("STL_NO"       , sSTL_NO);
			    commDao.update(tcRecord, updYdStock, logId, methodNm, "저장품 이동조건");
			    
			    
			    /***********************************************
			     * YDPTJ002 코일소재 이송완료실적BACKUP처리
			     ***********************************************/
			    tcRecord.setField("COIL_NO", sSTL_NO);
			    JDTORecord stlRecord = commDao.select(tcRecord, getCOILCOMM,logId, methodNm, "코일공통조회").getRecord(0);
			    String sSTL_APPEAR_GP =StringHelper.evl(stlRecord.getFieldString("STL_APPEAR_GP"), "");
			    
			    if (!"Y".equals(sSTL_APPEAR_GP)) {
					jparam.setField("STL_NO", sSTL_NO);
			    	
			    	//TB_PT_STLFRTOMOVE 테이블 업데이트
				    EJBConnector ejbConnPT2 = new EJBConnector("default", "YfCommBakSeEJB", this);
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
			 *  
			 *********************************************/ 
			jrParam.setField("STL_NO", sSTL_NO);
			dmRc = commDao.select(jrParam, getMsgYMDM001Info,logId, methodNm, "YDDMR001전문생성조회");

			// L3 -> 출하 -> 진행관리 -> 진도변경
			if (dmRc.size() > 0) {

				String sPut_Position = StringHelper.evl(dmRc.getRecord(0).getFieldString("PUT_POSITION"), "");
				String sCURR_PROG_CD = StringHelper.evl(dmRc.getRecord(0).getFieldString("CURR_PROG_CD"), "");
			
				String sYardGp = sPut_Position.substring(0, 1);
				commUtils.printLog(logId, "내부IF호출 : YDDMR001(일관제철 코일입고작업실적)","[INFO]");
                //코일입고작업실적
				JDTORecord tcRecordDM = JDTORecordFactory.getInstance().create();
				tcRecordDM.setField("JMS_TC_CD"         , "YDDMR001");
				tcRecordDM.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
				tcRecordDM.setField("TC_CODE"           , "YDDMR001");
				tcRecordDM.setField("TC_CREATE_DDTT"    , commUtils.getDateTime14());
				tcRecordDM.setField("RECEIPT_DATE"      , commUtils.getDate8());
				tcRecordDM.setField("RECEIPT_TIME"      , commUtils.getTime6());
				tcRecordDM.setField("GOODS_NO"    , sSTL_NO);
				tcRecordDM.setField("YD_GP"       , sYardGp);
				tcRecordDM.setField("STORE_LOC"   , sPut_Position);
				tcRecordDM.setField("CURR_PROG_CD", sCURR_PROG_CD);
				
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
		
		String methodNm = "산적위치수정[ACoilJspBakSeEJB.changeCoilLocationInfo] < " + jparam.getResultMsg();
		String logId    = jparam.getResultCode();
		
		String sSTL_NO  = jparam.getFieldString("STL_NO"); 
		String sUpLoc     = jparam.getFieldString("FROM_ADDR"); // FROM위치
		String sPutLoc    = jparam.getFieldString("YD_STR_LOC");// TO위치
		String sMODIFIER  = commUtils.nvl(jparam.getFieldString("MODIFIER"), "updStkLoc");
		
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
			JDTORecord jrStockInfo  = this.getCoilCurrProgCd(sSTL_NO, logId, methodNm);
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
			jrParam.setField("YD_STK_COL_GP"  , sPutStackColGp);
			jrParam.setField("YD_STK_BED_NO"  , sPutStackBedGp);
			jrParam.setField("YD_STK_LYR_NO", sPutStackLayerGp);
		 	
			JDTORecordSet jsPutLocInfo = commDao.select(jrParam, getYfStkLyrInfoWithPk, logId, methodNm, "산적위치 조회");
			
			if (jsPutLocInfo.size() <= 0) {
				throw new YFUserException("산적위치 수정=> To위치정보가 잘못 입력되었습니다.");
			}

			if (YfConstant.STACK_LAYER_GP_01.equals(sPutStackLayerGp)
			 || YfConstant.STACK_LAYER_GP_02.equals(sPutStackLayerGp)) {
				
			} else {
				throw new YFUserException("산적위치 수정=> 적치단(01단/02단) 정보가 잘못 입력되었습니다.");
			}
			
			
			/********************************
			 * 1. 작업예약 유무 체크
			 ********************************/ 
			jrParam.setField("STL_NO", sSTL_NO);
			JDTORecordSet stockV = commDao.select(jrParam, getYfStockInfoByPk, logId, methodNm, "재료의작업예약조회");
			
			if (stockV.size() > 0) {
				sYD_WBOOK_ID = StringHelper.evl(stockV.getRecord(0).getFieldString("YD_WBOOK_ID"), "");
				sDEL_YN      = StringHelper.evl(stockV.getRecord(0).getFieldString("DEL_YN"), "");
			} else {
//				throw new YFUserException("산적위치 수정=> 저장품정보가 존재하지 않습니다.");
			}
			
			commUtils.printLog(logId, "산적위치 수정=> 작업예약ID = " + sYD_WBOOK_ID, "[INFO]");
			commUtils.printLog(logId, "산적위치 수정=> 삭제유무   = " + sDEL_YN     , "[INFO]");
			
			if (!"".equals(sYD_WBOOK_ID)) {
				
				/* 작업예약 재료 위치 수정 */
				jrParam.setField("STL_NO"   , sSTL_NO);
				jrParam.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
				commDao.update(jrParam, updWmStrLoc, logId, methodNm, "작업예약 재료 위치 수정");
				
				/**
				 * 1.1 크레인스케줄 존재유무 체크
				 */ 
				jrParam.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
				jrParam.setField("STL_NO"   , sSTL_NO);
				JDTORecordSet jsSchInfo = commDao.select(jrParam, getCrnSchInfoWithWbookId, logId, methodNm, "크레인스케줄 존재유무 체크");
				
				if (jsSchInfo.size() > 0) {
					throw new YFUserException("해당 저장품의 크레인스케줄이 존재합니다.");
				}
				jrParam.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
				JDTORecordSet jsWrkBookInfo = commDao.select(jrParam, getWbookInfo, logId, methodNm, "작업예약 조회");
				
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
					jrParam.setField("YD_BAY_GP"  , sPutStackColGp.substring(1, 2));
					jrParam.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
					commDao.update(jrParam, updateBayGpWithWbookId, logId, methodNm, "작업예약 동 정보 변경");
				}
			}

			String sUP_YD_STK_COL_GP   = "";
			String sUP_YD_STK_BED_NO   = "";
			String sUP_YD_STK_LYR_NO = "";
			String sUP_SECT_GP = "";
			String sCtsYn = "";
			/********************************************************
			 * 2. 저장품의 MAP정보를 가져온다. 중복위치도 체크한다.
			 *  2019.11.01
			 *  - 1열연, 2열연 코일이 박판야드장에서 산적위치를 수정해야 한다면
			 *    : 관련 SQL 옮기기 ( YM -> YF ) YD는 일단 무시 
			 * 
			 * 
			 ********************************************************/
			commUtils.printLog(logId, "************************************************", "[INFO]");
			commUtils.printLog(logId, "저장품의 MAP정보를 가져온다. 중복위치도 체크한다", "[INFO]");
			commUtils.printLog(logId, "************************************************", "[INFO]");
			
			JDTORecord jStkLyrInfo = null;
			 
			jrParam.setField("STL_NO"   , sSTL_NO);
			jrParam.setField("STOCK_ID"   , sSTL_NO);
			JDTORecordSet jsYfStkLyrInfo = commDao.select(jrParam, getYfStkLyrInfoByStlNo, logId, methodNm, "TB_YF_STACKLAYER 조회");
			JDTORecordSet jsYmStkLyrInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmStkLyrInfoByStockId", logId, methodNm, "TB_YM_STACKLAYER 조회");
			JDTORecordSet jsYdStkLyrInfo = commDao.select(jrParam, getYdStkLyrInfoByStockId, logId, methodNm, "TB_YD_STKLYR 조회");
			
			// 저장품이 TB_YD_STKLYR에 존재할 때
			// 2열연
			if (jsYdStkLyrInfo.size() > 0) {
				for (int i = 0; i < jsYdStkLyrInfo.size(); ++i) {
					
					jStkLyrInfo = jsYdStkLyrInfo.getRecord(i);
					sUP_YD_STK_COL_GP 	= commUtils.nvl(jStkLyrInfo.getFieldString("YD_STK_COL_GP"), "");
					sUP_YD_STK_BED_NO 	= commUtils.nvl(jStkLyrInfo.getFieldString("YD_STK_BED_NO"), "");
					sUP_YD_STK_LYR_NO   = commUtils.nvl(jStkLyrInfo.getFieldString("YD_STK_LYR_NO"), ""); //YD테이블은 3자리
					 
					jrParam.setField("STOCK_ID"                 , sSTL_NO);
					jrParam.setField("FRTOMOVE_EQUIP_GP"      , "");      
					jrParam.setField("FRTOMOVE_EQUIP_BED_GP"  , "");  
					jrParam.setField("FRTOMOVE_EQUIP_LAYER_GP", "");
					jrParam.setField("CTS_RELAY_SADDLE"       , "");       
					jrParam.setField("CTS_RELAY_YN"           , "");           
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStockMoveEquipInfo", logId, methodNm, "TB_YM_STOCK 수정");
					
					jrParam.setField("STL_NO"             , "");
					jrParam.setField("YD_STK_LYR_MTL_STAT", YfConstant.YD_STK_LYR_MTL_STAT_E);
					jrParam.setField("YD_STK_COL_GP"      , sUP_YD_STK_COL_GP);
					jrParam.setField("YD_STK_BED_NO"      , sUP_YD_STK_BED_NO);
					jrParam.setField("YD_STK_LYR_NO"      , sUP_YD_STK_LYR_NO);
					commDao.update(jrParam, updYdStkLyrByPk, logId, methodNm, "TB_YD_STKLYR 수정");
					
				} //end for
			} //if (jrYdStkLyrInfo.size() > 0)
			
			// 저장품이 TB_YM_STKLYR에 존재할 때
			// 1열연
			if (jsYmStkLyrInfo.size() > 0) {
				for (int i = 0; i < jsYmStkLyrInfo.size(); ++i) {
					jStkLyrInfo = jsYmStkLyrInfo.getRecord(i);
					sUP_YD_STK_COL_GP 	= commUtils.nvl(jStkLyrInfo.getFieldString("YD_STK_COL_GP"), "");
					sUP_YD_STK_BED_NO 	= commUtils.nvl(jStkLyrInfo.getFieldString("YD_STK_BED_NO"), "");
					sUP_YD_STK_LYR_NO   = commUtils.nvl(jStkLyrInfo.getFieldString("YD_STK_LYR_NO"), ""); //YD테이블은 3자리
					 
					jrParam.setField("STOCK_ID"                 , sSTL_NO);
					jrParam.setField("FRTOMOVE_EQUIP_GP"      , "");      
					jrParam.setField("FRTOMOVE_EQUIP_BED_GP"  , "");  
					jrParam.setField("FRTOMOVE_EQUIP_LAYER_GP", "");
					jrParam.setField("CTS_RELAY_SADDLE"       , "");       
					jrParam.setField("CTS_RELAY_YN"           , "");           

					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStockMoveEquipInfo", logId, methodNm, "TB_YM_STOCK 수정");
					jrParam.setField("STOCK_ID"        , "");
					jrParam.setField("STACK_LAYER_STAT", YfConstant.YD_STK_LYR_MTL_STAT_E);
					jrParam.setField("YD_STK_COL_GP"    , sUP_YD_STK_COL_GP);
					jrParam.setField("STACK_BED_GP"    , sUP_YD_STK_BED_NO);
					jrParam.setField("STACK_LAYER_GP"  , sUP_YD_STK_LYR_NO);
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat", logId, methodNm, "TB_YM_STACKLAYER 초기화");
				}
			}
			
			if (jsYfStkLyrInfo.size() > 0) {
				for (int inx = 0; inx < jsYfStkLyrInfo.size(); inx++) {
					jStkLyrInfo = jsYfStkLyrInfo.getRecord(inx);

					sUP_YD_STK_COL_GP 	= commUtils.nvl(jStkLyrInfo.getFieldString("YD_STK_COL_GP"  ), "");
					sUP_YD_STK_BED_NO 	= commUtils.nvl(jStkLyrInfo.getFieldString("YD_STK_BED_NO"  ), "");
					sUP_YD_STK_LYR_NO  = commUtils.nvl(jStkLyrInfo.getFieldString("YD_STK_LYR_NO"), "");

					sUP_SECT_GP         = sUP_YD_STK_COL_GP.substring(2, 4); 
					/***************************
					 * FROM위치가 대차일 경우 
					 ***************************/
					if ("TC".equals(sUP_SECT_GP)) {
						jrParam.setField("QTY"         , "-1");
						jrParam.setField("YD_STK_COL_GP", sUP_YD_STK_COL_GP);
						jrParam.setField("YD_STK_BED_NO", sUP_YD_STK_BED_NO);
						commDao.update(jrParam, updateYdStkBedQtyInfo, logId, methodNm, "TB_YF_STKBED 정보 초기화");
						
					} // end if ("TC".equals(sUP_SECT_GP))
 
//					[확인사항] 산적위치관련 초기화 대상 컬럼 2019.10.30
					jrParam.setField("STL_NO"                , sSTL_NO);
//					jrParam.setField("FRTOMOVE_EQUIP_GP"      , "");      
//					jrParam.setField("FRTOMOVE_EQUIP_BED_GP"  , "");  
//					jrParam.setField("FRTOMOVE_EQUIP_LAYER_GP", "");
					jrParam.setField("CTS_RELAY_SADDLE"       , "");       
					jrParam.setField("CTS_RELAY_YN"           , "");           
////
					commDao.update(jrParam, updateStockMoveEquipInfo, logId, methodNm, "TB_YF_STOCK 수정");
//					
					/**
					 * 적치단 UP위치 Clear tb_yf_stklyr Table : stl_no =
					 * ''(Empty) tb_yf_stklyr Table : yd_stk_layer_stat =
					 * 'E'(적치가능)
					 */
					jrParam.setField("STL_NO"        , "");
					jrParam.setField("YD_STK_LYR_STAT", YfConstant.YD_STK_LYR_MTL_STAT_E);
					jrParam.setField("YD_STK_COL_GP"    , sUP_YD_STK_COL_GP);
					jrParam.setField("YD_STK_BED_NO"    , sUP_YD_STK_BED_NO);
					jrParam.setField("YD_STK_LYR_NO"  	, sUP_YD_STK_LYR_NO);
					
					commDao.update(jrParam, updateCraneYdStkLyrStat, logId, methodNm, "TB_YF_STKLYR 초기화");

				} // end for
			} //if (jsYmStkLyrInfo.size() > 0)
			
			// 2019. 12. 24 확장대차 1XTC03만 해당하는지 여부
			if ("TC".equals(sUP_SECT_GP) && !( YfConstant.CTS_1XTC01.equals(sUP_YD_STK_COL_GP) || YfConstant.CTS_1XTC02.equals(sUP_YD_STK_COL_GP)) ) {
				/*********************************
				 * 대차재료 삭제
				 ********************************/ 
				jrParam.setField("STL_NO"   , sSTL_NO);
				JDTORecordSet rst = commDao.select(jrParam, getYfTcarSchIdByStockId, logId, methodNm, "대차스케줄 조회");
				
				if (rst.size() > 0) {
					String sYD_TCAR_SCH_ID = rst.getRecord(0).getFieldString("YD_TCAR_SCH_ID");
					jrParam.setField("YD_TCAR_SCH_ID", sYD_TCAR_SCH_ID); 
					commDao.update(jrParam, updDelTcarFtMvMtl, logId, methodNm, "대차재료 삭제");
					
					/**
					 * 목적동에 작업예약이 생성 되었을 때에 삭제
					 */
					jrParam.setField("STL_NO", sSTL_NO);
					JDTORecordSet jsWbookInfo = commDao.select(jrParam, getWrkBookIdByStlNo, logId, methodNm, "작업예약 조회");
					
					if (jsWbookInfo.size() > 0) {
						
						String sWRK_KD = jsWbookInfo.getRecord(0).getFieldString("YD_SCH_CD").substring(2, 4); // 대차작업
						String sWRK_GP = jsWbookInfo.getRecord(0).getFieldString("YD_SCH_CD").substring(6, 8); // 하차작업
						
						if ("TC".equals(sWRK_KD) && "LM".equals(sWRK_GP)) { // 대차하차 스케줄
							jrParam.setField("YD_WBOOK_ID", jsWbookInfo.getRecord(0).getFieldString("YD_WBOOK_ID"));
							commDao.update(jrParam, updDelYnWrkBookMtl, logId, methodNm, "작업예약재료 삭제");
							commDao.update(jrParam, updDelYnWrkBook, logId, methodNm, "작업예약 삭제");
						}
					}
				} //대차재료 삭제
			}
			// Saddle일 경우 
			// Saddle 및 cts 차상위 일 경우 
			else if("SL".equals(sUP_SECT_GP) || "SR".equals(sUP_SECT_GP) || YfConstant.CTS_1XTC01.equals(sUP_YD_STK_COL_GP) || YfConstant.CTS_1XTC02.equals(sUP_YD_STK_COL_GP) ){
				/*****************
				 * CTS 재료삭제
				 *  1. CTS하차스케쥴만 존재하거나
				 *  2. CTS스케쥴 대기(L2작업지시 보내기 전)
				 * 
				 * *****************
				 */
				JDTORecordSet rst = null;
				jrParam.setField("STL_NO"   , sSTL_NO);
				rst = commDao.select(jrParam, selectCTSSch, logId, methodNm, "CTS 대차스케줄 조회");
				if(rst.size() < 1){
					commDao.update(jrParam, deleteCTSSchByStlNo , logId, methodNm, "TB_YF_CTS_SCH 삭제");
				}
				else {
					if(YfConstant.YD_SCH_PROG_STAT_W.equals(rst.getRecord(0).getFieldString("YD_WRK_PROG_STAT"))  ){
						commDao.update(jrParam, deleteCTSSchByStlNo , logId, methodNm, "TB_YF_CTS_SCH 삭제");
					}
					else{
						commUtils.printLog(logId, methodNm, "CTS 스케쥴이 W상태가 아니라서 산적위치 변경할 수 없음");
						throw new YFUserException("현재 CTS 작업지시대기["+rst.getRecord(0).getFieldString("YD_WRK_PROG_STAT")+"]상태 입니다. 해당 재료["+sSTL_NO+"]는 산적위치를 변경할 수 없습니다.");
					}	
				}
			}
			
			commUtils.printLog(logId, "○○○FROM 위치 수정 END", "[INFO]");

			/********************************
			 * 3. TO 위치 수정
			 ********************************/
			String sPUT_SECT_GP = sPutStackColGp.substring(2, 4); //SECT_GP

			// 가상 위치
			String sTempLayer = sPutStackColGp.substring(0,2) + "XX010101";
			jrParam.setField("YD_STK_COL_GP"  , sPutStackColGp);
			jrParam.setField("YD_STK_BED_NO"  , sPutStackBedGp);
			jrParam.setField("YD_STK_LYR_NO", sPutStackLayerGp);
		 	
		 	jsPutLocInfo = commDao.select(jrParam, getYfStkLyrInfoWithPk, logId, methodNm, "TB_YF_STKLYR 정보 조회");		
			
			String sToStlNo = "";
			
			if (jsPutLocInfo.size() > 0){
				sToStlNo 	= commUtils.nvl(jsPutLocInfo.getRecord(0).getFieldString("STL_NO"), "");
			}
			
			/******************************************************
			 * TO위치에 존재하는 기존 저장품 이력 저장 
			 ******************************************************/
			if (!"".equals(sToStlNo) && !sSTL_NO.equals(sToStlNo)){ 
	    	 	this.insertUpPutWrslRtData(sToStlNo, sPutLoc, sTempLayer, sUpYardGp, sMODIFIER, logId, methodNm);// 

	    	 	//코일공통에 위치이력 UPDATE
	    	 	JDTORecord jRecord = JDTORecordFactory.getInstance().create();
	    	 	jRecord.setField("STL_NO"   , sToStlNo);
	    	 	jRecord.setField("YD_LOC"     , sTempLayer);
	    	 	
	    	 	// 트랜잭션 분리
				EJBConnector ejbConn1 = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
				ejbConn1.trx("UpdCoilComLoc", new Class[] { JDTORecord.class }, new Object[] { jRecord });
			}
			
			/************************************
			 * 코일제품이적작업실적 전문 송신 
			 *************************************/
			commUtils.printLog(logId, "[YDDMR004] 코일제품이적작업실적 전문 전송", "[INFO]");
 			JDTORecord tcRecordDM = JDTORecordFactory.getInstance().create(); 
 			tcRecordDM.setField("JMS_TC_CD"         , "YDDMR004");
			tcRecordDM.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
 			tcRecordDM.setField("GOODS_NO"          , sToStlNo);
 			tcRecordDM.setField("BEFO_STORE_LOC"    , sPutStackColGp + sPutStackBedGp + sPutStackLayerGp);
 			tcRecordDM.setField("TO_STORE_LOC"      , sTempLayer);
 			
 			//인터페이스 전문 호출
 			jrRtn = commUtils.addSndData(jrRtn, tcRecordDM);
 			
            
 			/********************************
 			 * TO위치가 대차일 경우
 			 ********************************/
 			// 확인사항 2019. 12. 26 확장대차만 포함이 되는지
 			// 1XTC03
			if ("TC".equals(sPUT_SECT_GP)
			|| "SL".equals(sUP_SECT_GP) || "SR".equals(sUP_SECT_GP)		
			) {
				/* *******************************************************************
				 * 
				 * CASE 대차
				 * 1. 대차위치 파악
				 * 2. 대차위 다른 제품 존재하면                      대차재료 생성
				 *                     존재하지 않으면 대차초기화 후 대차재료 생성
				 *          
				 * CASE CTS
				 * 1. 해당 새들의 적치유무 판단
				 * 2. 빈 새들이면 적치단 Update
				 *    빈 새들이 아니면 오류 발생철리
				 *    
				 * *******************************************************************/
				
				if("TC".equals(sPUT_SECT_GP)){
					jrParam.setField("CURR_STOP_LOC", sPutStackColGp);
					JDTORecordSet jsTCInfo = commDao.select(jrParam, getTcarCurrLoc, logId, methodNm, "대차위치 조회");
					
					if (jsTCInfo.size() > 0) {
						String sYD_EQP_ID = jsTCInfo.getRecord(0).getFieldString("YD_EQP_ID");
						jrParam.setField("YD_EQP_ID" , sYD_EQP_ID);
						jrParam.setField("YD_MOVE_GP", "");
						JDTORecordSet jsTcarSchInfo = commDao.select(jrParam, getTcarSchByEqpid, logId, methodNm, "대차스케줄 조회");
						
						jrParam.setField("YD_TCAR_SCH_ID", jsTcarSchInfo.getRecord(0).getFieldString("YD_TCAR_SCH_ID"));
						JDTORecordSet jsTcarMtlInfo = commDao.select(jrParam, getYfTcarftmvmtlId, logId, methodNm, "대차 이송재료 조회");
						
						if (jsTcarMtlInfo.size() > 0) { 
							//대차위에 이송재료존재하면
						} else {
							
							commUtils.printLog(logId, "[####<로직확인용 LOG>#####] 대차스케줄관리 - 대차초기화", "[DEBUGE]");
							commUtils.printLog(logId, "jrParam {}" , jrParam.toString());
							//대차에 이송재료가 존재하지 않으면 대차초기화
							jrParam.setField("YD_EQP_ID"      , sYD_EQP_ID);
							jrParam.setField("YD_CURR_BAY_GP", sPutStackColGp.substring(1, 2));//현재동
							jrParam.setField("userid"        , sMODIFIER);
							
							// 2019. 11. 01 단건 처리 시작
							// 전문을 Setting하는데 Return값이 없다.
							// 산적위치 수정은 전문을 보내지 않는지? 확인요망
							this.initTcarSchMgt(jrParam);
							commUtils.printLog(logId, "[####<로직확인용 LOG>#####] 대차스케줄관리 - 대차초기화", "[DEBUGE]");
						}
						 
						jrParam.setField("STL_NO"       , sSTL_NO);
						jrParam.setField("YD_EQP_ID"    , sYD_EQP_ID);
						commDao.insert(jrParam, insTcarFtmvMtl, logId, methodNm, "대차이송재료 등록");
						
					} else {
						throw new YFUserException("해당 대차위치가 위치수정할 동에 있지 않습니다.");
					}
				}
				// 새들일 경우 아무것도 하지말자
				// cts작업현황조회에서 수동으로 작업지시를 내리자!
				else if("SL".equals(sUP_SECT_GP) || "SR".equals(sUP_SECT_GP)){
					  
				}
				/***************************
				 * 대차위치 CLEAR
				 ***************************/
				String sCurrQty = "1";
				jrParam.setField("QTY"         , sCurrQty);
				jrParam.setField("YD_STK_COL_GP", sPutStackColGp);
				jrParam.setField("YD_STK_BED_NO", sPutStackBedGp);
				commDao.update(jrParam, updateYdStkBedQtyInfo, logId, methodNm, "적치배드의 적치수량정보수정");

//				[확인사항] 산적위치관련 초기화 대상 컬럼 2019.10.30
				jrParam.setField("STL_NO"                , sSTL_NO);
//				jrParam.setField("STOCK_ID"               , sSTOCK_ID);
//				jrParam.setField("FRTOMOVE_EQUIP_GP"      , sPutStackColGp.substring(0, 1) + "X" + sPutStackColGp.substring(2));      
//				jrParam.setField("FRTOMOVE_EQUIP_BED_GP"  , sPutStackBedGp);  
//				jrParam.setField("FRTOMOVE_EQUIP_LAYER_GP", sPutStackLayerGp);
				jrParam.setField("CTS_RELAY_SADDLE"       , "");       
				jrParam.setField("CTS_RELAY_YN"           , "");         
				commDao.update(jrParam, updateStockMoveEquipInfo, logId, methodNm, "TB_YF_STOCK 수정");

			} 
			 
			else {
				//COIL 저장품 TABLE 수정 저장품TABLE의 이동설비항목에 권하위치값을 삭제한다.
				jrParam.setField("STL_NO"                 , sSTL_NO);
//				jrParam.setField("FRTOMOVE_EQUIP_GP"      , "");      
//				jrParam.setField("FRTOMOVE_EQUIP_BED_GP"  , "");  
//				jrParam.setField("FRTOMOVE_EQUIP_LAYER_GP", "");
				jrParam.setField("CTS_RELAY_SADDLE"       , "");       
				jrParam.setField("CTS_RELAY_YN"           , "");           

				commDao.update(jrParam, updateStockMoveEquipInfo, logId, methodNm, "TB_YF_STOCK 수정");
			} //END if ("TC".equals(sPUT_SECT_GP))

			
			// To위치가 정정실비 일 경우 적치단에 적재하지 말자
			// 설비 보급 추출의 경우 tb_yf_eqptracking테이블 참조!
			if (
					!(	"FE".equals(sPUT_SECT_GP)||
						"FD".equals(sPUT_SECT_GP) ||
						"KE".equals(sPUT_SECT_GP) ||
						"KD".equals(sPUT_SECT_GP) ||
						"QE".equals(sPUT_SECT_GP) || 
						"QD".equals(sPUT_SECT_GP)
					)
				) 
				{
				
					commUtils.printLog(logId, "[TO위치 적치로 상태변경]",  sSTL_NO + " :: YD_STK_LYR_STAT ::" + YfConstant.YD_STK_LYR_MTL_STAT_C);
				
					jrParam.setField("STL_NO"        , sSTL_NO);
					jrParam.setField("YD_STK_LYR_STAT", YfConstant.YD_STK_LYR_MTL_STAT_C);
					jrParam.setField("YD_STK_COL_GP"  , sPutStackColGp);
					jrParam.setField("YD_STK_BED_NO"  , sPutStackBedGp);
					jrParam.setField("YD_STK_LYR_NO"  , sPutStackLayerGp);
					commDao.update(jrParam, updateCraneYdStkLyrStat, logId, methodNm, "적치단 수정");
				
				}else{
					commUtils.printLog(logId, "[### 설비위로 To위치 변경은 적치단 테이블 변경대상이 아닙니다.]",  sSTL_NO + " :: To위치  ::" + sPutLoc );
				}
			/*******************************************
			 * To위치 -> 코일공통에 위치이력 UPDATE
			 *******************************************/
    	 	JDTORecord jRecordTO = JDTORecordFactory.getInstance().create();
    	 	jRecordTO.setField("STL_NO"     , sSTL_NO);
    	 	jRecordTO.setField("YD_LOC"     , sPutLoc);
    	 	
			EJBConnector ejbConnTO = new EJBConnector("default", "ACoilRcvL2BakSeEJB", this);
			ejbConnTO.trx("UpdCoilComLoc", new Class[] { JDTORecord.class }, new Object[] { jRecordTO });


			/*
			 * 저장품 예상PUT 위치 CLEAR 저장품지정조회화면에서 예약상태로 남아있는것을 방지하기 위해
			 *  - 2019. 11. 01 박판열연은 해당 컬럼 사용안함 ( 하차 PUT 위치 )
			 *  
			 */ 
//			jrParam.setField("STL_NO", sSTL_NO);
//			JDTORecordSet jsStockInfo = commDao.select(jrParam, getStockInfoByPk", logId, methodNm, "저장품 정보 조회");
//			
//			if (jsStockInfo.size() > 0) {
//				
//				// 하차PUT 위치
//				String sCARUNLOAD_PUT_LOC = StringHelper.evl(jsStockInfo.getRecord(0).getFieldString("CARUNLOAD_PUT_LOC"), "");// 하차PUT위치
//				if (sPutLoc.equals(sCARUNLOAD_PUT_LOC)) {
//					/*
//					UPDATE TB_YM_STOCK
//					   SET CARUNLOAD_PUT_LOC = :V_CARUNLOAD_PUT_LOC
//					     , MODIFIER = :V_MODIFIER
//					     , MOD_DDTT = SYSDATE     
//					 WHERE STOCK_ID = :V_STOCK_ID            
//					 */
//					jrParam.setField("CARUNLOAD_PUT_LOC", "");
//					jrParam.setField("STOCK_ID"         , sSTOCK_ID);
//					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStockByPk", logId, methodNm, "");
//				}
//			}

			/**
			 * 5. 저장품이동조건 수정
			 */
			 
			jrParam.setField("STOCK_MOVE_TERM", sSTOCK_MOVE_TERM);
			jrParam.setField("STL_NO"       , sSTL_NO);
			commDao.update(jrParam, updateStockTransInfo, logId, methodNm, "TB_YF_STOCK 저장품이동조건 수정");
			

			/**
			 * 6. Crane 작업 실적 등록
			 */
			this.insertUpPutWrslRtData(sSTL_NO.trim(), sUpLoc.trim(), sPutLoc.trim(), sPutYardGp ,sMODIFIER, logId, methodNm);

			/**
			 * 7. Coil 이적실적 (출하로 이적실적 송신 YDDMR004) 
			 * 공통 진도 Code가 출하작업지시대기 K
			 *                  제품충당대기     Z
			 *                  출하작업대기     L
			 *                  보관매출         M 이면 출하로 "이적실적 송신"
			 */

			if (YfConstant.CURR_PROG_CD_COIL_K.equals(sProgCd) ||
					YfConstant.CURR_PROG_CD_COIL_P.equals(sProgCd) ||
					YfConstant.CURR_PROG_CD_COIL_Z.equals(sProgCd) ||
					YfConstant.CURR_PROG_CD_COIL_J.equals(sProgCd) ||
					YfConstant.CURR_PROG_CD_COIL_L.equals(sProgCd) ||
					YfConstant.CURR_PROG_CD_COIL_X.equals(sProgCd) ||
					YfConstant.CURR_PROG_CD_COIL_M.equals(sProgCd)) {
				
				JDTORecord tcParam = JDTORecordFactory.getInstance().create();
				tcParam.setField("JMS_TC_CD"         , "YDDMR004");
				tcParam.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
				tcParam.setField("GOODS_NO"          , sSTL_NO.trim());
				tcParam.setField("BEFO_STORE_LOC"    , sUpLoc.trim());
				tcParam.setField("TO_STORE_LOC"      , sPutLoc.trim());
				
				jrRtn = commUtils.addSndData(jrRtn, tcParam);		
			}
			
			/**
			 * 10. TO위치가 SPM,HFL 입측이면 보급실적을 조업으로 송신한다.
			 * 
			 *  1B FE 01	B동 HFL입측컨베이어
				1C FD 01	C동 HFL출측컨베이어
				1D KE 01	D동 SPM입측컨베이어
				1E KD 01	E동 SPM출측컨베이어
				1E KE 01	E동 SPM입측컨베이어
				1F KD 01	F동 SPM출측컨베이어
				1F QD 01	F동 EQ출측컨베이어
				1G QD 01	G동 EQ출측컨베이어
				
				- tak-in 에대한 코드가 다른다. FE 입측으로 현재 사용
				[2020.01.20] 확인사항 EQL은 조업송신?
				
			 */
			if ("FE".equals(sPUT_SECT_GP) ||// COIL HFL 보급위치
				"KE".equals(sPUT_SECT_GP) ||// COIL SPM 보급위치
				"QE".equals(sPUT_SECT_GP) // COIL EQL 보급위치
			) 
			{
				// 실적전문 전송
				jrRtn = this.setSendToMsgBySupply(sPUT_SECT_GP, sSTL_NO, sPutLoc, false, jrRtn);
			}
			
			/**
			 * 12. YARD MAP 정보 실적 등록  YFF1L002
			 */
			commUtils.printLog(logId, "YFF1L002 INTERFACE SEND", "[INFO]");
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setField("TC_CD"          , "YFF1L002");
			jrYdMsg.setField("MSG_GP"         , "I");
			jrYdMsg.setField("YD_INFO_SYNC_CD", "5");
			jrYdMsg.setField("STL_NO"       , sSTL_NO.trim());

			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L002", jrYdMsg));
			
			
			return jrRtn;
			
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch (DAOException daoe) {
			throw daoe;
		} 

	}
	
	/**
	 * <pre>
	 * 	보급실적 송신 내부 Method
	 *   - 열연조업L3
	 *     : SPM, HFL -> YMPOJ161
	 *     : EQL -> YDHRJ005
	 *   - 품질L3열연정정입측보급실적		
	 *     : YDQMJ002
	 * </pre>
	 * @param sEqpDiv
	 * @param sStlNo
	 * @param sPutLoc
	 * @param isBak
	 * @param jrRtn
	 * @return
	 * @throws Exception
	 */
	private JDTORecord setSendToMsgBySupply(String sEqpDiv, String sStlNo, String sPutLoc, boolean isBak, JDTORecord jrRtn) throws Exception{
		
		if ("FE".equals(sEqpDiv) || "KE".equals(sEqpDiv)) {
			
			String sPlantGbn   = "";
			String sProcessId  = "";
			String sPositionNo = "";
			
			if ("FE".equals(sEqpDiv)) {
				sPlantGbn = YfConstant.EQP_WORK_ID_HFL;	sProcessId = "1";	sPositionNo = "D1";
			}	
			if ("KE".equals(sEqpDiv)) {
				sPlantGbn = YfConstant.EQP_WORK_ID_SPM; sProcessId = "1";	sPositionNo = "D1";
			}
			
			//코일보급 및 보급취소(YMPOJ161)
			JDTORecord tcParamYMPOJ161 = JDTORecordFactory.getInstance().create();
			tcParamYMPOJ161.setField("JMS_TC_CD"         , YfConstant.YMPOJ161);
			tcParamYMPOJ161.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
			tcParamYMPOJ161.setField("tcCode"            , YfConstant.YMPOJ161);
			tcParamYMPOJ161.setField("tcDate"            , YfCommUtils.getCurDate("yyyy-MM-dd"));
			tcParamYMPOJ161.setField("tcTime"            , YfCommUtils.getCurDate("HH-mm-ss"));
			tcParamYMPOJ161.setField("plantGbn"          , (isBak)?"B":"A");      //A열연(박판열연)
			tcParamYMPOJ161.setField("procGbn"           , sPlantGbn);  //* 공정구분 CHAR(1) H : Hot Filnal, S : SkinPass
			tcParamYMPOJ161.setField("coilNo"            , sStlNo.trim()); //* COIL번호 CHAR(11)
			tcParamYMPOJ161.setField("processId"         , sProcessId);    //* 처리구분 CHAR(1) 1:보급,2:보급취소,3:추출,4:Take-Out,5:Take-In
			tcParamYMPOJ161.setField("downDate"          , YfCommUtils.getCurDate("yyyyMMdd"));//* 권하일자 CHAR(8) yyyymmdd
			tcParamYMPOJ161.setField("downTime"          , YfCommUtils.getCurDate("HHmmss"));  //* 권하시각 CHAR(6) HHMMSS
			tcParamYMPOJ161.setField("positionNo"        , sPositionNo); //* 위치포지션 CHAR(2)
			jrRtn = commUtils.addSndData(jrRtn, tcParamYMPOJ161);
		}
		else if ("QE".equals(sEqpDiv)){
			
					JDTORecord tcParamYDHRJ005 = JDTORecordFactory.getInstance().create();
					tcParamYDHRJ005.setField("JMS_TC_CD",        "YDHRJ005"); 							//열연조업 L3 정정보급완료 실적  전문코드
					tcParamYDHRJ005.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
					tcParamYDHRJ005.setField("STL_NO",     	sStlNo.trim());								//재료번호
					tcParamYDHRJ005.setField("YD_EQP_ID",     sPutLoc.substring(0, 6));			//야드설비id :"1EQE01"
					tcParamYDHRJ005.setField("YD_STK_BED_NO", "01");			//야드적치베드번호
					tcParamYDHRJ005.setField("YD_DN_CMPL_DT", commUtils.getTcDate("yyyyMMddHHmmss"));	//야드권하완료일시
					tcParamYDHRJ005.setField("TREAT_GP", "1"); 
					jrRtn = commUtils.addSndData(jrRtn, tcParamYDHRJ005);
		}
		
		if(isBak){
			//품질L3열연정정입측보급실적
			JDTORecord jrYdMsgLINEIN = JDTORecordFactory.getInstance().create();
			//열연조업 코일보급 및 보급취소
			jrYdMsgLINEIN.setField("YD_DN_WR_LOC", sPutLoc);
			jrYdMsgLINEIN.setField("STL_NO"    , sStlNo);
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDQMJ002B" , jrYdMsgLINEIN));
			
		}else{
			//품질L3열연정정입측보급실적
			JDTORecord tcParamYDQMJ002 = JDTORecordFactory.getInstance().create();
			tcParamYDQMJ002.setField("JMS_TC_CD"         , YfConstant.YDQMJ002);
			tcParamYDQMJ002.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
			tcParamYDQMJ002.setField("STL_NO"            , sStlNo.trim());
			jrRtn = commUtils.addSndData(jrRtn, tcParamYDQMJ002);
		}

		
		return jrRtn;
	}
	
	/**
	 * <pre>
	 * 산적위치수정 - 삭제
	 * 크레인스케쥴 존재시 삭제 불가함
	 *  - 작업예약
	 *  - 차량스케쥴
	 *  - CTS
	 *  
	 * </pre>
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delStkLoc(GridData gdReq) throws DAOException {
		String methodNm = "산적위치수정-삭제[ACoilJspBakSeEJB.delStkLoc] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sYD_GP            = gdReq.getParam("YD_GP");
			String sSTL_NO         = gdReq.getParam("STL_NO");
			String sYD_WRKBOOK_YN    = gdReq.getParam("YD_WRKBOOK_YN"   );
			String sYD_CARSCH_YN     = gdReq.getParam("YD_CARSCH_YN"    );
			String sYD_TCARSCH_YN    = gdReq.getParam("YD_TCARSCH_YN"   );
			String sYD_CTS_YN        = gdReq.getParam("YD_CTS_YN"   );
			String sFROM_ADDR        = gdReq.getParam("FROM_ADDR");  //from 위치
			String sMODIFIER         = gdReq.getParam("userid");
			
			//Return Value
			JDTORecord jrRtn = null;
			
			/*****************************************************
			 * 저장품제원 : 코일야드L2로 송신(YFF1L002)
			 ******************************************************/
			commUtils.printLog(logId, "YFF1L002 JMS전송", "[INFO]");
			
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setField("TC_CD"          , "YFF1L002");
			jrYdMsg.setField("MSG_GP"         , "D"); //삭제일경우 D로 보냄 20170904
			jrYdMsg.setField("YD_INFO_SYNC_CD", "5");
			jrYdMsg.setField("STL_NO"       , sSTL_NO);

			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L002", jrYdMsg));

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			/*************************
			 * 산적위치수정-삭제
			 ***************************/ 
			jrParam.setField("STL_NO", sSTL_NO);
			commDao.update(jrParam, updStackLayer, logId, methodNm, "산적위치수정-삭제");
			
			/*************************
			 * 작업예약 삭제
			 ***************************/
			if ("Y".equals(sYD_WRKBOOK_YN)) { 
				JDTORecordSet rst = commDao.select(jrParam, getWrkBookIdByStlNo, logId, methodNm, "재료번호의 작업예약정보조회");
				
				if (rst.size() > 0) {
					jrParam.setField("YD_WBOOK_ID", rst.getRecord(0).getFieldString("YD_WBOOK_ID"));
					commDao.update(jrParam, updDelYnWrkBookMtl, logId, methodNm, "작업예약 재료정보 삭제");
					commDao.update(jrParam, updDelYnWrkBook, logId, methodNm, "작업예약 정보 삭제");
				}
			}
			/*******************
			 * 대차스케줄 삭제
			 ********************/
			if ("Y".equals(sYD_TCARSCH_YN)) {
				JDTORecordSet rstTcar = commDao.select(jrParam, getTcarFtMvMtlByStlNo, logId, methodNm, "재료번호의 차량스케쥴정보조회");
				
				if (rstTcar.size() > 0) {
					jrParam.setField("YD_TCAR_SCH_ID", rstTcar.getRecord(0).getFieldString("YD_TCAR_SCH_ID"));
					commDao.update(jrParam, updDelTcarFtMvMtl, logId, methodNm, "대차재료 삭제");
					
					if ("1".equals(rstTcar.getRecord(0).getFieldString("CNT"))) {
						commDao.update(jrParam, updDelTcarSch, logId, methodNm, "대차스케줄 삭제");
					}
				}
			}
			/**************************
			 * 차량스케줄 삭제
			 ***************************/
			if ("Y".equals(sYD_CARSCH_YN)) { 
				JDTORecordSet rstCar = commDao.select(jrParam, getCarFtMvMtlByStlNo, logId, methodNm, "재료번호의 차량스케쥴정보조회");
				
				if (rstCar.size() > 0) {
					jrParam.setField("YD_CAR_SCH_ID", rstCar.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
					commDao.update(jrParam, updDelCarFtMvMtl, logId, methodNm, "TB_YD_CARFTMVMTL 차량스케쥴 삭제");
					
					if ("1".equals(rstCar.getRecord(0).getFieldString("CNT"))) {
						commDao.update(jrParam, updDelCarSch, logId, methodNm, "TB_YD_CARSCH 차량스케쥴 삭제");
					}
				}
			}
				
			/*************************
			 * CTS 삭체
			 ***************************/
			if ("Y".equals(sYD_CTS_YN)) { 
				
				JDTORecordSet rst = null;
				jrParam.setField("STL_NO"   , sSTL_NO);
				rst = commDao.select(jrParam, selectCTSSch, logId, methodNm, "CTS 대차스케줄 조회");	
				if(rst.size() > 0){
					// 스케쥴 수행대기 일 경우에만 삭제가능 처리하자.
					if(YfConstant.YD_SCH_PROG_STAT_W.equals(rst.getRecord(0).getFieldString("YD_WRK_PROG_STAT"))){
						
						jrParam.setField("V_STL_NO", sSTL_NO);
						commDao.update(jrParam, deleteCTSSchByStlNo , logId, methodNm, "TB_YF_CTS_SCH 삭제");
					}else{
						commUtils.printLog(logId, methodNm, "CTS 스케쥴이 W상태가 아니라서 산적위치 변경할 수 없음");
						throw new YFUserException("현재 CTS["+rst.getRecord(0).getFieldString("YD_WRK_PROG_STAT")+"]상태가 입니다. 해당 재료["+sSTL_NO+"]는 산적위치를 삭제 할 수 없습니다.");
					}
				}
			}
			
			/**************************
			 * 실적테이블에 저장
			 **************************/
			this.insertUpPutWrslRtData(sSTL_NO, sFROM_ADDR, "", sYD_GP, sMODIFIER, logId, methodNm);//
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delStkLoc
	
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
	public int insertUpPutWrslRtData(String sStlNo, String sUpLoc, String sPutLoc, String sYdGp , String sUserId ,String logId, String methodNm) throws Exception {
		int iSeq = -1;
	
		try { 
			
			String sPutBay = sPutLoc.length() > 2 ? sPutLoc.substring(1, 2)	: "";
			JDTORecord jparam = commUtils.getParam(logId, methodNm, commUtils.trim(sUserId));
			jparam.setField("V_REGISTER"       , sUserId);
			jparam.setField("YD_GP"            , sYdGp);
			jparam.setField("STL_NO"           , sStlNo);
			jparam.setField("YD_CRN_SCH_ID"    , "000000000000000000");
			jparam.setField("YD_SCH_CD"        , "1X9999");
			jparam.setField("YD_EQP_ID"        , sYdGp + sPutBay + YfConstant.EQUIP_KIND_CR + "00");
			jparam.setField("YD_WRK_DUTY"      , commUtils.getWorkDuty());
			jparam.setField("YD_WRK_PARTY"     , commUtils.getWorkParty());
			jparam.setField("YD_UP_WO_LOC"     , sUpLoc);
			jparam.setField("YD_DN_WO_LOC"     , sPutLoc);
			jparam.setField("UP_FUNC"          , YfConstant.CRANE_FUNC_S);
			jparam.setField("PUT_FUNC"         , YfConstant.CRANE_FUNC_S);

			commDao.insert(jparam, insertCrnWrslt, logId, methodNm,"작업이력생성 - 크레인 실적 등록");
			
		} catch (DAOException daoe) {
			throw daoe;
		} 
		return iSeq;
	}
	/**
	 * 산적위치수정 - 전문백업
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return JDTORecord
	 * @throws DAOException
	 */ 

	public JDTORecord updStkLocBackUp(GridData gdReq) throws DAOException {
		
		String methodNm = "산적위치수정-수정[ACoilJspBakSeEJB.updStkLoc] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("YD_GP"	 , gdReq.getParam("YD_GP"));
			String sSTL_NO  = gdReq.getParam("STL_NO") ; // commUtils.getValue(gdReq, "STL_NO", 0); //재료번호
			String sINPUT_DATA1 = gdReq.getParam("PARA_INPUT_DATA1");	//설비ID
			
			String sSND_FLAG  = gdReq.getParam("SND_FLAG");	// 백업종류
			
			/******************************
			 *** 저장품
			 ******************************/
			String sFLAG   = ""; //신규 or 수정
			String sDEL_YN = "";
			if ("STOCK".equals(sSND_FLAG)) {
				jrParam.setField("STL_NO", sSTL_NO); 
				JDTORecordSet rst = commDao.select(jrParam, getStockInfoByPk, logId, methodNm, "저장품조회");
				if (rst.size() <= 0) {
					sFLAG = "I";
				} else {
					sDEL_YN = rst.getRecord(0).getFieldString("DEL_YN");
					if ("Y".equals(sDEL_YN)) {
						sFLAG = "U";
					} else {
						throw new YFUserException("해당 재료는 저장품에 있습니다.");
					}
				}
			
				if ("I".equals(sFLAG)) {
					jrParam.setField("COIL_NO", sSTL_NO);
					commDao.insert(jrParam, insertStock, logId, methodNm, "TB_YF_STOCK 저장품등록");
				} else if ("U".equals(sFLAG)) {
    				jrParam.setField("DEL_YN"  , "N");
					jrParam.setField("STL_NO", sSTL_NO);
					commDao.update(jrParam, updateStockDelYnInfo, logId, methodNm, "TB_YF_STOCK 저장품삭제");
				}
				
				//======================================================
				// 저장품제원 : 코일야드L2로 송신(YFF1L002)
				//======================================================
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("TC_CD"          , "YFF1L002");
				jrYdMsg.setField("MSG_GP"         , "I");
				jrYdMsg.setField("YD_INFO_SYNC_CD", "5");
				jrYdMsg.setField("STL_NO"       , sSTL_NO);
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L002", jrYdMsg));
			}
			
			/******************************
			 *** 보급
			 ******************************/
			if ("LINEIN".equals(sSND_FLAG)) {
				
				//품질L3열연정정입측보급실적
				JDTORecord jrYdMsgLINEIN = JDTORecordFactory.getInstance().create();
				//열연조업 코일보급 및 보급취소
				jrYdMsgLINEIN.setField("YD_DN_WR_LOC", sINPUT_DATA1);
				jrYdMsgLINEIN.setField("STL_NO"    , sSTL_NO);
				
				JDTORecordSet jsYMPOJ161 = commDao.select(jrYdMsgLINEIN, TcYMPOJ161BackUp, logId, methodNm, "설비위 권하처리 조회 ");
				if ( jsYMPOJ161.size() < 1 ) {
					commUtils.printLog(logId, methodNm+  "보급실적 송신:" + sSTL_NO, "SL");
					throw new YFUserException("최근 크레인 작업이력 정보가 존재하지 않아 보급실적 송신을 할 수 없습니다. 재료번호 :["+sSTL_NO+"]");
				} else {
				
					String sPUT_SECT_GP = sINPUT_DATA1.substring(2, 4); //SECT_GP
					jrRtn = setSendToMsgBySupply(sPUT_SECT_GP, sSTL_NO, sINPUT_DATA1, true, jrRtn);
				}						

				
			} //end if
//			
//			/******************************
//			 *** 추출
//			 ******************************/
//			if ("LINEOFF".equals(sSND_FLAG)) {
//				
//			}
			
			/******************************
			 *** TAKE-IN
			 ******************************/
			if ("TAKEIN".equals(sSND_FLAG)) {
			
				JDTORecord jrYdMsgTAKEIN = JDTORecordFactory.getInstance().create();

				//열연조업 코일보급 및 보급취소
				jrYdMsgTAKEIN.setField("YD_DN_WR_LOC", sINPUT_DATA1);
				jrYdMsgTAKEIN.setField("STL_NO"    , sSTL_NO); 

    			JDTORecordSet jsYMPOJ161 = commDao.select(jrYdMsgTAKEIN, TcYMPOJ161BackUp, logId, methodNm, "설비위 권하처리 조회 "); 
				if ( jsYMPOJ161.size() <= 0 ) {
					commUtils.printLog(logId, methodNm+  "보급실적 송신:" + sSTL_NO, "SL");
					throw new YFUserException("최근 크레인 작업이력 정보가 존재하지 않아 보급실적(Take-In) 송신을 할 수 없습니다. 재료번호 :["+sSTL_NO+"]");
				} else {
					String sPUT_SECT_GP = sINPUT_DATA1.substring(2, 4); //SECT_GP
					jrRtn = setSendToMsgBySupply(sPUT_SECT_GP, sSTL_NO, sINPUT_DATA1, true, jrRtn);
				}			
			}
			
//			/******************************
//			 *** TAKE-OUT
//			 ******************************/
//			if ("TAKEOUT".equals(sSND_FLAG)) { 
//				
//			}
			
			/******************************
			 *** 수입LINE-OFF
			 * 2020.03.31 확인
			 *  조업L2 송신있는지 확인 필요
			 *  분기 LINE OFF 스케줄이면  분기 CF1BP04 (Conveyor COIL Line Off 완료) 송신 
			 ******************************/
			if ("H2LINE".equals(sSND_FLAG)) { 
				
				JDTORecord jrYdMsgH2LINE = JDTORecordFactory.getInstance().create();
				
				//압연L2LINE OFF실적송신
				jrYdMsgH2LINE.setField("STL_NO", sSTL_NO);
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("CF1BP04B", jrYdMsgH2LINE));
			}
//			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updStkLocBackUp
	
	/**
	 * @throws JDTOException 
	 * 코일 공통 테이블의 진도코드를 참조해서 
	 * 저장품이동조건을 가져온다.
	 *
	 * @param  String	:	저장품ID
	 * @return JDTORecord
	 * @throws  
	 */			 
	public JDTORecord getCoilCurrProgCd(String sStlNo, String logId, String methodNm) throws JDTOException {	
		
		String sProgCd   = "";
		String sStocMv   = "";
		String sReturnGp = "";
			
		JDTORecord jRtn = JDTORecordFactory.getInstance().create();
		
		try {
			JDTORecord jparam = JDTORecordFactory.getInstance().create();
			 
			jparam.setField("COIL_NO", sStlNo);
			JDTORecordSet jtR = commDao.select(jparam, getCoilCommonInfo, logId, methodNm, "코일공통조회");
			
			if (jtR.size() > 0){
				sProgCd 	= StringHelper.evl(jtR.getRecord(0).getFieldString("CURR_PROG_CD"), "");
				sReturnGp	= StringHelper.evl(jtR.getRecord(0).getFieldString("RETURN_GP"), "");
			}
			
			// 일관제철 진도코드
			if (YfConstant.CURR_PROG_CD_COIL_A.equals(sProgCd)||
				YfConstant.CURR_PROG_CD_COIL_R.equals(sProgCd)){
				sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_AC;
			} else if (YfConstant.CURR_PROG_CD_COIL_B.equals(sProgCd)) {
				sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_BC;
			} else if (YfConstant.CURR_PROG_CD_COIL_C.equals(sProgCd)) {
				sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_CC;
			} else if (YfConstant.CURR_PROG_CD_COIL_D.equals(sProgCd)) {
				sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_DC;
			} else if (YfConstant.CURR_PROG_CD_COIL_E.equals(sProgCd)) {
				sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_CS;
			} else if (YfConstant.CURR_PROG_CD_COIL_F.equals(sProgCd)) {
				sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_FC;
			} else if (YfConstant.CURR_PROG_CD_COIL_K.equals(sProgCd)) {
				sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_KG;
			} else if (YfConstant.CURR_PROG_CD_COIL_G.equals(sProgCd)) {
				sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_GC;
			} else if (YfConstant.CURR_PROG_CD_COIL_H.equals(sProgCd)) {
				sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_HG;
			} else if (YfConstant.CURR_PROG_CD_COIL_J.equals(sProgCd)) {
	
				if (YfConstant.RETURN_GP_1.equals(sReturnGp)){
					sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_JR;
				} else {
					sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_JG;
				}
			} else if (YfConstant.CURR_PROG_CD_COIL_L.equals(sProgCd)) {//코일제품상차지시 
				sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_LG;
			} else if (YfConstant.CURR_PROG_CD_COIL_N.equals(sProgCd)) {
				sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_NG;
			} else if (YfConstant.CURR_PROG_CD_COIL_M.equals(sProgCd)||
					YfConstant.CURR_PROG_CD_COIL_P.equals(sProgCd)) {
				sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_MG;
			} else if (YfConstant.CURR_PROG_CD_COIL_X.equals(sProgCd)) {
				sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_XG;	
			} else if (YfConstant.CURR_PROG_CD_COIL_Y.equals(sProgCd)) {
				sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_YG;
			} else if (YfConstant.CURR_PROG_CD_COIL_Z.equals(sProgCd)) {
				sStocMv   = YfConstant.NEW_STOCK_MOVE_TERM_ZG;
			}															
			
			jRtn.setField("CURR_PROG_CD"   , sProgCd);
			jRtn.setField("STOCK_MOVE_TERM", sStocMv);

		} catch(DAOException de) {
			throw de;
		} 
	    return jRtn;
	} 
	/**
	 *  상차위치 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecord carLiftPosSet(GridData gdReq) throws DAOException {
		String methodNm = "상차위치변경 [ACoilJspBakSeEJB.carLiftPosSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrParam.setField("MODIFIER"	            ,commUtils.trim(gdReq.getParam("userid")));
				jrParam.setField("YD_CAR_UPP_LOC_CD"	    ,commUtils.getValue(gdReq, "YD_CAR_UPP_LOC_CD", ii)); 
//				jrParam.setField("YD_STK_LYR_NO"	    ,commUtils.getValue(gdReq, "YD_STK_LYR_NO", ii)); 
				jrParam.setField("YD_CAR_SCH_ID"	    ,commUtils.getValue(gdReq, "YD_CAR_SCH_ID", ii)); 
				jrParam.setField("STL_NO"     	    	,commUtils.getValue(gdReq, "STL_NO", ii)); 
				jrParam.setField("YD_STK_COL_GP"	    ,commUtils.getValue(gdReq, "YD_STK_COL_GP", ii));
				
				commDao.update(jrParam, updYdCarftmvmtl, logId, methodNm, "차량이송재료 UPDATE"); 			
				
				// 차상위치 수정은 TB_YF_STOCK
				commDao.update(jrParam, updateYfStockYdcarUppLocCd , logId, methodNm, "차량이송재료 UPDATE");
				
//				2020. 04. 10 적치단 정보는 바꾸지 말자
//				commDao.update(jrParam, updateCarLayer, logId, methodNm, "TB_YF_STKLYR 적치단 정보 재료삭제");			
//				commDao.update(jrParam, updateCarMapLayer, logId, methodNm, "TB_YF_STKLYR 적치단 상태변경 C, 재료번호 등록");
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
	 * 크레인스케줄 고도화기준 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updAdvSchRuleMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인스케줄 고도화기준 변경[ACoilJspBakSeEJB.updAdvSchRuleMgt] < " + gdReq.getNavigateValue();
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
					
					commDao.update(jrParam, updAdvSchRuleInfo, logId, methodNm, "크레인스케줄 고도화기준 수정");
					
					/**
					 * 스케줄 고도화 기준 수정시 스케줄 기준도 같이 수정
					 */
					jrParam.setField("ADV_CRN_PRIOR"    , commUtils.getValue(gdReq, "YD_WRK_CRN_PRIOR" , ii)); //작업크레인우선순위
					
//					String sAPP100A = ymComm.BCoilApplyYn("APP100","1","A");
//					String sAPP100C = ymComm.BCoilApplyYn("APP100","1","C");
//					String sAPP100E = ymComm.BCoilApplyYn("APP100","1","E");
//					if ("Y".equals(sAPP100A) || "Y".equals(sAPP100C) || "Y".equals(sAPP100E)) {
						commDao.update(jrParam, updAdvCrnPrior, logId, methodNm, "고도화우선순위 수정");
//					}
					
					
				} else if ("2".equals(sTAB_INDEX)) {
					/**
					 * 홈위치 기준 수정
					 */
					jrParam.setField("DEL_YN"                 , commUtils.getValue(gdReq, "DEL_YN"              , ii));
					jrParam.setField("DTL_ITEM1"               , commUtils.getValue(gdReq, "DTL_ITEM1"            , ii));
					jrParam.setField("DTL_ITEM2"               , commUtils.getValue(gdReq, "DTL_ITEM2"            , ii));
					jrParam.setField("DTL_ITEM3"               , commUtils.getValue(gdReq, "DTL_ITEM3"            , ii));
					jrParam.setField("DTL_ITEM4"               , commUtils.getValue(gdReq, "DTL_ITEM4"            , ii));
					jrParam.setField("DTL_ITEM5"               , commUtils.getValue(gdReq, "DTL_ITEM5"            , ii));
					jrParam.setField("CD_GP"                  , commUtils.getValue(gdReq, "CD_GP"               , ii));
					jrParam.setField("ITEM"                   , commUtils.getValue(gdReq, "ITEM"                , ii));
					
					commDao.update(jrParam, updHomeMvTermList, logId, methodNm, "홈위치기준 수정");
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
	 * GridData - 테이블Data조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData getTableData(GridData gdReq) throws DAOException{
		String methodNm = "테이블Data조회[ACoilJspBakSeEJB.getTableData(GridData)] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		List listTableData = null;
		JDTORecord jrParam = null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			String sTableName = commUtils.nvl(gdReq.getParam("TABLE_NAME"),"").trim();
			String sWhere = commUtils.nvl(gdReq.getParam("SQL_WHERE"),"").trim();
			String sOrderBy = gdReq.getParam("SQL_ORDER_BY");
			String sQuery = "";
			String PAGE_NO = commUtils.nvl(gdReq.getParam("page_no"),"1").trim();
			String PAGE_SIZE =  commUtils.nvl(gdReq.getParam("page_size"),"100").trim();
			String sUserId = commUtils.trim(gdReq.getParam("userid"));
			
			jrParam = commUtils.getParam(logId, methodNm, sUserId);
			jrParam.setField("TABLE_NAME", sTableName);
			JDTORecordSet jtR = commDao.select(jrParam, getYdTableColumnInfo, logId, methodNm, "테이블정보 조회");
			
			String _SELECT_COLUMNS_ = "";
			
			if(jtR.size() > 0){
				
				JDTORecord rd = null;
				String sTmpColumnName = "";
				String sTmpDataType = "";
				
				for (int i = 0; i < jtR.size(); ++i)
				{	
					rd = jtR.getRecord(i);
					sTmpColumnName = rd.getFieldString("COLUMN_NAME");
					sTmpDataType  = rd.getFieldString("DATA_TYPE");
					if("DATE".equals(sTmpDataType)){
						_SELECT_COLUMNS_ += "\n, TO_CHAR( T."+sTmpColumnName+", 'YYYY-MM-DD HH24:MI:SS') AS " + sTmpColumnName;	 
					}else{
						_SELECT_COLUMNS_ += "\n, T."+sTmpColumnName;
					}
				}
			}
			
			if("".equals(_SELECT_COLUMNS_)){
				_SELECT_COLUMNS_ = "\n , T.*";
			}
			
			sQuery += " \n /* com.inisteel.cim.yf.common.dao.YfCommDAO.getYdTableData */";
			sQuery += " \n SELECT X.*";
			sQuery += " \n   FROM(SELECT ROWNUM AS RNUM";
			sQuery += " \n             , A.*";
			sQuery += " \n          FROM(SELECT COUNT(*) OVER() AS TOTALCOUNT";
			sQuery += " \n                    "+ _SELECT_COLUMNS_;
			sQuery += " \n                 FROM "+sTableName+" T";
			if( !"".equals(sWhere) ){
				sQuery += "\n\n WHERE ";
				sQuery += sWhere.replaceAll("`", "'");
			}
			if( "".equals(sOrderBy)){
				sQuery += " \n                ORDER BY 1, 2, 3"; 
			}
			else{
				sQuery += "\n\n ORDER BY ";
				sQuery += sOrderBy;
			}
			sQuery += " \n              ) A";
			sQuery += " \n         WHERE ROWNUM <= "+PAGE_NO+" * "+PAGE_SIZE+") X";
			sQuery += " \n  WHERE RNUM >= (("+PAGE_NO+"-1)* "+PAGE_SIZE+")+1";
			 
			listTableData = commDao.getCommonList(getDummySql, sQuery, new Object[]{});
			 
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			GridData gdRet = commUtils.jdtoRecordToGridData(gdRtn, listTableData, gdReq);
			
			commUtils.printLog(logId, methodNm, "S-", gdReq);
			
			return gdRet;
		} 
		catch(DAOException e) {
			throw e;
		} 
		catch(Exception e){
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * GridData - 테이블Data조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData updTableData(GridData gdReq) throws DAOException{
		String methodNm = "테이블Data조회[ACoilJspBakSeEJB.updTableData(GridData)] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		GridData gdRet = null, gdRtn = null;
		JDTORecord jrParam = null;
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			String sUserId = commUtils.trim(gdReq.getParam("userid"));
			jrParam = commUtils.getParam(logId, methodNm, sUserId);
			
			gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			gdRet = commUtils.jdtoRecordToGridData(gdRtn, new java.util.ArrayList(), gdReq);
			
			String sTableName = commUtils.nvl(gdReq.getParam("TABLE_NAME"),"").trim();
			jrParam.setField("TABLE_NAME", sTableName);
			
			JDTORecordSet jtR = commDao.select(jrParam, getYdTableColumnInfo, logId, methodNm, "테이블정보 조회");
			String sMergeQuery  = "";
			sMergeQuery = "MERGE INTO "+ sTableName + " A";
			sMergeQuery +="\n USING( SELECT ";
			sMergeQuery +="\n $_SELECT_DUAL_$";
			sMergeQuery +="\n FROM DUAL ) B ON (";
			sMergeQuery +="\n $_ON_$";
			sMergeQuery +="\n )";
			sMergeQuery +="\n WHEN MATCHED THEN UPDATE SET";
			sMergeQuery +="\n $_UPDATE_SQL_$";
			sMergeQuery +="\n WHEN NOT MATCHED THEN INSERT(";
			sMergeQuery +="\n $_INS_SQL1_$";
			sMergeQuery +="\n )VALUES(";
			sMergeQuery +="\n $_INS_SQL2_$";
			sMergeQuery +="\n )";
			
			JDTORecord rd = null;
			String _SELECT_DUAL_ = "";
			String _ON_ = "";
			String _UPDATE_SQL_ = "";
			String _INS_SQL1_ = "";
			String _INS_SQL2_ = "";
			String sTmpColumnName = ""; 
			String sTmpDataType = "";
			String sTmpParam = "";
			java.util.List aColumns = new java.util.ArrayList();
			
			for (int i = 0; i < jtR.size(); ++i)
			{	
				rd = jtR.getRecord(i);
				
				sTmpColumnName = rd.getFieldString("COLUMN_NAME");
				sTmpDataType  = rd.getFieldString("DATA_TYPE");
				
				if(!"MOD_DDTT".equals(sTmpColumnName)){
					aColumns.add(sTmpColumnName);
				}
				
//				if( !("REGISTER".equals(sTmpColumnName) || "REG_DDTT".equals(sTmpColumnName)) ){
				
				if("DATE".equals(sTmpDataType)){
					if("MOD_DDTT".equals(sTmpColumnName)){
						sTmpParam = "SYSDATE";	
					}
					else{
//						sTmpParam = "TO_DATE( :V_"+sTmpColumnName+", 'YYYY-MM-DD HH24:MI:SS')";
						sTmpParam = "TO_DATE( ?, 'YYYY-MM-DD HH24:MI:SS')";
					}
				}else{
//					sTmpParam = ":V_"+sTmpColumnName;	
					sTmpParam = "?";
				}
				
				// 변수타입에 따라서 처리합시다.
				if(_SELECT_DUAL_.length() >  0){
					_SELECT_DUAL_ += "\n, ";
				}
				_SELECT_DUAL_ += sTmpParam + " AS " + sTmpColumnName;
				
				if( "P".equals(rd.getFieldString("CONSTRAINT_TYPE"))){
					if(_ON_.length() > 0){
						_ON_  += "\n AND";
					}
					_ON_ += " A."+sTmpColumnName + " = B."+ sTmpColumnName;
				}
				else{
					if( !("REGISTER".equals(sTmpColumnName) || "REG_DDTT".equals(sTmpColumnName)) ){
						if(_UPDATE_SQL_.length() > 0){
							_UPDATE_SQL_ += ",";
						} 	
						_UPDATE_SQL_ += "\n  A."+sTmpColumnName + " = B."+ sTmpColumnName;
					}
				}
				
				if(_INS_SQL1_.length() > 0){
					_INS_SQL1_ += "\n ,";	
				}
				_INS_SQL1_ += " A."+sTmpColumnName;
				
				
				
				if(_INS_SQL2_.length() > 0){
					_INS_SQL2_ += "\n ,";
				}
				
				if( "REGISTER".equals(sTmpColumnName)){
					_INS_SQL2_ += "'" + sUserId +"'";	
				}
				else if("REG_DDTT".equals(sTmpColumnName)){
					_INS_SQL2_ += " SYSDATE";	
				}else{
					_INS_SQL2_ += " B."+sTmpColumnName;
				}
			}
			
			sMergeQuery = StringHelper.replaceStr(sMergeQuery, "$_SELECT_DUAL_$", _SELECT_DUAL_);
			sMergeQuery = StringHelper.replaceStr(sMergeQuery, "$_ON_$", _ON_);
			sMergeQuery = StringHelper.replaceStr(sMergeQuery, "$_UPDATE_SQL_$", _UPDATE_SQL_);
			sMergeQuery = StringHelper.replaceStr(sMergeQuery, "$_INS_SQL1_$", _INS_SQL1_);
			sMergeQuery = StringHelper.replaceStr(sMergeQuery, "$_INS_SQL2_$", _INS_SQL2_);
			
//			commUtils.printLog(logId, methodNm, "변환된 쿼리 :: \n"+ sMergeQuery, gdReq);
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			Object[] objParam = null;
			int nParamLenth = aColumns.size();
			for (int i = 0; i < rowCnt; i++) {
//				jrParam = commUtils.getParam(logId, methodNm, sUserId);
				objParam = new Object[nParamLenth];
				for( int j=0; j<nParamLenth; j++){
//						commUtils.printLog(logId, methodNm, aColumns[j].toString());
//					if(!"MOD_DDTT".equals(aColumns[j].toString())){
						objParam[j] = gdReq.getHeader(String.valueOf(aColumns.get(j))).getValue(i);
//					}
//						jrParam.setField(aColumns[j].toString(), commUtils.getValue(gdReq, aColumns[j].toString(), j));
				}
				commDao.updateData(getDummySql, sMergeQuery, objParam);
//				commDao.updateDataByDynamic(getDummySql", sMergeQuery, objParam);
			}
			commUtils.printLog(logId, methodNm, "변환된 쿼리 :: \n"+ sMergeQuery, gdReq);
			commUtils.printLog(logId, methodNm, "S-", gdReq);
			return gdRet;
		} 
		catch(DAOException e) {
			throw e;
		} 
		catch(Exception e){
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * GridData - 쿼리스크립트 실행
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData executeQuery(GridData gdReq) throws DAOException{
		String methodNm = "쿼리스크립트 실행[ACoilJspBakSeEJB.executeQuery(GridData)] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		GridData gdRet = null;
		GridData gdRtn = null;
//		JDTORecord jrParam = null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
//			String sUserId = commUtils.trim(gdReq.getParam("userid"));
//			jrParam = commUtils.getParam(logId, methodNm, sUserId);
			gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			gdRet = commUtils.jdtoRecordToGridData(gdRtn, new java.util.ArrayList(), gdReq);
			
			String sQueryType = commUtils.nvl(gdReq.getParam("QUERY_TYPE"),"");
			if("DML".equals(sQueryType)){
				
				String sQuery = commUtils.nvl(gdReq.getParam("QUERY_SCRIPT"),"");
				commDao.updateData(getDummySql, sQuery, null);
			}
			else{
				String sOwner = commUtils.nvl(gdReq.getParam("OWNER2"),"");
				String sObjectName = commUtils.nvl(gdReq.getParam("OBJECT_NAME"),"");
//				{call SP_PO_EAI001(?,?)}
				String callScript = "\n CALL "+sOwner+"."+sObjectName+"(";
				int rowCnt = gdReq.getHeader("OBJECT_NAME").getRowCount();
				Object[][] param = new Object[rowCnt][2];
				for (int i = 0; i < rowCnt; i++) {
					if(i>0){
						callScript += ", ";
					}
					callScript += " ?";
					if( "IN".equals(gdReq.getHeader("IN_OUT").getValue(i))){
						param[i][0] = "IN";
						param[i][1] = commUtils.trim(gdReq.getHeader("PARAM").getValue(i));
					}
					else if( "OUT".equals(gdReq.getHeader("IN_OUT").getValue(i))){
						param[i][0] = "OUT";
						param[i][1] = new Integer(Types.VARCHAR);
					}
				}
				callScript += " )";
				commUtils.printLog(logId, methodNm, "변환된 쿼리 :: \n"+ callScript, gdReq);
				
				JDTORecord excuteRto = commDao.execute(getDummySql, callScript, param);
				gdRet.setStatus( String.valueOf(excuteRto.getField("IS_SUCCESS")));
				 
			} 
			
			commUtils.printLog(logId, methodNm, "S-", gdRet);
			return gdRet;
		} 
		catch(DAOException e) {
			throw e;
		} 
		catch(Exception e){
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 *      [A] 오퍼레이션명 : CTS 작업우선동순위작업 - 동 적용
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord setCTSAreaBaySeq(GridData gdReq) throws DAOException {
		String methodNm = "CTS 작업우선동순위작업 - 동 적용[ACoilJspBakSeEJB.setCTSAreaBaySeq] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			JDTORecord jrParam = null;

			// 그리드 값 체크 확인
			int rowCnt = gdReq.getHeader("CHK").getRowCount();
			JDTORecord jrRtn = null; 
			String sUserId = commUtils.trim(gdReq.getParam("userid"));
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrParam = commUtils.getParam(logId, methodNm, sUserId);

				//DAO Parameter - Log ID, Method, 수정자 Set
				jrParam.setField("REPR_CD_GP"     , gdReq.getHeader("REPR_CD_GP").getValue(ii));
				jrParam.setField("CD_GP"      	  , gdReq.getHeader("CD_GP").getValue(ii));
				jrParam.setField("ITEM"     	  , gdReq.getHeader("ITEM").getValue(ii));
				jrParam.setField("DTL_ITEM1"      , gdReq.getHeader("DTL_ITEM1").getValue(ii));
				jrParam.setField("DTL_ITEM2"      , gdReq.getHeader("DTL_ITEM2").getValue(ii));
				jrParam.setField("DTL_ITEM3"      , gdReq.getHeader("DTL_ITEM3").getValue(ii));
				jrParam.setField("YD_EQP_ID1"     , gdReq.getHeader("YD_EQP_ID1").getValue(ii));
				jrParam.setField("YD_EQP_ID2"     , gdReq.getHeader("YD_EQP_ID2").getValue(ii));
				jrParam.setField("MODIFIER"	      , sUserId); 

				//Return Value
				commDao.update(jrParam, setCTSAreaBaySeq, logId, methodNm, "TB_YF_RULE 동 적용");

				//중계국 수정 시 대차초기화
				if("PRI007".equals(commUtils.trim(gdReq.getHeader("REPR_CD_GP").getValue(ii)))
				&& !"".equals(commUtils.trim(gdReq.getHeader("DTL_ITEM1").getValue(ii)))){
					jrRtn = commUtils.addSndData(comm.reCTSSchLevWo(jrParam));
				}
				
				if("1XTC01".equals(gdReq.getHeader("YD_EQP_ID1").getValue(ii))){
					commDao.update(jrParam, updYdEqpHomeBayGp1, logId, methodNm, "TB_YF_EQP 중계국 1호대차 HOME구분 적용");
				}
				
				if("1XTC02".equals(gdReq.getHeader("YD_EQP_ID2").getValue(ii))){
					commDao.update(jrParam, updYdEqpHomeBayGp2, logId, methodNm, "TB_YF_EQP 중계국 2호대차 HOME구분 적용");
				}
				
			}
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}// end of setCTSAreaBaySeq
	
	/**
	 * 야드설비정비등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord insEqpTrblReg(GridData gdReq) throws DAOException {
		String methodNm = "야드설비정비등록[ACoilJspBakSeEJB.insEqpTrblReg] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Grid date 를 JDTORecord data 로 변환
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("YD_EQP_ID"	,gdReq.getParam("YD_EQP_ID")); 
			jrParam.setField("YD_EQP_PAUSE_CODE"	,gdReq.getParam("YD_EQP_PAUSE_CODE")); 
			jrParam.setField("YD_EQP_PAUSE_OCC_DT"	,gdReq.getParam("YD_EQP_PAUSE_OCC_DT")); 
			jrParam.setField("YD_EQP_PAUSE_OCC_WRK_DUTY"	,gdReq.getParam("YD_EQP_PAUSE_OCC_WRK_DUTY")); 
			jrParam.setField("YD_EQP_PAUSE_OCC_WRK_PARTY"	,gdReq.getParam("YD_EQP_PAUSE_OCC_WRK_PARTY")); 
			jrParam.setField("YD_EQP_PAUSE_RCVR_CNTS"	,gdReq.getParam("YD_EQP_PAUSE_RCVR_CNTS"));
			
			
			commDao.update(jrParam, insEqpTrblReg, logId, methodNm, "야드설비정비등록");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of insEqpTrblReg

	/**
	 * 기준관리 - 세부항목수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updYfRule(GridData gdReq) throws DAOException {
		String methodNm = "기준관리 - 세부항목수정[ACoilJspBakSeEJB.updYfRule] < " + gdReq.getNavigateValue();
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
				if(!"".equals(commUtils.getValue(gdReq, "DTL_ITEM1", ii))){
					jrParam.setField("DTL_ITEM1"		, commUtils.getValue(gdReq, "DTL_ITEM1", ii) );
				}
				if(!"".equals(commUtils.getValue(gdReq, "DTL_ITEM2", ii))){
					jrParam.setField("DTL_ITEM2"		, commUtils.getValue(gdReq, "DTL_ITEM2", ii) );
				}
				if(!"".equals(commUtils.getValue(gdReq, "DTL_ITEM3", ii))){
					jrParam.setField("DTL_ITEM3"		, commUtils.getValue(gdReq, "DTL_ITEM3", ii) );
				}
				if(!"".equals(commUtils.getValue(gdReq, "DTL_ITEM4", ii))){
					jrParam.setField("DTL_ITEM4"		, commUtils.getValue(gdReq, "DTL_ITEM4", ii) );
				}
				if(!"".equals(commUtils.getValue(gdReq, "DTL_ITEM5", ii))){
					jrParam.setField("DTL_ITEM5"		, commUtils.getValue(gdReq, "DTL_ITEM5", ii) );
				}
				if(!"".equals(commUtils.getValue(gdReq, "DTL_ITEM6", ii))){
					jrParam.setField("DTL_ITEM6"		, commUtils.getValue(gdReq, "DTL_ITEM6", ii) );
				}
				if(!"".equals(commUtils.getValue(gdReq, "DTL_ITEM7", ii))){
					jrParam.setField("DTL_ITEM7"		, commUtils.getValue(gdReq, "DTL_ITEM7", ii) );
				}
				if(!"".equals(commUtils.getValue(gdReq, "DTL_ITEM8", ii))){
					jrParam.setField("DTL_ITEM8"		, commUtils.getValue(gdReq, "DTL_ITEM8", ii) );
				}
				if(!"".equals(commUtils.getValue(gdReq, "DTL_ITEM9", ii))){
					jrParam.setField("DTL_ITEM9"		, commUtils.getValue(gdReq, "DTL_ITEM9", ii) );
				}
				if(!"".equals(commUtils.getValue(gdReq, "DTL_ITEM10", ii))){
					jrParam.setField("DTL_ITEM10"	, commUtils.getValue(gdReq, "DTL_ITEM10", ii) );
				}
				
				jrParam.setField("REPR_CD_GP"	, commUtils.getValue(gdReq, "REPR_CD_GP", ii) );
				jrParam.setField("CD_GP"		, commUtils.getValue(gdReq, "CD_GP", ii) );
				jrParam.setField("ITEM"			, commUtils.getValue(gdReq, "ITEM", ii));
				commDao.update(jrParam, updYfRule, logId, methodNm, "기준관리 수정");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updYfRule
	
	/**
	 * 설비상태 (변경 설비기준조회 )
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updEqpOprnStat(GridData gdReq) throws DAOException {
		String methodNm = "설비상태 변경[ACoilJspBakSeEJB.updEqpOprnStat] < " + gdReq.getNavigateValue();
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
				
				//설비테이블(TB_YF_EQUIP) 설비상태가 고장이 아니면 작업진행 상태 값으로 변경
				if(!"고장".equals(commUtils.getValue(gdReq, "YD_EQP_STAT", ii))) {
					
					if("S".equals(commUtils.getValue(gdReq, "YD_EQP_PROG_STAT", ii))) {
						//S 일경우 설비는 W 로 설정
						jrParam.setField("YD_EQP_PROG_STAT"	,"W"); 
					} else {
						jrParam.setField("YD_EQP_PROG_STAT"	,commUtils.getValue(gdReq, "YD_EQP_PROG_STAT", ii)); 
					}
						jrParam.setField("YD_EQP_ID"		,commUtils.getValue(gdReq, "YD_EQP_ID", ii)); 
					
					commDao.update(jrParam, updEqpOprnStat, logId, methodNm, "설비상태 변경");
				}
				
				//크레인스케줄 ID가 있다면 크레인스케쥴 상태 변경
				if(!"".equals(commUtils.getValue(gdReq, "YD_CRN_SCH_ID", ii))) {
				
					jrParam.setField("YD_WRK_PROG_STAT"		,commUtils.getValue(gdReq, "YD_EQP_PROG_STAT", ii)); 
					jrParam.setField("YD_CRN_SCH_ID"		,commUtils.getValue(gdReq, "YD_CRN_SCH_ID", ii)); 
					
					commDao.update(jrParam, updCreSchOprnStat, logId, methodNm, "크레인상태 변경");
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
	 * 적치단 활성상태 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecord updYdStkColActiveStat(GridData gdReq) throws DAOException {
		String methodNm = "적치단 활성상태 수정[ACoilJspBakSeEJB.updYdStkColActiveStat] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("YD_STK_COL_GP"	            ,commUtils.trim(gdReq.getParam("YD_STK_COL_GP")));
			jrParam.setField("YD_STK_COL_ACTIVE_STAT"	    ,commUtils.trim(gdReq.getParam("YD_STK_COL_ACTIVE_STAT")));

			//적치단 UPDATE
			commDao.update(jrParam, updYfStkColActiveStat, logId, methodNm, "적치단의 활성상태 UPDATE");
			
			return sndRecord;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updYdStkColActiveStat
	
	/**
	 * 저장품종류 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecord updStockItem(GridData gdReq) throws DAOException {
		String methodNm = "적치단 저장품종류 수정[ACoilJspBakSeEJB.updStockItem] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("YD_STK_COL_GP"	,commUtils.trim(gdReq.getParam("YD_STK_COL_GP")));
			jrParam.setField("STOCK_ITEM"	    ,commUtils.trim(gdReq.getParam("STOCK_ITEM")));

			//적치단 UPDATE
			commDao.update(jrParam, updStockItem, logId, methodNm, "적치단의 활성상태 UPDATE");
			
			return sndRecord;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updStockItem
	
	/**
	 * 야드및설비 열정보수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecord updCoilYdStkPosSet(GridData gdReq) throws DAOException {
		String methodNm = "야드및설비 열정보수정[ACoilJspBakSeEJB.updCoilYdStkPosSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			
			inRecord.setField("MODIFIER"	            	,commUtils.trim(gdReq.getParam("userid")));
			inRecord.setField("YD_STK_COL_GP"	        	,commUtils.getValue(gdReq, "YD_STK_COL_GP", 0));
			inRecord.setField("YD_STK_BED_ACTIVE_STAT"	    ,commUtils.getValue(gdReq, "YD_STK_COL_ACTIVE_STAT", 0));
			inRecord.setField("YD_STK_LYR_ACTIVE_STAT"	    ,commUtils.getValue(gdReq, "YD_STK_COL_ACTIVE_STAT", 0));
			inRecord.setField("YD_STK_COL_ACTIVE_STAT"	    ,commUtils.getValue(gdReq, "YD_STK_COL_ACTIVE_STAT", 0));
			inRecord.setField("YD_STK_BED_X_AXIS"	        ,commUtils.getValue(gdReq, "YD_STK_COL_RULE_X_AXIS", 0));
			inRecord.setField("YD_STK_LYR_X_AXIS"	        ,commUtils.getValue(gdReq, "YD_STK_COL_RULE_X_AXIS", 0));
			inRecord.setField("YD_STK_COL_RULE_X_AXIS"	    ,commUtils.getValue(gdReq, "YD_STK_COL_RULE_X_AXIS", 0));
			
			inRecord.setField("YD_STK_BED_Y_AXIS"	        ,commUtils.getValue(gdReq, "YD_STK_COL_RULE_Y_AXIS", 0));
			inRecord.setField("YD_STK_LYR_Y_AXIS"	        ,commUtils.getValue(gdReq, "YD_STK_COL_RULE_Y_AXIS", 0));
			inRecord.setField("YD_STK_COL_RULE_Y_AXIS"	    ,commUtils.getValue(gdReq, "YD_STK_COL_RULE_Y_AXIS", 0));
			
			inRecord.setField("YD_STK_BED_Z_AXIS"	        ,commUtils.getValue(gdReq, "YD_STK_COL_RULE_Z_AXIS", 0));
			inRecord.setField("YD_STK_LYR_Z_AXIS"	        ,commUtils.getValue(gdReq, "YD_STK_COL_RULE_Z_AXIS", 0));
			inRecord.setField("YD_STK_COL_RULE_Z_AXIS"	    ,commUtils.getValue(gdReq, "YD_STK_COL_RULE_Z_AXIS", 0));
			
			inRecord.setField("ROTATION_ANGLE"	            ,commUtils.getValue(gdReq, "ROTATION_ANGLE", 0));

			//적치 베드 정보의  UPDATE
			commDao.update(inRecord, updYfStkbedYfStrActStat, logId, methodNm, "적치 베드 정보의  UPDATE");
			
			//TB_YD_STKLYR 적치 베드 정보의  UPDATE
			commDao.update(inRecord, updYfStklyrColActStat, logId, methodNm, "TB_YD_STKLYR 적치 베드 정보의  UPDATE");
			
			//TB_YD_STKBED 적치 베드 정보의  UPDATE
			commDao.update(inRecord, updYfStkbedYfStrX, logId, methodNm, "TB_YD_STKBED 적치 베드 정보의  UPDATE");

			//야드적치단 좌표만  UPDATE
			commDao.update(inRecord, updYfStklyrX, logId, methodNm, "야드적치단 좌표만  UPDATE");
			
			//적치열 정보 UPDATE
			commDao.update(inRecord, updYfStkcol, logId, methodNm, "적치열 정보 UPDATE");
			
			JDTORecord jDrd = JDTORecordFactory.getInstance().create();
			
			jDrd.setField("MSG_ID"				, "YFF1L001");
			jDrd.setField("DATE"				, commUtils.getDate10());
			jDrd.setField("TIME"				, commUtils.getTime8());
			jDrd.setField("MSG_GP"				, "");
			jDrd.setField("MSG_LEN"				, "0089");
			jDrd.setField("YD_INFO_SYNC_CD"		, "3");						//1:동,2:SPAN,3:열,4:BED
			jDrd.setField("YD_GP"				, inRecord.getFieldString("YD_STK_COL_GP").substring(0, 1));
			jDrd.setField("COL_GP"				, inRecord.getFieldString("YD_STK_COL_GP").substring(5, 6));
			jDrd.setField("YD_STK_COL_GP"		, inRecord.getFieldString("YD_STK_COL_GP"));

			sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YFF1L001", jDrd));
			
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
		String methodNm = "야드및설비 베드정보수정[ACoilJspBakSeEJB.updCoilYdStkPosSetBed] < " + gdReq.getNavigateValue();
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
				inRecord.setField("YD_STK_LYR_ACTIVE_STAT"	    ,commUtils.getValue(gdReq, "YD_STK_LYR_ACTIVE_STAT", ii));
				inRecord.setField("YD_STK_LYR_X_AXIS"	        ,commUtils.getValue(gdReq, "YD_STK_LYR_X_AXIS", ii));
				inRecord.setField("YD_STK_LYR_Y_AXIS"	        ,commUtils.getValue(gdReq, "YD_STK_LYR_Y_AXIS", ii));
				inRecord.setField("YD_STK_LYR_Z_AXIS"			,commUtils.getValue(gdReq, "YD_STK_LYR_Z_AXIS", ii));
				inRecord.setField("YD_STK_COL_GP"	        	,commUtils.getValue(gdReq, "YD_STK_COL_GP", ii));
				inRecord.setField("YD_STK_BED_NO"	        	,commUtils.getValue(gdReq, "YD_STK_BED_NO", ii));
				inRecord.setField("YD_STK_LYR_NO"	        	,commUtils.getValue(gdReq, "YD_STK_LYR_NO", ii));
				inRecord.setField("YD_STK_BED_XAXIS_TOL"	    ,commUtils.getValue(gdReq, "YD_STK_BED_XAXIS_TOL", ii));
				inRecord.setField("YD_STK_BED_YAXIS_TOL"	    ,commUtils.getValue(gdReq, "YD_STK_BED_YAXIS_TOL", ii));
				inRecord.setField("YD_STK_BED_ZAXIS_TOL"	    ,commUtils.getValue(gdReq, "YD_STK_BED_ZAXIS_TOL", ii));
				
				//적치 단 정보의  UPDATE 
				commDao.update(inRecord, updYdStklyrDan2);
				
				commDao.update(inRecord, updYdStklyrTol2);
			
				jDrd.setField("MSG_ID"				, "YFF1L001");
				jDrd.setField("DATE"				, commUtils.getDate10());
				jDrd.setField("TIME"				, commUtils.getTime8());
				jDrd.setField("MSG_GP"				, "");
				jDrd.setField("MSG_LEN"				, "0089");
				jDrd.setField("YD_INFO_SYNC_CD"		, "4");						//1:동,2:SPAN,3:열,4:BED
				jDrd.setField("YD_GP"				, "1");
				jDrd.setField("YD_STK_BED_NO"		, commUtils.getValue(gdReq, "YD_STK_BED_NO", ii));
				jDrd.setField("YD_STK_COL_GP"		, commUtils.getValue(gdReq, "YD_STK_COL_GP", ii));
				
				sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YFF1L001", jDrd));
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
	 * 스크랩 차량 진입여부 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updScrpCar(GridData gdReq) throws DAOException {
		String methodNm = "스크랩 차량 진입여부 변경[ACoilJspBakSeEJB.updScrpCar] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord jrRtn = null;
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("YD_L2_HMI_STAT"	, gdReq.getParam("YD_L2_HMI_STAT")); 
			jrParam.setField("MODIFIER"			, gdReq.getParam("MODIFIER")); 
			jrParam.setField("YD_EQP_ID"		, gdReq.getParam("YD_EQP_ID")); 
			
			commDao.update(jrParam, updScrpCarEntYn, logId, methodNm, "스크랩 차량 진입여부 변경");
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updScrpCar
	
	/**
	 * 야드및설비 베드정보수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecord updZonMaxStcokRt(GridData gdReq) throws DAOException {
		String methodNm = "야드현황조회 존별 최대 적치율 - 저장[ACoilJspBakSeEJB.updZonMaxStcokRt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = null;
			
			for (int i = 0; i < rowCnt; i++) {
				
				jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
				jrParam.setField("DTL_ITEM1" , commUtils.nvl(commUtils.getValue(gdReq, "MAX_RATE", i),"0"));
				jrParam.setField("DTL_ITEM2" , "");
				jrParam.setField("DTL_ITEM3" , "");
				jrParam.setField("DTL_ITEM4" , "");
				jrParam.setField("DTL_ITEM5" , "");
				jrParam.setField("DTL_ITEM6" , "");
				jrParam.setField("DTL_ITEM7" , "");
				jrParam.setField("DTL_ITEM8" , "");
				jrParam.setField("DTL_ITEM9" , "");
				jrParam.setField("DTL_ITEM10", "");
				
				jrParam.setField("REPR_CD_GP", "YM6001");
				jrParam.setField("CD_GP"     , commUtils.getValue(gdReq, "YD_GP", i));
				jrParam.setField("ITEM"      , commUtils.getValue(gdReq, "YD_ZONE_GP", i));
				
				commDao.update(jrParam, updYfRule, logId, methodNm, "YF_RULE수정");
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
	 * 차량작업관리 - 전체입동제한
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */	
	public JDTORecord procAllCarPntYnReg(GridData gdReq) throws DAOException {
		String mthdNm = "전체입동제한[ACoilJspBakSeEJB.procAllCarPntYnReg] < " + gdReq.getNavigateValue();
		String logId  = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			
			JDTORecord jrRtn		= JDTORecordFactory.getInstance().create();

			/****************************************************
			 * 입동제한 UPDATE
			 ****************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, gdReq.getParam("userid"));
			jrParam.setField("PRE_SUP_YN"	, gdReq.getParam("PRE_SUP_YN"));
			jrParam.setField("YD_BAY_GP"		, gdReq.getParam("YD_BAY_GP"));
			
	    	commDao.update(jrParam, updCoilCarPointYnReg , logId, mthdNm, "입동제한UPDATE");
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
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
		String methodNm = "지포장 - 보급,추출 요구[ACoilJspBakSeEJB.reqPapWrapInOut] < " + gdReq.getNavigateValue();
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
			String sEqpColGp     = commUtils.trim(gdReq.getParam("EQP_COL_GP")); 	  //설비적치열 정보
			String sSTL_NO      = commUtils.trim(gdReq.getParam("STL_NO")); 		  //코일 번호
			String sYD_STK_BED_NO = commUtils.trim(gdReq.getParam("YD_STK_BED_NO"));  //번지
			
			// 보급(IN), 추출(OUT)
			String sJobType = "IN";
			if("3".equals(sPROCESSID)) {
				sJobType = "OUT";
			}
			
			if("".equals(sYD_GP)) {
				throw new YFUserException("야드 구분이 존재하지 않습니다.");
			}
			if("".equals(sEqpColGp)) {
				throw new YFUserException("동 정보가 존재하지 않습니다.");
			}
			if("".equals(sSTL_NO)) {
				throw new YFUserException("재료번호를 입력하세요.");
			}

			String sYD_BAY_GP    = sEqpColGp.substring(1,2);
			
			// To위치가이드 (보급스케쥴)
			String sYdToLocGuide = sEqpColGp+sYD_STK_BED_NO+"01";
			String sFrom_YD_STK_COL_GP = ""; //적치 열 구분
			String sFrom_YD_STK_BED_NO = ""; //적치 BED 구분
			String sFrom_YD_STK_LYR_NO = ""; //적치 단 구분
			String sFrom_YD_BAY_GP = "";
			String sFrmLoc = "";
			String sCrnCallBayGp = "";

			/******************************
			 *  검증
			 *   - 보급 : 해당 번지에 작업유무 확인
			 *   - 추출 : 
			 *   - 공통 : 해당 적치단의 상태를 확인
			 */
			// 보급시 설비의 작업정보를 확인하여 적절한 메시지
			if("IN".equals(sJobType)){
				jrParam.setField("GF_LOC", sYdToLocGuide);
				JDTORecordSet rsTmp = null;
				rsTmp = commDao.select(jrParam, getListPapWrapIn, logId, methodNm, "보급상황 체크 Check");
				if (rsTmp.size() > 0) {				
					throw new YFUserException(rsTmp.getRecord(0).getFieldString("MSG"));
				}
			} 
			else{
				jrParam.setField("STL_NO", sSTL_NO);
				jrParam.setField("GF_LOC", sYdToLocGuide);
				JDTORecordSet rsTmp = null;
				rsTmp = commDao.select(jrParam, getListPapWrapOUT, logId, methodNm, "추출상황체크 Check");
				if (rsTmp.size() > 0) {				
					throw new YFUserException(rsTmp.getRecord(0).getFieldString("MSG"));
				}
			}
			
			// 작업하고자하는 재료의 현재 적치단 상태를 확인(보급: 타 작업예약 존재여부, 추출:적치 )
			jrParam.setField("STL_NO", sSTL_NO);
			rsResult = commDao.select(jrParam, getYfStkLyrInfoByStlNo, logId, methodNm, "TB_Yf_STACKLAYER 에 적치상태  Check");
			if (rsResult.size() < 1) {
				throw new YFUserException("야드에 적치정보가 존재하지 않습니다.");
			} 
				
			
			String sYD_STK_LYR_STAT = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_LYR_STAT"));
			if(!"C".equals(sYD_STK_LYR_STAT)) {
				throw new YFUserException("적치단 상태가 '적치중(C)' 이 아닙니다! 현재 상태 : " + sYD_STK_LYR_STAT);
			}
			
			// 저장위치 정보 셋팅
			sFrom_YD_STK_COL_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP")); //적치 열 구분
			sFrom_YD_STK_BED_NO 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_BED_NO")); //적치 BED 구분
			sFrom_YD_STK_LYR_NO	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_LYR_NO")); //적치 단 구분
			sFrom_YD_BAY_GP = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_LYR_NO")); //동구분
			sFrmLoc = sFrom_YD_STK_COL_GP + sFrom_YD_STK_BED_NO + sFrom_YD_STK_LYR_NO;
			sFrom_YD_BAY_GP = sFrom_YD_STK_COL_GP.substring(1,2); 
			
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
			
			// 지포장보급은 별도 스케쥴 생성하지 않음
			// 동내이적 OR 동간이적상차 스케쥴 생성 -> TO위치가이드로 실제 설비에 보급처리한다.
			if("IN".equals(sJobType)) {
				
				// 크레인을 호출하는 동정보
				sCrnCallBayGp = sFrom_YD_BAY_GP;
				
				// 동간이적 대상일경우 CTS 
				if(!sFrom_YD_BAY_GP.equals(sYD_BAY_GP)){ 
					jrParam.setField("YD_WRK_PLAN_TCAR"		, YfConstant.CTS_1XTC01);
				}
				
				// 목적동 셋팅
				jrParam.setField("YD_AIM_BAY_GP"		, sYD_BAY_GP ); 
				
				// 크레인스케쥴 생성
				sYD_SCH_CD = this.getMoveStockSchCode(sFrom_YD_STK_COL_GP, sYD_BAY_GP, logId, methodNm);
				
				// to위치가이드 정보 
				jrParam.setField("YD_TO_LOC_GUIDE"		, sYdToLocGuide );
				jrParam.setField("YD_TO_LOC_GUIDE_FNL"		, sYdToLocGuide );
				
				// to위치지정은 사용자지정(결정)
				jrParam.setField("YD_TO_LOC_DCSN_MTD"		, "F" );
				
				sSTOCK_MOVE_TERM 	= YfConstant.NEW_STOCK_MOVE_TERM_GC; //종합판정대기 
				sYD_SCH_REQ_GP		= "U"; //조업설비 보급 스케줄 
				
			}
			else if("OUT".equals(sJobType)) {
				
				// 크레인을 호출하는 동정보
				sCrnCallBayGp = sYD_BAY_GP;
				//추출
				sYD_SCH_CD 			= sYD_GP + sYD_BAY_GP + "GF01LM";
				sSTOCK_MOVE_TERM 	= YfConstant.NEW_STOCK_MOVE_TERM_A8; 
				sYD_SCH_REQ_GP		= "L"; //조업설비 인출 스케줄 
				
				// 스케쥴기준적용: S
				jrParam.setField("YD_TO_LOC_DCSN_MTD"		, "S" );
			}
			
			sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook"); 
			//스케줄코드로 스케줄기준Table조회
			jrParam.setField("YD_SCH_CD", sYD_SCH_CD);
			rsResult = commDao.select(jrParam, getYdSchrule, logId, methodNm, "스케줄 기준 조회"); 
	    	
			if (rsResult != null && rsResult.size() > 0) {
				sYD_SCH_PRIOR = rsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
			} else {
				throw new YFUserException("스케쥴코드가 존재하지 않습니다. 스케쥴기준조회화면에서 확인하세요 : [" + sYD_SCH_CD + "]");
			}			
			
			/**********************************************************
			* 4. 작업예약(TB_YF_WRKBOOK) 생성
			**********************************************************/
			jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
			jrParam.setField("YD_GP"			, sYD_GP);
			jrParam.setField("YD_BAY_GP"		, sCrnCallBayGp);
			jrParam.setField("YD_SCH_CD"		, sYD_SCH_CD); //야드스케쥴코드
			jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
			jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
			jrParam.setField("YD_SCH_ST_GP"		, "M"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
			jrParam.setField("YD_SCH_REQ_GP"	, sYD_SCH_REQ_GP); //야드스케쥴요청구분
			jrParam.setField("YD_STR_LOC"	, sFrmLoc );//코일 저장위치 from
			commDao.insert(jrParam, insWrkBook2, logId, methodNm, "작업예약(TB_YF_WRKBOOK) 생성");

			/**********************************************************
			* 5. 작업예약재료(TB_YF_WRKBOOKMTL) 생성
			**********************************************************/
				
			jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
			jrParam.setField("STL_NO"			, sSTL_NO);
			jrParam.setField("YD_STK_COL_GP"	, sFrom_YD_STK_COL_GP);
			jrParam.setField("YD_STK_BED_NO"	, sFrom_YD_STK_BED_NO);
			jrParam.setField("YD_STK_LYR_NO"	, sFrom_YD_STK_LYR_NO);
			commDao.insert(jrParam, insYfWrkBookMtl, logId, methodNm, "작업예약재료(TB_YF_WRKBOOKMTL) 생성");
			
			/**********************************************************
			* 6. TB_YF_STOCK의 저장품 이동 조건(STOCK_MOVE_TERM) 변경
			**********************************************************/
			jrParam.setField("STL_NO"			, sSTL_NO);
			jrParam.setField("STOCK_MOVE_TERM"	, sSTOCK_MOVE_TERM);
			commDao.update(jrParam, updStockTransInfo1, logId, methodNm, "TB_YF_STOCK의 저장품 이동 조건(STOCK_MOVE_TERM) 변경");
			
			/**********************************************************
			* 7. 크레인스케줄 전문 호출
			**********************************************************/
			jrParam.setField("YD_WBOOK_ID"  	, sYD_WBOOK_ID); //야드작업예약ID
			jrParam.setField("YD_SCH_CD"    	, sYD_SCH_CD  ); //야드스케쥴코드
			jrParam.setField("YD_SCH_ST_GP" 	, "M"      ); //야드스케쥴기동구분
			jrParam.setField("YD_SCH_REQ_GP"	, sYD_SCH_REQ_GP); //야드스케쥴요청구분
			jrRtn = commUtils.addSndData(jrRtn, comm.getCrnSchMsg(jrParam));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		}catch(YFUserException e){
			throw new DAOException(commUtils.makeUserErrorLog(logId, methodNm, e));
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of reqPapWrapInOut
} 
