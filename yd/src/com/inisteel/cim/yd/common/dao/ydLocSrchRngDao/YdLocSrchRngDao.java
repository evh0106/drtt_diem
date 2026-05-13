package com.inisteel.cim.yd.common.dao.ydLocSrchRngDao;

import java.util.Iterator;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

/**
 *      [A] 클래스명 : 야드위치검색범위 DAO
 * 
*/

public class YdLocSrchRngDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
// PIDEV
	private YdPICommDAO	ydPICommDAO = new YdPICommDAO();
	private CCommUtils	   commUtils   = new CCommUtils();		
	
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrng";
	private String szQueryIdGet9 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdAidLocsrchrng";
	
	//20090315 이현성 
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngBySchCdandRouteGp";
	//20090325 이현성 
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngByYdgpBayGpSchCd";
	
	//20090422 이현성 - 야드별 MAX 차수 구하기  
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngMaxSno";
	
	//20090909 석창화 - 스케줄 체크를 위한 BED정보 읽어오기  
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngSchCheck";
	
	//20090909 석창화 - 스케줄 체크를 위한 BED정보 읽어오기(사용자 지증)  
	private String szQueryIdGet6 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngSchCheckByToLoc";
	 
	//20090922 이현성  getLocSrchRng
	private String szQueryIdGet7 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getLocSrchRng";
	//20100308 석창화 Coil제품쪽 위치검색배드
	private String szQueryIdGet8 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngBySchCdRtGpandStlNo_PIDEV";
	
	//20150728 hun Coil제품쪽 위치검색배드 두번째
	private String szQueryIdGet601 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngBySchCdandStkBedUsg";
	//20150728 hun Coil제품쪽 위치검색배드 세번째
	private String szQueryIdGet602 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngBySchCdandStkAllBed";
	
	private String szQueryIdGet603 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngUserByToLoc4";
	
	private String szQueryIdGet604 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngUserByToLocIn";
	
	//20140718 윤재광 Plate 특정제품에 대한 저장위치 검색
	private String szQueryIdGet11 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getIsLocDanPok";
	private String szQueryIdGet12 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getToLocDanPok";
	private String szQueryIdGet13 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getToLocDanPok3";
	
	//20100308 Coil제품쪽 위치검색배드(지정시)
	private String szQueryIdGet300 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngUserByToLoc";
	
//	20100402 이종헌 (화면:위치검색SPAN관리)
	private String szQueryIdGet301 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngByYdgpBayGpSchCdCoil";

//	20100406 이종헌 (화면:위치검색SPAN관리)
	private String szQueryIdGet302 = "com.inisteel.cim.yd.dao.ydlocsrchbeddao.YdLocsrchbedDao.getStrGtrCodeNew";

	// 위치검색배드
	private String szQueryIdGet303 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngBySchCdandRouteGpCoilStlNo";

	private String szQueryIdGet304 = "com.inisteel.cim.yd.dao.ydlocsrchbeddao.YdLocsrchbedDao.getSchCodeNew";
	
	//20100308 Coil소재쪽 위치검색배드(6자리지정시)
	private String szQueryIdGet400 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngUserByToLoc6";
	//20100308 Coil소재쪽 위치검색배드(8자리지정시)
	private String szQueryIdGet401 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngUserByToLoc8";
	
	//20100308 Coil소재쪽 위치검색배드(6자리지정시)
	private String szQueryIdGet500 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngUserGdsByToLoc6";
	//20100308 Coil제품쪽 대차위치검색배드
	private String szQueryIdGet501 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngUserGdsByToLoc8";

	
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.insYdLocsrchrng";
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.updYdLocsrchrng";
	
	//delete query id
	private String szQueryIdDed1 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.deldYdLocsrchrng";
	
//	20100406 이종헌 (화면:위치검색SPAN관리)
	private String szQueryIdUpd300 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.updYdLocsrchrngCoil";

//	20100526 박지열 - 야드별 MAX 차수 구하기  
	private String szQueryIdGet600 = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocsrchrngMaxSno_600";
	
	
/*------------------------------------- SELECT -------------------------------------------*/
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드위치검색범위 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:YD_SCH_CD,YD_ROUTE_GP,YD_LOC_SRCH_RNG_REG_SNO
	 *         								1:YD_SCH_CD, YD_ROUTE_GP
	 *         								2:YD_GP, YD_BAY_GP ,YD_SCH_CD
	 *         								3:YD_GP 
	 *         )
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdLocsrchrng(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdLocsrchrng";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;
		
		try {
			
			/*	JDTORecord
			 *	넘어온 inRec 헤더를 키를 본다
			 */
			/*
			Iterator iter = inRec.iterateName();
			while(iter.hasNext()){
				String str = (String) iter.next();
				ydUtils.putLog("DAO", szMethodName, "NAME::"+str +" -- value::" +inRec.getFieldString(str), YdConstant.INFO);
			}
			*/
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdLocsrchrng(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet1);
			else if(intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet2);
			else if(intGp == 2)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet3);
			else if(intGp == 3)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet4);
			else if(intGp == 4)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet5);
			else if(intGp == 5)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet6);
			else if(intGp == 6)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet7);
			else if(intGp == 7)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet8);
			else if(intGp == 8)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet9);
			else if(intGp == 300)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet300);
			else if(intGp == 301)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet301);
			else if(intGp == 302)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet302);
			else if(intGp == 303)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet303);
			else if(intGp == 304)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet304);
			else if(intGp == 400)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet400);
			else if(intGp == 401)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet401);
			else if(intGp == 500)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet500);
			else if(intGp == 501)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet501);
			else if(intGp == 600)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet600);
			else if(intGp == 601)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet601);
			else if(intGp == 602)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet602);
			else if(intGp == 603)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet603);
			else if(intGp == 604)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet604);

// PIDEV
			//PIDEV_S :병행가동용:PI_YD
//			String sPI_YD     = commUtils.nvl(recPara.getFieldString("V_PI_YD"), "*");						
//			String toQuery_ID = ydPICommDAO.getYdRulePI("", szMethodName, "YD0001", recPara.getField("JSPEED_QUERY_ID").toString(), "APPPI0", sPI_YD, "*");
//			recPara.setField("JSPEED_QUERY_ID", toQuery_ID);
			
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0)
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
			else {
				//data not found
	//			szMsg = "data not found!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 3);
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal = rsTemp.size();
	} //end of getYdLocsrchrng
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드위치검색범위 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec  parameter record
	 *         int        intGp  구분(0:YD_SCH_CD,YD_ROUTE_GP,YD_LOC_SRCH_RNG_REG_SNO
	 *         						 1:YD_SCH_CD, YD_ROUTE_GP 
	 *         						 2:YD_GP,YD_BAY_GP ,YD_SCH_CD
	 *         						 3: YD_LOC_SRCH_RNG_REG_SNO )
	 * @return boolean           true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdLocsrchrng(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;

		try {
			if (intGp == 0) {
				szFieldName = "V_YD_LOC_SRCH_RNG_REG_SNO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'L', 0, 0);	
				
			} else if (intGp ==1 || intGp ==6){
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_ROUTE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			} else if (intGp ==2 ){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_ROUTE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				
				
				
			}else if (intGp == 3) {
				//체크하지 않는다.
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				
			}else if (intGp == 4) {
			
				szFieldName = "V_YD_SCH_CD1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_ROUTE_GP1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				
				szFieldName = "V_YD_SCH_CD2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_ROUTE_GP2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				
				szFieldName = "V_YD_SCH_CD3";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_ROUTE_GP3";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			}else if (intGp == 5) {
				//체크하지 않는다.
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				
			} else if (intGp == 7){
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_ROUTE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'S', 0, 0);
			} else if (intGp == 300){
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'S', 0, 0);
			} else if (intGp == 400){
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'S', 0, 0);
			} else if (intGp == 401){
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'S', 0, 0);
			} else if (intGp == 500){
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'S', 0, 0);
			} else if (intGp == 501){
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'S', 0, 0);
			}	
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdLocsrchrng
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드위치검색범위 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdLocsrchrng(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkParameter(recPara);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
	
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdIns1);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdLocsrchrng
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드위치검색범위 INSERT parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_YD_SCH_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_ROUTE_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_LOC_SRCH_RNG_REG_SNO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STR_GTR_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_REGISTER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_REG_DDTT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
//			szFieldName = "V_MODIFIER";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
//			if (!blnErr) return blnErr;
//	
//			szFieldName = "V_MOD_DDTT";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
//			if (!blnErr) return blnErr;
//			
//			szFieldName = "V_DEL_YN";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
//			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_LOC_SRCH_RNG_SEQ";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_LOC_SRCH_RNG_ACT_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_SRCH_METHOD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		
		return blnErr;
	} //end of chkParameter
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드위치검색범위 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_SCH_CD,YD_ROUTE_GP,YD_LOC_SRCH_RNG_REG_SNO)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdLocsrchrng(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdLocsrchrng";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		
		try {
			
			//recordSet create
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			//변환용 레코드
			JDTORecord recInPara = null;
			JDTORecord recOutPara = null;
			
			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//update data select
			intRtnVal = this.getYdLocsrchrng(inRec, outRecSet, 0);
			
			//parameter error return
			if (intRtnVal < 0) {
	//			szMsg = "parameter error!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			}
			
			//data not found return
			if (intRtnVal == 0) {
	//			szMsg = "data not found!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			}
			
			//duplicate data return
			if (outRecSet.size() != 1) {
	//			szMsg = "duplicate data!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}
			
			outRecSet.first();
			outRec = outRecSet.getRecord();
	
			//필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(outRec, 0);
			
			//data mapping
			this.dataMapping(recInPara, recOutPara);
			
			//parameter check
			blnChk_Field = this.chkParameter(recOutPara);
			
			//parameter error return
			if (!blnChk_Field) return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd1);
			else if(intGp == 300)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd300);
				
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdLocsrchrng
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드위치검색범위 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		try {
			szFieldName = "V_YD_SCH_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_ROUTE_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_LOC_SRCH_RNG_REG_SNO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STR_GTR_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_REGISTER";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_REG_DDTT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_MODIFIER";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_MOD_DDTT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_DEL_YN";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_LOC_SRCH_RNG_SEQ";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_LOC_SRCH_RNG_ACT_STAT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_BED_SRCH_METHOD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}

	} // end of dataMapping
	
	
	
/*------------------------------------- DELETE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드위치검색열  delete
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_SCH_CD,YD_ROUTE_GP,
	 *                                YD_LOC_SRCH_RNG_REG_SNO,YD_LOC_SRCH_BED_REG_SNO)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int delYdLocSrchRngCoil(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "delYdLocSrchRngCoil";
		String szMsg = null;
	
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		
		try {			
			JDTORecord recPara = null;
						
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			/*	JDTORecord
			 *	넘어온 recOutPara 헤더를 키를 본다
			 */
//			Iterator iter = recPara.iterateName();
//			while(iter.hasNext()){
//				String str = (String) iter.next();
//				ydUtils.putLog("DAO", szMethodName, "NAME::"+str +" -- value::" +recPara.getFieldString(str), YdConstant.INFO);
//			}
			
			//query id setting
			if(intGp == 300) recPara.setField("JSPEED_QUERY_ID", szQueryIdDed1);
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of delYdLocSrchRngCoil
	
	public int getToLocWithDanPok(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getToLocWithDanPok";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		JDTORecord recPara = null;
		
		try {
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			 
			//query id setting
			if (intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet11);
			else if(intGp == 2)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet12);
			else if(intGp == 3)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet13);
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara); 
			
			//result recordSet check
			if (rsTemp.size() > 0)
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
			else {
				//data not found
	//			szMsg = "data not found!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 3);
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal = rsTemp.size();
	} //end of getToLocWithDanPok
	
} // end of class






