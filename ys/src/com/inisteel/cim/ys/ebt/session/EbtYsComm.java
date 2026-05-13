/**
 * @(#)EbtYsComm
 *
 * @version          V1.00
 * @author           김현규
 * @date             2025/07/01
 *
 * @description      특수강대형옥내야드 공통 처리 EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2025/07/01                         김현규      최초 등록
 */
package com.inisteel.cim.ys.ebt.session;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.common.util.YsQueryIFEbt;

import xlib.cmc.GridData;
import xlib.cmc.GridHeader;
import xlib.cmc.OperateGridData;


/**
 *      [A] 클래스명 : BILLET 야드 공통 처리
 *
*/

public class EbtYsComm implements YsQueryIFEbt {
	
	private YsCommUtils commUtils = new YsCommUtils();
	private YsCommDAO commDao = new YsCommDAO();	
	
	/***************************************************************************
	 * 공통 Check
	 **************************************************************************/
	
	/**
	 *      [A] 오퍼레이션명 : 설비상태 체크
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord chkEqpStat(JDTORecord rcvMsg) throws DAOException {
		String methodNm  = "설비상태체크[EbtYsComm.chkEqpStat] < " + rcvMsg.getResultMsg();
		String logId     = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			jrRtn.setField("YD_L3_HD_RS_CD", "EQ99");									//야드L3처리결과코드(Error)
			jrRtn.setField("YD_L3_MSG"     , "오류:설비상태Check 예상치 못한 오류");	//야드L3MESSAGE(40Byte)

			//수신 항목 값
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));		//야드설비ID
			String ydL3HdRsCd = "";														//야드L3처리결과코드
			String ydL3Msg    = "";														//야드L3MESSAGE
			
			
			/**********************************************************
			* 1. 설비ID 체크
			**********************************************************/
			if( "".equals(ydEqpId) ) {
				ydL3HdRsCd = "EQ01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if( ydEqpId.length() < 6 ) {
				ydL3HdRsCd = "EQ02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			}
			
			if( !"".equals(ydL3Msg) ) {
				jrRtn.setField("YD_L3_HD_RS_CD", ydL3HdRsCd);	//야드L3처리결과코드
				jrRtn.setField("YD_L3_MSG"     , ydL3Msg   );	//야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			
			/**********************************************************
			* 2. 설비상태(설비고장, OFF-LINE) 체크
			**********************************************************/
			JDTORecord jrParam  = commUtils.getParam(logId, methodNm, "");
			jrParam.setField("YD_EQP_ID", ydEqpId);				//야드설비ID

			JDTORecordSet jsChk = commDao.select(jrParam, getStatEqp, logId, methodNm, "설비상태 Check"); 

			String ydEqpStat    = "";							//야드설비상태
			String ydEqpWrkMode = "";							//야드설비작업Mode

			if( jsChk.size() > 0 ) {
				ydEqpStat    = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT"    ));
				ydEqpWrkMode = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE"));
			}
			
			if( "".equals(ydEqpStat) ) {
				ydL3HdRsCd = "EQ03";
				ydL3Msg    = "오류:크레인[" + ydEqpId + "] 정보 없음";
			} else if( "B".equals(ydEqpStat) ) {
				ydL3HdRsCd = "EQ04";
				ydL3Msg    = "오류:크레인[" + ydEqpId + "] 고장";
			} else if( !"1".equals(ydEqpWrkMode) ) {
				ydL3HdRsCd = "EQ05";
				ydL3Msg    = "오류:크레인[" + ydEqpId + "] Off-Line";
			}
			
			if( !"".equals(ydL3Msg) ) {
				jrRtn.setField("YD_L3_HD_RS_CD", ydL3HdRsCd);	//야드L3처리결과코드
				jrRtn.setField("YD_L3_MSG"     , ydL3Msg   );	//야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			jrRtn.setField("YD_L3_HD_RS_CD", "0000");			//야드L3처리결과코드
			jrRtn.setField("YD_L3_MSG"     , ""    );			//야드L3MESSAGE
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return jrRtn;
		} catch (Exception e) {
			return jrRtn;
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 스케줄코드 체크
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord chkSchCd(JDTORecord rcvMsg) {
		String methodNm  = "스케줄코드 체크[EbtYsComm.chkSchCd] < " + rcvMsg.getResultMsg();
		String logId     = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			jrRtn.setField("YD_L3_HD_RS_CD", "SC99");									//야드L3처리결과코드(Error)
			jrRtn.setField("YD_L3_MSG"     , "오류:스케줄코드Check 예상치 못한 오류");	//야드L3MESSAGE(40Byte)
			
			//수신 항목 값
			String ydSchCd    = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));		//야드스케쥴코드
			String ydL3HdRsCd = "";														//야드L3처리결과코드
			String ydL3Msg    = "";														//야드L3MESSAGE
			
			
			/**********************************************************
			* 1. 스케줄코드 체크
			**********************************************************/
			if( "".equals(ydSchCd) ) {
				ydL3HdRsCd = "SC01";
				ydL3Msg    = "오류:스케줄코드 없음";
			} else if( ydSchCd.length() < 8 ) {
				ydL3HdRsCd = "SC02";
				ydL3Msg    = "오류:스케줄코드[" + ydSchCd + "] 이상";
			}
			
			if( !"".equals(ydL3Msg) ) {
				jrRtn.setField("YD_L3_HD_RS_CD", ydL3HdRsCd);	//야드L3처리결과코드
				jrRtn.setField("YD_L3_MSG"     , ydL3Msg   );	//야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 2. 크레인스케줄 상태 Check
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, "");
			jrParam.setField("YD_SCH_CD", ydSchCd);				//야드스케쥴코드
			
			//야드스케쥴금지유무 조회
			JDTORecordSet jsChk = commDao.select(jrParam, getStatSchCd, logId, methodNm, "야드스케쥴금지유무 조회"); 
			
			String ydSchProhExn = "";							//야드스케쥴금지유무
			
			if( jsChk.size() > 0 ) {
				ydSchProhExn  = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_PROH_EXN"));
			}

			if( "".equals(ydSchProhExn) ) {
				ydL3HdRsCd = "SC03";
				ydL3Msg    = "오류:스케쥴코드[" + ydSchCd + "] 정보 없음";
			} else if( "Y".equals(ydSchProhExn) ) {
				ydL3HdRsCd = "SC04";
				ydL3Msg    = "오류:스케쥴코드[" + ydSchCd + "] 기동금지";
			}
			
			if (!"".equals(ydL3Msg)) {
				jrRtn.setField("YD_L3_HD_RS_CD", ydL3HdRsCd);	//야드L3처리결과코드
				jrRtn.setField("YD_L3_MSG"     , ydL3Msg   );	//야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			jrRtn.setField("YD_L3_HD_RS_CD", "0000"); 			//야드L3처리결과코드
			jrRtn.setField("YD_L3_MSG"     , ""    ); 			//야드L3MESSAGE

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return jrRtn;
		} catch (Exception e) {
			return jrRtn;
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 스케줄코드, 크레인 체크
	 *
	 *      @param JDTORecord jrParam
	 *      @return JDTORecord jrRtn
	 *      @throws DAOException
	*/
	public JDTORecord chkSchCdEqp(JDTORecord jrParam) {
		String methodNm = "스케줄코드, 크레인 체크[EbtYsComm.chkSchCdEqp] < " + jrParam.getResultMsg();
		String logId    = jrParam.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			//1. 스케줄코드 체크
			String ydSchCd = commUtils.trim(jrParam.getFieldString("YD_SCH_CD")); //스케줄코드
			if( "".equals(ydSchCd) ) {
				throw new Exception("스케쥴코드 없음");
			} else if( ydSchCd.length() < 8 ) {
				throw new Exception("스케쥴코드[" + ydSchCd + "] 이상");
			}
			
			//2. 야드스케줄금지유무 조회 및 체크
			JDTORecordSet jsChk = commDao.select(jrParam, getStatSchCd, logId, methodNm, "야드스케쥴금지유무 조회");
			
			String ydSchProhExn = "";  //야드스케쥴금지유무
			String ydEqpId      = "";  //야드설비ID(작업크레인)

			if (jsChk.size() > 0) {
				ydSchProhExn = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_PROH_EXN"));
				ydEqpId      = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_ID"      ));
			}
			
			//일단 작업예약이 생성되어야함으로 기타 에러 체크 패스. 화면에서 "스케줄점검" 기능을 제공하여 확인 가능하게 함. 
//			if ("".equals(ydSchProhExn)) {
//				throw new Exception("스케쥴코드[" + ydSchCd + "] 정보 없음");
//			} else if ("Y".equals(ydSchProhExn)) {
//				throw new Exception("스케쥴코드[" + ydSchCd + "] 기동금지");
//			} else if ("".equals(ydEqpId)) {
//				throw new Exception("스케쥴코드[" + ydSchCd + "] 작업가능 크레인 없음");
//			}
			if ("".equals(ydEqpId)) {
				throw new Exception("스케줄코드[" + ydSchCd + "] 작업가능 크레인 없음");
			}
			
			commUtils.printLog(logId, methodNm, "S-");
			
			return jsChk.getRecord(0);
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 스케줄코드 조회
	 *
	 *      @param JDTORecord jrParam
	 *      @return JDTORecord jrRtn
	 *      @throws DAOException
	*/
	public JDTORecord getSchCd(JDTORecord jrParam) {
		String methodNm = "스케줄코드 조회[EbtYsComm.getSchCd] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String ydSchCd     = commUtils.trim(jrParam.getFieldString("YD_SCH_CD"     )); //야드스케쥴코드
			String ydStkColGp  = commUtils.trim(jrParam.getFieldString("YD_STK_COL_GP" )); //야드적치열구분
			String ydStkBedNo  = commUtils.trim(jrParam.getFieldString("YD_STK_BED_NO" )); //야드적치Bed번호
			String ydSchWhioGp = commUtils.trim(jrParam.getFieldString("YD_SCH_WHIO_GP")); //야드스케쥴입출고구분
			
			//야드스케쥴코드 값이 없으면
			if (ydSchCd.length() < 7) {
				if (ydStkColGp.length() < 6) {
					throw new Exception("적치열구분(YD_STK_COL_GP) 이상 : [" + ydStkColGp + "]");
				} else if ("".equals(ydStkBedNo)) {
					throw new Exception("적치Bed번호(YD_STK_BED_NO) 없음");
				} else if (!"L".equals(ydSchWhioGp) && !"U".equals(ydSchWhioGp) && !"M".equals(ydSchWhioGp)) {
					//L:입고(수입,하차,Take-Out,Carry-Out), U:출고(불출,보급,상차,Take-In,Carry-In), M:이적
					throw new Exception("스케쥴입출고구분(YD_SCH_WHIO_GP) 이상 : [" + ydSchWhioGp + "]");
				}
	
				String ydGp       = ydStkColGp.substring(0, 1); //야드구분
				String ydEqpGp    = ydStkColGp.substring(2, 4); //야드설비구분
				String ydStkColNo = ydStkColGp.substring(4, 6); //야드적치열번호
				String ydSchDivGp = "M";	//야드스케쥴분할구분(중간)

				//C연주 Pickup Bed 적치열구분과 스케줄코드 체계가 다름
				if ("A".equals(ydGp) && "PU".equals(ydEqpGp)) {
					if ("PA".equals(ydStkColNo)) {
						ydStkColNo = "10";
					} else if ("PB".equals(ydStkColNo)) {
						ydStkColNo = "11";
					} else {
						ydStkColNo = "0" + ydStkColNo.substring(1, 2);
					}

					//C연주 장입 Pickup Bed 적치열구분과 스케줄코드 체계가 다름 : ADPU01,ACPU02,ADPU03, AAPU04, ABPU06, ACPU07
					if ("01".equals(ydStkColNo) ||"02".equals(ydStkColNo) ||"03".equals(ydStkColNo) || "04".equals(ydStkColNo) || "06".equals(ydStkColNo)) {
						if ("01".equals(ydStkBedNo) || "02".equals(ydStkBedNo) || "03".equals(ydStkBedNo) || "04".equals(ydStkBedNo)) {
							ydSchDivGp = "L"; //왼쪽
						} else { 
							ydSchDivGp = "R"; //오른쪽
						}
					}else if ("07".equals(ydStkColNo) || "08".equals(ydStkColNo)) {
						if ("01".equals(ydStkBedNo) || "02".equals(ydStkBedNo) || "03".equals(ydStkBedNo)) {
							ydSchDivGp = "L"; //왼쪽
						} else { 
							ydSchDivGp = "R"; //오른쪽
						}
					}else if ("05".equals(ydStkColNo)) {
						if ("01".equals(ydStkBedNo) || "02".equals(ydStkBedNo)) {
							ydSchDivGp = "L"; //왼쪽
						} else { 
							ydSchDivGp = "R"; //오른쪽
						}
					}
				}    
				
				ydSchCd = ydStkColGp.substring(0, 4) + ydStkColNo + ydSchWhioGp + ydSchDivGp;
				
				jrParam.setField("YD_SCH_CD", ydSchCd); //야드스케쥴코드
			} else if (ydSchCd.length() == 7) {
				String ydSchDivGp = "M";	//야드스케쥴분할구분(중간)
	
				//C연주 장입 Pickup Bed 적치열구분과 스케줄코드 체계가 다름 : ADPU01,ACPU02,ADPU03, AAPU04, ABPU06, ACPU07
				if (ydSchCd.startsWith("ADPU01") ||ydSchCd.startsWith("ADPU03") ||ydSchCd.startsWith("ACPU02") || ydSchCd.startsWith("AAPU04") || ydSchCd.startsWith("ABPU06")) {
					if ("01".equals(ydStkBedNo) || "02".equals(ydStkBedNo) || "03".equals(ydStkBedNo) || "04".equals(ydStkBedNo)) {
						ydSchDivGp = "L"; //왼쪽
					} else {
						ydSchDivGp = "R"; //오른쪽
					}
				}else if (ydSchCd.startsWith("ACPU07") || ydSchCd.startsWith("ADPU08")) {
					if ("01".equals(ydStkBedNo) || "02".equals(ydStkBedNo) || "03".equals(ydStkBedNo)) {
						ydSchDivGp = "L"; //왼쪽
					} else {
						ydSchDivGp = "R"; //오른쪽
					}
				}else if (ydSchCd.startsWith("ABPU05")) {
					if ("01".equals(ydStkBedNo) || "02".equals(ydStkBedNo)) {
						ydSchDivGp = "L"; //왼쪽
					} else {
						ydSchDivGp = "R"; //오른쪽
					}
				}
				
				ydSchCd = ydSchCd + ydSchDivGp;  
				
				jrParam.setField("YD_SCH_CD", ydSchCd); //야드스케쥴코드
			}
			
			//야드스케쥴금지유무 조회
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getStatSchCd", logId, methodNm, "야드스케쥴금지유무 조회"); 
			

			String ydSchProhExn = "";  //야드스케쥴금지유무
			String ydEqpId      = "";  //야드설비ID(작업크레인)

			if (jsChk.size() > 0) {
				ydSchProhExn = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_PROH_EXN"));
				ydEqpId      = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_ID"      ));
			}

			if ("".equals(ydSchProhExn)) {
				throw new Exception("스케쥴코드[" + ydSchCd + "] 정보 없음");
			} else if ("Y".equals(ydSchProhExn)) {
				throw new Exception("스케쥴코드[" + ydSchCd + "] 기동금지");
			} else if ("".equals(ydEqpId)) {
				throw new Exception("스케쥴코드[" + ydSchCd + "] 작업가능 크레인 없음");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jsChk.getRecord(0);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인사양 Check
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrUpLot
	 *      @return String
	 *      @throws DAOException
	*/
	public String chkCrnSpec(JDTORecord jrCrnSpec) throws DAOException {
		String methodNm = "크레인사양Check[EbtYsComm.chkCrnSpec] < " + jrCrnSpec.getResultMsg();
		String logId = jrCrnSpec.getResultCode();

		try {
			String crnSpecOvGp = ""; //결과

			//크레인사양
			int   ydWrkAbleSh   = Integer.parseInt(commUtils.nvl(jrCrnSpec.getFieldString("YD_WRK_ABLE_SH"   ),"0"));	//야드작업가능매수
			float ydWrkAbleWt   = Integer.parseInt(commUtils.nvl(jrCrnSpec.getFieldString("YD_WRK_ABLE_WT"   ),"0"));	//야드작업가능중량
			float ydCrnTongH    = Float.parseFloat(commUtils.nvl(jrCrnSpec.getFieldString("YD_CRN_TONG_H"    ),"0"));	//야드크레인집게높이
			float ydCrnTongWTol = Float.parseFloat(commUtils.nvl(jrCrnSpec.getFieldString("YD_CRN_TONG_W_TOL"),"0"));	//야드크레인집게폭허용오차
			//점검할 값
			int   mtlShSum = Integer.parseInt(commUtils.nvl(jrCrnSpec.getFieldString("MTL_SH_SUM"),"0"));	//재료매수합
			int   mtlWtSum = Integer.parseInt(commUtils.nvl(jrCrnSpec.getFieldString("MTL_WT_SUM"),"0"));	//재료중량합
			float mtlTSum  = Float.parseFloat(commUtils.nvl(jrCrnSpec.getFieldString("MTL_T_SUM" ),"0"));	//재료두께합
			float mtlWMax  = Float.parseFloat(commUtils.nvl(jrCrnSpec.getFieldString("MTL_W_MAX" ),"0"));	//재료폭최대
			float mtlW     = Float.parseFloat(commUtils.nvl(jrCrnSpec.getFieldString("MTL_W"     ),"0"));	//재료폭

			if (mtlShSum > ydWrkAbleSh) {
				crnSpecOvGp = "SH";	//매수 초과
			} else if (mtlWtSum > ydWrkAbleWt) {
				crnSpecOvGp = "WT";	//중량 초과
			} else if (mtlTSum > ydCrnTongH) {
				crnSpecOvGp = "T";	//두께 초과
			} else if ((mtlWMax - mtlW) > ydCrnTongWTol) {
				crnSpecOvGp = "W";	//폭 허용오차 초과
			}

			return crnSpecOvGp;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/***************************************************************************
	 * 공통 전문
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 전문 조회
	 *
	 *      @param String JDTORecord rcvMsg
	 *      @return String JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getCrnSchMsg(JDTORecord jrParam) {
 		/***************************************************************************
		 * 스케줄 기동시 사용: procCrnWrkBookMgtStart
		 **************************************************************************/
		
		String methodNm = "크레인스케줄전문조회[EbtYsComm.getCrnSchMsg] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn  = null;
			String currDate   = commUtils.getDateTime14();									//현재시각
			String ydGp       = commUtils.trim(jrParam.getFieldString("YD_GP"        ));	//야드구분
			String ydWbookId  = commUtils.trim(jrParam.getFieldString("YD_WBOOK_ID"  ));	//야드작업예약ID
			String ydSchCd    = commUtils.trim(jrParam.getFieldString("YD_SCH_CD"    ));	//야드스케쥴코드
			String ydEqpId    = commUtils.trim(jrParam.getFieldString("YD_EQP_ID"    ));	//야드설비ID
			String ydSchStGp  = commUtils.trim(jrParam.getFieldString("YD_SCH_ST_GP" ));	//야드스케쥴기동구분
			String ydSchReqGp = commUtils.trim(jrParam.getFieldString("YD_SCH_REQ_GP"));	//야드스케쥴요청구분
			String modifier   = commUtils.trim(jrParam.getFieldString("MODIFIER"     ));	//수정자

			if ("".equals(ydWbookId) && "".equals(ydSchCd) && "".equals(ydEqpId)) {
				commUtils.printLog(logId, "크레인스케줄 기동을 위한 정보가 없습니다.", "SL");
				return null;
			}

			//크레인스케줄기동구분 조회
			if (!"".equals(ydWbookId) && ("".equals(ydSchCd) || "".equals(ydEqpId))) {
				
				jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
				
				JDTORecordSet jsChk = commDao.select(jrParam, getCrnSchStartGp, logId, methodNm, "크레인스케줄기동구분 조회");

				if (jsChk.size() > 0) {
					ydGp    = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_GP"    ));	//야드구분
					ydSchCd = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_CD"));	//야드스케쥴코드
					ydEqpId = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_ID"));	//야드설비ID
				} else {
					commUtils.printLog(logId, "작업예약ID[" + ydWbookId + "]의 정보가 없어 크레인스케줄을 기동할 수 없습니다.", "SL");
					return null;
				}
				
			} else {
				
				if ("".equals(ydGp)) {
					if (!"".equals(ydSchCd)) {
						ydGp = ydSchCd.substring(0, 1);
					} else if (!"".equals(ydEqpId)) {
						ydGp = ydEqpId.substring(0, 1);
					}
				}

				jrParam.setField("YD_GP", ydGp); //야드구분
			}
			
			commUtils.printLog(logId, "[작업예약ID:" + ydWbookId + ", 스케쥴코드:" + ydSchCd + ", 설비ID:" + ydEqpId + ", ydSchStGp:" + ydSchStGp + ", ydSchReqGp:" + ydSchReqGp + ", modifier:" + modifier + "]", "SL");

			
			//크레인스케줄 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, jrParam.getResultMsg(), modifier);
			
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name			
			// 크레인스케줄 기동
			jrYdMsg.setField("JMS_TC_CD"		 , "YSYSJ602"); //대형옥내 크레인스케줄Main
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate  ); //JMSTC생성일시
			jrYdMsg.setField("YD_WBOOK_ID"       , ydWbookId ); //야드작업예약ID
			jrYdMsg.setField("YD_SCH_CD"         , ydSchCd   ); //야드스케쥴코드
			jrYdMsg.setField("YD_EQP_ID"         , ydEqpId   ); //야드설비ID
			jrYdMsg.setField("YD_SCH_ST_GP"      , ydSchStGp ); //야드스케쥴기동구분
			jrYdMsg.setField("YD_SCH_REQ_GP"     , ydSchReqGp); //야드스케쥴요청구분
			
			//작업지시 등 전송할 전문이 있으면 받아서 전송
			jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업실적응답(YSM5L004) 전문 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getYSN2L004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업실적응답 조회[EbtYsComm.getYSN2L004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			//수신 항목 값
			String msgId      = ""; //전문ID
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID
			String ydL2WrGp   = commUtils.trim(rcvMsg.getFieldString("YD_L2_WR_GP"   )); //야드L2실적구분
			String ydL3HdRsCd = commUtils.trim(rcvMsg.getFieldString("YD_L3_HD_RS_CD")); //야드L3처리결과코드
			String ydL3Msg    = commUtils.trim(rcvMsg.getFieldString("YD_L3_MSG"     )); //야드L3MESSAGE

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				return null;
			}

			if (ydEqpId.startsWith("C")) {
				//BLOOM야드
				msgId = "YSN2L004";
			} else {
				return null;
			}

			/**********************************************************
			* 2. 크레인작업실적응답 전문 생성
			**********************************************************/
			//야드L3Message가 없으면 생성
			if ("".equals(ydL3Msg)) {
				if ("U".equals(ydL2WrGp)) {
					ydL3Msg = "권상실적";
				} else if ("D".equals(ydL2WrGp)) {
					ydL3Msg = "권하실적";
				} else if ("E".equals(ydL2WrGp)) {
					ydL3Msg = "비상조업실적";
				} else if ("R".equals(ydL2WrGp)) {
					ydL3Msg = "고장복구실적";
				} else if ("M".equals(ydL2WrGp)) {
					ydL3Msg = "운전모드전환";
				} else if ("J".equals(ydL2WrGp)) {
					ydL3Msg = "지시요구";
				} else if ("F".equals(ydL2WrGp)) {
					ydL3Msg = "강제권하";
				} else if ("G".equals(ydL2WrGp)) {
					ydL3Msg = "강제권상요구";
				} else {
					ydL3Msg = ydL2WrGp;
				}

				if ("0000".equals(ydL3HdRsCd)) {
					ydL3Msg = ydL3Msg + " 정상 처리";
				} else if ("9999".equals(ydL3HdRsCd)) {
					ydL3Msg = ydL3Msg + " 정보 없음";
				} else {
					ydL3Msg = ydL3Msg + " 오류 <" + logId + ">";
				}
			}

			StringBuffer sbMsg = new StringBuffer();

			sbMsg = sbMsg.append(msgId                                 ); //전문ID
			sbMsg = sbMsg.append(commUtils.getDateTime18()             ); //생성일,생성시간(yyyy-MM-ddHH:mm:ss)
			sbMsg = sbMsg.append("I"                                   ); //전문구분
			sbMsg = sbMsg.append("0078"                                ); //전문길이
			sbMsg = sbMsg.append(commUtils.getRPad(" "       , 29, " ")); //임시
			sbMsg = sbMsg.append(commUtils.getRPad(ydEqpId   ,  6, " ")); //야드설비ID
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")),  1, " ")); //야드작업진행상태
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )),  8, " ")); //야드스케쥴코드
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )), 18, " ")); //야드크레인스케쥴ID
			sbMsg = sbMsg.append(commUtils.getRPad(ydL2WrGp  ,  1, " ")); //야드L2실적구분
			sbMsg = sbMsg.append(commUtils.getRPad(ydL3HdRsCd,  4, " ")); //야드L3처리결과코드
			sbMsg = sbMsg.append(commUtils.getRPad(ydL3Msg   , 40, " ")); //야드L3Message

			JDTORecord sndMsg = JDTORecordFactory.getInstance().create();

			sndMsg.setResultCode(logId);	//Log ID
			sndMsg.setResultMsg(methodNm);	//Log Method Name
			sndMsg.addField("JMS_TC_CD"          , msgId                    ); //JMSTC코드
			sndMsg.addField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); //JMSTC생성일시(yyyyMMddHHmmss)
			sndMsg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); //JMSTCMessage

			//전송 Data Return
			return commUtils.addSndData(sndMsg);
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return null;
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업실적응답(YSN8L004) 전문 조회  : 2025.08.04 소영조 Method 추가
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getYSN8L004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업실적응답 조회[EbtYsComm.getYSN8L004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			// 수신 항목 값
			String msgId      = ""; 														// 전문ID
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); 	// 야드설비ID
			String ydL2WrGp   = commUtils.trim(rcvMsg.getFieldString("YD_L2_WR_GP"   )); 	// 야드L2실적구분
			String ydL3HdRsCd = commUtils.trim(rcvMsg.getFieldString("YD_L3_HD_RS_CD")); 	// 야드L3처리결과코드
			String ydL3Msg    = commUtils.trim(rcvMsg.getFieldString("YD_L3_MSG"     )); 	// 야드L3MESSAGE

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				return null;
			}

			if (ydEqpId.startsWith("G")) {
				// YSN8L004(크레인작업실적응답)
				msgId = "YSN8L004";
			} else {
				return null;
			}

			/**********************************************************
			* 2. 크레인작업실적응답 전문 생성
			**********************************************************/
			// 야드L3Message가 없으면 생성
			if ("".equals(ydL3Msg)) {
				if ("U".equals(ydL2WrGp)) {
					ydL3Msg = "권상실적";
				} else if ("D".equals(ydL2WrGp)) {
					ydL3Msg = "권하실적";
				} else if ("E".equals(ydL2WrGp)) {
					ydL3Msg = "비상조업실적";
				} else if ("R".equals(ydL2WrGp)) {
					ydL3Msg = "고장복구실적";
				} else if ("M".equals(ydL2WrGp)) {
					ydL3Msg = "운전모드전환";
				} else if ("J".equals(ydL2WrGp)) {
					ydL3Msg = "지시요구";
				} else if ("F".equals(ydL2WrGp)) {
					ydL3Msg = "강제권하";
				} else if ("G".equals(ydL2WrGp)) {
					ydL3Msg = "강제권상요구";
				} else {
					ydL3Msg = ydL2WrGp;
				}

				if ("0000".equals(ydL3HdRsCd)) {
					ydL3Msg = ydL3Msg + " 정상 처리";
				} else if ("9999".equals(ydL3HdRsCd)) {
					ydL3Msg = ydL3Msg + " 정보 없음";
				} else {
					ydL3Msg = ydL3Msg + " 오류 <" + logId + ">";
				}
			}

			StringBuffer sbMsg = new StringBuffer();

			sbMsg = sbMsg.append(msgId                                 ); 			// 전문ID
			sbMsg = sbMsg.append(commUtils.getDateTime18()             ); 			// 생성일,생성시간(yyyy-MM-ddHH:mm:ss)
			sbMsg = sbMsg.append("I"                                   ); 			// 전문구분
			sbMsg = sbMsg.append("0078"                                ); 			// 전문길이
			sbMsg = sbMsg.append(commUtils.getRPad(" "       , 29, " ")); 			// 임시
			sbMsg = sbMsg.append(commUtils.getRPad(ydEqpId   ,  6, " ")); 			// 야드설비ID
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")),  1, " ")); // 야드작업진행상태
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )),  8, " ")); // 야드스케쥴코드
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )), 18, " ")); // 야드크레인스케쥴ID
			sbMsg = sbMsg.append(commUtils.getRPad(ydL2WrGp  ,  1, " ")); 			// 야드L2실적구분
			sbMsg = sbMsg.append(commUtils.getRPad(ydL3HdRsCd,  4, " ")); 			// 야드L3처리결과코드
			sbMsg = sbMsg.append(commUtils.getRPad(ydL3Msg   , 40, " ")); 			// 야드L3Message

			JDTORecord sndMsg = JDTORecordFactory.getInstance().create();

			sndMsg.setResultCode(logId);	//Log ID
			sndMsg.setResultMsg(methodNm);	//Log Method Name
			sndMsg.addField("JMS_TC_CD"          , msgId                    ); // JMS TC 코드
			sndMsg.addField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); // JMS TC 생성일시(yyyyMMddHHmmss)
			sndMsg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); // JMS TC Message

			//전송 Data Return
			return commUtils.addSndData(sndMsg);
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return null;
		}
	}
	
	// 2026.03.27 이송소재상차확인 -->
	/**
	 *      [A] 오퍼레이션명 : Parameter Logging : 2026.03.27 양태호 Method 추가
	 *
	 *      @param String paramNm
	 *      @param GridData obj
	 *      @return void
	*/
	public void printParam(String paramNm, GridData obj, int prtRowCnt) {
		String methodNm = "printParam";
		String logId = obj.getIPAddress();
		
		if (prtRowCnt < 4) {
			prtRowCnt = 4;
		}
		
		try {
			StringBuffer sb = new StringBuffer("\n▩▩ " + paramNm + " ▩▩");

			if (obj instanceof GridData) {
				GridData gridData = (GridData) obj;
				GridHeader[] headers = gridData.getHeaders();
				
				String[] paramNames = gridData.getParamNames();
				
				if (paramNames != null) {
					StringBuffer paramBuf = new StringBuffer();
					paramBuf.append("\n▩ params {");
					
					
					for (int i = 0; i < paramNames.length; i++) {
						String pName = paramNames[i];
						if (i > 0) {
							paramBuf.append(", ");
						}
						paramBuf.append(pName).append("=").append(gridData.getParam(pName));
					}
					paramBuf.append("}");
					sb.append(paramBuf.toString());
				}

				int maxRowCount = 0;
				if (headers != null) {
					for (int h = 0; h < headers.length; h++) {
						GridHeader header = headers[h];
						int headerRowCount = header.getRowCount();
						if (headerRowCount > maxRowCount) {
							maxRowCount = headerRowCount;
						}
					}
				}
				
				sb.append("\n▩ " + "TotalRowCount:" + maxRowCount);
				int[] printRows;
				if (maxRowCount <= prtRowCnt) {
					printRows = new int[maxRowCount];
					for (int i = 0; i < maxRowCount; i++) {
						printRows[i] = i;
					}
				} else {
					printRows = new int[] { 0, 1, maxRowCount - 2, maxRowCount -1 };
				}
				
				for (int r = 0; r < printRows.length; r++) {
					if (maxRowCount > prtRowCnt && r == 2) {
						sb.append("\n ......");
					}
					
					int i = printRows[r];
					StringBuffer rowBuf = new StringBuffer();
					rowBuf.append("\n▩ [").append(i).append("] ");
					if (headers != null) {
						for (int h = 0; h < headers.length; h++) {
							GridHeader header = headers[h];
							String headerName = resolveHeaderName(header, h);
							String value = getHeaderValue(header, i);
							if (h > 0) {
								rowBuf.append(", ");
							}
							rowBuf.append(headerName).append("=").append(value);
						}
					}

					sb.append(rowBuf.toString());
				}
				
			} else {
				sb = sb.append("\n▩ " + obj.toString());
			}

			sb = sb.append("\n▩▩ " + paramNm + " ▩▩");
			
			commUtils.printLog(logId, sb.toString(), "");
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : resolveHeaderName : 2026.03.27 양태호 Method 추가
	 *
	 *      @param GridHeader header
	 *      @param int index
	 *      @return String
	*/
	private String resolveHeaderName(GridHeader header, int index) {
		if (header == null) {
			return "header#" + index;
		}
		
		String[] candidates = new String[] { "getName", "getHeaderName", "getId", "getID", "getKey"};
		for (int i = 0; i < candidates.length; i++) {
			try {
				java.lang.reflect.Method m = header.getClass().getMethod(candidates[i], new Class[0]);
				Object v = m.invoke(header, new Object[0]);
				if (v != null) {
					String s = String.valueOf(v).trim();
					if (s.length() > 0) {
						return s;
					}
				}
					
			} catch (Exception ignore) {
			}
		}
		
		return "header#" + index;
	}
	
	/**
	 *      [A] 오퍼레이션명 : getHeaderValue : 2026.03.27 양태호 Method 추가
	 *
	 *      @param GridHeader header
	 *      @param int index
	 *      @return String
	*/
	private String getHeaderValue(GridHeader header, int index) {
		try {
			String rtnValue;
			if (header.getDataType().equals(OperateGridData.t_combo)) {
				rtnValue = commUtils.nvl(commUtils.trim(header.getComboHiddenValues()[header.getSelectedIndex(index)]), "");
			} else if (header.getDataType().equals(OperateGridData.t_number)) {
				rtnValue = commUtils.nvl(commUtils.trim(header.getValue(index)), "0");
			} else {
				rtnValue = commUtils.nvl(commUtils.trim(header.getValue(index)), "");
			}
			return rtnValue;
		} catch (Exception e) {
			return "";
		}
	}
	// 2026.03.27 이송소재상차확인 <--
}
