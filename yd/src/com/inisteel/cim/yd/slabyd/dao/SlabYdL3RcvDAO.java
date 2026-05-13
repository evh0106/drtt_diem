/**
 * @(#)SlabYdL3RcvDAO
 *
 * @version          V1.00
 * @author           허철호
 * @date             2012/11/22
 * 
 * @description      Slab야드 L3수신 DAO
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
 * [A] 클래스명 : Slab야드 L3수신 DAO
 *
 */

public class SlabYdL3RcvDAO extends DBAssistantDAO {

	private YdSlabUtils slabUtils = new YdSlabUtils();

	/***************************************************************************
	 * 연주조업(CS)
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 연주정정실적 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getCSYDJ003(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "연주정정실적[SlabYdL3RcvDAO.getCSYDJ003] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("WM".equals(trtGp)) {
				trtNm = "이적작업예약재료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL3RcvDAO.getCSYDJ003WM";
				param = new Object[] {
						slabUtils.trim(jrParam.getFieldString("V_STL_NO")) //재료번호
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

	
	/***************************************************************************
	 * 생산통제(CT)
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : (후판,C열연)압연지시확정 수정
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updCTYDJ031(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "압연지시확정[SlabYdL3RcvDAO.updCTYDJ031] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("StockOld".equals(trtGp)) {
				trtNm = "저장품(TB_YD_STOCK) 기존지시 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL3RcvDAO.updCTYDJ031StockOld";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_PTOP_PLNT_GP")) //조업공장구분
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"    )) //수정자
					};
			} else if ("StockNew".equals(trtGp)) {
				trtNm = "저장품(TB_YD_STOCK) 신규지시 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL3RcvDAO.updCTYDJ031StockNew";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_PTOP_PLNT_GP" )) //조업공장구분
						,slabUtils.trim(jrParam.getFieldString("V_CHG_WO_FR_PNT")) //장입지시FromPoint
						,slabUtils.trim(jrParam.getFieldString("V_CHG_WO_TO_PNT")) //장입지시ToPoint
						,slabUtils.trim(jrParam.getFieldString("V_PTOP_PLNT_GP" )) //조업공장구분
						,slabUtils.trim(jrParam.getFieldString("V_CHG_WO_FR_PNT")) //장입지시FromPoint
						,slabUtils.trim(jrParam.getFieldString("V_CHG_WO_TO_PNT")) //장입지시ToPoint
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

	
	/***************************************************************************
	 * 공정계획(PM)
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 슬라브충당실적 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getPMYDJ001(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "슬라브충당실적[SlabYdL3RcvDAO.getPMYDJ001] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("Stl".equals(trtGp)) {
				trtNm = "충당재료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL3RcvDAO.getPMYDJ001Stl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_WRK_HDS_DD")) //작업계상일자1
						,slabUtils.trim(jrParam.getFieldString("V_STEP_NO"   )) //차수1
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
	 *      [A] 오퍼레이션명 : 슬라브이송지시 조회
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getPMYDJ002(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "슬라브이송지시[SlabYdL3RcvDAO.getPMYDJ002] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("Stl".equals(trtGp)) {
				trtNm = "이송재료 조회";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL3RcvDAO.getPMYDJ002Stl";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_FRTOMOVE_WORD_DATE")) //이송작업지시일자1
						,slabUtils.trim(jrParam.getFieldString("V_TRANSWORD_SEQNO"   )) //이송지시차수1
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
	 *      [A] 오퍼레이션명 : 슬라브이송지시 수정
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updPMYDJ002(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "슬라브이송지시[SlabYdL3RcvDAO.updPMYDJ002] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("FM".equals(trtGp)) {
				trtNm = "소재이송지시(TB_PT_STLFRTOMOVE) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL3RcvDAO.updPMYDJ002FM";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_FRTOMOVE_WORD_DATE")) //이송작업지시일자1
						,slabUtils.trim(jrParam.getFieldString("V_TRANSWORD_SEQNO"   )) //이송지시차수1
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
	 * 출하관리(DM)
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 외판슬라브출하완료 등록
	 *      
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updDMYDR029(JDTORecord jrParam) throws DAOException {
		String methodNm = "외판슬라브출하완료[SlabYdL3RcvDAO.updDMYDR029] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "저장품(TB_YD_STOCK) 등록 : ";

		try {
			String jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL3RcvDAO.updDMYDR029ST";

			Object[] param = new Object[] {
					 slabUtils.trim(jrParam.getFieldString("V_MODIFIER")) //수정자
					,slabUtils.trim(jrParam.getFieldString("V_STL_NO"  )) //재료번호
				};
			
			int trtCnt = trtProcess(jspeed_query_id, param);

			slabUtils.printLog(logId, trtNm + trtCnt, "DB");

			return trtCnt;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 외판슬라브반품 등록
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updDMYDR032(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "외판슬라브반품[SlabYdL3RcvDAO.updDMYDR032] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("ST".equals(trtGp)) {
				trtNm = "저장품(TB_YD_STOCK) 등록";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL3RcvDAO.updDMYDR032ST";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_STL_NO"            )) //재료번호
						,slabUtils.trim(jrParam.getFieldString("V_ORD_GP"            )) //수주구분
						,slabUtils.trim(jrParam.getFieldString("V_CUST_CD"           )) //고객코드
						,slabUtils.trim(jrParam.getFieldString("V_DEST_CD"           )) //목적지코드
						,slabUtils.trim(jrParam.getFieldString("V_DEST_TEL_NO"       )) //목적지전화번호
						,slabUtils.trim(jrParam.getFieldString("V_DIST_SHIPASSIGN_GP")) //출하배선지시구분
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"          )) //수정자
					};
			} else if ("CS".equals(trtGp)) {
				trtNm = "차량스케줄(TB_YD_CARSCH) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL3RcvDAO.updDMYDR032CS";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER")) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"  )) //재료번호
					};
			} else if ("CM".equals(trtGp)) {
				trtNm = "차량이송재료(TB_YD_CARFTMVMTL) 삭제";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL3RcvDAO.updDMYDR032CM";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER")) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"  )) //재료번호
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
	 *      [A] 오퍼레이션명 : 외판슬라브출하차량출발실적 적치열 조회
	 *      
	 *      @param JDTORecord jrParam
	 *      @return JDTORecordSet
	 *      @throws DAOException
	*/
	public JDTORecordSet getDMYDR039(JDTORecord jrParam) throws DAOException {
		String methodNm = "외판슬라브출하차량출발실적[SlabYdL2RcvDAO.getDMYDR039] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "적치열 조회 : ";

		try {
			String jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL3RcvDAO.getDMYDR039SC";

			Object[] param = new Object[] {
					 slabUtils.trim(jrParam.getFieldString("V_WLOC_CD"  )) //개소코드
					,slabUtils.trim(jrParam.getFieldString("V_YD_PNT_CD")) //야드포인트코드
					,slabUtils.trim(jrParam.getFieldString("V_CAR_NO"   )) //차량번호
					,slabUtils.trim(jrParam.getFieldString("V_CARD_NO"  )) //카드번호
				};
			
			return getRecordSet(jspeed_query_id, param);
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, trtNm + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 외판슬라브출하차량출발실적 수정
	 *      
	 *      @param String trtGp
	 *      @param JDTORecord jrParam
	 *      @return int
	 *      @throws DAOException
	*/
	public int updDMYDR039(String trtGp, JDTORecord jrParam) throws DAOException {
		String methodNm = "외판슬라브출하차량출발실적[SlabYdL3RcvDAO.updDMYDR039] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String trtNm = "";

		try {
			String jspeed_query_id = "";
			Object[] param = null;

			if ("SC".equals(trtGp)) {
				trtNm = "적치열(TB_YD_STKCOL) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL3RcvDAO.updDMYDR039SC";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_STL_NO"            )) //재료번호
						,slabUtils.trim(jrParam.getFieldString("V_ORD_GP"            )) //수주구분
						,slabUtils.trim(jrParam.getFieldString("V_CUST_CD"           )) //고객코드
						,slabUtils.trim(jrParam.getFieldString("V_DEST_CD"           )) //목적지코드
						,slabUtils.trim(jrParam.getFieldString("V_DEST_TEL_NO"       )) //목적지전화번호
						,slabUtils.trim(jrParam.getFieldString("V_DIST_SHIPASSIGN_GP")) //출하배선지시구분
						,slabUtils.trim(jrParam.getFieldString("V_MODIFIER"          )) //수정자
					};
			} else if ("SB".equals(trtGp)) {
				trtNm = "적치Bed(TB_YD_STKBED) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL3RcvDAO.updDMYDR039SB";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER")) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"  )) //재료번호
					};
			} else if ("SL".equals(trtGp)) {
				trtNm = "적치단(TB_YD_STKLYR) 수정";
				jspeed_query_id = "com.inisteel.cim.yd.slabyd.dao.SlabYdL3RcvDAO.updDMYDR039SL";
				param = new Object[] {
						 slabUtils.trim(jrParam.getFieldString("V_MODIFIER")) //수정자
						,slabUtils.trim(jrParam.getFieldString("V_STL_NO"  )) //재료번호
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

}
