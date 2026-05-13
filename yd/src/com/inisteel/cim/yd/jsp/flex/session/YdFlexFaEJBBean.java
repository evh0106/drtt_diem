package com.inisteel.cim.yd.jsp.flex.session;

import java.util.HashMap;
import java.util.List;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jsp.common.YDComUtil;


/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Facade Session EJB클래스입니다.
 *
 * @ejb.bean name="YdFlexFaEJB" jndi-name="YdFlexFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class YdFlexFaEJBBean extends BaseSessionBean {

	private YDComUtil   ydComUtil = new YDComUtil();
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
	 * 플렉스 화면 - 후판제품야드 적치베드 조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public List getYdPlateGdsStkBedFlex(HashMap param) {
		EJBConnector ejbConn = null;

		String szMethodName = "getYdPlateGdsStkBedFlex";

		try {
			ejbConn = new EJBConnector("default", "YdFlexSeEJB", this);
			return (List) ejbConn.trx("getYdPlateGdsStkBedFlex",
					new Class[] { HashMap.class }, new Object[] { param });
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	/**
	 * 플렉스 화면 - 통합 야드 적치베드 조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public List getTotStkBedFlex(HashMap param) {
		EJBConnector ejbConn = null;

		String szMethodName = "getYdPlateGdsStkBedFlex";

		try {
			ejbConn = new EJBConnector("default", "YdFlexSeEJB", this);
			return (List) ejbConn.trx("getTotStkBedFlex",
					new Class[] { HashMap.class }, new Object[] { param });
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	
	/**
	 * 플렉스 화면 - C연주 슬라브야드 적치 재료 조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public List getYdCSlabStlnoStatFlex(HashMap param) {
		EJBConnector ejbConn = null;

		String szMethodName = "getYdCSlabStlnoStatFlex";

		try {
			ejbConn = new EJBConnector("default", "YdFlexSeEJB", this);
			return (List) ejbConn.trx("getYdCSlabStlnoStatFlex",
					new Class[] { HashMap.class }, new Object[] { param });
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	/**
	 * 플렉스 화면 - A후판야드 크레인 현황 조회 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public List getPlateYdCrnStatFlex(HashMap param) {
		EJBConnector ejbConn = null;

		String szMethodName = "getPlateYdCrnStatFlex";

		try {
			ejbConn = new EJBConnector("default", "YdFlexSeEJB", this);
			return (List) ejbConn.trx("getPlateYdCrnStatFlex",
					new Class[] { HashMap.class }, new Object[] { param });
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	
	/**
	 * 플렉스 화면 - C연주 슬라브야드 적치 재료 조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public List getYdStlnoStatFlex(HashMap param) {
		EJBConnector ejbConn = null;

		String szMethodName = "getYdStlnoStatFlex";

		try {
			ejbConn = new EJBConnector("default", "YdFlexSeEJB", this);
			return (List) ejbConn.trx("getYdStlnoStatFlex",
					new Class[] { HashMap.class }, new Object[] { param });
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	/**
	 * 플렉스 화면 - 코일소재야드 스판정보 조회(단정보까지 조회)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public List getYdStkLytInfoFlex(HashMap param) {
		EJBConnector ejbConn = null;

		String szMethodName = "getYdStkLytInfoFlex";

		try {
			ejbConn = new EJBConnector("default", "YdFlexSeEJB", this);
			return (List) ejbConn.trx("getYdStkLytInfoFlex",
					new Class[] { HashMap.class }, new Object[] { param });
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	
	/**
	 * 플렉스 화면 - 코일제품야드 스판정보 조회(단정보까지 조회)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap            
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public List getCoilGdsYdStkLytInfoFlex(HashMap param) {
		EJBConnector ejbConn = null;

		String szMethodName = "getCoilGdsYdStkLytInfoFlex";

		try {
			ejbConn = new EJBConnector("default", "YdFlexSeEJB", this);
			return (List) ejbConn.trx("getCoilGdsYdStkLytInfoFlex",
					new Class[] { HashMap.class }, new Object[] { param });
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	
	
	/**
	 * 플렉스 화면 - 차량 정보 조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public List getPlateYdCarInfoFlex(HashMap param) {
		EJBConnector ejbConn = null;
		String szMethodName = "getPlateYdCarInfoFlex";

		try {
			ejbConn = new EJBConnector("default", "YdFlexSeEJB", this);
			return (List) ejbConn.trx("getPlateYdCarInfoFlex",
					new Class[] { HashMap.class }, new Object[] { param });
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	

	/**
	 * 플렉스 화면 - 총매수 조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public List getAPlateYdTotCount(HashMap param) {
		EJBConnector ejbConn = null;
		String szMethodName = "getPlateYdCarInfoFlex";

		try {
			ejbConn = new EJBConnector("default", "YdFlexSeEJB", this);
			return (List) ejbConn.trx("getAPlateYdTotCount",
					new Class[] { HashMap.class }, new Object[] { param });
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	
	/**
	 * 플렉스 화면 - 총중량 조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public List getAPlateYdTotSumMgt(HashMap param) {
		EJBConnector ejbConn = null;
		String szMethodName = "getPlateYdCarInfoFlex";

		try {
			ejbConn = new EJBConnector("default", "YdFlexSeEJB", this);
			return (List) ejbConn.trx("getAPlateYdTotSumMgt",
					new Class[] { HashMap.class }, new Object[] { param });
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	
	
	
	/**
	 * 플렉스 화면 - 해당 베드 정보를 읽어오는 Function
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public List getChBedInfoFlex(HashMap param) {
		EJBConnector ejbConn = null;
		String szMethodName = "getChBedInfoFlex";

		try {
			ejbConn = new EJBConnector("default", "YdFlexSeEJB", this);
			return (List) ejbConn.trx("getChBedInfoFlex",
					new Class[] { HashMap.class }, new Object[] { param });
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	
	/**
	 * 플렉스 화면 - 선택된 설비(크레인)를 조회하는 Function 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public List getChCraneInfoFlex(HashMap param) {
		EJBConnector ejbConn = null;
		String szMethodName = "getChCraneInfoFlex";

		try {
			ejbConn = new EJBConnector("default", "YdFlexSeEJB", this);
			return (List) ejbConn.trx("getChCraneInfoFlex",
					new Class[] { HashMap.class }, new Object[] { param });
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	/**
	 * 플렉스 화면 - 전송 버퍼 (Delay를 위함)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public void wrkBuffer(JDTORecord param) {
		EJBConnector ejbConn = null;
		String szMethodName = "wrkBuffer";

		try {
			ejbConn = new EJBConnector("default", "YdFlexSeEJB", this);
			 ejbConn.trx("wrkBuffer",		new Class[] { JDTORecord.class }, new Object[] { param });
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	/**
	 * 플렉스 화면 - 코일야드 차량 정보 조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public List getCoilYdCarInfoFlex(HashMap param) {
		EJBConnector ejbConn = null;
		String szMethodName = "getCoilYdCarInfoFlex";

		try {
			ejbConn = new EJBConnector("default", "YdFlexSeEJB", this);
			return (List) ejbConn.trx("getCoilYdCarInfoFlex",
					new Class[] { HashMap.class }, new Object[] { param });
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	
	/**
	 * 플렉스 화면 - 코일야드 크레인 현황 조회 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public List getCoilYdCrnStatFlex(HashMap param) {
		EJBConnector ejbConn = null;

		String szMethodName = "getPlateYdCrnStatFlex";

		try {
			ejbConn = new EJBConnector("default", "YdFlexSeEJB", this);
			return (List) ejbConn.trx("getCoilYdCrnStatFlex",
					new Class[] { HashMap.class }, new Object[] { param });
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
	//
	
	/**
	 * 플렉스 화면 - 정정야드후판 조회(총중량/총매수 조회)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 * 
	 * 추가일 2013.03.08
	 * 추가자 권휘원
	 */
	public List getPlateShareYdTotCntMgt(HashMap param) {
		EJBConnector ejbConn = null;
		String szMethodName = "getPlateShareYdTotCntMgt";

		try {
			ejbConn = new EJBConnector("default", "YdFlexSeEJB", this);
			return (List) ejbConn.trx("getPlateShareYdTotCntMgt",
					new Class[] { HashMap.class }, new Object[] { param });
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), 1);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
 	
		
}
