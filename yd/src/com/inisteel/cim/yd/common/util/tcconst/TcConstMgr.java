/**
 * @(#)TcConstMgr.java
 * 
 * @version			1.0
 * @author 			YHWHman
 * @date			
 * 
 * @description		TC Code를 기반으로 송신 할 TC Code 생성 클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.01  2012.12.17   조병기      조병기      Y8 : 2후판제품야드L2 추가
 * V1.1   2021.01.06   윤재광      김광철      Y9 : 전사물류시스템개선 통합후판제품 크레인자동화 추가
 */

package com.inisteel.cim.yd.common.util.tcconst;

import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;


/**
 * TC Code를 기반으로 송신 할 TC Code 생성
 * @author YHWHman
 *
 */
public class TcConstMgr {

	private String szSessionName =getClass().getName();
	private YdUtils ydUtils =new YdUtils();
	

	
	
	
	
	/**
	 * 수신 한 Record를 기반으로 송신 할 TC Data 생성
	 * 
	 * @param msgRecord, tcRecSet
	 * @return 생성 한 Key의 갯수
	 */
	public int makeTc(JDTORecord msgRecord, JDTORecordSet tcRecSet){
		
		String szMsg="";
		String szMethodName ="makeTc";
		int nRtc=-99;
		
		String szTcCode="";
		
		try{
			
			szTcCode =ydUtils.getTcCode(msgRecord);
			if( szTcCode==null || szTcCode.equals("")){
				szMsg="TC Code("+szTcCode+") Error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				return -2;
				
			}			
			
		}catch(Exception e){
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			return -1;
			
		} // end of try-catch		
			
		
		// ┏━━━━━━━━━━━━━━━┓
		//     C3 : C연주정정L2 - 8
		// ┗━━━━━━━━━━━━━━━┛
		
		// YDC3L001	수불구변경응답
		if( szTcCode.equals("YDC3L001"))
			return (MakeTcC3.makeC3L001(msgRecord, tcRecSet));
		
		// YDC3L002	OhcTake-Out완료	
		else if(szTcCode.equals("YDC3L002"))
			return (MakeTcC3.makeC3L002(msgRecord, tcRecSet));
		
		// YDC3L003	Carry-Out완료	
		else if(szTcCode.equals("YDC3L003"))
			return (MakeTcC3.makeC3L003(msgRecord, tcRecSet));
		
		// YDC3L004	Carry-In완료	
		else if(szTcCode.equals("YDC3L004"))
			return (MakeTcC3.makeC3L004(msgRecord, tcRecSet));
		
		// YDC3L005	Carry-In재료정보	
		else if(szTcCode.equals("YDC3L005"))
			return (MakeTcC3.makeC3L005(msgRecord, tcRecSet));
		
		// YDC3L006	대차출발지시	
		else if(szTcCode.equals("YDC3L006"))
			return (MakeTcC3.makeC3L006(msgRecord, tcRecSet));
			
		// YDC3L007	대차작업실적	
		else if(szTcCode.equals("YDC3L007"))
			return (MakeTcC3.makeC3L007(msgRecord, tcRecSet));
		
		// YDC3L008	OhcTake-In완료
		else if(szTcCode.equals("YDC3L008"))
			return (MakeTcC3.makeC3L008(msgRecord, tcRecSet));
		
		// YDC3L009	열연재열재 재료정보
		else if(szTcCode.equals("YDC3L009"))
			return (MakeTcC3.makeC3L009(msgRecord, tcRecSet));

		
		// ┏━━━━━━━━━━━━━━━┓
		//     C7 : C연주2정정L2 - 4
		// ┗━━━━━━━━━━━━━━━┛
		
		// YDC7L001	수불구변경응답
		else if( szTcCode.equals("YDC7L001"))
			return (MakeTcC7.makeC7L001(msgRecord, tcRecSet));
		
		// YDC7L002	OhcTake-Out완료	
		else if(szTcCode.equals("YDC7L002"))
			return (MakeTcC7.makeC7L002(msgRecord, tcRecSet));
		
		// YDC7L003	Carry-Out완료	
		else if(szTcCode.equals("YDC7L003"))
			return (MakeTcC7.makeC7L003(msgRecord, tcRecSet));
		
		// YDC7L008	OhcTake-In완료
		else if(szTcCode.equals("YDC7L008"))
			return (MakeTcC7.makeC7L008(msgRecord, tcRecSet));

		
		// ┏━━━━━━━━━━━━━━━━┓
		//     CS : 연주조업 - 1 
		// ┗━━━━━━━━━━━━━━━━┛

		// YDCSJ001 슬라브수입실적
		else if(szTcCode.equals("YDCSJ001"))
			return (MakeTcCS.makeCSJ001(msgRecord, tcRecSet));	
		// YDCSJ002 슬라브이송지시요구
		else if(szTcCode.equals("YDCSJ002"))
			return (MakeTcCS.makeCSJ002(msgRecord, tcRecSet));	
		
		
		
		
		
		// ┏━━━━━━━━━━━━━━━┓
		//     E9 : 항만정정L2 - 4            : 항만슬라브야드 기능추가 - 2016.01.07 LeeJY
		// ┗━━━━━━━━━━━━━━━┛
		
		// YDE9L001	수불구변경응답
		else if( szTcCode.equals("YDE9L001"))
			return (MakeTcC3.makeC3L001(msgRecord, tcRecSet));
		
		// YDE9L003	Carry-Out완료	
		else if(szTcCode.equals("YDE9L003"))
			return (MakeTcC3.makeC3L003(msgRecord, tcRecSet));
		
		// YDE9L004	Carry-In완료	
		else if(szTcCode.equals("YDE9L004"))
			return (MakeTcC3.makeC3L004(msgRecord, tcRecSet));
		
		// YDE9L005	Carry-In재료정보	
		else if(szTcCode.equals("YDE9L005"))
			return (MakeTcC3.makeC3L005(msgRecord, tcRecSet));

		
		
		
		// ┏━━━━━━━━━━━━━━━━┓
		//     CS : 생산통제 - 3
		// ┗━━━━━━━━━━━━━━━━┛
		// YDCTJ021 후판제품저장계획	
		else if(szTcCode.equals("YDCTJ021"))
			return (MakeTcCT.makeCTJ021(msgRecord, tcRecSet));	
		
		// YDCTJ031 후판장입진행실적	
		else if(szTcCode.equals("YDCTJ031"))
			return (MakeTcCT.makeCTJ031(msgRecord, tcRecSet));	
		
		// YDCTJ033 C열연장입진행실적
		else if(szTcCode.equals("YDCTJ033"))
			return (MakeTcCT.makeCTJ033(msgRecord, tcRecSet));	

		// YDCTJ034 연주/후판슬라브야드 이송하차실적 
		else if(szTcCode.equals("YDCTJ034"))
			return (MakeTcCT.makeCTJ034(msgRecord, tcRecSet));	
		// YDCTJ035 연주/후판슬라브야드 이상재등록/해제 
		else if(szTcCode.equals("YDCTJ035"))
			return (MakeTcCT.makeCTJ035(msgRecord, tcRecSet));	
		
		
		
		
		// ┏━━━━━━━━━━━━━━━━┓
		//     DM : 출하관리 - 22 
		// ┗━━━━━━━━━━━━━━━━┛
		
		// YDDMR001	코일입고작업실적          
		else if(szTcCode.equals("YDDMR001"))
			return (MakeTcDM.makeDMR001(msgRecord, tcRecSet));	
		
		// YDDMR002	후판입고작업실적    
		else if(szTcCode.equals("YDDMR002"))
			return (MakeTcDM.makeDMR002(msgRecord, tcRecSet));	
		         
		// YDDMR003	임가공입고작업실적   
		else if(szTcCode.equals("YDDMR003"))
			return (MakeTcDM.makeDMR003(msgRecord, tcRecSet));	
		        
		// YDDMR004	코일제품이적작업실적   
		else if(szTcCode.equals("YDDMR004"))
			return (MakeTcDM.makeDMR004(msgRecord, tcRecSet));	
		      
		// YDDMR005	후판제품이적작업실적   
		else if(szTcCode.equals("YDDMR005"))
			return (MakeTcDM.makeDMR005(msgRecord, tcRecSet));	
		      
		// YDDMR006	후판제품선별작업실적   
		else if(szTcCode.equals("YDDMR006"))
			return (MakeTcDM.makeDMR006(msgRecord, tcRecSet));	
		      
		// YDDMR007	코일출하상차개시    
		else if(szTcCode.equals("YDDMR007"))
			return (MakeTcDM.makeDMR007(msgRecord, tcRecSet));	
		         
		// YDDMR008	후판출하상차개시     
		else if(szTcCode.equals("YDDMR008"))
			return (MakeTcDM.makeDMR008(msgRecord, tcRecSet));	
		        
		// YDDMR009	외판슬라브출하상차개시      
		else if(szTcCode.equals("YDDMR009"))
			return (MakeTcDM.makeDMR009(msgRecord, tcRecSet));	
		 
		// YDDMR010	SLAB 운송LOT 편성정보 수신       
		else if(szTcCode.equals("YDDMR010"))
			return (MakeTcDM.makeDMR010(msgRecord, tcRecSet));	
		        
		// YDDMR011	코일일품출하상차실적         
		else if(szTcCode.equals("YDDMR011"))
			return (MakeTcDM.makeDMR011(msgRecord, tcRecSet));	
		
		// YDDMR012	후판일품출하상차실적         
		else if(szTcCode.equals("YDDMR012"))
			return (MakeTcDM.makeDMR012(msgRecord, tcRecSet));	
		
		// YDDMR013	외판슬라브일품출하상차실적   
		else if(szTcCode.equals("YDDMR013"))
			return (MakeTcDM.makeDMR013(msgRecord, tcRecSet));	
		
		// YDDMR014	임가공일품출하상차실적      
		else if(szTcCode.equals("YDDMR014"))
			return (MakeTcDM.makeDMR014(msgRecord, tcRecSet));	
		 
		// YDDMR015	코일출하상차완료       
		else if(szTcCode.equals("YDDMR015"))
			return (MakeTcDM.makeDMR015(msgRecord, tcRecSet));	
		      
		// YDDMR016	후판출하상차완료           
		else if(szTcCode.equals("YDDMR016"))
			return (MakeTcDM.makeDMR016(msgRecord, tcRecSet));	
		  
		// YDDMR017	외판슬라브출하상차완료    
		else if(szTcCode.equals("YDDMR017"))
			return (MakeTcDM.makeDMR017(msgRecord, tcRecSet));	
		   
		// YDDMR018	임가공출하상차완료        	   
		else if(szTcCode.equals("YDDMR018"))
			return (MakeTcDM.makeDMR018(msgRecord, tcRecSet));	
		
		// YDDMR019	코일제품고간이송상하차개시   
		else if(szTcCode.equals("YDDMR019"))
			return (MakeTcDM.makeDMR019(msgRecord, tcRecSet));	
		
		// YDDMR020	임가공이송상하차개시(추가)   
		else if(szTcCode.equals("YDDMR020"))
			return (MakeTcDM.makeDMR020(msgRecord, tcRecSet));	
		
		// YDDMR021	코일제품고간이송상하차완료(추가)   
		else if(szTcCode.equals("YDDMR021"))
			return (MakeTcDM.makeDMR021(msgRecord, tcRecSet));	
		
		// YDDMR022	임가공이송상하차완료(추가)   
		else if(szTcCode.equals("YDDMR022"))
			return (MakeTcDM.makeDMR022(msgRecord, tcRecSet));	
		
		// YDDMR024	HYSCO대차이송실적(추가:20090814)   
		else if(szTcCode.equals("YDDMR024"))
			return (MakeTcDM.makeDMR024(msgRecord, tcRecSet));	

		// YDDMR025	HYSCO수냉실적(추가:20090814)   
		else if(szTcCode.equals("YDDMR025"))
			return (MakeTcDM.makeDMR025(msgRecord, tcRecSet));	

		// YDDMR026	포인트사용실적(추가:20090714)   
		else if(szTcCode.equals("YDDMR026"))
			return (MakeTcDM.makeDMR026(msgRecord, tcRecSet));	
		
		// YDDMR027	검수완료실적(추가:20090805)   
		else if(szTcCode.equals("YDDMR027"))
			return (MakeTcDM.makeDMR027(msgRecord, tcRecSet));	

		// YDDMR028	차량입동지시(추가:20090805)   
		else if(szTcCode.equals("YDDMR028"))
			return (MakeTcDM.makeDMR028(msgRecord, tcRecSet));	

		// YDDMR030	후판제품고간이송상차개시(추가:20090923)   
		else if(szTcCode.equals("YDDMR030"))
			return (MakeTcDM.makeDMR030(msgRecord, tcRecSet));	
		
		// YDDMR031	후판제품고간이송상차완료(추가:20090923)   
		else if(szTcCode.equals("YDDMR031"))
			return (MakeTcDM.makeDMR031(msgRecord, tcRecSet));
		
		else if(szTcCode.equals("YDDMR034"))
			return (MakeTcDM.makeDMR034(msgRecord, tcRecSet));
		
		else if(szTcCode.equals("YDDMR050"))
			return (MakeTcDM.makeDMR050(msgRecord, tcRecSet));
		
		else if(szTcCode.equals("YDDMR036"))
			return (MakeTcDM.makeDMR036(msgRecord, tcRecSet));
		
		else if(szTcCode.equals("YDDMR071"))
			return (MakeTcDM.makeDMR071(msgRecord, tcRecSet));
		
		else if(szTcCode.equals("YDDMR072"))
			return (MakeTcDM.makeDMR072(msgRecord, tcRecSet));
		
		else if(szTcCode.equals("YDDMR073"))
			return (MakeTcDM.makeDMR073(msgRecord, tcRecSet));
		
		else if(szTcCode.equals("YDDMR074"))
			return (MakeTcDM.makeDMR036(msgRecord, tcRecSet));
		
		else if(szTcCode.equals("YDDMR075"))
			return (MakeTcDM.makeDMR075(msgRecord, tcRecSet));
		
		else if(szTcCode.equals("YDDMR076"))
			return (MakeTcDM.makeDMR076(msgRecord, tcRecSet));
		
		// ┏━━━━━━━━━━━━━━━┓
		//     HR : 열연조업 - 1 
		// ┗━━━━━━━━━━━━━━━┛
		
		// YDHRJ001 열연정정보급완료실적	  
		else if(szTcCode.equals("YDHRJ001"))
			return (MakeTcHR.makeHRJ001(msgRecord, tcRecSet));	
		
		// YDHRJ002 열연정정추출완료실적 (추가:20090814) 
		else if(szTcCode.equals("YDHRJ002"))
			return (MakeTcHR.makeHRJ002(msgRecord, tcRecSet));	
		
		// YDHRJ003 열연조업 시편채취권상실적 (추가:20090814) 
		else if(szTcCode.equals("YDHRJ003"))
			return (MakeTcHR.makeHRJ003(msgRecord, tcRecSet));	
		
		// YDHRJ005 열연정정보급완료실적	-이퀄라이저  
		else if(szTcCode.equals("YDHRJ005"))
			return (MakeTcHR.makeHRJ005(msgRecord, tcRecSet));	
		
		// YDHRJ006 열연정정추출완료실적	-이퀄라이저  
		else if(szTcCode.equals("YDHRJ006"))
			return (MakeTcHR.makeHRJ006(msgRecord, tcRecSet));
		
		// ┏━━━━━━━━━━━━━━━┓
		//     P2 : 후판압연전단L2 - 2 
		// ┗━━━━━━━━━━━━━━━┛
		
		// YDP2L001	Routing Layout 지시 - Book-Out위치변경요구		  
		else if(szTcCode.equals("YDP2L001"))
			return (MakeTcP2.makeP2L001(msgRecord, tcRecSet));	
		

		
		// ┏━━━━━━━━━━━━━━━┓
		//     PR : 후판조업 - 2 
		// ┗━━━━━━━━━━━━━━━┛
		
		
		// YDPRJ001 후판제품반납하차실적 		  
		else if(szTcCode.equals("YDPRJ001"))
			return (MakeTcPR.makePRJ001(msgRecord, tcRecSet));	
		
		// YDPRJ002 후판제품입고상차실적			  
		else if(szTcCode.equals("YDPRJ002"))
			return (MakeTcPR.makePRJ002(msgRecord, tcRecSet));		

		// YDPRJ003 후판재열재슬라브적치실적			  
		else if(szTcCode.equals("YDPRJ003"))
			return (MakeTcPR.makePRJ003(msgRecord, tcRecSet));		
		
//		 YDPRJ004 후판정정야드 위치변경실적			  
		else if(szTcCode.equals("YDPRJ004"))
			return (MakeTcPR.makePRJ004(msgRecord, tcRecSet));	
		
		
		// ┏━━━━━━━━━━━━━━━┓
		//     PP : 2후판조업 - 2 
		// ┗━━━━━━━━━━━━━━━┛

		// YDPPJ001 2후판제품반납하차실적 		  
		else if(szTcCode.equals("YDPPJ001"))
			return (MakeTcPP.makePPJ001(msgRecord, tcRecSet));	
		
		
		
		// ┏━━━━━━━━━━━━━━━┓
		//     PT :  - 4 
		// ┗━━━━━━━━━━━━━━━┛		
		
		// YDPTJ001 슬라브소재이송완료실적			  
		else if(szTcCode.equals("YDPTJ001"))
			return (MakeTcPT.makePTJ001(msgRecord, tcRecSet));		
		
		// YDPTJ002 코일소재이송완료실적				  
		else if(szTcCode.equals("YDPTJ002"))
			return (MakeTcPT.makePTJ002(msgRecord, tcRecSet));		
		
		// YDPTJ003 코일소재임가공이송완료실적				  
		else if(szTcCode.equals("YDPTJ003"))
			return (MakeTcPT.makePTJ003(msgRecord, tcRecSet));		
		
		// YDPTJ004 구입슬라브입고실적				  
		else if(szTcCode.equals("YDPTJ004"))
			return (MakeTcPT.makePTJ004(msgRecord, tcRecSet));		
		
		// YDPTJ005 후판오버롤 체크				  
		else if(szTcCode.equals("YDPTJ005"))
			return (MakeTcPT.makePTJ005(msgRecord, tcRecSet));		
		
		// YDPTJ006 냉연코일이송진행 상태실적  체크				  
		else if(szTcCode.equals("YDPTJ006"))
			return (MakeTcPT.makePTJ006(msgRecord, tcRecSet));	
		
		// ┏━━━━━━━━━━━━━━━┓
		//     QM : 품질 - 1 
		// ┗━━━━━━━━━━━━━━━┛
		
		// YDQMJ002 열연정정입측보급실적	  
		else if(szTcCode.equals("YDQMJ002"))
			return (MakeTcQM.makeQMJ001(msgRecord, tcRecSet));	
		
//PIDEV_QM		
		// YDQMJ601 후판품질입고실적	  
		else if(szTcCode.equals("YDQMJ601"))
			return (MakeTcQM.makeQMJ601(msgRecord, tcRecSet));	
		

		// ┏━━━━━━━━━━━━━━━┓
		//     H1 : C열연MillL2 - 1
		// ┗━━━━━━━━━━━━━━━┛		

		// YDH1L001 압연분기Line-Off실적				  
		else if(szTcCode.equals("YDH1L001"))
			return (MakeTcH1.makeH1L001(msgRecord, tcRecSet));		
		
		// YDH1L002 재열재 Take-Out 완료				  
		else if(szTcCode.equals("YDH1L002"))
			return (MakeTcH1.makeH1L002(msgRecord, tcRecSet));		

		// ┏━━━━━━━━━━━━━━━┓
		//     H2 : C열연정정L2 - 4
		// ┗━━━━━━━━━━━━━━━┛		
		
		// YDH2L001	SPM1 정정입측Line-In실적송신				  
		else if(szTcCode.equals("YDH2L001"))
			return (MakeTcH2.makeH2L001(msgRecord, tcRecSet));
		
		// YDH2L003	SPM1 정정출측Line-Off실적송신				  
		else if(szTcCode.equals("YDH2L003"))
			return (MakeTcH2.makeH2L003(msgRecord, tcRecSet));

		// YDH2L004	SPM1 정정Take-Out실적송신				  
		else if(szTcCode.equals("YDH2L004"))
			return (MakeTcH2.makeH2L004(msgRecord, tcRecSet));

		// YDH2L011	HFL 정정입측Line-In실적송신				  
		else if(szTcCode.equals("YDH2L011"))
			return (MakeTcH2.makeH2L011(msgRecord, tcRecSet));
		
		// YDH2L013	HFL 정정출측Line-Off실적송신				  
		else if(szTcCode.equals("YDH2L013"))
			return (MakeTcH2.makeH2L013(msgRecord, tcRecSet));

		// YDH2L014	HFL 정정Take-Out실적송신				  
		else if(szTcCode.equals("YDH2L014"))
			return (MakeTcH2.makeH2L014(msgRecord, tcRecSet));

		// YDH2L021	SPM2 정정입측Line-In실적송신				  
		else if(szTcCode.equals("YDH2L021"))
			return (MakeTcH2.makeH2L021(msgRecord, tcRecSet));
		
		// YDH2L023	SPM2 정정출측Line-Off실적송신				  
		else if(szTcCode.equals("YDH2L023"))
			return (MakeTcH2.makeH2L023(msgRecord, tcRecSet));

		// YDH2L024	SPM2 정정Take-Out실적송신				  
		else if(szTcCode.equals("YDH2L024"))
			return (MakeTcH2.makeH2L024(msgRecord, tcRecSet));
		
//C증설
		// YDH2L031	SPM3 정정입측Line-In실적송신				  
		else if(szTcCode.equals("YDH2L031"))
			return (MakeTcH2.makeH2L031(msgRecord, tcRecSet));
		
		// YDH2L033	SPM3 정정출측Line-Off실적송신				  
		else if(szTcCode.equals("YDH2L033"))
			return (MakeTcH2.makeH2L033(msgRecord, tcRecSet));

		// YDH2L034	SPM3 정정Take-Out실적송신				  
		else if(szTcCode.equals("YDH2L034"))
			return (MakeTcH2.makeH2L034(msgRecord, tcRecSet));

		// YDH2L041	SPM4 정정입측Line-In실적송신				  
		else if(szTcCode.equals("YDH2L041"))
			return (MakeTcH2.makeH2L041(msgRecord, tcRecSet));
		
		// YDH2L071	SPM5 정정입측Line-In실적송신				  
		else if(szTcCode.equals("YDH2L071"))
			return (MakeTcH2.makeH2L071(msgRecord, tcRecSet));
		
		// YDH2L043	SPM4 정정출측Line-Off실적송신				  
		else if(szTcCode.equals("YDH2L043"))
			return (MakeTcH2.makeH2L043(msgRecord, tcRecSet));
		
		// YDH2L073	SPM5 정정출측Line-Off실적송신				  
		else if(szTcCode.equals("YDH2L073"))
			return (MakeTcH2.makeH2L073(msgRecord, tcRecSet));

		// YDH2L044	SPM4 정정Take-Out실적송신				  
		else if(szTcCode.equals("YDH2L044"))
			return (MakeTcH2.makeH2L044(msgRecord, tcRecSet));

		// YDH2L044	SPM5 정정Take-Out실적송신				  
		else if(szTcCode.equals("YDH2L074"))
			return (MakeTcH2.makeH2L074(msgRecord, tcRecSet));

		// YDH2L051	HFL4 정정입측Line-In실적송신				  
		else if(szTcCode.equals("YDH2L051"))
			return (MakeTcH2.makeH2L051(msgRecord, tcRecSet));
		
		// YDH2L053	HFL4 정정출측Line-Off실적송신				  
		else if(szTcCode.equals("YDH2L053"))
			return (MakeTcH2.makeH2L053(msgRecord, tcRecSet));

		// YDH2L054	HFL4 정정Take-Out실적송신				  
		else if(szTcCode.equals("YDH2L054"))
			return (MakeTcH2.makeH2L054(msgRecord, tcRecSet));
		
		// YDH2L061	HFL5 정정입측Line-In실적송신				  
		else if(szTcCode.equals("YDH2L061"))
			return (MakeTcH2.makeH2L061(msgRecord, tcRecSet));
		
		// YDH2L063	HFL5 정정출측Line-Off실적송신				  
		else if(szTcCode.equals("YDH2L063"))
			return (MakeTcH2.makeH2L063(msgRecord, tcRecSet));

		// YDH2L064	HFL3 정정Take-Out실적송신				  
		else if(szTcCode.equals("YDH2L064"))
			return (MakeTcH2.makeH2L064(msgRecord, tcRecSet));
		
		
		// ┏━━━━━━━━━━━━━━━┓
		//     TS : 구내운송 - 7
		// ┗━━━━━━━━━━━━━━━┛		
		
		// YDTSJ007 소재차량상차개시	  
		else if(szTcCode.equals("YDTSJ007"))
			return (MakeTcTS.makeTSJ007(msgRecord, tcRecSet));		
			
		// YDTSJ008 소재차량상차완료		  
		else if(szTcCode.equals("YDTSJ008"))
			return (MakeTcTS.makeTSJ008(msgRecord, tcRecSet));		
		
		// YDTSJ009 소재차량하차개시		  
		else if(szTcCode.equals("YDTSJ009"))
			return (MakeTcTS.makeTSJ009(msgRecord, tcRecSet));		
		
		// YDTSJ010 소재차량하차완료		  
		else if(szTcCode.equals("YDTSJ010"))
			return (MakeTcTS.makeTSJ010(msgRecord, tcRecSet));		
		
		// YDTSJ011 소재차량Point지시		  
		else if(szTcCode.equals("YDTSJ011"))
			return (MakeTcTS.makeTSJ011(msgRecord, tcRecSet));		
		
		// YDTSJ012 소재차량Point개폐		  
		else if(szTcCode.equals("YDTSJ012"))
			return (MakeTcTS.makeTSJ012(msgRecord, tcRecSet));		
		
		// YDTSJ013 소재차량상하차지연사유	  
		else if(szTcCode.equals("YDTSJ013"))
			return (MakeTcTS.makeTSJ013(msgRecord, tcRecSet));		

		// YDTSJ014 여재 Slab 소재운송요구	  
		else if(szTcCode.equals("YDTSJ014"))
			return (MakeTcTS.makeTSJ014(msgRecord, tcRecSet));		
			
		// YDTSJ015 제품운송요구(후판제품이송지시)	  
		else if(szTcCode.equals("YDTSJ015"))
			return (MakeTcTS.makeTSJ015(msgRecord, tcRecSet));			
		
		
		

		// ┏━━━━━━━━━━━━━━━┓
		//     Y1 : C연주슬라브야드L2 - 5
		// ┗━━━━━━━━━━━━━━━┛		

		// YDY1L001	저장위치제원			  
		else if(szTcCode.equals("YDY1L001"))
			return (MakeTcY1.makeY1L001(msgRecord, tcRecSet));		
		
		// YDY1L002	저장품제원			  
		else if(szTcCode.equals("YDY1L002"))
			return (MakeTcY1.makeY1L002(msgRecord, tcRecSet));		
		
		// YDY1L003	크레인작업계획 		  
		else if(szTcCode.equals("YDY1L003"))
			return (MakeTcY1.makeY1L003(msgRecord, tcRecSet));		
			
		// YDY1L004	크레인작업지시			  
		else if(szTcCode.equals("YDY1L004"))
			return (MakeTcY1.makeY1L004(msgRecord, tcRecSet));		
		
		// YDY1L005	크레인작업실적응답		  
		else if(szTcCode.equals("YDY1L005"))
			return (MakeTcY1.makeY1L005(msgRecord, tcRecSet));		
			
		// YDY1LXX2	저장품 제원 일괄지시 		  
		else if(szTcCode.equals("YDY1LXX2"))
			return (MakeTcY1.makeY1LXX2(msgRecord, tcRecSet));	
		
		
		
		
		// ┏━━━━━━━━━━━━━━━┓
		//     E7 : 항만슬라브야드L2 - 4        : //항만슬라브야드 기능추가 - 2016.01.07 LeeJY
		// ┗━━━━━━━━━━━━━━━┛		

		// YDE7L001	저장위치제원			  
		else if(szTcCode.equals("YDE7L001"))
			return (MakeTcY1.makeY1L001(msgRecord, tcRecSet));		
		
		// YDE7L002	저장품제원			  
		else if(szTcCode.equals("YDE7L002"))
			return (MakeTcY1.makeY1L002(msgRecord, tcRecSet));		
		
		// YDE71L004	크레인작업지시			  
		else if(szTcCode.equals("YDE7L004"))
			return (MakeTcY1.makeY1L004(msgRecord, tcRecSet));		
		
		// YDE7L005	크레인작업실적응답		  
		else if(szTcCode.equals("YDE7L005"))
			return (MakeTcY1.makeY1L005(msgRecord, tcRecSet));		
		
		
		// ┏━━━━━━━━━━━━━━━┓
		//     Y3 : A후판슬라브야드L2 - 6
		// ┗━━━━━━━━━━━━━━━┛	
		
		// YDY3L001	저장위치제원		  
		else if(szTcCode.equals("YDY3L001"))
			return (MakeTcY3.makeY3L001(msgRecord, tcRecSet));		
			
		// YDY3L002	저장품제원		  
		else if(szTcCode.equals("YDY3L002"))
			return (MakeTcY3.makeY3L002(msgRecord, tcRecSet));		
			
		// YDY3L003	크레인작업계획 	  
		else if(szTcCode.equals("YDY3L003"))
			return (MakeTcY3.makeY3L003(msgRecord, tcRecSet));		
				
		// YDY3L004	크레인작업지시	  
		else if(szTcCode.equals("YDY3L004"))
			return (MakeTcY3.makeY3L004(msgRecord, tcRecSet));		
				
		// YDY3L005	크레인작업실적응답	  
		else if(szTcCode.equals("YDY3L005"))
			return (MakeTcY3.makeY3L005(msgRecord, tcRecSet));		

		// YDY3L006	대차출발지시	  
		else if(szTcCode.equals("YDY3L006"))
			return (MakeTcY3.makeY3L006(msgRecord, tcRecSet));		

		// YDY3LXX2	저장품 제원 일괄지시 		  
		else if(szTcCode.equals("YDY3LXX2"))
			return (MakeTcY3.makeY3LXX2(msgRecord, tcRecSet));								

		// ┏━━━━━━━━━━━━━━━┓
		//     Y4 : 후판제품야드L2 - 4
		// ┗━━━━━━━━━━━━━━━┛	
		
		// YDY4L001	저장위치제원	  
		else if(szTcCode.equals("YDY4L001"))
			return (MakeTcY4.makeY4L001(msgRecord, tcRecSet));		
					
		// YDY4L002	저장품제원  
		else if(szTcCode.equals("YDY4L002"))
			return (MakeTcY4.makeY4L002(msgRecord, tcRecSet));		
						
		// YDY4L004	크레인작업지시  
		else if(szTcCode.equals("YDY4L004"))
			return (MakeTcY4.makeY4L004(msgRecord, tcRecSet));		
						
		// YDY4L005	크레인작업실적응답	  
		else if(szTcCode.equals("YDY4L005"))
			return (MakeTcY4.makeY4L005(msgRecord, tcRecSet));		
				
		// ┏━━━━━━━━━━━━━━━┓
		//     Y8 : 후판제품통합야드
		// ┗━━━━━━━━━━━━━━━┛	
		
		// YDY8L001	저장위치제원	  
		else if(szTcCode.equals("YDY8L001"))
			return (MakeTcY8.makeY8L001(msgRecord, tcRecSet));		
					
		// YDY8L002	저장품제원  
		else if(szTcCode.equals("YDY8L002"))
			return (MakeTcY8.makeY8L002(msgRecord, tcRecSet));		
						
		// YDY8L004	크레인작업지시  
		else if(szTcCode.equals("YDY8L004"))
			return (MakeTcY8.makeY8L004(msgRecord, tcRecSet));		
						
		// YDY8L005	크레인작업실적응답	  
		else if(szTcCode.equals("YDY8L005"))
			return (MakeTcY8.makeY8L005(msgRecord, tcRecSet));		
				
		// YDY8L006	ROUTING정보	  
		else if(szTcCode.equals("YDY8L006"))
			return (MakeTcY8.makeY8L006(msgRecord, tcRecSet));		

		// YDY8L007	집중입고알람	  
		else if(szTcCode.equals("YDY8L007"))
			return (MakeTcY8.makeY8L007(msgRecord, tcRecSet));		

		// YDY8L008	RT우선순위 정보	  
		else if(szTcCode.equals("YDY8L008"))
			return (MakeTcY8.makeY8L008(msgRecord, tcRecSet));		

		// YDY8L009	입고작업 정보	  
		else if(szTcCode.equals("YDY8L009"))
			return (MakeTcY8.makeY8L009(msgRecord, tcRecSet));
		
		// YDY8L010	SPAN별 재고현황 정보	  
		else if(szTcCode.equals("YDY8L010"))
			return (MakeTcY8.makeY8L010(msgRecord, tcRecSet));		
		
		
		// ┏━━━━━━━━━━━━━━━┓
		//     Y9 : 후판제품통합야드 자동화크레인
		// ┗━━━━━━━━━━━━━━━┛	
		
		// YDY8L001	저장위치제원	  
		else if(szTcCode.equals("YDY9L001"))
			return (MakeTcY9.makeY9L001(msgRecord, tcRecSet));		
					
		// YDY8L002	저장품제원  
		else if(szTcCode.equals("YDY9L002"))
			return (MakeTcY9.makeY9L002(msgRecord, tcRecSet));		
						
		// YDY8L004	크레인작업지시  
		else if(szTcCode.equals("YDY9L004"))
			return (MakeTcY9.makeY9L004(msgRecord, tcRecSet));		
						
		// YDY8L005	크레인작업실적응답	  
		else if(szTcCode.equals("YDY9L005"))
			return (MakeTcY9.makeY9L005(msgRecord, tcRecSet));		

		// YDY9L010	차량 출하, 입고 크레인작업실적	  
		else if(szTcCode.equals("YDY9L010"))
			return (MakeTcY9.makeY9L010(msgRecord, tcRecSet));		
		
		// YDY9L008	차량예정정보(전문전송방식변경처리함) no_mate_Tc함수이용 
//		else if(szTcCode.equals("YDY9L008"))
//			return (MakeTcY9.makeY9L008(msgRecord, tcRecSet));	
//		
		// ┏━━━━━━━━━━━━━━━┓
		//     S1 : 2후판전단정정L2
		// ┗━━━━━━━━━━━━━━━┛	
		
		// YDS1L004	파일링지시	  
		else if(szTcCode.equals("YDS1L004"))
			return (MakeTcS1.makeS1L004(msgRecord, tcRecSet));		
		
		// ┏━━━━━━━━━━━━━━━┓
		//     Y9 : 1후판전단PM45L2
		//     2020.1.6 사용안함 처리
		// ┗━━━━━━━━━━━━━━━┛	
		             
		// YDY9L001	북인/아웃실적	  
//		else if(szTcCode.equals("YDY9L001"))
//			return (MakeTcY9.makeY9L001(msgRecord, tcRecSet));					
//		// YDY9L002	저장품제원요구	  
//		else if(szTcCode.equals("YDY9L002"))
//			return (MakeTcY9.makeY9L002(msgRecord, tcRecSet));		
//		// YDY9L003	L2 ID정보 요구	  
//		else if(szTcCode.equals("YDY9L003"))
//			return (MakeTcY9.makeY9L003(msgRecord, tcRecSet));
//		// YDY9L104	크레인 작업지시	  
//		else if(szTcCode.equals("YDY9L104"))
//			return (MakeTcY9.makeY9LX04(msgRecord, tcRecSet));
//		// YDY9L204	크레인 작업지시	  
//		else if(szTcCode.equals("YDY9L204"))
//			return (MakeTcY9.makeY9LX04(msgRecord, tcRecSet));
//		// YDY9L304	크레인 작업지시	  
//		else if(szTcCode.equals("YDY9L304"))
//			return (MakeTcY9.makeY9LX04(msgRecord, tcRecSet));
//		// YDY9L404	크레인 작업지시	  
//		else if(szTcCode.equals("YDY9L404"))
//			return (MakeTcY9.makeY9LX04(msgRecord, tcRecSet));
//		// YDY9L105	크레인 작업응답
//		else if(szTcCode.equals("YDY9L105"))
//			return (MakeTcY9.makeY9LX05(msgRecord, tcRecSet));
//		// YDY9L205	크레인 작업응답
//		else if(szTcCode.equals("YDY9L205"))
//			return (MakeTcY9.makeY9LX05(msgRecord, tcRecSet));
//		// YDY9L305	크레인 작업응답
//		else if(szTcCode.equals("YDY9L305"))
//			return (MakeTcY9.makeY9LX05(msgRecord, tcRecSet));
//		// YDY9L405	크레인 작업응답
//		else if(szTcCode.equals("YDY9L405"))
//			return (MakeTcY9.makeY9LX05(msgRecord, tcRecSet));
		
		// ┏━━━━━━━━━━━━━━━┓
		//     Y5 : C열연코일야드L2 - 5
		// ┗━━━━━━━━━━━━━━━┛
		
		// YDY5L001	저장위치제원	
		else if(szTcCode.equals("YDY5L001"))
			return (MakeTcY5.makeY5L001(msgRecord, tcRecSet));		
							
		// YDY5L002	저장품제원	
		else if(szTcCode.equals("YDY5L002"))
			return (MakeTcY5.makeY5L002(msgRecord, tcRecSet));		
							
		// YDY5L004	크레인작업지시	
		else if(szTcCode.equals("YDY5L004"))
			return (MakeTcY5.makeY5L004(msgRecord, tcRecSet));		
		
		// YDY5L004	크레인작업지시Auto	
		else if(szTcCode.equals("YDY5L004Auto"))
			return (MakeTcY5.makeY5L004Auto(msgRecord, tcRecSet));		
							
		// YDY5L005	크레인작업실적응답	
		else if(szTcCode.equals("YDY5L005"))
			return (MakeTcY5.makeY5L005(msgRecord, tcRecSet));		
							
		// YDY5L006	대차출발지시	
		else if(szTcCode.equals("YDY5L006"))
			return (MakeTcY5.makeY5L006(msgRecord, tcRecSet));	
		
		// YDY5L007	C열연코일L2 작업현황응답	
		else if(szTcCode.equals("YDY5L007"))
			return (MakeTcY5.makeY5L007(msgRecord, tcRecSet));	

		// YDY5L008	C열연코일L2 차량작업 예정정보 전송	
		else if(szTcCode.equals("YDY5L008"))
			return (MakeTcY5.makeY5L008(msgRecord, tcRecSet));	
		
		// YDY5L008	C열연코일L2 차량작업 예정정보 전송	
		else if(szTcCode.equals("YDY5L008BACKUP"))
			return (MakeTcY5.makeY5L008BackUp(msgRecord, tcRecSet));	
		
		// YDY5L009	C열연코일L2 이상코일 발생정보 전송
		else if(szTcCode.equals("YDY5L009"))
			return (MakeTcY5.makeY5L009(msgRecord, tcRecSet));	
		// ┏━━━━━━━━━━━━━━━┓
		//     YD : 야드관리 - 1 (공통)
		// ┗━━━━━━━━━━━━━━━┛
		
		
//PIDEV		
		// ┏━━━━━━━━━━━━━━━━┓
		//     LM : 출하관리 - 22 
		// ┗━━━━━━━━━━━━━━━━┛
		
		// YDDMR002	후판입고작업실적    
		else if(szTcCode.equals("M10YDLMJ1012"))
			return (MakeTcLM.makeM10YDLMJ1012(msgRecord, tcRecSet));	
		// YDDMR005	후판제품이적작업실적   
		else if(szTcCode.equals("M10YDLMJ1032"))
			return (MakeTcLM.makeM10YDLMJ1032(msgRecord, tcRecSet));	
        // YDDMR050 후판 야드핸들링정보 
		else if(szTcCode.equals("M10YDLMJ1052"))
			return (MakeTcLM.makeM10YDLMJ1052(msgRecord, tcRecSet));
//		// YDDMR028	차량입동지시  
		else if(szTcCode.equals("M10YDLMJ1062"))
			return (MakeTcLM.makeM10YDLMJ1062(msgRecord, tcRecSet));
//		// 	               차량 선 입동지시(입동지시전 입동 알람차원. 권상완료 시점에 전송)  		
		else if(szTcCode.equals("M10YDLMJ1162"))
			return (MakeTcLM.makeM10YDLMJ1162(msgRecord, tcRecSet));		
//		// YDDMR008	후판출하상차개시     
		else if(szTcCode.equals("M10YDLMJ1072"))
			return (MakeTcLM.makeM10YDLMJ1072(msgRecord, tcRecSet));	
		// YDDMR009	외판슬라브출하상차개시      
		else if(szTcCode.equals("M10YDLMJ1073"))
			return (MakeTcLM.makeM10YDLMJ1073(msgRecord, tcRecSet));	
		// YDDMR012	후판일품출하상차실적         
		else if(szTcCode.equals("M10YDLMJ1082"))
			return (MakeTcLM.makeM10YDLMJ1082(msgRecord, tcRecSet));	
		// YDDMR013	외판슬라브일품출하상차실적   
		else if(szTcCode.equals("M10YDLMJ1083"))
			return (MakeTcLM.makeM10YDLMJ1083(msgRecord, tcRecSet));	
		// YDDMR016	후판출하상차완료           
		else if(szTcCode.equals("M10YDLMJ1092"))
			return (MakeTcLM.makeM10YDLMJ1092(msgRecord, tcRecSet));	
		// YDDMR017	외판슬라브출하상차완료    
		else if(szTcCode.equals("M10YDLMJ1093"))
			return (MakeTcLM.makeM10YDLMJ1093(msgRecord, tcRecSet));
		// YDDMR036	후판 검수완료    
		else if(szTcCode.equals("M10YDLMJ1102"))
			return (MakeTcLM.makeM10YDLMJ1102(msgRecord, tcRecSet));
		
		// ┏━━━━━━━━━━━━━━━┓
		//     YD : 공통 
		// ┗━━━━━━━━━━━━━━━┛
				
		// YDYDJ000		
		else if(szTcCode.substring(0, 4).equals( "YDYD"))
			return (MakeTcYD.makeYDJ000(msgRecord, tcRecSet));
		
		// ┏━━━━━━━━━━━━━━━┓
		//     QM : 품질관리 
		// ┗━━━━━━━━━━━━━━━┛
		
		// PRQMJ502		
		else if(szTcCode.equals( "PRQMJ502"))
			return (MakeTcDM.makeQMJ502(msgRecord, tcRecSet));	
		
		else{
			szMsg="Unknown TC Code() Error ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			return nRtc;
			
		} // end of if-else

	} // end of makeTc()
	
  //---------------------------------------------------------------------------	
} // end of class
