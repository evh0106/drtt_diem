package com.inisteel.cim.yd.common.util.tcconst;


import jspeed.base.log.LogLevel;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;






/**
 * P2 (A후판MillL2) 송신 용 전문 생성
 * @author 
 *
 */
public class MakeTcP2 {

	// YDP2L001	Routing Layout 지시(29008 TL3CRL)
	// YDP2L002	Book In 실적              (29049 TL3CII)


	//클래스명
	private static final String szClassName  = MakeTcP2.class.getName();

	
	/**
	 * YDP2L001 : Routing Layout 지시(29008 TL3CRL)
	 * @param JDTORecord inRec
	 * @return JDTORecord msgRec
	 */
	public static int makeP2L001(JDTORecord inRec, JDTORecordSet outRecSet){
		// 1	Telegram_Length				telegram length (including the header)	String	5        UShort 2
		// 2	Telegram_Id					telegram identification					String	5        UShort 2
		// 3	Sequence_Counter			sequence counter						String	3        UShort 2
		// 4	Flags						Flags (Spare)							String	2        UShort 2
		// 5	Spare1						Spare1									String	2        Short  2
		// 6	Year						Year									String	4        Short  2
		// 7	Month						Month									String	2        Short  2
		// 8	Day							Day										String	2        Short  2
		// 9	Hour						Hour									String	2        Short  2
		//10	Minute						Minute									String	2        Short  2
		//11	Second						Second									String	2        Short  2
		//12	Spare2						Spare2									String	2        Short  2
		
		//13	PL_MPL_NO					날판번호									VARCHAR2	32 
		//14	PL_DIV_TRIM_GP_CD			분할절단구분코드							VARCHAR2	4 
		//15	PL_DIV_TRIM_GP_SEQ			후판분할절단구분순번						NUMBER	4 
		//16	PL_TOT_ROUTE_CNT			후판총Routing수							NUMBER	4 
		//17	PL_L2_WO_SND_MD				후판L2지시송신모드							VARCHAR2	4 
		//18	PL_ROUTE_NODE_NO_GROUP1		후판Routing노드번호1						VARCHAR2	4 
		//19	PL_ROUTE_NODE_NO_GROUP2		후판Routing노드번호2						VARCHAR2	4 
		//20	PL_ROUTE_NODE_NO_GROUP3		후판Routing노드번호3						VARCHAR2	4 
		//21	PL_ROUTE_NODE_NO_GROUP4		후판Routing노드번호4						VARCHAR2	4 
		//22	PL_ROUTE_NODE_NO_GROUP5		후판Routing노드번호5						VARCHAR2	4 
		//23	PL_ROUTE_NODE_NO_GROUP6		후판Routing노드번호6						VARCHAR2	4 
		//24	PL_ROUTE_NODE_NO_GROUP7		후판Routing노드번호7						VARCHAR2	4 
		//25	PL_ROUTE_NODE_NO_GROUP8		후판Routing노드번호8						VARCHAR2	4 
		//26	PL_ROUTE_NODE_NO_GROUP9		후판Routing노드번호9						VARCHAR2	4 
		//27	PL_ROUTE_NODE_NO_GROUP10	후판Routing노드번호10						VARCHAR2	4 
		//28	PL_ROUTE_NODE_NO_GROUP11	후판Routing노드번호11						VARCHAR2	4 
		//29	PL_ROUTE_NODE_NO_GROUP12	후판Routing노드번호12						VARCHAR2	4 
		//30	PL_ROUTE_NODE_NO_GROUP13	후판Routing노드번호13						VARCHAR2	4 
		//31	PL_ROUTE_NODE_NO_GROUP14	후판Routing노드번호14						VARCHAR2	4 
		//32	PL_ROUTE_NODE_NO_GROUP15	후판Routing노드번호15						VARCHAR2	4 
		//33	PL_ROUTE_NODE_NO_GROUP16	후판Routing노드번호16						VARCHAR2	4 
		//34	PL_ROUTE_NODE_NO_GROUP17	후판Routing노드번호17						VARCHAR2	4 
		//35	PL_ROUTE_NODE_NO_GROUP18	후판Routing노드번호18						VARCHAR2	4 
		//36	PL_ROUTE_NODE_NO_GROUP19	후판Routing노드번호19						VARCHAR2	4 
		//37	PL_ROUTE_NODE_NO_GROUP20	후판Routing노드번호20						VARCHAR2	4 
		//38	PL_ROUTE_NODE_TYPE_GROUP1	후판Routing노드Type1						VARCHAR2	4 
		//39	PL_ROUTE_NODE_TYPE_GROUP2	후판Routing노드Type2						VARCHAR2	4 
		//40	PL_ROUTE_NODE_TYPE_GROUP3	후판Routing노드Type3						VARCHAR2	4 
		//41	PL_ROUTE_NODE_TYPE_GROUP4	후판Routing노드Type4						VARCHAR2	4 
		//42	PL_ROUTE_NODE_TYPE_GROUP5	후판Routing노드Type5						VARCHAR2	4 
		//43	PL_ROUTE_NODE_TYPE_GROUP6	후판Routing노드Type6						VARCHAR2	4 
		//44	PL_ROUTE_NODE_TYPE_GROUP7	후판Routing노드Type7						VARCHAR2	4 
		//45	PL_ROUTE_NODE_TYPE_GROUP8	후판Routing노드Type8						VARCHAR2	4 
		//46	PL_ROUTE_NODE_TYPE_GROUP9	후판Routing노드Type9						VARCHAR2	4 
		//47	PL_ROUTE_NODE_TYPE_GROUP10	후판Routing노드Type10						VARCHAR2	4 
		//48	PL_ROUTE_NODE_TYPE_GROUP11	후판Routing노드Type11						VARCHAR2	4 
		//49	PL_ROUTE_NODE_TYPE_GROUP12	후판Routing노드Type12						VARCHAR2	4 
		//50	PL_ROUTE_NODE_TYPE_GROUP13	후판Routing노드Type13						VARCHAR2	4 
		//51	PL_ROUTE_NODE_TYPE_GROUP14	후판Routing노드Type14						VARCHAR2	4 
		//52	PL_ROUTE_NODE_TYPE_GROUP15	후판Routing노드Type15						VARCHAR2	4 
		//53	PL_ROUTE_NODE_TYPE_GROUP16	후판Routing노드Type16						VARCHAR2	4 
		//54	PL_ROUTE_NODE_TYPE_GROUP17	후판Routing노드Type17						VARCHAR2	4 
		//55	PL_ROUTE_NODE_TYPE_GROUP18	후판Routing노드Type18						VARCHAR2	4 
		//56	PL_ROUTE_NODE_TYPE_GROUP19	후판Routing노드Type19						VARCHAR2	4 
		//57	PL_ROUTE_NODE_TYPE_GROUP20	후판Routing노드Type20						VARCHAR2	4 
		//58	PL_ROUTE_NODE_AREA_GP		후판Routing노드영역구분					VARCHAR2	16 
		//59	PL_ROUTE_NODE_AREA_POS		후판Routing노드영역위치					VARCHAR2	16 


		// DAO 선언 
		YdCrnSchDao ydCrnSchDao    = new YdCrnSchDao();
		YdUtils ydUtils 		   = new YdUtils();
		YdDaoUtils ydDaoUtils 	   = new YdDaoUtils();

		// 레코드 및 레코드셋 선언
		JDTORecordSet rsGetCrnSch  = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recPara         = null;
		JDTORecord outRec 	       = null;

		String szMethodName 	   = "makeP2L001";
		String szMsg 			   = "";
		String szOperationName     = "A후판MillL2 Routing Layout 지시(29008 TL3CRL)";

		// TC Length = Heder 24 + body 240
		int nTcLen                 = 264;

		// 리턴값
		int intRtnVal 	           = 0;

		// 인덱스
		int nIdx                   = 0;
		
		try{
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n=======================makeP2L001() IN==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, inRec);
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);
			
			// 헤더부
			outRec = JDTORecordFactory.getInstance().create();	

			outRec.setField("Telegram_Length" , "264");
			outRec.setField("Telegram_Id" , "29008");
			outRec.setField("Sequence_Counter" , YdUtils.fillSpZr("", 2, 1));
			outRec.setField("Flags" , YdUtils.fillSpZr("", 2, 1));
			outRec.setField("Spare1" , YdUtils.fillSpZr("", 1, 1));
			outRec.setField("Year" , YdUtils.getCurDate("yyyy"));
			outRec.setField("Month" , YdUtils.getCurDate("MM"));
			outRec.setField("Day" , YdUtils.getCurDate("dd"));
			outRec.setField("Hour" , YdUtils.getCurDate("HH"));
			outRec.setField("Minute" , YdUtils.getCurDate("mm"));
			outRec.setField("Second" , YdUtils.getCurDate("ss"));
			outRec.setField("Spare2" , YdUtils.fillSpZr("", 2, 1));
			
			// 재료번호
			outRec.setField("PL_MPL_NO", YdUtils.fillSpZr(inRec.getFieldString("STL_NO"), 32, 1));
			
			// 분할절단구분코드
			outRec.setField("PL_DIV_TRIM_GP_CD", YdUtils.fillSpZr(inRec.getFieldString("PL_DIV_TRIM_GP_CD"), 4, 1));

			// 후판분할절단구분순번
			outRec.setField("PL_DIV_TRIM_GP_SEQ", YdUtils.fillSpZr(inRec.getFieldString("PL_DIV_TRIM_GP_SEQ"), 4, 1));
			
			// 후판총Routing수
			outRec.setField("PL_TOT_ROUTE_CNT", YdUtils.fillSpZr(inRec.getFieldString("PL_TOT_ROUTE_CNT"), 4, 1));
			
			// 후판L2지시송신모드
			outRec.setField("PL_L2_WO_SND_MD", YdUtils.fillSpZr(inRec.getFieldString("PL_L2_WO_SND_MD"), 4, 1));

			intRtnVal = (intRtnVal >= 20) ? 20 : intRtnVal;
			
			for(nIdx=0; nIdx<intRtnVal; nIdx++) {
				//후판Routing노드번호 1~20 
				outRec.setField("PL_ROUTE_NODE_NO_GROUP" + Integer.toString(nIdx+1), YdUtils.fillSpZr(inRec.getFieldString("PL_ROUTE_NODE_NO_GROUP"), 4, 1));
			}
			
			// 공백 건수처리
			for(int nIdx2=nIdx; nIdx2<20; nIdx2++){
				outRec.setField("PL_ROUTE_NODE_NO_GROUP" + Integer.toString(nIdx2+1), YdUtils.fillSpZr(" ", 4, 1));						
			}			

			intRtnVal = (intRtnVal >= 20) ? 20 : intRtnVal;
			
			for(nIdx=0; nIdx<intRtnVal; nIdx++) {
				//후판Routing노드번호 1~20 
				outRec.setField("PL_ROUTE_NODE_TYPE_GROUP" + Integer.toString(nIdx+1), YdUtils.fillSpZr(inRec.getFieldString("PL_ROUTE_NODE_TYPE_GROUP"), 4, 1));
			}
			
			// 공백 건수처리
			for(int nIdx2=nIdx; nIdx2<20; nIdx2++){
				outRec.setField("PL_ROUTE_NODE_TYPE_GROUP" + Integer.toString(nIdx2+1), YdUtils.fillSpZr(" ", 4, 1));						
			}
			
			//후판Routing노드영역구분
			outRec.setField("PL_ROUTE_NODE_AREA_GP", YdUtils.fillSpZr(inRec.getFieldString("PL_ROUTE_NODE_AREA_GP"), 16, 1));			

			//후판Routing노드영역위치
			outRec.setField("PL_ROUTE_NODE_AREA_POS", YdUtils.fillSpZr(inRec.getFieldString("PL_ROUTE_NODE_AREA_POS"), 16, 1));
			
			// Debug MSG
			ydUtils.putLog(szClassName, szMethodName, "\n======================makeP2L001() OUT==========================\n", YdConstant.DEBUG);	
			ydUtils.displayRecord(szOperationName, outRec);			
			ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);

			// RecordSet에 추가
			outRecSet.addRecord(outRec);
			
		}catch(Exception e){
			szMsg = "YDP2L001 [Routing Layout 지시] 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -1;
		}

		return intRtnVal;

	} // end of makeP2L001()

	//---------------------------------------------------------------------------	
} // end of class MakeTcP2
