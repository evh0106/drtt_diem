package com.inisteel.cim.ym.ilkwan.session;

import java.util.List;
import java.util.ArrayList;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.ym.ilkwan.dao.ilkwanDAO;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.YmCommonDB;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;

import javax.naming.*;
import jspeed.base.record.*;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.*;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTORecord;
import jspeed.base.util.StringHelper;

/**
 * 차량입동지시스케쥴 Session EJB
 *
 * @ejb.bean name="CarPntRegEJB" jndi-name="JNDICarPntReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
 
public class CarPntRegSBean extends BaseSessionBean {
	
	private Logger logger 	= null;
	private ilkwanDAO dao 	= null;
	private ymCommonDAO ymCommonDAO = null;
	private YmCommDAO commDao = new YmCommDAO();
	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger 				= new Logger(config);
		dao 				= new ilkwanDAO();
	}
	
	/**
	 * 오퍼레이션명 : 차량입동지시스케줄 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public void rcvCarPointJisiReq(JDTORecord msgRecord)throws JDTOException  {
		String szMsg		= ""; 
		String szMethodName	= "rcvCarPointJisiReq";
        String sYdGp 		= "";
        String sYdBayGp		= "";
        String sCardNo		= "";
        String sTcYn		= "Y";
        
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
	    	//야드구분, 동구분, 차상위치, 카드번호
	    	sYdGp           = YmCommonUtil.paraRecChkNull(msgRecord, "YD_GP");     	// 필수값.
	    	
	    	sYdBayGp        = YmCommonUtil.paraRecChkNull(msgRecord, "YD_BAY_GP");	// 선택값.
	    	sCardNo       	= YmCommonUtil.paraRecChkNull(msgRecord, "CARD_NO");	// 선택값.
	    	sTcYn       	= StringHelper.evl(msgRecord.getFieldString("TC_YN") ,"Y");		// 전문전송 유무 
	    	
	    	
	    	if("".equals(sCardNo)){
	    		
		    	String sQueryId	= "ym.CarPnt.getListCarPntList_01_PIDEV";
	    		List CarSchL = ymCommonDAO.getInstance().getCommonList(sQueryId, new Object[]{sYdGp,sYdBayGp,sYdGp,sYdBayGp,sYdGp,sYdBayGp});
	    		/*
	    		 *  WITH TEMP_TABLE AS (
					SELECT
					    ROWNUM AS NUM,
					    CARD_NO,
					    YD_STK_COL_GP,
					    YD_STK_BED_NO,
					    YD_STK_LYR_NO
					FROM
					(    
					SELECT  
					    C.CARD_NO,
					    B.YD_STK_COL_GP,
					    B.YD_STK_BED_NO,
					    MAX(B.YD_STK_LYR_NO) AS YD_STK_LYR_NO
					FROM TB_YD_STKCOL A,
					     TB_YD_STKLYR B,
					     TB_YD_STOCK C
					WHERE A.YD_STK_COL_GP   = B.YD_STK_COL_GP
					AND   B.STL_NO          = C.STL_NO     
					AND   A.YD_GP           = :YD_GP
					AND   A.YD_BAY_GP       = :YD_BAY_GP
					AND   C.CARD_NO         IS NOT NULL
					AND   C.CARD_NO         NOT IN (
					                                SELECT  
					                                    C.CARD_NO
					                                FROM TB_YD_STKCOL    A,
					                                     TB_YD_STKLYR    B,
					                                     TB_YD_STOCK     C,
					                                     TB_YD_CRNWRKMTL D
					                                WHERE A.YD_STK_COL_GP   = B.YD_STK_COL_GP
					                                AND   B.STL_NO          = C.STL_NO     
					                                AND   A.YD_GP           = :YD_GP
					                                AND   A.YD_BAY_GP       = :YD_BAY_GP
					                                AND   C.STL_NO          = D.STL_NO
					                                AND   D.DEL_YN          = 'N'
					                                AND   C.CARD_NO         IS NOT NULL
					                                
					                                UNION
					                                
					                                SELECT  
					                                    C.CARD_NO
					                                FROM TB_YD_STKCOL   A,
					                                     TB_YD_STKLYR   B,
					                                     TB_YD_STOCK    C
					                                WHERE A.YD_STK_COL_GP   = B.YD_STK_COL_GP
					                                AND   B.STL_NO          = C.STL_NO     
					                                AND   A.YD_GP           = :YD_GP
					                                AND   A.YD_BAY_GP       = :YD_BAY_GP
					                                AND   C.CARD_NO         IS NOT NULL
					                                AND   A.YD_EQP_GP       IN ('PT','TF','RT')
					                                )
					GROUP BY    C.CARD_NO, 
					            B.YD_STK_COL_GP,
					            B.YD_STK_BED_NO
					ORDER BY    YD_STK_COL_GP,
					            YD_STK_BED_NO,
					            YD_STK_LYR_NO DESC            
					) A           
					) 
					SELECT * 
					FROM TEMP_TABLE AA
					WHERE NUM=(SELECT MIN(NUM) 
					             FROM TEMP_TABLE BB
					            WHERE AA.CARD_NO =BB.CARD_NO)
	    		 */ 
	    		for(int inx = 0; inx < CarSchL.size(); inx++ )
	    		{
	    			JDTORecord CarSchInfo = (JDTORecord)CarSchL.get(inx);
	    			sCardNo = StringHelper.evl(CarSchInfo.getFieldString("CARD_NO"),"");
	    			this.sndCarPointJisiReq(sYdGp,sYdBayGp,sCardNo,
	    									YmCommonConst.MODE_0 , sTcYn);
	    		}
	    	}else{
	    		this.sndCarPointJisiReq(sYdGp,sYdBayGp,sCardNo,
	    								YmCommonConst.MODE_1, sTcYn);
	    	}
    		
		}catch(Exception e){
	
			szMsg="차량입동지시스케줄 Error:" +e.getMessage();
			YmCommonUtil.putLog(szMethodName, szMethodName, szMsg, 1);
			throw new EJBServiceException(e);
		}
	
	
		szMsg="차량입동지시스케줄("+szMethodName+") 완료";
		YmCommonUtil.putLog(szMethodName, szMethodName, szMsg, 4);
	} //end of rcvCarPointJisiReq()
	
	/*
	 * 입동가능 Card번호를 넘겨받아서 해당동에 입동가능한 차상위치에 도착모듈을 호출한다.
	 */
	private void sndCarPointJisiReq(	String sYdGp,
										String sYdBayGp,
										String sCardNo,
										String sFlag,
										String sTcYn)throws JDTOException  {
		
		String sQueryId	= "";
		JDTORecord CarInfo = null;
		JDTORecord LocInfo = null;
		JDTORecord recPara = null;
		JDTORecord recTemp = null;
		JDTORecordSet rsResult = null; 
		YdStockDao ydStockDao  = new YdStockDao();
		int nRet	=0;
		String szCR_FRTOMOVE_GP = "";
		
		String sTransOrdDate	= "";
		String sTransOrdSeqno	= "";
		String sCarNo			= "";
		String sYdEqpGp			= "";
		String sWlocCd			= "";
		String sYdPntCd			= "";
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
			/*
			 * 1. Card No를 넘겨받아 추가항목 검색
			 */ 
				sQueryId	= "ym.CarPnt.getListCarPntList_02";
				/*
				 * 	SELECT  
					    C.STL_NO, 
					    C.TRANS_ORD_DATE,
					    C.TRANS_ORD_SEQNO,
					    C.CAR_NO,
					    C.CARD_NO,
					    A.YD_GP,
					    A.YD_BAY_GP,
					    A.YD_EQP_GP,
					    B.YD_STK_COL_GP,
					    B.YD_STK_BED_NO,
					    B.YD_STK_LYR_NO,
					    B.YD_STK_LYR_MTL_STAT
					FROM TB_YD_STKCOL A,
					     TB_YD_STKLYR B,
					     TB_YD_STOCK C
					WHERE A.YD_STK_COL_GP   = B.YD_STK_COL_GP
					AND   B.STL_NO          = C.STL_NO     
					AND   A.YD_GP           = :YD_GP
					AND   A.YD_BAY_GP       = :YD_BAY_GP
					AND   C.CARD_NO         = :CARD_NO
				 */
				CarInfo 	= ymCommonDAO.getInstance().getCommonInfo(sQueryId, 
																	  new Object[]{sYdGp,sYdBayGp ,sCardNo});
				if(CarInfo != null){
					sTransOrdDate 	= StringHelper.evl(CarInfo.getFieldString("TRANS_ORD_DATE"),"");
					sTransOrdSeqno 	= StringHelper.evl(CarInfo.getFieldString("TRANS_ORD_SEQNO"),"");
					sCarNo 			= StringHelper.evl(CarInfo.getFieldString("CAR_NO"),"");
					sYdEqpGp		= StringHelper.evl(CarInfo.getFieldString("YD_EQP_GP"),"");
				}
				
			/*
			 * 2. 해당야드,동에 입동가능 차상위치 검색
			 *   카드번호 : EXXX(ET Car), TXXX(해송차량), XXXX(육송차량)
			 * 	  후판육송 : 01,02 스판 > A2(Trailer 정지위치) , 03,04 스판 > B2 (Trailer 정지위치)
			 *   코일육송 : 제약사항 없슴.	 
			 */
			 
				sQueryId	= "ym.CarPnt.getListCarPntList_03";	
				/*
		    	 	SELECT 
					    YD_STK_COL_GP,
					    YD_STK_COL_NO,
					    YD_EQP_GP,
					    WLOC_CD,
					    YD_PNT_CD
					FROM    TB_YD_STKCOL
					WHERE   YD_GP         = :YD_GP
					AND     YD_BAY_GP     = :BAY_GP
					AND     YD_EQP_GP     = 'PT'
					AND     CAR_NO    IS NULL -- 현재차량정지위치
					AND     YD_STK_COL_NO LIKE DECODE(YD_GP,'J','%','H','%',DECODE(:LOC   , 'L','A_','R','B_')) 
					AND     YD_STK_COL_NO LIKE DECODE(YD_GP,'J','%','H','%',DECODE(:TRANS , 'E','_1','T','_2')) 
				*/
				
				String sLoc = "";
					if("01".equals(sYdEqpGp)||
					   "02".equals(sYdEqpGp)){
						sLoc = "L"; // 왼쪽 차상위치(A통로)
					}else{
						sLoc = "R";	// 오른쪽 차상위치(B통로)
					}
				String sTrans = "";
					if(sCardNo.startsWith("E")){
						sTrans = "E";	// ET CAR 차상위치
					}else{
						sTrans = "T";	// TRAILER 차상위치
					}
				
				LocInfo 	= ymCommonDAO.getInstance().getCommonInfo(sQueryId, 
					  											  new Object[]{sYdGp,sYdBayGp,sLoc,sTrans});
				if(LocInfo != null){
					sWlocCd 	= StringHelper.evl(LocInfo.getFieldString("WLOC_CD"),"");
					sYdPntCd 	= StringHelper.evl(LocInfo.getFieldString("YD_PNT_CD"),"");
				}
				
				/*
				 * 입동스케쥴 처리 후 입동지시 차상위치가 존재하지 않는 경우.
				 */
				if("".equals(sYdPntCd)&& sTcYn.equals("Y")){
					logger.println(LogLevel.DEBUG,this, "입동지시스케쥴 === 차상위치존재안함 .===YD_GP="+sYdGp+"/ CARD_NO="+sCardNo);
					
					if(YmCommonConst.MODE_1.equals(sFlag)){
						
						// 레코드생성-----------------------------------------------------------------
						rsResult = JDTORecordFactory.getInstance().createRecordSet("");
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("TRANS_ORD_DATE",  sTransOrdDate);
						recPara.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
						
						// (운송일자, 운송순번)로 저장품 조회(운송일자, 운송순번)
						/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDateAB*/
						nRet = ydStockDao.getYdStock(recPara, rsResult, 731);
						if(nRet > 0){
							rsResult.first();
							recTemp				= rsResult.getRecord();			
							szCR_FRTOMOVE_GP	=  StringHelper.evl(recTemp.getFieldString("CR_FRTOMOVE_GP") , "");
						}
						
						
						JDTORecord Tmp28 = JDTORecordFactory.getInstance().create();
						
						// PIDEV
//						String sApplyYnPI = commDao.ApplyYnPI("", "CarPntRegSBean => sndCarPointJisiReq", "APPPI0", "*", "*");
									
//						if ("Y".equals(sApplyYnPI)) {	
							if(szCR_FRTOMOVE_GP.equals("")) {
								Tmp28.setField("SCH_YN"			,"N"); // 스케쥴여부
							} else {
								Tmp28.setField("SCH_YN"			,"Y"); // 스케쥴여부
							}
							
							Tmp28.setField("MQ_TC_CD"            , "M10YDLMJ1061");
							Tmp28.setField("MQ_TC_CREATE_DDTT"	 , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
							// Tmp28.setField("CARD_NO"			 , sCardNo);
							Tmp28.setField("TRN_REQ_DATE"		 , sTransOrdDate);
							Tmp28.setField("TRN_REQ_SEQ" 		 , sTransOrdSeqno);
							Tmp28.setField("CAR_NO"				 , sCarNo);
							Tmp28.setField("YD_GP"				 , "3"); // 야드구분(1열연)
							Tmp28.setField("DIST_GOODS_GP"		 , "H"); // 출하제품구분 (열연코일)
							Tmp28.setField("BAYIN_DDTT" 		,new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
							Tmp28.setField("WLOC_CD"			,sWlocCd);
							Tmp28.setField("YD_PNT_CD"			,sYdPntCd);
							Tmp28.setField("LOAN_PULLOUT_ABLE_YN","N");
							
							if(!szCR_FRTOMOVE_GP.equals("")) {
								Tmp28.setField("CR_FRTOMOVE_GP"     , szCR_FRTOMOVE_GP);
							}
							
//						} else {
//							if(szCR_FRTOMOVE_GP.equals("")){
//								Tmp28.setField("TC_CODE"			,"YDDMR028");
//							}else{
//								Tmp28.setField("TC_CODE"            ,"YDDMR070");
//							}
//							
//							Tmp28.setField("TC_CREATE_DDTT"		,new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//							Tmp28.setField("CARD_NO"			,sCardNo);
//							Tmp28.setField("CAR_NO"				,sCarNo);
//							Tmp28.setField("TRANS_WORD_DATE"	,sTransOrdDate);
//							Tmp28.setField("TRANS_WORD_SEQNO" 	,sTransOrdSeqno);						
//							Tmp28.setField("BAYIN_DDTT" 		,new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//							Tmp28.setField("LOAN_PULLOUT_ABLE_YN","N");
//							
//							if(szCR_FRTOMOVE_GP.equals("")){
//								Tmp28.setField("WLOC_CD"			,sWlocCd);
//								Tmp28.setField("YD_PNT_CD"			,sYdPntCd);								
//							}else{
//								Tmp28.setField("CR_FRTOMOVE_GP"     , szCR_FRTOMOVE_GP);
//							}
//						}
						
						EJBConnector ejbConn0 = new EJBConnector("default","JNDIYardWrkResReg",this);
						ejbConn0.trx("sendInternalModel",new Class[]{JDTORecord.class},new Object[]{Tmp28});
					}
					
					return;
				}
			
			//2.1 입동가능 차상위치 점유	
				
				sQueryId = "ym.CarPnt.updateStkColCarInfo_01";
		    	int count = dao.updateData(sQueryId, new Object[]{	"G",
		    														"GXXXXXXX",
		    														sCarNo,
		    														sCardNo,
		    														sWlocCd,
		    														sYdPntCd}); 
		    	
		    	  //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
		        EJBConnector ejbConn = new EJBConnector("default","JNDITsInfoReg",this);
				ejbConn.trx("CarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
			  	             new Object[]{"D","",sCarNo,"",sWlocCd,sYdPntCd,"R"});
		    	/*
			    	UPDATE  TB_YD_STKCOL
		            SET     YD_CAR_USE_GP   = :YD_CAR_USE_GP,
		                    TRN_EQP_CD      = :TRN_EQP_CD,
		                    CAR_NO          = :CAR_NO,
		                    CARD_NO         = :CARD_NO
		            WHERE WLOC_CD   = :WLOC_CD
		            AND   YD_PNT_CD = :YD_PNT_CD
	            */
				
			//3. 입동가능 차상위치로 도착모듈 호출
				
				JDTORecord Tc = JDTORecordFactory.getInstance().create();
				String sTcCd	= "";
				String sMethod	= "";
				
				if("A".equals(sYdGp)){
					sTcCd	= "DMYDR035";
					sMethod	= "rcvOutplSlabDistCarArrWr";
				}else if("H".equals(sYdGp)){
					sTcCd	= "DMYDR037";
					sMethod	= "rcvCoilRentprocCarArrWr";
				}else if("K".equals(sYdGp)){
					sTcCd	= "DMYDR038";
					sMethod	= "rcvPlGdsDistCarArrWr";
				}else if("J".equals(sYdGp)){
					sTcCd	= "DMYDR036";
					sMethod	= "rcvCoilGdsDistCarArrWr";
				}
					
				Tc.setField("TC_CODE"			,sTcCd);
				Tc.setField("TC_CREATE_DDTT"	,new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
				Tc.setField("YD_GP"				,sYdGp);
				Tc.setField("TRANS_ORD_DT"		,sTransOrdDate);
				Tc.setField("TRANS_ORD_SEQNO" 	,sTransOrdSeqno);
				Tc.setField("CAR_NO"			,sCarNo);
				Tc.setField("CARD_NO"			,sCardNo);
				Tc.setField("SPOS_WLOC_CD"		,sWlocCd);	
				Tc.setField("SPOS_YD_PNT_CD"	,sYdPntCd);
				Tc.setField("IS_EJB_CALL"		,"Y");
				
				EJBConnector ejbConn1 = new EJBConnector("default","CarMvHdFaEJB",this);
				ejbConn1.trx(sMethod,new Class[]{JDTORecord.class},new Object[]{Tc});
				
			//4. 입동지시 출하로 송신.
				if(sTcYn.equals("Y")){
					
					// 레코드생성-----------------------------------------------------------------
					rsResult = JDTORecordFactory.getInstance().createRecordSet("");
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("TRANS_ORD_DATE",  sTransOrdDate);
					recPara.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
					
					// (운송일자, 운송순번)로 저장품 조회(운송일자, 운송순번)
					/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDateAB*/
					nRet = ydStockDao.getYdStock(recPara, rsResult, 731);
					if(nRet > 0){
						rsResult.first();
						recTemp				= rsResult.getRecord();			
						szCR_FRTOMOVE_GP	=  StringHelper.evl(recTemp.getFieldString("CR_FRTOMOVE_GP") , "");
					}
					
					JDTORecord Tc28 = JDTORecordFactory.getInstance().create();
					
					// PIDEV
//					String sApplyYnPI = commDao.ApplyYnPI("", "CarPntRegSBean => sndCarPointJisiReq", "APPPI0", "*", "*");
								
//					if ("Y".equals(sApplyYnPI)) {
						if(szCR_FRTOMOVE_GP.equals("")){
							Tc28.setField("SCH_YN"			,"N"); // 스케쥴여부
						} else {
							Tc28.setField("SCH_YN"			,"Y"); // 스케쥴여부
						}
						
						Tc28.setField("MQ_TC_CD"            , "M10YDLMJ1061");
						Tc28.setField("MQ_TC_CREATE_DDTT"	 , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
						// Tmp28.setField("CARD_NO"			 , sCardNo);
						Tc28.setField("TRN_REQ_DATE"		 , sTransOrdDate);
						Tc28.setField("TRN_REQ_SEQ" 		 , sTransOrdSeqno);
						Tc28.setField("CAR_NO"				 , sCarNo);
						Tc28.setField("YD_GP"				 , "3"); // 야드구분(1열연)
						Tc28.setField("DIST_GOODS_GP"		 , "H"); // 출하제품구분 (열연코일)							
						Tc28.setField("BAYIN_DDTT" 		,new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
						Tc28.setField("WLOC_CD"			,sWlocCd);
						Tc28.setField("YD_PNT_CD"			,sYdPntCd);
						Tc28.setField("LOAN_PULLOUT_ABLE_YN","N");
						
						if(!szCR_FRTOMOVE_GP.equals("")) {
							Tc28.setField("CR_FRTOMOVE_GP"	, szCR_FRTOMOVE_GP);
						}							
						
//					} else {
//						if(szCR_FRTOMOVE_GP.equals("")){
//							Tc28.setField("TC_CODE"			,"YDDMR028");
//						}else{
//							Tc28.setField("TC_CODE"            ,"YDDMR070");
//						}
//						 
//						Tc28.setField("TC_CREATE_DDTT"		,new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//						Tc28.setField("TRANS_WORD_DATE"		,sTransOrdDate);
//						Tc28.setField("TRANS_WORD_SEQNO" 	,sTransOrdSeqno);
//						Tc28.setField("CARD_NO"				,sCardNo);
//						Tc28.setField("CAR_NO"				,sCarNo);
//						Tc28.setField("BAYIN_DDTT" 			,new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//						Tc28.setField("LOAN_PULLOUT_ABLE_YN","Y");
//						 
//						if(szCR_FRTOMOVE_GP.equals("")) {
//							Tc28.setField("WLOC_CD"			,sWlocCd);
//							Tc28.setField("YD_PNT_CD"			,sYdPntCd);								
//						} else {
//							Tc28.setField("CR_FRTOMOVE_GP"     , szCR_FRTOMOVE_GP);
//						}
//					}
											
					EJBConnector ejbConn2 = new EJBConnector("default","JNDIYardWrkResReg",this);
					ejbConn2.trx("sendInternalModel",new Class[]{JDTORecord.class},new Object[]{Tc28});
				}
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
  //---------------------------------------------------------------------------	
} // end of class CarPntJisiSBean
