/**
 * @(#)SlabYdL2RcvDAO
 *
 * @version          V1.00
 * @author           허철호
 * @date             2012/11/22
 * 
 * @description      Slab야드 L2수신 DAO
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
 * [A] 클래스명 : Slab야드 L2수신 DAO
 *
 */

public class SlabYdL2RcvDAO extends DBAssistantDAO {

	private YdSlabUtils slabUtils = new YdSlabUtils();

	/***************************************************************************
	 * 연주정정L2(C3), 연주2정정L2(C7)
	 **************************************************************************/
	
	/**
	 *      [A] 오퍼레이션명 : Take-Out완료 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getC3YDL004(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "Take-Out완료[SlabYdL2RcvDAO.getC3YDL004] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("BedMtl".equals(trtGp)) {
				trtNm = "Bed재료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getC3YDL004BedMtl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
					};
			} else if ("AbMtl".equals(trtGp)) {
				trtNm = "후판Slab이상재 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getC3YDL004AbMtl";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_SLAB_NO")) //Slab번호
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
	 *      [A] 오퍼레이션명 : Take-In완료 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getC3YDL005(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "Take-In완료[SlabYdL2RcvDAO.getC3YDL005] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("Bed".equals(trtGp)) {
				trtNm = "Bed정보 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getC3YDL005Bed";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_LYR_NO")) //야드적치단번호
					};
			} else if ("BedLayer".equals(trtGp)) {
				trtNm = "BedLayer정보 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getC3YDL005BedLayer";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분 
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호 
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
	 *      [A] 오퍼레이션명 : Take-In완료 수정
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updC3YDL005(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "Take-In완료[SlabYdL2RcvDAO.updC3YDL005] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("MslabComm".equals(trtGp)) {
				trtNm = "주편공통(TB_PT_MSLABCOMM) 저장위치 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updC3YDL005MslabComm";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_STL_NO"    )) //재료번호
						,slabUtils.trim(jrParam.getFieldString("V_YD_STR_LOC")) //야드저장위치
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"  )) //수정자
					};
			} else if ("SlabComm".equals(trtGp)) {
				trtNm = "Slab공통(TB_PT_SLABCOMM) 저장위치 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updC3YDL005SlabComm";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_STL_NO"    )) //재료번호
						,slabUtils.trim(jrParam.getFieldString("V_YD_STR_LOC")) //야드저장위치
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"  )) //수정자
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
	 *      [A] 오퍼레이션명 : 대차이동실적 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getC3YDL007(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "대차이동실적[SlabYdL2RcvDAO.getC3YDL007] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("TcarSch".equals(trtGp)) {
				trtNm = "대차스케줄 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getC3YDL007TcarSch";
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

	
	/**
	 *      [A] 오퍼레이션명 : 대차이동실적 수정
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updC3YDL007(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "대차이동실적[SlabYdL2RcvDAO.updC3YDL007] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("TcarSch".equals(trtGp)) {
				trtNm = "대차스케줄(TB_YD_STKLYR) 차량진행상태 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updC3YDL007TcarSch";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_TCAR_MOVE_GP")) //야드대차이동구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP"  )) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"      )) //야드설비ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"       )) //수정자
					};
			} else if ("StkLyrStl".equals(trtGp)) {
				trtNm = "적치단(TB_YD_STKLYR) 재료번호 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updC3YDL007StkLyrStl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP" )) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO" )) //야드적치Bed번호
						,slabUtils.trim(jrParam.getFieldString("V_YD_TCAR_SCH_ID")) //야드대차스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
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

	
	/******************************************** *******************************
	 * C연주슬라브야드L2(Y1), 후판슬라브야드L2(Y3)
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : Crane Reschedule 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getCrnResch(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인리스케줄[SlabYdL2RcvDAO.getCrnResch] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("WoEqp".equals(trtGp)) {
				trtNm = "작업지시설비 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getCrnReschWoEqp";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID
						,slabUtils.trim(jrParam.getFieldString("V_BR_GP"    )) //고장복구구분
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
	 *      [A] 오퍼레이션명 : Crane Reschedule 수정
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updCrnResch(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인리스케줄[SlabYdL2RcvDAO.updCrnResch] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("WrkBook".equals(trtGp)) {
				trtNm = "작업예약(TB_YD_WRKBOOK) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updCrnReschWrkBook";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER" )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_BR_GP"    )) //고장복구구분
					};
			} else if ("CrnSch".equals(trtGp)) {
				trtNm = "크레인스케줄(TB_YD_CRNSCH) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updCrnReschCrnSch";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER" )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_BR_GP"    )) //고장복구구분
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
	 *      [A] 오퍼레이션명 : 설비고장복구실적 수정
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updY1YDL004(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "설비고장복구실적[SlabYdL2RcvDAO.updY1YDL004] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("CrnSchW".equals(trtGp)) {
				trtNm = "크레인스케줄(TB_YD_CRNSCH) 진행상태(대기) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL004CrnSchW";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER" )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID
					};
			} else if ("EqpPause".equals(trtGp)) {
				trtNm = "설비휴지(TB_YD_EQPPAUSE) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL004EqpPause";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"          )) //야드설비ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"           )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_PAUSE_CODE"  )) //야드설비휴지코드
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_PAUSE_OCC_DT")) //야드설비휴지발생일시
						,slabUtils.trim(jrParam.getFieldString("V_BR_GP"              )) //고장복구구분
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
	 *      [A] 오퍼레이션명 : 크레인권상실적 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getY1YDL008(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인권상실적[SlabYdL2RcvDAO.getY1YDL008] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("TcarSchUd".equals(trtGp)) {
				trtNm = "하차 대차스케줄 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL008TcarSchUd";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
					};
			} else if ("CarSchLd".equals(trtGp)) {
				trtNm = "상차 차량스케줄 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL008CarSchLd";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
					};
			} else if ("CarSchUd".equals(trtGp)) {
				trtNm = "하차 차량스케줄 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL008CarSchUd";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
					};
			} else if ("EndCrnSchYN".equals(trtGp)){
				trtNm = "작업예약의 마지막 크레인스케줄여부 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL008EndCrnSchYN";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID
					};
			} else if ("OtherCrnSchYN".equals(trtGp)){
				trtNm = "다른 작업예약 크레인 스케줄 기동 여부";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL008OtherCrnSchYN";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID
					};
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
	 *      [A] 오퍼레이션명 : 크레인권상실적 수정
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updY1YDL008(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인권상실적[SlabYdL2RcvDAO.updY1YDL008] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("TcarSchUd".equals(trtGp)) {
				trtNm = "대차스케줄(TB_YD_TCARSCH) 하차정보 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL008TcarSchUd";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"         )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_CARUD_STOP_LOC")) //야드하차정지위치
						,slabUtils.trim(jrParam.getFieldString("V_YD_CARUD_WRK_CRN" )) //야드하차작업크레인
						,slabUtils.trim(jrParam.getFieldString("V_YD_TCAR_SCH_ID"   )) //야드대차스케쥴ID
					};
			} else if ("TcarMtlDel".equals(trtGp)) {
				trtNm = "대차이송재료(TB_YD_TCARFTMVMTL) 하차정보 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL008TcarMtlDel";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_TCAR_SCH_ID")) //야드대차스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID" )) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
					};
			} else if ("CarSchUdWbId".equals(trtGp)) {
				trtNm = "차량스케줄(TB_YD_CARSCH) 작업예약ID 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL008CarSchUdWbId";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"   )) //수정자
					};
			} else if ("CarMtlDel".equals(trtGp)) {
				trtNm = "차량이송재료(TB_YD_CARFTMVMTL) 하차정보 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL008CarMtlDel";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
					};
			} else if ("CarSchUd".equals(trtGp)) {
				trtNm = "차량스케줄(TB_YD_CARSCH) 하차정보 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL008CarSchUd";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_WR_DT"      )) //운송작업시작일시
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"   )) //수정자
					};
			} else if ("CarSchLd".equals(trtGp)) {
				trtNm = "상차 차량스케줄(TB_YD_CARSCH) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL008CarSchLd";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_ARR_WLOC_CD"  )) //착지개소코드
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_WR_DT"        )) //운송작업시작일시
						,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
					};
			} else if ("CrnSch".equals(trtGp)) {
				trtNm = "크레인스케줄(TB_YD_CRNSCH) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL008CrnSch";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"        )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_UP_CMPL_DT"   )) //야드권상완료일시
						,slabUtils.trim(jrParam.getFieldString("V_YD_UP_WR_LOC"    )) //야드권상실적위치
						,slabUtils.trim(jrParam.getFieldString("V_YD_UP_WR_LAYER"  )) //야드권상실적단
						,slabUtils.trim(jrParam.getFieldString("V_YD_UP_WRK_ACT_GP")) //야드권상작업수행구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_UP_WR_XAXIS"  )) //야드권상실적X축
						,slabUtils.trim(jrParam.getFieldString("V_YD_UP_WR_YAXIS"  )) //야드권상실적Y축
						,slabUtils.trim(jrParam.getFieldString("V_YD_UP_WR_ZAXIS"  )) //야드권상실적Z축
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID"   )) //야드크레인스케쥴ID
					};
			} else if ("StkLyr".equals(trtGp)) {
				trtNm = "적치단(TB_YD_STKLYR) 크레인 및 권상위치 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL008StkLyr";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"    )) //야드설비ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
					};
			} else if ("StkBedF".equals(trtGp)) {
				trtNm = "적치Bed(TB_YD_STKBED) 완산여부 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL008StkBedF";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
					};
			} else if ("RethtHist".equals(trtGp)){
				trtNm = "회송이력(TB_YD_RETHTHIST) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL008RethtHist";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						//,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //크레인 스케쥴 ID
					};
			} 
			else {
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
	 *      [A] 오퍼레이션명 : 크레인권하실적 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getY1YDL009(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인권하실적[SlabYdL2RcvDAO.getY1YDL009] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("Curr".equals(trtGp)) {
				trtNm = "현재정보  조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL009Curr";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
					};
			} else if ("TcarSchLd".equals(trtGp)) {
				trtNm = "상차 대차스케줄  조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL009TcarSchLd";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID"   )) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"     )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WRK_PLAN_TCAR")) //야드작업계획대차
					};
			} else if ("CarSchLd".equals(trtGp)) {
				trtNm = "상차 차량스케줄  조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL009CarSchLd";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
					};
			} else if ("CarSchUd".equals(trtGp)) {
				trtNm = "하차 차량스케줄  조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL009CarSchUd";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("BankStlList".equals(trtGp)) {
				trtNm = "보온뱅크실적처리대상  조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL009BankStlList";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
					};	
			} else if ("MslabCommProg".equals(trtGp)) {
				trtNm = "주편공통진도  조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL009MslabCommProg";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
					   ,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
					};
			} else if ("SlabCommProg".equals(trtGp)) {
				trtNm = "Slab공통진도  조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL009SlabCommProg";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
					   ,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
					};
			} else if ("SlabCnt".equals(trtGp)) {
				trtNm = "슬라브 매수 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL009SlabCnt";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP"))
					   ,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO"))
					};
			} else if ("EmpBed".equals(trtGp)) {
				trtNm = "비어있는 베드 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL009EmpBed";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP"))
					   ,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO"))
					};
			} else if ("SlabWrkId".equals(trtGp)) {
				trtNm = "Slab에 지정되어 있는 작업예약 ID 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL009SlabWrkId";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_STL_NO")) //Slab번호
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
	 *      [A] 오퍼레이션명 : 크레인권하실적 수정
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updY1YDL009(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인권하실적[SlabYdL2RcvDAO.updY1YDL009] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("WbTcar".equals(trtGp)) {
				trtNm = "작업예약(TB_YD_WRKBOOK) 대차 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009WbTcar";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"        )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WRK_PLAN_TCAR")) //야드작업계획대차
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"     )) //야드작업예약ID
					};
			} else if ("WbTcarIns".equals(trtGp)) {
				trtNm = "작업예약 등록(TB_YD_WRKBOOK) 대차하차 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009WbTcarIns";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CARUD_WRK_BOOK_ID")) //야드하차작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"            )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_SCH_ST_GP"        )) //야드스케쥴기동구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"         )) //야드작업예약ID
					};
			} else if ("WbMtlTcarIns".equals(trtGp)) {
				trtNm = "작업예약재료(TB_YD_WRKBOOKMTL) 대차하차 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009WbMtlTcarIns";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CARUD_WRK_BOOK_ID")) //야드하차작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"            )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"         )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_TCAR_SCH_ID"      )) //야드대차스케쥴ID
					};
			} else if ("TcarSchLd".equals(trtGp)) {
				trtNm = "대차스케줄(TB_YD_TCARSCH) 상차 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009TcarSchLd";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"            )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_PROG_STAT"    )) //야드차량진행상태
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"         )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP"       )) //야드상차정지위치
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"           )) //야드설비ID(크레인)
						,slabUtils.trim(jrParam.getFieldString("V_WR_DT"               )) //야드상차개시,완료일시
						,slabUtils.trim(jrParam.getFieldString("V_YD_CARUD_WRK_BOOK_ID")) //야드하차작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_TCAR_SCH_ID"      )) //야드대차스케쥴ID
					};
			} else if ("TcarMtlIns".equals(trtGp)) {
				trtNm = "대차이송재료(TB_YD_TCARFTMVMTL) 상차 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009TcarMtlIns";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_TCAR_SCH_ID")) //야드대차스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_TCAR_SCH_ID")) //야드대차스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID" )) //야드크레인스케쥴ID
					};
			} else if ("CarMtlIns".equals(trtGp)) {
				trtNm = "차량이송재료(TB_YD_CARFTMVMTL) 상차 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009CarMtlIns";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID" )) //야드차량스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID" )) //야드차량스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID" )) //야드크레인스케쥴ID
					};
			} else if ("CarSchLd".equals(trtGp)) {
				trtNm = "차량스케줄(TB_YD_CARSCH) 상차 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009CarSchLd";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CAR_PROG_STAT")) //야드차량진행상태
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"     )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP"   )) //야드상차정지위치
						,slabUtils.trim(jrParam.getFieldString("V_WR_DT"           )) //야드상차개시,완료일시
						,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID"   )) //야드차량스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"        )) //수정자
					};
			} else if ("CarSchUd".equals(trtGp)) {
				trtNm = "차량스케줄(TB_YD_CARSCH) 하차 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009CarSchUd";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_WR_DT"        )) //야드하차완료일시
						,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
					};
			} else if ("StkLyr".equals(trtGp)) {
				trtNm = "적치단(TB_YD_STKLYR) 권하위치 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009StkLyr";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP" )) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO" )) //야드적치Bed번호
						,slabUtils.trim(jrParam.getFieldString("V_YD_DN_WR_LAYER")) //야드권하실적단
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID" )) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"   )) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"     )) //야드설비ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
					};
			} else if ("CrnMtl".equals(trtGp)) {
				trtNm = "크레인작업재료(TB_YD_CRNWRKMTL) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009CrnMtl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
					};
			} else if ("CrnSch".equals(trtGp)) {
				trtNm = "크레인스케줄(TB_YD_CRNSCH) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009CrnSch";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"        )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_DN_CMPL_DT"   )) //야드권하완료일시
						,slabUtils.trim(jrParam.getFieldString("V_YD_DN_WR_LOC"    )) //야드권하실적위치
						,slabUtils.trim(jrParam.getFieldString("V_YD_DN_WR_LAYER"  )) //야드권하실적단
						,slabUtils.trim(jrParam.getFieldString("V_YD_DN_WRK_ACT_GP")) //야드권하작업수행구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_DN_WR_XAXIS"  )) //야드권하실적X축
						,slabUtils.trim(jrParam.getFieldString("V_YD_DN_WR_YAXIS"  )) //야드권하실적Y축
						,slabUtils.trim(jrParam.getFieldString("V_YD_DN_WR_ZAXIS"  )) //야드권하실적Z축
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID"   )) //야드크레인스케쥴ID
					};
			} else if ("WbMtlDel".equals(trtGp)) {
				trtNm = "작업예약재료(TB_YD_WRKBOOKMTL) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009WbMtlDel";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"   )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("WbDel".equals(trtGp)) {
				trtNm = "작업예약(TB_YD_WRKBOOK) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009WbDel";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"   )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("MslabComm".equals(trtGp)) {
				trtNm = "주편공통(TB_PT_MSLABCOMM) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009MslabComm";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
					};
			} else if ("SlabComm".equals(trtGp)) {
				trtNm = "Slab공통(TB_PT_SLABCOMM) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009SlabComm";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
					};
			} else if ("MslabCommProg".equals(trtGp)) {
				trtNm = "주편공통(TB_PT_MSLABCOMM) 진도 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009MslabCommProg";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_CURR_PROG_REG_DDTT")) //현재진도등록일시
						,slabUtils.trim(jrParam.getFieldString("V_CURR_PROG_CD"      )) //현재진도코드
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"            )) //재료번호
					};
			} else if ("SlabCommProg".equals(trtGp)) {
				trtNm = "Slab공통(TB_PT_SLABCOMM) 진도 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009SlabCommProg";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_CURR_PROG_REG_DDTT")) //현재진도등록일시
						,slabUtils.trim(jrParam.getFieldString("V_CURR_PROG_CD"      )) //현재진도코드
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"            )) //재료번호
					};
			} else if ("Stock".equals(trtGp)) {
				trtNm = "저장품(TB_YD_STOCK) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009Stock";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"   )) //수정자
					};
			} else if ("Stock2".equals(trtGp)) {
				trtNm = "저장품2(TB_YD_STOCK) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009Stock2";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"   )) //수정자
					};
			} else if ("StlMoveLd".equals(trtGp)) {
				trtNm = "소재이송지시(TB_PT_STLFRTOMOVE) 상차 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009StlMoveLd";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_WR_DT"        )) //이송상차일자
						,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
					};
			} else if ("StlMoveUd".equals(trtGp)) {
				trtNm = "소재이송지시(TB_PT_STLFRTOMOVE) 하차 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009StlMoveUd";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_WR_DT"        )) //이송완료일시
						,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
					};
			} else if ("MslabCommCar".equals(trtGp)) {
				trtNm = "주편공통(TB_PT_MSLABCOMM) 상하차 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009MslabCommCar";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_LD_UD_GP"     )) //차량상하차구분
						,slabUtils.trim(jrParam.getFieldString("V_WR_DT"        )) //이송완료일시
						,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
					};
			} else if ("SlabCommCar".equals(trtGp)) {
				trtNm = "Slab공통(TB_PT_SLABCOMM) 상하차 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009SlabCommCar";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_LD_UD_GP"     )) //차량상하차구분
						,slabUtils.trim(jrParam.getFieldString("V_WR_DT"        )) //이송완료일시
						,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
					};
			} else if ("updateMslabCommonSubInfo".equals(trtGp)) {
				trtNm = "주편공통(TB_PT_MSLABCOMM) 보온뱅크장입시각 수정";
				jspeed_query_id = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateMslabCommonSubInfo";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MSLAB_NO"     )) //재료번호 
					};
			} else if ("updateSlabCommonSubInfo".equals(trtGp)) {
				trtNm = "슬라브공통(TB_PT_SLABCOMM) 보온뱅크적치유무 수정";
				jspeed_query_id = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateSlabCommonSubInfo";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_MSLAB_NO"     )) //재료번호 
					};
			} else if ("updateMslabCommonSubEndInfo".equals(trtGp)) {
				trtNm = "주편공통(TB_PT_MSLABCOMM) 보온뱅크추출시간 수정";
				jspeed_query_id = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateMslabCommonSubEndInfo";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_MSLAB_NO"     )) //재료번호 
					};	
			} else if ("CarSchUd2".equals(trtGp)) {
				trtNm = "차량스케줄(TB_YD_CARSCH) 하차 수정2";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009CarSchUd2";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_WR_DT"        )) //야드하차완료일시
						,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
					};
			} else if ("RethtHist".equals(trtGp)){
				trtNm = "회송이력(TB_YD_RETHTHIST) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL009RethtHist";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
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
	 *      [A] 오퍼레이션명 : 수불구변경요구 적치Bed 수정
	 *      
	 *      @param String[][] param
	 *      @param String logId
	 *      @param String methodNm
	 *      @return int
	 *      @throws DAOException
	*/
	public int updY3YDL011SB(String[][] param, String logId, String mthdNm) throws DAOException {
		String methodNm = "수불구변경요구[SlabYdL2RcvDAO.updY3YDL011SB] < " + mthdNm;
		String trtNm = "적치Bed(TB_YD_STKBED) 수정 : ";

		try {
			String jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updStatStkBedAct";

			slabUtils.printParam(logId, trtNm, param);			
			
			int[] trtRst = trtProcess(jspeed_query_id, param);

			return trtRst.length;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 크레인작업지시요구 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getY1YDL007(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인작업지시요구[SlabYdL2RcvDAO.getY1YDL007] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("CS".equals(trtGp)) {
				trtNm = "크레인스케줄 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL007CS";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"    )) //야드설비ID
					};
			} else if ("WB".equals(trtGp)) {
				trtNm = "작업예약 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL007WB";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID
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
	 *      [A] 오퍼레이션명 : 크레인작업지시요구 수정
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updY1YDL007(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인작업지시요구[SlabYdL2RcvDAO.updY1YDL007] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("CS".equals(trtGp)) {
				trtNm = "크레인스케줄(TB_YD_CRNSCH) 권상지시단 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL007CS";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
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
	 *      [A] 오퍼레이션명 : 강제권상요구 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getY1YDL012(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "강제권상요구[SlabYdL2RcvDAO.getY1YDL012] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("BL".equals(trtGp)) {
				trtNm = "Bed정보 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL012BL";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CRN_XAXIS" )) //야드크레인X축
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_YAXIS" )) //야드크레인Y축
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
					};
			} else if ("ES".equals(trtGp)) {
				trtNm = "설비상태 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL012ES";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID
					};
			} else if ("SR".equals(trtGp)) {
				trtNm = "스케줄코드 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL012SR";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD")) //야드스케쥴코드
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
	 *      [A] 오퍼레이션명 : 강제권상요구 등록
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updY1YDL012(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "강제권상요구[SlabYdL2RcvDAO.updY1YDL012] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("CS".equals(trtGp)) {
				trtNm = "크레인스케줄(TB_YD_CRNSCH) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL012CS";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"    )) //야드설비ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
					};
			} else if ("CM".equals(trtGp)) {
				trtNm = "크레인작업재료(TB_YD_CRNWRKMTL) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL012CM";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
					};
			} else if ("ST".equals(trtGp)) {
				trtNm = "저장품(TB_YD_STOCK) 작업예약정보 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL012ST";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"   )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD"  )) //야드스케쥴코드
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"     )) //재료번호
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
	 *      [A] 오퍼레이션명 : L2픽업크레인 지시정보 수정
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updY3YDL015(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "L2픽업크레인 지시정보 수정[SlabYdL2RcvDAO.updY3YDL015] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("BC".equals(trtGp)) {
				trtNm = "적치Bed(TB_YD_STKBED) Flag 초기화";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY3YDL015BC";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //적치열
					};
			} else if ("BU".equals(trtGp)) {
				trtNm = "적치Bed(TB_YD_STKBED) Flag 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY3YDL015BU";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"  )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //적치열
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO"     )) //적치Bed
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
	 *      [A] 오퍼레이션명 : 팔렛트 자동 상차완료 처리 여부 조회
	 *      
	 *      @param String ydDnWrLoc
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getPtAutoCompleteYn(String ydDnWrLoc) throws DAOException {
		String methodNm = "팔렛트 자동 상차완료 처리 여부 조회[SlabYdL2RcvDAO.getPtAutoCompleteYn] < " + ydDnWrLoc;
		String trtNm = "팔렛트 자동 상차완료 처리 여부 조회";

		try {
			String jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getPtAutoCompleteYn";
			Object[] param = null;

			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getPtAutoCompleteYn";
			param = new Object[] {
					 slabUtils.trim(ydDnWrLoc) //권하위치
					 }; 
			trtNm += " : ";

			JDTORecordSet jsRst = getRecordSet(jspeed_query_id, param);
				
			slabUtils.printLog(methodNm, trtNm + jsRst.size(), "DB");
			
			return jsRst;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(trtNm, methodNm, e));
		}
	}


}
