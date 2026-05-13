package com.inisteel.cim.yd.common.util;

import java.util.List;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.rule.GetBreRule5;
import com.inisteel.cim.yd.ydStock.StockSpecReg.StockSpecRegSeEJBBean;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;


/**
 * 저장품 업데이트 위한 데이터 매핑 
 * 2009.09.15
 * @author 권오창
 *
 * 연주전단 지시    CTYDJ011 (주편/슬라브 읽는것 보류)
 * 전단실적        CSYDJ001
 * Take-Out       C3YDL004 (주편/슬라브 읽는부분 없음)
 * 스카핑실적       CSYDJ002
 * 정정실적         CSYDJ003
 * 이송지시         PMYDJ002
 * 충당실적         PMYDJ001
 * 압연지시         CTYDJ031 (주편/슬라브 읽는부분 없음)
 * 결번실적         PRYDJ001
 * 출하지시대기     DMYDR004
 * 출하지시대기     DMYDR026
 * 외판운송지시대기  DMYDR016
 *
 */
public class YdCodeMapping {
	private YdUtils ydUtils       = new YdUtils();
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	private YdStockDao ydStockDao = new YdStockDao();
	private StockSpecRegSeEJBBean stock = new StockSpecRegSeEJBBean();
	
	private String szClassName    = YdCodeMapping.class.getName();
	
	
	public int MakeCodeMapping(String szTcCode, String szSTL_NO, JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		return this.MakeCodeMapping(szTcCode, szSTL_NO, inRec, outRec, ""); 
	}
	
	/**
	 * 데이터 매핑 함수
	 * @param inRec, outRec
	 * @return 처리건수
	 */	
	public int MakeCodeMapping(	String szTcCode, 
								String szSTL_NO, 
								JDTORecord inRec, 
								JDTORecord outRec, 
								String strStlGp) throws JDTOException {
		
		JDTORecord recPara     = null;
		JDTORecordSet rsResult = null;
		JDTORecord recGetVal   = null; 
		String szMethodName    = "MakeCodeMapping";
		String szMsg           = "";
		String szOperationName = "데이터 매핑 함수";
		String szRECORD_PROG_STAT = "";
		String szCC_MC_CD      = "";
		String szCC_CCM_NO     = "";
		String szPLNT_PROC_CD  = "";
		String szYD_GP         = "";
		String strPT_TB_COMM   = "";
		int nRet               = 0;

		// Debug MSG
		ydUtils.putLog(szClassName, szMethodName, "\n======= YdCodeMapping::MakeCodeMapping() IN ====================\n", YdConstant.DEBUG);	
		ydUtils.putLog(szClassName, szMethodName, "MakeCodeMapping() : TCCode(" + szTcCode + ") STL_NO(" + szSTL_NO + ")", YdConstant.DEBUG);
		ydUtils.putLog(szClassName, szMethodName, "\n================================================================\n", YdConstant.DEBUG);

		if("MSLAB".equals(strStlGp)) {
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();								
			recPara.setField("STL_NO", szSTL_NO);
			nRet = ydStockDao.getYdStock(recPara, rsResult, 134);
			if(nRet <= 0){
				return nRet;
			}
		}else if ("SLAB".equals(strStlGp)) {
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();								
			recPara.setField("STL_NO", szSTL_NO);
			nRet = ydStockDao.getYdStock(recPara, rsResult, 131);
			if(nRet <= 0){
				return nRet;
			}
		} else if ("".equals(strStlGp)) {

			//주편공통 조회
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();								
			recPara.setField("STL_NO", szSTL_NO);
			nRet = ydStockDao.getYdStock(recPara, rsResult, 134);
			if(nRet < 0){
				return nRet;
			} else if(nRet == 0){
				// 슬라브 조회
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recPara  = JDTORecordFactory.getInstance().create();								
				recPara.setField("STL_NO", szSTL_NO);
				nRet = ydStockDao.getYdStock(recPara, rsResult, 131);
				if(nRet < 0){
					return nRet;
				} else if(nRet == 0){
					return nRet;
				} else {
	
					rsResult.first();
					recGetVal = rsResult.getRecord();
		
					szRECORD_PROG_STAT 	= ydDaoUtils.paraRecChkNull(recGetVal, "RECORD_PROG_STAT");
					strPT_TB_COMM 		= szRECORD_PROG_STAT;
				}			
			} else {

				rsResult.first();
				recGetVal = rsResult.getRecord();
				
				szRECORD_PROG_STAT 	= ydDaoUtils.paraRecChkNull(recGetVal, "RECORD_PROG_STAT");
				strPT_TB_COMM 		= szRECORD_PROG_STAT.trim();
				
				if(szRECORD_PROG_STAT.trim().equals("3")){
				
					recGetVal = null;
					rsResult = JDTORecordFactory.getInstance().createRecordSet("");
					recPara  = JDTORecordFactory.getInstance().create();								
					recPara.setField("STL_NO", szSTL_NO);
					nRet = ydStockDao.getYdStock(recPara, rsResult, 131);
					if(nRet < 0){
						return nRet;
					} else if(nRet == 0){
						return nRet;
					} else {
						szMsg = "TB_PT_SLABCOMM(슬라브공통) + TB_YD_STOCK(저장품) + TB_YD_STKLYR(적치단) 테이블 조회 성공 STL_NO(" + szSTL_NO + ")";
			            ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);					
					}
				}
			}			
		}
		
		// 조회된 주편 or 슬라브의 레코드를 읽어온다. 
		recGetVal = null;
		rsResult.first();
		recGetVal = rsResult.getRecord();
		
		// 재료외형뿐만 아니라 스키핑여부와 스카핑완료도 공통에 읽은 것을 저장품에 업데이트 처리 위해 편집 
		outRec.setField("STL_APPEAR_GP"   , ydDaoUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP"));
		outRec.setField("SCARFING_YN"     , ydDaoUtils.paraRecChkNull(recGetVal, "SCARFING_YN"));
		outRec.setField("SCARFING_DONE_YN", ydDaoUtils.paraRecChkNull(recGetVal, "SCARFING_DONE_YN"));
		
		//=============================================================================================
		// 코드 매핑함수 호출 전 야드값 처리 - 연주전단지시(CTYDJ011), 연주전단실적(CSYDJ001)
		//=============================================================================================
		if(szTcCode.equals("CTYDJ011")){
			
			/*
			연주 Machine코드(CC_MC_CD)
			수신전문에서 연주 Machine코드(CC_MC_CD)값이 존재시 야드구분값(YD_GP)은 전문의 연주 Machine코드(CC_MC_CD)
			값으로 처리하고 만약 존재하지 않으면 공통에서 읽은 공장공정코드값(PLNT_PROC_CD)을 가지고 야드구분값(YD_GP)을 처리한다.
			수신 전문 레코드에서  연주 Machine코드(CC_MC_CD)를 뽑아온다.
			*/
			szCC_MC_CD = ydDaoUtils.paraRecChkNull(inRec, "CC_MC_CD");
			if(!szCC_MC_CD.trim().equals("")){
				if(szCC_MC_CD.equals("1")|| 
				   szCC_MC_CD.equals("2")|| 
				   szCC_MC_CD.equals("3")){
					szYD_GP = "A";				
				} else if(szCC_MC_CD.equals("6")){
					szYD_GP = "0";								
				}
			} else {
				/*
				조회한 레코드에서 공장공정코드값(PLNT_PROC_CD) 을 가져온다.
				앞의 두자리가 "CC" 면 C연주(A)야드 "CA" 면 B-CAST야드(0)
				*/ 
				szPLNT_PROC_CD = ydDaoUtils.paraRecChkNull(recGetVal, "PLNT_PROC_CD");
				if(!szPLNT_PROC_CD.equals("")){
					if(szPLNT_PROC_CD.substring(0, 2).equals("CC")){
						szYD_GP = "A";
					} else if(szPLNT_PROC_CD.substring(0, 2).equals("CA")){
						szYD_GP = "0";
					}
				}
			}
			// 야드값 설정
			recGetVal.setField("YD_GP", szYD_GP);
			
		} else if(szTcCode.equals("CSYDJ001")){
			/*
			연주CCM번호(CC_CCM_NO)
			수신전문에서 연주CCM번호(CC_CCM_NO)값이 존재시 야드구분값(YD_GP)은 전문의 연주CCM번호(CC_CCM_NO)
			값으로 처리하고 만약 존재하지 않으면 공통에서 읽은 공장공정코드값(PLNT_PROC_CD)을 가지고 야드구분값(YD_GP)을 처리한다.
			수신 전문 레코드에서  전문의 연주CCM번호(CC_CCM_NO)를 뽑아온다.
			*/
			szCC_CCM_NO = ydDaoUtils.paraRecChkNull(inRec, "CC_CCM_NO");
			if(!szCC_CCM_NO.trim().equals("")){
				if(szCC_CCM_NO.equals("1")|| 
				   szCC_CCM_NO.equals("2")|| 
				   szCC_CCM_NO.equals("3")){
					szYD_GP = "A";				
				} else if(szCC_CCM_NO.equals("6")){
					szYD_GP = "0";								
				}
			} else {
				/*
				조회한 레코드에서 공장공정코드값(PLNT_PROC_CD) 을 가져온다.
				앞의 두자리가 "CC" 면 C연주(A)야드 "CA" 면 B-CAST야드(0)
				*/ 
				szPLNT_PROC_CD = ydDaoUtils.paraRecChkNull(recGetVal, "PLNT_PROC_CD");
				if(!szPLNT_PROC_CD.equals("")){
					if(szPLNT_PROC_CD.substring(0, 2).equals("CC")){
						szYD_GP = "A";
					} else if(szPLNT_PROC_CD.substring(0, 2).equals("CA")){
						szYD_GP = "0";
					}
				}
			}
			
			// 야드값 설정
			recGetVal.setField("YD_GP", szYD_GP);
		}
		recGetVal.setField("JMS_TC_CD", szTcCode);
		
		nRet = CallMapping(recGetVal, outRec, strPT_TB_COMM);
		
		return nRet;
		
	}
	
	
	public int CallMapping(JDTORecord inRec, JDTORecord outRec, String strPT_TB_COMM) throws JDTOException {
		JDTORecord recPara        = null;
		JDTORecord tmpRec         = null;
		String szMethodName       = "CallMapping";
		String szMsg              = "";
		String szSLAB_WO_RT_CD    = "";
		String szSTL_APPEAR_GP    = "";
		String szORD_YEOJAE_GP    = "";
		String szSCARFING_YN      = "";
		String szSCARFING_DONE_YN = "";
		String szMILL_WO_EXN      = "";
		String szCURR_PROG_CD     = "";
		String szBayDistribution  = "";
		String szSTL_PROG_CD      = "";
		String szAimYd            = "";
		String szAimBay           = "";
		String szAimRt            = "";
		String szRcvTcCode        = "";
		String szSTL_NO        	  = "";
		
		int nCntCheck             = 0;
		int nRet                  = 0;
		boolean bRet              = false;
		ymCommonDAO dao = ymCommonDAO.getInstance();
	    List FrtoProductList = null;
		//===================================================================
		// 수신 파라미터에서 항목 추출
		//===================================================================
		szSLAB_WO_RT_CD    = ydDaoUtils.paraRecChkNull(inRec, "SLAB_WO_RT_CD");
		szSTL_APPEAR_GP    = ydDaoUtils.paraRecChkNull(inRec, "STL_APPEAR_GP");
		szORD_YEOJAE_GP    = ydDaoUtils.paraRecChkNull(inRec, "ORD_YEOJAE_GP");
		szSCARFING_YN      = ydDaoUtils.paraRecChkNull(inRec, "SCARFING_YN");
		szSCARFING_DONE_YN = ydDaoUtils.paraRecChkNull(inRec, "SCARFING_DONE_YN");
		szMILL_WO_EXN      = ydDaoUtils.paraRecChkNull(inRec, "MILL_WO_EXN");
		szCURR_PROG_CD     = ydDaoUtils.paraRecChkNull(inRec, "CURR_PROG_CD");
		szSTL_NO		   = ydDaoUtils.paraRecChkNull(inRec, "STL_NO");
		szRcvTcCode        = ydUtils.getTcCode(inRec);
		
		//===================================================================
		// 동분산 구분
		// String szBayDistribution = getBayDistribution(inRec);
		//===================================================================
		tmpRec = JDTORecordFactory.getInstance().create();
		bRet = GetBreRule5.getYDB001(inRec, tmpRec);
    	if(bRet){
        	szBayDistribution = ydDaoUtils.paraRecChkNull(tmpRec, "YD_BAY_GP");

    		szMsg = "*** 동분산구분 추출 성공 *** : " + szBayDistribution;
    		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
    	} else {
    		szMsg = "*** 동분산구분 추출 실패 *** : " + szBayDistribution;
    		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
    	}
    	
		//===================================================================
    	// 목표야드
    	// String szAimYd  = getAimYd(inRec);
		//===================================================================
		tmpRec = JDTORecordFactory.getInstance().create();
		bRet = GetBreRule5.getYDB002(inRec, tmpRec);
    	if(bRet){
    		szAimYd = ydDaoUtils.paraRecChkNull(tmpRec, "YD_AIM_YD_GP");

    		szMsg = "*** 목표야드 추출 성공 *** : " + szAimYd;
    		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
    	} else {
    		szMsg = "*** 목표야드 추출 실패 *** : " + szAimYd;
    		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
    	}
        
		//===================================================================
    	// 목표동
		// String szAimBay = getAimBay(inRec, szBayDistribution, szAimYd);
		//===================================================================
		tmpRec = JDTORecordFactory.getInstance().create();
    	recPara = JDTORecordFactory.getInstance().create();
    	recPara.setField("YD_AIM_YD_GP" , szAimYd);
    	recPara.setField("SLAB_WO_RT_CD", szSLAB_WO_RT_CD);
    	recPara.setField("STL_APPEAR_GP", szSTL_APPEAR_GP);
    	recPara.setField("YD_BAY_GP"    , szBayDistribution);
		
    	bRet = GetBreRule5.getYDB003(recPara, tmpRec);
    	if(bRet){
    		szAimBay = ydDaoUtils.paraRecChkNull(tmpRec, "YD_AIM_BAY_GP");

    		szMsg = "*** 목표동 추출 성공 *** : " + szAimBay;
    		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
    	} else {
    		szMsg = "*** 목표동 추출 실패 *** : " + szAimBay;
    		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
    	}    	
    	
		//===================================================================
    	// 목표행선구분
		// String szAimRt  = getAimRtBase(inRec, szBayDistribution, strPT_TB_COMM);
		//===================================================================
		tmpRec = JDTORecordFactory.getInstance().create();
    	recPara = JDTORecordFactory.getInstance().create();
    	recPara.setField("SLAB_WO_RT_CD"   , szSLAB_WO_RT_CD);
    	recPara.setField("STL_APPEAR_GP"   , szSTL_APPEAR_GP);
    	recPara.setField("YD_BAY_GP"       , szBayDistribution);

    	if(szRcvTcCode.equals("Y1YDL009")){
//			if(strPT_TB_COMM.equals("3")){
//				szSTL_PROG_CD = YdCommonUtils.getCurrProgCd("S", szSLAB_WO_RT_CD, szORD_YEOJAE_GP, szSCARFING_YN, szSCARFING_DONE_YN, szMILL_WO_EXN);								
//			} else {
//				szSTL_PROG_CD = YdCommonUtils.getCurrProgCd("B", szSLAB_WO_RT_CD, szORD_YEOJAE_GP, szSCARFING_YN, szSCARFING_DONE_YN, szMILL_WO_EXN);								
//			}
			
			//---------------------------------------------------------------------------------------------------------
			//---------------------------------------------------------------------------------------------------------
        	//공정 함수를 이용한 진도코드 가져오기
        	if(strPT_TB_COMM.equals("3")){
        		//슬라브 공통
    		    String queryID	= "ym.facilitywork.putwrecord.session.getListcurrprogcdSlab";
    		    FrtoProductList = dao.getCommonList(queryID, new Object[]{szSTL_NO});        		
        	}else  {
        		//주편 공통
    		    String queryID	= "ym.facilitywork.putwrecord.session.getListcurrprogcd";
    		    FrtoProductList = dao.getCommonList(queryID, new Object[]{szSTL_NO});
        	}       	

	    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);

	    	szSTL_PROG_CD =StringHelper.evl(FrtoProduct.getFieldString("CURR_PROG_CD"), "");
			ydUtils.putLog(szClassName, szMethodName, "IHSF_PM_주편SLAB진도찾기==>>:"+szSTL_PROG_CD, YdConstant.DEBUG);
			//---------------------------------------------------------------------------------------------------------
			//---------------------------------------------------------------------------------------------------------
			
			
		} else {
			szSTL_PROG_CD = szCURR_PROG_CD;		
		}
    	recPara.setField("STL_PROG_CD", szSTL_PROG_CD);
    	
    	bRet = GetBreRule5.getYDB004(recPara, tmpRec);
    	if(bRet){
    		szAimRt = ydDaoUtils.paraRecChkNull(tmpRec, "YD_AIM_RT_GP");

    		szMsg = "*** 목표행선구분 추출 성공 *** : " + szAimRt;
    		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
    	} else {
    		szMsg = "*** 목표행선구분 추출 실패 *** : " + szAimRt;
    		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
    	}    	
		
		if(!szAimYd.equals("")){
			outRec.setField("YD_AIM_YD_GP", szAimYd);
			nCntCheck++;
			nRet = 1;
		}
		
		if(!szAimRt.equals("")){
			outRec.setField("YD_AIM_RT_GP", szAimRt);
			nCntCheck++;
			nRet = 1;
		}
		
		if(!szAimBay.equals("")){
			outRec.setField("YD_AIM_BAY_GP", szAimBay);
			nCntCheck++;
			nRet = 1;
		}	
		
		return nRet;
	}
	
	public int getMappingCommonField(String strTcCode, String strSTL_NO) throws JDTOException {
		return this.getMappingCommonField(strTcCode, strSTL_NO, "", true);
	}
	
	public int getMappingCommonField(String strTcCode, String strSTL_NO, boolean isSend) throws JDTOException {
		return this.getMappingCommonField(strTcCode, strSTL_NO, "", isSend);
	}
	
	public int getMappingCommonField(String strTcCode, String strSTL_NO, String strStlGp) throws JDTOException {
		return this.getMappingCommonField(strTcCode, strSTL_NO, strStlGp, true);
	}
	
	/**
	 * 저장품 업데이트 위한 데이터 매핑 (MSLABCOMM, SLABCOMM)
	 * 권오창
	 * 2010.01.26
	 * 	
	 *  재료번호로 공통(주편 or 슬라브)테이블의 필수 공통 항목을 편집 및 코드매핑처리를 하여 저장품에 업데이트 한후 Level2에도 전송
	 *  
	 * @param TC_CODE, STL_NO
	 * @return 0:건수없음    -1:조회쿼리 파라미터에러 or 업데이트쿼리 실패     -2: 예외발생     -3: L2전송에러
	 */
	public int getMappingCommonField(String strTcCode, String strSTL_NO, String strStlGp,boolean isSend) throws JDTOException {
		// DAO및 UTIL객체 생성
		YdDelegate ydDelegate     = new YdDelegate();
		
		// 레코드 선언
		JDTORecordSet rsResult    = null;
		JDTORecord recPara        = null;
		JDTORecord recGetVal      = null; 
		JDTORecord outRecTemp     = null; 
		JDTORecord recEditRec     = null; 
		JDTORecord recResult      = null;
		
		// 변수 선언
		String szMethodName       = "getMappingCommonField";
		String szMsg              = "";
		String szOperationName    = "주편/슬라브/저장품 필수 공통항목 저장품에 업데이트 처리";
		String szRECORD_PROG_STAT = "";
		String szTcCode           = strTcCode;
		String szSTL_NO           = strSTL_NO;
		String szYD_GP            = "";
		int nRet                  = 0;
				
		try{	
		    //================================================================================================= 
            // 수정내용 : 재료형태를 파라미터로 입력받아서 처리
            //=================================================================================================
            rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();			
			
            if ("MSLAB".equals(strStlGp)) {
            	recPara.setField("MSLAB_NO", szSTL_NO);
            	nRet = ydStockDao.getYdStock(recPara, rsResult, 184);
    			if(nRet <= 0){
    				return nRet;
    			}
    			
                rsResult.first();
    			recGetVal = rsResult.getRecord();		
    			
            }else if("SLAB".equals(strStlGp)) {
            	
            	recPara.setField("SLAB_NO", szSTL_NO);
				nRet = ydStockDao.getYdStock(recPara, rsResult, 185);
				if(nRet <= 0){
					return nRet;
				}
				
	            rsResult.first();
				recGetVal = rsResult.getRecord();		
				
            } else if("".equals(strStlGp)) {
            
				//=================================================================================================
				// 주편공통 조회 (intGp : 184)
				//    쿼리 : com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCommonFieldYdStockMslabCommBySTLNo 
				// 파라미터 : V_MSLAB_NO
				//=================================================================================================
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recPara  = JDTORecordFactory.getInstance().create();								
				recPara.setField("MSLAB_NO", szSTL_NO);
				nRet = ydStockDao.getYdStock(recPara, rsResult, 184);
				if(nRet < 0){
					return nRet;
				} else if(nRet == 0){
					//=================================================================================================
					// 슬라브공통 조회 (intGp : 185)
					//    쿼리 : com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCommonFieldYdStockSlabCommBySTLNo
					// 파라미터 : V_SLAB_NO
					//=================================================================================================
					rsResult = JDTORecordFactory.getInstance().createRecordSet("");
					recPara  = JDTORecordFactory.getInstance().create();								
					recPara.setField("SLAB_NO", szSTL_NO);
					nRet = ydStockDao.getYdStock(recPara, rsResult, 185);
					if(nRet < 0){
						return nRet;
					} else if(nRet == 0){
						return nRet;
					} else {
						rsResult.first();
						recGetVal = rsResult.getRecord();
						
						strStlGp = "SLAB";	
					}			
				} else {
					rsResult.first();
					recGetVal = rsResult.getRecord();
					
					strStlGp = "MSLAB";	
					
					szRECORD_PROG_STAT = ydDaoUtils.paraRecChkNull(recGetVal, "RECORD_PROG_STAT");
					
					szMsg = "재료번호(" + szSTL_NO + ") 주편공통 레코드 상태(" + szRECORD_PROG_STAT + ")";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					
					if(szRECORD_PROG_STAT.trim().equals("3")){
						//=================================================================================================
						// 슬라브공통 조회 (intGp : 185)
						//    쿼리 : com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCommonFieldYdStockSlabCommBySTLNo
						// 파라미터 : V_SLAB_NO
						//=================================================================================================
						rsResult = JDTORecordFactory.getInstance().createRecordSet("");
						recPara  = JDTORecordFactory.getInstance().create();								
						recPara.setField("SLAB_NO", szSTL_NO);
						nRet = ydStockDao.getYdStock(recPara, rsResult, 185);
						if(nRet < 0){
							return nRet;
						} else if(nRet == 0){
							return nRet;
						} else {
							
				            rsResult.first();
							recGetVal = rsResult.getRecord();	
							
							strStlGp = "SLAB";	
						}
					}
				}			
            }
			//===================================================================================
			// (주편 or 슬라브공통)과 저장품 필수 공통항목 편집 
			//===================================================================================
			recEditRec = JDTORecordFactory.getInstance().create();								
			recEditRec.setField("STL_NO"          , ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO"));                           
			recEditRec.setField("STL_APPEAR_GP"   ,	ydDaoUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP"));
			recEditRec.setField("STL_PROG_CD"     ,	ydDaoUtils.paraRecChkNull(recGetVal, "STL_PROG_CD"));
			recEditRec.setField("ORD_YEOJAE_GP"   ,	ydDaoUtils.paraRecChkNull(recGetVal, "ORD_YEOJAE_GP"));
			recEditRec.setField("ORD_NO"          ,	ydDaoUtils.paraRecChkNull(recGetVal, "ORD_NO"));
			recEditRec.setField("SLAB_WO_RT_CD"   ,	ydDaoUtils.paraRecChkNull(recGetVal, "SLAB_WO_RT_CD"));
			recEditRec.setField("PTOP_PLNT_GP"    ,	ydDaoUtils.paraRecChkNull(recGetVal, "PTOP_PLNT_GP"));
			recEditRec.setField("SCARFING_YN"     ,	ydDaoUtils.paraRecChkNull(recGetVal, "SCARFING_YN"));
			recEditRec.setField("SCARFING_DONE_YN",	ydDaoUtils.paraRecChkNull(recGetVal, "SCARFING_DONE_YN"));
			recEditRec.setField("YD_MTL_T"        ,	ydDaoUtils.paraRecChkNull(recGetVal, "YD_MTL_T"));
			recEditRec.setField("YD_MTL_W"        ,	ydDaoUtils.paraRecChkNull(recGetVal, "YD_MTL_W"));
			recEditRec.setField("YD_MTL_L"        ,	ydDaoUtils.paraRecChkNull(recGetVal, "YD_MTL_L"));
			recEditRec.setField("YD_MTL_WT"       ,	ydDaoUtils.paraRecChkNull(recGetVal, "YD_MTL_WT"));
			recEditRec.setField("PLNT_PROC_CD"    ,	ydDaoUtils.paraRecChkNull(recGetVal, "PLNT_PROC_CD"));
			recEditRec.setField("ORD_DTL"         ,	ydDaoUtils.paraRecChkNull(recGetVal, "ORD_DTL"));
			recEditRec.setField("ITEMNAME_CD"     ,	ydDaoUtils.paraRecChkNull(recGetVal, "ITEMNAME_CD"));
			recEditRec.setField("SPEC_ABBSYM"     ,	ydDaoUtils.paraRecChkNull(recGetVal, "SPEC_ABBSYM"));
			recEditRec.setField("CC_CCM_NO"       ,	ydDaoUtils.paraRecChkNull(recGetVal, "CC_CCM_NO"));
			recEditRec.setField("HCR_GP"          ,	ydDaoUtils.paraRecChkNull(recGetVal, "HCR_GP"));
			recEditRec.setField("ORD_HCR_GP"      ,	ydDaoUtils.paraRecChkNull(recGetVal, "ORD_HCR_GP"));
			recEditRec.setField("DEMANDER_CD"     ,	ydDaoUtils.paraRecChkNull(recGetVal, "DEMANDER_CD"));
			recEditRec.setField("WO_MSLAB_RPR_MTD",	ydDaoUtils.paraRecChkNull(recGetVal, "WO_MSLAB_RPR_MTD"));
			recEditRec.setField("YD_STK_LOT_CD"   ,	ydDaoUtils.paraRecChkNull(recGetVal, "STACK_LOT_NO"));
	                    
			//===================================================================================
			// 코드매핑 처리
			//===================================================================================
			outRecTemp = JDTORecordFactory.getInstance().create();
			nRet = this.MakeCodeMapping(szTcCode, szSTL_NO, recGetVal, outRecTemp, strStlGp);
			if(nRet <= 0){
				szMsg = "[nRet " + nRet + "] 매핑되는 코드가 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			}else {
				String szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_RT_GP");
				if(!szYD_AIM_RT_GP.equals("")){
					recEditRec.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);
				}

				String szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_YD_GP");
				if(!szYD_AIM_YD_GP.equals("")){
					recEditRec.setField("YD_AIM_YD_GP", szYD_AIM_YD_GP);
				}

				String szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_BAY_GP");
				if(!szYD_AIM_BAY_GP.equals("")){
					recEditRec.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
				}
			}
			
			nRet = stock.setYdStkLocTpCd(recEditRec);
			if( nRet < 0 ){
				szMsg= "[산적LotType 산적LotCD SET] Error :: [" + nRet + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			}	
			
			//==================================================================================================
			// (주편 or 슬라브) 공통에서 읽어온 항목들을 저장품 테이블에 업데이트
			//==================================================================================================
			recEditRec.setField("MODIFIER", szTcCode);
			nRet = ydStockDao.updYdStock(recEditRec, 0);
			if(nRet <= 0){
				szMsg= "공통에서 읽어온 항목들을 저장품 테이블에 업데이트 실패 [" + nRet + "] TCCODE(" + szTcCode + ")";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			}else {
				szMsg= "공통에서 읽어온 항목들을 저장품 테이블에 업데이트 성공 [" + nRet + "] TCCODE(" + szTcCode + ")";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);					
			}
			
			if(isSend){
				//===================================================================================================
				// 야드L2로 저장품 제원 전송
				//===================================================================================================
				szYD_GP = ydDaoUtils.paraRecChkNull(recGetVal, "YD_GP");
				
				recResult = JDTORecordFactory.getInstance().create();
				if(szYD_GP.equals(YdConstant.YD_GP_C_SLAB_YARD)){				// C연주슬라브야드
					recResult.setField("MSG_ID", "YDY1L002");
					szMsg = "연주슬라브 L2로 저장품제원(YDY1L002) 송신";
				}else if(szYD_GP.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)){	// A후판슬라브야드
					recResult.setField("MSG_ID", "YDY3L002");
					szMsg = "후판슬라브 L2로 저장품제원(YDY3L002) 송신";
				}else if(szYD_GP.equals(YdConstant.YD_GP_PLATE_GDS_YARD)){		// 후판제품창고야드
					recResult.setField("MSG_ID", "YDY4L002");
					szMsg = "후판제품 L2로 저장품제원(YDY4L002) 송신";
				}else if(szYD_GP.equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD)){	// C열연코일소재야드
					recResult.setField("MSG_ID", "YDY5L002");
					szMsg = "코일야드 L2로 저장품제원(YDY5L002) 송신";
				}else if(szYD_GP.equals(YdConstant.YD_GP_C_HR_COIL_GDS_YARD)){	// C열연코일제품야드
					recResult.setField("MSG_ID", "YDY5L002");
					szMsg = "코일야드 L2로 저장품제원(YDY5L002) 송신";
				}else {
					szMsg = "야드구분값이 없거나 지원하지 않는 야드 구분입니다. YD_GP(" + szYD_GP + ")";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
		            return -3;
				}
				
				recResult.setField("YD_INFO_SYNC_CD", "5");        // 5:지정저장품
				recResult.setField("STL_NO"         , szSTL_NO);
				recResult.setField("YD_STK_COL_GP"  , "");
				recResult.setField("YD_STK_BED_NO"  , "");
				recResult.setField("DEL_YN_CHECK"   , "N");
				ydDelegate.sendMsg(recResult);
			}	
		}catch(Exception e){
			szMsg = "[YdCodeMapping::getMappingCommonField()] 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -2;
		}
		
		return nRet;
	}	

	/**
	 * 저장품 업데이트 위한 데이터 매핑 (PLATECOMM) 
	 * 권오창
	 * 2010.01.29
	 * 	
	 *  재료번호로 공통(플레이트)테이블의 필수 공통 항목을 편집 및 코드매핑처리를 하여 저장품에 업데이트 처리
	 *  
	 * @param TC_CODE, STL_NO
	 * @return 0:건수없음    -1:조회쿼리 파라미터에러 or 업데이트쿼리 실패     -2: 예외발생     -3: L2전송에러
	 */
	public int getMappingCommonFieldPlateComm(String strTcCode, String strSTL_NO) throws JDTOException {
		// DAO및 UTIL객체 생성
		YdDelegate ydDelegate     = new YdDelegate();
		
		
		// 레코드 선언
		JDTORecordSet rsResult    = null;
		JDTORecord recPara        = null;
		JDTORecord recGetVal      = null; 
		JDTORecord outRecTemp     = null; 
		JDTORecord recEditRec     = null; 
		JDTORecord recResult      = null;

		
		// 변수 선언
		String szMethodName       = "getMappingCommonFieldPlateComm";
		String szMsg              = "";
		String szOperationName    = "플레이트/저장품 필수 공통항목 저장품에 업데이트 처리";
		String szRECORD_PROG_STAT = "";
		String szTcCode           = strTcCode;
		String szSTL_NO           = strSTL_NO;
		String szYD_GP            = "";
		int nRet                  = 0;
		

		
		
		
		try{	
			szMsg = "================================ getMappingCommonFieldPlateComm() IN ========================================";
            ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);	
            
            
            
            
            
			//=================================================================================================
			// 플레이트 공통 조회 (intGp : 4)
			//    쿼리 : com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPLATECOMM_PIDEV
			// 파라미터 : V_PLATE_NO
			//=================================================================================================
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();								
			recPara.setField("PLATE_NO", szSTL_NO);
			nRet = ydStockDao.getYdStock(recPara, rsResult, 4);
			if(nRet < 0){
				szMsg = "TB_PT_PLATECOMM(플레이트공통) 테이블 조회 오류 [" + nRet + "] PLATE_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return nRet;
			} else if(nRet == 0){
				szMsg = "TB_PT_PLATECOMM(플레이트공통) 테이블 조회건수 없음 [" + nRet + "] PLATE_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				return nRet;
			} else {
				szMsg = "TB_PT_PLATECOMM(플레이트공통) 테이블 조회 성공 PLATE_NO(" + szSTL_NO + ")";
	            ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
	            
				rsResult.first();
				recGetVal = rsResult.getRecord();
			}			
			
			
			
			
			
			//====================================================================================
			// 조회항목 출력
			//====================================================================================
			szOperationName = "플레이트/저장품 필수 항목 조회 결과 출력";			
			ydUtils.displayRecord(szOperationName, recGetVal);
			
			
			
			
			
			//===================================================================================
			// 플레이트공통과 저장품 필수 공통항목 편집 
			//===================================================================================
			recEditRec = JDTORecordFactory.getInstance().create();								
			recEditRec.setField("STL_NO"             , ydDaoUtils.paraRecChkNull(recGetVal, "PLATE_NO"));                 // 재료번호는 필수
			recEditRec.setField("STL_PROG_CD"        , ydDaoUtils.paraRecChkNull(recGetVal, "CURR_PROG_CD"));        
			recEditRec.setField("ORD_YEOJAE_GP"      , ydDaoUtils.paraRecChkNull(recGetVal, "ORD_YEOJAE_GP"));       
			recEditRec.setField("ORD_NO"             , ydDaoUtils.paraRecChkNull(recGetVal, "ORD_NO"));              
			recEditRec.setField("ORD_DTL"            , ydDaoUtils.paraRecChkNull(recGetVal, "ORD_DTL"));             
			recEditRec.setField("PTOP_PLNT_GP"       , ydDaoUtils.paraRecChkNull(recGetVal, "PTOP_PLNT_GP"));        
			recEditRec.setField("ITEMNAME_CD"        , ydDaoUtils.paraRecChkNull(recGetVal, "ITEMNAME_CD"));         
			recEditRec.setField("PLNT_PROC_CD"       , ydDaoUtils.paraRecChkNull(recGetVal, "PLNT_PROC_CD"));        
			recEditRec.setField("STL_APPEAR_GP"      , ydDaoUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP"));       
			recEditRec.setField("DEMANDER_CD"        , ydDaoUtils.paraRecChkNull(recGetVal, "DEMANDER_CD"));         
			recEditRec.setField("SPEC_ABBSYM"        , ydDaoUtils.paraRecChkNull(recGetVal, "SPEC_ABBSYM"));         
			recEditRec.setField("APPEAR_GRADE"       , ydDaoUtils.paraRecChkNull(recGetVal, "APPEAR_GRADE"));        
			recEditRec.setField("OVERALL_STAMP_GRADE", ydDaoUtils.paraRecChkNull(recGetVal, "OVERALL_STAMP_GRADE")); 
			recEditRec.setField("YD_PILING_CD"       , ydDaoUtils.paraRecChkNull(recGetVal, "YD_PILING_CD"));        
			recEditRec.setField("YD_BOOK_OUT_LOC"    , ydDaoUtils.paraRecChkNull(recGetVal, "YD_BOOK_OUT_LOC"));     
	             
	           
             
			            
	                    
			//===================================================================================
			// 코드매핑 처리
			//===================================================================================
			outRecTemp = JDTORecordFactory.getInstance().create();
			nRet = this.MakeCodeMapping(szTcCode, szSTL_NO, recGetVal, outRecTemp);
			if(nRet <= 0){
				szMsg = "[nRet " + nRet + "] 매핑되는 코드가 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			}else {
				String szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_RT_GP");
				if(!szYD_AIM_RT_GP.equals("")){
					recEditRec.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);
				}

				String szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_YD_GP");
				if(!szYD_AIM_YD_GP.equals("")){
					recEditRec.setField("YD_AIM_YD_GP", szYD_AIM_YD_GP);
				}

				String szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(outRecTemp, "YD_AIM_BAY_GP");
				if(!szYD_AIM_BAY_GP.equals("")){
					recEditRec.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
				}
			}
			

			
			
			
			//==================================================================================================
			// 플레이트 공통에서 읽어온 항목들을 저장품 테이블에 업데이트
			//==================================================================================================
			recEditRec.setField("MODIFIER", szTcCode);
			nRet = ydStockDao.updYdStock(recEditRec, 0);
			if(nRet <= 0){
				szMsg= "공통에서 읽어온 항목들을 저장품 테이블에 업데이트 실패 [" + nRet + "] TCCODE(" + szTcCode + ")";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			}else {
				szMsg= "공통에서 읽어온 항목들을 저장품 테이블에 업데이트 성공 [" + nRet + "] TCCODE(" + szTcCode + ")";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);					
			}
			
			
			
			
			
			//===================================================================================================
			// 야드L2로 저장품 제원 전송
			//===================================================================================================
			szYD_GP = ydDaoUtils.paraRecChkNull(recGetVal, "YD_GP");

			
			recResult = JDTORecordFactory.getInstance().create();
			if(szYD_GP.equals(YdConstant.YD_GP_C_SLAB_YARD)){				// C연주슬라브야드
				recResult.setField("MSG_ID", "YDY1L002");
				szMsg = "연주슬라브 L2로 저장품제원(YDY1L002) 송신";
			}else if(szYD_GP.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)){	// A후판슬라브야드
				recResult.setField("MSG_ID", "YDY3L002");
				szMsg = "후판슬라브 L2로 저장품제원(YDY3L002) 송신";
			}else if(szYD_GP.equals(YdConstant.YD_GP_PLATE_GDS_YARD)){		// 후판제품창고야드
				recResult.setField("MSG_ID", "YDY4L002");
				szMsg = "후판제품 L2로 저장품제원(YDY4L002) 송신";
			}else if(szYD_GP.equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD)){	// C열연코일소재야드
				recResult.setField("MSG_ID", "YDY5L002");
				szMsg = "코일야드 L2로 저장품제원(YDY5L002) 송신";
			}else if(szYD_GP.equals(YdConstant.YD_GP_C_HR_COIL_GDS_YARD)){	// C열연코일제품야드
				recResult.setField("MSG_ID", "YDY5L002");
				szMsg = "코일야드 L2로 저장품제원(YDY5L002) 송신";
			}else {
				szMsg = "야드구분값이 없거나 지원하지 않는 야드 구분입니다. YD_GP(" + szYD_GP + ")";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
	            return -3;
			}
			
			recResult.setField("YD_INFO_SYNC_CD", "5");        // 5:지정저장품
			recResult.setField("STL_NO"         , szSTL_NO);
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			recResult.setField("DEL_YN_CHECK"   , "N");
			ydDelegate.sendMsg(recResult);
            ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);	
				
			
			szMsg = "================================ getMappingCommonFieldPlateComm() OUT ========================================";
            ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);	
		}catch(Exception e){
			szMsg = "[YdCodeMapping::getMappingCommonFieldPlateComm()] 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);		
			return -2;
		}
		
		return nRet;
	}
}
