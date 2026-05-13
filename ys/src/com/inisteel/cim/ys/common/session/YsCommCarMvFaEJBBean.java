/**
 * @(#)YsCommCarMvFaEJBBean.java
 * 
 * @version			1.0
 * @author 			현대제철
 * @date			2015/11/14
 * 
 * @description		이클래스는 차량이동처리 Session EJB클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.01  2013/04/03   송정현     
 */


package com.inisteel.cim.ys.common.session;


import com.inisteel.cim.common.exception.DAOException;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.record.JDTORecordFactory;

//2025.12.06 L2 -> L3 구내운송 영차도착				
import com.inisteel.cim.ys.common.dao.YsCommDAO;

import xlib.cmc.GridData;

import com.inisteel.cim.ys.common.util.YsCommUtils;

/**
 * 차량이동처리 Facade Session EJB 
 *
 * @ejb.bean name="YsCommCarMvFaEJB" jndi-name="YsCommCarMvFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class YsCommCarMvFaEJBBean extends BaseSessionBean {
	
	// Session Name 
	private static final long serialVersionUID = 1L;
	
	private YsCommUtils commUtils 	= new YsCommUtils();

	
	// 2025.12.06 L2 -> L3 구내운송 영차도착				
	private YsCommDAO 	commDao 	= new YsCommDAO();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	

	/**
	 *      [A] 오퍼레이션명 : 소재차량도착Point요구(TSYSJ002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYSJ002(JDTORecord rcvMsg)throws DAOException  {
		String methodNm 	= "소재차량도착Point요구[YsCommCarMvFaEJB.rcvTSYSJ002]";
		String logId 		= commUtils.getLogId();
	    String szMsg		= "";
	    String szWLOC_CD	= "";						// 개소코드

		JDTORecord	jrRst	= JDTORecordFactory.getInstance().create();

		try {

			commUtils.printLog(logId, methodNm, "F+", rcvMsg);
			
			szWLOC_CD	= commUtils.trim(rcvMsg.getFieldString("WLOC_CD")); 
// **************************************************************************
// 특수강 대형야드 신예화 개소 코드 이면  YsCommCarTSMvSeEJB Session Method
//		위치						차량위치			개소코드			야드포인트코드	
/////////////////////////////////////////////////////////////////////////////
//		빌렛정정 남1문				GETR11			S3Y22			1E01
//		빌렛정정 남2문				GETR21			S4S13			1E02
//		대형옥내 남4문				GETR41			S3S20			1E04
//		빌렛정정 보급대 크래들 (밴드쏘)		GETR42			S3Y99			2E04
/////////////////////////////////////////////////////////////////////////////
//		대형옥외 옥외야드				GFTR11			S3Y21			1F01
/////////////////////////////////////////////////////////////////////////////
//		사외통합 야드(철분말)			GHTR11			BSY04			1H01
//								GHTR21							1H02
//								GHTR31							1H03
/////////////////////////////////////////////////////////////////////////////
//		소형봉강 야드					GDTR11			SMS12			1A01
//								GDTR21			S5S20			2A01
// 2026.05.07 소형 야드 개소 코드 S5Y30 -> SMS12, S5S20 분리
// **************************************************************************
			szMsg	=	"****** 개소코드 [" + szWLOC_CD + "]";
			commUtils.printLog(logId, szMsg, "");
			
// 2026.01.28 소형봉강야드(S5Y30) 개소코드 추가
// 2026.05.07 소형 야드 개소 코드 S5Y30 -> SMS12, S5S20 분리
	    	if ( "S3Y22".equals(szWLOC_CD) 
	  	      || "S4S13".equals(szWLOC_CD)	 
		      || "S3S20".equals(szWLOC_CD)	 
			  || "S3Y21".equals(szWLOC_CD)	
			  || "BSY04".equals(szWLOC_CD)	
			  || "S3Y99".equals(szWLOC_CD)	
			  || "SMS12".equals(szWLOC_CD)	
			  || "S5S20".equals(szWLOC_CD)	
			  ) 
	    	{
				szMsg="****** 신규 YsCommCarTSMvSeEJB.rcvTSYSJ002 처리";
				commUtils.printLog(logId, szMsg, "");
	    		
				EJBConnector ejbConn = new EJBConnector("default", "YsCommCarTSMvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvTSYSJ002", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			} else {
				szMsg="****** 기존 YsCommCarMvSeEJB.rcvTSYSJ002 처리";
				commUtils.printLog(logId, szMsg, "");

				EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvTSYSJ002", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			}

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return jrRst;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
    } // end of rcvTSYSJ002()
	
	
	

	/**
	 *      [A] 오퍼레이션명 : 소재차량도착(TSYSJ003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYSJ003(JDTORecord rcvMsg)throws DAOException  {
		String methodNm 	= "소재차량도착(TSYSJ003)[YsCommCarMvFaEJB.rcvTSYSJ003]";
		String logId 		= commUtils.getLogId();
	    String szMsg		= "";
	    String szWLOC_CD	= "";						// 개소코드

		JDTORecord	jrRst	= JDTORecordFactory.getInstance().create();

		try {

			commUtils.printLog(logId, methodNm, "F+", rcvMsg);
			
			szWLOC_CD	= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 
// **************************************************************************
// 특수강 대형야드 신예화 개소 코드 이면  YsCommCarTSMvSeEJB Session Method
//		위치						차량위치			개소코드			야드포인트코드	
/////////////////////////////////////////////////////////////////////////////
//		빌렛정정 남1문				GETR11			S3Y22			1E01
//		빌렛정정 남2문				GETR21			S4S13			1E02
//		대형옥내 남4문				GETR41			S3S20			1E04
//		빌렛정정 보급대 크래들 (밴드쏘)		GETR42			S3Y99			2E04
/////////////////////////////////////////////////////////////////////////////
//		대형옥외 옥외야드				GFTR11			S3Y21			1F01
/////////////////////////////////////////////////////////////////////////////
//		사외통합 야드(철분말)			GHTR11			BSY04			1H01
//								GHTR21							1H02
//								GHTR31							1H03
/////////////////////////////////////////////////////////////////////////////
//		소형봉강 야드					GDTR11			S5Y30			1A01
//								GDTR21							2A01
// 2026.05.07 소형 야드 개소 코드 S5Y30 -> SMS12, S5S20 분리
// **************************************************************************
			szMsg	=	"****** 개소코드 [" + szWLOC_CD + "]";
			commUtils.printLog(logId, szMsg, "");
			
// 2026.01.28 소형봉강야드(S5Y30) 개소코드 추가
// 2026.05.07 소형 야드 개소 코드 S5Y30 -> SMS12, S5S20 분리
	    	if ( "S3Y22".equals(szWLOC_CD) 
	  	      || "S4S13".equals(szWLOC_CD)	 
		      || "S3S20".equals(szWLOC_CD)	 
			  || "S3Y21".equals(szWLOC_CD)	
			  || "BSY04".equals(szWLOC_CD)	
			  || "S3Y99".equals(szWLOC_CD)	
			  || "SMS12".equals(szWLOC_CD)	
			  || "S5S20".equals(szWLOC_CD)	
			  ) 
	    	{
				szMsg="****** 신규 YsCommCarTSMvSeEJB.rcvTSYSJ003 처리";
				commUtils.printLog(logId, szMsg, "");
	    		
				EJBConnector ejbConn = new EJBConnector("default", "YsCommCarTSMvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvTSYSJ003", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			} else {
				szMsg="****** 기존 YsCommCarMvSeEJB.rcvTSYSJ003 처리";
				commUtils.printLog(logId, szMsg, "");

				EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvTSYSJ003", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			}
	    	
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return jrRst;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
    } // end of rcvTSYSJ003()

	
	/**
	 *      [A] 오퍼레이션명 : 소재차량출발(TSYSJ004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvTSYSJ004(JDTORecord rcvMsg)throws DAOException  {
		String methodNm 				= "소재차량출발[YsCommCarMvFaEJB.rcvTSYSJ004]";
		String logId 					= commUtils.getLogId();
	    String szMsg					= "";
	    String szWLOC_CD				= "";		// 개소코드
	    String szSPOS_WLOC_CD			= "";		// 발지개소코드
	    String szARR_WLOC_CD			= "";		// 착지개소코드
	    String srTRN_WRK_FULLVOID_GP	= "";					// 운송작업영공구분

		JDTORecord	jrRst	= JDTORecordFactory.getInstance().create();

		try {

			commUtils.printLog(logId, methodNm, "F+", rcvMsg);
			
			szARR_WLOC_CD			= commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD")); 
			szSPOS_WLOC_CD			= commUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD")); 
			srTRN_WRK_FULLVOID_GP   = commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP")	); 
			
// 착지가 가상 개소 코드 이면 발지 개소 코드 사용
// 2025.11.27 특수강 신예화 외 개소코드 처리 관련 추가			
			if(srTRN_WRK_FULLVOID_GP.equals("E") ) {			
		    	if ( "DMY1P".equals(szARR_WLOC_CD)) {
		    		szWLOC_CD = szSPOS_WLOC_CD;
		    	} else {
		    		szWLOC_CD = szARR_WLOC_CD;
		    	}
			}
			else {

				// 영차  출발 이면 발지개소코드를 기본으로 설정 
	    		szWLOC_CD = szSPOS_WLOC_CD;
				
// 2025.12.06 L2 -> L3 구내운송 영차도착				
				String sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "006", "*");
				if("Y".equals(sApplyYnPI)){
					
					// 2025.12.06 발지가 L2(등록되지않은 개소코드) 이고 착지가  L3(등록된 개소코드) 이면 L2 영차 도착 실행
					if(  !"Y".equals(commDao.ApplyYnPI(logId, methodNm, "GE0005", szSPOS_WLOC_CD, "*"))
				      &&  "Y".equals(commDao.ApplyYnPI(logId, methodNm, "GE0005", szARR_WLOC_CD, "*"))){
							szWLOC_CD = szARR_WLOC_CD;

							szMsg	=	"L2 영차 출발 발지개소코드 [" + szWLOC_CD + "]";
							commUtils.printLog(logId, szMsg, "");
					}
				}
				
				
			}
			
// **************************************************************************
// 특수강 대형야드 신예화 개소 코드 이면  YsCommCarTSMvSeEJB Session Method
//		위치						차량위치			개소코드			야드포인트코드	
/////////////////////////////////////////////////////////////////////////////
//		빌렛정정 남1문				GETR11			S3Y22			1E01
//		빌렛정정 남2문				GETR21			S4S13			1E02
//		대형옥내 남4문				GETR41			S3S20			1E04
//		빌렛정정 보급대 크래들 (밴드쏘)		GETR42			S3Y99			2E04
/////////////////////////////////////////////////////////////////////////////
//		대형옥외 옥외야드				GFTR11			S3Y21			1F01
/////////////////////////////////////////////////////////////////////////////
//		사외통합 야드(철분말)			GHTR11			BSY04			1H01
//								GHTR21							1H02
//								GHTR31							1H03
/////////////////////////////////////////////////////////////////////////////
//		소형봉강 야드					GDTR11			S5Y30			1A01
//								GDTR21							2A01
// 2026.05.07 소형 야드 개소 코드 S5Y30 -> SMS12, S5S20 분리
// **************************************************************************
			szMsg	=	"****** 개소코드 [" + szWLOC_CD + "]";
			commUtils.printLog(logId, szMsg, "");
			
// 2026.01.28 소형봉강야드(S5Y30) 개소코드 추가
// 2026.05.07 소형 야드 개소 코드 S5Y30 -> SMS12, S5S20 분리
	    	if ( "S3Y22".equals(szWLOC_CD) 
	  	      || "S4S13".equals(szWLOC_CD)	 
		      || "S3S20".equals(szWLOC_CD)	 
			  || "S3Y21".equals(szWLOC_CD)	
			  || "BSY04".equals(szWLOC_CD)	
			  || "S3Y99".equals(szWLOC_CD)	
			  || "SMS12".equals(szWLOC_CD)	
			  || "S5S20".equals(szWLOC_CD)	
			  ) 
	    	{
				szMsg="****** 신규 YsCommCarTSMvSeEJB.rcvTSYSJ004 처리";
				commUtils.printLog(logId, szMsg, "");
	    		
				EJBConnector ejbConn = new EJBConnector("default", "YsCommCarTSMvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvTSYSJ004", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			} else {
				szMsg="****** 기존 YsCommCarMvSeEJB.rcvTSYSJ004 처리";
				commUtils.printLog(logId, szMsg, "");

				EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvTSYSJ004", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			}

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return jrRst;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
    } // end of rcvTSYSJ004()
	
	
	/**
	 * [A] 오퍼레이션명 : 소재차량대기장 도착(TSYSJ006) 기존에 없던 신규 전문
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcvTSYSJ006(JDTORecord rcvMsg) throws DAOException  {
		String methodNm 	= "소재차량대기장 도착[YsCommCarMvFaEJB.rcvTSYSJ006]";
		String logId 		= commUtils.getLogId();

		JDTORecord	jrRst	= JDTORecordFactory.getInstance().create();

		try {

			commUtils.printLog(logId, methodNm, "F+", rcvMsg);
    		
			EJBConnector ejbConn = new EJBConnector("default", "YsCommCarTSMvSeEJB", this);
			jrRst = (JDTORecord)ejbConn.trx("rcvTSYSJ006", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return jrRst;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
    } // end of rcvTSYSJ006()
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 공차출발취소(TSYSJ014) 기존에 없던 신규 전문
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvTSYSJ014(JDTORecord rcvMsg)throws DAOException  {
		String methodNm 	= "공차출발취소[YsCommCarMvFaEJB.rcvTSYSJ014]";
		String logId 		= commUtils.getLogId();

		JDTORecord	jrRst	= JDTORecordFactory.getInstance().create();

		try {

			commUtils.printLog(logId, methodNm, "F+", rcvMsg);
			
			EJBConnector ejbConn = new EJBConnector("default", "YsCommCarTSMvSeEJB", this);
			jrRst = (JDTORecord)ejbConn.trx("rcvTSYSJ014", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return jrRst;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
    } // end of rcvTSYSJ006()
	
	
  //---------------------------------------------------------------------------
} // end of class

