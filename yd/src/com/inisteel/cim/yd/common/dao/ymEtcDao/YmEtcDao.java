package com.inisteel.cim.yd.common.dao.ymEtcDao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import jspeed.base.ejb.EJBConnector;
import java.util.List;

/**
 *      [A] 클래스명 :저장이력  DAO
 * 
*/

public class YmEtcDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ymEtcDao.rcvMillOrdReqTL3CRL";
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ymEtcDao.rcvMillOrdReqTL3CP2";
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdRcptPlnMonitor_02_PIDEV";
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.callYdRcptPlnMonitor_02";
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ymEtcDao.getDmFrMvMtlList";
	private String szQueryIdGet6 = "com.inisteel.cim.yd.dao.ymEtcDao.getPlateUgSelToLocInfo_PIDEV";
	
	private String szQueryIdGet100 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.callYdRcptPlnMonitor_02_NEW";
	
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao";  //아직 미정
	
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao.updTB_YM_STACKCOL";
	private String szQueryIdUpd2 = "com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao.updTB_YM_STACKER";
	private String szQueryIdUpd3 = "com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao.updTB_YM_STACKLAYER";
	private String szQueryIdUpd4 = "com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao.updTB_DM_SETTLEDOWNWRSLTIFTEMP";
	private String szQueryIdUpd5 = "com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao.updDmFrStlWorkDate";
	private String szQueryIdUpd6 = "com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao.updDmFrStlLoc";
	private String szQueryIdUpd7 = "com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao.updDmFrStlHist";
	private String szQueryIdUpd8 = "com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao.updPyardCopy";
	private String szQueryIdUpd9 = "com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao.updPyardClear1";
	private String szQueryIdUpd10= "com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao.updPyardClear2";
	private String szQueryIdUpd11= "com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao.updDmFrMvMtl";
	private String szQueryIdUpd12 = "com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao.updTB_DM_SETTLEDOWNWRSLTIFTEMPCOIL";
	private String szQueryIdUpd13 = "com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao.updTB_PT_SLABCOMM_MATCH_HOLD_GP";
//PIDEV
	private String szQueryIdUpd82 = "com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao.updTB_DM_SETTLEDOWNWRSLTIFTEMPCOIL_PIDEV";
	private String szQueryIdUpd84 = "com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao.updTB_DM_SETTLEDOWNWRSLTIFTEMP_PIDEV";
	
/*------------------------------------- SELECT -------------------------------------------*/
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:
	 *                                    
	 *                                     )
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYmEtcDao(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYmEtcDao";
		JDTORecordSet rsTemp = null;
		int intRtnVal = -100;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
					
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet1);
			else if (intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet2);
			else if (intGp == 5)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet5);
			else if (intGp == 6)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet6);

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				szMsg = "[getYdWrkHist] data found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
			}else {
				//data not found
				szMsg = "[getYdWrkHist]data not found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		szMsg = "[getYdWrkHist] return value : " + intRtnVal;
		ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
		return intRtnVal;
	} //end of getYdStock
	
	public JDTORecord callSpYmEtcDao(String sPlateNo) throws DAOException, JDTOException {
		String szMethodName = "callSpYmEtcDao";
		String szMsg 		= null;
		try {
			com.inisteel.cim.common.dao.CommonDAO dao = new com.inisteel.cim.common.dao.CommonDAO();
			
			Object[][] obj = new Object[2][2];
			obj[0][0] = "IN";
			obj[0][1] = sPlateNo;
			
			obj[1][0] = "OUT";
			obj[1][1] = new Integer(java.sql.Types.VARCHAR);
			
			return dao.execute(szQueryIdGet4, obj);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
	} //end of callSpYmEtcDao

//SJH01005	
	public JDTORecord callSpYmEtcDao_NEW(String sPlateNo) throws DAOException, JDTOException {
		String szMethodName = "callSpYmEtcDao_NEW";
		String szMsg 		= null;
		try {
			com.inisteel.cim.common.dao.CommonDAO dao = new com.inisteel.cim.common.dao.CommonDAO();
			
			Object[][] obj = new Object[2][2];
			obj[0][0] = "IN";
			obj[0][1] = sPlateNo;
			
			obj[1][0] = "OUT";
			obj[1][1] = new Integer(java.sql.Types.VARCHAR);
			
			return dao.execute(szQueryIdGet100, obj);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
	} //end of callSpYmEtcDao
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : A.B열연 야드 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYmEtcDao(JDTORecord inRec) throws DAOException, JDTOException {
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		JDTORecord jRecordParam     = inRec;
		Object oParam[]             = null;
		int intRtnVal               = 0;
		String szMethodName         = "insYdWrkHist";
		String szMsg                = "";
		
		try {			

			oParam = new Object[] {
				
					ydDaoUtils.paraRecChkNull(jRecordParam, "YD_GP")
					
			};

			// INSERT 쿼리 실행
			intRtnVal  = assistantDAO.trtProcess(szQueryIdIns1, oParam);
			if(intRtnVal <= 0){
				szMsg = "INSERT 처리 실패 (" + intRtnVal + ")";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdStock
	
	
	
	
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분 ( 0: 
	 *                                 1: 
	 *                                 2:
	 *                             
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */
	public int uptYmEtcDao(JDTORecord inRec, int intGp) throws DAOException, JDTOException {

		String szMethodName         = "uptYmEtcDao";
		int intRtnVal               = 0;
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
//	    	if(CHK.equals("Y") && (intGp ==4 || intGp ==5 || intGp ==6 )){
				//트렌젝션 분리 적용	
//PIDEV			
//			if(intGp ==4 || intGp ==5 || intGp ==6 ){	
	    	if(intGp ==4 || intGp ==5 || intGp ==6 || intGp == 82 || intGp == 84){	
	    		String StringGp=Integer.toString(intGp) ;
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("uptYmEtcDaoReTX", new Class[] { JDTORecord.class, String.class }, new Object[] { inRec ,StringGp});
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					return intRtnVal = -1;
				}
	    	}else{
	    		//기존 방식 적용 
	    		intRtnVal = this.uptYmEtcDaoTX(inRec, intGp);
	    		if(intRtnVal ==0){
	    			return intRtnVal = -1;
	    		}
	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of uptYmEtcDao
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분 ( 0: 
	 *                                 1: 
	 *                                 2:
	 *                             
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int uptYmEtcDaoTX (JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "uptYmEtcDaoTX";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		Object oParam[]             = null;
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		
		try {
			
			//변환용 레코드
			JDTORecord recPara = null;
			recPara = inRec;
			
			//query id setting
			if (intGp == 1){
								
				oParam = new Object[] {						
						 ydDaoUtils.paraRecChkNull(recPara, "STACK_COL_ACTIVE_STAT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CAR_USE_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "TRN_EQP_CD")
						,ydDaoUtils.paraRecChkNull(recPara, "CAR_NO")
						,ydDaoUtils.paraRecChkNull(recPara, "MODIFIER")						
						,ydDaoUtils.paraRecChkNull(recPara, "STACK_COL_GP")
				
						};
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd1,oParam);
				
			}
			 else if (intGp == 2){
				
				oParam = new Object[] {						
						 ydDaoUtils.paraRecChkNull(recPara, "STACK_COL_ACTIVE_STAT")
						,ydDaoUtils.paraRecChkNull(recPara, "STACK_BED_WT_MAX")
						,ydDaoUtils.paraRecChkNull(recPara, "MODIFIER")
						,ydDaoUtils.paraRecChkNull(recPara, "STACK_COL_GP")					
						};
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd2,oParam);	
			} else if (intGp == 3){
				
				oParam = new Object[] {						
						 ydDaoUtils.paraRecChkNull(recPara, "STACK_LAYER_ACTIVE_STAT")
						,ydDaoUtils.paraRecChkNull(recPara, "STACK_LAYER_STAT")
						,ydDaoUtils.paraRecChkNull(recPara, "STOCK_ID")
						,ydDaoUtils.paraRecChkNull(recPara, "MODIFIER")
						,ydDaoUtils.paraRecChkNull(recPara, "STACK_COL_GP")
					
						};
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd3,oParam);	
			
			} else if (intGp == 4){
				
				oParam = new Object[] {			
						 ydDaoUtils.paraRecChkNull(recPara, "YD_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_STR_LOC")
						,ydDaoUtils.paraRecChkNull(recPara, "TRANSMIT_DATE")
						,ydDaoUtils.paraRecChkNull(recPara, "SEND_SEQ")
						};
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd4,oParam);	
			
			} else if (intGp == 5){
				
				oParam = new Object[] {			
						ydDaoUtils.paraRecChkNull(recPara, "YD_WBOOK_ID")
				};
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd5,oParam);	
			} else if (intGp == 6){
							
				oParam = new Object[] {			
						ydDaoUtils.paraRecChkNull(recPara, "YD_WBOOK_ID")
				};
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd6,oParam);	
			} else if (intGp == 7){
				
				oParam = new Object[] {			
						 ydDaoUtils.paraRecChkNull(recPara, "YD_WBOOK_ID")
				};
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd7,oParam);	
			} else if (intGp == 8){
				
				oParam = new Object[] {			
						 ydDaoUtils.paraRecChkNull(recPara, "CAR_POINT"),
						 ydDaoUtils.paraRecChkNull(recPara, "CAR_INFO")
				};
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd8,oParam);	
			} else if (intGp == 9){
				
				oParam = new Object[] {			
						 ydDaoUtils.paraRecChkNull(recPara, "CAR_STAT"),
						 ydDaoUtils.paraRecChkNull(recPara, "CAR_INFO")
				};
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd9,oParam);	
			} else if (intGp == 10){
				
				oParam = new Object[] {			
						 ydDaoUtils.paraRecChkNull(recPara, "CAR_INFO")
				};
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd10,oParam);	
			} else if (intGp == 11){
				
				oParam = new Object[] {			
						 ydDaoUtils.paraRecChkNull(recPara, "CAR_NO")
				};
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd11,oParam);	
			}else if (intGp == 12){
				
				oParam = new Object[] {			
						 ydDaoUtils.paraRecChkNull(recPara, "YD_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_STR_LOC")
						,ydDaoUtils.paraRecChkNull(recPara, "TRANSMIT_DATE")
						,ydDaoUtils.paraRecChkNull(recPara, "SEND_SEQ")
						};
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd12,oParam);	
			} else if (intGp == 13){
				
				oParam = new Object[] {			
						 ydDaoUtils.paraRecChkNull(recPara, "MATCH_HOLD_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "MATCH_HOLD_RSN_CD")
						,ydDaoUtils.paraRecChkNull(recPara, "SLAB_NO")
				}; 
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd13,oParam);	
//PIDEV				
			}else if (intGp == 82){
				
				oParam = new Object[] {			
						 ydDaoUtils.paraRecChkNull(recPara, "YD_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_STR_LOC")
						,ydDaoUtils.paraRecChkNull(recPara, "TRN_REQ_DATE")
						,ydDaoUtils.paraRecChkNull(recPara, "TRN_REQ_SEQ")
						,ydDaoUtils.paraRecChkNull(recPara, "CAR_NO")
						};
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd82,oParam);	

			} else if (intGp == 84){
				
				oParam = new Object[] {			
						 ydDaoUtils.paraRecChkNull(recPara, "YD_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_STR_LOC")
						,ydDaoUtils.paraRecChkNull(recPara, "TRN_REQ_DATE")
						,ydDaoUtils.paraRecChkNull(recPara, "TRN_REQ_SEQ")
						,ydDaoUtils.paraRecChkNull(recPara, "CAR_NO")
						
						};
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd84,oParam);	
			} ;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of uptYmEtcDaoTX
	
	
	 

	

/*------------------------------------- DELETE -------------------------------------------*/
} // end of class
