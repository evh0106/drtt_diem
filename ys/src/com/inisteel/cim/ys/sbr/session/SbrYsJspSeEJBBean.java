/**
 * @(#)SbrYsJspSeEJBBean
 *
 * @version          V1.00
 * @author           김현규
 * @date             2026/01/07
 *
 * @description      특수강소형야드 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2026/01/07                         김현규      최초 등록
 */
package com.inisteel.cim.ys.sbr.session;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ct.common.util.CmnUtil;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.ys.sbr.dao.SbrYsDAO;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.session.YsComm;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.common.util.YsConstant;
import com.inisteel.cim.ys.common.util.YsQueryIF;
import com.inisteel.cim.ys.common.util.YsQueryIFCar;
import com.inisteel.cim.ys.common.util.YsQueryIFSbr;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import xlib.cmc.GridData;

/**
 * [A] 클래스명 : 대형 압연 옥내 야드 화면관리 Session EJB
 *
 * @ejb.bean name="SbrYsJspSeEJB" jndi-name="SbrYsJspSeEJB" type="Stateless" view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
 */
public class SbrYsJspSeEJBBean extends BaseSessionBean implements YsQueryIFSbr, YsQueryIF, YsQueryIFCar {
	private static final long serialVersionUID = 1L;
	
	private YsCommUtils 		commUtils  = new YsCommUtils();
	private YsConstant  		constant   = new YsConstant();
	private YsCommDAO   		commDao    = new YsCommDAO();
	private YsComm      		ysComm     = new YsComm();
	
	private SbrYsComm   		sbrYsComm  = new SbrYsComm();
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 작업예약등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updbtMvStkWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "작업예약등록[SbrYsJspSeEJB.updbtMvStkWrkBook] < " + gdReq.getNavigateValue();
		String logId    = gdReq.getIPAddress();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord    jrRtn  = null;
			JDTORecordSet jsMsg  = JDTORecordFactory.getInstance().createRecordSet("");

			String stlNos        = commUtils.trim(gdReq.getParam("SSTL_NOS"         ));	//재료번호들
			String ysStkColGp    = commUtils.trim(gdReq.getParam("YS_STK_COL_GP"   ));	//야드적치열구분(6자리 이상)
			String ydToLocGuide  = commUtils.trim(gdReq.getParam("YD_TO_LOC_GUIDE" ));	//야드To위치Guide
			String ydWrkCrn		 = commUtils.trim(gdReq.getParam("YD_WRK_CRN"));		//야드작업크레인(작업자지정 크레인)
			String ydPrepSchId   = commUtils.trim(gdReq.getParam("YD_PREP_SCH_ID" ));	//야드준비스케쥴ID(차량상차작업예약ID)

			StringBuffer sbImpPros  = new StringBuffer();	//주요진행내용로그
			
			
			/**********************************************************
			* 0. 파라미터 체크
			**********************************************************/
			commUtils.printLog(logId, "파라미터체크::stlNos[" + stlNos + "], ysStkColGp[" + ysStkColGp + "], ydToLocGuide[" + ydToLocGuide + "], ydWrkCrn[" + ydWrkCrn + "], ydPrepSchId[" + ydPrepSchId + "]", "SL");
			sbImpPros.append("[작업예약등록 > 파라미터체크]::시작 \r\n");
			sbImpPros.append(" > 파라미터체크::stlNos["+stlNos+"] \r\n");
			sbImpPros.append(" > 파라미터체크::ysStkColGp["+ysStkColGp+"] \r\n");
			sbImpPros.append(" > 파라미터체크::ydToLocGuide["+ydToLocGuide+"] \r\n");
			sbImpPros.append(" > 파라미터체크::ydWrkCrn["+ydWrkCrn+"] \r\n");
			sbImpPros.append(" > 파라미터체크::ydPrepSchId["+ydPrepSchId+"] \r\n");
			
			//0.1. 재료번호 존재유무 체크
			if( "".equals(stlNos) ) {
				throw new Exception("이적 재료번호가 없습니다.");
			//0.2. 야드적치열구분(6자리 이상) 체크
			} else if( "".equals(ysStkColGp) || ysStkColGp.length() < 6 ) {
				throw new Exception("적치열[" + ysStkColGp + "] 정보가 없습니다.");
			}

			sbImpPros.append("[작업예약등록 > TO파라미터체크]::종료 \r\n \r\n");
			
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));	//DAO Parameter - Log ID, Method, 수정자 Set
			jrParam.setField("SSTL_NOS"        , stlNos    );	//재료번호들
			jrParam.setField("YS_STK_COL_GP"   , ysStkColGp);	//야드적치열구분
			
			
			/**********************************************************
			* 1. 추출대 입고 크레인 지정여부 확인
			**********************************************************/
			sbImpPros.append("[작업예약등록 > 추출대 입고 크레인 지정여부 확인]::시작 \r\n");
			
        	//"추출대 입고"이고, "지정크레인" 존재시 지정된 크레인으로 변경
    		JDTORecordSet rsParam = commDao.select(jrParam, getPCFixedEqpId, logId, methodNm, "추출대(GDPC11/GDPC21) 입고용 지정 크레인 조회");
    		
			if( rsParam.size() > 0 ) {
				
				rsParam.first();
				JDTORecord recInPara = rsParam.getRecord();
				
				String sPCFixedEqpId = commUtils.trim(recInPara.getFieldString("YD_EQP_ID"));

				commUtils.printLog(logId, "	 > 추출대 입고 지정 크레인::야드작업크레인(작업자지정 크레인) ("+ydWrkCrn+"), sPCFixedEqpId ("+sPCFixedEqpId+")", "SL");
    			sbImpPros.append("	 > 추출대 입고 지정 크레인::야드작업크레인(작업자지정 크레인) ("+ydWrkCrn+"), sPCFixedEqpId ("+sPCFixedEqpId+") \r\n");

    			//"지정크레인" 존재시 지정된 크레인으로 변경
        		if( !"".equals(sPCFixedEqpId) && !"ALL".equals(sPCFixedEqpId) ) {
        			ydWrkCrn = sPCFixedEqpId;
        		}
			}

			sbImpPros.append("[작업예약등록 > 추출대 입고 크레인 지정여부 확인]::종료 \r\n");
        	
        	
			/**********************************************************
			* 2. 스케줄코드 설정
			**********************************************************/
			sbImpPros.append("[작업예약등록 > 스케줄코드설정]::시작 \r\n");
			
			String ydSchCd    = "";	//야드스케줄코드
			String ydAimBayGp = "";	//야드목표동구분
			
			//2.1. "야드목표동구분" 설정
			if( "".equals(ydToLocGuide) ) {
				ydAimBayGp = ysStkColGp.substring(1, 2);
			} else {
				ydAimBayGp = ydToLocGuide.substring(1, 2);
				//"To위치"가 "야드+동"까지만 있으면 "위치검색BED 기준 적용"
				if( ydToLocGuide.length() < 4 ) {
					ydToLocGuide = "";
				}
			}
			
			//2.2. 스케줄코드 생성
			String sStkSpan    = ysStkColGp.substring(2, 4);	// From-SPAN   : [소형적치대: 01/02/03/TY],[설비: TF/PC/TC/TR]
			String sStkSpanCol = ysStkColGp.substring(2, 6);	// From-SPANCOL: [소형적치대: 0101-08/0201-08/0301-18/TY01-02],[설비: TF11 12 21 22/PC11 21/TC04 05 06/TR11 21]
			
			commUtils.printLog(logId, " > 스케줄코드 생성::ysStkColGp  == "+ysStkColGp,  "SL");
			commUtils.printLog(logId, " > 스케줄코드 생성::sStkSpan    == "+sStkSpan,    "SL");
			commUtils.printLog(logId, " > 스케줄코드 생성::sStkSpanCol == "+sStkSpanCol, "SL");
			
			//"[From] 적치된 위치"가 존재하면...
			if( !"".equals(ysStkColGp) ) {
				//소형 추출대입고 (#1)
				if( constant.EQP_D_GDPC11.substring(2).equals(sStkSpanCol) ) {
					ydSchCd = constant.SCH_CD_GDPC11LM;
				//소형 추출대입고 (#2)
				} else if( constant.EQP_D_GDPC21.substring(2).equals(sStkSpanCol) ) {
					ydSchCd = constant.SCH_CD_GDPC21LM;
					
				//소형 이송입고 (#1)
				} else if( constant.EQP_D_GDTR11.substring(2).equals(sStkSpanCol) ) {
					ydSchCd = constant.SCH_CD_GDTR11LM;
				//소형 이송입고 (#2)
				} else if( constant.EQP_D_GDTR21.substring(2).equals(sStkSpanCol) ) {
					ydSchCd = constant.SCH_CD_GDTR21LM;

				//소형 대차하차 (#4)
				} else if( constant.EQP_D_GDTC04.substring(2).equals(sStkSpanCol) ) {
					ydSchCd = constant.SCH_CD_GDTC04LM;
				//소형 대차하차 (#5)
				} else if( constant.EQP_D_GDTC05.substring(2).equals(sStkSpanCol) ) {
					ydSchCd = constant.SCH_CD_GDTC05LM;
				//소형 대차하차 (#6)
				} else if( constant.EQP_D_GDTC06.substring(2).equals(sStkSpanCol) ) {
					ydSchCd = constant.SCH_CD_GDTC06LM;
				}
			}
			
			//스케줄코드 미확정 && "[To] TO위치가이드"가 존재하면...
			//"TO위치가이드"가 존재하면... 
			// - "이적작업팝업" 화면에서 TO위치가 설비이면 6자리 이상, 야드이면 4자리 이상 선택됨
			if( !"".equals(ydToLocGuide) && ydToLocGuide.length() >= 6 ) {

				String sToSpanCol = ydToLocGuide.substring(2, 6);	//   To-SPANCOL

				//소형 정렬대이적 (#11)
				if( constant.EQP_D_GDTF11.substring(2).equals(sToSpanCol) ) {
					ydSchCd = constant.SCH_CD_GDTF11MM;
				//소형 정렬대이적 (#12)
				} else if( constant.EQP_D_GDTF12.substring(2).equals(sToSpanCol) ) {
					ydSchCd = constant.SCH_CD_GDTF12MM;
				//소형 정렬대이적 (#21)
				} else if( constant.EQP_D_GDTF21.substring(2).equals(sToSpanCol) ) {
					ydSchCd = constant.SCH_CD_GDTF21MM;
				//소형 정렬대이적 (#22)
				} else if( constant.EQP_D_GDTF22.substring(2).equals(sToSpanCol) ) {
					ydSchCd = constant.SCH_CD_GDTF22MM;
					
				//소형 이송출고 (#1)
				} else if( constant.EQP_D_GDTR11.substring(2).equals(sToSpanCol) ) {
					ydSchCd = constant.SCH_CD_GDTR11UM;
				//소형 이송출고 (#2)
				} else if( constant.EQP_D_GDTR21.substring(2).equals(sToSpanCol) ) {
					ydSchCd = constant.SCH_CD_GDTR21UM;

				//소형 대차상차 (#4)
				} else if( constant.EQP_D_GDTC04.substring(2).equals(sToSpanCol) ) {
					ydSchCd = constant.SCH_CD_GDTC04UM;
				//소형 대차상차 (#5)
				} else if( constant.EQP_D_GDTC05.substring(2).equals(sToSpanCol) ) {
					ydSchCd = constant.SCH_CD_GDTC05UM;
				//소형 대차상차 (#6)
				} else if( constant.EQP_D_GDTC06.substring(2).equals(sToSpanCol) ) {
					ydSchCd = constant.SCH_CD_GDTC06UM;
				}
			}

			//스케줄코드 미확정이면...
			if( "".equals(ydSchCd) ) {
				//소형 동내이적
				ydSchCd = constant.SCH_CD_GDYD01MM;
			}
			
			commUtils.printLog(logId, " > 스케줄코드설정::ydSchCd ("+ydSchCd+")", "SL");
			sbImpPros.append(" > 스케줄코드설정::ydSchCd ("+ydSchCd+") \r\n");
			sbImpPros.append("[작업예약등록 > 스케줄코드설정]::종료 \r\n \r\n");
			
						
			/**********************************************************
			* 3. 작업예약 등록
			**********************************************************/
			sbImpPros.append("[작업예약등록 > 재료번호로조회]::시작 \r\n");
			
			//3.1. 작업예약 대상재료 조회
			if( constant.SCH_CD_GDPC11LM.equals(ydSchCd) || constant.SCH_CD_GDPC21LM.equals(ydSchCd) 		//추출대입고 (#1, #2)
				|| constant.SCH_CD_GDTR11LM.equals(ydSchCd) || constant.SCH_CD_GDTR21LM.equals(ydSchCd)	) {	//  이송입고 (#1, #2)
				jrParam.setField("ASC_DESC", "ASC");	//작업예약 생성시 BED 정순
			} else {
				jrParam.setField("ASC_DESC", "DESC");	//작업예약 생성시 BED 역순
			}
			
			JDTORecordSet jsWbMtl = commDao.select(jrParam, getMvStkWrkBookMtlPp, logId, methodNm, "재료번호로 조회");

			int rowCnt = jsWbMtl.size();
			if( rowCnt <= 0 ) {
				throw new Exception("이적 재료 정보가 없습니다.");
			}

			sbImpPros.append("[작업예약등록 > 재료번호로조회]::종료 \r\n \r\n");
			
			
			//3.2. 작업예약등록
			jrParam.setField("YD_SCH_CD"       , ydSchCd      );	//야드스케줄코드
			jrParam.setField("YD_AIM_BAY_GP"   , ydAimBayGp   );	//야드목표동구분
			jrParam.setField("YD_TO_LOC_GUIDE" , ydToLocGuide );	//야드To위치Guide
			jrParam.setField("YD_WRK_CRN"	   , ydWrkCrn     );	//야드작업크레인(작업자지정 크레인)
			jrParam.setField("YD_PREP_SCH_ID"  , ydPrepSchId  );	//야드준비스케쥴ID(차량상차작업예약ID)

			sbImpPros.append("[작업예약등록 > 작업예약등록]::시작 \r\n");
			
			jsMsg.addRecord(this.insMvstkWrkBook(jrParam, jsWbMtl, sbImpPros));
			
			
			/**********************************************************
			* 4. 크레인별 첫번째 스케줄 전송
			**********************************************************/
			
			String sRuleItem = ysComm.getYsRuleItem(logId, methodNm, "APPSBR", "001");
			if (sRuleItem.equals("Y")) {
				jrRtn = commUtils.addSndData(jrRtn, this.setCrnSchMsg(jsMsg, logId, methodNm));
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
	 *      [A] 오퍼레이션명 : 작업예약등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord insMvstkWrkBook(JDTORecord jrParam, JDTORecordSet jsWbMtl, StringBuffer sbImpPros) throws DAOException {
		String methodNm = "작업예약등록[SbrYsJspSeEJB.insMvstkWrkBook] < " + jrParam.getResultMsg();
		String logId    = jrParam.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			String ydSchCd       = commUtils.trim(jrParam.getFieldString("YD_SCH_CD"       ));	//야드스케줄코드
			String ydAimBayGp    = commUtils.trim(jrParam.getFieldString("YD_AIM_BAY_GP"   ));	//야드목표동구분
			String ydToLocGuide  = commUtils.trim(jrParam.getFieldString("YD_TO_LOC_GUIDE" ));	//야드To위치Guide
			String modifier      = commUtils.trim(jrParam.getFieldString("MODIFIER"        ));	//수정자
			String ydWrkCrn      = commUtils.trim(jrParam.getFieldString("YD_WRK_CRN"      ));	//야드작업크레인(작업자지정 크레인)
			String ydPrepSchId   = commUtils.trim(jrParam.getFieldString("YD_PREP_SCH_ID"  ));	//야드준비스케쥴ID(차량상차작업예약ID)
			
			
			/**********************************************************
			* 1. 스케줄코드, 크레인 체크
			**********************************************************/
			sbImpPros.append("	[작업예약등록 > 작업예약등록 > 스케줄코드,크레인체크]::시작 \r\n");
			
			JDTORecord jrCrnSpec = sbrYsComm.chkSchCdEqp(jrParam);
			
			String ydGp           = ydSchCd.substring(0, 1);									//야드구분
			String ydBayGp        = ydSchCd.substring(1, 2);									//야드동구분
			String ydEqpId        = commUtils.trim(jrCrnSpec.getFieldString("YD_EQP_ID"   ));	//야드설비ID(크레인): 사용가능여부, 우선순위 고려한 크레인 결정
			String ydSchPrior     = commUtils.trim(jrCrnSpec.getFieldString("YD_SCH_PRIOR"));	//야드스케줄우선순위
			String ydToLocDcsnMtd = "S";														//야드TO위치결정방법(S:스케줄지정, F:지정위치)
			
			//지정된 작업크레인이 있으면...
			if(!"".equals(ydWrkCrn)){
				ydEqpId = ydWrkCrn;
			}
			
			// "야드목적동구분" 체크
			if( "".equals(ydAimBayGp) ) {
				ydAimBayGp = ydBayGp;
			}
			
			// "야드TO위치결정방법" 체크
			if( !"".equals(ydToLocGuide) ) {
				ydToLocDcsnMtd = "F";
			}
			
			commUtils.printLog(logId, " > 작업크레인::야드작업크레인(작업자지정 크레인) (ydWrkCrn:"+ydWrkCrn+"), 사용가능한 크레인 (ydEqpId:"+ydEqpId+")", "SL");
			sbImpPros.append("	[작업예약등록 > 작업예약등록 > 스케줄코드,크레인체크]::종료 \r\n \r\n");
			
			
			/**********************************************************
			* 2. 크레인사양 분리
			**********************************************************/
			sbImpPros.append("	[작업예약등록 > 작업예약등록 > 크레인사양분리]::시작 \r\n");
			
			jrCrnSpec.setResultCode(logId);		//Log ID
			jrCrnSpec.setResultMsg(methodNm);	//Log Method Name
			
			Vector vcLot = this.setCrnSpecSpr(jrCrnSpec, jsWbMtl);
			
			sbImpPros.append("	> 크레인사양분리 작업예약수::["+vcLot.size()+"] \r\n");
			sbImpPros.append("	[작업예약등록 > 작업예약등록 > 크레인사양분리]::종료 \r\n \r\n");
			
			/**********************************************************
			* 3. 작업예약 등록
			**********************************************************/
			JDTORecordSet jsLotMtl        = null;
			JDTORecord    jrRow           = null;
			
			int           lotCnt          = vcLot.size();	//크레인사양분리 작업예약수
			int           lotMtlSh        = 0;				//작업예약재료매수
			String        ydWbookId       = "";				//야드작업예약ID
			String        ydWbookIdFst    = "";				//야드작업예약ID(첫번째)

    		StringBuffer  sbImpPros_ToLoc = null;
    		
    		commUtils.printLog(logId, " > 크레인사양분리 작업예약수::(lotCnt:"+lotCnt+")", "SL");
			
			for( int ii = 0; ii < lotCnt; ii++ ) {
				
				jsLotMtl = (JDTORecordSet)vcLot.get(ii);	//Lot: 작업예약별 재료묶음
				lotMtlSh = jsLotMtl.size();					//작업재료 개수
				if( lotMtlSh <= 0 ) {
					continue;
				}
				
        		sbImpPros_ToLoc = new StringBuffer();
        		sbImpPros_ToLoc.append("	[작업예약등록 > 작업예약등록 > 작업예약,재료](순번:"+ii+")::시작 \r\n");
				
        		
				//3.1. 작업예약ID 조회
				ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
				if( "".equals(ydWbookId) ) {
					throw new Exception("작업예약ID 생성 실패");
				}
				
        		sbImpPros_ToLoc.append("	 > 작업예약ID조회  ::YD_WBOOK_ID ("+ydWbookId+") \r\n");
				
				//크레인스케줄 기동용 첫번째 작업예약번호 저장
				if( ii == 0 ) {
					ydWbookIdFst = ydWbookId;
				}
				
				//3.2. 작업예약 등록
				jrParam.setField("YD_WBOOK_ID"       , ydWbookId     );	//야드작업예약ID
				jrParam.setField("MODIFIER"          , modifier      ); //수정자
				jrParam.setField("YD_GP"             , ydGp          ); //야드구분
				jrParam.setField("YD_BAY_GP"         , ydBayGp       ); //야드동구분
				jrParam.setField("YD_SCH_CD"         , ydSchCd       ); //야드스케줄코드
				jrParam.setField("YD_SCH_PRIOR"      , ydSchPrior    ); //야드스케줄우선순위
				jrParam.setField("YD_SCH_PROG_STAT"  , "W"           ); //야드스케줄진행상태(W:스케줄수행대기)
				jrParam.setField("YD_SCH_ST_GP"      , "M"           ); //야드스케줄기동구분(M:Manual)
				jrParam.setField("YD_SCH_REQ_GP"     , "M"           ); //야드스케줄요청구분(M:이적)
				jrParam.setField("YD_AIM_YD_GP"      , ydGp          ); //야드목표야드구분
				jrParam.setField("YD_AIM_BAY_GP"     , ydAimBayGp    ); //야드목표동구분
				jrParam.setField("YD_TO_LOC_DCSN_MTD", ydToLocDcsnMtd); //야드TO위치결정방법
				jrParam.setField("YD_TO_LOC_GUIDE"   , ydToLocGuide  ); //야드To위치Guide
				jrParam.setField("YD_WRK_PLAN_TCAR"  , ydSchCd.substring(2, 4).equals("TC") ? ydSchCd.substring(0, 6) : "" ); //야드작업계획대차
				jrParam.setField("YD_WRK_PLAN_CRN"   , ydWrkCrn      ); //야드작업계획크레인
				jrParam.setField("CAR_YD_WBOOK_ID"   , ydPrepSchId   ); //야드준비스케쥴ID(차량상차작업예약ID)
				
				commDao.insert(jrParam, insWrkBook_sbr, logId, methodNm, "TB_YS_WRKBOOK");

				sbImpPros_ToLoc.append("	 > 작업예약등록    ::YD_WBOOK_ID ("+ydWbookId+"), YD_SCH_CD ("+ydSchCd+"), YD_SCH_PRIOR ("+ydSchPrior+"), YD_TO_LOC_GUIDE ("+ydToLocGuide+"), YD_WRK_PLAN_CRN ("+ydWrkCrn+") \r\n");
				
				//3.3. 작업예약재료 등록
				String sSSTL_NO = "";
				for( int jj = 0; jj < lotMtlSh; jj++ ) {
					
					jrRow = jsLotMtl.getRecord(jj);	//작업재료
					
					JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();
					jrRtn1.setField("YD_WBOOK_ID"   , ydWbookId                                            );	//야드작업예약ID
					jrRtn1.setField("SSTL_NO"      	, commUtils.trim(jrRow.getFieldString("SSTL_NO"      )));	//재료번호
					jrRtn1.setField("YS_STK_COL_GP" , commUtils.trim(jrRow.getFieldString("YS_STK_COL_GP")));	//야드적치열구분
					jrRtn1.setField("YS_STK_BED_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_BED_NO")));	//야드적치Bed번호
					jrRtn1.setField("YS_STK_LYR_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_LYR_NO")));	//야드적치단번호
					jrRtn1.setField("MODIFIER"     	, modifier                                             );	//등록자
					
					commDao.insert(jrRtn1, insWrkBookMtl_sbr, logId, methodNm, "TB_YS_WRKBOOKMTL");

					sSSTL_NO += commUtils.trim(jrRow.getFieldString("SSTL_NO")) + " ";
				}

				sbImpPros_ToLoc.append("	 > 작업예약재료등록::YD_WBOOK_ID ("+ydWbookId+"), SSTL_NO ("+sSSTL_NO+") \r\n");
				sbImpPros_ToLoc.append("	[작업예약등록 > 작업예약등록 > 작업예약,재료](순번:"+ii+")::종료 \r\n");
				sbImpPros_ToLoc.append("[작업예약등록 > 작업예약등록]::종료 \r\n");
				
				//주요진행내용로그: 단계별항목정보 ([logId],[재료번호],[적치열],[적치BED],[적치단],[적치SEQ],[야드To위치Guide],[야드스케줄코드],[야드작업계획크레인])
				String sSCH_CONTENTS = logId+","+sSSTL_NO+","+commUtils.trim(jrParam.getFieldString("YS_STK_COL_GP"))+",,,,"+ydToLocGuide+","+ydSchCd+","+ydWrkCrn;	
				String sParamVal = "WB"+"#"+"G"+"#"+"D"+"#"+ydWbookId+"#"+" "+"#"+"2"+"#"+sbImpPros.toString()+sbImpPros_ToLoc.toString()+"\r\n"+"#"+sSCH_CONTENTS;
				JDTORecord   jrSchlog = JDTORecordFactory.getInstance().create();
						     jrSchlog.setField("PARAM_VALUE", sParamVal);
				EJBConnector SchLogConn = new EJBConnector("default", "SbrYsSchSeEJB", this);
							 SchLogConn.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrSchlog });
			}
			
			/**********************************************************
			* 4. 크레인스케줄(YSYSJ702) 전송용 기초 전문 생성
			**********************************************************/
			//크레인스케줄 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);

			jrYdMsg.setField("YD_WBOOK_ID"     , ydWbookIdFst ); //야드작업예약ID(첫번째꺼만)
			jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케줄코드
			jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			jrYdMsg.setField("YD_SCH_ST_GP"    , "M"          ); //야드스케줄기동구분(M:Manual)
			jrYdMsg.setField("YD_SCH_REQ_GP"   , "M"          ); //야드스케줄요청구분(M:이적)
			
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrYdMsg;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
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
		String methodNm = "크레인스케줄전문정리[SbrYsJspSeEJB.setCrnSchMsg] < " + mthdNm;

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			if( !commUtils.isEmpty(jsMsg) ) {
				
				String  ydEqpId   = "";		//야드설비ID(크레인)
				String  ydEqpId2  = "";		//야드설비ID(크레인)
				String  ydEqpStat = "";		//야드설비상태
				boolean fstYn     = false;	//동일크레인에서 첫번째 여부
				
				//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord    jrParam = commUtils.getParam(logId, methodNm, "");
				JDTORecord    jrRow   = null;
				JDTORecordSet jsChk   = null;

				int rowCnt = jsMsg.size();
				
				for( int ii = rowCnt - 1; ii >= 0; ii-- ) {
					
					jrRow = jsMsg.getRecord(ii);
					jrRow.setResultCode(logId);		//Log ID
					jrRow.setResultMsg(methodNm);	//Log Method Name	
					
					fstYn   = true;
					ydEqpId = commUtils.trim(jrRow.getFieldString("YD_EQP_ID"));
					
					for( int jj = 0; jj < ii; jj++ ) {
						
						ydEqpId2 = jsMsg.getRecord(jj).getFieldString("YD_EQP_ID");
						
						commUtils.printLog(logId, "ydEqpId ["+ ydEqpId +"] == ["+ ydEqpId2 +"]", "SL");
						
						if( ydEqpId.equals(ydEqpId2) ) {
							fstYn = false;
							break;
						}
					}
					
					commUtils.printLog(logId, "fstYn ["+ fstYn +"]", "SL");
					
					//동일크레인에서 첫번째 이면
					if( fstYn ) {
						
						//크레인 상태 확인
						jrParam.setField("YD_EQP_ID", ydEqpId); //야드설비ID

						jsChk = commDao.select(jrParam, getStatEqp, logId, methodNm, "설비상태조회");
						
						ydEqpStat = "";

						if( jsChk.size() > 0 ) {
							ydEqpStat = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT"));
						}

						//크레인이 작업대기 상태이면 크레인스케줄 전송
						if( "W".equals(ydEqpStat) ) {	//W: 대기(wait)
							jrRtn = commUtils.addSndData(jrRtn, sbrYsComm.getCrnSchMsg(jrRow));
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
	 *      [A] 오퍼레이션명 : 크레인사양분리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrCrnSpec
	 *      @param JDTORecordSet jsWrkMtl
	 *      @return Vector
	 *      @throws DAOException
	*/
	public Vector setCrnSpecSpr(JDTORecord jrCrnSpec, JDTORecordSet jsWrkMtl) throws DAOException {
		String methodNm = "크레인사양분리[SbrYsJspSeEJB.setCrnSpecSpr] < " + jrCrnSpec.getResultMsg();
		String logId = jrCrnSpec.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			Vector        vcLot = new Vector();											//크레인사양분리결과
			JDTORecord    jrRow = null;													//현재 Row
			JDTORecordSet jsLot = JDTORecordFactory.getInstance().createRecordSet("");	//Lot: 크레인사양 (작업예약별 작업재료묶음)
			
			String szYS_STK_COL_GP     = "";	//현재 열
			String szYS_STK_BED_NO     = "";	//현재 BED
			String szYS_STK_LYR_NO     = "";	//현재 단
			String szHEAT_NO           = "";	//현재 Heat No
			String szYS_STK_COL_GP_BEF = "";	//이전 열
			String szYS_STK_BED_NO_BEF = "";	//이전 BED
			String szYS_STK_LYR_NO_BEF = "";	//이전 단
			String szHEAT_NO_BEF       = "";	//이전 Heat No
			
			int    rowCnt              = jsWrkMtl.size();	//소재 개수
			int    iYD_MTL_WT          = 0;		//소재 중량 (Kg)
			int    iYD_MTL_WT_SUM      = 0;		//소재 중량 합계 (Kg)
			
			
			//크레인사양분리 기준: [동일 "열/BED/단"에 있는 작업재료 묶음] + [작업소재 중량합계를 10톤 이하로 제한] + [Heat_No가 다를 경우 분리]
			for( int ii = 0; ii < rowCnt; ii++ ) {
				
				jrRow = jsWrkMtl.getRecord(ii);
				
				szYS_STK_COL_GP = commUtils.trim(jrRow.getFieldString("YS_STK_COL_GP"));
				szYS_STK_BED_NO = commUtils.trim(jrRow.getFieldString("YS_STK_BED_NO"));
				szYS_STK_LYR_NO = commUtils.trim(jrRow.getFieldString("YS_STK_LYR_NO"));
				szHEAT_NO       = commUtils.trim(jrRow.getFieldString("HEAT_NO"));
				iYD_MTL_WT      = Integer.parseInt(commUtils.nvl(jrRow.getFieldString("YD_MTL_WT"),"0"));	//소재 중량
				
				if( ii > 0 ) {
					
					String sLoc_BEF = szYS_STK_COL_GP_BEF + szYS_STK_BED_NO_BEF + szYS_STK_LYR_NO_BEF;
					String sLoc_CUR = szYS_STK_COL_GP     + szYS_STK_BED_NO     + szYS_STK_LYR_NO;
					
					iYD_MTL_WT_SUM = iYD_MTL_WT_SUM + iYD_MTL_WT;
					
					commUtils.printLog(logId, "[sLoc_BEF / sLoc_CUR] :: " + sLoc_BEF + " / " + sLoc_CUR, "");
					commUtils.printLog(logId, "[iYD_MTL_WT(중량) / iYD_MTL_WT_SUM(중량합계)] :: " + iYD_MTL_WT + " / " + iYD_MTL_WT_SUM, "");
					
					//(이전 위치정보 != 현재 위치정보) || (작업소재 중량합계를 10톤 이하로 제한) || (Heat_No가 다를 경우 분리)
					if( !(sLoc_BEF).equals(sLoc_CUR) || iYD_MTL_WT_SUM > 10000 || !(szHEAT_NO_BEF).equals(szHEAT_NO) ) {
						
						// "크레인사양분리결과"에 이전 Lot 추가
						vcLot.add(jsLot);
						
						// 신규 Lot 생성 & 작업재료 추가
						jsLot = JDTORecordFactory.getInstance().createRecordSet("");
						
						szYS_STK_COL_GP_BEF  = szYS_STK_COL_GP;
						szYS_STK_BED_NO_BEF  = szYS_STK_BED_NO;
						szYS_STK_LYR_NO_BEF  = szYS_STK_LYR_NO;
						szHEAT_NO_BEF        = szHEAT_NO;

						iYD_MTL_WT_SUM       = iYD_MTL_WT;
					}
					
				} else {
					szYS_STK_COL_GP_BEF  = szYS_STK_COL_GP;
					szYS_STK_BED_NO_BEF  = szYS_STK_BED_NO;
					szYS_STK_LYR_NO_BEF  = szYS_STK_LYR_NO;
					szHEAT_NO_BEF        = szHEAT_NO;

					iYD_MTL_WT_SUM       = iYD_MTL_WT;
				}
				
				//만들어진 Lot에 작업재료 추가 
				jsLot.addRecord(jrRow);
			}
			
			//마지막 Lot 추가
			vcLot.add(jsLot);
			
			commUtils.printLog(logId, methodNm, "S-");

			return vcLot;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : Flex에 쿼리를 실행시키기 위한 메소드
	 * 
	 * @ejb.interface-method
	 */
	public List getListWithFlex(HashMap paramMap) throws DAOException {
		String methodNm = "조회[SbrYsJspSeEJB.getListWithFlex(HashMap)]";
		String logId = (paramMap.get("logId") != null) ? paramMap.get("logId").toString() : "";

		try {
			commUtils.printLog(logId, methodNm, "S+", commUtils.hashMapToGridData(paramMap));

			List rtnList = CmnUtil.listJdtoRecordTohashMap(new SbrYsDAO().getListWithFlex(paramMap).toList());
			
			commUtils.printLog(logId, methodNm, "S-", commUtils.hashMapToGridData(paramMap));

			return rtnList;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	/**
	 * [A] 오퍼레이션명 : Flex에 멀티쿼리를 실행시키기 위한 메소드
	 * @ejb.interface-method
	*/
	public List getMultiListWithFlex(HashMap paramMap) throws DAOException {
		String methodNm = "조회[SbrYsJspSeEJB.getMultiListWithFlex(HashMap)]";
		String logId = (paramMap.get("logId") != null) ? paramMap.get("logId").toString() : "";
		
		try {
			commUtils.printLog(logId, methodNm, "S+", commUtils.hashMapToGridData(paramMap));
			
			List rtnList = new SbrYsDAO().getMultiListWithFlex(paramMap);
			
			commUtils.printLog(logId, methodNm, "S-", commUtils.hashMapToGridData(paramMap));
			return rtnList ;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}
	}

	/**
	 * 스케줄기준관리 - 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updSchRule(GridData gdReq) throws DAOException {
		String methodNm = "스케줄기준관리 - 수정[SbrYsJspSeEJB.updSchRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			commUtils.printLog(logId, "rowCnt:" + rowCnt, "");
			commUtils.printLog(logId, "YD_SCH_GP:" + gdReq.getParam("YD_SCH_GP"), "");

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					// 스케줄 수정
					jrParam.setField("YD_CRN1", commUtils.getValue(gdReq, "YD_CRN1", ii));
					jrParam.setField("M_CRN_PRIOR1", commUtils.getValue(gdReq, "M_CRN_PRIOR1", ii));
					jrParam.setField("YD_CRN2", commUtils.getValue(gdReq, "YD_CRN2", ii));
					jrParam.setField("M_CRN_PRIOR2", commUtils.getValue(gdReq, "M_CRN_PRIOR2", ii));
					jrParam.setField("USERID", commUtils.trim(gdReq.getParam("userid")));
					jrParam.setField("YD_SCH_CD", commUtils.getValue(gdReq, "YD_SCH_CD", ii));

					commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updYdSchRuleLn", logId, methodNm, "스케줄기준 수정");
				}
			}

			if ("CR".equals(gdReq.getParam("YD_SCH_GP"))) {
				for (int ii = 0; ii < rowCnt; ii++) {
					if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
						// 스케줄금지여부 수정
						jrParam.setField("YD_SCH_PROH_EXN", commUtils.getValue(gdReq, "YD_SCH_PROH_EXN", ii));
						jrParam.setField("YD_SCH_CD", commUtils.getValue(gdReq, "YD_SCH_CD", ii));

						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdSchProhExn", logId, methodNm, "스케줄금지여부수정");
					}
				}
			}

			if (!"CR".equals(gdReq.getParam("YD_SCH_GP"))) {

				String msgId;

				for (int ii = 0; ii < rowCnt; ii++) {

					if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
						// S-Crane 작업순위 변경 전송
						if ("A".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
							msgId = "YSN6L005";
						} else if ("D".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
							msgId = "YSN5L005";
						} else {
							msgId = "";
						}

						jrParam.setField("YD_GP", commUtils.trim(gdReq.getParam("YD_GP")));
						jrParam.setField("YD_BAY_GP", commUtils.trim(gdReq.getParam("YD_BAY_GP")));
						jrParam.setField("YD_EQP_ID", commUtils.trim(gdReq.getParam("CRN_NO")));
						jrParam.setField("YD_SCH_PRIOR", commUtils.getValue(gdReq, "M_CRN_PRIOR1", ii));
						jrParam.setField("YD_SCH_CD", commUtils.getValue(gdReq, "YD_SCH_CD", ii));

						// 전송Data 조회
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2(msgId, jrParam));
					}
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updSchRule

	/**
	 * 스케줄기준관리 - 선택복구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord resetSchRule(GridData gdReq) throws DAOException {
		String methodNm = "스케줄기준관리 - 선택복구[SbrYsJspSeEJB.resetSchRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					// 스케줄기준 수정
					jrParam.setField("R_CRN_PRIOR1", commUtils.getValue(gdReq, "R_CRN_PRIOR1", ii));
					jrParam.setField("R_CRN_PRIOR2", commUtils.getValue(gdReq, "R_CRN_PRIOR2", ii));
					jrParam.setField("YD_SCH_CD", commUtils.getValue(gdReq, "YD_SCH_CD", ii));
					jrParam.setField("YD_SCH_GP", commUtils.trim(gdReq.getParam("YD_SCH_GP")));
					jrParam.setField("YD_CRN_STAT1", commUtils.getValue(gdReq, "YD_CRN_STAT1", ii));
					jrParam.setField("YD_CRN_STAT2", commUtils.getValue(gdReq, "YD_CRN_STAT2", ii));

					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.resetSchRule", logId, methodNm, "스케줄기준 선택복구");
				}
			}

			if (!"CR".equals(gdReq.getParam("YD_SCH_GP"))) {
				String msgId;

				for (int ii = 0; ii < rowCnt; ii++) {
					if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
						// S-Crane 작업순위 변경 전송
						if ("A".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
							msgId = "YSN6L005";
						} else if ("D".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
							msgId = "YSN5L005";
						} else {
							msgId = "";
						}

						jrParam.setField("YD_GP", commUtils.trim(gdReq.getParam("YD_GP")));
						jrParam.setField("YD_BAY_GP", commUtils.trim(gdReq.getParam("YD_BAY_GP")));
						jrParam.setField("YD_EQP_ID", commUtils.trim(gdReq.getParam("CRN_NO")));
						jrParam.setField("YD_SCH_PRIOR", commUtils.getValue(gdReq, "M_CRN_PRIOR1", ii));
						jrParam.setField("YD_SCH_CD", commUtils.getValue(gdReq, "YD_SCH_CD", ii));

						// 전송Data 조회
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2(msgId, jrParam));
					}
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of resetSchRule

	/**
	 * 스케줄기준관리 - 전체복구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord resetAllSchRule(GridData gdReq) throws DAOException {
		String methodNm = "스케줄기준관리 - 전체복구[SbrYsJspSeEJB.resetAllSchRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name

			// 스케줄기준 수정
			jrParam.setField("USERID", commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("YD_GP", commUtils.trim(gdReq.getParam("YD_GP")));
			jrParam.setField("YD_BAY_GP", commUtils.trim(gdReq.getParam("YD_BAY_GP")));

			commDao.update(jrParam, resetAllSchRuleLn, logId, methodNm, "스케줄기준 전체복구");

			if (!"CR".equals(gdReq.getParam("YD_SCH_GP"))) {

				String msgId;

				// S-Crane 작업순위 변경 전송
				if ("A".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
					msgId = "YSN6L005All";
				} else if ("D".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
					msgId = "YSN5L005All";
				} else {
					msgId = "";
				}

				jrParam.setField("YD_GP", commUtils.trim(gdReq.getParam("YD_GP")));
				jrParam.setField("YD_BAY_GP", commUtils.trim(gdReq.getParam("YD_BAY_GP")));

				// 전송Data 조회
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2(msgId, jrParam));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of resetAllSchRule
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업예약관리 - 스케줄기동
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procCrnWrkBookStart(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업예약관리 - 스케줄기동[SbrYsJspSeEJB.procCrnWrkBookStart] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			JDTORecord jrRtn1 = null;
			
			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name
			jrParam.setField("YD_GP", commUtils.trim(gdReq.getParam("YD_GP"))); // 야드구분
			jrParam.setField("YD_SCH_ST_GP", "M"); // 야드스케쥴기동구분(Manual)
			jrParam.setField("YD_SCH_REQ_GP", "W"); // 야드스케쥴요청구분(작업예약조회화면)
			
			// 선택한 작업예약
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					
					//1.스케줄코드 확인
					String sYD_SCH_CD = gdReq.getHeader("YD_SCH_CD").getValue(ii).substring(0,4);
					String sYD_SCH_CD2 = gdReq.getHeader("YD_SCH_CD").getValue(ii).substring(0,6);
					
					if(sYD_SCH_CD.equals("GDTC") ) {
						//1.대차위치 확인
						JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
						String sYD_EQP_ID = gdReq.getHeader("YD_TO_LOC_GUIDE").getValue(ii);
						jrParam1.setField("YD_EQP_ID", commUtils.nvl(sYD_EQP_ID, sYD_SCH_CD2));
						JDTORecordSet jsCol1 = commDao.select(jrParam1, getStatEqp, logId, methodNm, "현재 대차정보 조회");
						
						if( jsCol1.size() > 0 ) {
							jsCol1.first();
							JDTORecord recInPara = jsCol1.getRecord();
							
							String sYD_CURR_BAY_GP = commUtils.trim(recInPara.getFieldString("YD_CURR_BAY_GP"));
							String sYD_EQP_STAT = commUtils.trim(recInPara.getFieldString("YD_EQP_STAT"));
							String sYD_EQP_WRK_MODE = commUtils.trim(recInPara.getFieldString("YD_EQP_WRK_MODE"));
							
							// 현재 대차 위치 확인
							if(sYD_CURR_BAY_GP.equals("A")) {
								if(sYD_EQP_STAT.equals("N") && sYD_EQP_WRK_MODE.equals("1") ) {
									// 크레인스케줄기동
									jrParam.setField("YD_WBOOK_ID", commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii))); // 야드작업예약ID
									jrParam.setField("YD_SCH_CD", commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii))); // 야드스케쥴코드
									jrParam.setField("YD_EQP_ID", commUtils.trim(gdReq.getHeader("YD_WRK_CRN").getValue(ii))); // 야드설비ID
									jrParam.setField("EJB_CALL_YN", "Y"); // EJBCall여부(신 크레인스케줄)
					
									// 크레인스케줄기동 전문
									jrRtn = commUtils.addSndData(jrRtn, sbrYsComm.getCrnSchMsg(jrParam));
								}
							} else {
								if(sYD_EQP_STAT.equals("N") && sYD_EQP_WRK_MODE.equals("1") ) {
									
									// 대차이동요청
									JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
									
									//대차이동요청(YSN7L312)
									jrParam2.setField("YD_EQP_ID", gdReq.getHeader("YD_SCH_CD").getValue(ii).substring(0,6));
									jrParam2.setField("TCAR_LOC", "A"); // 목표동
									jrParam2.setField("YD_EQP_WRK_STAT", "U"); // 적재상태
									jrParam2.setField("ST_LOC", "B"); // 출발동
									jrParam2.setField("END_LOC", "A"); // 도착동
									
									jrRtn1 = commUtils.addSndData(jrRtn1, commDao.getMsgL2("YSN7L312", jrParam2));
									
									if(jrRtn1 != null) {
										EJBConnector sndConn1 = new EJBConnector("default", "YsCommEJB", this);
										sndConn1.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn1 });
									}
									
									// 크레인스케줄기동
									jrParam.setField("YD_WBOOK_ID", commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii))); // 야드작업예약ID
									jrParam.setField("YD_SCH_CD", commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii))); // 야드스케쥴코드
									jrParam.setField("YD_EQP_ID", commUtils.trim(gdReq.getHeader("YD_WRK_CRN").getValue(ii))); // 야드설비ID
									jrParam.setField("EJB_CALL_YN", "Y"); // EJBCall여부(신 크레인스케줄)
					
									// 크레인스케줄기동 전문
									jrRtn = commUtils.addSndData(jrRtn, sbrYsComm.getCrnSchMsg(jrParam));
								}
							}
						}
					} else {						
						// 크레인스케줄기동
						jrParam.setField("YD_WBOOK_ID", commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii))); // 야드작업예약ID
						jrParam.setField("YD_SCH_CD", commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii))); // 야드스케쥴코드
						jrParam.setField("YD_EQP_ID", commUtils.trim(gdReq.getHeader("YD_WRK_CRN").getValue(ii))); // 야드설비ID
						jrParam.setField("EJB_CALL_YN", "Y"); // EJBCall여부(신 크레인스케줄)
		
						// 크레인스케줄기동 전문
						jrRtn = commUtils.addSndData(jrRtn, sbrYsComm.getCrnSchMsg(jrParam));
					}
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업예약관리 - 작업예약삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord delWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업예약관리 - 작업예약삭제[SbrYsJspSeEJB.delWrkBook] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			String ydWbookId   = ""; // 야드작업예약ID
			String ydEqpId     = ""; // 야드설비ID
			String ydSchCd     = ""; // 야드스케쥴코드
			String ydPrepSchId = ""; // 야드준비스케쥴ID(차량상차작업예약ID)
			String ydTcarSchId = ""; // 야드대차스케쥴ID(대차상차작업예약ID)

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name
			// 처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					ydWbookId   = commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii));
					ydEqpId     = commUtils.trim(gdReq.getHeader("YD_WRK_CRN").getValue(ii));
					ydSchCd     = commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii));
					ydPrepSchId = commUtils.trim(gdReq.getHeader("CAR_YD_WBOOK_ID").getValue(ii));
					ydTcarSchId = commUtils.trim(gdReq.getHeader("YD_TCAR_SCH_ID").getValue(ii));
	
					jrParam.setField("YD_WBOOK_ID"    , ydWbookId);
					jrParam.setField("YD_EQP_ID"      , ydEqpId);
					jrParam.setField("YD_SCH_CD"      , ydSchCd);
					jrParam.setField("CAR_YD_WBOOK_ID", ydPrepSchId);
					jrParam.setField("YD_TCAR_SCH_ID" , ydTcarSchId);
	
					/**********************************************************
					 * 2. 작업예약 취소
					 **********************************************************/
					jrRtn = commUtils.addSndData(jrRtn, this.trtWrkBookCncl(jrParam));
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
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
	public JDTORecord trtWrkBookCncl(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "작업예약 취소처리[SbrYsJspSeEJB.trtWrkBookCncl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			// String ydCrnSchId= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID" )); //야드설비ID
			String ydWbookId   = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID")); // 야드작업예약ID
			// String ydEqpId = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID" )); //야드설비ID
			// String ydSchCd = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD" )); //야드스케쥴코드
			String ydPrepSchId = commUtils.trim(rcvMsg.getFieldString("CAR_YD_WBOOK_ID")); // 야드준비스케쥴ID(차량상차작업예약ID)
			String modifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); // 수정자
			String ydTcarSchId = commUtils.trim(rcvMsg.getFieldString("YD_TCAR_SCH_ID")); // 야드대차스케줄ID

			if ("".equals(ydWbookId)) {
				throw new Exception("작업예약ID가 없습니다.");
			}

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);

			jrParam.setField("YD_WBOOK_ID"    , ydWbookId);
			jrParam.setField("CAR_YD_WBOOK_ID", ydPrepSchId);

			/**********************************************************
			 * 1. 크레인스케줄 존재여부 Check
			 **********************************************************/

			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCommWbCrnSch", logId, methodNm, "크레인작업지시read");
			if (jsCrnSch != null && jsCrnSch.size() > 0) {
				throw new Exception("작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 " + jsCrnSch.size() + " 건 존재합니다.");
			}

			/**********************************************************
			 * 2. 준비스케줄 복원
			 **********************************************************/
			
			JDTORecord jrParam1 = commUtils.getParam(logId, methodNm, modifier);
			
			jrParam1.setField("CAR_YD_WBOOK_ID", ydPrepSchId);
			
			JDTORecordSet jsPrepSch = commDao.select(jrParam1, "com.inisteel.cim.ys.sbr.dao.getCommWrkBookSch", logId, methodNm, "작업예약에서 차량스케줄 조회");
			
			JDTORecord jrParam2 = commUtils.getParam(logId, methodNm, modifier);
			
			jrParam2.setField("MODIFIER", modifier);
			jrParam2.setField("CAR_YD_WBOOK_ID", ydPrepSchId);
			
			/*
			 * com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepSchRcvr --준비스케줄 복원 - UPDATE TB_YS_PREPSCH SET MODIFIER = :V_MODIFIER ,MOD_DDTT = SYSDATE ,DEL_YN = 'N' ,YD_WBOOK_ID = NULL WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			 */
			if(jsPrepSch.size() == 1) {
				commDao.update(jrParam2, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepSchRcvr", logId, methodNm, "TB_YS_PREPSCH");
			}
			
			/*
			 * com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepMtlRcvr --준비재료 복원 - UPDATE TB_YS_PREPMTL SET MODIFIER = :V_MODIFIER ,MOD_DDTT = SYSDATE ,DEL_YN = 'N' WHERE YD_PREP_SCH_ID IN (SELECT YD_PREP_SCH_ID FROM TB_YS_PREPSCH WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID)
			 */
			JDTORecord jrParam4 = commUtils.getParam(logId, methodNm, modifier);
			
			jrParam4.setField("YD_WBOOK_ID", ydWbookId);
			
			JDTORecordSet jsPrepSchMtl = commDao.select(jrParam4, "com.inisteel.cim.ys.sbr.dao.getCommWrkBookMtl", logId, methodNm, "차량스케줄 조회");
			
			for(int i = 0; i < jsPrepSchMtl.size(); i++) {
				JDTORecord jrParam5 = JDTORecordFactory.getInstance().create();
				
				JDTORecord jrPrepMtl = jsPrepSchMtl.getRecord(i);
				
				jrParam5.setField("MODIFIER", 		modifier);
				jrParam5.setField("SSTL_NO", 		jrPrepMtl.getFieldString("SSTL_NO"));
				jrParam5.setField("YS_STK_COL_GP", 	jrPrepMtl.getFieldString("YS_STK_COL_GP"));
				jrParam5.setField("YS_STK_BED_NO", 	jrPrepMtl.getFieldString("YS_STK_BED_NO"));
				jrParam5.setField("YS_STK_LYR_NO", 	jrPrepMtl.getFieldString("YS_STK_LYR_NO"));
				
				commDao.update(jrParam5, "com.inisteel.cim.ys.sbr.dao.updCommPrepMtlRcvr", logId, methodNm, "TB_YS_PREPMTL");
				
				// 소재이송지시 취소
				commDao.update(jrParam5, "com.inisteel.cim.ys.sbr.dao.updStlFrtonMoveStat", logId, methodNm, "소재이송지시 취소");
			}			

			/**********************************************************
			 * 3. 차량/대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			 **********************************************************/
			// 차량스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommCarSchWbDel", logId, methodNm, "TB_YS_CARSCH");

			// 대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommTcarSchWbDel", logId, methodNm, "TB_YS_TCARSCH");

			/**********************************************************
			 * 4. 작업예약/재료 삭제
			 **********************************************************/
			// 작업예약재료 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");

			// 작업예약 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
			
			/**********************************************************
			 * 5. 대차작업예약/재료 삭제
			 **********************************************************/
			
			if(!ydTcarSchId.equals("")) {
				JDTORecord jrParam3 = commUtils.getParam(logId, methodNm, modifier);
				
				String[] ydTcarSchId2 = ydTcarSchId.split(",");
				
				for(int i = 0; i < ydTcarSchId2.length; i++) {
					jrParam3.setField("MODIFIER", modifier);
					jrParam3.setField("YD_TCAR_SCH_ID", ydTcarSchId2[i]);
					
					// 대차작업예약재료 삭제
					commDao.update(jrParam3, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnTcarSch", logId, methodNm, "TB_YS_TCARSCH");

					// 대차작업예약 삭제
					commDao.update(jrParam3, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnTcarFtmvMtl", logId, methodNm, "TB_YS_TCARFTMVMTL");
				}
			}

			// /**********************************************************
			// * 5. 크레인작업지시요구 전문 조회
			// **********************************************************/
			// //크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
			// JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
			// jrYdMsg.setResultCode(logId); //Log ID
			// jrYdMsg.setResultMsg(methodNm); //Log Method Name
			// jrYdMsg.setField("JMS_TC_CD" , YsConstant.N2YSL004); //크레인작업지시요구
			// jrYdMsg.setField("YD_EQP_ID" , ydEqpId ); //야드설비ID
			// jrYdMsg.setField("YD_WRK_PROG_STAT", "4" ); //야드작업진행상태(권하완료)
			// jrYdMsg.setField("YD_SCH_CD" , ydSchCd ); //야드스케쥴코드
			// jrYdMsg.setField("YD_CRN_SCH_ID" , ydCrnSchId); //야드크레인스케쥴ID
			//
			// EJBConnector sndConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
			// jrRtn = (JDTORecord)sndConn.trx("rcvN2YSL004", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : 크레인 상태 설정변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updbtCrnStsSetPp(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 상태 설정변경[SbrYsJspSeEJB.updbtCrnStsSetPp] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name
			String trtDtlGp = commUtils.trim(gdReq.getParam("TRT_DTL_GP")); // 처리상세구분
			String currDate = commUtils.getDateTime14(); // 현재시각
			String ydEqpId = commUtils.trim(gdReq.getParam("W_YD_EQP_ID")); // 야드설비ID(크레인)

			if ("".equals(ydEqpId)) {
				throw new Exception("크레인설비ID가 없습니다.");
			}

			jrParam.setField("YD_EQP_ID", ydEqpId); // 야드설비ID
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					if ("ST".equals(trtDtlGp)) {
						// 설비상태 변경
						jrParam.setField("JMS_TC_CD", "N7YSL303"); // 설비고장복구실적
						jrParam.setField("YD_EQP_STAT", commUtils.trim(gdReq.getParam("YD_EQP_STAT"))); // 야드설비상태(B:고장, N:정상)
						jrParam.setField("YD_EQP_PAUSE_CODE", "0000"); // 야드설비휴지코드
						jrParam.setField("YD_EQP_TRBL_RCVR_DT", currDate); // 야드설비고장복구일시

						EJBConnector sndConn = new EJBConnector("default", "SbrYsL2RcvSeEJB", this);
						jrRtn = (JDTORecord) sndConn.trx("rcvN7YSL303", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					} else if ("MD".equals(trtDtlGp)) {
						// 작업Mode 변경
						jrParam.setField("JMS_TC_CD", "N7YSL003"); // 설비운전모드전환
						jrParam.setField("YD_EQP_WRK_MODE", commUtils.trim(gdReq.getParam("YD_EQP_WRK_MODE"))); // 야드설비작업Mode(1:On-Line, 0:Off-Line)

						EJBConnector sndConn = new EJBConnector("default", "SbrYsL2RcvSeEJB", this);
						jrRtn = (JDTORecord) sndConn.trx("rcvN7YSL303", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					} else if ("WO".equals(trtDtlGp)) {
						// 명령선택기동
						jrParam.setField("JMS_TC_CD", "N7YSL304"); // 크레인작업지시요구
						jrParam.setField("YD_WRK_PROG_STAT", "W"); // 야드작업진행상태(명령선택대기)
						jrParam.setField("YD_SCH_CD", commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii))); // 야드스케쥴코드
						jrParam.setField("YD_CRN_SCH_ID", commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID").getValue(ii))); // 야드크레인스케쥴ID

						EJBConnector sndConn = new EJBConnector("default", "SbrYsL2RcvSeEJB", this);
						jrRtn = (JDTORecord) sndConn.trx("rcvN7YSL304", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					} else if ("WU".equals(trtDtlGp)) {
						// 권상실적처리
						jrParam.setField("JMS_TC_CD", "N7YSL305"); // 크레인권상실적
						jrParam.setField("YD_EQP_WRK_MODE", "9"); // 야드설비작업Mode(Backup)
						jrParam.setField("YD_WRK_PROG_STAT", "2"); // 야드작업진행상태(권상완료)
						jrParam.setField("YD_SCH_CD", commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii))); // 야드스케쥴코드
						jrParam.setField("YD_CRN_SCH_ID", commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID").getValue(ii))); // 야드크레인스케쥴ID
						jrParam.setField("YS_UP_WR_LOC", commUtils.trim(gdReq.getHeader("YS_UP_WO_LOC").getValue(ii))); // 야드권상실적위치
						jrParam.setField("YS_UP_WR_LAYER", commUtils.trim(gdReq.getHeader("YS_UP_WO_LAYER").getValue(ii))); // 야드권상실적단
						jrParam.setField("YD_CRN_XAXIS", commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_XAXIS").getValue(ii))); // 야드크레인X축
						jrParam.setField("YD_CRN_YAXIS", commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_YAXIS").getValue(ii))); // 야드크레인Y축
						jrParam.setField("YD_CRN_ZAXIS", commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_ZAXIS").getValue(ii))); // 야드크레인Z축

						EJBConnector sndConn = new EJBConnector("default", "SbrYsL2RcvSeEJB", this);
						jrRtn = (JDTORecord) sndConn.trx("rcvN7YSL305", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					} else if ("WD".equals(trtDtlGp)) {
						// 권하실적처리
						jrParam.setField("JMS_TC_CD", "N7YSL306"); // 크레인권하실적
						jrParam.setField("YD_EQP_WRK_MODE", "9"); // 야드설비작업Mode(Backup)
						jrParam.setField("YD_WRK_PROG_STAT", "4"); // 야드작업진행상태(권하완료)
						jrParam.setField("YD_SCH_CD", commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii))); // 야드스케쥴코드
						jrParam.setField("YD_CRN_SCH_ID", commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID").getValue(ii))); // 야드크레인스케쥴ID
						jrParam.setField("YS_DN_WR_LOC", commUtils.trim(gdReq.getHeader("YS_DN_WO_LOC").getValue(ii))); // 야드권하실적위치
						jrParam.setField("YS_DN_WR_LAYER", commUtils.trim(gdReq.getHeader("YS_DN_WO_LAYER").getValue(ii))); // 야드권하실적단
						jrParam.setField("YD_CRN_XAXIS", commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_XAXIS").getValue(ii))); // 야드크레인X축
						jrParam.setField("YD_CRN_YAXIS", commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_YAXIS").getValue(ii))); // 야드크레인Y축
						jrParam.setField("YD_CRN_ZAXIS", commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_ZAXIS").getValue(ii))); // 야드크레인Z축

						EJBConnector sndConn = new EJBConnector("default", "SbrYsL2RcvSeEJB", this);
						jrRtn = (JDTORecord) sndConn.trx("rcvN7YSL306", new Class[] { JDTORecord.class }, new Object[] { jrParam });
						
						//UPDATE
//						String YD_SCH_CD = commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii));
//						
//						jrParam.setField("CAR_NO", commUtils.nvl(gdReq.getHeader("CAR_NO").getValue(ii), ""));
//						
//						if (YD_SCH_CD.substring(0,4).equals("GDTR")) {
//							if(YD_SCH_CD.substring(7,4).equals("UM")){
//								commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updStockCarNo", logId, methodNm, "저장품 CAR_NO 등록");
//							} else {
//								commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updStockCarNo", logId, methodNm, "저장품 CAR_NO 삭제");
//							}
//						}
					} else if ("DL".equals(trtDtlGp)) {
						// 권하위치변경
						jrParam.setField("YD_WRK_PROG_STAT", commUtils.trim(gdReq.getHeader("YD_WRK_PROG_STAT").getValue(ii))); // 야드작업진행상태
						jrParam.setField("YD_SCH_CD", commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii))); // 야드스케쥴코드
						jrParam.setField("YD_CRN_SCH_ID", commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID").getValue(ii))); // 야드크레인스케쥴ID
						jrParam.setField("YD_WBOOK_ID", commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii))); // 야드작업예약ID
						jrParam.setField("YS_DN_WO_LOC", commUtils.trim(gdReq.getParam("YS_DN_WO_LOC"))); // 야드권하지시위치(신규)

						jrRtn = this.updCrnSchDnWoLoc(jrParam);
					} else {
						throw new Exception("정의되지 않은 처리구분[" + trtDtlGp + "] 입니다.");
					}
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * [A] 오퍼레이션명 : 크레인스케줄 권하지시위치 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updCrnSchDnWoLoc(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인스케줄 권하지시위치 변경[SbrYsJspSeEJB.updCrnSchDnWoLoc] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydEqpId = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); // 야드설비ID(크레인)
			String ydSchCd = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD")); // 야드스케쥴코드
			String ydCrnSchId = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); // 야드크레인스케쥴ID
			String ydWbookId = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID")); // 야드작업예약ID
			String ysDnWoLoc = commUtils.trim(rcvMsg.getFieldString("YS_DN_WO_LOC")); // 야드권하지시위치(신규)
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); // 야드작업진행상태
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); // 수정자

			commUtils.printParam("rcvMsg", rcvMsg);

			if ("".equals(ydCrnSchId)) {
				throw new Exception("크레인스케쥴ID가 없습니다.");
			} else if ("".equals(ysDnWoLoc)) {
				throw new Exception("변경할 권하지시위치가 없습니다.");
			}

			// Return Value
			JDTORecord sndRecord = JDTORecordFactory.getInstance().create();

			String ysStkColGp = ysDnWoLoc.substring(0, 6); // 야드적치열구분

			String ysStkBedNo = ""; // 야드권하지시위치(기존)
			String ydDnWoLocOld = ""; // 야드권하지시위치(기존)
			String ydDnWoLayerOld = ""; // 야드권하지시위치(기존)
			String ydDnWoLayer = ""; // 야드권하지시단(신규)
			String ydDnWoLocXaxis = ""; // 야드권하지시X축(신규)
			String ydDnWoLocYaxis = ""; // 야드권하지시Y축(신규)
			String ydDnWoLocZaxis = ""; // 야드권하지시Z축(신규)

			JDTORecordSet jsCrnSch = JDTORecordFactory.getInstance().createRecordSet("Temp");

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);

			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
			jrParam.setField("YD_CARLD_WRK_BOOK_ID", ydWbookId); // 야드상차작업예약ID
			jrParam.setField("YS_STK_COL_GP", ysStkColGp);

			jrParam.setField("MODIFIER", modifier);

			if (ysDnWoLoc.length() == 6) {

				/**********************************************************
				 * 1. 신규 권하지시위치 Bed정보 조회
				 **********************************************************/
				/*
				 * com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchDnWoLocBt -- 베드 조회 SELECT * FROM ( SELECT A.YS_STK_COL_GP , A.YS_STK_BED_NO , B.YS_STK_LYR_NO , MIN(A.YD_STK_BED_XAXIS) AS YD_DN_WO_LOC_XAXIS , MIN(A.YD_STK_BED_YAXIS) AS YD_DN_WO_LOC_YAXIS , MIN(A.YD_STK_BED_ZAXIS) AS
				 * YD_DN_WO_LOC_ZAXIS , MIN(CM.YS_DN_WO_LOC) AS YD_DN_WO_LOC_OLD , MIN(CM.YS_DN_WO_LAYER) AS YD_DN_WO_LAYER_OLD , (SELECT CASE WHEN SUM(DECODE(YD_STK_LYR_MTL_STAT,'U',1,0)) > 0 THEN 'UP' --권상대기 있음 ELSE 'AAA' END FROM TB_YS_STKLYR WHERE YS_STK_COL_GP = A.YS_STK_COL_GP AND
				 * YS_STK_LYR_NO < B.YS_STK_LYR_NO ) AS DL_LOC_CHK_RST FROM TB_YS_STKBED A , TB_YS_STKLYR B , (SELECT CM.YS_DN_WO_LOC ,CM.YS_DN_WO_LAYER ,CM.YD_MTL_SH ,CM.YD_MTL_WT ,CM.YD_MTL_T
				 * 
				 * FROM (SELECT CS.YD_CRN_SCH_ID ,MIN(CS.YD_WBOOK_ID ) AS YD_WBOOK_ID ,MIN(CS.YS_DN_WO_LOC ) AS YS_DN_WO_LOC ,MIN(CS.YS_DN_WO_LAYER) AS YS_DN_WO_LAYER ,COUNT(*) AS YD_MTL_SH ,SUM(ST.YD_MTL_WT) AS YD_MTL_WT ,SUM(ST.YD_MTL_T ) AS YD_MTL_T FROM TB_YS_CRNSCH CS ,TB_YS_CRNWRKMTL CM
				 * ,TB_YS_STOCK ST WHERE CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID AND CM.YD_CRN_SCH_ID = CS.YD_CRN_SCH_ID AND CM.SSTL_NO = ST.SSTL_NO AND CM.DEL_YN = 'N' GROUP BY CS.YD_CRN_SCH_ID) CM) CM WHERE A.YS_STK_COL_GP = SUBSTR(:V_YS_STK_COL_GP, 1, 6) AND A.YS_STK_COL_GP = B.YS_STK_COL_GP AND
				 * A.YS_STK_BED_NO = B.YS_STK_BED_NO AND B.YS_STK_LYR_NO = NVL( (SELECT YS_STK_LYR_NO + DECODE(SUM_MTL_CNT,6,1,0) --블름은 6개bed FROM ( SELECT YS_STK_LYR_NO , SUM(CASE WHEN MTL_CNT > 0 THEN 1 ELSE 0 END ) AS SUM_MTL_CNT FROM ( SELECT YS_STK_COL_GP , YS_STK_BED_NO , YS_STK_LYR_NO ,
				 * COUNT(SSTL_NO) AS MTL_CNT FROM TB_YS_STKLYR C WHERE C.YS_STK_COL_GP = SUBSTR(:V_YS_STK_COL_GP, 1, 6) AND C.SSTL_NO IS NOT NULL AND C.YD_STK_LYR_ACT_STAT = 'E' GROUP BY YS_STK_COL_GP, YS_STK_BED_NO, YS_STK_LYR_NO ) C GROUP BY YS_STK_COL_GP, YS_STK_LYR_NO ORDER BY YS_STK_COL_GP,
				 * YS_STK_LYR_NO DESC ) WHERE ROWNUM =1 ),'01') AND A.YS_STK_COL_GP = SUBSTR(:V_YS_STK_COL_GP, 1, 6) AND A.DEL_YN = 'N' AND A.YD_STK_BED_ACT_STAT = 'L' AND B.YD_STK_LYR_ACT_STAT = 'E' AND B.SSTL_NO IS NULL GROUP BY A.YS_STK_COL_GP, A.YS_STK_BED_NO, B.YS_STK_LYR_NO ORDER BY
				 * YS_STK_BED_NO ) WHERE ROWNUM = 1
				 */
				jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchDnWoLocBt", logId, methodNm, "신규권하위치 조회");
			} else {
				ysStkBedNo = ysDnWoLoc.substring(6, 8); // 야드적치Bed번호
				jrParam.setField("YS_STK_BED_NO", ysStkBedNo);

				/**********************************************************
				 * 1. 신규 권하지시위치 Bed정보 조회
				 **********************************************************/
				/*
				 * com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchDnWoLocCurBed SELECT A.YS_STK_COL_GP , A.YS_STK_BED_NO , B.YS_STK_LYR_NO , MIN(A.YD_STK_BED_XAXIS) AS YD_DN_WO_LOC_XAXIS , MIN(A.YD_STK_BED_YAXIS) AS YD_DN_WO_LOC_YAXIS , MIN(A.YD_STK_BED_ZAXIS) AS YD_DN_WO_LOC_ZAXIS ,
				 * MIN(CM.YS_DN_WO_LOC) AS YD_DN_WO_LOC_OLD , MIN(CM.YS_DN_WO_LAYER) AS YD_DN_WO_LAYER_OLD , (SELECT CASE WHEN SUM(DECODE(YD_STK_LYR_MTL_STAT,'U',1,0)) > 0 THEN 'UP' --권상대기 있음 ELSE 'AAA' END FROM TB_YS_STKLYR WHERE YS_STK_COL_GP = A.YS_STK_COL_GP AND YS_STK_LYR_NO < B.YS_STK_LYR_NO )
				 * AS DL_LOC_CHK_RST FROM TB_YS_STKBED A , TB_YS_STKLYR B , (SELECT CM.YS_DN_WO_LOC ,CM.YS_DN_WO_LAYER ,CM.YD_MTL_SH ,CM.YD_MTL_WT ,CM.YD_MTL_T
				 * 
				 * FROM (SELECT CS.YD_CRN_SCH_ID ,MIN(CS.YD_WBOOK_ID ) AS YD_WBOOK_ID ,MIN(CS.YS_DN_WO_LOC ) AS YS_DN_WO_LOC ,MIN(CS.YS_DN_WO_LAYER) AS YS_DN_WO_LAYER ,COUNT(*) AS YD_MTL_SH ,SUM(ST.YD_MTL_WT) AS YD_MTL_WT ,SUM(ST.YD_MTL_T ) AS YD_MTL_T FROM TB_YS_CRNSCH CS ,TB_YS_CRNWRKMTL CM
				 * ,TB_YS_STOCK ST WHERE CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID AND CM.YD_CRN_SCH_ID = CS.YD_CRN_SCH_ID AND CM.SSTL_NO = ST.SSTL_NO AND CM.DEL_YN = 'N' GROUP BY CS.YD_CRN_SCH_ID) CM) CM WHERE A.YS_STK_COL_GP = SUBSTR(:V_YS_STK_COL_GP, 1, 6) AND A.YS_STK_COL_GP = B.YS_STK_COL_GP AND
				 * A.YS_STK_BED_NO = B.YS_STK_BED_NO AND B.YS_STK_LYR_NO = NVL( (SELECT MAX(YS_STK_LYR_NO) + 1 FROM TB_YS_STKLYR C WHERE C.YS_STK_COL_GP = A.YS_STK_COL_GP AND C.YS_STK_BED_NO = A.YS_STK_BED_NO AND C.SSTL_NO IS NOT NULL GROUP BY C.YS_STK_COL_GP, C.YS_STK_BED_NO ),'01') AND
				 * A.YS_STK_BED_NO = :V_YS_STK_BED_NO AND A.DEL_YN = 'N' AND A.YD_STK_BED_ACT_STAT = 'L' AND B.YD_STK_LYR_ACT_STAT = 'E' AND B.SSTL_NO IS NULL GROUP BY A.YS_STK_COL_GP,A.YS_STK_BED_NO,B.YS_STK_LYR_NO
				 * 
				 */
				jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchDnWoLocCurBed", logId, methodNm, "신규권하위치 조회");
			}

			if (jsCrnSch == null || jsCrnSch.size() <= 0) {
				throw new Exception("신규 권하지시위치[" + ysDnWoLoc + "] 정보가 없습니다.");
			} else {

				JDTORecord jrCrnSch = jsCrnSch.getRecord(0);

				ydDnWoLocOld = commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_OLD"));
				ydDnWoLayerOld = commUtils.trim(jrCrnSch.getFieldString("YS_DN_WO_LAYER_OLD"));
				ysStkBedNo = commUtils.trim(jrCrnSch.getFieldString("YS_STK_BED_NO"));
				ydDnWoLayer = commUtils.trim(jrCrnSch.getFieldString("YS_STK_LYR_NO"));
				ydDnWoLocXaxis = commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_XAXIS"));
				ydDnWoLocYaxis = commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_YAXIS"));
				ydDnWoLocZaxis = commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_ZAXIS"));
				String dlLocChkRst = commUtils.trim(jrCrnSch.getFieldString("DL_LOC_CHK_RST"));

				if ("UP".equals(dlLocChkRst)) {
					throw new Exception("권상/권하대기(U) 재료가 적치되어 있습니다.");
				}

				// 혹시 권하지시위치가 잘못 등록되어 있으면
				if (ydDnWoLocOld.length() != 8) {
					ydDnWoLocOld = "XX010101";
				}
			}

			/**********************************************************
			 * 2. 권하지시위치 수정
			 **********************************************************/
			jrParam.setField("YD_STK_COL_GP_OLD", ydDnWoLocOld.substring(0, 6));
			jrParam.setField("YD_STK_BED_NO_OLD", ydDnWoLocOld.substring(6, 8));
			jrParam.setField("YD_STK_LYR_NO_OLD", ydDnWoLayerOld);
			jrParam.setField("YD_STK_COL_GP_NEW", ysStkColGp);
			jrParam.setField("YD_STK_BED_NO_NEW", ysStkBedNo);
			if (ysDnWoLoc.length() == 6) {
				jrParam.setField("YS_DN_WO_LOC", ysDnWoLoc + ysStkBedNo);
			} else {
				jrParam.setField("YS_DN_WO_LOC", ysDnWoLoc);
			}

			jrParam.setField("YS_DN_WO_LAYER", ydDnWoLayer);
			jrParam.setField("YS_STK_BED_NO", ysStkBedNo);
			jrParam.setField("YS_STK_LYR_NO", ydDnWoLayer);
			jrParam.setField("YD_DN_WO_LOC_XAXIS", ydDnWoLocXaxis);
			jrParam.setField("YD_DN_WO_LOC_YAXIS", ydDnWoLocYaxis);
			jrParam.setField("YD_DN_WO_LOC_ZAXIS", ydDnWoLocZaxis);

			// 적치단 수정 - 기존 및 신규 권하지시위치
			// commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchDnWoLocStkLyr", logId, methodNm, "TB_YS_STKLYR");

			// 적치단 수정 - 기존
			/*
			 * com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdByCrnSchId --기존 권하지시위치 UPDATE TB_YS_STKLYR SET SSTL_NO = NULL , YD_STK_LYR_MTL_STAT = 'E' WHERE SSTL_NO IN (SELECT SSTL_NO FROM TB_YS_CRNWRKMTL WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID ) AND YS_STK_COL_GP = :V_YD_STK_COL_GP_OLD AND
			 * YS_STK_BED_NO = :V_YD_STK_BED_NO_OLD
			 * 
			 */
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdByCrnSchId", logId, methodNm, "기존권하위치 CLEAR");

			// 신규 적치단 재료정보READ
			/*
			 * com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWekMtlByschid SELECT A.YD_CRN_SCH_ID , A.SSTL_NO -- 기존 재료정보 , (SELECT YS_STK_SEQ_NO FROM TB_YS_STKLYR WHERE SSTL_NO = A.SSTL_NO AND YD_STK_LYR_MTL_STAT IN ('C','U') ) AS YS_STK_SEQ_NO --신규 위치에 SEQ_NO FROM TB_YS_CRNWRKMTL A WHERE
			 * A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID AND A.DEL_YN = 'N'
			 */

//			JDTORecordSet jsCrnSchMtl = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWekMtlByschid", logId, methodNm, "기존권하위치 조회"); // 2025.11.11
			JDTORecordSet jsCrnSchMtl = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.selCrnWekMtlByschid", logId, methodNm, "기존권하위치 조회");

			JDTORecord recOutTemp = null;
			JDTORecord recInTemp = null;

			String szSSTL_NO = null;
			String szSEQ_NO = null;

			int intRtnVal = 0;

			// ----------------------------------------------------------------------------------------------------------
			// 신규적치단 활성화
			// ----------------------------------------------------------------------------------------------------------
			for (int Loop_i = 1; Loop_i <= jsCrnSchMtl.size(); Loop_i++) {
				jsCrnSchMtl.absolute(Loop_i);
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(jsCrnSchMtl.getRecord());

				szSSTL_NO = commUtils.trim(recOutTemp.getFieldString("SSTL_NO"));
				szSEQ_NO = commUtils.trim(recOutTemp.getFieldString("YS_STK_SEQ_NO"));

				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YS_STK_COL_GP", ysStkColGp);
				recInTemp.setField("YS_STK_BED_NO", ysStkBedNo);
				recInTemp.setField("YS_STK_LYR_NO", ydDnWoLayer);
				recInTemp.setField("YS_STK_SEQ_NO", szSEQ_NO);
				recInTemp.setField("SSTL_NO", szSSTL_NO);
				recInTemp.setField("YD_STK_LYR_ACT_STAT", "E");
				recInTemp.setField("YD_STK_LYR_MTL_STAT", "D");
				recInTemp.setField("MODIFIER", modifier);

				/*
				 * com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColBedGp UPDATE TB_YS_STKLYR SET MOD_DDTT = SYSDATE , MODIFIER = :V_MODIFIER , YD_STK_LYR_ACT_STAT = NVL(:V_YD_STK_LYR_ACT_STAT,YD_STK_LYR_ACT_STAT) , SSTL_NO = NVL(:V_SSTL_NO,SSTL_NO) , YD_STK_LYR_MTL_STAT =
				 * NVL(:V_YD_STK_LYR_MTL_STAT,YD_STK_LYR_MTL_STAT) WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP AND YS_STK_BED_NO = :V_YS_STK_BED_NO AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO AND YS_STK_SEQ_NO = :V_YS_STK_SEQ_NO
				 */
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColBedGp", logId, methodNm, "TB_YS_STKLYR 등록");

				if (intRtnVal <= 0) {
					commUtils.printLog(logId, "[" + methodNm + "] 적치단[" + ysStkColGp + "]활성화중 ERROR 발생", "SL");
					throw new Exception("적치단변경시 오류 발생.");
				}
			}

			/**********************************************************
			 * 1. 크레인스케줄 취소 빌렛소형 임시베드 권하위치를 수정 하는 경우 , 단 입고시에만,
			 **********************************************************/
			if ("TY".equals(ydDnWoLocOld.substring(2, 4)) && ("CATF01LM".equals(ydSchCd) || "CBTF01LM".equals(ydSchCd))) {

				JDTORecordSet jsCrnSchWB = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWekCancelschid", logId, methodNm, "나머지 스케쥴 조회");

				for (int Loop_i = 1; Loop_i <= jsCrnSchWB.size(); Loop_i++) {
					jsCrnSchWB.absolute(Loop_i);
					recOutTemp = JDTORecordFactory.getInstance().create();
					recOutTemp.setRecord(jsCrnSchWB.getRecord());

					String szWbookId = commUtils.trim(recOutTemp.getFieldString("YD_WBOOK_ID"));
					String szCrnSchId = commUtils.trim(recOutTemp.getFieldString("YD_CRN_SCH_ID"));

					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_WBOOK_ID", szWbookId);
					recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);

					this.trtCrnSchCncl(recInTemp);
				}
			}

			// 적치Bed 수정 - 완산Bed 해제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchDnWoLocStkBed", logId, methodNm, "TB_YS_STKBED");

			// 크레인스케줄 수정 - 권상, 권하지시위치
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchDnWoLocCrnSch", logId, methodNm, "TB_YS_CRNSCH");

			// 기존 대차, 차량 권하위치에서 일반야드로 변경 시 대차 or 차량스케줄 작업예약ID 삭제
			ydDnWoLocOld = ydDnWoLocOld.substring(2, 4);
			if (("TC".equals(ydDnWoLocOld) || "TR".equals(ydDnWoLocOld)) && !ydDnWoLocOld.equals(ysDnWoLoc.substring(2, 4))) {
				if ("TC".equals(ydDnWoLocOld)) {
					// 대차스케줄 수정 - 상차작업예약ID 삭제
					// 작업예약 Table 우선순위 Update
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchDnWoLocTCarSch", logId, methodNm, "TB_YS_TCARSCH");

				} else {
					// 차량스케줄 수정 - 상차작업예약ID 삭제
					// 작업예약 Table 우선순위 Update
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchDnWoLocCarSch", logId, methodNm, "TB_YS_CARSCH");

					// 적치열 수정 - 야드적치대용도코드 삭제
					// 작업예약 Table 우선순위 Update
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchDnWoLocStkCol", logId, methodNm, "TB_YS_STKCOL");
				}
			}

			/**********************************************************
			 * 3. 크레인작업지시요구 전문 조회
			 **********************************************************/
			// 크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);

			jrYdMsg.setResultCode(logId); // Log ID
			jrYdMsg.setResultMsg(methodNm); // Log Method Name
			jrYdMsg.setField("JMS_TC_CD", YsConstant.N7YSL304); // 크레인작업지시요구
			jrYdMsg.setField("YD_EQP_ID", ydEqpId); // 야드설비ID
			jrYdMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); // 야드작업진행상태
			jrYdMsg.setField("YD_SCH_CD", ydSchCd); // 야드스케쥴코드
			jrYdMsg.setField("YD_CRN_SCH_ID", ydCrnSchId); // 야드크레인스케쥴ID
			jrYdMsg.setField("MODIFIER", modifier); // 수정자

			EJBConnector sndConn = new EJBConnector("default", "SbrYsL2RcvSeEJB", this);
			JDTORecord jrRtn = (JDTORecord) sndConn.trx("rcvN7YSL304", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			// rcvN7YSL204

			sndRecord = commUtils.addSndData(sndRecord, jrRtn);

			commUtils.printLog(logId, methodNm, "S-");

			return sndRecord;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * [A] 오퍼레이션명 : 크레인스케줄 취소처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord trtCrnSchCncl(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인스케줄 취소처리[SbrYsJspSeEJB.trtCrnSchCncl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydCrnSchId = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); // 야드크레인스케쥴ID
			String ydWbookId = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID")); // 야드작업예약ID

			if ("".equals(ydCrnSchId)) {
				throw new Exception("크레인스케쥴ID가 없습니다.");
			} else if ("".equals(ydWbookId)) {
				throw new Exception("작업예약ID가 없습니다.");
			}

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
			jrParam.setField("YD_WBOOK_ID", ydWbookId);

			/**********************************************************
			 * 1. 크레인스케쥴 정보 Check
			 **********************************************************/
			// com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWrkMgtSCSch
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWrkMgtSCSch", logId, methodNm, "크레인작업지시read");
			if (jsCrnSch == null || jsCrnSch.size() <= 0) {
				throw new Exception("크레인스케쥴ID[" + ydCrnSchId + "]의 크레인스케줄 정보가 존재하지 않습니다.");
			}

			JDTORecord jrCrnSch = jsCrnSch.getRecord(0);

			String ydWrkProgStat = commUtils.trim(jrCrnSch.getFieldString("YD_WRK_PROG_STAT")); // 야드작업진행상태
			String eqpUpdYn = commUtils.trim(jrCrnSch.getFieldString("EQP_UPD_YN")); // 설비상태수정여부
			String ydEqpId = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID")); // 야드설비ID
			String ydEqpStat = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_STAT")); // 야드설비상태

			if ("2".equals(ydWrkProgStat)) {
				throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [2:권상완료]이므로 취소하실 수 없습니다.");
			} else if ("3".equals(ydWrkProgStat)) {
				throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [3:권하지시]이므로 취소하실 수 없습니다.");
			} else if ("4".equals(ydWrkProgStat)) {
				throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [4:권하완료]이므로 취소하실 수 없습니다.");
			}

			/**********************************************************
			 * 2. 작업진행상태가 [1:권상지시] 이면 작업지시취소 전문 전송
			 **********************************************************/
			if ("1".equals(ydWrkProgStat)) {
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); // 야드크레인스케쥴ID
				jrParam.setField("MSG_GP", "D"); // 전문구분(취소)

				// 크레인작업지시(YDY1L004, YDY3L004) 전문 조회
				String szJMS_TC_CD = "";
				// String szYdGpBay = ydEqpId.substring(0,2);

				szJMS_TC_CD = "YSN7L303";

				jrRtn = commUtils.addSndData(commDao.getMsgL2(szJMS_TC_CD, jrParam));
			}

			/**********************************************************
			 * 3. 권상, 권하위치 원복 - 적치단, 적치Bed
			 **********************************************************/
			// 적치단 수정 - 권상위치(U -> C), 권하위치(D -> E)
			commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updCrnWrkMgtSCStkLyr", logId, methodNm, "TB_YS_STKLYR");

			// 적치Bed 수정 - 완산Bed 해제
			// jspDao.updCrnWrkMgt("SCStkBed", jrParam); com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtSCStkBed
			// BtYsDao.update(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.updCrnWrkMgtSCStkBed", logId, methodNm, "TB_YS_STKBED");

			/**********************************************************
			 * 4. 크레인스케줄 삭제
			 **********************************************************/
			// 크레인작업재료 삭제
			// jspDao.updCrnWrkMgt("SCCrnMtl", jrParam); com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtSCCrnMtl
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtSCCrnMtl", logId, methodNm, "TB_YS_CRNWRKMTL");

			// 크레인스케줄 삭제
			// jspDao.updCrnWrkMgt("SCCrnSch", jrParam); com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtSCCrnSch
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtSCCrnSch", logId, methodNm, "TB_YS_CRNSCH");

			// 분리 및 모음 작업시 작업예약MTL은 삭제이나 작업예약 TABLE에 존재 할수 있음
			/*
			 * com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkBookClear UPDATE TB_YS_WRKBOOK SET DEL_YN = 'Y' WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID AND 0 = ( SELECT SUM(DECODE(DEL_YN,'N',1,0)) FROM TB_YS_WRKBOOKMTL WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID )
			 */
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkBookClear", logId, methodNm, "TB_YS_WRKBOOK");

			/**********************************************************
			 * 5. 설비상태 수정 - 크레인이 고장 또는 Off-Line이 아니고 상태가 다르면
			 **********************************************************/
			if ("Y".equals(eqpUpdYn)) {
				jrParam.setField("YD_EQP_ID", ydEqpId); // 야드설비ID
				jrParam.setField("YD_EQP_STAT", ydEqpStat); // 야드설비상태

				commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updStatEqp", logId, methodNm, "TB_YD_EQP");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 * [A] 오퍼레이션명 : 크레인스케줄 취소처리 (차량)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord trtCrnSchCncl_Car(String sYS_STK_COL_GP) throws DAOException {
		String methodNm = "크레인스케줄 취소처리 (차량)[SbrYsJspSeEJB.trtCrnSchCncl_Car] < " + sYS_STK_COL_GP;
		String logId = commUtils.getLogId();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printLog(logId, "SbrYsJspSeEJB.trtCrnSchCncl_Car :: sYS_STK_COL_GP["+sYS_STK_COL_GP+"]", "SL");

			// Return Value
			JDTORecord jrRtn = null;

			if( "".equals(sYS_STK_COL_GP) || !("TR").equals(sYS_STK_COL_GP.substring(2,4)) ) {
				commUtils.printLog(logId, "차량위치정보가 없습니다.", "SL");
				return jrRtn;
			}
			
			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name
			jrParam.setField("YS_STK_COL_GP", sYS_STK_COL_GP);

			/**********************************************************
			 * 1. 취소할 차량상차 크레인스케줄 정보 Check
			 **********************************************************/
			JDTORecordSet jsCrnSch = commDao.select(jrParam, getCrnWrkMgtSCSch_Car, logId, methodNm, "크레인작업관리 > 취소할 차량상차 크레인스케줄 조회");
			JDTORecord    jrCrnSch = JDTORecordFactory.getInstance().create();
			if (jsCrnSch == null || jsCrnSch.size() <= 0) {
				commUtils.printLog(logId, "차량위치정보[" + sYS_STK_COL_GP + "]의 크레인스케줄 정보가 존재하지 않습니다.", "SL");
				return jrRtn;
			}

			for( int Loop_k = 1; Loop_k <= jsCrnSch.size(); Loop_k++ ) {
				
				jsCrnSch.absolute(Loop_k);
				jrCrnSch = jsCrnSch.getRecord();
				
				String szYD_WBOOK_ID      = commUtils.trim(jrCrnSch.getFieldString("YD_WBOOK_ID"     ));	//야드크레인작업예약ID
				String szYD_CRN_SCH_ID    = commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"   ));	//야드크레인스케쥴ID
				String szYD_WRK_PROG_STAT = commUtils.trim(jrCrnSch.getFieldString("YD_WRK_PROG_STAT"));	//야드작업진행상태: [C:스케쥴명령취소][S:스케쥴작성중][W:명령선택대기][1:권상지시][2:권상완료][3:권하지시][4:권하완료]
				String szEQP_UPD_YN       = commUtils.trim(jrCrnSch.getFieldString("EQP_UPD_YN"      ));	//설비상태수정여부
				String szYD_EQP_ID        = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"       ));	//야드설비ID
				String szYD_EQP_STAT      = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_STAT"     ));	//야드설비상태
				
				commUtils.printLog(logId, "야드크레인작업예약 [" + szYD_WBOOK_ID + "], 야드크레인스케쥴 [" + szYD_CRN_SCH_ID + "],  야드작업진행상태 [" + szYD_WRK_PROG_STAT + "]", "SL");
				
				if ("2".equals(szYD_WRK_PROG_STAT)) {
					commUtils.printLog(logId, "크레인스케줄 [" + szYD_CRN_SCH_ID + "]의 작업진행상태가 [2:권상완료]이므로 취소하실 수 없습니다.", "SL");
					return jrRtn;
				} else if ("3".equals(szYD_WRK_PROG_STAT)) {
					commUtils.printLog(logId, "크레인스케줄 [" + szYD_CRN_SCH_ID + "]의 작업진행상태가 [3:권하지시]이므로 취소하실 수 없습니다.", "SL");
					return jrRtn;
				} else if ("4".equals(szYD_WRK_PROG_STAT)) {
					commUtils.printLog(logId, "크레인스케줄 [" + szYD_CRN_SCH_ID + "]의 작업진행상태가 [4:권하완료]이므로 취소하실 수 없습니다.", "SL");
					return jrRtn;
				}
				
				jrParam.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
				jrParam.setField("YD_WBOOK_ID",   szYD_WBOOK_ID  );
				jrParam.setField("MODIFIER",      "YardSystem"   );
				
				/**********************************************************
				 * 2. 작업진행상태가 [1:권상지시] 이면 작업지시취소 전문 전송
				 **********************************************************/
				if( "1".equals(szYD_WRK_PROG_STAT) ) {
					jrParam.setField("MSG_GP", "D");					//전문구분(취소)
	
					// 크레인작업지시(YDY1L004, YDY3L004) 전문 조회
					String szJMS_TC_CD = "YSN7L203";
					
					jrRtn = commUtils.addSndData(commDao.getMsgL2(szJMS_TC_CD, jrParam));
				}
	
				/**********************************************************
				 * 3. 권상, 권하위치 원복 - 적치단, 적치Bed
				 **********************************************************/
				// 적치단 수정 - 권상위치(U -> C), 권하위치(D -> E)
				commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updCrnWrkMgtSCStkLyr", logId, methodNm, "TB_YS_STKLYR");
				
				/**********************************************************
				 * 4. 크레인 작업재료 & 스케줄 삭제
				 **********************************************************/
				// 크레인스케줄 작업재료 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtSCCrnMtl", logId, methodNm, "TB_YS_CRNWRKMTL");
				
				// 크레인스케줄 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtSCCrnSch", logId, methodNm, "TB_YS_CRNSCH");

				/**********************************************************
				 * 5. 설비상태 수정 - 크레인이 고장 또는 Off-Line이 아니고 상태가 다르면
				 **********************************************************/
				if ("Y".equals(szEQP_UPD_YN)) {
					jrParam.setField("YD_EQP_ID",   szYD_EQP_ID  ); // 야드설비ID
					jrParam.setField("YD_EQP_STAT", szYD_EQP_STAT); // 야드설비상태
					
					commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updStatEqp", logId, methodNm, "TB_YD_EQP");
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리 크레인변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updCraneChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 크레인변경[SbrYsJspSeEJB.updCraneChange] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			String ydCrnSchId = ""; // 야드크레인스케쥴ID
			String ydWbookId = ""; // 야드작업예약ID
			String ydWrkProgStat = ""; // 야드작업진행상태
			String ydSchCd = ""; // 야드스케쥴코드
			String ydEqpId = ""; // 야드설비ID(크레인)
			String chgYdEqpId = ""; // 변경 야드설비ID(크레인)
			String chgYdSchPrior = ""; // 변경 야드스케쥴우선순위
			String chgYdEqpStat = ""; // 변경 야드설비상태
			String chgYdEqpWrkMode = ""; // 변경 야드설비작업Mode
			String modifier = commUtils.trim(gdReq.getParam("userid")); // 수정자

			// DAO Parameter
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			// 처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[] arrYdWbookId = new String[rowCnt];

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					ydWbookId = commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii));
					ydCrnSchId = commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID").getValue(ii));

					// 작업할 야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
					if (commUtils.chkExist(arrYdWbookId, ydWbookId)) {
						continue;
					} // 해당 값이 있는지를 Check

					arrYdWbookId[ii] = ydWbookId;

					/**********************************************************
					 * 1. 크레인스케줄, 스케줄기준, 설비정보 Check 1.1 크레인스케줄의 스케줄ID 및 설비상태 Check 1.2 크레인스케줄 설비ID로 스케줄기준의 주 및 대체 크레인설비ID와 비교하여 변경 크레인설비ID와 순위를 Set 1.3 변경 할 크레인 정보를 Check
					 **********************************************************/
					jrParam.setField("YD_WBOOK_ID", ydWbookId);
					jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);

					// 기본정보조회
					JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCraneChange1", logId, methodNm, "크레인변경 조회");

					if (jsCrn == null || jsCrn.size() <= 0) {
						throw new Exception("크레인스케쥴ID[" + ydCrnSchId + "]의 크레인스케줄 정보가 존재하지 않습니다.");
					}

					JDTORecord jrCrn = jsCrn.getRecord(0);

					ydWrkProgStat = commUtils.trim(jrCrn.getFieldString("YD_WRK_PROG_STAT")); // 야드작업진행상태
					ydSchCd = commUtils.trim(jrCrn.getFieldString("YD_SCH_CD")); // 야드스케쥴코드
					ydEqpId = commUtils.trim(jrCrn.getFieldString("YD_EQP_ID")); // 야드설비ID
					chgYdEqpId = commUtils.trim(jrCrn.getFieldString("CHG_YD_EQP_ID")); // 변경 야드설비ID
					chgYdSchPrior = commUtils.trim(jrCrn.getFieldString("CHG_YD_SCH_PRIOR")); // 변경 야드스케쥴우선순위
					chgYdEqpStat = commUtils.trim(jrCrn.getFieldString("CHG_YD_EQP_STAT")); // 변경 야드설비상태
					chgYdEqpWrkMode = commUtils.trim(jrCrn.getFieldString("CHG_YD_EQP_WRK_MODE")); // 변경 야드설비작업Mode

					if ("2".equals(ydWrkProgStat)) {
						throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [2:권상완료]이므로 변경하실 수 없습니다.");
					} else if ("3".equals(ydWrkProgStat)) {
						throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [3:권하지시]이므로 변경하실 수 없습니다.");
					} else if ("4".equals(ydWrkProgStat)) {
						throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [4:권하완료]이므로 변경하실 수 없습니다.");
					} else if ("".equals(chgYdEqpId)) {
						throw new Exception("변경 크레인 [" + chgYdEqpId + "]의 정보가 존재하지 않습니다.");
					} else if ("B".equals(chgYdEqpStat)) {
						throw new Exception("변경 크레인 [" + chgYdEqpId + "]의 설비상태가 [B:고장]이므로 변경하실 수 없습니다.");
					} else if (!"1".equals(chgYdEqpWrkMode)) {
						throw new Exception("변경 크레인 [" + chgYdEqpId + "]의 설비작업Mode가 [Off-Line]이므로 변경하실 수 없습니다.");
					} else if ("1".equals(chgYdEqpStat) || "2".equals(chgYdEqpStat) || "3".equals(chgYdEqpStat)) {
						throw new Exception("변경 크레인 [" + chgYdEqpId + "]의 작업지시가 이미 내려진 상태이므로 변경하실 수 없습니다.");
					}

					commUtils.printLog(logId, "크레인변경 [ " + ydWbookId + " : " + ydEqpId + " >> " + chgYdEqpId + " ]", "SL");

					/**********************************************************
					 * 2. 작업예약 및 크레인스케줄 Table에 대체 크레인ID와 우선순위를 Update
					 **********************************************************/
					jrParam.setField("MODIFIER", modifier);
					jrParam.setField("YD_SCH_PRIOR", chgYdSchPrior);
					jrParam.setField("YD_EQP_ID", chgYdEqpId);

					// 작업예약 Table 우선순위 Update
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updWrkBookPrior", logId, methodNm, "TB_YS_WRKBOOK");

					if ("1".equals(ydWrkProgStat)) {
						/**********************************************************
						 * 2.1 이전 크레인의 작업지시 취소 전문 송신
						 **********************************************************/
						jrParam.setField("MSG_GP", "D"); // 전문구분(취소)
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L203", jrParam));
					}

					// 크레인스케줄 Table 크레인ID, 우선순위 Update
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtW", logId, methodNm, "TB_YS_CRNSCH");

					/**********************************************************
					 * 3. 현 작업상태가 권상지시[1]인 경우
					 **********************************************************/
					if ("1".equals(ydWrkProgStat)) {
						/**********************************************************
						 * 3.1 변경 크레인의 설비 Table 상태정보를 Update
						 **********************************************************/
						jrParam.setField("MODIFIER", modifier);
						jrParam.setField("YD_EQP_STAT", "1"); // 야드설비상태 : 권상작업지시
						jrParam.setField("YD_EQP_ID", chgYdEqpId);
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnYsEqp", logId, methodNm, "TB_YS_EQP");

						/**********************************************************
						 * 3.2 변경 크레인의 크레인작업지시요구 처리
						 **********************************************************/
						// 크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
						JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
						jrYdMsg.setResultCode(logId); // Log ID
						jrYdMsg.setResultMsg(methodNm); // Log Method Name
						jrYdMsg.setField("JMS_TC_CD", YsConstant.N7YSL304); // 크레인작업지시요구
						jrYdMsg.setField("YD_EQP_ID", chgYdEqpId); // 야드설비ID
						jrYdMsg.setField("YD_WRK_PROG_STAT", "1"); // 야드작업진행상태(권상작업지시)
						jrYdMsg.setField("YD_SCH_CD", ydSchCd); // 야드스케쥴코드
						jrYdMsg.setField("YD_CRN_SCH_ID", ydCrnSchId); // 야드크레인스케쥴ID

						EJBConnector sndConn = new EJBConnector("default", "SbrYsL2RcvSeEJB", this);
						JDTORecord jrSnd = (JDTORecord) sndConn.trx("rcvN7YSL304", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
						// rcvN7YSL204

						jrRtn = commUtils.addSndData(jrRtn, jrSnd);

						/**********************************************************
						 * 3.3 이전 크레인의 설비 Table 상태정보를 Update
						 **********************************************************/
						jrParam.setField("MODIFIER", modifier);
						jrParam.setField("YD_EQP_ID", ydEqpId);
						jrParam.setField("YD_EQP_STAT", "W"); // 야드설비상태 : 권상작업지시
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnYsEqp", logId, methodNm, "TB_YS_EQP");

						/**********************************************************
						 * 3.4 이전 크레인의 작업실적응답 전문을 전송
						 **********************************************************/
						JDTORecord resMsg = JDTORecordFactory.getInstance().create(); // 크레인작업실적응답 전문 생성용

						resMsg.setResultCode(logId); // Log ID
						resMsg.setResultMsg(methodNm); // Log Method Name
						resMsg.setField("YD_EQP_ID", ydEqpId); // 야드설비ID
						resMsg.setField("YD_L2_WR_GP", "J"); // 야드L2실적구분(지시요구)
						resMsg.setField("YD_L3_HD_RS_CD", "9999"); // 야드L3처리결과코드(Error)
						resMsg.setField("YD_L3_MSG", "크레인변경[" + chgYdEqpId + "]"); // 야드L3MESSAGE

//						jrRtn = commUtils.addSndData(jrRtn, sbrYsComm.getYSN7L204(resMsg));
					}
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * [A] 오퍼레이션명 : 크레인작업관리 순위변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updPriorChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 순위변경[SbrYsJspSeEJB.updPriorChange] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydWbookId = ""; // 야드작업예약ID
			String ydSchPrior = ""; // 야드스케쥴우선순위

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			// 처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[] arrYdWbookId = new String[rowCnt];

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					/**********************************************************
					 * 1. 작업예약ID Check
					 **********************************************************/
					ydWbookId = commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii)); // 야드작업예약ID
					ydSchPrior = commUtils.trim(gdReq.getHeader("YD_SCH_PRIOR").getValue(ii)); // 야드스케쥴우선순위

					// 작업할 야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
					if (commUtils.chkExist(arrYdWbookId, ydWbookId)) {
						continue;
					}
					arrYdWbookId[ii] = ydWbookId;

					commUtils.printLog(logId, "우선순위변경 [ " + ydWbookId + " >> " + ydSchPrior + " ]", "SL");

					/**********************************************************
					 * 2. 작업예약 및 크레인스케줄 Table에 우선순위를 Update
					 **********************************************************/
					jrParam.setField("YD_WBOOK_ID", ydWbookId);
					jrParam.setField("YD_SCH_PRIOR", ydSchPrior);

					// 작업예약 Table 우선순위 Update
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updWrkBookPrior", logId, methodNm, "TB_YS_WRKBOOK");

					// 크레인스케줄 Table 크레인ID, 우선순위 Update
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgt", logId, methodNm, "TB_YS_CRNSCH");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * [A] 오퍼레이션명 : 크레인작업관리 긴급작업
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updPriorWrkChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 순위변경[SbrYsJspSeEJB.updPriorWrkChange] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydWbookId = ""; // 야드작업예약ID
			String ydSchPrior = ""; // 야드스케쥴우선순위
			String ydEqpId = "";
			String ydCrnSchId = "";
			String ydCrnSchIdWrk = "";
			String ydSchCd = "";

			JDTORecord jrRtn = null;
			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			// 처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[] arrYdWbookId = new String[rowCnt];

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					/**********************************************************
					 * 1. 작업예약ID Check
					 **********************************************************/
					ydEqpId = commUtils.trim(gdReq.getHeader("YD_EQP_ID").getValue(ii));
					ydWbookId = commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii)); // 야드작업예약ID
					ydSchPrior = commUtils.trim(gdReq.getHeader("YD_SCH_PRIOR").getValue(ii));
					ydCrnSchId = commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID").getValue(ii));
					ydSchCd = commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii));

					commUtils.printLog(logId, "긴급작업 [ " + ydEqpId + " >> " + ydWbookId + " >> " + ydSchPrior + " >> " + ydCrnSchId + " >> " + ydSchCd + " ]", "SL");

					// 작업할 야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
					if (commUtils.chkExist(arrYdWbookId, ydWbookId)) {
						continue;
					}
					arrYdWbookId[ii] = ydWbookId;

					jrParam.setField("YD_EQP_ID", ydEqpId);
					jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
					jrParam.setField("YD_WBOOK_ID", ydWbookId);
					jrParam.setField("YD_SCH_PRIOR", "0");

					JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWrkMgtPriorWrk", logId, methodNm, "크레인변경 조회");

					// 작업예약 Table 우선순위 Update
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updWrkBookPrior", logId, methodNm, "TB_YS_WRKBOOK");

					if (jsCrn == null || jsCrn.size() <= 0) {
						// 기존 작업 우선순위 변경
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgt", logId, methodNm, "TB_YS_CRNSCH");

					} else {

						JDTORecord jrCrn = jsCrn.getRecord(0);
						ydCrnSchIdWrk = commUtils.trim(jrCrn.getFieldString("YD_CRN_SCH_ID"));
						jrParam.setField("YD_CRN_SCH_ID", ydCrnSchIdWrk);
						// 크레인스케줄 Table 크레인ID, 우선순위 Update,
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtPriorWrk", logId, methodNm, "TB_YS_CRNSCH");

						/**********************************************************
						 * 2. 작업진행상태가 [1:권상지시] 이면 작업지시취소 전문 전송
						 **********************************************************/
						if (!"".equals(ydCrnSchIdWrk)) {
							jrParam.setField("MSG_GP", "D"); // 전문구분(취소)

							jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN7L203", jrParam));
						}
						/**********************************************************
						 * 3.2 변경 크레인의 크레인작업지시요구 처리
						 **********************************************************/
						// 크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
						JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

						jrYdMsg.setResultCode(logId); // Log ID
						jrYdMsg.setResultMsg(methodNm); // Log Method Name
						jrYdMsg.setField("JMS_TC_CD", YsConstant.N7YSL304); // 크레인작업지시요구
						jrYdMsg.setField("YD_EQP_ID", ydEqpId); // 야드설비ID
						jrYdMsg.setField("YD_WRK_PROG_STAT", "4"); // 야드작업진행상태(권상작업지시)
						jrYdMsg.setField("YD_SCH_CD", ydSchCd); // 야드스케쥴코드
						jrYdMsg.setField("YD_CRN_SCH_ID", ydCrnSchId); // 야드크레인스케쥴ID

						// rcvYSYSJ001 에서 공장l2 확인함
						EJBConnector sndConn = new EJBConnector("default", "SbrYsL2RcvSeEJB", this);
						JDTORecord jrSnd = (JDTORecord) sndConn.trx("rcvN7YSL304", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
						// rcvN7YSL204

						jrRtn = commUtils.addSndData(jrRtn, jrSnd);
					}
				}
			}

			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * [A] 오퍼레이션명 : 크레인작업관리 권하위치변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updDownLocChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 권하위치변경[SbrYsJspSeEJB.updDownLocChange] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			JDTORecord jrRtn2 = JDTORecordFactory.getInstance().create();

			// EJB Call을 위한 Message 생성 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrYdMsg.setResultCode(logId); // Log ID
			jrYdMsg.setResultMsg(methodNm); // Log Method Name

			// 권하위치변경 대상 스케줄
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					
					String ydSchCd = gdReq.getHeader("YD_SCH_CD").getValue(ii).substring(0, 4);
					String ydSchCd2= gdReq.getHeader("YD_SCH_CD").getValue(ii).substring(0, 6);
					
					// 스케줄 코드가 대차 상차, 하차 일 경우
					if( ydSchCd.equals("GDTC") ) {
							// 현재 대차 정보 확인
							JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
							jrParam1.setField("YD_EQP_ID", ydSchCd2);
							
							JDTORecordSet jsCol1 = commDao.select(jrParam1, getStatEqp, logId, methodNm, "현재 대차정보 조회");
							
							if( jsCol1.size() > 0 ) {
								jsCol1.first();
								JDTORecord recInPara = jsCol1.getRecord();
								
								String sYD_CURR_BAY_GP = commUtils.trim(recInPara.getFieldString("YD_CURR_BAY_GP"));
								String sYD_EQP_STAT = commUtils.trim(recInPara.getFieldString("YD_EQP_STAT"));
								String sYD_EQP_WRK_MODE = commUtils.trim(recInPara.getFieldString("YD_EQP_WRK_MODE"));
								// 대차가 A동에 있을 경우
								if( sYD_CURR_BAY_GP.equals("A") ) {
									if( sYD_EQP_STAT.equals("N") && sYD_EQP_WRK_MODE.equals("1") ) {
										jrYdMsg.setField("YD_EQP_ID", commUtils.trim(gdReq.getHeader("YD_EQP_ID").getValue(ii))); // 야드설비ID(크레인)
										jrYdMsg.setField("YD_SCH_CD", commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii))); // 야드스케쥴코드
										jrYdMsg.setField("YD_CRN_SCH_ID", commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID").getValue(ii))); // 야드크레인스케쥴ID
										jrYdMsg.setField("YD_WBOOK_ID", commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii))); // 야드작업예약ID
										jrYdMsg.setField("YS_DN_WO_LOC", commUtils.trim(gdReq.getHeader("YS_DN_WO_LOC").getValue(ii))); // 야드권하지시위치(신규)
										jrYdMsg.setField("YD_WRK_PROG_STAT", commUtils.trim(gdReq.getHeader("YD_WRK_PROG_STAT").getValue(ii))); // 야드작업진행상태

										// 권하지시위치 변경
										jrRtn = commUtils.addSndData(jrRtn, this.updCrnSchDnWoLoc(jrYdMsg));
									}
								} else {
									if( sYD_EQP_STAT.equals("N") && sYD_EQP_WRK_MODE.equals("1") ) {
										JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
										
										//대차이동요청(YSN7L312)
										jrParam2.setField("YD_EQP_ID", ydSchCd2);
										jrParam2.setField("TCAR_LOC", "A"); // 목표동
										jrParam2.setField("YD_EQP_WRK_STAT", "U"); // 적재상태
										jrParam2.setField("ST_LOC", "B"); // 출발동
										jrParam2.setField("END_LOC", "A"); // 도착동
										
										jrRtn2 = commUtils.addSndData(jrRtn2, commDao.getMsgL2("YSN7L312", jrParam2));
										
										if(jrRtn2 != null) {
											EJBConnector sndConn1 = new EJBConnector("default", "YsCommEJB", this);
											sndConn1.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn2 });
										}
										
										jrYdMsg.setField("YD_EQP_ID", commUtils.trim(gdReq.getHeader("YD_EQP_ID").getValue(ii))); // 야드설비ID(크레인)
										jrYdMsg.setField("YD_SCH_CD", commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii))); // 야드스케쥴코드
										jrYdMsg.setField("YD_CRN_SCH_ID", commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID").getValue(ii))); // 야드크레인스케쥴ID
										jrYdMsg.setField("YD_WBOOK_ID", commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii))); // 야드작업예약ID
										jrYdMsg.setField("YS_DN_WO_LOC", commUtils.trim(gdReq.getHeader("YS_DN_WO_LOC").getValue(ii))); // 야드권하지시위치(신규)
										jrYdMsg.setField("YD_WRK_PROG_STAT", commUtils.trim(gdReq.getHeader("YD_WRK_PROG_STAT").getValue(ii))); // 야드작업진행상태

										// 권하지시위치 변경
										jrRtn = commUtils.addSndData(jrRtn, this.updCrnSchDnWoLoc(jrYdMsg));
									}
								}
							}
					} else {
						jrYdMsg.setField("YD_EQP_ID", commUtils.trim(gdReq.getHeader("YD_EQP_ID").getValue(ii))); // 야드설비ID(크레인)
						jrYdMsg.setField("YD_SCH_CD", commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii))); // 야드스케쥴코드
						jrYdMsg.setField("YD_CRN_SCH_ID", commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID").getValue(ii))); // 야드크레인스케쥴ID
						jrYdMsg.setField("YD_WBOOK_ID", commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii))); // 야드작업예약ID
						jrYdMsg.setField("YS_DN_WO_LOC", commUtils.trim(gdReq.getHeader("YS_DN_WO_LOC").getValue(ii))); // 야드권하지시위치(신규)
						jrYdMsg.setField("YD_WRK_PROG_STAT", commUtils.trim(gdReq.getHeader("YD_WRK_PROG_STAT").getValue(ii))); // 야드작업진행상태

						// 권하지시위치 변경
						jrRtn = commUtils.addSndData(jrRtn, this.updCrnSchDnWoLoc(jrYdMsg));
					}
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리 작업취소
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updCraneWrkCancel(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 작업취소[SbrYsJspSeEJB.updCraneWrkCancel] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			String ydCrnSchId = ""; // 야드크레인스케쥴ID
			String ydWbookId = ""; // 야드작업예약ID
			String ydEqpId = ""; // 야드설비ID
			String ydSchCd = ""; // 야드스케쥴코드
			String ydPrepSchId = ""; // 야드준비스케쥴ID
			String ydTcarSchId = ""; // 야드대차스케줄ID

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name
			// 처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[] arrYdWbookId = new String[rowCnt];

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					ydWbookId = commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii));
					ydEqpId = commUtils.trim(gdReq.getHeader("YD_EQP_ID").getValue(ii));
					ydSchCd = commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii));
					ydPrepSchId = commUtils.trim(gdReq.getHeader("CAR_YD_WBOOK_ID").getValue(ii));
					ydTcarSchId = commUtils.trim(gdReq.getHeader("YD_TCAR_SCH_ID").getValue(ii));

					// 작업할 야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
					if (commUtils.chkExist(arrYdWbookId, ydWbookId)) {
						continue;
					}
					arrYdWbookId[ii] = ydWbookId;

					// 기본정보조회
					jrParam.setField("YD_WBOOK_ID", ydWbookId);

					JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnsch", logId, methodNm, "크레인작업지시read");
					if (jsCrnSch == null || jsCrnSch.size() <= 0) {
						throw new Exception("작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 존재하지 않습니다.");
					}
					ydCrnSchId = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID")); // 야드크레인스케쥴ID

					commUtils.printLog(logId, "작업취소 [ " + ydWbookId + " - " + ydCrnSchId + " ]", "SL");

					jrParam.setField("YD_WBOOK_ID", ydWbookId);
					jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
					jrParam.setField("YD_EQP_ID", ydEqpId);
					jrParam.setField("YD_SCH_CD", ydSchCd);
					jrParam.setField("CAR_YD_WBOOK_ID", ydPrepSchId);
					jrParam.setField("YD_TCAR_SCH_ID", ydTcarSchId);
					jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));

					/**********************************************************
					 * 1. 크레인스케줄 취소
					 **********************************************************/
					jrRtn = commUtils.addSndData(jrRtn, this.trtCrnSchCncl(jrParam));

					/**********************************************************
					 * 2. 작업예약 취소
					 **********************************************************/
					jrRtn = commUtils.addSndData(jrRtn, this.trtWrkBookCncl(jrParam));
				}
			}

			/**********************************************************
			 * 5. 크레인작업지시요구 전문 조회
			 **********************************************************/
			// 크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, "delWrkBook");

			jrYdMsg.setResultCode(logId); // Log ID
			jrYdMsg.setResultMsg(methodNm); // Log Method Name
			jrYdMsg.setField("JMS_TC_CD", "N7YSL304"); // 크레인작업지시요구
			jrYdMsg.setField("YD_EQP_ID", ydEqpId); // 야드설비ID
			jrYdMsg.setField("YD_WRK_PROG_STAT", "4"); // 야드작업진행상태(권하완료)
			jrYdMsg.setField("YD_SCH_CD", ydSchCd); // 야드스케쥴코드
			jrYdMsg.setField("YD_CRN_SCH_ID", ydCrnSchId); // 야드크레인스케쥴ID

			EJBConnector sndConn = new EJBConnector("default", "SbrYsL2RcvSeEJB", this);
			JDTORecord jrRtn1 = (JDTORecord) sndConn.trx("rcvN7YSL304", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			// rcvN7YSL204

			jrRtn = commUtils.addSndData(jrRtn, jrRtn1);

			commUtils.printParam(logId, jrRtn);
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리-스케줄취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public JDTORecord updCraneSchCancel(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 스케줄취소[SbrYsJspSeEJB.updCraneSchCancel] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			String ydCrnSchId = ""; // 야드크레인스케쥴ID
			String ydWbookId = ""; // 야드작업예약ID

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name
			// 처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[] arrYdWbookId = new String[rowCnt];

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					ydWbookId = commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii));
					ydCrnSchId = commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID").getValue(ii));

					// 작업할 야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
					if (commUtils.chkExist(arrYdWbookId, ydWbookId)) {
						continue;
					}
					arrYdWbookId[ii] = ydWbookId;

					commUtils.printLog(logId, "스케줄취소 [ " + ydWbookId + " - " + ydCrnSchId + " ]", "SL");

					jrParam.setField("YD_WBOOK_ID", ydWbookId);
					jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);

					/**********************************************************
					 * 1. 크레인스케줄 취소
					 **********************************************************/
					jrRtn = commUtils.addSndData(jrRtn, this.trtCrnSchCncl(jrParam));
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updCraneSchCancel
	
	/**
	 * 저장위치수정 - 저장품등록 및 변경전 정합성 체크
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord insBtYsStock(GridData gdReq) throws DAOException {
		String methodNm = "저장위치수정 - 저장품등록 및 변경전 정합성 체크[SbrYsJspSeEJB.insBtYsStock] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet jsTemp = null;
			JDTORecord jrTemp = null;

			String szStlNo = null;
			String szYsStkColGp = null;
			String szYsStkBedNo = null;
			String szYsStkLyrNo = null;
			String szYsStkSeqNo = null;

			String szOldStlNo = null;
			String szOldYsStkColGp = null;
			String szOldYsStkBedNo = null;
			String szOldYsStkLyrNo = null;
			String szOldYsStkSeqNo = null;

			String szFromStlNo = null;
			String szFromYsStkColGp = null;
			String szFromYsStkBedNo = null;
			String szFromYsStkLyrNo = null;
			String szFromYsStkSeqNo = null;

			String szStkStlNo = null;
			// String szCurrProgCd = null;
			// String szOrdYeojaeGp = null;
			String szWbookId = null;
			String szCrnSchId = null;
			String szToLocMtlStat = null;

			String szModGp = null; // 작업구분
			String sFromLoc = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {

				szStlNo = commUtils.getValue(gdReq, "SSTL_NO", ii);
				szYsStkColGp = commUtils.getValue(gdReq, "YS_STK_COL_GP", ii);
				szYsStkBedNo = commUtils.getValue(gdReq, "YS_STK_BED_NO", ii);
				szYsStkLyrNo = commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii);
				szYsStkSeqNo = commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii);

				szOldStlNo = commUtils.getValue(gdReq, "OLD_SSTL_NO", ii);
				szOldYsStkColGp = commUtils.getValue(gdReq, "OLD_YS_STK_COL_GP", ii);
				szOldYsStkBedNo = commUtils.getValue(gdReq, "OLD_YS_STK_BED_NO", ii);
				szOldYsStkLyrNo = commUtils.getValue(gdReq, "OLD_YS_STK_LYR_NO", ii);
				szOldYsStkSeqNo = commUtils.getValue(gdReq, "OLD_YS_STK_SEQ_NO", ii);

				szFromStlNo = commUtils.getValue(gdReq, "FROM_SSTL_NO", ii);
				szFromYsStkColGp = commUtils.getValue(gdReq, "FROM_YS_STK_COL_GP", ii);
				szFromYsStkBedNo = commUtils.getValue(gdReq, "FROM_YS_STK_BED_NO", ii);
				szFromYsStkLyrNo = commUtils.getValue(gdReq, "FROM_YS_STK_LYR_NO", ii);
				szFromYsStkSeqNo = commUtils.getValue(gdReq, "FROM_YS_STK_SEQ_NO", ii);

				if (szStlNo.equals(szOldStlNo) && szYsStkColGp.equals(szOldYsStkColGp) && szYsStkBedNo.equals(szOldYsStkBedNo) && szYsStkLyrNo.equals(szOldYsStkLyrNo)
						&& szYsStkSeqNo.equals(szOldYsStkSeqNo)) {
					// 변경사항이 없음으로 Skip 한다.
					continue;
				}

				if ("".equals(szStlNo) && !"".equals(szOldStlNo)) {
					// 삭제처리
					szModGp = "DELETE";
					jrParam.setField("SSTL_NO", szOldStlNo);
				}

				if (!"".equals(szStlNo) && "".equals(szOldStlNo) && "".equals(szFromStlNo)) {
					// 추가처리
					szModGp = "ADD";
					jrParam.setField("SSTL_NO", szStlNo);
				}

				if (!"".equals(szStlNo) && "".equals(szOldStlNo) && szStlNo.equals(szFromStlNo)) {
					// 이동처리
					szModGp = "MOVE";
					jrParam.setField("SSTL_NO", szStlNo);
				}

				if (!szYsStkColGp.equals(szOldYsStkColGp) || !szYsStkBedNo.equals(szOldYsStkBedNo) || !szYsStkLyrNo.equals(szOldYsStkLyrNo) || !szYsStkSeqNo.equals(szOldYsStkSeqNo)) {
					// SEQ변경처리 UP,DOWN
					szModGp = "UPDOWN";
					jrParam.setField("SSTL_NO", szStlNo);
				}

				jrParam.setField("YS_STK_COL_GP", szYsStkColGp);
				jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
				jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
				jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);

				// 제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BILLET공통에서 현재진도코드를 가져온다.
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");

				if (jsTemp != null && jsTemp.size() > 0) {
					jrTemp = jsTemp.getRecord(0);

					szStkStlNo = commUtils.trim(jrTemp.getFieldString("SSTL_NO"));
					// szCurrProgCd = commUtils.trim(jrTemp.getFieldString("CURR_PROG_CD"));
					// szOrdYeojaeGp = commUtils.trim(jrTemp.getFieldString("ORD_YEOJAE_GP"));
					szWbookId = commUtils.trim(jrTemp.getFieldString("YD_WBOOK_ID"));
					szCrnSchId = commUtils.trim(jrTemp.getFieldString("YD_CRN_SCH_ID"));
					szToLocMtlStat = commUtils.trim(jrTemp.getFieldString("TOLOC_MTL_STAT"));

				} else {
					throw new Exception("제품번호로 저장품 및 작업대상여부 조회시 에러가 발생했습니다!");
				}

				// 저장품에 존재하는 제품번호인지 체크
				if ("ADD".equals(szModGp) || "MOVE".equals(szModGp)) {
					if ("".equals(szStkStlNo)) {
						// 저장품 등록
						commDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.insBtYdStock", logId, methodNm, "저장품 등록");
					}
				}

				// 추가,삭제,이동 모두 크레인스케줄에 작업대상인지 체크
				if (!"".equals(szCrnSchId)) {
					throw new Exception("제품번호 : " + jrParam.getFieldString("SSTL_NO") + " 가 크레인스케줄(" + szCrnSchId + ")에 작업대상으로 잡혀있습니다! 크레인작업이 완료된 이후나 크래인작업을 취소 한 후 수정이 가능합니다.");
				}

				// 삭제는 작업예약에 대상으로 잡혀있으면 삭제 불가함
				if ("DELETE".equals(szModGp) && !"".equals(szWbookId) && "C".equals(szToLocMtlStat)) {
					throw new Exception("제품번호 : " + jrParam.getFieldString("SSTL_NO") + " 가 작업예약(" + szWbookId + ")에 작업대상으로 잡혀있습니다! 작업예약을 취소한 후 삭제가 가능합니다.");
				}

				if ("ADD".equals(szModGp) || "MOVE".equals(szModGp)) {
					// TO위치의 재료상태가 'E' 가 아니면 작업할 수 없음
					if (!"E".equals(szToLocMtlStat)) {
						throw new Exception("TO 위치의 재료상태가 " + szToLocMtlStat + " 입니다. 등록(이동) 작업을 할 수 없습니다.");
					}
				} else {
					// TO위치의 재료상태가 'U'나 'D'일 경우 수정작업을 할 수 없음
					// if("U".equals(szToLocMtlStat) || "D".equals(szToLocMtlStat) ) {
					// throw new Exception("TO 위치의 재료상태가 " + szToLocMtlStat + " 로 변경되었습니다. 삭제(변경) 작업을 할 수 없습니다.");
					// }
				}

				// 이동인 경우 From위치에 szStlNo가 적치중 인지 확인
				if ("MOVE".equals(szModGp)) {
					jrParam.setField("YS_STK_COL_GP", szFromYsStkColGp);
					jrParam.setField("YS_STK_BED_NO", szFromYsStkBedNo);
					jrParam.setField("YS_STK_LYR_NO", szFromYsStkLyrNo);
					jrParam.setField("YS_STK_SEQ_NO", szFromYsStkSeqNo);

					// From 위치 확인 하기
					jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyr", logId, methodNm, "적치단정보조회");

					if (jsTemp != null && jsTemp.size() > 0) {
						jrTemp = jsTemp.getRecord(0);

						if (!szStlNo.equals(commUtils.trim(jrTemp.getFieldString("SSTL_NO")))) {

							sFromLoc = szFromYsStkColGp + "-" + szFromYsStkBedNo + "-" + szFromYsStkLyrNo + "-" + szFromYsStkSeqNo;

							throw new Exception("From 위치[" + sFromLoc + "]의 재료번호가 [" + szStlNo + "]가 아닙니다. Crane작업이나 다른 작업자에 의해 이미 수정되었습니다.");
						}

					} else {
						throw new Exception("From 위치 조회시 에러가 발생했습니다!");
					}
				}

				// SEQ변경처리 UP,DOWN 인 경우 이전위치에 szStlNo가 적치중 인지 확인
				if ("UPDOWN".equals(szModGp)) {

					jrParam.setField("YS_STK_COL_GP", szOldYsStkColGp);
					jrParam.setField("YS_STK_BED_NO", szOldYsStkBedNo);
					jrParam.setField("YS_STK_LYR_NO", szOldYsStkLyrNo);
					jrParam.setField("YS_STK_SEQ_NO", szOldYsStkSeqNo);

					// From 위치 확인 하기
					jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyr", logId, methodNm, "적치단정보조회");
 
					if (jsTemp != null && jsTemp.size() > 0) {
						jrTemp = jsTemp.getRecord(0);

						if (!szStlNo.equals(commUtils.trim(jrTemp.getFieldString("SSTL_NO")))) {

							sFromLoc = szOldYsStkColGp + "-" + szOldYsStkBedNo + "-" + szOldYsStkLyrNo + "-" + szOldYsStkSeqNo;

							throw new Exception("이전위치[" + sFromLoc + "]의 재료번호가 [" + szStlNo + "]가 아닙니다. Crane작업이나 다른 작업자에 의해 이미 수정되었습니다.");
						}

					} else {
						throw new Exception("이전(Old) 위치 조회시 에러가 발생했습니다!");
					}
				}

				// DELETE인 경우 이전위치에 szOldStlNo가 적치중 인지 확인
				if ("DELETE".equals(szModGp)) {

					jrParam.setField("YS_STK_COL_GP", szYsStkColGp);
					jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
					jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
					jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);

					// From 위치 확인 하기
					jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyr", logId, methodNm, "적치단정보조회");

					if (jsTemp != null && jsTemp.size() > 0) {
						jrTemp = jsTemp.getRecord(0);

						if (!szOldStlNo.equals(commUtils.trim(jrTemp.getFieldString("SSTL_NO")))) {

							sFromLoc = szYsStkColGp + "-" + szYsStkBedNo + "-" + szYsStkLyrNo + "-" + szYsStkSeqNo;

							throw new Exception("현재 위치[" + sFromLoc + "]의 재료번호가 [" + szOldStlNo + "]가 아닙니다. Crane작업이나 다른 작업자에 의해 이미 수정되었습니다.");
						}

					} else {
						throw new Exception("현재 위치 조회시 에러가 발생했습니다!");
					}
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of insBtYsStock

	/**
	 * 저장위치 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord updStrLocMod(GridData gdReq) throws DAOException {
		String methodNm = "저장위치 수정[SbrYsJspSeEJB.updStrLocMod] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet jsTemp = null;
			JDTORecord jrTemp = null;

			String szStlNo = null;
			String szYsStkColGp = null;
			String szYsStkBedNo = null;
			String szYsStkLyrNo = null;
			String szYsStkSeqNo = null;

			String szOldStlNo = null;
			String szOldYsStkColGp = null;
			String szOldYsStkBedNo = null;
			String szOldYsStkLyrNo = null;
			String szOldYsStkSeqNo = null;

			String szFromStlNo = null;
			String szFromYsStkColGp = null;
			String szFromYsStkBedNo = null;
			String szFromYsStkLyrNo = null;
			String szFromYsStkSeqNo = null;

			String szStkStlNo = null;
			String szCurrProgCd = null;
			String szOrdYeojaeGp = null;
			// String szWbookId = null;
			// String szCrnSchId = null;
			// String szToLocMtlStat = null;

			String szModGp = null; // 작업구분
			String szFtmvCarudCmplYn = null; // 이송하차완료처리

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name

			// 이송하차완료처리
			szFtmvCarudCmplYn = commUtils.trim(gdReq.getParam("FTMV_CARUD_CMPL_YN"));

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {

				szStlNo = commUtils.getValue(gdReq, "SSTL_NO", ii);
				szYsStkColGp = commUtils.getValue(gdReq, "YS_STK_COL_GP", ii);
				szYsStkBedNo = commUtils.getValue(gdReq, "YS_STK_BED_NO", ii);
				szYsStkLyrNo = commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii);
				szYsStkSeqNo = commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii);

				szOldStlNo = commUtils.getValue(gdReq, "OLD_SSTL_NO", ii);
				szOldYsStkColGp = commUtils.getValue(gdReq, "OLD_YS_STK_COL_GP", ii);
				szOldYsStkBedNo = commUtils.getValue(gdReq, "OLD_YS_STK_BED_NO", ii);
				szOldYsStkLyrNo = commUtils.getValue(gdReq, "OLD_YS_STK_LYR_NO", ii);
				szOldYsStkSeqNo = commUtils.getValue(gdReq, "OLD_YS_STK_SEQ_NO", ii);

				szFromStlNo = commUtils.getValue(gdReq, "FROM_SSTL_NO", ii);
				szFromYsStkColGp = commUtils.getValue(gdReq, "FROM_YS_STK_COL_GP", ii);
				szFromYsStkBedNo = commUtils.getValue(gdReq, "FROM_YS_STK_BED_NO", ii);
				szFromYsStkLyrNo = commUtils.getValue(gdReq, "FROM_YS_STK_LYR_NO", ii);
				szFromYsStkSeqNo = commUtils.getValue(gdReq, "FROM_YS_STK_SEQ_NO", ii);

				if (szStlNo.equals(szOldStlNo) && szYsStkColGp.equals(szOldYsStkColGp) && szYsStkBedNo.equals(szOldYsStkBedNo) && szYsStkLyrNo.equals(szOldYsStkLyrNo)
						&& szYsStkSeqNo.equals(szOldYsStkSeqNo)) {
					// 변경사항이 없음으로 Skip 한다.
					continue;
				}

				if ("".equals(szStlNo) && !"".equals(szOldStlNo)) {
					// 삭제처리
					szModGp = "DELETE";
					jrParam.setField("SSTL_NO", szOldStlNo);
				}

				if (!"".equals(szStlNo) && "".equals(szOldStlNo) && "".equals(szFromStlNo)) {
					// 추가처리
					szModGp = "ADD";
					jrParam.setField("SSTL_NO", szStlNo);
				}

				if (!"".equals(szStlNo) && "".equals(szOldStlNo) && szStlNo.equals(szFromStlNo)) {
					// 이동처리
					szModGp = "MOVE";
					jrParam.setField("SSTL_NO", szStlNo);
				}

				if (!szYsStkColGp.equals(szOldYsStkColGp) || !szYsStkBedNo.equals(szOldYsStkBedNo) || !szYsStkLyrNo.equals(szOldYsStkLyrNo) || !szYsStkSeqNo.equals(szOldYsStkSeqNo)) {
					// SEQ변경처리 UP,DOWN
					szModGp = "UPDOWN";
					jrParam.setField("SSTL_NO", szStlNo);
				}

				jrParam.setField("YS_STK_COL_GP", szYsStkColGp);
				jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
				jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
				jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);

				// 제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BILLET공통에서 현재진도코드를 가져온다.
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");

				if (jsTemp != null && jsTemp.size() > 0) {
					jrTemp = jsTemp.getRecord(0);

					szStkStlNo = commUtils.trim(jrTemp.getFieldString("SSTL_NO"));
					szCurrProgCd = commUtils.trim(jrTemp.getFieldString("CURR_PROG_CD"));
					szOrdYeojaeGp = commUtils.trim(jrTemp.getFieldString("ORD_YEOJAE_GP"));
					// szWbookId = commUtils.trim(jrTemp.getFieldString("YD_WBOOK_ID"));
					// szCrnSchId = commUtils.trim(jrTemp.getFieldString("YD_CRN_SCH_ID"));
					// szToLocMtlStat = commUtils.trim(jrTemp.getFieldString("TOLOC_MTL_STAT"));

				} else {
					throw new Exception("제품번호로 저장품 및 작업대상여부 조회시 에러가 발생했습니다!");
				}

				// 저장품에 존재하는 제품번호인지 체크
				if ("ADD".equals(szModGp) || "MOVE".equals(szModGp)) {
					if ("".equals(szStkStlNo)) {
						throw new Exception("제품번호 : " + jrParam.getFieldString("SSTL_NO") + " 가 TB_YS_STOCK 에 없습니다.");
					}
				}

				// ------------------------------------------------------------------------------------------
				if ("ADD".equals(szModGp)) {

					// SSTL_NO로 저장위치 조회하여 FROM위치가 존재하면 그 위치에서 SSTL_NO를 Clear 한다.
					jrParam.setField("SSTL_NO", szStlNo);
					jrParam.setField("YD_GP", gdReq.getParam("YD_GP"));

					JDTORecordSet jsStkLyrStlNo = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStrLocRegPda", logId, methodNm, "재료번호로 조회");

					if (jsStkLyrStlNo.size() > 0) {

						String sFromLoc = null;

						for (int mm = 0; mm < jsStkLyrStlNo.size(); mm++) {
							if (!"".equals(jsStkLyrStlNo.getRecord(mm).getFieldString("YD_CRN_SCH_ID"))) {
								// 크레인스케줄 편성 대상이면 에러 메세지를 리턴하고 종료한다.

								sFromLoc = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP") + "-" + jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_BED_NO") + "-"
										+ jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_LYR_NO") + "-" + jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_SEQ_NO");

								throw new Exception("재료번호: " + jsStkLyrStlNo.getRecord(mm).getFieldString("SSTL_NO") + " 는 FROM 위치(" + sFromLoc + ")에서  크레인스케줄에 편성되어 있습니다. 등록 작업을 할 수 없습니다.");
							} else {
								// 작업이력에 남길 From 위치설정를 읽어 온다.
								szFromYsStkColGp = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP");
								szFromYsStkBedNo = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_BED_NO");
								szFromYsStkLyrNo = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_LYR_NO");
								szFromYsStkSeqNo = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_SEQ_NO");
							}
						}
					}
				}
				// ------------------------------------------------------------------------------------------

				// SSTL_NO 로 STKLYR 'C','U','D' 모두 Clear 하기
				jrParam.setField("SSTL_NO", szStlNo);
				jrParam.setField("YD_GP", gdReq.getParam("YD_GP"));
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.clearStkLyr", logId, methodNm, "모든 SSTL_NO가 있던 위치 Clear");

				if ("UPDOWN".equals(szModGp)) {
					// UP,DOWN 키를 눌러 SEQ 가 변경되었다면 해당 야드맵의 적치단재료상태를 재료번호가 있으면 적치중으로 없으면 적치가능으로 설정한다.

					jrParam.setField("SSTL_NO", szStlNo); // szStlNo 에는 삭제일경우 빈값이 들어있고 그외는 제품번호가 들어 있다.
					jrParam.setField("YD_STK_LYR_ACT_STAT", ""); // "" 값은 이전값을 변경안한다는 의미
					if ("".equals(szStlNo)) {
						jrParam.setField("YD_STK_LYR_MTL_STAT", "E"); // 적치가능
					} else {
						jrParam.setField("YD_STK_LYR_MTL_STAT", "C"); // 적치중
					}
					jrParam.setField("YS_STK_COL_GP", szYsStkColGp);
					jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
					jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
					jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);

					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "적치단정보 수정");

				} else {

					// To 위치 적치단 정보 수정
					jrParam.setField("SSTL_NO", szStlNo); // szStlNo 에는 삭제일경우 빈값이 들어있고 그외는 제품번호가 들어 있다.
					jrParam.setField("YD_STK_LYR_ACT_STAT", ""); // "" 값은 이전값을 변경안한다는 의미
					if ("DELETE".equals(szModGp)) {
						jrParam.setField("YD_STK_LYR_MTL_STAT", "E"); // 적치가능
					} else {
						jrParam.setField("YD_STK_LYR_MTL_STAT", "C"); // 적치중
					}
					jrParam.setField("YS_STK_COL_GP", szYsStkColGp);
					jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
					jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
					jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);

					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "적치단정보 수정");
				}

				// BILLET공통 위치정보 수정하기
				if ("DELETE".equals(szModGp)) {
					// 삭제일경우 처리 ???
					jrParam.setField("FNL_REG_PGM", "btStrLocModjm");
					jrParam.setField("YD_GP", "_");
					jrParam.setField("YD_BAY_GP", szYsStkColGp.substring(1, 2));
					jrParam.setField("YD_EQP_GP", szYsStkColGp.substring(2, 4));
					jrParam.setField("YS_STK_COL_NO", szYsStkColGp.substring(4, 6));
					jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
					jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
					jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);
					jrParam.setField("YS_STR_LOC", "_" + szYsStkColGp.substring(1, 6) + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo);
					jrParam.setField("SSTL_NO", szOldStlNo); // 삭제된 번호

					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLoc", logId, methodNm, "BILLET공통 야드저장위치 수정");
				} else {
					// 추가, 이동
					jrParam.setField("FNL_REG_PGM", "btStrLocModjm");
					jrParam.setField("YD_GP", szYsStkColGp.substring(0, 1));
					jrParam.setField("YD_BAY_GP", szYsStkColGp.substring(1, 2));
					jrParam.setField("YD_EQP_GP", szYsStkColGp.substring(2, 4));
					jrParam.setField("YS_STK_COL_NO", szYsStkColGp.substring(4, 6));
					jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
					jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
					jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);
					jrParam.setField("YS_STR_LOC", szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo);
					jrParam.setField("SSTL_NO", szStlNo);

//////////////////////////////////////////////////////////////////////////					
// 2025.05.22 저장위치수정 화면에서 이송하차완료처리 체크한 경우 szFtmvCarudCmplYn = 'Y'
//            화면에서 이송하차완료 체크 삭제 하기로 함 (신진희 책임과 협의함 : 빌렛정정에서는 진도 변경 없음)					
					if ("Y".equals(szFtmvCarudCmplYn)) {

//						// 이송하차완료 처리
//						if ("1".equals(szOrdYeojaeGp)) { // 주문재인경우
//							jrParam.setField("CURR_PROG_CD", "B");
//						} else if ("2".equals(szOrdYeojaeGp)) { // 여재인경우
//							jrParam.setField("CURR_PROG_CD", "Y");
//						} else {
//							jrParam.setField("CURR_PROG_CD", szCurrProgCd);
//						}
//
//						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLocProgCd", logId, methodNm, "BILLET공통 야드저장위치,진도코드 수정");
//
//						// 진행관리로 YSPBJ002 전송
//						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSPBJ002", jrParam));
//
//						// 2)이송지시 테이블 변경
//						// - 완료일자,계상일자,STATUS('*') 변경하기
//						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updFtmvCarudCmpl", logId, methodNm, "이송하차완료 처리");

					} else {
//						if ("D".equals(szCurrProgCd) && "1".equals(szOrdYeojaeGp)) {
//							// 공통의 진도코드가 'D':이송지시대기 이고 주여구분이 '1':주문재 이면 야드저장품의 재료진도코드를 'B':지시대기 로 변경한다. + 위치정보 수정
//// 2025.08.22 TB_PB_BILLETCOMM(BILLET공통) 야드저장위치,진도코드 수정 우선 막음							
////							jrParam.setField("CURR_PROG_CD", "B");
////							commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLocProgCd", logId, methodNm, "BILLET공통 야드저장위치,진도코드 수정");
//						} else {
//							commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLoc", logId, methodNm, "BILLET공통 야드저장위치 수정");
//						}
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLoc", logId, methodNm, "BILLET공통 야드저장위치 수정");
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정"); // 추가 2025.12.24
					}
				}
//////////////////////////////////////////////////////////////////////////

				// 야드저장품 위치정보 수정하기
				if ("DELETE".equals(szModGp)) {
					// 삭제일경우 처리 ???
					jrParam.setField("YS_STK_COL_GP", "_" + szYsStkColGp.substring(1, 6));
					jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
					jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
					jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);
					jrParam.setField("YS_STR_LOC", "_" + szYsStkColGp.substring(1, 6) + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo);
					jrParam.setField("SSTL_NO", szOldStlNo); // 삭제된 번호

					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
				} else {
					// 추가, 이동
					jrParam.setField("YS_STK_COL_GP", szYsStkColGp);
					jrParam.setField("YS_STK_BED_NO", szYsStkBedNo);
					jrParam.setField("YS_STK_LYR_NO", szYsStkLyrNo);
					jrParam.setField("YS_STK_SEQ_NO", szYsStkSeqNo);
					jrParam.setField("YS_STR_LOC", szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo);
					jrParam.setField("SSTL_NO", szStlNo);

// 2025.05.22 빌렛정정에서는 진도 변경 없음 (신진희 책임과 협의함 : )					
					
//					if ("D".equals(szCurrProgCd) && "1".equals(szOrdYeojaeGp)) {
//						// 공통의 진도코드가 'D':이송지시대기 이고 주여구분이 '1':주문재 이면 야드저장품의 재료진도코드를 'B':지시대기 로 변경한다. + 위치정보 수정
//// 2025.08.22 TB_PB_BILLETCOMM(BILLET공통) 야드저장위치,진도코드 수정 우선 막음							
////						jrParam.setField("STL_PROG_CD", "B");
////						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLocProgCd", logId, methodNm, "야드저장품 야드저장위치,재료진도코드 수정");
//					} else {
//						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
//					}
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
				}

				// 이력정보 등록하기
				if ("DELETE".equals(szModGp)) {
					// 삭제일경우
					jrParam.setField("SSTL_NO", szOldStlNo);
					jrParam.setField("YD_GP", szYsStkColGp.substring(0, 1));
					jrParam.setField("YD_SCH_CD", szYsStkColGp.substring(0, 2) + "YD01MM");

					jrParam.setField("YS_UP_WR_LOC", szYsStkColGp + szYsStkBedNo);
					jrParam.setField("YS_UP_WR_LAYER", szYsStkLyrNo);
					jrParam.setField("YS_UP_WR_SEQ_NO", szYsStkSeqNo);

					jrParam.setField("YS_DN_WR_LOC", "");
					jrParam.setField("YS_DN_WR_LAYER", "");
					jrParam.setField("YS_DN_WR_SEQ_NO", "");

				} else if ("ADD".equals(szModGp)) {
					// 추가
					jrParam.setField("SSTL_NO", szStlNo);
					jrParam.setField("YD_GP", szYsStkColGp.substring(0, 1));
					jrParam.setField("YD_SCH_CD", szYsStkColGp.substring(0, 2) + "YD01MM");

					jrParam.setField("YS_UP_WR_LOC", szFromYsStkColGp + szFromYsStkBedNo);
					jrParam.setField("YS_UP_WR_LAYER", szFromYsStkLyrNo);
					jrParam.setField("YS_UP_WR_SEQ_NO", szFromYsStkSeqNo);

					jrParam.setField("YS_DN_WR_LOC", szYsStkColGp + szYsStkBedNo);
					jrParam.setField("YS_DN_WR_LAYER", szYsStkLyrNo);
					jrParam.setField("YS_DN_WR_SEQ_NO", szYsStkSeqNo);

				} else if ("MOVE".equals(szModGp)) {
					// 이동
					jrParam.setField("SSTL_NO", szStlNo);
					jrParam.setField("YD_GP", szYsStkColGp.substring(0, 1));
					jrParam.setField("YD_SCH_CD", szYsStkColGp.substring(0, 2) + "YD01MM");

					jrParam.setField("YS_UP_WR_LOC", szFromYsStkColGp + szFromYsStkBedNo);
					jrParam.setField("YS_UP_WR_LAYER", szFromYsStkLyrNo);
					jrParam.setField("YS_UP_WR_SEQ_NO", szFromYsStkSeqNo);

					jrParam.setField("YS_DN_WR_LOC", szYsStkColGp + szYsStkBedNo);
					jrParam.setField("YS_DN_WR_LAYER", szYsStkLyrNo);
					jrParam.setField("YS_DN_WR_SEQ_NO", szYsStkSeqNo);

				} else {
					// UPDOWN
					jrParam.setField("SSTL_NO", szStlNo);
					jrParam.setField("YD_GP", szYsStkColGp.substring(0, 1));
					jrParam.setField("YD_SCH_CD", szYsStkColGp.substring(0, 2) + "YD01MM");

					jrParam.setField("YS_UP_WR_LOC", szOldYsStkColGp + szOldYsStkBedNo);
					jrParam.setField("YS_UP_WR_LAYER", szOldYsStkLyrNo);
					jrParam.setField("YS_UP_WR_SEQ_NO", szOldYsStkSeqNo);

					jrParam.setField("YS_DN_WR_LOC", szYsStkColGp + szYsStkBedNo);
					jrParam.setField("YS_DN_WR_LAYER", szYsStkLyrNo);
					jrParam.setField("YS_DN_WR_SEQ_NO", szYsStkSeqNo);
				}
				jrParam.setField("YD_SCH_ST_GP", "B"); // 야드스케줄 기동 구분 "B" 로 넣어준다. B:작업자 Backup
				jrParam.setField("YD_AID_WRK_YN", "N"); // 야드보조작업여부 - N:주작업

				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkHistByJsp", logId, methodNm, "화면에의한 이력정보 수정");

				// L2로 재원정보 전문 전송
				if ("DELETE".equals(szModGp)) {
					jrParam.setField("YD_INFO_SYNC_CD", "D"); // 야드정보동기화코드 D:생산종료(삭제)
				} else if ("ADD".equals(szModGp)) {
					jrParam.setField("YD_INFO_SYNC_CD", "A"); // 야드정보동기화코드 A:생산실적
				} else {
					jrParam.setField("YD_INFO_SYNC_CD", "5"); // 야드정보동기화코드 5:지정저장품
				}

				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L202", jrParam));

////////////////////////////////////////////////////////////				
// 2025.08.21 타부분 전송 막음 START				
//				// TO위치가 장입대(TZ)이면 생산통제 소형압연장입진행실적 (YSCUJ032) 전송
//				if ("ADD".equals(szModGp) || "MOVE".equals(szModGp)) {
//
//					if ("TZ".equals(szYsStkColGp.substring(2, 4)) && "01".equals(szYsStkBedNo)) {
//
//						jrParam.setField("CHG_SUP_PROG_STAT", "30");
//						jrParam.setField("SSTL_NO", szStlNo);
//						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSCUJ032Backup", jrParam));
//					}
//				}

//				// 생산통제 빌렛입고실적(YSCUJ038)
//				jrParam.setField("SSTL_NO", szStlNo);
//				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSCUJ038Backup", jrParam));
//
//				
//				// 소형압연L2로 장입이상재 CARRY-OUT 완료 실적을 전송
//				if (("MOVE".equals(szModGp) && "LB".equals(szFromYsStkColGp.substring(2, 4))) || ("DELETE".equals(szModGp) && "LB".equals(szYsStkColGp.substring(2, 4)))) {
//					jrParam.setField("SSTL_NO1", "");
//					jrParam.setField("SSTL_NO2", "");
//					jrParam.setField("SSTL_NO3", "");
//					jrParam.setField("SSTL_NO4", "");
//					jrParam.setField("SSTL_NO5", "");
//					jrParam.setField("SSTL_NO6", "");
//					jrParam.setField("SSTL_NO7", "");
//					jrParam.setField("SSTL_NO8", "");
//					jrParam.setField("SSTL_NO9", "");
//					jrParam.setField("SSTL_NO10", "");
//
//					jrParam.setField("YD_STK_BED_STL_SH", "1");
//					jrParam.setField("YD_EQP_WRK_SH", "1");
//
//					if ("DELETE".equals(szModGp)) {
//						jrParam.setField("SSTL_NO1", szOldStlNo);
//						jrParam.setField("YD_STK_COL_GP", szYsStkColGp);
//						jrParam.setField("YD_STK_BED_NO", szYsStkBedNo);
//					} else {
//						jrParam.setField("SSTL_NO1", szStlNo);
//						jrParam.setField("YD_STK_COL_GP", szFromYsStkColGp);
//						jrParam.setField("YD_STK_BED_NO", szFromYsStkBedNo);
//					}
//
//					// 장입이상재 Carry-out 완료 송신
//					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM5L101BackUp", jrParam));
//				
//				}
// 2025.08.21 타부분 전송 막음 END				
////////////////////////////////////////////////////////////

				// String sApplyYnPI1 = commDao.ApplyYnPI("", methodNm, "APPPI1", "*", "*");

				/*
				 * MES_PI 2022-09-14 이준기 당진공장 내 특수강 이송실적 통계로 송신 USRPDA.SP_SS_PD_MATL_FTMV_WR_MAIN('WBV041018','KD01','KDCS','20220711',:v1)
				 */// PIDEV

				// if("Y".equals(sApplyYnPI1)){

////////////////////////////////////////////////////////////
// 2025.08.21 타부분 처리 막음 START				
//				String fromLoc = "";
//				String toLoc = "";
//				// JDTORecord recordSp = null;
//				int[] inParamIndex = { 1, 2, 3, 4 };
//				String currDt = commUtils.getDateTime14(); // 현재일시(yyyyMMddHHmmss)
//				String iniDate = YsCommUtils.getIniDate(currDt);
//
//				if ("DELETE".equals(szModGp) && "C".equals(szYsStkColGp.substring(1, 2))) {
//
//					commUtils.printLog(logId, "szOldStlNo : " + szOldStlNo, "반입SL");
//
//					fromLoc = "S220";
//					toLoc = "S210";
//					Object[] inParam = { szOldStlNo, fromLoc, toLoc, iniDate };
//					// recordSp = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_PIDEV");
//					commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_PIDEV");
//
//					commUtils.printLog(logId, "sstlNo : " + szOldStlNo, "반입SL");
//					commUtils.printLog(logId, "ydWrkHdsDd : " + iniDate, "반입SL");
//					commUtils.printLog(logId, "fromLoc : " + fromLoc, "반입SL");
//					commUtils.printLog(logId, "toLoc : " + toLoc, "반입SL");
//
//				} else if ("ADD".equals(szModGp) && ("A".equals(szYsStkColGp.substring(1, 2)) || "B".equals(szYsStkColGp.substring(1, 2)))) {
//
//					commUtils.printLog(logId, "szStlNo : " + szStlNo, "입고SL");
//
//					fromLoc = "S210";
//					toLoc = "S220";
//					Object[] inParam = { szStlNo, fromLoc, toLoc, iniDate };
//					// recordSp = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_PIDEV");
//					commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_PIDEV");
//
//					commUtils.printLog(logId, "sstlNo : " + szStlNo, "입고SL");
//					commUtils.printLog(logId, "ydWrkHdsDd : " + iniDate, "입고SL");
//					commUtils.printLog(logId, "fromLoc : " + fromLoc, "입고SL");
//					commUtils.printLog(logId, "toLoc : " + toLoc, "입고SL");
//				}
// 2025.08.21 타부분 처리 막음 END				
////////////////////////////////////////////////////////////
				// }
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updStrLocMod
	
	/**
	 * 재료 지정 등록/해제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updStockAgsnReg(GridData gdReq) throws DAOException {
		String methodNm = "재료 지정 등록/해제[SbrYsJspSeEJB.updStockAgsnReg(GridData)]";
		String logId = gdReq.getIPAddress();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
		
			String vStock_No = gdReq.getParam("V_STL_NOS");
			String vStockList[] = vStock_No.split(",");
				
			for (int i = 0; i < vStockList.length; i++) {
				//열정보 수정
				
				if(gdReq.getParam("V_GP").equals("1")) {
					jrParam.setField("SSTL_NO"		, vStockList[i]); 
					jrParam.setField("CHK_YN"		, gdReq.getParam("V_CHK_YN")); 
		
					commDao.update(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.updYdStkAsgnReg", logId, methodNm, "재료 지정 등록");
					commUtils.printLog(logId, methodNm, "S-");
				}
				else {
					jrParam.setField("SSTL_NO"		, vStockList[i]); 
					jrParam.setField("CHK_YN"		, null); 
		
					commDao.update(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.updYdStkAsgnReg", logId, methodNm, "재료 지정 해제");
					commUtils.printLog(logId, methodNm, "S-");
				}
			}
			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updStockAgsnReg
	
	/**
	 * 차량작업관리 > 차량Point작업현황 - 차량초기화
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord initMvCarSchMgt(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "이송차량스케줄 초기화[SbrYsJspSeEJB.initMvCarSchMgt] < ";
		String logId = gdReq[0].getRequestUserIp();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			String ydCarSchId  		= null;
			String trnEqpCd    		= null;
			String ysStkColGp 		= null;  
			String ydCarpntCd 		= null;  
			
			String WLOC_CD			= null;
			String YD_PNT_CD		= null;
			
	    	int				intLevLocGp     	    = 0;
	    	int 			intRtnVal				= 0;
	    	String			szMsg					= null;
	    	String 			szYD_CARLD_STOP_LOC		= null;
	    	
			JDTORecordSet 	rsStkCol 				= JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord 		recInTemp 				= JDTORecordFactory.getInstance().create();
	    	JDTORecord		recOutTemp				= JDTORecordFactory.getInstance().create();
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq[0].getFieldString("userId")));

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			String ydGp		 	= commUtils.trim(gdReq[0].getFieldString("YD_GP"));
			
			for (int ii = 0; ii < gdReq.length; ii++) {
				
				ydCarSchId		= commUtils.nvl(gdReq[ii].getFieldString("YD_CAR_SCH_ID"), "");
				trnEqpCd		= commUtils.nvl(gdReq[ii].getFieldString("TRN_EQP_CD"), "");
				ysStkColGp 		= commUtils.nvl(gdReq[ii].getFieldString("YS_STK_COL_GP"), "");
				ydCarpntCd		= commUtils.nvl(gdReq[ii].getFieldString("YD_CARPNT_CD"), "");
				WLOC_CD			= commUtils.nvl(gdReq[ii].getFieldString("WLOC_CD"), "");
				YD_PNT_CD 		= commUtils.nvl(gdReq[ii].getFieldString("YD_PNT_CD"), "");
				
				/**********************************************************
		    	 * 5.출발지 적치열 베드/단 정보 체크
		    	 **********************************************************/			
		    	recInTemp.setField("WLOC_CD",   WLOC_CD);
		    	recInTemp.setField("YD_PNT_CD", YD_PNT_CD);

		    	rsStkCol = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolWLocCdandPntCd", logId, methodNm, "적치열 조회"); 
		    	intLevLocGp = rsStkCol.size();	    	
		    	if (rsStkCol == null || intLevLocGp == 0) {
		    		szMsg= "[" + methodNm + "] 발지개소["+WLOC_CD+"] 및 포인트 코드["+YD_PNT_CD+"]가 타공정코드가 아니고 대기장입니다.";
		    		commUtils.printLog(logId, szMsg, "SL");
		    	}
		    	
				/**********************************************************
		    	 * 6.출발지 정보 CLEAR / 비활성화 상태(YD_STK_COL_ACT_STAT = C)로 업데이트
		    	 **********************************************************/
		    	if(intLevLocGp > 0) {
		    		
		    		rsStkCol.absolute(1);
			    	recOutTemp.setRecord(rsStkCol.getRecord());		    	
			    	szYD_CARLD_STOP_LOC     	= commUtils.trim(recOutTemp.getFieldString("YS_STK_COL_GP")); 
			    	String szCOL_TRN_EQP_CD   	= commUtils.trim(recOutTemp.getFieldString("TRN_EQP_CD")); 		    	
			    	szMsg = "[" + methodNm + "] 발지개소코드["+WLOC_CD+"], " +
			    			"발지개소POINT코드["+YD_PNT_CD+"]로 야드에서 관리되는 적치열구분[출발지:"+szYD_CARLD_STOP_LOC+"]이 존재합니다.";
			    	commUtils.printLog(logId, szMsg, "SL");
					
					/**********************************************************
			    	 * 6-1.(적치열의 운송코드 = 전문 운송코드) -> 맵 Clear
			    	 **********************************************************/
					if( szCOL_TRN_EQP_CD.equals(trnEqpCd))	{					
						szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CARLD_STOP_LOC+"]의 운송장비코드["+szCOL_TRN_EQP_CD+"]와 전문의 운송장비코드["+trnEqpCd+"]가 같으므로 맵 Clear 시작 ";
						commUtils.printLog(logId, szMsg, "SL");
						
						/**********************************************************
				    	 * 6-1-1. 출발야드 적치열 -> 비활성상태(C) 로 업데이트
				    	 **********************************************************/
						szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CARLD_STOP_LOC+"]을 비활성상태로 변경처리 시작 ";
						commUtils.printLog(logId, szMsg, "SL");
						
				    	recInTemp.setField("YS_STK_COL_GP",        szYD_CARLD_STOP_LOC);
				    	recInTemp.setField("YD_STK_COL_ACT_STAT",  "C");
				    	recInTemp.setField("YD_CAR_USE_GP",        "");
				    	recInTemp.setField("TRN_EQP_CD",           "");
				    	recInTemp.setField("CAR_NO",               "");
				    	recInTemp.setField("CARD_NO",              "");
				    	recInTemp.setField("MODIFIER", 			   commUtils.trim(gdReq[0].getFieldString("userid")));
				    	
				    	intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStatClear", logId, methodNm, "TB_YS_STKCOL 등록");
						if(intRtnVal <= 0) {
							szMsg="[" + methodNm + "] 적치열[" + szYD_CARLD_STOP_LOC + "]활성화중 ERROR 발생.";
							commUtils.printLog(logId, methodNm, "SL");
							m_ctx.setRollbackOnly();
							throw new DAOException(szMsg);
						}
					
						/**********************************************************
				    	 * 6-1-2. 차량포인트통합관리 
				    	 **********************************************************/
						//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
						//YsCarPointinforeg2("2","","",szYD_CARLD_STOP_LOC,"","","C",logId,methodNm);

						recInTemp.setField("STAT", "C");
						recInTemp.setField("YS_STK_COL_GP", szYD_CARLD_STOP_LOC);
						
						intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointstackcolgpupdateCT", logId, methodNm, "저장위치로 초기화 하는 경우(구내운송)");
						if(intRtnVal <= 0) {
							szMsg="저장위치로 차량포인트 초기화중 ERROR 발생.";
							commUtils.printLog(logId, methodNm, "SL");
							throw new DAOException(szMsg);
						}
						
						 // 적치베드 비활성상태로 변경
						/**********************************************************
				    	 * 6-1-3. 출발야드 적치베드 -> 야드적치베드활성상태(=C(비활성상태), YD_STK_BED_ACT_STAT) 
				    	 *                         및 BED중량MAX(=기본값, YD_STK_BED_WT_MAX) 으로 업데이트
				    	 **********************************************************/
						szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CARLD_STOP_LOC+"]의 베드를 비활성상태와 BED중량MAX기본값으로 변경처리 시작 ";
						commUtils.printLog(logId, szMsg, "SL");
						
						recInTemp.setField("YD_STK_BED_WT_MAX", YsConstant.YD_STK_BED_WT_MAX_DEFAULT); // YsConstant
						recInTemp.setField("YS_STK_COL_GP", szYD_CARLD_STOP_LOC);
						recInTemp.setField("YD_STK_BED_ACT_STAT", "C");
						
						intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedYdStkColGp", logId, methodNm, "TB_YS_STKBED 등록");
						if(intRtnVal <= 0) {
							szMsg="[" + methodNm + "] 적치BED[" + szYD_CARLD_STOP_LOC + "]활성화중 ERROR 발생.";
							commUtils.printLog(logId, methodNm, "SL");
							throw new DAOException(szMsg);
						}
						
						/**********************************************************
				    	 * 6-1-4. 출발야드 적치단 -> 야드적치단활성상태(=C(비활성상태), YD_STK_LYR_ACT_STAT) 
				    	 *                       및 야드적치단재료상태(=E(적치가능), YD_STK_LYR_MTL_STAT) 로 업데이트
				    	 **********************************************************/
						szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CARLD_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 시작 ";
						commUtils.printLog(logId, methodNm, "SL");
						
						recInTemp.setResultCode(logId);	//Log ID
						recInTemp.setResultMsg(methodNm);	//Log Method Name
						recInTemp.setField("YS_STK_COL_GP", szYD_CARLD_STOP_LOC);
						recInTemp.setField("YD_STK_LYR_ACT_STAT", "C");
						recInTemp.setField("SSTL_NO", "");
						recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
						
						intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "TB_YS_STKLYR 등록");
						if(intRtnVal <= 0) {
							szMsg="[" + methodNm + "] 적치단[" + szYD_CARLD_STOP_LOC + "]활성화중 ERROR 발생.";
							commUtils.printLog(logId, szMsg, "SL");
							throw new DAOException(szMsg);
						}
						
						/**********************************************************
				    	 * 6-1-5. 차량 출발 시 상차지 저장위치 제원 야드 L2 로 전송
				    	 *          야드BLOOM:N1,BILLET:N2,선재:N3,봉강:N4,선재자동화:N5,봉강자동화:N6
				    	 *          YSN7L201 저장위치제원
				    	 *          YSN7L202 저장품제원
				    	 *          YSN7L203 크레인작업지시
				    	 *          YSN7L404 크레인작업실적응답
				    	 **********************************************************/
						String	szJMS_TC_CD = "YSN7L301";
			    		recInTemp.setField("MSG_ID"			,    szJMS_TC_CD);
						recInTemp.setField("YD_INFO_SYNC_CD", "3");						//1:동,2:SPAN,3:열,4:BED
						recInTemp.setField("YD_GP"			, szYD_CARLD_STOP_LOC.substring(0, 1));
						recInTemp.setField("YS_STK_COL_GP"	, szYD_CARLD_STOP_LOC);
						recInTemp.setField("YD_CAR_PROG_STAT", "1");
						recInTemp.setField("YD_EQP_WRK_STAT" , "U");
						szMsg = "[" + methodNm + "] 공차출발시 시 저장위치 제원 야드L2로 전송";
						
						//전송 Data 생성
						jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2(szJMS_TC_CD, recInTemp));
					}
		    	}
				
				/**********************************************************
				* 2. 기존 이송차량스케줄/재료 삭제
				**********************************************************/
				jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
				jrParam.setField("MODIFIER", commUtils.trim(gdReq[0].getFieldString("userid")));

				//이송차량재료 초기화
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSchMtl", logId, methodNm, "이송차량재료 초기화");

				//이송차량스케줄 초기화
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch", logId, methodNm, "이송차량스케줄 초기화");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of initMvCarSchMgt
	
	/**
	 * 차량작업 포인트 현황- 입동지시
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procBayInWo(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-입동지시[SbrYsJspSeEJB.procBayInWo] < ";
		String logId = gdReq[0].getRequestUserIp();
		JDTORecord recInTemp = null;
		JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setResultCode(logId);	//Log ID
			recInTemp.setResultMsg(methodNm);	//Log Method Name
			recInTemp.setField("JMS_TC_CD"				,"YSYSJ801");  //차량입동지시 요구 기존:YDYDJ662
			recInTemp.setField("JMS_TC_CREATE_DDTT"		,commUtils.getDateTime14());
				
			for (int ii = 0; ii < gdReq.length; ii++) {
				
				commUtils.printLog(logId, commUtils.nvl(gdReq[ii].getFieldString("YD_CARPNT_CD"), ""), "SL");
				
				recInTemp.setField("YD_CARPNT_CD"	, commUtils.nvl(gdReq[ii].getFieldString("YD_CARPNT_CD"), ""));		//입동포인트
				recInTemp.setField("YD_CAR_STOP_LOC", commUtils.nvl(gdReq[ii].getFieldString("YS_STK_COL_GP"), ""));		//입동포인트
//				recInTemp.setField("YD_CAR_SCH_ID"	, commUtils.getValue(gdReq, "YD_CAR_SCH_ID", ii));	        //차량스케줄ID
				recInTemp.setField("CAR_NO" 		, commUtils.nvl(gdReq[ii].getFieldString("TRN_EQP_CD"), ""));
				sndRecord = commUtils.addSndData(sndRecord,recInTemp);
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return sndRecord;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 차량작업 포인트 현황
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procPntUnit(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-포인트 사용 등록[SbrYsJspSeEJB.procPntUnit] < ";
		String logId = gdReq[0].getRequestUserIp();
		String szMsg = null;
		String szMethodName = "procPntUnit";
		String szYS_STK_COL_GP		= null;
		String szYD_STK_COL_ACT_STAT= null;
		String szOLD_YD_STK_COL_ACT_STAT= null;
		String szJMS_TC_CD  = null; 
		JDTORecord recInTemp = null;
		JDTORecord recInTemp1 = null;
		boolean isSendable				= true;
		try {
			commUtils.printLog(logId, methodNm, "S+");
			//Return Value
			JDTORecord recOutTemp = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq[0].getFieldString("userid")));
			JDTORecordSet rsStkCol = JDTORecordFactory.getInstance().createRecordSet("");	
			JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
			
			for (int ii = 0; ii < gdReq.length; ii++) {
				
				szYS_STK_COL_GP 		= commUtils.nvl(gdReq[ii].getFieldString("YS_STK_COL_GP"), "");
				szYD_STK_COL_ACT_STAT	= commUtils.nvl(gdReq[ii].getFieldString("YD_STK_COL_ACT_STAT"), "");

    			recOutTemp = JDTORecordFactory.getInstance().create();
    			jrParam.setField("YS_STK_COL_GP", szYS_STK_COL_GP);
    			jrParam.setField("YD_STK_COL_ACT_STAT", szYD_STK_COL_ACT_STAT);
    			jrParam.setField("MODIFIER", 	szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
    	    	
    	    	rsStkCol = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkCol1", logId, methodNm, "적치열 조회"); 
		    	
		    	if (rsStkCol == null || rsStkCol.size() <= 0) {
					szMsg="["+methodNm+"] 적치열 조회 getYdStkcol data not found";
					throw new Exception(szMsg);
				}

		    	rsStkCol.absolute(1);
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsStkCol.getRecord());

		    	szOLD_YD_STK_COL_ACT_STAT   = commUtils.trim(recOutTemp.getFieldString("YD_STK_COL_ACT_STAT"       )); 
    	    	
    	    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStat 
				UPDATE TB_YS_STKCOL
				   SET MOD_DDTT     = SYSDATE             
					 , MODIFIER     = :V_MODIFIER             
					 , YD_STK_COL_ACT_STAT = NVL(:V_YD_STK_COL_ACT_STAT,YD_STK_COL_ACT_STAT)
					 , TRN_EQP_CD   = NVL(:V_TRN_EQP_CD, TRN_EQP_CD)       
					 , CAR_NO       = NVL(:V_CAR_NO, CAR_NO)           
					 , CARD_NO      = NVL(:V_CARD_NO, CARD_NO)              
				     , YD_STKBED_USG_CD = NVL(:V_STKBED_USG_CD,YD_STKBED_USG_CD)
				WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
			   */
		    	commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStat", logId, methodNm, "TB_YS_STKCOL 등록");				    	    	
    	    	
		    	commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarpoint", logId, methodNm, "Car-Point 등록");
		    	
		    	/******************************************
		    	 * 포인트 구내 운송 으로 전송처리
		    	 ***************************************/
		    	recInTemp1  = JDTORecordFactory.getInstance().create();
		    	recInTemp1.setResultCode(logId);	//Log ID
		    	recInTemp1.setResultMsg(methodNm);	//Log Method Name
		    	recInTemp1.setField("JMS_TC_CD",		"YSTSJ012");
		    	recInTemp1.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
//		    	recInTemp1.setField("YD_GP", 			szYS_STK_COL_GP.substring(0,1));
//		    	recInTemp1.setField("YS_STK_COL_GP", 	szYS_STK_COL_GP);
// 2025.10.01 YSTSJ012 전문 항목 변경
				String szWLOC_CD	= commUtils.trim(recOutTemp.getFieldString("WLOC_CD"	));	// 개소코드
				String szYD_PNT_CD	= commUtils.trim(recOutTemp.getFieldString("YD_PNT_CD"	));	// 야드포인트코드
		    	
		    	recInTemp1.setField("PRSNT_LOC_WLOC_CD", 	szWLOC_CD); 				// 현위치개소코드
		    	recInTemp1.setField("YD_PNT_CD", 			szYD_PNT_CD);				// 야드포인트코드
		    	recInTemp1.setField("YD_PNT_OP_CL_TT", 		commUtils.getDateTime14()); // 야드포인트개폐시각

		    	szOLD_YD_STK_COL_ACT_STAT   = commUtils.trim(recOutTemp.getFieldString("YD_STK_COL_ACT_STAT"       )); 
				
		    	szMsg	= "szYD_STK_COL_ACT_STAT: [" + szYD_STK_COL_ACT_STAT + "  szOLD_YD_STK_COL_ACT_STAT: [" + szOLD_YD_STK_COL_ACT_STAT + "] 비교";
				commUtils.printLog(logId, szMsg, "SL");		
				
				
		    	if(szYD_STK_COL_ACT_STAT.equals ("C") 
						|| szYD_STK_COL_ACT_STAT.equals("L")
						|| szYD_STK_COL_ACT_STAT.equals("R")){
		    		
					if( szOLD_YD_STK_COL_ACT_STAT.equals("N")) {			//사용불가
						recInTemp1.setField("PNT_UNIT_CL_GP",	"C");

					}else{
						recInTemp1.setField("PNT_UNIT_CL_GP",	"O");
						isSendable = false;
					}
				}else if(szYD_STK_COL_ACT_STAT.equals ("N")){
					
					recInTemp1.setField("PNT_UNIT_CL_GP",		"C");
				}		    

		    	szMsg	= "소재차량Point개폐(YSTSJ012) 전문" + recInTemp1.toString();;
				commUtils.printLog(logId, szMsg, "SL");		
		    	
				sndRecord = commUtils.addSndData(sndRecord,recInTemp1);						
		    	
				
		    	if( isSendable ) {
//		    		szYdGp = szYS_STK_COL_GP.substring(0,2);
		    		/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		    		 * 업무기준 : 차량출발시 저장위치 제원 야드L2로 전송
		    		 * 야드BLOOM:N1,BILLET:N2,선재:N3,봉강:N4,선재자동화:N5,봉강자동화:N6 	
			         *         YSN7L301 저장위치제원
				     *         YSN7L302 저장품제원
				     *         YSN7L303 크레인작업지시
				     *         YSN7L304 크레인작업실적응답
		    		 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
					szJMS_TC_CD = "YSN7L301";
					recInTemp  = JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);	//Log ID
					recInTemp.setResultMsg(methodNm);	//Log Method Name
		    		recInTemp.setField("MSG_ID",    szJMS_TC_CD);
					recInTemp.setField("YD_INFO_SYNC_CD", "3");						//1:동,2:SPAN,3:열,4:BED
					recInTemp.setField("YD_GP", szYS_STK_COL_GP.substring(0, 1));
					recInTemp.setField("YS_STK_COL_GP", szYS_STK_COL_GP);
					
					//전송 Data 생성
					sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2(szJMS_TC_CD, recInTemp));

					szMsg="["+methodNm+"] 포인트 개패시 시 저장위치 제원 야드L2로 전송";
					commUtils.printLog(logId, szMsg, "SL");			    		
		    	}
				
			}

			szMsg="[구내내운송 소재차량Point개폐 전송  성공]";
			commUtils.printLog(logId, szMsg, "SL");
	    	
	    	szMsg = "["+methodNm+"] YS_STK_COL_GP["+szYS_STK_COL_GP+"]의 진행상태["+szYD_STK_COL_ACT_STAT+"] 변경처리함";
			commUtils.printLog(logId, szMsg, "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return sndRecord;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * 차량작업 관리- 입동순서 변경처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procGdsBayInWoSeqChang(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-입동지시[SbrYsJspSeEJB.procGdsBayInWoSeqChang] < ";
		String logId = gdReq[0].getRequestUserIp();
		JDTORecord recInTemp = null;
		JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
		int RtnVal = 0;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			String syd_car_sch_id = null;
				
			for(int x = 0; x < gdReq.length; x++){
				for(int i = 1; i <= 5; i++){
					syd_car_sch_id = commUtils.nvl(gdReq[x].getField("YD_CAR_SCH_ID"+i), "");
					
					if(!syd_car_sch_id.equals("")){
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("YD_CAR_SCH_ID"	    ,commUtils.nvl(gdReq[x].getFieldString("YD_CAR_SCH_ID"+i), ""));
						recInTemp.setField("YD_BAYIN_WO_SEQ"	,commUtils.nvl(gdReq[x].getFieldString("YD_BAYIN_WO_SEQ"+i), ""));
						recInTemp.setField("MODIFIER"			,commUtils.nvl(gdReq[0].getFieldString("MODIFIER"), ""));

						/*com.inisteel.cim.ys.common.dao.YsCommDAO.updBayInWoSeqChang
						UPDATE TB_YS_CARSCH
						   SET MOD_DDTT = SYSDATE
						     , MODIFIER = :V_MODIFIER
						     , YD_BAYIN_WO_SEQ = :V_YD_BAYIN_WO_SEQ
						 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  
						*/
						RtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBayInWoSeqChang", logId, methodNm, "차량스케쥴 등록");		
					}
					if (RtnVal < 0) {
						commUtils.printLog(logId, "차량스케쥴 등록 오류", "SL");
					} // end of if
				}	
			}
			commUtils.printLog(logId, methodNm, "S-");
			return sndRecord;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * 이송Lot등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regFtmvLot(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "이송Lot등록[SbrYsJspSeEJB.regFtmvLot] < ";
		String logId = commUtils.getLogId();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq[0].getFieldString("userid")));
			String ydPrepSchId = commDao.getSeqId(logId, methodNm, "PrepSch");
			
			if ("".equals(ydPrepSchId)) {
				throw new Exception("준비스케쥴ID 생성 실패");
			}			

			jrParam.setField("YD_PREP_SCH_ID", ydPrepSchId); //야드준비스케쥴ID
			jrParam.setField("YD_SCH_CD",      gdReq[0].getField("YD_SCH_CD")); //스케줄코드
			jrParam.setField("YD_PREP_WK_ST", commUtils.nvl(gdReq[0].getField("YD_PREP_WK_ST"),"")); //야드준비작업상태 
			
			for (int ii = 0; ii < gdReq.length; ii++) {
				
				//준비재료 등록
				jrParam.setField("SSTL_NO"			, commUtils.nvl(gdReq[ii].getFieldString("SSTL_NO"), "")); 
				jrParam.setField("YS_STK_COL_GP"	, commUtils.nvl(gdReq[ii].getFieldString("YD_STR_LOC"), "").substring(0,6)); 
				jrParam.setField("YS_STK_BED_NO"	, commUtils.nvl(gdReq[ii].getFieldString("YD_STR_LOC"), "").substring(7,8)); 
				jrParam.setField("YS_STK_LYR_NO"	, commUtils.nvl(gdReq[ii].getFieldString("YS_STK_LYR_NO"), "")); 
				jrParam.setField("YS_STK_SEQ_NO"	, commUtils.nvl(gdReq[ii].getFieldString("YS_STK_SEQ_NO"), "")); 
				
				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepMtl", logId, methodNm, "준비재료 등록");
			}
			
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepSch", logId, methodNm, "준비스케줄 등록");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of regFtmvLot
	
	/**
	 * 차량입고LOT등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regCarFtmvLot(GridData gdReq) throws DAOException {
		String methodNm = "차량입고LOT등록[SbrYsJspSeEJB.regCarFtmvLot] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			
			JDTORecord recCarSch        	= null;
			JDTORecord rcvMsgCol			= null;
			JDTORecord rcvMsg				= null;
			
			String szTRN_EQP_CD    			= null;
			String szARR_WLOC_CD			= null;
			String szARR_YD_PNT_CD			= null;
		    String szYD_WBOOK_ID   			= "";
		    String szYD_SCH_CD				= "";
		    String szSSTL_NO				= "";
			
			String szMsg           			= null;
			
			JDTORecordSet rsResult 			= null;
			
			String vBAY_GP = gdReq.getParam("YD_AIM_BAY_GP");
			String ARR_WLOC_CD = "";
			
			switch (vBAY_GP) {
				case "X": // 정정
					ARR_WLOC_CD = "S6Y20";
					break;
				case "H": // 사외통합
					ARR_WLOC_CD = "BSY04";
					break;
			}

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String ydPrepSchId = commDao.getSeqId(logId, methodNm, "PrepSch");
			
			if ("".equals(ydPrepSchId)) {
				throw new Exception("입고이송Lot 준비스케쥴ID 생성 실패");
			}
			
			jrParam.setField("YD_PREP_SCH_ID", 	ydPrepSchId); 										//야드준비스케쥴ID
			jrParam.setField("YD_SCH_CD", 		gdReq.getParam("YD_SCH_CD")); 						//스케줄코드
			jrParam.setField("YD_PREP_WK_ST", 	commUtils.nvl(gdReq.getParam("YD_PREP_WK_ST"),"")); //야드준비작업상태
			jrParam.setField("YD_AIM_BAY_GP", 	gdReq.getParam("YD_AIM_BAY_GP")); 					//목적동
			jrParam.setField("YD_TO_LOC_GUIDE", gdReq.getParam("YD_TO_LOC_GUIDE")); 				//야드To위치Guide
			jrParam.setField("ARR_WLOC_CD", 	ARR_WLOC_CD); 										//착지개소코드

			//등록 할  레코드 수  gdReq.getHeader("CHECK").getRowCount() 결과값 0 막음
//			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			int rowCnt = 100;
			
			szYD_SCH_CD = gdReq.getParam("YD_SCH_CD"); 						//스케줄코드
            szMsg = "\n\t YD_PREP_SCH_ID   	: " 	+ ydPrepSchId 
                  + "\n\t YD_SCH_CD      	: " 	+ szYD_SCH_CD 
                  + "\n\t YD_PREP_WK_ST     : " 	+ commUtils.nvl(gdReq.getParam("YD_PREP_WK_ST"),"") 
                  + "\n\t YD_AIM_BAY_GP   	: " 	+ gdReq.getParam("YD_AIM_BAY_GP") 
                  + "\n\t YD_GP   			: " 	+ gdReq.getParam("YD_GP") 
                  + "\n\t YD_BAY_GP   		: " 	+ gdReq.getParam("YD_BAY_GP") 
                  + "\n\t userid   			: " 	+ gdReq.getParam("userid") 
                  ;

      		commUtils.printLog(logId, szMsg, "");
			
			for (int ii = 0; ii < rowCnt; ii++) {
				szSSTL_NO = "";
				szSSTL_NO = commUtils.getValue(gdReq, "SSTL_NO", ii);
				if (!"".equals(szSSTL_NO)) {
					//준비재료 등록
					jrParam.setField("SSTL_NO"			, commUtils.getValue(gdReq, "SSTL_NO", ii)); 
					jrParam.setField("YD_GP"			, commUtils.getValue(gdReq, "YS_STR_LOC", ii).substring(0,1)); 
					jrParam.setField("YS_STK_COL_GP"	, commUtils.getValue(gdReq, "YS_STR_LOC", ii).substring(0,6)); 
					jrParam.setField("YS_STK_BED_NO"	, commUtils.getValue(gdReq, "YS_STR_LOC", ii).substring(6,8)); 
					jrParam.setField("YS_STK_LYR_NO"	, commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii)); 
					jrParam.setField("YS_STK_SEQ_NO"	, commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii)); 
					
					commDao.insert(jrParam, "com.inisteel.cim.ys.sbr.dao.insPrepMtl", logId, methodNm, "준비재료 등록");
				}
				
			}
			
			commDao.insert(jrParam, "com.inisteel.cim.ys.sbr.dao.insPrepSchAimBay", logId, methodNm, "준비스케줄 등록");
			// com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepSchAimBay
			
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updToLocGuide", logId, methodNm, "야드To위치Guide 입력");
			
// ****************************************
// 2025.08.21 차량 예약 등록 START
// ****************************************
	    	boolean isReqCheck = true; // 차량 예약 등록

			if( isReqCheck ){
				String modifier 			= commUtils.trim(gdReq.getParam("userid"));	//수정자
			    String sYD_SCH_CD 			= ""; 
				String sWLOC_CD				= null;
				String sYD_PNT_CD			= null;
			    String szYD_CAR_SCH_ID 		= "";
				String stlNos 				= "";
				String sYS_STK_COL_GP 		= "";
				String sTRN_EQP_CD			= "";
				
			    int intRtnVal 				= 0 ;
			    
				JDTORecord		recPara					= null;	
				JDTORecord	  	sndRecord	= JDTORecordFactory.getInstance().create();
				GridData inGridData = new GridData();


				sYD_SCH_CD = gdReq.getParam("YD_SCH_CD");
				
				// 차량위치로 TB_YS_STKCOL에서 개소코드와 야드포인트를 읽어온다. 
				jrParam.setField("YS_STK_COL_GP", sYD_SCH_CD.substring(0,6));

				szMsg = "\n\t YS_STK_COL_GP   		: " 	+ sYD_SCH_CD.substring(0,6);

		      	commUtils.printLog(logId, szMsg, "");

				JDTORecordSet jsCol = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getYdStkCol1", logId, methodNm, "적치열 개소코드,포인트 조회");
				// com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkCol1
				
				if(jsCol != null && jsCol.size() > 0) {
					
					sWLOC_CD		= commUtils.trim(jsCol.getRecord(0).getFieldString("WLOC_CD"));
					sYD_PNT_CD		= commUtils.trim(jsCol.getRecord(0).getFieldString("YD_PNT_CD"));
					szYD_CAR_SCH_ID = commDao.getSeqId(logId, methodNm, "CarSch");
					sTRN_EQP_CD		= commUtils.trim(jsCol.getRecord(0).getFieldString("YD_PNT_CD"));
					
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("REGISTER",         modifier);
				    recPara.setField("YD_EQP_WRK_STAT",  "L");                    			// 야드설비작업상태
				    recPara.setField("YD_EQP_ID",        YsConstant.YD_TS_CAR_EQP_ID);		// 야드설비ID
				    recPara.setField("TRN_EQP_CD",       szTRN_EQP_CD);               		// 운송장비코드
				    recPara.setField("YD_CAR_USE_GP",    "L");                              // 차량사용구분(L:구내,G:출하)
				    recPara.setField("SPOS_WLOC_CD",     sWLOC_CD);                			// 발지개소코드
				    recPara.setField("YD_CARLD_LEV_LOC", "");          						// 야드상차출발위치
				    recPara.setField("YD_CARLD_LEV_DT",  commUtils.getDateTime14());   		// 상차출발일시
				    recPara.setField("YD_BAYIN_WO_SEQ",  "99");                    			// 입동지시순번 - WC 수정 : 각강 입고 차량의 경우 입고검수완료 전까지 99로 설정함, 검수 이후 9로 셋팅
				    recPara.setField("YD_CAR_PROG_STAT", YsConstant.YD_CARLD_LEV);          // 상차출발상태
				    recPara.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);

//				    intRtnVal = commDao.insert(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.insYdCarsch", logId, methodNm, "차량스케줄 등록"); // 2025.10.22 차량에 재료가 중복으로 등록되는 현상으로 주석 처리				      
//			        if( intRtnVal <= 0 ){
//			        	szMsg= methodNm + "개소코드["+sWLOC_CD+"] : 차량스케줄["+szYD_CAR_SCH_ID+"] 생성 시 오류발생 - 반환값 : " + intRtnVal;
//			        	commUtils.printLog(logId, szMsg, "SL");
//			        	sndRecord.setTaskCode("-1");
//			        	return sndRecord;
//			        } // 2025.10.29 변수(intRtnVal)가 exption 발생으로 주석처리
					
			        szYD_CAR_SCH_ID = commUtils.trim(recPara.getFieldString("YD_CAR_SCH_ID"));
			        szMsg= methodNm + "개소코드["+sWLOC_CD+"] : 차량스케줄["+szYD_CAR_SCH_ID+"] 생성 완료 - 반환값 : " + intRtnVal;
			        commUtils.printLog(logId, szMsg, "SL");
			      
					for (int ii = 0; ii < rowCnt; ii++) {
						szSSTL_NO = "";
						szSSTL_NO = commUtils.getValue(gdReq, "SSTL_NO", ii);
						if (!"".equals(szSSTL_NO)) {
							
							jrParam = JDTORecordFactory.getInstance().create();
							jrParam.setResultCode(logId);	//Log ID
							jrParam.setResultMsg(methodNm);	//Log Method Name
							jrParam.setField("SSTL_NO", 		commUtils.getValue(gdReq, "SSTL_NO", ii));
							jrParam.setField("LOC"	  , 		commUtils.getValue(gdReq, "YS_STR_LOC", ii)); 
							jrParam.setField("YD_CAR_SCH_ID", 	szYD_CAR_SCH_ID);
							
							// 차량 이송 재료 등록 
//							commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insSbCarmvmtl", logId, methodNm, "차량 이송재료 등록"); // 2025.10.21 차량에 재료가 중복으로 등록되는 현상으로 주석 처리
							
							if(ii != 0) {
								stlNos += ",";
							}
							
							stlNos += szSSTL_NO;	// 재료번호
							sYS_STK_COL_GP = commUtils.getValue(gdReq, "YS_STR_LOC", ii);
						}
						
					}
			     
					// 작업예약등록
					inGridData.addParam("SSTL_NOS", 		stlNos);						// 재료번호들
					inGridData.addParam("YS_STK_COL_GP", 	sYS_STK_COL_GP);				// 야드적치열구분(6자리 이상)
					inGridData.addParam("YD_TO_LOC_GUIDE", 	sYD_SCH_CD.substring(0,6));		// 야드To위치Guide
					inGridData.addParam("YD_WRK_CRN", 		"");							// 야드작업크레인(작업자지정 크레인)
					inGridData.addParam("YD_PREP_SCH_ID", 	ydPrepSchId);					// 야드준비스케쥴ID(차량상차작업예약ID)
					inGridData.addParam("userid", 			modifier);						// 수정자
					
					szMsg = "\n\t SSTL_NO   		: " 	+ stlNos 
	                	  + "\n\t YS_STK_COL_GP     : " 	+ sYS_STK_COL_GP
	                	  + "\n\t YD_TO_LOC_GUIDE     : " 	+ sYD_SCH_CD.substring(0,6)
	                	  ;

		      		commUtils.printLog(logId, szMsg, "");
					
					jrRtn = updbtMvStkWrkBook(inGridData);
					
				}			
				
			}
			
// ****************************************
// 2025.08.21 차량 예약 등록 END
// ****************************************
			
			//차량이 있을 경우 2번 만들어지는 현상으로 주석처리(2025.11.25)
//			//이송LOT를 생성한 동에 도착한 이송차량이 있는지 체크 (차량스케줄에 위치가 이송LOT생성한 동이고 상차도착상태에 작업예약이 없는 스케줄)
//			jrParam.setField("YD_GP"		, gdReq.getParam("YD_GP")); //야드구분
//			jrParam.setField("YD_BAY_GP"	, gdReq.getParam("YD_BAY_GP")); //동구분
//			
//			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchArrNoJob", logId, methodNm, "작업예약 없는 도착차량 조회");
//
//			if (jsCarSch != null && jsCarSch.size() > 0) {
//			
//				jsCarSch.first();
//				rcvMsg = jsCarSch.getRecord(); //상차도착전문 정보를 담는다..  	
//				
//				szTRN_EQP_CD 		= rcvMsg.getFieldString("TRN_EQP_CD");
//				szARR_WLOC_CD		= rcvMsg.getFieldString("ARR_WLOC_CD");
//				szARR_YD_PNT_CD		= rcvMsg.getFieldString("ARR_YD_PNT_CD");
//				
//
//				//운송장비코드로 차량스케줄 조회 --------------------------------------------------------------------------------	    
//				jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD);
//		    	
//				rsResult = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschDaoTrnEqpCd", logId, methodNm, "차량스케줄을 조회"); 	
//		    	
//				if (rsResult == null || rsResult.size() < 0) {
//					szMsg="["+methodNm+"] 이송Lot생성 후 차량스케줄 조회시 운송장비코드["+szTRN_EQP_CD+"] : parameter error";
//					commUtils.printLog(logId, szMsg, "SL");
//					throw new Exception(szMsg);
//				} else if (rsResult.size() > 1) {
//					szMsg= "[" + methodNm + "] 이송Lot생성 후 차량스케줄 조회 시 오류발생 - 운송장비코드로 차량스케줄이 여러건["+rsResult.size()+"]이 존재합니다.";
//					commUtils.printLog(logId, szMsg, "SL");
//					throw new Exception(szMsg);
//				}
//		    	
//		    	rsResult.first();
//		    	recCarSch = rsResult.getRecord(); 
//
//
//		    	//착지개소코드와 착지야드포인트코드로 적치열을 조회한다. ------------------------------------------------------------
//		    	jrParam.setField("WLOC_CD",   szARR_WLOC_CD);
//		    	jrParam.setField("YD_PNT_CD", szARR_YD_PNT_CD);
//
//		    	rsResult = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolWLocCdandPntCd", logId, methodNm, "적치열 조회"); 
//		    	
//		    	if (rsResult == null || rsResult.size() <= 0) {
//		    		szMsg="["+methodNm+"] 수신된 착지개소코드["+szARR_WLOC_CD+"]와 수신된 착지야드포인트코드["+szARR_YD_PNT_CD+"] 적치열 조회 시 적치열이 존재하지 않습니다.";
//		    		commUtils.printLog(logId, szMsg, "SL");
//					throw new Exception(szMsg);
//		    	}
//		    	
//		    	rsResult.first();
//		    	rcvMsgCol = rsResult.getRecord();
//		    	
//	    		if(commUtils.trim(rcvMsgCol.getFieldString("YD_STK_COL_ACT_STAT")).equals("N")) {
//	    			szMsg="["+methodNm+"] 차량정지위치가 사용 불가상태입니다.";
//		    		commUtils.printLog(logId, szMsg, "SL");
//					throw new Exception(szMsg);
//		    	}
//		    	
//		    	
//		    	//작업예약정보에서 --------------------------------------------------------------------------------------------
//		    	//운송장비코드 , 야드차량사용구분으로  조회 
//		    	//해당된 작업예약 재료 정보를 가지고 온다
//	    		jrParam.setField("TRN_EQP_CD",    szTRN_EQP_CD);
//		    
//	    		rsResult = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookMtlbyCarUsrGpTrnEqpCd", logId, methodNm, "작업예약을 조회"); 
//				
//		    	if (rsResult == null || rsResult.size() < 0 ) {
//					szMsg="["+methodNm+"] 운송장비코드["+szTRN_EQP_CD+"]로 작업예약 조회 시 : parameter error";
//					commUtils.printLog(logId, szMsg, "SL");
//					throw new Exception(szMsg);
//				} else if (rsResult.size() == 0 ){
//					
//				} else {
//					
//			    	rsResult.first();
//			    	JDTORecord recOutTemp = rsResult.getRecord();
//			    	
//					szYD_WBOOK_ID	= commUtils.trim(recOutTemp.getFieldString("YD_WBOOK_ID")); 
//					szYD_SCH_CD    	= commUtils.trim(recOutTemp.getFieldString("YD_SCH_CD")); 
//				}
//	    		
//	    		rcvMsg.setField("YD_CARLD_WRK_BOOK_ID"	, szYD_WBOOK_ID);
//	    		rcvMsg.setField("YD_SCH_CD"				, szYD_SCH_CD);
//	    		
//	    		//소재차량 공차도착 실적 호출
//				EJBConnector ejbConn = new EJBConnector("default", "YsCommCarTSMvSeEJB", this);
//				
//				jrRtn = (JDTORecord)ejbConn.trx("procLDMatlCarArr", new Class[] { String.class, JDTORecord.class, JDTORecord.class, JDTORecord.class }, new Object[] { logId, rcvMsg, recCarSch , rcvMsgCol });
//	    		
//			} else {
//				
//				//상차위치가 Lot편성 동과 같고 작업예약 없는 상차출발차량 조회 : 이 차량이 들어올 차량임으로 포인트 지시를 할 필요 없음  
//				jrParam.setField("YD_GP"		, gdReq.getParam("YD_GP")); //야드구분
//				jrParam.setField("YD_BAY_GP"	, gdReq.getParam("YD_BAY_GP")); //동구분
//				
//				jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchStartToLd", logId, methodNm, "상차위치 있고 작업예약 없는 상차출발차량 조회");
//				
//				if (jsCarSch.size() == 0) {
//					
//					//차량스케줄 중에 상차출발이면서 도착포인트가 없는 차량이 있으면 포인트 지시 처리
//					jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchNoLdStopLoc", logId, methodNm, "상차위치 없는 상차출발차량 조회");
//					
//					if (jsCarSch != null && jsCarSch.size() > 0) {
//						
//						jsCarSch.first();
//						rcvMsg = jsCarSch.getRecord(); 
//						
//						String sTRN_EQP_CD 		= rcvMsg.getFieldString("TRN_EQP_CD");
//						String sYD_CAR_SCH_ID	= rcvMsg.getFieldString("YD_CAR_SCH_ID");
//						String sYD_GP			= gdReq.getParam("YD_GP");
//// 2025.08.22 기존 로직 수정						
////						String sTO_LOC 			= gdReq.getParam("YD_GP") + gdReq.getParam("YD_BAY_GP") + "TR1";
//						String sTO_LOC 			= szYD_SCH_CD.substring(0,6); 
//						
//						String sWLOC_CD		= null;
//						String sYD_PNT_CD	= null;
//						
//						String modifier 			= commUtils.trim(gdReq.getParam("userid"));	//수정자
//						
//						JDTORecord jrYdMsg 			= commUtils.getParam(logId, methodNm, modifier);
//						jrYdMsg.setResultCode(logId);	//Log ID
//						jrYdMsg.setResultMsg(methodNm);	//Log Method Name
//						
//						String currDate 			= commUtils.getDateTime14();	//현재시각
//						String sTRN_WRK_FULLVOID_GP = "E";	//공차
//						String sSPOS_WLOC_CD		= "";
//						String sYD_PNT_CD1			= "";
//						String sYD_CARLD_STOP_LOC	= "";
//						String sARR_WLOC_CD			= "";
//						String sYD_PNT_CD3			= "";
//						String sYD_CARUD_STOP_LOC	= "";
//						
//						//-------------------------------------------------------------------------------------------
//						//소재차량Point지시 
//
//						
//						//목표지위치로 TB_YS_STKCOL에서 개소코드와 야드포인트를 읽어온다. 
//						jrParam.setField("YS_STK_COL_GP", sTO_LOC);
//
//						szMsg = "\n\t YS_STK_COL_GP   		: " 	+ sTO_LOC;
//
//				      	commUtils.printLog(logId, szMsg, "");
//						
//						JDTORecordSet jsCol = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkCol1", logId, methodNm, "적치열 개소코드,포인트 조회");
//						
//						if(jsCol != null && jsCol.size() > 0) {
//							sWLOC_CD	= commUtils.trim(jsCol.getRecord(0).getFieldString("WLOC_CD"));
//							sYD_PNT_CD	= commUtils.trim(jsCol.getRecord(0).getFieldString("YD_PNT_CD"));
//							
//							if("".equals(sWLOC_CD) || "".equals(sYD_PNT_CD)) {
//								
//								throw new Exception(sTO_LOC + " 의 개소코드 또는 야드포인트에 NULL 값이 있습니다.");
//							}
//							
//							if(!"".equals(commUtils.trim(jsCol.getRecord(0).getFieldString("TRN_EQP_CD")))) {
//								
//								throw new Exception(sTO_LOC + " 에 이미 " + commUtils.trim(jsCol.getRecord(0).getFieldString("TRN_EQP_CD")) + " 운송장비가 점유하고  있습니다.");
//							}
//							
//						} else {
//							throw new Exception(sTO_LOC + " 의 개소코드와 야드포인트를 TB_YS_SCKCOL 에서 찾지 못했습니다.");
//						}
//						
//						// YSTSJ011(소재차량Point지시)
//						jrYdMsg.setField("JMS_TC_CD"         	, YsConstant.YSTSJ011);
//						jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, currDate    ); //JMSTC생성일시
//						jrYdMsg.setField("TRN_EQP_CD"        	, sTRN_EQP_CD); //운송장비코드
//						jrYdMsg.setField("WLOC_CD"     	 		, sWLOC_CD);
//						jrYdMsg.setField("YD_PNT_CD"     		, sYD_PNT_CD); 
//						jrYdMsg.setField("PNT_WO_GP"     		, "A"    	);
//						jrYdMsg.setField("PNT_WO_DT"     		, currDate ); 
//// 2025.08.19 YD_MSG_NM(야드메시지) 추가	
//						jrYdMsg.setField("YD_MSG_NM"			, "특수강정정야드 -> 현황관리 -> 저장위치별 현황 -> 차량이송LOT 실행. 사용자[" + modifier + "]");
//						
//						//전송할 전문에 추가
//						jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
//						
//						String sYD_CARLD_PNT_WO_DT = "";
//						String sYD_CARUD_PNT_WO_DT = "";
//
//						//차량스케줄의 개소코드, 야드포인트, 정지위치를 UDPATE 한다.
//						if("E".equals(sTRN_WRK_FULLVOID_GP)) { //공차:상차
//							sSPOS_WLOC_CD 		= sWLOC_CD;
//							sYD_PNT_CD1			= sYD_PNT_CD;	
//							sYD_CARLD_STOP_LOC 	= sTO_LOC;
//							sYD_CARLD_PNT_WO_DT = currDate;
//						} else { //영차:하차
//							sARR_WLOC_CD 		= sWLOC_CD;
//							sYD_PNT_CD3			= sYD_PNT_CD;	
//							sYD_CARUD_STOP_LOC 	= sTO_LOC;
//							sYD_CARUD_PNT_WO_DT = currDate;
//						}
//						
//						jrParam.setField("YD_CAR_PROG_STAT"		, "");  //""이면 이전 상태 유지된다.
//						jrParam.setField("SPOS_WLOC_CD"			, sSPOS_WLOC_CD);
//						jrParam.setField("YD_CARLD_PNT_WO_DT"	, sYD_CARLD_PNT_WO_DT);
//						jrParam.setField("YD_PNT_CD1"			, sYD_PNT_CD1);
//						jrParam.setField("YD_CARLD_STOP_LOC"	, sYD_CARLD_STOP_LOC); 
//						jrParam.setField("ARR_WLOC_CD"			, sARR_WLOC_CD);
//						jrParam.setField("YD_CARUD_PNT_WO_DT"	, sYD_CARUD_PNT_WO_DT);
//						jrParam.setField("YD_PNT_CD3"			, sYD_PNT_CD3);
//						jrParam.setField("YD_CARUD_STOP_LOC"	, sYD_CARUD_STOP_LOC); 
//						jrParam.setField("YD_CAR_SCH_ID"		, sYD_CAR_SCH_ID); 
//						
//						//이송차량스케줄 수정 - 차량포인트 수정
//						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updMvCarSchPntWo", logId, methodNm, "차량포인트 지시 수정");
//						
//						
//						jrParam.setField("YD_GP"				, sYD_GP);
//						jrParam.setField("TRN_EQP_CD"			, sTRN_EQP_CD);
//						jrParam.setField("YS_STK_COL_GP"		, sTO_LOC);
//						
//						//적치열 포인트지시 예약하기
//						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkColPntWo", logId, methodNm, "적치열 포인트지시 예약하기");
//						
//						//TB_YD_CARPOINT 포인트지시 예약하기 
//						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCarPointPntWo", logId, methodNm, "TB_YD_CARPOINT 포인트지시 예약하기");
//						
//						if("E".equals(sTRN_WRK_FULLVOID_GP)) { //공차:상차
//
//							jrParam.setField("YD_GP", sYD_GP);
//							jrParam.setField("YD_SCH_CD", sTO_LOC.substring(0,2) + "TR___M");
//							
//							JDTORecordSet jsPrepSch = commDao.select(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.getPrepSchWithOutTrnEqpCd", logId, methodNm, "예약 안걸린 이송LOT ID 조회");
//
//							if(jsPrepSch != null && jsPrepSch.size() > 0) {
//								
//								String sYD_PREP_SCH_ID	= commUtils.trim(jsPrepSch.getRecord(0).getFieldString("YD_PREP_SCH_ID"));
//								
//								jrParam.setField("TRN_EQP_CD"			, sTRN_EQP_CD);
//								jrParam.setField("YD_PREP_SCH_ID"		, sYD_PREP_SCH_ID);
//								
//								//TB_YS_PREPSCH 이송LOT 예약하기
//								commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updPreSchYdToLocGuide", logId, methodNm, "이송LOT 예약하기");
//							}		
//						}
//						//-------------------------------------------------------------------------------------------
//					}
//
//					
//				}
//			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of regCarFtmvLot
	
	/**
	 * GridData - 차량상차정보조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getgdsCarldInfoInqjl(JDTORecord recPara) throws DAOException {
		String methodNm = "차량상차정보조회 [SbrYsJspSeEJB.getgdsCarldInfoInqjl] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();
		String szCarProgStat = null;
		JDTORecordSet jsTcar = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		try {
			commUtils.printLog(logId, methodNm, "S+", recPara);
			commUtils.printParam("recPara", recPara);
			
			// 기본정보조회 
//			recPara.setField("CAR_NO", commUtils.nvl(recPara.getFieldString("CAR_NO"), ""));
//			recPara.setField("TRN_EQP_CD", commUtils.nvl(recPara.getFieldString("CAR_NO"), ""));
			
//			JDTORecordSet jsCrn = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCarSch", logId, methodNm, "TB_YS_CARSCH");
//			if (jsCrn == null || jsCrn.size() <= 0) {
//				throw new Exception("차량스케줄에서 조회시 에러..스케줄 정보가 존재하지 않습니다.");
//			}
//
//			JDTORecord jrCrn = jsCrn.getRecord(0);

			// 차량 진행 상태 코드 값이 '1','2',(상차출발, 상차도착) 인 경우
			szCarProgStat = commUtils.trim(recPara.getFieldString("YD_CAR_PROG_STAT")); // 야드작업진행상태

			// ******************************
			// 2개의 쿼리는 컬럼명을 동일하게 하여 읽어올수 있도록 한다.
			// ******************************
			if ("1".equals(szCarProgStat) || "2".equals(szCarProgStat)) {
				// 차량 스케줄에 상차 작업예약 ID 로 작업예약 재료 정보 조회를 한다.
				if (commUtils.trim(recPara.getFieldString("YD_CARLD_WRK_BOOK_ID")).equals("")) {
					// throw new Exception("작업예약 ID가 없습니다( 차량진도코드가 : 1, 2 경우)");
					commUtils.printLog(logId, methodNm, "S-");
					return jsTcar;
				} else {
					jsTcar = commDao.select(recPara, getCarldInfoInqjlByYdWrkBook, logId, methodNm, "차량번호로 작업예약 조회");
				}
			} else {
				// 차량 진행 상태 코드값이 그 이외인 경우 는 차량 이송재료 정보를 읽어온다.
				recPara.setField("YD_CAR_SCH_ID", commUtils.trim(recPara.getFieldString("YD_CAR_SCH_ID")));
				jsTcar = commDao.select(recPara, getCarldInfoInqjlByCarFtmvMtl, logId, methodNm, "차량 스케줄번호로 차량이송재료 조회");
			}

			if (jsTcar == null || jsTcar.size() == 0) {
			}
			// 데이터 존재시 첫번째 레코드 위치에 차량진도코드를 보내준다.
			// jsTcar.first();
			// recCarProgStat = jsTcar.getRecord(0);
			// recCarProgStat.setField("CAR_PROG_STAT", szCarProgStat);
			commUtils.printLog(logId, methodNm, "S-");

			return jsTcar;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}// end of getgdsCarldInfoInqjl
	
	
	/**
	 * [A] 오퍼레이션명 : 차량상차정보 조회 - 차상위치 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updgdsCarldInfoInqjl(GridData gdReq) throws DAOException {
		String methodNm = "차량상차정보 조회 - 차상위치 수정[SbrYsJspSeEJB.updgdsCarldInfoInqjl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					// 차량상세 수정
					jrParam.setField("SSTL_NO", commUtils.getValue(gdReq, "SSTL_NO", ii));
					jrParam.setField("YD_CAR_SCH_ID", commUtils.getValue(gdReq, "YD_CAR_SCH_ID", ii));
					jrParam.setField("YS_STK_COL_GP", commUtils.getValue(gdReq, "YS_STK_COL_GP", ii));
					jrParam.setField("YS_STK_BED_NO", commUtils.getValue(gdReq, "YS_STK_BED_NO", ii)); // 차상위치
					jrParam.setField("YS_STK_LYR_NO", commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii)); // 단
					
					commUtils.printParam("jrParam" + Integer.toString(ii), jrParam);

					// 차량재료정보 수정
					commDao.update(jrParam, udtWhsPlnInfojlByCarFtmvMtl, logId, methodNm, "TB_YS_CARFTMVMTL");

					// 기존위치 CLEAR
					commDao.update(jrParam, udtWhsPlnInfojlByStkLyrLn, logId, methodNm, "TB_YS_STKLYR");

					// 차량위치 등록
					commDao.update(jrParam, udtWhsPlnInfojlByCarStkLyr, logId, methodNm, "TB_YS_STKLYR");

					// 차량재료정보 수정
					commDao.update(jrParam, udtWhsPlnInfojlByCarFtmvMtl, logId, methodNm, "TB_YS_CARFTMVMTL");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}// end of updgdsCarldInfoInqjl
	
	/**
	 * 차량작업관리 > 배차내역 - 차량회송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord doDelCarSch(GridData gdReq) throws DAOException {
		
		String methodNm = "빌렛차량작업 관리-차량회송처리[SbrYsJspSeEJB.doDelCarSch] < " + gdReq.getNavigateValue();
		String logId 			= gdReq.getIPAddress();
		String szMsg 			= null;
		String szARR_YD_PNT_CD 	= null;
		String szCurrDate 		= commUtils.getCurDate("yyyyMMddHHmmss");
		int intRtnVal 			= 0;
		
		JDTORecord sndRecord	= JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);	
			
			String szYD_CAR_SCH_ID 		= commUtils.getValue(gdReq, "YD_CAR_SCH_ID", 0);
			String szYD_WBOOK_ID 		= commUtils.getValue(gdReq, "YD_WBOOK_ID", 0);
			String szYD_CRN_SCH_ID 		= commUtils.getValue(gdReq, "YD_CRN_SCH_ID", 0);
			String szARR_WLOC_CD 		= commUtils.getValue(gdReq, "ARR_WLOC_CD", 0);
			String szTRN_EQP_CD 		= commUtils.getValue(gdReq, "TRN_EQP_CD", 0);
			String szYD_CAR_STOP_LOC 	= commUtils.getValue(gdReq, "YD_CAR_STOP_LOC", 0);
			String szYD_CAR_PROG_STAT 	= commUtils.getValue(gdReq, "YD_CAR_PROG_STAT", 0);
			String szYD_CAR_USE_GP 		= commUtils.getValue(gdReq, "YD_CAR_USE_GP", 0);
			
			szMsg = "차량 STATUS["+szYD_CAR_PROG_STAT+"],차량스케줄ID["+szYD_CAR_SCH_ID+"], 작업예약ID["+szYD_WBOOK_ID+"], 스케쥴ID["+szYD_CRN_SCH_ID+"], 운송장비코드["+szTRN_EQP_CD+"], 착지개소코드["+szARR_WLOC_CD+"]";
			
			commUtils.printLog(logId, szMsg, "SL");
			
			JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
			
			if("B".equals(szYD_CAR_PROG_STAT)||
			   "C".equals(szYD_CAR_PROG_STAT)||
			   "D".equals(szYD_CAR_PROG_STAT)||
			   "E".equals(szYD_CAR_PROG_STAT)){
				/**********************************************************
		    	 * 1. 출발야드 적치열 -> 비활성상태(C) 로 업데이트
		    	 **********************************************************/
				szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CAR_STOP_LOC+"]을 비활성상태로 변경처리 시작 ";
				commUtils.printLog(logId, szMsg, "SL");
				
		    	recInTemp.setField("YS_STK_COL_GP",        szYD_CAR_STOP_LOC);
		    	recInTemp.setField("YD_STK_COL_ACT_STAT",  "C");
		    	recInTemp.setField("YD_CAR_USE_GP",        "");
		    	recInTemp.setField("TRN_EQP_CD",           "");
		    	recInTemp.setField("CAR_NO",               "");
		    	recInTemp.setField("CARD_NO",              "");
		    	recInTemp.setField("MODIFIER", 			   commUtils.trim(gdReq.getParam("userid")));
		    	
		    	intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStatClear", logId, methodNm, "TB_YS_STKCOL 등록");
								
				/**********************************************************
		    	 * 2. 차량포인트통합관리 
		    	 **********************************************************/
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YS_STK_COL_GP", szYD_CAR_STOP_LOC);
				recInTemp.setField("STAT", "C");
				
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointstackcolgpupdateCT", logId, methodNm, "저장위치로 초기화 하는 경우(구내운송)");
				
				/**********************************************************
		    	 * 3. 출발야드 적치베드 -> 야드적치베드활성상태(=C(비활성상태), YD_STK_BED_ACT_STAT) 
		    	 *                     및 BED중량MAX(=기본값, YD_STK_BED_WT_MAX) 으로 업데이트
		    	 **********************************************************/
				szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CAR_STOP_LOC+"]의 베드를 비활성상태와 BED중량MAX기본값으로 변경처리 시작 ";
				commUtils.printLog(logId, szMsg, "SL");
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_BED_WT_MAX", YsConstant.YD_STK_BED_WT_MAX_DEFAULT);
				recInTemp.setField("YS_STK_COL_GP", szYD_CAR_STOP_LOC);
				recInTemp.setField("YD_STK_BED_ACT_STAT", "C");
				
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedYdStkColGp", logId, methodNm, "TB_YS_STKBED 등록");
				
				/**********************************************************
		    	 * 4. 출발야드 적치단 -> 야드적치단활성상태(=C(비활성상태), YD_STK_LYR_ACT_STAT) 
		    	 *                   및 야드적치단재료상태(=E(적치가능), YD_STK_LYR_MTL_STAT) 로 업데이트
		    	 **********************************************************/
				szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CAR_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 시작 ";
				commUtils.printLog(logId, methodNm, "SL");
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setResultCode(logId);	//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				recInTemp.setField("YS_STK_COL_GP", szYD_CAR_STOP_LOC);
				recInTemp.setField("YD_STK_LYR_ACT_STAT", "C");
				recInTemp.setField("SSTL_NO", "");
				recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
				
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "TB_YS_STKLYR 등록");
				
			}
			
			/**********************************************************
			* 5. 기존 이송차량스케줄/재료 삭제
			**********************************************************/
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);

			//이송차량재료 초기화
			commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSchMtl", logId, methodNm, "이송차량재료 초기화");

			//이송차량스케줄 초기화
			commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch", logId, methodNm, "이송차량스케줄 초기화");
			
			
			/**********************************************************
	    	 * 6. 빌렛옥외야드(L2)이송시 송신
	    	 **********************************************************/
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			recInTemp.setField("YD_INFO_SYNC_CD", "7" );
			
			//전송 Data 생성
			sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YSN8L002", recInTemp));
			
           /**********************************************************
			* 7.소재차량회송 하차완료 전송 시작
			**********************************************************/
			sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL3("YSTSJ016", recInTemp));
						
			commUtils.printLog(logId, methodNm, "S-");
				
			return sndRecord;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end doDelCarSch
	
	/**
	 * 차량작업 포인트 현황- 차량출발
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procLeaveCar(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-차량출발[SbrYsJspSeEJB.procLeaveCar] < ";
		String logId = gdReq[0].getRequestUserIp();
		String szMsg = null;
		String szYD_CAR_SCH_ID		= null;
		String szYD_CAR_PROG_STAT	= null;
		String szCAR_NO				= null;
		String szCARD_NO			= null;
		String szSPOS_WLOC_CD		= null;
		String szYD_PNT_CD			= null;
		String szTRANS_ORD_DATE		= null;
		String szTRANS_ORD_SEQNO	= null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			//Return Value
			JDTORecord jrRtn = null;
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq[0].getFieldString("userid")));
			
			for (int ii = 0; ii < gdReq.length; ii++) {
				
				szYD_CAR_SCH_ID = commUtils.nvl(gdReq[ii].getFieldString("YD_CAR_SCH_ID"), "");
				
				
				//--------------------------------------------------------------------------------
				//	차량스케줄ID로 차량스케줄 조회
				//--------------------------------------------------------------------------------
				
			
				JDTORecord recTemp			= JDTORecordFactory.getInstance().create();
				jrParam.setField("YD_CAR_SCH_ID", 			szYD_CAR_SCH_ID);
				
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch
				SELECT *
				FROM TB_YS_CARSCH C
				WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/
				//차량스케쥴 조회
				JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch", logId, methodNm, "차량스케쥴 조회");

				int rowCnt1 = jsCarSch.size();

				if (rowCnt1 <= 0) {
					commUtils.printLog(logId, "차량스케줄이 업습니다. SKIP", "SL");
					continue;
				}				
				
				jsCarSch.first();
				recTemp		= jsCarSch.getRecord();
				
				szYD_CAR_PROG_STAT		= commUtils.trim(recTemp.getFieldString("YD_CAR_PROG_STAT"));
				
				if( !szYD_CAR_PROG_STAT.equals(YdConstant.YD_CARLD_CMPL)) {
					szMsg = "["+methodNm+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량진행상태["+szYD_CAR_PROG_STAT+"]가 상차완료가 아니므로 SKIP시킴";
					commUtils.printLog(logId, szMsg, "SL");
					continue;
				}
				
				szCAR_NO				=  commUtils.trim(recTemp.getFieldString("CAR_NO"));
				szCARD_NO				=  commUtils.trim(recTemp.getFieldString("CARD_NO"));
				szSPOS_WLOC_CD			=  commUtils.trim(recTemp.getFieldString("SPOS_WLOC_CD"));
				szYD_PNT_CD				=  commUtils.trim(recTemp.getFieldString("YD_PNT_CD1"));
				szTRANS_ORD_DATE		=  commUtils.trim(recTemp.getFieldString("TRANS_ORD_DATE"));
				szTRANS_ORD_SEQNO		=  commUtils.trim(recTemp.getFieldString("TRANS_ORD_SEQNO"));
				
				
				//--------------------------------------------------------------------------------
				
				szMsg = "차량스케줄["+szYD_CAR_SCH_ID+"]의 차량출발 처리 EJB 호출";
				commUtils.printLog(logId, szMsg, "SL");
				
				JDTORecord recInTemp			= JDTORecordFactory.getInstance().create();
				recInTemp.setResultCode(logId);	//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name				
				recInTemp.setField("CARD_NO", 				szCARD_NO);
				recInTemp.setField("CAR_NO", 				szCAR_NO);			
				recInTemp.setField("SPOS_WLOC_CD", 			szSPOS_WLOC_CD);
				recInTemp.setField("SPOS_YD_PNT_CD", 		szYD_PNT_CD);
				recInTemp.setField("TRANS_ORD_DATE", 		szTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
				recInTemp.setResultCode(logId);		//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				
				EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
				jrRtn = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
				
				//ydDelegate.sendMsg(recInTemp);
			
				szMsg = "차량스케줄["+szYD_CAR_SCH_ID+"]의 차량출발 처리 EJB 호출완료";
				commUtils.printLog(logId, szMsg, "SL");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end procLeaveCar
	
	/**
	 * 차량작업관리 > 배차내역 - 하차완료처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord procCarUd(GridData gdReq) throws DAOException {
		String methodNm = "차량작업관리화면 하차완료처리[BtYsJspSeEJB.procCarUd] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String szARR_YD_PNT_CD = null;
		String szCurrDate = commUtils.getCurDate("yyyyMMddHHmmss");
		JDTORecord sndRecord	= JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			int intRtnVal = 0;
	
			//DAO Parameter - Log ID, Method, 수정자 Set
			
			commUtils.trim(gdReq.getParam("SSTL_NOS"));
			String szYD_CAR_SCH_ID 		= commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID")	);
			String szYD_WBOOK_ID 		= commUtils.trim(gdReq.getParam("YD_WRK_BOOK_ID")	);
			String szARR_WLOC_CD 		= commUtils.trim(gdReq.getParam("ARR_WLOC_CD")		);
			String szTRN_EQP_CD 		= commUtils.trim(gdReq.getParam("TRN_EQP_CD")		);
			String szYD_CAR_STOP_LOC 	= commUtils.trim(gdReq.getParam("YD_CAR_STOP_LOC")	);
			String szYD_CAR_USE_GP 		= commUtils.trim(gdReq.getParam("YD_CAR_USE_GP")	);
			String szYD_WBOOK_ID2		= commUtils.trim(gdReq.getParam("YD_WBOOK_ID")		);
			
//			if( !szYD_CAR_STOP_LOC.equals("") ) {
//				szYD_GP = szYD_CAR_STOP_LOC.substring(0, 1);
//			}
			
			
			szMsg = "차량스케줄ID["+szYD_CAR_SCH_ID+"], 작업예약ID["+szYD_WBOOK_ID+"], 운송장비코드["+szTRN_EQP_CD+"], 착지개소코드["+szARR_WLOC_CD+"]";
			commUtils.printLog(logId, szMsg, "SL");
			
			//------------------------------------------------------------------------------------------------------
			//	차량스케줄 조회 후 차량진행상태 확인 시작 - 다른유저에 의해서 상태가 변경될 수 있으므로 먼저 상태를 확인 필요
			//	차량스케줄 조회
			//------------------------------------------------------------------------------------------------------
			szMsg = "차량스케줄ID["+szYD_CAR_SCH_ID+"]로 조회 전";
			commUtils.printLog(logId, szMsg, "SL");
			
			JDTORecord recTemp	= JDTORecordFactory.getInstance().create();
			JDTORecord recPara	= JDTORecordFactory.getInstance().create();
			JDTORecord recStkCol= JDTORecordFactory.getInstance().create();
			//1. 차량스케줄을 먼저 조회해서 존재하는 지를 확인
			recTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch
			SELECT *
			FROM TB_YS_CARSCH C
			WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			*/
			//차량스케쥴 조회
			JDTORecordSet jsCarSch = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch", logId, methodNm, "차량스케쥴 조회");
			if (jsCarSch.size() <= 0) {
				throw new Exception( "차량스케줄ID["+szYD_CAR_SCH_ID+"]이 존재하지 않습니다");
			}				
		
			jsCarSch.first();
			recPara = jsCarSch.getRecord();

			String szYD_CAR_PROG_STAT = commUtils.trim(recPara.getFieldString("YD_CAR_PROG_STAT"          ));	//차량진행상태
			szMsg = "차량스케줄ID["+szYD_CAR_SCH_ID+"]로 야드차량진행상태["+szYD_CAR_PROG_STAT+"]";
			commUtils.printLog(logId, szMsg, "SL");

			if( !szYD_CAR_PROG_STAT.equals("B") && !szYD_CAR_PROG_STAT.equals("C")) {
				throw new Exception( "차량스케줄["+szYD_CAR_SCH_ID+"]의 야드차량진행상태["+szYD_CAR_PROG_STAT+"]가 하차완료[하차완료가능상태 : 하차도착(B), 하차검수(C)]할 수 있는 상태가 아닙니다.");
			}
			
			szMsg = "차량스케줄["+szYD_CAR_SCH_ID+"]의 야드차량진행상태["+szYD_CAR_PROG_STAT+"]가 하차완료처리가능한 상태입니다.";
			commUtils.printLog(logId, szMsg, "SL");

			//------------------------------------------------------------------------------------------------------
			// 차량스케줄의 차량진행상태를 하차완료로 변경 - 삭제처리를 하지 않음
			//------------------------------------------------------------------------------------------------------
			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID"	, szYD_CAR_SCH_ID);			//차량스케줄ID
			recPara.setField("YD_EQP_WRK_STAT"	, "U");						//야드설비작업상태
			recPara.setField("YD_CARUD_ST_DT"	, szCurrDate);				//하차개시일시
			recPara.setField("YD_CARUD_CMPL_DT"	, szCurrDate);				//하차완료일시
			recPara.setField("YD_CAR_PROG_STAT"	, "E");						//차량진행상태 : 하차완료[E]
			recPara.setField("MODIFIER"			, commUtils.trim(gdReq.getParam("userid")));					//수정자
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarSchCarWrkDn 
			UPDATE TB_YS_CARSCH
			   SET MODIFIER     = :V_MODIFIER
			     , MOD_DDTT     = SYSDATE
			     , YD_EQP_WRK_STAT = NVL(:V_YD_EQP_WRK_STAT,YD_EQP_WRK_STAT)
			     , YD_CARUD_ST_DT= NVL(:V_YD_CARUD_ST_DT,YD_CARUD_ST_DT)
			     , YD_CARUD_CMPL_DT= NVL(:V_YD_CARUD_CMPL_DT,YD_CARUD_CMPL_DT)
			     , YD_CAR_PROG_STAT= NVL(:V_YD_CAR_PROG_STAT,YD_CAR_PROG_STAT)
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			 */
			intRtnVal = commDao.update(recPara, "com.inisteel.cim.ys.sbr.dao.updYdCarSchCarWrkDn", logId, methodNm, "차량스케줄 갱신");
			
			if( intRtnVal == 0 ) {
				throw new Exception( "차량스케줄["+szYD_CAR_SCH_ID+"]에 하차개시일시, 하차완료일시, 차량진행상태[하차완료-E]를 업데이트시 차량스케줄이 존재하지 않습니다");
			}
			
			//------------------------------------------------------------------------------------------------------
			// 1. 차량 이송재료를 조회 후 삭제처리
			//------------------------------------------------------------------------------------------------------
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID2);
			recPara.setField("MODIFIER",commUtils.trim(gdReq.getParam("userid")));					//수정자
			
			//차량이송소재 종료
			//intRtnVal = ydCarFtmvMtlDao.updYdCarftmvmtl(recInTemp, 1) ;
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarFtmvMtl  
			UPDATE TB_YS_CARFTMVMTL
			   SET MODIFIER = :V_MODIFIER
			     , MOD_DDTT    = SYSDATE
		  	     , DEL_YN = :V_DEL_YN
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			   AND DEL_YN = 'N'
			  */ 
			
			commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBook", logId, methodNm, "TB_YS_CARFTMVMTL 종료");

			if(szYD_CAR_USE_GP.equals("G")) {
			
			} else {	
				//구내운송
				/**********************************************************
				* 1.하차개시 전송 시작
				**********************************************************/
				recPara         = JDTORecordFactory.getInstance().create();
				recStkCol       = JDTORecordFactory.getInstance().create();
				recPara.setField("YS_STK_COL_GP", 			szYD_CAR_STOP_LOC);
	
				//적치열 Table를 조회한다.
	    		 /* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkCol1 
	    		SELECT 
	    			YS_STK_COL_GP AS YS_STK_COL_GP
	    			,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
	    			,REGISTER AS REGISTER
	    			,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
	    			,MODIFIER AS MODIFIER
	    			,DEL_YN AS DEL_YN
	    			,YD_GP AS YD_GP
	    			,YD_BAY_GP AS YD_BAY_GP
	    			,YD_EQP_GP	AS YD_EQP_GP
	    			,YD_STK_COL_NO AS YD_STK_COL_NO
	    			,YD_STK_COL_ACT_STAT AS YD_STK_COL_ACT_STAT
	    			,YD_STK_COL_RULE_XAXIS AS YD_STK_COL_RULE_XAXIS
	    			,YD_STK_COL_RULE_YAXIS AS YD_STK_COL_RULE_YAXIS
	    			,YD_STK_COL_W AS YD_STK_COL_W
	    			,YD_STK_COL_L AS YD_STK_COL_L
	    			,YD_CAR_USE_GP AS YD_CAR_USE_GP
	    			,TRN_EQP_CD AS TRN_EQP_CD
	    			,CAR_NO AS CAR_NO
	    			,CARD_NO AS CARD_NO
	    			,WLOC_CD AS WLOC_CD
	    			,YD_PNT_CD AS YD_PNT_CD
	    			,YS_STK_COL_T_GP AS YS_STK_COL_T_GP
	    			,YS_STK_COL_W_GP AS YS_STK_COL_W_GP
	    			,YS_STK_COL_L_GP AS YD_STK_COL_L_GP
	    		--	,YD_STK_COL_H_MAX AS YD_STK_COL_H_MAX--
	    			--,YD_STK_COL_BED_L_TP AS YD_STK_COL_BED_L_TP
	    		    ,YS_OUTDIA_GRP_GP AS YS_OUTDIA_GRP_GP 
	    		    ,YD_STKBED_USG_CD
	    		FROM TB_YS_STKCOL
	    		WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
	    		AND DEL_YN ='N'
	    			*/
				JDTORecordSet rsStkCol = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkCol1", logId, methodNm, "적치열 조회"); 
		    	
		    	if (rsStkCol == null || rsStkCol.size() <= 0) {
					szMsg="차량정지위치[" + szYD_CAR_STOP_LOC + "]에 대한 개소코드가 없습니다.";
					throw new Exception(szMsg);
				}
				
		    	rsStkCol.first();
				recStkCol = rsStkCol.getRecord();
				
				szARR_WLOC_CD   = commUtils.trim(recStkCol.getFieldString("WLOC_CD"          ));
				szARR_YD_PNT_CD = commUtils.trim(recStkCol.getFieldString("YD_PNT_CD"          ));
				szMsg="차량정지위치[" + szYD_CAR_STOP_LOC + "]에 대한 개소코드[" + szARR_WLOC_CD + "]와 야드포인트코드[" + szARR_YD_PNT_CD + "]";
				commUtils.printLog(logId, szMsg, "SL");
				
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setResultCode(logId);	//Log ID
				recPara.setResultMsg(methodNm);	//Log Method Name
				/**********************************************************
				* 1.하차개시 전송 시작
				*  JMS_TC_CD	JMSTC코드	CHAR	8
				*  JMS_TC_CREATE_DDTT	JMSTC생성일시	DATE	14
				*  TRN_EQP_CD            운송장비코드	CHAR	8
				*  ARR_WLOC_CD           착지개소코드	CHAR	5
				*  ARR_YD_PNT_CD         착지야드포인트코드	CHAR	4
				*  TRN_WRK_ST_DT			운송작업시작일시	DATE	14
				**********************************************************/
				
				recPara.setField("JMS_TC_CD", 			"YSTSJ009");
				recPara.setField("JMS_TC_CREATE_DDTT", 	szCurrDate);
				recPara.setField("TRN_EQP_CD",     		szTRN_EQP_CD);
				recPara.setField("ARR_WLOC_CD", 		szARR_WLOC_CD);
				recPara.setField("ARR_YD_PNT_CD", 		szARR_YD_PNT_CD);
				recPara.setField("TRN_WRK_ST_DT", 		szCurrDate);
				sndRecord = commUtils.addSndData(sndRecord,recPara);
				
				commUtils.printLog(logId, "하차개시전문을 구내운송으로 전송 완료", "SL");
							
				//+++++++++++++++++ 하차개시 전송 끝 ++++++++++++++++
				
				//+++++++++++++++++ 하차완료 전송 시작 ++++++++++++++++
				/**********************************************************
				* 1.하차완료 전송 시작
				*  JMS_TC_CD	JMSTC코드	CHAR	8
				*  JMS_TC_CREATE_DDTT	JMSTC생성일시	DATE	14
				*  TRN_EQP_CD            운송장비코드	CHAR	8
				*  ARR_WLOC_CD           착지개소코드	CHAR	5
				*  ARR_YD_PNT_CD         착지야드포인트코드	CHAR	4
				*  CARUD_CMPL_DT		  하차완료일시	DATE	14
				**********************************************************/
				
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setResultCode(logId);	//Log ID
				recPara.setResultMsg(methodNm);	//Log Method Name
				//2. 하차완료를 구내운송으로 전송
				recPara.setField("JMS_TC_CD", 			"YSTSJ010");
				recPara.setField("JMS_TC_CREATE_DDTT", 	szCurrDate);
				recPara.setField("TRN_EQP_CD",     		szTRN_EQP_CD);
				recPara.setField("ARR_WLOC_CD", 		szARR_WLOC_CD);
				recPara.setField("ARR_YD_PNT_CD", 		szARR_YD_PNT_CD);
				recPara.setField("CARUD_CMPL_DT", 		commUtils.getCurDate("yyyyMMddHHmmss"));
				
				sndRecord = commUtils.addSndData(sndRecord,recPara);
				
				commUtils.printLog(logId, "하차완료전문을 구내운송으로 전송 완료", "SL");
				//+++++++++++++++++ 하차완료 전송 끝 ++++++++++++++++
				
				commUtils.printLog(logId, methodNm, "S-");
			}	
			return sndRecord;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end procCarUd
	
	/**
	 * 저장위치좌표설정 - 열정보 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updStrLocPosSetCol(GridData gdReq) throws DAOException {
		String methodNm = "저장위치좌표설정 - 열정보 변경 [SbrYsJspSeEJB.updStrLocPosSetCol] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					// 열정보 수정
					jrParam.setField("YD_GP", commUtils.getValue(gdReq, "YD_GP", ii));
					jrParam.setField("YD_BAY_GP", commUtils.getValue(gdReq, "YD_BAY_GP", ii));
					jrParam.setField("YD_EQP_GP", commUtils.getValue(gdReq, "YD_EQP_GP", ii));
					jrParam.setField("YD_STK_COL_NO", commUtils.getValue(gdReq, "YD_STK_COL_NO", ii));
					jrParam.setField("YD_STK_COL_ACT_STAT", commUtils.getValue(gdReq, "YD_STK_COL_ACT_STAT", ii));
					jrParam.setField("YD_STK_COL_RULE_XAXIS", commUtils.getValue(gdReq, "YD_STK_COL_RULE_XAXIS", ii));
					jrParam.setField("YD_STK_COL_RULE_YAXIS", commUtils.getValue(gdReq, "YD_STK_COL_RULE_YAXIS", ii));
					jrParam.setField("YD_STK_COL_W", commUtils.getValue(gdReq, "YD_STK_COL_W", ii));
					jrParam.setField("YD_STK_COL_L", commUtils.getValue(gdReq, "YD_STK_COL_L", ii));
					jrParam.setField("YS_STK_COL_L_GP", commUtils.getValue(gdReq, "YS_STK_COL_L_GP", ii));
					jrParam.setField("YS_STK_COL_GP", commUtils.getValue(gdReq, "YS_STK_COL_GP", ii));
					jrParam.setField("YD_STK_COL_DIR_GP", commUtils.getValue(gdReq, "V_YD_STK_COL_DIR_GP", ii));
					jrParam.setField("YD_STKBED_USG_CD", commUtils.getValue(gdReq, "YD_STKBED_USG_CD", ii));
					jrParam.setField("YD_STK_BED_LYR_MAX", commUtils.getValue(gdReq, "YD_STK_BED_LYR_MAX", ii));
					jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
	
					commDao.update(jrParam, updYdStkcol, logId, methodNm, "열정보 수정");
					
					commDao.update(jrParam, updYdStkbedLyrMax, logId, methodNm, "BED MAX 단 일괄 수정");

					jrParam.setField("YD_INFO_SYNC_CD", "4"); // 야드정보동기화코드(Bed)
					jrParam.setField("YS_STK_COL_GP", commUtils.trim(gdReq.getHeader("YS_STK_COL_GP").getValue(ii))); // 야드적치열구분
					// jrParam.setField("YD_STK_BED_NO" , "01" ); //야드적치Bed번호
	
					// 전송Data 조회
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L301", jrParam)); // YSN2L001 저장위치제원(송신)
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updStrLocPosSetCol
	
	/**
	 * 저장위치좌표설정 - Bed정보 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updStrLocPosSetBed(GridData gdReq) throws DAOException {
		String methodNm = "저장위치좌표설정 - Bed정보 변경 [SbrYsJspSeEJB.updStrLocPosSetBed] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name
			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					// Bed정보 수정
					jrParam.setField("YD_STR_GTR_CD", commUtils.getValue(gdReq, "YD_STR_GTR_CD", ii));
					jrParam.setField("YD_STK_BED_ACT_STAT", commUtils.getValue(gdReq, "YD_STK_BED_ACT_STAT", ii));
					jrParam.setField("YD_STK_BED_WHIO_STAT", commUtils.getValue(gdReq, "YD_STK_BED_WHIO_STAT", ii));
					jrParam.setField("YD_STK_BED_XAXIS", commUtils.getValue(gdReq, "YD_STK_BED_XAXIS", ii));
					jrParam.setField("YD_STK_BED_YAXIS", commUtils.getValue(gdReq, "YD_STK_BED_YAXIS", ii));
					jrParam.setField("YD_STK_BED_WT_MAX", commUtils.getValue(gdReq, "YD_STK_BED_WT_MAX", ii));
					jrParam.setField("YD_STK_BED_H_MAX", commUtils.getValue(gdReq, "YD_STK_BED_H_MAX", ii));
					jrParam.setField("YD_STK_BED_L_MAX", commUtils.getValue(gdReq, "YD_STK_BED_L_MAX", ii));
					jrParam.setField("YD_STK_BED_W_MAX", commUtils.getValue(gdReq, "YD_STK_BED_W_MAX", ii));
					jrParam.setField("YD_STK_BED_XAXIS_TOL", commUtils.getValue(gdReq, "YD_STK_BED_XAXIS_TOL", ii));
					jrParam.setField("YD_STK_BED_YAXIS_TOL", commUtils.getValue(gdReq, "YD_STK_BED_YAXIS_TOL", ii));
					jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
					jrParam.setField("YD_STK_BED_XAXIS1", commUtils.getValue(gdReq, "YD_STK_BED_XAXIS1", ii));
					jrParam.setField("YD_STK_BED_YAXIS1", commUtils.getValue(gdReq, "YD_STK_BED_YAXIS1", ii));
					jrParam.setField("YS_STK_COL_GP", commUtils.getValue(gdReq, "YS_STK_COL_GP", ii));
					jrParam.setField("YS_STK_BED_NO", commUtils.getValue(gdReq, "YS_STK_BED_NO", ii));
					
					jrParam.setField("YS_STK_BED_TP", commUtils.getValue(gdReq, "YS_STK_BED_TP", ii));
					jrParam.setField("YS_STK_BED_L_GP", commUtils.getValue(gdReq, "YS_STK_BED_L_GP", ii));
					jrParam.setField("YD_STK_BED_DIR_GP", commUtils.getValue(gdReq, "YD_STK_BED_DIR_GP", ii));
	
					commDao.update(jrParam, updYdStkbedLm, logId, methodNm, "Bed정보 수정");
	
					jrParam.setField("YD_INFO_SYNC_CD", "4"); // 야드정보동기화코드(Bed)
					jrParam.setField("YS_STK_COL_GP", commUtils.trim(gdReq.getHeader("YS_STK_COL_GP").getValue(ii))); // 야드적치열구분
					jrParam.setField("YS_STK_BED_NO", commUtils.trim(gdReq.getHeader("YS_STK_BED_NO").getValue(ii))); // 야드적치Bed번호
	
					// 전송Data 조회
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L301", jrParam)); // YSN2L001
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updStrLocPosSetBed
	
	/**
	 * 대형압연재예정저장위치 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord regLMillPlnStrLoc(GridData gdReq) throws DAOException {
		String methodNm = "대형압연재예정저장위치 등록 [SbrYsJspSeEJB.regLMillPlnStrLoc] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					jrParam.setField("PLN_BLM_NO", commUtils.getValue(gdReq, "PLN_BLM_NO", ii));
					jrParam.setField("BLM_NO", commUtils.getValue(gdReq, "BLM_NO", ii));
					jrParam.setField("HEAT_NO", commUtils.getValue(gdReq, "HEAT_NO", ii));
					jrParam.setField("SPEC_ABBSYM", commUtils.getValue(gdReq, "SPEC_ABBSYM", ii));
					jrParam.setField("ITEMNAME_CD", commUtils.getValue(gdReq, "ITEMNAME_CD", ii));
					jrParam.setField("ORD_NO", commUtils.getValue(gdReq, "ORD_NO", ii));
					jrParam.setField("ORD_DTL", commUtils.getValue(gdReq, "ORD_DTL", ii));
					jrParam.setField("USAGE_CD", commUtils.getValue(gdReq, "USAGE_CD", ii));
					jrParam.setField("BLM_WT", commUtils.getValue(gdReq, "BLM_WT", ii));
					jrParam.setField("ORD_SZ", commUtils.getValue(gdReq, "ORD_SZ1", ii));
					jrParam.setField("YD_RCPT_PLN_STR_LOC", commUtils.getValue(gdReq, "YD_RCPT_PLN_STR_LOC", ii));
					jrParam.setField("REGISTER", commUtils.trim(gdReq.getParam("userid")));
					jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));

					// 대형압연재예정저장위치 등록 및 수정
					commDao.update(jrParam, insLmillplnstrloc, logId, methodNm, "TB_YS_LMILLPLNSTRLOC");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}// end of regLMillPlnStrLoc
	
	/**
	 * 대형압연재예정저장위치 일괄등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord regBulkLMillPlnStrLoc(GridData gdReq) throws DAOException {
		String methodNm = "대형압연재예정저장위치 일괄등록 [SbrYsJspSeEJB.regBulkLMillPlnStrLoc] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			String bulkRcptPlnStrLoc = commUtils.trim(gdReq.getParam("bulk_rcpt_pln_str_loc"));
			
			if (commUtils.nvl(bulkRcptPlnStrLoc, "").equals("")) {
				throw new Exception("예정저장위치 값이 없습니다.");
			}

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					jrParam.setField("PLN_BLM_NO", commUtils.getValue(gdReq, "PLN_BLM_NO", ii));
					jrParam.setField("BLM_NO", commUtils.getValue(gdReq, "BLM_NO", ii));
					jrParam.setField("HEAT_NO", commUtils.getValue(gdReq, "HEAT_NO", ii));
					jrParam.setField("SPEC_ABBSYM", commUtils.getValue(gdReq, "SPEC_ABBSYM", ii));
					jrParam.setField("ITEMNAME_CD", commUtils.getValue(gdReq, "ITEMNAME_CD", ii));
					jrParam.setField("ORD_NO", commUtils.getValue(gdReq, "ORD_NO", ii));
					jrParam.setField("ORD_DTL", commUtils.getValue(gdReq, "ORD_DTL", ii));
					jrParam.setField("USAGE_CD", commUtils.getValue(gdReq, "USAGE_CD", ii));
					jrParam.setField("BLM_WT", commUtils.getValue(gdReq, "BLM_WT", ii));
					jrParam.setField("ORD_SZ", commUtils.getValue(gdReq, "ORD_SZ1", ii));
					jrParam.setField("YD_RCPT_PLN_STR_LOC", bulkRcptPlnStrLoc);
					jrParam.setField("REGISTER", commUtils.trim(gdReq.getParam("userid")));
					jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));

					// 대형압연재예정저장위치 등록 및 수정
					commDao.update(jrParam, insLmillplnstrloc, logId, methodNm, "TB_YS_LMILLPLNSTRLOC");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}// end of regLMillPlnStrLoc
	
	/**
	 * 기준관리 - 검색가이드 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updYsRuleSrchGdBt(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "기준관리 - 검색가이드 수정[SbrYsJspSeEJB.updYsRuleSrchGdBt] < ";
		String logId = gdReq[0].getRequestUserIp();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq[0].getFieldString("userid")));

			//수정할 레코드 수
			int rowCnt = 0;
			
			int ruleCnt = Integer.parseInt(gdReq[0].getFieldString("REPR_CD_GP_CNT"));
			String szRuleCdGp = null;
			String szRuleCdContents = null;
			
			for (int ii = 0; ii < gdReq.length; ii++) {
				
				String szRuleCdGps = null;
				String sCdGp = null;
				String sItem = null;
			
				for (int jj = 0; jj < ruleCnt; jj++) {
					
					szRuleCdGp = gdReq[0].getFieldString("REPR_CD_GP"+(jj+1)); // C00011, C00012, C00013, C00014, C00015 
					szRuleCdGps = gdReq[ii].getFieldString("REPR_CD_GPS"+(jj+1)); //0, 1
					szRuleCdContents = gdReq[0].getFieldString("REPR_CD_CONTENTS"+(jj+1)); 
					sCdGp = "00"+gdReq[ii].getFieldString("YS_STK_BED_NO");
					sItem = gdReq[ii].getFieldString("YS_STK_COL_GP");
					
					jrParam.setField("REPR_CD_GP"	, szRuleCdGp );
					jrParam.setField("CD_GP"		, sCdGp );
					jrParam.setField("ITEM"			, sItem);
					
					commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.delYsRule", logId, methodNm, "기준관리 삭제");
						
					if("1".equals(szRuleCdGps)) {
						//기준 등록
						jrParam.setField("REPR_CD_GP"		, szRuleCdGp );
						jrParam.setField("CD_GP"			, commUtils.nvl(gdReq[ii].getFieldString("YS_STK_BED_NO"), "")); 
						jrParam.setField("ITEM"				, commUtils.nvl(gdReq[ii].getFieldString("YS_STK_COL_GP"), "")); 
						jrParam.setField("REPR_CD_CONTENTS"	, szRuleCdContents); 
						
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insYsRule", logId, methodNm, "기준관리 등록");
					} else {
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
	} // end of updYsRuleSrchGdBt

	/**
	 *      [A] 오퍼레이션명 : 이적작업팝업-작업예약등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord callupdbtMvStkWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "이적작업팝업-작업예약등록[SbrYsJspSeEJB.callupdbtMvStkWrkBook] < " + gdReq.getNavigateValue();
		String logId    = gdReq.getIPAddress();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
			JDTORecord jrRtn1   = null;
			JDTORecord jrRtn2   = JDTORecordFactory.getInstance().create();
			JDTORecord jrRtn3   = JDTORecordFactory.getInstance().create();
			
			String ydToLocGuide = commUtils.substr(gdReq.getParam("YD_TO_LOC_GUIDE"), 2, 2);
			String modifier = gdReq.getParam("userid");
			
			// 이적 등록 시 "TC" 인 경우
			if(ydToLocGuide.equals("TC")) {
				
				//현재 대차상태 조회
				jrParam1.setField("YD_EQP_ID", gdReq.getParam("YD_EQP_ID"));
				JDTORecordSet jsCol1 = commDao.select(jrParam1, getStatEqp, logId, methodNm, "현재 대차정보 조회");
				
				if( jsCol1.size() > 0 ) {
					jsCol1.first();
					JDTORecord recInPara = jsCol1.getRecord();
					
					String sYD_EQP_ID = commUtils.trim(recInPara.getFieldString("YD_EQP_ID"));
					String sYD_CURR_BAY_GP = commUtils.trim(recInPara.getFieldString("YD_CURR_BAY_GP"));
					String sYD_EQP_STAT = commUtils.trim(recInPara.getFieldString("YD_EQP_STAT"));
					String sYD_EQP_WRK_MODE = commUtils.trim(recInPara.getFieldString("YD_EQP_WRK_MODE"));
					
					if(sYD_CURR_BAY_GP.equals("A")) { //대차위치가 A동일 경우 작업예약등록 실행
						if(sYD_EQP_STAT.equals("N") && sYD_EQP_WRK_MODE.equals("1") ) {
							
							//작업예약등록 실행
							jrRtn1 = this.updbtMvStkWrkBook(gdReq);
							
							if(jrRtn1 != null) {
								EJBConnector sndConn2 = new EJBConnector("default", "YsCommEJB", this);
								sndConn2.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn1 });
							}
							
							// 대차작업예약등록
							JDTORecord jrParam3 = JDTORecordFactory.getInstance().create();
							
							jrParam3.setField("SSTL_NOS" 	, gdReq.getParam("SSTL_NOS"));	//재료번호들
							jrParam3.setField("YD_SCH_CD"	, sYD_EQP_ID+"UM");	//
							
							JDTORecordSet jsCol2 = commDao.select(jrParam3, "com.inisteel.cim.ys.sbr.dao.getWrkBookInfo", logId, methodNm, "작업예약 조회");
							
							for( int ii = 0; ii < jsCol2.size(); ii++ ) {
								
								String ydTCarWbookId = "";
								String ydWbookId = jsCol2.getRecord(ii).getFieldString("YD_WBOOK_ID");
								
								//1. 대차작업예약ID 생성
								ydTCarWbookId = commDao.getSeqId(logId, methodNm, "TcarSch"); //대차스케줄ID 생성
								
								//2. 대차스케줄 생성
								JDTORecord jrParam4 = JDTORecordFactory.getInstance().create();
								
								jrParam4.setField("YD_TCAR_SCH_ID"    		, ydTCarWbookId ); //야드대차스케쥴ID
								jrParam4.setField("YD_EQP_ID"    	 		, sYD_EQP_ID	); //야드설비ID
								jrParam4.setField("YD_EQP_WRK_STAT"   		, "U"			); //야드설비작업상태
								jrParam4.setField("YD_WRK_PROG_STAT"  		, "W"			); //야드작업진행상태
								jrParam4.setField("YD_CARLD_LEV_LOC"  		, "A"			); //야드상차출발위치
								jrParam4.setField("YD_CARLD_WRK_BOOK_ID"	, ydWbookId		); //야드상차작업예약ID
								jrParam4.setField("YD_CAR_PROG_STAT"		, "0"			); //야드차량진행상태
								jrParam4.setField("REGISTER"          		, modifier      ); //등록자
								jrParam4.setField("MODIFIER"          		, modifier      ); //수정자
								
								commDao.insert(jrParam4, "com.inisteel.cim.ys.sbr.dao.insTCarWrkBook", logId, methodNm, "TB_YS_TCARSCH"); // 대차스케줄
								
								//3. 대차이송재료 생성								
								JDTORecord jrParam5 = JDTORecordFactory.getInstance().create();
								
								jrParam5.setField("YD_WBOOK_ID", ydWbookId);
								
								JDTORecordSet jsCol3 = commDao.select(jrParam5, "com.inisteel.cim.ys.sbr.dao.getWrkBookInfoMtl", logId, methodNm, "작업예약 조회");
								
								//3.3. 작업예약재료 등록
								for( int jj = 0; jj < jsCol3.size(); jj++ ) {
									
									JDTORecord jrRow = JDTORecordFactory.getInstance().create();
									jrRow = jsCol3.getRecord(jj);	//작업재료
									
									JDTORecord jrParam6 = JDTORecordFactory.getInstance().create();
									jrParam6.setField("YD_TCAR_SCH_ID"	, ydTCarWbookId                                        );	//야드대차스케쥴ID
									jrParam6.setField("SSTL_NO"      	, commUtils.trim(jrRow.getFieldString("SSTL_NO"    	 )));	//재료번호
									jrParam6.setField("YS_STK_BED_NO" 	, commUtils.trim(jrRow.getFieldString("YS_STK_BED_NO")));	//야드적치Bed번호
									jrParam6.setField("YS_STK_LYR_NO" 	, commUtils.trim(jrRow.getFieldString("YS_STK_LYR_NO")));	//야드적치단번호
									jrParam6.setField("YS_STK_SEQ_NO" 	, commUtils.trim(jrRow.getFieldString("YS_STK_SEQ_NO")));	//야드적치Seq
									jrParam6.setField("STL_PROG_CD"		, commUtils.trim(jrRow.getFieldString("STL_PROG_CD")  ));	//재료진도코드
									jrParam6.setField("REGISTER"     	, modifier                                             );	//등록자
									jrParam6.setField("MODIFIER"     	, modifier                                             );	//수정자
									
									commDao.insert(jrParam6, "com.inisteel.cim.ys.sbr.dao.insTCarWrkBookMtl", logId, methodNm, "TB_YS_TCARFTMVMTL"); // 대차이송재료
								}
							}
							
						} else {
							jrRtn3.addField("ErrorMsg", "해당 대차["+gdReq.getParam("YD_EQP_ID")+"]는 고장입니다.");
						}
						
					} else {
						if(sYD_EQP_STAT.equals("N") && sYD_EQP_WRK_MODE.equals("1") ) {
							
							JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
							
							//대차이동요청(YSN7L312)
							jrParam2.setField("YD_EQP_ID", sYD_EQP_ID);
							jrParam2.setField("END_LOC", "A"); // 목표동
							jrParam2.setField("YD_EQP_WRK_STAT", "U"); // 적재상태
							jrParam2.setField("ST_LOC", "B"); // 출발동
							jrParam2.setField("END_LOC", "A"); // 도착동
							
							jrRtn2 = commUtils.addSndData(jrRtn2, commDao.getMsgL2("YSN7L312", jrParam2));
							
							if(jrRtn2 != null) {
								EJBConnector sndConn1 = new EJBConnector("default", "YsCommEJB", this);
								sndConn1.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn2 });
							}
							
							//작업예약등록 실행
							jrRtn1 = this.updbtMvStkWrkBook(gdReq);
							
							if(jrRtn1 != null) {
								EJBConnector sndConn2 = new EJBConnector("default", "YsCommEJB", this);
								sndConn2.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn1 });
							}
							
							// 대차작업예약등록
							JDTORecord jrParam3 = JDTORecordFactory.getInstance().create();
							
							jrParam3.setField("SSTL_NOS" 	, gdReq.getParam("SSTL_NOS"));	//재료번호들
							jrParam3.setField("YD_SCH_CD"	, sYD_EQP_ID+"UM");	//
							
							JDTORecordSet jsCol2 = commDao.select(jrParam3, "com.inisteel.cim.ys.sbr.dao.getWrkBookInfo", logId, methodNm, "작업예약 조회");
							
							for( int ii = 0; ii < jsCol2.size(); ii++ ) {
								
								String ydTCarWbookId = "";
								String ydWbookId = jsCol2.getRecord(ii).getFieldString("YD_WBOOK_ID");
								
								//1. 대차작업예약ID 생성
								ydTCarWbookId = commDao.getSeqId(logId, methodNm, "TcarSch"); //대차스케줄ID 생성
								
								//2. 대차스케줄 생성
								JDTORecord jrParam4 = JDTORecordFactory.getInstance().create();
								
								jrParam4.setField("YD_TCAR_SCH_ID"    		, ydTCarWbookId );	//야드대차스케쥴ID
								jrParam4.setField("YD_EQP_ID"    	 		, sYD_EQP_ID	);	//야드설비ID
								jrParam4.setField("YD_EQP_WRK_STAT"   		, "U"			);	//야드설비작업상태
								jrParam4.setField("YD_WRK_PROG_STAT"  		, "W"			);	//야드작업진행상태
								jrParam4.setField("YD_CARLD_LEV_LOC"  		, "A"			);	//야드상차출발위치
								jrParam4.setField("YD_CARLD_WRK_BOOK_ID"	, ydWbookId		);	//야드상차작업예약ID
								jrParam4.setField("YD_CAR_PROG_STAT"		, "0"			); //야드차량진행상태
								jrParam4.setField("REGISTER"          		, modifier      ); //등록자
								jrParam4.setField("MODIFIER"          		, modifier      ); //수정자
								
								commDao.insert(jrParam4, "com.inisteel.cim.ys.sbr.dao.insTCarWrkBook", logId, methodNm, "TB_YS_TCARSCH"); // 대차스케줄
								
								//3. 대차이송재료 생성								
								JDTORecord jrParam5 = JDTORecordFactory.getInstance().create();
								jrParam5.setField("YD_WBOOK_ID", ydWbookId);
								
								JDTORecordSet jsCol3 = commDao.select(jrParam5, "com.inisteel.cim.ys.sbr.dao.getWrkBookInfoMtl", logId, methodNm, "작업예약 조회");
								
								for( int jj = 0; jj < jsCol3.size(); jj++ ) {
									
									JDTORecord jrRow = JDTORecordFactory.getInstance().create();
									
									jrRow = jsCol3.getRecord(jj);	//작업재료
									
									JDTORecord jrParam6 = JDTORecordFactory.getInstance().create();
									jrParam6.setField("YD_TCAR_SCH_ID", ydTCarWbookId                                          );	//야드대차스케쥴ID
									jrParam6.setField("SSTL_NO"      	, commUtils.trim(jrRow.getFieldString("SSTL_NO"    	 )));	//재료번호
									jrParam6.setField("YS_STK_BED_NO" 	, commUtils.trim(jrRow.getFieldString("YS_STK_BED_NO")));	//야드적치Bed번호
									jrParam6.setField("YS_STK_LYR_NO" 	, commUtils.trim(jrRow.getFieldString("YS_STK_LYR_NO")));	//야드적치단번호
									jrParam6.setField("YS_STK_SEQ_NO" 	, commUtils.trim(jrRow.getFieldString("YS_STK_SEQ_NO")));	//야드적치Seq
									jrParam6.setField("STL_PROG_CD"		, commUtils.trim(jrRow.getFieldString("STL_PROG_CD")  ));	//재료진도코드
									jrParam6.setField("REGISTER"     	, modifier                                             );	//등록자
									jrParam6.setField("MODIFIER"     	, modifier                                             );	//수정자
									
									commDao.insert(jrParam6, "com.inisteel.cim.ys.sbr.dao.insTCarWrkBookMtl", logId, methodNm, "TB_YS_TCARFTMVMTL"); // 대차이송재료
								}
							}
							
						} else {
							jrRtn3.addField("ErrorMsg", "해당 대차["+gdReq.getParam("YD_EQP_ID")+"]는 고장입니다.");
						}
					}
				}
				
			} else {
				jrRtn1 = this.updbtMvStkWrkBook(gdReq);
				
				if(jrRtn1 != null) {
					EJBConnector sndConn2 = new EJBConnector("default", "YsCommEJB", this);
					sndConn2.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn1 });
				}
				
				jrRtn3.addField("ErrorMsg", "");
			}
			
			return jrRtn3;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		} 
	} // end of callupdbtMvStkWrkBook
	
	/**
	 *      [A] 오퍼레이션명 : 저장위치별현황 - 차량입고LOT등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord callregCarFtmvLot(GridData gdReq) throws DAOException {
		String methodNm = "저장위치별현황 - 차량입고LOT등록[SbrYsJspSeEJB.callregCarFtmvLot] < " + gdReq.getNavigateValue();
		String logId    = gdReq.getIPAddress();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord jrRtn = this.regCarFtmvLot(gdReq);
			
			//특수강이송지시 등록
			JDTORecord jrParam = null;
			jrParam = JDTORecordFactory.getInstance().create();
			
			String SPOS_WLOC_CD = "";
			String vYD_SCH_CD = gdReq.getParam("YD_SCH_CD");
			
			switch (vYD_SCH_CD) {
				case "GDTR11UM": // 소형야드 서문
					SPOS_WLOC_CD = "SMS12";
					break;
				case "GDTR21UM": // 소형야드 동문
					SPOS_WLOC_CD = "S5S20";
					break;
			}
			
			String ARR_WLOC_CD = "";
			String vBAY_GP = gdReq.getParam("YD_AIM_BAY_GP");
			
			switch (vBAY_GP) {
				case "X": // 정정
					ARR_WLOC_CD = "S6Y20";
					break;
				case "H": // 사외통합
					ARR_WLOC_CD = "BSY04";
					break;
			}			
			
			for(int ii = 0; ii < 100; ii++) {
				String vSSTL_NO = "";
				vSSTL_NO = commUtils.getValue(gdReq, "SSTL_NO", ii);
				
				if(!"".equals(vSSTL_NO)) {
					jrParam.setField("SSTL_NO"      , commUtils.getValue(gdReq, "SSTL_NO", ii)	);	//재료번호들
					jrParam.setField("USER_ID"      , gdReq.getParam("userid")					);	//사용자
					jrParam.setField("SPOS_WLOC_CD" , SPOS_WLOC_CD								);	//소형야드 
					jrParam.setField("ARR_WLOC_CD"  , ARR_WLOC_CD								);  //착지 개소 코드 - TB_YS_LMILLPLNSTRLOC.YD_RCPT_PLN_STR_LOC(야드입고예정위치)
					jrParam.setField("REG_PGM"      , "YardSystem"								);	//등록프로그램
					
					EJBConnector sndConn = new EJBConnector("default", "SbrYsL2RcvSeEJB", this);
					sndConn.trx("insPbStlFrToMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				}
			}
			
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		} 
	} // end of callregCarFtmvLot
	
	/**
	 * 상차완료백업처리팝업-등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord trtMvCarStatSet2(GridData gdReq) throws DAOException {
		String methodNm = "상차완료백업처리팝업-등록 [SbrYsJspSeEJB.trtMvCarStatSet2] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			commUtils.printParam("gdReq", commUtils.gridDataTojdtoRecord(gdReq));

			String sJMS_TC_CD = commUtils.trim(gdReq.getParam("JMS_TC_CD"));
			String sTRN_WRK_FULLVOID_GP = commUtils.trim(gdReq.getParam("TRN_WRK_FULLVOID_GP"));
			String sTRN_EQP_CD = commUtils.trim(gdReq.getParam("TRN_EQP_CD"));
			String sYD_CAR_SCH_ID = commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"));
			String sSPOS_WLOC_CD = commUtils.trim(gdReq.getParam("SPOS_WLOC_CD"));
			String sYD_PNT_CD1 = commUtils.trim(gdReq.getParam("YD_PNT_CD1"));
			String sYD_CARLD_STOP_LOC = commUtils.trim(gdReq.getParam("YD_CARLD_STOP_LOC"));
			String sARR_WLOC_CD = commUtils.trim(gdReq.getParam("ARR_WLOC_CD"));
			String sYD_PNT_CD3 = commUtils.trim(gdReq.getParam("YD_PNT_CD3"));
			String sYD_CARUD_STOP_LOC = commUtils.trim(gdReq.getParam("YD_CARUD_STOP_LOC"));
			String sTO_LOC = commUtils.trim(gdReq.getParam("TO_LOC"));
			String sWLOC_CD = null;
			String sYD_PNT_CD = null;

			String modifier = commUtils.trim(gdReq.getParam("userid")); // 수정자
			String currDate = commUtils.getDateTime14(); // 현재시각

			if ("".equals(sTRN_EQP_CD)) {
				throw new Exception("운송장비코드가 없습니다.");
			}

			// Return Value
			JDTORecord jrRtn = null;
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);

			jrYdMsg.setField("JMS_TC_CD", sJMS_TC_CD);

			if ("TSYSJ003".equals(sJMS_TC_CD)) { // 소재차량도착
				jrYdMsg.setField("TRN_EQP_CD", sTRN_EQP_CD);
				if ("F".equals(sTRN_WRK_FULLVOID_GP)) {
					// 하차도착
					jrYdMsg.setField("ARR_WLOC_CD", sARR_WLOC_CD);
					jrYdMsg.setField("ARR_YD_PNT_CD", sYD_PNT_CD3);
					jrYdMsg.setField("TRN_WRK_FULLVOID_GP", sTRN_WRK_FULLVOID_GP);
				} else if ("E".equals(sTRN_WRK_FULLVOID_GP)) {
					// 상차도착
					jrYdMsg.setField("ARR_WLOC_CD", sSPOS_WLOC_CD);
					jrYdMsg.setField("ARR_YD_PNT_CD", sYD_PNT_CD1);
					jrYdMsg.setField("TRN_WRK_FULLVOID_GP", sTRN_WRK_FULLVOID_GP);
				}

				EJBConnector sndConn = new EJBConnector("default", "YsCommCarTSMvSeEJB", this);
				jrRtn = (JDTORecord) sndConn.trx("rcvTSYSJ003", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

			} else if ("TSYSJ004".equals(sJMS_TC_CD)) { // 소재차량출발
				jrYdMsg.setField("TRN_EQP_CD", sTRN_EQP_CD);
				jrYdMsg.setField("TRN_WRK_FULLVOID_GP", sTRN_WRK_FULLVOID_GP);
				jrYdMsg.setField("TRN_EQP_STK_CAPA", "80000");

				if ("F".equals(sTRN_WRK_FULLVOID_GP)) { // 영차:하차하러 출발
					jrYdMsg.setField("SPOS_WLOC_CD", sSPOS_WLOC_CD);
					jrYdMsg.setField("SPOS_YD_PNT_CD", sYD_PNT_CD1);
					jrYdMsg.setField("ARR_WLOC_CD", sARR_WLOC_CD);
					jrYdMsg.setField("ARR_YD_PNT_CD", sYD_PNT_CD3);

				} else if ("E".equals(sTRN_WRK_FULLVOID_GP)) { // 하차완료후 출발처리로 착지개소를 DMY1P로 줌으로써 차량스케줄 완료처리를 한다.
					jrYdMsg.setField("SPOS_WLOC_CD", sARR_WLOC_CD);
					jrYdMsg.setField("SPOS_YD_PNT_CD", sYD_PNT_CD3);
					jrYdMsg.setField("ARR_WLOC_CD", "DMY1P");
					jrYdMsg.setField("ARR_YD_PNT_CD", "");
				}

				EJBConnector sndConn = new EJBConnector("default", "YsCommCarTSMvSeEJB", this);
				jrRtn = (JDTORecord) sndConn.trx("rcvTSYSJ004", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

			} else if ("YSTSJ007".equals(sJMS_TC_CD)) { // 소재차량상차개시
				// 차량진행상태를 하차완료로 설정
				jrParam.setField("YD_CAR_PROG_STAT", "4"); // 상차개시
				jrParam.setField("YD_CARLD_CMPL_DT", ""); // 상차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_CMPL_DT", ""); // 하차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);

				commDao.update(jrParam, updMvCarSchCmpl, logId, methodNm, "이송차량스케줄 상차개시로 수정");

				jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate); // JMSTC생성일시
				jrYdMsg.setField("TRN_EQP_CD", sTRN_EQP_CD); // 운송장비코드
				jrYdMsg.setField("SPOS_WLOC_CD", sSPOS_WLOC_CD); // 발지개소코드
				jrYdMsg.setField("SPOS_YD_PNT_CD", sYD_PNT_CD1); // 발지야드포인트코드
				jrYdMsg.setField("ARR_WLOC_CD", sARR_WLOC_CD); // 착지개소코드
				jrYdMsg.setField("TRN_WRK_ST_DT", currDate); // 운송작업시작일시

				// 전송할 전문에 추가
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);

			} else if ("YSTSJ008".equals(sJMS_TC_CD)) { // 소재차량상차완료
				// 차량진행상태를 상차완료로 설정
				jrParam.setField("YD_CAR_PROG_STAT", "5"); // 상차완료
				jrParam.setField("YD_CARLD_CMPL_DT", currDate); // 상차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_CMPL_DT", ""); // 하차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);

				commDao.update(jrParam, updMvCarSchCmpl, logId, methodNm, "이송차량스케줄 상차완료로 수정");

				jrYdMsg.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID); // 차량스케줄ID
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ008", jrYdMsg));

			} else if ("YSTSJ009".equals(sJMS_TC_CD)) { // 소재차량하차개시
				// 차량진행상태를 하차완료로 설정
				jrParam.setField("YD_CAR_PROG_STAT", "D"); // 하차개시
				jrParam.setField("YD_CARLD_CMPL_DT", ""); // 상차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_CMPL_DT", ""); // 하차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);

				commDao.update(jrParam, updMvCarSchCmpl, logId, methodNm, "이송차량스케줄 하차개시로 수정");

				jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate); // JMSTC생성일시
				jrYdMsg.setField("TRN_EQP_CD", sTRN_EQP_CD); // 운송장비코드
				jrYdMsg.setField("ARR_WLOC_CD", sARR_WLOC_CD); // 착지개소코드
				jrYdMsg.setField("ARR_YD_PNT_CD", sYD_PNT_CD3); // 착지야드포인트코드
				jrYdMsg.setField("TRN_WRK_ST_DT", currDate); // 운송작업시작일시

				// 전송할 전문에 추가
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);

			} else if ("YSTSJ010".equals(sJMS_TC_CD)) { // 소재차량하차완료
				// 차량진행상태를 하차완료로 설정
				jrParam.setField("YD_CAR_PROG_STAT", "E"); // 하차완료
				jrParam.setField("YD_CARLD_CMPL_DT", ""); // 상차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_CMPL_DT", currDate); // 하차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);

				commDao.update(jrParam, updMvCarSchCmpl, logId, methodNm, "이송차량스케줄 하차완료로 수정");
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate); // JMSTC생성일시
				jrYdMsg.setField("TRN_EQP_CD", sTRN_EQP_CD); // 운송장비코드
				jrYdMsg.setField("ARR_WLOC_CD", sARR_WLOC_CD); // 착지개소코드
				jrYdMsg.setField("ARR_YD_PNT_CD", sYD_PNT_CD3); // 착지야드포인트코드
				jrYdMsg.setField("TRN_WRK_ST_DT", currDate); // 운송작업시작일시

				// 전송할 전문에 추가
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);

			} else if ("YSTSJ011".equals(sJMS_TC_CD)) { // 소재차량Point지시
				// 야드적치열구분으로 차량포인트 정보 조회
				jrParam.setField("YD_STK_COL_GP", sTO_LOC);
				JDTORecordSet jsCol = commDao.select(jrParam, getYdPntByStkColGp, logId, methodNm, "야드적치열구분으로 차량포인트 정보 조회");

				if (jsCol != null && jsCol.size() > 0) {
					sWLOC_CD = commUtils.trim(jsCol.getRecord(0).getFieldString("WLOC_CD"));
					sYD_PNT_CD = commUtils.trim(jsCol.getRecord(0).getFieldString("YD_PNT_CD"));

					if ("".equals(sWLOC_CD) || "".equals(sYD_PNT_CD)) {
						throw new Exception(sTO_LOC + " 의 개소코드 또는 야드포인트에 NULL 값이 있습니다.");
					}

					if (!"".equals(commUtils.trim(jsCol.getRecord(0).getFieldString("TRN_EQP_CD")))) {
						throw new Exception(sTO_LOC + " 에 이미 " + commUtils.trim(jsCol.getRecord(0).getFieldString("TRN_EQP_CD")) + " 운송장비가 점유하고  있습니다.");
					}

					if (!"".equals(commUtils.trim(jsCol.getRecord(0).getFieldString("CAR_NO")))) {
						throw new Exception(sTO_LOC + " 에 이미 " + commUtils.trim(jsCol.getRecord(0).getFieldString("CAR_NO")) + " 차량이 점유하고  있습니다.");
					}

				} else {
					throw new Exception(sTO_LOC + " 의 개소코드와 야드포인트를 TB_YD_CARPOINT 에서 찾지 못했습니다.");
				}

				jrYdMsg.setField("JMS_TC_CD", sJMS_TC_CD); // "YSTSJ011"
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate); // JMSTC생성일시
				jrYdMsg.setField("TRN_EQP_CD", sTRN_EQP_CD); // 운송장비코드
				jrYdMsg.setField("WLOC_CD", sWLOC_CD);
				jrYdMsg.setField("YD_PNT_CD", sYD_PNT_CD);
				jrYdMsg.setField("PNT_WO_GP", "A");
				jrYdMsg.setField("PNT_WO_DT", currDate);

				// 전송할 전문에 추가
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);

				String sYD_CARLD_PNT_WO_DT = "";
				String sYD_CARUD_PNT_WO_DT = "";

				// 차량스케줄의 개소코드, 야드포인트, 정지위치를 UDPATE 한다.
				if ("E".equals(sTRN_WRK_FULLVOID_GP)) { // 공차:상차
					sSPOS_WLOC_CD = sWLOC_CD;
					sYD_PNT_CD1 = sYD_PNT_CD;
					sYD_CARLD_STOP_LOC = sTO_LOC;
					sYD_CARLD_PNT_WO_DT = currDate;

				} else { // 영차:하차
					sARR_WLOC_CD = sWLOC_CD;
					sYD_PNT_CD3 = sYD_PNT_CD;
					sYD_CARUD_STOP_LOC = sTO_LOC;
					sYD_CARUD_PNT_WO_DT = currDate;
				}

				// 이송차량스케줄 수정
				jrParam.setField("YD_CAR_PROG_STAT", "1"); // ""이면 이전 상태 유지된다.
				jrParam.setField("SPOS_WLOC_CD", sSPOS_WLOC_CD);
				jrParam.setField("YD_CARLD_PNT_WO_DT", sYD_CARLD_PNT_WO_DT);
				jrParam.setField("YD_PNT_CD1", sYD_PNT_CD1);
				jrParam.setField("YD_CARLD_STOP_LOC", sYD_CARLD_STOP_LOC);
				jrParam.setField("ARR_WLOC_CD", sARR_WLOC_CD);
				jrParam.setField("YD_CARUD_PNT_WO_DT", sYD_CARUD_PNT_WO_DT);
				jrParam.setField("YD_PNT_CD3", sYD_PNT_CD3);
				jrParam.setField("YD_CARUD_STOP_LOC", sYD_CARUD_STOP_LOC);
				jrParam.setField("YD_CAR_SCH_ID", sYD_CAR_SCH_ID);

				commDao.update(jrParam, updMvCarSchPntWo, logId, methodNm, "차량포이트 지시 수정");

				// TB_YM_STACKCOL 예약정보등록
				jrParam.setField("YD_STK_COL_ACT_STAT", "L");
				jrParam.setField("YS_STK_COL_GP", sTO_LOC);

				commDao.update(jrParam, updColActStat, logId, methodNm, "적치열 활성상태 변경");

				// TB_YD_CARPOINT 포인트지시 예약하기
				EJBConnector ejbConn1 = new EJBConnector("default", "YsCommCarTSMvSeEJB", this);
				ejbConn1.trx("YsCarPointinforeg2", new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class },
						new Object[] { "3", "", sTRN_EQP_CD, sTO_LOC, "", "", "R", logId, methodNm });
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of trtMvCarStatSet2

	/**
	 * 상차완료백업처리팝업-이송작업재료등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm = "상차완료백업처리팝업-이송작업재료등록 [SbrYsJspSeEJB.updCarFtMvMtl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			String sstlNo;
			JDTORecordSet rsResult;

			// 등록 할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					sstlNo = commUtils.getValue(gdReq, "SSTL_NO", ii);
					jrParam.setField("SSTL_NO", sstlNo);
					rsResult = commDao.select(jrParam, getYsStockchk, logId, methodNm, "TB_YS_STOCK 에 존재 하는지 확인");

					if (rsResult.size() <= 0) {
						throw new Exception("TB_YS_STOCK에 존재하지 않는 제품 : " + sstlNo);
					}

					// 이송작업재료삭제
					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID")));
					jrParam.setField("SSTL_NO", commUtils.getValue(gdReq, "OLD_SSTL_NO", ii));

					commDao.update(jrParam, delCarFtMvMtl, logId, methodNm, "이송작업재료삭제");

					// 이송작업재료등록
					jrParam.setField("YS_STK_BED_NO", commUtils.getValue(gdReq, "YS_STK_BED_NO", ii));
					jrParam.setField("YS_STK_LYR_NO", commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii));
					jrParam.setField("YS_STK_SEQ_NO", Integer.parseInt(commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii)));
					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID")));
					jrParam.setField("MODIFIE", commUtils.trim(gdReq.getParam("userid")));
					jrParam.setField("SSTL_NO", commUtils.getValue(gdReq, "SSTL_NO", ii));

					commDao.insert(jrParam, updCarFtMvMtl, logId, methodNm, "이송작업재료등록");
				}
			}

			// 차량 작업 상태,매수,작업완료시간 update
			jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID")));
			commDao.update(jrParam, updCarSchWrkSt, logId, methodNm, "이송차량스케줄 차량작업상태 수정");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updCarFtMvMtl

	/**
	 * 상차완료백업처리팝업-이송작업재료삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord delCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm = "상차완료백업처리팝업-이송작업재료삭제 [SbrYsJspSeEJB.delCarFtMvMtl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			// 등록 할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					// 이송작업재료삭제
					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID")));
					jrParam.setField("SSTL_NO", commUtils.getValue(gdReq, "OLD_SSTL_NO", ii));

					commDao.insert(jrParam, delCarFtMvMtl, logId, methodNm, "이송작업재료삭제");
				}
			}

			// 차량 작업 상태,매수,작업완료시간 update
			jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID")));
			commDao.update(jrParam, updCarSchWrkSt, logId, methodNm, "이송차량스케줄 차량작업상태 수정");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of delCarFtMvMtl
	
	/**
	 * 상차완료백업처리팝업-이송작업재료위치변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord chgCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm = "상차완료백업처리팝업-이송작업재료위치변경 [SbrYsJspSeEJB.chgCarFtMvMtl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			// 등록 할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					// 이송작업재료삭제
					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID")));
					jrParam.setField("SSTL_NO", commUtils.getValue(gdReq, "SSTL_NO", ii));

					commDao.insert(jrParam, delCarFtMvMtl, logId, methodNm, "이송작업재료위치변경");
				}
			}

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					// 이송작업재료등록
					jrParam.setField("YS_STK_BED_NO", commUtils.getValue(gdReq, "YS_STK_BED_NO", ii));
					jrParam.setField("YS_STK_LYR_NO", commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii));
					jrParam.setField("YS_STK_SEQ_NO", Integer.parseInt(commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii)));
					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID")));
					jrParam.setField("MODIFIE", commUtils.trim(gdReq.getParam("userid")));
					jrParam.setField("SSTL_NO", commUtils.getValue(gdReq, "OLD_SSTL_NO", ii));

					commDao.insert(jrParam, updCarFtMvMtl, logId, methodNm, "이송작업재료등록");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of chgCarFtMvMtl

	/**
	 * 하차백업생성팝업-등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord mkUdCarSch(GridData gdReq) throws DAOException {
		String methodNm = "하차백업생성팝업-등록 [SbrYsJspSeEJB.mkUdCarSch] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("YD_CAR_SCH_ID", commDao.getSeqId(logId, methodNm, "CarSch")); // 야드차량스케쥴ID
			jrParam.setField("REGISTER", commUtils.trim(gdReq.getParam("userid"))); // 야드차량스케쥴ID
			jrParam.setField("YD_EQP_ID", commUtils.trim(gdReq.getParam("YD_EQP_ID"))); // 야드설비ID
			jrParam.setField("YD_CAR_PROG_STAT", "5"); // 차량진행상태 (5:상차완료)
			jrParam.setField("YD_CAR_USE_GP", "L"); // 야드차량사용구분 (L:구내운송, G:출하차량 )
			jrParam.setField("SPOS_WLOC_CD", gdReq.getParam("SPOS_WLOC_CD")); // 발지개소코드(상차지)
			jrParam.setField("ARR_WLOC_CD", gdReq.getParam("ARR_WLOC_CD")); // 착지개소코드(하차지)
			jrParam.setField("YD_PNT_CD", ""); // 야드상차포인트코드(발지)
			jrParam.setField("TRN_EQP_CD", gdReq.getParam("TRN_EQP_CD")); // 운송장비코드

			commDao.insert(jrParam, insYsCarsch, logId, methodNm, "차량스케쥴 상차출발(5)로 INSERT ");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of mkUdCarSch
	
	/**
	 * 야드맵정보 불일치 확인 - L2정보요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord insStrLocMtlIn(GridData gdReq) throws DAOException {
		String methodNm = "야드맵정보 불일치 확인 - L2정보요구[SbrYsJspSeEJB.insStrLocMtlIn] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			JDTORecord jrParam = null;
			jrParam = JDTORecordFactory.getInstance().create();

			jrParam.setField("INF_REQ_NO", commUtils.trim(gdReq.getParam("INF_REQ_NO"))); // 정보요구번호
			jrParam.setField("LOD_LOC", commUtils.trim(gdReq.getParam("LOD_LOC"))); // 요청 적치열
			
			EJBConnector ejbConn = new EJBConnector("default", "SbrYsL2RcvSeEJB", this);
			ejbConn.trx("insStrLocMtlIn", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of mkUdCarSch

	/**
	 * [A] 오퍼레이션명 : 위치검색열-스케줄별 위치검색 기준 저장
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updCrnPosSearchCol_SCH(GridData gdReq) throws DAOException {
		String methodNm = "위치검색열-스케줄별 위치검색 기준 저장[SbrYsJspSeEJB.updCrnPosSearchCol_SCH] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	// Log ID
			jrParam.setResultMsg(methodNm);	// Log Method Name

			String sYdGp    = commUtils.trim(gdReq.getParam("P_YD_GP"));		//야드
			String sYdBayGp = commUtils.trim(gdReq.getParam("P_YD_BAY_GP"));	//동
			String sUserId  = commUtils.trim(gdReq.getParam("userid"));			//사용자
			
			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					jrParam.setField("YD_SCH_CD"    , commUtils.getValue(gdReq, "YD_SCH_CD"    , ii));
					jrParam.setField("YD_STR_GTR_CD", commUtils.getValue(gdReq, "YD_STR_GTR_CD", ii));	//작업구분: [M:주작업],[W:부작업]
					jrParam.setField("YS_ROUTE_GP"  , commUtils.getValue(gdReq, "YS_ROUTE_GP",   ii));	//품명구분: 소형압연에서는 미사용. 작업구분값과 동일하게 입력.
					jrParam.setField("USER_ID"      , sUserId);
					
					commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updCrnPosSearchCol_SCH", logId, methodNm, "위치검색열-스케줄별 위치검색 기준 저장");
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 * [A] 오퍼레이션명 : 위치검색열-미선택된 위치검색 열 추가or삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updCrnPosSearchCol_SelCol(GridData gdReq) throws DAOException {
		String methodNm = "위치검색열-미선택된 위치검색 열 추가or삭제[SbrYsJspSeEJB.updCrnPosSearchCol_SelCol] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			String sYdLSRRS = commUtils.trim(gdReq.getParam("YD_LOC_SRCH_RNG_REG_SNO"));
			String sDelYn   = commUtils.trim(gdReq.getParam("DEL_YN"));
			String sUserId  = commUtils.trim(gdReq.getParam("userid"));
			
			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, sUserId);
			jrParam.setResultCode(logId);	// Log ID
			jrParam.setResultMsg(methodNm);	// Log Method Name
			
			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					
					commUtils.printLog(logId, "YS_STK_COL_GP :: " + commUtils.getValue(gdReq, "YS_STK_COL_GP", ii) , "");
					
					jrParam.setField("YD_LOC_SRCH_RNG_REG_SNO", sYdLSRRS);
					jrParam.setField("YS_STK_COL_GP"          , commUtils.getValue(gdReq, "YS_STK_COL_GP", ii));
					jrParam.setField("USER_ID"                , sUserId);
					jrParam.setField("DEL_YN"                 , sDelYn);
					
					commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updCrnPosSearchCol_SelCol", logId, methodNm, "위치검색열-미선택된 위치검색 열 추가or삭제");
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 크레인작업매수관리 - 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updCrnWrkCntMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업매수관리 - 수정[SbrYsJspSeEJB.updCrnWrkCntMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					
					String ITM_GP = commUtils.getValue(gdReq, "ITEM_GP", ii).trim();
					String T_LWLM = commUtils.getValue(gdReq, "T_LWLM", ii).trim();
					String T_UWLM = commUtils.getValue(gdReq, "T_UWLM", ii).trim();
					String L_LWLM = commUtils.getValue(gdReq, "L_LWLM", ii).trim();
					String L_UWLM = commUtils.getValue(gdReq, "L_UWLM", ii).trim();
					String MTL_CNT = commUtils.getValue(gdReq, "MTL_CNT", ii).trim();
					String R_DESC = commUtils.getValue(gdReq, "R_DESC", ii).trim();
					String REPR_CD_CONTENTS = ITM_GP+"#"+T_LWLM+"#"+T_UWLM+"#"+L_LWLM+"#"+L_UWLM+"#"+MTL_CNT+"#"+R_DESC;
					
					jrParam.setField("REPR_CD_GP", commUtils.getValue(gdReq, "REPR_CD_GP", ii));
					jrParam.setField("CD_GP", commUtils.getValue(gdReq, "CD_GP", ii));
					jrParam.setField("ITEM", commUtils.getValue(gdReq, "ITEM", ii));
					jrParam.setField("REPR_CD_CONTENTS", REPR_CD_CONTENTS);
					jrParam.setField("REGISTER", commUtils.trim(gdReq.getParam("userid")));
					jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));

					commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updCrnWrkCntMgt", logId, methodNm, "크레인작업매수관리 수정");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updCrnWrkCntMgt
	
	/**
	 * 크레인작업매수관리 - 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord delCrnWrkCntMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업매수관리 - 삭제[SbrYsJspSeEJB.delCrnWrkCntMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					
					jrParam.setField("USERID", commUtils.trim(gdReq.getParam("userid")));
					jrParam.setField("REPR_CD_GP", commUtils.getValue(gdReq, "REPR_CD_GP", ii));
					jrParam.setField("CD_GP", commUtils.getValue(gdReq, "CD_GP", ii));
					jrParam.setField("ITEM", commUtils.getValue(gdReq, "ITEM", ii));
					
					commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.delCrnWrkCntMgt", logId, methodNm, "크레인작업매수관리 삭제");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of delCrnWrkCntMgt
	
	/**
	 * 압연출하상 - 추출완료
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updExtCmpl(GridData gdReq) throws DAOException {
		String methodNm = "압연출하상 - 추출완료[SbrYsJspSeEJB.updExtCmpl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name

			// 수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					jrParam.setField("JMS_TC_CD" , 			"YSM5L301"										); // JMSTC코드
					jrParam.setField("YD_EQP_ID", 			commUtils.trim(gdReq.getParam("YD_EQP_ID"))		); // 야드설비ID
					jrParam.setField("YD_STK_BED_STL_SH", 	(ii+1)											); // 야드적치Bed재료매수
					jrParam.setField("YD_EQP_WRK_SH", 		(ii+1)											); // 야드설비작업매수
					jrParam.setField("STL_APPEAR_GP", 		commUtils.getValue(gdReq, "STL_APPEAR_GP", ii)	); // 재료외형구분
					jrParam.setField("L3_HMI", 				""												); // 백업화면 기동 여부
					jrParam.setField("SSTL_NO"+(ii+1), 		commUtils.getValue(gdReq, "SSTL_NO", ii)		); // 재료번호
					
					EJBConnector sndConn = new EJBConnector("default", "SbrYsL2RcvSeEJB", this);
					jrRtn = (JDTORecord)sndConn.trx("rcvM5YSL301", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					
					// com.inisteel.cim.ys.common.dao.YsCommDAO.TcYSM3L201
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updExtCmpl
	
	/**
	 * 압연출하상 - 크레인작업지시 여부
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updCrnWoYn(GridData gdReq) throws DAOException {
		String methodNm = "압연출하상 - 크레인작업지시 여부[SbrYsJspSeEJB.updCrnWoYn] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name
			
			jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("REPR_CD_GP", commUtils.trim(gdReq.getParam("REPR_CD_GP")));
			jrParam.setField("CD_GP", commUtils.trim(gdReq.getParam("CD_GP")));
			jrParam.setField("ITEM", commUtils.trim(gdReq.getParam("ITEM")));
			
			commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updCrnWoYn", logId, methodNm, "크레인작업지시_여부");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updCrnWoYn
	
	/**
	 * 프레스교정기보급요구 - 예정저장위치 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updExtPlnStrLoc(GridData gdReq) throws DAOException {
		String methodNm = "프레스교정기보급요구 - 예정저장위치 등록 [SbrYsJspSeEJB.updExtPlnStrLoc] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("REPR_CD_GP", "APPGI6");
			jrParam.setField("CD_GP", "*");
			jrParam.setField("ITEM", commUtils.trim(gdReq.getParam("EXTPLNSTRLOC")));
			jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
			
			commDao.update(jrParam, "com.inisteel.cim.ys.sbt.dao.updRule", logId, methodNm, "TB_YS_RULE");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}// end of regBulkLMillPlnStrLoc
	
	/**
	 * 차량 - 자동상차완료 여부
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updLdcCmplYn(GridData gdReq) throws DAOException {
		String methodNm = "차량 - 자동상차완료 여부[SbrYsJspSeEJB.updLdcCmplYn] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;

			// DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name
			
			jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("REPR_CD_GP", commUtils.trim(gdReq.getParam("REPR_CD_GP")));
			jrParam.setField("CD_GP", commUtils.trim(gdReq.getParam("CD_GP")));
			jrParam.setField("ITEM", commUtils.trim(gdReq.getParam("ITEM")));
			
			commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updLdcCmplYn", logId, methodNm, "자동상차완료_여부");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updCrnWoYn
	
	// 2026.01.15 소영조 부장님에게 물어본 결과 대차초기화 기능 자체는 없음, 인터페이스로 호출만 하는 기능만 있음
//	/**
//	 * 대차스케줄관리 - 대차초기화
//	 *
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param GridData gdReq
//	 * @return JDTORecord
//	 * @throws DAOException
//	*/
//	public JDTORecord initTcarSchMgt(GridData gdReq) throws DAOException {
//		String methodNm = "대차스케줄관리 대차초기화[SbrYsJspSeEJB.initTcarSchMgt] < " + gdReq.getNavigateValue();
//		String logId = gdReq.getIPAddress();
//
//		try {
//			commUtils.printLog(logId, methodNm, "S+", gdReq);
//
//			//Return Value
//			JDTORecord jrRtn = null;
//
//			String ydEqpId     = ""; //야드설비ID(대차)
//			String ydCurrBayGp = ""; //야드현재동구분(신규)
//			
//			//DAO Parameter - Log ID, Method, 수정자 Set
//			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
//
//			jrParam.setField("YD_EQP_WRK_STAT"    , "U"); //야드설비작업상태(공차)
//			jrParam.setField("YD_CARLD_SCH_REQ_GP", "6"); //야드상차스케쥴요청구분(공대차도착)
//			jrParam.setField("YD_CARUD_SCH_REQ_GP", "3"); //야드하차스케쥴요청구분(영대차도착)
//			jrParam.setField("YD_CAR_PROG_STAT"   , "0"); //야드차량진행상태(상차대기)
//
//			//대차정보
//			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
//			
//			for (int ii = 0; ii < rowCnt; ii++) {
//				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
//					ydEqpId     = commUtils.trim(commUtils.getValue(gdReq, "YD_EQP_ID", ii));
//					ydCurrBayGp = commUtils.trim(commUtils.getValue(gdReq, "YD_CURR_BAY_GP", ii));
//	
//					if ("".equals(ydEqpId)) {
//						throw new Exception("설비ID가 없습니다.");
//					} else if ("".equals(ydCurrBayGp)) {
//						throw new Exception("변경할 현재동이 없습니다.");
//					}
//					
//					/**********************************************************
//					* 2. 기존 대차스케줄/재료 삭제
//					**********************************************************/
//					jrParam.setField("YD_EQP_ID", ydEqpId);
//	
//					//대차이송재료 초기화
//					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updTcarSchInitMtl", logId, methodNm, "대차이송재료 초기화");
//	
//					//대차스케줄 초기화
//					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updTcarSchInitSch", logId, methodNm, "대차스케줄 초기화");
//					
//					/**********************************************************
//					* 3. 신규 대차스케줄 등록
//					**********************************************************/
//					//야드대차스케쥴ID 생성
//					String ydTcarSchId = commDao.getSeqId(logId, methodNm, "TcarSch");
//	
//					if ("".equals(ydTcarSchId)) {
//						throw new Exception( "대차스케줄ID 생성 중 오류가 발생하였습니다.");
//					}
//					
//					//대차스케줄 등록
//					jrParam.setField("YD_TCAR_SCH_ID"   , ydTcarSchId); //야드대차스케쥴ID
//					jrParam.setField("YD_CAR_PROG_STAT" , "0"        ); //야드차량진행상태(상차대기)
//					jrParam.setField("YD_CARLD_STOP_LOC", ydEqpId.substring(0, 1) + ydCurrBayGp + ydEqpId.substring(2)); //야드상차정지위치
//	
//					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updTcarSchInsSch", logId, methodNm, "대차스케줄 등록");
//					
//					/**********************************************************
//					* 4. 대차 현재동 변경
//					**********************************************************/
//					jrParam.setField("YD_EQP_ID"     , ydEqpId    );
//					jrParam.setField("YD_CURR_BAY_GP", ydCurrBayGp);
//	
//					jrRtn = commUtils.addSndData(jrRtn, this.updTcarCurrBay(jrParam));
//				}
//			}
//
//			commUtils.printLog(logId, methodNm, "S-");
//
//			return jrRtn;
//		} catch(DAOException e) {
//			throw e;
//		} catch(Exception e) {
//			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
//		}	
//	} // end of initTcarSchMgt
//	
//	/**
//	 *      [A] 오퍼레이션명 : 대차 현재동 변경
//	 *
//	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 *      @param GridData gdReq
//	 *      @return JDTORecord
//	 *      @throws DAOException
//	*/
//	public JDTORecord updTcarCurrBay(JDTORecord rcvMsg) throws DAOException {
//		String methodNm = "대차 현재동 변경[SbrYsJspSeEJB.updTcarCurrBay] < " + rcvMsg.getResultMsg();
//		String logId = rcvMsg.getResultCode();
//
//		try {
//			commUtils.printLog(logId, methodNm, "S+");
//
//			String ydEqpId        = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID(대차)
//			String ydCurrBayGpNew = commUtils.trim(rcvMsg.getFieldString("YD_CURR_BAY_GP")); //야드현재동구분(신규)
//
//			if ("".equals(ydEqpId)) {
//				throw new Exception("설비ID가 없습니다.");
//			} else if ("".equals(ydCurrBayGpNew)) {
//				throw new Exception("변경할 현재동이 없습니다.");
//			}
//
//			//Return Value
//			JDTORecord jrRtn = null;
//
//			String ydCurrBayGpCur     = ""; //야드현재동구분(현재)
//			String ydStkColGpCur      = ""; //야드적치열구분(현재)
//			String ydStkColGpNew      = ""; //야드적치열구분(신규)
//			String ydStkBedActStatCur = ""; //야드적치Bed활성상태(현재Bed)
//			String ydStkBedActStatNew = ""; //야드적치Bed활성상태(신규Bed)
//			
//			//DAO Parameter - Log ID, Method, 수정자 Set
//			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));
//			jrParam.setResultCode(logId);	//Log ID
//			jrParam.setResultMsg(methodNm);	//Log Method Name
//			jrParam.setField("YD_EQP_ID"         , ydEqpId       );
//			jrParam.setField("YD_CURR_BAY_GP_NEW", ydCurrBayGpNew);
//			
//			/**********************************************************
//			* 1. 대차Bed상태 조회
//			**********************************************************/
//			JDTORecordSet jsTcar = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatTcarBed", logId, methodNm, "대차Bed상태 조회");
//
//			if (jsTcar != null && jsTcar.size() > 0) {
//		    	JDTORecord jrTcar = jsTcar.getRecord(0);
//
//		    	ydCurrBayGpCur     = commUtils.trim(jrTcar.getFieldString("YD_CURR_BAY_GP"         ));
//			    ydStkColGpCur      = commUtils.trim(jrTcar.getFieldString("YD_STK_COL_GP_CUR"      ));
//			    ydStkColGpNew      = commUtils.trim(jrTcar.getFieldString("YD_STK_COL_GP_NEW"      ));
//			    ydStkBedActStatCur = commUtils.trim(jrTcar.getFieldString("YD_STK_BED_ACT_STAT_CUR"));
//			    ydStkBedActStatNew = commUtils.trim(jrTcar.getFieldString("YD_STK_BED_ACT_STAT_NEW"));
//
//			    if ("".equals(ydStkColGpNew)) {
//					throw new Exception("변경할 적치열이 없습니다.");
//				} else if ("".equals(ydStkBedActStatNew)) {
//					throw new Exception("변경할 Bed[" + ydStkColGpNew + "] 활성상태가 없습니다.");
//				}
//		    } else {
//				throw new Exception("대차 Bed상태 정보가 없습니다.");
//		    }
//			
//			/**********************************************************
//			* 2. 대차 저장위치 전체 비 활성화
//			**********************************************************/
//			jrParam.setField("YD_STK_COL_GP", ydEqpId.substring(0, 1) + "_" + ydEqpId.substring(2)); //야드적치열구분(대차전체Bed)
//
//			//적치Bed(전체) 비활성화
//			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatStkBedActCA", logId, methodNm, "적치Bed(전체) 비활성화");
//
//			//적치단 재료 삭제
//			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClr", logId, methodNm, "적치단 재료 삭제");
//
//			/**********************************************************
//			* 3. 현재동 변경 및 저장위치제원 전문 조회
//			**********************************************************/
//			if (!ydCurrBayGpCur.equals(ydCurrBayGpNew)) {
//				//설비 현재동 수정
//				jrParam.setField("YD_CURR_BAY_GP", ydCurrBayGpNew);
//
//				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdEqpCurrBay", logId, methodNm, "설비 현재동 수정");
//
//				//기존 Bed의 상태가 변경되었으면 저장위치제원 전문 조회
//				if ("L".equals(ydStkBedActStatCur)) {
//					jrParam.setField("YD_INFO_SYNC_CD", "4"          ); //야드정보동기화코드(Bed)
//					jrParam.setField("YS_STK_COL_GP"  , ydStkColGpCur); //야드적치열구분
//					jrParam.setField("YS_STK_BED_NO"  , "01"         ); //야드적치Bed번호
//
//					//전송Data 조회
//					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN4L001", jrParam));
//				}
//			}
//			
//			/**********************************************************
//			* 4. 신규 저장위치  활성화 및 저장위치제원 전문 조회
//			**********************************************************/
//			//신규 적치Bed Close 상태이면 활성화
//			jrParam.setField("YD_STK_COL_GP"      , ydStkColGpNew); //야드적치열구분
//			jrParam.setField("YD_STK_BED_NO"      , "01"         ); //야드적치Bed번호
//			jrParam.setField("YD_STK_BED_ACT_STAT", "L"          ); //야드적치Bed활성상태(적치가능)
//
//			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatStkBedAct", logId, methodNm, "신규 적치Bed Close 상태이면 활성화");
//
//			//적치단 재료 삭제
//			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClr", logId, methodNm, "적치단 재료 삭제");
//
//			//신규 Bed의 상태가 변경되었으면 저장위치제원 전문 전송
//			if ("C".equals(ydStkBedActStatNew)) {
//				jrParam.setField("YD_INFO_SYNC_CD", "4"); //야드정보동기화코드(Bed)
//				jrParam.setField("YS_STK_COL_GP"  , ydStkColGpNew); //야드적치열구분
//				jrParam.setField("YS_STK_BED_NO"  , "01"         ); //야드적치Bed번호
//
//				//전송Data 조회
//				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN4L001", jrParam));
//			}
//
//			commUtils.printLog(logId, methodNm, "S-");
//
//			return jrRtn;
//		} catch(DAOException e) {
//			throw e;
//		} catch(Exception e) {
//			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
//		}	
//	} // end of updTcarCurrBay
	
	/**
	 * 대차스케줄관리 - 작업예약 우선순위변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord upTcardWrkBookPrior(GridData gdReq) throws DAOException {
		String methodNm = "작업예약 우선순위변경[SbrYsJspSeEJB.upTcardWrkBookPrior] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();

			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					jrParam.setField("MODIFIER", 		gdReq.getParam("userid")						);
					jrParam.setField("YD_SCH_PRIOR", 	commUtils.getValue(gdReq, "YD_SCH_PRIOR", ii)	);
					jrParam.setField("YD_WBOOK_ID", 	commUtils.getValue(gdReq, "YD_WBOOK_ID", ii)	);
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdWbPrior", logId, methodNm, "작업예약(TB_YD_WRKBOOK) 야드스케쥴우선순위 수정");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of upTcardWrkBookPrior
	
	/**
	 * 대차작업관리 - 작업예약삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delTcarWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "작업예약삭제[SbrYsJspSeEJB.delTcarWrkBook] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydWbookId = ""; //야드작업예약ID
			String ydTcarSchId = ""; // 야드대차스케쥴ID(대차상차작업예약ID)
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();

			//작업예약
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			/**********************************************************
			* 1. 크레인스케줄 존재여부 Check
			**********************************************************/
			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					ydWbookId = commUtils.getValue(gdReq, "YD_WBOOK_ID", ii);
					ydTcarSchId = commUtils.getValue(gdReq, "YD_TCAR_SCH_ID", ii);
					jrParam.setField("YD_WBOOK_ID", ydWbookId);
					
					//작업예약 크레인스케줄정보 조회
					JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCommWbCrnSch", logId, methodNm, "작업예약 크레인스케줄정보 조회"); 
	
				    if (jsCrn != null && jsCrn.size() > 0) {
						StringBuffer sbMsg = new StringBuffer();
						sbMsg = sbMsg.append("작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 " + jsCrn.size() + " 건 존재합니다.");
//						for (int mm = 0; mm < jsCrn.size(); mm++) {
//							sbMsg = sbMsg.append("\n" + mm + " : " + jsCrn.getRecord(mm).getFieldString("YD_CRN_SCH_ID"));	//야드크레인스케쥴ID
//						}
						jrRtn.addField("errorMsg", sbMsg.toString());
				    } else {
				    
					    jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
		
	//					//차량/대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
	//				    arrCar[ii][0] = modifier;	//수정자
	//				    arrCar[ii][1] = ydWbookId;	//야드작업예약ID
	//				    arrCar[ii][2] = ydWbookId;	//야드작업예약ID
	//				    arrCar[ii][3] = ydWbookId;	//야드작업예약ID
	//				    arrCar[ii][4] = ydWbookId;	//야드작업예약ID
	//				
	//					//작업예약/재료 삭제
	//				    arrWrkBook[ii][0] = modifier;	//수정자
	//				    arrWrkBook[ii][1] = ydWbookId;	//야드작업예약ID
					    
					    /**********************************************************
						* 2. 차량/대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
						**********************************************************/
						//차량스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommCarSchWbDel", logId, methodNm, "TB_YS_CARSCH");				
					
						//대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommTcarSchWbDel", logId, methodNm, "TB_YS_TCARSCH");				
						
						/**********************************************************
						* 4. 작업예약/재료 삭제
						**********************************************************/
						//작업예약재료 삭제
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");				
	
						//작업예약 삭제
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
						
						/**********************************************************
						* 5. 대차작업예약/재료 삭제
						**********************************************************/
						
						JDTORecord jrParam3 = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
						
						jrParam3.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
						jrParam3.setField("YD_TCAR_SCH_ID", ydTcarSchId);
						
						// 대차작업예약재료 삭제
						commDao.update(jrParam3, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnTcarSch", logId, methodNm, "TB_YS_TCARSCH");
	
						// 대차작업예약 삭제
						commDao.update(jrParam3, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnTcarFtmvMtl", logId, methodNm, "TB_YS_TCARFTMVMTL");
						
						jrRtn = null;
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
	} // end of delTcarWrkBook
	
	/**
	 *      [A] 오퍼레이션명 : 대차상태설정 등록처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtTcarStatSet(GridData gdReq) throws DAOException {
		String methodNm = "대차상태설정 등록처리[SbrYsJspSeEJB.trtTcarStatSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String trtDtlGp = commUtils.trim(gdReq.getParam("TRT_DTL_GP"));	//처리상세구분
			String ydEqpId  = commUtils.trim(gdReq.getParam("YD_EQP_ID" ));	//야드설비ID(대차)
			String modifier = commUtils.trim(gdReq.getParam("userid"      ));	//수정자
			String currDate = commUtils.getDateTime14();						//현재시각

			if ("".equals(ydEqpId)) {
				throw new Exception("대차설비ID가 없습니다.");
			}

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//EJB Call을 위한 Message 생성용 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
			
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			jrYdMsg.setField("YD_EQP_ID", ydEqpId); //야드설비ID
			
			JDTORecordSet jsCol = commDao.select(jrYdMsg, "com.inisteel.cim.ys.sbr.dao.getTcarSchMgtTC", logId, methodNm, "현재 대차위치 조회");
			
			if( jsCol.size() > 0 ) {
				jsCol.first();
				JDTORecord recInPara = jsCol.getRecord();

				if ("ST".equals(trtDtlGp)) { // 설비상태변경
					jrYdMsg.setField("JMS_TC_CD"          , "N7YSL303"); //설비고장복구실적
					jrYdMsg.setField("YD_EQP_STAT"        , commUtils.trim(gdReq.getParam("YD_EQP_STAT"))); //야드설비상태(B:고장, N:정상)
					jrYdMsg.setField("YD_EQP_PAUSE_CODE"  , "0000"    ); //야드설비휴지코드
					jrYdMsg.setField("YD_EQP_TRBL_RCVR_DT", currDate  ); //야드설비고장복구일시
	
					EJBConnector sndConn = new EJBConnector("default", "SbrYsL2RcvSeEJB", this);
					jrRtn = (JDTORecord)sndConn.trx("rcvN7YSL303", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
//				} else if ("MD".equals(trtDtlGp)) {
//					//작업Mode 변경
//					jrYdMsg.setField("JMS_TC_CD"      , "N4YSL003"); //설비운전모드전환
//					jrYdMsg.setField("YD_EQP_WRK_MODE", commUtils.trim(gdReq.getParam("YD_EQP_WRK_MODE"))); //야드설비작업Mode(1:On-Line, 0:Off-Line)
//	
//					EJBConnector sndConn = new EJBConnector("default", "SbrYsL2RcvSeEJB", this);
//					jrRtn = (JDTORecord)sndConn.trx("rcvN4YSL003", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				} else if ("HB".equals(trtDtlGp)) { // Home동변경
					JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);
	
					jrParam.setField("YD_EQP_ID"     , ydEqpId); //야드설비ID
					jrParam.setField("YD_HOME_BAY_GP", commUtils.trim(gdReq.getParam("YD_HOME_BAY_GP")));
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdEqpHomeBay", logId, methodNm, "Home동 변경");
										
				} else if ("CB".equals(trtDtlGp)) { // 현재동변경
					jrYdMsg.setField("YD_CURR_BAY_GP", commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP")));
	
					this.updTcarCurrBay(jrYdMsg); // 현재동 변경 후 
					
				} else if ("TS".equals(trtDtlGp)) { // 대차이동요청
//					jrYdMsg.setField("YD_BAY_GP", commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TS"))); //야드동구분(상차동)
//					jrYdMsg.setField("YD_L2_ID" , "N5"    );
//					jrYdMsg.setField("YD_CURR_BAY_GP" , commUtils.trim(recInPara.getFieldString("YD_CURR_BAY_GP"))); //현위치
//					jrYdMsg.setField("YD_EQP_WRK_STAT" , commUtils.trim(recInPara.getFieldString("YD_EQP_WRK_STAT")));
//					
//					jrRtn = ysComm.trtTcarSchLevWo(jrYdMsg);
					
					String sYD_EQP_WRK_STAT = commUtils.trim(recInPara.getFieldString("YD_EQP_WRK_STAT"));
					String sYD_CURR_BAY_GP = commUtils.trim(recInPara.getFieldString("YD_CURR_BAY_GP"));
					String sARR_BAY_GP = "";
					String EQP_WRK_STAT = "";
					
					if(sYD_CURR_BAY_GP.equals("A")) {
						sARR_BAY_GP = "B";
					} else {
						sARR_BAY_GP = "A";
					}
					
					if(sYD_EQP_WRK_STAT.equals("") || sYD_EQP_WRK_STAT == null) {
						EQP_WRK_STAT = "U";
					} else {
						EQP_WRK_STAT = sYD_EQP_WRK_STAT;
					}
					
					// 대차이동요청(YSN7L312)
					jrYdMsg.setField("YD_EQP_ID", 		ydEqpId);
					jrYdMsg.setField("TCAR_LOC", 		sARR_BAY_GP); 		// 목표동
					jrYdMsg.setField("YD_EQP_WRK_STAT", EQP_WRK_STAT); 		// 적재상태
					jrYdMsg.setField("ST_LOC", 			sYD_CURR_BAY_GP); 	// 출발동
					jrYdMsg.setField("END_LOC", 		sARR_BAY_GP); 		// 도착동
					
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L312", jrYdMsg));
					
				} else if ("TL".equals(trtDtlGp)) { // 출발실적처리
					String sSTLOC = "";
					String YD_CURR_BAY_GP_TL = gdReq.getParam("YD_CURR_BAY_GP_TL");
					
					if(YD_CURR_BAY_GP_TL.equals("A")) {
						sSTLOC = "F";
					} else {
						sSTLOC = "B";
					}
					
					jrYdMsg.setField("JMS_TC_CD"    , "N7YSL323"); //대차이동실적
					jrYdMsg.setField("YD_EQP_ID"	, ydEqpId   ); //야드설비ID
					jrYdMsg.setField("MOVE_GP"		, "S"       ); //야드대차이동구분(출발)
					jrYdMsg.setField("ST_LOC"		, sSTLOC    ); //야드대차이동방향
					jrYdMsg.setField("CURR_LOC"		, commUtils.trim(recInPara.getFieldString("YD_CURR_BAY_GP"))); //현위치
					jrYdMsg.setField("END_LOC"   	, commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TL"))); //야드동구분1(현재동)
	
					EJBConnector sndConn = new EJBConnector("default", "SbrYsL2RcvSeEJB", this);
					jrRtn = (JDTORecord)sndConn.trx("rcvN7YSL323", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				} else if ("TA".equals(trtDtlGp)) { // 도착실적처리
					String sSTLOC = "";
					String YD_CURR_BAY_GP_TL = gdReq.getParam("YD_CURR_BAY_GP_TL");
					
					if(YD_CURR_BAY_GP_TL.equals("A")) {
						sSTLOC = "F";
					} else {
						sSTLOC = "B";
					}
					
					jrYdMsg.setField("JMS_TC_CD"	, "N7YSL323"); //대차이동실적
					jrYdMsg.setField("YD_EQP_ID"	, ydEqpId   ); //야드설비ID
					jrYdMsg.setField("MOVE_GP"		, "E"       ); //야드대차이동구분(도착)
					jrYdMsg.setField("ST_LOC"		, sSTLOC    ); //야드대차이동방향
					jrYdMsg.setField("CURR_LOC"		, commUtils.trim(recInPara.getFieldString("YD_CURR_BAY_GP"))); //현위치
					jrYdMsg.setField("END_LOC"		, commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TA"))); //야드동구분1(현재동)
	
					EJBConnector sndConn = new EJBConnector("default", "SbrYsL2RcvSeEJB", this);
					jrRtn = (JDTORecord)sndConn.trx("rcvN7YSL323", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				} else if ("TC".equals(trtDtlGp)) { // 완료실적처리
					String ydCarProgStat = commUtils.trim(gdReq.getHeader("YD_CAR_PROG_STAT").getValue(0));	//야드차량진행상태
					
					if ("4".equals(ydCarProgStat)) {
						//상차개시 -> 상차완료(영대차출발지시)
						jrRtn = ysComm.trtTcarSchLdCmpl(jrYdMsg);
					} else if ("D".equals(ydCarProgStat)) {
						//하차개시 -> 하차완료(공대차출발지시)
						jrYdMsg.setField("YD_BAY_GP", commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TC"))); //야드동구분(공대차출발지시 상차동)
						jrYdMsg.setField("YD_L2_ID", "N5");
						jrYdMsg.setField("YD_CURR_BAY_GP" , commUtils.trim(recInPara.getFieldString("YD_CURR_BAY_GP"))); //현위치
						jrYdMsg.setField("YD_EQP_WRK_STAT" , commUtils.trim(recInPara.getFieldString("YD_EQP_WRK_STAT")));
						
						jrRtn = ysComm.trtTcarSchUdCmpl(jrYdMsg);
					}
				}
//				else {
//					throw new Exception("정의되지 않은 처리구분[" + trtDtlGp + "] 입니다.");
//				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of trtTcarStatSet
	
	/**
	 *      [A] 오퍼레이션명 : 대차 현재동 변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updTcarCurrBay(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차 현재동 변경[SbrYsJspSeEJB.updTcarCurrBay] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydEqpId        = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID(대차)
			String ydCurrBayGpNew = commUtils.trim(rcvMsg.getFieldString("YD_CURR_BAY_GP")); //야드현재동구분(신규)

			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
			} else if ("".equals(ydCurrBayGpNew)) {
				throw new Exception("변경할 현재동이 없습니다.");
			}

			//Return Value
			JDTORecord jrRtn = null;

			String ydCurrBayGpCur     = ""; //야드현재동구분(현재)
			String ydStkColGpCur      = ""; //야드적치열구분(현재)
			String ydStkColGpNew      = ""; //야드적치열구분(신규)
			String ydStkBedActStatCur = ""; //야드적치Bed활성상태(현재Bed)
			String ydStkBedActStatNew = ""; //야드적치Bed활성상태(신규Bed)
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"         , ydEqpId       );
			jrParam.setField("YD_CURR_BAY_GP_NEW", ydCurrBayGpNew);
			
			/**********************************************************
			* 1. 대차Bed상태 조회
			**********************************************************/
			JDTORecordSet jsTcar = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getStatTcarBed", logId, methodNm, "대차Bed상태 조회");
			// com.inisteel.cim.ys.common.dao.YsCommDAO.getStatTcarBed
			if (jsTcar != null && jsTcar.size() > 0) {
		    	JDTORecord jrTcar = jsTcar.getRecord(0);

		    	ydCurrBayGpCur     = commUtils.trim(jrTcar.getFieldString("YD_CURR_BAY_GP"         ));
			    ydStkColGpCur      = commUtils.trim(jrTcar.getFieldString("YD_STK_COL_GP_CUR"      ));
			    ydStkColGpNew      = commUtils.trim(jrTcar.getFieldString("YD_STK_COL_GP_NEW"      ));
			    ydStkBedActStatCur = commUtils.trim(jrTcar.getFieldString("YD_STK_BED_ACT_STAT_CUR"));
			    ydStkBedActStatNew = commUtils.trim(jrTcar.getFieldString("YD_STK_BED_ACT_STAT_NEW"));

			    if ("".equals(ydStkColGpNew)) {
					throw new Exception("변경할 적치열이 없습니다.");
				} else if ("".equals(ydStkBedActStatNew)) {
					throw new Exception("변경할 Bed[" + ydStkColGpNew + "] 활성상태가 없습니다.");
				}
		    } else {
				throw new Exception("대차 Bed상태 정보가 없습니다.");
		    }
			
			/**********************************************************
			* 2. 대차 저장위치 전체 비 활성화
			**********************************************************/
			jrParam.setField("YD_STK_COL_GP", ydEqpId.substring(0, 1) + "_" + ydEqpId.substring(2)); //야드적치열구분(대차전체Bed)

			//적치Bed(전체) 비활성화
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatStkBedActCA", logId, methodNm, "적치Bed(전체) 비활성화");

			//적치단 재료 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClr", logId, methodNm, "적치단 재료 삭제");

			/**********************************************************
			* 3. 현재동 변경 및 저장위치제원 전문 조회
			**********************************************************/
			if (!ydCurrBayGpCur.equals(ydCurrBayGpNew)) {
				//설비 현재동 수정
				jrParam.setField("YD_CURR_BAY_GP", ydCurrBayGpNew);

				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdEqpCurrBay", logId, methodNm, "설비 현재동 수정");

				//기존 Bed의 상태가 변경되었으면 저장위치제원 전문 조회
				//2026.03.24 전문 전송 시 해당 컬럼의 파라미터 문제가 있어서 주석처리
//				if ("L".equals(ydStkBedActStatCur)) {
//					jrParam.setField("YD_INFO_SYNC_CD", "4"          ); //야드정보동기화코드(Bed)
//					jrParam.setField("YS_STK_COL_GP"  , ydStkColGpCur); //야드적치열구분
//					jrParam.setField("YS_STK_BED_NO"  , "01"         ); //야드적치Bed번호
//
//					//전송Data 조회
//					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L301", jrParam)); // YSN4L001
//				}
			}
			
			/**********************************************************
			* 4. 신규 저장위치  활성화 및 저장위치제원 전문 조회
			**********************************************************/
			//신규 적치Bed Close 상태이면 활성화
			jrParam.setField("YD_STK_COL_GP"      , ydStkColGpNew); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO"      , "01"         ); //야드적치Bed번호
			jrParam.setField("YD_STK_BED_ACT_STAT", "L"          ); //야드적치Bed활성상태(적치가능)

			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatStkBedAct", logId, methodNm, "신규 적치Bed Close 상태이면 활성화");

			//적치단 재료 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClr", logId, methodNm, "적치단 재료 삭제");

			//신규 Bed의 상태가 변경되었으면 저장위치제원 전문 전송
			//2026.03.24 전문 전송 시 해당 컬럼의 파라미터 문제가 있어서 주석처리
//			if ("C".equals(ydStkBedActStatNew)) {
//				jrParam.setField("YD_INFO_SYNC_CD", "4"); //야드정보동기화코드(Bed)
//				jrParam.setField("YS_STK_COL_GP"  , ydStkColGpNew); //야드적치열구분
//				jrParam.setField("YS_STK_BED_NO"  , "01"         ); //야드적치Bed번호
//
//				//전송Data 조회
//				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L301", jrParam)); // YSN4L001
//			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updTcarCurrBay
	
	/**
	 * 이송Lot List - 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updPrepSchLot(GridData gdReq) throws DAOException {
		String methodNm = "이송Lot List - 수정[SbrYsJspSeEJB.updPrepSchLot] < ";
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					jrParam.setField("YD_PREP_SCH_ID"	, commUtils.nvl(commUtils.getValue(gdReq, "YD_PREP_SCH_ID", ii), ""));
					jrParam.setField("YD_AIM_BAY_GP"	, commUtils.nvl(commUtils.getValue(gdReq, "YD_AIM_BAY_GP", ii), "")); 
					jrParam.setField("YD_CARASGN_SEQ"	, commUtils.nvl(commUtils.getValue(gdReq, "YD_CARASGN_SEQ", ii), "")); 
					
					//준비스케줄 수정
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updPrepSch", logId, methodNm, "준비스케줄 수정");
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updPrepSchLot
	
	/**
	 * 이송Lot List - 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delPrepSchLot(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "이송Lot List - 삭제[SbtYsJspSeEJB.delPrepSchLot] < ";
		String logId = gdReq[0].getRequestUserIp();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet rsResult = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq[0].getFieldString("userid")));
			
			for (int ii = 0; ii < gdReq.length; ii++) {
				
				jrParam.setField("YD_PREP_SCH_ID"	, commUtils.nvl(gdReq[ii].getFieldString("YD_PREP_SCH_ID"), ""));
				
				//준비스케줄 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updPrepSchDelY", logId, methodNm, "준비스케줄 삭제");
				
				//이송LOT ID로 재료번호 조회
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.selSSTLNO", logId, methodNm, "재료번호 조회");
				
				// 재료번호 갯수 만큼 작업
				for(int jj = 0; jj < rsResult.size(); jj++) {
					String v_SSTL_NO = rsResult.getRecord(jj).getFieldString("SSTL_NO");
					
					jrParam.setField("SSTL_NO", v_SSTL_NO);
					
					//준비재료 삭제
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updPrepMtlDelY", logId, methodNm, "준비재료 삭제");
					
					// 소재이송지시 취소
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStlFrtonMoveStat", logId, methodNm, "소재이송지시 취소");
					
					v_SSTL_NO = "";
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delPrepSchLot
	
	/**
	 * 이송Lot List - 재료삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delPrepMtl(JDTORecord[] gdReq) throws DAOException {
		String methodNm = "이송Lot List - 재료삭제[SbrYsJspSeEJB.delPrepMtl] < ";
		String logId = gdReq[0].getRequestUserIp();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq[0].getFieldString("userid")));
			
			for (int ii = 0; ii < gdReq.length; ii++) {
				
				//준비재료 삭제
				jrParam.setField("SSTL_NO"			, commUtils.nvl(gdReq[ii].getFieldString("SSTL_NO"), "")); 
				jrParam.setField("YD_PREP_SCH_ID"	, commUtils.nvl(gdReq[ii].getFieldString("YD_PREP_SCH_ID"), "")); 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updPrepMtlDelY", logId, methodNm, "준비재료 삭제");
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delPrepMtl
	
	/**
	 * 소형출하상 작업현황 - 예정저장위치, 추출크레인 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord regSmszStbrPlnStrLoc(GridData gdReq) throws DAOException {
		String methodNm = "소형출하상 작업현황 - 예정저장위치 등록 [SbrYsJspSeEJB.regSmszStbrPlnStrLoc] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			// Return Value
			JDTORecord jrRtn = null;
			JDTORecord jrParam1 = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrParam2 = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String Name = gdReq.getParam("NAME");
			 
			// 예정저장위치
			if(Name.equals("PlnStrLoc")) {
			
				jrParam1.setField("REPR_CD_GP", "GD0002");
				jrParam1.setField("CD_GP", commUtils.trim(gdReq.getParam("YD_STR_LOC")));
				jrParam1.setField("ITEM", commUtils.trim(gdReq.getParam("PLNSTRLOC")));
				jrParam1.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
				
				commDao.update(jrParam1, "com.inisteel.cim.ys.sbr.dao.updRule", logId, methodNm, "TB_YS_RULE");
			
			}
			
			// 추출 크레인
			if(Name.equals("ExtCrn")) {
				
				jrParam2.setField("REPR_CD_GP", "GD0003");
				jrParam2.setField("CD_GP", "*");
				jrParam2.setField("ITEM", commUtils.trim(gdReq.getParam("YD_WRK_CRN")));
				jrParam2.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
				
				commDao.update(jrParam2, "com.inisteel.cim.ys.sbr.dao.updRule", logId, methodNm, "TB_YS_RULE");
			
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}// end of regBulkLMillPlnStrLoc
	
	/**
	 * 대차스케줄관리 - 작업예약 우선순위변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updWrkBookPrior(GridData gdReq) throws DAOException {
		String methodNm = "작업예약 우선순위변경[SbrYsJspSeEJB.updWrkBookPrior] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();

			//작업예약
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
					jrParam.setField("YD_SCH_PRIOR", commUtils.getValue(gdReq, "YD_SCH_PRIOR", ii)); //야드스케쥴우선순위
					jrParam.setField("YD_WBOOK_ID", commUtils.getValue(gdReq, "YD_WBOOK_ID", ii));   //야드작업예약ID
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdWbPrior", logId, methodNm, "TB_YS_WRKBOOK");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updWrkBookPrior
	
	/**
	 * 보류재 등록 - 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regRsgd(GridData gdReq) throws DAOException {
		String methodNm = "보류재 등록 - 등록[SbrYsJspSeEJB.regRsgd] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();

			//작업예약
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
					jrParam.setField("BLT_NO", 			commUtils.getValue(gdReq, "BLT_NO", ii));			// 빌렛번호
					jrParam.setField("BLM_NO", 			commUtils.getValue(gdReq, "BLM_NO", ii));			// 블룸번호
					jrParam.setField("HEAT_NO", 		commUtils.getValue(gdReq, "HEAT_NO", ii));			// 히트번호
					jrParam.setField("BLT_T", 			commUtils.getValue(gdReq, "BILLET_T", ii));			// 두께
					jrParam.setField("BLT_W", 			commUtils.getValue(gdReq, "BILLET_W", ii));			// 폭
					jrParam.setField("BLT_L", 			commUtils.getValue(gdReq, "BILLET_LEN", ii));		// 길이
					jrParam.setField("BLT_WT", 			commUtils.getValue(gdReq, "BILLET_WT", ii));		// 중량
					jrParam.setField("HOLD_GP", 		commUtils.getValue(gdReq, "HOLD_GP", ii));			// 보류처리구분
					jrParam.setField("HOLD_CD", 		commUtils.getValue(gdReq, "HOLD_CD", ii));			// 보루사유
					jrParam.setField("HOLD_TREAT_WAY", 	commUtils.getValue(gdReq, "HOLD_TREAT_WAY", ii));	// 처리방안 
					jrParam.setField("HOLD_REL_CHLD", 	commUtils.trim(gdReq.getParam("userid")));			// 보류등록자
					jrParam.setField("REGISTER", 		commUtils.trim(gdReq.getParam("userid")));
					jrParam.setField("MODIFIER", 		commUtils.trim(gdReq.getParam("userid")));
					
					commDao.insert(jrParam, "com.inisteel.cim.ys.sbr.dao.insRsgdReg", logId, methodNm, "TB_YS_BLTHOLDWR");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of regRsgd
	
	/**
	 * 보류재 조회 - 보류해제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord udpRsgdRel(GridData gdReq) throws DAOException {
		String methodNm = "보류재 조회 - 보류해제[SbrYsJspSeEJB.udpRsgdRel] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();

			//작업예약
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				if ("1".equals(commUtils.getValue(gdReq, "CHECK", ii))) {
//					jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
//					jrParam.setField("YD_SCH_PRIOR", commUtils.getValue(gdReq, "YD_SCH_PRIOR", ii)); //야드스케쥴우선순위
//					jrParam.setField("YD_WBOOK_ID", commUtils.getValue(gdReq, "YD_WBOOK_ID", ii));   //야드작업예약ID
//					
//					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdWbPrior", logId, methodNm, "TB_YS_WRKBOOK");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of udpRsgdRel
}	