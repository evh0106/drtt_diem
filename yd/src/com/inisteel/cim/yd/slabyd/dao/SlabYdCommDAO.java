/**
 * @(#)SlabYdCommDAO
 *
 * @version          V1.00
 * @author           허철호
 * @date             2012/11/22
 * 
 * @description      Slab야드 공통 DAO
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2012/11/22   허철호      허철호      최초 등록
 * V1.02  2015/12/14   이준영      이준영      항만 신규설비 추가
 */
package com.inisteel.cim.yd.slabyd.dao;

import java.util.Iterator;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdSlabUtils;

/**
 * [A] 클래스명 : Slab야드 공통 DAO
 *
 */

public class SlabYdCommDAO extends DBAssistantDAO {

	private YdSlabUtils slabUtils = new YdSlabUtils();

	/***************************************************************************
	 * 공통 조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : Sequence ID 조회
	 *      
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String trtGp
	 *      @return String
	 *      @throws DAOException
	*/
	public String getSeqId(String logId, String mthdNm, String trtGp) throws DAOException {
		String methodNm = "SeqID조회[SlabYdCommDAO.getSeqId] < " + mthdNm;
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			String seqId = ""; //반환할 Sequence ID

			if ("CrnSch".equals(trtGp)) {
				trtNm = "야드크레인스케쥴ID";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getSeqIdCrnSch";
			} else if ("WrkBook".equals(trtGp)) {
				trtNm = "야드작업예약ID";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getSeqIdWrkBook";
			} else if ("PrepSch".equals(trtGp)) {
				trtNm = "야드준비스케쥴ID";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getSeqIdPrepSch";
			} else if ("TcarSch".equals(trtGp)) {
				trtNm = "야드대차스케쥴ID";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getSeqIdTcarSch";
			} else if ("CarSch".equals(trtGp)) {
				trtNm = "야드차량스케쥴ID";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getSeqIdCarSch";
			} else if ("RetHt".equals(trtGp)) {
				trtNm = "회송이력ID";
				jspeed_query_id = "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getRetHtHistID";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			JDTORecordSet jsRst = getRecordSet(jspeed_query_id, null);

			if (jsRst.size() > 0) {
				seqId = slabUtils.trim(jsRst.getRecord(0).getFieldString("SEQ_ID")); //Sequence ID
			}
			
			return seqId;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	

	/**
	 *      [A] 오퍼레이션명 : 상태 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getStat(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "상태조회[SlabYdCommDAO.getStat] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;
			
			

			if ("CrnDan".equals(trtGp)) {
				trtNm = "단적치상태";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getStatDan";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
						,slabUtils.trim(jrParam.getFieldString("V_YD_UP_WR_LAYER")) //야드적치Bed번호
					};
			} else if ("Eqp".equals(trtGp)) {
				trtNm = "설비상태";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getStatEqp";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID
					};
			} else if ("SchCd".equals(trtGp)) {
				trtNm = "스케줄코드상태";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getStatSchCd";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD")) //야드스케쥴코드
					};
			} else if ("CrnSch".equals(trtGp)) {
				trtNm = "크레인스케줄상태";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getStatCrnSch";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
					};
			} else if ("Bed".equals(trtGp)) {
				trtNm = "Bed상태";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getStatBed";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
					};
			} else if ("TcarBed".equals(trtGp)) {
				trtNm = "대차Bed상태";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getStatTcarBed";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"         )) //야드설비ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CURR_BAY_GP_NEW")) //야드현재동구분(신규)
					};
			} else if ("PuBedTcar".equals(trtGp)) {
				trtNm = "PickupBed대차상태";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getStatPuBedTcar";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분(Pickup)
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호(Pickup)
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"    )) //야드설비ID(대차)
					};
			} else if ("RehtBed".equals(trtGp)) {
				trtNm = "재열재Bed상태";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getStatRehtBed";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_STL_NO"       )) //재료번호
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
					};
			} else if ("FlexSndYn".equals(trtGp)) {
				trtNm = "Flex송신여부";
				jspeed_query_id = "com.inisteel.cim.yd.common.util.YdUtils.chklist";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_GP")) //야드구분
					};
			} else if ("SlabCurrProg".equals(trtGp)) {
				trtNm = "슬라브 현재 진도 조회";
				jspeed_query_id = "com.inisteel.cim.yd.common.util.YdUtils.getSlabCommProg";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("STOCK_ID")) //재료번호
				};
			} else if ("SchCdWrkCrn".equals(trtGp)) {
				trtNm = "지정크레인기준스케줄코드상태"; //2025.05 지정크레인이 있는 경우 지정크레인의 Spec을 가져올 수 있도록 수정
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getStatSchCdWrkCrn";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("YD_EQP_ID")) //지정크레인
					   ,slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD")) //야드스케쥴코드
					   
					};				
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 저장 위치 조회
	 *      
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String trtGp
	 *      @return String
	 *      @throws DAOException
	*/
	public JDTORecordSet getStrLocInfo(String msgId, JDTORecord jrParam) throws DAOException {
		String methodNm = "저장 위치 조회[SlabYdCommDAO.getStrLocInfo] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			trtNm = "저장 위치 정보";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getStrLocInfo";
			param = new Object[] {
					 slabUtils.trim(jrParam.getFieldString("V_STL_NO"     )) //재료번호
				};
		
			trtNm = trtNm + "(" + msgId + ") : ";

			JDTORecordSet jsRst = getRecordSet(jspeed_query_id, param);
			slabUtils.printLog(logId, trtNm + jsRst.size(), "DB");
			
			return jsRst;
			
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 저장 위치 조회2
	 *      
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String trtGp
	 *      @return String
	 *      @throws DAOException
	*/
	public JDTORecordSet getStrLocInfo2(String msgId, JDTORecord jrParam) throws DAOException {
		String methodNm = "저장 위치 조회2[SlabYdCommDAO.getStrLocInfo2] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			trtNm = "저장 위치 정보2";
			jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getStrLocInfo2";
			param = new Object[] {
					 slabUtils.trim(jrParam.getFieldString("V_STL_NO"     )) //재료번호
				};
		
			trtNm = trtNm + "(" + msgId + ") : ";

			JDTORecordSet jsRst = getRecordSet(jspeed_query_id, param);
			slabUtils.printLog(logId, trtNm + jsRst.size(), "DB");
			
			return jsRst;
			
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 상태 수정
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updStat(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "상태수정[SlabYdCommDAO.updStat] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("CrnSchWrkProg".equals(trtGp)) {
				trtNm = "크레인스케줄(TB_YD_CRNSCH) 야드작업진행상태 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updStatCrnSchWrkProg";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"        )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WRK_PROG_STAT")) //야드작업진행상태
						,slabUtils.trim(jrParam.getFieldString("V_YD_WRK_PROG_STAT")) //야드작업진행상태
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID"   )) //야드크레인스케쥴ID
					};
			} else if ("Eqp".equals(trtGp)) {
				trtNm = "설비(TB_YD_EQP) 야드설비상태 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updStatEqp";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"   )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_STAT")) //야드설비상태
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"  )) //야드설비ID
					};
			} else if ("EqpMode".equals(trtGp)) {
				trtNm = "설비(TB_YD_EQP) 야드설비작업Mode 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updStatEqpMode";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"       )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_WRK_MODE")) //야드설비작업Mode
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"      )) //야드설비ID
					};
			} else if ("StkBedAct".equals(trtGp)) {
				trtNm = "적치Bed(TB_YD_STKBED) 야드적치Bed활성상태 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updStatStkBedAct";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"           )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_ACT_STAT")) //야드적치Bed활성상태
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP"      )) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO"      )) //야드적치Bed번호
					};
			} else if ("StkBedActCA".equals(trtGp)) {
				trtNm = "적치Bed(TB_YD_STKBED) 야드적치Bed활성상태 CloseAll";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updStatStkBedActCA";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
					};
			} else if ("StkBedUsg".equals(trtGp)) {
				trtNm = "적치Bed(TB_YD_STKBED) 야드적치Bed용도구분 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updStatStkBedUsg";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"         )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_USG_GP")) //야드적치Bed용도구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP"    )) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO"    )) //야드적치Bed번호
					};
			} else if ("StkLyrMtl".equals(trtGp)) {
				trtNm = "적치단(TB_YD_STKLYR) 야드적치단재료상태 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updStatStkLyrMtl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"           )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_LYR_MTL_STAT")) //야드적치단재료상태
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP"      )) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO"      )) //야드적치Bed번호
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_LYR_NO"      )) //야드적치단번호
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
	 * Table별 등록처리
	 **************************************************************************/
	
	/**
	 *      [A] 오퍼레이션명 : Table별 등록처리
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int insSlabYd(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "공통등록[SlabYdCommDAO.insSlabYd] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("Stock".equals(trtGp)) {
				trtNm = "저장품(TB_YD_STOCK) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.insSlabYdStock";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER")) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"  )) //재료번호
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"  )) //재료번호
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"  )) //재료번호
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"  )) //재료번호
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"  )) //재료번호
					};
			} else if ("PrepSch".equals(trtGp)) {
				trtNm = "준비스케줄(TB_YD_PREPSCH) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.insSlabYdPrepSch";
				param = new Object[] {
 						 slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD"     )) //야드스케쥴코드
 						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //등록자
						,slabUtils.trim(jrParam.getFieldString("V_YD_PREP_SCH_ID")) //야드준비스케쥴ID
					};
			} else if ("PrepMtl".equals(trtGp)) {
				trtNm = "준비재료(TB_YD_PREPMTL) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.insSlabYdPrepMtl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_PREP_SCH_ID")) //야드준비스케쥴ID
 						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"        )) //재료번호
 						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //등록자
 						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
 						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP" )) //야드적치열구분
 						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO" )) //야드적치Bed번호
 						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_LYR_NO" )) //야드적치단번호
					};
			} else if ("WrkBook".equals(trtGp)) {
				trtNm = "작업예약(TB_YD_WRKBOOK) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.insSlabYdWrkBook2";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"       )) //야드작업예약ID
 						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"          )) //등록자
 						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"          )) //수정자
 						,slabUtils.trim(jrParam.getFieldString("V_YD_GP"             )) //야드구분
 						,slabUtils.trim(jrParam.getFieldString("V_YD_BAY_GP"         )) //야드동구분
 						,slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD"         )) //야드스케쥴코드
 						,slabUtils.trim(jrParam.getFieldString("V_YD_SCH_PRIOR"      )) //야드스케쥴우선순위
 						,slabUtils.trim(jrParam.getFieldString("V_YD_SCH_PROG_STAT"  )) //야드스케쥴진행상태
 						,slabUtils.trim(jrParam.getFieldString("V_YD_SCH_ST_GP"      )) //야드스케쥴기동구분
 						,slabUtils.trim(jrParam.getFieldString("V_YD_SCH_REQ_GP"     )) //야드스케쥴요청구분
 						,slabUtils.trim(jrParam.getFieldString("V_YD_AIM_YD_GP"      )) //야드목표야드구분
 						,slabUtils.trim(jrParam.getFieldString("V_YD_AIM_BAY_GP"     )) //야드목표동구분
 						,slabUtils.trim(jrParam.getFieldString("V_YD_TO_LOC_DCSN_MTD")) //야드To위치결정방법
 						,slabUtils.trim(jrParam.getFieldString("V_YD_TO_LOC_GUIDE"   )) //야드To위치Guide
 						,slabUtils.trim(jrParam.getFieldString("V_YD_WRK_PLAN_TCAR"  )) //야드작업계획대차
 						,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_USE_GP"     )) //야드차량사용구분
 						,slabUtils.trim(jrParam.getFieldString("V_TRN_EQP_CD"        )) //운송장비코드
 						,slabUtils.trim(jrParam.getFieldString("V_CAR_NO"            )) //차량번호
 						,slabUtils.trim(jrParam.getFieldString("V_CARD_NO"           )) //카드번호
 						,slabUtils.trim(jrParam.getFieldString("V_YD_WRK_PLAN_CRN"   )) //야드작업계획크레인
					};
			} else if ("WrkBookMtl".equals(trtGp)) {
				trtNm = "작업예약재료(TB_YD_WRKBOOKMTL) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.insSlabYdWrkBookMtl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"   )) //야드작업예약ID
 						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"        )) //재료번호
 						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //등록자
 						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
 						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP" )) //야드적치열구분
 						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO" )) //야드적치Bed번호
 						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_LYR_NO" )) //야드적치단번호
 						,slabUtils.trim(jrParam.getFieldString("V_YD_UP_COLL_SEQ")) //야드권상모음순서
					};
			} else if ("WrkHist".equals(trtGp)) {
				trtNm = "작업이력(TB_YD_WRKHIST) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.insSlabYdWrkHist";
				param = new Object[] {
 						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
 						,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID" )) //야드차량스케쥴ID
 						,slabUtils.trim(jrParam.getFieldString("V_YD_TCAR_SCH_ID")) //야드대차스케쥴ID
 						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID" )) //야드크레인스케쥴ID
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
	 *      [A] 오퍼레이션명 : Table별 수정처리
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updSlabYd(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "공통수정[SlabYdCommDAO.updSlabYd] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("EqpCurrBay".equals(trtGp)) {
				trtNm = "설비(TB_YD_EQP) 야드현재동구분 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdEqpCurrBay";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_CURR_BAY_GP")) //야드현재동구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"     )) //야드설비ID
					};
			} else if ("EqpHomeBay".equals(trtGp)) {
				trtNm = "설비(TB_YD_EQP) 야드현재동구분 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdEqpHomeBay";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_HOME_BAY_GP")) //야드Home동구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"     )) //야드설비ID
					};
			} else if ("StockLoc".equals(trtGp)) {
				trtNm = "저장품(TB_YD_STOCK) 저장위치 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdStockLoc";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
					}; 
			} else if ("StockAbMtl".equals(trtGp)) {
				trtNm = "저장품(TB_YD_STOCK) 이상재코드 수정"; 
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdStockAbMtl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_AB_OCCR_RSN_CD"))   //이상재코드
						,slabUtils.trim(jrParam.getFieldString("V_YD_ABMTL_ASGN_DD")) //이상재등록일자
						,slabUtils.trim(jrParam.getFieldString("V_SNDBK_GP_ETC")) 	  //이상재로그
						,slabUtils.trim(jrParam.getFieldString("V_SLAB_NO"     )) 	  //재료번호
					}; 	
			} else if ("StkLyrClr".equals(trtGp)) {
				trtNm = "적치단(TB_YD_STKLYR) 재료번호 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdStkLyrClr";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
					};
			} else if ("StkLyrStlNo".equals(trtGp)) {
				trtNm = "적치단(TB_YD_STKLYR) 재료번호 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdStkLyrStlNo";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"           )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"             )) //재료번호
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_LYR_MTL_STAT")) //야드적치단재료상태
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP"      )) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO"      )) //야드적치Bed번호
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_LYR_NO"      )) //야드적치단번호
					};
			} else if ("StkMtlMV".equals(trtGp)) {
				trtNm = "적치단(TB_YD_STKLYR) 재료번호 이동";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdStkMtlMV";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP_TO")) //야드적치열구분(To)
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO_TO")) //야드적치Bed번호(To)
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP_FR")) //야드적치열구분(From)
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO_FR")) //야드적치Bed번호(From)
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP_FR")) //야드적치열구분(From)
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO_FR")) //야드적치Bed번호(From)
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"        )) //수정자
					};
			} else if ("StkLyrShift".equals(trtGp)) {
				trtNm = "적치단(TB_YD_STKLYR) 재열재 Shift";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdStkLyrShift";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
					};
			} else if ("WbPrior".equals(trtGp)) {
				trtNm = "작업예약(TB_YD_WRKBOOK) 야드스케쥴우선순위 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdWbPrior";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"    )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_SCH_PRIOR")) //야드스케쥴우선순위
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID" )) //야드작업예약ID
					};
			} else if ("PsPrior".equals(trtGp)) {
				trtNm = "준비스케줄(TB_YD_PREPSCH) 보급순서(야드배차순서) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdPsPrior";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_CARASGN_SEQ")) //보급순서(야드배차순서)
						,slabUtils.trim(jrParam.getFieldString("V_YD_PREP_SCH_ID")) //야드준비스케쥴ID
					};
			} else if ("SlabCommLyr".equals(trtGp)) {
				trtNm = "슬라브공통(TB_PT_SLABCOMM) 현재저장위치 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabCommLyr";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_STL_NO"      )) //재료번호
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP"      )) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO"      )) //야드적치Bed번호
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_LYR_NO2"      )) //야드적치단번호
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"    )) //수정자
					};
			} else if ("MSlabCommLyr".equals(trtGp)) {
				trtNm = "주편공통(TB_PT_MSLABCOMM) 현재저장위치 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updMSlabCommLyr";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_STL_NO"      )) //재료번호
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP"      )) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO"      )) //야드적치Bed번호
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_LYR_NO2"      )) //야드적치단번호
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"    )) //수정자
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
	 *      [A] 오퍼레이션명 : Batch 처리
	 *      
	 *      @param String trtGp
	 *      @param String[][] param
	 *      @param String logId
	 *      @param String methodNm
	 *      @return int
	 *      @throws DAOException
	*/
	public int upsBatch(String trtGp, String[][] param, String logId, String mthdNm) throws DAOException {
		String methodNm = "공통Batch등록[SlabYdCommDAO.upsBatch] < " + mthdNm;
		String trtNm = "";

		try {
			String jspeed_query_id = "";

			if ("StkLyrStlNo".equals(trtGp)) {
				trtNm = "적치단(TB_YD_STKLYR) 재료번호 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdStkLyrStlNo";
			} else if ("PrepMtl".equals(trtGp)) {
				trtNm = "준비재료(TB_YD_PREPMTL) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.insSlabYdPrepMtl";
			} else if ("WbCrn".equals(trtGp)) {
				trtNm = "작업예약(TB_YD_WRKBOOK) 야드작업계획크레인 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdWbCrn";
			} else if ("WbPrior".equals(trtGp)) {
				trtNm = "작업예약(TB_YD_WRKBOOK) 야드스케쥴우선순위 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdWbPrior";
			} else if ("WrkBookMtl".equals(trtGp)) {
				trtNm = "작업예약재료(TB_YD_WRKBOOKMTL) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.insSlabYdWrkBookMtl";
			} else if ("WrkBookDel".equals(trtGp)) {
				trtNm = "작업예약(TB_YD_WRKBOOK) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updDelYnWrkBook";
			} else if ("WrkBookMtlDel".equals(trtGp)) {
				trtNm = "작업예약재료(TB_YD_WRKBOOKMTL) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updDelYnWrkBookMtl";
			} else if ("WrkBookToGuide".equals(trtGp)) {
				trtNm = "작업예약(TB_YD_WRKBOOK) To위치 Guide 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO.updYDYDJ420WbToLoc";
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
	 * Table별 삭제처리
	 **************************************************************************/
	
	/**
	 *      [A] 오퍼레이션명 : Table별 삭제처리
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updDelYn(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "공통삭제[SlabYdCommDAO.updDelYn] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("Stock".equals(trtGp)) {
				trtNm = "저장품(TB_YD_STOCK) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updDelYnStock";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER")) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"  )) //재료번호
					};
			} else if ("WrkBook".equals(trtGp)) {
				trtNm = "작업예약(TB_YD_WRKBOOK) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updDelYnWrkBook";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"   )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("WrkBookMtl".equals(trtGp)) {
				trtNm = "작업예약재료(TB_YD_WRKBOOKMTL) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updDelYnWrkBookMtl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"   )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else if ("CarSch".equals(trtGp)) {
				trtNm = "차량스케줄(TB_YD_CARSCH) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updDelYnCarSch";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
					};
			} else if ("CarFtmvMtl".equals(trtGp)) {
				trtNm = "차량이송재료(TB_YD_CARFTMVMTL) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updDelYnCarFtmvMtl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"     )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
					};
			} else if ("PrepSch".equals(trtGp)) {
				trtNm = "준비스케줄(TB_YD_PREPSCH) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updDelYnPrepSch";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_PREP_SCH_ID")) //야드준비스케쥴ID
					};
			} else if ("PrepMtl".equals(trtGp)) {
				trtNm = "준비재료(TB_YD_PREPMTL) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updDelYnPrepMtl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_PREP_SCH_ID")) //야드준비스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"        )) //재료번호
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
	 * L2 송신 전문 조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : L2전문조회
	 *      
	 *      @param String msgId
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMsgL2(String msgId, JDTORecord jrParam) throws DAOException {
		String methodNm = "L2전문조회[SlabYdCommDAO.getMsgL2] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("YDC3L001".equals(msgId) || "YDC7L001".equals(msgId) || "YDE9L001".equals(msgId)) { // 항만야드 기능적용 보완 : 2015.12.15 by LeeJY
				trtNm = "수불구변경응답";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDC3L001";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_JMS_TC_CD"    )) //JMSTC코드
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
					};
			} else if ("YDC3L003".equals(msgId) || "YDC7L003".equals(msgId) || "YDE9L003".equals(msgId)) { // 항만야드 기능적용 보완 : 2015.12.15 by LeeJY
				trtNm = "Carry-Out완료";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDC3L003";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분(권상실적위치)
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호(권상실적위치)
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
					};
			} else if ("YDC7L003Tcar".equals(msgId)) {
				trtNm = "Carry-Out완료(대차)";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDC7L003Tcar";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분(권상실적위치)
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호(권상실적위치)
					};
			} else if ("YDC3L004".equals(msgId) || "YDE9L004".equals(msgId) || "YDC7L004".equals(msgId)) {  // 항만야드 기능적용 보완 : 2015.12.23 by LeeJY, C연주 보온뱅크 기능 적용 보완:2018.06.19
				trtNm = "Carry-In완료";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDC3L004";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분(권하실적위치)
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호(권하실적위치)
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID"  )) //야드작업예약ID
					};
			} else if ("YDC3L005".equals(msgId)) { 
				trtNm = "Carry-In재료정보";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDC3L005";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분(권하실적위치)
					};
			} else if ("YDE9L005".equals(msgId)) {  // 항만야드 기능적용 보완 : 2015.12.23 by LeeJY
				trtNm = "Carry-In재료정보";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDE9L005";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분(권하실적위치)
					};
			} else if ("YDC3L006".equals(msgId) || "YDC7L006".equals(msgId) || "YDY3L006".equals(msgId)) {
				trtNm = "대차출발지시";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDC3L006";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_TCAR_SCH_ID")) //야드대차스케쥴ID
					};
			} else if ("YDC3L007".equals(msgId) || "YDC7L007".equals(msgId) || "YDY3L007".equals(msgId)) {
				trtNm = "대차작업실적";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDC3L007";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_TCAR_SCH_ID")) //야드대차스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_TCAR_SCH_ID")) //야드대차스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID" )) //야드크레인스케쥴ID
					};
			} else if ("YDC3L009".equals(msgId)) {
				trtNm = "열연재열재재료정보";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDC3L009";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_STL_NO")) //재료번호
					};
			//} else if ("YDY1L001".equals(msgId) || "YDY3L001".equals(msgId)) {
			} else if ("YDY1L001".equals(msgId) || "YDY3L001".equals(msgId) || "YDE7L001".equals(msgId)) { //항만야드추가 : 2015.12.14 LeeJY
				trtNm = "저장위치제원";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDY1L001";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_INFO_SYNC_CD")) //야드정보동기화코드
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP"  )) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP"  )) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP"  )) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO"  )) //야드적치Bed번호
					};
			//} else if ("YDY1L002".equals(msgId) || "YDY3L002".equals(msgId)) {
			} else if ("YDY1L002".equals(msgId) || "YDY3L002".equals(msgId) || "YDE7L002".equals(msgId)) { //항만야드추가 : 2015.12.14 LeeJY
				trtNm = "저장품제원";

				//야드정보동기화코드
				// 1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제),H:C열연장입,P:1후판장입,Q:2후판장입
				String ydInfoSyncCd = slabUtils.trim(jrParam.getFieldString("V_YD_INFO_SYNC_CD"));
				
				if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
					//저장위치별
					jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDY1L002Loc";
					param = new Object[] {
							 ydInfoSyncCd                                              //야드정보동기화코드
							,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
							,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
						};
				} else {
					//재료별
					jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDY1L002Stl";
					param = new Object[] {
							 slabUtils.trim(jrParam.getFieldString("V_YD_GP" )) //야드구분
							,ydInfoSyncCd                                       //야드정보동기화코드
							,slabUtils.trim(jrParam.getFieldString("V_STL_NO")) //재료번호
							,slabUtils.trim(jrParam.getFieldString("V_STL_NO")) //재료번호
						};
				}
			} else if ("YDY1L002DnWr".equals(msgId) || "YDY3L002DnWr".equals(msgId) || "YDE7L002DnWr".equals(msgId)) { // 항만야드 기능적용 보완 : 2015.12.17 by LeeJY
				trtNm = "저장품제원(권하실적)";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDY1L002DnWr";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
					};
			} else if ("YDY3L002Chg".equals(msgId)) {
				trtNm = "저장품제원(후판장입)";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDY3L002Chg";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_PTOP_PLNT_GP")) //조업공장구분
					};
			} else if ("YDY1L003".equals(msgId) || "YDY3L003".equals(msgId)) {
				trtNm = "크레인작업계획";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsg" + msgId;
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_PTOP_PLNT_GP")) //조업공장구분
					};
			} else if ("YDC8L001".equals(msgId)) {
				trtNm = "그라인딩머신Carry-Out완료";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDC8L001";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
					};
			} else if ("YDY1L004".equals(msgId)) {  //수정 (2015.12.14) : 항만야드  By LeeJY
				trtNm = "크레인작업지시";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDY1L004";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MSG_GP"       )) //전문구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
					};
			} else if ("YDE7L004".equals(msgId)) {  //수정 (2015.12.14) : 항만야드  By LeeJY
				trtNm = "크레인작업지시";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDE7L004";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MSG_GP"       )) //전문구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
					};
			} 
			else if ("YDY1L006".equals(msgId) ){ //2020.06.26 추가 C2 스카핑 검사장 인터락 여부
				trtNm = "C2 스카핑 검사장 인터락 여부";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDY1L006";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"       )) //장비 ID 
						,slabUtils.trim(jrParam.getFieldString("C2CR_INTERLOCK_YN")) //인터락 여부
					};
			}  else if ("YDY1L007".equals(msgId) ){ //2020.06.26 추가 C2 스카핑 검사장 인터락 여부
				trtNm = "슬라브야드 인터락 여부";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDY1L007";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("YD_INTERLOCK_ID"       )) //장비 ID 
						,slabUtils.trim(jrParam.getFieldString("INTERLOCK_YN")) //인터락 여부
					};
			} else if ("YDT1L001".equals(msgId)){ //2022.01.13 추가 연주슬라브야드 전광판
				trtNm = "연주슬라브야드 전광판4문";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDT1L001";
				
			} else if ("YDT2L001".equals(msgId)){ //2021.12.31 추가 연주슬라브야드 전광판
				trtNm = "연주슬라브야드 전광판6문";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDT2L001";
				
			} else {
				throw new Exception("정의되지 않은 전문ID[" + msgId + "] 입니다.");
			}
			
			
			
			trtNm = trtNm + "(" + msgId + ") : ";
			
			JDTORecordSet jsRst = getRecordSet(jspeed_query_id, param);
				
			slabUtils.printLog(logId, trtNm + jsRst.size(), "DB");
			
			return jsRst;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/***************************************************************************
	 * L3 송신 전문 조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : L3전문조회
	 *      
	 *      @param String msgId
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getMsgL3(String msgId, JDTORecord jrParam) throws DAOException {
		String methodNm = "L3전문조회[SlabYdCommDAO.getMsgL3] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("YDCTJ033UD".equals(msgId) || "YDCTJ031UD".equals(msgId)) {
				trtNm = "장입진행실적(권상권하)";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDCTJ033UD";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_UP_DN_GP"     )) //권상권하구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_WR_DT"        )) //실적발생일시
					};
			} else if ("YDCTJ033TI".equals(msgId) || "YDCTJ031TI".equals(msgId)) {
				trtNm = "장입진행실적(Take-In)";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDCTJ033TI";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"       )) //재료번호
					};
			} else if ("YDCTJ034".equals(msgId)) {
				trtNm = "이송하차실적";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDCTJ034";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
					};
			} else if ("YDPRJ003".equals(msgId)) {
				trtNm = "후판재열재슬라브적치실적";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDPRJ003";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_WR_DT"        )) //불출일시
						,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
					};
			} else if ("YDPTJ001Mslab".equals(msgId)) {
				trtNm = "Slab이송완료실적(주편)";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDPTJ001Mslab";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_STL_NO")) //재료번호
					};
			} else if ("YDPTJ001Slab".equals(msgId)) {
				trtNm = "Slab이송완료실적(Slab)";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDPTJ001Slab";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_STL_NO")) //재료번호
					};
			} else if ("YDQMJ001".equals(msgId)) {
				trtNm = "M-Scarfing입측보급실적";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDQMJ001";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
					};
			} else if ("YDCSJ001".equals(msgId)) {
				trtNm = "그라인딩머신보급실적";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDCSJ001";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CRN_SCH_ID")) //야드크레인스케쥴ID
					};	
			} else if ("YDTSJ009".equals(msgId)) {
				trtNm = "소재차량하차개시";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDTSJ009";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_WR_DT"        )) //운송작업시작일시
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분(차량정지위치)
					};
			} else if ("YDTSJ010".equals(msgId)) {
				trtNm = "소재차량하차완료";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDTSJ010";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
					};
			} else if ("YDDMR013".equals(msgId)) {
				trtNm = "외판슬라브일품출하상차실적";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDDMR013";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
					};
			} else if ("YDDMR017".equals(msgId)) {
				trtNm = "외판슬라브출하상차완료";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDDMR017";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_CAR_SCH_ID")) //야드차량스케쥴ID
					};
			} else if ("YDDMR028".equals(msgId)) {
				trtNm = "차량입동지시";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDDMR028";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분(차량정지위치)
					};
			} else if ("M10YDLMJ1061A".equals(msgId)) {
				trtNm = "차량입동지시";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgM10YDLMJ1061A_PIDEV";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분(차량정지위치)
					};
			}  else if ("DMYDR039".equals(msgId)) {
				trtNm = "외판슬라브출하차량출발실적";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgDMYDR039";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_STL_NO")) //재료번호
					};
			}  else if ("YDCSJ003".equals(msgId)) {
				trtNm = "그라인딩머신TAKE-IN완료실적";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDCSJ003";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_MSLAB_NO")) //재료번호
						,slabUtils.trim(jrParam.getFieldString("V_SCRF_MCNO_GP")) //스카핑머신번호
					};
			} else {
				throw new Exception("정의되지 않은 전문ID[" + msgId + "] 입니다.");
			}

			trtNm = trtNm + "(" + msgId + ") : ";
			
			JDTORecordSet jsRst = getRecordSet(jspeed_query_id, param);
				
			slabUtils.printLog(logId, trtNm + jsRst.size(), "DB");
			
			return jsRst;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/***************************************************************************
	 * 대차관련 공통 처리
	 **************************************************************************/
	
	/**
	 *      [A] 오퍼레이션명 : 대차스케줄 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getTcarSch(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "대차스케줄[SlabYdCommDAO.getTcarSch] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("LdCmpl".equals(trtGp)) {
				trtNm = "대차상차완료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getTcarSchLdCmpl";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID(대차)
					};
			} else if ("UdCmpl".equals(trtGp)) {
				trtNm = "대차하차완료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getTcarSchUdCmpl";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID(대차)
					};
			} else if ("LevWo".equals(trtGp)) {
				trtNm = "공대차출발지시 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getTcarSchLevWo";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID(대차)
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID(대차)
					};
			} else if ("WbMtl".equals(trtGp)) {
				trtNm = "작업예약재료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getTcarSchWbMtl";

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
	 *      [A] 오퍼레이션명 : 대차스케줄 수정
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updTcarSch(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "대차스케줄[SlabYdCommDAO.updTcarSch] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("DelSch".equals(trtGp)) {
				trtNm = "대차스케줄(TB_YD_TCARSCH) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updTcarSchDelSch";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_TCAR_SCH_ID")) //야드대차스케쥴ID
					};
			} else if ("DelMtl".equals(trtGp)) {
				trtNm = "대차이송재료(TB_YD_TCARFTMVMTL) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updTcarSchDelMtl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER"      )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_TCAR_SCH_ID")) //야드대차스케쥴ID
					};
			} else if ("InitSch".equals(trtGp)) {
				trtNm = "대차스케줄(TB_YD_TCARSCH) 초기화";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updTcarSchInitSch";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER" )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID(대차)
					};
			} else if ("InitMtl".equals(trtGp)) {
				trtNm = "대차이송재료(TB_YD_TCARFTMVMTL) 초기화";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updTcarSchInitMtl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER" )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID(대차)
					};
			} else if ("InsSch".equals(trtGp)) {
				trtNm = "대차스케줄(TB_YD_TCARSCH) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updTcarSchInsSch";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_TCAR_SCH_ID"      )) //야드대차스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"            )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID"           )) //야드설비ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CAR_PROG_STAT"    )) //야드차량진행상태
						,slabUtils.trim(jrParam.getFieldString("V_YD_CARLD_WRK_BOOK_ID")) //야드상차작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_CARLD_LEV_LOC"    )) //야드상차출발위치
						,slabUtils.trim(jrParam.getFieldString("V_YD_CARLD_STOP_LOC"   )) //야드상차정지위치
						,slabUtils.trim(jrParam.getFieldString("V_YD_CARUD_STOP_LOC"   )) //야드하차정지위치
					};
			} else if ("UpdLdSch".equals(trtGp)) {
				trtNm = "대차스케줄(TB_YD_TCARSCH) 상차 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updTcarSchUpdLdSch";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CARUD_WRK_BOOK_ID")) //야드하차작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_TCAR_SCH_ID"      )) //야드대차스케쥴ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"            )) //수정자
					};
			} else if ("InsUdWb".equals(trtGp)) {
				trtNm = "작업예약(TB_YD_WRKBOOK) 대차하차 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updTcarSchInsUdWb";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CARUD_WRK_BOOK_ID")) //야드하차작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"            )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_CARLD_WRK_BOOK_ID")) //야드상차작업예약ID
					};
			} else if ("InsUdWbMtl".equals(trtGp)) {
				trtNm = "작업예약재료(TB_YD_WRKBOOKMTL) 대차하차 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updTcarSchInsUdWbMtl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_CARUD_WRK_BOOK_ID")) //야드하차작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"            )) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_YD_CARLD_WRK_BOOK_ID")) //야드상차작업예약ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_TCAR_SCH_ID"      )) //야드대차스케쥴ID
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
	 * Flex Push Server 전송Data 조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : Flex정보 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getFlex(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "Flex정보조회[SlabYdCommDAO.getFlex] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("Bed".equals(trtGp)) {
				trtNm = "Bed정보";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getFlexBed_PIDEV";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
					};
			} else if ("Mtl".equals(trtGp)) {
				trtNm = "재료정보";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getFlexMtl_PIDEV";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
						,slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO")) //야드적치Bed번호
					};
			} else if ("Crn".equals(trtGp)) {
				trtNm = "크레인정보";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getFlexCrn_PIDEV";

				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID
						,slabUtils.trim(jrParam.getFieldString("V_YD_EQP_ID")) //야드설비ID
					};
			} else if ("Car".equals(trtGp)) {
				trtNm = "차량정보";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getFlexCar";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
					};
			} else if ("Sum".equals(trtGp)) {
				trtNm = "합계";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getFlexSum";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP")) //야드적치열구분
					};
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			trtNm += " : ";

			return getRecordSet(jspeed_query_id, param);
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/***************************************************************************
	 * 기타 조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄기동구분 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getCrnSchStGp(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인스케줄기동구분조회[SlabYdCommDAO.getCrnSchStGp] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("WB".equals(trtGp)) {
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getCrnSchStGpWb";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_WBOOK_ID")) //야드작업예약ID
					};
			} else {
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getCrnSchStGp";

				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_YD_GP")) //야드구분
					};
			}

			return getRecordSet(jspeed_query_id, param);
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : UPDATE 메소드
	 * 
	 * @param  JDTORecord inRec 		parameter record
	 *         String     queryId   	QueryId 
	 *         String     logId   	 
	 *         String     mthdNm   	 
	 *         String     trtNm   	 
	 * @return int        execution count
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int update(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException {
		
		String methodNm = trtNm + "[YdCommDAO.update] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
			slabUtils.printLog(logId, trtNm + "[YdCommDAO.update] 결과 건수: " + intRtnVal , "DB");
			
		} catch (Exception e) {

			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
		return intRtnVal;
	} 
	
	
	/**
	 *      [A] 오퍼레이션명 : SELECT 메소드
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         String        queryId    QueryId 
	 *         String     	 logId   	 
	 *         String     	 mthdNm   	 
	 *         String     	 trtNm   	 
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public JDTORecordSet select(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException {
		
		String methodNm = trtNm + "[YdCommDAO.select] < " + mthdNm;
		
		JDTORecord recPara = null;	
		
		try {
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			JDTORecordSet rsTemp = getRecordSet(recPara);
			
			slabUtils.printLog(logId, "조회[YdCommDAO.select] 결과 건수: " + rsTemp.size() , "DB");
			
			return rsTemp;
			
		} catch (Exception e) {
			
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : INSERT 메소드
	 * 
	 * @param  JDTORecord inRec 		parameter record
	 *         String     queryId   	QueryId 
	 *         String     logId   	 
	 *         String     mthdNm   	 
	 *         String     trtNm   	 
	 * @return int        execution count
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insert(JDTORecord inRec, String queryId, String logId, String mthdNm, String trtNm) throws DAOException, JDTOException {
		
		String methodNm = trtNm + "[YdCommDAO.insert] < " + mthdNm;
		
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = conversionFieldname(inRec, 0);
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", queryId);
			//query execute
			intRtnVal = trtProcess(recPara);
			
			slabUtils.printLog(logId, trtNm + "[YdCommDAO.insert] 결과 건수: " + intRtnVal , "DB");
			
		} catch (Exception e) {

			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
		return intRtnVal;	
	}
	
	/**
	 *      [A] 오퍼레이션명 : conversionFieldname 
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         int intGp             // 구분(0:"V_" 추가, 1:"V_" 제거
	 * @return JDTORecord			 // 필드명을 변환한 결과레코드
	 * @throws JDTOException 
	 */
	public JDTORecord conversionFieldname(JDTORecord recPara, int intGp) throws JDTOException {
		JDTORecord recRtnVal = JDTORecordFactory.getInstance().create();
		String szFieldName = null;
		Iterator itrFieldName = null;
		
		//필드명을 가져온다.
		itrFieldName = recPara.iterateName();
		
		//필드명 갯수만큼 루프를 돈다.
		while(itrFieldName.hasNext()) {
			
			szFieldName = (String)itrFieldName.next();
			//"V_" 추가
			if (intGp == 0) {
				recRtnVal.setField("V_" + szFieldName, recPara.getField(szFieldName));
			//"V_" 제거
			} else {
				recRtnVal.setField(szFieldName.substring(2), recPara.getField(szFieldName));
			}
		}
		
		return recRtnVal ;
	}
	
	/***************************************************************************
	 * 신규로직 공통 Check
	 **************************************************************************/
	/**
	 *      [A] 오퍼레이션명 :  신규시스템 적용 여부
	 *      -- 
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String slabApplyYn(String FieldName) throws DAOException {
		String methodNm = "신규시스템 적용 여부[slabApplyYn]" ;
		String logId = "";
		String APPLY_YN  = "N";

		try {
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			JDTORecordSet jsApplyYNChk = this.select(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getSlabApplyYnDualSql", logId, methodNm, "열정보 Read"); 

			APPLY_YN     = slabUtils.trim(jsApplyYNChk.getRecord(0).getFieldString(FieldName));
            
			

			return APPLY_YN;
		} catch (DAOException e) {
			
			return APPLY_YN;
		} catch (Exception e) {
			return APPLY_YN;
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 :  이송지시 상차지 체크
	 *      
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public String stlFrtoMoveChk(JDTORecord jrParam) throws DAOException {
		String methodNm = "권하위치와 이송지시 상차지 비교 [stlFrtoMoveChk]" ;
		String logId = "";
		String CHK_YN  = "N"; //N : 권하위치와 이송지시 상차지가 동일(ERROR)
		String SPOS_WLOC_CD = "X";
		String YD_GP1 = "X";
		String YD_GP2 = "X";

		try {
					
			JDTORecordSet stlFrtoMoveChk = this.select(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.stlFrtoMoveChk", logId, methodNm, "이송지시 상차지와 비교"); 

			CHK_YN     = slabUtils.trim(stlFrtoMoveChk.getRecord(0).getFieldString("CHK_YN"));
			SPOS_WLOC_CD     = slabUtils.trim(stlFrtoMoveChk.getRecord(0).getFieldString("SPOS_WLOC_CD"));
			YD_GP1     = slabUtils.trim(stlFrtoMoveChk.getRecord(0).getFieldString("YD_GP1"));
			YD_GP2     = slabUtils.trim(stlFrtoMoveChk.getRecord(0).getFieldString("YD_GP2"));
			slabUtils.printLog(logId, "권하위치와 이송지시 상차지 비교 결과 : 상차지[" + SPOS_WLOC_CD + "] YD_GP1[" + YD_GP1 + "] YD_GP2[" + YD_GP2 + "] 결과 : " + CHK_YN , "DB");
			return CHK_YN;
		} catch (DAOException e) {
			
			return CHK_YN;
		} catch (Exception e) {
			return CHK_YN;
		}
	}

}
