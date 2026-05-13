/**
 * @(#)YsConstant.java
 * 
 * @version			1.0
 * @author 			현대제철
 * @date			2015.
 * 
 * @description		특수강 야드에서 사용되는 공통 상수를 정의하는 클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.02  2015.01.19   송정현      송정현      특수강용 YsConstant 
 */

package com.inisteel.cim.ys.common.util;

public class YsConstant {
	
	//-------------------------------------------------------------------------------------------------
	// 	TC_CODE 상수 정의 시작
	//-------------------------------------------------------------------------------------------------
	/** 연주조업	 */
	public final static String   SEYSJ001 = "SEYSJ001";    //BLOOM야드 전단실적 
	public final static String   SEYSJ002 = "SEYSJ002";    //BLOOM야드 2차전단분할실적
 
	/** 압연조업	 */
	public final static String   SBYSJ001 = "SBYSJ001";    //BILLET야드 전단실적 
	public final static String   SBYSJ002 = "SBYSJ002";    //봉강제품야드 Banding 실적
	public final static String   SBYSJ003 = "SBYSJ003";    //선재제품야드 평량실적 실적
	public final static String   SBYSJ004 = "SBYSJ004";    //봉강제품야드 봉강정정지시
	public final static String   SBYSJ005 = "SBYSJ005";    //봉강제품야드 각광번들실적

	/** 생산통제	 */
	public final static String   CUYSJ001 = "CUYSJ001";    //BLOOM야드 압연지시확정
	public final static String   CUYSJ002 = "CUYSJ002";    //BILLET야드 압연지시확정
	public final static String   YSCUJ031 = "YSCUJ031";    //BLOOM야드 장입진행
	public final static String   YSCUJ032 = "YSCUJ032";    //BILLET야드 장입진행정보

	/** 공정계획	 */
	public final static String   PAYSJ001 = "PAYSJ001";    //BLOOM야드 충당실적
	public final static String   PAYSJ002 = "PAYSJ002";    //BLOOM야드 이송지시
	public final static String   PAYSJ003 = "PAYSJ003";    //BILLET야드 충당실적
	public final static String   PAYSJ004 = "PAYSJ004";    //BILLET야드 이송지시

	/** 구내운송	 */
	public final static String   TSYSJ002 = "TSYSJ002";    //소재차량도착Point요구
	public final static String   TSYSJ003 = "TSYSJ003";    //소재차량도착
	public final static String   TSYSJ004 = "TSYSJ004";    //소재차량출발
	public final static String   YSTSJ007 = "YSTSJ007";    //소재차량상차개시
	public final static String   YSTSJ008 = "YSTSJ008";    //소재차량상차완료
	public final static String   YSTSJ009 = "YSTSJ009";    //소재차량하차개시
	public final static String   YSTSJ010 = "YSTSJ010";    //소재차량하차완료
	public final static String   YSTSJ011 = "YSTSJ011";    //소재차량Point지시
	public final static String   YSTSJ012 = "YSTSJ012";    //소재차량Point개폐
	
	/** 출하관리	 */
	public final static String   DSYSJ001 = "DSYSJ001";    //특수강제품야드 특수강제품출하지시대기
	public final static String   DSYSJ002 = "DSYSJ002";    //특수강제품야드 특수강제품운송지시대기
	public final static String   DSYSJ003 = "DSYSJ003";    //특수강제품야드 특수강제품선별Lot편성정보
	public final static String   DSYSJ004 = "DSYSJ004";    //특수강제품야드 특수강제품운송상차지시
	public final static String   DSYSJ005 = "DSYSJ005";    //특수강제품야드 특수강대기장도착실적
	public final static String   DSYSJ006 = "DSYSJ006";    //특수강제품야드 특수강제품출하완료
	public final static String   DSYSJ007 = "DSYSJ007";    //특수강제품야드 특수강제품반납대기
	public final static String   DSYSJ008 = "DSYSJ008";    //특수강제품야드 특수강제품목전
	public final static String   DSYSJ009 = "DSYSJ009";    //특수강제품야드 특수강제품보관지시
	public final static String   DSYSJ010 = "DSYSJ010";    //특수강제품야드 특수강제품긴급/보류재처리실적
	public final static String   DSYSJ011 = "DSYSJ011";    //반품대상재 차량정보 수신
	public final static String   DSYSJ012 = "DSYSJ012";    //특수강제품야드 2냉연야드상차완료정보
	
	public final static String   YSDSJ001 = "YSDSJ001";    //특수강입고작업실적
	public final static String   YSDSJ002 = "YSDSJ002";    //특수강제품이적작업실적
	public final static String   YSDSJ003 = "YSDSJ003";    //특수강제품선별작업실적
	public final static String   YSDSJ004 = "YSDSJ004";    //특수강제품선별일괄작업실적
	public final static String   YSDSJ005 = "YSDSJ005";    //특수강차량입동지시
	public final static String   YSDSJ006 = "YSDSJ006";    //특수강제품출하상차개시
	public final static String   YSDSJ007 = "YSDSJ007";    //특수강일품출하상차실적
	public final static String   YSDSJ008 = "YSDSJ008";    //특수강출하상차완료
	public final static String   YSDSJ009 = "YSDSJ009";    //특수강제품검수완료실적
	public final static String   YSDSJ010 = "YSDSJ010";    //특수강야드핸들링정보수신
	public final static String   YSDSJ011 = "YSDSJ011";    //특수강제품반납확인정보
	
	/** 진행관리 */
	public final static String   YSPBJ001 = "YSPBJ001";    //BLOOM야드에의한진도변경
	public final static String   YSPBJ002 = "YSPBJ002";    //BILLET야드에의한진도변경
	
	public final static String   PBYSJ001 = "PBYSJ001";    //포항이송재 이송지시
	
/** 조업	 */
	/** 연주L2(M2)	 */
	public final static String   M2YSL101 = "M2YSL101";    //BLOOM야드 Carry-Out요구
	public final static String   YSM2L101 = "YSM2L101";    //BLOOM야드 Carry-Out완료

	/** 대형압연L2(M3)	 */
	public final static String   M3YSL101 = "M3YSL101";    //BLOOM야드 장입Carry-In요구
	public final static String   M3YSL102 = "M3YSL102";    //BLOOM야드 장입이상재 Carry-out요구
	public final static String   YSM3L101 = "YSM3L101";    //BLOOM야드 장입Carry-In완료
	public final static String   YSM3L102 = "YSM3L102";    //BLOOM야드 장입이상재 Carry-out완료

	/** 빌렛정정L2(M4)	 */
	public final static String   M4YSL001 = "M4YSL001";    //BILLET야드 목표동(라우팅) 요청정보
	public final static String   M4YSL002 = "M4YSL002";    //BILLET야드 입고/장입Carry-Out요구
	
	/** 소형압연L2(M5)	 */
	public final static String   M5YSL101 = "M5YSL101";    //BILLET야드 장입Carry-Out요구
	public final static String   M5YSL102 = "M5YSL102";    //BILLET야드 장입Carry-In요구
	public final static String   YSM5L101 = "YSM5L101";    //BILLET야드 입고/장입Carry-Out완료
	public final static String   YSM5L102 = "YSM5L102";    //BILLET야드 장입 Carry-In완료
	
	/** 봉강정정L2(M6)	 */
	public final static String   M6YSL101 = "M6YSL101";    //봉강제품야드 입고Carry-Out
	public final static String   YSM6L101 = "YSM6L101";    //봉강제품야드 입고Carry-Out 완료
	
	/** C-HOOK 지멘스(N7)	 */
	public final static String   YSN7L101 = "YSN7L101";    //선재제품야드 입고라우팅지시
	
	
/** 야드	 */	
	/*--------------------------------------------------------------------------------*/
	public final static String   N1YSL001 = "N1YSL001";    //BLOOM야드 저장위치제원요구
	public final static String   N2YSL001 = "N2YSL001";    //BILLET야드 저장위치제원요구
	public final static String   N3YSL001 = "N3YSL001";    //선재제품야드 저장위치제원요구
	public final static String   N4YSL001 = "N4YSL001";    //봉강제품야드 저장위치제원요구
	public final static String   N5YSL001 = "N5YSL001";    //선재자동화제품야드 저장위치제원요구
	public final static String   N6YSL001 = "N6YSL001";    //봉강자동화제품야드 저장위치제원요구
	public final static String   N7YSL201 = "N7YSL201";    // 2025.09.10 빌렛정정 저장위치제원요구
	public final static String   N8YSL001 = "N8YSL001";    // 2025.09.10 대형옥외 저장위치제원요구
	
	public final static String   N1YSL002 = "N1YSL002";    //BLOOM야드 저장품제원요구
	public final static String   N2YSL002 = "N2YSL002";    //BILLET야드 저장품제원요구
	public final static String   N3YSL002 = "N3YSL002";    //선재제품야드 저장품제원요구
	public final static String   N4YSL002 = "N4YSL002";    //봉강제품야드 저장품제원요구
	public final static String   N5YSL002 = "N5YSL002";    //선재자동화제품야드 저장품제원요구
	public final static String   N6YSL002 = "N6YSL002";    //봉강자동화제품야드 저장품제원요구
	public final static String   N7YSL202 = "N7YSL202";    // 2025.09.10 빌렛정정 저장품제원요구
	public final static String   N8YSL002 = "N8YSL002";    // 2025.09.10 대형옥외 저장품제원요구

	public final static String   N1YSL003 = "N1YSL003";    //BLOOM야드 설비고장복구실적
	public final static String   N2YSL003 = "N2YSL003";    //BILLET야드 설비고장복구실적
	public final static String   N3YSL003 = "N3YSL003";    //선재제품야드 설비고장복구실적
	public final static String   N4YSL003 = "N4YSL003";    //봉강제품야드 설비고장복구실적
	public final static String   N5YSL003 = "N5YSL003";    //선재자동화제품야드 설비고장복구실적
	public final static String   N6YSL003 = "N6YSL003";    //봉강자동화제품야드 설비고장복구실적
	public final static String   N7YSL203 = "N7YSL203";    // 2025.09.10 빌렛정정 설비고장복구실적
	public final static String   N8YSL003 = "N8YSL003";    // 2025.09.10 대형옥외 설비고장복구실적
	
	public final static String   N1YSL004 = "N1YSL004";    //BLOOM야드 크레인작업지시요구
	public final static String   N2YSL004 = "N2YSL004";    //BILLET야드 크레인작업지시요구
	public final static String   N3YSL004 = "N3YSL004";    //선재제품야드 크레인작업지시요구
	public final static String   N4YSL004 = "N4YSL004";    //봉강제품야드 크레인작업지시요구
	public final static String   N5YSL004 = "N5YSL004";    //선재자동화제품야드 크레인작업지시요구
	public final static String   N6YSL004 = "N6YSL004";    //봉강자동화제품야드 크레인작업지시요구
	public final static String   N7YSL204 = "N7YSL204";    // 2025.09.10 빌렛정정 크레인작업지시요구
	public final static String   N8YSL004 = "N8YSL004";    // 2025.09.10 대형옥외 크레인작업지시요구

	public final static String   N1YSL005 = "N1YSL005";    //BLOOM야드 크레인권상실적
	public final static String   N2YSL005 = "N2YSL005";    //BILLET야드 크레인권상실적
	public final static String   N3YSL005 = "N3YSL005";    //선재제품야드 크레인권상실적
	public final static String   N4YSL005 = "N4YSL005";    //봉강제품야드 크레인권상실적
	public final static String   N5YSL005 = "N5YSL005";    //선재자동화제품야드 크레인권상실적
	public final static String   N6YSL005 = "N6YSL005";    //봉강자동화제품야드 크레인권상실적
	public final static String   N7YSL205 = "N7YSL205";    // 2025.09.10 빌렛정정  크레인권상실적
	public final static String   N8YSL005 = "N8YSL005";    // 2025.09.10 대형옥외 크레인권상실적
	
	public final static String   N1YSL006 = "N1YSL006";    //BLOOM야드 크레인권하실적
	public final static String   N2YSL006 = "N2YSL006";    //BILLET야드 크레인권하실적
	public final static String   N3YSL006 = "N3YSL006";    //선재제품야드 크레인권하실적
	public final static String   N4YSL006 = "N4YSL006";    //봉강제품야드 크레인권하실적
	public final static String   N5YSL006 = "N5YSL006";    //선재자동화제품야드 크레인권하실적
	public final static String   N6YSL006 = "N6YSL006";    //봉강자동화제품야드 크레인권하실적
	public final static String   N7YSL206 = "N7YSL206";    // 2025.09.10 빌렛정정  크레인권하실적
	public final static String   N8YSL006 = "N8YSL006";    // 2025.09.10 대형옥외 크레인권하실적

	public final static String   YSN1L001 = "YSN1L001";    //BLOOM야드 저장위치제원
	public final static String   YSN2L001 = "YSN2L001";    //BILLET야드 저장위치제원
	public final static String   YSN3L001 = "YSN3L001";    //선재제품야드 저장위치제원
	public final static String   YSN4L001 = "YSN4L001";    //봉강제품야드 저장위치제원
	public final static String   YSN5L001 = "YSN5L001";    //선재자동화제품야드 저장위치제원
	public final static String   YSN6L001 = "YSN6L001";    //봉강자동화제품야드 저장위치제원
	public final static String   YSN7L201 = "YSN7L201";    // 2025.09.10 빌렛정정  저장위치제원
	public final static String   YSN8L001 = "YSN8L001";    // 2025.09.10 대형옥외 저장위치제원
	
	public final static String   YSN1L002 = "YSN1L002";    //BLOOM야드 저장품제원
	public final static String   YSN2L002 = "YSN2L002";    //BILLET야드 저장품제원
	public final static String   YSN3L002 = "YSN3L002";    //선재제품야드 저장품제원
	public final static String   YSN4L002 = "YSN4L002";    //봉강제품야드 저장품제원
	public final static String   YSN5L002 = "YSN5L002";    //선재자동화제품야드 저장품제원
	public final static String   YSN6L002 = "YSN6L002";    //봉강자동화제품야드 저장품제원
	public final static String   YSN7L202 = "YSN7L202";    // 2025.09.10 빌렛정정  저장품제원
	public final static String   YSN8L002 = "YSN8L002";    // 2025.09.10 대형옥외  저장품제원
	
	public final static String   YSN1L003 = "YSN1L003";    //BLOOM야드 크레인작업지시
	public final static String   YSN2L003 = "YSN2L003";    //BILLET야드 크레인작업지시
	public final static String   YSN3L003 = "YSN3L003";    //선재제품야드 크레인작업지시
	public final static String   YSN4L003 = "YSN4L003";    //봉강제품야드 크레인작업지시
	public final static String   YSN5L003 = "YSN5L003";    //선재자동화제품야드 크레인작업지시
	public final static String   YSN6L003 = "YSN6L003";    //봉강자동화제품야드 크레인작업지시
	public final static String   YSN7L203 = "YSN7L203";    // 2025.09.10 빌렛정정 크레인작업지시
	public final static String   YSN8L003 = "YSN8L003";    // 2025.09.10 대형옥외  크레인작업지시

	public final static String   YSN1L004 = "YSN1L004";    //BLOOM야드 크레인작업실적응답
	public final static String   YSN2L004 = "YSN2L004";    //BILLET야드 크레인작업실적응답
	public final static String   YSN3L004 = "YSN3L004";    //선재제품야드 크레인작업실적응답
	public final static String   YSN4L004 = "YSN4L004";    //봉강제품야드 크레인작업실적응답
	public final static String   YSN5L004 = "YSN5L004";    //선재자동화제품야드 크레인작업실적응답
	public final static String   YSN6L004 = "YSN6L004";    //봉강자동화제품야드 크레인작업실적응답
	public final static String   YSN7L204 = "YSN7L204";    // 2025.09.10 빌렛정정 크레인작업실적응답
	public final static String   YSN8L004 = "YSN8L004";    // 2025.09.10 대형옥외 크레인작업실적응답
	
	public final static String   N7YSL301 = "N7YSL301";    // 2026.01.15 소형야드 저장위치제원요구
	public final static String   N7YSL302 = "N7YSL302";    // 2026.01.15 소형야드 저장품제원요구
	public final static String   N7YSL303 = "N7YSL303";    // 2026.01.15 소형야드 설비고장복구실적
	public final static String   N7YSL304 = "N7YSL304";    // 2026.01.15 소형야드 크레인작업지시요구
	public final static String   N7YSL305 = "N7YSL305";    // 2026.01.15 소형야드 크레인권상실적
	public final static String   N7YSL306 = "N7YSL306";    // 2026.01.15 소형야드 크레인권하실적
//	public final static String   N7YSL307 = "N7YSL307";    // 2026.01.15 소형야드 크레인운전모드전환
	public final static String   N7YSL308 = "N7YSL308";    // 2026.01.15 소형야드 차량작업예정정보요구
	public final static String   N7YSL309 = "N7YSL309";    // 2026.01.15 소형야드 크레인작업가능유무응답
	public final static String   N7YSL310 = "N7YSL304";    // 2026.01.15 소형야드 L2야드적치현황정보
	public final static String   N7YSL311 = "N7YSL311";    // 2026.01.15 소형야드 L2야드좌표정보
	public final static String   N7YSL321 = "N7YSL321";    // 2026.01.15 소형야드 대차상차실적
	public final static String   N7YSL322 = "N7YSL322";    // 2026.01.15 소형야드 대차하차실적
	public final static String   N7YSL323 = "N7YSL323";    // 2026.01.15 소형야드 대차이동정보
	public final static String   N7YSL324 = "N7YSL324";    // 2026.01.15 소형야드 대차상태정보
	public final static String   N7YSL325 = "N7YSL325";    // 2026.01.15 소형야드 대차이송재료요구
	
	public final static String   YSN7L301 = "YSN7L301";    // 2026.01.15 소형야드 저장위치제원
	public final static String   YSN7L302 = "YSN7L302";    // 2026.01.15 소형야드 저장품제원
	public final static String   YSN7L303 = "YSN7L303";    // 2026.01.15 소형야드 크레인작업지시
	public final static String   YSN7L304 = "YSN7L304";    // 2026.01.15 소형야드 크레인작업실적응답
	public final static String   YSN7L305 = "YSN7L305";    // 2026.01.15 소형야드 차량작업예정정보
	public final static String   YSN7L310 = "YSN7L310";    // 2026.01.15 소형야드 L2야드적치현황정보요구
	public final static String   YSN7L321 = "YSN7L321";    // 2026.01.15 소형야드 대차상차실적
	public final static String   YSN7L322 = "YSN7L322";    // 2026.01.15 소형야드 대차하차실적
	public final static String   YSN7L323 = "YSN7L323";    // 2026.01.15 소형야드 대차이동요구
	
	/** 블룸소재야드 L2(N1)	 */
	public final static String   N1YSL007 = "N1YSL007";    //BLOOM야드 대차이동실적
	public final static String   N1YSL008 = "N1YSL008";    //BLOOM야드 크레인작업계획요구

	public final static String   YSN1L005 = "YSN1L005";    //BLOOM야드 대차출발지시
	public final static String   YSN1L006 = "YSN1L006";    //BLOOM야드 대차작업실적
	public final static String   YSN1L007 = "YSN1L007";    //BLOOM야드 크레인작업계획
	
	/** 빌렛소재야드 L2(N2)	 */
	public final static String   N2YSL007 = "N2YSL007";    //BILLET야드 대차이동실적
	public final static String   N2YSL008 = "N2YSL008";    //BILLET야드 크레인작업계획요구
	public final static String   N2YSL009 = "N2YSL009";    //BILLET야드 컨베이어 적치정보
	public final static String   N2YSL010 = "N2YSL010";    //BILLET야드 입고/장입Carry-Out요구
	public final static String   N2YSL011 = "N2YSL011";    //BILLET야드 크레인작업모드 셋팅(A:순번,Z:장입,F:입고,T:차량하차)
	
	public final static String   YSN2L005 = "YSN2L005";    //BILLET야드 크레인작업계획
	public final static String   YSN2L006 = "YSN2L006";    //BILLET야드 라우팅지시

	/** 선재야드L2(N3)	 */
	/** 봉강야드L2(N4)	 */
	public final static String   N4YSL007 = "N4YSL007";    //봉강제품야드 대차이동실적

	public final static String   YSN4L005 = "YSN4L005";    //봉강제품야드 대차출발지시
	public final static String   YSN4L006 = "YSN4L006";    //봉강제품야드 대차작업실적

	/** 선재자동화창고L2(N5)	 */
	public final static String   N5YSL007 = "N5YSL007";    //선재자동화제품야드 설비이동실적
	public final static String   N5YSL008 = "N5YSL008";    //선재자동화제품야드 HIGH BAY RGV BOOK-OUT 정보
	public final static String   N5YSL009 = "N5YSL009";    //선재자동화제품야드 S-Crane 작업예정지시
	
	public final static String   YSN5L005 = "YSN5L005";    //선재자동화제품야드 S-Crane 작업순서 변경
	public final static String   YSN5L006 = "YSN5L006";    //선재자동화제품야드 S-Crane  작업지시

	/** 봉강자동화창고L2(N6)	 */
	public final static String   N6YSL007 = "N6YSL007";    //봉강자동화제품야드 TRV 작업대기존 도착실적
	public final static String   N6YSL008 = "N6YSL008";    //봉강자동화제품야드 S-Crane 작업예정지시

	public final static String   YSN6L005 = "YSN6L005";    //봉강자동화제품야드 S-Crane 작업순서 변경
	public final static String   YSN6L006 = "YSN6L006";    //봉강자동화제품야드 S-Crane  작업지시

	
	/** 내부인터페이스 정의	 */
	public final static String   YSYSJ001 = "YSYSJ001";    //블름 크레인작업지시요구YDYDJ642

	/** 1:블름,2:빌렛,3:봉강,4:선재	 */
	public final static String   YSYSJ101 = "YSYSJ101";    //블름 크레인작업지시요구YDYDJ642
	public final static String   YSYSJ102 = "YSYSJ102";    //블름 크레인스케줄MainYDYDJ506
	public final static String   YSYSJ103 = "YSYSJ103";    //블름 크레인저장위치결정MainYDYDJ507
	public final static String   YSYSJ104 = "YSYSJ104";    //블름 크레인권상실적백업YDYDJ604
	public final static String   YSYSJ105 = "YSYSJ105";    //블름 크레인권하실적백업YDYDJ605
	public final static String   YSYSJ106 = "YSYSJ106";    //블름 출하차량도착실적BackupYDYDJ655
	public final static String   YSYSJ107 = "YSYSJ107";    //블름 출하차량출발실적BackupYDYDJ659
	public final static String   YSYSJ108 = "YSYSJ108";    //블름 차량상차작업요구YDYDJ247
	public final static String   YSYSJ109 = "YSYSJ109";    //블름 차량하차작업요구YDYDJ208
	public final static String   YSYSJ110 = "YSYSJ110";    //블름 대차스케줄MAINYDYDJ521
	public final static String   YSYSJ111 = "YSYSJ111";    //블름 대차하차작업요구YDYDJ210
	public final static String   YSYSJ112 = "YSYSJ112";    //블름 대차상차작업요구YDYDJ250
	public final static String   YSYSJ113 = "YSYSJ113";    //블름 입고Carry-Out요구YDYDJ201
	public final static String   YSYSJ114 = "YSYSJ114";    //블름 보급Carry-In작업요구YDYDJ241
	
	public final static String   YSYSJ201 = "YSYSJ201";    //빌렛 크레인작업지시요구YDYDJ642
	public final static String   YSYSJ202 = "YSYSJ202";    //빌렛 크레인스케줄MainYDYDJ506
	public final static String   YSYSJ203 = "YSYSJ203";    //빌렛 크레인저장위치결정MainYDYDJ507
	public final static String   YSYSJ204 = "YSYSJ204";    //빌렛 크레인권상실적백업YDYDJ604
	public final static String   YSYSJ205 = "YSYSJ205";    //빌렛 크레인권하실적백업YDYDJ605
	public final static String   YSYSJ206 = "YSYSJ206";    //빌렛 출하차량도착실적BackupYDYDJ655
	public final static String   YSYSJ207 = "YSYSJ207";    //빌렛 출하차량출발실적BackupYDYDJ659
	public final static String   YSYSJ208 = "YSYSJ208";    //빌렛 차량상차작업요구YDYDJ247
	public final static String   YSYSJ209 = "YSYSJ209";    //빌렛 차량하차작업요구YDYDJ208
	public final static String   YSYSJ210 = "YSYSJ210";    //빌렛 대차스케줄MAINYDYDJ521
	public final static String   YSYSJ211 = "YSYSJ211";    //빌렛 대차하차작업요구YDYDJ210
	public final static String   YSYSJ212 = "YSYSJ212";    //빌렛 대차상차작업요구YDYDJ250
	public final static String   YSYSJ213 = "YSYSJ213";    //빌렛 입고Carry-Out요구YDYDJ201
	public final static String   YSYSJ214 = "YSYSJ214";    //빌렛 보급Carry-In작업요구YDYDJ241
	
	public final static String   YSYSJ301 = "YSYSJ301";    //봉강 크레인작업지시요구YDYDJ642
	public final static String   YSYSJ302 = "YSYSJ302";    //봉강 크레인스케줄MainYDYDJ506
	public final static String   YSYSJ303 = "YSYSJ303";    //봉강 크레인저장위치결정MainYDYDJ507
	public final static String   YSYSJ304 = "YSYSJ304";    //봉강 크레인권상실적백업YDYDJ604
	public final static String   YSYSJ305 = "YSYSJ305";    //봉강 크레인권하실적백업YDYDJ605
	public final static String   YSYSJ306 = "YSYSJ306";    //봉강 출하차량도착실적BackupYDYDJ655
	public final static String   YSYSJ307 = "YSYSJ307";    //봉강 출하차량출발실적BackupYDYDJ659
	public final static String   YSYSJ308 = "YSYSJ308";    //봉강 차량상차작업요구YDYDJ247
	public final static String   YSYSJ309 = "YSYSJ309";    //봉강 차량하차작업요구YDYDJ208
	public final static String   YSYSJ310 = "YSYSJ310";    //봉강 대차스케줄MAINYDYDJ521
	public final static String   YSYSJ311 = "YSYSJ311";    //봉강 대차하차작업요구YDYDJ210
	public final static String   YSYSJ312 = "YSYSJ312";    //봉강 대차상차작업요구YDYDJ250
	public final static String   YSYSJ313 = "YSYSJ313";    //봉강 입고Carry-Out요구YDYDJ201
	
	public final static String   YSYSJ401 = "YSYSJ401";    //선재 크레인작업지시요구YDYDJ642
	public final static String   YSYSJ402 = "YSYSJ402";    //선재 크레인스케줄MainYDYDJ506
	public final static String   YSYSJ403 = "YSYSJ403";    //선재 크레인저장위치결정MainYDYDJ507
	public final static String   YSYSJ404 = "YSYSJ404";    //선재 크레인권상실적백업YDYDJ604
	public final static String   YSYSJ405 = "YSYSJ405";    //선재 크레인권하실적백업YDYDJ605
	public final static String   YSYSJ406 = "YSYSJ406";    //선재 출하차량도착실적BackupYDYDJ655
	public final static String   YSYSJ407 = "YSYSJ407";    //선재 출하차량출발실적BackupYDYDJ659
	public final static String   YSYSJ408 = "YSYSJ408";    //선재 차량상차작업요구YDYDJ247
	public final static String   YSYSJ409 = "YSYSJ409";    //선재 차량하차작업요구YDYDJ208
	public final static String   YSYSJ413 = "YSYSJ413";    //선재 입고Carry-Out요구YDYDJ201

	public final static String   YSYSJ502 = "YSYSJ502";    //대형옥내 크레인스케줄Main
	public final static String   YSYSJ602 = "YSYSJ602";    //대형옥외 크레인스케줄Main
	public final static String   YSYSJ702 = "YSYSJ702";    //소형봉강 크레인스케줄Main
	
	public final static String   YSYSJ801 = "YSYSJ801";    //공통 차량입동지시 요구
	public final static String   YSYSJ802 = "YSYSJ802";    //공통 메일발송메세지 송신
	public final static String   YSYSJ901 = "YSYSJ901";    //공통 소재차량정지Point요구
	public final static String   YSYSJ902 = "YSYSJ902";    //공통 운전모드 변경요구
	public final static String   YSYSJ906 = "YSYSJ906";    //공통 출하차량도착수신
	
	
	
	/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	* 야드업무 내부적으로 사용되는 함수리턴코드 정의
	 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	//공통 문자리턴값
	public static final String RETN_CD_SUCCESS							= "SUCCESS";		//성공메세지코드
	public static final String RETN_CD_FAILURE							= "FAILURE";		//실패메세지코드
	
	//정수리턴값
	public static final int RETN_INT_SUCCESS							= 1;		//성공메세지코드
	public static final int RETN_INT_FAILURE							= -10000;	//실패메세지코드
	public static final int RETN_INT_TC_ERROR							= -10001;	//전문에러	
	
	//야드차량진행상태
	public static final String YD_CARLD_LEV								= "1";				//상차출발
	public static final String YD_CARLD_ARR								= "2";				//상차도착
	public static final String YD_CARLD_CHK								= "3";				//상차검수
	public static final String YD_CARLD_ST								= "4";				//상차개시
	public static final String YD_CARLD_CMPL							= "5";				//상차완료
	public static final String YD_CARUD_LEV								= "A";				//하차출발
	public static final String YD_CARUD_ARR								= "B";				//하차도착
	public static final String YD_CARUD_CHK								= "C";				//하차검수
	public static final String YD_CARUD_ST								= "D";				//하차개시
	public static final String YD_CARUD_CMPL							= "E";				//하차완료	
	
	public static final String YD_STK_BED_WT_MAX_DEFAULT				= "100000";			//MAX높이 BED
	
	//차량에 대한 설비 기본값
	public static final String YD_TS_CAR_EQP_ID							= "XXPT01";			//구내운송차량에 대한 기본 설비ID

	//개소코드
	public static final String BL_WLOC_CD_A						        = "S3Y10";			//BL 개소 A동
	public static final String BL_WLOC_CD_B						        = "S3Y11";			//BL 개소 B동
	public static final String BL_WLOC_CD_C						        = "S3Y30";			//BL 개소 C동
	public static final String BA_WLOC_CD						        = "S5Y10";			//BT 개소 AB
	public static final String BC_WLOC_CD						        = "S5Y20";			//BT 개소 C
	public static final String GA_WLOC_CD						        = "S6Y40";			//봉강 개소 A동
	public static final String GB_WLOC_CD						        = "S6Y50";			//봉강 개소 B동
	public static final String GD_WLOC_CD						        = "S5Y40";			//선재 개소 D동
	public static final String GE_WLOC_CD						        = "S5Y50";			//선재 개소 E동
	
// 2025.08.19 개소코드 추가	
	public static final String G11_WLOC_CD						        = "S3Y22";			// 빌렛정정 남1문
	public static final String G21_WLOC_CD						        = "S4S13";			// 빌렛정정 남2문
	public static final String G41_WLOC_CD						        = "S3S20";			// 대형옥내 남4문
	public static final String G42_WLOC_CD						        = "S3Y99";			// 빌렛정정 대형옥외야드 이송재(밴드쏘)
	public static final String GF_WLOC_CD						        = "S3Y21";			// 대형옥외 옥외야드
	public static final String GH_WLOC_CD						        = "BSY04";			// 철분말
	
	
	
	//포인트 요구시 필요
	public static final String DEFAULT_BL_BAY_GP				        = "B";				//이송 동
	public static final String DEFAULT_BA_BAY_GP				        = "B";				//이송 동
	public static final String DEFAULT_BC_BAY_GP				        = "C";				//이송 동
	public static final String DEFAULT_GA_BAY_GP				        = "A";				//이송 동
	public static final String DEFAULT_GB_BAY_GP				        = "B";				//이송 동
	public static final String DEFAULT_GC_BAY_GP				        = "C";				//이송 동
	public static final String DEFAULT_GD_BAY_GP				        = "D";				//이송 동
	public static final String DEFAULT_GE_BAY_GP				        = "E";				//이송 동

	//크레인관련리턴값(제품크레인 상태
	public static final String RETN_CRN_SCH_PROH						= "SCH_PROH";		//야드스케쥴금지
	public static final String RETN_CRN_NO_WRK							= "NO_WORK";		//작업예약이 존재하지 않음
	public static final String RETN_CRN_EXIST_WRK						= "EXIST_WORK";		//작업예약이 존재
	public static final String RETN_CRN_NO_SCH							= "NO_SCH";			//크레인스케줄이 존재하지 않음
	public static final String RETN_CRN_EXIST_SCH						= "EXIST_SCH";		//크레인스케줄이 존재
	public static final String RETN_CRN_STATUS_ERR						= "STATUS_ERR";		//크레인의 작업상태가 올바르지 않음
	public static final String RETN_CRN_NO_ALT_CRN						= "NO_ALT_CRN";		//대체크레인이 존재하지 않음
	public static final String RETN_CD_NOTEXIST							= "NOTEXIST";		//값이 존재하지 않음
	public static final String RETN_CD_NO_PARAM							= "NOPARAM";		//파라미터가 존재하지 않음
	public static final String RETN_CD_TC_ERROR							= "TC_ERROR";		//전문에러
	public static final String YD_EQP_NOTEXIST							= "EQP_NOTEXIST";	//설비가 존재하지 않음
//	
//	
//	
//	//전문버퍼의 전문항목이름  
//	public static final String TC_BODY									= "ZZ_TC_BODY";
//	
//	//야드 모니터링 채널
//	public static final String YD_MONITORING_CHANNEL_01					= "yd_monitor01";
//	
//	public static final String YD_MONITORING_CHANNEL_A					= "yd_monitorA";
//	public static final String YD_MONITORING_CHANNEL_D					= "yd_monitorD";
//	public static final String YD_MONITORING_CHANNEL_K					= "yd_monitorK";
//	public static final String YD_MONITORING_CHANNEL_T					= "yd_monitorT";
//	public static final String YD_MONITORING_CHANNEL_H					= "yd_monitorH";
//	public static final String YD_MONITORING_CHANNEL_J					= "yd_monitorJ";
//	public static final String YD_MONITORING_CHANNEL_S					= "yd_monitorS";
//	
//	//로그레벨 상수 정의
//	public static final  int ERROR   = 1;
//	public static final  int WARNING = 2;
//	public static final  int INFO    = 3;
//	public static final  int DEBUG   = 4;
//	
//	/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	* 야드업무 내부적으로 사용되는 함수리턴코드 정의
//	 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	//공통 문자리턴값
//	public static final String RETN_CD_SUCCESS							= "SUCCESS";		//성공메세지코드
//	public static final String RETN_CD_FAILURE							= "FAILURE";		//실패메세지코드
//	public static final String RETN_CD_NOTEXIST							= "NOTEXIST";		//값이 존재하지 않음
//	public static final String RETN_CD_EXIST							= "EXIST";			//값이 존재함
//	public static final String RETN_CD_DUPLICATE						= "DUPLICATE";		//값이 중복됨
//	public static final String RETN_CD_TC_ERROR							= "TC_ERROR";		//전문에러
//	public static final String RETN_CD_NO_PARAM							= "NOPARAM";		//파라미터가 존재하지 않음
//	public static final String RETN_CD_EQ_STATUS						= "EQUAL_STATUS";	//상태값이 같은 경우
//	public static final String RETN_CD_NOTEQ_STATUS						= "NOTEQUAL_STATUS";//상태값이 다른 경우
//	
//	//크레인관련리턴값
//	public static final String RETN_CRN_SCH_PROH						= "SCH_PROH";		//야드스케쥴금지
//	public static final String RETN_CRN_NO_WRK							= "NO_WORK";		//작업예약이 존재하지 않음
//	public static final String RETN_CRN_EXIST_WRK						= "EXIST_WORK";		//작업예약이 존재
//	public static final String RETN_CRN_NO_SCH							= "NO_SCH";			//크레인스케줄이 존재하지 않음
//	public static final String RETN_CRN_EXIST_SCH						= "EXIST_SCH";		//크레인스케줄이 존재
//	public static final String RETN_CRN_STATUS_ERR						= "STATUS_ERR";		//크레인의 작업상태가 올바르지 않음
//	public static final String RETN_CRN_NO_ALT_CRN						= "NO_ALT_CRN";		//대체크레인이 존재하지 않음
//	
//	//운송설비관련리턴값
//	public static final String RETN_TRN_COL_ACT							= "COL_ACT";		//정지위치 활성 상태
//	public static final String RETN_TRN_COL_INACT						= "COL_INACT";		//정지위치 비활성 상태
//	
//	//정수리턴값
//	public static final Integer RETN_INT_SUCCESS						= new Integer(1);	//성공메세지코드
//	public static final Integer RETN_INT_FAILURE						= new Integer(-10000);	//실패메세지코드
//	public static final Integer RETN_INT_TC_ERROR						= new Integer(-10001);	//전문에러
//	
//	//TO위치결정 시 사용되는 반환값 정의
//	public static final String RETN_SH_OVER								= "SH_OVER";		//매수초과
//	public static final String RETN_WT_OVER								= "WT_OVER";		//중량초과
//	public static final String RETN_H_OVER								= "H_OVER";			//높이초과
//	public static final String RETN_BED_INACT							= "BED_INACT";		//적치베드가 활성상태가 아님
//	public static final String RETN_BED_WHIO_NOT_IN						= "WHIO_NOT_IN";	//입고불가능상태
//	public static final String RETN_BED_UN_WAIT							= "UN_WAIT";		//권상대기
//	public static final String RETN_SAME_SCH_CD							= "SAME_SCH_CD";	//스케줄코드가 같음
//	public static final String RETN_SCH_EARLY_PRIOR						= "EARLY_PRIOR";	//우선순위가 빠름
//	public static final String RETN_SCH_LATE_PRIOR						= "LATE_PRIOR";		//우선순위가 늦음
//	public static final String RETN_NOT_EXIST_SCH_CD					= "NOTEXIST_SCH_CD";//스케줄코드가 존재하지 않음
//	public static final String RETN_NOT_EXIST_BED						= "NOTEXIST_BED";	//적치가능베드가 존재하지 않음
//	public static final String RETN_BIG_NOT_EXIST_BED					= "BIG_NOTEXIST_BED";	//대형고객사 적치가능베드가 존재하지 않음
//	/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	
//	//C연주슬라브야드의 B열연이송대기 기본 이송 적치열 - D동 팔레트 저장위치
//	public static final String A_YD_BASE_FTMV_COL 						= "ADPT01";
//	
	//야드구분
//	public static final String YD_GP_C_SLAB_YARD						= "A";				//C연주슬라브야드
//	public static final String YD_GP_A_PLATE_SLAB_YARD					= "D";				//A후판슬라브야드
//	public static final String YD_GP_C_HR_COIL_MATL_YARD				= "H";				//C열연코일소재야드
//	public static final String YD_GP_C_HR_COIL_GDS_YARD					= "J";				//C열연코일제품야드
//	public static final String YD_GP_PLATE_GDS_YARD						= "K";				//후판제품창고야드
//	public static final String YD_GP_PLATE2_JJ_YARD						= "F";				//2후판정정야드
//	public static final String YD_GP_PLATE2_GDS_YARD					= "T";				//2후판제품창고야드 - 2012.12.17 추가 (3기)
//	public static final String YD_GP_INTGR_PLATE_GDS_YARD				= "T";				//1,2 후판 통합 제품창고야드 (Data 이행 후 소스 상에서 'K'-->'T'로 일괄 변경하기 위한 상수로 데이터 이전에 K , 데이터 이후에 T 로 변경하여 컴파일 할 것) 
//	public static final String YD_GP_INTGR_YARD							= "S";				//통합야드A(부두)
//	public static final String YD_GP_A_HR_SLAB_YARD						= "0";				//A열연슬라브야드
//	public static final String YD_GP_A_HR_COIL_YARD						= "1";				//A열연COIL야드
//	public static final String YD_GP_B_HR_SLAB_YARD						= "2";				//B열연슬라브야드
//	public static final String YD_GP_B_HR_COIL_YARD						= "3";				//B열연COIL야드
//	public static final String YD_GP_A_PLATE_PLANT						= "@";				//A후판조업 - 가상야드구분 사용
//	public static final String YD_GP_C_HR_PLANT							= "%";				//C열연조업 - 가상야드구분 사용
	public static final String YD_GP_G									= "G";				//특수강 대형압연옥내야드, 정정야드, 사외통합야드, 대형봉강옥외야드, 소형봉강야드
	
	//동구분
	public static final String YD_BAY_GP_E								= "E";				//특수강 대형압연옥내야드, 빌렛정정야드
	public static final String YD_BAY_GP_F								= "F";				//특수강 대형봉강옥외야드
	public static final String YD_BAY_GP_H								= "H";				//특수강 사외통합야드
	public static final String YD_BAY_GP_D								= "D";				//특수강 소형봉강야드

//	//야드설비상태
//	public static final String YD_EQP_NOTEXIST							= "EQP_NOTEXIST";	//설비가 존재하지 않음
//	public static final String YD_EQP_STAT_NORM							= "N";				//정상
//	public static final String YD_EQP_STAT_BREAK						= "B";				//고장
//	
//	//야드설비작업Mode
//	public static final String YD_EQP_WRK_MODE_ON_LINE					= "1";				//ON LINE
//	public static final String YD_EQP_WRK_MODE_OFF_LINE					= "2";				//OFF LINE
//	
//	//크레인의 설비작업상태
//	public static final String YD_EQP_STAT_IDLE 						= "W";				//크레인이 IDLE인 상태 - 스케줄수행대기
//	public static final String YD_EQP_STAT_OW 							= "0";				//명령선택대기
//	public static final String YD_EQP_STAT_UP_WO 						= "1";				//권상지시
//	public static final String YD_EQP_STAT_UP_CMPL 						= "2";				//권상완료
//	public static final String YD_EQP_STAT_DN_WO 						= "3";				//권하지시
//	public static final String YD_EQP_STAT_DN_CMPL 						= "4";				//권하완료
//	
//	//야드적치Bed용도구분
//	public static final String YD_STK_BED_USG_GP_RCPT					= "S";				//수입구 - CARRYOUT
//	public static final String YD_STK_BED_USG_GP_ISSUE					= "B";				//불출구 - CARRYIN
//	public static final String YD_STK_BED_USG_GP_YARD					= "Y";				//야드베드
//	
//	/*+++++++++++++++++++++
//	 * 차량관련 상수 정의 시작
//	 +++++++++++++++++++++*/
//	//포인트개폐구분
//	public static final String PNT_UNIT_CL_GP_CLOSE						= "C";				//폐
//	public static final String PNT_UNIT_CL_GP_OPEN						= "O";				//개
//	
//	//차량에 대한 설비 기본값
//	public static final String YD_TS_CAR_EQP_ID							= "XXPT01";			//구내운송차량에 대한 기본 설비ID
//	public static final String YD_DM_CAR_EQP_ID							= "XXPT02";			//출하차량에 대한 기본 설비ID
//	
//	
//	//차량사용구분  출하,구내 운송  ( L : 구내운송 , G : 출하차량)
//	public static final String YD_CAR_USE_GP_TS						    = "L";				// 구내운송
//	public static final String YD_CAR_USE_GP_DM						    = "G";				// 출하차량
// 
//	
//	//야드차량진행상태
//	public static final String YD_CARLD_LEV								= "1";				//상차출발
//	public static final String YD_CARLD_ARR								= "2";				//상차도착
//	public static final String YD_CARLD_CHK								= "3";				//상차검수
//	public static final String YD_CARLD_ST								= "4";				//상차개시
//	public static final String YD_CARLD_CMPL							= "5";				//상차완료
//	public static final String YD_CARUD_LEV								= "A";				//하차출발
//	public static final String YD_CARUD_ARR								= "B";				//하차도착
//	public static final String YD_CARUD_CHK								= "C";				//하차검수
//	public static final String YD_CARUD_ST								= "D";				//하차개시
//	public static final String YD_CARUD_CMPL							= "E";				//하차완료
//
//	
//	//야드차량생성시 사용되는 입동지시순번 기본값
//	public static final String YD_BAYIN_WO_SEQ_DEFAULT					= "9";				//입동지시순번 기본값
//	
//	//야드배차순서 기본값
//	public static final String YD_CARASGN_SEQ_AUTO_DEFAULT				= "9";				//자동이송LOT편성일 경우 기본값
//	public static final String YD_CARASGN_SEQ_MAN_DEFAULT				= "99";				//수동이송LOT편성일 경우 기본값
//	
//	//운송작업영공구분
//	public static final String TRN_WRK_VOID								= "E";				//공차
//	public static final String TRN_WRK_FULL								= "F";				//영차
//	
//	//YD_EQP_WRK_STAT - 야드설비작업상태
//	public static final String YD_EQP_WRK_STAT_LD						= "L";				//상차
//	public static final String YD_EQP_WRK_STAT_UD						= "U";				//하차
//	
//	//차량포인트지시 시 포인트가 없을 경우 사용되는 포인트코드
//	public static final String YD_PNT_CD_NULL							= "0000";
//	
//	//대기장 포인트코드
//	public static final String YD_WAIT_PNT_CD							= "1Z99";			//대기장포인트코드
//	
//	public static final String YD_REPAIR_WLOC_CD						= "DMY1P";			//중장비수리고
//	
//	public static final String WLOC_CD_C_SLAB_YARD						= "DHY21";			//C연주슬라브야드(연주-옥내 Yard)
//	public static final String WLOC_CD_C_SLAB_YARD2						= "DVY19";			//C연주슬라브야드(2연주-옥내 Yard)
//	public static final String WLOC_CD_A_PLATE_SLAB_YARD				= "DKY21";			//1후판슬라브야드(1후판-옥내 Yard)
//	public static final String WLOC_CD_2_PLATE_SLAB_YARD				= "DWY22";			//2후판슬라브야드(2후판-옥내 Yard)
//	public static final String WLOC_CD_C_HR_COIL_MATL_YARD				= "DJY21";			//C열연 소재야드(D,E)
//	public static final String WLOC_CD_C_HR_COIL_MATL_YARD2				= "DJY22";			//C열연 소재야드(G,H)
//	public static final String WLOC_CD_C_HR_COIL_MATL_YARD3				= "DJY1E";			//C열연 제품야드(D,E,F,G,H)
//	
//	public static final String WLOC_CD_A_PLATE_PLANT 					= "DKY23";			//후판SIZING 개소코드
//	public static final String WLOC_CD_B_PLATE_PLANT 					= "DWY23";			//2후판SIZING 개소코드
//	public static final String WLOC_CD_A_PLATE_PLANT_PNT_CD				= "1A01";			//후판SIZING Point코드
//	public static final String WLOC_CD_C_HR_PLANT 						= "DJY24";			//열연재열재 개소코드 
//	public static final String WLOC_CD_C_HR_PLANT_PNT_CD 				= "1A01";			//열연재열재 Point코드
//	public static final String WLOC_CD_B_HR_PLANT 						= "D3Y43";			//B열연 개소코드	
//	public static final String WLOC_CD_A_HR_PLANT 						= "D2Y43";			//A열연 개소코드	
//	
//	public static final String WLOC_CD_PLATE_GDS_YARD 					= "DWY26";			//1후판 제품창고 개소코드 - 2012.12.18 추가 (3기) , 통합후 DKY30 -> DWY26
//	
//	public static final String WLOC_CD_PLATE2_GDS_YARD 					= "DWY26";			//2후판 제품창고 개소코드 - 2012.12.18 추가 (3기) *구내운송에서 개소코드 2013.03.12 일 DWY26으로 확정*
//	
//	
//	/*+++++++++++++++ 차량관련 상수 정의 끝 +++++++++++++++*/
//	
//	//야드적치열활성상태
//	public static final String YD_STK_COL_ACTIVE						= "L";				//적치가능
//	public static final String YD_STK_COL_INACTIVE						= "C";				//비활성화
//	public static final String YD_STK_COL_NOUSE							= "N";				//사용불가
//	
//	//야드적치Bed활성상태
//	public static final String YD_STK_BED_ACTIVE						= "L";				//적치가능
//	public static final String YD_STK_BED_INACTIVE						= "C";				//비활성화
//	public static final String YD_STK_BED_NOUSE							= "N";				//사용불가
//	
//	//적치단활성상태
//	public static final String YD_STK_LYR_ACTIVE						= "E";				//적치가능
//	public static final String YD_STK_LYR_INACTIVE						= "C";				//비활성화
//	public static final String YD_STK_LYR_NOUSE							= "N";				//사용불가
//	public static final String YD_STK_LYR_FULL							= "F";				//적치완료
//	
//	//적치단 재료 상태 	
//	public static final String YD_STK_LYR_MTL_STAT_STK                  = "C";              //적치중
//	public static final String YD_STK_LYR_MTL_STAT_DN_WAIT              = "D";              //권하대기
//	public static final String YD_STK_LYR_MTL_STAT_STK_ABLE             = "E";              //적치가능
//	public static final String YD_STK_LYR_MTL_STAT_UN_WAIT              = "U";              //권상대기
//	public static final String YD_STK_LYR_MTL_STAT_STK_UNABLE           = "X";              //적치불가
//	
//	//야드적치BED입출고상태
//	public static final String YD_STK_BED_WHIO_ENABLE					= "E";				//입출고가능
//	public static final String YD_STK_BED_WHIO_FULL						= "F";				//완산BED
//	public static final String YD_STK_BED_WHIO_X						= "X";				//입출고금지
//	public static final String YD_STK_BED_WHIO_VIRTUAL					= "G";				//가적BED
//
//	
//	
//	//베드의 기본 야드적치Bed중량Max --> 구내운송이나 출하차량이 출발 시 차량정지위치의 BED를 비활성화 시 같이 설정하는 베드중량 MAX
//	public static final String YD_STK_BED_WT_MAX_DEFAULT				= "300000";
//	
//	
//	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	 * 크레인작업실적응답 
//	 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
//	//U:권상실적, D:권하실적, E: 비상조업실적, F: 강제권하,   R:고장, M:모드변경, J : 지시요구
//	public static final String CRN_WRK_RE_LD_WR							= "U";				//권상실적
//	public static final String CRN_WRK_RE_DN_WR							= "D";				//권하실적
//	public static final String CRN_WRK_RE_EMG_PTOP						= "E";				//비상조업실적
//	public static final String CRN_WRK_RE_FRCE_DN						= "F";				//강제권하
//	public static final String CRN_WRK_RE_TRBL							= "R";				//고장
//	public static final String CRN_WRK_RE_MD_MOD						= "M";				//모드변경
//	public static final String CRN_WRK_RE_WO_DMD						= "J";				//지시요구
//	
//	// 크레인작업실적응답 코드
//	public static final String CRN_WRK_RE_CD_NORMAL_HD					= "0000";			//정상처리
//	public static final String CRN_WRK_RE_CD_NO_WRK						= "9999";			//크레인작업지시가 없을 경우
//	public static final String CRN_WRK_RE_CD_NO_WRK2					= "8888";			//강제권상요구 대상이 부적합 경우
//	//.....에러코드를 계속 정의 필요.....
//	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
//	
//	//야드재료품목
//	//public static final String YD_MTL_ITEM_
//	
//	//재료진도코드
//	public static final String PROG_CD_SLAB_CORRCETION_WRK_WAIT			= "A";				//SLAB정정작업대기
//	public static final String PROG_CD_WO_WAIT							= "B";				//지시대기
//	public static final String PROG_CD_WRK_WAIT							= "C";				//작업대기
//	public static final String PROG_CD_FTMV_WO_WAIT						= "D";				//이송지시대기
//	public static final String PROG_CD_FTMV_WRK_WAIT					= "E";				//이송작업대기
//	public static final String PROG_CD_STMP_HOLD						= "F";				//판정보류
//	public static final String PROG_CD_OVALL_STMP_WAIT					= "G";				//종합판정대기
//	public static final String PROG_CD_RCPT_WAIT						= "H";				//입고대기
//	public static final String PROG_CD_RETN_WAIT						= "J";				//반납대기
//	public static final String PROG_CD_DIST_WO_WAIT						= "K";				//출하지시대기
//	public static final String PROG_CD_TRN_WAIT							= "L";				//운송대기
//	public static final String PROG_CD_DIST_CMPL						= "M";				//출하완료
//	public static final String PROG_CD_TRN_WO_WAIT						= "N";				//운송지시대기
//	public static final String PROG_CD_DELIVERY_CMPL					= "P";				//인도완료
//	public static final String PROG_CD_FTMV_TKOV_WAIT					= "Q";				//이송인수대기
//	public static final String PROG_CD_AUCT_TG_PKUP						= "X";				//경매대상선정
//	public static final String PROG_CD_INLINE_MATCH_WAIT				= "Y";				//재공충당대기
//	public static final String PROG_CD_GDS_MATCH_WAIT					= "Z";				//제품충당대기
//	public static final String PROG_CD_SLAB_BUY_REG						= "0";				//SLAB구입등록(품질)
//	public static final String PROG_CD_SLAB_BUY_CMMT					= "1";				//SLAB구입확정(품질)
//	
//	//야드목표행선구분
//	public static final String AR_SLAB_BUY_REG							= "01";				//슬라브구입등록
//	public static final String AR_SLAB_BUY_CMMT							= "11";				//슬라브구입확정
//	public static final String AR_CORRCETION_WRK_WAIT_B_CCR_SF 			= "A1";				//정정작업대기(B열연CCR스카핑)
//	public static final String AR_CORRCETION_WRK_WAIT_C_CCR_SF 			= "A2";				//정정작업대기(C열연CCR스카핑)
//	public static final String AR_CORRCETION_WRK_WAIT_A_BP_SF 			= "A3";				//정정작업대기(A후판주편스카핑)
//	public static final String AR_CORRCETION_WRK_WAIT_A_BP 				= "A4";				//정정작업대기(A후판주편정정)
//	public static final String AR_WO_WAIT_B_HCR 						= "B1";				//지시대기(B열연HCR)
//	public static final String AR_WO_WAIT_B_CCR 						= "B2";				//지시대기(B열연CCR)
//	public static final String AR_WO_WAIT_C_HCR 						= "B3";				//지시대기(C열연HCR)
//	public static final String AR_WO_WAIT_C_CCR 						= "B4";				//지시대기(C열연CCR)
//	public static final String AR_WO_WAIT_A_HCR 						= "B5";				//지시대기(A후판HCR)
//	public static final String AR_WO_WAIT_A_CCR 						= "B6";				//지시대기(A후판CCR)
//	public static final String AR_WO_WAIT_2_HCR 						= "B7";				//지시대기(2후판HCR) --추가
//	public static final String AR_WO_WAIT_2_CCR 						= "B8";				//지시대기(2후판CCR) --추가
//	public static final String AR_WO_WAIT_A_AIR_COOLING					= "BA";				//지시대기(A열연공냉재)
//	public static final String AR_WO_WAIT_B_AIR_COOLING					= "BB";				//지시대기(B열연공냉재)
//	public static final String AR_WO_WAIT_C_AIR_COOLING					= "BC";				//지시대기(C열연공냉재)
//	public static final String AR_WRK_WAIT_B_MILL 						= "C1";				//작업대기(B열연압연)
//	public static final String AR_WRK_WAIT_C_MILL 						= "C2";				//작업대기(C열연압연)
//	public static final String AR_WRK_WAIT_A_MILL 						= "C3";				//작업대기(A후판압연)
//	public static final String AR_WRK_WAIT_2_MILL 						= "C3";				//작업대기(2후판압연) --추가
//	public static final String AR_WRK_WAIT_B_HFL 						= "CA";				//작업대기(B열연HFL)
//	public static final String AR_WRK_WAIT_B_1SPM 						= "CB";				//작업대기(B열연#1SPM)
//	public static final String AR_WRK_WAIT_B_2SPM 						= "CC";				//작업대기(B열연#2SPM)
//	public static final String AR_WRK_WAIT_B_WATER_COOLING				= "CD";				//작업대기(B열연수냉재)
//	public static final String AR_WRK_WAIT_C_HFL						= "CE";				//작업대기(C열연HFL)
//	public static final String AR_WRK_WAIT_C_SPM1 						= "CF";				//작업대기(C열연SPM1)
//	public static final String AR_WRK_WAIT_C_SPM2 						= "CG";				//작업대기(C열연SPM2)
//	public static final String AR_WRK_WAIT_C_1BINDING					= "CH";				//작업대기(C열연#1결속대)
//	public static final String AR_WRK_WAIT_C_2BINDING					= "CI";				//작업대기(C열연#2결속대)
//	public static final String AR_INLINE_MATCH_WAIT_B_MILL				= "Y1";				//재공충당대기(B열연압연)
//	public static final String AR_INLINE_MATCH_WAIT_C_MILL				= "Y2";				//재공충당대기(C열연압연)
//	public static final String AR_INLINE_MATCH_WAIT_A_BP_CORRCETION		= "Y3";				//재공충당대기(A후판주편정정)
//	public static final String AR_INLINE_MATCH_WAIT_A_MILL				= "Y4";				//재공충당대기(A후판압연)
//	public static final String AR_INLINE_MATCH_WAIT_2_BP_CORRCETION		= "Y5";				//재공충당대기(2후판주편정정) --추가
//	public static final String AR_INLINE_MATCH_WAIT_2_MILL				= "Y6";				//재공충당대기(2후판압연)     --추가
//	public static final String AR_INLINE_MATCH_WAIT_A_CORRCETION		= "YA";				//재공충당대기(A열연정정)
//	public static final String AR_INLINE_MATCH_WAIT_B_CORRCETION		= "YB";				//재공충당대기(B열연정정)
//	public static final String AR_INLINE_MATCH_WAIT_C_CORRCETION		= "YC";				//재공충당대기(C열연정정)
//	public static final String AR_INLINE_MATCH_WAIT_A_PLATE				= "YD";				//재공충당대기(A후판Plate)
//	public static final String AR_INLINE_MATCH_WAIT_2_PLATE				= "YE";				//재공충당대기(2후판Plate) --추가
//	public static final String AR_INLINE_FTMV_WRK_WAIT_B_HCR			= "E1";				//재공이송작업대기(B열연HCR)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_B_SCARF			= "E2";				//재공이송작업대기(B열연스카핑)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_B_NONSCARF		= "E3";				//재공이송작업대기(B열연NON스카핑)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_C_SCARF			= "E4";				//재공이송작업대기(C열연스카핑)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_C_NONSCARF		= "E5";				//재공이송작업대기(C열연NON스카핑)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_A_BP_SCARF		= "E6";				//재공이송작업대기(A후판주편스카핑)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_A_BP_CORRCETION	= "E7";				//재공이송작업대기(A후판주편정정)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_A_SP				= "E8";				//재공이송작업대기(A후판슬라브)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_A_SP_SIZING		= "E9";				//재공이송작업대기(A후판슬라브SIZING)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_2_BP_SCARF		= "EK";				//재공이송작업대기(2후판주편스카핑)   --추가
//	public static final String AR_INLINE_FTMV_WRK_WAIT_2_BP_CORRCETION	= "EL";				//재공이송작업대기(2후판주편정정)     --추가
//	public static final String AR_INLINE_FTMV_WRK_WAIT_2_SP				= "EM";				//재공이송작업대기(2후판슬라브)       --추가
//	public static final String AR_INLINE_FTMV_WRK_WAIT_2_SP_SIZING		= "EN";				//재공이송작업대기(2후판슬라브SIZING) --추가
//	public static final String AR_INLINE_FTMV_WRK_WAIT_A_CORRCETION		= "EA";				//재공이송작업대기(A열연정정)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_B_CORRCETION		= "EB";				//재공이송작업대기(B열연정정)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_C_CORRCETION		= "EC";				//재공이송작업대기(C열연정정)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_A_RENTPROC		= "ED";				//재공이송작업대기(임가공이송A사)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_B_RENTPROC		= "EE";				//재공이송작업대기(임가공이송B사)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_C_RENTPROC		= "EF";				//재공이송작업대기(임가공이송C사)
//	public static final String AR_OVALL_STMP_WAIT_OUTPL_SLAB			= "G1";				//종합판정대기(외판슬라브)
//	public static final String AR_OVALL_STMP_WAIT_COIL					= "G2";				//종합판정대기(COIL)
//	public static final String AR_OVALL_STMP_WAIT_PLATE					= "G3";				//종합판정대기(PLATE)
//	public static final String AR_SNDBK_WAIT_COIL						= "I2";				//반송대기(COIL)
//	public static final String AR_SNDBK_WAIT_PLATE						= "I3";				//반송대기(PLATE)
//	public static final String AR_RCPT_WAIT_OUTPL_SLAB					= "H1";				//입고대기(외판슬라브)
//	public static final String AR_RCPT_WAIT_COIL						= "H2";				//입고대기(COIL)
//	public static final String AR_RCPT_WAIT_PLATE						= "H3";				//입고대기(PLATE)
//	public static final String AR_RETN_WAIT_OUTPL_SLAB					= "J1";				//반납대기(외판슬라브)
//	public static final String AR_RETN_WAIT_COIL						= "J2";				//반납대기(COIL)
//	public static final String AR_RETN_WAIT_PLATE						= "J3";				//반납대기(PLATE)
//	public static final String AR_DIST_WO_WAIT_OUTPL_SLAB				= "K1";				//출하지시대기(외판슬라브)
//	public static final String AR_DIST_WO_WAIT_COIL						= "K2";				//출하지시대기(COIL)
//	public static final String AR_DIST_WO_WAIT_PLATE					= "K3";				//출하지시대기(PLATE)
//	public static final String AR_TRN_WO_WAIT_OUTPL_SLAB				= "N1";				//운송지시대기(외판슬라브)
//	public static final String AR_TRN_WO_WAIT_COIL						= "N2";				//운송지시대기(COIL)
//	public static final String AR_TRN_WO_WAIT_PLATE_SEL_WRK				= "N3";				//운송지시대기(PLATE선별작업대상)
//	public static final String AR_TRN_WO_WAIT_PLATE_SEL_WRK_CMPL		= "NA";				//운송지시대기(PLATE선별작업완료)
//	public static final String AR_TRN_WO_WAIT_PLATE_SEL_CMPL_SND		= "NB";				//운송지시대기(PLATE선별완료송신)
//	public static final String AR_TRN_WAIT_OUTPL_SLAB					= "L1";				//운송대기(외판슬라브)
//	public static final String AR_TRN_WAIT_COIL							= "L2";				//운송대기(COIL)
//	public static final String AR_TRN_WAIT_PLATE						= "L3";				//운송대기(PLATE)
//	public static final String AR_CARLD_WAIT_OUTPL_SLAB					= "L4";				//상차대기(외판슬라브)
//	public static final String AR_CARLD_WAIT_COIL						= "L5";				//상차대기(COIL)
//	public static final String AR_CARLD_WAIT_PLATE						= "L6";				//상차대기(PLATE)
//	public static final String AR_DIST_CMPL_OUTPL_SLAB					= "M1";				//출하완료(외판슬라브)
//	public static final String AR_DIST_CMPL_COIL						= "M2";				//출하완료(COIL)
//	public static final String AR_DIST_CMPL_PLATE						= "M3";				//출하완료(PLATE)
//	public static final String AR_GDS_MATCH_WAIT_OUTPL_SLAB				= "Z1"; 			//제품충당대기(외판슬라브)
//	public static final String AR_GDS_MATCH_WAIT_COIL					= "Z2"; 			//제품충당대기(COIL)
//	public static final String AR_GDS_MATCH_WAIT_PLATE					= "Z3"; 			//제품충당대기(PLATE)
//	public static final String AR_AUCT_TG_PKUP_OUTPL_SLAB				= "X1";				//경매대상선정(외판슬라브)
//	public static final String AR_AUCT_TG_PKUP_COIL						= "X2";				//경매대상선정(COIL)
//	public static final String AR_AUCT_TG_PKUP_PLATE					= "X3";				//경매대상선정(PLATE)
//	public static final String AR_STMP_HOLD_A_CORRCETION				= "F1";				//판정보류(A열연정정)
//	public static final String AR_STMP_HOLD_B_CORRCETION				= "F2";				//판정보류(B열연정정)
//	public static final String AR_STMP_HOLD_C_CORRCETION				= "F3";				//판정보류(C열연정정)
//	public static final String AR_STMP_HOLD_C_CORRCETION4				= "F4";				//재작업(코일-정정입측재투입)
//	public static final String AR_STMP_HOLD_C_CORRCETION5				= "F5";				//재작업(코일-보류장인출)
//	public static final String AR_WH_FTMV_A_WH							= "O1";				//고간이송(A열연창고)
//	public static final String AR_WH_FTMV_B_WH							= "O2";				//고간이송(B열연창고)
//	public static final String AR_WH_FTMV_C_WH							= "O3";				//고간이송(C열연창고)
//	public static final String AR_WH_FTMV_S_WH							= "O4";				//고간이송(통합야드)
//	public static final String AR_WH_FTMV_Z_WH							= "O5";				//고간이송(가상야드)
//	
//
//	/*++++++++++++++++++++++++++++++++++++++++++++++++++++
//	/* 			특수강 대형옥내 야드 상수 정의 시작
//	++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	//야드스케줄코드 정의
	public static final String SCH_CD_GEPC01LA							= "GEPC01LA";		//대형옥내 입고 (빌렛/각강출하상)
	public static final String SCH_CD_GEPC02LA							= "GEPC02LA";		//대형옥내 입고 (봉강출하상)
	public static final String SCH_CD_GEYD01MA							= "GEYD01MA";		//대형옥내 동내이적
	public static final String SCH_CD_GETR41UA							= "GETR41UA";		//대형옥내 이송출고 (남4문)
	public static final String SCH_CD_GETR41UB							= "GETR41UB";		//빌렛정정 이송출고 (남4문)
	public static final String SCH_CD_GETR41LB							= "GETR41LB";		//빌렛정정 이송입고 (남4문)
	public static final String SCH_CD_GEYD01MB							= "GEYD01MB";		//빌렛정정 동내이적
	public static final String SCH_CD_GETR21LB							= "GETR21LB";		//빌렛정정 이송입고 (남2문)
	public static final String SCH_CD_GETR21UB							= "GETR21UB";		//빌렛정정 이송출고 (남2문)
	public static final String SCH_CD_GETF01LB							= "GETF01LB";		//빌렛정정 빌렛정정보급
	public static final String SCH_CD_GETF01MB							= "GETF01MB";		//빌렛정정 빌렛정정보급추출
	public static final String SCH_CD_GEPC01UB							= "GEPC01UB";		//빌렛정정 빌렛정정추출
	public static final String SCH_CD_GETR11LB							= "GETR11LB";		//빌렛정정 이송입고 (남1문)
	public static final String SCH_CD_GETR11UB							= "GETR11UB";		//빌렛정정 이송출고 (남1문)
	public static final String SCH_CD_GETF04LB							= "GETF04LB";		//빌렛정정 빌렛소형보급
	public static final String SCH_CD_GETF04MB							= "GETF04MB";		//빌렛정정 빌렛소형보급추출
	public static final String SCH_CD_GETF02LB							= "GETF02LB";		//빌렛정정 프레스교정보급
	public static final String SCH_CD_GETF02MB							= "GETF02MB";		//빌렛정정 프레스교정보급추출
	public static final String SCH_CD_GEPC03UB							= "GEPC03UB";		//빌렛정정 프레스교정추출
	public static final String SCH_CD_GETR42UB							= "GETR42UB";		//빌렛정정 이송출고 (밴드쏘)
	public static final String SCH_CD_GETF03LB							= "GETF03LB";		//빌렛정정 하부비파괴추출테이블보급
	public static final String SCH_CD_GEPC04UB							= "GEPC04UB";		//빌렛정정 글라인더크래들추출
	public static final String SCH_CD_GFYD01MM							= "GFYD01MM";		//대형옥외 동내이적
	public static final String SCH_CD_GFTR01LM							= "GFTR01LM";		//대형옥외 이송입고
	public static final String SCH_CD_GFTR01UM							= "GFTR01UM";		//대형옥외 이송출고
	public static final String SCH_CD_GFBS01LM							= "GFBS01LM";		//대형옥외 밴드쏘보급
	public static final String SCH_CD_GFBS01UM							= "GFBS01UM";		//대형옥외 밴드쏘추출
	public static final String SCH_CD_GFRP01LM							= "GFRP01LM";		//대형옥외 재포장보급
	public static final String SCH_CD_GFRP01UM							= "GFRP01UM";		//대형옥외 재포장추출
	
	//설비ID
	public static final String EQP_B_GECRE2								= "GECRE2";			//대형옥내 SLB12호기 (빌렛정정)
	public static final String EQP_B_GECRE4								= "GECRE4";			//대형옥내 SLB14호기 (빌렛정정)
	public static final String EQP_A_GECRE5								= "GECRE5";			//대형옥내 SLB15호기 (대형옥내)
	public static final String EQP_F_GECRF1								= "GECRF1";			//대형옥외 SLB12호기 (대형옥외)
	public static final String EQP_A_GEPC42								= "GEPC42";			//대형옥내 빌렛각강출하상
	public static final String EQP_A_GEPC41								= "GEPC41";			//대형옥내 빌렛각강출하상
	public static final String EQP_A_GEPC32								= "GEPC32";			//대형옥내 빌렛각강출하상
	public static final String EQP_A_GEPC31								= "GEPC31";			//대형옥내 빌렛각강출하상
	public static final String EQP_A_GEPC51								= "GEPC51";			//대형옥내 봉강출하상
	public static final String EQP_B_GETR11								= "GETR11";			//빌렛정정 차량포인터
	public static final String EQP_B_GETR21								= "GETR21";			//빌렛정정 차량포인터
	public static final String EQP_A_GETR41								= "GETR41";			//대형옥내 차량포인터
	public static final String EQP_B_GETR42								= "GETR42";			//빌렛정정 차량포인터
	public static final String EQP_F_GFTR11								= "GFTR11";			//대형옥외 차량포인터
	public static final String EQP_B_GETF01								= "GETF01";			//빌렛정정 빌렛정정보급대
	public static final String EQP_B_GETF02								= "GETF02";			//빌렛정정 프레스교정기보급대
	public static final String EQP_B_GETF03								= "GETF03";			//빌렛정정 하부비파괴추출테이블
	public static final String EQP_B_GETF04								= "GETF04";			//대형옥내 소형적재테이블
	public static final String EQP_F_GFBS01								= "GFBS01";			//대형옥외 밴드쏘 보급대
	public static final String EQP_F_GFRP01								= "GFRP01";			//대형옥외 재포장대
	public static final String EQP_B_GEPC01								= "GEPC01";			//빌렛정정 빌렛정정추출대-상부제품컨베어
	public static final String EQP_B_GEPC02								= "GEPC02";			//빌렛정정 빌렛정정추출대-하부제품컨베어
	public static final String EQP_B_GEPC03								= "GEPC03";			//빌렛정정 프레스교정기추출대
	public static final String EQP_B_GEPC04								= "GEPC04";			//빌렛정정 글라인더 크래들

	
//	/*++++++++++++++++++++++++++++++++++++++++++++++++++++
//	/* 			특수강 소형봉강 야드 상수 정의 시작
//	++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	//야드스케줄코드 정의
	public static final String SCH_CD_GDTF11MM							= "GDTF11MM";		//소형봉강 정렬대이적 (#11)
	public static final String SCH_CD_GDTF12MM							= "GDTF12MM";		//소형봉강 정렬대이적 (#12)
    public static final String SCH_CD_GDTF21MM							= "GDTF21MM";		//소형봉강 정렬대이적 (#21)
    public static final String SCH_CD_GDTF22MM							= "GDTF22MM";		//소형봉강 정렬대이적 (#22)
    public static final String SCH_CD_GDPC11LM							= "GDPC11LM";		//소형봉강 추출대입고 (#1)
    public static final String SCH_CD_GDPC21LM							= "GDPC21LM";		//소형봉강 추출대입고 (#2)
    public static final String SCH_CD_GDYD01MM							= "GDYD01MM";		//소형봉강 동내이적
    public static final String SCH_CD_GDTR11UM							= "GDTR11UM";		//소형봉강 이송출고 (#1)
    public static final String SCH_CD_GDTR21UM							= "GDTR21UM";		//소형봉강 이송출고 (#2)
    public static final String SCH_CD_GDTR11LM							= "GDTR11LM";		//소형봉강 이송입고 (#1)
    public static final String SCH_CD_GDTR21LM							= "GDTR21LM";		//소형봉강 이송입고 (#2)
    public static final String SCH_CD_GDTC04UM							= "GDTC04UM";		//소형봉강 대차상차 (#4)
    public static final String SCH_CD_GDTC05UM							= "GDTC05UM";		//소형봉강 대차상차 (#5)
    public static final String SCH_CD_GDTC06UM							= "GDTC06UM";		//소형봉강 대차상차 (#6)
    public static final String SCH_CD_GDTC04LM							= "GDTC04LM";		//소형봉강 대차하차 (#4)
    public static final String SCH_CD_GDTC05LM							= "GDTC05LM";		//소형봉강 대차하차 (#5)
    public static final String SCH_CD_GDTC06LM							= "GDTC06LM";		//소형봉강 대차하차 (#6)
	
	//설비ID
	public static final String EQP_D_GDCRD7								= "GDCRD7";			//SSB17호기
	public static final String EQP_D_GDCRD8								= "GDCRD8";			//SSB18호기
	public static final String EQP_D_GDTF11								= "GDTF11";			//정렬대 #11 (1Line 1Chain)
	public static final String EQP_D_GDTF12								= "GDTF12";			//정렬대 #12 (1Line 2Chain)
	public static final String EQP_D_GDTF21								= "GDTF21";			//정렬대 #21 (2Line 1Chain)
	public static final String EQP_D_GDTF22								= "GDTF22";			//정렬대 #22 (2Line 2Chain)
	public static final String EQP_D_GDPC11								= "GDPC11";			//소형봉강추출대 #1
	public static final String EQP_D_GDPC21								= "GDPC21";			//소형봉강추출대 #2
	public static final String EQP_D_GDTR11								= "GDTR11";			//차량포인트 #1 통로
	public static final String EQP_D_GDTR21								= "GDTR21";			//차량포인트 #2 통로
	public static final String EQP_D_GDTC04								= "GDTC04";			//대차 #4 (봉강정정)
	public static final String EQP_D_GDTC05								= "GDTC05";			//대차 #5 (봉강정정)
	public static final String EQP_D_GDTC06								= "GDTC06";			//대차 #6 (열처리대차)

	
//	/*++++++++++++++++++++++++++++++++++++++++++++++++++++
//	/* 			C연주 슬라브 야드 상수 정의 시작
//	++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	//C연주슬라브야드 대차 적재중량
//	public static final int TCAR_WT_CAPA								= 150000;			//적치가능 중량 120TON
//	
//	//야드스케줄코드 정의 - 스케줄코드 추가 예정
//	public static final String SCH_CD_A_REFUR_SUP1						= "AAPU04UM";		//A동 PU04 장입(C열연가열로보급스케줄)
//	public static final String SCH_CD_A_REFUR_SUP2						= "ACPU02UM";		//C동 PU02 장입(C열연가열로보급스케줄)
//	public static final String SCH_CD_A_REFUR_SUP6_LEFT					= "ABPU06UL";		//B동 PU06 장입(C열연가열로보급스케줄) - 01, 02, 03베드
//	public static final String SCH_CD_A_REFUR_SUP4_LEFT					= "AAPU04UL";		//A동 PU04 장입(C열연가열로보급스케줄) - 01, 02, 03베드
//	public static final String SCH_CD_A_REFUR_SUP2_LEFT					= "ACPU02UL";		//C동 PU02 장입(C열연가열로보급스케줄) - 01, 02, 03베드
//	public static final String SCH_CD_A_REFUR_SUP6_RIGHT				= "ABPU06UR";		//B동 PU06 장입(C열연가열로보급스케줄) - 04, 05, 06베드
//	public static final String SCH_CD_A_REFUR_SUP4_RIGHT				= "AAPU04UR";		//A동 PU04 장입(C열연가열로보급스케줄) - 04, 05, 06베드
//	public static final String SCH_CD_A_REFUR_SUP2_RIGHT				= "ACPU02UR";		//C동 PU02 장입(C열연가열로보급스케줄) - 04, 05, 06베드
//	public static final String SCH_CD_A_OHC_TAKE_OUT2					= "ADRT02LM";		//#2 Machine OHC TAKE-OUT 스케줄코드
//	public static final String SCH_CD_A_OHC_TAKE_OUT1					= "ADRT01LM";		//#1 Machine OHC TAKE-OUT 스케줄코드
//	public static final String SCH_CD_A_OHC_TAKE_OUT3					= "ADRT03LM";		//#3 Machine OHC TAKE-OUT 스케줄코드
//	public static final String SCH_CD_A_OHC_TAKE_OUT4					= "ADDR01LM";		
//	public static final String SCH_CD_A_OHC_TAKE_OUT5					= "ACER01LM";		
//	public static final String SCH_CD_A_OHC_TAKE_OUT6					= "AAAR01LM";		
//	public static final String SCH_CD_A_OHC_TAKE_OUT7					= "ADRT04LM";		//#4 Machine OHC TAKE-OUT 스케줄코드 --추가
//	public static final String SCH_CD_A_OHC_TAKE_OUT8					= "ADRT05LM";		//#5 Machine OHC TAKE-OUT 스케줄코드 --추가
//	public static final String SCH_CD_A_OHC_TAKE_IN1					= "ADRT01UM";		//#1 Machine OHC TAKE-IN 스케줄코드
//	public static final String SCH_CD_A_OHC_TAKE_IN2					= "ADRT02UM";		//#2 Machine OHC TAKE-IN 스케줄코드
//	public static final String SCH_CD_A_OHC_TAKE_IN3					= "ADRT03UM";		//#3 Machine OHC TAKE-IN 스케줄코드
//	public static final String SCH_CD_A_OHC_TAKE_IN4					= "ADDR01UM";		
//	public static final String SCH_CD_A_OHC_TAKE_IN5					= "ACER01UM";		
//	public static final String SCH_CD_A_OHC_TAKE_IN6					= "AAAR01UM";		
//	public static final String SCH_CD_A_OHC_TAKE_IN7					= "ADRT07UM";		//#7 Machine OHC TAKE-IN 스케줄코드 --추가
//	public static final String SCH_CD_A_OHC_TAKE_IN8					= "ADRT08UM";		//#8 Machine OHC TAKE-IN 스케줄코드 --추가
//	public static final String SCH_CD_A_DP_CARRY_IN_01					= "ACDP01UM";		//#1 DEPILER CARRAY-IN 스케줄코드
//	public static final String SCH_CD_A_DP_CARRY_IN_02					= "AADP02UM";		//#2 DEPILER CARRAY-IN 스케줄코드
//	public static final String SCH_CD_A_DP_CARRY_IN_03					= "ABDP03UM";		//C연주 B동 #2 Scarfer Depiler03 CARRAY-IN 스케줄코드 --추가
//	public static final String SCH_CD_A_PS_CARRY_OUT_01					= "AAPS01LM";		//A동 재열재인출
//	public static final String SCH_CD_A_PI_CARRY_OUT_01					= "ACPI01LM";		//C동 Piler01 입고
//	public static final String SCH_CD_A_PI_CARRY_OUT_03					= "ACPI03LM";		//C동 Piler03 입고
//	public static final String SCH_CD_A_PI_CARRY_OUT_04					= "ADPI04LM";		//C연주 D동 #4 M/C Piler04 입고 --추가
//	public static final String SCH_CD_A_PI_CARRY_OUT_05					= "ACPI05LM";		//C연주 C동 #5 M/C Piler05 입고 --추가
//	public static final String SCH_CD_A_PU_CARRY_OUT_04					= "AAPU04LM";		//A동 PU04 수입
//	public static final String SCH_CD_A_PU_CARRY_OUT_02					= "ACPU02LM";		//C동 PU02 수입
//	public static final String SCH_CD_A_PU_CARRY_OUT_04_LEFT			= "AAPU04LL";		//A동 PU04 수입 - 01, 02, 03베드
//	public static final String SCH_CD_A_PU_CARRY_OUT_02_LEFT			= "ACPU02LL";		//C동 PU02 수입 - 01, 02, 03베드
//	public static final String SCH_CD_A_PU_CARRY_OUT_04_RIGHT			= "AAPU04LR";		//A동 PU04 수입 - 04, 05, 06베드
//	public static final String SCH_CD_A_PU_CARRY_OUT_02_RIGHT			= "ACPU02LR";		//C동 PU02 수입 - 04, 05, 06베드
//	public static final String SCH_CD_A_PU_CARRY_OUT_01					= "ADPU01LM";		//D동 PU01 입고
//	public static final String SCH_CD_AD_PU_CARRY_OUT_02				= "ADPU02LM";		//D동 PU02 입고
//	public static final String SCH_CD_A_PU_CARRY_OUT_03					= "ADPU03LM";		//D동 PU03 입고
//	public static final String SCH_CD_A_PU_CARRY_OUT_07					= "ACPUP7LM";		//C연주 C동 #4 M/C Pickup07 입고 --추가
//	public static final String SCH_CD_A_PU_CARRY_OUT_08					= "ADPUP8LM";		//C연주 D동 #5 M/C Pickup08 입고 --추가
//	//20090911 김진욱 추가 핸드스카핑입고
//	public static final String SCH_CD_A_SB_CARRY_OUT_01					= "ABSB01LM";		//B동 SB01 입고
//	
//	//설비ID
//	public static final String EQP_A_PU1								= "ADPUP1";			//C연주 D동 #1 M/C Pickup01 (불출 Bed)
//	public static final String EQP_A_PU2								= "ACPUP2";			//C연주 C동 #2 M/C Pickup02 (불출 및 가열로보급[수입] Bed)
//	public static final String EQP_A_PU4								= "AAPUP4";			//C연주 A동 #2 M/C Pickup04 (불출 및 가열로보급[수입] Bed)
//	public static final String EQP_A_PU6								= "ABPUP6";			//C연주 B동 #2 M/C Pickup06 (불출 및 가열로보급[수입] Bed)
//	public static final String EQP_A_PU3								= "ADPUP3";			//C연주 D동 #3 M/C Pickup03 (불출 Bed)
//	public static final String EQP_A_PU7								= "ACPUP7";			//C연주 C동 #4 M/C Pickup07 (불출 Bed) 
//	public static final String EQP_A_PU8								= "ADPUP8";			//C연주 D동 #5 M/C Pickup08 (불출 Bed)
//	//추후 검토후 삭제 (없는 설비 임)
//	public static final String EQP_AD_PU2								= "ADPUP2";			//#2 Machine D동 불출PICKUP베드
//
//	public static final String EQP_A_PI_01								= "ACPI01";			//C연주 C동 #1 M/C Piler01
//	public static final String EQP_A_PI_03								= "ACPI03";			//C연주 C동 #3 M/C Piler03
//	public static final String EQP_A_PI_04								= "ADPI04";			//C연주 D동 #4 M/C Piler04 
//	public static final String EQP_A_PI_05								= "ACPI05";			//C연주 C동 #5 M/C Piler05 
//	
//	public static final String EQP_A_PU5								= "ABPUP5";			//C연주 B동 #1 Scarfer Pickup05
//	public static final String EQP_A_PUB								= "AAPUPB";			//C연주 A동 #2 Scarfer Pickup11 
//	public static final String EQP_A_PU9								= "AAPUP9";			//C연주 A동 #2 2차절단 Pickup09 
//	public static final String EQP_A_PUA								= "AAPUPA";			//C연주 A동 #3 2차절단 Pickup10 
//	public static final String EQP_A_DP_01								= "ACDP01";			//C연주 C동 #1 Scarfer Depiler01
//	public static final String EQP_A_DP_03								= "ABDP03";			//C연주 B동 #2 Scarfer Depiler03
//	public static final String EQP_A_DP_02								= "AADP02";			//C연주 A동 #1 2차절단 Depiler02
//	
//	public static final String EQP_A_SB1								= "ABSB01";			//C연주 B동 Hand Scarfing
//	public static final String EQP_A_PS_01								= "AAPS01";			//C연주 A동 재열재수입
//	
//	public static final String EQP_A_RT_01								= "ADRT01";			//#1 Machine Roller Table
//	public static final String EQP_A_RT_02								= "ADRT02";			//#2 Machine Roller Table
//	public static final String EQP_A_RT_03								= "ADRT03";			//#3 Machine Roller Table
//	public static final String EQP_A_RT_04								= "ADDR01";			
//	public static final String EQP_A_RT_05								= "ACER01";			
//	public static final String EQP_A_RT_06								= "AAAR01";			
//	public static final String EQP_A_RT_07								= "ADRT04";			//#4 Machine Roller Table
//	public static final String EQP_A_RT_08								= "ADRT05";			//#5 Machine Roller Table
//
//	
//	//크레인 작업허용오차
//	public static final int C_SLAB_CRANE_GAP_X							= 100;					//주행 오차값은 100mm
//	public static final int C_SLAB_CRANE_GAP_Y							= 200;					//횡행 오차값은 200mm
//	public static final int C_SLAB_CRANE_GAP_Z  						= 50;					//주행 오차값은 100mm
//	
//	//크레인 작업허용오차
//	public static final int C_COIL_CRANE_GAP_X							= 100;					//주행 오차값은 100mm
//	public static final int C_COIL_CRANE_GAP_Y							= 200;					//횡행 오차값은 200mm
//	public static final int C_COIL_CRANE_GAP_Z  						= 50;					//주행 오차값은 100mm
//		
//	//대차 상/하차스케줄이거나 스카핑인출스케줄 시 사용되는 주행/횡행오차 값
//	public static final int C_SLAB_CRANE_GAP_X1							= 500;					//주행 오차값은 500mm
//	public static final int C_SLAB_CRANE_GAP_Y1							= 1000;					//횡행 오차값은 1000mm
//	
//	//PI 및 DP 설비 인출/보급시 사용되는 주행/횡행오차 값
//	public static final int C_SLAB_CRANE_GAP_X3							= 200;					//주행 오차값은 500mm
//	public static final int C_SLAB_CRANE_GAP_Y3							= 550;					//횡행 오차값은 1000mm
//		
//	//대차 상/하차스케줄이거나 스카핑인출스케줄 시 사용되는 주행/횡행오차 값
//	public static final int C_COIL_CRANE_GAP_X1							= 500;					//주행 오차값은 500mm
//	public static final int C_COIL_CRANE_GAP_Y1							= 1000;					//횡행 오차값은 1000mm
//	
//	//스카핑인출/보그 스케줄 시 사용되는 주행/횡행오차 값
//	public static final int C_SLAB_CRANE_GAP_X2							= 50;					//주행 오차값은 50mm
//	public static final int C_SLAB_CRANE_GAP_Y2							= 50;					//횡행 오차값은 50mm
//	
//	//대차
//	public static final String YD_TCAR_MOVE_GP_LEAVE					= "S";				//출발
//	public static final String YD_TCAR_MOVE_GP_ARRIVE					= "E";				//도착
//	public static final String YD_TCAR_MOVE_GP_MOVE						= "M";				//이동
//	
//	/*++++++++++++++++++++++++++++++++++++++++++++++++++++
//	/* 			C연주 슬라브 야드 상수 정의 끝
//	++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	
//	/*++++++++++++++++++++++++++++++++++++++++++++++++++++
//	/* 			A후판 슬라브 야드 상수 정의 시작
//	++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	//야드스케줄코드 정의 - 스케줄코드 추가 예정
//	public static final String SCH_CD_D_REFUR_SUP1						= "DAPU01UM";		//A동 DAPU01 장입(A후판가열로보급스케줄)
//	public static final String SCH_CD_D_PU_CARRY_OUT_02					= "DAPU02LM";		//A동 DAPU02 입고(A후판입고스케줄)
//	public static final String SCH_CD_D_DAYD99MR						= "DAYD99MR";		//A동 DAPU01 장입준비(A후판가열로보급준비스케줄)
//	
//	//설비ID
//	public static final String EQP_D_PU1								= "DAPU01";			//A동 가열로보급[수입]PICKUP베드
//	public static final String EQP_D_PU2								= "DAPU02";			//A동 입고PICKUP베드
//	
//	//가열로장입예정일련번호 비교 시 반환값 정의
//	public static final String REFUR_CHG_PLN_SERNO_SMALL				= "SERNO_SMALL";	//일련번호가 작다.
//	public static final String REFUR_CHG_PLN_SERNO_EQUAL				= "SERNO_EQUAL";	//일련번호가 같다.
//	public static final String REFUR_CHG_PLN_SERNO_BIG					= "SERNO_BIG";		//일련번호가 크다.
//	
//	//크레인 작업허용오차
//	public static final int A_PLATE_SLAB_CRANE_GAP						= 50;					//주행/횡행 오차값은 50mm
//	/*++++++++++++++++++++++++++++++++++++++++++++++++++++
//	/* 			A후판 슬라브 야드 상수 정의 끝
//	++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	
//	
//	//-------------------------------------------------------------------------------------------------
//	//	후판제품창고야드 상수 정의
//	//-------------------------------------------------------------------------------------------------
//	
//	//후판제품 크레인 작업허용오차
//	public static final int PLATE_CRANE_GAP								= 2000;					//크레인허용오차 2M (2013.02.15 1M-->2M 변경)
//	public static final int PLATE_CRANE_PT_X_GAP						= 3000;					//크레인허용오차 3M
//	public static final int PLATE_CRANE_BRT_GAP							= 2000;					//B-RT위치일 경우의 허용오차2M
//	public static final int PLATE_CRANE_PT_GAP							= 30000;				//차상위치일 경우의 허용오차30M
//	
//	//후판제품 길이구분
//	public static final String YD_MTL_LEN_EXTRA							= "X";
//	public static final String YD_MTL_LEN_LONG							= "L";
//	public static final String YD_MTL_LEN_MIDDLE						= "M";
//	public static final String YD_MTL_LEN_SHORT							= "S";
//	public static final String YD_MTL_LEN_EXTRA_SHORT					= "U";		//초단척
//	
//	//후판제품 폭구분
//	public static final String YD_MTL_WIDTH_WIDE						= "L";
//	public static final String YD_MTL_WIDTH_MIDDLE						= "M";
//	public static final String YD_MTL_WIDTH_SMALL						= "S";
//	
//	//후판 제품야드에서 사용되는 광폭, 중폭, 소폭 값정의 
//	public static final  int STKBED_W_GP_L   							= 4800;		// 광폭			
//	public static final  int STKBED_W_GP_M   							= 3450;		// 중폭
//	public static final  int STKBED_W_GP_S   							= 2100;		// 소폭
//	
//	//후판 제품야드에서 광폭/ 중폭/소폭시 높이값
//	
//	public static final  int STKBED_H_GP_L   							= 2000;		// 광폭			
//	public static final  int STKBED_H_GP_M   							= 2000;		// 중폭
//	public static final  int STKBED_H_GP_S   							= 1500;		// 소폭
//	
//	//Plate공통테이블에 등록된 입고대상재의 현저장위치 기본값
//	public static final String KARTPA									= "KARTPA";				//On Line
//	public static final String KBRTPA									= "KBRTPA";				//Off Line
//	public static final String KCRTPA									= "KCRTPA";				//On Line (NO2 DS)
//	
//	public static final String TARTPA									= "TARTPA";				//2후판 A-RT (NO2 DS)
//	public static final String TBRTPA									= "TBRTPA";				//2후판 B-RT (NO1 DS)
//	public static final String TCRTPA									= "TCRTPA";				//2후판 C-RT (CPL)
//	
//	public static final String YD_STR_LOC_CURR							= "CUR_LOC";
//	public static final String YD_STR_LOC_HIS1							= "HIS1_LOC";
//	public static final String YD_STR_LOC_HIS2							= "HIS2_LOC";
//	
//	
//	//검색 방향을 정의하는 상수
//	public static final String SCAN_DIR_RT2PT							= "R2P";				//Roller Table ==> 차량정지위치 방향
//	public static final String SCAN_DIR_PT2RT							= "P2R";				//차량정지위치  ==> Roller Table 방향
//	
//	//검색대상 스판을 정의하는 상수
//	public static final String SPAN_ORDER_1234							= "1234";				//01,02스판검색 후 03,04스판 검색
//	public static final String SPAN_ORDER_3412							= "3412";				//03,04스판검색 후 01,02스판 검색
//	public static final String SPAN_ORDER_12							= "12";					//01,02스판 검색
//	public static final String SPAN_ORDER_34							= "34";					//03,04스판검색
//	
//	public static final String SPAN_ORDER_NEW_01						= "01";					
//	public static final String SPAN_ORDER_NEW_02						= "02";					
//	public static final String SPAN_ORDER_NEW_03						= "03";					
//	public static final String SPAN_ORDER_NEW_04						= "04";					
//	public static final String SPAN_ORDER_NEW_05						= "05";
//	public static final String SPAN_ORDER_NEW_06						= "06";
//	public static final String SPAN_ORDER_NEW_07						= "07";
//	public static final String SPAN_ORDER_NEW_TP						= "07";
//	
//	public static final String ORDER_BY_ASC								= "ASC";				//오름차순 정렬
//	public static final String ORDER_BY_DESC							= "DESC";				//내림차순 정렬
//	
//	//--------------------------------------------------------------------------
//	//	Online Book-Out 코드 정의
//	//--------------------------------------------------------------------------
//	public static final String TEMPSTK_LOC_A_BOOK_OUT_01				= "55991";				//A동 가적장 Book-Out코드[KAGJ0106]
//	public static final String TEMPSTK_LOC_A_BOOK_OUT_02				= "55992";				//A동 가적장 Book-Out코드[KAGJ0116]
//	
//	public static final String ONLINE_TF_LOC_A_BOOK_OUT_01				= "56106";				//ONLINE A동 Transfer Book-Out코드[KATF0206]
//	public static final String ONLINE_TF_LOC_A_BOOK_OUT_02				= "56116";				//ONLINE A동 Transfer Book-Out코드[KATF0216]
//	
//	public static final String ONLINE_TF_LOC_B_BOOK_OUT_01				= "56206";				//ONLINE B동 Transfer Book-Out코드[KBTF0206]
//	public static final String ONLINE_TF_LOC_B_BOOK_OUT_02				= "56216";				//ONLINE B동 Transfer Book-Out코드[KBTF0216]
//	
//	//--------------------------------------------------------------------------
//	//	Offline Book-Out 코드 정의
//	//--------------------------------------------------------------------------
//	public static final String OFFLINE_LOC_A_BOOK_OUT_01				= "58010";				//OFFLINE A동 Book-Out코드[KARTRB10]
//	public static final String OFFLINE_LOC_A_BOOK_OUT_02				= "58020";				//OFFLINE A동 Book-Out코드[KARTRB20]
//	
//	public static final String OFFLINE_LOC_B_BOOK_OUT_01				= "58030";				//OFFLINE B동 Book-Out코드[KBRTRB30]
//	public static final String OFFLINE_LOC_B_BOOK_OUT_02				= "58040";				//OFFLINE B동 Book-Out코드[KBRTRB40]
//	//-------------------------------------------------------------------------------------------------
//	
//	
//	
//	//-------------------------------------------------------------------------------------------------
//	// 			TC_CODE 상수 정의 시작
//	//-------------------------------------------------------------------------------------------------
//	
//	/*
//	 * 야드내부 TC CODE
//	 */
//	
//	public final static String YDYDJ701									= "YDYDJ701";		//전문전송버퍼 BUFFER
//	public final static String YDYDJ702									= "YDYDJ702";		//로그메세지처리
//	public final static String YDYDJ901									= "YDYDJ901";		//플렉스 버퍼
//	
//	public final static String YDPRJ003									= "YDPRJ003";		//후판조업L3
//	
//	/**
//	 * 출하인터페이스 TC_CODE
//	 */
//	public final static String DMYDR002	="DMYDR002";   //코일제품보류확정              
//	public final static String DMYDR004	="DMYDR004";   //외판슬라브출하지시대기        
//	public final static String DMYDR005	="DMYDR005";   //코일제품출하지시대기          
//	public final static String DMYDR008	="DMYDR008";   //코일제품반납대기              
//	public final static String DMYDR011	="DMYDR011";   //코일제품고간이송지시          
//	public final static String DMYDR013	="DMYDR013";   //외판슬라브목전(주문자변경)    
//	public final static String DMYDR014	="DMYDR014";   //코일제품목전                  
//	public final static String DMYDR016	="DMYDR016";   //외판슬라브운송대기            
//	public final static String DMYDR020	="DMYDR020";   //코일제품운송지시              
//	public final static String DMYDR023	="DMYDR023";   //코일제품상차지시              
//	public final static String DMYDR026	="DMYDR026";   //외판슬라브보관지시            
//	public final static String DMYDR027	="DMYDR027";   //코일제품보관지시              
//	public final static String DMYDR029	="DMYDR029";   //외판슬라브출하완료            
//	public final static String DMYDR030	="DMYDR030";   //코일제품출하완료              
//	public final static String DMYDR032	="DMYDR032";   //외판슬라브반품                
//	public final static String DMYDR033	="DMYDR033";   //코일제품반품                  
//	public final static String DMYDR035	="DMYDR035";   //외판슬라브출하차량도착실적  
//	public final static String DMYDR036	="DMYDR036";   //코일제품출하차량도착실적 
//	public final static String DMYDR037	="DMYDR037";   //코일임가공차량도착실적 
//	public final static String DMYDR039	="DMYDR039";   //외판슬라브출하차량출발실적 
//	public final static String DMYDR040	="DMYDR040";   //코일제품출하차량출발실적 
//	public final static String DMYDR041	="DMYDR041";   //코일임가공차량출발실적 	
//	public final static String DMYDR012	="DMYDR012";	//후판제품고간이송지시
//	public final static String DMYDR015	="DMYDR015";	//후판제품목전
//	public final static String DMYDR009	="DMYDR009";	//후판제품반납대기
//	public final static String DMYDR028	="DMYDR028";	//후판제품보관지시
//	public final static String DMYDR003	="DMYDR003";	//후판제품보류확정
//	public final static String DMYDR024	="DMYDR024";	//후판제품상차지시(삭제)
//	public final static String DMYDR021	="DMYDR021";	//후판제품운송상차지시
//	public final static String DMYDR046	="DMYDR046";	//후판제품선별LOT편성정보
//	public final static String DMYDR022	="DMYDR022";	//외판슬라브운송상차지시
//	public final static String DMYDR025	="DMYDR025";	//임가공이송상차지시
//	public final static String DMYDR018	="DMYDR018";	//후판제품운송지시대기
//	public final static String DMYDR031	="DMYDR031";	//후판제품출하완료
//	public final static String DMYDR044	="DMYDR044";	//후판제품목적지변경
//	public final static String DMYDR045	="DMYDR045";	//코일제품사외이송
//	public final static String DMYDR060	="DMYDR060";	//코일제품운송상차지시
//	public final static String DMYDR070	="DMYDR070";	//코일이송상차대기장도착PDA
//	public final static String DMYDR073	="DMYDR073";	//코일이송하차대기장도착PDA
//
//	//-------------------------------------------------------------------------------------------------
//	
//	// HCR구분
//	public static final String HCR_GP_CCR                               = "CCR";            // CCR
//	public static final String HCR_GP_HDR                               = "HDR";            // HDR
//	public static final String HCR_GP_HCR                               = "HCR";            // HCR
//	public static final String HCR_GP_WCR                               = "WCR";            // WCR
//	
//	// 재료외형구분
//	public static final String STL_APPEAR_PREPARE                       = "A";              // 예정 재료
//	public static final String STL_APPEAR_MSLAB                         = "B";              // 주편
//	public static final String STL_APPEAR_SLAB                          = "C";              // SLAB
//	public static final String STL_APPEAR_SLABSIZING                    = "D";              // SLAB(SIZING재)
//	public static final String STL_APPEAR_HOTROLLCOIL                   = "E";              // 열연코일
//	public static final String STL_APPEAR_PARENTPLATE                   = "F";              // 날판
//	public static final String STL_APPEAR_PLATE                         = "G";              // PLATE
//	public static final String STL_APPEAR_PRODUCT                       = "Y";              // 제품
//	
//	// SLAB지시행선코드
//	public static final String SLAB_ORD_RT_CD_HOTROLL_A                 = "HA";             // A열연
//	public static final String SLAB_ORD_RT_CD_HOTROLL_B                 = "HB";             // B열연
//	public static final String SLAB_ORD_RT_CD_HOTROLL_C                 = "HC";             // C열연
//	public static final String SLAB_ORD_RT_CD_SELL_SLAB                 = "MS";             // 판매SLAB
//	public static final String SLAB_ORD_RT_CD_PLATE_A                   = "PA";             // A후판
//	public static final String SLAB_ORD_RT_CD_PLATE_B                   = "PB";             // B후판
//	
//	// 주문여재구분
//	public static final String ORD_YEOJAE_GP_ORD                        = "1";              // 주문재
//	public static final String ORD_YEOJAE_GP_YEOJAE                     = "2";              // 여재
//	
//	//통합야드에서 사용되는 목표행선 정의
//	public static final Hashtable S_YD_AIM_RT							= new Hashtable();
//	
//	static {
//		/*
//		 * 통합야드의 목표행선 설정 시작
//		 */
//		S_YD_AIM_RT.put("01", "슬라브구입등록");
//		S_YD_AIM_RT.put("11", "슬라브구입확정");
//		S_YD_AIM_RT.put("Y1", "재공충당대기(B열연압연)");
//		S_YD_AIM_RT.put("Y2", "재공충당대기(C열연압연)");
//		S_YD_AIM_RT.put("Y3", "재공충당대기(A후판주편정정)");
//		S_YD_AIM_RT.put("Y4", "재공충당대기(A후판압연)");
//		S_YD_AIM_RT.put("E2", "재공이송작업대기(B열연스카핑)");
//		S_YD_AIM_RT.put("E3", "재공이송작업대기(B열연Non스카핑)");
//		S_YD_AIM_RT.put("E4", "재공이송작업대기(C열연스카핑)");
//		S_YD_AIM_RT.put("E5", "재공이송작업대기(C열연Non스카핑)");
//		S_YD_AIM_RT.put("E6", "재공이송작업대기(A후판주편스카핑)");
//		S_YD_AIM_RT.put("E7", "재공이송작업대기(A후판주편정정)");
//		S_YD_AIM_RT.put("E8", "재공이송작업대기(A후판슬라브)");
//		S_YD_AIM_RT.put("E9", "재공이송작업대기(A후판슬라브Sizing)");
//		S_YD_AIM_RT.put("J1", "반납대기(외판슬라브)");
//		S_YD_AIM_RT.put("K1", "출하지시대기(외판슬라브)");
//		S_YD_AIM_RT.put("N1", "운송지시대기(외판슬라브)");
//		S_YD_AIM_RT.put("L1", "운송대기(외판슬라브)");
//		S_YD_AIM_RT.put("L4", "상차대기(외판슬라브)");
//		S_YD_AIM_RT.put("M1", "출하완료(외판슬라브)");
//		S_YD_AIM_RT.put("Z1", "제품충당대기(외판슬라브)");
//		S_YD_AIM_RT.put("X1", "경매대상선정(외판슬라브)");
//		S_YD_AIM_RT.put("O4", "고간이송(통합야드)");
//		/*
//		 * 통합야드의 목표행선 설정 끝
//		 */
//	}
//	
//	// 적치된 상태를 구분해서 재료를 조회 시 사용되는 구분자
//	public static final String MTL_STAT_C_U_D							= "CUD";
//	public static final String MTL_STAT_C_D								= "CD";
//	public static final String MTL_STAT_C_U								= "CU";
//	public static final String MTL_STAT_C								= "C";
//	public static final String MTL_STAT_U								= "U";
//	public static final String MTL_STAT_D								= "D";
//	
//	//--------------------------------------------------------------------------------------------------------
//	//	TO위치 순위 정의 상수
//	//--------------------------------------------------------------------------------------------------------
//	
//	public static final int TO_LOC_PRIOR_USER							= 1;
//	//C연주슬라브야드/후판슬라브야드/통합야드
//	public static final int TO_LOC_PRIOR_SAME_LOT_CD					= 2;
//	public static final int TO_LOC_PRIOR_EMPTY_BED						= 3;
//	public static final int TO_LOC_PRIOR_MIXED_LOT_CD					= 4;
//	
//	//후판제품창고
//	public static final int TO_LOC_PRIOR_PLATE_SAME_PILING_CD			= 2;
//
////DONG_INSERT	
//	public static final int TO_LOC_PRIOR_PLATE_EMPTY_BED				= 3;
//	public static final int TO_LOC_PRIOR_PLATE_MIXED_LOT_CD				= 4;
//	
//	public static final int TO_LOC_PRIOR_STEP							= 10000;
//	
//	//--------------------------------------------------------------------------------------------------------
//	
//	//준비스케줄의 야드준비작업상태 - YD_PREP_WK_ST
//	public static final String PREP_WK_SEL_WK								= "S";				//선별
//	public static final String PREP_WK_CAR_LD								= "L";				//상차LOT
//	
//	
//	//전문버퍼전송 시 사용되는 실제 TC CODE 항목
//	public static final String BUFFER_TC_CD								= "BUFFER_TC_CD";
//	
//	//TC CODE ID 항목이름
//	public static final String JMS_TC_CD								= "JMS_TC_CD";
//	public static final String MSG_ID									= "MSG_ID";
//	public static final String TC_CODE									= "TC_CODE";
//	
//	//BED 적치가능 유무에 대한 에러코드
//	public static final int YD_BED_ERR_CD_SH_OVER						= 1;				//매수초과(MAX단 초과)
//	public static final int YD_BED_ERR_CD_WT_OVER						= 3;				//중량초과
//	public static final int YD_BED_ERR_CD_H_OVER						= 5;				//높이초과
//	public static final int YD_BED_ERR_CD_SH_WT_OVER					= 4;				//매수/중량초과
//	public static final int YD_BED_ERR_CD_SH_H_OVER						= 6;				//매수/높이초과
//	public static final int YD_BED_ERR_CD_WT_H_OVER						= 8;				//중량/높이초과
//	public static final int YD_BED_ERR_CD_SH_WT_H_OVER					= 9;				//매수/중량/높이초과
//	
//	public static final int YD_BED_STACKABLE							= 10000;			//적치가능 코드
//	
//	
//	// 야드 Event 구분 (C:크레인, Q:설비, E:에러, W:경고, I:정보, Z:기타)
//	// putLogMsg Method Parameter 설정
//	public static final String YD_EVT_CRANE                  = "C";
//	public static final String YD_EVT_EQP                    = "Q";
//	public static final String YD_EVT_ERROR                  = "E";
//	public static final String YD_EVT_WARNING                = "W";
//	public static final String YD_EVT_INFO                   = "I";
//	public static final String YD_EVT_ETC                    = "Z";
//	public static final String YD_EVT_FUN                    = "F";
//	
//	// 야드프로그램유형 (W:화면, S:스케줄, I:인터페이스)
//	public static final String YD_PGMGP_WEB                    = "W";
//	public static final String YD_PGMGP_SCH                    = "S";
//	public static final String YD_PGMGP_INTERFACE              = "I";
//	
//	
//	// 야드 수불 구분
//	
//	public static final String YD_GNT_GP_RCPT                  = "L";
//	public static final String YD_GNT_GP_MVSTK                 = "M";
//	public static final String YD_GNT_GP_ISSUE           	   = "U";
//	
//	// 주작업크레인과 대체작업크레인의 구분자
//	public static final String YD_WRK_CRN						= "WRK_CRN";				//주작업크레인
//	public static final String YD_ALT_CRN						= "ALT_CRN";				//대체작업크레인
	
	// 야드설비구분
	public static final String YD_EQP_GP_TCAR                  = "TC"; //대차
//	public static final String YD_EQP_GP_PALLET                = "PT"; //Pallet
	public static final String YD_EQP_GP_CR                    = "CR"; //CRane (크레인)
	public static final String YD_EQP_GP_PC                    = "PC"; //Product Conveyor
	public static final String YD_EQP_GP_TF                    = "TF"; //TransFer (출하상)
	public static final String YD_EQP_GP_RT                    = "RT"; //RollTable
	public static final String YD_EQP_GP_TR                    = "TR"; //TRailer (트레일러.차량)
	public static final String YD_EQP_GP_BS                    = "BS"; //Band Saw (밴드쏘)
	public static final String YD_EQP_GP_RP                    = "RP"; //RePacking (재포장대)
		
//	//YD_EQP_WRK_STAT - 스케줄 상하차 구분
//	public static final String YD_CRN_SCH_CD_LD					= "L";				//하차
//	public static final String YD_CRN_SCH_CD_UD					= "U";				//상차
//
//	//C열연 대차 CAPA
//	public static final int YD_COIL_TC_WEIGH_MAX				= 75000;			//대차 무게
//	
//	
//	
//	//후판정정야드 운전실구분
//	public static final String YD_PPLATE_LOC_GP_US				= "UST";			
//	public static final String YD_PPLATE_LOC_GP_CS				= "C/S";			
//	public static final String YD_PPLATE_LOC_GP_DSS				= "DSS";			
//	public static final String YD_PPLATE_LOC_GP_DS				= "D/S";			
//	public static final String YD_PPLATE_LOC_GP_HMD				= "열간교정운전실";	
//	public static final String YD_PPLATE_LOC_GP_CMD				= "냉간교정운전실";
//	public static final String YD_PPLATE_LOC_GP_GT				= "정정분기";			
//	public static final String YD_PPLATE_LOC_GP_WT				= "열처리";			
//	public static final String YD_PPLATE_LOC_GP_SB				= "Shot blast";			
//	public static final String YD_PPLATE_LOC_GP_CR				= "검사실";	
//	
//	//후판정정야드 스판별 야드구분
//	public static final String YD_PPLATE_EQP_GP_01				= "극후물";
//	public static final String YD_PPLATE_EQP_GP_02				= "냉각대출측";
//	public static final String YD_PPLATE_EQP_GP_03				= "#1GAS";
//	public static final String YD_PPLATE_EQP_GP_04				= "#1전단";
//	public static final String YD_PPLATE_EQP_GP_05				= "#2전단";
//	public static final String YD_PPLATE_EQP_GP_06				= "#2GAS";
//	public static final String YD_PPLATE_EQP_GP_07				= "극후물GAS";
//	public static final String YD_PPLATE_EQP_GP_08				= "전단GAS";
//	public static final String YD_PPLATE_EQP_GP_09				= "냉간교정야드";
//	public static final String YD_PPLATE_EQP_GP_10				= "보수장";
//	public static final String YD_PPLATE_EQP_GP_11				= "보수장GAS";
//	public static final String YD_PPLATE_EQP_GP_12				= "열처리";
//	public static final String YD_PPLATE_EQP_GP_13				= "ShotBlast";
//	public static final String YD_PPLATE_EQP_GP_14				= "열처리GAS";
//	public static final String YD_PPLATE_EQP_GP_15				= "제품창고#1GAS";
//	public static final String YD_PPLATE_EQP_GP_16				= "제품창고#2GAS";
//	public static final String YD_PPLATE_EQP_GP_17				= "제품창고#3GAS";
//	public static final String YD_PPLATE_EQP_GP_18				= "제품창고#4GAS";
//	
////	후판정정야드 스케쥴코드
//	public static final String SCH_CD_PPLATE_BOOK_OUT					= "PABO01LM";	
//	public static final String SCH_CD_PPLATE_BOOK_IN					= "PABI01UM";
//	public static final String SCH_CD_PPLATE_FROM_TO_LOCCHANGE                   =  "PAYD01MM"; //이적 

//	public static final int YS_EXCPTHIST_IF_ID_LEN					= 10;			//예외처리이력.인터페이스ID 길이
	public static final int YS_EXCPTHIST_IF_DATA_LEN				= 100;			//예외처리이력.인터페이스DATA 길이
	public static final int YS_EXCPTHIST_EXCPT_MSG_LEN				= 80;			//예외처리이력.예외메세지 길이
	
	
}
