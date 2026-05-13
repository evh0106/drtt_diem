/**
 * @(#)SlabYdJspFaEJBBean
 *
 * @version          V1.00
 * @author           허철호
 * @date             2013/03/04
 *
 * @description      Slab야드 화면 관리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2013/03/04   허철호      허철호      최초 등록
 */
package com.inisteel.cim.yd.slabyd.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.jsp.common.YDComUtil;

/**
 *      [A] 클래스명 : Slab야드 화면관리
 *
 * @ejb.bean name="SlabYdJspFaEJB" jndi-name="SlabYdJspFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class SlabYdJspFaEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YdSlabUtils slabUtils = new YdSlabUtils();
	YDComUtil   ydComUtil = new YDComUtil();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	/***************************************************************************
	 * Slab야드 화면
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 크레인작업관리 [C연주Slab야드, 후판Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrCrnWrkMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리[SlabYdJspFaEJB.scrCrnWrkMgt]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("CM".equals(trtGp)) {
				methodNm += " 크레인변경";
				ejbMethod = "trtCrnWrkMgtCM";
			} else if ("PM".equals(trtGp)) {
				methodNm += " 순위변경";
				ejbMethod = "trtCrnWrkMgtPM";
			} else if ("DM".equals(trtGp)) {
				methodNm += " 권하위치변경";
				ejbMethod = "trtCrnWrkMgtDM";
			} else if ("HD".equals(trtGp)) {
				methodNm += " 보류";
				ejbMethod = "trtCrnWrkMgtHR";
			} else if ("HR".equals(trtGp)) {
				methodNm += " 보류해제";
				ejbMethod = "trtCrnWrkMgtHR";
			} else if ("WC".equals(trtGp)) {
				methodNm += " 작업취소";
				ejbMethod = "trtCrnWrkMgtWC";
			} else if ("SC".equals(trtGp)) {
				methodNm += " 스케줄취소";
				ejbMethod = "trtCrnWrkMgtSC";
			} else if ("SM".equals(trtGp)) {
				methodNm += " 스케줄재료조회";
				ejbMethod = "getCrnWrkMgt";
			} else if ("SS".equals(trtGp)) {
				methodNm += " 스케줄정보조회";
				ejbMethod = "getCrnWrkMgt";
			} else if ("SL".equals(trtGp)) {
				methodNm += " 권하불가위치조회";
				ejbMethod = "getYdDnWoLocNotAllowedInfo";				
			} else if ("SN".equals(trtGp)) {
				methodNm += " 권하불가재료조회";
				ejbMethod = "getSlabDnNotAllowedInfo";				
			} else if ("SD".equals(trtGp)) {
				methodNm += "중복적치재료조회";
				ejbMethod = "getMvStkWrkBookRegDup";	
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);

			if (ejbMethod.startsWith("trt")) {
				//등록처리      
				JDTORecord jrRst = (JDTORecord)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);

					EJBConnector sndConn = new EJBConnector("default", "YdCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}

				//스케줄정보조회
				ejbMethod = "getCrnWrkMgt";
			}

			//조회
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 크레인상태설정 [C연주Slab야드, 후판Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrCrnStatSet(GridData gdReq) throws DAOException {
		String methodNm = "크레인상태설정[SlabYdJspFaEJB.scrCrnStatSet]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("CS".equals(trtGp)) {
				methodNm += " 등록처리";
				ejbMethod = "trtCrnStatSet";
			} else if ("ST".equals(trtGp)) {
				methodNm += " 상태정보조회";
				ejbMethod = "getCrnStatSet";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);

			if ("CS".equals(trtGp)) {
				//크레인상태설정
				JDTORecord jrRst = (JDTORecord)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);

					EJBConnector sndConn = new EJBConnector("default", "YdCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
			}

			//조회
			gdRtn = (GridData)ejbConn.trx("getCrnStatSet", new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업예약관리 [C연주Slab야드, 후판Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrCrnWrkBookMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업예약관리[SlabYdJspFaEJB.scrCrnWrkBookMgt]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("SC".equals(trtGp)) {
				methodNm += " 스케줄점검";
				ejbMethod = "chkCrnWrkBookMgtSC";
			} else if ("SS".equals(trtGp)) {
				methodNm += " 스케줄기동";
				ejbMethod = "trtCrnWrkBookMgtSS";
			} else if ("PM".equals(trtGp)) {
				methodNm += " 우선순위변경";
				ejbMethod = "updWrkBookPrior";
			} else if ("CM".equals(trtGp)) {
				methodNm += " 크레인지정";
				ejbMethod = "updWrkBookCrn";
			} else if ("WD".equals(trtGp)) {
				methodNm += " 작업예약삭제";
				ejbMethod = "delWrkBook";
			} else if ("SM".equals(trtGp)) {
				methodNm += " 작업예약재료조회";
				ejbMethod = "getCrnWrkBookMgt";
			} else if ("SW".equals(trtGp)) {
				methodNm += " 작업예약정보조회";
				ejbMethod = "getCrnWrkBookMgt";
			} else if ("TM".equals(trtGp)) {
				methodNm += " To위치변경";
				ejbMethod = "updWrkBookToGuide";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);

			if (ejbMethod.startsWith("get")) {
				//조회
				gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });
			} else if ("SC".equals(trtGp)) {
				//스케줄점검
				String rstMsg = (String)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

				//스케줄정보조회
				gdRtn = (GridData)ejbConn.trx("getCrnWrkBookMgt", new Class[] { GridData.class }, new Object[] { gdReq });
				gdRtn.addParam("RST_MSG", rstMsg);
			} else {
				//등록처리
				JDTORecord jrRst = (JDTORecord)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);

					EJBConnector sndConn = new EJBConnector("default", "YdCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}

				//스케줄정보조회
				gdRtn = (GridData)ejbConn.trx("getCrnWrkBookMgt", new Class[] { GridData.class }, new Object[] { gdReq });
			}

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 공장휴지계획 조회[C연주Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrCrnFixPlanMgt(GridData gdReq) throws DAOException {
		String methodNm = "공장휴지계획 조회[SlabYdJspFaEJB.scrCrnFixPlanMgt]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String ejbMethod = "getCrnFixPlan";
			methodNm += " 조회";
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);

			//조회
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인보수계획관리 조회[C연주Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrCcCrnFixPlanMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인보수계획관리 조회[SlabYdJspFaEJB.scrCcCrnFixPlanMgt]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String ejbMethod = "getCcCrnFixPlanMgt";
			methodNm += " 조회";
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);

			//조회
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인 보수계획 수정[C연주Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrCcCrnFixPlanUpd(GridData gdReq) throws DAOException {
		String methodNm = "크레인 보수계획 수정[SlabYdJspFaEJB.scrCcCrnFixPlanUpd]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String ejbMethod = "trtCcCrnFixPlanUpd";
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);

			//조회
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인 보수계획 삭제[C연주Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrCcCrnFixPlanDel(GridData gdReq) throws DAOException {
		String methodNm = "크레인 보수계획 삭제[SlabYdJspFaEJB.scrCcCrnFixPlanDel]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String ejbMethod = "trtCcCrnFixPlanDel";
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);

			//조회
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 공장 별 보수명 조회[C연주Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrMaintName(GridData gdReq) throws DAOException {
		String methodNm = "공장 별 보수명 조회[SlabYdJspFaEJB.scrMaintName]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String ejbMethod = "trtMaintName";
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);

			//조회
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 공장 휴지계획 등록[C연주Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrPausePlan(GridData gdReq) throws DAOException {
		String methodNm = "공장 휴지계획 등록[SlabYdJspFaEJB.scrPausePlan]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String ejbMethod = "insPausePlan";
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);

			//조회
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 공장 휴지계획 삭제[C연주Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrPausePlanDel(GridData gdReq) throws DAOException {
		String methodNm = "공장 휴지계획 삭제[SlabYdJspFaEJB.scrPausePlanDel]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String ejbMethod = "delPausePlan";
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);

			//조회
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 공장 휴지계획 완료 등록[C연주Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrPausePlanConf(GridData gdReq) throws DAOException {
		String methodNm = "공장 휴지계획 완료 등록[SlabYdJspFaEJB.scrPausePlanConf]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String ejbMethod = "updPausePlan";
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);

			//조회
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인별 휴지코드 조회[C연주Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrPauseCode(GridData gdReq) throws DAOException {
		String methodNm = "크레인별 휴지코드 조회[SlabYdJspFaEJB.scrPauseCode]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String ejbMethod = "trtPauseCode";
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);

			//조회
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 압연지시조회 [C연주Slab야드, 후판Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrMillWoInq(GridData gdReq) throws DAOException {
		String methodNm = "압연지시조회[SlabYdJspFaEJB.scrMillWoInq]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("DM".equals(trtGp)) {
				methodNm += " Dummy이적지시";
				ejbMethod = "trtMillWoInqDM";
			} else if ("CL".equals(trtGp)) {
				methodNm += " 보급Lot등록";
				ejbMethod = "trtMillWoInqCL";
			} else if ("SH".equals(trtGp)) {
				methodNm = "C열연 " + methodNm;
				ejbMethod = "getMillWoInq";
			} else if ("SP".equals(trtGp)) {
				methodNm = "후판 " + methodNm;
				ejbMethod = "getMillWoInq";
			} else if ("SP2".equals(trtGp)) {
				methodNm = "후판 " + methodNm;
				ejbMethod = "getMillWoInq2";
			} else if ("SP3".equals(trtGp)) {
				methodNm = "후판 일별조회" + methodNm;
				ejbMethod = "getMillWoInq3";
			} else if ("SPSCH".equals(trtGp)){
				methodNm = "후판 예정압연조회" + methodNm;
				ejbMethod = "getMillWoInqSCH";
			}
			else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);

			if (ejbMethod.startsWith("trt")) {
				//등록처리      
				JDTORecord jrRst = (JDTORecord)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);

					EJBConnector sndConn = new EJBConnector("default", "YdCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
				
				//등록처리후 조회      
				ejbMethod = "getMillWoInq";
			}

			//조회
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 *      [A] 오퍼레이션명 : 설비인출보급, 후판Piler인출, 후판Depiler보급 [C연주Slab야드, 후판Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrEqpPulloutSup(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급[SlabYdJspFaEJB.scrEqpPulloutSup]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("CI".equals(trtGp)) {
				methodNm += " 보급요구(Carry-In)";
				ejbMethod = "trtEqpPulloutSupCI";
			} else if ("CO".equals(trtGp)) {
				methodNm += " 인출요구(Carry-Out)";
				ejbMethod = "trtEqpPulloutSupCO";
			} else if ("TI".equals(trtGp)) {
				methodNm += " Take-In완료";
				ejbMethod = "trtEqpPulloutSupTI";
			} else if ("TO".equals(trtGp)) {
				methodNm += " Take-Out완료";
				ejbMethod = "trtEqpPulloutSupTO";
			} else if ("RM".equals(trtGp)) {
				methodNm += " 행선변경";
				ejbMethod = "updEqpPulloutSupRt";
			} else if ("MD".equals(trtGp)) {
				methodNm += " Bed재료삭제";
				ejbMethod = "delEqpPulloutSupMtl";
			} else if ("SR".equals(trtGp)) {
				methodNm += " 보급편성기준변경";
				ejbMethod = "updEqpPulloutSupRule";
			} else if ("CR".equals(trtGp)) {
				methodNm += " 크레인편성기준변경";
				ejbMethod = "updCrnPulloutSupRule";	
			} else if ("SM".equals(trtGp)) {
				methodNm += " Bed재료조회";
				ejbMethod = "getEqpPulloutSup";
			} else if ("SP".equals(trtGp)) {
				methodNm += " 후판압연지시조회";
				ejbMethod = "getMillWoInq";
			} else if ("SPSCH".equals(trtGp)) {
				methodNm += "후판예정압연지시조회";
				ejbMethod = "getMillWoInqSCH";
			} 
			else if ("TM".equals(trtGp)) {
				methodNm += " Take-Out재료조회";
				ejbMethod = "getEqpPulloutSup";
			} else if ("SB".equals(trtGp)) {
				methodNm += " Bed정보조회";
				ejbMethod = "getEqpPulloutSup";
			} else if ("SS".equals(trtGp)) {
				methodNm += "순번변경";
				ejbMethod = "updStockSeqNo";
			} else if ("CT".equals(trtGp)){
				methodNm += "생산통제후판메세지조회";
				ejbMethod = "getMillWoInqMsg";
			} else if ("CA".equals(trtGp)){
				methodNm += "생산통제후판메세지이력조회";
				ejbMethod = "getMillWoInqMsgAll";
			} else if ("CS".equals(trtGp)){
				methodNm += "생산통제후판메세지조회bySeq";
				ejbMethod = "getMillWoInqMsgBySeqNo";
			} 
			else if ("CC".equals(trtGp)){
				methodNm += "생산통제후판메세지확인";
				ejbMethod = "updMillWoInqMsg";
			} else {
			
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			
			if (ejbMethod.startsWith("trt")) {
				//등록처리
				JDTORecord jrRst = (JDTORecord)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);
						
					EJBConnector sndConn = new EJBConnector("default", "YdCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
			} else {
				//조회
				gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });
			}

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 장입보급기준 [C연주Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrChgSupRule(GridData gdReq) throws DAOException {
		String methodNm = "장입보급기준[SlabYdJspFaEJB.scrChgSupRule]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("CR".equals(trtGp)) {
				methodNm += " 등록";
				ejbMethod = "trtChgSupRule";
			} else if ("SR".equals(trtGp)) {
				methodNm += " 조회";
				ejbMethod = "getChgSupRule";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 수불구용도변경 [C연주Slab야드, 후판Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrBedUsgGpSet(GridData gdReq) throws DAOException {
		String methodNm = "수불구용도변경[SlabYdJspFaEJB.scrBedUsgGpSet]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("M".equals(trtGp)) {
				methodNm += " 수정";
				ejbMethod = "trtBedUsgGpSet";
			} else if ("S".equals(trtGp)) {
				methodNm += " 조회";
				ejbMethod = "getBedUsgGpSet";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);

			if ("M".equals(trtGp)) {
				//수불구용도변경
				JDTORecord jrRst = (JDTORecord)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);

					EJBConnector sndConn = new EJBConnector("default", "YdCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
			}

			//조회
			gdRtn = (GridData)ejbConn.trx("getBedUsgGpSet", new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 고강도재 상하면스카핑구분 등록 [C연주Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrHsmAllSfSet(GridData gdReq) throws DAOException {
		String methodNm = "고강도재 상하면스카핑구분 등록[SlabYdJspFaEJB.scrHsmAllSfSet]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("C".equals(trtGp)) {
				methodNm += " 등록";
				ejbMethod = "trtHsmAllSfSet";
			} else if ("S".equals(trtGp)) {
				methodNm += " 조회";
				ejbMethod = "getHsmAllSfSet";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 *      [A] 오퍼레이션명 : 스카핑보급관리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrSfSupMgt(GridData gdReq) throws DAOException {
		String methodNm = "스카핑보급관리[SlabYdJspFaEJB.scrSfSupMgt]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("CL".equals(trtGp)) {
				methodNm += " 보급Lot등록";
				ejbMethod = "trtSfSupMgtCL";
			} else if ("MD".equals(trtGp)) {
				methodNm += " 지연사유등록(긴급재)";
				ejbMethod = "trtSfSupMgtMD";
			}  else if ("MS".equals(trtGp)) {
				methodNm += " 지연사유등록(공정요구)";
				ejbMethod = "trtSfSupMgtMS";
			} else if ("SS".equals(trtGp)) {
				methodNm += " 대상조회";
				ejbMethod = "getSfSupMgt";
			} else if ("ST".equals(trtGp)) {
				methodNm += " 긴급재조회";
				ejbMethod = "getSfSupMgt";
			} else if ("SC".equals(trtGp)) {
				methodNm += " 공정요구대상조회";
				ejbMethod = "getSfSupMgt";
			} else if ("SM".equals(trtGp)) {
				methodNm += " 완료조회";
				ejbMethod = "getSfSupMgt";
			} else if ("BR".equals(trtGp)) {
				methodNm += " 보급제한기준조회";
				ejbMethod = "getSupRuleMax";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			
			if (ejbMethod.startsWith("trt")) {
				//등록처리
				JDTORecord jrRst = (JDTORecord)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);
						
					EJBConnector sndConn = new EJBConnector("default", "YdCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
				
				ejbMethod = "getSfSupMgt"; 
				
			}else{ 
				//조회
				gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });
				 
			}
			slabUtils.printLog(logId, methodNm, "F-");
			//조회결과
			return gdRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 *      [A] 오퍼레이션명 : 2차절단보급관리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrStcmSupMgt(GridData gdReq) throws DAOException {
		String methodNm = "2차절단보급관리[SlabYdJspFaEJB.scrStcmSupMgt]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("CL".equals(trtGp)) {
				methodNm += " 보급Lot등록";
				ejbMethod = "trtStcmSupMgtCL";
			} else if ("ST".equals(trtGp)) {
				methodNm += " 대상조회";
				ejbMethod = "getStcmSupMgt";
			} else if ("SM".equals(trtGp)) {
				methodNm += " 완료조회";
				ejbMethod = "getStcmSupMgt";
			} else if ("BR".equals(trtGp)) {
				methodNm += " 보급제한기준조회";
				ejbMethod = "getSupRuleMax";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			
			if (ejbMethod.startsWith("trt")) {
				//등록처리
				JDTORecord jrRst = (JDTORecord)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);
						
					EJBConnector sndConn = new EJBConnector("default", "YdCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
				
				ejbMethod = "getStcmSupMgt";
			}

			//조회
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 2차절단보급관리(신)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrStcmSupMgtNew(GridData gdReq) throws DAOException {
		String methodNm = "2차절단보급관리[scrStcmSupMgtNew]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("CL".equals(trtGp)) {
				methodNm += " 보급Lot등록";
				ejbMethod = "trtStcmSupMgtCL";
			} else if ("ST".equals(trtGp)) {
				methodNm += " 대상조회";
				ejbMethod = "getStcmSupMgtNew";
			} else if ("SM".equals(trtGp)) {
				methodNm += " 완료조회";
				ejbMethod = "getStcmSupMgtNew";
			} else if ("BR".equals(trtGp)) {
				methodNm += " 보급제한기준조회";
				ejbMethod = "getSupRuleMax";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			
			if (ejbMethod.startsWith("trt")) {
				//등록처리
				JDTORecord jrRst = (JDTORecord)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);
						
					EJBConnector sndConn = new EJBConnector("default", "YdCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
				
				ejbMethod = "getStcmSupMgtNew";
			}

			//조회
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 2차절단보급관리(신)팝업
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrStcmSupMgtNewpp(GridData gdReq) throws DAOException {
		String methodNm = "2차절단보급관리[scrStcmSupMgtNewpp]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("C".equals(trtGp)) {
				methodNm += " 보급Lot등록";
				ejbMethod = "trtStcmSupMgtCLpp";
			} else if ("S".equals(trtGp)) {
				methodNm += " 대상조회";
				ejbMethod = "getStcmSupMgtNewpp";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			
			if (ejbMethod.startsWith("trt")) {
				//등록처리
				JDTORecord jrRst = (JDTORecord)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);
						
					EJBConnector sndConn = new EJBConnector("default", "YdCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
				
				ejbMethod = "getStcmSupMgtNewpp";
			}

			//조회
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 보급Lot관리 (장입/스카핑/2차절단)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrSupLotMgt(GridData gdReq) throws DAOException {
		String methodNm = "보급Lot관리[SlabYdJspFaEJB.scrSupLotMgt]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("MS".equals(trtGp)) {
				methodNm += " 보급순서 수정";
				ejbMethod = "trtSupLotMgtMS";
			} else if ("DS".equals(trtGp)) {
				methodNm += " 삭제";
				ejbMethod = "trtSupLotMgtDS";
			} else if ("DM".equals(trtGp)) {
				methodNm += " 재료삭제";
				ejbMethod = "trtSupLotMgtDM";
			} else if ("SS".equals(trtGp)) {
				methodNm += " 조회";
				ejbMethod = "getSupLotMgt";
			} else if ("SM".equals(trtGp)) {
				methodNm += " 재료조회";
				ejbMethod = "getSupLotMgt";
			} else if ("CM".equals(trtGp)){
				methodNm += " 크레인지정";
				ejbMethod = "trtSupLotMgtCM";
			}else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 *      [A] 오퍼레이션명 : 스카핑/2차절단보급Lot
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrSfStcmSupLot(GridData gdReq) throws DAOException {
		String methodNm = "스카핑/2차절단보급Lot[SlabYdJspFaEJB.scrSfStcmSupLot]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("MS".equals(trtGp)) {
				methodNm += " 보급순서 수정";
				ejbMethod = "trtSfStcmSupLotMS";
			} else if ("DS".equals(trtGp)) {
				methodNm += " 삭제";
				ejbMethod = "trtSfStcmSupLotDS";
			} else if ("DM".equals(trtGp)) {
				methodNm += " 재료삭제";
				ejbMethod = "trtSfStcmSupLotDM";
			} else if ("SS".equals(trtGp)) {
				methodNm += " 조회";
				ejbMethod = "getSfStcmSupLot";
			} else if ("SM".equals(trtGp)) {
				methodNm += " 재료조회";
				ejbMethod = "getSfStcmSupLot";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 대차스케줄관리 [C연주Slab야드, 후판Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrTcarSchMgt(GridData gdReq) throws DAOException {
		String methodNm = "대차스케줄관리[SlabYdJspFaEJB.scrTcarSchMgt]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("TI".equals(trtGp)) {
				methodNm += " 대차초기화";
				ejbMethod = "trtTcarSchMgtTI";
			} else if ("PM".equals(trtGp)) {
				methodNm += " 우선순위변경";
				ejbMethod = "updWrkBookPrior";
			} else if ("WD".equals(trtGp)) {
				methodNm += " 작업예약삭제";
				ejbMethod = "delWrkBook";
			} else if ("ST".equals(trtGp)) {
				methodNm += " 대차정보조회";
				ejbMethod = "getTcarSchMgt";
			} else if ("SW".equals(trtGp)) {
				methodNm += " 작업예약정보조회";
				ejbMethod = "getTcarSchMgt";
			} else if ("SM".equals(trtGp)) {
				methodNm += " 작업예약재료조회";
				ejbMethod = "getTcarSchMgt";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);

			if ("TI".equals(trtGp)) {
				//대차초기화
				JDTORecord jrRst = (JDTORecord)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);

					EJBConnector sndConn = new EJBConnector("default", "YdCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
			} else if ("PM".equals(trtGp)) {
				//우선순위변경
				ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });
			} else if ("WD".equals(trtGp)) {
				//작업예약삭제
				ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });
			}

			//조회
			gdRtn = (GridData)ejbConn.trx("getTcarSchMgt", new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 대차상태설정 [C연주Slab야드, 후판Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrTcarStatSet(GridData gdReq) throws DAOException {
		String methodNm = "대차상태설정[SlabYdJspFaEJB.scrTcarStatSet]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("CS".equals(trtGp)) {
				methodNm += " 등록처리";
				ejbMethod = "trtTcarStatSet";
			} else if ("ST".equals(trtGp)) {
				methodNm += " 상태정보조회";
				ejbMethod = "getTcarSchMgt";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);

			if ("CS".equals(trtGp)) {
				//대차상태설정
				JDTORecord jrRst = (JDTORecord)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);

					EJBConnector sndConn = new EJBConnector("default", "YdCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
			}

			//조회
			gdRtn = (GridData)ejbConn.trx("getTcarSchMgt", new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 대차스케줄기준 [C연주Slab야드, 후판Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrTcarSchRule(GridData gdReq) throws DAOException {
		String methodNm = "대차스케줄기준[SlabYdJspFaEJB.scrTcarSchRule]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("CR".equals(trtGp)) {
				methodNm += " 등록처리";
				ejbMethod = "trtTcarSchRule";
			} else if ("ER".equals(trtGp)) {
				methodNm += " 설비기준조회";
				ejbMethod = "getTcarSchRule";
			} else if ("SR".equals(trtGp)) {
				methodNm += " 검색기준조회";
				ejbMethod = "getTcarSchRule";
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 이적작업예약등록 [C연주Slab야드, 후판Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrMvStkWrkBookReg(GridData gdReq) throws DAOException {
		String methodNm = "이적작업예약등록[SlabYdJspFaEJB.scrMvStkWrkBookReg]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("CW".equals(trtGp)) {
				methodNm += " 등록처리";
				ejbMethod = "trtMvStkWrkBookReg";
			} else if ("SB".equals(trtGp)) {
				methodNm += " Bed정보조회";
				ejbMethod = "getMvStkWrkBookReg";
			} else if ("SM".equals(trtGp)) {
				methodNm += " 재료정보조회";
				ejbMethod = "getMvStkWrkBookReg";
			} else if ("SL".equals(trtGp)) {
				methodNm += "권하불가위치조회";
				ejbMethod = "getYdDnWoLocNotAllowedInfo";
			} else if ("SN".equals(trtGp)) {
				methodNm += "권하불가재료조회";
				ejbMethod = "getSlabDnNotAllowedInfo";
			} else if ("SD".equals(trtGp)) {
				methodNm += "중복적치재료조회";
				ejbMethod = "getMvStkWrkBookRegDup";				
			} else if ("SH".equals(trtGp)) {
				methodNm += "핸드스카핑보류재여부조회";
				ejbMethod = "getHandScarfingHoldYn";							
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtGp + "] 입니다.");
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);

			if ("CW".equals(trtGp)) {
				//작업예약등록
				JDTORecord jrRst = (JDTORecord)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

				String resultMessage = "";
				if(jrRst != null ) resultMessage = slabUtils.nvl(jrRst.getResultMsg(),"");
				if(!"".equals(resultMessage)){
					gdRtn.setMessage(resultMessage);
					return gdRtn;
				}
				
				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);

					EJBConnector sndConn = new EJBConnector("default", "YdCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
			} else {
				//조회
				gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });
			}

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 저장위치별정보조회 [C연주Slab야드, 후판Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrStrLocInfo(GridData gdReq) throws DAOException {
		String methodNm = "저장위치별정보조회[SlabYdJspFaEJB.scrStrLocInfo]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			gdRtn = (GridData)ejbConn.trx("getStrLocInfo", new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 작업재료List [C연주Slab야드, 후판Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrWrkMtlList(GridData gdReq) throws DAOException {
		String methodNm = "작업재료List[SlabYdJspFaEJB.scrWrkMtlList]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			gdRtn = (GridData)ejbConn.trx("getWrkMtlList", new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/***************************************************************************
	 * Monitoring 화면 - Flex
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : Slab야드Monitoring [C연주Slab야드, 후판Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  HashMap hmReq
	 *      @return List
	 *      @throws DAOException
	*/
	public List scrSlabYdMonitor(HashMap hmReq) throws DAOException {
		String methodNm = "Slab야드Monitoring[SlabYdJspFaEJB.scrSlabYdMonitor]";
		String logId = slabUtils.getLogId();

		try {
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, hmReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			List lsRtn = (List)ejbConn.trx("getSlabYdMonitor", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return lsRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/***************************************************************************
	 * 인터페이스 관련 화면
	 **************************************************************************/
	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스Test
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrIfTest(GridData gdReq) throws DAOException {
		String methodNm = "인터페이스Test[SlabYdJspFaEJB.scrIfTest]";
		String logId = slabUtils.getLogId();
		
		try {
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("DI".equals(trtGp)) {
				methodNm += " 전송Data 저장";
				ejbMethod = "updIfTestData";
			} else if ("DS".equals(trtGp)) {
				methodNm += " 전송";
				ejbMethod = "sndIfTest";
			} else if ("ES".equals(trtGp)) {
				methodNm += " EAI 전송";
				ejbMethod = "sndIfTestEAI";
			} else if ("SL".equals(trtGp)) {
				methodNm += " I/F Layout 조회";
				ejbMethod = "getIfTest";
			} else {
				methodNm += " I/F List 조회";
				ejbMethod = "getIfTest";
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			GridData gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스관리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrIfMgt(GridData gdReq) throws DAOException {
		String methodNm = "인터페이스관리[SlabYdJspFaEJB.scrIfMgt]";
		String logId = slabUtils.getLogId();
		
		try {
			String trtGp = slabUtils.trim(gdReq.getParam("V_TRT_GP"));
			String ejbMethod = "";

			if ("TU".equals(trtGp)) {
				methodNm += " 등록";
				ejbMethod = "upsIfMgt";
			} else if ("TD".equals(trtGp)) {
				methodNm += " 삭제";
				ejbMethod = "delIfMgt";
			} else {
				methodNm += " I/F List 조회";
				ejbMethod = "getIfMgt";
			}
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			GridData gdRtn = (GridData)ejbConn.trx(ejbMethod, new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/***************************************************************************
	 * Code조회
	 **************************************************************************/
	
	/**
	 *      [A] 오퍼레이션명 : Slab야드 코드 조회(WiseGrid)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getSlabYdCode(GridData gdReq) throws DAOException {
		String methodNm = "Slab야드코드조회[SlabYdJspFaEJB.getSlabYdCode]";
		String logId = slabUtils.getLogId();

		try {
			gdReq.setNavigateValue(methodNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			
			//코드조회
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			return (GridData)ejbConn.trx("getSlabYdCode", new Class[] { GridData.class }, new Object[] { gdReq });
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * [A] 오퍼레이션명: 후판압연지시 메시지등록
	 * 
	 * @ejb.interface-method
	 * @param inParam
	 * @return
	 * @throws JDTOException
	 */
	public GridData updateStlMessagePa(GridData inParam) throws JDTOException {
		EJBConnector ejbConn = null;
		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(inParam);
			ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			ejbConn.trx("updateStlMessagePa", new Class[] { GridData.class }, new Object[] { inParam });
			
			return inParam ;
		} catch (Exception e) {
			throw new JDTOException(getClass().getName() + " :: " + e.getMessage(), e);
		} finally {
		}
	}
	
	
	
	
	/**
	 *  벤딩재 이력 조회
	 * 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public JDTORecordSet getBendingSlabHist(JDTORecord inDto) throws JDTOException {
		//LOG
		String szOperationName	= "벤딩재 이력 조회";
		String methodNm = "벤딩재이력조회[SlabYdJspFaEJB.getBendingSlabHist]";
		String logId = slabUtils.getLogId();
		
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		
		try{
			
			slabUtils.printLog(logId, methodNm, "F+");
			
			ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			recordSet = (JDTORecordSet)ejbConn.trx("getBendingSlabHist", new Class[] { JDTORecord.class }, new Object[] { inDto });
			return recordSet;

		} catch(Exception e){		
			slabUtils.printLog(logId, methodNm, e.getMessage());
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : Sizing Slab 이송 재료 List [2후판정정야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrWrkMtlListFromPlate(GridData gdReq) throws DAOException {
		String methodNm = "Sizing Slab 이송 재료 List[SlabYdJspFaEJB.scrWrkMtlListFromPlate]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			gdRtn = (GridData)ejbConn.trx("getWrkMtlListFromPlate", new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 스카핑 슬라브 이송실적 [C연주Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData scrScarfSlabMvList(GridData gdReq) throws DAOException {
		String methodNm = "스카핑 슬라브 이송실적[SlabYdJspFaEJB.scrScarfSlabMvList]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			gdRtn = (GridData)ejbConn.trx("getScarfSlabMvList", new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 현재 적치수량,중량 조회(C1,C2) [C연주Slab야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData srcCurCntWt(GridData gdReq) throws DAOException {
		String methodNm = "현재 적치수량,중량 조회(C1,C2)[SlabYdJspFaEJB.srcCurCntWt]";
		String logId = slabUtils.getLogId();

		try {
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			
			slabUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			gdRtn = (GridData)ejbConn.trx("getCurCntWt", new Class[] { GridData.class }, new Object[] { gdReq });

			slabUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *  야드관리 > 항만슬라브야드 > monitoring > 차량동간이적
	 *  허정욱
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updSyCarMvStkReg(GridData gdReq) throws DAOException {
		String mthdNm = "차량동간이적등록[SlabYdJspFaEJB.updSyCarMvStkReg]";
		String logId  = slabUtils.getLogId();

		try{
			mthdNm = mthdNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updSyCarMvStkReg", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "YdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("차량동간이적 처리가 완료 됐습니다.");
			} 			
			slabUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *  야드관리 > 연주슬라브야드 > monitoring > 연주야드 현황판 메세지 전송
	 *  허정욱
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @throws DAOException
	 */
	public List sendMsg(HashMap paramMap, ArrayList paramList) throws DAOException {
		EJBConnector ejbConn    = null;
		Object[]     oObjs	    = null;
		String       sYn	    = null;
		String       sSeqNo   = null;
		List  returnList = new ArrayList();
		
		String logId  = slabUtils.getLogId();
		String methodNm = "슬라브야드 메세지 전송[SlabYdJspFaEJB.sendMsg]";

		try {
			slabUtils.printLog(logId, methodNm, "F+");
			
			        
			ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);			
			ejbConn.trx("sendMsg", new Class[] { HashMap.class,ArrayList.class }, new Object[] { paramMap,paramList });
      

			
			returnList.add(sYn);
			returnList.add(sSeqNo);
			
			return returnList;
			
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		} finally {

		}
	}
	
	/**
	 *  야드관리 > 연주슬라브야드 > monitoring > 연주야드 현황판 계획량 수정
	 *  허정욱
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData cSlabYdInOutStatPlanModify(GridData gdReq) throws DAOException {
		String mthdNm = "연주야드 현황판 계획량 수정[SlabYdJspFaEJB.cSlabYdInOutStatPlanModify]";
		String logId  = slabUtils.getLogId();

		try{
			mthdNm = mthdNm + " < " + slabUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + slabUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			slabUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("cSlabYdInOutStatPlanModify", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= slabUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= slabUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			slabUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("계획량 수정이 완료되었습니다.");
			} 			
			slabUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 권하불가위치 조회
	 *		오원재(1524711)
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getYdDnWoLocNotAllowedInfo(GridData gdReq) throws DAOException {
		String methodNm = "권하불가위치 조회[SlabYdJspFaEJB.getYdDnWoLocNotAllowedInfo]";
		String logId = slabUtils.getLogId();

		try {
			gdReq.setNavigateValue(methodNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID

			//조회
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			return (GridData)ejbConn.trx("getYdDnWoLocNotAllowedInfo", new Class[] { GridData.class }, new Object[] { gdReq });
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 권하불가재료 조회
	 *		오원재(1524711)
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getSlabDnNotAllowedInfo(GridData gdReq) throws DAOException {
		String methodNm = "권하불가재료 조회[SlabYdJspFaEJB.getSlabDnNotAllowedInfo]";
		String logId = slabUtils.getLogId();

		try {
			gdReq.setNavigateValue(methodNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID

			//조회
			EJBConnector ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			return (GridData)ejbConn.trx("getSlabDnNotAllowedInfo", new Class[] { GridData.class }, new Object[] { gdReq });
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 팔렛트 자동 상차완료 처리 여부 변경
	 *		오원재(1524711)
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param inDto
	 *      @return 
	 *      @throws JDTOException
	*/
	public GridData updPtAutoComplete(GridData inDto) throws JDTOException {
		String methodNm = "팔렛트 자동 상차완료 처리 여부 변경[SlabYdJspFaEJB.updPtAutoComplete]";
		String logId = slabUtils.getLogId();
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
			//JDTORecord [] inRecord =  slabUtils.genGridToJDTORecord(inDto);

			slabUtils.printLog(logId, methodNm, "F+", inDto);
			
			ejbConn = new EJBConnector("default", "SlabYdJspSeEJB", this);
			ejbConn.trx("updPtAutoComplete", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			slabUtils.printLog(logId, methodNm, "F-", inDto);
			
			
		}catch(Exception e){

			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;

	}
	
	
	
}
