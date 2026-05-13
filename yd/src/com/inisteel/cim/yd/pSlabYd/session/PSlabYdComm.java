/**
 * @(#)PSlabComm
 *
 * @version          V1.00
 * @author           염용선
 * @date             2020/05/06
 * 
 * @description      Slab야드 공통 처리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  =  ======  ======  ==
 * V1.00  2020/05/06   염용선      염용선      최초 등록
 */
package com.inisteel.cim.yd.pSlabYd.session;

import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.cm.message.MessageSenderAuto;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.pSlabCommon.util.PSlabYdConstant;
import com.inisteel.cim.yd.pSlabCommon.util.PSlabYdUtils;
import com.inisteel.cim.yd.pSlabYd.dao.PSlabYdCommDAO;

/**
 * [A] 클래스명 : Slab야드 공통
 * 
 */


public class PSlabYdComm {

	private static final long serialVersionUID = 1L;
	private PSlabYdUtils slabUtils = new PSlabYdUtils();
	private PSlabYdCommDAO commDao = new PSlabYdCommDAO();
	
	/***************************************************************************
	 * 공통 Check
	 **************************************************************************/
	
	/**
	 *      [A] 오퍼레이션명 : 설비상태 Check
	 *      염용선 2020 06 11
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord chkEqpStat(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "설비상태Check[PSlabComm.chkEqpStat] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			jrRtn.setField("YD_L3_HD_RS_CD", "EQ99"); //야드L3처리결과코드(Error)
			jrRtn.setField("YD_L3_MSG"     , "오류:설비상태Check 예상치 못한 오류"); //야드L3MESSAGE(40Byte)

			//수신 항목 값
			String ydEqpId = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); //야드설비ID
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
				//throw new Exception(ydL3Msg);
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", ydL3Msg);
				return jrRtn;
			}
			
			/**********************************************************
			* 2. 설비상태 Check
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, "");

			jrParam.setField("YD_EQP_ID", ydEqpId); //야드설비ID

			//JDTORecordSet jsChk = commDao.getStat("Eqp", jrParam);
			/*설비상태조회
			SELECT YD_EQP_STAT
			      ,DECODE(YD_EQP_WRK_MODE,'1','1','0') AS YD_EQP_WRK_MODE
			  FROM TB_YD_EQP EQ
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
			   AND DEL_YN    = 'N'
		    */
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.getStatEqp", logId, methodNm, "설비상태 조회");
			
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
			}
			
			
			
			
			
			
			
			if (!"".equals(ydL3Msg)) {
				jrRtn.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				jrRtn.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				//throw new Exception(ydL3Msg);
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", ydL3Msg);
				return jrRtn;
			}
			
			jrRtn.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드
			jrRtn.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "");
			
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
	 *      염용선 2020 06 11
	 *      
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord chkSchCd(JDTORecord rcvMsg) {
		String methodNm = "스케줄코드Check[PSlabYdComm.chkSchCd] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			jrRtn.setField("YD_L3_HD_RS_CD", "SC99"); //야드L3처리결과코드(Error)
			jrRtn.setField("YD_L3_MSG"     , "오류:스케줄코드Check 예상치 못한 오류"); //야드L3MESSAGE(40Byte)
			
			//수신 항목 값
			String ydSchCd = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_CD")); //야드스케쥴코드
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
				//throw new Exception(ydL3Msg);
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", ydL3Msg);
				return jrRtn;
			}
			
			/**********************************************************
			* 2. 크레인스케줄 상태 Check
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, "");

			jrParam.setField("YD_SCH_CD", ydSchCd); //야드스케쥴코드

			//야드스케쥴금지유무 조회
			//JDTORecordSet jsChk = commDao.getStat("SchCd", jrParam);
			/*스케줄코드상태조회 
			SELECT SR.YD_SCH_CD
			      ,SR.YD_SCH_PROH_EXN
			      ,SR.YD_EQP_ID
			      ,SR.YD_SCH_PRIOR
			      ,SR.YD_WRK_CRN_PRIOR  --강제권상요구에서 사용
			      ,CS.YD_WRK_ABLE_SH    --야드작업가능매수
			      ,CS.YD_WRK_ABLE_WT    --야드작업가능중량
			      ,CS.YD_CRN_TONG_H     --야드크레인집게높이
			      ,CS.YD_CRN_TONG_W_TOL --야드크레인집게폭허용오차
			  FROM TB_YD_CRNSPEC CS
			      ,(SELECT SR.YD_SCH_CD
			              ,SR.YD_SCH_PROH_EXN
			              ,CASE WHEN WC.YD_EQP_STAT != 'B' AND WC.YD_EQP_WRK_MODE = '1' THEN SR.YD_WRK_CRN
			              	    WHEN AC.YD_EQP_STAT != 'B' AND AC.YD_EQP_WRK_MODE = '1' THEN SR.YD_ALT_CRN
			               END AS YD_EQP_ID
			              ,CASE WHEN WC.YD_EQP_STAT != 'B' AND WC.YD_EQP_WRK_MODE = '1' THEN SR.YD_WRK_CRN_PRIOR
			              	    WHEN AC.YD_EQP_STAT != 'B' AND AC.YD_EQP_WRK_MODE = '1' THEN SR.YD_ALT_CRN_PRIOR
			               END AS YD_SCH_PRIOR
			              ,SR.YD_WRK_CRN_PRIOR
			          FROM TB_YD_SCHRULE SR
			              ,TB_YD_EQP     WC
			              ,TB_YD_EQP     AC
			         WHERE SR.YD_WRK_CRN = WC.YD_EQP_ID(+)
			           AND SR.YD_ALT_CRN = AC.YD_EQP_ID(+)
			           AND SR.YD_SCH_CD  = :V_YD_SCH_CD
			           AND SR.DEL_YN     = 'N') SR
			 WHERE SR.YD_EQP_ID = CS.YD_EQP_ID(+)
             */
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.getStatSchCd", logId, methodNm, "스케줄코드 상태 조회");
			 
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
				//throw new Exception(ydL3Msg);
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", ydL3Msg);
				return jrRtn;
			}
			
			jrRtn.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드
			jrRtn.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
			jrRtn.setField("RTN_CD"	, "1");
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
	 *      염용선 2020-07-10
	 *      @param JDTORecord jrParam
	 *      @return JDTORecord jrRtn
	 *      @throws DAOException
	*/
	public JDTORecord chkSchCdEqp(JDTORecord jrParam) {
		String methodNm = "스케줄코드 및 크레인 Check[PSlabComm.chkSchCdEqp] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과
			//수신 항목 값
			String ydSchCd = slabUtils.trim(jrParam.getFieldString("YD_SCH_CD")); //야드스케쥴코드

			if ("".equals(ydSchCd)) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "스케쥴코드 없음");
				return jrRtn;
			} else if (ydSchCd.length() < 8) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "스케쥴코드[" + ydSchCd + "] 이상");
				return jrRtn;
			}
			
			//야드스케쥴금지유무 조회
			//JDTORecordSet jsChk = commDao.getStat("SchCd", jrParam);
			/*스케줄코드상태조회 
			SELECT SR.YD_SCH_CD
			      ,SR.YD_SCH_PROH_EXN
			      ,SR.YD_EQP_ID
			      ,SR.YD_SCH_PRIOR
			      ,SR.YD_WRK_CRN_PRIOR  --강제권상요구에서 사용
			      ,CS.YD_WRK_ABLE_SH    --야드작업가능매수
			      ,CS.YD_WRK_ABLE_WT    --야드작업가능중량
			      ,CS.YD_CRN_TONG_H     --야드크레인집게높이
			      ,CS.YD_CRN_TONG_W_TOL --야드크레인집게폭허용오차
			  FROM TB_YD_CRNSPEC CS
			      ,(SELECT SR.YD_SCH_CD
			              ,SR.YD_SCH_PROH_EXN
			              ,CASE WHEN WC.YD_EQP_STAT != 'B' AND WC.YD_EQP_WRK_MODE = '1' THEN SR.YD_WRK_CRN
			              	    WHEN AC.YD_EQP_STAT != 'B' AND AC.YD_EQP_WRK_MODE = '1' THEN SR.YD_ALT_CRN
			               END AS YD_EQP_ID
			              ,CASE WHEN WC.YD_EQP_STAT != 'B' AND WC.YD_EQP_WRK_MODE = '1' THEN SR.YD_WRK_CRN_PRIOR
			              	    WHEN AC.YD_EQP_STAT != 'B' AND AC.YD_EQP_WRK_MODE = '1' THEN SR.YD_ALT_CRN_PRIOR
			               END AS YD_SCH_PRIOR
			              ,SR.YD_WRK_CRN_PRIOR
			          FROM TB_YD_SCHRULE SR
			              ,TB_YD_EQP     WC
			              ,TB_YD_EQP     AC
			         WHERE SR.YD_WRK_CRN = WC.YD_EQP_ID(+)
			           AND SR.YD_ALT_CRN = AC.YD_EQP_ID(+)
			           AND SR.YD_SCH_CD  = :V_YD_SCH_CD
			           AND SR.DEL_YN     = 'N') SR
			 WHERE SR.YD_EQP_ID = CS.YD_EQP_ID(+)
             */
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.getStatSchCd", logId, methodNm, "스케줄코드 상태 조회");
			 
			String ydSchProhExn = "";  //야드스케쥴금지유무
			String ydEqpId      = "";  //야드설비ID(작업크레인)

			if (jsChk.size() > 0) {
				ydSchProhExn = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_PROH_EXN"));
				ydEqpId      = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_ID"      ));
			}

			if ("".equals(ydSchProhExn)) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "스케쥴코드[" + ydSchCd + "] 정보 없음");
				return jrRtn;
			} else if ("Y".equals(ydSchProhExn)) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "스케쥴코드[" + ydSchCd + "] 기동금지");
				return jrRtn;
			} else if ("".equals(ydEqpId)) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "스케쥴코드[" + ydSchCd + "] 작업가능 크레인 없음");
				return jrRtn;
			}

			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn = jsChk.getRecord(0);
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "크레인 Check 완료");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 스케줄코드 조회
	 *      염용선 2020-09-04
	 *      @param JDTORecord jrParam
	 *      @return JDTORecord jrRtn
	 *      @throws DAOException
	*/
	public JDTORecord getSchCd(JDTORecord jrParam) {
		String methodNm = "스케줄코드 조회[PSlabComm.getSchCd] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			JDTORecord jrRtn    = slabUtils.getParam(logId, methodNm, "");
			//수신 항목 값
			String ydSchCd     = slabUtils.trim(jrParam.getFieldString("YD_SCH_CD"     )); //야드스케쥴코드
			String ydStkColGp  = slabUtils.trim(jrParam.getFieldString("YD_STK_COL_GP" )); //야드적치열구분
			String ydStkBedNo  = slabUtils.trim(jrParam.getFieldString("YD_STK_BED_NO" )); //야드적치Bed번호
			String ydSchWhioGp = slabUtils.trim(jrParam.getFieldString("YD_SCH_WHIO_GP")); //야드스케쥴입출고구분
			
			//야드스케쥴코드 값이 없으면
			if (ydSchCd.length() < 7) {
				if (ydStkColGp.length() < 6) {
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "적치열구분(YD_STK_COL_GP) 이상 : [" + ydStkColGp + "]");
					return jrRtn;
				} else if ("".equals(ydStkBedNo)) {
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "적치Bed번호(YD_STK_BED_NO) 없음");
					return jrRtn;
				} else if (!"L".equals(ydSchWhioGp) && !"U".equals(ydSchWhioGp) && !"M".equals(ydSchWhioGp)) {
					//L:입고(수입,하차,Take-Out,Carry-Out), U:출고(불출,보급,상차,Take-In,Carry-In), M:이적
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "스케쥴입출고구분(YD_SCH_WHIO_GP) 이상 : [" + ydSchWhioGp + "]");
					return jrRtn;
				}
	
				String ydGp       = ydStkColGp.substring(0, 1); //야드구분
				String ydEqpGp    = ydStkColGp.substring(2, 4); //야드설비구분
				String ydStkColNo = ydStkColGp.substring(4, 6); //야드적치열번호
				String ydSchDivGp = "M";	//야드스케쥴분할구분(중간)

				//C연주 Pickup Bed 적치열구분과 스케줄코드 체계가 다름
				/*
				 * 해당없음 - 염용선
				 * if ("A".equals(ydGp) && "PU".equals(ydEqpGp)) {
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
				}  */  
				
				ydSchCd = ydStkColGp.substring(0, 4) + ydStkColNo + ydSchWhioGp + ydSchDivGp;
				
				jrParam.setField("YD_SCH_CD", ydSchCd); //야드스케쥴코드
			} 
//	해당 없음 : YYS		else if (ydSchCd.length() == 7) {
//				String ydSchDivGp = "M";	//야드스케쥴분할구분(중간)
//	
//				//C연주 장입 Pickup Bed 적치열구분과 스케줄코드 체계가 다름 : ADPU01,ACPU02,ADPU03, AAPU04, ABPU06, ACPU07
//				if (ydSchCd.startsWith("ADPU01") ||ydSchCd.startsWith("ADPU03") ||ydSchCd.startsWith("ACPU02") || ydSchCd.startsWith("AAPU04") || ydSchCd.startsWith("ABPU06")) {
//					if ("01".equals(ydStkBedNo) || "02".equals(ydStkBedNo) || "03".equals(ydStkBedNo) || "04".equals(ydStkBedNo)) {
//						ydSchDivGp = "L"; //왼쪽
//					} else {
//						ydSchDivGp = "R"; //오른쪽
//					}
//				}else if (ydSchCd.startsWith("ACPU07") || ydSchCd.startsWith("ADPU08")) {
//					if ("01".equals(ydStkBedNo) || "02".equals(ydStkBedNo) || "03".equals(ydStkBedNo)) {
//						ydSchDivGp = "L"; //왼쪽
//					} else {
//						ydSchDivGp = "R"; //오른쪽
//					}
//				}else if (ydSchCd.startsWith("ABPU05")) {
//					if ("01".equals(ydStkBedNo) || "02".equals(ydStkBedNo)) {
//						ydSchDivGp = "L"; //왼쪽
//					} else {
//						ydSchDivGp = "R"; //오른쪽
//					}
//				}
//				
//				ydSchCd = ydSchCd + ydSchDivGp;  
//				
//				jrParam.setField("YD_SCH_CD", ydSchCd); //야드스케쥴코드
//			}
			
			//야드스케쥴금지유무 조회
			//JDTORecordSet jsChk = commDao.getStat("SchCd", jrParam);
			/*스케줄코드상태조회 
			SELECT SR.YD_SCH_CD
			      ,SR.YD_SCH_PROH_EXN
			      ,SR.YD_EQP_ID
			      ,SR.YD_SCH_PRIOR
			      ,SR.YD_WRK_CRN_PRIOR  --강제권상요구에서 사용
			      ,CS.YD_WRK_ABLE_SH    --야드작업가능매수
			      ,CS.YD_WRK_ABLE_WT    --야드작업가능중량
			      ,CS.YD_CRN_TONG_H     --야드크레인집게높이
			      ,CS.YD_CRN_TONG_W_TOL --야드크레인집게폭허용오차
			  FROM TB_YD_CRNSPEC CS
			      ,(SELECT SR.YD_SCH_CD
			              ,SR.YD_SCH_PROH_EXN
			              ,CASE WHEN WC.YD_EQP_STAT != 'B' AND WC.YD_EQP_WRK_MODE = '1' THEN SR.YD_WRK_CRN
			              	    WHEN AC.YD_EQP_STAT != 'B' AND AC.YD_EQP_WRK_MODE = '1' THEN SR.YD_ALT_CRN
			               END AS YD_EQP_ID
			              ,CASE WHEN WC.YD_EQP_STAT != 'B' AND WC.YD_EQP_WRK_MODE = '1' THEN SR.YD_WRK_CRN_PRIOR
			              	    WHEN AC.YD_EQP_STAT != 'B' AND AC.YD_EQP_WRK_MODE = '1' THEN SR.YD_ALT_CRN_PRIOR
			               END AS YD_SCH_PRIOR
			              ,SR.YD_WRK_CRN_PRIOR
			          FROM TB_YD_SCHRULE SR
			              ,TB_YD_EQP     WC
			              ,TB_YD_EQP     AC
			         WHERE SR.YD_WRK_CRN = WC.YD_EQP_ID(+)
			           AND SR.YD_ALT_CRN = AC.YD_EQP_ID(+)
			           AND SR.YD_SCH_CD  = :V_YD_SCH_CD
			           AND SR.DEL_YN     = 'N') SR
			 WHERE SR.YD_EQP_ID = CS.YD_EQP_ID(+)
             */
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.getStatSchCd", logId, methodNm, "스케줄코드 상태 조회");
			
			String ydSchProhExn = "";  //야드스케쥴금지유무
			String ydEqpId      = "";  //야드설비ID(작업크레인)

			if (jsChk.size() > 0) {
				ydSchProhExn = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_PROH_EXN"));
				ydEqpId      = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_ID"      ));
			}

			if ("".equals(ydSchProhExn)) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "스케쥴코드[" + ydSchCd + "] 정보 없음");
				return jrRtn;
			} else if ("Y".equals(ydSchProhExn)) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "스케쥴코드[" + ydSchCd + "] 기동금지");
				return jrRtn;
			} else if ("".equals(ydEqpId)) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "스케쥴코드[" + ydSchCd + "] 작업가능 크레인 없음");
				return jrRtn;
			}

			slabUtils.printLog(logId, methodNm, "S-");
			
			jrRtn  = jsChk.getRecord(0);
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "");
			return jrRtn;
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
		String methodNm = "크레인사양Check[PSlabComm.chkCrnSpec] < " + jrCrnSpec.getResultMsg();
		String logId = jrCrnSpec.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			
			String crnSpecOvGp = ""; //결과
			
			String Is_plate_StoA_Scarfing = "N";
			
			Is_plate_StoA_Scarfing = slabUtils.nvl(jrCrnSpec.getFieldString("PLATE_SIDE_SCARFING_YN"   ),"N");
			
			slabUtils.printParam(logId + "크레인사양Check Param : jrCrnSpec", jrCrnSpec);
			
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
			
			slabUtils.printLog(logId, methodNm, "S-");
			
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
	 *      염용선 2020 06 11
	 *      @param String JDTORecord rcvMsg
	 *      @return String JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getCrnSchMsg(JDTORecord jrParam) {
		String methodNm = "크레인스케줄전문조회[PSlabYdComm.getCrnSchMsg] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			
			String crnSchStGp = "N";														//크레인스케줄기동구분(New)
			String currDate   = slabUtils.getDateTime14();									//현재시각
			String ydGp       = slabUtils.trim(jrParam.getFieldString("YD_GP"        ));	//야드구분
			String ydWbookId  = slabUtils.trim(jrParam.getFieldString("YD_WBOOK_ID"  ));	//야드작업예약ID
			String ydSchCd    = slabUtils.trim(jrParam.getFieldString("YD_SCH_CD"    ));	//야드스케쥴코드
			String ydEqpId    = slabUtils.trim(jrParam.getFieldString("YD_EQP_ID"    ));	//야드설비ID
			String ydSchStGp  = slabUtils.trim(jrParam.getFieldString("YD_SCH_ST_GP" ));	//야드스케쥴기동구분
			String ydSchReqGp = slabUtils.trim(jrParam.getFieldString("YD_SCH_REQ_GP"));	//야드스케쥴요청구분
			String sModifier   = slabUtils.trim(jrParam.getFieldString("MODIFIER"   ));	//수정자
			String ejbCallYn  = slabUtils.trim(jrParam.getFieldString("EJB_CALL_YN"  ));	//EJBCall여부(trtCrnWrkBookMgtSS 이 메소드에서만 사용)
			//Return Value
			JDTORecord jrRtn  = slabUtils.getParam(logId, methodNm, sModifier);
			if ("".equals(ydWbookId) && "".equals(ydSchCd) && "".equals(ydEqpId)) {
				
					slabUtils.printLog(logId, "ejbCallYn ["+ejbCallYn+"] 크레인스케줄 기동을 위한 정보가 없습니다.", "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "크레인스케줄 기동을 위한 정보가 없습니다.");
					return jrRtn;
				
			}
			
			
			slabUtils.printLog(logId, "YD_GP(야드구분):"+ydGp+">>YD_WBOOK_ID//야드작업예약ID:"+ydWbookId
					+">>YD_SCH_CD//야드스케쥴코드:"+ydSchCd+">>YD_EQP_ID//야드설비ID:"+ydEqpId
					+">>YD_SCH_ST_GP//야드스케쥴기동구분:"+ydSchStGp+">>YD_SCH_REQ_GP//야드스케쥴요청구분:"+ydSchReqGp
					, "SL");
			//크레인스케줄기동구분 조회
			if (!"".equals(ydWbookId) && ("".equals(ydSchCd) || "".equals(ydEqpId))) {
				jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
				//JDTORecordSet jsChk = commDao.getCrnSchStGp("WB", jrParam);
				/*
				 *크레인스케줄기동구분조회 
					SELECT WB.YD_GP
					      ,WB.YD_SCH_CD
					      ,(SELECT SR.YD_WRK_CRN
					          FROM TB_YD_SCHRULE SR
					         WHERE SR.YD_SCH_CD = WB.YD_SCH_CD) AS YD_EQP_ID
					      ,(SELECT MIN(BR.ITEM_VALUE2)
					          FROM TB_YD_RULE BR
					         WHERE BR.DTL_ITEM1 = 'CRN_SCH_ST_GP'
					           AND BR.REPR_CD_GP = 'DYD100'
					           AND BR.CD_GP  = WB.YD_GP)        AS CRN_SCH_ST_GP
					  FROM TB_YD_WRKBOOK WB
					 WHERE WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND WB.DEL_YN      = 'N'
				 */
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.getCrnSchStGpWb", logId, methodNm, "크레인스케줄기동구분조회");
	    		
				if (jsChk.size() > 0) {
					ydGp       = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_GP"        ));	//야드구분
					ydSchCd    = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_CD"    ));	//야드스케쥴코드
					ydEqpId    = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_ID"    ));	//야드설비ID
					crnSchStGp = slabUtils.trim(jsChk.getRecord(0).getFieldString("CRN_SCH_ST_GP"));	//크레인스케줄기동구분
				} else {					
						slabUtils.printLog(logId, "작업예약ID[" + ydWbookId + "]의 정보가 없어 크레인스케줄을 기동할 수 없습니다.", "SL");
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", "작업예약ID[" + ydWbookId + "]의 정보가 없어 크레인스케줄을 기동할 수 없습니다.");
						return jrRtn;					
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
				//JDTORecordSet jsChk = commDao.getCrnSchStGp("SG", jrParam);
				/*
				 * 크레인스케줄기동구분조회
					SELECT BR.ITEM_VALUE2 AS CRN_SCH_ST_GP
					  FROM USRYDA.TB_YD_RULE BR
					 WHERE BR.DTL_ITEM1 = 'CRN_SCH_ST_GP'
					    AND BR.CD_GP  = 'D'
					    AND BR.REPR_CD_GP = 'DYD100'
				 */
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.getCrnSchStGp", logId, methodNm, "크레인스케줄기동구분조회");
	    		
				if (jsChk.size() > 0) {
					crnSchStGp = slabUtils.trim(jsChk.getRecord(0).getFieldString("CRN_SCH_ST_GP"));	//크레인스케줄기동구분
				}
			}

			//업무기준에 등록되어 있지 않으면 신규 스케줄
			if ("".equals(crnSchStGp)) {
				crnSchStGp = "N";
			}

			slabUtils.printLog(logId, "[작업예약ID:" + ydWbookId + ", 스케쥴코드:" + ydSchCd + ", 설비ID:" + ydEqpId + "]의 야드 크레인스케줄기동구분 : " + crnSchStGp, "SL");
			slabUtils.printLog(logId, "[EJBCall여부:" + ejbCallYn , "SL");
			
			//크레인스케줄 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = slabUtils.getParam(logId, jrParam.getResultMsg(), sModifier);
			
			
			
			if ("N".equals(crnSchStGp)) {
				
				//화면에서 작업예약을 선택해서 기동 시
				if (!"".equals(ydWbookId) && "M".equals(ydSchStGp)) {
					ydSchCd = "";	//야드스케쥴코드
					ydEqpId = "";	//야드설비ID
				}
			}
			//신 크레인스케줄 기동		
			
			jrYdMsg.setField("JMS_TC_CD", "YDYDJ401"); //Slab야드 크레인스케줄
			
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate  ); //JMSTC생성일시
			jrYdMsg.setField("YD_WBOOK_ID"       , ydWbookId ); //야드작업예약ID
			jrYdMsg.setField("YD_SCH_CD"         , ydSchCd   ); //야드스케쥴코드
			jrYdMsg.setField("YD_EQP_ID"         , ydEqpId   ); //야드설비ID
			jrYdMsg.setField("YD_SCH_ST_GP"      , ydSchStGp ); //야드스케쥴기동구분
			jrYdMsg.setField("YD_SCH_REQ_GP"     , ydSchReqGp); //야드스케쥴요청구분
			slabUtils.printLog(logId, "크레인스케줄기동구분: >>> ["+crnSchStGp+"], 크레인스케줄기동구분: >>> ["+ejbCallYn+"]", "SL");
			
			// trtCrnWrkBookMgtSS 호출시 ejbCallYn = Y 로 
			if ("N".equals(crnSchStGp) && "Y".equals(ejbCallYn)) {
				
				EJBConnector sndConn = new EJBConnector("default", "PSlabYdSchSeEJB", this); // rcvYDYDJ400 >> rcvYDYDJ401 로 변경
				JDTORecord jrRst = (JDTORecord)sndConn.trx("rcvYDYDJ401", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				String rtnCd	 = slabUtils.nvl(jrRst.getFieldString("RTN_CD"), "0");
				String rtnMsg	 = slabUtils.nvl(jrRst.getFieldString("RTN_MSG"), "");
				slabUtils.printLog(logId, rtnCd+"===RETURN VALUE CHECK========"+rtnMsg, "SL");
				if (rtnCd.equals("0")) {
					//작업지시 등 전송할 전문이 있으면 받아서 전송
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", rtnMsg);
					return jrRtn;
				}
				jrRtn = slabUtils.addSndData(jrRtn, jrRst);
			} else {
				jrRtn = slabUtils.addSndData(jrRtn, jrYdMsg);
			}
			
			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "크레인스케줄 기동이 처리 되었습니다.");
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업실적응답(YDY3L005) 전문 조회
	 *      염용선  2020 06 18
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getYDY3L005(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업실적응답 조회[PSlabComm.getYDY3L005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			JDTORecord sndMsg = JDTORecordFactory.getInstance().create();
			//수신 항목 값
			String msgId = "YDY3L005"; //전문ID
			String ydEqpId    = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID
			String ydL2WrGp   = slabUtils.trim(rcvMsg.getFieldString("YD_L2_WR_GP"   )); //야드L2실적구분
			String ydL3HdRsCd = slabUtils.trim(rcvMsg.getFieldString("YD_L3_HD_RS_CD")); //야드L3처리결과코드
			String ydL3Msg    = slabUtils.trim(rcvMsg.getFieldString("YD_L3_MSG"     )); //야드L3MESSAGE
			String ydRepA     = slabUtils.nvl(rcvMsg.getFieldString("A"), "");//A동 대표크레인
			String ydRepB     = slabUtils.trim(rcvMsg.getFieldString("B"        ));//B동 대표크레인
			
			String ydBayGP = slabUtils.trim(rcvMsg.getFieldString("BAY_GP"));
			
			String ydWrkProgStat = slabUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT"));
		if ("".equals(ydRepA)) {
			/*
			 * 
			 */
				
		
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				return null;
			}

			
			

			/**********************************************************
			* 2. 크레인작업실적응답 전문 생성
			**********************************************************/
			//야드L3Message가 없으면 생성
			if ("".equals(ydL3Msg)) {
				if ("U".equals(ydL2WrGp)) {
					ydL3Msg = "권상실적";//2
				} else if ("D".equals(ydL2WrGp)) {
					ydL3Msg = "권하실적";//4
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
			 if ("9999".equals(ydL3HdRsCd)){
				sbMsg = sbMsg.append(slabUtils.getRPad(" "       ,  1, " ")     ); //야드작업진행상태
				sbMsg = sbMsg.append(slabUtils.getRPad(" "       ,  8, " ")     ); //야드스케쥴코드
				sbMsg = sbMsg.append(slabUtils.getRPad(" "       , 18, " ")     ); //야드크레인스케쥴ID
			 }else{
				 //2021-06-07일 A2 크레인 무인화 테스트진행중 처리 L2에서 요구 : YYS
				 if(("2".equals(ydWrkProgStat) || "4".equals(ydWrkProgStat) ) 
						 && !"M".equals(ydL2WrGp) ){//권상 이거나 권하 이면 나머진는 
					sbMsg = sbMsg.append(slabUtils.getRPad(ydWrkProgStat,  1, " ")); //야드작업진행상태	
					 ///////////////////////////////
					sbMsg = sbMsg.append(slabUtils.getRPad(slabUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )),  8, " ")); //야드스케쥴코드
					sbMsg = sbMsg.append(slabUtils.getRPad(slabUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )), 18, " ")); //야드크레인스케쥴ID
				 }else{
					 sbMsg = sbMsg.append(slabUtils.getRPad(" "       ,  1, " ")     ); //야드작업진행상태 
					 sbMsg = sbMsg.append(slabUtils.getRPad(" "       ,  8, " ")     ); //야드스케쥴코드
					 sbMsg = sbMsg.append(slabUtils.getRPad(" "       , 18, " ")     ); //야드크레인스케쥴ID
				 }
				
			 }
			sbMsg = sbMsg.append(slabUtils.getRPad(ydL2WrGp  ,  1, " ")); //야드L2실적구분
			sbMsg = sbMsg.append(slabUtils.getRPad(ydL3HdRsCd,  4, " ")); //야드L3처리결과코드
			sbMsg = sbMsg.append(slabUtils.getRPad(ydL3Msg   , 40, " ")); //야드L3Message

			sndMsg = JDTORecordFactory.getInstance().create();

			sndMsg.addField("JMS_TC_CD"          , msgId                    ); //JMSTC코드
			sndMsg.addField("JMS_TC_CREATE_DDTT" , slabUtils.getDateTime14()); //JMSTC생성일시(yyyyMMddHHmmss)
			sndMsg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); //JMSTCMessage
		}else{
			ydL3Msg    = "";														//야드L3처리결과메세지
			
			if ("0000".equals(ydL3HdRsCd)) {
				ydL3Msg = ydL3Msg + "주행금지구역 설정 처리완료";
			} /*else if ("9999".equals(ydL3HdRsCd)) {
				ydL3Msg = ydL3Msg + "주행거리오류발생";
			}*/ else {
				ydL3Msg = ydL3Msg + " 오류 <" + logId + ">";
			}
			
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			// 없음
			
			
			/**********************************************************
			* 2. 크레인작업실적응답 전문 생성
			**********************************************************/
			//야드L3Message가 없으면 생성
			

			StringBuffer sbMsg = new StringBuffer();

			sbMsg = sbMsg.append(msgId                                      ); //전문ID
			sbMsg = sbMsg.append(slabUtils.getDateTime18()                  ); //생성일,생성시간(yyyy-MM-ddHH:mm:ss)
			sbMsg = sbMsg.append("I"                                        ); //전문구분
			sbMsg = sbMsg.append("0078"                                     ); //전문길이
			sbMsg = sbMsg.append(slabUtils.getRPad(" "	     , 29, " ")     ); //임시
			if("A".equals(ydBayGP)){
				sbMsg = sbMsg.append(slabUtils.getRPad(ydRepA	     ,  6, " ")     ); //A동 대표크레인
			}else if("B".equals(ydBayGP)){
				sbMsg = sbMsg.append(slabUtils.getRPad(ydRepB	     ,  6, " ")     ); //B동 대표크레인
			}
			
			sbMsg = sbMsg.append(slabUtils.getRPad(" "       ,  1, " ")     ); //야드작업진행상태
			sbMsg = sbMsg.append(slabUtils.getRPad(" "       ,  8, " ")     ); //야드스케쥴코드
			sbMsg = sbMsg.append(slabUtils.getRPad(" "       , 18, " ")     ); //야드크레인스케쥴ID
			sbMsg = sbMsg.append(slabUtils.getRPad("X"       ,  1, " ")     ); //야드L2실적구분
			sbMsg = sbMsg.append(slabUtils.getRPad(ydL3HdRsCd,  4, " ")     ); //야드L3처리결과코드
			sbMsg = sbMsg.append(slabUtils.getRPad(ydL3Msg   , 40, " ")     ); //야드L3Message

			sndMsg = JDTORecordFactory.getInstance().create();

			sndMsg.setResultCode(logId);		//Log ID
			sndMsg.setResultMsg(methodNm);	//Log Method Name
			sndMsg.addField("JMS_TC_CD"          , msgId                    ); //JMSTC코드
			sndMsg.addField("JMS_TC_CREATE_DDTT" , slabUtils.getDateTime14()); //JMSTC생성일시(yyyyMMddHHmmss)
			sndMsg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); //JMSTCMessage

		}
			//전송 Data Return
			return sndMsg;
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
	 *      염용선 2020-09-04
	 *      @param String JDTORecord rcvMsg
	 *      @return String JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvMsgToEjbCall(JDTORecord rcvMsg) {
		String methodNm = "전문수신처리EJBCall[PSlabComm.rcvMsgToEjbCall] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			String ejbMsgId = slabUtils.trim(rcvMsg.getFieldString("EJB_MSG_ID"));	//EJB Call 전문ID
			JDTORecordSet sendData = (JDTORecordSet)rcvMsg.getField("SEND_DATA");	//전송Data
			
			if ("".equals(ejbMsgId) || sendData == null) {
				return rcvMsg;
			}

			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//반환 결과
			JDTORecord jrMsg = JDTORecordFactory.getInstance().create();	//전문Message
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
						rcvClass = "PSlabYdL2RcvSeEJB";
					} else {
						if ("YDYD".equals(msgId.substring(0, 4))) {
							//JMS수신(내부)
							rcvClass = "PSlabYdSchSeEJB";
						} else {
							//JMS수신
							rcvClass = "PSlabYdL3RcvSeEJB";
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
	 * 대차스케줄
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 대차스케줄 상차완료 처리
	 *      염용선 2020-08-26
	 *      
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtTcarSchLdCmpl(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차스케줄 상차완료 처리[PSlabComm.trtTcarSchLdCmpl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String ydEqpId  = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID" )); //야드설비ID
			String ydUserId = slabUtils.trim(rcvMsg.getFieldString("MODIFIER" ));
			//전문 Return
			JDTORecord jrRtn = slabUtils.getParam(logId, methodNm, ydUserId);

			if ("".equals(ydEqpId)) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "설비ID가 없습니다.");
				return jrRtn;
			}

			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, ydUserId);

			jrParam.setField("YD_EQP_ID", ydEqpId);	//야드설비ID

			/**********************************************************
			* 1. 대차 상차 정보 Check
			**********************************************************/
			//대차상차스케쥴 정보 조회
			//JDTORecordSet jsChk = commDao.getTcarSch("LdCmpl", jrParam);
			/*
			 * --대차스케줄 대차상차완료 조회 
				SELECT YD_TCAR_SCH_ID
				      ,YD_CARLD_WRK_BOOK_ID
				      ,CASE WHEN TC_MTL_SH > 0 THEN
				            CASE WHEN TC_SCH_YN = 'N' OR
				                     (TC_SCH_YN = 'Y' AND TC_MTL_SH = WB_MTL_SH) THEN 'Y'
				            ELSE 'N' END
				       ELSE 'N' END AS TCAR_LD_CMPL_YN --대차상차완료여부
				      ,'대차스케쥴ID : '||YD_TCAR_SCH_ID||', 상차작업예약ID : '||YD_CARLD_WRK_BOOK_ID||', 스케쥴코드 : '||YD_SCH_CD||
				       ', 대차이송재료매수 : '||TO_CHAR(TC_MTL_SH)||', 작업예약재료매수 : '||TO_CHAR(WB_MTL_SH) AS TCAR_SCH_ST_MSG
				  FROM (SELECT TS.YD_TCAR_SCH_ID
				              ,TS.YD_CARLD_WRK_BOOK_ID
				              ,WB.YD_SCH_CD
				              ,DECODE(SUBSTR(WB.YD_SCH_CD,3,2),'TC','Y','N') AS TC_SCH_YN --대차스케줄여부
				              ,(SELECT COUNT(*)
				                  FROM TB_YD_TCARFTMVMTL TM
				                 WHERE TM.YD_TCAR_SCH_ID = TS.YD_TCAR_SCH_ID
				                   AND TM.DEL_YN = 'N') AS TC_MTL_SH --대차이송재료매수
				              ,(SELECT COUNT(*)
				                  FROM TB_YD_WRKBOOKMTL WM
				                 WHERE WM.YD_WBOOK_ID = WB.YD_WBOOK_ID) AS WB_MTL_SH --작업예약재료매수
				          FROM TB_YD_TCARSCH TS
				              ,TB_YD_WRKBOOK WB
				         WHERE TS.YD_CARLD_WRK_BOOK_ID = WB.YD_WBOOK_ID(+)
				           AND TS.YD_EQP_ID = :V_YD_EQP_ID
				           AND TS.DEL_YN    = 'N')

			 */
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.getTcarSchLdCmpl", logId, methodNm, "대차상차완료조회");
			
			if (jsChk == null || jsChk.size() <= 0) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "대차 상차스케쥴 정보가 없습니다.");
				return jrRtn;
		    }

			JDTORecord jrChk = jsChk.getRecord(0);

			String ydTcarSchId      = slabUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID"      )); //야드대차스케쥴ID
			String ydCarldWrkBookId = slabUtils.trim(jrChk.getFieldString("YD_CARLD_WRK_BOOK_ID")); //야드상차작업예약ID
			String tcarLdCmplYn     = slabUtils.trim(jrChk.getFieldString("TCAR_LD_CMPL_YN"     )); //대차상차완료여부

			slabUtils.printLog(logId, slabUtils.trim(jrChk.getFieldString("TCAR_SCH_ST_MSG")) + " >> 대차상차완료여부 : [" + tcarLdCmplYn + "]", "SL");
			
			jrParam.setField("YD_TCAR_SCH_ID"      , ydTcarSchId     ); //야드대차스케쥴ID
			jrParam.setField("YD_CARLD_WRK_BOOK_ID", ydCarldWrkBookId); //야드상차작업예약ID

			/**********************************************************
			* 2. 상차완료이면 하차작업예약 생성
			**********************************************************/
			if ("Y".equals(tcarLdCmplYn)) {
				//상차완료이면 하차작업예약 생성
				//야드하차작업예약ID 생성
				String ydCarudWrkBookId = commDao.getSeqId(logId, methodNm, "WrkBook");
				
				if ("".equals(ydCarudWrkBookId)) {
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "대차 하차작업예약ID 생성 중 오류가 발생하였습니다.");
					return jrRtn;
				}
				

				//작업예약 등록
				jrParam.setField("YD_CARUD_WRK_BOOK_ID", ydCarudWrkBookId); //야드하차작업예약ID

				//하차 작업예약 등록
				//commDao.updTcarSch("InsUdWb", jrParam);
				/*
				 * --대차스케줄 하차작업예약 등록 
					MERGE INTO TB_YD_WRKBOOK WB USING (
					SELECT :V_YD_CARUD_WRK_BOOK_ID AS YD_WBOOK_ID      --야드작업예약ID
					      ,:V_MODIFIER             AS MODIFIER         --수정자
					      ,SYSDATE                 AS MOD_DDTT         --수정일시
					      ,'N'                     AS DEL_YN           --삭제유무
					      ,WB.YD_GP                                    --야드구분
					      ,WB.YD_BAY_GP                                --야드동구분
					      ,WB.YD_SCH_CD                                --야드스케쥴코드
					      ,(SELECT SR.YD_WRK_CRN_PRIOR
					          FROM TB_YD_SCHRULE SR
					         WHERE SR.YD_SCH_CD = WB.YD_SCH_CD) AS YD_SCH_PRIOR --야드스케쥴우선순위
					      ,'W'                     AS YD_SCH_PROG_STAT --야드스케쥴진행상태(스케줄수행대기)
					      ,'M'                     AS YD_SCH_ST_GP     --야드스케쥴기동구분(Manual)
					      ,'1'                     AS YD_SCH_REQ_GP    --야드스케쥴요청구분(대차상차완료)
					      ,WB.YD_TO_LOC_DCSN_MTD                       --야드To위치결정방법
					      ,WB.YD_TO_LOC_GUIDE                          --야드To위치Guide
					      ,WB.YD_WRK_PLAN_TCAR                         --야드작업계획대차
					  FROM (SELECT WB.YD_GP
					              ,WB.YD_AIM_BAY_GP AS YD_BAY_GP
					              ,WB.YD_GP||WB.YD_AIM_BAY_GP||SUBSTR(WB.YD_WRK_PLAN_TCAR,3)||'LM' AS YD_SCH_CD
					              ,CASE WHEN WB.YD_TO_LOC_DCSN_MTD = 'F' AND WB.YD_AIM_BAY_GP != SUBSTR(WB.YD_TO_LOC_GUIDE,2,1)
					                    THEN 'S' ELSE WB.YD_TO_LOC_DCSN_MTD END AS YD_TO_LOC_DCSN_MTD
					              ,CASE WHEN WB.YD_TO_LOC_DCSN_MTD = 'F' AND WB.YD_AIM_BAY_GP != SUBSTR(WB.YD_TO_LOC_GUIDE,2,1)
					                    THEN ''  ELSE WB.YD_TO_LOC_GUIDE    END AS YD_TO_LOC_GUIDE
					              ,WB.YD_WRK_PLAN_TCAR
					          FROM TB_YD_WRKBOOK WB
					         WHERE WB.YD_WBOOK_ID = :V_YD_CARLD_WRK_BOOK_ID) WB
					) DD ON (WB.YD_WBOOK_ID = DD.YD_WBOOK_ID)
					WHEN NOT MATCHED THEN
					INSERT (WB.YD_WBOOK_ID       , WB.REGISTER       , WB.REG_DDTT        , WB.MODIFIER    , WB.MOD_DDTT     ,
					        WB.DEL_YN            , WB.YD_GP          , WB.YD_BAY_GP       , WB.YD_SCH_CD   , WB.YD_SCH_PRIOR ,
					        WB.YD_SCH_PROG_STAT  , WB.YD_SCH_ST_GP   , WB.YD_SCH_REQ_GP   , WB.YD_AIM_YD_GP, WB.YD_AIM_BAY_GP,
					        WB.YD_TO_LOC_DCSN_MTD, WB.YD_TO_LOC_GUIDE, WB.YD_WRK_PLAN_TCAR)
					VALUES (DD.YD_WBOOK_ID       , DD.MODIFIER       , DD.MOD_DDTT        , DD.MODIFIER    , DD.MOD_DDTT     ,
					        DD.DEL_YN            , DD.YD_GP          , DD.YD_BAY_GP       , DD.YD_SCH_CD   , DD.YD_SCH_PRIOR ,
					        DD.YD_SCH_PROG_STAT  , DD.YD_SCH_ST_GP   , DD.YD_SCH_REQ_GP   , DD.YD_GP       , DD.YD_BAY_GP    ,
					        DD.YD_TO_LOC_DCSN_MTD, DD.YD_TO_LOC_GUIDE, DD.YD_WRK_PLAN_TCAR)

				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.updTcarSchInsUdWb", logId, methodNm, "작업예약(TB_YD_WRKBOOK) 대차하차 등록");
				
				//하차 작업예약재료 등록
				//commDao.updTcarSch("InsUdWbMtl", jrParam);
				/*
				 * --대차스케줄 하차작업예약재료 등록 - 
					MERGE INTO TB_YD_WRKBOOKMTL WM USING (
					SELECT :V_YD_CARUD_WRK_BOOK_ID AS YD_WBOOK_ID
					      ,TM.STL_NO
					      ,:V_MODIFIER             AS MODIFIER
					      ,SYSDATE                 AS MOD_DDTT
					      ,'N'                     AS DEL_YN
					      ,WB.YD_STK_COL_GP
					      ,'01'                    AS YD_STK_BED_NO
					      ,TM.YD_STK_LYR_NO
					      ,COUNT(*) OVER () - ROW_NUMBER() OVER (ORDER BY TM.YD_STK_LYR_NO) + 1 AS YD_UP_COLL_SEQ
					  FROM TB_YD_TCARFTMVMTL TM
					      ,(SELECT WB.YD_GP||WB.YD_AIM_BAY_GP||SUBSTR(WB.YD_WRK_PLAN_TCAR,3) AS YD_STK_COL_GP
					          FROM TB_YD_WRKBOOK WB
					         WHERE WB.YD_WBOOK_ID = :V_YD_CARLD_WRK_BOOK_ID) WB
					 WHERE TM.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
					   AND TM.DEL_YN         = 'N'
					) DD ON (WM.YD_WBOOK_ID = DD.YD_WBOOK_ID AND WM.STL_NO = DD.STL_NO)
					WHEN NOT MATCHED THEN
					INSERT (WM.YD_WBOOK_ID  , WM.STL_NO       , WM.REGISTER      , WM.REG_DDTT     ,
					        WM.MODIFIER     , WM.MOD_DDTT     , WM.DEL_YN        , WM.YD_STK_COL_GP,
					        WM.YD_STK_BED_NO, WM.YD_STK_LYR_NO, WM.YD_UP_COLL_SEQ)
					VALUES (DD.YD_WBOOK_ID  , DD.STL_NO       , DD.MODIFIER      , DD.MOD_DDTT     ,
					        DD.MODIFIER     , DD.MOD_DDTT     , DD.DEL_YN        , DD.YD_STK_COL_GP,
					        DD.YD_STK_BED_NO, DD.YD_STK_LYR_NO, DD.YD_UP_COLL_SEQ)

				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.updTcarSchInsUdWbMtl", logId, methodNm, "작업예약재료(TB_YD_WRKBOOKMTL) 대차하차 등록");
				
				/**********************************************************
				* 3. 상차 대차스케줄 수정
				*  - 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등
				**********************************************************/
				//commDao.updTcarSch("UpdLdSch", jrParam);
				/*
				 * --대차스케줄 상차스케줄 수정 
					MERGE INTO TB_YD_TCARSCH TS USING (
					SELECT TM.YD_TCAR_SCH_ID
					      ,TM.YD_EQP_WRK_SH
					      ,TM.YD_EQP_WRK_WT
					      ,WB.YD_WBOOK_ID           AS YD_CARUD_WRK_BOOK_ID
					      ,SUBSTR(WB.YD_SCH_CD,1,6) AS YD_CARUD_STOP_LOC
					  FROM TB_YD_WRKBOOK WB
					      ,(SELECT TM.YD_TCAR_SCH_ID
					              ,COUNT(*)                AS YD_EQP_WRK_SH
					              ,SUM(ST.YD_MTL_WT)       AS YD_EQP_WRK_WT
					              ,:V_YD_CARUD_WRK_BOOK_ID AS YD_CARUD_WRK_BOOK_ID
					          FROM TB_YD_TCARFTMVMTL TM
					              ,TB_YD_STOCK       ST
					         WHERE TM.STL_NO         = ST.STL_NO
					           AND TM.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
					           AND TM.DEL_YN         = 'N'
					         GROUP BY TM.YD_TCAR_SCH_ID) TM
					  WHERE TM.YD_CARUD_WRK_BOOK_ID = WB.YD_WBOOK_ID(+)
					) DD ON (TS.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID)
					WHEN MATCHED THEN UPDATE SET
						 TS.MODIFIER             = :V_MODIFIER
					    ,TS.MOD_DDTT             = SYSDATE
					    ,TS.YD_EQP_WRK_STAT      = 'L' --영차
					    ,TS.YD_CAR_PROG_STAT     = '5' --상차완료
					    ,TS.YD_EQP_WRK_SH        = DD.YD_EQP_WRK_SH
					    ,TS.YD_EQP_WRK_WT        = DD.YD_EQP_WRK_WT
					    ,TS.YD_CARLD_ST_DT       = NVL(TS.YD_CARLD_ST_DT  ,SYSDATE)
					    ,TS.YD_CARLD_CMPL_DT     = NVL(TS.YD_CARLD_CMPL_DT,SYSDATE)
					    ,TS.YD_CARUD_WRK_BOOK_ID = DD.YD_CARUD_WRK_BOOK_ID
					    ,TS.YD_CARUD_STOP_LOC    = DD.YD_CARUD_STOP_LOC

				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.updTcarSchUpdLdSch", logId, methodNm, "대차스케줄(TB_YD_TCARSCH) 상차 수정");
				
				/**********************************************************
				* 4. 대차작업실적 및 대차출발지시 전송
				*   - L2, 후판Slab야드L2 대차출발지시( YDY3L006)
				**********************************************************/
				
				
				//L2, 후판Slab야드L2 영대차출발지시
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY3L006", jrParam));
			} else {
				//상차완료가 아니면
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "대차 상차완료처리할 수 있는 상태가 아닙니다.");
				return jrRtn;
			}

			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "대차스케줄 상차완료 처리.");
			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}


	/**
	 *      [A] 오퍼레이션명 : 대차스케줄 하차완료 처리
	 *      염용선 2020-06-29
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtTcarSchUdCmpl(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차스케줄 하차완료 처리[PSlabComm.trtTcarSchUdCmpl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String ydEqpId     = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID(대차)
			String ydTcarSchId = slabUtils.trim(rcvMsg.getFieldString("YD_TCAR_SCH_ID")); //야드대차스케쥴ID
			String ydModifier  = slabUtils.trim(rcvMsg.getFieldString("MODIFIER"));
			JDTORecord jrRtn = slabUtils.getParam(logId, methodNm, ydModifier);
			if ("".equals(ydEqpId)) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "설비ID가 없습니다.");
				return jrRtn;
			}

			

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, ydModifier);

			jrParam.setField("YD_EQP_ID", ydEqpId);	//야드설비ID

			/**********************************************************
			* 1. 대차 하차스케쥴 정보 조회
			**********************************************************/
			if ("".equals(ydTcarSchId)) {
				//대차하차스케쥴 조회
				//JDTORecordSet jsChk = commDao.getTcarSch("UdCmpl", jrParam);
				/*
				 * SELECT TS.YD_TCAR_SCH_ID
					  FROM TB_YD_TCARSCH TS
					 WHERE TS.YD_EQP_ID = :V_YD_EQP_ID
					   AND TS.DEL_YN    = 'N'
				 */
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.getTcarSchUdCmpl", logId, methodNm, "대차하차완료 조회");
	    		
				if (jsChk != null && jsChk.size() > 0) {
					JDTORecord jrChk = jsChk.getRecord(0);

					ydTcarSchId = slabUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID")); //야드대차스케쥴ID
				} else {
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "대차 하차스케쥴 정보가 없습니다.");
					return jrRtn;
			    }
			}
			
			jrParam.setField("YD_TCAR_SCH_ID", ydTcarSchId);	//야드대차스케쥴ID

			/**********************************************************
			* 2. 대차스케줄 삭제
			**********************************************************/
			//대차이송재료 삭제
			//commDao.updTcarSch("DelMtl", jrParam);
			/*
			 * 대차스케줄재료 삭제 
				UPDATE TB_YD_TCARFTMVMTL
				   SET MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,DEL_YN   = 'Y'
				 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
				   AND DEL_YN   = 'N'
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.updTcarSchDelMtl", logId, methodNm, "대차이송재료(TB_YD_TCARFTMVMTL) 삭제");				
			
			//대차스케줄 삭제
			//commDao.updTcarSch("DelSch", jrParam);
			/*
			 * 대차스케줄(하차완료) 삭제
				MERGE INTO TB_YD_TCARSCH TS USING (
				SELECT TS.YD_TCAR_SCH_ID
				      ,:V_MODIFIER AS MODIFIER
				      ,NVL(TS.YD_CARUD_STOP_LOC,SUBSTR(WB.YD_SCH_CD,1,6)) AS YD_CARUD_STOP_LOC
				      ,(SELECT MAX(CS.YD_EQP_ID)
				          FROM TB_YD_CRNSCH CS
				         WHERE CS.YD_WBOOK_ID = WB.YD_WBOOK_ID) AS YD_CARUD_WRK_CRN
				  FROM TB_YD_TCARSCH TS
				      ,TB_YD_WRKBOOK WB
				 WHERE TS.YD_CARUD_WRK_BOOK_ID = WB.YD_WBOOK_ID(+)
				   AND TS.YD_TCAR_SCH_ID       = :V_YD_TCAR_SCH_ID
				   AND TS.DEL_YN               = 'N'
				) DD ON (TS.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID)
				WHEN MATCHED THEN UPDATE SET
					 TS.MODIFIER          = DD.MODIFIER
				    ,TS.MOD_DDTT          = SYSDATE
				    ,TS.DEL_YN            = 'Y'
				    ,TS.YD_EQP_WRK_STAT   = 'U' --공차
				    ,TS.YD_CAR_PROG_STAT  = 'E' --하차완료
				    ,TS.YD_CARUD_ST_DT    = NVL(TS.YD_CARUD_ST_DT,SYSDATE)
				    ,TS.YD_CARUD_CMPL_DT  = SYSDATE
				    ,TS.YD_CARUD_WRK_CRN  = DD.YD_CARUD_WRK_CRN
				    ,TS.YD_CARUD_STOP_LOC = DD.YD_CARUD_STOP_LOC
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.updTcarSchDelSch", logId, methodNm, "대차스케줄(TB_YD_TCARSCH) 삭제");				
			
			/**********************************************************
			* 4. 공대차출발지시 처리
			**********************************************************/
			JDTORecord jrTcarSchLevWo = this.trtTcarSchLevWo(rcvMsg);
			String rtnCd	 = slabUtils.nvl(jrTcarSchLevWo.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = slabUtils.nvl(jrTcarSchLevWo.getFieldString("RTN_MSG"), "");
			
			if ("0".equals(rtnCd)) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", rtnMsg);
				return jrRtn;
			}
			
			jrRtn = slabUtils.addSndData(jrRtn, jrTcarSchLevWo);
			
			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "대차스케줄 하차완료 처리");
			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}


	/**
	 *      [A] 오퍼레이션명 : 대차스케줄 공대차출발지시 처리
	 *      염용선 2020-06-29
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtTcarSchLevWo(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차스케줄 공대차출발지시 처리[PSlabComm.trtTcarSchLevWo] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String ydEqpId   = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID" )); //야드설비ID
			String ydBayGpTo = slabUtils.trim(rcvMsg.getFieldString("YD_BAY_GP" )); //야드동구분(상차동)
			String ydUserId  = slabUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자
			JDTORecord jrRtn = slabUtils.getParam(logId, methodNm, ydUserId);

			if ("".equals(ydEqpId)) {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "설비ID가 없습니다.");
				return jrRtn;
			}

			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, ydUserId);

			jrParam.setField("YD_EQP_ID", ydEqpId);	//야드설비ID

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
			//JDTORecordSet jsChk = commDao.getTcarSch("LevWo", jrParam);
			/*
			 * 대차스케줄 공대차출발지시 조회
				SELECT TS.YD_TCAR_SCH_ID
				      ,EQ.YD_EQP_STAT
				      ,EQ.YD_EQP_WRK_MODE
				      ,NVL(EQ.YD_CURR_BAY_GP,WB.YD_BAY_GP) AS YD_CURR_BAY_GP --이동중이면 상차동을 현재동으로
				      ,EQ.YD_HOME_BAY_GP
				      ,NVL(EQ.RCPT_TCAR_USE_YN,'N') AS AUTO_TCAR_SCH_YN   --자동대차스케줄여부
				      ,WB.YD_WBOOK_ID               AS YD_WBOOK_ID_CURR   --현재 대차스케줄 상차작업예약ID
				      ,WB.YD_BAY_GP                 AS YD_BAY_GP_CURR     --현재 대차스케줄 상차동
				      ,WB.YD_AIM_BAY_GP             AS YD_AIM_BAY_GP_CURR --현재 대차스케줄 하차동
				      ,XB.YD_WBOOK_ID               AS YD_WBOOK_ID_NEXT   --다음 상차작업예약ID
				      ,XB.YD_BAY_GP                 AS YD_BAY_GP_NEXT     --다음 상차동
				      ,XB.YD_AIM_BAY_GP             AS YD_AIM_BAY_GP_NEXT --다음 하차동
				      ,(SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END
				          FROM TB_YD_TCARFTMVMTL TM
				         WHERE TM.YD_TCAR_SCH_ID = TS.YD_TCAR_SCH_ID
				           AND TM.DEL_YN = 'N') AS TC_MTL_YN
				  FROM TB_YD_EQP     EQ
				      ,TB_YD_TCARSCH TS
				      ,TB_YD_WRKBOOK WB
				      ,(SELECT MIN(YD_WBOOK_ID  ) AS YD_WBOOK_ID
				              ,MIN(YD_BAY_GP    ) AS YD_BAY_GP
				              ,MIN(YD_AIM_BAY_GP) AS YD_AIM_BAY_GP
				          FROM (SELECT YD_WBOOK_ID
				                      ,YD_BAY_GP
				                      ,YD_AIM_BAY_GP
				                  FROM TB_YD_WRKBOOK
				                 WHERE YD_WRK_PLAN_TCAR = :V_YD_EQP_ID
				                   AND YD_WBOOK_ID NOT IN
				                      (SELECT NVL(YD_CARLD_WRK_BOOK_ID,YD_CARUD_WRK_BOOK_ID) AS YD_WBOOK_ID
				                         FROM TB_YD_TCARSCH
				                        WHERE DEL_YN = 'N'
				                          AND (YD_CARLD_WRK_BOOK_ID IS NOT NULL	OR YD_CARUD_WRK_BOOK_ID IS NOT NULL))
				                   AND YD_SCH_CD LIKE '__TC__U%'
				                   AND DEL_YN = 'N'
				                 ORDER BY YD_SCH_PRIOR, YD_WBOOK_ID)
				         WHERE ROWNUM = 1) XB
				 WHERE EQ.YD_EQP_ID            = TS.YD_EQP_ID(+)
				   AND 'N'                     = TS.DEL_YN(+)
				   AND TS.YD_CARLD_WRK_BOOK_ID = WB.YD_WBOOK_ID(+)
				   AND 'N'                     = WB.DEL_YN(+)
				   AND EQ.YD_EQP_ID            = :V_YD_EQP_ID
			 */
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.getTcarSchLevWo", logId, methodNm, "공대차출발지시 조회");
    		
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
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "대차[" + ydEqpId + "]는 고장 상태입니다.");
					return jrRtn;
				} else if (!"1".equals(slabUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE")))) {
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "대차[" + ydEqpId + "]는 Off-Line 상태입니다.");
					return jrRtn;
				} else if ("Y".equals(slabUtils.trim(jrChk.getFieldString("TC_MTL_YN")))) {
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "대차스케줄[" + ydEqpId + " : " + ydTcarSchId + "]의 이송재료가 존재하여 공대차출발지시를 할 수 없습니다.");
					return jrRtn;
				}
			} else {
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", "대차 정보가 없습니다.");
				return jrRtn;
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
				/*
				 * 대차스케줄 작업예약재료 조회 
					SELECT TM.YD_SCH_CD
					      ,(SELECT SR.YD_WRK_CRN_PRIOR
					          FROM TB_YD_SCHRULE SR
					         WHERE SR.YD_SCH_CD = TM.YD_SCH_CD) AS YD_SCH_PRIOR
					      ,TM.YD_UD_BAY_GP AS YD_AIM_BAY_GP
					      ,TM.STL_NO
					      ,TM.YD_STK_COL_GP
					      ,TM.YD_STK_BED_NO
					      ,TM.YD_STK_LYR_NO
					      ,ROWNUM AS YD_UP_COLL_SEQ
					  FROM (SELECT SUBSTR(SL.YD_STK_COL_GP,1,2)||SUBSTR(TR.YD_EQP_ID,3)||'UM' AS YD_SCH_CD
					              ,TR.YD_UD_BAY_GP
					              ,TR.AUTO_TCAR_SCH_SH_MIN
					              ,TR.AUTO_TCAR_SCH_SH_MAX
					              ,SL.YD_STK_COL_GP
					              ,SL.YD_STK_BED_NO
					              ,SL.YD_STK_LYR_NO
					              ,SL.STL_NO
					              ,DECODE(WB.YD_WBOOK_ID,NULL,'N','Y') AS WB_MTL_YN
					              ,SUM(DECODE(WB.YD_WBOOK_ID,NULL,1,0)) OVER (PARTITION BY TR.SEQ_NO) AS YD_MTL_SH
					          FROM TB_YD_STKLYR SL
					              ,TB_YD_STOCK  ST
					              ,TB_YD_WRKBOOKMTL WM
					              ,TB_YD_WRKBOOK    WB
					              ,(SELECT EQ.YD_EQP_ID
					                      ,TO_NUMBER(TR.CD_GP)               AS SEQ_NO       --순번
					                      ,TR.ITEM1                          AS YD_AIM_RT_GP --야드목표행선구분
					                      ,TR.ITEM_VALUE1                    AS YD_LD_COL_GP --야드상차열구분
					                      ,TR.ITEM2                          AS YD_UD_BAY_GP --야드하차동구분
					                      ,NVL(EQ.YD_CRN_CONT_CARASGN_CNT,3) AS AUTO_TCAR_SCH_SH_MIN --자동대차스케줄매수최소
					                      ,NVL(EQ.YD_CRN_CONT_CARASGN_WR ,3) AS AUTO_TCAR_SCH_SH_MAX --자동대차스케줄매수최대
					                  FROM TB_YD_EQP  EQ
					                      ,TB_YD_RULE TR
					                 WHERE EQ.YD_EQP_ID = TR.REPR_CD_GP
					                   AND EQ.YD_EQP_ID = :V_YD_EQP_ID
					                   AND EQ.RCPT_TCAR_USE_YN = 'Y') TR
					         WHERE SL.STL_NO = ST.STL_NO
					           AND SL.YD_STK_COL_GP LIKE TR.YD_LD_COL_GP||'%'
					           AND ST.YD_AIM_RT_GP = TR.YD_AIM_RT_GP
					           AND SL.YD_STK_LYR_MTL_STAT = 'C'
					           AND ST.STL_NO = WM.STL_NO(+)
					           AND 'N' = WM.DEL_YN(+)
					           AND WM.YD_WBOOK_ID = WB.YD_WBOOK_ID(+)
					           AND 'N' = WB.DEL_YN(+)
					         ORDER BY TR.SEQ_NO, SL.YD_STK_COL_GP, SL.YD_STK_BED_NO, SL.YD_STK_LYR_NO DESC) TM
					 WHERE TM.WB_MTL_YN = 'N'
					   AND TM.YD_MTL_SH >= TM.AUTO_TCAR_SCH_SH_MIN
					   AND ROWNUM <= TM.AUTO_TCAR_SCH_SH_MAX
				 */
				JDTORecordSet jsWbMtl = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.getTcarSchWbMtl", logId, methodNm, "작업예약 조회");
	    		
				int wbMtlSh = jsWbMtl.size();
				
				if (wbMtlSh > 0) {
					JDTORecord jrRow = jsWbMtl.getRecord(0);

					String ydStkColGp = slabUtils.trim(jrRow.getFieldString("YD_STK_COL_GP"));	//야드적치열구분
					String ydGp       = ydStkColGp.substring(0, 1);								//야드구분
					       ydBayGp    = ydStkColGp.substring(1, 2);								//야드동구분(상차동)
					       ydAimBayGp = slabUtils.trim(jrRow.getFieldString("YD_AIM_BAY_GP"));	//야드목표동구분(하차동)
					String ydSchCd    = slabUtils.trim(jrRow.getFieldString("YD_SCH_CD"    ));	//야드스케쥴코드
					String ydSchPrior = slabUtils.trim(jrRow.getFieldString("YD_SCH_PRIOR" ));	//야드스케쥴우선순위
					String ydSchStGp  = "M";													//야드스케쥴기동구분
					if (ydUserId.length() == 8 && "YD".equals(ydUserId.substring(2, 4))) {
						ydSchStGp = "A";	//Auto
					}
					
					//작업예약ID 생성
					ydCarldWrkBookId = commDao.getSeqId(logId, methodNm, "WrkBook");
					
					if ("".equals(ydCarldWrkBookId)) {
						jrRtn.setField("RTN_CD"	, "0");
						jrRtn.setField("RTN_MSG", "대차상차 작업예약ID 생성중 오류가 발생하였습니다.");
						return jrRtn;
					}

					//작업예약 등록
					jrParam.setField("YD_WBOOK_ID"       , ydCarldWrkBookId); //야드작업예약ID
					jrParam.setField("YD_GP"             , ydGp            ); //야드구분
					jrParam.setField("YD_BAY_GP"         , ydBayGp         ); //야드동구분
					jrParam.setField("YD_SCH_CD"         , ydSchCd         ); //야드스케쥴코드
					jrParam.setField("YD_SCH_PRIOR"      , ydSchPrior      ); //야드스케쥴우선순위
					jrParam.setField("YD_SCH_PROG_STAT"  , "W"             ); //야드스케쥴진행상태(스케줄수행대기)
					jrParam.setField("YD_SCH_ST_GP"      , ydSchStGp       ); //야드스케쥴기동구분
					jrParam.setField("YD_SCH_REQ_GP"     , "5"             ); //야드스케쥴요청구분(공대차출발)
					jrParam.setField("YD_AIM_YD_GP"      , ydGp            ); //야드목표야드구분
					jrParam.setField("YD_AIM_BAY_GP"     , ydAimBayGp      ); //야드목표동구분
					jrParam.setField("YD_TO_LOC_DCSN_MTD", "S"             ); //야드TO위치결정방법
					jrParam.setField("YD_WRK_PLAN_TCAR"  , ydEqpId         ); //야드작업계획대차					
					
					jrParam.setField("YD_TO_LOC_GUIDE",""); //야드To위치Guide
					jrParam.setField("YD_CAR_USE_GP"  ,""); //야드차량사용구분
					jrParam.setField("TRN_EQP_CD"     ,""); //운송장비코드
					jrParam.setField("CAR_NO"         ,""); //차량번호
					jrParam.setField("CARD_NO"        ,""); //카드번호
					jrParam.setField("YD_WRK_PLAN_CRN",""); //야드작업계획크레인
						
					//commDao.insSlabYd("WrkBook", jrParam);
					/*
					 * 작업예약 등록
						INSERT INTO TB_YD_WRKBOOK (
						  YD_WBOOK_ID        --야드작업예약ID
						 ,REGISTER           --등록자
						 ,REG_DDTT           --등록일시
						 ,MODIFIER           --수정자
						 ,MOD_DDTT           --수정일시
						 ,DEL_YN             --삭제유무
						 ,YD_GP              --야드구분
						 ,YD_BAY_GP          --야드동구분
						 ,YD_SCH_CD          --야드스케쥴코드
						 ,YD_SCH_PRIOR       --야드스케쥴우선순위
						 ,YD_SCH_PROG_STAT   --야드스케쥴진행상태
						 ,YD_SCH_ST_GP       --야드스케쥴기동구분
						 ,YD_SCH_REQ_GP      --야드스케쥴요청구분
						 ,YD_AIM_YD_GP       --야드목표야드구분
						 ,YD_AIM_BAY_GP      --야드목표동구분
						 ,YD_TO_LOC_DCSN_MTD --야드To위치결정방법
						 ,YD_TO_LOC_GUIDE    --야드To위치Guide
						 ,YD_WRK_PLAN_TCAR   --야드작업계획대차
						 ,YD_CAR_USE_GP      --야드차량사용구분
						 ,TRN_EQP_CD         --운송장비코드
						 ,CAR_NO             --차량번호
						 ,CARD_NO            --카드번호
						 ,YD_WRK_PLAN_CRN
						) VALUES (
						  :V_YD_WBOOK_ID
						 ,:V_MODIFIER
						 ,SYSDATE
						 ,:V_MODIFIER
						 ,SYSDATE
						 ,'N'
						 ,:V_YD_GP
						 ,:V_YD_BAY_GP
						 ,:V_YD_SCH_CD
						 ,TO_NUMBER(:V_YD_SCH_PRIOR)
						 ,:V_YD_SCH_PROG_STAT
						 ,:V_YD_SCH_ST_GP
						 ,:V_YD_SCH_REQ_GP
						 ,:V_YD_AIM_YD_GP
						 ,:V_YD_AIM_BAY_GP
						 ,:V_YD_TO_LOC_DCSN_MTD
						 ,:V_YD_TO_LOC_GUIDE
						 ,:V_YD_WRK_PLAN_TCAR
						 ,:V_YD_CAR_USE_GP
						 ,:V_TRN_EQP_CD
						 ,:V_CAR_NO
						 ,:V_CARD_NO
						 ,:V_YD_WRK_PLAN_CRN
						)
					 */
					int iRtnVal = commDao.insert(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.insSlabYdWrkBook2", logId, methodNm, "작업예약(TB_YD_WRKBOOK) 등록");
					if(iRtnVal < 1) {
						
					}
					//작업예약재료 등록
					//String[][] wmParam = new String[wbMtlSh][8];
					JDTORecord jrWmParam = slabUtils.getParam(logId, methodNm, ydUserId);
					
					for (int ii = 0; ii < wbMtlSh; ii++) {
						jrRow = jsWbMtl.getRecord(ii);
						/*
						wmParam[ii][0] = ydCarldWrkBookId;											//야드작업예약ID
						wmParam[ii][1] = slabUtils.trim(jrRow.getFieldString("STL_NO"        ));	//재료번호
						wmParam[ii][2] = ydUserId;													//등록자
						wmParam[ii][3] = ydUserId;													//수정자
						wmParam[ii][4] = slabUtils.trim(jrRow.getFieldString("YD_STK_COL_GP" ));	//야드적치열구분
						wmParam[ii][5] = slabUtils.trim(jrRow.getFieldString("YD_STK_BED_NO" ));	//야드적치Bed번호
						wmParam[ii][6] = slabUtils.trim(jrRow.getFieldString("YD_STK_LYR_NO" ));	//야드적치단번호
						wmParam[ii][7] = slabUtils.trim(jrRow.getFieldString("YD_UP_COLL_SEQ"));	//야드권상모음순서
						*/
						jrWmParam.setField("YD_WBOOK_ID"   , ydCarldWrkBookId); //야드작업예약ID
						jrWmParam.setField("STL_NO"          , slabUtils.trim(jrRow.getFieldString("STL_NO"        ))); //재료번호
						jrWmParam.setField("YD_STK_COL_GP"   , slabUtils.trim(jrRow.getFieldString("YD_STK_COL_GP" )));	//야드적치열구분
						jrWmParam.setField("YD_STK_BED_NO"   , slabUtils.trim(jrRow.getFieldString("YD_STK_BED_NO" )));	//야드적치Bed번호
						jrWmParam.setField("YD_STK_LYR_NO"   , slabUtils.trim(jrRow.getFieldString("YD_STK_LYR_NO" )));	//야드적치단번호
						jrWmParam.setField("YD_UP_COLL_SEQ"  , slabUtils.trim(jrRow.getFieldString("YD_UP_COLL_SEQ")));	//야드권상모음순서
						
						/*
						 * 작업예약재료 등록 
							INSERT INTO TB_YD_WRKBOOKMTL (
							  YD_WBOOK_ID    --야드작업예약ID
							 ,STL_NO         --재료번호
							 ,REGISTER       --등록자
							 ,REG_DDTT       --등록일시
							 ,MODIFIER       --수정자
							 ,MOD_DDTT       --수정일시
							 ,DEL_YN         --삭제유무
							 ,YD_STK_COL_GP  --야드적치열구분
							 ,YD_STK_BED_NO  --야드적치Bed번호
							 ,YD_STK_LYR_NO  --야드적치단번호
							 ,YD_UP_COLL_SEQ --야드권상모음순서
							) VALUES (
							  :V_YD_WBOOK_ID
							 ,:V_STL_NO
							 ,:V_MODIFIER
							 ,SYSDATE
							 ,:V_MODIFIER
							 ,SYSDATE
							 ,'N'
							 ,:V_YD_STK_COL_GP
							 ,:V_YD_STK_BED_NO
							 ,:V_YD_STK_LYR_NO
							 ,TO_NUMBER(:V_YD_UP_COLL_SEQ)
							)
						 */
						commDao.insert(jrWmParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.insSlabYdWrkBookMtl", logId, methodNm, "작업예약재료(TB_YD_WRKBOOKMTL) 등록");
						
					}
					
					//commDao.upsBatch("WrkBookMtl", wmParam, logId, methodNm);
					
					
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
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", "대차스케줄ID 생성 중 오류가 발생하였습니다.");
					return jrRtn;
				}
			}
			
			jrParam.setField("YD_TCAR_SCH_ID"      , ydTcarSchId     );	//야드대차스케쥴ID
			jrParam.setField("YD_CAR_PROG_STAT"    , ydCarProgStat   );	//야드차량진행상태
			jrParam.setField("YD_CARLD_WRK_BOOK_ID", ydCarldWrkBookId);	//야드상차작업예약ID
			jrParam.setField("YD_CARLD_LEV_LOC"    , ydCarldLevLoc   );	//야드상차출발위치
			jrParam.setField("YD_CARLD_STOP_LOC"   , ydCarldStopLoc  );	//야드상차정지위치
			jrParam.setField("YD_CARUD_STOP_LOC"   , ydCarudStopLoc  );	//야드하차정지위치
			
			//대차스케줄 수정 또는 생성
			//commDao.updTcarSch("InsSch", jrParam);
			/*
			 * 대차스케줄 등록 
				MERGE INTO TB_YD_TCARSCH TS USING (
				SELECT :V_YD_TCAR_SCH_ID       AS YD_TCAR_SCH_ID
				      ,:V_MODIFIER             AS MODIFIER
				      ,SYSDATE                 AS MOD_DDTT
				      ,'N'                     AS DEL_YN
				      ,:V_YD_EQP_ID            AS YD_EQP_ID
				      ,'U'                     AS YD_EQP_WRK_STAT     --공차
				      ,:V_YD_CAR_PROG_STAT     AS YD_CAR_PROG_STAT
				      ,:V_YD_CARLD_WRK_BOOK_ID AS YD_CARLD_WRK_BOOK_ID
				      ,:V_YD_CARLD_LEV_LOC     AS YD_CARLD_LEV_LOC
				      ,:V_YD_CARLD_STOP_LOC    AS YD_CARLD_STOP_LOC
				      ,:V_YD_CARUD_STOP_LOC    AS YD_CARUD_STOP_LOC
				      ,'6'                     AS YD_CARLD_SCH_REQ_GP --공대차도착
				      ,'3'                     AS YD_CARUD_SCH_REQ_GP --영대차도착
				  FROM DUAL
				) DD ON (TS.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID)
				WHEN MATCHED THEN UPDATE SET
					 TS.MODIFIER             = DD.MODIFIER
				    ,TS.MOD_DDTT             = DD.MOD_DDTT
				    ,TS.YD_EQP_WRK_STAT      = DD.YD_EQP_WRK_STAT
				    ,TS.YD_CAR_PROG_STAT     = DD.YD_CAR_PROG_STAT
				    ,TS.YD_CARLD_WRK_BOOK_ID = DD.YD_CARLD_WRK_BOOK_ID
				    ,TS.YD_CARLD_LEV_LOC     = DD.YD_CARLD_LEV_LOC
				    ,TS.YD_CARLD_STOP_LOC    = DD.YD_CARLD_STOP_LOC
				    ,TS.YD_CARUD_STOP_LOC    = DD.YD_CARUD_STOP_LOC
				    ,TS.YD_CARLD_SCH_REQ_GP  = DD.YD_CARLD_SCH_REQ_GP
				    ,TS.YD_CARUD_SCH_REQ_GP  = DD.YD_CARUD_SCH_REQ_GP
				WHEN NOT MATCHED THEN
				INSERT (TS.YD_TCAR_SCH_ID   , TS.REGISTER            , TS.REG_DDTT           , TS.MODIFIER         ,
				        TS.MOD_DDTT         , TS.DEL_YN              , TS.YD_EQP_ID          , TS.YD_EQP_WRK_STAT  ,
				        TS.YD_CAR_PROG_STAT , TS.YD_CARLD_WRK_BOOK_ID, TS.YD_CARLD_LEV_LOC   , TS.YD_CARLD_STOP_LOC,
				        TS.YD_CARUD_STOP_LOC, TS.YD_CARLD_SCH_REQ_GP , TS.YD_CARUD_SCH_REQ_GP)
				VALUES (DD.YD_TCAR_SCH_ID   , DD.MODIFIER            , DD.MOD_DDTT           , DD.MODIFIER         ,
				        DD.MOD_DDTT         , DD.DEL_YN              , DD.YD_EQP_ID          , DD.YD_EQP_WRK_STAT  ,
				        DD.YD_CAR_PROG_STAT , DD.YD_CARLD_WRK_BOOK_ID, DD.YD_CARLD_LEV_LOC   , DD.YD_CARLD_STOP_LOC,
				        DD.YD_CARUD_STOP_LOC, DD.YD_CARLD_SCH_REQ_GP , DD.YD_CARUD_SCH_REQ_GP)
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.updTcarSchInsSch", logId, methodNm, "대차스케줄(TB_YD_TCARSCH) 등록");
			JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, ydUserId);
			
			slabUtils.printLog(logId, "[야드작업예약ID(현재 대차스케줄 상차작업예약ID)]"+ydWbookIdCurr+ ">>ydCarProgStat[야드차량진행상태] : "+ydCarProgStat, "SL");
			slabUtils.printLog(logId, "ydCarldWrkBookId:"+ydCarldWrkBookId, "SL");
			
				if ("0".equals(ydCarProgStat)) {
					/**********************************************************
					* 4. 상차출발위치와 상차도착위치가 다르면 공대차출발지시 전송
					*  - 후판Slab야드L2(YDY3L006)
					**********************************************************/
					jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY3L006", jrParam));
				} else if (!"".equals(ydCarldWrkBookId) && "".equals(ydWbookIdCurr)) {
					/**********************************************************
					* 5. 상차출발위치와 상차도착위치가 같고 신규 작업예약ID이면 크레인스케줄 호출
					**********************************************************/
					jrYdMsg.setField("YD_WBOOK_ID"  , ydCarldWrkBookId); //야드작업예약ID
					jrYdMsg.setField("YD_SCH_ST_GP" , "A"             ); //야드스케쥴기동구분(Auto)
					jrYdMsg.setField("YD_SCH_REQ_GP", "6"             ); //야드스케쥴요청구분(공대차도착)
					jrRtn = slabUtils.addSndData(jrRtn, this.getCrnSchMsg(jrYdMsg));
				}
				
		
			
			slabUtils.printLog(logId, methodNm, "S-");
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "대차스케줄 공대차출발지시 처리.");
			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : Pickup-Bed 대차(설비ID) 조회
	 *      염용선 2020-09-08
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
		String methodNm = "Bed용도구분설정[PSlabComm.setBedUsgGp] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		String exceptionYn = "Y";

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String ydStkColGp    = slabUtils.trim(jrParam.getFieldString("YD_STK_COL_GP"    )); //야드적치열구분
			String ydStkBedNo    = slabUtils.trim(jrParam.getFieldString("YD_STK_BED_NO"    )); //야드적치Bed번호
			String ydStkBedUsgGp = slabUtils.trim(jrParam.getFieldString("YD_STK_BED_USG_GP")); //야드적치Bed용도구분
			String ydUserId      = slabUtils.trim(jrParam.getFieldString("MODIFIER"         )); //수정자

			//Exception이 발생해도 변경되지 않은 정보를 L2로 전송하기 위해 관리
			if ("Y3YDL001".equals(ydUserId)) {
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

			jrParam.setField("YD_STK_COL_GP", ydStkColGp); //야드적치열구분(Pickup)
			jrParam.setField("YD_STK_BED_NO", ydStkBedNo); //야드적치Bed번호(Pickup)
			jrParam.setField("YD_EQP_ID"    , ydEqpId   ); //야드설비ID(대차)
			jrParam.setField("MODIFIER"     , ydUserId  ); //수정자

			//JDTORecordSet jsChk = commDao.getStat("PuBedTcar", jrParam);
			/*
			 * --PickupBed대차상태조회 
				SELECT SB.YD_STK_BED_USG_GP
				      ,(SELECT COUNT(*)
				          FROM TB_YD_STKLYR SL
				         WHERE SL.YD_STK_COL_GP = SB.YD_STK_COL_GP
				           AND SL.YD_STK_BED_NO = SB.YD_STK_BED_NO
				           AND SL.YD_STK_LYR_MTL_STAT IN ('C','U')
				           AND SL.STL_NO IS NOT NULL) AS BED_MTL_SH_PU
				      ,TS.YD_CURR_BAY_GP
				      ,TS.YD_CARLD_WRK_BOOK_ID
				      ,(SELECT COUNT(*)
				          FROM TB_YD_STKLYR SL
				         WHERE SL.YD_STK_COL_GP = TS.YD_STK_COL_GP
				           AND SL.YD_STK_BED_NO = '01'
				           AND SL.STL_NO IS NOT NULL) AS BED_MTL_SH_TC
				  FROM (SELECT MIN(SB.YD_STK_COL_GP    ) AS YD_STK_COL_GP
				              ,MIN(SB.YD_STK_BED_NO    ) AS YD_STK_BED_NO
				              ,MIN(SB.YD_STK_BED_USG_GP) AS YD_STK_BED_USG_GP
				          FROM TB_YD_STKBED SB
				         WHERE SB.YD_STK_COL_GP = :V_YD_STK_COL_GP
				           AND SB.YD_STK_BED_NO = :V_YD_STK_BED_NO) SB
				      ,(SELECT MIN(TS.YD_TCAR_SCH_ID      ) AS YD_TCAR_SCH_ID
				              ,MIN(TS.YD_CARLD_WRK_BOOK_ID) AS YD_CARLD_WRK_BOOK_ID
				              ,MIN(EQ.YD_CURR_BAY_GP      ) AS YD_CURR_BAY_GP
				              ,MIN(EQ.YD_GP)||MIN(EQ.YD_CURR_BAY_GP)||'TC'||MIN(EQ.YD_EQP_NO) AS YD_STK_COL_GP
				          FROM TB_YD_EQP     EQ
				              ,TB_YD_TCARSCH TS
				         WHERE EQ.YD_EQP_ID = TS.YD_EQP_ID(+)
				           AND EQ.YD_EQP_ID = :V_YD_EQP_ID
				           AND TS.DEL_YN(+) = 'N') TS

			 */
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabSchSeEJB.getStatPuBedTcar", logId, methodNm, "PickupBed대차상태");
			
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
							jrParam.setField("YD_STK_COL_GP_FR", ydStkColGp  ); //야드적치열구분(Pickup)
							jrParam.setField("YD_STK_BED_NO_FR", ydStkBedNo  ); //야드적치Bed번호(Pickup)
							jrParam.setField("YD_STK_COL_GP_TO", ydStkColGpTc); //야드적치열구분(대차)
							jrParam.setField("YD_STK_BED_NO_TO", "01"        ); //야드적치열구분(대차)

							//commDao.updSlabYd("StkMtlMV", jrParam);
							/*
							 * --적치단 재료번호 이동
								MERGE INTO TB_YD_STKLYR SL USING (
								SELECT :V_YD_STK_COL_GP_TO AS YD_STK_COL_GP
								      ,:V_YD_STK_BED_NO_TO AS YD_STK_BED_NO
								      ,YD_STK_LYR_NO
								      ,STL_NO
								      ,YD_STK_LYR_MTL_STAT
								  FROM TB_YD_STKLYR
								 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP_FR
								   AND YD_STK_BED_NO = :V_YD_STK_BED_NO_FR
								   AND DEL_YN        = 'N'
								 UNION ALL
								SELECT YD_STK_COL_GP
								      ,YD_STK_BED_NO
								      ,YD_STK_LYR_NO
								      ,NULL AS STL_NO
								      ,'E'  AS YD_STK_LYR_MTL_STAT
								  FROM TB_YD_STKLYR
								 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP_FR
								   AND YD_STK_BED_NO = :V_YD_STK_BED_NO_FR
								   AND DEL_YN        = 'N'
								) DD ON (SL.YD_STK_COL_GP = DD.YD_STK_COL_GP AND SL.YD_STK_BED_NO = DD.YD_STK_BED_NO AND SL.YD_STK_LYR_NO = DD.YD_STK_LYR_NO)
								WHEN MATCHED THEN UPDATE SET
									 SL.MODIFIER            = :V_MODIFIER
								    ,SL.MOD_DDTT            = SYSDATE
								    ,SL.STL_NO              = DD.STL_NO
								    ,SL.YD_STK_LYR_MTL_STAT = DD.YD_STK_LYR_MTL_STAT

							 */
							commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabComm.updSlabYdStkMtlMV", logId, methodNm, "적치단(TB_YD_STKLYR) 재료번호 이동");
							
						}

						//해당 대차 Bed 적치가능(L) 상태로 변경
						jrParam.setField("YD_STK_COL_GP"      , ydStkColGpTc); //야드적치열구분(대차)
						jrParam.setField("YD_STK_BED_NO"      , "01"        ); //야드적치Bed번호(대차)
						jrParam.setField("YD_STK_BED_ACT_STAT", "L"         ); //야드적치Bed활성상태(적치가능)
						
						//commDao.updStat("StkBedAct", jrParam);
						/*
						 * UPDATE TB_YD_STKBED
							   SET MODIFIER      = :V_MODIFIER
							      ,MOD_DDTT       = SYSDATE
							      ,YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
							 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
							   AND YD_STK_BED_NO         = :V_YD_STK_BED_NO
							   AND DEL_YN              = 'N'

						 */
						commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStatStkBedAct", logId, methodNm, "적치단(TB_YD_STKLYR) 재료번호 이동");
						
					} else {
						//Pickup Bed로 전환하는 경우(용도구분:C -> S)
						if (!"".equals(ydCarldWrkBookId)) {
							throw new Exception("대차[" + ydEqpId + "] 상차작업예약[" + ydCarldWrkBookId + "]이 존재하므로 Pickup Bed로 전환 불가");
						}
						//전 대차 Bed 비활성화(C) 상태로 변경
						jrParam.setField("YD_STK_COL_GP", ydStkColGpTc.substring(0, 1) + "_" + ydStkColGpTc.substring(2)); //야드적치열구분(대차전체Bed)

						//적치Bed(전체) 비활성화
						//commDao.updStat("StkBedActCA", jrParam);
						/*
						 * UPDATE TB_YD_STKBED
							   SET MODIFIER            = :V_MODIFIER
							      ,MOD_DDTT            = SYSDATE
							      ,YD_STK_BED_ACT_STAT = 'C'   --비활성화
							 WHERE YD_STK_COL_GP    LIKE :V_YD_STK_COL_GP
							   AND DEL_YN              = 'N'
							   AND YD_STK_BED_ACT_STAT = 'L'

						 */
						
						commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStatStkBedActCA", logId, methodNm, "적치Bed 활성상태 CloseAll");
						
					}
				}
				
				jrParam.setField("YD_STK_COL_GP"    , ydStkColGp   ); //야드적치열구분
				jrParam.setField("YD_STK_BED_NO"    , ydStkBedNo   ); //야드적치Bed번호
				jrParam.setField("YD_STK_BED_USG_GP", ydStkBedUsgGp); //야드적치Bed용도구분

				//적치Bed Table에 용도구분을 Update
				//commDao.updStat("StkBedUsg", jrParam);
				/*
				 * 적치Bed 용도구분 수정 - 
					UPDATE TB_YD_STKBED
					   SET MODIFIER          = :V_MODIFIER
					      ,MOD_DDTT          = SYSDATE
					      ,YD_STK_BED_USG_GP = :V_YD_STK_BED_USG_GP
					 WHERE YD_STK_COL_GP     = :V_YD_STK_COL_GP
					   AND YD_STK_BED_NO     = :V_YD_STK_BED_NO
					   AND DEL_YN            = 'N'

				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updStatStkBedUsg", logId, methodNm, "적치Bed(TB_YD_STKBED) 야드적치Bed용도구분 수정");
				
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

	
	/** 염용선 2020-09-16
	 * 오퍼레이션명 : 야드별로 대차상차스케줄 코드를 부여하는 함수[대차는 C연주슬라브야드(A), 후판슬라브야드(D), C열연코일소재,제품(J)만 존재]
	 * @param ydStkColGp
	 * @return
	 */
	public String[] getSchCdNTcar(String logId, String methodNm, String sModifier,String ydStkColGp) {
		//메세지값
		String szMsg = null;
		//반환값
		String[] retValue = new String[2];
		//야드구분
		String ydGp = ydStkColGp.substring(0, 1);
		//스케줄코드
		String szYD_SCH_CD = "";
		//대차설비ID
		String szTCAR = "";
		//메소드명 
		String szMethodName = "getSchCdNTcar";
		
		if( ydGp.equals("A") ) {		//야드구분이 C연주슬라브야드 
			if( ydStkColGp.substring(2, 4).equals("01") || ydStkColGp.substring(2, 4).equals("02") ) {		// 01,02스판은 #1대차 배정
				szYD_SCH_CD = ydStkColGp.substring(0, 2) + "TC01UM";
				szTCAR = ydStkColGp.substring(0, 1) + "XTC01";
			}else if( ydStkColGp.substring(2, 4).equals("03") ) {													// 03스판은 #2대차 배정
				szYD_SCH_CD = ydStkColGp.substring(0, 2) + "TC02UM";
				szTCAR = ydStkColGp.substring(0, 1) + "XTC02";
			}else if( ydStkColGp.substring(2, 4).equals("04") ) {													// 04스판은 #3대차 배정
				szYD_SCH_CD = ydStkColGp.substring(0, 2) + "TC03UM";
				szTCAR = ydStkColGp.substring(0, 1) + "XTC03";
			}else if( ydStkColGp.substring(2, 4).equals("05") ) {													// 04스판은 #3대차 배정
				//03열 이하는 4번 대차 이후는 5번 대차
				if( ydStkColGp.substring(4, 6).equals("01") ||
					ydStkColGp.substring(4, 6).equals("02") ||
					ydStkColGp.substring(4, 6).equals("03")) {
					szYD_SCH_CD = ydStkColGp.substring(0, 2) + "TC04UM";
					szTCAR = ydStkColGp.substring(0, 1) + "XTC04";
				} else {
					szYD_SCH_CD = ydStkColGp.substring(0, 2) + "TC05UM";
					szTCAR = ydStkColGp.substring(0, 1) + "XTC05";
				}
			}else if( ydStkColGp.substring(2, 4).equals("06") ) {													// 04스판은 #3대차 배정
				szYD_SCH_CD = ydStkColGp.substring(0, 2) + "TC06UM";
				szTCAR = ydStkColGp.substring(0, 1) + "XTC06";
			}
		} else if( ydGp.equals("D") ) {	//야드구분이 후판슬라브야드 
			szYD_SCH_CD = ydStkColGp.substring(0, 2) + "TC01UM";
			szTCAR = ydStkColGp.substring(0, 1) + "XTC01";
		}else{
			szMsg = "현재 지원하지 않는 야드구분[" + ydGp + "]입니다." ;
			slabUtils.printLog(logId, szMsg, "SL");
		}
		retValue[0] = szYD_SCH_CD;
		retValue[1] = szTCAR;
		return retValue;
	}
	
	
	/**
	 * 오퍼레이션명 : 크레인사양과 비교 체크
	 * 
	 * @param  dblCurrWidth, dblMaxWidth, lngSumWt, intMtlSh, recCrnSpec
	 * @return intRtnVal [1 : 성공, -1 : 크레인사양의 집게허용 이상, -2 : 크레인 작업가능 중량 이상, -3 : 크레인 작업가능 매수 이상
	 * @throws JDTOException
	 */
	public int chkGetCrnspec(String logId,String methodNm,String sModifier , double  dblCurrWidth, double dblMaxWidth, long lngSumWt, int intMtlSh, JDTORecord recCrnSpec)throws JDTOException  {

		String szMsg              = null;
		String szMethodName       = "chkGetCrnspec";

		int intRtnVal             = 0;
		
		//크레인 집게폭 오차
		double intCrnTongWTol     = 0;
		//크레인 허용 중량
		long lngWrkAbleWt         = 0;
		//크레인 허용 매수
		int intWrkAbleSh          = 0;
		
		try {
			
			//크레인 집게허용 오차
			intCrnTongWTol = slabUtils.paraRecChkNullDouble(recCrnSpec,  "YD_CRN_TONG_W_TOL");
			//크레인 작업가능 중량
			lngWrkAbleWt   = slabUtils.paraRecChkNullLong(recCrnSpec, "YD_WRK_ABLE_WT");
			//크레인 작업가능 매수
			intWrkAbleSh   = slabUtils.paraRecChkNullInt(recCrnSpec,  "YD_WRK_ABLE_SH");
			
			
			//크레인사양의 집게허용 오차 Check
			if(dblMaxWidth > dblCurrWidth + intCrnTongWTol) {
				
				szMsg = "dblMaxWidth : " + dblMaxWidth + " > dblCurrWidth : " + dblCurrWidth + " intCrnTongWTol : " + intCrnTongWTol;
				slabUtils.printLog(logId, szMsg, "SL");
				return intRtnVal = -1;
			}

			//크레인 작업가능 중량 Check
			if(lngWrkAbleWt < lngSumWt) {
				szMsg = "lngWrkAbleWt : " + lngWrkAbleWt + " < lngSumWt : " + lngSumWt;
				slabUtils.printLog(logId, szMsg, "SL");
				return intRtnVal = -2;
			}
							
			//크레인 작업가능 매수 Check
			if (intWrkAbleSh < intMtlSh) {
				szMsg = "intWrkAbleSh : " + intWrkAbleSh + " < intMtlSh : " + intMtlSh;
				slabUtils.printLog(logId, szMsg, "SL");
				return intRtnVal = -3;
			}
			
			return intRtnVal = 1;
			
		} catch(Exception e) {
			szMsg = "크레인사양과 비교 체크 중 Error :	" + e.getMessage();
			slabUtils.printLog(logId, szMsg, "SL");
			return intRtnVal = -100;
		}
		
	} 
	
	
	/**
	 * 오퍼레이션명 : 스케쥴기준을 조회하여 크레인 정보를 반환하는 메소드
	 * @param szYD_SCH_CD
	 * @param recResult
	 * @return int
	 * 			1 : 메소드 호출 성공
	 * 			-1 : 스케쥴금지
	 * 			-2 : 작업크레인고장이고 대체크레인정보가 없는 경우
	 * 			-3  : 작업크레인고장이고 대체크레인 고장인 경우 작업 불가
	 * 			-4 : 스케쥴기준 조회에러
	 * 			-5 : 크레인설비 정보 조회시 에러 발생
	 * @throws JDTOException
	 */
	public int getCrnInfoByCrnSchRule(String logId, String methodNm, String sModifier ,String szYD_SCH_CD, JDTORecord recResult) throws JDTOException {
		String szMsg = "";
		String szCrn = "";
		String szYD_SCH_PRIOR = "9";
		// 리턴 recordSet 생성
		JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);
		   jrParam.setField("YD_SCH_CD", szYD_SCH_CD );
		// 스케줄 기준 체크
		//boolean blnRtnVal = chkGetSchRule(szYD_SCH_CD, rsResult);
		//com.inisteel.cim.yd.dao.ydschruledao.YdSchruleDao.getYdSchrule
		//com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdSchrule
		/*
		 * (원본) "com.inisteel.cim.yd.dao.ydschruledao.YdSchruleDao.getYdSchrule";
		 * 
			SELECT YD_SCH_CD                              AS YD_SCH_CD
			      ,REGISTER                               AS REGISTER
			      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
			      ,MODIFIER                               AS MODIFIER
			      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
			      ,DEL_YN                                 AS DEL_YN
			      ,YD_GP                                  AS YD_GP
			      ,YD_BAY_GP                              AS YD_BAY_GP
			      ,YD_SCH_RNG_CD                          AS YD_SCH_RNG_CD
			      ,YD_SCH_WHIO_GP                         AS YD_SCH_WHIO_GP
			      ,YD_SCH_DIV_GP                          AS YD_SCH_DIV_GP
			      ,YD_SCH_RULE_ACT_STAT                   AS YD_SCH_RULE_ACT_STAT
			      ,YD_WRK_CRN                             AS YD_WRK_CRN
			      ,YD_WRK_CRN_PRIOR                       AS YD_WRK_CRN_PRIOR
			      ,YD_ALT_CRN_YN                          AS YD_ALT_CRN_YN
			      ,YD_ALT_CRN                             AS YD_ALT_CRN
			      ,YD_ALT_CRN_PRIOR                       AS YD_ALT_CRN_PRIOR
			      ,CD_CONTENTS                            AS CD_CONTENTS
			      ,YD_SCH_PROH_EXN                        AS YD_SCH_PROH_EXN
			   FROM TB_YD_SCHRULE
			 WHERE YD_SCH_CD = replace( :V_YD_SCH_CD ,'HEKD05LM','HEDD05LM')
		 */
		
		rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdSchrule", logId, methodNm, "스케줄 기준 조회");

		if (rsResult == null || rsResult.size()>1) return -4;

		// 레코드 추출
		rsResult.first();
		JDTORecord recPara = rsResult.getRecord();

		// 스케줄CD 체크
		// 스케줄 금지 유무
		String szYD_SCH_PROH_EXN = slabUtils.paraRecChkNull(recPara,"YD_SCH_PROH_EXN");
		
		// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
		if (szYD_SCH_PROH_EXN.equals("Y")) {

			szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
			slabUtils.printLog(logId, szMsg, "SL");
			return -1;
		}
		
		// 작업크레인
		String szYD_WRK_CRN = slabUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
		// 작업크레인우선순위
		String szYD_WRK_CRN_PRIOR = slabUtils.paraRecChkNull(recPara,"YD_WRK_CRN_PRIOR");
		// 대체크레인유무
		String szYD_ALT_CRN_YN = slabUtils.paraRecChkNull(recPara,"YD_ALT_CRN_YN");
		// 대체크레인
		String szYD_ALT_CRN = slabUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
		// 대체크레인우선순위
		String szYD_ALT_CRN_PRIOR = slabUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");
		
		jrParam.setField("YD_EQP_ID", szYD_WRK_CRN );
		// 작업크레인 설비 상태 체크
		//blnRtnVal = eqpStatCheck(szYD_WRK_CRN);
		//com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqp
		/*
		 * 
			SELECT 
			    YD_EQP_ID AS YD_EQP_ID
			    ,REGISTER AS REGISTER
			    ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
			    ,MODIFIER AS MODIFIER
			    ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
			    ,DEL_YN AS DEL_YN
			    ,YD_GP AS YD_GP
			    ,YD_BAY_GP AS YD_BAY_GP
			    ,YD_EQP_GP AS YD_EQP_GP
			    ,YD_EQP_NO AS YD_EQP_NO
			    ,YD_WRK_ALW_XAXIS_TO AS YD_WRK_ALW_XAXIS_TO
			    ,YD_EQP_NAME AS YD_EQP_NAME
			    ,YD_EQP_STAT AS YD_EQP_STAT
			    ,YD_EQP_WRK_MODE AS YD_EQP_WRK_MODE
			    ,YD_EQP_WRK_MODE2 AS YD_EQP_WRK_MODE2
			    ,YD_WRK_ALW_XAXIS_FR AS YD_WRK_ALW_XAXIS_FR
			    ,YD_WRK_ALW_YAXIS_FR AS YD_WRK_ALW_YAXIS_FR
			    ,YD_WRK_ALW_YAXIS_TO AS YD_WRK_ALW_YAXIS_TO
			    ,YD_WRK_ALW_ZAXIS_FR AS YD_WRK_ALW_ZAXIS_FR
			    ,YD_WRK_ALW_ZAXIS_TO AS YD_WRK_ALW_ZAXIS_TO
			    ,YD_CRN_TRAVL_OFFSET AS YD_CRN_TRAVL_OFFSET
			    ,YD_CRN_GRAB_TP AS YD_CRN_GRAB_TP
			    ,YD_CRN_TRAVS_OFFSET AS YD_CRN_TRAVS_OFFSET
			    ,YD_L2_HMI_STAT AS YD_L2_HMI_STAT
			    ,YD_CTS_RELAY_YN AS YD_CTS_RELAY_YN
			    ,YD_CTS_RELAY_BAY_GP AS YD_CTS_RELAY_BAY_GP
			    ,YD_CRN_GRAB1_ACT_STAT AS YD_CRN_GRAB1_ACT_STAT
			    ,YD_CRN_GRAB2_ACT_STAT AS YD_CRN_GRAB2_ACT_STAT
			    ,YD_WRK_ABLE_XAXIS_FR AS YD_WRK_ABLE_XAXIS_FR
			    ,YD_WRK_ABLE_XAXIS_TO AS YD_WRK_ABLE_XAXIS_TO
			    ,YD_WRK_ABLE_YAXIS_FR AS YD_WRK_ABLE_YAXIS_FR
			    ,YD_WRK_ABLE_YAXIS_TO AS YD_WRK_ABLE_YAXIS_TO
			    ,YD_WRK_ABLE_ZAXIS_FR AS YD_WRK_ABLE_ZAXIS_FR
			    ,YD_WRK_ABLE_ZAXIS_TO AS YD_WRK_ABLE_ZAXIS_TO
			    ,YD_CURR_BAY_GP AS YD_CURR_BAY_GP
			    ,YD_HOME_BAY_GP AS YD_HOME_BAY_GP
			    ,YD_TCAR_WRK_ABLE_BAY1 AS YD_TCAR_WRK_ABLE_BAY1
			    ,YD_TCAR_WRK_ABLE_BAY2 AS YD_TCAR_WRK_ABLE_BAY2
			    ,YD_TCAR_WRK_ABLE_BAY3 AS YD_TCAR_WRK_ABLE_BAY3
			    ,YD_TCAR_WRK_ABLE_BAY4 AS YD_TCAR_WRK_ABLE_BAY4
			    ,YD_TCAR_WRK_ABLE_BAY5 AS YD_TCAR_WRK_ABLE_BAY5    
			    ,YD_CRN_USE_SEQ AS YD_CRN_USE_SEQ
			    ,YD_CRN_CONT_CARASGN_CNT AS YD_CRN_CONT_CARASGN_CNT
			    ,YD_CRN_CONT_CARASGN_WR AS YD_CRN_CONT_CARASGN_WR
			    ,YD_EQP_AUTO_CRN_MODE AS YD_EQP_AUTO_CRN_MODE
			FROM TB_YD_EQP
			WHERE YD_EQP_ID = :V_YD_EQP_ID
			    AND DEL_YN='N'
		 */
		rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdEqp", logId, methodNm, "크레인별 배차기준조회");

		// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
		if (rsResult == null || rsResult.size()>1) {

			szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
			slabUtils.printLog(logId, szMsg, "SL");
			// 대체크레인의 유무를 체크한다.
			// 대체크레인이 없으면 에러 리턴
			if (!szYD_ALT_CRN_YN.equals("Y")) {

				szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
				slabUtils.printLog(logId, szMsg, "SL");
				return -2;

			}
			// 대체크레인이 있으면 대체크레인 설비 상태 체크
			//blnRtnVal = eqpStatCheck(szYD_ALT_CRN);
			jrParam.setField("YD_EQP_ID", szYD_ALT_CRN );
			rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdEqp", logId, methodNm, " 대체크레인 설비 상태 체크");

			// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
			if (rsResult.size() == 0 || rsResult == null ) {

				szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
				slabUtils.printLog(logId, szMsg, "SL");
				return -3;

			} else {
				// 대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
				szCrn = szYD_ALT_CRN;
				szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
			}
		} else {
			// 작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
			szCrn = szYD_WRK_CRN;
			szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
		}
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		jrParam.setField("YD_EQP_ID", szCrn);
		//blnRtnVal = chkGetCrnSpec(szCrn, rsResult);
		/*
		 * 	SELECT YD_EQP_ID  AS YD_EQP_ID
			      ,REGISTER  AS REGISTER
			      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
			      ,MODIFIER  AS MODIFIER
			      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
			      ,DEL_YN  AS DEL_YN
			      ,YD_CRN_GRAB_TP  AS YD_CRN_GRAB_TP
			      ,YD_CRN_TONG_H  AS YD_CRN_TONG_H
			      ,YD_CRN_TONG_L  AS YD_CRN_TONG_L
			      ,YD_CRN_TONG_INTVL_W  AS YD_CRN_TONG_INTVL_W
			      ,YD_CRN_TONG_END_T  AS YD_CRN_TONG_END_T
			      ,YD_CRN_TONG_W_TOL  AS YD_CRN_TONG_W_TOL
			      ,YD_EQP_WAIT_LOC  AS YD_EQP_WAIT_LOC
			      ,YD_CRN_SB_H  AS YD_CRN_SB_H
			      ,YD_WRK_ABLE_L  AS YD_WRK_ABLE_L
			      ,YD_WRK_ABLE_W  AS YD_WRK_ABLE_W
			      ,YD_WRK_ABLE_SH  AS YD_WRK_ABLE_SH
			      ,YD_WRK_ABLE_WT  AS YD_WRK_ABLE_WT
			      ,YD_CRN_GRAB1_ABLE_SH  AS YD_CRN_GRAB1_ABLE_SH
			      ,YD_CRN_GRAB1_ABLE_WT  AS YD_CRN_GRAB1_ABLE_WT
			      ,YD_CRN_GRAB2_ABLE_SH  AS YD_CRN_GRAB2_ABLE_SH
			      ,YD_CRN_GRAB2_ABLE_WT  AS YD_CRN_GRAB2_ABLE_WT
			      ,YD_WRK_ABLE_XAXIS_FR  AS YD_WRK_ABLE_XAXIS_FR
			      ,YD_WRK_ABLE_XAXIS_TO  AS YD_WRK_ABLE_XAXIS_TO
			      ,YD_WRK_ABLE_YAXIS_FR  AS YD_WRK_ABLE_YAXIS_FR
			      ,YD_WRK_ABLE_YAXIS_TO  AS YD_WRK_ABLE_YAXIS_TO
			      ,YD_WRK_ABLE_ZAXIS_FR  AS YD_WRK_ABLE_ZAXIS_FR
			      ,YD_WRK_ABLE_ZAXIS_TO  AS YD_WRK_ABLE_ZAXIS_TO
			      ,YD_CRN_GRAB1_BM_EXPN_L  AS YD_CRN_GRAB1_BM_EXPN_L
			      ,YD_CRN_GRAB1_MGNT_CNT  AS YD_CRN_GRAB1_MGNT_CNT
			      ,YD_CRN_GRAB1_MGNT_GAP  AS YD_CRN_GRAB1_MGNT_GAP
			      ,YD_CRN_GRAB2_BM_EXPN_L  AS YD_CRN_GRAB2_BM_EXPN_L
			      ,YD_CRN_GRAB2_MGNT_CNT  AS YD_CRN_GRAB2_MGNT_CNT
			      ,YD_CRN_GRAB2_MGNT_GAP  AS YD_CRN_GRAB2_MGNT_GAP
			      ,YD_CRN_PILNG_EQPM_EXN  AS YD_CRN_PILNG_EQPM_EXN
			  FROM TB_YD_CRNSPEC
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
		 */
		rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.getYdCrnspec", logId, methodNm, " 대체크레인 설비 상태 체크");

		if(rsResult.size() == 0 || rsResult == null ) return -5;
		rsResult.first();
		recPara = rsResult.getRecord();
		//작업가능한 크레인
		recResult.setField("YD_WRK_CRN", szCrn);
		//스케쥴우선순위
		recResult.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR);
		//크레인 작업허용중량
		recResult.setField("YD_WRK_ABLE_WT", slabUtils.paraRecChkNull(recPara,"YD_WRK_ABLE_WT"));
		//크레인 집게허용 오차
		recResult.setField("YD_CRN_TONG_W_TOL", slabUtils.paraRecChkNull(recPara,"YD_CRN_TONG_W_TOL"));
		//크레인 작업가능 매수
		recResult.setField("YD_WRK_ABLE_SH", slabUtils.paraRecChkNull(recPara,"YD_WRK_ABLE_SH"));
		
		return 1;
	}
	

	/**
	 * 차량정지위치활성/비활성처리															원본위치: YdCommonUtils.procCarPosActiveOrInActive(recPara);
	 * 박영수    2020.11.10  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procCarPosActiveOrInActive(JDTORecord recInParam, String logId) throws JDTOException{
		String szMethodName    		= "procCarPosActiveOrInActive";
		String szOperationName 		= "차량정지위치활성/비활성처리";
		String methodNm        		= szMethodName+szOperationName;
		slabUtils.printLog(logId, methodNm , "S+");
		int intRtnVal               = -100;
		String szMsg                = null;
		String szRtnMsg             = PSlabYdConstant.RETN_CD_SUCCESS;
		String szYdStkBedActStat 	= "";					//야드적치베드활성상태
		String szYdStkLyrActStat 	= "";					//야드적치단활성상태
		//String logId              = recInParam.getResultCode();
		//String szYdStkLyrMtlStat 	= "";					//야드적치단재료상태

		/*
		 * 파라미터 확인
		 */
		String szYdStkColGp       = slabUtils.nvl(recInParam.getFieldString("YD_STK_COL_GP"), "");	//적치열
		String szYdCarUseGp       = slabUtils.nvl(recInParam.getFieldString("YD_CAR_USE_GP"), "");  //차량사용구분
		String szTrnEqpCd         = slabUtils.nvl(recInParam.getFieldString("TRN_EQP_CD"), "");		//운송장비코드
		String szCarNo            = slabUtils.nvl(recInParam.getFieldString("CAR_NO"), "");
		String szCardNo           = slabUtils.nvl(recInParam.getFieldString("CARD_NO"), "");
		String szTrnEqpStkCapa    = slabUtils.nvl(recInParam.getFieldString("TRN_EQP_STK_CAPA"), "");
		String szYdStkColActStat  = slabUtils.nvl(recInParam.getFieldString("YD_STK_COL_ACT_STAT"), "");
		
		String sModifier          = slabUtils.trim(recInParam.getFieldString("MODIFIER" )); 	//수정자(MODIFIER)
		JDTORecord jrRtn          = slabUtils.getParam(logId, methodNm, sModifier);		//Return Value
		//JDTORecord jrParam      = slabUtils.getParam(logId, methodNm, sModifier);		//DAO Parameter - Log ID, Method, 수정자 Set
		JDTORecord recPara        = slabUtils.getParam(logId, methodNm, sModifier);
		JDTORecord recInTemp      = slabUtils.getParam(logId, methodNm, sModifier);

	try {
		slabUtils.printLog(logId, "szYdStkColGp:" + szYdStkColGp , "SL");
		if( szYdStkColGp.equals("") ) {
			szMsg="[" + szOperationName + "] 적치열이 존재하지 않습니다.";
			//ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			slabUtils.printLog(logId, szMsg, "SL");
			
			jrRtn.setField("RTN_CD"	, "0");
			jrRtn.setField("RTN_MSG", szMsg );	 //RETN_CD_NO_PARAM
			return jrRtn;
		}
		
		if( szYdCarUseGp.equals(PSlabYdConstant.YD_CAR_USE_GP_TS) ) {			//구내운송
			if( szYdStkColActStat.equals(PSlabYdConstant.YD_STK_COL_ACTIVE) && szTrnEqpCd.equals("") ) {
				szMsg="[" + szOperationName + "] 차량사용구분["+szYdCarUseGp+"]이므로 운송장비코드가 존재해야합니다.";
				slabUtils.printLog(logId, szMsg, "SL");
				
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", szMsg );	 //RETN_CD_FAILURE
				return jrRtn;
			}
		}else if( szYdCarUseGp.equals(PSlabYdConstant.YD_CAR_USE_GP_DM) ) {	//출하차량
			if( szYdStkColActStat.equals(PSlabYdConstant.YD_STK_COL_ACTIVE) ) {
				if( szCarNo.equals("") ) {
					szMsg="[" + szOperationName + "] 차량사용구분["+szYdCarUseGp+"]이므로 차량번호가 존재해야합니다.";
					slabUtils.printLog(logId, szMsg, "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", szMsg );	 //RETN_CD_FAILURE
					return jrRtn;
				}
				if( szCardNo.equals("") ) {
					szMsg="[" + szOperationName + "] 차량사용구분["+szYdCarUseGp+"]이므로 카드번호가 존재해야합니다.";
					slabUtils.printLog(logId, szMsg, "SL");
					jrRtn.setField("RTN_CD"	, "0");
					jrRtn.setField("RTN_MSG", szMsg );	 //RETN_CD_FAILURE
					return jrRtn;
				}
			}
		}

		if( PSlabYdConstant.YD_STK_COL_ACTIVE.equals(szYdStkColActStat) ) {				//활성화
			szYdStkBedActStat 		= PSlabYdConstant.YD_STK_BED_ACTIVE;
			szYdStkLyrActStat 		= PSlabYdConstant.YD_STK_LYR_ACTIVE;
		}else if( PSlabYdConstant.YD_STK_COL_INACTIVE.equals(szYdStkColActStat) ) {		//비활성화
			szYdStkBedActStat 		= PSlabYdConstant.YD_STK_BED_INACTIVE;
			szYdStkLyrActStat 		= PSlabYdConstant.YD_STK_LYR_INACTIVE;
			szTrnEqpStkCapa 		= PSlabYdConstant.YD_STK_BED_WT_MAX_DEFAULT;
			szYdCarUseGp			= "";
		}else{
			szMsg="[" + szOperationName + "] 사용가능값[활성화"+PSlabYdConstant.YD_STK_COL_ACTIVE+":, 비활성화:"+PSlabYdConstant.YD_STK_COL_INACTIVE+"] - 사용할 수 없는 값["+szYdStkColActStat+"]입니다.";
			//ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			slabUtils.printLog(logId, szMsg, "SL");
			jrRtn.setField("RTN_CD"	, "0");
			jrRtn.setField("RTN_MSG", szMsg );	 //RETN_CD_FAILURE
			return jrRtn;
		}
		
			/*
			 * 적치열 활성/비활성 처리
			 */
	    	recInTemp = slabUtils.getParam(logId, methodNm, sModifier); 
	    	recInTemp.setField("YD_STK_COL_GP",        	szYdStkColGp);  //(k)
	    	recInTemp.setField("YD_CAR_USE_GP",        	szYdCarUseGp);
	    	recInTemp.setField("TRN_EQP_CD",           	szTrnEqpCd);
	    	recInTemp.setField("CAR_NO",           		szCarNo);
	    	recInTemp.setField("CARD_NO",           	szCardNo);
	    	recInTemp.setField("MODIFIER",   			sModifier);
	    	recInTemp.setField("YD_STK_COL_ACT_STAT",   szYdStkColActStat);
	    	
	    	/*
	    	 * (원본) szQueryIdUpd1 = com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.updYdStkcol
	    	 * UPDATE TB_YD_STKCOL
					  SET            
					      YD_CAR_USE_GP         = :V_YD_CAR_USE_GP        <--G        
					     ,TRN_EQP_CD            = :V_TRN_EQP_CD                    
					     ,CAR_NO                = :V_CAR_NO                            
					     ,CARD_NO               = :V_CARD_NO                          
					     ,YD_STK_COL_ACT_STAT   = :V_YD_STK_COL_ACT_STAT  <--L
					     ,MODIFIER              = :V_MODIFIER                        
					     ,MOD_DDTT              = SYSDATE             
					WHERE YD_STK_COL_GP         = :V_YD_STK_COL_GP

	    	 *	//intRtnVal = ydStkColDao.updYdStkcol(recInTemp, 0);
	    	 */
	    	
	    	int iupdCnt = commDao.update(recInTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdStkcol13", logId, methodNm, "적치열 활성/비활성 처리");
	    	
			if(iupdCnt <= 0) {
				szMsg="[" + szOperationName + "] 적치열[" + szYdStkColGp + "]수정 시 해당자료가 존재하지 않습니다.";
				slabUtils.printLog(logId, szMsg, "SL");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", szMsg );		//RETN_CD_FAILURE
				return jrRtn;
			}
			
			//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			//YdStockDAO ydStockDAO = new YdStockDAO();
			//ymCommonDAO dao = ymCommonDAO.getInstance();
			
			//장애 발생시 이전 소스로 원복 하기 위한 조치				(원본)
			//String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.CarPointinforegchklist";
		    //List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
		    
		    /*
		     * (원본) com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.CarPointinforegchklist
		     * select 'Y' AS CHK FROM DUAL
		     */
			JDTORecordSet unloadPointrec = commDao.select(recInTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.CarPointinforegchklist", logId, methodNm, "적치열 활성/비활성 처리");
			//JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
		    
			unloadPointrec.first();
			JDTORecord recTemp = unloadPointrec.getRecord();
			
	    	String CHK   = slabUtils.nvl(recTemp.getFieldString("CHK"), "");
	    	slabUtils.printLog(logId, "무조건Y:" + CHK, "SL");
	    	if(CHK.equals("Y")){ 	
	    		
	    		szMsg =  "▣▣▣▣차량포인트 통합관리(START):통합작업 시작 ▣▣▣▣";
	    		slabUtils.printLog(logId, szMsg, "SL");
	    		//저장위치로 차량 포인트 예약 하는 경우(출하)
				//String stkQueryId = "com.inisteel.cim.yd.dao.ydstkcoldao.carpointtrneqpcdupdateC2";
				
				//ydStockDAO.requestupdateData(stkQueryId, new Object[]{ szYD_STK_COL_ACT_STAT,szCAR_NO ,szCARD_NO,szYD_STK_COL_GP});
	    		/*
	    		 * (원본) com.inisteel.cim.yd.dao.ydstkcoldao.carpointtrneqpcdupdateC2
	    		 * update USRYDA.TB_YD_CARPOINT
					  set   YD_STK_COL_ACT_STAT=:V_STAT
					    ,   CAR_NO             =:V_CAR_NO
					    ,   CARD_NO            =:V_TRN_EQP_CD
					    ,   MOD_DDTT           =SYSDATE
					    ,   MODIFIER           ='CarPointC'
					 where YD_STK_COL_GP       =:V_YD_STK_COL_GP
	    		 */
	    		/* 포인트에 대한 추가 요건 필요!    kEY는 4자리 인데, 넘어가는 값은 6자리 :  현재 V_YD_STK_COL_GP 는  예)"DA0129"를 넘기고 있음 .   <--RETURN CODE 체크 안함. (
	    		 * 11E1, 11G1, 12E1, 31A1, 31A2, H1A2, H1B1, H1C1, H1F2, K1A1, K1D1, K1D2, K1E1,   (기존 데이터) 
	    		 * S1R2, T1B2, T1C1, T1D1, T1E1, T1E2, T1F1, T1G1, T2C1, T2D1, T2D2, T2F2, T2G1, T3A1, T3C1, T3C2, T3E1, T5E1
	    		 */
	    		
	    		recPara.setField("STAT",     		szYdStkColActStat);
	    		recPara.setField("CAR_NO",   		szCarNo);
	    		recPara.setField("TRN_EQP_CD",   	szCardNo);
	    		recPara.setField("YD_STK_COL_GP",   szYdStkColGp );

	    		iupdCnt = commDao.update(recPara, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.carpointtrneqpcdupdateC2", logId, methodNm, "차량포인트 통합관리(START):통합작업 시작");
	    		slabUtils.printLog(logId, "carpointtrneqpcdupdateC2 result:" + iupdCnt, "SL");
	    	}	
			
			/*
			 * 적치베드 상태 활성/비활성 처리
			 */
			recInTemp = slabUtils.getParam(logId, methodNm, sModifier); 
			recInTemp.setField("YD_STK_COL_GP", 		szYdStkColGp);
			recInTemp.setField("YD_STK_BED_ACT_STAT", 	szYdStkBedActStat);
			recInTemp.setField("YD_STK_BED_WT_MAX", 	szTrnEqpStkCapa);
			
			/*
			 * 
				UPDATE TB_YD_STKBED
				   SET YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
				     , YD_STK_BED_WT_MAX   = :V_YD_STK_BED_WT_MAX
				 WHERE YD_STK_COL_GP       = :V_YD_STK_COL_GP
			 */
			iupdCnt = commDao.update(recInTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdStkbedYdStkColGp", logId, methodNm, "적치베드 상태 활성/비활성 처리");

			if(iupdCnt <= 0) {
				szMsg = "[" + szOperationName + "] 적치열[" + szYdStkColGp + "]의 적치베드를 수정 시 오류발생 - 반환값 : " + iupdCnt;
				slabUtils.printLog(logId, szMsg, "SL");
				slabUtils.printLog(logId, szMsg, "SL");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", szMsg ); //RETN_CD_FAILURE
				return jrRtn;
			}
			
			/*
			 * 적치단 활성/비활성 처리
			 */
			recInTemp = slabUtils.getParam(logId, methodNm, sModifier); 
			recInTemp.setField("YD_STK_COL_GP", 		szYdStkColGp);
			recInTemp.setField("YD_STK_LYR_ACT_STAT", 	szYdStkLyrActStat);
			recInTemp.setField("STL_NO",			    "");
			recInTemp.setField("YD_STK_LYR_MTL_STAT", 	"E");
	    	
			/*
			 * (원본) com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrYdStkColGp
			 * 
				UPDATE TB_YD_STKLYR            
				   SET YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
				      ,STL_NO = :V_STL_NO
				      ,YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
				      ,MOD_DDTT = SYSDATE
				WHERE YD_STK_COL_GP  = :V_YD_STK_COL_GP
			 * 
				//intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGp(recInTemp);
			 */
			
			intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.pslabyd.dao.PSlabJspSeEJB.updYdStkbedYdStkColGp", logId, methodNm, "적치단 활성/비활성 처리");
			
		
			if(intRtnVal <= 0) {
				szMsg = "[" + szOperationName + "] 적치열[" + szYdStkColGp + "]의 적치단을 수정 시 오류발생 - 반환값 : " + intRtnVal;
				slabUtils.printLog(logId, szMsg, "SL");
				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", szMsg );	//RETN_CD_FAILURE
				return jrRtn;
			}
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", szRtnMsg );
			return jrRtn;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	
	}
	
	
	
	/**
	 * 다음 크레인 작업 반경내 슬라브 최대 높이 계산
	 * 염용선    2020.11.10   
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String getSlabMaxH(String gubun ,JDTORecord recInParam, String logId , String methodNm ,String sModifier) throws JDTOException{
		String setMaxSlab = "";	
		String queryId    = "";
		String ydUpWoLoc  = "";
		String ydDnWoLoc  = "";
		try {
			JDTORecord jrMaxParam = slabUtils.getParam(logId, methodNm, sModifier); 
			JDTORecordSet jsGetMaxLoc = JDTORecordFactory.getInstance().createRecordSet("temp");	// 초기화
			//다음작업 동선상 적치슬라브 최고 높이
			/*
			 * SELECT DD.YD_CRN_SCH_ID
				      ,CS.YD_UP_WO_LOC
				      ,CS.YD_DN_WO_LOC      
				  FROM TB_YD_CRNSCH    CS
				      ,TB_YD_CRNWRKMTL CM
				      ,(SELECT YD_CRN_SCH_ID
				              ,YD_EQP_ID
				          FROM TB_YD_CRNSCH
				         WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID) DD
				 WHERE CS.YD_EQP_ID       = DD.YD_EQP_ID
				   AND CS.YD_CRN_SCH_ID    != DD.YD_CRN_SCH_ID
				   AND CS.YD_CRN_SCH_ID     = CM.YD_CRN_SCH_ID
				   AND CS.YD_WRK_PROG_STAT IN ('W','1','2','3')
				   AND CS.DEL_YN            = 'N'
				   AND CM.DEL_YN            = 'N'
				 ORDER BY CS.YD_SCH_PRIOR , CS.YD_CRN_SCH_ID, CM.YD_STK_LYR_NO DESC
			 */
			if(gubun == "NEXT"){
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getUpDnLocNext";
			}else{
				queryId = "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getUpDnLoc";
			}
			
			JDTORecordSet jsGetUpDnLoc = commDao.select(recInParam, queryId, logId, methodNm, "권상권하위치 조회");

			if (jsGetUpDnLoc.size() > 0) {
				
				 ydUpWoLoc = slabUtils.trim(jsGetUpDnLoc.getRecord(0).getFieldString("YD_UP_WO_LOC")); //권상지시위치
				 ydDnWoLoc = slabUtils.trim(jsGetUpDnLoc.getRecord(0).getFieldString("YD_DN_WO_LOC")); //권하지시위치
				 
				if(ydDnWoLoc.startsWith("XX")){//권하위치 
					
				}else{
				     slabUtils.printLog(logId, ydUpWoLoc.substring(0, 6), "ydUpWoLoc-COL_GP---");
					 slabUtils.printLog(logId, ydUpWoLoc.substring(6, 8), "ydUpWoLoc-BED_NO---");
					 slabUtils.printLog(logId, ydDnWoLoc.substring(0, 6), "ydDnWoLoc-COL_GP---");
					 slabUtils.printLog(logId, ydDnWoLoc.substring(6, 8), "ydDnWoLoc-BED_NO---");
					 
				/*
				 * 	
					WITH LOCX AS
					(
					SELECT NVL((SELECT YD_STK_BED_XAXIS 
					              FROM TB_YD_STKBED A
					             WHERE A.YD_STK_COL_GP = :V_YD_UP_STK_COL_GP
					               AND A.YD_STK_BED_NO = :V_YD_UP_STK_BED_NO),0) AS UP_LOCX
					     , NVL((SELECT YD_STK_BED_XAXIS 
					              FROM TB_YD_STKBED A
					             WHERE A.YD_STK_COL_GP = :V_YD_DN_STK_COL_GP
					               AND A.YD_STK_BED_NO = :V_YD_DN_STK_BED_NO),0) AS DN_LOCX
					  FROM DUAL
					)
					SELECT MAX(YD_MTL_SUM) AS YD_MTL_T
					  FROM 
					       (
					        SELECT A.YD_STK_COL_GP
					             , A.YD_STK_BED_NO
					             , A.YD_STK_BED_XAXIS 
					             , NVL((SELECT SUM(SC.REAL_MEASURE_SLAB_T )
					                      FROM USRPTA.TB_PT_SLABCOMM SC 
					                         , TB_YD_STKLYR SL 
					                     WHERE SC.SLAB_NO = SL.STL_NO     
					                       AND SL.YD_STK_COL_GP = A.YD_STK_COL_GP
					                       AND SL.YD_STK_BED_NO = A.YD_STK_BED_NO
					                       
					               ),0) AS YD_MTL_SUM
					          FROM TB_YD_STKBED A
					             , LOCX B
					         WHERE A.YD_STK_COL_GP LIKE SUBSTR(:V_YD_UP_STK_COL_GP,0,2)||'%'
					           AND A.YD_STK_BED_XAXIS BETWEEN CASE WHEN B.UP_LOCX < B.DN_LOCX THEN B.UP_LOCX
					                                               ELSE DN_LOCX END 
					                                      AND CASE WHEN B.UP_LOCX < B.DN_LOCX THEN B.DN_LOCX
					                                               ELSE B.UP_LOCX END
					       ) 
				 */
					 
					 jrMaxParam.setField("YD_UP_STK_COL_GP", ydUpWoLoc.substring(0, 6)); //야드적치열구분
					 jrMaxParam.setField("YD_UP_STK_BED_NO", ydUpWoLoc.substring(6, 8)); //야드적치Bed번호
					 jrMaxParam.setField("YD_DN_STK_COL_GP", ydDnWoLoc.substring(0, 6)); //야드적치열구분
					 jrMaxParam.setField("YD_DN_STK_BED_NO", ydDnWoLoc.substring(6, 8)); //야드적치Bed번호
					 
					 jsGetMaxLoc = commDao.select(jrMaxParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabL2RcvSeEJB.getSlabMaxT", logId, methodNm, "적치열 최고높이 조회");
					 
					 if (jsGetMaxLoc.size() > 0) {
						 setMaxSlab = slabUtils.trim(jsGetMaxLoc.getRecord(0).getFieldString("YD_MTL_T")); //열 중에서 가장 높은 슬라브 두께
					 }
				}	 
			}	
				
			slabUtils.printLog(logId, gubun+" MaxSlabH : "+setMaxSlab, "SL");
			return setMaxSlab;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}
	
	/**
	 *      [A] 오퍼레이션명 :  자동화 크레인 CHECK 여부
	 *      -- 
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public boolean chkAutoCrn(String logId,String sModifier,String ydEqpId) throws DAOException {
		String methodNm = "자동화 크레인 CHECK [PSlabYdComm.chkAutoCrn]" ;
		String szYD_EQP_ID_GET = "";

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			/**********************************************************
			* 2. 설비정보 read
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, sModifier);
			jrParam.setField("YD_EQP_ID", ydEqpId); //공장구분 2,3

			/*
			SELECT NVL(YD_EQP_WRK_MODE2,'M')  AS  YD_EQP_WRK_MODE2
			  FROM TB_YD_EQP
			 WHERE DEL_YN = 'N'
			   AND YD_EQP_ID = :V_YD_EQP_ID
			*/  
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdComm.ChkCrnMode2", logId, methodNm, "설비정보 조회"); 
			
			if (jsChk.size() > 0) {
				szYD_EQP_ID_GET    = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE2"));
			}
			//if ("A".equals(szYD_EQP_ID_GET) ||"R".equals(szYD_EQP_ID_GET)){
			if ("A".equals(szYD_EQP_ID_GET)){ //리모컨은 유인
				slabUtils.printLog(logId, "크레인 상태 : "+szYD_EQP_ID_GET, "S-");
				return true;
			} else {
				slabUtils.printLog(logId, "크레인 상태 : "+szYD_EQP_ID_GET, "S-");
				return false;
			}
		} catch (DAOException e) {
			slabUtils.printErrorLog(slabUtils.makeErrorLog(logId, methodNm, e), this, e);
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	/***************************************************************************
	 * 공통 Check
	 **************************************************************************/
	/**
	 * [A] 오퍼레이션명 :  신규시스템 적용 여부
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public String ApplyYn(String szREPR_CD_GP,String szCD_GP,String szITEM) throws DAOException {
		String methodNm = "신규시스템 적용여부[PSlabYdComm.ApplyYn]" ;
		String logId = "";
		String szAPPLY_YN = "N";

		try {
			slabUtils.printLog(logId, methodNm, "SLC+");

			//수신 항목 값
			/**********************************************************
			* 2. 열정보 read
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam("", methodNm, "");
			jrParam.setField("REPR_CD_GP", szREPR_CD_GP  ); //작업구분
			jrParam.setField("CD_GP"     , szCD_GP       ); //구분
			jrParam.setField("ITEM"      , szITEM        ); //ITEM

			/* com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.getSlabApplyYn
			SELECT NVL(MAX(DTL_ITEM1),'N') AS APPLY_YN
			  FROM USRYDA.TB_YD_RULE
			 WHERE REPR_CD_GP  = :V_REPR_CD_GP  -- DYD300
			   AND CD_GP       = :V_CD_GP       -- D
			   AND ITEM        = :V_ITEM		-- 각각의 사용목적에 따라 다름
			   AND DEL_YN      = 'N'
			*/  
			
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.getSlabApplyYn", logId, methodNm, "열정보 Read"); 

			if (jsChk.size() > 0) {
				szAPPLY_YN    = slabUtils.trim(jsChk.getRecord(0).getFieldString("APPLY_YN"));
			}
			slabUtils.printLog(logId, methodNm, "SLC-");
			return szAPPLY_YN;
		} catch (DAOException e) {
			slabUtils.printErrorLog(slabUtils.makeErrorLog(logId, methodNm, e), this, e);
			return szAPPLY_YN;
		} catch (Exception e) {
			return szAPPLY_YN;
		}
	}
	


	//+-------------------+ 추가  +---------------------------------------------------------------------------+
	/**
	 *      [A] 오퍼레이션명 : 차량작업예정정보요구(procCarPlanInfo)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecordSet procCarPlanInfo_Slab(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "차량작업예정정보요구[PSlabYdComm.procCarPlanInfo_Slab] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		JDTORecordSet jsRst = null;
		
		try {
			slabUtils.printLog(logId, methodNm, "SL");
			
			//전달 값 
			String SearchFlag	= slabUtils.trim(rcvMsg.getFieldString("SEARCH_FLAG"));   	//1:상차도, 2:차량스케쥴 ID, 3:(상차차량 진입시 LOT편성건의 예약작업 자동생성건 _예정정보 전문 처리) 
			String ydLoadLoc   	= slabUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC"  )); 	//상차도 위치
			String ydPrepSchId 	= slabUtils.trim(rcvMsg.getFieldString("YD_PREP_SCH_ID"));
			String ydTrnEqpCd   = slabUtils.trim(rcvMsg.getFieldString("TRN_EQP_CD")); 
			
			String ydCarSchId  	= slabUtils.trim(rcvMsg.getFieldString("YD_CAR_SCH_ID"));  	//차량스케쥴 ID
			
			String msgId        = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			slabUtils.printLog(logId, "작업구분:"+ SearchFlag+ " 상차도 위치:"+ydLoadLoc + " msgId-->" + msgId, "SL"); 

			String sModifier    = slabUtils.trim(rcvMsg.getFieldString("MODIFIER")); 		//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydLoadLoc) && "".equals(ydCarSchId)) {
				slabUtils.printLog(logId, "상차도위치 또는 차량스케줄Id가 없음" , "SL");
			}

			JDTORecordSet jrCarInfo = JDTORecordFactory.getInstance().createRecordSet("");
			
			JDTORecord jrParam		= slabUtils.getParam(logId, methodNm, sModifier);
			jrParam.setField("YD_CARUD_STOP_LOC"	, ydLoadLoc);

			if (SearchFlag.length() < 0) {
				slabUtils.printLog(logId, methodNm + " 검색조건 없음 [" + SearchFlag + "]" , "SL");
				//throw new Exception("검색조건 없음 [" + SearchFlag + "]");
			} else if (SearchFlag.equals("1")) {
				if (ydLoadLoc.length() < 6) {						//상차도 위치  
					slabUtils.printLog(logId, methodNm + " 상차도 위치 Error [" + ydLoadLoc + "]" , "SL");
					//throw new Exception("상차도 위치 Error [" + ydLoadLoc + "]");
				}
			} else if (SearchFlag.equals("2")) {
				if (ydCarSchId.equals("")) {
					slabUtils.printLog(logId, methodNm + " 차량스케쥴 ID Error [" + ydCarSchId + "]" , "SL");
					//throw new Exception("차량스케쥴 ID Error [" + ydCarSchId + "]");
				}
			} 
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			/**********************************************************
			* 2. 차량예정정보 조회
			**********************************************************/			
			if(SearchFlag.equals("1") ) {
				//상차위치로 차량예정정보 조회
				jrParam.setField("YD_CARUD_STOP_LOC", ydLoadLoc);  	

				/* com.inisteel.cim.yd.pslabyd.dao.PSlabYdComm.getYdCarschCarGetInWorkByCarNo  
				SELECT  B.STL_NO     AS YD_STL_NO
				      , A.TRN_EQP_CD 
				      , A.CARD_NO    AS YD_CARD_NO
				      , (CASE WHEN YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN YD_CARLD_STOP_LOC ELSE YD_CARUD_STOP_LOC END) AS YD_PT_LOAD_LOC 
				      , TRIM(TO_CHAR(TO_NUMBER(B.YD_STK_LYR_NO),'00')) AS YD_LOAD_LOC_CD
				      , LPAD(NVL(SUBSTR(A.TRN_EQP_CD,2,2),A.CAR_KIND),2,' ') AS YD_PT_CLS
				      , A.YD_CAR_WRK_GP
				      , A.YD_CAR_PROG_STAT
				      , A.YD_CAR_USE_GP
				      , A.CAR_KIND
				      , A.YD_CAR_SCH_ID
				      , CASE WHEN A.YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN '4'  -- 구내출고(상차)
				             WHEN A.YD_CAR_PROG_STAT IN ('A','B','C','D','E') THEN '3'  -- 구내입고(하차)
				             END AS YD_WORK_CLS
				      ,COUNT(B.STL_NO) Over() AS YD_WORK_MAX_CNT
				      ,CASE WHEN A.YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN 
				            (
				               DECODE(SUBSTR(NVL((SELECT YD_STK_COL_GP FROM TB_YD_STKLYR WHERE STL_NO = B.STL_NO AND YD_STK_LYR_ACT_STAT = 'C' AND ROWNUM = 1), 'XXXXXX'),3,2),'PT','*','0')
				            )
				            WHEN A.YD_CAR_PROG_STAT IN ('A','B','C','D','E') THEN 
				            (   
				               DECODE(SUBSTR(NVL((SELECT YD_STK_COL_GP FROM TB_YD_STKLYR WHERE STL_NO = B.STL_NO AND YD_STK_LYR_ACT_STAT = 'C' AND ROWNUM = 1), 'XXXXXX'),3,2),'01','*','02','*','0')
				            )
				            ELSE '0' END  AS YD_WORK_STATE
				      , A.TRN_EQP_CD
				      , A.TRANS_ORD_DATE 
				      , A.TRANS_ORD_SEQNO 
				      , A.TRANS_ORD_DATE
				      , A.YD_CAR_WRK_GP
				      , YD_CARUD_STOP_LOC AS STOP_LOC                                -- 하차
				      , '' AS CMBN_CARLD_NO
				  FROM  TB_YD_CARSCH     A
				      , TB_YD_CARFTMVMTL B
				 WHERE  A.YD_CAR_SCH_ID = (
				                   SELECT MAX(YD_CAR_SCH_ID) AS YD_CAR_SCH_ID
				                    FROM USRYDA.TB_YD_CARSCH
				                   WHERE (CASE WHEN YD_CAR_PROG_STAT IN('2','3','4','5') THEN YD_CARLD_STOP_LOC
				                               ELSE YD_CARUD_STOP_LOC END) = :V_YD_CARUD_STOP_LOC
				                     AND DEL_YN='N'
				                    -- AND YD_CAR_PROG_STAT NOT IN ('1','A')
				                     AND TRN_EQP_CD = (
				                                             SELECT TRN_EQP_CD
				                                             FROM   TB_YD_CARPOINT
				                                             WHERE  YD_STK_COL_GP = :V_YD_CARUD_STOP_LOC                                  
				                                      )
				                          )
				   AND  A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID(+)
				   AND  A.YD_CAR_USE_GP = 'L'
				   AND  A.DEL_YN = 'N'
				   AND  B.DEL_YN(+) = 'N'
				   AND  (A.SPOS_WLOC_CD IN ('DWY22', 'DKY21') OR A.ARR_WLOC_CD IN ('DWY22', 'DKY21') )
				 ORDER BY B.YD_STK_LYR_NO 
				 */
				 
				jrCarInfo = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdComm.getYdCarschCarGetInWorkByCarNo", logId, methodNm, "상차위치로 차량예정정보 조회");
			} else if(SearchFlag.equals("3") ) { 					//--상차 저장품의 위치 정보 불확실성으로   현재 사용안하고 있음.  
				//상차차량 진입시 LOT편성건의 예약작업 자동생성건 :  예정정보 전문 처리 
				jrParam.setField("YD_CARUD_STOP_LOC", ydLoadLoc);  	
				jrParam.setField("YD_PREP_SCH_ID"	, ydPrepSchId);
				jrParam.setField("TRN_EQP_CD"	    , ydTrnEqpCd );	//상차진입차량번호(구내운송)
				/* com.inisteel.cim.yd.pslabyd.dao.PSlabYdComm.getYdCarschCarGetInLotByCarNo
				 * 
				WITH TMP_TBL  AS (
				  SELECT :V_TRN_EQP_CD        AS TRN_EQP_CD    FROM DUAL
				)
				SELECT WBM.STL_NO            AS YD_STL_NO   
				     , CS.TRN_EQP_CD           
				     , CS.YD_CAR_WRK_GP
				     , CS.YD_CAR_PROG_STAT
				     , CS.YD_CAR_USE_GP
				     , CS.CAR_KIND
				     , CS.YD_CAR_SCH_ID
				     , CS.YD_CARLD_WRK_BOOK_ID 
				     , CASE WHEN CS.YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN '4'  -- 구내출고(상차)
				            WHEN CS.YD_CAR_PROG_STAT IN ('A','B','C','D','E') THEN '3'  -- 구내입고(하차)
				             END AS YD_WORK_CLS
				     , LPAD(NVL(SUBSTR(CS.TRN_EQP_CD,2,2),CS.CAR_KIND),2,' ') AS YD_PT_CLS
				      ,COUNT(WBM.STL_NO) Over() AS YD_WORK_MAX_CNT
				     , CS.TRANS_ORD_DATE 
				     , CS.TRANS_ORD_SEQNO 
				     , '' YD_CAR_NO
				     , CS.YD_CAR_WRK_GP
				     , '' AS CMBN_CARLD_NO
				     , CS.YD_CAR_PROG_STAT
				     , CS.YD_CARLD_STOP_LOC AS STOP_LOC    
				     , (CASE WHEN CS.YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN YD_CARLD_STOP_LOC ELSE CS.YD_CARUD_STOP_LOC END) AS YD_PT_LOAD_LOC 
				     --, (SELECT YD_STK_LYR_NO FROM TB_YD_STKLYR WHERE DEL_YN = 'N' AND STL_NO = WBM.STL_NO AND ROWNUM = 1) AS YD_LOAD_LOC_CD  --현재LAY위치 
				     , LPAD(rownum, 2, '0') YD_LOAD_LOC_CD
				     ,CASE WHEN CS.YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN 
				                (DECODE(SUBSTR(NVL((SELECT YD_STK_COL_GP FROM TB_YD_STKLYR WHERE STL_NO = WBM.STL_NO AND YD_STK_LYR_ACT_STAT = 'C' AND ROWNUM = 1), 'XXXXXX'),3,2),'PT','*','0')   )
				           WHEN CS.YD_CAR_PROG_STAT IN ('A','B','C','D','E') THEN 
				                (DECODE(SUBSTR(NVL((SELECT YD_STK_COL_GP FROM TB_YD_STKLYR WHERE STL_NO = WBM.STL_NO AND YD_STK_LYR_ACT_STAT = 'C' AND ROWNUM = 1), 'XXXXXX'),3,2),'01','*','02','*','0')  )
				           ELSE '0' END  AS YD_WORK_STATE
				     
				  FROM  USRYDA.TB_YD_CARSCH     CS  
				      , TMP_TBL                 TM
				      , USRYDA.TB_YD_WRKBOOKMTL WBM  --(TB_YD_PREPMTL )과 CARSCH 매치X
				      , USRYDA.TB_YD_STOCK      ST 
				 WHERE  CS.YD_CAR_SCH_ID  =  ( 
				               SELECT YD_CAR_SCH_ID
				                 FROM USRYDA.TB_YD_CARSCH CS1
				                    , TMP_TBL             TM1
				                WHERE CS1.DEL_YN = 'N'
				                  AND CS1.TRN_EQP_CD = TM1.TRN_EQP_CD
				                  AND ROWNUM = 1
				                  )
				   AND  CS.YD_CARLD_WRK_BOOK_ID = WBM.YD_WBOOK_ID           
				   AND  WBM.STL_NO              = ST.STL_NO 
				 --  AND  WBM.DEL_YN              = 'N' 
				   AND  CS.YD_CAR_USE_GP        = 'L'
				 */
				jrCarInfo = commDao.select(jrParam, "com.inisteel.cim.yd.pslabyd.dao.PSlabYdComm.getYdCarschCarGetInLotByCarNo", logId, methodNm, "상차위치로 차량예정정보 조회");
			}
			

			/**********************************************************
			* 2. 차량예정정보 송신
			**********************************************************/
			if(jrCarInfo.size() > 0) {
				
				jrCarInfo.first();
				
				JDTORecord jsCarInfo = JDTORecordFactory.getInstance().create();
			    
				jsCarInfo= jrCarInfo.getRecord();
				String sTemp = slabUtils.paraRecChkNull(jsCarInfo, "YD_PT_LOAD_LOC");
				
				jrParam.setField("PT_LOAD_LOC"      	, slabUtils.paraRecChkNull(jsCarInfo, "YD_PT_LOAD_LOC"));   		// 상차도 위치  (예:DBPT01)				
				jrParam.setField("CAR_NO"      			, slabUtils.paraRecChkNull(jsCarInfo, "TRN_EQP_CD"    )); 			// 차량번호
				jrParam.setField("PT_CLS"       		, slabUtils.paraRecChkNull(jsCarInfo, "YD_PT_CLS"     ));   		// 차량구분				
				
				jrParam.setField("WORK_CLS"      		, slabUtils.paraRecChkNull(jsCarInfo, "YD_WORK_CLS"    ));			// 작업구분  (3)  				
				jrParam.setField("WORK_COIL_MAX_CNT"	, slabUtils.paraRecChkNull(jsCarInfo, "YD_WORK_MAX_CNT"));			// 작업총 수량
				
			    for (int ii = 0; ii < jrCarInfo.size(); ii++) {
			    	//차량작업 예정정보 전문 data setup
			    	slabUtils.printLog(logId, ii+"---->"+ sTemp, "SL");
			    	
			    	sTemp = slabUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_PT_LOAD_LOC"  ));
			    	slabUtils.printLog(logId, ""+ sTemp, "SL");
			    	
			    	
			    	jrParam.setField("STL_NO_"+ii	      	, slabUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_STL_NO"  )));	
			    	jrParam.setField("LOAD_LOC_CD_"+ii     	, slabUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_LOAD_LOC_CD"  )));	
			    	jrParam.setField("WORK_STATE_"+ii      	, slabUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_WORK_STATE"  )));	
			    	
			    	sTemp = slabUtils.trim(jrCarInfo.getRecord(ii).getFieldString("YD_WORK_STATE"  ));
			    	slabUtils.printLog(logId, ""+ sTemp, "SL");
				}
				//차량예정정보 백업 송신
			    jsRst = commDao.getMsgL2("YDY3L008BackUp", jrParam);
			    
			} else {
				//빈 전문 생성
			    jrParam.setField("PT_LOAD_LOC"      	, ydLoadLoc);   // 상차도 위치				
			    jrParam.setField("CAR_NO"      			, "");  		// 차량번호	
			    jrParam.setField("CARD_NO"      		, ""); 			// 차량번호	
			    jrParam.setField("PT_CLS"       		, "");   		// 차량구분				
			    jrParam.setField("WORK_CLS"      		, "");			// 작업구분  				
			    jrParam.setField("WORK_COIL_MAX_CNT"	, "0");			// 작업총 수량
			    
		    	jrParam.setField("STL_NO_0"				, ""); 
		    	jrParam.setField("LOAD_LOC_CD_0"		, "");
		    	jrParam.setField("WORK_STATE_0"			, "");
		    	
			    jsRst = commDao.getMsgL2("YDY3L008BackUp", jrParam);

			}
			slabUtils.printLog(logId, methodNm, "S-");
			
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", "전송되었습니다");
			return jsRst;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}		
	 
	

	/**
	 * 작업예약/재료삭제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param szYdWbookId
	 * @param szYdUserId
	 * @param logId
	 * @return String
	 * @throws DAOException
	 */
	public String delYdWrkbookNMtl(String szYdWbookId, String szYdUserId, String logId) throws DAOException {
		
		String szRtnMsg					= PSlabYdConstant.RETN_CD_SUCCESS;
		String szMethodName				= "[delYdWrkbookNMtl]";
		String szOperationName			= "작업예약/재료삭제";
		String methodNm 				= szOperationName+szMethodName;
		String szMsg					= "";
		String query_id 				= "";
		JDTORecord recPara				= null;
		//--------------------------------------------------------------------------------------------
		//	작업예약재료 삭제
		//--------------------------------------------------------------------------------------------
		try {
			
			szMsg="["+szOperationName+"] 작업예약["+szYdWbookId+"]재료 삭제 시작";
			slabUtils.printLog(logId,  szMsg, "S+");
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_WBOOK_ID", 		szYdWbookId);
			recPara.setField("DEL_YN", 				"Y");
			recPara.setField("MODIFIER", 			szYdUserId);
			
			if(!"".equals(szYdWbookId) ) {
				/*
				UPDATE TB_YD_WRKBOOKMTL  
				   SET MOD_DDTT    = SYSDATE
				        ,MODIFIER  = :V_MODIFIER  
				        ,DEL_YN    = :V_DEL_YN
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				 * 
				 */
				query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdMvCarSeEJB.updYdWrkbookmtl";
				commDao.update(recPara, query_id, logId, methodNm, "작업예약 재료 삭제 완료");
			
			}
			//--------------------------------------------------------------------------------------------
			//	저장품에서 작업예약ID와 스케줄코드 Clear시킴
			//--------------------------------------------------------------------------------------------
			
			szMsg="["+szOperationName+"] 저장품에서 작업예약ID["+szYdWbookId+"]와 스케줄코드 Clear 시작";
			slabUtils.printLog(logId, szMsg, "SL");
			
			/*
			UPDATE USRYDA.TB_YD_STOCK
			   SET YD_WBOOK_ID 		= NULL
			     , YD_SCH_CD 		= NULL 
			     , TRANS_ORD_SEQNO 	=(CASE WHEN CARD_NO IN('5555','6666','0000','2222','3333','9999','7777','8888') THEN NULL ELSE TRANS_ORD_SEQNO END)
			     , CARD_NO         	=(CASE WHEN CARD_NO IN('5555','6666','0000','2222','3333','9999','7777','8888') THEN NULL ELSE CARD_NO END)
			     , SAILNO          	=(CASE WHEN CARD_NO IN('5555','6666','0000','2222','3333','9999','7777','8888') THEN NULL ELSE SAILNO END)
			     , SCARFING_YN     	=(CASE WHEN CARD_NO IN('5555','6666','0000','2222','3333','9999','7777','8888') THEN NULL ELSE SCARFING_YN END)
			     , COIL_CAR_NO     	= NULL 
			     , COIL_CAR_LOTID_YN  = NULL 
			 WHERE STL_NO IN ( SELECT STL_NO 
			                     FROM TB_YD_WRKBOOKMTL 
				WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID) 
			 * 
			 * // 저장품의 작업예약ID와 스케줄코드를 삭제하는 쿼리
			 */
			query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdJspSeEJB.updYdStockDelYdWBookId2";
			commDao.update(recPara, query_id, logId, methodNm, "저장품에서 작업예약ID["+szYdWbookId+"]와 스케줄코드 Clear 완료");
			//--------------------------------------------------------------------------------------------
			
			//--------------------------------------------------------------------------------------------
			//	작업예약 삭제  : (기존) TB_YD_WRKBOOK 을 읽고, 다시 UPDATE 처리  하고 있음.     
			//--------------------------------------------------------------------------------------------
			szMsg="["+szOperationName+"] 작업예약["+szYdWbookId+"] 삭제 시작";
			slabUtils.printLog(logId, szMsg, "SL");
			
			/*
			//1. com.inisteel.cim.yd.pslabyd.dao.PSlabYdComm.getYdWrkbook
				SELECT YD_WBOOK_ID  AS YD_WBOOK_ID
				      ,REGISTER  AS REGISTER
				      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
				      ,MODIFIER  AS MODIFIER
				      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
				      ,DEL_YN  AS DEL_YN
				      ,YD_GP  AS YD_GP
				      ,YD_BAY_GP  AS YD_BAY_GP
				      ,YD_SCH_CD  AS YD_SCH_CD
				      ,YD_SCH_PRIOR  AS YD_SCH_PRIOR
				      ,YD_SCH_PROG_STAT  AS YD_SCH_PROG_STAT
				      ,YD_SCH_ST_GP  AS YD_SCH_ST_GP
				      ,YD_SCH_REQ_GP  AS YD_SCH_REQ_GP
				      ,YD_AIM_YD_GP  AS YD_AIM_YD_GP
				      ,YD_AIM_BAY_GP  AS YD_AIM_BAY_GP
				      ,YD_CTS_RELAY_YN  AS YD_CTS_RELAY_YN
				      ,YD_CTS_RELAY_BAY_GP  AS YD_CTS_RELAY_BAY_GP
				      ,YD_TO_LOC_DCSN_MTD AS YD_TO_LOC_DCSN_MTD
				      ,YD_TO_LOC_GUIDE  AS YD_TO_LOC_GUIDE
				      ,YD_WRK_PLAN_TCAR AS YD_WRK_PLAN_TCAR
				      ,(CASE WHEN YD_SCH_CD LIKE 'J_PT0_LM' THEN '' 
				             WHEN YD_SCH_CD LIKE 'J_PT5_LM' THEN '' 
				             WHEN YD_SCH_CD LIKE 'J_TR0_MM' THEN 'G' 
				             WHEN YD_SCH_CD LIKE 'J_TR5_MM' THEN 'G' 
				        ELSE YD_CAR_USE_GP END) AS YD_CAR_USE_GP
				      ,TRN_EQP_CD AS TRN_EQP_CD
				      ,CAR_NO AS CAR_NO
				      ,CARD_NO AS CARD_NO
				   FROM TB_YD_WRKBOOK
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				 
			//2. com.inisteel.cim.yd.pslabyd.dao.PSlabYdComm.updYdWrkbook 
				UPDATE TB_YD_WRKBOOK
				   SET MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,DEL_YN = :V_DEL_YN
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			 */
			
			query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdComm.getYdWrkbook";
			JDTORecordSet jrWbook = commDao.select(recPara, query_id, logId, methodNm, "작업예약ID Clear 대상확인작업");
			if (jrWbook.size() > 0) {
				
				query_id = "com.inisteel.cim.yd.pslabyd.dao.PSlabYdComm.updYdWrkbook";
				commDao.update(recPara, query_id, logId, methodNm, "작업예약ID Clear 작업");
			}
			
			szMsg="["+szOperationName+"] 작업예약["+szYdWbookId+"] 삭제 완료 - 반환메세지 : " + szRtnMsg;
			slabUtils.printLog(logId, szMsg, "SL");
			//--------------------------------------------------------------------------------------------
			return szRtnMsg;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	

	/**
	 *      [A] 오퍼레이션명 : chkField 
	 *      원본: ydCarSchDao -> YdDaoUtils
	 * 
	 * @param JDTORecord inRec	         대상 레코드,
	 *        String     szFieldName     Field Name,
	 *        int        intMaxLen       Field Length,
	 *        int        intNullChk      Null Check 구분(0: primary key Check, 1: Null Check Length Check, 
	 *                                                  2: Length Check,  3: No Check),
	 *        char       chDataType      DataType('S':String, 'D':double, 'L':long, 'P':PAGE[LONG], 'R':ROW[LONG]),
	 *        int        intPre          지수부 길이,
	 *        int        intPost         소수부 길이,
	 * @return true, false			     true:성공, false:실패
	 * @throws DAOException
	 */	
	public boolean chkField (JDTORecord inRec, String szFieldName, int intMaxLen, 
			                 int intChkNull, String chDataType , int intPre, int  intPost, String logId) throws DAOException {
		String szMethodName = "chkField";
		int 	intRtnVal 	= 0;
		int 	intVal 		= 0;
		Integer intObj 		= null;
		double 	dblVal 		= 0;
		Double 	dblObj 		= null;
		long 	lngVal 		= 0;
		Long 	lngObj 		= null;
		boolean blnRtnVal 	= true;
		String 	szTemp 		= "";
		String 	szData 		= "";
		String 	szMsg 		= "";
		String  methodNm    = "";
		
		String STRING_TYPE			= "S";
		String DATETIME_TYPE 		= "T";
		String DOUBLE_TYPE 			= "D";
		String LONG_TYPE			= "L";
		String INTEGER_TYPE			= "I";
		String PAGE_COUNT_TYPE		= "P";
		String ROW_COUNT_TYPE		= "R";

		try {
			szTemp = this.paraRecChkNull_2(inRec, szFieldName);
	//		if (inRec.getFieldString(szFieldName) == null) {
	//			szTemp = "";
	//		}
	//		else
	//			szTemp = inRec.getFieldString(szFieldName);
			
			// parameter check
			intRtnVal = this.chkParam(szTemp, intMaxLen, intChkNull, logId);
			
			// primary key error return
			if (intRtnVal == -1 || intRtnVal == -2) {
				szMsg = szFieldName + " Error!!!";
				slabUtils.printLog(logId, szMsg, "SL");
				return blnRtnVal = false;
			}
			
			// data length error
			if (intRtnVal == -3)
				//data cut
				szData = this.dataCut(szTemp, intMaxLen);
			else
				szData = szTemp;
			
			
			if (szData.equals("")) {
				inRec.setField(szFieldName, szData);
			} else {
				//double
				if (chDataType == DOUBLE_TYPE) {
					dblVal = StringHelper.parseDouble(szData, this.makeErrorDouble(intPre, intPost));
					dblObj = new Double(dblVal);
					inRec.setField(szFieldName, dblObj);
				}else if( chDataType == INTEGER_TYPE ) {
				//int
					intVal = StringHelper.parseInt(szData);
					intObj = new Integer(intVal);
					inRec.setField(szFieldName, intObj);
				//long 
				} else if (chDataType == LONG_TYPE) {
					lngVal = StringHelper.parseLong(szData, this.makeErrorLong(intMaxLen));
					lngObj = new Long(lngVal);
					inRec.setField(szFieldName, lngObj);
				//Page Count 처리
				} else if (chDataType == PAGE_COUNT_TYPE) {
					lngVal = StringHelper.parseLong(szData, 1);
					lngObj = new Long(lngVal);
					inRec.setField(szFieldName, lngObj);
				//Row Count 처리	
				} else if (chDataType == ROW_COUNT_TYPE) {	
					lngVal = StringHelper.parseLong(szData, 10);
					lngObj = new Long(lngVal);
					inRec.setField(szFieldName, lngObj);
				}else if( chDataType == DATETIME_TYPE ) {
					inRec.setField(szFieldName, szData.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", ""));
				//String	
				} else
					inRec.setField(szFieldName, szData);
			}
	
			return blnRtnVal;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			//	szMsg = "chkField() Exception";
			//	slabUtils.printLog(logId,  szMsg, "SL");
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			//throw new JDTOException(szClassName + e.getMessage(), e);
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : chkParam 
	 * 
	 * @param String szData			// 체크 대상 문자열
	 *        int    intDataLen     // 체크 대상 문자열 최대 길이
	 *        int    intNullChk     // Null Check 구분 0: primary key Check, 1: Null Check Length Check, 
	 *                                                2: Length Check, 3: No Check
	 * @return int      			// 0:성공, -1:pk error, -3:data length over
	 * @throws
	 */	
	public int chkParam(String szData, int intDataLen, int intNullChk , String logId) {
		String szMsg        = null;
		String szMethodName = "chkParam";
		int intRtnVal = 0;
		String logId2 = logId; 
		
		try {
			if (intNullChk == 0) {
				//not null이고 고정길이 체크
				if(szData.equals("") || ((!szData.equals("")) && szData.length() != intDataLen)) {
					intRtnVal = -1;
					//szMsg="<"+szMethodName+"  >>>>>  null이거나 고정길이 Error : Data ("+szData
					//         +"), Length("+szData.length()+", "+intDataLen+")";
					//ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.WARNING);
				}
			} else if (intNullChk == 1) {
				//not null이고 가변길이 체크
				if(szData.equals("")) {
					intRtnVal = -1;
					//szMsg="<"+szMethodName+"  >>>>> Null Data P1 : Data ("+szData+")";
					//ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.WARNING);
				}
				else {
					//제한길이보다 길면 cut
					if (szData.trim().length() > intDataLen) {
						//szMsg="<"+szMethodName+"  >>>>> Data길이 Error : Length("+szData.length()+", "+intDataLen+")";
						//ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.WARNING);
						intRtnVal = -3;
					}
				}
			} else if (intNullChk == 2) {
				//가변길이 체크
				if (szData.equals("")) {
					intRtnVal = 0;
					//szMsg="<"+szMethodName+"  >>>>> Null Data P2 : Data ("+szData+")";
					//ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.WARNING);
				}
				else
					//제한길이보다 길면 cut
					if (szData.trim().length() > intDataLen) {
						intRtnVal = -3;
						//szMsg="<"+szMethodName+"  >>>>> 제한길이보다 큼 : Length("+szData.length()+", "+intDataLen+")";
						//ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.WARNING);
					}
			} else if (intNullChk == 3) {
				//no check
				intRtnVal = 0;
			}
		} catch(Exception e) {
			
			szMsg = "Exception: " + e.getMessage();
			slabUtils.printLog(logId2, szMsg, "SL");
			return intRtnVal = 0;
			
		}
		return intRtnVal;
	} // end of chkParam
	
	/**
	 *      [A] 오퍼레이션명 : dataCut 
	 * 
	 * @param int intMaxLen         // Field Length
	 * @return String     			// 데이터 길이로 보정된 String
	 */	
	public String dataCut(String strValue, int intMaxLen) {
		String strRtnVal = new String();

		for(int i = 0; i < intMaxLen; i++){
			strRtnVal = strRtnVal + strValue.charAt(i);
		}

		return strRtnVal;
	}

	/**
	 *      [A] 오퍼레이션명 : makeErrorLong 
	 * 
	 * @param int intMaxLen         // Field Length
	 * @return long     			// 데이터 길이만큼 9로 채워진 long 값
	 */	
	public long makeErrorLong(int intMaxLen) {
		String szMsg        = null;
		String szMethodName = "makeErrorLong";
		long lngRtnVal;
		String strTemp = new String();
		
		for (int i = 0; i < intMaxLen; i++) {
			strTemp = strTemp.concat("9");
		}
		lngRtnVal = StringHelper.parseLong(strTemp);

		return lngRtnVal;
	} // end of makeErrorLong
	

	
	/**
	 *      [A] 오퍼레이션명 : makeErrorDouble 
	 * 
	 * @param int intPre            // Field Length(지수부)
	 *        int intPost           // Field Length(소수부)
	 * @return double			    // 데이터 길이만큼 9로 채워진 double 값
	 */	
	public double makeErrorDouble(int intPre, int intPost) {

		double dblRtnVal;
		String strTemp = new String();

		if (intPre == 0 && intPost == 0)
			return dblRtnVal = 0;
		for (int i = 0; i < intPre; i++) {
			strTemp = strTemp.concat("9");
		}
		if (intPost > 0) {
			strTemp = strTemp.concat(".");
			for (int i = 0; i < intPost; i++) {
				strTemp = strTemp.concat("9");
			}
		}
		dblRtnVal = StringHelper.parseDouble(strTemp);

		return dblRtnVal;
	} // end of makeErrorDouble
	
	
	/**
	 *      [A] 오퍼레이션명 : paraRecChkNull_2 
	 * 
	 * @param  JDTORecord recPara    // 파라미터 레코드
	 *         String szFieldName    // 필드 이름
	 * @return String			     // 해당 필드의 데이터
	 * @throws JDTOException 
	 */
	public String paraRecChkNull_2(JDTORecord recPara, String szFieldName) throws JDTOException {
		String szRtnVal = null;
		if (recPara.getField(szFieldName) == null)
			szRtnVal = "";
		else
			szRtnVal = recPara.getFieldString(szFieldName);				
		
		return szRtnVal;
	} 


	/**
	 * SMS SENDER
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
    public String updSmsMsgSend(JDTORecord recInPara) throws DAOException {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName = null;
		
		szMsg        				= "";
		szMethodName 				= "updSmsMsgSend";
		String szOperationName 		= "SMS SENDER";
		 
		JDTORecord	inRecord 		= null;
		try {
			
			szMsg = "SMS SENDER 시작";
			slabUtils.printLog(szOperationName, szMsg, "SMS");
			
			// JDTORecord 생성
			inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("FROM_SENDER_NAME", new String("야드관리"));	// SMS 보내는 사람 성명
			
			inRecord.setField("FROM_PHONE_NO", recInPara.getFieldString("FROM_PHONE_NO"));	// SMS 보내는 사람 핸드펀번호
			inRecord.setField("TO_PHONE_NO"  , recInPara.getFieldString("TO_PHONE_NO"));	// SMS 받는 사람 핸드펀번호
			inRecord.setField("TO_CONTENT"   , recInPara.getFieldString("TO_CONTENT"));		// SMS 전송 내용
			inRecord.setField("TO_SEND_TIME" , new String(""));								// SMS 전송시간
			
			//---------------------------------------------------------------------
//			// SMS전송 객체
//			SmsSender	sender			= null;	
//			// 객체생성
//		    sender = new SmsSender();
//		    // 객체초기화
//		    sender.initService();
//		
//		    sender.send(inRecord);
			//---------------------------------------------------------------------
		    
			//---------------------------------------------------------------------
		    MessageSenderAuto    sender = new MessageSenderAuto();
		    inRecord.setField("RECV_ID", "YD00001");
    		inRecord.setField("GROUP_ID", "MMS1");
		    inRecord.setField("PROGRAM_ID", "updSmsMsgSendYD");

		    sender.sendAutoSMS(inRecord);
		    //---------------------------------------------------------------------

			
			szMsg = "SMS SENDER 끝";
			slabUtils.printLog(szOperationName, szMsg, "SMS");
			
		}catch(Exception ex) {
			szMsg = "["+szOperationName+"] SMS 송신 ERROR - 메세지 : " + ex.getMessage();
			slabUtils.printLog(szOperationName, szMsg, "SMS");
		}
		return PSlabYdConstant.RETN_CD_SUCCESS;
		
		
	}	
    

	/**
	 * [A] 오퍼레이션명 :  후판슬라브_신규모듈_시스템 적용 여부                               
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public String AppDydYn(String sREPR_CD_GP, String sCD_GP, String sITEM, String sDESC, String sModifier) throws DAOException {
		String methodNm = "후판슬라브_신규모듈_시스템 적용여부[PSlabYdComm.AppDydYn]" ;
		String szAPPLY_YN = "N";
		String logId = "appDydYn";

		try {
			//DAO Parameter - Log ID, Method, 수정자 Set
			slabUtils.printLog(logId, methodNm, "SL+");
			JDTORecord jrParam     = slabUtils.getParam("appDydYn", methodNm, sModifier);
			//1.수신 항목 값
			
			jrParam.setField("REPR_CD_GP", sREPR_CD_GP  ); //작업구분
			jrParam.setField("CD_GP"     , sCD_GP       ); //구분 ("D":업무 적용, "N":업무미적용처리)
			if("".equals(sCD_GP) ) {
				slabUtils.printLog(logId, "세부 업무seq 오류 입니다", "SL");
				return "N";       //구분코드 오류 입력
			}
			
			jrParam.setField("ITEM"      , sITEM        ); //ITEM
			
			jrParam.setField("REGISTER"  , sModifier    );
			jrParam.setField("DTL_ITEM1" , "N"          ); //적용여부 (최초N값) , 반영후 Y로 변경하면 적용 시작 
			jrParam.setField("DTL_ITEM2" , ""           );
			jrParam.setField("DTL_ITEM3" , ""           );
			jrParam.setField("DEL_YN"    , "N"          );
			jrParam.setField("REPR_CD_CONTENTS", sDESC  ); //적용업무내용 
			
			if("N".equals(sCD_GP)) {
				//업무 미적용 처리  
				/* com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.updYdRuleNvl
				UPDATE TB_YD_RULE
				   SET DTL_ITEM1 = 'N'
				       , DECODE(DTL_ITEM2, '','', 'N')
				       , DECODE(DTL_ITEM3, '','', 'N')
				   , MODIFIER = :V_MODIFIER
				   , MOD_DDTT = SYSDATE  
				 WHERE REPR_CD_GP = :V_REPR_CD_GP
				   AND CD_GP = 'D'
				   AND ITEM = :V_ITEM
				 */
				String queryId = "com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.updYdRuleNvl";
				commDao.update(jrParam, queryId, logId, methodNm, "(주의)모듈_미적용처리");
				
			} else {
				
				/* 신규모듈_(후판슬라브)시스템 적용 여부 -com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.getSlabAppDydYn 
				MERGE INTO TB_YD_RULE TM USING (
				
				  SELECT :V_REPR_CD_GP  AS REPR_CD_GP
				       , :V_CD_GP       AS CD_GP
				       , :V_ITEM        AS ITEM
				    FROM DUAL  
				
				) DD ON (TM.REPR_CD_GP = DD.REPR_CD_GP AND TM.CD_GP = DD.CD_GP AND TM.ITEM = DD.ITEM )    
				WHEN NOT MATCHED THEN
				
				  INSERT ( 
				   REPR_CD_GP, CD_GP   ,ITEM
				  ,REPR_CD_CONTENTS    ,DEL_YN
				  ,REGISTER  ,REG_DDTT ,MODIFIER ,MOD_DDTT
				  ,DTL_ITEM1 ,DTL_ITEM2 ,DTL_ITEM3    
				  )   VALUES     (
				   :V_REPR_CD_GP, :V_CD_GP  ,:V_ITEM
				  ,:V_REPR_CD_CONTENTS      ,:V_DEL_YN
				  ,:V_REGISTER  ,SYSDATE ,:V_MODIFIER ,SYSDATE
				  ,:V_DTL_ITEM1 ,:V_DTL_ITEM2 ,:V_DTL_ITEM3  
				  )
				 */
				String queryId = "com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.getSlabAppDydYn";
				commDao.update(jrParam, queryId, logId, methodNm, "신규모듈적용여부_등록체크");
				/* com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.getSlabApplyYn
				SELECT NVL(MAX(DTL_ITEM1),'N') AS APPLY_YN
				  FROM USRYDA.TB_YD_RULE
				 WHERE REPR_CD_GP  = :V_REPR_CD_GP  
				   AND CD_GP       = :V_CD_GP       
				   AND ITEM        = :V_ITEM		
				   AND DEL_YN      = 'N'
				 */  
				queryId = "com.inisteel.cim.yd.pslabyd.session.PSlabYdComm.getSlabApplyYn";
				JDTORecordSet jsChk = commDao.select(jrParam, queryId, logId, methodNm, "신규모듈적용여부"); 
				
				if (jsChk.size() > 0) {
					szAPPLY_YN    = slabUtils.trim(jsChk.getRecord(0).getFieldString("APPLY_YN"));
				}
			}
			slabUtils.printLog(logId, methodNm, "SL-");
			return szAPPLY_YN;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
    
}
