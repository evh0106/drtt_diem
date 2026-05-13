/**
 * @(#)SlabYdJspDAO
 *
 * @version          V1.00
 * @author           허철호
 * @date             2013/03/04
 *
 * @description      Slab야드 화면 처리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2013/03/04   허철호      허철호      최초 등록
 * V1.01  2015/12/15   이준영      이준영      항만야드 설비추가
 */
package com.inisteel.cim.yd.slabyd.dao;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;
import jspeed.base.log.LogLevel;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import java.util.ArrayList;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ct.common.util.CmnUtil;
import com.inisteel.cim.ts.common.TsCommUtil;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

public class SlabYdJspDAO extends DBAssistantDAO {

	private YdSlabUtils slabUtils = new YdSlabUtils();
	private YdPICommDAO ydPICommDAO = new YdPICommDAO();
	
	/***************************************************************************
	 * 크레인작업관리
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 크레인작업관리 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getCrnWrkMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리[SlabYdJspDAO.getCrnWrkMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";
		
		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("SM".equals(gdReq.getParam("V_TRT_GP"))) {
				trtNm = "스케줄재료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCrnWrkMgtMtl_PIDEV";
				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
					};
			} else {
				trtNm = "스케줄정보 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCrnWrkMgtSch";
				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_YD_GP"    )) //야드구분
						,slabUtils.trim(gdReq.getParam("V_YD_BAY_GP")) //야드동구분
						,slabUtils.trim(gdReq.getParam("V_YD_EQP_ID")) //야드설비구분(크레인)
						,slabUtils.trim(gdReq.getParam("V_YD_SCH_CD")) //야드스케줄코드
						,slabUtils.trim(gdReq.getParam("viewRows"   )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"   )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"   )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"   )) //viewPage
					};
			}
			
			trtNm += " : ";
			// PIDEV
//			jspeed_query_id = ydPICommDAO.getYdRulePI("", trtNm + methodNm, "YD0001", jspeed_query_id, "APPPI0", "*", "*" );		
			

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업관리 조회 (처리용)
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getCrnWrkMgt(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인작업관리[SlabYdJspDAO.getCrnWrkMgt] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("CMSch".equals(trtGp)) {
				trtNm = "크레인변경 스케줄정보 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCrnWrkMgtCMSch";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
					};
			} else if ("DMBed".equals(trtGp)) {
				trtNm = "권하위치변경 Bed정보 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCrnWrkMgtDMBed";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
					};
			} else if ("WCSch".equals(trtGp)) {
				trtNm = "작업취소 스케줄정보 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCrnWrkMgtWCSch";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("SCSch".equals(trtGp)) {
				trtNm = "크레인스케줄취소 스케줄정보 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCrnWrkMgtSCSch";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
					};
			} else if("SI".equals(trtGp)){
				trtNm = "스케줄 조회by CRN_SCH_ID";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCrnWrkMgtSchById";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			JDTORecordSet jsRst = getRecordSet(jspeed_query_id, param);
				
			slabUtils.printLog(logId, trtNm + jsRst.size(), "DB");
			
			return jsRst;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업관리 수정
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updCrnWrkMgt(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인작업관리[SlabYdJspDAO.updCrnWrkMgt] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("CMCrnSch".equals(trtGp)) {
				trtNm = "크레인스케줄(TB_YD_CRNSCH) 크레인변경 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtCMCrnSch";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"    )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_SCH_PRIOR")) //야드스케쥴우선순위
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"   )) //야드설비ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID" )) //야드작업예약ID
					};
			} else if ("DMStkLyr".equals(trtGp)) {
				trtNm = "적치단(TB_YD_STKLYR) 권하위치변경 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtDMStkLyr";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_DN_WO_LOC"  )) //야드권하지시위치
						,slabUtils.trim(jrParam.getFieldString("V_YD_DN_WO_LAYER")) //야드권하지시단
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID" )) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
					};
			} else if ("DMStkBed".equals(trtGp)) {
				trtNm = "적치Bed(TB_YD_STKBED) 권하위치변경(완산) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtDMStkBed";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP_OLD")) //야드적치열구분(기존)
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO_OLD")) //야드적치Bed번호(기존)
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP_NEW")) //야드적치열구분(신규)
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO_NEW")) //야드적치Bed번호(신규)
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP_OLD")) //야드적치열구분(기존)
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO_OLD")) //야드적치Bed번호(기존)
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"         )) //수정자
					};
			} else if ("DMCrnSch".equals(trtGp)) {
				trtNm = "크레인스케줄(TB_YD_CRNSCH) 권하위치변경 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtDMCrnSch";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_DN_WO_LOC"      )) //야드권하지시위치
						,slabUtils.trim(jrParam.getFieldString("V_YD_DN_WO_LAYER"    )) //야드권하지시단
						,slabUtils.trim(jrParam.getFieldString("V_YD_DN_WO_LOC_XAXIS")) //야드권하지시X축
						,slabUtils.trim(jrParam.getFieldString("V_YD_DN_WO_LOC_YAXIS")) //야드권하지시Y축
						,slabUtils.trim(jrParam.getFieldString("V_YD_DN_WO_LOC_ZAXIS")) //야드권하지시Z축
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID"     )) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"          )) //수정자
					};
			} else if ("DMTcarSch".equals(trtGp)) {
				trtNm = "대차스케줄(TB_YD_TCARSCH) 권하위치변경 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtDMTcarSch";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"            )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_CARLD_WRK_BOOK_ID")) //야드상차작업예약ID
					};
			} else if ("DMCarSch".equals(trtGp)) {
				trtNm = "차량스케줄(TB_YD_CARSCH) 권하위치변경 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtDMCarSch";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"            )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_CARLD_WRK_BOOK_ID")) //야드상차작업예약ID
					};
			} else if ("DMStkCol".equals(trtGp)) {
				trtNm = "적치열(TB_YD_STKCOL) 권하위치변경 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtDMStkCol";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"            )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_CARLD_WRK_BOOK_ID")) //야드상차작업예약ID
					};
			} else if ("HRCrnSch".equals(trtGp)) {
				trtNm = "크레인스케줄(TB_YD_CRNSCH) 보류/해제 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtHRCrnSch";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"            )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WRK_PROG_STAT_NEW")) //야드작업진행상태(신규)
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"         )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WRK_PROG_STAT_OLD")) //야드작업진행상태(기존)
					};
			} else if ("SCStkLyr".equals(trtGp)) {
				trtNm = "적치단(TB_YD_STKLYR) 크레인스케줄취소 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtSCStkLyr";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
					};
			} else if ("SCStkBed".equals(trtGp)) {
				trtNm = "적치Bed(TB_YD_STKBED) 크레인스케줄취소(완산) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtSCStkBed";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
					};
			} else if ("SCCrnMtl".equals(trtGp)) {
				trtNm = "크레인작업재료(TB_YD_CRNWRKMTL) 크레인스케줄취소 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtSCCrnMtl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
					};
			} else if ("SCCrnSch".equals(trtGp)) {
				trtNm = "크레인스케줄(TB_YD_CRNSCH) 크레인스케줄취소 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtSCCrnSch";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
					};
			} else if ("WBtoLocGuide".equals(trtGp)) {
				trtNm = "작업예약(TB_YD_WRKBOOK) TO위치 GUIDE 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updWbToLocGuide";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_TO_LOC_GUIDE")) //TO위치GUIDE
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
					};
			} else if ("DMCrnSch2".equals(trtGp)) {
				trtNm = "크레인스케줄(TB_YD_CRNSCH) 권하위치변경 수정2";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtDMCrnSch2";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_DN_WO_LOC"      )) //야드권하지시위치
						,slabUtils.trim(jrParam.getFieldString("V_YD_DN_WO_LAYER"    )) //야드권하지시단
						,slabUtils.trim(jrParam.getFieldString("V_YD_DN_WO_LOC_XAXIS")) //야드권하지시X축
						,slabUtils.trim(jrParam.getFieldString("V_YD_DN_WO_LOC_YAXIS")) //야드권하지시Y축
						,slabUtils.trim(jrParam.getFieldString("V_YD_DN_WO_LOC_ZAXIS")) //야드권하지시Z축
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID"     )) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"          )) //수정자
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";
			
			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt, "DB");

			return trtCnt;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/***************************************************************************
	 * 크레인상태설정
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 크레인상태설정 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getCrnStatSet(GridData gdReq) throws DAOException {
		String methodNm = "크레인상태설정[SlabYdJspDAO.getCrnStatSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";
		
		try {
			trtNm = "크레인상태정보 조회";
			String jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCrnStatSet";
			Object[] param = new Object[] {
					 slabUtils.trim(gdReq.getParam("V_YD_EQP_ID")) //야드설비구분(크레인)
					,slabUtils.trim(gdReq.getParam("V_YD_EQP_ID")) //야드설비구분(크레인)
				};
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}


	/***************************************************************************
	 * 크레인작업예약관리
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 크레인작업예약관리
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getCrnWrkBookMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업예약관리[SlabYdJspDAO.getCrnWrkBookMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("SM".equals(gdReq.getParam("V_TRT_GP"))) {
				trtNm = "작업예약재료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCrnWrkBookMgtMtl";

				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else {
				if ("N".equals(gdReq.getParam("V_DEL_YN"))) {
					trtNm = "작업예약정보(미완료) 조회";
					jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCrnWrkBookMgtWBN";

					param = new Object[] {
							 slabUtils.trim(gdReq.getParam("V_YD_GP"    )) //야드구분
							,slabUtils.trim(gdReq.getParam("V_YD_BAY_GP")) //야드동구분
							,slabUtils.trim(gdReq.getParam("V_YD_SCH_CD")) //야드스케줄코드
							,slabUtils.trim(gdReq.getParam("V_YD_SCH_CD")) //야드스케줄코드
							,slabUtils.trim(gdReq.getParam("V_YD_EQP_ID")) //야드작업크레인
							,slabUtils.trim(gdReq.getParam("viewRows"   )) //viewRows
							,slabUtils.trim(gdReq.getParam("viewPage"   )) //viewPage
							,slabUtils.trim(gdReq.getParam("viewRows"   )) //viewRows
							,slabUtils.trim(gdReq.getParam("viewPage"   )) //viewPage
						};
				} else {
					trtNm = "작업예약정보(완료) 조회";
					jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCrnWrkBookMgtWBY";

					param = new Object[] {
							 slabUtils.trim(gdReq.getParam("V_YD_GP"    )) //야드구분
							,slabUtils.trim(gdReq.getParam("V_YD_BAY_GP")) //야드동구분
							,slabUtils.trim(gdReq.getParam("V_YD_SCH_CD")) //야드스케줄코드
							,slabUtils.trim(gdReq.getParam("V_YD_SCH_CD")) //야드스케줄코드
							,slabUtils.trim(gdReq.getParam("V_YD_EQP_ID")) //야드작업크레인
							,slabUtils.trim(gdReq.getParam("V_DATE_FR"  )) //시작일자
							,slabUtils.trim(gdReq.getParam("V_DATE_TO"  )) //종료일자
							,slabUtils.trim(gdReq.getParam("viewRows"   )) //viewRows
							,slabUtils.trim(gdReq.getParam("viewPage"   )) //viewPage
							,slabUtils.trim(gdReq.getParam("viewRows"   )) //viewRows
							,slabUtils.trim(gdReq.getParam("viewPage"   )) //viewPage
						};
				}
			}
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업예약관리 조회 (처리용)
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getCrnWrkBookMgt(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인작업예약관리[SlabYdJspDAO.getCrnWrkBookMgt] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("CrnSch".equals(trtGp)) {
				trtNm = "크레인스케줄 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCrnWrkBookMgtCrnSch";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			JDTORecordSet jsRst = getRecordSet(jspeed_query_id, param);
				
			slabUtils.printLog(logId, trtNm + jsRst.size(), "DB");
			
			return jsRst;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업예약관리 Batch 처리
	 *      
	 *      @param String trtGp
	 *      @param String[][] param
	 *      @param String logId
	 *      @param String methodNm
	 *      @return int
	 *      @throws DAOException
	*/
	public int updCrnWrkBookMgt(String trtGp, String[][] param, String logId, String mthdNm) throws DAOException {
		String methodNm = "크레인작업예약관리[SlabYdJspDAO.updCrnWrkBookMgt] < " + mthdNm;
		String trtNm = "";

		try {
			String jspeed_query_id = "";

			if ("CarSch".equals(trtGp)) {
				trtNm = "차량스케줄(TB_YD_CARSCH) 작업예약ID 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkBookMgtCarSch";
			} else if ("TcarSch".equals(trtGp)) {
				trtNm = "대차스케줄(TB_YD_TCARSCH) 작업예약ID 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkBookMgtTcarSch";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			slabUtils.printParam(logId, trtNm, param);
			
			int[] trtRst = trtProcess(jspeed_query_id, param);

			return trtRst.length;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 공장휴지계획 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getCrnFixPlan(GridData gdReq) throws DAOException {
		String methodNm = "공장휴지계획 조회[SlabYdJspDAO.getCrnFixPlan] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "공장휴지계획 조회";
		
		try {
			String jspeed_query_id = "";
			Object[] param = null;

			trtNm = "공장휴지계획 조회";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCrnFixPlan";
			param = new Object[] {
					slabUtils.trim(gdReq.getParam("V_DOWN_START_DDTT")) //휴지시작일시
					};
		
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인보수계획관리 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getCcCrnFixPlan(GridData gdReq) throws DAOException {
		String methodNm = "크레인보수계획관리 조회[SlabYdJspDAO.getCcCrnFixPlan] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "보수계획관리 조회";
		
		try {
			String jspeed_query_id = "";
			Object[] param = null;

			trtNm = "보수계획관리 조회";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCcCrnFixPlan";
			param = new Object[] {
					 slabUtils.trim(gdReq.getParam("V_PAUSE_CODE")) //휴지코드
					,slabUtils.trim(gdReq.getParam("V_MAINT_START_DATE")) //휴지시작일시
					,slabUtils.trim(gdReq.getParam("V_YD_WRK_CRN")) //크레인
					};
		
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인 보수계획 수정
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public int updCrnFixPlan(GridData gdReq) throws DAOException {
		String methodNm = "크레인 보수계획 수정[SlabYdJspDAO.updCrnFixPlan] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "크레인 보수계획 수정";
		
		try {
			String jspeed_query_id = "";
			Object[] param = null;

			trtNm = "크레인 보수계획 수정";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCcCrnFixPlan";
			param = new Object[] {
					 slabUtils.trim(gdReq.getParam("V_PAUSE_PLAN_DATE"))
					,slabUtils.trim(gdReq.getParam("V_PAUSE_CODE"))
					,slabUtils.trim(gdReq.getParam("V_YD_WRK_CRN"))
					,slabUtils.trim(gdReq.getParam("userid"))
					,slabUtils.trim(gdReq.getParam("WIRE_MAINT_CONTENTS"))
					,slabUtils.trim(gdReq.getParam("ELEC_MAINT_CONTENTS"))
					,slabUtils.trim(gdReq.getParam("MC_MAINT_CONTENTS"))
					,slabUtils.trim(gdReq.getParam("ETC_CONTENTS"))
					,slabUtils.trim(gdReq.getParam("USE_PLN_CONTENTS"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS1"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS2"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS3"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS4"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS5"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS6"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS7"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS8"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS9"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS10"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS11"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS12"))
					,slabUtils.trim(gdReq.getParam("V_PAUSE_PLAN_DATE"))
					,slabUtils.trim(gdReq.getParam("V_PAUSE_CODE"))
					,slabUtils.trim(gdReq.getParam("V_YD_WRK_CRN"))
					,slabUtils.trim(gdReq.getParam("userid"))
					,slabUtils.trim(gdReq.getParam("userid"))
					,slabUtils.trim(gdReq.getParam("WIRE_MAINT_CONTENTS"))
					,slabUtils.trim(gdReq.getParam("ELEC_MAINT_CONTENTS"))
					,slabUtils.trim(gdReq.getParam("MC_MAINT_CONTENTS"))
					,slabUtils.trim(gdReq.getParam("ETC_CONTENTS"))
					,slabUtils.trim(gdReq.getParam("USE_PLN_CONTENTS"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS1"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS2"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS3"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS4"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS5"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS6"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS7"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS8"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS9"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS10"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS11"))
					,slabUtils.trim(gdReq.getParam("HR_CONTENTS12"))
					};
		
			trtNm += " : ";

			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt + " 건", "DB");

			return trtCnt;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인 보수계획 삭제
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public int delCrnFixPlan(GridData gdReq) throws DAOException {
		String methodNm = "크레인 보수계획 삭제[SlabYdJspDAO.delCrnFixPlan] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "크레인 보수계획 삭제";
		
		try {
			String jspeed_query_id = "";
			Object[] param = null;

			trtNm = "크레인 보수계획 삭제";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.delCcCrnFixPlan";
			param = new Object[] {
					 slabUtils.trim(gdReq.getParam("userid"))
					,slabUtils.trim(gdReq.getParam("V_PAUSE_PLAN_DATE"))
					,slabUtils.trim(gdReq.getParam("V_PAUSE_CODE"))
					,slabUtils.trim(gdReq.getParam("V_YD_WRK_CRN"))
					};
		
			trtNm += " : ";

			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt + " 건", "DB");

			return trtCnt;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 공장 별 보수명 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMaintName(GridData gdReq) throws DAOException {
		String methodNm = "공장 별 보수명 조회[SlabYdJspDAO.getMaintName] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "공장 별 보수명 조회";
		
		try {
			String jspeed_query_id = "";
			Object[] param = null;

			trtNm = "공장 별 보수명 조회";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getMaintName";
			param = new Object[] {
					 slabUtils.trim(gdReq.getParam("V_IDX_NM")) //공장명
					};
		
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 공장 휴지계획 등록
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public int insPausePlan(GridData gdReq) throws DAOException {
		String methodNm = "공장 휴지계획 등록[SlabYdJspDAO.insPausePlan] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "공장 휴지계획 등록";
		
		try {
			String jspeed_query_id = "";
			Object[] param = null;

			trtNm = "공장 휴지계획 등록";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.insCrnFixPlan";
			param = new Object[] {
					 slabUtils.trim(gdReq.getParam("V_PAUSE_PLAN_DATE")) //휴지계획일
					,slabUtils.trim(gdReq.getParam("V_PAUSE_CODE")) //휴지코드
					,slabUtils.trim(gdReq.getParam("userid"))
					,slabUtils.trim(gdReq.getParam("userid"))
					,slabUtils.trim(gdReq.getParam("V_PAUSE_PLAN_TIME")) //휴지계획시간
					};
		
			trtNm += " : ";

			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt + " 건", "DB");

			return trtCnt;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 공장 휴지계획 삭제
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public int delPausePlan(GridData gdReq) throws DAOException {
		String methodNm = "공장 휴지계획 삭제[SlabYdJspDAO.delPausePlan] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "공장 휴지계획 삭제";
		
		try {
			String jspeed_query_id = "";
			Object[] param = null;

			trtNm = "공장 휴지계획 삭제";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnFixPlan";
			param = new Object[] {
					 slabUtils.trim(gdReq.getParam("V_PAUSE_PLAN_DATE")) //휴지계획일
					,slabUtils.trim(gdReq.getParam("V_PAUSE_CODE")) //휴지코드
					};
		
			trtNm += " : ";

			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt + " 건", "DB");

			return trtCnt;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 공장 휴지계획 완료 등록
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public int updPausePlan(GridData gdReq) throws DAOException {
		String methodNm = "공장 휴지계획 완료 등록[SlabYdJspDAO.updPausePlan] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "공장 휴지계획 완료 등록";
		
		try {
			String jspeed_query_id = "";
			Object[] param = null;

			trtNm = "공장 휴지계획 완료 등록";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnFixPlanFin";
			param = new Object[] {
					 slabUtils.trim(gdReq.getParam("V_PAUSE_PLAN_DATE")) //휴지계획일
					,slabUtils.trim(gdReq.getParam("V_PAUSE_CODE")) //휴지코드
					,slabUtils.trim(gdReq.getParam("V_YD_WRK_CRN"))
					};
		
			trtNm += " : ";

			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt + " 건", "DB");

			return trtCnt;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 와이어 보수이력 기존 실적 삭제
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public int updWireMaintHist(GridData gdReq) throws DAOException {
		String methodNm = "와이어 보수이력 기존 실적 삭제[SlabYdJspDAO.updWireMaintHist] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "와이어 보수이력 기존 실적 삭제";
		
		try {
			String jspeed_query_id = "";
			Object[] param = null;

			trtNm = "와이어 보수이력 기존 실적 삭제";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updWireMaintHist";
			param = new Object[] {
					 slabUtils.trim(gdReq.getParam("userid"))
					,slabUtils.trim(gdReq.getParam("V_YD_WRK_CRN"))
					,slabUtils.trim(gdReq.getParam("V_YD_WRK_CRN")) 
					,slabUtils.trim(gdReq.getParam("V_WIRE_MAINT_CONTENTS"))
					,slabUtils.trim(gdReq.getParam("V_WIRE_MAINT_CONTENTS"))
					};
		
			trtNm += " : ";

			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt + " 건", "DB");

			return trtCnt;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 와이어 보수이력 등록
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public int insWireMaintHist(GridData gdReq) throws DAOException {
		String methodNm = "와이어 보수이력 등록[SlabYdJspDAO.insWireMaintHist] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "와이어 보수이력 등록";
		
		try {
			String jspeed_query_id = "";
			Object[] param = null;

			trtNm = "와이어 보수이력 등록";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.insWireMaintHist";
			param = new Object[] {
					 slabUtils.trim(gdReq.getParam("V_YD_WRK_CRN"))
					,slabUtils.trim(gdReq.getParam("V_YD_WRK_CRN"))
					,slabUtils.trim(gdReq.getParam("V_WIRE_MAINT_CONTENTS"))
					,slabUtils.trim(gdReq.getParam("V_WIRE_MAINT_CONTENTS"))
					,slabUtils.trim(gdReq.getParam("userid"))
					,slabUtils.trim(gdReq.getParam("userid"))
					};
		
			trtNm += " : ";

			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt + " 건", "DB");

			return trtCnt;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인별 휴지코드 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getPauseCode(GridData gdReq) throws DAOException {
		String methodNm = "크레인별 휴지코드 조회[SlabYdJspDAO.getPauseCode] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "크레인별 휴지코드 조회";
		
		try {
			String jspeed_query_id = "";
			Object[] param = null;

			trtNm = "크레인별 휴지코드 조회";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getPauseCodeByCrn";
			param = new Object[] {
					 slabUtils.trim(gdReq.getParam("V_YD_WRK_CRN")) //크레인명
					};
		
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	
	/***************************************************************************
	 * 압연지시조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 압연지시조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMillWoInq(GridData gdReq) throws DAOException {
		String methodNm = "압연지시조회[SlabYdJspDAO.getMillWoInq] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			String ydGp = slabUtils.trim(gdReq.getParam("V_YD_GP")); //야드구분

			if ("".equals(ydGp)) {
				return null;
			}
			
			if ("A".equals(ydGp)) {
				trtNm = "C열연압연지시 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getMillWoInqHr";
			} else if ("D".equals(ydGp)) {
				trtNm = "후판압연지시 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getMillWoInqPr";
			}

			if ("A".equals(ydGp)) {
				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
					};
			} else if ("D".equals(ydGp)) {
				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")) //야드적치열구분 
						,slabUtils.trim(gdReq.getParam("V_YD_STK_BED_NO")) //베드번호
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
						,slabUtils.trim(gdReq.getParam("V_DUMMY_YN"       ))
						,slabUtils.trim(gdReq.getParam("V_DUMMY_YN"       ))
					};
			}
			
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 압연지시조회2
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMillWoInq2(GridData gdReq) throws DAOException {
		String methodNm = "압연지시조회[SlabYdJspDAO.getMillWoInq2] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			String ydGp = slabUtils.trim(gdReq.getParam("V_YD_GP")); //야드구분

			if ("".equals(ydGp)) {
				return null;
			}
			
			if ("A".equals(ydGp)) {
				trtNm = "C열연압연지시 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getMillWoInqHr";
			} else if ("D".equals(ydGp)) {
				trtNm = "후판압연지시 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getMillWoInqPr2";
			}

			if ("A".equals(ydGp)) {
				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
					};
			} else if ("D".equals(ydGp)) {
				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_CHK")) //체크구분
						,slabUtils.trim(gdReq.getParam("V_YD_STR_LOC")) //야드적치열구분
						,slabUtils.trim(gdReq.getParam("V_CHK")) //체크구분
						,slabUtils.trim(gdReq.getParam("V_YD_STR_LOC")) //야드적치열구분
						,slabUtils.trim(gdReq.getParam("V_YD_STR_LOC")) //야드적치열구분
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
						,slabUtils.trim(gdReq.getParam("V_DUMMY_YN"       ))
						,slabUtils.trim(gdReq.getParam("V_DUMMY_YN"       ))
					};
			}
			
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 압연지시일별조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMillWoInq3(GridData gdReq) throws DAOException {
		String methodNm = "압연지시일별조회[SlabYdJspDAO.getMillWoInq3] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			String ydGp = slabUtils.trim(gdReq.getParam("V_YD_GP")); //야드구분

			if ("".equals(ydGp)) {
				return null;
			}
			
			if ("D".equals(ydGp)) {
				trtNm = "후판압연지시 일별조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getMillWoInqPrByDay";
			}

			if ("D".equals(ydGp)) {
				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_CHK")) //체크구분
						,slabUtils.trim(gdReq.getParam("V_YD_STR_LOC")) //야드적치열구분
						,slabUtils.trim(gdReq.getParam("V_CHK")) //체크구분
						,slabUtils.trim(gdReq.getParam("V_YD_STR_LOC")) //야드적치열구분
						,slabUtils.trim(gdReq.getParam("V_DATE_FROM"))
						,slabUtils.trim(gdReq.getParam("V_DATE_TO"))
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
						,slabUtils.trim(gdReq.getParam("V_DUMMY_YN"       ))
						,slabUtils.trim(gdReq.getParam("V_DUMMY_YN"       ))
					};
			}
			
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 후판예정압연지시조회(2차절단예정포함)
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMillWoInqSCH(GridData gdReq) throws DAOException {
		String methodNm = "후판예정압연지시조회[SlabYdJspDAO.getMillWoInqSCH] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			String ydGp = slabUtils.trim(gdReq.getParam("V_YD_GP")); //야드구분

			if ("".equals(ydGp)) {
				return null;
			}
			
			
			if ("D".equals(ydGp)) {
				trtNm = "후판예정압연지시 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getMillWoInqPrSCH";
			}

			if ("D".equals(ydGp)) {
				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_CHK")) //체크구분
						,slabUtils.trim(gdReq.getParam("V_YD_STR_LOC")) //야드적치열구분
						,slabUtils.trim(gdReq.getParam("V_CHK")) //체크구분
						,slabUtils.trim(gdReq.getParam("V_YD_STR_LOC")) //야드적치열구분
						,slabUtils.trim(gdReq.getParam("V_YD_STR_LOC")) //야드적치열구분
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
						,slabUtils.trim(gdReq.getParam("V_DUMMY_YN"       ))
						,slabUtils.trim(gdReq.getParam("V_DUMMY_YN"       ))
					};
			}
			
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 후판 압연지시 메세지 조회 
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMillWoInqMsg(GridData gdReq) throws DAOException {
		String methodNm = "후판압연지시메세지조회[SlabYdJspDAO.getMillWoInqMsg] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			
			trtNm = "후판압연지시메세지조회";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getMillWoInqMsg";
			
			param = new Object[] {
					 slabUtils.trim(gdReq.getParam("DATE_FROM")) //조회날짜 FROM
						,slabUtils.trim(gdReq.getParam("DATE_TO")) // 조회날짜 TO
						,slabUtils.trim(gdReq.getParam("MAX_SEQ_NO")) // 최대 seq 넘버
				};
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 후판 압연지시 메세지 조회 
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMillWoInqMsgBySeqNo(GridData gdReq) throws DAOException {
		String methodNm = "후판압연지시메세지조회[SlabYdJspDAO.getMillWoInqMsgBySeqNo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			
			trtNm = "후판압연지시메세지조회";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getMillWoInqMsgBySeqNo";
			
			param = new Object[] {
						slabUtils.trim(gdReq.getParam("SEQ_NO")) // 최대 seq 넘버
				};
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 후판 압연지시 메세지 이력 조회 
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMillWoInqMsgAll(GridData gdReq) throws DAOException {
		String methodNm = "후판압연지시메세지이력조회[SlabYdJspDAO.getMillWoInqMsgAll] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			
			trtNm = "후판압연지시메세지이력조회";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getMillWoInqMsgAll";
			
			param = new Object[] {
					 slabUtils.trim(gdReq.getParam("DATE_FROM")) //조회날짜 FROM
					,slabUtils.trim(gdReq.getParam("DATE_TO")) // 조회날짜 TO
					,slabUtils.trim(gdReq.getParam("V_YD_BAY_GP")) //야드적치동 구분
				};
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 후판 압연지시 메세지 확인 
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public int updMillWoInqMsg(GridData gdReq) throws DAOException {
		String methodNm = "후판압연지시메세지확인[SlabYdJspDAO.updMillWoInqMsg] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;
			
			if("".equals(slabUtils.trim(gdReq.getParam("length")))
				||	"".equals(slabUtils.trim(gdReq.getParam("userid")))) return 0;
			
			int result = 0;
			int length = Integer.parseInt(slabUtils.trim(gdReq.getParam("length")).toString());
			
			String userid = "";
			userid = slabUtils.trim(gdReq.getParam("userid"));
			
			for(int i=0; i<length; i++){
				String seq_no = "";
				seq_no = slabUtils.trim(gdReq.getParam("SEQ_NO"+(i))).toString();
				if("".equals(seq_no)) continue;
				
				trtNm = "후판압연지시메세지확인";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updMillWoInqMsg";
				
				param = new Object[] {
						userid,
						seq_no //체크구분
					};
				
				trtNm += " : ";
				
				result += trtProcess(jspeed_query_id, param);
			}
			/*
			String seq_no = "";
			seq_no = slabUtils.trim(gdReq.getParam("V_SEQ_NO")).toString();
			
			if("".equals(seq_no)) return 0;
			
			trtNm = "후판압연지시메세지확인";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updMillWoInqMsg";
			
			
			param = new Object[] {
					 slabUtils.trim(gdReq.getParam("V_SEQ_NO")) //체크구분
					
				};
			
			trtNm += " : ";*/

			return result;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	/***************************************************************************
	 * 설비인출보급
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 설비인출보급 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getEqpPulloutSup(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급[SlabYdJspDAO.getEqpPulloutSup] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));

			if ("SB".equals(trtGp)) {
				trtNm = "Bed정보 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getEqpPulloutSupBed";

				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")) //야드적치열구분
					};
			} else if ("TM".equals(trtGp)) {
				trtNm = "Take-Out재료선택 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getEqpPulloutSupMtlTO";

				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_STL_NO")) //재료번호
						,slabUtils.trim(gdReq.getParam("viewRows")) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage")) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows")) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage")) //viewPage
					};
			} else {
				trtNm = "Bed재료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getEqpPulloutSupMtl_PIDEV";

				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(gdReq.getParam("V_YD_STK_BED_NO")) //야드적치Bed번호
					};
			}
			
			trtNm += " : ";
			//PIDEV
//			jspeed_query_id = ydPICommDAO.getYdRulePI("", trtNm + methodNm, "YD0001", jspeed_query_id, "APPPI0", "*", "*" );

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 설비인출보급 Batch 처리
	 *      
	 *      @param String trtGp
	 *      @param String[][] param
	 *      @param String logId
	 *      @param String methodNm
	 *      @return int
	 *      @throws DAOException
	*/
	public int updEqpPulloutSup(String trtGp, String[][] param, String logId, String mthdNm) throws DAOException {
		String methodNm = "설비인출보급[SlabYdJspDAO.updEqpPulloutSup] < " + mthdNm;
		String trtNm = "";

		try {
			String jspeed_query_id = "";

			if ("Stock".equals(trtGp)) {
				trtNm = "저장품(TB_YD_STOCK) 목표행선수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updEqpPulloutSupStock";
			} else if ("StkLyr".equals(trtGp)) {
				trtNm = "적치단(TB_YD_STKLYR) 재료삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updEqpPulloutSupStkLyr";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}

			slabUtils.printParam(logId, trtNm, param);
			
			trtNm += " : ";
			
			int[] trtRst = trtProcess(jspeed_query_id, param);

			return trtRst.length;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 설비인출보급 수정
	 *
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updEqpPulloutSup(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "설비인출보급[SlabYdJspDAO.updEqpPulloutSup] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("StkLyrFix".equals(trtGp)) {
				trtNm = "적치단(TB_YD_STKLYR) 정리 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updEqpPulloutSupStkLyrFix";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt + " 건", "DB");

			return trtCnt;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}


	/***************************************************************************
	 * 장입보급기준
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 장입보급기준 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getChgSupRule(GridData gdReq) throws DAOException {
		String methodNm = "장입보급기준 조회[SlabYdJspDAO.getChgSupRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			String jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getChgSupRule";

			Object[] param = new Object[] {
					slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")) //야드적치열구분
				};

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 장입보급기준 등록
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updChgSupRule(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "장입보급기준 등록[SlabYdJspDAO.updHsmAllSfSet] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("Rule".equals(trtGp)) {
				trtNm = "야드기준(TB_YD_RULE) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updChgSupRule";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP" )) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_TI_SUP_YN"     )) //TakeIn공Bed보급요구여부
						,slabUtils.trim(jrParam.getFieldString("V_TI_PRE_SUP_YN" )) //TakeInBed1매선보급요구여부
						,slabUtils.trim(jrParam.getFieldString("V_CI_AUTO_GP_YN" )) //보급요구대상재자동편성여부
						,slabUtils.trim(jrParam.getFieldString("V_CI_CHG_EXT_YN" )) //보급요구자동편성장입확장여부
						,slabUtils.trim(jrParam.getFieldString("V_CI_CHG_MTL_CNT")) //보급요구자동편성장입재료수
						,slabUtils.trim(jrParam.getFieldString("V_CI_CHG_LOT_CNT")) //보급요구자동편성장입Lot수
						,slabUtils.trim(jrParam.getFieldString("V_CI_CHG_NO_SORT")) //보급요구자동편성장입순위정렬여부
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt + " 건", "DB");

			return trtCnt;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/***************************************************************************
	 * 고강도재 상하면스카핑여부 등록
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 고강도재 상하면스카핑구분 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getHsmAllSfSet(GridData gdReq) throws DAOException {
		String methodNm = "고강도재 상하면스카핑구분 조회[SlabYdJspDAO.getHsmAllSfSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			String jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getHsmAllSfSet";

			Object[] param = new Object[] {
					slabUtils.trim(gdReq.getParam("V_STL_NO")) //재료번호
				};
			
			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 고강도재 상하면스카핑구분 등록
	 *
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updHsmAllSfSet(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "고강도재 상하면스카핑구분 등록[SlabYdJspDAO.updHsmAllSfSet] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("Stock".equals(trtGp)) {
				trtNm = "저장품(TB_YD_STOCK) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updHsmAllSfSet";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER")) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_JSF_GP"  )) //상하면스카핑구분(SNDBK_GP:반송요청구분)
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"  )) //재료번호
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt + " 건", "DB");

			return trtCnt;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}


	/***************************************************************************
	 * 스카핑보급관리
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 스카핑보급관리 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getSfSupMgt(GridData gdReq) throws DAOException {
		String methodNm = "스카핑보급관리[SlabYdJspDAO.getSfSupMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("SM".equals(gdReq.getParam("V_TRT_GP"))) {
				trtNm = "보급Lot편성완료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getSfSupMgtLot";

				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_SCARF_APR_PRIORITY")) //Scarfing승인우선순위
						,slabUtils.trim(gdReq.getParam("V_DATE_FR"           )) //Scarfing예정일자(시작일)
						,slabUtils.trim(gdReq.getParam("V_DATE_TO"           )) //Scarfing예정일자(종료일)
						,slabUtils.trim(gdReq.getParam("V_SCARF_PLAN_PLNT_GP")) //Scarfing예정공장
						,slabUtils.trim(gdReq.getParam("V_YD_GP"             ))	//야드구분
						,slabUtils.trim(gdReq.getParam("V_YD_GP"             ))	//야드구분
						,slabUtils.trim(gdReq.getParam("V_YD_BAY_GP"         )) //야드동구분
						,slabUtils.trim(gdReq.getParam("V_YD_EQP_GP"         )) //야드설비구분
						,slabUtils.trim(gdReq.getParam("V_YD_STK_COL_NO"     )) //야드적치열번호
						,slabUtils.trim(gdReq.getParam("V_YD_STK_BED_NO"     )) //야드적치베드번호
						,slabUtils.trim(gdReq.getParam("V_YD_AIM_RT_GP"      )) //야드목표행선구분
						,slabUtils.trim(gdReq.getParam("V_CURR_PROG_CD"      )) //현재진도코드
						,slabUtils.trim(gdReq.getParam("V_ORD_YEOJAE_GP"     )) //주문여재구분
						,slabUtils.trim(gdReq.getParam("V_WO_MSLAB_RPR_MTD"  )) //지시주편손질방법
						,slabUtils.trim(gdReq.getParam("V_STL_NO"            )) //재료번호
						,slabUtils.trim(gdReq.getParam("V_STL_NO"            )) //재료번호
						,slabUtils.trim(gdReq.getParam("V_SCARF_UGNT_MTL_GP" )) //Scarfing긴급재여부
						,slabUtils.trim(gdReq.getParam("V_VO_YN"             )) //자외판여부
						,slabUtils.trim(gdReq.getParam("viewRows"            )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"            )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"            )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"            )) //viewPage
						,slabUtils.trim(gdReq.getParam("V_MSLAB_RPR_MC_GP"   )) //그라인더 필수구분 (추가 : 2016.02.15 LeeJY)
					};
			} else if ("SC".equals(gdReq.getParam("V_TRT_GP"))) {
				trtNm = "보급Lot편성공정요구대상 조회";
				/* 항만야드 기능적용 보완 : 2015.12.15 by LeeJY
				if (slabUtils.trim(gdReq.getParam("V_YD_GP")).equals("M")) {
					jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getPortSfSupMgtTgt";
				}
				else {
					jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getSfSupMgtTgt2";
				}
				*/
				
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getSfSupMgtTgt2";

				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_YD_GP"             ))	//야드구분
						,slabUtils.trim(gdReq.getParam("V_SCARF_APR_PRIORITY")) //Scarfing승인우선순위
						,slabUtils.trim(gdReq.getParam("V_DATE_FR"           )) //Scarfing예정일자(시작일)
						,slabUtils.trim(gdReq.getParam("V_DATE_TO"           )) //Scarfing예정일자(종료일)
						,slabUtils.trim(gdReq.getParam("V_SCARF_PLAN_PLNT_GP")) //Scarfing예정공장
						,slabUtils.trim(gdReq.getParam("V_YD_GP"             ))	//야드구분
						,slabUtils.trim(gdReq.getParam("V_YD_BAY_GP"         )) //야드동구분
						,slabUtils.trim(gdReq.getParam("V_YD_EQP_GP"         )) //야드설비구분
						,slabUtils.trim(gdReq.getParam("V_YD_STK_COL_NO"     )) //야드적치열번호
						,slabUtils.trim(gdReq.getParam("V_YD_STK_BED_NO"     )) //야드적치베드번호
						,slabUtils.trim(gdReq.getParam("V_YD_AIM_RT_GP"      )) //야드목표행선구분
						,slabUtils.trim(gdReq.getParam("V_CURR_PROG_CD"      )) //현재진도코드
						,slabUtils.trim(gdReq.getParam("V_ORD_YEOJAE_GP"     )) //주문여재구분
						,slabUtils.trim(gdReq.getParam("V_WO_MSLAB_RPR_MTD"  )) //지시주편손질방법
						,slabUtils.trim(gdReq.getParam("V_MSLAB_RPR_MC_GP"   )) //그라인더 필수구분 (추가 : 2016.02.15 LeeJY)
						,slabUtils.trim(gdReq.getParam("V_STL_NO"            )) //재료번호
						,slabUtils.trim(gdReq.getParam("V_STL_NO"            )) //재료번호
						,slabUtils.trim(gdReq.getParam("V_YD_GP"             ))	//야드구분
						,slabUtils.trim(gdReq.getParam("V_SCARF_UGNT_MTL_GP" )) //Scarfing긴급재여부
						,slabUtils.trim(gdReq.getParam("V_VO_YN"             )) //자외판여부
						,slabUtils.trim(gdReq.getParam("viewRows"            )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"            )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"            )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"            )) //viewPage
					};
			} else if ("ST".equals(gdReq.getParam("V_TRT_GP"))) {
				trtNm = "보급Lot편성긴급재 조회";
				/* 항만야드 기능적용 보완 : 2015.12.15 by LeeJY
				if (slabUtils.trim(gdReq.getParam("V_YD_GP")).equals("M")) {
					jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getPortSfSupMgtTgt1";
				}
				else {
					jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getSfSupMgtTgt";
				}
				*/
				
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getSfSupMgtTgt";

				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_SCARF_APR_PRIORITY")) //Scarfing승인우선순위
						,slabUtils.trim(gdReq.getParam("V_DATE_FR"           )) //Scarfing예정일자(시작일)
						,slabUtils.trim(gdReq.getParam("V_DATE_TO"           )) //Scarfing예정일자(종료일)
						,slabUtils.trim(gdReq.getParam("V_SCARF_PLAN_PLNT_GP")) //Scarfing예정공장
						,slabUtils.trim(gdReq.getParam("V_YD_GP"             ))	//야드구분
						,slabUtils.trim(gdReq.getParam("V_YD_BAY_GP"         )) //야드동구분
						,slabUtils.trim(gdReq.getParam("V_YD_EQP_GP"         )) //야드설비구분
						,slabUtils.trim(gdReq.getParam("V_YD_STK_COL_NO"     )) //야드적치열번호
						,slabUtils.trim(gdReq.getParam("V_YD_STK_BED_NO"     )) //야드적치베드번호
						,slabUtils.trim(gdReq.getParam("V_YD_AIM_RT_GP"      )) //야드목표행선구분
						,slabUtils.trim(gdReq.getParam("V_CURR_PROG_CD"      )) //현재진도코드
						,slabUtils.trim(gdReq.getParam("V_ORD_YEOJAE_GP"     )) //주문여재구분
						,slabUtils.trim(gdReq.getParam("V_WO_MSLAB_RPR_MTD"  )) //지시주편손질방법
						,slabUtils.trim(gdReq.getParam("V_MSLAB_RPR_MC_GP"   )) //그라인더 필수구분 (추가 : 2016.02.15 LeeJY)
						,slabUtils.trim(gdReq.getParam("V_STL_NO"            )) //재료번호
						,slabUtils.trim(gdReq.getParam("V_STL_NO"            )) //재료번호
						,slabUtils.trim(gdReq.getParam("V_YD_GP"             ))	//야드구분
						,slabUtils.trim(gdReq.getParam("V_SCARF_UGNT_MTL_GP" )) //Scarfing긴급재여부
						,slabUtils.trim(gdReq.getParam("V_VO_YN"             )) //자외판여부
						,slabUtils.trim(gdReq.getParam("viewRows"            )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"            )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"            )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"            )) //viewPage
					};
			}else  {
				trtNm = "보급Lot편성대상재 조회";
		  
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getSfSupMgtTgtSelect";

				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_SCARF_APR_PRIORITY")) //Scarfing승인우선순위
						,slabUtils.trim(gdReq.getParam("V_DATE_FR"           )) //Scarfing예정일자(시작일)
						,slabUtils.trim(gdReq.getParam("V_DATE_TO"           )) //Scarfing예정일자(종료일)
						,slabUtils.trim(gdReq.getParam("V_SCARF_PLAN_PLNT_GP")) //Scarfing예정공장
						,slabUtils.trim(gdReq.getParam("V_YD_GP"             ))	//야드구분
						,slabUtils.trim(gdReq.getParam("V_YD_BAY_GP"         )) //야드동구분
						,slabUtils.trim(gdReq.getParam("V_YD_EQP_GP"         )) //야드설비구분
						,slabUtils.trim(gdReq.getParam("V_YD_STK_COL_NO"     )) //야드적치열번호
						,slabUtils.trim(gdReq.getParam("V_YD_STK_BED_NO"     )) //야드적치베드번호
						,slabUtils.trim(gdReq.getParam("V_YD_AIM_RT_GP"      )) //야드목표행선구분
						,slabUtils.trim(gdReq.getParam("V_CURR_PROG_CD"      )) //현재진도코드
						,slabUtils.trim(gdReq.getParam("V_ORD_YEOJAE_GP"     )) //주문여재구분
						,slabUtils.trim(gdReq.getParam("V_WO_MSLAB_RPR_MTD"  )) //지시주편손질방법
						,slabUtils.trim(gdReq.getParam("V_MSLAB_RPR_MC_GP"   )) //그라인더 필수구분 (추가 : 2016.02.15 LeeJY)
						,slabUtils.trim(gdReq.getParam("V_STL_NO"            )) //재료번호
						,slabUtils.trim(gdReq.getParam("V_STL_NO"            )) //재료번호
						,slabUtils.trim(gdReq.getParam("V_YD_GP"             ))	//야드구분
						,slabUtils.trim(gdReq.getParam("V_SCARF_UGNT_MTL_GP" )) //Scarfing긴급재여부
						,slabUtils.trim(gdReq.getParam("V_VO_YN"             )) //자외판여부
						,slabUtils.trim(gdReq.getParam("viewRows"            )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"            )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"            )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"            )) //viewPage
					};
			}
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 스카핑보급관리 등록
	 *
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int insSfSupMgt(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "스카핑보급관리[SlabYdJspDAO.insSfSupMgt] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("PrepMtl".equals(trtGp)) {
				trtNm = "준비재료(TB_YD_PREPMTL) 등록)";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.insSfSupMgtPrepMtl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_PREP_SCH_ID")) //야드준비스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP" )) //야드적치열구분
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt + " 건", "DB");

			return trtCnt;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 스카핑보급관리 Batch 처리
	 *      
	 *      @param String trtGp
	 *      @param String[][] param
	 *      @param String logId
	 *      @param String methodNm
	 *      @return int
	 *      @throws DAOException
	*/
	public int updSfSupMgt(String trtGp, String[][] param, String logId, String mthdNm) throws DAOException {
		String methodNm = "스카핑보급관리[SlabYdJspDAO.updSfSupMgt] < " + mthdNm;
		String trtNm = "";

		try {
			String jspeed_query_id = "";

			if ("Dely".equals(trtGp)) {
				trtNm = "SLAB스카핑긴급대상지정(TB_PM_B_SLABSCARFINGUGNTASGN) 지연사유 등록(긴급재)";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updSfSupMgtDely";
			} else if ("DelyMs".equals(trtGp)) {
				trtNm = "SLAB스카핑긴급대상지정(TB_PM_B_SLABSCARFINGUGNTASGN) 지연사유 등록(공정요구)";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updSfSupMgtDely2";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}

			slabUtils.printParam(logId, trtNm, param);
			
			trtNm += " : ";
			
			int[] trtRst = trtProcess(jspeed_query_id, param);

			return trtRst.length;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}


	/***************************************************************************
	 * 2차절단보급관리
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 2차절단보급관리 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getStcmSupMgt(GridData gdReq) throws DAOException {
		String methodNm = "2차절단보급관리[SlabYdJspDAO.getStcmSupMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("SM".equals(gdReq.getParam("V_TRT_GP"))) {
				trtNm = "보급Lot편성완료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getStcmSupMgtLot";

				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_YD_BAY_GP"    )) //야드동구분
						,slabUtils.trim(gdReq.getParam("V_YD_EQP_GP"    )) //야드설비구분
						,slabUtils.trim(gdReq.getParam("V_YD_STK_COL_NO")) //야드적치열번호
						,slabUtils.trim(gdReq.getParam("V_YD_AIM_RT_GP" )) //야드목표행선구분
						,slabUtils.trim(gdReq.getParam("V_STL_NO"       )) //재료번호
						,slabUtils.trim(gdReq.getParam("V_HCR_GP"       )) //HCR구분
						,slabUtils.trim(gdReq.getParam("V_ORD_YEOJAE_GP")) //주문여재구분
						,slabUtils.trim(gdReq.getParam("V_DATE_FR"      )) //Slab정정작업기한일(시작일)
						,slabUtils.trim(gdReq.getParam("V_DATE_TO"      )) //Slab정정작업기한일(종료일)
						,slabUtils.trim(gdReq.getParam("V_SORT_GP"      )) //정렬구분
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
					};
			} else {
				trtNm = "보급Lot편성대상 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getStcmSupMgtTgt";

				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_YD_BAY_GP"    )) //야드동구분
						,slabUtils.trim(gdReq.getParam("V_YD_EQP_GP"    )) //야드설비구분
						,slabUtils.trim(gdReq.getParam("V_YD_STK_COL_NO")) //야드적치열번호
						,slabUtils.trim(gdReq.getParam("V_YD_AIM_RT_GP" )) //야드목표행선구분
						,slabUtils.trim(gdReq.getParam("V_STL_NO"       )) //재료번호
						,slabUtils.trim(gdReq.getParam("V_HCR_GP"       )) //HCR구분
						,slabUtils.trim(gdReq.getParam("V_ORD_YEOJAE_GP")) //주문여재구분
						,slabUtils.trim(gdReq.getParam("V_DATE_FR"      )) //Slab정정작업기한일(시작일)
						,slabUtils.trim(gdReq.getParam("V_DATE_TO"      )) //Slab정정작업기한일(종료일)
						,slabUtils.trim(gdReq.getParam("V_SORT_GP"      )) //정렬구분
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
					};
			}
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 2차절단보급관리 조회(신)
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getStcmSupMgtNew(GridData gdReq) throws DAOException {
		String methodNm = "2차절단보급관리[SlabYdJspDAO.getStcmSupMgtNew] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("SM".equals(gdReq.getParam("V_TRT_GP"))) {
				trtNm = "보급Lot편성완료 조회";
				if ("M".equals(gdReq.getParam("V_MTL_GP"))) {
					jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getStcmSupMgtLotMNew";
				}else{
					jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getStcmSupMgtLotSNew";
				}

				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_YD_BAY_GP"    )) //야드동구분
						,slabUtils.trim(gdReq.getParam("V_YD_EQP_GP"    )) //야드설비구분
						,slabUtils.trim(gdReq.getParam("V_YD_STK_COL_NO")) //야드적치열번호
						,slabUtils.trim(gdReq.getParam("V_YD_STK_BED_NO")) //야드적치베드번호
						,slabUtils.trim(gdReq.getParam("V_SLAB_WO_RT_CD")) //후판공장
						,slabUtils.trim(gdReq.getParam("V_ORDER_SEQ" 	)) //우선순위
						,slabUtils.trim(gdReq.getParam("V_ORDER_SEQ" 	)) //우선순위
						,slabUtils.trim(gdReq.getParam("V_ORDER_SEQ" 	)) //우선순위 
						,slabUtils.trim(gdReq.getParam("V_ORDER_SEQ" 	)) //우선순위   
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
					};
			} else {
				trtNm = "보급Lot편성대상 조회";
				if ("M".equals(gdReq.getParam("V_MTL_GP"))) {
					jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getStcmSupMgtTgtMNew";
				}else{
					jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getStcmSupMgtTgtSNew";
				}

				param = new Object[] {
					  	 slabUtils.trim(gdReq.getParam("V_YD_BAY_GP"    )) //야드동구분
						,slabUtils.trim(gdReq.getParam("V_YD_EQP_GP"    )) //야드설비구분
						,slabUtils.trim(gdReq.getParam("V_YD_STK_COL_NO")) //야드적치열번호
						,slabUtils.trim(gdReq.getParam("V_YD_STK_BED_NO")) //야드적치베드번호
						,slabUtils.trim(gdReq.getParam("V_SLAB_WO_RT_CD")) //후판공장
						,slabUtils.trim(gdReq.getParam("V_ORDER_SEQ" 	)) //우선순위
						,slabUtils.trim(gdReq.getParam("V_ORDER_SEQ" 	)) //우선순위
						,slabUtils.trim(gdReq.getParam("V_ORDER_SEQ" 	)) //우선순위 
						,slabUtils.trim(gdReq.getParam("V_ORDER_SEQ" 	)) //우선순위   
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
					};
			}
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	public JDTORecordSet getStcmSupMgtNewpp(GridData gdReq) throws DAOException {
		String methodNm = "2차절단보급관리[SlabYdJspDAO.getStcmSupMgtNewpp] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;
			
			trtNm = "보급Lot편성대상 조회";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getStcmSupMgtTgtMNewpp";
			
			param = new Object[] {
				  	slabUtils.trim(gdReq.getParam("V_STL_LIST"		)) //재료리스트
				};
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 2차절단보급관리 등록
	 *
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int insStcmSupMgt(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "2차절단보급관리[SlabYdJspDAO.insStcmSupMgt] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("PrepMtl".equals(trtGp)) {
				trtNm = "준비재료(TB_YD_PREPMTL) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.insStcmSupMgtPrepMtl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_PREP_SCH_ID")) //야드준비스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP" )) //야드적치열구분
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt + " 건", "DB");

			return trtCnt;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}


	/***************************************************************************
	 * 보급Lot관리 (장입/스카핑/2차절단)
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 보급Lot관리 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getSupLotMgt(GridData gdReq) throws DAOException {
		String methodNm = "보급Lot관리[SlabYdJspDAO.getSupLotMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("SM".equals(gdReq.getParam("V_TRT_GP"))|| "DM".equals(gdReq.getParam("V_TRT_GP"))) {
				trtNm = "보급Lot재료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getSupLotMgtMtl";

				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_YD_PREP_SCH_ID")) //야드준비스케쥴ID
					};
			} else {
				trtNm = "보급Lot 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getSupLotMgt";

				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_YD_GP"       )) //야드구분
						,slabUtils.trim(gdReq.getParam("V_YD_SCH_CD"   )) //야드스케쥴코드
						,slabUtils.trim(gdReq.getParam("V_YD_AIM_RT_GP")) //야드목표행선구분
						,slabUtils.trim(gdReq.getParam("viewRows"      )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"      )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"      )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"      )) //viewPage
					};
			}
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}


	/***************************************************************************
	 * 스카핑/2차절단보급Lot
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 스카핑/2차절단보급Lot 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getSfStcmSupLot(GridData gdReq) throws DAOException {
		String methodNm = "스카핑/2차절단보급Lot[SlabYdJspDAO.getSfStcmSupLot] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("SM".equals(gdReq.getParam("V_TRT_GP"))|| "DM".equals(gdReq.getParam("V_TRT_GP"))) {
				trtNm = "보급Lot재료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getSfStcmSupLotMtl";

				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_YD_PREP_SCH_ID")) //야드준비스케쥴ID
					};
			} else {
				trtNm = "보급Lot 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getSfStcmSupLot";

				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_YD_SCH_CD"   )) //야드스케쥴코드
						,slabUtils.trim(gdReq.getParam("V_YD_AIM_RT_GP")) //야드목표행선구분
						,slabUtils.trim(gdReq.getParam("viewRows"      )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"      )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"      )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"      )) //viewPage
					};
			}
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}


	/***************************************************************************
	 * 대차스케줄관리
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 대차스케줄관리
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getTcarSchMgt(GridData gdReq) throws DAOException {
		String methodNm = "대차스케줄관리[SlabYdJspDAO.getTcarSchMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("SM".equals(gdReq.getParam("V_TRT_GP"))) {
				trtNm = "작업예약재료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getTcarSchMgtWM";

				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("SW".equals(gdReq.getParam("V_TRT_GP")) || "PM".equals(gdReq.getParam("V_TRT_GP")) || "WD".equals(gdReq.getParam("V_TRT_GP"))) {
				//작업예약정보조회 or 우선순위변경 or 작업예약삭제 시
				trtNm = "작업예약정보 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getTcarSchMgtWB";

				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_YD_WRK_PLAN_TCAR")) //야드작업계획대차
					};
			} else {
				//대차정보조회 or 대차초기화 or 대차상태설정 시
				trtNm = "대차정보 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getTcarSchMgtTC";

				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_YD_EQP_ID")) //야드설비ID(대차)
					};
			}
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 대차스케줄기준 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getTcarSchRule(GridData gdReq) throws DAOException {
		String methodNm = "대차스케줄기준[SlabYdJspDAO.getTcarSchRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("ER".equals(gdReq.getParam("V_TRT_GP"))) {
				trtNm = "설비기준 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getTcarSchRuleER";

				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_YD_EQP_ID")) //야드설비ID(대차)
					};
			} else {
				trtNm = "검색기준 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getTcarSchRuleSR";

				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_YD_EQP_ID")) //야드설비ID(대차)
					};
			}
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 대차스케줄기준 수정
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updTcarSchRule(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "대차스케줄기준[SlabYdJspDAO.updTcarSchRule] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("ER".equals(trtGp)) {
				trtNm = "설비(TB_YD_EQP) 대차스케줄 설비기준 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updTcarSchRuleER";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"            )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_AUTO_TCAR_SCH_YN"    )) //자동대차스케줄여부
						,slabUtils.trim(jrParam.getFieldString("V_AUTO_TCAR_SCH_SH_MIN")) //자동대차스케줄매수최소
						,slabUtils.trim(jrParam.getFieldString("V_AUTO_TCAR_SCH_SH_MAX")) //자동대차스케줄매수최대
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"           )) //야드설비ID(대차)
					};
			} else if ("SR".equals(trtGp)) {
				trtNm = "야드기준(TB_YD_RULE) 대차스케줄 검색기준 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updTcarSchRuleSR";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID(대차)
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";
			
			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt, "DB");

			return trtCnt;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 대차스케줄기준 Batch 등록
	 *      
	 *      @param String trtGp
	 *      @param String[][] param
	 *      @param String logId
	 *      @param String methodNm
	 *      @return int
	 *      @throws DAOException
	*/
	public int insTcarSchRule(String trtGp, String[][] param, String logId, String mthdNm) throws DAOException {
		String methodNm = "대차스케줄기준[SlabYdJspDAO.insTcarSchRule] < " + mthdNm;
		String trtNm = "";

		try {
			String jspeed_query_id = "";

			if ("SR".equals(trtGp)) {
				trtNm = "야드기준(TB_YD_RULE) 대차스케줄 검색기준 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.insTcarSchRuleSR";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			slabUtils.printParam(logId, trtNm, param);
			
			int[] trtRst = trtProcess(jspeed_query_id, param);

			return trtRst.length;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/***************************************************************************
	 * 저장위치별정보조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 저장위치별정보조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getStrLocInfo(GridData gdReq) throws DAOException {
		String methodNm = "저장위치별정보조회[SlabYdJspDAO.getStrLocInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			String ydGp = slabUtils.trim(gdReq.getParam("V_YD_GP")); //야드구분

			if ("A".equals(ydGp) || "M".equals(ydGp)) {   // 항만야드 기능적용 보완 : 2015.12.15 by LeeJY
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getStrLocInfoA";

				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(gdReq.getParam("V_YD_STK_BED_NO")) //야드적치Bed번호
						,slabUtils.trim(gdReq.getParam("V_MC_SCARF_YN"  )) //M/C스카핑여부(Hand스카핑대기)
						,slabUtils.trim(gdReq.getParam("V_BANK_WORK_YN" )) // 보온뱅크재여부
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
					};
			} else if ("D".equals(ydGp)) {
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getStrLocInfoD";

				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(gdReq.getParam("V_YD_STK_BED_NO")) //야드적치Bed번호
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
					};
			} else {
				return null;
			}
			
			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/***************************************************************************
	 * 저장위치별현황
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 저장위치별현황 조회 (JSP에서 바로 조회)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getStrLocStat(JDTORecord jrParam) throws DAOException {
		String methodNm = "저장위치별현황조회[SlabYdJspDAO.getStrLocStat]";

		try {
			String jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getStrLocStat";

			Object[] param = new Object[] {
					slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
				};

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(slabUtils.getLogId(), methodNm, e));
		}
	}


	/***************************************************************************
	 * 이적작업예약등록
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 이적작업예약등록 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMvStkWrkBookReg(GridData gdReq) throws DAOException {
		String methodNm = "이적작업예약등록[SlabYdJspDAO.getMvStkWrkBookReg] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("SB".equals(gdReq.getParam("V_TRT_GP"))) {
				trtNm = "Bed정보 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getMvStkWrkBookRegBed";

				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(gdReq.getParam("V_YD_STK_BED_NO")) //야드적치Bed번호
					};
			} else {
				trtNm = "재료정보 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getMvStkWrkBookRegMvMtl_Screen";

				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_STL_NOS"      )) //재료번호들
						,slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")) //야드적치열구분
					};
			}
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 이적작업예약등록 (처리용)
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMvStkWrkBookReg(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "이적작업예약등록[SlabYdJspDAO.getMvStkWrkBookReg] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("MvMtl".equals(trtGp)) {
				trtNm = "이적재료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getMvStkWrkBookRegMvMtl";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_STL_NOS"      )) //재료번호들
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
					};
			} else if ("DmMtl".equals(trtGp)) {
				trtNm = "Dummy이적재료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getMvStkWrkBookRegDmMtl";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_LYR_NO")) //야드적치단번호
					};
			} else if ("MvMtl2".equals(trtGp)) {
				trtNm = "이적재료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getMvStkWrkBookRegMvMtl2";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_STL_NOS"      )) //재료번호들
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분  
						,slabUtils.trim(jrParam.getFieldString("V_YD_TO_LOC_GUIDE")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_TO_LOC_GUIDE")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_TO_LOC_GUIDE")) //야드적치열구분
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			JDTORecordSet jsRst = getRecordSet(jspeed_query_id, param);
				
			slabUtils.printLog(logId, trtNm + jsRst.size(), "DB");
			
			return jsRst;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 이적작업예약등록 수정
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int insMvStkWrkBookReg(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "이적작업예약등록[SlabYdJspDAO.insMvStkWrkBookReg] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("ER".equals(trtGp)) {
				trtNm = "설비(TB_YD_EQP) 대차스케줄 설비기준 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updTcarSchRuleER";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"            )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_AUTO_TCAR_SCH_YN"    )) //자동대차스케줄여부
						,slabUtils.trim(jrParam.getFieldString("V_AUTO_TCAR_SCH_SH_MIN")) //자동대차스케줄매수최소
						,slabUtils.trim(jrParam.getFieldString("V_AUTO_TCAR_SCH_SH_MAX")) //자동대차스케줄매수최대
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"           )) //야드설비ID(대차)
					};
			} else if ("SR".equals(trtGp)) {
				trtNm = "야드기준(TB_YD_RULE) 대차스케줄 검색기준 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updTcarSchRuleSR";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID(대차)
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";
			
			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt, "DB");

			return trtCnt;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/***************************************************************************
	 * 작업재료List
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 작업재료List
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getWrkMtlList(GridData gdReq) throws DAOException {
		String methodNm = "작업재료List[SlabYdJspDAO.getWrkMtlList] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			Object[] param = null;
			
			String jspeed_query_id = "";
			
			if("D".equals(slabUtils.trim(gdReq.getParam("V_YD_GP")))){//후판슬라브
				
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getWrkMtlList_01";
				
				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_DATE_FR"      )) //시작일시(야드권하완료일시)
						,slabUtils.trim(gdReq.getParam("V_DATE_TO"      )) //종료일시(야드권하완료일시)
						,slabUtils.trim(gdReq.getParam("V_DATE_FR"      )) //시작일시(야드권하완료일시)
						,slabUtils.trim(gdReq.getParam("V_YD_GP"        )) //야드구분
						,slabUtils.trim(gdReq.getParam("V_SLAB_WO_RT_CD")) //창고구분
						,slabUtils.trim(gdReq.getParam("V_DATE_FR"      )) //시작일시(야드권하완료일시)
						,slabUtils.trim(gdReq.getParam("V_DATE_TO"      )) //종료일시(야드권하완료일시)
						,slabUtils.trim(gdReq.getParam("V_YD_EQP_ID"    )) //야드설비ID(크레인)
						,slabUtils.trim(gdReq.getParam("V_YD_AID_WRK_YN")) //야드보조작업여부
						,slabUtils.trim(gdReq.getParam("V_YD_WRK_GP"    )) //야드작업구분
						,slabUtils.trim(gdReq.getParam("V_YD_WRK_GP"    )) //야드작업구분
						,slabUtils.trim(gdReq.getParam("V_YD_WRK_GP"    )) //야드작업구분
						,slabUtils.trim(gdReq.getParam("V_YD_WRK_GP"    )) //야드작업구분
						,slabUtils.trim(gdReq.getParam("V_YD_WRK_GP"    )) //야드작업구분
						,slabUtils.trim(gdReq.getParam("V_YD_WRK_GP"    )) //야드작업구분
						,slabUtils.trim(gdReq.getParam("V_YD_WRK_GP"    )) //야드작업구분
						,slabUtils.trim(gdReq.getParam("V_DATE_FR"      )) //시작일시(야드권하완료일시)
						,slabUtils.trim(gdReq.getParam("V_DATE_FR"		)) //시작일시(야드권하완료일시)
						,slabUtils.trim(gdReq.getParam("V_DATE_TO"      )) //종료일시(야드권하완료일시)
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
						,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
						,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
						
					};
				
			}else{//연주슬라브
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getWrkMtlList";
				
				param = new Object[] {
					 slabUtils.trim(gdReq.getParam("V_YD_GP"        )) //야드구분
					,slabUtils.trim(gdReq.getParam("V_DATE_FR"      )) //시작일시(야드권하완료일시)
					,slabUtils.trim(gdReq.getParam("V_DATE_TO"      )) //종료일시(야드권하완료일시)
					,slabUtils.trim(gdReq.getParam("V_YD_EQP_ID"    )) //야드설비ID(크레인)
					,slabUtils.trim(gdReq.getParam("V_YD_AID_WRK_YN")) //야드보조작업여부
					,slabUtils.trim(gdReq.getParam("V_YD_WRK_GP"    )) //야드작업구분
					,slabUtils.trim(gdReq.getParam("V_YD_WRK_GP"    )) //야드작업구분
					,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
					,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
					,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
					,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
				};
			}
			
			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/***************************************************************************
	 * 화면공통
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 화면공통 조회
	 *      
	 *      @param String trtGp
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getComm(String trtGp, GridData gdReq) throws DAOException {
		String methodNm = "화면공통[SlabYdJspDAO.getComm] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("SupRuleMax".equals(trtGp)) {
				trtNm = "보급설비제한기준 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCommSupRuleMax";

				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_SUP_EQP_GP")) //보급설비구분(SF,ST,HC,PA,PB)
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 화면공통 조회 (처리용)
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getComm(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "화면공통[SlabYdJspDAO.getComm] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("WbCrnSch".equals(trtGp)) {
				trtNm = "작업예약 크레인스케줄조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCommWbCrnSch";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			JDTORecordSet jsRst = getRecordSet(jspeed_query_id, param);
				
			slabUtils.printLog(logId, trtNm + jsRst.size(), "DB");
			
			return jsRst;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 화면공통 수정
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updComm(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "화면공통[SlabYdJspDAO.updComm] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;
			
			if ("ChgSupRule".equals(trtGp)) {
				trtNm = "야드기준(TB_YD_RULE) 장입보급기준 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCommChgSupRule";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_TI_SUP_YN"    )) //TakeIn공Bed보급요구여부
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
					};
			} else if ("ChgCrnRule".equals(trtGp)) {
				trtNm = "야드기준(TB_YD_RULE) 크레인보급기준 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCommChgCrnRule";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WRK_CRN"   )) //작업크레인
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
					};
			} else if ("TcarSchRuleDel".equals(trtGp)) {
				trtNm = "야드기준(TB_YD_RULE) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCommTcarSchRuleDel";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드작업계획대차
					};
			} else if ("PrepMtlRcvr".equals(trtGp)) {
				trtNm = "준비재료(TB_YD_PREPMTL) 복원";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCommPrepMtlRcvr";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"   )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("PrepSchRcvr".equals(trtGp)) {
				trtNm = "준비스케줄(TB_YD_PREPSCH) 복원";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCommPrepSchRcvr";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"   )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("CarSchWbDel".equals(trtGp)) {
				trtNm = "차량스케줄(TB_YD_CARSCH) 작업예약ID 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCommCarSchWbDel";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"   )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("TcarSchWbDel".equals(trtGp)) {
				trtNm = "대차스케줄(TB_YD_TCARSCH) 작업예약ID 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCommTcarSchWbDel";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"   )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("ChgSeqNo".equals(trtGp)) {
				trtNm = "저장품(TB_YD_TCARSCH) 순번 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCommStockSeqNo";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_SEQ_NO"   )) //순번
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER" )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO")) //재료번호
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";
			
			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt, "DB");

			return trtCnt;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 화면공통 Batch 처리
	 *      
	 *      @param String trtGp
	 *      @param String[][] param
	 *      @param String logId
	 *      @param String methodNm
	 *      @return int
	 *      @throws DAOException
	*/
	public int updComm(String trtGp, String[][] param, String logId, String mthdNm) throws DAOException {
		String methodNm = "화면공통[SlabYdJspDAO.updComm] < " + mthdNm;
		String trtNm = "";

		try {
			String jspeed_query_id = "";

			if ("CarSchWbDel".equals(trtGp)) {
				trtNm = "차량스케줄(TB_YD_CARSCH) 작업예약ID 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCommCarSchWbDel";
			} else if ("TcarSchWbDel".equals(trtGp)) {
				trtNm = "대차스케줄(TB_YD_TCARSCH) 작업예약ID 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCommTcarSchWbDel";
			} else if ("TcarSchRuleIns".equals(trtGp)) {
				trtNm = "야드기준(TB_YD_RULE) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCommTcarSchRuleIns";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			slabUtils.printParam(logId, trtNm, param);
			
			int[] trtRst = trtProcess(jspeed_query_id, param);

			return trtRst.length;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	/***************************************************************************
	 * 인터페이스Test
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 인터페이스Test 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getIfTest(GridData gdReq) throws DAOException {
		String methodNm = "인터페이스Test[SlabYdJspDAO.getIfTest] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("SI".equals(gdReq.getParam("V_TRT_GP"))) {
				trtNm = "I/F List 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getIfTestList";
				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_SYS_GP"      )) //시스템구분
						,slabUtils.trim(gdReq.getParam("V_SYS_GP"      )) //시스템구분
						,slabUtils.trim(gdReq.getParam("V_OPRN_SYS_GP" )) //운영시스템구분
						,slabUtils.trim(gdReq.getParam("V_IF_MTH_GP"   )) //IF방법구분
						,slabUtils.trim(gdReq.getParam("V_IF_SNDRCV_GP")) //IF송수신구분
						,slabUtils.trim(gdReq.getParam("V_IF_ID"       )) //IFID
						,slabUtils.trim(gdReq.getParam("V_IF_ID"       )) //IFID
					};
			} else {
				trtNm = "I/F Layout 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getIfTestLayout";
				param = new Object[] {
						slabUtils.trim(gdReq.getParam("IF_ID")) //IFID
					};
			}
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 인터페이스Test 전송Data 저장
	 *
	 *      @param String logId
	 *      @param String methodNm
	 *      @param Object[][] param
	 *      @return int
	 *      @throws DAOException
	*/
	public int updIfTestData(String[][] param, String logId, String mthdNm) throws DAOException {
		String methodNm = "인터페이스Test[SlabYdJspDAO.updIfTestData] < " + mthdNm;
		String trtNm = "인터페이스Layout(TB_YD_Z_IFLAYOUT) 수정 : ";

		try {
			String jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updIfTestData";

			slabUtils.printParam(logId, trtNm, param);

			int[] trtRst = trtProcess(jspeed_query_id, param);

			return trtRst.length;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/***************************************************************************
	 * 인터페이스관리
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 인터페이스관리 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getIfMgt(GridData gdReq) throws DAOException {
		String methodNm = "인터페이스관리[SlabYdJspDAO.getIfMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "I/F List 조회 : ";

		try {
			String jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getIfMgt";
			
			Object[] param = new Object[] {
					 slabUtils.trim(gdReq.getParam("V_SYS_GP"      )) //시스템구분
					,slabUtils.trim(gdReq.getParam("V_SYS_GP"      )) //시스템구분
					,slabUtils.trim(gdReq.getParam("V_OPRN_SYS_GP" )) //운영시스템구분
					,slabUtils.trim(gdReq.getParam("V_IF_MTH_GP"   )) //IF방법구분
					,slabUtils.trim(gdReq.getParam("V_IF_SNDRCV_GP")) //IF송수신구분
					,slabUtils.trim(gdReq.getParam("V_IF_ID"       )) //IFID
					,slabUtils.trim(gdReq.getParam("V_IF_ID"       )) //IFID
				};

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스관리 IFID조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public boolean getIfMgtId(String ifId, String logId, String mthdNm) throws DAOException {
		String methodNm = "인터페이스관리[SlabYdJspDAO.getIfMgtId] < " + mthdNm;
		String trtNm = "I/F ID 조회 : ";

		try {
			String jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getIfMgtId";

			Object[] param = new Object[] { ifId }; //IFID

			JDTORecordSet jsChk = getRecordSet(jspeed_query_id, param);
			
			return (jsChk.size() > 0);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스관리 등록
	 *
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int upsIfMgt(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "인터페이스관리[SlabYdJspDAO.upsIfMgt] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("MstUps".equals(trtGp)) {
				trtNm = "인터페이스(TB_YD_Z_IF) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.upsIfMgtMst";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_IF_ID"      )) //I/F ID
						,slabUtils.trim(jrParam.getFieldString("V_IF_NM"      )) //I/F 명
						,slabUtils.trim(jrParam.getFieldString("V_PGM_NM1"    )) //수신Class
						,slabUtils.trim(jrParam.getFieldString("V_PGM_NM2"    )) //수신Method
						,slabUtils.trim(jrParam.getFieldString("V_PGM_NM3"    )) //송신Queue
						,slabUtils.trim(jrParam.getFieldString("V_OPRN_SYS_GP")) //야드구분
						,slabUtils.trim(jrParam.getFieldString("V_REMARKS"    )) //비고
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"   )) //수정자
					};
			} else if ("DtlUps".equals(trtGp)) {
				trtNm = "인터페이스Layout(TB_YD_Z_IFLAYOUT) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.upsIfMgtDtl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_IF_ID"   )) //I/F ID
						,slabUtils.trim(jrParam.getFieldString("V_ITM_VAL1")) //기존Class
						,slabUtils.trim(jrParam.getFieldString("V_ITM_VAL2")) //기존Method
						,slabUtils.trim(jrParam.getFieldString("V_ITM_VAL3")) //신규Class
						,slabUtils.trim(jrParam.getFieldString("V_ITM_VAL4")) //신규Method
					};
			} else if ("MstDel".equals(trtGp)) {
				trtNm = "인터페이스(TB_YD_Z_IF) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.delIfMgtMst";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_IF_ID")) //I/F ID
					};
			} else if ("DtlDel".equals(trtGp)) {
				trtNm = "인터페이스Layout(TB_YD_Z_IFLAYOUT) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.delIfMgtDtl";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_IF_ID")) //I/F ID
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt + " 건", "DB");

			return trtCnt;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}


	/***************************************************************************
	 * Code조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : Slab야드 코드 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getSlabYdCode(GridData gdReq) throws DAOException {
		String methodNm = "코드조회[SlabYdJspDAO.getSlabYdCode] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;
			
			String itmGp = slabUtils.trim(gdReq.getParam("V_ITM_GP")); //코드항목구분

			if ("YD_BAY_GP".equals(itmGp)) {
				trtNm = "동구분";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCodeYdBayGp";
				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_YD_GP")) //야드구분
					};
			} else if ("YD_EQP_GP".equals(itmGp)) {
				trtNm = "설비구분";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCodeYdEqpGp";
				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")) //야드구분 + 동구분
					};
			} else if ("YD_STK_COL_NO".equals(itmGp)) {
				trtNm = "적치열번호";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCodeYdStkColNo";
				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")) //야드구분 + 동구분 + Span구분
					};
			} else if ("YD_STK_BED_NO".equals(itmGp)) {
				trtNm = "적치Bed번호";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCodeYdStkBedNo";
				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")) //야드적치열구분
					};
			} else if ("YD_STK_ABLE_BED".equals(itmGp)) {
				trtNm = "적치가능Bed";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCodeStkAbleBed";
				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_ITM_ID"       )) //항목ID
						,slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")) //야드적치열구분
					};
			} else if ("YD_EQP_ID_CR".equals(itmGp)) {
				trtNm = "크레인설비ID";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCodeYdEqpIdCrn";
				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_YD_GP"    )) //야드구분
						,slabUtils.trim(gdReq.getParam("V_YD_BAY_GP")) //동구분
					};
			} else if ("YD_EQP_ID_TC".equals(itmGp)) {
				trtNm = "대차설비ID";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCodeYdEqpIdTc";
				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_YD_GP")) //야드구분
					};
			} else if ("YD_SCH_CD".equals(itmGp)) {
				trtNm = "스케줄코드";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCodeYdSchCd";
				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_YD_GP"    )) //야드구분
					   ,slabUtils.trim(gdReq.getParam("V_YD_BAY_GP")) //동구분
					};
			} else { //공통코드조회
				trtNm = "[" + itmGp + "]코드";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCodeCmCodes";
				param = new Object[] {
						itmGp //코드영문ID
					   ,slabUtils.trim(gdReq.getParam("V_CD_CAT_ID")) //코드카테고리ID
					};
			}
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 화면의 메뉴ID로 야드구분을 조회
	 *
	 *      @param GridData gdReq
	 *      @return String
	 *      @throws DAOException
	*/
	public String getMenuYdGp(String menu_id_path) throws DAOException {
		try {
			String jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getMenuYdGp";
			Object[] param = new Object[] { menu_id_path }; //화면의 Menu ID Path
			String ydGp = "A"; //Default C연주Slab야드

			JDTORecordSet jsRtn = getRecordSet(jspeed_query_id, param);
			if (jsRtn != null && jsRtn.size() > 0) {
				JDTORecord rcRet = jsRtn.getRecord(0);
				ydGp = slabUtils.nvl(rcRet.getFieldString("YD_GP"), "A");
			}

			return ydGp;
		} catch(Exception e) {
			return "A";
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 벤딩재 실적 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getBendingSlabHist(JDTORecord jrParam) throws DAOException {
		String methodNm = "벤딩재 실적 조회[SlabYdJspDAO.getBendingSlabHist] < " + jrParam.getResultMsg();
		String logId = slabUtils.getLogId();
		String trtNm = "";

		try {
			String jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getBendingSlabHist";
			Object[] param = null;

			param = new Object[] {
					slabUtils.trim(jrParam.getFieldString("V_FR_DD")) //시작일
				   ,slabUtils.trim(jrParam.getFieldString("V_TO_DD")) //종료일
				   ,slabUtils.trim(jrParam.getFieldString("V_DD_GP")) //조회기준
				};
			
			trtNm += " : ";

			JDTORecordSet jsRst = getRecordSet(jspeed_query_id, param);
				
			slabUtils.printLog(logId, trtNm + jsRst.size(), "DB");
			
			return jsRst;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	
	/***************************************************************************
	 * Sizing Slab 이송 재료 List
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : Sizing Slab 이송 재료 List
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getWrkMtlListFromPlate(GridData gdReq) throws DAOException {
		String methodNm = "Sizing Slab 이송 재료 List[SlabYdJspDAO.getWrkMtlListFromPlate] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			Object[] param = null;
			
			String jspeed_query_id = "";
			
				
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getWrkMtlListjlFromPlate";
			
			param = new Object[] {
					 slabUtils.trim(gdReq.getParam("V_DATE_FROM"      )) //시작일시(야드권하완료일시)
					,slabUtils.trim(gdReq.getParam("V_DATE_TO"      )) //종료일시(야드권하완료일시)
					,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
					,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
					,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
					,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
					
				};
				
			
			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	
	/**
	 *      [A] 오퍼레이션명 : 스카핑 슬라브 이송실적
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getScarfSlabMvList(GridData gdReq) throws DAOException {
		String methodNm = "스카핑 슬라브 이송실적[SlabYdJspDAO.getScarfSlabMvList] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			Object[] param = null;
			
			String jspeed_query_id = "";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getScarfSlabMvList";
			
			param = new Object[] {
					slabUtils.trim(gdReq.getParam("V_DT_FR"       )) //시작일시(야드권하완료일시)
					,slabUtils.trim(gdReq.getParam("V_DT_TO"      )) //종료일시(야드권하완료일시)
					,slabUtils.trim(gdReq.getParam("V_YD_MOVE_GP"   )) //선택구분
					,slabUtils.trim(gdReq.getParam("V_YD_MOVE_GP"   )) //선택구분
					,slabUtils.trim(gdReq.getParam("V_DT_FR"      )) //시작일시(야드권하완료일시)
					,slabUtils.trim(gdReq.getParam("V_DT_TO"      )) //종료일시(야드권하완료일시)
					,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
					,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
					,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
					,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
					
				};
			
			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 현재 적치수량,중량 조회(C1,C2)
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getCurCntWt(GridData gdReq) throws DAOException {
		String methodNm = "현재 적치수량,중량 조회(C1,C2)[SlabYdJspDAO.getCurCntWt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			Object[] param = null;
			
			String jspeed_query_id = "";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCurCntWt";
			
			param = new Object[] {
				/*	slabUtils.trim(gdReq.getParam("V_DT_FR"       )) //시작일시(야드권하완료일시)
					,slabUtils.trim(gdReq.getParam("V_DT_TO"      )) //종료일시(야드권하완료일시)
					,slabUtils.trim(gdReq.getParam("V_YD_MOVE_GP"   )) //선택구분
					,slabUtils.trim(gdReq.getParam("V_YD_MOVE_GP"   )) //선택구분
					,slabUtils.trim(gdReq.getParam("V_DT_FR"      )) //시작일시(야드권하완료일시)
					,slabUtils.trim(gdReq.getParam("V_DT_TO"      )) //종료일시(야드권하완료일시)
					,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
					,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
					,slabUtils.trim(gdReq.getParam("viewRows"       )) //viewRows
					,slabUtils.trim(gdReq.getParam("viewPage"       )) //viewPage
*/					
				};
			
			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 권하불가위치 조회
	 *		오원재(1524711)
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getYdDnWoLocNotAllowedInfo(GridData gdReq) throws DAOException {
		String methodNm = "권하불가위치 조회[SlabYdJspDAO.getYdDnWoLocNotAllowedInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

				trtNm = "권하불가위치 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getYdDnWoLocNotAllowedInfo";

				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_YD_DN_WO_LOC")) //저장위치로 조회한다.
					};

			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 권하불가재료 조회
	 *		오원재(1524711)
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getSlabDnNotAllowedInfo(GridData gdReq) throws DAOException {
		String methodNm = "권하불가재료 조회[SlabYdJspDAO.getSlabDnNotAllowedInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

				trtNm = "권하불가재료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getSlabDnNotAllowedInfo";

				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_SLAB_NO")), //재료번호로 조회한다.
						slabUtils.trim(gdReq.getParam("V_YD_DN_WO_LOC")) //저장위치로 조회한다.
					};

			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 이적작업예약등록 (처리용) 수행 전 중복재료 체크
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMvStkWrkBookRegDup(GridData gdReq) throws DAOException {
		String methodNm = "이적작업예약등록전중복체크[SlabYdJspDAO.getMvStkWrkBookRegDup] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

				trtNm = "이적재료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getMvStkWrkBookRegMvMtl2Dup";

				param = new Object[] {
						 slabUtils.trim(gdReq.getParam("V_STL_NOS"      )) //재료번호들
						//,slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")) //야드적치열구분  
						//,slabUtils.trim(gdReq.getParam("V_YD_TO_LOC_GUIDE")) //야드적치열구분
						//,slabUtils.trim(gdReq.getParam("V_YD_TO_LOC_GUIDE")) //야드적치열구분
						//,slabUtils.trim(gdReq.getParam("V_YD_TO_LOC_GUIDE")) //야드적치열구분
					};
			
			
			trtNm += " : ";

			JDTORecordSet jsRst = getRecordSet(jspeed_query_id, param);
				
			slabUtils.printLog(logId, trtNm + jsRst.size(), "DB");
			
			return jsRst;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 핸드스카핑보류재 조회
	 *		오원재(1524711)
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getHandScarfingHoldYn(GridData gdReq) throws DAOException {
		String methodNm = "핸드스카핑보류재 조회[SlabYdJspDAO.getHandScarfingHoldYn] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

				trtNm = "핸드스카핑보류재 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getHandScarfingHoldYn";

				param = new Object[] {
						slabUtils.trim(gdReq.getParam("V_SLAB_NO")) //재료번호로 조회한다.
					};

			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 팔렛트 자동 상차완료 처리 여부 변경
	 *		오원재(1524711)
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public int updPtAutoComplete(JDTORecord inRec) throws DAOException {
		String methodNm = "팔렛트 자동 상차완료 처리 여부 변경[SlabYdJspDAO.updPtAutoComplete] < ";
		String logId = slabUtils.getLogId();
		int intRtnVal = 0;

		try {
				inRec.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updPtAutoComplete");
				intRtnVal = trtProcess(inRec);

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return intRtnVal;
	}

	/***************************************************************************
	 * 기준Heat번호 조회 (별적/입회재 관리화면)
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 기준Heat번호 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getDefaultHeatNo(GridData gdReq) throws DAOException {
		String methodNm = "별적/입회재 관리화면 기준Heat번호 조회[SlabYdJspDAO.getDefaultHeatNo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";
		
		try {
			String jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getDefaultHeatNo";
			Object[] param = null;

			trtNm = "기준Heat번호 조회";

			param = new Object[] {
					 slabUtils.trim(gdReq.getParam("V_INSD_PLNT_GP"   )) //내부공장구분
					,slabUtils.trim(gdReq.getParam("V_PLNT_GP")) //공장구분
					,slabUtils.trim(gdReq.getParam("V_MC_CD"      )) //연주기구분
				};
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
			
		} catch(Exception e) {
			
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
			
		}
	}
	
	
	/***************************************************************************
	 * 기준Heat번호로 앞뒤 Heat번호 조회 (별적/입회재 관리화면 Heat번호 콤보박스용)
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 기준Heat번호로 앞뒤 Heat번호 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getDefaultHeatNoForCombo(GridData gdReq) throws DAOException {
		String methodNm = "별적/입회재 관리화면 기준Heat번호로 앞뒤 Heat번호 조회[SlabYdJspDAO.getDefaultHeatNoForCombo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";
		
		try {
			String jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getDefaultHeatNoForCombo";
			Object[] param = null;

			trtNm = "기준Heat번호로 앞뒤 Heat번호 조회";

			param = new Object[] {
					 slabUtils.trim(gdReq.getParam("V_INSD_PLNT_GP"   )) //내부공장구분
					,slabUtils.trim(gdReq.getParam("V_HEAT_NO"      )) //히트번호
					,slabUtils.trim(gdReq.getParam("V_PLNT_GP")) //공장구분
					,slabUtils.trim(gdReq.getParam("V_MC_CD"      )) //연주기구분
					,slabUtils.trim(gdReq.getParam("V_CT_WO_CS_ST_DT"      )) //
					,slabUtils.trim(gdReq.getParam("V_PLNT_GP")) //공장구분
					,slabUtils.trim(gdReq.getParam("V_MC_CD"      )) //연주기구분
					,slabUtils.trim(gdReq.getParam("V_CT_WO_CS_ST_DT"      )) //
				};
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
			
		} catch(Exception e) {
			
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
			
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 저장위치별현황 조회 (JSP에서 바로 조회)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getSeparateStackSeq(String jspeed_query_id, Object[] sObj) throws DAOException { 
		String methodNm = "별적/입회재 관리 시퀀스 Nextval 조회";
		JDTORecordSet outRecordSet = null;
		DBAssistantDAO assistantDAO = new DBAssistantDAO();

		try {
			//지정 된 쿼리 아이디와 조건 파라미터를 넘겨서 Heat사양을 저장한다.
			outRecordSet = assistantDAO.getRecordSet(jspeed_query_id,	//쿼리 아이디
																sObj	//조건 파라미터
						);

			return outRecordSet;
			
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(slabUtils.getLogId(), methodNm, e));
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : 별적/입회재 요청등록
	 * @author  1524711
	 * @date 	2025.02.18
	 */
	public int insSeparateStackReq(String jspeed_query_id, Object[] objs) {
		
		String methodNm = "별적/입회재 요청등록";
		try {
			
			DBAssistantDAO assistantDAO = new DBAssistantDAO();
			return assistantDAO.trtProcess(jspeed_query_id, objs);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(slabUtils.makeErrorLog(slabUtils.getLogId(), methodNm, e));
		} finally {
		
		}
	}
	
	/***************************************************************************
	 * 별적요청 조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 별적요청 조회
	 *
	 *      @param GridData gdReq
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getSeparateReqList(GridData gdReq) throws DAOException {
		String methodNm = "별적요청 조회[SlabYdJspDAO.getSeparateReqList] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String trtNm = "";
		
		try {
			String jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getSeparateReqList";
			Object[] param = null;

			trtNm = "별적요청 조회";

			param = new Object[] {
					CmnUtil.nvl(gdReq.getParam("V_HEAT_NO"), "")
				   ,CmnUtil.nvl(gdReq.getParam("V_SEPARATE_PROG_GP"), "")
				};
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
			
		} catch(Exception e) {
			
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
			
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : 입회검사 요청/확정/완료처리
	 * @author  1524711
	 * @date 	2025.02.19
	 */
	public int updInspectionState(String jspeed_query_id, Object[] objs) {
		/* V_SEPARATE_PROG_GP
		-- 1 : 별적요청
		-- 2 : 별적완료
		-- 3 : 입회검사요청
		-- 4 : 입회검사확정
		-- 5 : 입회검사완료
		-- 0 : 별적요청취소
		*/
		String methodNm = "입회검사 요청/확정/완료처리";
		try {
			
			DBAssistantDAO assistantDAO = new DBAssistantDAO();
			return assistantDAO.trtProcess(jspeed_query_id, objs);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(slabUtils.makeErrorLog(slabUtils.getLogId(), methodNm, e));
		} finally {
		
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : 별적/입회재 관련 메일발송 내용조회
	 * 
	 */
	public JDTORecordSet getEmailContentsForSeparateStack(GridData inRecord) throws DAOException {
		String methodNm = "별적/입회재 관련 메일발송 내용조회";
		JDTORecordSet outRecordSet = null;
		try {
			DBAssistantDAO assistantDAO = new DBAssistantDAO();
			ArrayList arrayList = new ArrayList();
			
			
			
			if(inRecord.getParam("V_CANCEL_YN").equals("N")) {
				
				arrayList.add(inRecord.getParam("V_SEQ"));
				
				if(inRecord.getParam("V_SEPARATE_PROG_GP").equals("1")) {
					outRecordSet = assistantDAO.getRecordSet("com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getEmailContentsForSeparateStack", arrayList.toArray());
				}
				
				if(inRecord.getParam("V_SEPARATE_PROG_GP").equals("2")) {		
					outRecordSet = assistantDAO.getRecordSet("com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getEmailContentsForSeparateStackDone", arrayList.toArray());
				}
				
				if(inRecord.getParam("V_SEPARATE_PROG_GP").equals("3")) {	
					outRecordSet = assistantDAO.getRecordSet("com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getEmailContentsForReqInspection", arrayList.toArray());
				}
				
				if(inRecord.getParam("V_SEPARATE_PROG_GP").equals("4")) {		
					outRecordSet = assistantDAO.getRecordSet("com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getEmailContentsForPlanInspection", arrayList.toArray());
				}
				
				if(inRecord.getParam("V_SEPARATE_PROG_GP").equals("5")) {		
					outRecordSet = assistantDAO.getRecordSet("com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getEmailContentsForDoneInspection", arrayList.toArray());
				}
				
			} else {
				arrayList.add(inRecord.getParam("V_SEPARATE_PROG_GP"));	
				arrayList.add(inRecord.getParam("V_SEQ"));
				outRecordSet = assistantDAO.getRecordSet("com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getEmailContentsForSeparateStackCancel", arrayList.toArray());
				
			}
					
			
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + ":" + e.getMessage(), e);
		}
		
		return outRecordSet;		
    }
	
	/**
	 * [A] 오퍼레이션명 : 별적/입회재 관련 메일발송 수신자 조회
	 * 
	 */
	public JDTORecordSet getEmailReceiverForSeparateStack(GridData inRecord) throws DAOException {
		String methodNm = "별적/입회재 관련 메일발송 수신자 조회";
		JDTORecordSet outRecordSet = null;
		try {
			//GRID파리미터를 JDTORecord로 변환
			//JDTORecord paramRec = TsCommUtil.gridParamToJdtoRcd(inRecord);
			//DBAssistantDAO assistantDAO = new DBAssistantDAO();
			
			DBAssistantDAO assistantDAO = new DBAssistantDAO();
			ArrayList arrayList = new ArrayList();
			arrayList.add(inRecord.getParam("V_SEPARATE_PROG_GP"));
			arrayList.add(inRecord.getParam("V_RECEIVER"));
			outRecordSet = assistantDAO.getRecordSet("com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getEmailReceiverForSeparateStack", arrayList.toArray());
			return outRecordSet;
			
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage());
		} 
    }

}
