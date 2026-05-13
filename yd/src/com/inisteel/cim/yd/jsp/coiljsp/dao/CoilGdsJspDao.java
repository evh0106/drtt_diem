package com.inisteel.cim.yd.jsp.coiljsp.dao;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
/**
 * 클래스명 : 제품야드 DAO
 * @author : 박지열
 * @작성일 : 2010.04.27
 */
public class CoilGdsJspDao extends DBAssistantDAO {
	
	//ToDo... dao호출시 this를 이용한다.
	private YdPICommDAO	   		ydPICommDAO   = new YdPICommDAO();
	private CCommUtils	   commUtils   = new CCommUtils();		
	
	
	
	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 스케줄기준관리 조회 (화면:스케줄기준관리)
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public JDTORecordSet getSchRuleMgtList(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getSchRuleMgtList";
		//com.inisteel.cim.yd.dao.ydschruledao.YdSchruleDao.getSlabYdSchStd_New
		JDTORecordSet 	outRdSet 		= null;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return outRdSet;
	}
	

	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 설비기준관리 조회 (화면:설비기준관리)
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : hun
	 * @작성일 : 2015.09.7
	 */
	public JDTORecordSet getEqpMgtList(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getCoilYdCrnStsSetByYdgp";
		JDTORecordSet 	outRdSet 		= null;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return outRdSet;
	}
	
	
	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 스케줄기준관리 (수정정보 조회)
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public JDTORecordSet getSchruleRuleInfo(JDTORecord inData) throws DAOException{
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getSchruleRuleInfo";
		//com.inisteel.cim.yd.dao.ydschruledao.YdSchruleDao.getYdSchrule
		JDTORecordSet 	outRdSet 		= null;
		
		try {

			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);

			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);


		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {

		}
		
		return outRdSet;
	}
	
	
	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 스케줄기준관리 (수정)
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public int updSchruleRuleInfo(JDTORecord inData) throws DAOException{
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updSchruleRuleInfo";
		//com.inisteel.cim.yd.dao.ydschruledao.YdSchruleDao.getYdSchrule
		int 			ret 			= 0;
		
		try {

			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);

			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);


		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {

		}
		
		return ret;
	}
	
	
	
	/**
	 * 야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 대차상태설정팝업조회
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public JDTORecordSet getCoilYdTcarStsSet(JDTORecord inData) throws DAOException{
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getCoilYdTcarStsSet";
		//com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getSlabYdCrnStsSetID
		JDTORecordSet 	outRdSet 		= null;
		
		try {

			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);

			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);


		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {

		}
		
		return outRdSet;
	}
	
	
	/**
	 * 야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 대차상태설정팝업 (설비정보 조회)
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.10
	 */
	public JDTORecordSet getEqpInfo(JDTORecord inData) throws DAOException{
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getEqpInfo";
		//com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqp
		JDTORecordSet 	outRdSet 		= null;
		
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		
		return outRdSet;
	}
	
	
	
	/**
	 * 야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 대차이동구간변경 팝업 조회
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.10
	 */
	public JDTORecordSet getTCarYdGpMgt(JDTORecord inData) throws DAOException{
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getTCarYdGpMgt";
		JDTORecordSet 	outRdSet 		= null;
		
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		
		return outRdSet;
	}
	
	
	/** 
	 * 야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 대차이동구간변경 (수정데이터 /스케줄 확인 조회)
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.10
	 */
	public JDTORecordSet getTCarYdGpMgtSch(JDTORecord inData) throws DAOException{
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getTCarYdGpMgtSch";
		JDTORecordSet 	outRdSet 		= null;
		
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		
		return outRdSet;
	}
	
	
	/**
	 * 야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 대차이동구간변경 (수정->BED)
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.22
	 */
	public int updTCarYdGpMgtBed(JDTORecord inData) throws DAOException{
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updTCarYdGpMgtBed";
		int 			ret 			= 0;
		try {
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
		return ret;
	}
	
	
	/**
	 * 야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 대차이동구간변경 (수정->LYR)
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.22
	 */
	public int updTCarYdGpMgtLyr(JDTORecord inData) throws DAOException{
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updTCarYdGpMgtLyr";
		int 			ret 			= 0;
		try {
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {			
		}		
		return ret;
	}
	
	
	/**
	 * 야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 대차이동구간변경 (수정)
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.22
	 */
	public int updTCarYdGpMgt(JDTORecord inData) throws DAOException{
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updTCarYdGpMgt";
		int 			ret 			= 0;		
		try {			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);			
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {			
		}		
		return ret;
	}
	
	
	/**
	 * 이적/이송 대상 폭구분 체크 
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.25
	 */
	public JDTORecordSet getStkColWidthGp(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getStkColWidthGp";
		JDTORecordSet 	outRdSet 		= null;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return outRdSet;
	}
	
	
	/**
	 * 위치검색순서관리 하단 왼쪽 그리드
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public JDTORecordSet getSpanbyLowInfo(JDTORecord inData) throws DAOException {

                                         //com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao.getSpanbyLowInfo
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getSpanbyLowInfo";
		JDTORecordSet 	outRdSet 		= null;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
				// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			

		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {

		}
		return outRdSet;
	}
	
	
	/**
	 * 야드관리 > 코일소재야드 > 기준관리 > 저장위치용도관리  목록조회
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public JDTORecordSet getStrlocUsgSetList(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getStrlocUsgSetList";
		JDTORecordSet 	outRdSet 		= null;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return outRdSet;
	}
	
	/**
	 * 야드관리 > 코일소재야드 > 기준관리 > 저장위치용도관리  등록
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public int updStrlocUsgSet1(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updStrlocUsgSet1";
		int 			ret	 			= 0;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return ret;
	}
	
	
	/**
	 * 야드관리 > 코일소재야드 > 기준관리 > 저장위치용도관리  등록
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public int updStrlocUsgSet2(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updStrlocUsgSet2";
		int 			ret	 			= 0;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return ret;
	}
	
	
	/**
	 * 이적/이송 대상 대차 체크 
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.06
	 */
	public JDTORecordSet getTCarInfo(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getTCarInfo";
		JDTORecordSet 	outRdSet 		= null;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return outRdSet;
	}
	
	
	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 위치검색순서관리   적치구분 콤보리스트 조회
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.06
	 */
	public JDTORecordSet getYDB700ComboList(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getYDB700ComboList";
		JDTORecordSet 	outRdSet 		= null;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return outRdSet;
	}
	
	
	/**
	 * 야드관리 > 코일제품창고 > 저장관리  <스판단위 코일 Display 조회 >
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.06
	 */
	public JDTORecordSet getMtlUnitMvstkReg(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getMtlUnitMvstkReg";
		JDTORecordSet 	outRdSet 		= null;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return outRdSet;
	}

	/** 
	 * 야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리 --> 대차SCH 정보 READ
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.10
	 */
	public JDTORecordSet getTEqpInfo(JDTORecord inData) throws DAOException{
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getTEqpInfo";
		JDTORecordSet 	outRdSet 		= null;
		
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		
		return outRdSet;
	}
	
	/**
	 * 코일제품창고 작업실적일품 조회
	 * @param inData
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilGdsYdWrkRsltDdArtcl(JDTORecord inData) throws DAOException{
		JDTORecordSet 	outRdSet 		= null;
		
		try {
			
			String queryId = "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getCoilGdsYdWrkRsltDdArtcl_PIDEV";
			
//PIDEV
			//PIDEV_S :병행가동용:PI_YD 
//			String sPI_YD     = commUtils.nvl(inData.getFieldString("PI_YD"), "*");	
//			queryId = ydPICommDAO.getYdRulePI("", "CoilGdsJspDao.getCoilGdsYdWrkRsltDdArtcl", "YD0001", queryId, "APPPI0", sPI_YD, "*" );			
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
				
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		
		return outRdSet;
	}
	/**
	 * 차량관리에서 초기화 처리 
	 * @param inData
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCarWrMgt(JDTORecord inData) throws DAOException{
		JDTORecordSet 	outRdSet 		= null;
		
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getCarWrMgt");
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		
		return outRdSet;
	}	

	/**
	 * 차량상태 초기화 처리
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public int delCarWrMgtWorkBookMtl(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.delCarWrMgtWorkBookMtl";
		int 			ret	 			= 0;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return ret;
	}
	/**
	 * 차량상태 초기화 처리
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public int delCarWrMgtWorkBook(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.delCarWrMgtWorkBook";
		int 			ret	 			= 0;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return ret;
	}
		
	/**
	 * 차량재료 저장위치  초기화 처리
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 정종균
	 * @작성일 : 2015.08.10
	 */
	public int delCarSchMtlLayer(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.ym.jsp.coiljsp.dao.CoilGdsJspDao.delCarSchMtlLayer_PIDEV";
		int 			ret	 			= 0;
		try {
			
			
			//PIDEV
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "delCarSchMtlLayer => 차량재료 저장위치  초기화 처리", "APPPI0", "*", "*");			
			
//			if("Y".equals(queryId)) {
//				queryId = queryId + "_PIDEV";
//			}
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return ret;
	}
	
	/**
	 * 차량상태 초기화 처리
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public int delCarWrMgtCarSchMtl(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.delCarWrMgtCarSchMtl";
		int 			ret	 			= 0;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return ret;
	}
	/**
	 * 차량상태 초기화 처리
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public int delCarWrMgtCarSch(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.delCarWrMgtCarSch";
		int 			ret	 			= 0;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return ret;
	}

	/**
	 * 차량상태 초기화 처리
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public int updCarWrMgtStkcol(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updCarWrMgtStkcol";
		int 			ret	 			= 0;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return ret;
	}
		
	
	/**
	 * 차량상태 초기화 처리
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public int updCarWrMgtPrepSchMtl(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updCarWrMgtPrepSchMtl";
		int 			ret	 			= 0;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return ret;
	}
	/**
	 * 차량상태 초기화 처리
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public int updCarWrMgtPrepSch(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updCarWrMgtPrepSch";
		int 			ret	 			= 0;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return ret;
	}
	
	
	/**
	 * 코일 번호로 저장위치 조회 하기 
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilTolyr(JDTORecord inData) throws DAOException{
		JDTORecordSet 	outRdSet 		= null;
		
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getCoilTolyr");
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 대차 권상가능여부 CHECK
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getStkColTCarUpChk(JDTORecord inData) throws DAOException{
		JDTORecordSet 	outRdSet 		= null;
		
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getStkColTCarUpChk");
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	/**
	 * 대차 권상가능여부 CHECK
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getStkColTCarUpChk2(JDTORecord inData) throws DAOException{
		JDTORecordSet 	outRdSet 		= null;
		
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getStkColTCarUpChk2");
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	
	/**
	 * 대차 권상가능여부 CHECK
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getStkColTCarUpChk3(JDTORecord inData) throws DAOException{
		JDTORecordSet 	outRdSet 		= null;
		
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getStkColTCarUpChk3");
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	/**
	 * 차량출발등록(PDA) 목록 조회
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.28 
	 */
	public JDTORecordSet getCarStartMgtList(JDTORecord inData) throws DAOException{
		JDTORecordSet 	outRdSet 		= null;
		
		try {
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getCarStartMgtList");
			outRdSet = this.getRecordSet(inData);
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 동별 야드포인트코드(PDA) 조회(selectBox 용)
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.28 
	 */
	public JDTORecordSet getYdPointCdList(JDTORecord inData) throws DAOException{
		JDTORecordSet 	outRdSet 		= null;
		
		try {
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getYdPointCdList");
			outRdSet = this.getRecordSet(inData);
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 차량도착등록(PDA) 목록 조회 
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.28 
	 */
	public JDTORecordSet getCarArrivalMgtList(JDTORecord inData) throws DAOException{
		JDTORecordSet 	outRdSet 		= null;
		
		try {
			
			String queryId = "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getCarArrivalMgtList_PIDEV";
			
//PIDEV
			//PIDEV_S :병행가동용:PI_YD 
//			String sPI_YD     = commUtils.nvl(inData.getFieldString("PI_YD"), "*");	
//			queryId = ydPICommDAO.getYdRulePI("", "CoilGdsJspDao.getCarArrivalMgtList", "YD0001", queryId, "APPPI0", sPI_YD, "*" );			
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			outRdSet = this.getRecordSet(inData);
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 반송등록
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public int updCoilYdRetMgt(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updCoilYdRetMgt";
		int 			ret	 			= 0;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return ret;
	}
	/**
	 * 반송기타 수정
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public int updCoilYdRetMgtUpdate(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updCoilYdRetMgtUpdate";
		int 			ret	 			= 0;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return ret;
	}
	
	
	/**
	 * 반송확정
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public int updCoilYdRetMgt1(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updCoilYdRetMgt1";
		int 			ret	 			= 0;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return ret;
	}
	
	/**
	 * 반송정보 조회 
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.28 
	 */
	public JDTORecordSet getCoilYdRetMgt(JDTORecord inData) throws DAOException{
		JDTORecordSet 	outRdSet 		= null;
		
		try {
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getCoilYdRetMgt");
			outRdSet = this.getRecordSet(inData);
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * SCH LOG 등록
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public int insSchLog(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.insSchLog";
		int 			ret	 			= 0;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return ret;
	}
	
	/**
	 *  동별 SCHDULE 정보
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.28 
	 */
	public JDTORecordSet getSchRuleList(JDTORecord inData) throws DAOException{
		JDTORecordSet 	outRdSet 		= null;
		
		try {
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getSchRuleList");
			outRdSet = this.getRecordSet(inData);
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}

	/**
	 * YM STOCK INSERT
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public int insYmStockCoil(JDTORecord inData) throws DAOException{
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.insYmStockCoil";
		//com.inisteel.cim.yd.dao.ydschruledao.YdSchruleDao.getYdSchrule
		int 			ret 			= 0;
		
		try {

			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);

			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);


		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {

		}
		
		return ret;
	}
 
	
	/**
	 * YM STOCK update
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public int updYmStockCoil(JDTORecord inData) throws DAOException{
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updYmStockCoil";
		//com.inisteel.cim.yd.dao.ydschruledao.YdSchruleDao.getYdSchrule
		int 			ret 			= 0;
		
		try {

			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);

			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);


		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {

		}
		
		return ret;
	}
	
	/**
	 * 야드관리 > 코일소재야드 > 기준관리 > 적치위치변경
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public JDTORecordSet getStrlocChgSetList(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getStrlocChgSetList";
		JDTORecordSet 	outRdSet 		= null;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return outRdSet;
	}
	
	/**
	 * 야드관리 > 코일야드 > 기준관리 > 적치된 코일 read
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public JDTORecordSet getStrlocChgSet(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getStrlocChgSet";
		JDTORecordSet 	outRdSet 		= null;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return outRdSet;
	}

	/**
	 * 적치된 코일 COL update
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public int updStrlocChgColSet(JDTORecord inData) throws DAOException{
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updStrlocChgColSet";
		int 			ret 			= 0;
		
		try {
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {

		}
		return ret;
	}
	/**
	 * 적치된 코일 BED update
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public int updStrlocChgBedSet(JDTORecord inData) throws DAOException{
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updStrlocChgBedSet";
		int 			ret 			= 0;
		
		try {
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {

		}
		return ret;
	}
	/**
	 * 적치된 코일 LYR update
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public int updStrlocChgLyrSet(JDTORecord inData) throws DAOException{
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updStrlocChgLyrSet";
		int 			ret 			= 0;
		
		try {
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {

		}
		return ret;
	}
	
	/**
	 * 야드관리 > 코일야드 > 기준관리 > bre read
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public JDTORecordSet getYdEqpTcBreYDB011(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getYdEqpTcBreYDB011";
		JDTORecordSet 	outRdSet 		= null;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return outRdSet;
	}
	/**
	 * 크레인 출하작업 현황
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.28 
	 */
	public JDTORecordSet getCoilCraneCarWrkPDA(JDTORecord inData) throws DAOException{
		JDTORecordSet 	outRdSet 		= null;
		
		try {
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getCoilCraneCarWrkPDA");
			outRdSet = this.getRecordSet(inData);
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	/**
	 * 공장별 크레인 정보
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.28 
	 */
	public JDTORecordSet getCrnGp(JDTORecord inData) throws DAOException{
		JDTORecordSet 	outRdSet 		= null;
		
		try {
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getCrnGpBreYDB703");
			outRdSet = this.getRecordSet(inData);
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}	
	/**
	 * C연주야드현황조회
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public JDTORecordSet getCSlabYdMgtList(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getCSlabYdMgtList";
		JDTORecordSet 	outRdSet 		= null;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return outRdSet;
	}
	
	/**
	 * B열연야드현황조회
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public JDTORecordSet getBSlabYdMgtList(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getBSlabYdMgtList";
		JDTORecordSet 	outRdSet 		= null;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return outRdSet;
	}
	
	/**
	 * 후판야드현황조회
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public JDTORecordSet getPSlabYdMgtList(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getPSlabYdMgtList";
		JDTORecordSet 	outRdSet 		= null;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			outRdSet = this.getRecordSet(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return outRdSet;
	}
	
	
	/**
	 *  반납대상 긴급재 지정
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 정종균
	 * @작성일 : 2011.08.05
	 */
	public int updCoilYdemergencyMgt(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updCoilYdemergencyMgt";
		int 			ret	 			= 0;
		try {
			
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);
			
			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
			
		}
		return ret;
	}
	/**
	 * YD STOCK INSERT
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public int insYdStock(JDTORecord inData) throws DAOException{
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.insYdStock";
		int 			ret 			= 0;		
		try {
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);

			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);

		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}		
		return ret;
	}
	
	
	/**
	 * YM STOCK INSERT
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public int insYmStock(JDTORecord inData) throws DAOException{
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.insYmStock";
		int 			ret 			= 0;		
		try {
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);

			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);

		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}		
		return ret;
	}
	
	
	/**
	 * 야드관리 > 통합슬라브야드 > Monitoring > 슬라브이송지연사유등록 (수정)
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public int updSlabTotYdToMoveInfo(JDTORecord inData) throws DAOException{
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updSlabTotYdToMoveInfo";
		int 			ret 			= 0;
		
		try {

			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);

			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);


		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {

		}
		
		return ret;
	}
	
	
	/**
	 * TB_HR_C_SHEARWOWR_MSG_LOG INSERT
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public int insHrShrMsgLog(JDTORecord inData) throws DAOException{
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.insHrShrMsgLog";
 		int 			ret 			= 0;		
		try {
			//  파라미터 설정 
			inData.setField("JSPEED_QUERY_ID", queryId);

			// 검색문을 실행합니다.
			ret = this.trtProcess(inData);


		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {

		}
		
		return ret;
	}
	
} // end of class






