package com.inisteel.cim.ym.facilitystatus.facilityinquiry.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.parser.Level2Parser;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;

import java.util.ArrayList;
import java.util.List;

import jspeed.base.ejb.EJBConnector;				// 최규성 추가 2009-10-05
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="BCnvyrStatRegEJB" jndi-name="JNDIBCnvyrStatReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class BCnvyrStatRegSBean extends BaseSessionBean  {
	
	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
	}  

	/**
	 * 오퍼레이션명 : 확장 Conveyor Tracking 정보
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void receiveBCnvyrStat(String sMessage)  throws java.rmi.RemoteException{ 
		/*
	     *  야드 LEVEL2로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
	     *  전문내용을 JDTORecord로 파싱한다.
	     *  업무 로직
	     *	1.TC_CD - CK1PB01
	     *	2.야드 LEVEL2로부터 확장 Conveyor Tracking 정보를 수신
	     *  3.조업 TB_PO_ABHRTRACKING Table에 Insert
	     * 
			WB21SKIDCOILNo	C	10
			WB22SKIDCOILNo	C	10
			WB23SKIDCOILNo	C	10
			WB24SKIDCOILNo	C	10
			NO6SCCOILNo	    C	10
			WB31SKIDCOILNo	C	10
			WB32SKIDCOILNo	C	10
			WB33SKIDCOILNo	C	10
			WB34SKIDCOILNo	C	10
			WB35SKIDCOILNo	C	10
			WB36SKIDCOILNo	C	10
			WB37SKIDCOILNo	C	10
			NO7SCCOILNo	    C	10
			WB41SKIDCOILNo	C	10
			WB42SKIDCOILNo	C	10
			WB43SKIDCOILNo	C	10
			WB44SKIDCOILNo	C	10
			WB45SKIDCOILNo	C	10
			WB46SKIDCOILNo	C	10
			WB47SKIDCOILNo	C	10
			NO8SCCOILNo	    C	10
			NO9SCCOILNo	    C	10
			WB51SKIDCOILNo	C	10
			WB52SKIDCOILNo	C	10
			WB53SKIDCOILNo	C	10
			WB54SKIDCOILNo	C	10
			WB55SKIDCOILNo	C	10
			WB61SKIDCOILNo	C	10
			WB62SKIDCOILNo	C	10
			WB63SKIDCOILNo	C	10
			WB64SKIDCOILNo	C	10
			WB71SKIDCOILNo	C	10
			WB72SKIDCOILNo	C	10
			WB73SKIDCOILNo	C	10
			WB74SKIDCOILNo	C	10
			WB75SKIDCOILNo	C	10
			NO10SCCOILNo	C	10
			SC101SKIDCOILNo	C	10
			SC102SKIDCOILNo	C	10
			SC103SKIDCOILNo	C	10
			SC104SKIDCOILNo	C	10
			SC105SKIDCOILNo	C	10
			
			PLANT_GP	VARCHAR2(1)	    Not Null	공장 구분 "B"
			PROC_GP	    VARCHAR2(1)	    Not Null	공정 구분 "D"
			EQUIP_GP	VARCHAR2(6)	    Not Null	설비 구분 
			
			STL_NO	    VARCHAR2(11)		                재료 번호
			OCCUR_DDTT	VARCHAR2(16)	Not Null	발생 일시
			SORT_SEQ	NUMBER(4)		            SORT 순서
			
	     */
		
        LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveBCnvyrStat()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			int iResult = 0;
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(sMessage);

			String XTWB21   = StringHelper.evl(jDTORecord.getFieldString("WB21SKIDCOILNo"), "");
			String XTWB22   = StringHelper.evl(jDTORecord.getFieldString("WB22SKIDCOILNo"), "");
			String XTWB23   = StringHelper.evl(jDTORecord.getFieldString("WB23SKIDCOILNo"), "");
			String XTWB24   = StringHelper.evl(jDTORecord.getFieldString("WB24SKIDCOILNo"), "");
			String XT6SC    = StringHelper.evl(jDTORecord.getFieldString("NO6SCCOILNo"), "");
			String XTWB31   = StringHelper.evl(jDTORecord.getFieldString("WB31SKIDCOILNo"), "");
			String XTWB32   = StringHelper.evl(jDTORecord.getFieldString("WB32SKIDCOILNo"), "");
			String XTWB33   = StringHelper.evl(jDTORecord.getFieldString("WB33SKIDCOILNo"), "");
			String XTWB34   = StringHelper.evl(jDTORecord.getFieldString("WB34SKIDCOILNo"), "");
			String XTWB35   = StringHelper.evl(jDTORecord.getFieldString("WB35SKIDCOILNo"), "");
			String XTWB36   = StringHelper.evl(jDTORecord.getFieldString("WB36SKIDCOILNo"), "");
			String XTWB37   = StringHelper.evl(jDTORecord.getFieldString("WB37SKIDCOILNo"), "");
			String XT7SC    = StringHelper.evl(jDTORecord.getFieldString("NO7SCCOILNo"), "");
			String XTWB41   = StringHelper.evl(jDTORecord.getFieldString("WB41SKIDCOILNo"), "");
			String XTWB42   = StringHelper.evl(jDTORecord.getFieldString("WB42SKIDCOILNo"), "");
			String XTWB43   = StringHelper.evl(jDTORecord.getFieldString("WB43SKIDCOILNo"), "");
			String XTWB44   = StringHelper.evl(jDTORecord.getFieldString("WB44SKIDCOILNo"), "");
			String XTWB45   = StringHelper.evl(jDTORecord.getFieldString("WB45SKIDCOILNo"), "");
			String XTWB46   = StringHelper.evl(jDTORecord.getFieldString("WB46SKIDCOILNo"), "");
			String XTWB47   = StringHelper.evl(jDTORecord.getFieldString("WB47SKIDCOILNo"), "");
			String XT8SC    = StringHelper.evl(jDTORecord.getFieldString("NO8SCCOILNo"), "");
			String XT9SC    = StringHelper.evl(jDTORecord.getFieldString("NO9SCCOILNo"), "");
			String XTWB51   = StringHelper.evl(jDTORecord.getFieldString("WB51SKIDCOILNo"), "");
			String XTWB52   = StringHelper.evl(jDTORecord.getFieldString("WB52SKIDCOILNo"), "");
			String XTWB53   = StringHelper.evl(jDTORecord.getFieldString("WB53SKIDCOILNo"), "");
			String XTWB54   = StringHelper.evl(jDTORecord.getFieldString("WB54SKIDCOILNo"), "");
			String XTWB55   = StringHelper.evl(jDTORecord.getFieldString("WB55SKIDCOILNo"), "");
			String XTWB61   = StringHelper.evl(jDTORecord.getFieldString("WB61SKIDCOILNo"), "");
			String XTWB62   = StringHelper.evl(jDTORecord.getFieldString("WB62SKIDCOILNo"), "");
			String XTWB63   = StringHelper.evl(jDTORecord.getFieldString("WB63SKIDCOILNo"), "");
			String XTWB64   = StringHelper.evl(jDTORecord.getFieldString("WB64SKIDCOILNo"), "");
			String XTWB71   = StringHelper.evl(jDTORecord.getFieldString("WB71SKIDCOILNo"), "");
			String XTWB72   = StringHelper.evl(jDTORecord.getFieldString("WB72SKIDCOILNo"), "");
			String XTWB73   = StringHelper.evl(jDTORecord.getFieldString("WB73SKIDCOILNo"), "");
			String XTWB74   = StringHelper.evl(jDTORecord.getFieldString("WB74SKIDCOILNo"), "");
			String XTWB75   = StringHelper.evl(jDTORecord.getFieldString("WB75SKIDCOILNo"), "");
			String XT10SC   = StringHelper.evl(jDTORecord.getFieldString("NO10SCCOILNo"), "");
			String XT101    = StringHelper.evl(jDTORecord.getFieldString("WB101SKIDCOILNo"), "");
			String XT102    = StringHelper.evl(jDTORecord.getFieldString("WB102SKIDCOILNo"), "");
			String XT103    = StringHelper.evl(jDTORecord.getFieldString("WB103SKIDCOILNo"), "");
			String XT104    = StringHelper.evl(jDTORecord.getFieldString("WB104SKIDCOILNo"), "");
			String XT105    = StringHelper.evl(jDTORecord.getFieldString("WB105SKIDCOILNo"), "");
			
//			updateABHRTRACKINGDB(XTWB21.trim(), "1");
//			updateABHRTRACKINGDB(XTWB22.trim(), "2");
//			updateABHRTRACKINGDB(XTWB23.trim(), "3");
//			updateABHRTRACKINGDB(XTWB24.trim(), "4");
//			updateABHRTRACKINGDB(XT6SC.trim(),  "5");
//			updateABHRTRACKINGDB(XTWB31.trim(), "6");
//			updateABHRTRACKINGDB(XTWB32.trim(), "7");
//			updateABHRTRACKINGDB(XTWB33.trim(), "8");
//			updateABHRTRACKINGDB(XTWB34.trim(), "9");
//			updateABHRTRACKINGDB(XTWB35.trim(), "10");
//			updateABHRTRACKINGDB(XTWB36.trim(), "11");
//			updateABHRTRACKINGDB(XTWB37.trim(), "12");
//			updateABHRTRACKINGDB(XT7SC.trim(),  "13");
//			updateABHRTRACKINGDB(XTWB41.trim(), "14");
//			updateABHRTRACKINGDB(XTWB42.trim(), "15");
//			updateABHRTRACKINGDB(XTWB43.trim(), "16");
//			updateABHRTRACKINGDB(XTWB44.trim(), "17");
//			updateABHRTRACKINGDB(XTWB45.trim(), "18");
//			updateABHRTRACKINGDB(XTWB46.trim(), "19");
//			updateABHRTRACKINGDB(XTWB47.trim(), "20");
//			updateABHRTRACKINGDB(XT8SC.trim(),  "21");
//			updateABHRTRACKINGDB(XT9SC.trim(),  "22");
//			updateABHRTRACKINGDB(XTWB51.trim(), "23");
//			updateABHRTRACKINGDB(XTWB52.trim(), "24");
//			updateABHRTRACKINGDB(XTWB53.trim(), "25");
//			updateABHRTRACKINGDB(XTWB54.trim(), "26");
//			updateABHRTRACKINGDB(XTWB55.trim(), "27");			
//			updateABHRTRACKINGDB(XTWB61.trim(), "28");
//			updateABHRTRACKINGDB(XTWB62.trim(), "29");
//			updateABHRTRACKINGDB(XTWB63.trim(), "30");
//			updateABHRTRACKINGDB(XTWB64.trim(), "31");
//			updateABHRTRACKINGDB(XT10SC.trim(), "32");
//			updateABHRTRACKINGDB(XT101.trim(),  "33");
//			updateABHRTRACKINGDB(XT102.trim(),  "34");
//			updateABHRTRACKINGDB(XT103.trim(),  "35");
//			updateABHRTRACKINGDB(XT104.trim(),  "36");
//			updateABHRTRACKINGDB(XT105.trim(),  "37");
//			updateABHRTRACKINGDB(XTWB71.trim(), "38");
//			updateABHRTRACKINGDB(XTWB72.trim(), "39");
//			updateABHRTRACKINGDB(XTWB73.trim(), "40");
//			updateABHRTRACKINGDB(XTWB74.trim(), "41");
//			updateABHRTRACKINGDB(XTWB75.trim(), "42");
			
//2010.02.01 기능개선 정종균 : 단일 view update 방식으로 변경 작업	
			String updateABHRTRACKING = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateABHRTRACKING2";
			int updateabhrtracking    = ydStackLayerDAO.requestupdateData(updateABHRTRACKING, 
										new Object[]{ 			
													XTWB21.trim(),//1
													XTWB22.trim(),//2
													XTWB23.trim(),//3
													XTWB24.trim(),//4
													XT6SC.trim(), //5
													XTWB31.trim(),//6
													XTWB32.trim(),//7
													XTWB33.trim(),//8
													XTWB34.trim(),//9
													XTWB35.trim(),//10
													XTWB36.trim(),//11
													XTWB37.trim(),//12
													XT7SC.trim(), //13
													XTWB41.trim(),//14
													XTWB42.trim(),//15
													XTWB43.trim(),//16
													XTWB44.trim(),//17
													XTWB45.trim(),//18
													XTWB46.trim(),//19
													XTWB47.trim(),//20
													XT8SC.trim(), //21
													XT9SC.trim(), //22
													XTWB51.trim(),//23
													XTWB52.trim(),//24
													XTWB53.trim(),//25
													XTWB54.trim(),//26
													XTWB55.trim(),//27			
													XTWB61.trim(),//28
													XTWB62.trim(),//29
													XTWB63.trim(),//30
													XTWB64.trim(),//31
													XT10SC.trim(),//32
													XT101.trim(), //33
													XT102.trim(), //34
													XT103.trim(), //35
													XT104.trim(), //36
													XT105.trim(), //37
													XTWB71.trim(),//38
													XTWB72.trim(),//39
													XTWB73.trim(),//40
													XTWB74.trim(),//41
													XTWB75.trim(),//42
													});
			
//			// 최규성 2009-10-05
//			// 확장분기위치코드에 대한 정보를 변경한다.
//			// 처리 방법
//			// 1. 설비내의 고장난 설비의 분기위치코드를 가져온다.
//			// 		검사조건 :  	1. 고장난 설비 존재, 
//			//				   	2. 고장난 설비의 분기위치코드를 사용하는 코일이 존재
//			//		반환값  :  고장난 설비명, 설비분기위치코드,
//			// 2. 조건일치시 고장난 설비의 분기위치코드를 가진 코일의 확장분기위치코드를 변경한다.
//			/*설비별					코일별 
//			 * 고장	기존분기위치코드	기존확장분기위치코드	변경분기위치코드	변경확장분기위치코드
//			 * 2WB		4E									3S
//			 * 3WB		4E									3S
//			 * 4WB		4E									3S
//			 * 5WB		4E				All									5E or 6E
//			 * 6WB		4E				All									4E or 5E
//			 * 7WB		4E				All									4E or 6E
//			 * 
//			 */

//
//  			List listFacilityData = new ArrayList();
//  			List listHindrance = new ArrayList();
//  			listFacilityData.clear();
//  			listHindrance.clear();
//  
//			Boolean bVal = Boolean.FALSE;
//			logger.println(LogLevel.DEBUG,this,"고장난 설비를 가져옴");
//			EJBConnector ejbCon = new EJBConnector("default", "JNDICCExtWrkOrdReg", this);
//			
//			// 고장난 설비 정보(설비명,설비상태)와 분기위치코드를 가져온다.
//			// 인자로 사용된 List에는 아무것도 추가하지 않음.
//			listFacilityData = (List)ejbCon.trx("checkFacilityHindrance", new Class[]{List.class}, new Object[]{listHindrance} );
//			
////			JDTORecord jtrFacilityData = (JDTORecord)ejbCon.trx("checkFacilityHindrance", new Class[]{List.class}, new Object[]{listHindrance} );
//
//			JDTORecord jtrData = null;
//			boolean bRes = false;
//			List listArgu = new ArrayList();
//			listArgu.clear();
//			
//			if( listFacilityData.size() > 0 )
//			{
//				logger.println(LogLevel.DEBUG,this,"고장난 설비가 존재함. 변경 가는한 확장Conv분기위치코드를 구함");
//				for(int i=0; i< listFacilityData.size(); i++)
//				{
//					jtrData = (JDTORecord)listFacilityData.get(i);
//					String sCurrExBrCd = jtrData.getFieldString("EXT_BR_CD");	// 고장난 설비의 분기위치코드
//						/*
//					리스트 자체를 넘김.
//						확장Conv분기위치코드선정함수(리스트(고장난 설비명, 분기코드) ) >>>>반환값>>>>변경가능한 분기위치코드 String
//							- 변경가능순위 판정시 고장여부 판별 필요.. 인자로 받은 리스트에서 고장여부 비교.
//							- 1순위가 이상이 있을 경우 2순위 선정. 
//							- 2순위도 문제가 있을 경우 확장Conv분기위치코드 변경 불가 - 메시지 표시.
//						*/	
//					String sModifiedExBrCd = getModifiedExBranchCode(jtrData);
//						// 선정된 "확장Conv분기위치코드"로 컨베어상의 코일의 확장Conv분기위치코드를 변경한다.
//						// 변경전 코드와 변경할 코드
//					if( sModifiedExBrCd.length() < 3)
//					{
//						logger.println(LogLevel.DEBUG,this,"고장난 설비가 존재함. 확장Conv분기위치코드를 변경함. 변경코드" + sModifiedExBrCd +" 현재코드"+sCurrExBrCd);
//						listArgu.add(sModifiedExBrCd);	// Modify EX_BR_CD
//						listArgu.add(sCurrExBrCd);		// current EX_BR_CD
//						listArgu.add(sCurrExBrCd);		// current EX_BR_CD
//						
//						bVal = (Boolean)ejbCon.trx("CCExtWrkOrdUpdateExtConvBranchCode", new Class[]{ List.class },
//																						 new Object[]{ listArgu }  );
//					}
//				}
//			}
//
//				
			logger.println(LogLevel.DEBUG,this,"End-receiveBCnvyrStat()");
			
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }  		
	}
	// 2009-11-11 최규성 
	// 고장난 설비의 분기위치코드를 변경하기 위해 변경가능한 분기위치코드를 구한다.
	// 반환 값은 변경 가능한 확장Conv분기위치코드.
	//private String getModifiedExBranchCode(List listArgu_IN){
	private String getModifiedExBranchCode(JDTORecord jtrData_IN){
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-getModifiedExBranchCode()");
		
		String sModifiedExBranchCode = "변경불가";
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			/*
			// 다음 기준에 맞춰 변경할 확장Conv분기위치코드를 선정한다.
			//   고장난 설비			변경가능1순위			변경가능2순위 
			//	WB05( 4E )				6E					5E
			//	WB06 or 10SC( 6E )		5E					4E
			//	WB07( 5E )				6E					4E
				
				리스트 자체를 넘김.w
				확장Conv분기위치코드선정함수(리스트(고장난 설비명, 분기코드) ) >>>>반환값>>>>변경가능한 분기위치코드 String
					- 변경가능순위 판정시 고장여부 판별 필요.. 인자로 받은 리스트에서 고장여부 비교.
					- 1순위가 이상이 있을 경우 2순위 선정. 
					- 2순위도 문제가 있을 경우 확장Conv분기위치코드 변경 불가 - 메시지 표시.
			*/
			
			String sCurrExBrCd = jtrData_IN.getFieldString("EXT_BR_CD");	// 고장난 설비의 분기위치코드
			logger.println(LogLevel.DEBUG,this,"고장난 설비의 확장Conv분기위치코드 " +sCurrExBrCd);
			if (sCurrExBrCd.equals("4E"))					// 
			{
				// 변경가능한코드가 사용가능한지 검사한다.
				if( checkExBranchCode("6E") ){
					sModifiedExBranchCode = "6E";
				}else if( checkExBranchCode("5E") ){
					sModifiedExBranchCode = "5E";
				}
			}else if (sCurrExBrCd.equals("6E"))
			{
				if( checkExBranchCode("5E") ){
					sModifiedExBranchCode = "5E";
				}else if( checkExBranchCode("4E") ){
					sModifiedExBranchCode = "4E";
				}
			}else if (sCurrExBrCd.equals("5E"))
			{
				if( checkExBranchCode("6E") ){
					sModifiedExBranchCode = "6E";
				}else if( checkExBranchCode("4E") ){
					sModifiedExBranchCode = "4E";
				}
			}
			logger.println(LogLevel.DEBUG,this,"변경 확장Conv분기위치코드 " +sModifiedExBranchCode);
			logger.println(LogLevel.DEBUG,this,"End-getModifiedExBranchCode()");
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
		return sModifiedExBranchCode;
	}
	// 최규성 2009-11-11
	// 고장난 분기위치코드에 따라서 변경가능한 분기위치코드를 반환한다.
	// brcode_IN : 고장난 확장
	private boolean checkExBranchCode(String brcode_IN){
		boolean bRet = false;
		
//		YdStockDAO ydStockDAO 	        = new YdStockDAO();
//		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
//		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		List listFacilityData = new ArrayList();
		List listHindrance = null;
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			EJBConnector ejbCon = new EJBConnector("default", "JNDICCExtWrkOrdReg", this);
			listFacilityData = (List)ejbCon.trx("checkFacilityHindrance", new Class[]{List.class}, new Object[]{listHindrance} );
			
			JDTORecord jtrCompRec = null;
			for(int i=0; i<listFacilityData.size(); i++){
				jtrCompRec = (JDTORecord)listFacilityData.get(i);
				if(jtrCompRec.getFieldString("EXT_BR_CD").equals(brcode_IN) )
				{
					bRet = false;
					break;
				}else{
					bRet = true;
				}
			}
		}catch(DAOException daoe){
			throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
		
		return bRet;
	}
	/**
	 * Update 확장 conveyor(조업 USRPOA.TB_PO_ABHRTRACKING) 
	 * @param STLNO   : Stock_id
	 * @param SORTSEQ : 순서
	 * @return
	 */
	private boolean updateABHRTRACKINGDB(String STLNO, String SORTSEQ){
		boolean isSuccess = false;
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	    	int iResult = 0;
	    	
			/*			
			 Update USRPOA.TB_PO_ABHRTRACKING
			    Set STL_NO    = ?,
			    	OCCUR_DDTT = to_char(sysdate,'YYYYMMDDHH24MISS')
			  where PLANT_GP  = 'B'
			    And PROC_GP   = 'X'
			    And SORT_SEQ  = ?
			    And EQUIP_GP  like 'XT%'
			*/
			String updateABHRTRACKING = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateABHRTRACKING";
			int updateabhrtracking    = ydStackLayerDAO.requestupdateData(updateABHRTRACKING, new Object[]{ STLNO.trim(), SORTSEQ.trim() });	
			
	    	return isSuccess;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
}

