package com.inisteel.cim.yd.jsp.coiljsp.dao;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;

import xlib.cmc.GridData;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.hr.common.util.CmnUtil;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
/**
 * 테스트 관련 DAO 클래스 입니다.
 *
 * @author 김창일
 */
public class CoilJspDAO extends DBAssistantDAO{

	private YdPICommDAO	   		ydPICommDAO   = new YdPICommDAO();
	private CCommUtils	   commUtils   = new CCommUtils();	

	/**
	 * 저장품 Master Data (검색어, 업무영역코드)
	 *
	 * @param inDto
	 * @return
	 * @throws Exception
	 */
	public JDTORecordSet getYdStock(JDTORecord inDto) throws DAOException {
		JDTORecordSet outRecordSet = null;
		try {
			// 실행할 queryID를 JDTORecord에 할당하여 DAO를 호출한다.
			
			inDto.setField("JSPEED_QUERY_ID",
					"com.inisteel.cim.yd.coiljsp.ydStock");

			// 검색문을 실행합니다.
			outRecordSet = this.getRecordSet(inDto);

		} catch (Exception e) {
			e.getStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {

		}
		return outRecordSet;
	}	
	
	
	/**
	 * coil Comm 데이터 검색 (검색어, 업무영역코드)
	 *
	 * @param inDto
	 * @return
	 * @throws Exception
	 */
	public JDTORecordSet getPtCoilComm(JDTORecord inDto) throws DAOException {
		JDTORecordSet outRecordSet = null;
		try {
			// 실행할 queryID를 JDTORecord에 할당하여 DAO를 호출한다.
			
			inDto.setField("JSPEED_QUERY_ID",
					"com.inisteel.cim.yd.coiljsp.ptCoilComm");

			// 검색문을 실행합니다.
			outRecordSet = this.getRecordSet(inDto);

		} catch (Exception e) {
			e.getStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {

		}
		return outRecordSet;
	}
	
	
	
	/**
	 * 작업지시이력 (검색어, 업무영역코드)
	 *
	 * @param inDto
	 * @return
	 * @throws Exception
	 */
	public JDTORecordSet getYdMtlWoInfo(JDTORecord inDto) throws DAOException {
		JDTORecordSet outRecordSet = null;
		try {
			// 실행할 queryID를 JDTORecord에 할당하여 DAO를 호출한다.
			
			inDto.setField("JSPEED_QUERY_ID",
					"com.inisteel.cim.yd.coiljsp.ydMtlWoInfo");

			// 검색문을 실행합니다.
			outRecordSet = this.getRecordSet(inDto);

		} catch (Exception e) {
			e.getStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {

		}
		return outRecordSet;
	}	
	
	/**
	 * 설비사양설정을 조회한다.()
	 *
	 * @param inDto
	 * @return
	 * @throws Exception
	 */
	public JDTORecordSet getCoilYdEqpSetSpec(JDTORecord inDto) throws DAOException {
		JDTORecordSet outRecordSet = null;
		try {
			// 실행할 queryID를 JDTORecord에 할당하여 DAO를 호출한다.
			
			inDto.setField("JSPEED_QUERY_ID",
					"com.inisteel.cim.yd.coiljsp.ydMtlStatModHist");

			// 검색문을 실행합니다.
			outRecordSet = this.getRecordSet(inDto);

		} catch (Exception e) {
			e.getStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {

		}
		return outRecordSet;
	}	
	
	/**
	 * [A] 오퍼레이션명 : 열연Coil상세조회
	 * 
	 * @param GridData gdReq
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.04.09
	*/
	public JDTORecordSet getHrCoilDtlInq(GridData gdReq) throws DAOException {
		String jspeed_query_id = "com.inisteel.cim.yd.coiljsp.getHrCoilDtlInq_PIDEV";

		try {
			Object[] objs = new Object[]{					
					 CmnUtil.nvl(gdReq.getParam("V_COIL_NO"), "") //Coil번호
					,CmnUtil.nvl(gdReq.getParam("V_COIL_NO"), "") //Coil번호
					,CmnUtil.nvl(gdReq.getParam("V_COIL_NO"), "") //Coil번호
					,CmnUtil.nvl(gdReq.getParam("V_COIL_NO"), "") //Coil번호
				};

        	return this.getRecordSet(jspeed_query_id, objs);
		} catch(Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	/****************************************************************************************************/
	// 신규 추가 함수
	/****************************************************************************************************/
	
	/**
	 * 야드관리 > 코일소재야드 > 크레인실적관리 > 스판단위이적등록 (이적가능 Count, 예약 Count조회) 
	 * @작성자 :박지열
	 * @작성일 : 2010.04.22
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getToDongUseCount(JDTORecord inData) throws DAOException {
		String query_id = "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.getToDongUseCount";
		
		JDTORecordSet retRdSet = null;
		try {
			
			inData.setField("JSPEED_QUERY_ID", query_id);
			
			retRdSet = this.getRecordSet(inData);
			
		} catch(Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return retRdSet;
	}
		
	
	/**
	 * 위치검색SPAN관리하단 왼쪽 그리드
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.04.12
	 */
	public JDTORecordSet getSpanbyLowInfo(JDTORecord inData) throws DAOException {

                                         //com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao.getSpanbyLowInfo
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getSpanbyLowInfo";
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
	 * @작성일 : 2010.04.12
	 */
	public JDTORecordSet getStrlocUsgSetList(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getStrlocUsgSetList";
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
	 * @작성일 : 2010.04.12
	 */
	public int updStrlocUsgSet1(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.updStrlocUsgSet1";
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
	 * @작성일 : 2010.04.12
	 */
	public int updStrlocUsgSet2(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.updStrlocUsgSet2";
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
	 * 야드관리 > 코일소재야드 > 제공관리 > 저장위치관리 조회 (현재는 테스트에 들어 잇음 2010.05.11)
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.11
	 */
	public JDTORecordSet getCoilYdStrlocModMgt(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getCoilYdStrlocModMgt";
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
	
	
	
	//===================================================================================================
	// 저장위치 변경관리 Start
	//===================================================================================================
	/**
	 * 저장위치변경관리 (수정위치의 재료번호 등록 여부를 조회 )
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.11
	 */
	public JDTORecordSet getStrlocModMgtChk(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getStrlocModMgtChk";
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
	 * 저장위치변경관리 (수정할 재료번호 최신정보 조회 )
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.11
	 */
	public JDTORecordSet getStrlocModMgtStlInfo(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getStrlocModMgtStlInfo";
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
	 * 저장위치변경관리 (현재 레이어 위치, 변경될 레이어 위치 수정   )
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.12
	 */
	public int updStrlocModMgtFromAndTo(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.updStrlocModMgtFromAndTo";
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
	 * 저장위치변경관리 (저장품 테이블 변경 )
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.12
	 */
	public int updStrlocModMgtStock(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.updStrlocModMgtStock";
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
	 * 저장위치변경관리 (코일공통 테이블 변경 )
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.12
	 */
	public int updStrlocModMgtColComm(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.updStrlocModMgtColComm";
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
	
	//===================================================================================================
	// 저장위치 변경관리 End
	//===================================================================================================
	
	
	/**
	 * InterLock 스케쥴 조회
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.19
	 */
	public JDTORecordSet getInterLockList(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getInterLockList";
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
	 * InterLock 스케쥴 조회
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.19
	 */
	public JDTORecordSet getInterLockList1(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getInterLockList1";
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
	 * 야드관리 > 코일소재야드 > 설비/차량관리 > 야드보급순서  목록조회
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.03
	 */
	public JDTORecordSet getSupplyInOrderList(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getSupplyInOrderList";
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
	 * 야드관리 > 코일소재야드 > 설비/차량관리 > 야드보급순서  목록조회
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.03
	 */
	public JDTORecordSet getSupplyInOrderListNEW(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getSupplyInOrderListNEW";
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
	 * 이적/이송 대상 폭구분 체크 
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.03
	 */
	public JDTORecordSet getStkColWidthGp(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDao.getStkColWidthGp";
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
	 * 이적/이송 대상 대차 체크 
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.06
	 */
	public JDTORecordSet getTCarInfo(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDao.getTCarInfo";
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
	 * 야드관리 > 코일소재야드 > 설비/차량관리 > 이송대상재관리   (이송공장별 이송량 조회)
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.20
	 */
	public JDTORecordSet getCoilYdFrtMoveGpList(JDTORecord inData) throws DAOException {
		JDTORecordSet 	outRdSet 		= null;
		try {
			inData.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDao.getCoilYdFrtMoveGpList");
			outRdSet = this.getRecordSet(inData);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		return outRdSet;
	}
	
	/**
	 * 야드관리 > 코일소재야드 > 설비/차량관리 > 이송대상재관리   (이송대상목록 조회)
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.20
	 */
	public JDTORecordSet getCoilYdMvMtlList(JDTORecord inData) throws DAOException {
		JDTORecordSet 	outRdSet 		= null;
		try {
			inData.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDao.getCoilYdMvMtlList");
			outRdSet = this.getRecordSet(inData);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		return outRdSet;
	}
	
	
	/**
	 * 코일번호로 스케줄 조회 
	 * @param inData
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdSchAtCoilNo(JDTORecord inData) throws DAOException {
		JDTORecordSet 	outRdSet 		= null;
		try {
			inData.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDao.getCoilYdSchAtCoilNo");
			outRdSet = this.getRecordSet(inData);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		return outRdSet;
	}
	
	/**
	 * 차량상차완료 처리
	 * @param inData
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getcoilYdCarUpEndPp(JDTORecord inData) throws DAOException {
		JDTORecordSet 	outRdSet 		= null;
		try {
			inData.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDao.getcoilYdCarUpEndPp");
			outRdSet = this.getRecordSet(inData);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		return outRdSet;
	}
	
	/**
	 * 차량상차완료 처리 조회
	 *
	 * @param inDto
	 * @return
	 * @throws Exception
	 */
	public JDTORecordSet getYdStockYdCoilCommLyr(JDTORecord inDto) throws DAOException {
		JDTORecordSet outRecordSet = null;
		try {
			// 실행할 queryID를 JDTORecord에 할당하여 DAO를 호출한다.
			
			inDto.setField("JSPEED_QUERY_ID",
					"com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.getYdStockYdCoilCommLyr");

			// 검색문을 실행합니다.
			outRecordSet = this.getRecordSet(inDto);

		} catch (Exception e) {
			e.getStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {

		}
		return outRecordSet;
	}	
	
	/**
	 * 야드관리 > C열연 코일소재야드 > 야드현황관리 > 재료진도별재공현황  (저장물품목록)
	 *
	 * @param inDto
	 * @return
	 * @throws Exception
	 */
	public JDTORecordSet getMtlProgStlList(JDTORecord inDto) throws DAOException {
		JDTORecordSet outRecordSet = null;
		try {

			String queryId = "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.getMtlProgStlList_PIDEV";
//PIDEV
			//PIDEV_S :병행가동용:PI_YD 
//			String sPI_YD     = commUtils.nvl(inDto.getFieldString("PI_YD"), "*");				
//			queryId = ydPICommDAO.getYdRulePI("", "CoilGdsJspDao.getCoilGdsYdWrkRsltDdArtcl", "YD0001", queryId, "APPPI0",sPI_YD, "*" );
			
			inDto.setField("JSPEED_QUERY_ID", queryId);			
			
			// 검색문을 실행합니다.
			outRecordSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.getStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} 
		return outRecordSet;
	}	
	
	/**
	 * 야드관리 > C열연 코일소재야드 > 산적LOT관리 > 반입현황조회  (A,B열연 반입카운트 조회 )
	 *
	 * @param inDto
	 * @return
	 * @throws Exception
	 */
	public JDTORecordSet getCarryBayCnt(JDTORecord inDto) throws DAOException {
		JDTORecordSet outRecordSet = null;
		try {
			inDto.setField("JSPEED_QUERY_ID",
			"com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.getCarryBayCnt");
			
			// 검색문을 실행합니다.
			outRecordSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.getStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} 
		return outRecordSet;
	}	
	
	/**
	 * 야드관리 > C열연 코일소재야드 > 산적LOT관리 > 반입현황조회  (대상재목록 조회 )
	 *
	 * @param inDto
	 * @return
	 * @throws Exception
	 */
	public JDTORecordSet getCarryList(JDTORecord inDto) throws DAOException {
		JDTORecordSet outRecordSet = null;
		try {
			inDto.setField("JSPEED_QUERY_ID",
			"com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.getCarryList");
			
			// 검색문을 실행합니다.
			outRecordSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.getStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} 
		return outRecordSet;
	}	
	
	/**
	 * 야드관리 > C열연 코일소재야드 > 이상재 현황A
	 *
	 * @param inDto
	 * @return
	 * @throws Exception
	 */
	public JDTORecordSet getMtlErrorListA(JDTORecord inDto) throws DAOException {
		JDTORecordSet outRecordSet = null;
		try {
			inDto.setField("JSPEED_QUERY_ID",
			"com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.getMtlErrorListA");
			
			// 검색문을 실행합니다.
			outRecordSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.getStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} 
		return outRecordSet;
	}	
	
	/**
	 * 야드관리 > C열연 코일소재야드 > 이상재 현황B
	 *
	 * @param inDto
	 * @return
	 * @throws Exception
	 */
	public JDTORecordSet getMtlErrorListB(JDTORecord inDto) throws DAOException {
		JDTORecordSet outRecordSet = null;
		try {
			inDto.setField("JSPEED_QUERY_ID",
			"com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.getMtlErrorListB");
			
			// 검색문을 실행합니다.
			outRecordSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.getStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} 
		return outRecordSet;
	}	
	
	/**
	 * 야드관리 > C열연 코일소재야드 > 이상재 현황C
	 *
	 * @param inDto
	 * @return
	 * @throws Exception
	 */
	public JDTORecordSet getMtlErrorListC(JDTORecord inDto) throws DAOException {
		JDTORecordSet outRecordSet = null;
		try {
			inDto.setField("JSPEED_QUERY_ID",
			"com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.getMtlErrorListC");
			
			// 검색문을 실행합니다.
			outRecordSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.getStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} 
		return outRecordSet;
	}	
	/**
	 * 야드관리 > C열연 코일소재야드 > 이상재 현황D
	 *
	 * @param inDto
	 * @return
	 * @throws Exception
	 */
	public JDTORecordSet getMtlErrorListD(JDTORecord inDto) throws DAOException {
		JDTORecordSet outRecordSet = null;
		try {
			inDto.setField("JSPEED_QUERY_ID",
			"com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.getMtlErrorListD");
			
			// 검색문을 실행합니다.
			outRecordSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.getStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} 
		return outRecordSet;
	}	
	/**
	 * 야드관리 > C열연 코일소재야드 > 이상재 현황E
	 *
	 * @param inDto
	 * @return
	 * @throws Exception
	 */
	public JDTORecordSet getMtlErrorListE(JDTORecord inDto) throws DAOException {
		JDTORecordSet outRecordSet = null;
		try {
			inDto.setField("JSPEED_QUERY_ID",
			"com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.getMtlErrorListE");
			
			// 검색문을 실행합니다.
			outRecordSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.getStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} 
		return outRecordSet;
	}	
	
	/**
	 * 야드관리 > 코일소재야드 > 설비/차량관리 > 야드보급순서  목록조회(신)
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.03
	 */
	public JDTORecordSet getTrackingList(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getTrackingList";
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
	
//////////////////////////////////////////////////////////////////////	
	/**
	 * 야드관리 > 조업보급 순서 
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.03
	 */
	public JDTORecordSet getLineSupplyOrderNEW(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getLineSupplyOrderNEW";
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
	 * 야드관리 > 조업보급 순서 
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.03
	 */
	public JDTORecordSet getLineSupplyOrderNEWStlNo(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getLineSupplyOrderNEWStlNo";
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
	 * 야드관리 > 조업보급 순서2 
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.03
	 */
	public JDTORecordSet getLineSupplyOrder(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getLineSupplyOrder";
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
	 * 야드관리 > 조업보급 순서 
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.03
	 */
	public JDTORecordSet getHFLLineSupplyOrderNEW(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getHFLLineSupplyOrderNEW";
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
	 * 야드관리 > 조업보급 순서 
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.03
	 */
	public JDTORecordSet getHFLLineSupplyOrderNEWStlNo(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getHFLLineSupplyOrderNEWStlNo";
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
	 * 야드관리 > 조업보급 순서2 
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.03
	 */
	public JDTORecordSet getHFLLineSupplyOrder(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getHFLLineSupplyOrder";
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
	 * 야드관리 > 코일소재야드 > 크레인실적관리 > 스판단위이적등록 (작업가능 대차)
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.03
	 */
	public JDTORecordSet getToDongTcarUse(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getToDongTcarUse";
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
	 * 결로ON  버튼 클릭시 실행
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.03
	 */
	public int updConOffResultYN(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.updConOffResultYN";
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
	 * 야드관리 > 코일소재야드 >  
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.03
	 */
	public JDTORecordSet getHrShrWoUnitCmtUnit(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getHrShrWoUnitCmtUnit";
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
	 * 야드관리 > 코일소재야드 > B열연야드결로위치지정
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 신지은
	 * @작성일 : 2017.05.31
	 */
	public int updCondensationAsgnInfo(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.updCondensationAsgnInfo";
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
	 * 야드관리 > 코일소재야드 > B열연야드결로위치해제(상태변경)
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 신지은
	 * @작성일 : 2017.05.31
	 */
	public int updCondensationRelInfo(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.updCondensationRelInfo";
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
	 * 야드관리 > 코일소재야드 > B열연야드결로위치해제(신규정보등록)
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 신지은
	 * @작성일 : 2017.05.31
	 */
	public int insCondensationInfo(JDTORecord inData) throws DAOException {
		String 			queryId 		= "com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.insNewCondensationInfo";
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
	
}
