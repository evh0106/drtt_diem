package com.inisteel.cim.yd.jsp.common.Dao;

import java.util.List;

import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;


import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
//import com.inisteel.cim.cs.common.util.CmnUtil;
//import com.inisteel.cim.cs.common.util.CsGridUtil;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;

/**
 * 화면 공통 UPDATE 관련 DAO (독자적)
 *
 * @author 이현성
 */
public class JspCommonDAO extends DBAssistantDAO{


	private YdPICommDAO ydPICommDAO = new YdPICommDAO();
	private CCommUtils	   commUtils   = new CCommUtils();
	
	/**
	 * 코일 공통 UPDATE 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updPtCoilComm(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.updPtCoilComm");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	/**
	 * 후판 공통 UPDATE 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updPtPlateComm(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.updPtPlateComm");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	
	/**
	 *저장품 (코일) 항목 수정
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updStockCoilComm(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.updStockCoilComm");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	
	
	
	/**
	 *  저장품 (후판) 항목 수정
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updStockPlateComm(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.updStockPlateComm");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	/**
	 * 후판 공통 Book-Out 위치 UPDATE 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updPtPlateCommForBookOut(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.updPtPlateCommForBookOut");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	/**
	 * 후판 공통 Book-Out 위치 UPDATE 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public int updPtPlateCommForBookOutBYOrdPiling(JDTORecord inRec) throws DAOException, JDTOException {

		String szMethodName         = "updPtPlateCommForBookOutBYOrdPiling";
		int intRtnVal               = -1;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
//				//트렌젝션 분리 적용	
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				ejbConn.trx("updPtPlateCommForBookOutBYOrdPilingReTX", new Class[] { JDTORecord.class }, new Object[] { inRec});

//	    	}else{
//	    		//기존 방식 적용 
//	            this.updPtPlateCommForBookOutBYOrdPilingTX(inRec);
//	   
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException( e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updPtPlateCommForBookOutBYOrdPiling
	/**
	 * 후판 공통 Book-Out 위치 UPDATE 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updPtPlateCommForBookOutBYOrdPilingTX(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.updPtPlateCommForBookOutBYOrdPiling");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	/**
	 * 후판 공통 Book-Out 위치 UPDATE 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public int updPtPlateCommForBookOutYeojae(JDTORecord inRec) throws DAOException, JDTOException {

		String szMethodName         = "updPtPlateCommForBookOutYeojae";
		int intRtnVal               = -1;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
//				//트렌젝션 분리 적용	
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				ejbConn.trx("updPtPlateCommForBookOutYeojaeReTX", new Class[] { JDTORecord.class }, new Object[] { inRec});

//	    	}else{
//	    		//기존 방식 적용 
//	            this.updPtPlateCommForBookOutYeojaeTX(inRec);
//	   
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException( e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updPtPlateCommForBookOutYeojae
	
	/**
	 * 후판 공통 Book-Out 위치 UPDATE 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updPtPlateCommForBookOutYeojaeTX(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.updPtPlateCommForBookOutYeojae");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}//updPtPlateCommForBookOutYeojaeTX
	
	
	/**
	 * 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.14
	 */
	public JDTORecordSet getUsableBedList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO.getUsableBedList");
			outRdSet = this.getRecordSet(inDto);
		
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	
	/**
	 * 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @작성자 :
	 * @작성일 : 
	 */
	public JDTORecordSet getpPlateYdBookoutStlList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO.getpPlateYdBookoutStlList");
			outRdSet = this.getRecordSet(inDto);
		
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	
	/**
	 * 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @작성자 :
	 * @작성일 : 
	 */
	public JDTORecordSet getpPlateYdBedList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO.getpPlateYdBedList");
			outRdSet = this.getRecordSet(inDto);
		
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @작성자 :
	 * @작성일 : 
	 */
	public JDTORecordSet getpPlateYdBedList99(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO.getpPlateYdBedList99");
			outRdSet = this.getRecordSet(inDto);
		
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	/**
	 * 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @작성자 :
	 * @작성일 : 
	 */
	public JDTORecordSet getpPlateYdBedList2(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO.getpPlateYdBedList2");
			outRdSet = this.getRecordSet(inDto);
		
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	
	/**
	 * 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @작성자 :
	 * @작성일 : 
	 */
	public JDTORecordSet getpPlateYdLayerList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO.getpPlateYdLayerList");
			outRdSet = this.getRecordSet(inDto);
		
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	/**
	 * 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @작성자 :
	 * @작성일 : 
	 */
	public JDTORecordSet getpPlateYdLayerList_L(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO.getpPlateYdLayerList_L");
			outRdSet = this.getRecordSet(inDto);
		
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @작성자 :
	 * @작성일 : 
	 */
	public JDTORecordSet getpPlateYdLocationList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO.getpPlateYdLocationList");
			outRdSet = this.getRecordSet(inDto);
		
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	
	/**
	 * 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @작성자 :
	 * @작성일 : 
	 */
	public JDTORecordSet getpPlateYdBookoutStlDtlList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			
			String queryId = "com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO.getpPlateYdBookoutStlDtlList_PIDEV";
			
//PIDEV
//			queryId = ydPICommDAO.getYdRulePI("", "getpPlateYdBookoutStlDtlList", "YD0001", queryId, "APPPI0", "*", "*");			
			
			inDto.setField("JSPEED_QUERY_ID", queryId);
			outRdSet = this.getRecordSet(inDto);
		
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	/**
	 * 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @작성자 :
	 * @작성일 : 
	 */
	public JDTORecordSet getpPlateYdStlList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO.getpPlateYdStlList");
			outRdSet = this.getRecordSet(inDto);
		
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	
	/**
	 * 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @작성자 : 
	 * @작성일 : 
	 */
	public JDTORecordSet getpPlateYdBookoutYdList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO.getpPlateYdBookoutYdList");
			outRdSet = this.getRecordSet(inDto);
		
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	
	
	/**
	 * 야드관리 > 코일소재야드 / 코일제품창고 > 저장관리 > 이적작업진행관리(목록리스트 조회 -> 대상재 )
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.19
	 */
	public JDTORecordSet getMvstkProgMgtList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO.getMvstkProgMgtList");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 야드관리 > 코일소재야드 / 코일제품창고 > 저장관리 > 이적작업진행관리(목록리스트 조회 -> 작진행분  )
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.19
	 */
	public JDTORecordSet getMvstkProgMgtWorkList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO.getMvstkProgMgtWorkList");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 야드관리 > 코일소재야드 / 코일제품창고 > 저장관리 > 이적작업진행관리(동별 이적/이송건수 조회 -> 대상재)
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.19
	 */
	public JDTORecordSet getMvstkProgMgtBayCnt(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO.getMvstkProgMgtBayCnt");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 야드관리 > 코일소재야드 / 코일제품창고 > 저장관리 > 이적작업진행관리(동별 이적/이송건수 조회 -> 작업진행분 )
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.19
	 */
	public JDTORecordSet getMvstkProgMgtWorkBayCnt(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO.getMvstkProgMgtWorkBayCnt");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	/**
	 * 야드관리 > 코일제품창고 > 출하관리 > 출하차량상차LOT List  (목록조회)
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.19
	 */
	public JDTORecordSet getDmCarLotList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO.getDmCarLotList_PIDEV");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	
	/**
	 * 후판 정정야드  크레인스케쥴 등록 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void insertpPlateCrnSch(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.insertpPlateCrnSch");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	

	/**
	 * 후판 정정야드  크레인스케쥴재료 등록 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void insertpPlateCrnSchMtl(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.insertpPlateCrnSchMtl");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	
	/**
	 * 후판 정정야드  북아웃정보 스케쥴등록유무 업데이트
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updatepPlateCrnSchMtl(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.updatepPlateCrnSchMtl");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	/**
	 * 후판 정정야드 단정보 업데이트
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updatepPlateYdStkLayer(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.updatepPlateYdStkLayer");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	
	/**
	 * 후판 정정야드 단정보 업데이트
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updatepPlateYdStkLayer_Up(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.updatepPlateYdStkLayer_Up");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	
	/**
	 * 후판 정정야드 마감단정보 업데이트
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updatepPlateYdStkLayer_End(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.updatepPlateYdStkLayer_End");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	/**
	 * 후판 정정야드 마감단정보 업데이트
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updatepPlateYdStkLayer_End2(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.updatepPlateYdStkLayer_End2");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	/**
	 * 후판 정정야드 마감단정보 행/열정보 업데이트
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updatepPlateYdStkCol_End(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.updatepPlateYdStkCol_End");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	
	/**
	 * 후판 정정야드  북아웃원인코드 등록(ROLL_MAT)
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updatepPlateBookoutRse_ROLL(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.updatepPlateBookoutRse_ROLL");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	/**
	 * 후판 정정야드  북아웃원인코드 등록(PLATE_MAT)
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updatepPlateBookoutRse_PLATE(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.updatepPlateBookoutRse_PLATE");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : Machine Scarfing 실적 조회
	 */
	public JDTORecordSet getMachineScarfingWr(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.CsScarfingWrDAO.getMachineScarfingWr");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * [A] 오퍼레이션명 : Machine Scarfing 실적 요약 조회
	 */
	public JDTORecordSet getMachineScarfingSummary(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.CsScarfingWrDAO.getMachineScarfingSummary");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
}
