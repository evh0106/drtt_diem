/**
 * @(#)SlabYdComm
 *
 * @version          V1.00
 * @author           허철호
 * @date             2012/11/22
 * 
 * @description      Slab야드 공통 처리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2012/11/22   허철호      허철호      최초 등록
 * V1.02  2015/12/14   이준영      이준영      항만 신규설비 추가
 */
package com.inisteel.cim.yd.slabyd.session;

import java.util.HashMap;

import xlib.cmc.GridData;

import flex.messaging.MessageBroker;
import flex.messaging.messages.AsyncMessage;
import flex.messaging.util.UUIDUtils;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.pSlabYd.dao.PSlabYdCommDAO;
import com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO;

/**
 * [A] 클래스명 : Slab야드 공통
 *
 */

public class SlabYdComm {

	private YdSlabUtils slabUtils = new YdSlabUtils();
	private SlabYdCommDAO commDao = new SlabYdCommDAO();
	
	private PSlabYdCommDAO PcommDao = new PSlabYdCommDAO();
	/***************************************************************************
	 * 공통 Check
	 **************************************************************************/
	
	/**
	 *      [A] 오퍼레이션명 : 설비상태 Check
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord chkEqpStat(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "설비상태Check[SlabYdComm.chkEqpStat] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			jrRtn.setField("YD_L3_HD_RS_CD", "EQ99"); //야드L3처리결과코드(Error)
			jrRtn.setField("YD_L3_MSG"     , "오류:설비상태Check 예상치 못한 오류"); //야드L3MESSAGE(40Byte)

			//수신 항목 값
			String ydEqpId = slabUtils.trim(rcvMsg.getFieldString("V_YD_EQP_ID")); //야드설비ID
			String ydL3HdRsCd = ""; //야드L3처리결과코드
			String ydL3Msg    = ""; //야드L3MESSAGE
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "EQ01";
				ydL3Msg = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "EQ02";
				ydL3Msg = "오류:설비ID[" + ydEqpId + "] 이상";
			}

			if (!"".equals(ydL3Msg)) {
				jrRtn.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				jrRtn.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			/**********************************************************
			* 2. 설비상태 Check
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, "");

			jrParam.setField("V_YD_EQP_ID", ydEqpId); //야드설비ID

			JDTORecordSet jsChk = commDao.getStat("Eqp", jrParam);

			String ydEqpStat     = ""; //야드설비상태
			String ydEqpWrkMode  = ""; //야드설비작업Mode

			if (jsChk.size() > 0) {
				ydEqpStat    = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT"    ));
				ydEqpWrkMode = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE"));
			}

			if ("".equals(ydEqpStat)) {
				//설비 Table 정보 Check
				ydL3HdRsCd = "EQ03";
				ydL3Msg = "오류:크레인[" + ydEqpId + "] 정보 없음";
			} else if ("B".equals(ydEqpStat)) {
				//설비 Table 설비상태 Check
				ydL3HdRsCd = "EQ04";
				ydL3Msg = "오류:크레인[" + ydEqpId + "] 고장";
			} else if (!"1".equals(ydEqpWrkMode)) {
				//설비 Table 설비작업Mode Check
				ydL3HdRsCd = "EQ05";
				ydL3Msg = "오류:크레인[" + ydEqpId + "] Off-Line";
			}
			
			if (!"".equals(ydL3Msg)) {
				jrRtn.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				jrRtn.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			jrRtn.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드
			jrRtn.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			slabUtils.printErrorLog(slabUtils.makeErrorLog(logId, methodNm, e), this, e);
			return jrRtn;
		} catch (Exception e) {
			return jrRtn;
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 스케줄코드 Check
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord chkSchCd(JDTORecord rcvMsg) {
		String methodNm = "스케줄코드Check[SlabYdComm.chkSchCd] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			jrRtn.setField("YD_L3_HD_RS_CD", "SC99"); //야드L3처리결과코드(Error)
			jrRtn.setField("YD_L3_MSG"     , "오류:스케줄코드Check 예상치 못한 오류"); //야드L3MESSAGE(40Byte)
			
			//수신 항목 값
			String ydSchCd = slabUtils.trim(rcvMsg.getFieldString("V_YD_SCH_CD")); //야드스케쥴코드
			String ydL3HdRsCd = ""; //야드L3처리결과코드
			String ydL3Msg    = ""; //야드L3MESSAGE
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydSchCd)) {
				ydL3HdRsCd = "SC01";
				ydL3Msg = "오류:스케줄코드 없음";
			} else if (ydSchCd.length() < 8) {
				ydL3HdRsCd = "SC02";
				ydL3Msg = "오류:스케줄코드[" + ydSchCd + "] 이상";
			}

			if (!"".equals(ydL3Msg)) {
				jrRtn.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				jrRtn.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			/**********************************************************
			* 2. 크레인스케줄 상태 Check
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, "");

			jrParam.setField("V_YD_SCH_CD", ydSchCd); //야드스케쥴코드

			//야드스케쥴금지유무 조회
			JDTORecordSet jsChk = commDao.getStat("SchCd", jrParam);

			String ydSchProhExn = "";  //야드스케쥴금지유무

			if (jsChk.size() > 0) {
				ydSchProhExn  = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_PROH_EXN"));
			}

			if ("".equals(ydSchProhExn)) {
				//스케줄기준 Table 정보 Check
				ydL3HdRsCd = "SC03";
				ydL3Msg = "오류:스케쥴코드[" + ydSchCd + "] 정보 없음";
			} else if ("Y".equals(ydSchProhExn)) {
				//스케줄 금지여부 Check
				ydL3HdRsCd = "SC04";
				ydL3Msg = "오류:스케쥴코드[" + ydSchCd + "] 기동금지";
			}
			
			if (!"".equals(ydL3Msg)) {
				jrRtn.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				jrRtn.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			jrRtn.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드
			jrRtn.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			slabUtils.printErrorLog(slabUtils.makeErrorLog(logId, methodNm, e), this, e);
			return jrRtn;
		} catch (Exception e) {
			return jrRtn;
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 스케줄코드 및 크레인 Check
	 *
	 *      @param JDTORecord jrParam
	 *      @return JDTORecord jrRtn
	 *      @throws DAOException
	*/
	public JDTORecord chkSchCdEqp(JDTORecord jrParam) {
		String methodNm = "스케줄코드 및 크레인 Check[SlabYdComm.chkSchCdEqp] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String ydSchCd = slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD")); //야드스케쥴코드
			String callMethodNm	= slabUtils.trim(jrParam.getFieldString("CALL_METHOD_NM")); //직전 호출 메소드
			
			//2025.05 지정크레인이 있는 경우 지정크레인의 Spec을 가져올 수 있도록 수정
			String ydWrkCrn = slabUtils.trim(slabUtils.nvl(jrParam.getFieldString("YD_EQP_ID"), ""));
			
			JDTORecordSet jsChk = null;
			
			if ("".equals(ydSchCd)) {
				throw new Exception("스케쥴코드 없음");
			} else if (ydSchCd.length() < 8) {
				throw new Exception("스케쥴코드[" + ydSchCd + "] 이상");
			}
			
			//2025.05 지정크레인이 있는 경우 지정크레인의 Spec을 가져올 수 있도록 수정
			if(!"".equals(ydWrkCrn)) {
				jsChk = commDao.getStat("SchCdWrkCrn", jrParam);
			} else {
				//야드스케쥴금지유무 조회
				jsChk = commDao.getStat("SchCd", jrParam);
			}
			
			String ydSchProhExn = "";  //야드스케쥴금지유무
			String ydEqpId      = "";  //야드설비ID(작업크레인)
			
			/*2025.03.28 연주 김충만계장 요청. 기동금지 스케줄도 작업예약까진 등록 되어야된다.
			 * 등록 후 크레인스케줄만 기동 안되면 됨.
			 * 
			 *  */
			if (jsChk.size() > 0) {
				// 기동금지 스케줄도 저장위치별정보조회 통해서 작업예약 등록시, 작업 예약은 가능하도록 수정 2024.02.15
				if( "insMvstkWrkBook".equals(callMethodNm)  ) { //test : ACTC01UM
					ydSchProhExn = "N";
					slabUtils.printLog(logId, "스케줄 기동 금지상태이나 작업예약은 가능 : " + ydSchCd , "S+");
				} else {
					ydSchProhExn = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_PROH_EXN"));
				}
				
				ydEqpId      = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_ID"      ));
			}

			if ("".equals(ydSchProhExn)) {
				throw new Exception("스케쥴코드[" + ydSchCd + "] 정보 없음");
			} else if ("Y".equals(ydSchProhExn)) {
				throw new Exception("스케쥴코드[" + ydSchCd + "] 기동금지");
			} else if ("".equals(ydEqpId)) {
				throw new Exception("스케쥴코드[" + ydSchCd + "] 작업가능 크레인 없음");
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jsChk.getRecord(0);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
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
		String methodNm = "스케줄코드 조회[SlabYdComm.getSchCd] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String ydSchCd     = slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD"     )); //야드스케쥴코드
			String ydStkColGp  = slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP" )); //야드적치열구분
			String ydStkBedNo  = slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO" )); //야드적치Bed번호
			String ydSchWhioGp = slabUtils.trim(jrParam.getFieldString("V_YD_SCH_WHIO_GP")); //야드스케쥴입출고구분
			
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
				
				jrParam.setField("V_YD_SCH_CD", ydSchCd); //야드스케쥴코드
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
				
				jrParam.setField("V_YD_SCH_CD", ydSchCd); //야드스케쥴코드
			}
			
			//야드스케쥴금지유무 조회
			JDTORecordSet jsChk = commDao.getStat("SchCd", jrParam);

			String ydSchProhExn = "";  //야드스케쥴금지유무
			String ydEqpId      = "";  //야드설비ID(작업크레인)

			if (jsChk.size() > 0) {
				ydSchProhExn = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_PROH_EXN"));
				ydEqpId      = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_ID"      ));
			}

			if ("".equals(ydSchProhExn)) {
				throw new Exception("스케쥴코드[" + ydSchCd + "] 정보 없음");
			} else if ("Y".equals(ydSchProhExn)) {
				//throw new Exception("스케쥴코드[" + ydSchCd + "] 기동금지");
				//--2023.09.05 스케줄 기동금지는 exception 제외처리.
				return jsChk.getRecord(0);
			} else if ("".equals(ydEqpId)) {
				throw new Exception("스케쥴코드[" + ydSchCd + "] 작업가능 크레인 없음");
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jsChk.getRecord(0);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
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
		String methodNm = "크레인사양Check[SlabYdComm.chkCrnSpec] < " + jrCrnSpec.getResultMsg();
		String logId = jrCrnSpec.getResultCode();

		try {
			String crnSpecOvGp = ""; //결과
			
			String Is_plate_StoA_Scarfing = "N";
			
			Is_plate_StoA_Scarfing = slabUtils.nvl(jrCrnSpec.getFieldString("PLATE_SIDE_SCARFING_YN"   ),"N");
			
			//크레인사양
			int   ydWrkAbleSh   = Integer.parseInt(slabUtils.nvl(jrCrnSpec.getFieldString("YD_WRK_ABLE_SH"   ),"0"));	//야드작업가능매수
			float ydWrkAbleWt   = Integer.parseInt(slabUtils.nvl(jrCrnSpec.getFieldString("YD_WRK_ABLE_WT"   ),"0"));	//야드작업가능중량
			float ydCrnTongH    = Float.parseFloat(slabUtils.nvl(jrCrnSpec.getFieldString("YD_CRN_TONG_H"    ),"0"));	//야드크레인집게높이
			float ydCrnTongWTol = Float.parseFloat(slabUtils.nvl(jrCrnSpec.getFieldString("YD_CRN_TONG_W_TOL"),"0"));	//야드크레인집게폭허용오차
			//점검할 값
			int   mtlShSum = Integer.parseInt(slabUtils.nvl(jrCrnSpec.getFieldString("MTL_SH_SUM"),"0"));	//재료매수합
			int   mtlWtSum = Integer.parseInt(slabUtils.nvl(jrCrnSpec.getFieldString("MTL_WT_SUM"),"0"));	//재료중량합
			float mtlTSum  = Float.parseFloat(slabUtils.nvl(jrCrnSpec.getFieldString("MTL_T_SUM" ),"0"));	//재료두께합
			float mtlWMax  = Float.parseFloat(slabUtils.nvl(jrCrnSpec.getFieldString("MTL_W_MAX" ),"0"));	//재료폭최대
			float mtlW     = Float.parseFloat(slabUtils.nvl(jrCrnSpec.getFieldString("MTL_W"     ),"0"));	//재료폭
			
			//통합->연주 후판재 측면 스카핑되있는 것. 작업시 한매씩 작업하게끔
			if("Y".equals(Is_plate_StoA_Scarfing)){
				crnSpecOvGp = "SH";
			}
			else if (mtlShSum > ydWrkAbleSh) {
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
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
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
		String methodNm = "크레인스케줄전문조회[SlabYdComm.getCrnSchMsg] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn  = null;
			String crnSchStGp = "N";														//크레인스케줄기동구분(New)
			String currDate   = slabUtils.getDateTime14();									//현재시각
			String ydGp       = slabUtils.trim(jrParam.getFieldString("YD_GP"        ));	//야드구분
			String ydWbookId  = slabUtils.trim(jrParam.getFieldString("YD_WBOOK_ID"  ));	//야드작업예약ID
			String ydSchCd    = slabUtils.trim(jrParam.getFieldString("YD_SCH_CD"    ));	//야드스케쥴코드
			String ydEqpId    = slabUtils.trim(jrParam.getFieldString("YD_EQP_ID"    ));	//야드설비ID
			String ydSchStGp  = slabUtils.trim(jrParam.getFieldString("YD_SCH_ST_GP" ));	//야드스케쥴기동구분
			String ydSchReqGp = slabUtils.trim(jrParam.getFieldString("YD_SCH_REQ_GP"));	//야드스케쥴요청구분
			String modifier   = slabUtils.trim(jrParam.getFieldString("V_MODIFIER"   ));	//수정자
			String ejbCallYn  = slabUtils.trim(jrParam.getFieldString("EJB_CALL_YN"  ));	//EJBCall여부(신 크레인스케줄)

			if ("".equals(ydWbookId) && "".equals(ydSchCd) && "".equals(ydEqpId)) {
				if ("Y".equals(ejbCallYn)) {
					throw new Exception("크레인스케줄 기동을 위한 정보가 없습니다.");
				} else {
					slabUtils.printLog(logId, "크레인스케줄 기동을 위한 정보가 없습니다.", "SL");
					return null;
				}
			}

			//크레인스케줄기동구분 조회
			if (!"".equals(ydWbookId) && ("".equals(ydSchCd) || "".equals(ydEqpId))) {
				jrParam.setField("V_YD_WBOOK_ID", ydWbookId); //야드작업예약ID
				JDTORecordSet jsChk = commDao.getCrnSchStGp("WB", jrParam);

				if (jsChk.size() > 0) {
					ydGp       = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_GP"        ));	//야드구분
					ydSchCd    = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_CD"    ));	//야드스케쥴코드
					ydEqpId    = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_ID"    ));	//야드설비ID
					crnSchStGp = slabUtils.trim(jsChk.getRecord(0).getFieldString("CRN_SCH_ST_GP"));	//크레인스케줄기동구분
				} else {
					if ("Y".equals(ejbCallYn)) {
						throw new Exception("작업예약ID[" + ydWbookId + "]의 정보가 없어 크레인스케줄을 기동할 수 없습니다.");
					} else {
						slabUtils.printLog(logId, "작업예약ID[" + ydWbookId + "]의 정보가 없어 크레인스케줄을 기동할 수 없습니다.", "SL");
						return null;
					}
				}
			} else {
				if ("".equals(ydGp)) {
					if (!"".equals(ydSchCd)) {
						ydGp = ydSchCd.substring(0, 1);
					} else if (!"".equals(ydEqpId)) {
						ydGp = ydEqpId.substring(0, 1);
					}
				}

				jrParam.setField("V_YD_GP", ydGp); //야드구분
				JDTORecordSet jsChk = commDao.getCrnSchStGp("SG", jrParam);

				if (jsChk.size() > 0) {
					crnSchStGp = slabUtils.trim(jsChk.getRecord(0).getFieldString("CRN_SCH_ST_GP"));	//크레인스케줄기동구분
				}
			}

			//업무기준에 등록되어 있지 않으면 신규 스케줄
			if ("".equals(crnSchStGp)) {
				crnSchStGp = "N";
			}

			slabUtils.printLog(logId, "[작업예약ID:" + ydWbookId + ", 스케쥴코드:" + ydSchCd + ", 설비ID:" + ydEqpId + "]의 야드 크레인스케줄기동구분 : " + crnSchStGp, "SL");

			//크레인스케줄 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = slabUtils.getParam(logId, jrParam.getResultMsg(), modifier);
			
			if ("N".equals(crnSchStGp)) {
				//신 크레인스케줄 기동
				jrYdMsg.setField("JMS_TC_CD", "YDYDJ400"); //Slab야드 크레인스케줄
				
				//화면에서 작업예약을 선택해서 기동 시
				if (!"".equals(ydWbookId) && "M".equals(ydSchStGp)) {
					ydSchCd = "";	//야드스케쥴코드
					ydEqpId = "";	//야드설비ID
				}
			} else {
				//구 크레인스케줄 기동
				if ("A".equals(ydGp)) {
					jrYdMsg.setField("JMS_TC_CD", "YDYDJ500"); //C연주야드 크레인스케줄
				} else if ("M".equals(ydGp)) {
					jrYdMsg.setField("JMS_TC_CD", "YDYDJ500"); //항만야드 크레인스케줄 -- 항만야드 기능적용 보완 : 2015.12.15 by LeeJY
				} else {
					jrYdMsg.setField("JMS_TC_CD", "YDYDJ503"); //후판Slab야드 크레인스케줄
				}
			}

			jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate  ); //JMSTC생성일시
			jrYdMsg.setField("YD_WBOOK_ID"       , ydWbookId ); //야드작업예약ID
			jrYdMsg.setField("YD_SCH_CD"         , ydSchCd   ); //야드스케쥴코드
			jrYdMsg.setField("YD_EQP_ID"         , ydEqpId   ); //야드설비ID
			jrYdMsg.setField("YD_SCH_ST_GP"      , ydSchStGp ); //야드스케쥴기동구분
			jrYdMsg.setField("YD_SCH_REQ_GP"     , ydSchReqGp); //야드스케쥴요청구분

			//신 크레인스케줄이고 EJBCall여부가 'Y'이면
			if ("N".equals(crnSchStGp) && "Y".equals(ejbCallYn)) {
				//신 크레인스케줄 EJB Call
				EJBConnector sndConn = new EJBConnector("default", "SlabYdSchSeEJB", this);
				JDTORecord jrRst = (JDTORecord)sndConn.trx("rcvYDYDJ400", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				
				
				if (jrRst != null) {
					//작업지시 등 전송할 전문이 있으면 받아서 전송
					jrRtn = slabUtils.addSndData(jrRtn, jrRst);
				}
			} else {
				//구 크레인스케줄 전송
				jrRtn = slabUtils.addSndData(jrRtn, jrYdMsg);
			}
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업실적응답(YDY1L005) 전문 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getYDY1L005(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업실적응답 조회[SlabYdComm.getYDY1L005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			//수신 항목 값
			String msgId      = ""; //전문ID
			String ydEqpId    = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID
			String ydL2WrGp   = slabUtils.trim(rcvMsg.getFieldString("YD_L2_WR_GP"   )); //야드L2실적구분
			String ydL3HdRsCd = slabUtils.trim(rcvMsg.getFieldString("YD_L3_HD_RS_CD")); //야드L3처리결과코드
			String ydL3Msg    = slabUtils.trim(rcvMsg.getFieldString("YD_L3_MSG"     )); //야드L3MESSAGE

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				return null;
			}
			//연주야드 AACRA0 등 0 으로 끝나는 가상크레인 실적응답에서 제외. --2023.01.04 연주 김충만계장 요청(가상크레인 생성)
			if(ydEqpId.length() == 6 && "0".equals(ydEqpId.substring(ydEqpId.length()-1))){
				return null;
			}
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////
	
	     if("Y".equals(APPLY_YN34)){
	    	 if (ydEqpId.startsWith("D")) {
					//후판Slab야드
					msgId = "YDY3L005";
				} 
          }

			if (ydEqpId.startsWith("A")) {
				//연주야드
				msgId = "YDY1L005";
			} else if (ydEqpId.startsWith("M")) {  //수정 (2015.12.14) : 항만야드  By LeeJY
				//항만Slab야드
				msgId = "YDE7L005";
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
			sbMsg = sbMsg.append(slabUtils.getDateTime18()             ); //생성일,생성시간(yyyy-MM-ddHH:mm:ss)
			sbMsg = sbMsg.append("I"                                   ); //전문구분
			sbMsg = sbMsg.append("0078"                                ); //전문길이
			sbMsg = sbMsg.append(slabUtils.getRPad(" "       , 29, " ")); //임시
			sbMsg = sbMsg.append(slabUtils.getRPad(ydEqpId   ,  6, " ")); //야드설비ID
			sbMsg = sbMsg.append(slabUtils.getRPad(slabUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")),  1, " ")); //야드작업진행상태
			sbMsg = sbMsg.append(slabUtils.getRPad(slabUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )),  8, " ")); //야드스케쥴코드
			sbMsg = sbMsg.append(slabUtils.getRPad(slabUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )), 18, " ")); //야드크레인스케쥴ID
			sbMsg = sbMsg.append(slabUtils.getRPad(ydL2WrGp  ,  1, " ")); //야드L2실적구분
			sbMsg = sbMsg.append(slabUtils.getRPad(ydL3HdRsCd,  4, " ")); //야드L3처리결과코드
			sbMsg = sbMsg.append(slabUtils.getRPad(ydL3Msg   , 40, " ")); //야드L3Message

			JDTORecord sndMsg = JDTORecordFactory.getInstance().create();

			sndMsg.addField("JMS_TC_CD"          , msgId                    ); //JMSTC코드
			sndMsg.addField("JMS_TC_CREATE_DDTT" , slabUtils.getDateTime14()); //JMSTC생성일시(yyyyMMddHHmmss)
			sndMsg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); //JMSTCMessage

			//전송 Data Return
			return slabUtils.addSndData(sndMsg);
		} catch (Exception e) {
			slabUtils.printErrorLog(slabUtils.makeErrorLog(logId, methodNm, e), this, e);
			return null;
		}
	}

	
	/***************************************************************************
	 * 공통 처리
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 전문수신처리 EJB Call
	 *
	 *      @param String JDTORecord rcvMsg
	 *      @return String JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvMsgToEjbCall(JDTORecord rcvMsg) {
		String methodNm = "전문수신처리EJBCall[SlabYdComm.rcvMsgToEjbCall] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			String ejbMsgId = slabUtils.trim(rcvMsg.getFieldString("EJB_MSG_ID"));	//EJB Call 전문ID
			JDTORecordSet sendData = (JDTORecordSet)rcvMsg.getField("SEND_DATA");	//전송Data
			
			if ("".equals(ejbMsgId) || sendData == null) {
				return rcvMsg;
			}

			JDTORecord jrRtn = null;	//반환 결과
			JDTORecord jrMsg = null;	//전문Message
			String msgId     = "";		//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String rcvClass  = "";		//전문수신 Class 명
			String rcvMethod = "";		//전문수신 Method 명
			
			int rowCnt = sendData.size();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrMsg = sendData.getRecord(ii);
				msgId = slabUtils.getMsgId(jrMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
				
				if (ejbMsgId.equals(msgId)) {
					if ("L".equals(msgId.substring(4, 5))) {
						//EAI수신
						rcvClass = "SlabYdL2RcvSeEJB";
					} else {
						if ("YDYD".equals(msgId.substring(0, 4))) {
							//JMS수신(내부)
							rcvClass = "SlabYdSchSeEJB";
						} else {
							//JMS수신
							rcvClass = "SlabYdL3RcvSeEJB";
						}
					}
					rcvMethod = "rcv" + msgId;
					slabUtils.printLog(logId, "EJB Call [ 전문ID : " + msgId + ", Class : " + rcvClass + ", Method : " + rcvMethod + " ]", "SL");

					jrMsg.setResultCode(logId);		//Log ID
					jrMsg.setResultMsg(methodNm);	//Log Method Name

					//EJB Call
					EJBConnector sndConn = new EJBConnector("default", rcvClass, this);
					JDTORecord jrRst = (JDTORecord)sndConn.trx(rcvMethod, new Class[] { JDTORecord.class }, new Object[] { jrMsg });
					//전송할 전문이 있으면 받아서 추가
					jrRtn = slabUtils.addSndData(jrRtn, jrRst);
				} else {
					//EJB Call 하지않는 전문은 다시 추가
					jrRtn = slabUtils.addSndData(jrRtn, jrMsg);
				}
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/***************************************************************************
	 * Flex Push Server 전송 처리
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : Flex Data 전송
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return void
	*/
	public void sndToFlexData(JDTORecord rcvMsg) {
		String methodNm = "FlexData전송[SlabYdComm.sndToFlexData] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			String ydEqpId   = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"   )); //야드설비ID
			String ydUpWrLoc = slabUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LOC")); //야드권상실적위치
			String ydDnWrLoc = slabUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LOC")); //야드권하실적위치
			String ydGp      = ""; //야드구분

			//설비, 위치 Check
			if (ydEqpId.length() == 6) {
				ydGp = ydEqpId.substring(0, 1);
			} else if (ydUpWrLoc.length() == 8) {
				ydGp = ydUpWrLoc.substring(0, 1);
			} else if (ydDnWrLoc.length() == 8) {
				ydGp = ydDnWrLoc.substring(0, 1);
			} else {
				return;
			}

			HashMap hmData = new HashMap(); //전송할 Data

			hmData.put("MSG_GP"      , "F"      ); //야드실적
			hmData.put("YD_GP"       , ydGp     );
			hmData.put("YD_EQP_ID"   , ydEqpId  );
			hmData.put("YD_UP_WR_LOC", ydUpWrLoc);
			hmData.put("YD_DN_WR_LOC", ydDnWrLoc);

			//조회결과
			JDTORecordSet jsRst = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, "");

			//크레인정보
			if (ydEqpId.length() == 6) {
				jrParam.setField("V_YD_EQP_ID", ydEqpId); //야드설비ID
				jsRst = commDao.getFlex("Crn", jrParam);
				hmData.put("YD_EQP_ID_ARR", slabUtils.listJdtoRecordTohashMap(jsRst.toList()));
			}

			//권상실적위치 정보
			if (ydUpWrLoc.length() == 8) {
				jrParam.setField("V_YD_STK_COL_GP", ydUpWrLoc.substring(0,6)); //야드적치열구분
				jrParam.setField("V_YD_STK_BED_NO", ydUpWrLoc.substring(6,8)); //야드적치Bed번호
				jsRst = commDao.getFlex("Mtl", jrParam);
				hmData.put("YD_UP_WR_LOC_ARR", slabUtils.listJdtoRecordTohashMap(jsRst.toList()));
			}

			//권하실적위치 정보
			if (ydDnWrLoc.length() == 8) {
				jrParam.setField("V_YD_STK_COL_GP", ydDnWrLoc.substring(0,6)); //야드적치열구분
				jrParam.setField("V_YD_STK_BED_NO", ydDnWrLoc.substring(6,8)); //야드적치Bed번호
				jsRst = commDao.getFlex("Mtl", jrParam);
				hmData.put("YD_DN_WR_LOC_ARR", slabUtils.listJdtoRecordTohashMap(jsRst.toList()));
			}

			this.sndToFlex("yd_monitor" + ydGp, hmData);
		} catch (Exception e) {
			slabUtils.printErrorLog(slabUtils.makeErrorLog(logId, methodNm, e), this, e);
		}
	}

	/**
	 *      [A] 오퍼레이션명 : Flex Push Server로 Data를 전송
	 *
	 *      @param String dest		: 목적지ID 이므로 함수내에서 지정해서 사용해도 무방함.
	 *      @param Object sndData	: Map형식으로 전송
	 *      @return void
	*/
	public void sndToFlex(String dest, Object sndData) {
		try {
			if ("".equals(dest)) {
				slabUtils.printLog("sndToFlex", "Destination 없음", "FL");
				return;
			} else if (sndData == null) {
				slabUtils.printLog("sndToFlex", "Push Data 없음", "FL");
				return;
			}
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam("sndToFlex", "sndToFlex", "");
			
			jrParam.setField("V_YD_GP", dest.substring(dest.length() - 1)); //야드구분

			JDTORecordSet jsChk = commDao.getStat("FlexSndYn", jrParam);

			//Flex 송신여부 Check
			if (jsChk == null || jsChk.size() == 0 ||
				!"Y".equals(slabUtils.trim(jsChk.getRecord(0).getFieldString("CHK")))) {
				slabUtils.printLog("sndToFlex", dest + " 송신 금지", "FL");
				return;
			}

			MessageBroker msgBroker = MessageBroker.getMessageBroker(null);
			String cliendID = UUIDUtils.createUUID(false);
			AsyncMessage msg = new AsyncMessage();

			msg.setDestination(dest);
			msg.setClientId(cliendID);
			msg.setMessageId(UUIDUtils.createUUID(false));
			msg.setTimestamp(System.currentTimeMillis());
			msg.setBody(sndData);

			msgBroker.routeMessageToService(msg, null);
			
			slabUtils.printLog("sndToFlex", dest + " 송신", "FL");
		} catch (Exception e) {
			slabUtils.printErrorLog(slabUtils.makeErrorLog("sndToFlex", dest, e), this, e);
		}
	}

	
	/***************************************************************************
	 * 대차스케줄
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 대차스케줄 상차완료 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtTcarSchLdCmpl(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차스케줄 상차완료 처리[SlabYdComm.trtTcarSchLdCmpl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String ydEqpId = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID" )); //야드설비ID

			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
			}

			//전문 Return
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER")));

			jrParam.setField("V_YD_EQP_ID", ydEqpId);	//야드설비ID

			/**********************************************************
			* 1. 대차 상차 정보 Check
			**********************************************************/
			//대차상차스케쥴 정보 조회
			JDTORecordSet jsChk = commDao.getTcarSch("LdCmpl", jrParam);

			if (jsChk == null || jsChk.size() <= 0) {
				throw new Exception("대차 상차스케쥴 정보가 없습니다.");
		    }

			JDTORecord jrChk = jsChk.getRecord(0);

			String ydTcarSchId      = slabUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID"      )); //야드대차스케쥴ID
			String ydCarldWrkBookId = slabUtils.trim(jrChk.getFieldString("YD_CARLD_WRK_BOOK_ID")); //야드상차작업예약ID
			String tcarLdCmplYn     = slabUtils.trim(jrChk.getFieldString("TCAR_LD_CMPL_YN"     )); //대차상차완료여부

			slabUtils.printLog(logId, slabUtils.trim(jrChk.getFieldString("TCAR_SCH_ST_MSG")) + " >> 대차상차완료여부 : [" + tcarLdCmplYn + "]", "SL");
			
			jrParam.setField("V_YD_TCAR_SCH_ID"      , ydTcarSchId     ); //야드대차스케쥴ID
			jrParam.setField("V_YD_CARLD_WRK_BOOK_ID", ydCarldWrkBookId); //야드상차작업예약ID

			/**********************************************************
			* 2. 상차완료이면 하차작업예약 생성
			**********************************************************/
			if ("Y".equals(tcarLdCmplYn)) {
				//상차완료이면 하차작업예약 생성
				//야드하차작업예약ID 생성
				String ydCarudWrkBookId = commDao.getSeqId(logId, methodNm, "WrkBook");
	
				if ("".equals(ydCarudWrkBookId)) {
					throw new Exception("대차 하차작업예약ID 생성 중 오류가 발생하였습니다.");
				}

				//작업예약 등록
				jrParam.setField("V_YD_CARUD_WRK_BOOK_ID", ydCarudWrkBookId); //야드하차작업예약ID

				//하차 작업예약 등록
				commDao.updTcarSch("InsUdWb", jrParam);
	
				//하차 작업예약재료 등록
				commDao.updTcarSch("InsUdWbMtl", jrParam);

				/**********************************************************
				* 3. 상차 대차스케줄 수정
				*  - 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등
				**********************************************************/
				commDao.updTcarSch("UpdLdSch", jrParam);
				
				/**********************************************************
				* 4. 대차작업실적 및 대차출발지시 전송
				*   - C연주정정L2 대차작업실적(YDC3L007, YDC7L007)
				*   - C연주정정L2, 후판Slab야드L2 대차출발지시(YDC3L006, YDC7L006, YDY3L006)
				**********************************************************/
				//C연주정정L2 대차작업실적
				if ("A".equals(ydEqpId.substring(0, 1))) {
					jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDC3L007", jrParam));
				}
				
				//C연주정정L2, 후판Slab야드L2 영대차출발지시
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDC3L006", jrParam));
			} else {
				//상차완료가 아니면
				throw new Exception("대차 상차완료처리할 수 있는 상태가 아닙니다.");
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}


	/**
	 *      [A] 오퍼레이션명 : 대차스케줄 하차완료 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtTcarSchUdCmpl(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차스케줄 하차완료 처리[SlabYdComm.trtTcarSchUdCmpl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String ydEqpId     = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID(대차)
			String ydTcarSchId = slabUtils.trim(rcvMsg.getFieldString("YD_TCAR_SCH_ID")); //야드대차스케쥴ID

			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
			}

			//전문 Return
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER")));

			jrParam.setField("V_YD_EQP_ID", ydEqpId);	//야드설비ID

			/**********************************************************
			* 1. 대차 하차스케쥴 정보 조회
			**********************************************************/
			if ("".equals(ydTcarSchId)) {
				//대차하차스케쥴 조회
				JDTORecordSet jsChk = commDao.getTcarSch("UdCmpl", jrParam);

				if (jsChk != null && jsChk.size() > 0) {
					JDTORecord jrChk = jsChk.getRecord(0);

					ydTcarSchId = slabUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID")); //야드대차스케쥴ID
				} else {
					throw new Exception("대차 하차스케쥴 정보가 없습니다.");
			    }
			}
			
			jrParam.setField("V_YD_TCAR_SCH_ID", ydTcarSchId);	//야드대차스케쥴ID

			/**********************************************************
			* 2. 대차스케줄 삭제
			**********************************************************/
			//대차이송재료 삭제
			commDao.updTcarSch("DelMtl", jrParam);

			//대차스케줄 삭제
			commDao.updTcarSch("DelSch", jrParam);
			
			/**********************************************************
			* 3. C연주정정L2 대차작업실적(YDC3L007, YDC7L007) 전송
			**********************************************************/
			if ("A".equals(ydEqpId.substring(0, 1))) {
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDC3L007", jrParam));
			}

			/**********************************************************
			* 4. 공대차출발지시 처리
			**********************************************************/
			jrRtn = slabUtils.addSndData(jrRtn, this.trtTcarSchLevWo(rcvMsg));
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}


	/**
	 *      [A] 오퍼레이션명 : 대차스케줄 공대차출발지시 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtTcarSchLevWo(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차스케줄 공대차출발지시 처리[SlabYdComm.trtTcarSchLevWo] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String ydEqpId   = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID" )); //야드설비ID
			String ydBayGpTo = slabUtils.trim(rcvMsg.getFieldString("YD_BAY_GP" )); //야드동구분(상차동)
			String modifier  = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER")); //수정자

			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
			}

			//전문 Return
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, modifier);

			jrParam.setField("V_YD_EQP_ID", ydEqpId);	//야드설비ID

			/**********************************************************
			* 1. 대차스케줄 정보 조회
			**********************************************************/
			String ydCurrBayGp      = ""; //야드현재동구분
			String ydHomeBayGp      = ""; //야드Home동구분
			String autoTcarSchYn    = ""; //자동대차스케줄여부
			String ydTcarSchId      = ""; //야드대차스케쥴ID
			String ydWbookIdCurr    = ""; //야드작업예약ID(현재 대차스케줄 상차작업예약ID)
			String ydBayGpCurr      = ""; //야드동구분(현재 대차스케줄 상차동)
			String ydAimBayGpCurr   = ""; //야드목표동구분(현재 대차스케줄 하차동)
			String ydWbookIdNext    = ""; //야드작업예약ID(다음 상차작업예약ID)
			String ydBayGpNext      = ""; //야드동구분(다음 작업예약 상차동)
			String ydAimBayGpNext   = ""; //야드목표동구분(다음 작업예약 하차동)
		 			
			//대차스케쥴정보(공대차출발지시) 조회
			JDTORecordSet jsChk = commDao.getTcarSch("LevWo", jrParam);

			if (jsChk != null && jsChk.size() > 0) {
				JDTORecord jrChk = jsChk.getRecord(0);

				ydCurrBayGp    = slabUtils.trim(jrChk.getFieldString("YD_CURR_BAY_GP"    ));
				ydHomeBayGp    = slabUtils.trim(jrChk.getFieldString("YD_HOME_BAY_GP"    ));
				autoTcarSchYn  = slabUtils.trim(jrChk.getFieldString("AUTO_TCAR_SCH_YN"  ));
				ydTcarSchId    = slabUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID"    ));
				ydWbookIdCurr  = slabUtils.trim(jrChk.getFieldString("YD_WBOOK_ID_CURR"  ));
				ydBayGpCurr    = slabUtils.trim(jrChk.getFieldString("YD_BAY_GP_CURR"    ));
				ydAimBayGpCurr = slabUtils.trim(jrChk.getFieldString("YD_AIM_BAY_GP_CURR"));
				ydWbookIdNext  = slabUtils.trim(jrChk.getFieldString("YD_WBOOK_ID_NEXT"  ));
				ydBayGpNext    = slabUtils.trim(jrChk.getFieldString("YD_BAY_GP_NEXT"    ));
				ydAimBayGpNext = slabUtils.trim(jrChk.getFieldString("YD_AIM_BAY_GP_NEXT"));

				//현재동이 없으면 Home동을 현재동으로
				if ("".equals(ydCurrBayGp)) {
					ydCurrBayGp = ydHomeBayGp;
				}
				
				if ("B".equals(slabUtils.trim(jrChk.getFieldString("YD_EQP_STAT")))) {
					throw new Exception("대차[" + ydEqpId + "]는 고장 상태입니다.");
				} else if (!"1".equals(slabUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE")))) {
					throw new Exception("대차[" + ydEqpId + "]는 Off-Line 상태입니다.");
				} else if ("Y".equals(slabUtils.trim(jrChk.getFieldString("TC_MTL_YN")))) {
					throw new Exception("대차스케줄[" + ydEqpId + " : " + ydTcarSchId + "]의 이송재료가 존재하여 공대차출발지시를 할 수 없습니다.");
				}
			} else {
				throw new Exception("대차 정보가 없습니다.");
		    }

			/**********************************************************
			* 2. 상차작업예약ID 및 상차도착위치, 하차도착위치 결정
			* 2.1 대차스케줄의 야드상차작업예약ID가 있으면 그대로
			* 2.2 대차스케줄의 야드상차작업예약ID가 없으면
			*   - 야드작업계획대차의 작업예약 정보로 작업예약 조회
			* 2.3 야드작업계획대차의 작업예약이 없고 자동대차스케줄 기준이 'Y'이면
			*   - 자동 스케줄 기준에 해당하는 작업예약 생성
			**********************************************************/
			String ydCarldWrkBookId = ""; //야드상차작업예약ID
			String ydCarldLevLoc    = ""; //야드상차출발위치
			String ydCarldStopLoc   = ""; //야드상차정지위치
			String ydCarudStopLoc   = ""; //야드하차정지위치
			String ydCarProgStat    = "0"; //야드차량진행상태(상차대기)
			String ydBayGp          = ""; //야드동구분(상차동)
			String ydAimBayGp       = ""; //야드동구분(하차동)

			if (!"".equals(ydWbookIdCurr)) {
				//대차스케줄 및 상차작업예약이 있는 경우
				//상차동으로 출발지시
				ydCarldWrkBookId = ydWbookIdCurr;	//야드상차작업예약ID
				ydBayGp          = ydBayGpCurr;		//야드상차동
				ydAimBayGp       = ydAimBayGpCurr;	//야드하차동
			} else if (!"".equals(ydWbookIdNext)) {
				//대차의 다음 상차작업예약이 있는 경우
				ydCarldWrkBookId = ydWbookIdNext;	//야드상차작업예약ID
				ydBayGp          = ydBayGpNext;		//야드상차동
				ydAimBayGp       = ydAimBayGpNext;	//야드하차동
			} else if ("Y".equals(autoTcarSchYn)) {
				//대차 상차작업예약이 없으면 상차작업예약을 생성
				//자동 대차스케줄 처리(상차작업예약 생성)
				JDTORecordSet jsWbMtl = commDao.getTcarSch("WbMtl", jrParam);
				int wbMtlSh = jsWbMtl.size();
				JDTORecord jrRow = null;

				if (wbMtlSh > 0) {
					jrRow = jsWbMtl.getRecord(0);

					String ydStkColGp = slabUtils.trim(jrRow.getFieldString("YD_STK_COL_GP"));	//야드적치열구분
					String ydGp       = ydStkColGp.substring(0, 1);								//야드구분
					       ydBayGp    = ydStkColGp.substring(1, 2);								//야드동구분(상차동)
					       ydAimBayGp = slabUtils.trim(jrRow.getFieldString("YD_AIM_BAY_GP"));	//야드목표동구분(하차동)
					String ydSchCd    = slabUtils.trim(jrRow.getFieldString("YD_SCH_CD"    ));	//야드스케쥴코드
					String ydSchPrior = slabUtils.trim(jrRow.getFieldString("YD_SCH_PRIOR" ));	//야드스케쥴우선순위
					String ydSchStGp  = "M";													//야드스케쥴기동구분
					if (modifier.length() == 8 && "YD".equals(modifier.substring(2, 4))) {
						ydSchStGp = "A";	//Auto
					}
					
					//작업예약ID 생성
					ydCarldWrkBookId = commDao.getSeqId(logId, methodNm, "WrkBook");

					if ("".equals(ydCarldWrkBookId)) {
						throw new Exception("대차상차 작업예약ID 생성중 오류가 발생하였습니다.");
					}

					//작업예약 등록
					jrParam.setField("V_YD_WBOOK_ID"       , ydCarldWrkBookId); //야드작업예약ID
					jrParam.setField("V_YD_GP"             , ydGp            ); //야드구분
					jrParam.setField("V_YD_BAY_GP"         , ydBayGp         ); //야드동구분
					jrParam.setField("V_YD_SCH_CD"         , ydSchCd         ); //야드스케쥴코드
					jrParam.setField("V_YD_SCH_PRIOR"      , ydSchPrior      ); //야드스케쥴우선순위
					jrParam.setField("V_YD_SCH_PROG_STAT"  , "W"             ); //야드스케쥴진행상태(스케줄수행대기)
					jrParam.setField("V_YD_SCH_ST_GP"      , ydSchStGp       ); //야드스케쥴기동구분
					jrParam.setField("V_YD_SCH_REQ_GP"     , "5"             ); //야드스케쥴요청구분(공대차출발)
					jrParam.setField("V_YD_AIM_YD_GP"      , ydGp            ); //야드목표야드구분
					jrParam.setField("V_YD_AIM_BAY_GP"     , ydAimBayGp      ); //야드목표동구분
					jrParam.setField("V_YD_TO_LOC_DCSN_MTD", "S"             ); //야드TO위치결정방법
					jrParam.setField("V_YD_WRK_PLAN_TCAR"  , ydEqpId         ); //야드작업계획대차

					commDao.insSlabYd("WrkBook", jrParam);

					//작업예약재료 등록
					String[][] wmParam = new String[wbMtlSh][8];
					
					for (int ii = 0; ii < wbMtlSh; ii++) {
						jrRow = jsWbMtl.getRecord(ii);
						
						wmParam[ii][0] = ydCarldWrkBookId;											//야드작업예약ID
						wmParam[ii][1] = slabUtils.trim(jrRow.getFieldString("STL_NO"        ));	//재료번호
						wmParam[ii][2] = modifier;													//등록자
						wmParam[ii][3] = modifier;													//수정자
						wmParam[ii][4] = slabUtils.trim(jrRow.getFieldString("YD_STK_COL_GP" ));	//야드적치열구분
						wmParam[ii][5] = slabUtils.trim(jrRow.getFieldString("YD_STK_BED_NO" ));	//야드적치Bed번호
						wmParam[ii][6] = slabUtils.trim(jrRow.getFieldString("YD_STK_LYR_NO" ));	//야드적치단번호
						wmParam[ii][7] = slabUtils.trim(jrRow.getFieldString("YD_UP_COLL_SEQ"));	//야드권상모음순서
					}
					
					commDao.upsBatch("WrkBookMtl", wmParam, logId, methodNm);
				}
			}

			//상차출발위치, 상차도착위치, 하차도착위치 Set
			ydCarldLevLoc = ydEqpId.substring(0, 1) + ydCurrBayGp + ydEqpId.substring(2, 6);
			if ("".equals(ydCarldWrkBookId)) {
				//대차 상차작업예약이 없으면
				if ("".equals(ydBayGpTo)) {
					ydCarldStopLoc = ydEqpId.substring(0, 1) + ydHomeBayGp + ydEqpId.substring(2, 6);
				} else {
					ydCarldStopLoc = ydEqpId.substring(0, 1) + ydBayGpTo + ydEqpId.substring(2, 6);
				}
			} else {
				//대차 상차작업예약이 있으면
				ydCarldStopLoc = ydEqpId.substring(0, 1) + ydBayGp     + ydEqpId.substring(2, 6);
				ydCarudStopLoc = ydEqpId.substring(0, 1) + ydAimBayGp  + ydEqpId.substring(2, 6);
			}

			//상차출발위치와 상차도착위치가 같으면 상차도착 상태
			if (ydCarldLevLoc.equals(ydCarldStopLoc)) {
				ydCarProgStat = "2"; //야드차량진행상태(상차도착)
			}
			
			/**********************************************************
			* 3. 대차스케줄 생성 또는 수정
			* 3.1  대차스케줄 없으면 야드대차스케쥴ID 생성하여 대차스케줄 생성
			* 3.1  대차스케줄 있으면 대차스케줄 수정
			**********************************************************/
			if ("".equals(ydTcarSchId)) {
				ydTcarSchId = commDao.getSeqId(logId, methodNm, "TcarSch");

				if ("".equals(ydTcarSchId)) {
					throw new Exception( "대차스케줄ID 생성 중 오류가 발생하였습니다.");
				}
			}
			
			jrParam.setField("V_YD_TCAR_SCH_ID"      , ydTcarSchId     );	//야드대차스케쥴ID
			jrParam.setField("V_YD_CAR_PROG_STAT"    , ydCarProgStat   );	//야드차량진행상태
			jrParam.setField("V_YD_CARLD_WRK_BOOK_ID", ydCarldWrkBookId);	//야드상차작업예약ID
			jrParam.setField("V_YD_CARLD_LEV_LOC"    , ydCarldLevLoc   );	//야드상차출발위치
			jrParam.setField("V_YD_CARLD_STOP_LOC"   , ydCarldStopLoc  );	//야드상차정지위치
			jrParam.setField("V_YD_CARUD_STOP_LOC"   , ydCarudStopLoc  );	//야드하차정지위치
			
			//대차스케줄 수정 또는 생성
			commDao.updTcarSch("InsSch", jrParam);

			if ("0".equals(ydCarProgStat)) {
				/**********************************************************
				* 4. 상차출발위치와 상차도착위치가 다르면 공대차출발지시 전송
				*  - C연주정정L2(YDC3L006, YDC7L006), 후판Slab야드L2(YDY3L006)
				**********************************************************/
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDC3L006", jrParam));
			} else if (!"".equals(ydCarldWrkBookId) && "".equals(ydWbookIdCurr)) {
				/**********************************************************
				* 5. 상차출발위치와 상차도착위치가 같고 신규 작업예약ID이면 크레인스케줄 호출
				**********************************************************/
				//크레인스케줄 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, modifier);

				jrYdMsg.setField("YD_WBOOK_ID"  , ydCarldWrkBookId); //야드작업예약ID
				jrYdMsg.setField("YD_SCH_ST_GP" , "A"             ); //야드스케쥴기동구분(Auto)
				jrYdMsg.setField("YD_SCH_REQ_GP", "6"             ); //야드스케쥴요청구분(공대차도착)

				jrRtn = slabUtils.addSndData(jrRtn, this.getCrnSchMsg(jrYdMsg));
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : Pickup-Bed 대차(설비ID) 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param String ydStkColGp
	 *      @param String ydStkBedNo
	 *      @return String
	 *      @throws DAOException
	*/
	public String getPickupBedTcar(String ydStkColGp, String ydStkBedNo, String logId, String methodNm) throws DAOException {
		try {
			String ydEqpId = "";
			
			if ("ACPUP7".equals(ydStkColGp) && "01".equals(ydStkBedNo)) {
				ydEqpId = "AXTC04";
			} else if ("ACPUP7".equals(ydStkColGp) && "07".equals(ydStkBedNo)) {
				ydEqpId = "AXTC05";
			} else if ("ADPUP8".equals(ydStkColGp) && "01".equals(ydStkBedNo)) {
				ydEqpId = "AXTC06";
			}

			return ydEqpId;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : Bed용도구분 설정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return int
	 *      @throws DAOException
	*/
	public int setBedUsgGp(JDTORecord jrParam) throws DAOException {
		String methodNm = "Bed용도구분설정[SlabYdComm.setBedUsgGp] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String exceptionYn = "Y";

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String ydStkColGp    = slabUtils.trim(jrParam.getFieldString("V_YD_STK_COL_GP"    )); //야드적치열구분
			String ydStkBedNo    = slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_NO"    )); //야드적치Bed번호
			String ydStkBedUsgGp = slabUtils.trim(jrParam.getFieldString("V_YD_STK_BED_USG_GP")); //야드적치Bed용도구분
			String modifier      = slabUtils.trim(jrParam.getFieldString("V_MODIFIER"         )); //수정자

			//Exception이 발생해도 변경되지 않은 정보를 L2로 전송하기 위해 관리
			if ("C3YDL001".equals(modifier) || "C7YDL001".equals(modifier)) {
				exceptionYn = "N";
			}
			
			//처리건수
			int trtCnt = 0;
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydStkColGp.length() < 6) {
				throw new Exception("적치열(YD_STK_COL_GP) 이상 [" + ydStkColGp + "]");
			} else if ("".equals(ydStkBedNo)) {
				throw new Exception("적치Bed(YD_STK_BED_NO) 없음");
			} else if ("".equals(ydStkBedUsgGp)) {
				throw new Exception("용도구분(YD_STK_BED_USG_GP) 없음");
			}

			//대차 설비ID 조회
			String ydEqpId = this.getPickupBedTcar(ydStkColGp, ydStkBedNo, logId, methodNm);

			//Pickup,대차Bed 상태정보 조회
			String ydStkBedUsgGpPu  = "";	//야드적치Bed용도구분(Pickup)
			String ydCurrBayGp      = "";	//야드현재동구분(대차)
			String ydCarldWrkBookId = "";	//야드상차작업예약ID(대차)
			int    bedMtlShPu       = 0;	//Bed적치매수(Pickup)
			int    bedMtlShTc       = 0;	//Bed적치매수(대차)

			jrParam.setField("V_YD_STK_COL_GP", ydStkColGp); //야드적치열구분(Pickup)
			jrParam.setField("V_YD_STK_BED_NO", ydStkBedNo); //야드적치Bed번호(Pickup)
			jrParam.setField("V_YD_EQP_ID"    , ydEqpId   ); //야드설비ID(대차)
			jrParam.setField("V_MODIFIER"     , modifier  ); //수정자

			JDTORecordSet jsChk = commDao.getStat("PuBedTcar", jrParam);

			if (jsChk != null && jsChk.size() > 0) {
				JDTORecord jrChk = jsChk.getRecord(0);
				ydStkBedUsgGpPu  = slabUtils.trim(jrChk.getFieldString("YD_STK_BED_USG_GP"   ));
				ydCurrBayGp      = slabUtils.trim(jrChk.getFieldString("YD_CURR_BAY_GP"      ));
				ydCarldWrkBookId = slabUtils.trim(jrChk.getFieldString("YD_CARLD_WRK_BOOK_ID"));
				bedMtlShPu       = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("BED_MTL_SH_PU"),"0"));
				bedMtlShTc       = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("BED_MTL_SH_TC"),"0"));
			} else {
				throw new Exception("Pickup Bed [" + ydStkColGp + "-" + ydStkBedNo + "] 또는 대차 [" + ydEqpId + "] 정보 없음");
			}

			/**********************************************************
			* 2. 용도구분이 변경되었으면
			*  - 대차 Bed이면 대차정보 Update
			*  - 적치Bed Table에 용도구분을 Update
			**********************************************************/
			if (!ydStkBedUsgGp.equals(ydStkBedUsgGpPu)) {
				//대차 Bed이면 대차정보 Update
				if (!"".equals(ydEqpId)) {
					//대차에 재료가 있는 상태에서는 용도 변경 불가
					if (!ydCurrBayGp.equals(ydStkColGp.substring(1, 2))) {
						throw new Exception("대차[" + ydEqpId + "] 현재동[" + ydCurrBayGp + "]이 Pickup Bed[" + ydStkColGp + "] 동과 달라 용도변경 불가");
					} else if (bedMtlShTc > 0) {
						throw new Exception("대차[" + ydEqpId + "] 적치재료[" + bedMtlShTc + " 매]가 존재하여 용도변경 불가");
					}

					//대차 적치열
					String ydStkColGpTc = ydStkColGp.substring(0, 2) + ydEqpId.substring(2);

					//용도구분에 따라 Pickup Bed <-> 대차 전환
					if ("C".equals(ydStkBedUsgGp)) {
						//대차로 전환하는 경우(용도구분:S -> C)
						if (bedMtlShPu > 0) {
							//Pickup Bed 재료가 있으면 대차로 이동
							jrParam.setField("V_YD_STK_COL_GP_FR", ydStkColGp  ); //야드적치열구분(Pickup)
							jrParam.setField("V_YD_STK_BED_NO_FR", ydStkBedNo  ); //야드적치Bed번호(Pickup)
							jrParam.setField("V_YD_STK_COL_GP_TO", ydStkColGpTc); //야드적치열구분(대차)
							jrParam.setField("V_YD_STK_BED_NO_TO", "01"        ); //야드적치열구분(대차)

							commDao.updSlabYd("StkMtlMV", jrParam);
						}

						//해당 대차 Bed 적치가능(L) 상태로 변경
						jrParam.setField("V_YD_STK_COL_GP"      , ydStkColGpTc); //야드적치열구분(대차)
						jrParam.setField("V_YD_STK_BED_NO"      , "01"        ); //야드적치Bed번호(대차)
						jrParam.setField("V_YD_STK_BED_ACT_STAT", "L"         ); //야드적치Bed활성상태(적치가능)

						commDao.updStat("StkBedAct", jrParam);
					} else {
						//Pickup Bed로 전환하는 경우(용도구분:C -> S)
						if (!"".equals(ydCarldWrkBookId)) {
							throw new Exception("대차[" + ydEqpId + "] 상차작업예약[" + ydCarldWrkBookId + "]이 존재하므로 Pickup Bed로 전환 불가");
						}
						//전 대차 Bed 비활성화(C) 상태로 변경
						jrParam.setField("V_YD_STK_COL_GP", ydStkColGpTc.substring(0, 1) + "_" + ydStkColGpTc.substring(2)); //야드적치열구분(대차전체Bed)

						//적치Bed(전체) 비활성화
						commDao.updStat("StkBedActCA", jrParam);
					}
				}
				
				jrParam.setField("V_YD_STK_COL_GP"    , ydStkColGp   ); //야드적치열구분
				jrParam.setField("V_YD_STK_BED_NO"    , ydStkBedNo   ); //야드적치Bed번호
				jrParam.setField("V_YD_STK_BED_USG_GP", ydStkBedUsgGp); //야드적치Bed용도구분

				//적치Bed Table에 용도구분을 Update
				commDao.updStat("StkBedUsg", jrParam);
				
				trtCnt = 1;
			} else {
				slabUtils.printLog(logId, "수불구 용도구분 변경 없음 [" + ydStkColGp + "-" + ydStkBedNo + " : " + ydStkBedUsgGp + "]", "SL");
			}

			slabUtils.printLog(logId, methodNm, "S-");
			
			return trtCnt;
		} catch (Exception e) {
			if ("N".equals(exceptionYn)) {
				slabUtils.printErrorLog(slabUtils.makeErrorLog(logId, methodNm, e), this, e);
				return 0;
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : Slab이송완료실적 전송
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param String stlAppearGp
	 *      @param String stlNo
	 *      @return String
	 *      @throws DAOException
	*/
	public String sndYDPTJ001(String stlAppearGp, String stlNo) throws DAOException {
		String methodNm = "Slab이송완료실적 전송[SlabYdComm.sndYDPTJ001]";
		String logId = "YDPTJ001";
		
		try {
			slabUtils.printLog(logId, methodNm, "S+");

			if ("".equals(stlNo)) {
				return "재료번호가 없습니다.";
			} else if ("".equals(stlAppearGp)) {
				return "재료외형구분이 없습니다.";
			}

			String msgId = "";

			if ("B".equals(stlAppearGp)) {
				msgId = "YDPTJ001Mslab";
			} else {
				msgId = "YDPTJ001Slab";
			}
			
			//조회 Param 생성 - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, logId);

			jrParam.setField("V_STL_NO", stlNo); //재료번호

			//진행관리 Slab이송완료실적(진도변경) 전송
			JDTORecordSet sndData = commDao.getMsgL3(msgId, jrParam);

			if (sndData.size() > 0) {
				//송신 공통 EJB를 이용하여 전송
				EJBConnector ejbConn = new EJBConnector("default", "YdCommEJB", this);
				ejbConn.trx("sndToJMS", new Class[] { JDTORecord.class }, new Object[] { sndData.getRecord(0) });
			} else {
				return "전송할 정보가 없습니다.";
			}

			slabUtils.printLog(logId, methodNm, "S-");
			
			return "정상";
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	// 기동금지 스케줄도 작업 예약은 가능하도록 수정 2024.02.15
	// 스케줄 문자열 비교 메소드 (A_PT__LM 스케줄은 예약 가능...)
	public boolean isMatch(String word, int n, String pattern, int m) {
		
		if (m == pattern.length()) {
			return n == word.length();
		}
		
		if (n == word.length()) {
			for (int i = m; i < pattern.length(); i++) {
				if(pattern.charAt(i) != '*') {
					return false;
				}
			}
			
			return true;
		}
		
		if (pattern.charAt(m) == '?' || pattern.charAt(m) == word.charAt(n)) {
			return isMatch(word, n + 1, pattern, m + 1);
		}
		
		if (pattern.charAt(m) == '*') {
			return isMatch(word, n + 1, pattern, m) || isMatch(word, n, pattern, m + 1);
		}
		
		return false;
		
	}
	// 기동금지 스케줄도 작업 예약은 가능하도록 수정 2024.02.15
	// 스케줄 문자열 비교 메소드 (A_PT__LM 스케줄은 예약 가능...)
	public boolean isMatch(String word, String pattern) {
		return isMatch(word, 0, pattern, 0);
	}

}
