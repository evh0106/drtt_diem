/**
 * @(#)SlabYdSchDAO
 *
 * @version          V1.00
 * @author           허철호
 * @date             2012/11/22
 * 
 * @description      Slab야드 Schedule DAO
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2012/11/22   허철호      허철호      최초 등록
 */
package com.inisteel.cim.yd.slabyd.dao;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdSlabUtils;

/**
 * [A] 클래스명 : Slab야드 Schedule DAO
 *
 */

public class SlabYdSchDAO extends DBAssistantDAO {

	private YdSlabUtils slabUtils = new YdSlabUtils();

	/***************************************************************************
	 * Crane Schedule
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : Slab야드 크레인스케줄 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getYDYDJ400(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인스케줄[SlabYdSchDAO.getYDYDJ400] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("WbId".equals(trtGp)) {
				trtNm = "작업예약-작업예약ID 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400WbId";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("WbSchCd".equals(trtGp)) {
				trtNm = "작업예약-스케줄코드 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400WbSchCd";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD")) //야드스케쥴코드
					};
			} else if ("WbEqpId".equals(trtGp)) {
				trtNm = "작업예약-설비ID 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400WbEqpId";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID
					};
			} else if ("Stat".equals(trtGp)) {
				trtNm = "상태정보 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400Stat";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("ToLocGuide".equals(trtGp)) {
				trtNm = "To위치점검-Guide 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400ToLocGuide";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("ToLocGuideNew".equals(trtGp)) {
				trtNm = "신규To위치Guide 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400ToLocGuideNew";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"  )) //야드설비ID
					};
			} else if ("ToLocCar".equals(trtGp)) {
				trtNm = "To위치점검-차량상차 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400ToLocCar";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID 
					};
			} else if ("ToLocExt".equals(trtGp)) {
				trtNm = "To위치점검-불출 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400ToLocExt";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("ToLocEtc".equals(trtGp)) {
				trtNm = "To위치점검-기타 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400ToLocEtc";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("CrnSpec".equals(trtGp)) {
				trtNm = "크레인사양 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400CrnSpec";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID
					};
			} else if ("BedSpec".equals(trtGp)) {
				trtNm = "Bed사양 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400BedSpec";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("CrnMtl".equals(trtGp)) {
				trtNm = "크레인작업재료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400CrnMtl";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("CurrLoc".equals(trtGp)) {
				trtNm = "현재위치 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400CurrLoc";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("CrnSchId".equals(trtGp)) {
				trtNm = "크레인스케쥴ID 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400CrnSchId";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_CRN_SCH_CNT")) //크레인스케쥴수
					};
			} else if ("DnLocWX".equals(trtGp)) {
				trtNm = "권하위치(보조작업) 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400DnLocWX";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_UP_WO_LOC"      )) //야드권상지시위치
						,slabUtils.trim(jrParam.getFieldString("V_YD_UP_WO_LOC_XAXIS")) //야드권상지시X축
						,slabUtils.trim(jrParam.getFieldString("V_YD_UP_WO_LOC_YAXIS")) //야드권상지시Y축
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_WRK_SH"     )) //야드설비작업매수
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_WRK_WT"     )) //야드설비작업중량
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_WRK_T"      )) //야드설비작업총두께
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"            )) //재료번호(크레인최하단)
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"         )) //야드설비ID
					};
			} else if ("DnLocMT".equals(trtGp)) {
				trtNm = "권하위치(주작업이적) 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400DnLocMT";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_UP_WO_LOC"      )) //야드권상지시위치
						,slabUtils.trim(jrParam.getFieldString("V_YD_UP_WO_LOC_XAXIS")) //야드권상지시X축
						,slabUtils.trim(jrParam.getFieldString("V_YD_UP_WO_LOC_YAXIS")) //야드권상지시Y축
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_WRK_SH"     )) //야드설비작업매수
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_WRK_WT"     )) //야드설비작업중량
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_WRK_T"      )) //야드설비작업총두께
					};
			} else if ("DnLocTD".equals(trtGp)) {
				trtNm = "권하위치(대차상차,Depiler불출) 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400DnLocTD";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD"    )) //야드스케쥴코드
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_WRK_SH")) //야드설비작업매수
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_WRK_WT")) //야드설비작업중량
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_WRK_T" )) //야드설비작업총두께
					};
			} else if ("DnLocPT".equals(trtGp)) {
				trtNm = "권하위치(차량상차) 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400DnLocPT";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_WRK_SH")) //야드설비작업매수
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_WRK_WT")) //야드설비작업중량
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_WRK_T" )) //야드설비작업총두께
					};
			} else if ("DnLocSY".equals(trtGp)) {
				trtNm = "권하위치(주작업) 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400DnLocSY";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD"        )) //야드스케쥴코드
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_WRK_SH"    )) //야드설비작업매수
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_WRK_WT"    )) //야드설비작업중량
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_WRK_T"     )) //야드설비작업총두께
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_WRK_MIN_W" )) //야드설비작업최소폭
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_WRK_MIN_L" )) //야드설비작업최소길이
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_TONG_W_TOL")) //야드크레인집게폭허용오차
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"           )) //재료번호(크레인최하단)
						,slabUtils.trim(jrParam.getFieldString("V_IS_IG_STMP_GP_NULL")) //성분미판정재료 포함여부
					};
			} else if ("DnLocTG".equals(trtGp)) {
				trtNm = "권하위치(To위치Guide) 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400DnLocTG";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
					};
			} else if ("EndCrnSchYN".equals(trtGp)){
				trtNm = "작업예약의 마지막 크레인스케줄여부 조회 by EQPID";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL008EndCrnSchYNByEQPID";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_EQP_ID")) //장비 ID 
					};
			} else if ("newModuleYN".equals(trtGp)){
				trtNm = "YDYDJ400 신규모듈 적용여부";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ400NewModuleYN";

			}
			
			else {
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
	 *      [A] 오퍼레이션명 : Slab야드 크레인스케줄 수정
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updYDYDJ400(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인스케줄[SlabYdSchDAO.updYDYDJ400] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("WmStrLoc".equals(trtGp)) {
				trtNm = "작업예약재료(TB_YD_WRKBOOKMTL) 저장위치 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.updYDYDJ400WmStrLoc";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"   )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("StkLyrU".equals(trtGp)) {
				trtNm = "적치단(TB_YD_STKLYR) 권상대기 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.updYDYDJ400StkLyrU";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"   )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("StkBedF".equals(trtGp)) {
				trtNm = "적치Bed(TB_YD_STKBED) 완산Bed 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.updYDYDJ400StkBedF";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
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
	 *      [A] 오퍼레이션명 : Slab야드 크레인스케줄 등록
	 *      
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String trtGp
	 *      @param String[][] param
	 *      @return int
	 *      @throws DAOException
	*/
	public int insYDYDJ400(String trtGp, String[][] param, String logId, String mthdNm) throws DAOException {
		String methodNm = "크레인스케줄[SlabYdSchDAO.insYDYDJ400] < " + mthdNm;
		String trtNm = "";

		try {
			String jspeed_query_id = "";

			if ("StkLyrD".equals(trtGp)) {
				trtNm = "적치단(TB_YD_STKLYR) 권하대기 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.insYDYDJ400StkLyrD";
			} else if ("CrnSch".equals(trtGp)) {
				trtNm = "크레인스케줄(TB_YD_CRNSCH) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.insYDYDJ400CrnSch";
			} else if ("CrnMtl".equals(trtGp)) {
				trtNm = "크레인작업재료(TB_YD_CRNWRKMTL) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.insYDYDJ400CrnMtl";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";
			
			int[] trtRst = trtProcess(jspeed_query_id, param);

			return trtRst.length;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/***************************************************************************
	 * 야드관리(YD) 내부
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 설비인출요구 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getYDYDJ410(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "설비인출요구[SlabYdSchDAO.getYDYDJ410] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("WB".equals(trtGp)) {
				trtNm = "작업예약 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ410WB";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"    )) //야드설비ID(대차)
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
					};
			} else if ("TcarSch".equals(trtGp)) {
				trtNm = "대차스케줄  조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ410TcarSch";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID(대차)
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
	 *      [A] 오퍼레이션명 : 설비인출요구 등록
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int insYDYDJ410(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "설비인출요구[SlabYdSchDAO.insYDYDJ410] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("WbMtl".equals(trtGp)) {
				trtNm = "작업예약재료(TB_YD_WRKBOOKMTL) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.insYDYDJ410WbMtl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
					};
			} else if("WbMtl2".equals(trtGp)) {
				trtNm = "작업예약재료(TB_YD_WRKBOOKMTL) 등록2";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.insYDYDJ410WbMtl2";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
						,slabUtils.trim(jrParam.getFieldString("V_STL_NOS")) //작업재료
					};
			} else if ("WbTcar".equals(trtGp)) {
				trtNm = "작업예약 등록(TB_YD_WRKBOOK) 대차하차 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.insYDYDJ410WbTcar";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_BAY_GP"    )) //야드동구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_SCH_ST_GP" )) //야드스케쥴기동구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_AIM_BAY_GP")) //야드목표동구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"    )) //야드설비ID(대차)
						,slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD"    )) //야드스케쥴코드
					};
			} else if ("WbMtlTcar".equals(trtGp)) {
				trtNm = "작업예약재료 등록(TB_YD_WRKBOOKMTL) 대차하차 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.insYDYDJ410WbMtlTcar";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"      )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"         )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_CARUD_STOP_LOC")) //야드하차정지위치
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP"    )) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO"    )) //야드적치Bed번호
					};
			} else if ("TcarSch".equals(trtGp)) {
				trtNm = "대차스케줄(TB_YD_TCARSCH) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.insYDYDJ410TcarSch";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_TCAR_SCH_ID"   )) //야드대차스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"         )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"        )) //야드설비ID(대차)
						,slabUtils.trim(jrParam.getFieldString("V_YD_CARLD_STOP_LOC")) //야드상차정지위치
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"      )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CARUD_STOP_LOC")) //야드하차정지위치
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP"    )) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO"    )) //야드적치Bed번호
					};
			} else if ("TcarMtl".equals(trtGp)) {
				trtNm = "대차이송재료(TB_YD_TCARFTMVMTL) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.insYDYDJ410TcarMtl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_TCAR_SCH_ID")) //야드대차스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP" )) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO" )) //야드적치Bed번호
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
	 *      [A] 오퍼레이션명 : 설비보급요구 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getYDYDJ420(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "설비보급요구[SlabYdSchDAO.getYDYDJ420] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("Rule".equals(trtGp)) {
				trtNm = "기준정보 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ420Rule";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
					};
			} else if ("CrnSch".equals(trtGp)) {
				trtNm = "크레인스케줄 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ420CrnSch";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD")) //야드스케쥴코드
					};
			} else if ("WrkBook".equals(trtGp)) {
				trtNm = "작업예약 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ420WrkBook";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD")) //야드스케쥴코드
					};
			} else if ("PrepSchSS".equals(trtGp)) {
				trtNm = "스카핑/2차절단 보급Lot 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ420PrepSchSS";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD")) //야드스케쥴코드
						,slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD")) //야드스케쥴코드
					};
			} else if ("PrepSchHC".equals(trtGp)) {
				trtNm = "C열연장입 보급Lot 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ420PrepSchHC";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD")) //야드스케쥴코드
						,slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD")) //야드스케쥴코드
					};
			} else if ("HR".equals(trtGp)) {
				trtNm = "C열연장입재 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ420HR";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD"    )) //야드스케쥴코드
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
					};
			} else if ("PR".equals(trtGp)) {
				trtNm = "후판장입재 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ420PR";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD")) //야드스케쥴코드
						,slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD")) //야드스케쥴코드
						,slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD")) //야드설비ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD")) //야드설비ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD")) //야드설비ID
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
	 *      [A] 오퍼레이션명 : 설비보급요구 수정
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updYDYDJ420(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "설비보급요구[SlabYdSchDAO.updYDYDJ420] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("WbToLoc".equals(trtGp)) {
				trtNm = "작업예약(TB_YD_WRKBOOK) To위치 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.updYDYDJ420WbToLoc";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"       )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_TO_LOC_GUIDE")) //야드To위치Guide
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"    )) //야드작업예약ID
					};
			} else if ("PP".equals(trtGp)) {
				trtNm = "준비스케줄(TB_YD_PREPSCH) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.updYDYDJ420PP";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"   )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_PREP_SCH_ID")) //야드준비스케쥴ID
					};
			} else if ("PM".equals(trtGp)) {
				trtNm = "준비재료(TB_YD_PREPMTL) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.updYDYDJ420PM";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_PREP_SCH_ID")) //야드준비스케쥴ID
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
	 *      [A] 오퍼레이션명 : 장입준비작업요구 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getYDYDJ430(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "장입준비작업요구[SlabYdSchDAO.getYDYDJ430] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("WM".equals(trtGp)) {
				trtNm = "작업예약재료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.getYDYDJ430WM";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID
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

}
