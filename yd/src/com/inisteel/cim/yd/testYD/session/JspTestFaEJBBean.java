package com.inisteel.cim.yd.testYD.session;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.rmi.RemoteException;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jsp.common.YDComUtil;

import flex.messaging.io.ArrayList;

/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Facade Session EJB클래스입니다.
 * 
 * @ejb.bean name="JspTestFaEJB" jndi-name="JspTestFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class JspTestFaEJBBean extends BaseSessionBean {

	YDComUtil ydComUtil = new YDComUtil();
	private YdUtils ydUtils = new YdUtils();

	private String szSessionName = getClass().getName();

	/**
	 * ejbCrate()
	 * 
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * FACADE EJB 전문 전송 SIMULATION
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData sendJVMTest(GridData inDto) throws DAOException {

		GridData gdRes = new GridData();
		EJBConnector ejbConn = null;
		try {
			// JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);

			ejbConn = new EJBConnector("default", "JspTestSeEJB", this);

			ejbConn.trx("sendJVMTest", new Class[] { GridData.class },
					new Object[] { inDto });

			gdRes = OperateGridData.cloneResponseGridData(inDto);
		} catch (Exception e) {

		}
		
		return gdRes;

	}

	/**
	 * 작업예약조회 데이터
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getWRKBOOK(GridData inDto) throws DAOException {
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try {
			// 화면 WiseGrid로 부터 온 파라메터 데이터를 JDTORecord로 변환한다.
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "JspTestSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn
					.trx("getWRKBOOK", new Class[] { JDTORecord.class },
							new Object[] { inRecord });

			// DAO로부터 온 JDTORecordSet 데이터를 화면으로 보낼 GridData 로 변환한다.
			gdRes = CmUtil.genGridData(inDto, recordSet);

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");

		return gdRes;
	}

	/**
	 * 작업예약 재료 조회 데이터
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getWRKBOOKMTL(GridData inDto) throws DAOException {
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try {
			// 화면 WiseGrid로 부터 온 파라메터 데이터를 JDTORecord로 변환한다.
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "JspTestSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn
					.trx("getWRKBOOKMTL", new Class[] { JDTORecord.class },
							new Object[] { inRecord });

			// DAO로부터 온 JDTORecordSet 데이터를 화면으로 보낼 GridData 로 변환한다.
			gdRes = CmUtil.genGridData(inDto, recordSet);

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}

	/**
	 * [테스트]크레인 스케줄 조회 DATA
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCRNSCH(GridData inDto) throws DAOException {
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try {
			// 화면 WiseGrid로 부터 온 파라메터 데이터를 JDTORecord로 변환한다.
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "JspTestSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn
					.trx("getCRNSCH", new Class[] { JDTORecord.class },
							new Object[] { inRecord });

			// DAO로부터 온 JDTORecordSet 데이터를 화면으로 보낼 GridData 로 변환한다.
			gdRes = CmUtil.genGridData(inDto, recordSet);

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}

	/**
	 * [테스트]크레인 스케줄 조회 DATA
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCRNWRKMTL(GridData inDto) throws DAOException {
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try {
			// 화면 WiseGrid로 부터 온 파라메터 데이터를 JDTORecord로 변환한다.
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "JspTestSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn
					.trx("getCRNWRKMTL", new Class[] { JDTORecord.class },
							new Object[] { inRecord });

			// DAO로부터 온 JDTORecordSet 데이터를 화면으로 보낼 GridData 로 변환한다.
			gdRes = CmUtil.genGridData(inDto, recordSet);

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}

	/**
	 * [테스트]저장품 조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getSTOCK(GridData inDto) throws DAOException {
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try {
			// 화면 WiseGrid로 부터 온 파라메터 데이터를 JDTORecord로 변환한다.
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "JspTestSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn
					.trx("getSTOCK", new Class[] { JDTORecord.class },
							new Object[] { inRecord });

			// DAO로부터 온 JDTORecordSet 데이터를 화면으로 보낼 GridData 로 변환한다.
			gdRes = CmUtil.genGridData(inDto, recordSet);

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}

	/**
	 * [테스트]재료변경이력 조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getMTLSTATMODHIST(GridData inDto) throws DAOException {
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try {
			// 화면 WiseGrid로 부터 온 파라메터 데이터를 JDTORecord로 변환한다.
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "JspTestSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn
					.trx("getMTLSTATMODHIST", new Class[] { JDTORecord.class },
							new Object[] { inRecord });

			// DAO로부터 온 JDTORecordSet 데이터를 화면으로 보낼 GridData 로 변환한다.
			gdRes = CmUtil.genGridData(inDto, recordSet);

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}

	/**
	 * [테스트]적치단 조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getStkLyr(GridData inDto) throws DAOException {
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try {
			// 화면 WiseGrid로 부터 온 파라메터 데이터를 JDTORecord로 변환한다.
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "JspTestSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn
					.trx("getStkLyr", new Class[] { JDTORecord.class },
							new Object[] { inRecord });

			// DAO로부터 온 JDTORecordSet 데이터를 화면으로 보낼 GridData 로 변환한다.
			gdRes = CmUtil.genGridData(inDto, recordSet);

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}

	/**
	 * 권상실적 전문을 내부큐로 전송 (수정버튼)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData sendRstLdDn(GridData inDto) throws DAOException {
		// LOG
		String szMsg = "";
		String szMethodName = "sendRstLdDn";

		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try {
			JDTORecord[] inRecord = ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "JspTestSeEJB", this);
			ejbConn.trx("sendRstLdDn", new Class[] { JDTORecord[].class },
					new Object[] { inRecord });

			gdRes = OperateGridData.cloneResponseGridData(inDto);

		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} // end of sendRstLdDn
	
	

	/**
	 * 권하실적 전문을 내부큐로 전송 (수정버튼)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData sendRstDn(GridData inDto) throws DAOException {
		// LOG
		String szMsg = "";
		String szMethodName = "sendRstDn";

		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try {
			JDTORecord[] inRecord = ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "JspTestSeEJB", this);
			ejbConn.trx("sendRstDn", new Class[] { JDTORecord[].class },
					new Object[] { inRecord });

			gdRes = OperateGridData.cloneResponseGridData(inDto);

		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} // end of sendRstDn
	
	

	/**
	 * TO 위치 결정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData setToPosition(GridData inDto) throws DAOException {
		// LOG
		String szMsg = "";
		String szMethodName = "setToPosition";

		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try {
			JDTORecord[] inRecord = ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "JspTestSeEJB", this);
			ejbConn.trx("setToPosition", new Class[] { JDTORecord[].class },
					new Object[] { inRecord });

			gdRes = OperateGridData.cloneResponseGridData(inDto);

		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} // end of setToPosition

	/**
	 * 플렉스 화면 조회 테스트
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public List getYdFlexTest(HashMap param) {
		EJBConnector ejbConn = null;

		String szMethodName = "getYdFlexTest";

		try {
			ejbConn = new EJBConnector("default", "JspTestSeEJB", this);
			return (List) ejbConn.trx("getYdFlexTest",
					new Class[] { HashMap.class }, new Object[] { param });
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}

	/**
	 * 야드 대차 스케쥴 조회(테스트용 - 임춘수 20090209)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData getTcarSch(GridData inDto) throws DAOException {
		// LOG
		String szMsg = "";
		String szMethodName = "getTcarSch";

		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try {
			// kud
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("JspTestSeEJB",
					"getTcarSch", inRecord);
			gdRes = CmUtil.genGridData(inDto, recordSet);

		} catch (Exception e) {
			// ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}

	/**
	 * 야드 대차 이송재료 조회(테스트용 - 임춘수 20090209)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData getTcarSchFtmvMtl(GridData inDto) throws DAOException {
		// LOG
		String szMsg = "";
		String szMethodName = "getTcarSchFtmvMtl";

		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try {
			// kud
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("JspTestSeEJB",
					"getTcarSchFtmvMtl", inRecord);
			gdRes = CmUtil.genGridData(inDto, recordSet);

		} catch (Exception e) {
			// ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}


	/**
	 * 플렉스 화면 - 후판 ROLL TABlE 각 재료정보 조회 POP
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 *            param
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public List getYdRTPlateStlNo(HashMap param) {
		EJBConnector ejbConn = null;

		String szMethodName = "getYdRTPlateStlNo";

		try {
			ejbConn = new EJBConnector("default", "JspTestSeEJB", this);
			return (List) ejbConn.trx("getYdRTPlateStlNo",
					new Class[] { HashMap.class }, new Object[] { param });
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}

	/**
	 * 야드 대차 베드 단 상태 조회(테스트용 - 임춘수 20090209)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData getTcarBedStkLyr(GridData inDto) throws DAOException {
		// LOG
		String szMsg = "";
		String szMethodName = "getTcarBedStkLyr";

		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try {
			// kud
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("JspTestSeEJB",
					"getTcarBedStkLyr", inRecord);
			gdRes = CmUtil.genGridData(inDto, recordSet);

		} catch (Exception e) {
			// ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}

	/**
	 * 야드별 대차 조회(테스트용 - 임춘수 20090209)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getTcarSearch(GridData inDto) throws DAOException {
		// LOG
		String szMsg = "";
		String szMethodName = "getTcarSearch";

		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try {
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			// ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			// ejbConn.trx("getTcarSearch", new Class[] { JDTORecord[].class },
			// new Object[] { inRecord });
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("JspTestSeEJB",
					"getTcarSearch", inRecord);
			gdRes = CmUtil.genGridData(inDto, recordSet);
			// gdRes = OperateGridData.cloneResponseGridData(gdReq);
			// gdRes = CmUtil.copyGDParam(gdReq, gdRes);

		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage(), e);

		}
		// gdRes.setStatus("true");
		// gdRes.setMessage("Success");
		return gdRes;

	}

	/**
	 * 차량 조회(테스트용 - 임춘수 20090209)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCarSearch(GridData inDto) throws DAOException {
		// LOG
		String szMsg = "";
		String szMethodName = "getCarSearch";

		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try {
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			// ejbConn = new EJBConnector("default", "SlabJspSeEJB", this);
			// ejbConn.trx("getTcarSearch", new Class[] { JDTORecord[].class },
			// new Object[] { inRecord });
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("JspTestSeEJB",
					"getCarSearch", inRecord);
			gdRes = CmUtil.genGridData(inDto, recordSet);
			// gdRes = OperateGridData.cloneResponseGridData(gdReq);
			// gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
			// ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
		}
		// gdRes.setStatus("true");
		// gdRes.setMessage("Success");
		return gdRes;

	}

	/**
	 * 차량 상하차 베드 단 상태 조회(테스트용 - 임춘수 20090209)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData getCarBedStkLyr(GridData inDto) throws DAOException {
		// LOG
		String szMsg = "";
		String szMethodName = "getCarBedStkLyr";

		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try {
			// kud
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("JspTestSeEJB",
					"getCarBedStkLyr", inRecord);
			gdRes = CmUtil.genGridData(inDto, recordSet);
			
		} catch (Exception e) {
			// ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}

	/**
	 * 야드 차량스케쥴 조회(테스트용 - 임춘수 20090209)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData getCarSch(GridData inDto) throws DAOException {
		// LOG
		String szMsg = "";
		String szMethodName = "getCarSch";

		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try {
			// kud
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("JspTestSeEJB",
					"getCarSch", inRecord);
			gdRes = CmUtil.genGridData(inDto, recordSet);
			
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 야드 차량스케쥴 등록, 수정, 삭제(테스트용 - 임춘수 20090401)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData uptCarSch(GridData inDto) throws DAOException {
		// LOG
		String szMsg = "";
		String szMethodName = "uptCarSch";

		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		Vector inVParam = new Vector();
		try {
			JDTORecord inParam = CmUtil.genJDTORecord(inDto);
			
			JDTORecord[] inRecord = CmUtil.genJDTORecordSet(inDto);
			inVParam.addElement(inRecord);
			
			ejbConn = new EJBConnector("default", this);
			System.out.println("야드 차량스케쥴 등록, 수정, 삭제 호출 전");
			ejbConn.trx("JspTestSeEJB", "uptCarSch", new Class[] {JDTORecord.class, Vector.class }, new Object[]{inParam, inVParam});
			System.out.println("야드 차량스케쥴 등록, 수정, 삭제 호출 성공");
			//gdRes = CmUtil.genGridData(inDto, recordSet);
			
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes = inDto;
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}

	/**
	 * 야드 차량 이송재료 조회(테스트용 - 임춘수 20090209)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData getCarSchFtmvMtl(GridData inDto) throws DAOException {
		// LOG
		String szMsg = "";
		String szMethodName = "getCarSchFtmvMtl";

		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try {
			// kud
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("JspTestSeEJB",
					"getCarSchFtmvMtl", inRecord);
			gdRes = CmUtil.genGridData(inDto, recordSet);
			 
		} catch (Exception e) {
			// ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}

	/**
	 * 슬라브야드 스케줄 기동
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData trxRunSchedule(GridData gdReq) throws DAOException {
		// LOG
		String szMsg = "";
		String szMethodName = "trxRunSchedule";

		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try {
			
			szMsg = "JSP-FACADE [슬라브야드 스케줄 기동 ] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			//JDTORecord[] inRecord = CmUtil.genJDTORecordSet(gdReq);
			
			JDTORecord[] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
			szMsg = "JSP-FACADE [그리드 데이터 =>JDTORecord[] 변환 완료  ]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			ejbConn = new EJBConnector("default", "JspTestSeEJB", this);
			ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecord });
			
			szMsg = "JSP-FACADE [스케줄 SESSION 호출 끝 ]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			
			szMsg = "JSP-FACADE [슬라브야드 스케줄 기동 ] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			 
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
		}

		return gdRes;
	} // end of trxRunSchedule
	
	/**
	 * 슬라브야드 스케줄 우선순위 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData trxRunSchPrior(GridData gdReq) throws DAOException {
		// LOG
		String szMsg = "";
		String szMethodName = "trxRunSchPrior";

		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try {
			
			szMsg = "JSP-FACADE [슬라브야드 스케줄 기동 ] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			//JDTORecord[] inRecord = CmUtil.genJDTORecordSet(gdReq);
			
			JDTORecord[] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
			szMsg = "JSP-FACADE [그리드 데이터 =>JDTORecord[] 변환 완료  ]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			ejbConn = new EJBConnector("default", "JspTestSeEJB", this);
			ejbConn.trx("trxRunSchPrior", new Class[] { JDTORecord[].class },	new Object[] { inRecord });
			
			szMsg = "JSP-FACADE [스케줄 SESSION 호출 끝 ]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			
			szMsg = "JSP-FACADE [슬라브야드 스케줄 기동 ] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			 
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
		}

		return gdRes;
	} // end of trxRunSchPrior

	
	/**
	 * 플렉스 화면 - Push Test
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 *            param
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public List pushYdTest(HashMap param) {
		
		EJBConnector ejbConn1 = null;		
		EJBConnector ejbConn2 = null;
		ArrayList pushData  = new ArrayList();
		
		pushData.add(param.get("MSG"));

		try {

			String dest = YdConstant.YD_MONITORING_CHANNEL_A;//(String)param.get("DEST");
			
			//15 was SE EJB call
			ejbConn1 = new EJBConnector("default", "YdJspCommonFaEJB", this);//Class를 지정 해 주고
			
			ejbConn1.trx("pushToFlexClient"
								  , new Class[] { String.class, ArrayList.class }
								  , new Object[] { dest,  pushData}
						);
	

			return new ArrayList();

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
		}
	}
	
	
	
	/**
	 * 플렉스 화면 - Push Test
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 *            param
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public List pushYdTest2(HashMap param) {
		
		EJBConnector ejbConn1 = null;		
		EJBConnector ejbConn2 = null;
		ArrayList pushData  = new ArrayList();
		
		pushData.add(param.get("MSG"));

		try {

			String dest = YdConstant.YD_MONITORING_CHANNEL_A;//(String)param.get("DEST");
			
			//15 was SE EJB call
			ejbConn1 = new EJBConnector("default", "YdJspCommonFaEJB", this);//Class를 지정 해 주고
			
			ejbConn1.trx("pushToFlexClient2"
								  , new Class[] { String.class, Object.class }
								  //, new Object[] { dest,  pushData}
								  , new Object[] { dest,  param}
						);
	

			return new ArrayList();

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
		}
	}
	
	
	/**
	 * FACADE EJB 전문 전송 SIMULATION
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public void receiveEAITest(JDTORecord inRecord) throws JDTOException,RemoteException {
 
		EJBConnector ejbConn = null;
		try {
			ejbConn = new EJBConnector("default", "JspTestSeEJB", this);
			ejbConn.trx("receiveEAITest", new Class[] { JDTORecord.class }, new Object[] { inRecord });
 
		} catch (Exception e) {

		}
		
		return ;

	}

}
