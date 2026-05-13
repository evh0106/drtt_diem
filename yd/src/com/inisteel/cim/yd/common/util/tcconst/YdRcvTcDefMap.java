package com.inisteel.cim.yd.common.util.tcconst;

import com.inisteel.cim.yd.common.util.YdConstant;

/**
 * YD 시스템에서 사용 하는 수신 / TC에 대한 정의 
 * @author YHWHman 2009.02.17
 *
 */
public class YdRcvTcDefMap {


	public String[][] strTcMap={


			//저장품관리-저장품제원등록
			 {	"CTYDJ021",	"StockSpecRegFaEJB", "rcvPlMillSpecCmmt", "후판압연사양확정등록" }
			,{	"YDYDJ031",	"StockSpecRegFaEJB", "procYdBayLocPln3GNew",  "후판저장계획메인" ,"T" }
			,{	"YDYDJ032",	"StockSpecRegFaEJB", "procYdBayLocPln3GNewSub",  "후판저장계획서브" ,"T" }
			,{	"CSYDJ001",	"StockSpecRegFaEJB", "rcvCcFsWr", "연주전단실적" }
			,{	"CSYDJ002",	"StockSpecRegFaEJB", "rcvScarfWr",	"SCARFING실적" }
			,{	"CSYDJ003",	"StockSpecRegFaEJB", "rcvCsShearWr",	"연주정정실적" }
			,{	"PRYDJ003",	"StockSpecRegFaEJB", "rcvAPlSlabDivWr", "A후판슬라브분할실적수신"	}
			,{	"HRYDJ003",	"StockSpecRegFaEJB", "rcvCHrMillPrdWr",	"C열연압연생산실적수신"	 }
			,{	"HRYDJ007",	"StockSpecRegFaEJB", "rcvCHrShearWrkWr", "C열연정정작업실적수신" }
			,{	"PRYDJ004",	"StockSpecRegFaEJB", "rcvAPlGdsPrdWr", "A후판제품생산실적수신" }
			,{	"PPYDJ004",	"StockSpecRegFaEJB", "rcvAPlGdsPrdWr", "2후판제품생산실적수신" }
			,{	"PPYDJ008",	"StockSpecRegFaEJB", "rcvPl2AbmtWr", "2후판이상재실적수신" }
			,{	"DMYDR032",	"StockSpecRegFaEJB", "rcvOutplSlabRetngds", "외판슬라브반품" }
			,{	"DMYDR033",	"StockSpecRegFaEJB", "rcvCoilGdsRetngds", "코일제품반품" }
			,{	"DMYDR034",	"StockSpecRegFaEJB", "rcvPlGdsRetngds", "후판제품반품" 	}
			,{	"DMYDR044",	"StockSpecRegFaEJB", "rcvPlGdsDestChgInfo", "후판제품목적지코드변경" 	}
			,{	"QMYDJ001",	"StockSpecRegFaEJB", "rcvBuySlabRegWr", "구입슬라브등록실적" }
			,{	"HRYDJ004",	"StockSpecRegFaEJB", "rcvCHrMillWrkWr",	"C열연압연생산실적수신"	 }

			//저장품관리-행선변경등록
			,{	"CTYDJ012",	"RtModRegFaEJB", "rcvMslabDsCmmtOrd",	"주편재설계확정지시등록"	}
			,{	"CTYDJ013",	"RtModRegFaEJB", "rcvOutplRtChng", "외판행선변경확정" }
			,{	"DMYDR002",	"RtModRegFaEJB", "rcvCoilGdsHoldCommt", "코일제품보류확정" }
			,{	"DMYDR003",	"RtModRegFaEJB", "rcvPlGdsHoldCommt", "후판제품보류확정" }
			,{	"PMYDJ001",	"RtModRegFaEJB", "rcvSlabMatchWr", "슬라브충당실적"	}
			,{	"PTYDJ001",	"RtModRegFaEJB", "rcvCoilMatchWr",	"코일충당실적" }
			,{	"DMYDR008",	"RtModRegFaEJB", "rcvCoilGdsRetnWait", "코일제품반납대기" }
			,{	"DMYDR009",	"RtModRegFaEJB", "rcvPlGdsRetnWait",	"후판제품반납대기" }
			,{	"DMYDR013",	"RtModRegFaEJB", "rcvOutplSlabOrdtrn", "외판슬라브목전" }
			,{	"DMYDR014",	"RtModRegFaEJB", "rcvCoilGdsOrdtrn", "코일제품목전" }
			,{	"DMYDR015",	"RtModRegFaEJB", "rcvPlageGdsOrdtrn", "후판제품목전" }
			,{	"DMYDR004",	"RtModRegFaEJB", "rcvOutplSlabDistOrdWait", "외판슬라브출하지시대기" }
			,{	"DMYDR016",	"RtModRegFaEJB", "rcvOutplSlabTrnOrdWait", "외판슬라브운송지시대기" }
			,{	"DMYDR005",	"RtModRegFaEJB", "rcvCoilGdsDistOrdWait", "코일제품출하지시대기" }
			,{	"DMYDR020",	"RtModRegFaEJB", "rcvCoilGdsTrnOrd", "코일제품운송지시" }
			,{	"DMYDR006",	"RtModRegFaEJB", "rcvPlateGdsDistOrdWait", "후판제품출하지시대기" }
			,{	"DMYDR018",	"RtModRegFaEJB", "rcvPlateGdsTrnOrdWait", "후판제품운송지시대기" }
			,{	"DMYDR021",	"RtModRegFaEJB", "rcvPlateGdsTrnOrd", "후판제품운송상차지시" }
			,{	"DMYDR046",	"RtModRegFaEJB", "rcvPlateGdsTrnOrdLot", "후판제품선별LOT편성정보" }
			,{	"DMYDR048",	"RtModRegFaEJB", "rcvPlateGdsShptrTrnOrdLot", "후판제품해송선별LOT편성정보" }
			,{	"DMYDR049",	"RtModRegFaEJB", "rcvPlateGdsShpudGrpGpInfo", "적하그룹편성정보" }
			,{	"PMYDJ002",	"RtModRegFaEJB", "rcvSlavFtmvOrd", "슬라브이송지시"	}
			,{	"PMYDJ003",	"RtModRegFaEJB", "rcvSlabProgSync", "슬라브진행변경"	}
			,{	"PTYDJ002",	"RtModRegFaEJB", "rcvCoilMatlFtmvOrd", "코일소재이송지시" }
			,{	"PTYDJ003",	"RtModRegFaEJB", "rcvCoilMatlRentprocFtmvOrd", "코일소재임가공이송지시" }
			,{	"PTYDJ004",	"RtModRegFaEJB", "rcvOrdInputHis", "OS주문투입실적" }
			,{	"PTYDJ005",	"RtModRegFaEJB", "rcvOrdInputChg", "OS주문정보변경" }
			,{	"DMYDR011",	"RtModRegFaEJB", "rcvCoilGdsWhFtmvOrd", "코일제품고간이송지시" }
			,{	"DMYDR012",	"RtModRegFaEJB", "rcvPlGdsWhFtmvOrd", "후판제품고간이송지시" }
			,{	"DMYDR026",	"RtModRegFaEJB", "rcvOutplSlabKeepOrd", "외판슬라브보관지시" }
			,{	"DMYDR027",	"RtModRegFaEJB", "rcvCoilGdsKeepOrd", "코일제품보관지시" }
			,{	"DMYDR028",	"RtModRegFaEJB", "rcvPlGdsKeepOrd", "후판제품보관지시" }
			,{	"DMYDR060",	"RtModRegFaEJB", "rcvCoilGdsTrnOrd", "제품운송상차지시" }
			,{	"DMYDR061",	"RtModRegFaEJB", "rcvStandByYdArrive", "대기장도착실적" }
			
			,{	"DMYDR070",	"RtModRegFaEJB", "rcvCoilGdsTrnOrdLdPDA", "코일이송상차대기장도착PDA" }
			,{	"DMYDR071",	"CarLdLotRegFaEJB", "rcvStandByYdArriveLdPDA", "코일이송상차도착PDA" }
			,{	"DMYDR072",	"StockSpecEndFaEJB", "rcvCoilGdsDistCmplLdPDA", "코일이송상차완료PDA" }
			,{	"DMYDR073",	"RtModRegFaEJB", "rcvCoilGdsTrnOrdUdPDA", "코일이송하차대기장도착PDA" }
			,{	"DMYDR074",	"CarLdLotRegFaEJB", "rcvStandByYdArriveUdPDA", "코일이송하차도착PDA" }
			,{	"DMYDR075",	"StockSpecEndFaEJB", "rcvCoilGdsDistCmplUdPDA", "코일이송하차완료PDA" }
			
			,{ "Y5YDL016",	"CarLdLotRegFaEJB", "rcvY5DrvCarPlan", "차량작업 예정정보 요구" } // 150626 hun 크레인무인화
			,{ "Y5YDL017",	"CarLdLotRegFaEJB", "rcvY5CarNotWrk", "상차도 작업 불가" } // 150626 hun 크레인무인화

			// 저장품관리-작업예정등록
			,{	"CTYDJ011",	"WrkPlnRegFaEJB", "rcvCcFsOrdCmmt",	"연주전단지시확정" }
			,{	"CTYDJ031",	"WrkPlnRegFaEJB", "rcvPlMillOrdCmmt",	"후판압연지시확정" }
			,{	"PRYDJ001",	"WrkPlnRegFaEJB", "rcvAPlMillOrdMissnoWr", "A후판압연지시결번실적수신" }
			,{	"CTYDJ033",	"WrkPlnRegFaEJB", "rcvCHrMillOrdCmmt", "C열연압연지시확정" }
			,{	"HRYDJ001",	"WrkPlnRegFaEJB", "rcvCHrMillOrdMissnoWr", "C열연압연지시결번실적수신" }
			,{	"HRYDJ005",	"WrkPlnRegFaEJB", "rcvCHrShearOrd", "C열연정정작업지시수신" }
			,{	"HRYDJ006",	"WrkPlnRegFaEJB", "rcvCHrShearOrdMissnoWr", "C열연정정지시결번실적수신" }
			,{	"CTYDJ032",	"WrkPlnRegFaEJB", "rcvBHrMillOrdCmmt", "B열연압연지시확정" }

			// 저장품관리-상차LOT등록
			,{	"DMYDR022",	"RtModRegFaEJB", "rcvSlabGdsTrnOrd", "외판슬라브상차지시등록" }
			,{	"DMYDR023",	"CarLdLotRegFaEJB", "rcvCoilGdsCarLdOrd", "코일제품상차지시등록" }
			,{	"DMYDR024",	"CarLdLotRegFaEJB", "rcvPlGdsCarLdOrd", "후판제품상차지시등록" }
			,{	"DMYDR025",	"RtModRegFaEJB", "rcvSlabGdsWhFtmvOrd", "임가공이송상차지시등록" }

			// 품질관리-슬라브보류실적
			, { "QMYDJ002", "StockSpecRegFaEJB", "rcvSlabHoldWr", "슬라브보류실적"}
			, { "QMYDJ003", "StockSpecRegFaEJB", "rcvSlabScarSTLNo", "슬라브 진행 변경"}
			, { "QMYDJ004", "StockSpecRegFaEJB", "rcvScarfWr", "Scarfing실적"}
			, { "QMYDJ005", "StockSpecRegFaEJB", "rcvQMPlateProgSync", "후판제품상세변경"}

			// 저장품관리-저장품제원종료
			,{	"PRYDJ002",	"StockSpecEndFaEJB", "rcvAPlRefurExtWr",	"A후판가열로추출실적수신" }
			,{	"HRYDJ002",	"StockSpecEndFaEJB", "rcvCHrRefurExtWr", "C열연가열로추출실적수신" }
			,{	"HRYDJ010",	"StockSpecEndFaEJB", "rcvCHrRefurExtWr", "C열연재열재및압연오작실적수신" }
			,{	"DMYDR029",	"StockSpecEndFaEJB", "rcvOutplSlabDistCmpl", "외판슬라브출하완료" }
			,{	"DMYDR030",	"StockSpecEndFaEJB", "rcvCoilGdsDistCmpl", "코일제품출하완료" }
			,{	"DMYDR031",	"StockSpecEndFaEJB", "rcvPlGdsDistCmpl", "후판제품출하완료" }
			,{	"YDYDJ101",	"StockSpecEndFaEJB", "rcvCCsMslabPrdPlnEnd", "C연주주편생산예정종료" ,"A"}
			,{	"YDYDJ105",	"StockSpecEndFaEJB", "rcvAPlSlabSpecEnd", "A후판모슬라브제원종료","D" }

			// 작업실행관리-권상실적처리
			,{ "Y1YDL007",	"CraneLdHdFaEJB", "rcvY1CrnWrkOrdReq", "Y1크레인작업지시요구" }
			,{ "YDYDJ640",	"CraneLdHdFaEJB", "rcvY1CrnWrkOrdReq", "Y1크레인작업지시요구백업" ,"A"}
			,{ "Y1YDL008",	"CraneLdHdFaEJB", "rcvY1CrnLdWr",	"Y1크레인권상실적" }
			,{ "Y3YDL007",	"CraneLdHdFaEJB", "rcvY3CrnWrkOrdReq", "Y3크레인작업지시요구" }
			,{ "YDYDJ641",	"CraneLdHdFaEJB", "rcvY3CrnWrkOrdReq", "Y3크레인작업지시요구백업","D" }
			,{ "Y3YDL008",	"CraneLdHdFaEJB", "rcvY3CrnLdWr",	"Y3크레인권상실적" }
			,{ "Y4YDL007",	"CraneLdHdFaEJB", "rcvY4CrnWrkOrdReq", "Y4크레인작업지시요구" }
			,{ "Y8YDL007",	"CraneLdHdFaEJB", "rcvY8CrnWrkOrdReq", "Y8크레인작업지시요구" }
			,{ "YDYDJ642",	"CraneLdHdFaEJB", "rcvY4CrnWrkOrdReq", "Y4크레인작업지시요구백업" ,"K"}
			,{ "Y4YDL008",	"CraneLdHdFaEJB", "rcvY4CrnLdWr",	"Y4크레인권상실적" }
			,{ "Y8YDL008",	"CraneLdHdFaEJB", "rcvY8CrnLdWr",	"Y8크레인권상실적" }
			,{ "Y5YDL007",	"CraneLdHdFaEJB", "rcvY5CrnWrkOrdReq", "Y5크레인작업지시요구" }
			,{ "YDYDJ643",	"CraneLdHdFaEJB", "rcvY5CrnWrkOrdReq", "Y5크레인작업지시요구백업" ,"H"}
			,{ "YDYDJ644",	"CraneLdHdFaEJB", "rcvY0CrnWrkOrdReq", "Y0크레인작업지시요구백업","A" }
			,{ "Y5YDL008",	"CraneLdHdFaEJB", "rcvY5CrnLdWr", "Y5크레인권상실적" }
			,{ "YDYDJ600",	"CraneLdHdFaEJB", "rcvY1CrnLdWr", "C연주크레인권상실적백업" ,"A"}
			,{ "YDYDJ602",	"CraneLdHdFaEJB", "rcvY3CrnLdWr", "A후판크레인권상실적백업" ,"D"}
			,{ "YDYDJ604",	"CraneLdHdFaEJB", "rcvY4CrnLdWr", "제품창고크레인권상실적백업","K" }
			,{ "YDYDJ606",	"CraneLdHdFaEJB", "rcvY5CrnLdWr", "C열연크레인권상실적백업" ,"H"}
			,{ "YDYDJ600",	"CraneLdHdFaEJB", "rcvY1CrnLdWr", "C연주크레인권상실적백업","A" }
			,{ "YDYDJ608",	"CraneLdHdFaEJB", "rcvY0CrnLdWr", "통합야드크레인권상실적백업" ,"S"}
			,{ "Y5YDL012",	"CraneLdHdFaEJB", "rcvY5CrnMvstk", "C열연 코일 야드 크레인이적처리 요구 " }
			,{ "Y5YDL013",	"CraneLdHdFaEJB", "rcvY5CrnMvwbk", "C열연 코일 야드 코일크레인작업현황요구 " }
			,{ "Y5YDL014",	"CraneLdHdFaEJB", "procY5CrnSchRequest", "C열연 코일 야드 코일크레인스케줄작업요구 " }

			// 작업실행관리-권하실적처리
			,{ "Y1YDL009",	"CraneUdHdFaEJB", "rcvY1CrnUdWr", "Y1크레인권하실적"	}
			,{ "Y3YDL009",	"CraneUdHdFaEJB", "rcvY3CrnUdWr", "Y3크레인권하실적"	}
			,{ "Y4YDL009",	"CraneUdHdFaEJB", "rcvY4CrnUdWr", "Y4크레인권하실적"	}
			,{ "Y8YDL009",	"CraneUdHdFaEJB", "rcvY8CrnUdWr", "Y8크레인권하실적"	}
			,{ "Y5YDL009",	"CraneUdHdFaEJB", "rcvY5CrnUdWr", "Y5크레인권하실적"	}
			,{ "Y1YDL010",	"CraneUdHdFaEJB", "rcvY1CrnEmgPtopWr", "Y1크레인비상조업실적" }
			,{ "Y3YDL010",	"CraneUdHdFaEJB", "rcvY3CrnEmgPtopWr", "Y3크레인비상조업실적" }
			,{ "Y5YDL010",	"CraneUdHdFaEJB", "rcvY5CrnEmgPtopWr", "Y5크레인비상조업실적" }  //20090623 추가
			,{ "YDYDJ601",	"CraneUdHdFaEJB", "rcvY1CrnUdWr", "C연주크레인권하실적백업","A" }
			,{ "YDYDJ603",	"CraneUdHdFaEJB", "rcvY3CrnUdWr", "A후판크레인권하실적백업" ,"D"}
			,{ "YDYDJ605",	"CraneUdHdFaEJB", "rcvY4CrnUdWr", "제품창고크레인권하실적백업","K" }
			,{ "YDYDJ607",	"CraneUdHdFaEJB", "rcvY5CrnUdWr", "C열연크레인권하실적백업" ,"H"}
			,{ "YDYDJ609",	"CraneUdHdFaEJB", "rcvY0CrnUdWr", "통합야드크레인권하실적백업","S" }
			,{ "Y5YDL015",	"CoilJspFaEJB", "updToPosFixCoilProc", "크레인 작업가능 유무 응답" } // 150626 hun 크레인무인화
			

			// 설비상태관리-설비Tracking
			,{ "C3YDL001",	"EqpTrackingFaEJB", "rcvC3TkovlocUsgMod",	"C3수불구용도변경요구" }
			,{ "C3YDL002",	"EqpTrackingFaEJB", "rcvC3MatlStkInfo", "C3수불구재료적치정보" }
			,{ "C3YDL008",	"EqpTrackingFaEJB", "rcvC3EqpTrblRcvrWr", "C3설비고장복구실적" }
			,{ "C3YDL009",	"EqpTrackingFaEJB", "rcvC3EqpMdModWr", "C3설비모드변경실적" }
			,{ "C3YDL010",	"EqpTrackingFaEJB", "rcvC3RotMatlArrPassInfo", "C3ROT재료도착통과정보" }
			,{ "C3YDL011",	"EqpTrackingFaEJB", "rcvHandScarfingWrkProgInfo", "HandScarfing작업진행정보" }
			// C연주 2정정L2 추가 -- 2012.08.06
			,{ "C7YDL001",	"EqpTrackingFaEJB", "rcvC3TkovlocUsgMod",	"C3수불구용도변경요구" }
			,{ "C7YDL002",	"EqpTrackingFaEJB", "rcvC3MatlStkInfo", "C3수불구재료적치정보" }
			,{ "C7YDL008",	"EqpTrackingFaEJB", "rcvC3EqpTrblRcvrWr", "C3설비고장복구실적" }
			,{ "C7YDL009",	"EqpTrackingFaEJB", "rcvC3EqpMdModWr", "C3설비모드변경실적" }
			,{ "C7YDL010",	"EqpTrackingFaEJB", "rcvC3RotMatlArrPassInfo", "C3ROT재료도착통과정보" }
			,{ "Y1YDL003",	"EqpTrackingFaEJB", "rcvY1EqpDrvMdTurnov", "Y1설비운전모드전환" }
			,{ "Y1YDL004",	"EqpTrackingFaEJB", "rcvY1EqpTrblRcvrWr", "Y1설비고장복구실적" }
			,{ "Y1YDL005",	"EqpTrackingFaEJB", "rcvY1CrnCurrLoc", "Y1크레인현재위치" }
			,{ "Y3YDL003",	"EqpTrackingFaEJB", "rcvY3EqpDrvMdTurnov", "Y3설비운전모드전환" }
			,{ "Y3YDL004",	"EqpTrackingFaEJB", "rcvY3EqpTrblRcvrWr", "Y3설비고장복구실적" }
			,{ "Y3YDL005",	"EqpTrackingFaEJB", "rcvY3CrnCurrLoc", "Y3크레인현재위치" }
			,{ "Y3YDL011",	"EqpTrackingFaEJB", "rcvY3TkovlocUsgMod", "Y3수불구용도변경요구" }
			,{ "Y4YDL003",	"EqpTrackingFaEJB", "rcvY4EqpDrvMdTurnov", "Y4설비운전모드전환" }
			,{ "Y4YDL004",	"EqpTrackingFaEJB", "rcvY4EqpTrblRcvrWr", "Y4설비고장복구실적" }
			,{ "Y4YDL005",	"EqpTrackingFaEJB", "rcvY4CrnCurrLoc", "Y4크레인현재위치" }
			,{ "Y5YDL003",	"EqpTrackingFaEJB", "rcvY5EqpDrvMdTurnov", "Y5설비운전모드전환" }
			,{ "Y5YDL004",	"EqpTrackingFaEJB", "rcvY5EqpTrblRcvrWr", "Y5설비고장복구실적" }
			,{ "Y5YDL005",	"EqpTrackingFaEJB", "rcvY5CrnCurrLoc", "Y5크레인현재위치" }
			,{ "Y8YDL003",	"EqpTrackingFaEJB", "rcvY8EqpDrvMdTurnov", "Y8설비운전모드전환" }
			,{ "Y8YDL004",	"EqpTrackingFaEJB", "rcvY8EqpTrblRcvrWr", "Y8설비고장복구실적" }
			,{ "Y8YDL005",	"EqpTrackingFaEJB", "rcvY8CrnCurrLoc", "Y8크레인현재위치" }
			,{ "Y8YDL010",	"EqpTrackingFaEJB", "rcvY8RcptZoneMtlInfo", "Y8입고존재료정보" }
			,{ "Y8YDL011",	"EqpTrackingFaEJB", "rcvY8TfTrckInfo", "Y8TF트래킹정보" }
			,{ "Y8YDL012",	"EqpTrackingFaEJB", "rcvY8BookOutReq", "Y8BOOK-OUT요구" }
			,{ "Y8YDL013",	"EqpTrackingFaEJB", "rcvY8SpanMtlInfo", "SPAN별 재고현황 요청" }  //2024.04.16 박종호 신규항목 추가			


			// 설비상태관리-Map동기화
			,{ "Y1YDL001",	"MapSyncFaEJB", "rcvY1StrLocSpecReq", "Y1저장위치제원요구" }
			,{ "Y1YDL002",	"MapSyncFaEJB", "rcvY1StockSpecReq",  "Y1저장품제원요구" }
			,{ "Y1YDL006",	"MapSyncFaEJB", "rcvY1CrnWrkPlnReq",  "Y1크레인작업계획요구" }
			,{ "Y3YDL001",	"MapSyncFaEJB", "rcvY3StrLocSpecReq", "Y3저장위치제원요구" }
			,{ "Y3YDL002",	"MapSyncFaEJB", "rcvY3StockSpecReq",  "Y3저장품제원요구" }
			,{ "Y3YDL006",	"MapSyncFaEJB", "rcvY3CrnWrkPlnReq",  "Y3크레인작업계획요구" }
			,{ "Y4YDL001",	"MapSyncFaEJB", "rcvY4StrLocSpecReq", "Y4저장위치제원요구" }
			,{ "Y4YDL002",	"MapSyncFaEJB", "rcvY4StockSpecReq",  "Y4저장품제원요구" }
			,{ "Y5YDL001",	"MapSyncFaEJB", "rcvY5StrLocSpecReq", "Y5저장위치제원요구" }
			,{ "Y5YDL002",	"MapSyncFaEJB", "rcvY5StockSpecReq",  "Y5저장품제원요구"	}
			,{ "Y8YDL001",	"MapSyncFaEJB", "rcvY8StrLocSpecReq", "Y8저장위치제원요구" }
			,{ "Y8YDL002",	"MapSyncFaEJB", "rcvY8StockSpecReq",  "Y8저장품제원요구" }

			// 작업계획관리-입고작업계획

			// 작업계획관리-출고작업계획

			// 작업실행관리-대차이동처리
			,{ "C3YDL007",	"TcarMvHdFaEJB", "rcvC3TcarMvWr", "C3대차이동실적" }
			,{ "Y3YDL014",	"TcarMvHdFaEJB", "rcvY3TcarMvWr", "Y3대차이동실적" }
			,{ "Y5YDL011",	"TcarMvHdFaEJB", "rcvY5TcarMvWr", "Y5대차이동실적" }
			,{ "YDYDJ620",	"TcarMvHdFaEJB", "rcvC3TcarMvWr", "C3대차이동실적백업","D" }
			,{ "YDYDJ622",	"TcarMvHdFaEJB", "rcvC3TcarMvWr", "Y3대차이동실적백업","D" }
			,{ "YDYDJ621",	"TcarMvHdFaEJB", "rcvY5TcarMvWr", "Y5대차이동실적백업" ,"H"}
			,{ "Y5YDL018",	"TcarMvHdFaEJB", "rcvY5TcarArriveMvWr", "Y5차량동간이적도착실적" ,"H"}  // 151013 hun 차량 동간이적 개선

			// 작업실행관리-차량이동처리
			,{ "TSYDJ002",	"CarMvHdFaEJB", "rcvMatlCarArrPntReq", "소재차량도착Point요구" }
			,{ "TSYDJ003",	"CarMvHdFaEJB", "rcvMatlCarArr", "소재차량도착" }
			,{ "TSYDJ004",	"CarMvHdFaEJB", "rcvMatlCarLev", "소재차량출발" }
			,{ "TSYDJ005",	"CarMvHdFaEJB", "rcvMatlCarWaitLocArr", "소재차량대기장도착" }
			,{ "TSYDJ014",	"CarMvHdFaEJB", "rcvCarstartDelete", "출발취소정보" }
			,{ "TSYDJ015",	"CarMvHdFaEJB", "rcvCarArrive", "스크랩 하차완료" }
			,{ "DMYDR035",	"CarMvHdFaEJB", "rcvOutplSlabDistCarArrWr", "외판슬라브출하차량도착실적" }
			,{ "DMYDR036",	"CarMvHdFaEJB", "rcvCoilGdsDistCarArrWr", "코일제품출하차량도착실적" }
			,{ "DMYDR037",	"CarMvHdFaEJB", "rcvCoilRentprocCarArrWr", "코일임가공차량도착실적" }
			,{ "DMYDR038",	"CarMvHdFaEJB", "rcvPlGdsDistCarArrWr", "후판제품출하차량도착실적" }
			,{ "DMYDR039",	"CarMvHdFaEJB", "rcvOutplSlabDistCarLevWr", "외판슬라브출하차량출발실적" }
			,{ "DMYDR040",	"CarMvHdFaEJB", "rcvCoilGdsdistCarLevWr", "코일제품출하차량출발실적" }
			,{ "DMYDR041",	"CarMvHdFaEJB", "rcvCoilRenrprocCarLevWr", "코일임가공차량출발실적" }
			,{ "DMYDR042",	"CarMvHdFaEJB", "rcvPlGdsDistCarLevWr", "후판제품출하차량출발실적" }
			,{ "DMYDR043",	"CarMvHdFaEJB", "rcvPlGdsDistShipArrWr", "후판제품연안해송도착실적" }


			,{ "YDYDJ630",	"CarMvHdFaEJB", "rcvMatlCarArrPntReq", "C연주소재차량도착Point요구" ,"A"}
			,{ "YDYDJ631",	"CarMvHdFaEJB", "rcvY3MatlCarArrPntReq", "A후판소재차량도착Point요구" ,"D"}
			,{ "YDYDJ632",	"CarMvHdFaEJB", "rcvUnityYardCarArrPntReq", "통합야드소재차량도착Point요구" ,"S"}
			,{ "YDYDJ633",	"CarMvHdFaEJB", "rcvCarBayInOrdReq", "차량입동지시요구" ,"S"}						//임춘수 추가 2009.09.11
			,{ "YDYDJ634",	"CarMvHdFaEJB", "rcvSchRuleNCallCrnSch", "스케줄기준체크/크레인호출" ,"H"}			//임춘수 추가 2009.10.02
			,{ "YDYDJ650",	"CarMvHdFaEJB", "rcvMatlCarArr", "소재차량도착" ,"S"}
			,{ "YDYDJ651",	"CarMvHdFaEJB", "rcvMatlCarLev", "소재차량출발" ,"S"}
			,{ "YDYDJ652",	"CarMvHdFaEJB", "rcvOutplSlabDistCarArrWr", "외판슬라브출하차량도착실적" ,"A"}
			,{ "YDYDJ653",	"CarMvHdFaEJB", "rcvCoilGdsDistCarArrWr", "코일제품출하차량도착실적" ,"H"}
			,{ "YDYDJ654",	"CarMvHdFaEJB", "rcvCoilRentprocCarArrWr", "코일임가공차량도착실적","H" }
			,{ "YDYDJ655",	"CarMvHdFaEJB", "rcvPlGdsDistCarArrWr", "후판제품출하차량도착실적","K" }
			,{ "YDYDJ661",	"CarMvHdFaEJB", "rcvPlGdsRcptCarArrWr", "후판제품입고차량도착실적","K" }
			,{ "YDYDJ656",	"CarMvHdFaEJB", "rcvOutplSlabDistCarLevWr", "외판슬라브출하차량출발실적","D" }
			,{ "YDYDJ657",	"CarMvHdFaEJB", "rcvCoilGdsdistCarLevWr", "코일제품출하차량출발실적","H" }
			,{ "YDYDJ658",	"CarMvHdFaEJB", "rcvCoilRenrprocCarLevWr", "코일임가공차량출발실적" ,"H"}
			,{ "YDYDJ659",	"CarMvHdFaEJB", "rcvPlGdsDistCarLevWr", "후판제품출하차량출발실적" ,"K"}
			,{ "YDYDJ660",	"CarMvHdFaEJB", "rcvCoilHYSCOprocCarArrWr", "코일하이스코차량도착실적","H" }
			,{ "YDYDJ662",	"CarMvHdSeEJB", "procCarBayInOrdReqNEW", "차량입동지시요구NEW" ,"S"}

//			SJH 추가(사외이송)
			,{ "DMYDR045",	"CarMvHdFaEJB", "rcvCoilGdsDistShipArrWr", "코일제품연안해송도착실적" }


			// 작업요구관리-입고작업요구
			,{ "C3YDL003",	"RcptWrkDmdFaEJB", "rcvC3OhcTakeOutReq", "C3OhcTake-out요구" }
			,{ "C3YDL004",	"RcptWrkDmdFaEJB", "rcvC3TakeOutCmpl", "C3Take-Out완료" }
			// C연주 2정정L2 추가 -- 2012.08.06
			,{ "C7YDL003",	"RcptWrkDmdFaEJB", "rcvC3OhcTakeOutReq", "C3OhcTake-out요구" }
			,{ "C7YDL004",	"RcptWrkDmdFaEJB", "rcvC3TakeOutCmpl", "C3Take-Out완료" }
			,{ "Y3YDL012",	"RcptWrkDmdFaEJB", "rcvY3TakeOutCmpl", "Y3Take-Out완료" }
			,{ "H1YDL001",	"RcptWrkDmdFaEJB", "rcvR2MillBrLineOffReq", "H1압연분기Line-Off요구" }
			,{ "H2YDL003",	"RcptWrkDmdFaEJB", "rcvR3ShearOutLineOffReq", "H2열연정정Line-Off요구" }
			,{ "H2YDL013",	"RcptWrkDmdFaEJB", "rcvR3ShearOutLineOffReq", "H2열연정정Line-Off요구" }
			,{ "H2YDL023",	"RcptWrkDmdFaEJB", "rcvR3ShearOutLineOffReq", "H2열연정정Line-Off요구" }
			,{ "HRYDJ009",	"RcptWrkDmdFaEJB", "rcvR3ShearOutLineOffReq", "HR열연정정Line-Off요구" }
			,{ "H2YDL004",	"RcptWrkDmdFaEJB", "rcvR3WtclTnkLineOffReq", "H2수소탱크Line-Off요구" }
			,{ "P2YDL001",	"RcptWrkDmdFaEJB", "rcvP2PillingWr", "P2Pilling 실적"	}
			,{ "P2YDL003",	"RcptWrkDmdFaEJB", "rcvP2BookInReq",	"P2Book-In 실적"	}
			,{ "PRYDJ006",	"RcptWrkDmdFaEJB", "rcvAPlBookOutWr", "A후판 Book-Out실적" }
			,{ "YDYDJ201",	"RcptWrkDmdFaEJB", "rcvCCsExtSectCarryOutReq", "C연주불출구Carry-Out요구","A" }
			,{ "YDYDJ202",	"RcptWrkDmdFaEJB", "rcvY3CarryOutReq", "A후판슬라브야드Carry-Out요구"	,"D"}
			,{ "YDYDJ203",	"RcptWrkDmdFaEJB", "rcvY4CarryOutReq", "A후판창고야드Carry-Out요구","K" }
			,{ "YDYDJ204",	"RcptWrkDmdFaEJB", "rcvCCsOhcCarryOutReq", "C연주OhcCarry-Out요구","A" }
			,{ "YDYDJ205",	"RcptWrkDmdFaEJB", "rcvAplCarUdWrkReq", "A후판차량하차작업요구" ,"K"}
			,{ "YDYDJ206",	"RcptWrkDmdFaEJB", "rcvCCsCarUdWrkReq", "C연주차량하차작업요구" ,"A"}
			,{ "YDYDJ207",	"RcptWrkDmdFaEJB", "rcvCHrCarUdWrkReq", "C열연차량하차작업요구" ,"A"}
			,{ "YDYDJ208",	"RcptWrkDmdFaEJB", "rcvY4CarUdWrkReq", "후판창고차량하차작업요구" ,"K"}
			,{ "YDYDJ209",	"RcptWrkDmdFaEJB", "rcvCCsTcarUdWrkReq", "C연주대차하차작업요구" ,"A"}
			,{ "YDYDJ210",	"RcptWrkDmdFaEJB", "rcvCHrTcarUdWrkReq", "C열연대차하차작업요구" ,"H"}

			//
			// 작업요구관리-출고작업요구
			,{ "Y3YDL013",	"IssueWrkDmdFaEJB", "rcvY3TakeInCmpl", "Y3Take-In완료" }
			,{ "C3YDL005",	"IssueWrkDmdFaEJB", "rcvC3TakeInCmpl", "C3Take-In완료" }
			,{ "C3YDL006",	"IssueWrkDmdFaEJB", "rcvOHCTakeInCmpl", "C3OhcTake-In요구" }
			// C연주 2정정L2 추가 -- 2012.08.06
			,{ "C7YDL005",	"IssueWrkDmdFaEJB", "rcvC3TakeInCmpl", "C3Take-In완료" }
			,{ "C7YDL006",	"IssueWrkDmdFaEJB", "rcvOHCTakeInCmpl", "C3OhcTake-In요구" }
//			,{ "YDYDJ252",	"IssueWrkDmdFaEJB", "rcvR3ShearInLineInReq", "H2정정입측Line-In요구" }
			,{ "H2YDL001",	"IssueWrkDmdFaEJB", "rcvR3ShearInLineInReq", "H2정정입측Line-In요구" }
			,{ "H2YDL011",	"IssueWrkDmdFaEJB", "rcvR3ShearInLineInReq", "H2정정입측Line-In요구" }
			,{ "H2YDL021",	"IssueWrkDmdFaEJB", "rcvR3ShearInLineInReq", "H2정정입측Line-In요구" }
//			,{ "H2YDL001",	"IssueWrkDmdFaEJB", "rcvCHrShearInSupLotComp", "C열연 정정입측 보급Lot 편성" }
			,{ "HRYDJ008",	"IssueWrkDmdFaEJB", "rcvCHrShearInSupLotCompFromHr", "C열연 정정입측 보급Lot 편성 백업" }
			,{ "YDYDJ251",	"IssueWrkDmdFaEJB", "rcvR3WtclTnkLineInReq", "H2수냉탱크Line-In요구" ,"D"}
			,{ "H2YDL002",	"IssueWrkDmdFaEJB", "rcvCHrWtclTnkSupLotComp", "C열연수냉탱크보급Lot편성" }
			,{ "H2YDL004",	"IssueWrkDmdFaEJB", "rcvCHrShearTakeInReq", "C열연정정 Take-In 요구" }
			,{ "H2YDL014",	"IssueWrkDmdFaEJB", "rcvCHrShearTakeInReq", "C열연정정 Take-In 요구" }
			,{ "H2YDL024",	"IssueWrkDmdFaEJB", "rcvCHrShearTakeInReq", "C열연정정 Take-In 요구" }
			,{ "H2YDL005",	"IssueWrkDmdFaEJB", "rcvCHrShearTakeOutReq", "C열연정정 Take-Out 요구" }
			,{ "H2YDL015",	"IssueWrkDmdFaEJB", "rcvCHrShearTakeOutReq", "C열연정정 Take-Out 요구" }
			,{ "H2YDL025",	"IssueWrkDmdFaEJB", "rcvCHrShearTakeOutReq", "C열연정정 Take-Out 요구" }
			,{ "YDYDJ231",	"IssueWrkDmdFaEJB", "rcvCCsCHrSupLotComp", "C연주C열연보급Lot편성" ,"A"}
			,{ "YDYDJ232",	"IssueWrkDmdFaEJB", "rcvCCsMScarfingSupLotComp", "C연주M-Scarfing보급Lot편성","A"	}
			,{ "YDYDJ233",	"IssueWrkDmdFaEJB", "rcvCCsShearSupLotComp", "C연주정정보급Lot편성" ,"A"}
			,{ "YDYDJ241",	"IssueWrkDmdFaEJB", "rcvCCsCHrSupCarryInWrkReq", "C연주C열연보급Carry-In작업요구","A" }
			,{ "YDYDJ242",	"IssueWrkDmdFaEJB", "rcvCCsScarfingSupCarryInWrkReq", "C연주Scarfing보급Carry-In작업요구"	,"A"}
			,{ "YDYDJ243",	"IssueWrkDmdFaEJB", "rcvCCSShearSupCarryInWrkReq", "C연주정정보급Carry-In작업요구","A" }
			,{ "YDYDJ234",	"IssueWrkDmdFaEJB", "rcvCCsMatlFtmvCarLdLotComp", "C연주소재이송상차LOT편성" ,"A"}
			,{ "YDYDJ235",	"IssueWrkDmdFaEJB", "rcvCCsOutplDistCarLdlotComp", "C연주외판출하상차LOT편성" ,"A"}
			,{ "YDYDJ236",	"IssueWrkDmdFaEJB", "rcvCCsTcarLdLotComp", "C연주대차상차LOT편성" ,"A"}
			,{ "YDYDJ244",	"IssueWrkDmdFaEJB", "rcvCCsCarLdWrkReq", "C연주차량상차작업요구" ,"A"}
			,{ "YDYDJ249",	"IssueWrkDmdFaEJB", "rcvCCsTcarLdWrkReq", "C연주대차상차작업요구" ,"A"}
			,{ "YDYDJ237",	"IssueWrkDmdFaEJB", "rcvAPlRefurSupLotComp", "A후판가열로보급Lot편성" ,"D"}
			,{ "YDYDJ245",	"IssueWrkDmdFaEJB", "rcvAPlCarryInWrkReq", "A후판Carry-In작업요구","D" }
			,{ "YDYDJ497",	"IssueWrkDmdFaEJB", "rcvBPlRefurSupLotComp", "2후판가열로보급Lot편성" ,"D"}
			,{ "YDYDJ495",	"IssueWrkDmdFaEJB", "rcvBPlCarryInWrkReq", "2후판Carry-In작업요구","D" }
			,{ "YDYDJ238",	"IssueWrkDmdFaEJB", "rcvAPlMatlFtmvCarLdLotComp", "A후판소재이송상차Lot편성","D" }
			,{ "YDYDJ246",	"IssueWrkDmdFaEJB", "rcvAPlCarldWrkReq", "A후판차량상차작업요구","D" }
			,{ "YDYDJ247",	"IssueWrkDmdFaEJB", "rcvY4CarLdWrkReq", "후판창고차량상차작업요구","K" }
			,{ "YDYDJ248",	"IssueWrkDmdFaEJB", "rcvCHrCarLdWrkReq", "C열연차량상차작업요구","H" }
			,{ "YDYDJ239",	"IssueWrkDmdFaEJB", "rcvCHrMatlRentProcLotComp", "C열연소재임가공LOT편성","H" }
			,{ "YDYDJ264",	"IssueWrkDmdFaEJB", "rcvAPlChgLotNoEffSupLotComp", "A후판장입LotNo적용보급Lot편성" ,"D"}
			,{ "YDYDJ250",	"IssueWrkDmdFaEJB", "rcvCHrTcarLdWrkReq", "C열연대차상차작업요구" ,"H"}
			,{ "YDYDJ253",	"IssueWrkDmdFaEJB", "rcvCHrMatlFtmvLotComp", "C열연소재이송Lot편성","H" }
			,{ "YDYDJ254",	"IssueWrkDmdFaEJB", "rcvCHrGdsWhFtmvLotComp", "C열연제품고간이송Lot편성" ,"H"}
			,{ "YDYDJ255",	"IssueWrkDmdFaEJB", "rcvCHrMatlTcarLdLotComp", "C열연소재대차상차Lot편성","H" }
			,{ "YDYDJ256",	"IssueWrkDmdFaEJB", "rcvCHrGdsTcarLdLotComp", "C열연제품대차상차Lot편성","H" }
			,{ "YDYDJ257",	"IssueWrkDmdFaEJB", "rcvPlGdsFtmvCarLdLotComp", "후판제품이송상차Lot편성" ,"K"}
			,{ "YDYDJ258",	"IssueWrkDmdFaEJB", "rcvCCsChgLotNoEffSupLotComp", "C연주장입LotNo적용보급Lot 편성","A" }
			,{ "YDYDJ282",	"IssueWrkDmdFaEJB", "rcvCoilGdsDistCarLdLotComp", "코일제품출하상차Lot 편성","H" }
			,{ "YDYDJ283",	"IssueWrkDmdFaEJB", "rcvCoilOutplDistCarLdLotComp", "코일임가공출하상차Lot 편성" ,"H"}
			,{ "YDYDJ284",	"IssueWrkDmdFaEJB", "rcvPlGdsDistCarLdLotComp", "후판제품출하상차Lot 편성" ,"K"}
//			,{ "YDYDJ285",	"IssueWrkDmdFaEJB", "rcvCCsChgLotNoEffSupLotComp", "C연주장입LotNo적용보급Lot편성" }
//			,{ "YDYDJ286",	"IssueWrkDmdFaEJB", "rcvAPlChgLotNoEffSupLotComp", "A후판장입LotNo적용보급Lot편성" }
//			,{ "YDYDJ287",	"IssueWrkDmdFaEJB", "rcvPlGdsDistCarLdLotComp", "후판제품출하상차Lot 편성" }
			,{ "YDYDJ285",	"IssueWrkDmdFaEJB", "rcvPlGdsDistCarLdWrkReq", "후판제품출하상차작업요구","K" }
			,{ "YDYDJ286",	"IssueWrkDmdFaEJB", "rcvPlGdsRetnLotComp", "후판제품반납대상재Lot편성","K" }
			,{ "YDYDJ288",	"IssueWrkDmdFaEJB", "rcvSUnMatlFtmvCarLdLotComp", "통합야드소재이송상차LOT편성" ,"S"}
			,{ "YDYDJ295",	"IssueWrkDmdFaEJB", "rcvSUnMatlFtmvCarLdLotCompCrn", "통합야드소재이송상차LOT편성(크레인별)" ,"S"}
			,{ "YDYDJ296",	"IssueWrkDmdFaEJB", "rcvSlabTotCarLdWrkReq", "통합야드차량상차작업요구" ,"S"}
			,{ "YDYDJ292",	"IssueWrkDmdFaEJB", "rcvCoilGdsDistCarLdWrkReq", "코일제품출하차량상차작업요구","H" }
			,{ "YDYDJ293",	"IssueWrkDmdFaEJB", "rcvDistCarSch", "출하차량스케줄","H" }
			,{ "YDYDJ393",	"JNDICraneSchReg",  "callCraneSchInfoRecord", "출하차량스케줄","3" }
			,{ "YDYDJ394",	"RtModRegSeEJB",  	"procStandByYdArrive", "대기장도착실적","J" }
			,{ "YDYDJ294",	"IssueWrkDmdFaEJB", "rcvAutoWorkLotComp", "연주/후판 자동준비작업요구","D" }
			,{ "YDYDJ300",	"IssueWrkDmdFaEJB", "rcvCoilHYSCOCarLdLotComp", "코일HYSCO출하상차Lot 편성","H" }
			,{ "YDYDJ301",	"IssueWrkDmdFaEJB", "rcvCoilHYSCOCarLdWrkReq", "코일HYSCO출하차량상차작업요구","H" }

//C열연 증설
			,{ "H2YDL031",	"IssueWrkDmdFaEJB", "rcvR3ShearInLineInReq", "SPM3정정입측Line-In요구" }
			,{ "H2YDL041",	"IssueWrkDmdFaEJB", "rcvR3ShearInLineInReq", "SPM4정정입측Line-In요구" }
			,{ "H2YDL071",	"IssueWrkDmdFaEJB", "rcvR3ShearInLineInReq", "SPM5정정입측Line-In요구" }
			,{ "H2YDL051",	"IssueWrkDmdFaEJB", "rcvR3ShearInLineInReq", "HFL4정정입측Line-In요구" }
			,{ "H2YDL061",	"IssueWrkDmdFaEJB", "rcvR3ShearInLineInReq", "HFL5정정입측Line-In요구" }

			,{ "H2YDL033",	"RcptWrkDmdFaEJB",  "rcvR3ShearOutLineOffReq", "SPM3열연정정Line-Off요구" }
			,{ "H2YDL043",	"RcptWrkDmdFaEJB",  "rcvR3ShearOutLineOffReq", "SPM4열연정정Line-Off요구" }
			,{ "H2YDL073",	"RcptWrkDmdFaEJB",  "rcvR3ShearOutLineOffReq", "SPM5열연정정Line-Off요구" }
			,{ "H2YDL053",	"RcptWrkDmdFaEJB",  "rcvR3ShearOutLineOffReq", "HFL4열연정정Line-Off요구" }
			,{ "H2YDL063",	"RcptWrkDmdFaEJB",  "rcvR3ShearOutLineOffReq", "HFL5열연정정Line-Off요구" }

			,{ "H2YDL034",	"IssueWrkDmdFaEJB", "rcvCHrShearTakeInReq", "SPM3열연정정 Take-In 요구" }
			,{ "H2YDL044",	"IssueWrkDmdFaEJB", "rcvCHrShearTakeInReq", "SPM4열연정정 Take-In 요구" }
			,{ "H2YDL054",	"IssueWrkDmdFaEJB", "rcvCHrShearTakeInReq", "HFL4열연정정 Take-In 요구" }
			,{ "H2YDL064",	"IssueWrkDmdFaEJB", "rcvCHrShearTakeInReq", "HFL5열연정정 Take-In 요구" }
			,{ "H2YDL074",	"IssueWrkDmdFaEJB", "rcvCHrShearTakeInReq", "SPM5열연정정 Take-In 요구" }

			,{ "H2YDL035",	"IssueWrkDmdFaEJB", "rcvCHrShearTakeOutReq", "SPM3열연정정 Take-Out 요구" }
			,{ "H2YDL045",	"IssueWrkDmdFaEJB", "rcvCHrShearTakeOutReq", "SPM4열연정정 Take-Out 요구" }
			,{ "H2YDL055",	"IssueWrkDmdFaEJB", "rcvCHrShearTakeOutReq", "HFL4열연정정 Take-Out 요구" }
			,{ "H2YDL065",	"IssueWrkDmdFaEJB", "rcvCHrShearTakeOutReq", "HFL5열연정정 Take-Out 요구" }
			,{ "H2YDL075",	"IssueWrkDmdFaEJB", "rcvCHrShearTakeOutReq", "SPM5열연정정 Take-Out 요구" }

			// 작업요구관리-이적작업요구
			,{ "YDYDJ265",	"MvStkWrkDmdFaEJB", "rcvAPlChgPrepWrkReq", "A후판장입준비작업요구","D" }
			,{ "YDYDJ266",	"MvStkWrkDmdFaEJB", "rcvAPlEmptyBedSecurLotComp", "A후판공Bed확보Lot편성" ,"D"}
			,{ "YDYDJ267",	"MvStkWrkDmdFaEJB", "rcvAPlEmptyBedSecurWrkReq", "A후판공Bed확보작업요구","D" }
			,{ "YDYDJ268",	"MvStkWrkDmdFaEJB", "rcvAPlReadjLotComp", "A후판정리Lot편성" ,"D"}
			,{ "YDYDJ269",	"MvStkWrkDmdFaEJB", "rcvAPlReadjWrkReq", "A후판정리작업요구" ,"D"}
			,{ "YDYDJ270",	"MvStkWrkDmdFaEJB", "rcvY4SelWrkLotComp", "후판창고선별작업Lot편성","K" }
			,{ "YDYDJ271",	"MvStkWrkDmdFaEJB", "rcvY4SelWrkReq", "후판창고선별작업요구","K" }
			,{ "YDYDJ272",	"MvStkWrkDmdFaEJB", "rcvY4EmptyBedSecurLotComp", "후판창고공Bed확보Lot편성","K" }
			,{ "YDYDJ273",	"MvStkWrkDmdFaEJB", "rcvY4EmptyBedSecurWrkReq", "후판창고공Bed확보작업요구" ,"K"}
			,{ "YDYDJ274",	"MvStkWrkDmdFaEJB", "rcvY4ReadjLotComp", "후판창고정리Lot편성" ,"K"}
			,{ "YDYDJ275",	"MvStkWrkDmdFaEJB", "rcvY4ReadjWrkReq", "후판창고정리작업요구","K" }
			,{ "YDYDJ276",	"MvStkWrkDmdFaEJB", "rcvCHrShearSupPrepLotComp", "C열연정정보급준비Lot편성","H" }
			,{ "YDYDJ277",	"MvStkWrkDmdFaEJB", "rcvCHrShearSupPrepWrkReq", "C열연정정보급준비작업요구","H" }
			,{ "YDYDJ278",	"MvStkWrkDmdFaEJB", "rcvCHrMatlAdjLotComp", "C열연소재정리Lot편성","H" }
			,{ "YDYDJ279",	"MvStkWrkDmdFaEJB", "rcvCHrMatlAdjWrkReq", "C열연소재정리작업요구","H" }
			,{ "YDYDJ280",	"MvStkWrkDmdFaEJB", "rcvCHrGdsAdjLotComp", "C열연제품정리Lot편성","H"	}
			,{ "YDYDJ281",	"MvStkWrkDmdFaEJB", "rcvCHrGdsAdjWrkReq", "C열연제품정리작업요구","H" }
			,{ "YDYDJ259",	"MvStkWrkDmdFaEJB", "rcvCCsChgPrepWrkReq", "C연주장입준비작업요구","A" }
			,{ "YDYDJ260",	"MvStkWrkDmdFaEJB", "rcvCCsEmptyBedSecurLotComp", "C연주공Bed확보Lot편성","A" }
			,{ "YDYDJ261",	"MvStkWrkDmdFaEJB", "rcvCCsEmptyBedSecurWrkReq", "C연주공Bed확보작업요구","A" }
			,{ "YDYDJ262",	"MvStkWrkDmdFaEJB", "rcvCCsReadjLotComp", "C연주정리Lot편성","A" }
			,{ "YDYDJ263",	"MvStkWrkDmdFaEJB", "rcvCCsReadjWrkReq", "C연주정리작업요구","A" }
			,{ "YDYDJ287",	"MvStkWrkDmdFaEJB", "rcvMvDummyMtlAboveTgMtlLotComp", "대상재상단더미재이적Lot편성","A" }
			,{ "YDYDJ289",	"MvStkWrkDmdFaEJB", "rcvCCMvLotComp", "C연주 이적(동내,동간) LOT편성","A" }
			,{ "YDYDJ290",	"MvStkWrkDmdFaEJB", "rcvCCPrepLotCompByCapa", "준비스케줄 LOT편성","A" }
			,{ "YDYDJ291",	"MvStkWrkDmdFaEJB", "rcvFtmvOrdLotReq", "이적 작업요구","A" }

			// 스케줄관리-크레인스케줄
			,{ "YDYDJ500",	"CrnSchFaEJB", "rcvY1CrnSchMain", "C연주크레인스케줄Main","A" }
			,{ "YDYDJ501",	"CrnSchFaEJB", "rcvY1CrnStrLocDeciMain", "C연주크레인저장위치결정Main","A" }
			,{ "YDYDJ503",	"CrnSchFaEJB", "rcvY3CrnSchMain", "A후판크레인스케줄Main","D" }
			,{ "YDYDJ504",	"CrnSchFaEJB", "rcvY3CrnStrLocDeciMain", "A후판크레인저장위치결정Main","D" }
			,{ "YDYDJ506",	"CrnSchFaEJB", "rcvY4CrnSchMain", "제품창고크레인스케줄Main","K" }
			,{ "YDYDJ507",	"CrnSchFaEJB", "rcvY4CrnStrLocDeciMain", "제품창고크레인저장위치결정Main","K" }
			,{ "YDYDJ509",	"CrnSchFaEJB", "rcvY5CrnSchMain", "C열연크레인스케줄Main","H" }
			,{ "YDYDJ599",	"CoilCrnSchSeEJB", "procY5CrnSchMainB", "C열연크레인스케줄MainB","H" }
			,{ "YDYDJ510",	"CrnSchFaEJB", "rcvY5CrnStrLocDeciMain", "C열연크레인저장위치결정Main","H" }
			,{ "YDYDJ512",	"CrnSchFaEJB", "rcvY0CrnSchMain", "통합야드 크레인스케줄Main","S" }
			,{ "YDYDJ513",	"CrnSchFaEJB", "rcvY0CrnStrLocDeciMain", "통합야드 크레인저장위치결정Main","S" }
			
			//2021. 10. 07 후판제품 추가
			,{ "YDYDJ557",	"PlateYdRcvFaEJB", "rcvInterface", "입고가적베드이적작업","K" }
			
			//전문역전되는 현상을 방지하기 위해서 버퍼역할을 하는 모듈 추가
			,{ YdConstant.YDYDJ701,	"CrnSchFaEJB", "rcvForwardTcRecord", "전문전송버퍼","G" }

			// 모니터링관리
			,{ YdConstant.YDYDJ702,	"MonitoringFaEJB", "rcvLogMsg", "로그메세지처리","S" }
			,{ YdConstant.YDYDJ901,	"YdFlexFaEJB", "wrkBuffer", "메세지 버퍼","S" }

			//
			// 스케줄관리-크레인리스케줄
			,{ "YDYDJ502",	"CrnReSchFaEJB", "rcvY1CrnReSch", "C연주크레인리스케줄","A" }
			,{ "YDYDJ505",	"CrnReSchFaEJB", "rcvY3CrnReSch", "A후판크레인리스케줄","D" }
			,{ "YDYDJ508",	"CrnReSchFaEJB", "rcvY4CrnReSch", "제품창고크레인리스케줄","K" }
			,{ "YDYDJ511",	"CrnReSchFaEJB", "rcvY5CrnReSch", "C열연크레인리스케줄","H" }
			,{ "YDYDJ514",	"CrnReSchFaEJB", "rcvY0CrnReSch", "통합야드크레인리스케줄","S" }

			// 스케줄관리-이동설비스케줄
			,{ "YDYDJ520",	"TransEqpSchFaEJB",	"rcvY1TcarSch", "C연주대차스케줄","A" }
			,{ "YDYDJ521",	"TransEqpSchFaEJB",	"rcvY5TcarSch", "C열연대차스케줄","H" }
			,{ "YDYDJ522",	"TransEqpSchFaEJB",	"rcvY3TcarSch", "후판대차스케줄","D" }
			,{ "YDYDJ900", "YdSimFaEJB", "testDao", "testdao 테스트","S"}
			,{ "YDYDJ800", "YdSimFaEJB", "reqTestCCC", "DMdelegate 테스트","S"}

			//후판정정야드-가스절단실적
			,{ "PRYDJ007", "PlateReviseFaEJB", "rcvpPlateYdGascutresult", "후판정정야드가스절단실적"}

			//후판제품창고-제품오버롤체크
			,{ "YDYDJ297", "RcptWrkDmdFaEJB", "rcvPlateOverRollCheck", "후판제품오버롤체크","K"}
			//연주/후판슬라브 이상재등록/해제 - 공정관리
			,{ "YDYDJ298", "RcptWrkDmdFaEJB", "rcvAbmtlOccurSend", "연주/후판 이상재등록/해제","S"}

			//2후판전단정정 L2 수신전문
			//,{	"S1PPL014",	"EqpTrackingFaEJB", "rcvS1RcptZoneArrInfo",	"입고존도착정보"	}
			//,{	"S1PPL016",	"EqpTrackingFaEJB", "rcvS1PilingWr",		"파일링실적"	}
			,{	"S1YDL014",	"EqpTrackingFaEJB", "rcvS1RcptZoneArrInfo",	"입고존도착정보"	}
			,{	"S1YDL016",	"EqpTrackingFaEJB", "rcvS1PilingWr",		"파일링실적"	}
			           
			//1후판정정PM45 L2 수신전문
			,{	"Y9YDL001",	"PlateReviseFaEJB", "rcvPm45BookInOutWslt",	"북인/아웃실적"	}
			,{	"Y9YDL002",	"PlateReviseFaEJB", "rcvPm45LocInfo",		"저장품제원정보"	}

			//	2후판정정야드 - 야드L2 수신전문
			,{	"Y7YDL001",	"JPlateYdL2RcvFaEJB", "rcvY7StrLocSpecReq",	"2후판정정 저장위치제원요구" }
			,{	"Y7YDL002",	"JPlateYdL2RcvFaEJB", "rcvY7StockSpecReq",	"2후판정정 저장품제원요구" }
			,{	"Y7YDL003",	"JPlateYdL2RcvFaEJB", "rcvY7EqpDrvModeChg",	"2후판정정 설비운전모드전환" }
			,{	"Y7YDL004",	"JPlateYdL2RcvFaEJB", "rcvY7EqpTrblRcvrWr",	"2후판정정 설비고장복구실적" }
			,{	"Y7YDL007",	"JPlateYdL2RcvFaEJB", "rcvY7CrnWrkOrdReq",	"2후판정정 크레인작업지시요구"	}
			,{  "Y7YDL008",	"JPlateYdL2RcvFaEJB", "rcvY7CrnUpWr", 		"2후판정정 권상실적" }
			,{  "Y7YDL009",	"JPlateYdL2RcvFaEJB", "rcvY7CrnDownWr", 	"2후판정정 권하실적" }
			,{  "Y7YDL010",	"JPlateYdL2RcvFaEJB", "rcvY7OffCrnUpWr", 	"2후판정정 강제권상요구" }
			,{  "Y7YDL011",	"JPlateYdL2RcvFaEJB", "rcvY7OffCrnDownWr", 	"2후판정정 강제권하요구"	}
			,{  "Y7YDL012",	"JPlateYdL2RcvFaEJB", "rcvY7CrnOrderSel", 	"2후판정정 크레인명령선택"	}
			,{  "Y7YDL013",	"JPlateYdL2RcvFaEJB", "rcvY7PilingRslt", 	"2후판정정 파일링실적"	}
			
			//	2후판정정야드 - 2후판전단L2 수신전문
			,{ 	"S1YDL013",	"JPlateYdL2RcvFaEJB", "rcvS1BookInOutReq",	"2후판정정 Book-In/Book-Out요구" }

			//	2후판정정야드 - 후판조업L3 수신전문
			,{  "PPYDJ014",	"JPlateYdL3RcvFaEJB", "rcvPPGasCutResult", 	"2후판정정 GAS장절단실적"	}
			,{  "PPYDJ015",	"JPlateYdL3RcvFaEJB", "rcvPPNextDeciInfo", 	"2후판정정 차행선결정정보"	}
			,{  "PPYDJ016",	"JPlateYdL3RcvFaEJB", "rcvPPCBRTBookOut", 	"2후판정정야드극후물대북아웃수신"	}
			
			//	1후판정정야드 - 1후판정정야드 수신전문
			,{  "Y9YDL001",	"PlateReviseFaEJB", "rcvY9Interface", 	"1후판정정 Book-In/Book-Out 실적"	}
			,{  "Y9YDL002",	"PlateReviseFaEJB", "rcvY9Interface", 	"1후판정정 저장품제원"	}
			,{  "Y9YDL003",	"PlateReviseFaEJB", "rcvY9Interface", 	"1후판정정 저장품제원요구"	}
			,{  "Y9YDL004",	"PlateReviseFaEJB", "rcvY9Interface", 	"1후판정정 설비고장복구실적"	}
			,{  "Y9YDL007",	"PlateReviseFaEJB", "rcvY9Interface", 	"1후판정정 크레인작업지시요구"	}
			,{  "Y9YDL008",	"PlateReviseFaEJB", "rcvY9Interface", 	"1후판정정 크레인권상실적"	}
			,{  "Y9YDL009",	"PlateReviseFaEJB", "rcvY9Interface", 	"1후판정정 크레인권하실적"	}
			,{  "E5YDL001",	"PlateReviseFaEJB", "rcvY9Interface", 	"1후판압연전단 Book-In요구"	}
			,{  "E5YDL002",	"PlateReviseFaEJB", "rcvY9Interface", 	"1후판압연전단 Book-Out요구"	}
			
			,{	"YDYDJ710",	"PlateReviseFaEJB", "rcvY9CrnSchMain",	"1후판정정 크레인스케쥴"      ,"S"}
			,{	"YDYDJ720",	"PlateReviseFaEJB", "rcvY9Interface",	"1후판정정 크레인작업지시요구" ,"S"}
			
			//	2후판정정야드 - 내부인터페이스
			,{	"YDYDJ770",	"JPlateYdL3RcvFaEJB", "rcvYdPcarWrkEnd",	"2후판정정야드상차완료실적수신", "F" }
			
//SJH16			
			//	1후판정정야드 - 1후판정정야드 수신전문
			,{	"Y2YDL001",	"JPlateYdL2RcvFaEJB", "rcvY2StrLocSpecReq",	"1후판정정 저장위치제원요구" }
			,{	"Y2YDL002",	"JPlateYdL2RcvFaEJB", "rcvY2StockSpecReq",	"1후판정정 저장품제원요구" }
			,{	"Y2YDL003",	"JPlateYdL2RcvFaEJB", "rcvY2EqpDrvModeChg",	"1후판정정 설비운전모드전환" }
			,{	"Y2YDL004",	"JPlateYdL2RcvFaEJB", "rcvY2EqpTrblRcvrWr",	"1후판정정 설비고장복구실적" }
			,{	"Y2YDL007",	"JPlateYdL2RcvFaEJB", "rcvY2CrnWrkOrdReq",	"1후판정정 크레인작업지시요구"	}
			,{  "Y2YDL008",	"JPlateYdL2RcvFaEJB", "rcvY2CrnUpWr", 		"1후판정정 권상실적" }
			,{  "Y2YDL009",	"JPlateYdL2RcvFaEJB", "rcvY2CrnDownWr", 	"1후판정정 권하실적" }
			,{  "Y2YDL010",	"JPlateYdL2RcvFaEJB", "rcvY2OffCrnUpWr", 	"1후판정정 강제권상요구" }
			,{  "Y2YDL011",	"JPlateYdL2RcvFaEJB", "rcvY2OffCrnDownWr", 	"1후판정정 강제권하요구"	}
			,{  "Y2YDL012",	"JPlateYdL2RcvFaEJB", "rcvY2CrnOrderSel", 	"1후판정정 크레인명령선택"	}
			,{  "Y2YDL013",	"JPlateYdL2RcvFaEJB", "rcvY2PilingRslt", 	"1후판정정 파일링실적"	}
			,{  "Y2YDL014",	"JPlateYdL2RcvFaEJB", "rcvY2BookInOutRslt",	"1후판정정  Book-In/Book-Out 실적"	}
			,{  "Y2YDL015",	"JPlateYdL2RcvFaEJB", "rcvY2YDL015",		"1후판정정 후판L2제품번호요구"	}
			,{  "Y2YDL016",	"JPlateYdL2RcvFaEJB", "rcvY2YDL016",		"1후판정정 저장위치제원정보수신"	}
			//	1후판정정야드 - 후판조업L3 수신전문
			,{  "PRYDJ014",	"JPlateYdL3RcvFaEJB", "rcvPRRentCutResult", "1후판정정 임가공절단장 절단실적"	}
			,{  "PRYDJ015",	"JPlateYdL3RcvFaEJB", "rcvPRNextDeciInfo", 	"1후판정정 차행선결정정보"	}
			,{  "PRYDJ016",	"JPlateYdL3RcvFaEJB", "rcvPRYDJ016", 		"1후판정정 설비완료실적"	}
			
			//	1후판정정야드 - 1후판전단L2 수신전문
			,{ 	"P2YDL501",	"JPlateYdL2RcvFaEJB", "rcvP2BookInOutReq",	"1후판전단 Book-In/Book-Out요구" }

			//	1후판정정야드 - 1후판압연L2 수신전문
			,{ 	"P2YDL601",	"JPlateYdL2RcvFaEJB", "rcvP2YDL601",		"1후판압연 Book-In/Book-Out요구" }
			
			//	1후판정정야드 - 1후판열처리L2 수신전문
			,{ 	"P3YDL501",	"JPlateYdL2RcvFaEJB", "rcvP3BookInOutReq",	"1후판열처리 Book-In/Book-Out요구" }

//PIDEV
			,{ "M10LMYDJ1082",	"CarMvHdFaEJB", "rcvPlGdsDistCarLevWr", "후판제품출하차량출발실적" }

//-----------------------------------------------------------------------------------------------------------------
// 2024.11.05 1후판 정정 2열처리 Book-In/Book-Out 요구 L2 수신 전문 추가			
//-----------------------------------------------------------------------------------------------------------------
			,{ 	"P8YDL501",	"JPlateYdL2RcvFaEJB", "rcvP8BookInOutReq",	"1후판 2열처리 Book-In/Book-Out요구" }
			
			// ┏━━━━━━━━━━┓
			//  End of YdRcvTcMap
			// ┗━━━━━━━━━━┛
	}; // end szTcMap

  //---------------------------------------------------------------------------
} // end of class YdRcvTcMap
