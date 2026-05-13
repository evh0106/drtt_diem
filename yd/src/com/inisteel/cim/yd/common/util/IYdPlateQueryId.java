/**
 * @(#)IYdPlateQueryId.java
 * 
 * @version			1.0
 * @author 			현대제철
 * @date			2012/11/14
 * 
 * @description		후판제품야드에서 사용되는 Query id를 정의하는 인터페이스 이다.
 * ------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/24   조병기      조병기      최초 등록
 * V1.01  2012/11/28   조병기      조병기      추가등록
 */

package com.inisteel.cim.yd.common.util;

public interface IYdPlateQueryId {
	
	//야드MAP관리 조회
	public static final String SELECT_QUERY_ID_0001	= "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0001"; 
	//저장위치별 재고 List 조회
	public static final String SELECT_QUERY_ID_0002	= "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0002_PIDEV"; 
	//1차저장계획등록 조회
	public static final String SELECT_QUERY_ID_0003	= "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0003_PIDEV"; 
	//일품별 재고조회
	public static final String SELECT_QUERY_ID_0004	= "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0004_PIDEV"; 
	//작업실적 일품조회
	public static final String SELECT_QUERY_ID_0005	= "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0005_PIDEV"; 
	//작업실적 조회
	public static final String SELECT_QUERY_ID_0006	= "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0006"; 
	//SIZE별 재고분석
	public static final String SELECT_QUERY_ID_0007	= "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0007"; 
	//파일링정보조회(파일링코드별 재고LIST)
	public static final String SELECT_QUERY_ID_0008	= "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0008_PIDEV";
	//저장위치과부족현황 - 수량
	public static final String SELECT_QUERY_ID_0009	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0009"; 
	//저장위치과부족현황 - 중량
	public static final String SELECT_QUERY_ID_0010	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0010"; 
	//RT 모니터링 - 조회
	public static final String SELECT_QUERY_ID_0011	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0011"; 
	//수주별 재고조회 - 조회,엑셀
	public static final String SELECT_QUERY_ID_0012	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0012_PIDEV"; 
	//주문재고LIST(popup) -조회
	public static final String SELECT_QUERY_ID_0013	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0013"; 
	//주문별 재고조회 - 조회,엑셀
	public static final String SELECT_QUERY_ID_0014	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0014"; 
	//저장위치별정보조회 - 조회,엑셀
	public static final String SELECT_QUERY_ID_0015	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0015_PIDEV"; 
	//권역별재고List 상세 Popup - 조회
	public static final String SELECT_QUERY_ID_0016	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0016_PIDEV"; 
	//권역별재고List - 길이
	public static final String SELECT_QUERY_ID_0017	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0017_PIDEV"; 
	//권역별재고List - 길이(상세)
	public static final String SELECT_QUERY_ID_0018	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0018_PIDEV"; 
	//권역별재고List - 폭
	public static final String SELECT_QUERY_ID_0019	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0019_PIDEV"; 
	//권역별재고List - 폭(상세)
	public static final String SELECT_QUERY_ID_0020	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0020_PIDEV"; 
	//저장위치 수정 - 조회
	public static final String SELECT_QUERY_ID_0021	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0021"; 
	//RT 모니터링 - 상세조회
	public static final String SELECT_QUERY_ID_0022	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0022_PIDEV"; 
	//이송재료 LIST - 조회(지시)
	public static final String SELECT_QUERY_ID_0023	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0023"; 
	//이송재료 LIST - 조회(완료)
	public static final String SELECT_QUERY_ID_0024	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0024"; 
	//반납 LIST - 조회(반납)
	public static final String SELECT_QUERY_ID_0025	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0025_PIDEV"; 
	//반납 LIST - 조회(반송)
	public static final String SELECT_QUERY_ID_0026	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0026_PIDEV"; 
	//반납 LIST - 조회(반납+반송)
	public static final String SELECT_QUERY_ID_0027	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0027_PIDEV"; 
	//선별대상LIST조회 - 조회
	public static final String SELECT_QUERY_ID_0028	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0028_PIDEV"; 
	//저장속성별 재고현황 - 조회
	public static final String SELECT_QUERY_ID_0029	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0029_PIDEV"; 
	//저장위치 좌표설정 - 조회
	public static final String SELECT_QUERY_ID_0030	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0030"; 
	//저장위치 좌표설정 - BED조회
	public static final String SELECT_QUERY_ID_0031	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0031"; 
	
	
	//야드MAP관리 수정 - 적치열변경
	public static final String UPDATE_QUERY_ID_0001	= "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0001";
	//야드MAP관리 수정 - 적치Bed변경
	public static final String UPDATE_QUERY_ID_0002	= "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updateQueryId_0002"; 
	//이송재료 LIST - 수정 (목표행선/야드/동 변경)
	public static final String UPDATE_QUERY_ID_0003	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0003"; 
	//반납 LIST - 수정 (PLNT_PROC_CD:공장공정코드 변경)
	public static final String UPDATE_QUERY_ID_0004	= "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0004"; 

}
