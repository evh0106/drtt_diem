/**
 * @(#)YdFlexSeEJBBean.java
 * 
 * @version			1.0
 * @author 			현대제철
 * @date			2011/04/28
 * 
 * @description		이클래스는 Flex 모니터링 화면 관련  Session EJB 클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2011/04/28                    최초 등록
 * V1.01  2013/03/12   조병기       조병기       2후판 제품창고 모니터링 관련  
 *                                      : getPlateYdCrnStatFlex, getYdStlnoStatFlex,
 *                                        getPlateYdCarInfoFlex
 *                                    
 */

package com.inisteel.cim.yd.jsp.flex.session;

import java.util.List;
import xlib.cmc.GridData;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.log.LogLevel;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;

//DAO
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;

//UTIL
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jsp.common.CmnUtil;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.common.util.CmUtil;
import java.util.HashMap;

/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="YdFlexSeEJB" jndi-name="YdFlexSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class YdFlexSeEJBBean extends BaseSessionBean {
	
	
	private YdUtils ydUtils = new YdUtils();
	private String szSessionName = getClass().getName();
	
	/**
	 * 메뉴관련 데이터베이스를 조작하는 DAO
	 */
	 
	private YDDataUtil  yddatautil = new YDDataUtil();
	 
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
		
	}
	
	/**
	 * 플렉스 화면 - 적치 BED 조회 화면    [getYdPlateGdsStkBedFlex- 후판제품 화면]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public List getYdPlateGdsStkBedFlex(HashMap param) {
		try {
		
			JDTORecord recGendata = JDTORecordFactory.getInstance().create();
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecordSet outRecordSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");			
			GridData grs = new GridData();
			YdStkBedDao ydStkBedDao = new YdStkBedDao();
			YdPlateCommDAO commDao = new YdPlateCommDAO();
			
			
			//Hash Map Data => Grid Data			
			grs = CmnUtil.hashMapToGridData(param);
			
			//Grid Data => JdtoRecord Data			
			recGendata = CmUtil.genJDTORecord(grs);
			recPara.setField("YD_GP", recGendata.getField("YD_GP"));
			recPara.setField("YD_STK_COL_GP", recGendata.getField("YD_STK_COL_GP"));
			
			if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(recGendata.getField("YD_GP"))) {
				//2후판(야드구분:T)일경우만 1,2후판 정보를 모두 읽을 수 있는 쿼리를 수행한다. (파라메터로 야드구분 2개를 넘겨준다)
				recPara.setField("YD_GP2", recGendata.getField("YD_GP2"));
				commDao.select(recPara, outRecordSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0049");
			} else {
				ydStkBedDao.getYdStkbed(recPara, outRecordSet, 8);
			}
			
			return CmnUtil.listJdtoRecordTohashMap(outRecordSet.toList());
		
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}	
		
	} //end of getYdPlateGdsStkBedFlex()
	
	
	/**
	 * 플렉스 화면 - 적치 BED 조회 화면    [통합]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public List getTotStkBedFlex(HashMap param) {
		try {
		
			JDTORecord recGendata = JDTORecordFactory.getInstance().create();
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecordSet outRecordSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");			
			GridData grs = new GridData();
			YdStkBedDao ydStkBedDao = new YdStkBedDao();
			
			
			//Hash Map Data => Grid Data			
			grs = CmnUtil.hashMapToGridData(param);
			
			//Grid Data => JdtoRecord Data			
			recGendata = CmUtil.genJDTORecord(grs);
			
			ydStkBedDao.getYdStkbed(recPara, outRecordSet, 37);
			return CmnUtil.listJdtoRecordTohashMap(outRecordSet.toList());
		
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}	
		
	} //end of getYdPlateGdsStkBedFlex()
	
	
	/**
	 * 플렉스 화면 -  C연주 슬라브야드 적치 재료 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public List getYdCSlabStlnoStatFlex(HashMap param) {
		try {
		
			JDTORecord recGendata = JDTORecordFactory.getInstance().create();			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");			
			GridData grs = new GridData();
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			
			
			//Hash Map Data => Grid Data			
			grs = CmnUtil.hashMapToGridData(param);
			
			//Grid Data => JdtoRecord Data			
			recGendata = CmUtil.genJDTORecord(grs);
			
			
			ydStkLyrDao.getYdStklyr(recGendata, outRecSet, 19);
			return CmnUtil.listJdtoRecordTohashMap(outRecSet.toList());
		
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}	
		
	} //end of getYdCSlabStlnoStatFlex()
	
	
	/**
	 * 플렉스 화면 - 크레인 상태 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public List getPlateYdCrnStatFlex(HashMap param) {
		try {
		
			JDTORecord recGendata = JDTORecordFactory.getInstance().create();
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");			
			GridData grs = new GridData();
			YdEqpDao ydEqpDao = new YdEqpDao();
			YdPlateCommDAO commDao = new YdPlateCommDAO();
			
			//Hash Map Data => Grid Data			
			grs = CmnUtil.hashMapToGridData(param);
			
			//Grid Data => JdtoRecord Data			
			recGendata = CmUtil.genJDTORecord(grs);
			
			if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(recGendata.getField("YD_GP"))) {
				//2후판(야드구분:T)일경우만 1,2후판 정보를 모두 읽을 수 있는 쿼리를 수행한다. (파라메터로 야드구분 2개를 넘겨준다)
				commDao.select(recGendata, outRecSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0052");
			} else {
				ydEqpDao.getYdEqp(recGendata, outRecSet, 5);
			}
			return CmnUtil.listJdtoRecordTohashMap(outRecSet.toList());
		
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}	
		
	} //end of getPlateYdCrnStatFlex()
	
	
	/**
	 * 플렉스 화면 -야드 적치 재료 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public List getYdStlnoStatFlex(HashMap param) {
		try {
		
			JDTORecord recGendata = JDTORecordFactory.getInstance().create();
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");			
			GridData grs = new GridData();
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			YdPlateCommDAO commDao = new YdPlateCommDAO();
			
			//Hash Map Data => Grid Data			
			grs = CmnUtil.hashMapToGridData(param);
			
			//Grid Data => JdtoRecord Data			
			recGendata = CmUtil.genJDTORecord(grs);
			
			recPara.setField("YD_GP1",yddatautil.setDataDefault(recGendata.getField("YD_GP"), ""));
			recPara.setField("YD_GP2",yddatautil.setDataDefault(recGendata.getField("YD_GP"), ""));
			recPara.setField("YD_STK_COL_GP",yddatautil.setDataDefault(recGendata.getField("YD_STK_COL_GP"), ""));
			
			String syd_gp= yddatautil.setDataDefault(recGendata.getField("YD_GP"), "");
			if(syd_gp.equals("K")){
				//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStlnoStatFlexK
				ydStkLyrDao.getYdStklyr(recPara, outRecSet, 616);
				
			}else if(syd_gp.equals("T")) {
				
				//2후판(야드구분:T)일경우만 1,2후판 정보를 모두 읽을 수 있는 쿼리를 수행한다. (파라메터로 야드구분 2개를 넘겨준다)
				recPara.setField("YD_GP",  recGendata.getField("YD_GP"));
				recPara.setField("YD_GP2", recGendata.getField("YD_GP2"));
				commDao.select(recPara, outRecSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0050");	
				
			}else {
				//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStlnoStatFlex
				ydStkLyrDao.getYdStklyr(recPara, outRecSet, 38);
			}
			return CmnUtil.listJdtoRecordTohashMap(outRecSet.toList());
		
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}	
		
	} //end of getYdStlnoStatFlex()
	
	/**
	 * 플렉스 화면 -코일소재야드 스판정보 조회(단정보까지 조회)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public List getYdStkLytInfoFlex(HashMap param) {
		try {
		
			JDTORecord recGendata = JDTORecordFactory.getInstance().create();
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");			
			GridData grs = new GridData();
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			
			
			//Hash Map Data => Grid Data			
			grs = CmnUtil.hashMapToGridData(param);
			
			//Grid Data => JdtoRecord Data			
			recGendata = CmUtil.genJDTORecord(grs);
			
			recPara.setField("YD_STK_COL_GP",yddatautil.setDataDefault(recGendata.getField("YD_STK_COL_GP"), ""));
			
			
			ydStkLyrDao.getYdStklyr(recPara, outRecSet, 43);
			return CmnUtil.listJdtoRecordTohashMap(outRecSet.toList());
		
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}	
		
	} //end of getYdStkLytInfoFlex()
	
	
	
	/**
	 * 플렉스 화면 -코일소재야드 스판정보 조회(단정보까지 조회)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public List getCoilGdsYdStkLytInfoFlex(HashMap param) {
		try {
		
			JDTORecord recGendata = JDTORecordFactory.getInstance().create();
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");			
			GridData grs = new GridData();
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			
			
			//Hash Map Data => Grid Data			
			grs = CmnUtil.hashMapToGridData(param);
			
			//Grid Data => JdtoRecord Data			
			recGendata = CmUtil.genJDTORecord(grs);
			
			recPara.setField("YD_STK_COL_GP",yddatautil.setDataDefault(recGendata.getField("YD_STK_COL_GP"), ""));
			
			
			ydStkLyrDao.getYdStklyr(recPara, outRecSet, 47);
			return CmnUtil.listJdtoRecordTohashMap(outRecSet.toList());
		
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}	
		
	} //end of getCoilGdsYdStkLytInfoFlex()
	
	
	
	
	/**
	 * 플렉스 화면 -코일소재야드 스판정보 조회(단정보까지 조회)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public List getPlateYdCarInfoFlex(HashMap param) {
		try {
		
			JDTORecord recGendata = JDTORecordFactory.getInstance().create();
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");			
			GridData grs = new GridData();
			
			
			YdStkColDao ydStkColDao = new YdStkColDao();
			YdPlateCommDAO commDao = new YdPlateCommDAO();
			
			
			//Hash Map Data => Grid Data			
			grs = CmnUtil.hashMapToGridData(param);
			
			//Grid Data => JdtoRecord Data			
			recGendata = CmUtil.genJDTORecord(grs);
			
			recPara.setField("YD_GP",yddatautil.setDataDefault(recGendata.getField("YD_GP"), ""));
			
			if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(recGendata.getField("YD_GP"))) {
				//2후판(야드구분:T)일경우만 1,2후판 정보를 모두 읽을 수 있는 쿼리를 수행한다. (파라메터로 야드구분 2개를 넘겨준다)
				recPara.setField("YD_GP2", recGendata.getField("YD_GP2"));
				commDao.select(recPara, outRecSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0051");
			} else {
				ydStkColDao.getYdStkcol(recPara, outRecSet, 12);
			}
			
			
	
			return CmnUtil.listJdtoRecordTohashMap(outRecSet.toList());
		
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}	
		
	} //end of getPlateYdCarInfoFlex()
	
	
	
	/**
	 * 플렉스 화면 - 총매수 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public List getAPlateYdTotCount(HashMap param) {
		try {
		
			JDTORecord recGendata = JDTORecordFactory.getInstance().create();
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");			
			GridData grs = new GridData();
			
			
			
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			//Hash Map Data => Grid Data			
			grs = CmnUtil.hashMapToGridData(param);
			
			//Grid Data => JdtoRecord Data			
			recGendata = CmUtil.genJDTORecord(grs);
			
			recPara.setField("YD_GP",yddatautil.setDataDefault(recGendata.getField("YD_GP"), ""));
			recPara.setField("YD_STK_COL_GP",yddatautil.setDataDefault(recGendata.getField("YD_STK_COL_GP"), ""));
			
			ydStkLyrDao.getYdStklyr(recPara, outRecSet, 85);
	
			return CmnUtil.listJdtoRecordTohashMap(outRecSet.toList());
		
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}	
		
	} //end of getAPlateYdTotCount()
	
	
	/**
	 * 플렉스 화면 - 총중량 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public List getAPlateYdTotSumMgt(HashMap param) {
		try {
		
			JDTORecord recGendata = JDTORecordFactory.getInstance().create();
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");			
			GridData grs = new GridData();
			
			
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			
			
			//Hash Map Data => Grid Data			
			grs = CmnUtil.hashMapToGridData(param);
			
			//Grid Data => JdtoRecord Data			
			recGendata = CmUtil.genJDTORecord(grs);
			
			recPara.setField("YD_GP",yddatautil.setDataDefault(recGendata.getField("YD_GP"), ""));
			recPara.setField("YD_STK_COL_GP",yddatautil.setDataDefault(recGendata.getField("YD_STK_COL_GP"), ""));
			
			ydStkLyrDao.getYdStklyr(recPara, outRecSet, 86);
	
			return CmnUtil.listJdtoRecordTohashMap(outRecSet.toList());
		
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}	
		
	} //end of getAPlateYdTotSumMgt()
	
	
	
	

	/**
	 * 플렉스 화면 - 해당 베드 정보를 읽어오는 Function
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public List getChBedInfoFlex(HashMap param) {
		try {
		
			JDTORecord recGendata = JDTORecordFactory.getInstance().create();
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");			
			GridData grs = new GridData();
			
			
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			
			
			//Hash Map Data => Grid Data			
			grs = CmnUtil.hashMapToGridData(param);
			
			//Grid Data => JdtoRecord Data			
			recGendata = CmUtil.genJDTORecord(grs);
			
			recPara.setField("YD_STK_COL_GP",yddatautil.setDataDefault(recGendata.getField("YD_STK_COL_GP"), ""));
			recPara.setField("YD_STK_BED_NO",yddatautil.setDataDefault(recGendata.getField("YD_STK_BED_NO"), ""));
			
			ydStkLyrDao.getYdStklyr(recPara, outRecSet, 94);

			return CmnUtil.listJdtoRecordTohashMap(outRecSet.toList());
		
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}	
		
	} //end of getChBedInfoFlex()
	
	
	
	

	/**
	 * 플렉스 화면 - 선택된 설비(크레인)를 조회하는 Function 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public List getChCraneInfoFlex(HashMap param) {
		try {
		
			JDTORecord recGendata = JDTORecordFactory.getInstance().create();
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");			
			GridData grs = new GridData();
			
			
			YdEqpDao ydEqpDao = new YdEqpDao();
			
			//Hash Map Data => Grid Data			
			grs = CmnUtil.hashMapToGridData(param);
			
			//Grid Data => JdtoRecord Data			
			recGendata = CmUtil.genJDTORecord(grs);
			
			recPara.setField("YD_EQP_ID",yddatautil.setDataDefault(recGendata.getField("YD_EQP_ID"), ""));

			ydEqpDao.getYdEqp(recPara, outRecSet, 0);
			
			return CmnUtil.listJdtoRecordTohashMap(outRecSet.toList());
		
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}	
		
	} //end of getChCraneInfoFlex()
	
	
	/**
	 * 플렉스 화면 - 선택된 설비(크레인)를 조회하는 Function 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void wrkBuffer(JDTORecord param) {
		try {
			
			
			
		//	ydUtils.putYdFlexCrnWrk("", param);
			
			
			
			
			
			
			String szMsg="";	
			HashMap hmap = new HashMap();
			
			HashMap subhmap = new HashMap();
			String szOperationName = "크레인 작업 실적 위치 및 설비 전송";
			String szMethodName = "putYdFlexCrnWrk";
			String szEqpId = "";
			String szFromStkPos = "";
			String szToStkPos = "";
			String szYdGp = "";
			String destid = "";
			JDTORecord recPara =  JDTORecordFactory.getInstance().create();
			JDTORecordSet outRecSet = null;
			int intRtn = 0;
			
			YdDaoUtils ydDaoUtils = new YdDaoUtils();
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			
			
			try{
			
				szYdGp = ydDaoUtils.paraRecChkNull(param, "YD_GP");
				szEqpId = ydDaoUtils.paraRecChkNull(param, "YD_EQP_ID");
				szFromStkPos = ydDaoUtils.paraRecChkNull(param, "YD_UP_WR_LOC");
				szToStkPos = ydDaoUtils.paraRecChkNull(param, "YD_DN_WR_LOC");

				destid = YdConstant.YD_MONITORING_CHANNEL_A;
			
				
				hmap.put("MSG_GP", YdConstant.YD_EVT_FUN);  //  야드 실적정보받아 처리 ('F')
				hmap.put("YD_GP", szYdGp ); 
				hmap.put("YD_EQP_ID", szEqpId);
				
				hmap.put("YD_UP_WR_LOC", szFromStkPos);
				hmap.put("YD_DN_WR_LOC", szToStkPos);
				
				szMsg	= "[YdUtils : "+szOperationName+"] 채널 전송";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				if(szFromStkPos.length()==8){
					outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");	
					recPara.setField("YD_STK_COL_GP",szFromStkPos.substring(0,6));
					recPara.setField("YD_STK_BED_NO",szFromStkPos.substring(6,8));
					intRtn = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 94);
					
					
					szMsg	= "[YdUtils : "+szOperationName+"]  UP 조회건수 :" + intRtn +"건";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					hmap.put("YD_UP_WR_LOC_ARR" , CmnUtil.listJdtoRecordTohashMap(outRecSet.toList()));
					
				}
				
				
				if(szToStkPos.length()==8){
					outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");	
					recPara.setField("YD_STK_COL_GP",szToStkPos.substring(0,6));
					recPara.setField("YD_STK_BED_NO",szToStkPos.substring(6,8));
					intRtn = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 94);
					
					
					szMsg	= "[YdUtils : "+szOperationName+"] DN 조회건수 :" + intRtn +"건";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					hmap.put("YD_DN_WR_LOC_ARR" , CmnUtil.listJdtoRecordTohashMap(outRecSet.toList()));
					
				}
				
				//FLAX_PUSH destid 지정 /////////////////////////////////////////////////
				if(szYdGp.equals("A")){
					destid = YdConstant.YD_MONITORING_CHANNEL_A;
				}else if(szYdGp.equals("D")){
					destid = YdConstant.YD_MONITORING_CHANNEL_D;
				}else if(szYdGp.equals("K")){
					destid = YdConstant.YD_MONITORING_CHANNEL_K;
				}else if(szYdGp.equals("T")){
					destid = YdConstant.YD_MONITORING_CHANNEL_T;
				}else if(szYdGp.equals("H")){
					destid = YdConstant.YD_MONITORING_CHANNEL_H;
				}else if(szYdGp.equals("J")){
					destid = YdConstant.YD_MONITORING_CHANNEL_J;
				}else if(szYdGp.equals("S")){
					destid = YdConstant.YD_MONITORING_CHANNEL_S;
				}else {
					destid = YdConstant.YD_MONITORING_CHANNEL_A;
				}
				/////////////////////////////////////////////////////////////////////////
				
				//FLAX_PUSH사용여부 체크 /////////////////////////////////////////////////
			 	//JDTORecord recPara =  JDTORecordFactory.getInstance().create();
		    	JDTORecord recInTemp =  JDTORecordFactory.getInstance().create();
		    	String CHK ="N";
				//JDTORecordSet outRecSet = null;
				//YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
				
		    	outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				recPara =  JDTORecordFactory.getInstance().create();
				recPara.setField("YD_GP",szYdGp);
				/*com.inisteel.cim.yd.common.util.YdUtils.chklist*/
				int intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 999);
				
				if( intRtnVal > 0 ) {
					outRecSet.first();
					recInTemp = outRecSet.getRecord();
					CHK = recInTemp.getFieldString("CHK").trim();
				}
 
				ydUtils.putLog(szSessionName, szMethodName,  szYdGp+":야드 FLAX PUSH 사용유무:"+CHK+",destID:"+destid, YdConstant.INFO);
				/////////////////////////////////////////////////////////////////////////
		    	if(CHK.equals("Y")){
				ydUtils.pushToFlexClient(destid, hmap);
		    	}
				
				
			}catch (Exception e){
				
				szMsg	= "[YdUtils : "+szOperationName+"] Exception Error : "+ e.getLocalizedMessage();
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
			} // end of try-catch()
		
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}	
		
	} //end of getChCraneInfoFlex()
	
	/**
	 * 플렉스 화면 -코일야드 스판정보 조회(증설)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public List getCoilYdCarInfoFlex(HashMap param) {
		try {
		
			JDTORecord recGendata = JDTORecordFactory.getInstance().create();
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");			
			GridData grs = new GridData();
			
			
			YdStkColDao ydStkColDao = new YdStkColDao();
			
			
			//Hash Map Data => Grid Data			
			grs = CmnUtil.hashMapToGridData(param);
			
			//Grid Data => JdtoRecord Data			
			recGendata = CmUtil.genJDTORecord(grs);
			
			recPara.setField("YD_GP",yddatautil.setDataDefault(recGendata.getField("YD_GP"), ""));
			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getCoilYdStkColByYdgp_Flex*/
			ydStkColDao.getYdStkcol(recPara, outRecSet, 401);
	
			return CmnUtil.listJdtoRecordTohashMap(outRecSet.toList());
		
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}	
		
	} //end of getPlateYdCarInfoFlex()
	
	/**
	 * 플렉스 화면 - 코일야드크레인 상태 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public List getCoilYdCrnStatFlex(HashMap param) {
		try {
		
			JDTORecord recGendata = JDTORecordFactory.getInstance().create();
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");			
			GridData grs = new GridData();
			YdEqpDao ydEqpDao = new YdEqpDao();
			
			
			//Hash Map Data => Grid Data			
			grs = CmnUtil.hashMapToGridData(param);
			
			//Grid Data => JdtoRecord Data			
			recGendata = CmUtil.genJDTORecord(grs);
			
			/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpCoilFlex*/
			ydEqpDao.getYdEqp(recGendata, outRecSet, 402);
			return CmnUtil.listJdtoRecordTohashMap(outRecSet.toList());
		
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}	
		
	} //end of getPlateYdCrnStatFlex()
	
	//
	/**
	 * 플렉스 화면 - 정정야드 총매수 총중량 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public List getPlateShareYdTotCntMgt(HashMap param) {
		try {
		
			JDTORecord recGendata = JDTORecordFactory.getInstance().create();
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");			
			GridData grs = new GridData();
			
			
			
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			//Hash Map Data => Grid Data			
			grs = CmnUtil.hashMapToGridData(param);
			
			//Grid Data => JdtoRecord Data			
			recGendata = CmUtil.genJDTORecord(grs);
			
			recPara.setField("YD_GP",yddatautil.setDataDefault(recGendata.getField("YD_GP"), ""));
			
			ydStkLyrDao.getYdStklyr(recPara, outRecSet, 108);
	
			return CmnUtil.listJdtoRecordTohashMap(outRecSet.toList());
		
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
			
		}	
		
	} //end of getAPlateYdTotCount()
	
}

