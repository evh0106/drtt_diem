/**
 * @(#)CCoilJspFaEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2019/05/02
 *
 * @description      2열연 COIL 야드 화면관리 Facade EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자     요청자  수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2019/05/02   정종균  이현진      최초 등록
 * 
 */
package com.inisteel.cim.yd.ccoil.session;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord; 
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
 
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.yd.ccoil.dao.CCoilDAO;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.yd.ccommon.util.CConstant;

/**
 *      [A] 클래스명 : 2열연 COIL 야드 화면관리 Facade EJB
 *
 * @ejb.bean name="CCoilJspFaEJB" jndi-name="CCoilJspFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class CCoilJspFaEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private CCommUtils commUtils = new CCommUtils();       
	private CCoilDAO   coilDao   = new CCoilDAO();
	
	/**
	 * ejbCrate() 
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	/**
	 *      [A] 오퍼레이션명 : YD야드 코드 조회(WiseGrid)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getYdCode(GridData gdReq) throws DAOException {
		String mthdNm = "YD야드코드조회[CCoilJspFaEJB.getYdCode]";
		String logId  = commUtils.getLogId();

		try {
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			//코드조회
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			return (GridData)ejbConn.trx("getYdCode", new Class[] { GridData.class }, new Object[] { gdReq });
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * GridData - 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getSelectData(GridData gdReq) throws DAOException {
		String mthdNm = "조회[CCoilJspFaEJB.getSelectData]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
			                    + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));
			
			commUtils.printLog(logId, mthdNm, "F+");

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, mthdNm, "F-");
			gdRet.setMessage(CConstant.RETN_CD_SUCCESS);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}		

	/**
	 * 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSelectData(JDTORecord recPara) throws DAOException {
		String mthdNm = "조회[CCoilJspFaEJB.getSelectData]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) 
			                    + "("   + commUtils.trim(recPara.getFieldString("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(recPara.getFieldString("jsp_page_func_nm"));
			
			commUtils.printLog(logId, mthdNm, "F+");

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecordSet jsRecordSet = (JDTORecordSet)ejbConn.trx("getSelectData", new Class[] { JDTORecord.class }, new Object[] { recPara });

			commUtils.printLog(logId, mthdNm, "F-");
			
			return jsRecordSet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}	

	/**
	 * IFTest Layout 변경 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updIfTestData(GridData gdReq) throws DAOException {
		String mthdNm = "IFTest Layout 변경[CCoilJspFaEJB.updIfTestData]";
		String logId  = commUtils.getLogId();
		
		try {

			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
			ejbConn.trx("updIfTestData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}	
	
	/**
	 * IFTest 전송 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData sndIfTest(GridData gdReq) throws DAOException {
		String mthdNm = "IFTest 전송[CCoilJspFaEJB.sndIfTest]";
		String logId  = commUtils.getLogId();
		
		try {

			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");		
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
			ejbConn.trx("sndIfTest", new Class[] { GridData.class }, new Object[] { gdReq });			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			 
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}	
	
	/**
	 * IFTest EAI전송
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData sndIfTestEAI(GridData gdReq) throws DAOException {
		String mthdNm = "IFTest EAI전송[CCoilJspFaEJB.sndIfTestEAI]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
			GridData gdRet = (GridData) ejbConn.trx("sndIfTestEAI", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}	

	/**
	 *  코일 야드 크레인 TO위치 재설정 
	 *  염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData updCarprg(GridData gdReq) throws DAOException {
		String mthdNm	= "차량동간이적 상차완료처리[CCoilJspFaEJB.updCarprg]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCarprg", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + "updCarprg rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			 	

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("차량동간이적 상차완료처리 됐습니다.");
			}
			commUtils.printLog(logId, mthdNm, "F-");			
			return gdRes;	
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/******
	 *      [A] 오퍼레이션명 : 크레인스케줄 기동 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData trxRunSchedule(GridData gdReq) throws DAOException {
		String mthdNm = "크레인스케줄기동[CCoilJspFaEJB.trxRunSchedule]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			JDTORecord [] jrInRecArr =  commUtils.genJDTORecordSet(gdReq);
			
			String ydSchCd		= "";
			String ydSchPrior	= "";
			String ydWbookId	= "";
			String rtnCd		= "";
			String rtnMsg		= "";	
			String sModifier	= commUtils.nvl(gdReq.getParam("YD_USER_ID"), "");
			String ydLocGp		= commUtils.nvl(gdReq.getParam("YD_LOC_GP"), "");
			
			EJBConnector ejbConn = null;
			
			CCoilDAO coilDao = new CCoilDAO();
    		String sAPP021_H_YN = coilDao.ApplyYn(logId, mthdNm, "APP021", "H", "*");
    		String sAPP021_J_YN = coilDao.ApplyYn(logId, mthdNm, "APP021", "J", "*");
    		
    		commUtils.printLog(logId, "========== 야드구분 ["+ ydLocGp +"]", "SL");
    		commUtils.printLog(logId, "==========[[[ APP021 작업예약 YDYDJ552호출 소재 : " + sAPP021_H_YN + " ]]]============", "SL");
    		commUtils.printLog(logId, "==========[[[ APP021 작업예약 YDYDJ552호출 제품 : " + sAPP021_J_YN + " ]]]============", "SL");
    		
    		if( "Y".equals(sAPP021_H_YN) && "H".equals(ydLocGp) ) {

    			commUtils.printLog(logId, "스케쥴 COUNT["+ gdReq.getHeader("CHECK").getRowCount() +"]", "SL");
    			// 다중스케쥴 기동
    			if( gdReq.getHeader("CHECK").getRowCount() > 1 ) {
    				ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
    				JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trxRunScheduleNew", new Class[] { GridData.class }, new Object[] { gdReq });
    				rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
    				rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
    				commUtils.printLog(logId, ">> rtnCd:["+ rtnCd +"] rtnMsg:["+ rtnMsg +"]", "SL");
    				
        			// ROLLBACK 시 전문 발생 안함
        			if (!"0".equals(rtnCd)) {

        				jrRtn.setResultCode(logId);
        				jrRtn.setResultMsg(mthdNm);

        				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
        				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
        			}
    			} else {
    				// 단일스케쥴 기동
					ydSchCd    = commUtils.trim(jrInRecArr[0].getFieldString("YD_SCH_CD"));
					ydSchPrior = commUtils.trim(jrInRecArr[0].getFieldString("YD_SCH_PRIOR"));
					ydWbookId  = commUtils.trim(jrInRecArr[0].getFieldString("YD_WBOOK_ID")); 
					
					JDTORecord jrInParam  = commUtils.getParam(logId, mthdNm, sModifier);
					jrInParam.setField("YD_SCH_CD"   , ydSchCd);
					jrInParam.setField("YD_SCH_PRIOR", ydSchPrior);
					jrInParam.setField("YD_WBOOK_ID" , ydWbookId); 
					jrInParam.setField("RUN_FLAG"    , "Y"      ); //작업예약 조회에서 스케줄 기동
					
					ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
					JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord.class }, new Object[] { jrInParam });
		
					rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
					rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
					
					commUtils.printLog(logId, "□ejbEnd□ RTN_CD:"+ rtnCd + "RTN_MSG : " + rtnMsg, "SL");
					
					//ROLLBACK 시 전문 발생
					if (!"0".equals(rtnCd)) {
						
						jrRtn.setResultCode(logId);
						jrRtn.setResultMsg(mthdNm);
		
						EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
						sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
					}
    			}
    		} else if( "Y".equals(sAPP021_J_YN) && "J".equals(ydLocGp) ) {
    			
				ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
				JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trxRunScheduleNew", new Class[] { GridData.class }, new Object[] { gdReq });
				rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
				rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
				commUtils.printLog(logId, ">> rtnCd:["+ rtnCd +"] rtnMsg:["+ rtnMsg +"]", "SL");
				
    			// ROLLBACK 시 전문 발생 안함
    			if (!"0".equals(rtnCd)) {

    				jrRtn.setResultCode(logId);
    				jrRtn.setResultMsg(mthdNm);

    				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
    				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
    			}
    		} else {
    		
				for(int ii=0; ii< jrInRecArr.length; ii++){
					
					ydSchCd    = commUtils.trim(jrInRecArr[ii].getFieldString("YD_SCH_CD"));
					ydSchPrior = commUtils.trim(jrInRecArr[ii].getFieldString("YD_SCH_PRIOR"));
					ydWbookId  = commUtils.trim(jrInRecArr[ii].getFieldString("YD_WBOOK_ID")); 
					
					JDTORecord jrInParam  = commUtils.getParam(logId, mthdNm, sModifier);
					jrInParam.setField("YD_SCH_CD"   , ydSchCd);
					jrInParam.setField("YD_SCH_PRIOR", ydSchPrior);
					jrInParam.setField("YD_WBOOK_ID" , ydWbookId); 
					jrInParam.setField("RUN_FLAG"    , "Y"      ); //작업예약 조회에서 스케줄 기동
					
					ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
					JDTORecord jrRtn = (JDTORecord)ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord.class }, new Object[] { jrInParam });
		
					rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
					rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
					
					commUtils.printLog(logId, "□ejbEnd□ RTN_CD:"+ rtnCd + "RTN_MSG : " + rtnMsg, "SL");
					
					//ROLLBACK 시 전문 발생
					if (!"0".equals(rtnCd)) {
						
						jrRtn.setResultCode(logId);
						jrRtn.setResultMsg(mthdNm);
		
						EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
						sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
					}
				}
    		}
			
    		commUtils.printLog(logId, "□END□ rtnCd:"+ rtnCd + "rtnMsg : " + rtnMsg, "SL");
    		
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				if ("".equals(rtnMsg)) {
					gdRet.setMessage("크레인스케줄기동 처리가 완료 됐습니다.");	
				} else {
					gdRet.setMessage(rtnMsg);	
				}
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}
	
	/**
	 *      [A] 오퍼레이션명 : 작업예약 삭제 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData delWrkBook(GridData gdReq) throws DAOException {
		String mthdNm = "작업예약삭제[CCoilJspFaEJB.delWrkBook]";
		String logId  = commUtils.getLogId();

		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
            EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("delWrkBook", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");			
			
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}

			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("정상적으로 작업예약이 삭제 되었습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}		


	/**
	 * 야드적치열정보 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */ 
	public GridData updCoilYdColStsSet(GridData gdReq) throws DAOException {
		String mthdNm = "야드적치열정보 수정[CCoilJspFaEJB.updCoilYdColStsSet]";
		String logId  = commUtils.getLogId();

		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCoilYdColStsSet", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			if (!"1".equals(rtnCd)) {		
				m_ctx.setRollbackOnly();
			} 	

			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			gdRes.setMessage(rtnMsg);
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}		

	} 

	/**
	 * 야드적치열정보 수정
	 * @작성자 : 염용선
	 * @작성일 : 2019.07.30
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	
	public GridData updCoilYdColStsSetInfo(GridData gdReq) throws DAOException {
		String mthdNm = "야드적치열정보 수정[CCoilJspFaEJB.updCoilYdColStsSetInfo]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			//JDTORecord [] inRecord = commUtils.genJDTORecordSet(gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
			JDTORecord jrRtn = (JDTORecord) ejbConn.trx("updCoilYdColStsSetInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			
			if (!"1".equals(rtnCd)) {		
				m_ctx.setRollbackOnly();
			} 	

			gdRes.setMessage(rtnMsg);
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} 

	/**
	 *  코일 야드 베드금지 / 해제 
	 *  염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdBedBanCnc(GridData gdReq) throws DAOException {
		String mthdNm = "코일 야드 베드금지/해제[CCoilJspFaEJB.updCoilYdBedBanCnc]";
		String logId  = commUtils.getLogId();		
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord) ejbConn.trx("updCoilYdBedBanCnc", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			if (!"1".equals(rtnCd)) {		
				m_ctx.setRollbackOnly();
			} 	

			gdRes.setMessage(rtnMsg);
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 *      재공율 조정작업
	 *      염용선
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData updYdNextprocList2Pop(GridData gdReq) throws DAOException {
		String mthdNm = "재공율 조정작업[CCoilJspFaEJB.updYdNextprocList2Pop]";
		String logId  = commUtils.getLogId();		
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord) ejbConn.trx("updYdNextprocList2Pop", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			if (!"1".equals(rtnCd)) {		
				m_ctx.setRollbackOnly();
			} 	

			gdRes.setMessage(rtnMsg);
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 *      HOT COIL이용한 결로방지 시스템
	 *      염용선
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData procHotcoilAuto(GridData gdReq) throws DAOException {
		String mthdNm = "결로재보급[CCoilJspFaEJB.procHotcoilAuto]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord) ejbConn.trx("procHotcoilAuto", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("결로재보급 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		    
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 *      HOT COIL이용한 결로방지 시스템
	 *      염용선
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData procHotcoilchklist(GridData gdReq) throws DAOException {
		String mthdNm = "결로재추출[CCoilJspFaEJB.procHotcoilchklist]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord) ejbConn.trx("procHotcoilchklist", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("결로재추출 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		    
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 * Line-Off 분기 Conv
	 * L3 interface
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updDivConvLineOff(GridData gdReq) throws DAOException {
		String mthdNm	= "수입백업처리[CCoilJspFaEJB.updDivConvLineOff]";
		String logId	= commUtils.getLogId();

		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");		
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);	
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updDivConvLineOff", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd  = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			if (!"1".equals(rtnCd)) {		
				m_ctx.setRollbackOnly();
				gdRes.setMessage(rtnMsg);
			}else{
				gdRes.setMessage("수입백업처리가 완료 됐습니다.");
			}

			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	} 


		
	/**
	 * 코일제품야드 tracking 팝업 조회보급등록[추출 등록]
	 * SPM/HFL입측관리 > 추출
	 * 염용선 2019.08.14
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData inscoilGdsYdLineWrPp(GridData gdReq) throws DAOException {	
		String mthdNm	= "추출 등록[CCoilJspFaEJB.inscoilGdsYdLineWrPp]";
		String logId	= commUtils.getLogId();		
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");	
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			String sModifier = commUtils.trim(gdReq.getParam("YD_USER_ID"));
			
			String jmsTcCdMthNm = ""; // 호출할 EJB 메소드[전문처리 인터페이스]
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			JDTORecord jrRec = CmUtil.genJDTORecord(gdReq);			
			//			 * TREAT_GP	처리구분	C	1	Y	1:보급, 2:보급취소, 3:추출, 4:Take-Out, 5:Take-In				
			//			 * STL_NO	재료번호	C	11	Y					
			//           * EQP_GP	설비구분	C	6		보급, Take-In 요구시 Coil 위치					
			
			JDTORecord jrRtn   = commUtils.getParam(logId, mthdNm, sModifier);
			JDTORecord jrInRec = commUtils.getParam(logId, mthdNm, sModifier);
			
			String ydBay    = jrRec.getFieldString("PARA_YD_EQP_ID1").substring(1, 2);
			String ydEqp    = "J"+jrRec.getFieldString("PARA_YD_EQP_ID1").substring(1)+jrRec.getFieldString("PARA_YD_STK_BED_NO");
			String sTreatGp = jrRec.getFieldString("TREAT_GP");
			
			jrInRec.setField("TREAT_GP"		    , sTreatGp); 
			if(("D".equals(ydBay))||("F".equals(ydBay))){
				jmsTcCdMthNm = "rcvHRYDJ009";   //HR열연정정Line-Off요구
				jrInRec.setField("JMS_TC_CD"		, "HRYDJ009"); 
				jrInRec.setField("TREAT_GP"			, "3"); 
            //C증설					
			} else if("A".equals(ydBay)){
				
				jmsTcCdMthNm = "rcvH2YDL073"; //SPM5 출측Line-Off요구
				jrInRec.setField("JMS_TC_CD"	, "H2YDL073");
				
			} else if("B".equals(ydBay)){
				if("K".equals(ydEqp.substring(2,3))){
					jmsTcCdMthNm = "rcvH2YDL043"; //SPM4 출측Line-Off요구
					jrInRec.setField("JMS_TC_CD"	, "H2YDL043");
				} else{	 
					jmsTcCdMthNm = "rcvHRYDJ009";   //HR열연정정Line-Off요구
					jrInRec.setField("JMS_TC_CD"	, "HRYDJ009"); //HFL#5
					jrInRec.setField("TREAT_GP"			, "3"); 
				}
			} else if("C".equals(ydBay)){
				if("K".equals(ydEqp.substring(2,3))){
					jmsTcCdMthNm = "rcvH2YDL033"; //SPM3 출측Line-Off요구
					jrInRec.setField("JMS_TC_CD"	, "H2YDL033");
				} else{	
					jmsTcCdMthNm = "rcvH2YDL053"; //HFL4 출측Line-Off요구
					jrInRec.setField("JMS_TC_CD"	, "H2YDL053");
				}
				
					
				
			} else if("E".equals(ydBay)){ 
				jmsTcCdMthNm = "rcvH2YDL023"; //SPM2 출측Line-Off요구
				jrInRec.setField("JMS_TC_CD"		, "H2YDL023"); 
			} else if("G".equals(ydBay)){ 
				jmsTcCdMthNm = "rcvH2YDL013"; //HFL1 출측Line-Off요구
				jrInRec.setField("JMS_TC_CD"		, "H2YDL013"); 
			} else if("H".equals(ydBay)){
				jmsTcCdMthNm = "rcvH2YDL003"; //SPM1 출측 Line-Off요구
				jrInRec.setField("JMS_TC_CD"		, "H2YDL003"); 
			}
			jrInRec.setField("STL_NO"		    , jrRec.getFieldString("PARA_STL_NO")); 
			jrInRec.setField("YD_EQP_ID"	  	, jrRec.getFieldString("PARA_YD_EQP_ID1")); 
			jrInRec.setField("YD_STK_BED_NO"	, jrRec.getFieldString("PARA_YD_STK_BED_NO")); 
			jrInRec.setField("EQP_GP"			, (String)CCommUtils.h_hstEqpGpMatch.get(ydEqp)); 
			
			commUtils.printLog(logId, mthdNm +"jmsTcCdMthNm : "+jmsTcCdMthNm+ "/// STL_NO:"+ jrRec.getFieldString("PARA_STL_NO"), "SL");
			commUtils.printLog(logId, ydBay, "ydBay===========");
			commUtils.printLog(logId, ydEqp, "ydEqp===========");
			commUtils.printLog(logId, (String)CCommUtils.h_hstEqpGpMatch.get(ydEqp), "EQP_GP===========");
			commUtils.printLog(logId, jrRec.getFieldString("PARA_YD_EQP_ID1"), "YD_EQP_ID===========");
			
			if(!("".equals(jmsTcCdMthNm))){
				EJBConnector ejbConn = new EJBConnector("default", "CCoilL3RcvSeEJB", this);
				jrRtn = (JDTORecord)ejbConn.trx( jmsTcCdMthNm, new Class[] { JDTORecord.class }, new Object[] { jrInRec }); //procCCoilShearOutLineOffReq

				String rtnCd	= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
				String rtnMsg	= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
				commUtils.printLog(logId, " rtnMsg11:"+ rtnMsg, "FL");
				commUtils.printLog(logId, " rtnCd:"+ rtnCd, "FL");
				//ROLLBACK 시 전문 발생
				if (!"0".equals(rtnCd)) {
					
					jrRtn.setResultCode(logId);
					jrRtn.setResultMsg(mthdNm);

					EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

				}
				if (!"1".equals(rtnCd)) {
					gdRes.setMessage(rtnMsg);
					m_ctx.setRollbackOnly();
				}else{
					//처리구분 1:보급, 2:보급취소, 3:추출, 4:Take-Out, 5:Take-In
					String Msg = "";
					if("1".equals(sTreatGp)){
						Msg = "보급";
					}else if("2".equals(sTreatGp)){
						Msg = "보급취소";
					}else if("3".equals(sTreatGp)){
						Msg = "추출";
					}else if("4".equals(sTreatGp)){
						Msg = "Take-Out";
					}else if("5".equals(sTreatGp)){
						Msg = "Take-In";
					}
					gdRes.setMessage(Msg+" 처리가 완료 됐습니다.");
				}
				commUtils.printLog(logId, " rtnMsg22:"+ rtnMsg, "FL");
				
			}else{
				gdRes.setMessage("처리할 메소드 명이 없습니다 선택 동을 확인 하세요.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}

	}

	/**
	 * 코일소재야드 tracking 팝업 조회보급등록
	 * * SPM/HFL입측관리 > 보급 > 등록
	 * YYS 2019.08.14
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData inscoilYdLineWrPp(GridData gdReq) throws DAOException {
		String mthdNm	= "소재입측 보급[CCoilJspFaEJB.inscoilYdLineWrPp]";
		String logId	= commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			String jmsTcCdMthNm = ""; // 호출할 EJB 메소드[전문처리 인터페이스]

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID

			JDTORecord [] jrInRecArr =  commUtils.genJDTORecordSet(gdReq);			
//			 * TREAT_GP	처리구분	C	1	Y	1:보급, 2:보급취소, 3:추출, 4:Take-Out, 5:Take-In				
//			 * STL_NO	재료번호	C	11	Y					
//           * EQP_GP	설비구분	C	6		보급, Take-In 요구시 Coil 위치
			String sModifier = commUtils.trim(gdReq.getParam("YD_USER_ID"));
			JDTORecord jrInRec 	= commUtils.getParam(logId, mthdNm, sModifier);
			String ydEqpId      = "";
			
			for(int i=0; i < jrInRecArr.length; i++){
				jrInRec.setField("TREAT_GP"		, "1"); 
				ydEqpId = jrInRecArr[i].getFieldString("PARA_YD_EQP_ID");
				
				//결속대 BACKUP인 경우 
				if ("JFFE02".equals(ydEqpId) || "JDFE03".equals(ydEqpId)|| "JBFE05".equals(ydEqpId)){						
					jmsTcCdMthNm = "rcvHRYDJ008";  // 2열연 정정입측 보급Lot 편성 백업
					jrInRec.setField("JMS_TC_CD"		, "HRYDJ008"); 
					if ("JFFE02".equals(ydEqpId)){
						jrInRec.setField("WORD_PROC"		, "FH"); 
					} else if ("JDFE03".equals(ydEqpId)){
						jrInRec.setField("WORD_PROC"		, "DH");
					} else if ("JBFE05".equals(ydEqpId)){
						jrInRec.setField("WORD_PROC"		, "BH");
					}					
				}else{
					jmsTcCdMthNm = "rcvH2YDL001";  // SPM1 입측 Line-In요구
					jrInRec.setField("JMS_TC_CD"		, "H2YDL001"); 
				}
				jrInRec.setField("STL_NO"			, jrInRecArr[i].getFieldString("COIL_NO")); 
				jrInRec.setField("YD_EQP_ID"		, jrInRecArr[i].getFieldString("PARA_YD_EQP_ID")); 
				jrInRec.setResultCode(logId);	//Logging 을 위한 ID
				jrInRec.setResultMsg(mthdNm);	//상위 Method 명
				commUtils.printLog(logId,  " YD_EQP_ID:"+ jrInRecArr[i].getFieldString("PARA_YD_EQP_ID"), "FL");
				commUtils.printLog(logId,  " STL_NO:"+ jrInRecArr[i].getFieldString("COIL_NO"), "FL");
				commUtils.printLog(logId, mthdNm + " LOC:"+ jrInRecArr[i].getFieldString("LOC"), "FL");
				commUtils.printLog(logId, "CCoilL3RcvSeEJB--"+jmsTcCdMthNm , "FL");
				EJBConnector ejbConn = new EJBConnector("default", "CCoilL3RcvSeEJB", this);
				JDTORecord jrRtn = (JDTORecord)ejbConn.trx(jmsTcCdMthNm, new Class[] { JDTORecord.class }, new Object[] { jrInRec });
				String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
				String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
				commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
				//ROLLBACK 시 전문 발생
				if (!"0".equals(rtnCd)) {
					
					jrRtn.setResultCode(logId);
					jrRtn.setResultMsg(mthdNm);

					EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

				}
				if (!"1".equals(rtnCd)) {
					gdRes.setMessage(rtnMsg);
					m_ctx.setRollbackOnly();
				}else{
					gdRes.setMessage("소재입측 보급 처리 완료 됐습니다.");
				}

				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
				
					
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}  


	/**
	 * 코일소재야드 tracking 팝업 조회보급취소등록
	 * SPM/HFL입측관리 > 보급취소 > 등록
	 * YYS 2019.08.14
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updcoilYdLineWrCancelPp(GridData gdReq) throws DAOException {
		String mthdNm = " 조회보급취소등록[CCoilJspFaEJB.updcoilYdLineWrCancelPp]";
		String logId  = commUtils.getLogId();

		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updcoilYdLineWrCancel", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("보급취소 등록 완료 했습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
						
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}  
	

	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 위치검색순서관리   적치구분 콤보리스트 조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : YYS
	 * @작성일 : 2019.07.07
	 */
	public GridData getYDB700ComboList(GridData gdReq) throws JDTOException {
		String mthdNm = "적치구분 콤보리스트 조회[CCoilJspFaEJB.getYDB700ComboList]";
		String logId  = commUtils.getLogId();

		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			GridData gdRes = (GridData)ejbConn.trx("getYDB700ComboList", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} 

	/**
	 * 공통 코드 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getComboCodeList(GridData gdReq) throws JDTOException {
		String mthdNm = "공통 코드 조회[CCoilJspFaEJB.getComboCodeList]";
		String logId  = commUtils.getLogId();		

		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			JDTORecord jrInRec = CmUtil.genJDTORecord(gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecordSet jsRset = (JDTORecordSet)ejbConn.trx("getComboCodeList", new Class[] { JDTORecord.class }, new Object[] { jrInRec });
			
			GridData gdRes = CmUtil.genGridData(gdReq , jsRset);

			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 반납반송요청
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdRetCrnReg(GridData gdReq) throws DAOException {
		String mthdNm = "반납반송요청[CCoilJspFaEJB.updCoilYdRetCrnReg]";
		String logId  = commUtils.getLogId();

		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCoilYdRetCrnReg", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("반납요청 처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}  

	/**
	 * 반납대상 긴급재 지정
	 * YYS 2019-08-27
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdemergencyMgt(GridData gdReq) throws DAOException {
		String mthdNm = "반납대상 긴급재 지정[CCoilJspFaEJB.updCoilYdemergencyMgt]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");			

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCoilYdemergencyMgt", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("긴급재 지정 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}  

	/**
	 * 코일소재야드 tracking 팝업 TakeIn 등록
	 * YYS 2019-08-28
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updcoilYdLineWrTakeInPp(GridData gdReq) throws DAOException {
		String mthdNm = "소재야드 TakeIn 등록[CCoilJspFaEJB.updcoilYdLineWrTakeInPp]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			String sModifier	= commUtils.trim(gdReq.getParam("YD_USER_ID"));
			String sTreatGp     = commUtils.trim(gdReq.getParam("TREAT_GP"));
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			JDTORecord [] jrInRecArr =  commUtils.genJDTORecordSet(gdReq);
			
			EJBConnector ejbConn	= null;
			String sStlNo 			= "";
			String ydEqpId  		= "";
			String ydBedNo 			= "";			
			String sWordProc        = "";
			JDTORecord jrInRec		= JDTORecordFactory.getInstance().create();
			
			/*
			 * 
			 * String sTreatGp   = commUtils.trim(rcvMsg.getFieldString("TREAT_GP" ));	//처리구분 1:보급, 2:보급취소, 3:추출, 4:Take-Out, 5:Take-In
			String sWordProc  = commUtils.trim(rcvMsg.getFieldString("WORD_PROC"));	//작업지시공정
			
			//H2YDL001, H2YDL004 ..
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    ));	//설비ID
			String ydBayGp    = ydEqpId.substring(1, 2);
			String sTmpEqp    = ydEqpId.substring(2, 3);
			
			String ydStkBedNo = commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO"));	//야드적치Bed번호
			String sStlNo     = commUtils.trim(rcvMsg.getFieldString("STL_NO"       )); 
			*/
			for(int i=0; i< jrInRecArr.length; i++){

				sStlNo 	= commUtils.trim(jrInRecArr[i].getFieldString("COIL_NO"));
				ydEqpId = commUtils.trim(jrInRecArr[i].getFieldString("PARA_YD_EQP_ID"));
				ydBedNo = commUtils.trim(jrInRecArr[i].getFieldString("YD_STK_BED_NO"));
				
				sWordProc  = commUtils.trim(jrInRecArr[i].getFieldString("WORD_PROC"));	//작업지시공정
				commUtils.printLog(logId, sTreatGp, "SL=====sTreatGp");
				commUtils.printLog(logId, sWordProc, "SL=====sWordProc");
				commUtils.printLog(logId, sStlNo, "SL=====sStlNo");
				commUtils.printLog(logId, ydEqpId, "SL=====ydEqpId");
				commUtils.printLog(logId, ydBedNo, "SL=====ydBedNo");
				commUtils.printLog(logId, sModifier+"> MSG_ID : H2YDL004", "SL=====sModifier");
				jrInRec = commUtils.getParam(logId, mthdNm, sModifier);
				jrInRec.setField("MSG_ID"		, "H2YDL004"); 							//열연조업 take_in  전문코드
				jrInRec.setField("STL_NO"		, sStlNo);								//재료번호
				jrInRec.setField("YD_EQP_ID"	, ydEqpId); 
				jrInRec.setField("YD_STK_BED_NO", ydBedNo);	
				jrInRec.setField("TREAT_GP"	, sTreatGp); 
				jrInRec.setField("WORD_PROC", sWordProc);
//				크레인 및 작업 예약 확인
				ejbConn = new EJBConnector("default", "CCoilL3RcvSeEJB", this);
				JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procCCoilShearInSupLotComp", new Class[] { JDTORecord.class }, new Object[] { jrInRec });
				String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
				String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
				commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
				//ROLLBACK 시 전문 발생
				if (!"0".equals(rtnCd)) {
					
					jrRtn.setResultCode(logId);
					jrRtn.setResultMsg(mthdNm);

					EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

				}
				if (!"1".equals(rtnCd)) {
					gdRes.setMessage(rtnMsg);
					m_ctx.setRollbackOnly();
				}else{
					gdRes.setMessage("Take-In 처리가 완료 됐습니다.");
				}
				

			}

			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}

	}  

	
	/**
	 * 코일소재야드 tracking 팝업 조회보급취소등록
	 * SPM/HFL입측관리 > 보급취소 > 등록
	 * YYS 2019.08.14
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updcoilYdLineWrTakeOutPp(GridData gdReq) throws DAOException {
		String mthdNm = "TakeOut 등록[CCoilJspFaEJB.updcoilYdLineWrTakeOutPp]";
		String logId  = commUtils.getLogId();
	
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
	
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updcoilYdLineWrTakeOutPp", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);
	
				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
	
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("Take-Out 처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}  

	/**
	 *  야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 입고대차 지정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws JDTOException
	 * @작성자 : 염용선
	 * @작성일 : 2019.09.15 
	 */
	public GridData updCoilYdTcarStsSetRcpt(GridData gdReq) throws JDTOException {
		String mthdNm = "입고대차 설정[CCoilJspFaEJB.updCoilYdTcarStsSetRcpt]";
		String logId  = commUtils.getLogId();

		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+"); 

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
			JDTORecord jrRtn	= (JDTORecord)ejbConn.trx("updCoilYdTcarStsSetRcpt",new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			

			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("입고대차 백업 처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		}catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}
	
	
	/**
	 *  야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 결로재 지정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws JDTOException
	 * @작성자 :  염용선
	 * @작성일 : 2019.09.15 
	 */
	public GridData updCoilYdTcarStsSetCond(GridData gdReq) throws JDTOException {
		String mthdNm = "결로재지정[CCoilJspFaEJB.updCoilYdTcarStsSetCond]";
		String logId  = commUtils.getLogId();

		try{
            mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
            commUtils.printLog(logId, mthdNm, "F+");
            
            gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCoilYdTcarStsSetCond", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			

			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("결로재 처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		}catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}

	/**
	 *  공대차 스케줄 호출 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 * 염용선
	 * @작성일 : 2019.09.15 
	 */
	
	public GridData procTcarStsSetTcarA(GridData gdReq) throws JDTOException {
		String mthdNm = "공대차 스케줄 호출[CCoilJspFaEJB.procTcarStsSetTcarA]";
		String logId  = commUtils.getLogId();

		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+"); 

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);			
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procTcarStsSetTcarA", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("공대차이동지시 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		}catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}

	}  

	/**
	 *  출발 실적
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData procTcarStsSetTcarB(GridData gdReq) throws JDTOException {
		String mthdNm = "대차스케줄관리--출발 실적[CCoilJspFaEJB.procTcarStsSetTcarB]"; 
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+"); 

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procTcarStsSetTcarB", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd  = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("출발실적 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		}catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}

	} 

	/**
	 *  도착실적
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData procTcarStsSetTcarC(GridData gdReq) throws JDTOException {
		String mthdNm = "대차스케줄관리--대차 도착 실적[CCoilJspFaEJB.procTcarStsSetTcarC]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+"); 

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);			
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procTcarStsSetTcarC", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("도착 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		}catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}  

	/**
	 *  완료 실적
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData procTcarStsSetTcarD(GridData gdReq) throws JDTOException {
		String mthdNm = "대차스케줄관리--대차 완료실적[CCoilJspFaEJB.procTcarStsSetTcarD]";
		String logId  = commUtils.getLogId();

		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procTcarStsSetTcarD", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("실적 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		}catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}  

	/**
	 *  현재동 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData procTcarStsSetTcarE(GridData gdReq) throws JDTOException {
		String mthdNm = "대차스케줄관리--현위치 변경[CCoilJspFaEJB.procTcarStsSetTcarE]";
		String logId  = commUtils.getLogId();

		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+"); 
			
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procTcarStsSetTcarE", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("현위치변경 처리가 완료 됐습니다.");	
			}		
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		}catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}  

	/**
	 * 대차스케줄관리--HOME 동 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData procTcarStsSetTcarF(GridData gdReq) throws JDTOException {
		String mthdNm = "대차스케줄관리--HOME 동 변경[CCoilJspFaEJB.procTcarStsSetTcarF]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procTcarStsSetTcarF", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("HOME 동 변경 처리가 완료 됐습니다.");	
			}		
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		}catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}

	}  	

	/**
	 *  야드관리 > 코일소재야드 > 재공관리 > 열단위이적등록[제품] (이적지시)
	 *  YYS 2019.09.06 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updColUnitMvstkReg(GridData gdReq) throws DAOException {
		String mthdNm	= "열단위이적등록/스판단위이적등록[CCoilJspFaEJB.updColUnitMvstkReg]";
		String logId	= commUtils.getLogId();	
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updColUnitMvstkReg", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("열단위 이적등록 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *  야드관리 > 2열연 제품 코일야드[신] > 저장관리  > 열단위이적등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updColUnitMvstkRegJ(GridData gdReq) throws DAOException {
		String mthdNm	= "열단위이적등록[제품][CCoilJspFaEJB.updColUnitMvstkRegJ]";
		String logId	= commUtils.getLogId();	
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updColUnitMvstkRegJ", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("열단위 이적등록 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	

	/**
	 *  야드관리 > 코일소재야드 > 재공관리 > 열단위이적등록[제품] (이적지시)
	 *  YYS 2019.09.06 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updColUnitMvstkRegH(GridData gdReq) throws DAOException {
		String mthdNm	= "열단위이적등록[소재][CCoilJspFaEJB.updColUnitMvstkRegH]";
		String logId	= commUtils.getLogId();	
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updColUnitMvstkRegH", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("열단위 이적등록 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	

	
	/**
	 *  야드크레인 작업관리 (스케줄 취소)
	 *  염용선 2019.09.11
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCraneSchCancel(GridData gdReq)throws DAOException {	
		String mthdNm = "스케줄 취소[CCoilJspFaEJB.updCraneSchCancel]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			//JDTORecord jrRtn = (JDTORecord)ejbConn.trx("cancelSchCoilYdCrnWorkMgt", new Class[] { GridData.class }, new Object[] { gdReq });
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCraneSchCancel", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("스케줄취소 처리가 완료 됐습니다.");
			} 	

			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} 
	
	/**
	 * 지포장 보급 등록 
	 * 강정선 2019.09.18
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCoilYdSendGF(GridData gdReq) throws DAOException {
		String mthdNm = "지포장 등록[CCoilJspFaEJB.updCoilYdSendGF]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCoilYdSendGF", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			 	

			GridData gdRes 		 = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("지포장보급 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");

			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 지포장 긴급재 보급 요청 
	 * 강정선 2019.09.18
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCoilYdSendGFEmergency(GridData gdReq) throws DAOException {
		String mthdNm = "지포장 긴급재 보급 요청[CCoilJspFaEJB.updCoilYdSendGFEmergency]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn 	 	= (JDTORecord)ejbConn.trx("updCoilYdSendGFEmergency", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd			= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg			= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
				

			GridData gdRes 	= OperateGridData.cloneResponseGridData(gdReq);
			
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("지포장 긴급재 보급 처리가 완료 됐습니다.");
			} 
			
			commUtils.printLog(logId, mthdNm, "F-");

			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 지포장 입고 등록 
	 * 강정선 2019.09.18
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCoilYdSendGF2(GridData gdReq) throws DAOException {
		String mthdNm = "지포장 입고 등록[CCoilJspFaEJB.updCoilYdSendGF2]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn		= (JDTORecord)ejbConn.trx("updCoilYdSendGF2", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd			= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg			= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			
			GridData gdRes 		 = OperateGridData.cloneResponseGridData(gdReq);
			
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("지포장 입고 처리가 완료 됐습니다.");
			} 	

			
			commUtils.printLog(logId, mthdNm, "F-");

			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 지포장 삭제 
	 * 강정선 2019.09.18
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCoilGdsYdReSendGFDel(GridData gdReq) throws DAOException {
		String mthdNm = "지포장 삭제[CCoilJspFaEJB.updCoilGdsYdReSendGFDel]";
		String logId  = commUtils.getLogId();
		
		try {
			
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCoilGdsYdReSendGFDel", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			
			GridData gdRes 		 = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("지포장 삭제 처리가 완료 됐습니다.");
			} 
			
			commUtils.printLog(logId, mthdNm, "F-");

			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/***
	 * 소재코일야드 메뉴얼 작업지시 편성-일품단위 이적등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updMtlUnitMvstkReg(GridData gdReq)throws DAOException {
		String mthdNm = "일품단위 이적등록[CCoilJspFaEJB.updMtlUnitMvstkReg]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			String gubun = gdReq.getParam("YD_LOC_GP");


			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			JDTORecord jrRtn  = commUtils.getParam(logId, mthdNm, "");
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			if ("H".equals(gubun)) {
				jrRtn = (JDTORecord)ejbConn.trx("updMtlUnitMvstkRegH", new Class[] { GridData.class }, new Object[] { gdReq });
				
			}else{
				jrRtn = (JDTORecord)ejbConn.trx("updMtlUnitMvstkRegJ", new Class[] { GridData.class }, new Object[] { gdReq });
			}
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("일품단위 이적등록 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} 
	
	/***
	 * 야드관리 > 2열연 제품 코일야드[신] > 저장관리  > 제품단위이적등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updMtlUnitMvstkRegJ(GridData gdReq)throws DAOException {
		String mthdNm = "일품단위 이적등록[CCoilJspFaEJB.updMtlUnitMvstkRegJ]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
//			String gubun = gdReq.getParam("YD_LOC_GP");


			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			JDTORecord jrRtn  = commUtils.getParam(logId, mthdNm, "");
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn.trx("updMtlUnitMvstkRegJ", new Class[] { GridData.class }, new Object[] { gdReq }); //updMtlUnitMvstkRegJ
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("일품단위 이적등록 처리가 완료 됐습니다.");	
			}		
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} 
	
	/***
	 * 소재코일야드 메뉴얼 작업지시 편성-일품단위 이적등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updMtlUnitMvstkRegH(GridData gdReq)throws DAOException {
		String mthdNm = "일품단위 이적등록[CCoilJspFaEJB.updMtlUnitMvstkRegH]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
//			String gubun = gdReq.getParam("YD_LOC_GP");


			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			JDTORecord jrRtn  = commUtils.getParam(logId, mthdNm, "");
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn.trx("updMtlUnitMvstkRegH", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}			

			GridData gdRes 		 = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("일품단위 이적등록 처리가 완료 됐습니다.");	
			}
				
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} 
	
	/**
	 *  코일 야드 크레인 상태 수정(UPDATE) 
	 *  염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdCrnStsSetCrnStat(GridData gdReq) throws DAOException {
		String mthdNm = "코일 야드 크레인 상태 수정(UPDATE)[CCoilJspFaEJB.updCoilYdCrnStsSetCrnStat]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);	
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCoilYdCrnStsSetCrnStat", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);	
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("크레인 상태 변경 처리가 완료 됐습니다.");
			}
				
			
			commUtils.printLog(logId, mthdNm, "F-");	
			return gdRes;	
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}
	
	/**
	 *  코일 야드 크레인 운전모드 수정(UPDATE) 
	 *  염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdCrnStsSetCrnMode(GridData gdReq) throws DAOException {
		String mthdNm = "코일 야드 크레인 상태 수정(UPDATE)[CCoilJspFaEJB.updCoilYdCrnStsSetCrnMode]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);	
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCoilYdCrnStsSetCrnMode", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("크레인 상태 변경 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");	
	
			return gdRes;	
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}

	/**
	 * 크레인상태관리 - 명령선택기동
	 * 염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */

	public GridData updCmdSelStart(GridData gdReq) throws JDTOException {	
		String mthdNm = "크레인상태관리 - 명령선택기동[CCoilJspFaEJB.updCmdSelStart]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, mthdNm, "F+");

			
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID		
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);	
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCmdSelStart", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setStatus(rtnCd);
				gdRes.setMessage("명령선택기동 처리가 완료 됐습니다.");
			}
			
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}		

	} 

	/**
	 * 크레인 작업 구분 지정
	 *  염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */

	public GridData crnWrkGPartSet(GridData gdReq) throws JDTOException {	
		String mthdNm = "크레인 작업 구분 지정[CCoilJspFaEJB.crnWrkGPartSet]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, mthdNm, "F+");
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);	
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("crnWrkGPartSet", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			if (!"1".equals(rtnCd)) {		
				m_ctx.setRollbackOnly();
			} 	

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			if(rtnCd =="1"){
				rtnMsg = "SUCCESS";
			}else{
				rtnMsg = "FAILURE";
			}
			gdRes.setStatus(rtnCd);
			gdRes.setMessage(rtnMsg);
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}	
	} 

	/**
	 *  코일 야드 크레인 작업모드 수정(UPDATE) 
	 * 염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdCrnStsSetCrnMode2(GridData gdReq) throws DAOException {
		String mthdNm	= "코일 야드 크레인 작업모드 수정(UPDATE)[CCoilJspFaEJB.updCoilYdCrnStsSetCrnMode2]";
		String logId	= commUtils.getLogId();	
		
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);	
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCoilYdCrnStsSetCrnMode2", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("작업모드 수정 처리가 완료 됐습니다.");
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;	
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}
	
	/**
	 *  야드크레인 작업관리 POP_UP (권상실적 처리)
	 * 염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */

	public GridData updCrnUpPrsBackUp(GridData gdReq) throws JDTOException {
		String mthdNm = "야드크레인 작업관리 POP_UP (권상실적 처리)[CCoilJspFaEJB.updCrnUpPrsBackUp]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			//JDTORecord [] jrParam = ydComUtil.genJDTORecordSet(gdReq);		
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCrnUpPrsBackUp", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnMsg:"+ rtnMsg, "SL");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			if (!"1".equals(rtnCd)) {		
				m_ctx.setRollbackOnly();
			} 	

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);		
						
			gdRes.setStatus(rtnCd);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("권상실적 처리가 완료 됐습니다.");
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 야드크레인 작업관리 POP_UP (권하실적 처리)
	 * 염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	
	public GridData updCrnDnPrsBackUp(GridData gdReq) throws DAOException {
		String mthdNm	= "야드크레인 작업관리 POP_UP (권하실적 처리)[CCoilJspFaEJB.updCrnDnPrsBackUp]";
		String logId  = commUtils.getLogId();	

		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

            EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
            JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCrnDnPrsBackUp", new Class[] { GridData.class }, new Object[] { gdReq });
            String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
            String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
            commUtils.printLog(logId, mthdNm + " rtnMsg:"+ rtnMsg, "SL");
            commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			if (!"1".equals(rtnCd)) {		
				m_ctx.setRollbackOnly();
			} 	

			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("권하실적 처리가 완료 됐습니다.");
			}
			gdRes.setStatus(rtnCd);
			
			commUtils.printLog(logId, mthdNm, "F-");
			
			return gdRes;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *  야드크레인 작업관리 POP_UP (권상/권하 처리)
	 * 염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */

	public GridData updCrnUpDnPrsBackUp(GridData gdReq) throws JDTOException {
		String mthdNm	= "야드크레인 작업관리 POP_UP (권상/권하 처리)[CCoilJspFaEJB.updCrnUpDnPrsBackUp]";
		String logId	= commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");	
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			
			/***********************
			 * 권상실적 처리				
			 ***********************/
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCrnUpPrsBackUp", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			if (!"1".equals(rtnCd)) {		
				m_ctx.setRollbackOnly();
			}
			
			/*****************************************************
			 * 권하실적 처리
			 *  - 권상 실적처리가 참일 경우만 권하 처리를 실행한다.
			 ****************************************************/
			if ( "1".equals(rtnCd)) {
				//권하 실적 처리				
				ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
				jrRtn = (JDTORecord)ejbConn.trx("updCrnDnPrsBackUp", new Class[] { GridData.class }, new Object[] { gdReq });
				
				rtnCd   = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
				rtnMsg	= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
				
				commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
				//ROLLBACK 시 전문 발생
				if (!"0".equals(rtnCd)) {
					
					jrRtn.setResultCode(logId);
					jrRtn.setResultMsg(mthdNm);

					EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
				}
				if (!"1".equals(rtnCd)) {		
					m_ctx.setRollbackOnly();
				} 	
			}
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("권상/권하 처리가 완료 됐습니다.");
			}

			commUtils.printLog(logId, mthdNm, "F-");	
			
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 *  코일 야드 크레인 작업실적 응답 (SEND) 
	 *  염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData sendCoilYdCrnAnswer(GridData gdReq) throws DAOException {
		String mthdNm	= "코일 야드 크레인 작업실적 응답 (SEND) [CCoilJspFaEJB.sendCoilYdCrnAnswer]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("sendCoilYdCrnAnswer", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			 	

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("작업실적 응답 처리가 완료 됐습니다.");
			}
			commUtils.printLog(logId, mthdNm, "F-");			
			return gdRes;	
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 *  코일 야드 크레인 TO위치 재설정 
	 *  염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData updToLocSch(GridData gdReq) throws DAOException {
		String mthdNm	= "크레인 TO위치 재설정[CCoilJspFaEJB.updToLocSch]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updToLocSch", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + "updToLocSch rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			 	

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("TO위치 재설정이 완료 됐습니다.");
			}
			commUtils.printLog(logId, mthdNm, "F-");			
			return gdRes;	
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	
	/**
	 * 적치 가능 번지 리스트 조회 (select box용  소재,제품 공통 )
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @작성자 : 염용선
	 * @작성일 : 2019.09.14
	 */
	public GridData getUsableBedList(GridData gdReq) throws DAOException {
		String mthdNm	= "적치 가능 번지 리스트 조회[CCoilJspFaEJB.getUsableBedList]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			GridData gdRes = (GridData)ejbConn.trx("getUsableBedList", new Class[] { GridData.class }, new Object[] { gdReq });
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} 


	/**
	 * 설비휴지테이블에 등록 (팝업)-확인
	 * 김환진
	 * 2019.09.27
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */ 
	public GridData insEqpPauseHist(GridData gdReq) throws JDTOException {
		String mthdNm = "설비휴지테이블에 등록 (팝업))[CCoilJspFaEJB.insEqpPauseHist]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn		= (JDTORecord)ejbConn.trx("insEqpPauseHist", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("설비이력 등록 처리가 완료 됐습니다.");
			}
			
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 * 입동차량정보 삭제
	 * 야드관리 > 2열연 소재 코일야드[신] > 이송작업관리 > 차량작업관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procCarPntCarInfoClear(GridData gdReq) throws DAOException {
		String mthdNm = "입동차량정보 삭제[CCoilJspFaEJB.procCarPntCarInfoClear]";
		String logId  = commUtils.getLogId();

		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procCarPntCarInfoClear", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생 안함
			if (!"0".equals(rtnCd)) {

				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes.setMessage(rtnMsg);

			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 차량 Point 개폐 기존 : procCoilYdGdsPntUnitCLCoil
	 * 강정선 2019.10.01
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procCoilYdPntUnitCL(GridData gdReq) throws DAOException {
		String mthdNm = "차량Point개폐[CCoilJspFaEJB.procCoilYdPntUnitCL]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn		= (JDTORecord)ejbConn.trx("procCoilYdPntUnitCL", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			
			
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("차량Point개폐 처리가 완료 됐습니다.");	
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 입동순서 변경 : 기존 procCoilYdGdsBayInWoSeqChangCoil
	 * 강정선 2019.10.02
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procBayInWoSeqChange(GridData gdReq) throws DAOException {
		String mthdNm = "입동순서변경[CCoilJspFaEJB.procBayInWoSeqChange]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn		= (JDTORecord)ejbConn.trx("procBayInWoSeqChange", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd			= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg			= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("입동순서변경 처리가 완료 됐습니다.");	
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 차량도착처리 : 기존 CarArrivalNEW
	 * 강정선 2019.10.02
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procCarArrival(GridData gdReq) throws DAOException {
		String mthdNm = "차량도착처리[CCoilJspFaEJB.procCarArrival]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procCarArrival", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("차량도착 처리가 완료 됐습니다.");	
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 차량 초기화
	 * 강정선 2019.10.02
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCarWrMgt(GridData gdReq) throws DAOException {
		String mthdNm = "차량초기화[CCoilJspFaEJB.updCarWrMgt]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn		= (JDTORecord)ejbConn.trx("updCarWrMgt", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd			= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg			= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("차량초기화 처리가 완료 됐습니다.");	
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 차량작업관리 - 상차LOT편성
	 * 강정선 2019.10.03
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData insCarLdLotCoil(GridData gdReq) throws DAOException {
		String mthdNm = "상차LOT편성[CCoilJspFaEJB.insCarLdLotCoil]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn		= (JDTORecord)ejbConn.trx("insCarLdLotCoil", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd			= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg			= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("상차LOT편성 처리가 완료 됐습니다.");	
			}

			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 차량작업관리 상차LOT편성 취소
	 * 강정선 2019.10.10
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delCarLdLotCoil(GridData gdReq) throws DAOException {
		String mthdNm = "상차LOT취소[CCoilJspFaEJBdelCarLdLotCoil]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn		= (JDTORecord)ejbConn.trx("delCarLdLotCoil", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd			= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg			= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("상차LOT취소 처리가 완료 됐습니다.");	
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 차량 상차완료 처리
	 * 강정선 2019.10.16
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCoilYdCarUpEndPp(GridData gdReq) throws DAOException {
		String mthdNm = "차량상차완료[CCoilJspFaEJB.updCoilYdCarUpEndPp]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn		= (JDTORecord)ejbConn.trx("updCoilYdCarUpEndPp", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd			= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg			= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
						
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("차량상차 처리가 완료 됐습니다.");	
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 상차정보조회 - 차상위수정
	 * 강정선 2019.10.16
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData carLiftPosSet(GridData gdReq) throws DAOException {
		String mthdNm = "차상위수정[CCoilJspFaEJB.carLiftPosSet]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("carLiftPosSet", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("차상위수정 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 차량작업관리 - 전체입동제한
	 * 야드관리 > 2열연 코일야드[신] > 출하이송관리 > 차량작업관리
	 * 강정선 2019.11.05
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procAllCarPntYnReg(GridData gdReq) throws DAOException {
		String mthdNm = "전체입동제한[CCoilJspFaEJB.procAllCarPntYnReg]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procAllCarPntYnReg", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			

			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("전체입동제한 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 차량작업관리 - 전체입동제한
	 * 야드관리 > 2열연 소재 코일야드[신] > 이송작업관리 > 소재차량작업관리
	 * 염용선 2023.05.31
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procAllCarPntYnRegH(GridData gdReq) throws DAOException {
		String mthdNm = "전체입동제한[CCoilJspFaEJB.procAllCarPntYnRegH]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procAllCarPntYnRegH", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			

			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("전체입동제한 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 배차차량작업관리 - 제품이송우선순위
	 * 야드관리 > 2열연 제품 코일야드[신] > 출하관리 > 배차차량작업관리
	 * 강정선 2020.01.17
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCoilCarMovYn(GridData gdReq) throws DAOException {
		String mthdNm = "제품이송우선순위[CCoilJspFaEJB.updCoilCarMovYn]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCoilCarMovYn", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("제품이송우선순위 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 차량작업관리 - 대기장도착
	 * 야드관리 > 2열연 코일야드[신] > 출하이송관리 > 차량작업관리
	 * 강정선 2019.11.05
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procStandByYdArrive(GridData gdReq) throws DAOException {
		String mthdNm = "대기장도착[CCoilJspFaEJB.procStandByYdArrive]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procStandByYdArrive", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("대기장도착 처리가 완료 됐습니다.");
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 차량작업관리 - 출발처리
	 * 야드관리 > 2열연 코일야드[신] > 출하이송관리 > 차량작업관리
	 * 강정선 2019.11.05
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procCarStart(GridData gdReq) throws DAOException {
		String mthdNm = "출발처리[CCoilJspFaEJB.procCarStart]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procCarStart", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			

			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("차량출발 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 차량작업관리 - 차량POINT SPAN 범위 설정
	 * 야드관리 > 2열연 코일야드[신] > 출하이송관리 > 차량작업관리
	 * 강정선 2019.11.08
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCarSpanRng(GridData gdReq) throws DAOException {
		String mthdNm = "차량포인트SPAN범위설정[CCoilJspFaEJB.updCarSpanRng]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCarSpanRng", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			

			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("범위설정 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 제품 반품/회송작업 등록 - 하차작업등록
	 * 강정선 2019.10.22
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regCarUdWrk(GridData gdReq) throws DAOException {
		String mthdNm = "하차작업등록[CCoilJspFaEJB.regCarUdWrk]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("regCarUdWrk", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("하차작업등록 처리가 완료 됐습니다.");	
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 * 소재 반품/부분하차 등록 - 하차작업등록
	 * 염용선 2023 03 17
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regCarUdWrkH(GridData gdReq) throws DAOException {
		String mthdNm = "하차작업등록[CCoilJspFaEJB.regCarUdWrkH]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("regCarUdWrkH", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("하차작업등록 처리가 완료 됐습니다.");	
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 제품 통합이적지시 - 이적지시
	 * 야드관리 > 2열연 코일야드[신] > 산적LOT관리  > 제품통합이적지시
	 * 강정선 2019.11.04
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updGdsColUnitMvstkReg(GridData gdReq) throws DAOException {
		String mthdNm = "이적지시[CCoilJspFaEJB.updGdsColUnitMvstkReg]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updGdsColUnitMvstkReg", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("제품통합 이적지시 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 검수이상제품조회 - 출고검수
	 * 야드관리 > 2열연 코일야드[신] > 출하이송관리 > 검수이상제품조회
	 * 강정선 2019.11.18
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procCarExamination(GridData gdReq) throws DAOException {
		String mthdNm = "출고검수[CCoilJspFaEJB.procCarExamination]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procCarExamination", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("출고검수 처리가 완료 됐습니다.");	
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 검수이상제품조회 - 차상위치 수정
	 * 야드관리 > 2열연 코일야드[신] > 출하이송관리 > 검수이상제품조회
	 * 강정선 2019.11.19
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCarExaminationCarUppLocCd(GridData gdReq) throws DAOException {
		String mthdNm = "차상위치수정[CCoilJspFaEJB.updCarExaminationCarUppLocCd]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCarExaminationCarUppLocCd", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");


			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("차상위치수정 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 검수이상제품조회 - 이상코드 수정
	 * 야드관리 > 2열연 코일야드[신] > 출하이송관리 > 검수이상제품조회
	 * 강정선 2019.11.19
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCarExaminationYdAbCd(GridData gdReq) throws DAOException {
		String mthdNm = "이상코드수정[CCoilJspFaEJB.updCarExaminationYdAbCd]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCarExaminationYdAbCd", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			

			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("이상코드수정 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *  결로재 보급 ON/OFF
	 *  YYS 2019.11.05
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updConOffResultList(GridData gdReq) throws DAOException {
		String mthdNm	= "결로재 보급 ON/OFF[CCoilJspFaEJB.updConOffResultList]";
		String logId	= commUtils.getLogId();	
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updConOffResultList", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				String sMsg = "ON";
				if( "N".equals(gdReq.getParam("MODE") ) ) {
					sMsg = "OFF";
				}
				gdRet.setMessage("결로재 보급 "+ sMsg +" 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 준비스케줄과 준비재료삭제
	 * 
	 * 김환진 2019.11.20
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData delYdPrepSch(GridData gdReq) throws JDTOException {
		/*
		 * 업무기준 : 그리드에 선택된 준비스케줄과 준비재료 삭제
		 */
		String mthdNm	= "준비스케줄과 준비재료삭제 [CCoilJspFaEJB.delYdPrepSch]";
		String logId	= commUtils.getLogId();	

		try{
			
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("delYdPrepSch", new Class[] { GridData.class }, new Object[] { gdReq });

			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("준비재료삭제 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	
	/**
	 * (제품)지포장재 반입관리 - 재반입
	 * 야드관리 > 2열연 코일야드[신] > 산적LOT관리 > 제품지포장재 반입관리
	 * 강정선 2019.11.22
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 * 기존 : updCoilGdsYdReSendGF
	 */
	public GridData updCoilReSendGF(GridData gdReq) throws DAOException {
		String mthdNm = "지포장재 재반입[CCoilJspFaEJB.updCoilReSendGF]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCoilReSendGF", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("지포장재 재반입 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 *  야드관리 > 기준관리 > 소제스케줄기준관리 (수정)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws JDTOException
	 * @작성자 : 송정현
	 * @작성일 : 2019.11.30
	 */
	
	public GridData updSchRuleMgtH(GridData gdReq) throws DAOException {
		String mthdNm = "소재크레인스케줄 기준 변경[CCoilJspFaEJB.updSchRuleMgtH]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID		
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updSchRuleMgtH", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("크레인스케줄 기준 변경 처리가 완료 됐습니다.");	
			}		
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}	
	
	/**
	 *  야드관리 > 2열연 소재 코일야드[신] > 크레인작업관리 > 크레인스케쥴현황조회 (수정) 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws JDTOException
	 * @작성자 : 정종균
	 * @작성일 : 2024.07.02
	 */
	
	public GridData updSchProhExnH(GridData gdReq) throws DAOException {
		String mthdNm = "소재크레인스케줄금지 기준 변경[CCoilJspFaEJB.updSchProhExnH]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID		
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updSchProhExnH", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("크레인스케줄금지 기준 변경 처리가 완료 됐습니다.");	
			}		
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}
	/**
	 *  야드관리 > 기준관리 > 제품스케줄기준관리 (수정)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws JDTOException
	 * @작성자 : 송정현
	 * @작성일 : 2019.11.30
	 */
	
	public GridData updSchRuleMgtJ(GridData gdReq) throws DAOException {
		String mthdNm = "제품크레인스케줄 기준 변경[CCoilJspFaEJB.updSchRuleMgtJ]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID		
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updSchRuleMgtJ", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("크레인스케줄 기준 변경 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 대차이동가능구간설정
	 * 야드관리 > 2열연 코일야드[신] > 대차작업관리 > (소재)대차작업현황조회
	 * 강정선 2019.11.26
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCoilYdTcarWrkBay(GridData gdReq) throws DAOException {
		String mthdNm = "대차이동가능구간설정[CCoilJspFaEJB.updCoilYdTcarWrkBay]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCoilYdTcarWrkBay", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("대차이동가능구간설정 처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	/**
	 * 저장위치 좌표설정화면 열 수정 
	 * @작성자 : 염용선
	 * @작성일 : 2019.07.26
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData updCoilYdStkPosSetH(GridData gdReq) throws DAOException {
		String mthdNm = "좌표설정[CCoilJspFaEJB.updCoilYdStkPosSetH]";
		String logId  = commUtils.getLogId();
			
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
			JDTORecord jrRtn = (JDTORecord) ejbConn.trx("updCoilYdStkPosSetH", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("좌표설정 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}				
	} 	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드 및 설비 베드정보수정 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData updCoilYdStkPosSetBedH(GridData gdReq) throws DAOException {
		String mthdNm = "야드 및 설비 베드정보수정[CCoilJspFaEJB.updCoilYdStkPosSetBedH]";
		String logId  = commUtils.getLogId();

		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCoilYdStkPosSetBedH", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("베드정보수정 처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	/**
	 * 저장위치 좌표설정화면 열 수정 
	 * @작성자 : 염용선
	 * @작성일 : 2019.07.26
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData updCoilYdStkPosSetJ(GridData gdReq) throws DAOException {
		String mthdNm = "저장위치 좌표설정화면 열 수정[CCoilJspFaEJB.updCoilYdStkPosSetJ]";
		String logId  = commUtils.getLogId();
			
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
			JDTORecord jrRtn = (JDTORecord) ejbConn.trx("updCoilYdStkPosSetJ", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("좌표설정 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}				
	} 	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드 및 설비 베드정보수정 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData updCoilYdStkPosSetBedJ(GridData gdReq) throws DAOException {
		String mthdNm = "야드 및 설비 베드정보수정[CCoilJspFaEJB.updCoilYdStkPosSetBedJ]";
		String logId  = commUtils.getLogId();

		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCoilYdStkPosSetBedJ", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}

			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("베드정보수정 처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	
	
	/**
	 * 야드크레인 작업관리 (스케줄 취소)-제품
	 * 염용선 2019.11.10
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws JDTOException
	 */
	
	public GridData cancelSchCoilYdCrnWorkMgtJ(GridData gdReq) throws JDTOException {
		String mthdNm = "스케줄 취소[CCoilJspFaEJB.cancelSchCoilYdCrnWorkMgtJ]";
		String logId  = commUtils.getLogId();	
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, mthdNm, "F+");
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("cancelSchCoilYdCrnWorkMgtJ", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			

			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("스케줄 취소 처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}



	/**
	 * 야드크레인 작업관리 (스케줄 취소)-제품
	 * 염용선 2019.11.10
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws JDTOException
	 */
	
	public GridData cancelSchCoilYdCrnWorkMgtH(GridData gdReq) throws JDTOException {
		String mthdNm = "스케줄 취소[CCoilJspFaEJB.cancelSchCoilYdCrnWorkMgtH]";
		String logId  = commUtils.getLogId();	
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, mthdNm, "F+");
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("cancelSchCoilYdCrnWorkMgtH", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			

			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("스케줄 취소 처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

		
	/**
	 *  야드크레인 작업관리 (작업취소) - 제품
	 *  염용선 2019.09.11
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 */
	public GridData delWorkCoilYdCrnWorkMgtJ(GridData gdReq) throws DAOException {
		String mthdNm = "야드크레인 작업관리 (작업취소)[CCoilJspFaEJB.delWorkCoilYdCrnWorkMgtJ]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("delWorkCoilYdCrnWorkMgtJ", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("작업취소 처리가 완료 됐습니다.");
			}
			
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *  야드크레인 작업관리 (작업취소) - 소재
	 *  염용선 2019.09.11
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 */
	public GridData delWorkCoilYdCrnWorkMgtH(GridData gdReq) throws DAOException {
		String mthdNm = "야드크레인 작업관리 (작업취소)[CCoilJspFaEJB.delWorkCoilYdCrnWorkMgtH]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("delWorkCoilYdCrnWorkMgtH", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("작업취소 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	
	/**
	 * 야드크레인 작업관리 (스케줄 재전송)-제품
	 * 염용선 2019.11.10
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws JDTOException
	 */
	
	public GridData reSendSchCoilYdCrnWorkMgtJ(GridData gdReq) throws JDTOException {
		String mthdNm = "크레인상태관리 - 야드크레인 작업관리 (스케줄 재전송)-제품[CCoilJspFaEJB.reSendSchCoilYdCrnWorkMgtJ]";
		String logId  = commUtils.getLogId();	
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, mthdNm, "F+");
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("reSendSchCoilYdCrnWorkMgtJ", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("스케줄 재전송 처리가 완료 됐습니다.");	
			}		
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

/**
	 * 야드크레인 작업관리 (스케줄 재전송) - 소재
	 * 염용선 2019.11.10
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws JDTOException
	 */
	
	public GridData reSendSchCoilYdCrnWorkMgtH(GridData gdReq) throws JDTOException {
		String mthdNm = "크레인상태관리 - 야드크레인 작업관리 (스케줄 재전송)-소재[CCoilJspFaEJB.reSendSchCoilYdCrnWorkMgtH]";
		String logId  = commUtils.getLogId();	
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, mthdNm, "F+");
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("reSendSchCoilYdCrnWorkMgtH", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("스케줄 재전송 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	
	/**
	 * 크레인상태관리 - 순위변경[제품]
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws JDTOException
	 */
	
	public GridData crnSchPriorOrderJ(GridData gdReq) throws JDTOException {
		String mthdNm = "크레인상태관리 - 순위변경[제품][CCoilJspFaEJB.crnSchPriorOrderJ]";
		String logId  = commUtils.getLogId();	
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, mthdNm, "F+");
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("crnChgSchPriorCoilJ", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
	
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("순위변경 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 * 크레인상태관리 - 순위변경 소재
	 * 염용선 2019.09.10
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws JDTOException
	 */
	
	public GridData crnSchPriorOrderH(GridData gdReq) throws JDTOException {
		String mthdNm = "크레인상태관리 - 순위 변경[소재][CCoilJspFaEJB.crnSchPriorOrderH]";
		String logId  = commUtils.getLogId();	
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, mthdNm, "F+");
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("crnChgSchPriorCoilH", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("순위변경 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 * 크레인상태관리 - 긴급작업 변경[제품]
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws JDTOException
	 */
	
	public GridData crnSchPriorCoilJ(GridData gdReq) throws JDTOException {
		String mthdNm = "크레인상태관리 - 긴급작업 변경[제품][CCoilJspFaEJB.crnSchPriorCoilJ]";
		String logId  = commUtils.getLogId();	
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, mthdNm, "F+");
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			String sApp825 = coilDao.ApplyYn(logId, mthdNm, "APP825","J","*"); //긴급작업
			
			if ("Y".equals(sApp825)) {
				jrRtn = (JDTORecord)ejbConn.trx("updPriorWrkChange", new Class[] { GridData.class }, new Object[] { gdReq });	
			} else {
				jrRtn = (JDTORecord)ejbConn.trx("crnSchPriorCoilJ", new Class[] { GridData.class }, new Object[] { gdReq });
			}
			
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			

			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("긴급작업 변경 처리가 완료 됐습니다.");
			}		
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}


	/**
	 * 크레인상태관리 - 긴급작업 변경[ 소재]
	 * 염용선 2019.09.10
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws JDTOException
	 */
	
	public GridData crnSchPriorCoilH(GridData gdReq) throws JDTOException {
		String mthdNm = "크레인상태관리 - 긴급작업 변경[CCoilJspFaEJB.crnSchPriorCoilH]";
		String logId  = commUtils.getLogId();	
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, mthdNm, "F+");
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("crnSchPriorCoilH", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("긴급작업 변경 처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	/**
	 * 크레인상태관리 - 크레인 변경[제품]
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData wrkCrnChangeJ(GridData gdReq) throws JDTOException {					
		String mthdNm = "크레인상태관리 - 크레인 변경[제품][CCoilJspFaEJB.wrkCrnChangeJ]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");	

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);				
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);	
			
   			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			String sApp826 = coilDao.ApplyYn(logId, mthdNm, "APP826","J","*"); //크레인변경
			
			if ("Y".equals(sApp826)) {
				jrRtn = (JDTORecord)ejbConn.trx("wrkCrnChangeJNew", new Class[] { GridData.class }, new Object[] { gdReq });	
			} else {
				jrRtn = (JDTORecord)ejbConn.trx("wrkCrnChangeJ", new Class[] { GridData.class }, new Object[] { gdReq });
			}
			
			String rtnCd	= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);
				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("크레인 변경 처리가 완료 됐습니다.");
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;

		}catch(DAOException e) {
			throw e;
		}catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}

	} 

	
	/**
	 * 크레인상태관리 - 크레인 변경[소재]
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */

	public GridData wrkCrnChangeH(GridData gdReq) throws JDTOException {						
		String mthdNm = "크레인상태관리 - 크레인 변경[소재][CCoilJspFaEJB.wrkCrnChangeH]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");	

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);				
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("wrkCrnChangeH", new Class[] { GridData.class }, new Object[] { gdReq });				
			
			String rtnCd	= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("크레인 변경 처리가 완료 됐습니다.");
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;

		}catch(DAOException e) {
			throw e;
		}catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}

	} 

	/**
	 * 크레인상태관리 - 수입작업시 크레인 변경[소재]
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */

	public GridData cvCrnChangeH(GridData gdReq) throws JDTOException {						
		String mthdNm = "크레인상태관리 - 수입작업시 크레인 변경[소재][CCoilJspFaEJB.cvCrnChangeH]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");	

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);				
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("cvCrnChangeHNew", new Class[] { GridData.class }, new Object[] { gdReq });				
			
			String rtnCd	= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("수입 크레인 변경 처리가 완료 됐습니다.");
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;

		}catch(DAOException e) {
			throw e;
		}catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}

	} 

	
	/**
	 *  권하위치 변경 (크레인작업관리 화면)-소재
	 *  염용선 2019.09.10
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws JDTOException
	 */
	public GridData updToPosFixCoilH(GridData gdReq) throws JDTOException {
		String mthdNm = "권하위치 변경 (크레인작업관리 화면[소재])[CCoilJspFaEJB.updToPosFixCoilH]";
		String logId  	= commUtils.getLogId();
	
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updToPosFixCoilH", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			 	
			
			gdRes.setStatus(rtnCd);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("권하위치 변경 처리가 완료 됐습니다.");	
			}
			
			commUtils.printLog(logId, mthdNm, "F-");			
			return gdRes;
			
		}catch(DAOException e) {
			throw e;
		}catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}  

	
	/**
	 *  권하위치 변경 (크레인작업관리 화면)-제품
	 *  염용선 2019.09.10
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws JDTOException
	 */
	public GridData updToPosFixCoilJ(GridData gdReq) throws JDTOException {
		String mthdNm = "권하위치 변경 (크레인작업관리 화면[제품])[CCoilJspFaEJB.updToPosFixCoilJ]";
		String logId  = commUtils.getLogId();
	
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updToPosFixCoilJ", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("권하위치 변경 처리가 완료 됐습니다.");	
			}
			
			commUtils.printLog(logId, mthdNm, "F-");			
			return gdRes;
			
		}catch(DAOException e) {
			throw e;
		}catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}  
	
	/**
	 *  권하위치 변경 (크레인작업관리 화면)-제품
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws JDTOException
	 */
	public GridData updToPosFixCoil(GridData gdReq) throws JDTOException {
		String mthdNm = "권하위치 변경[CCoilJspFaEJB.updToPosFixCoilJ]";
		String logId  = commUtils.getLogId();
	
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updToPosFixCoil", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			if (!"0".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("권하위치 변경 처리가 완료 됐습니다.");	
			}
			
			commUtils.printLog(logId, mthdNm, "F-");			
			return gdRes;
			
		}catch(DAOException e) {
			throw e;
		}catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}  
	
	
	/**
	 *  야드관리 > 코일소재야드 > 재공관리 > 차량이적등록  (제품도이적지시)
	 *  염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updColUnitCarMvstkRegH(GridData gdReq) throws DAOException {
		String mthdNm = "차량동간이적등록[CCoilJspFaEJB.updColUnitCarMvstkRegH]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updColUnitCarMvstkRegH", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("차량동간이적 처리가 완료 됐습니다.");
			} 			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 차량동간이적 (이적지시 ) - BCoilJspFaEJB.updColUnitCarMvstkRegNew
	 * 염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updBayAndBayCarMvstkRegJ(GridData gdReq) throws DAOException {		
		String mthdNm = "차량이적등록[CCoilJspFaEJB.updBayAndBayCarMvstkRegJ]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updBayAndBayCarMvstkRegJ", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			commUtils.printLog(logId, mthdNm + " rtnMsg:"+ rtnMsg, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("차량이적등록 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}	// end of updIfTestData
	
	
	/** 
	 * 대차상태변경
	 * 야드관리 > 2열연 코일야드[신] > 대차작업관리 > 대차작업현황조회 > 대차작업 BackUp
	 * 강정선 2019.11.27
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 * 기존 : updCoilYdTcarStsSet
	 */
	public GridData updCoilYdTcarStsSet(GridData gdReq) throws DAOException {
		String mthdNm = "대차상태변경[CCoilJspFaEJB.updCoilYdTcarStsSet]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCoilYdTcarStsSet", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("대차상태변경 처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/** 
	 * 대차운전모드변경
	 * 야드관리 > 2열연 코일야드[신] > 대차작업관리 > 대차작업현황조회 > 대차작업 BackUp
	 * 강정선 2019.11.27
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCoilYdTcarStsSetCrnMode(GridData gdReq) throws DAOException {
		String mthdNm = "대차운전모드변경[CCoilJspFaEJB.updCoilYdTcarStsSetCrnMode]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCoilYdTcarStsSetCrnMode", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("대차운전모드변경 처리가 완료 됐습니다.");
			}					
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/** 
	 * 대차상태초기화
	 * 야드관리 > 2열연 코일야드[신] > 대차작업관리 > 대차작업현황조회 > 대차작업 BackUp
	 * 강정선 2019.11.27
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCoilYdTcarClear(GridData gdReq) throws DAOException {
		String mthdNm = "대차상태초기화[CCoilJspFaEJB.updCoilYdTcarClear]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCoilYdTcarClear", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("대차상태초기화 처리가 완료 됐습니다.");
			}				
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *  야드관리 > 코일소재야드 > 재공관리 > 차량이적등록  (제품도이적지시)
	 *  염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updColUnitCarMvstkRegJ(GridData gdReq) throws DAOException {
		String mthdNm = "차량이적등록[CCoilJspFaEJB.updColUnitCarMvstkRegJ]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updColUnitCarMvstkRegJ", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("차량동간이적 처리가 완료 됐습니다.");
			} 			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	
	/**
	 * 저장위치 삭제(현재위치의 재료번호를 NULL로 수정, 스케줄/작업예약 삭제, 차량/대차스케줄 삭제)-제품
	 * 염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData delCoilYdStkPosInfoJ(GridData gdReq) throws DAOException {
		String mthdNm	= "저장위치 삭제[제품][CCoilJspFaEJB.delCoilYdStkPosInfoJ]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("delCoilYdStkPosJ", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("저장위치 삭제 처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	
	
	
	
	/**
	 * 저장위치 삭제(현재위치의 재료번호를 NULL로 수정, 스케줄/작업예약 삭제, 차량/대차스케줄 삭제)-소재
	 * 염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData delCoilYdStkPosInfoH(GridData gdReq) throws DAOException {
		String mthdNm	= "저장위치 삭제[소재][CCoilJspFaEJB.delCoilYdStkPosInfoH]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("delCoilYdStkPosH", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("저장위치 삭제 처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	
	
	/**
	 * 저장위치변경관리 (저장위치수정[제품])
	 * 염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updStrlocMod3J(GridData gdReq) throws DAOException {
		String mthdNm = "저장위치변경관리[제품][CCoilJspFaEJB.updStrlocMod3J]";
		String logId  = commUtils.getLogId();	

		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updStrlocMod3J", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("저장위치변경 처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} 

	
	
	/**
	 * 저장위치변경관리 (저장위치수정:소재)
	 * 염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updStrlocMod3H(GridData gdReq) throws DAOException {
		String mthdNm = "저장위치변경관리[소재][CCoilJspFaEJB.updStrlocMod3H]";
		String logId  = commUtils.getLogId();	

		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updStrlocMod3H", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("저장위치변경 처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} 

	/**
	 * 저장위치변경관리 (저장위치수정: 송신처리[소재])
	 * 염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updStrlocMod2H(GridData gdReq) throws DAOException {
		String mthdNm = "저장위치 삭제[소재][CCoilJspFaEJB.updStrlocMod2H]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updStrlocMod2H", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("송신처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 * 저장위치변경관리 (저장위치수정: 송신처리[제품])
	 * 염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updStrlocMod2J(GridData gdReq) throws DAOException {
		String mthdNm = "저장위치 삭제[제품][CCoilJspFaEJB.updStrlocMod2J]";
		String logId  = commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updStrlocMod2J", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("송신처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 * 제품저장위치대용도코드 등록
	 * 야드관리 > 2열연 코일야드[신] > 기준관리 > 제품저장위치용도관리 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 김환진
	 * @작성일 : 2019/11/29
	 */
	public GridData updStrlocUsgSetJ(GridData gdReq){
		String mthdNm	= "제품저장위치용도관리 등록[CCoilJspFaEJB.updStrlocUsgSetJ]";
		String logId	= commUtils.getLogId();	

		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updStrlocUsgSetJ", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			

		
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("코드 등록 처리가 완료 됐습니다.");
			}		
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 소재저장위치대용도코드 등록
	 * 야드관리 > 2열연 코일야드[신] > 기준관리 > 소재저장위치용도관리 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 김환진
	 * @작성일 : 2019/11/29
	 */
	public GridData updStrlocUsgSetH(GridData gdReq){
		String mthdNm	= "소재저장위치대용도코드 등록[CCoilJspFaEJB.updStrlocUsgSetH]";
		String logId	= commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updStrlocUsgSetH", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			

			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("코드 등록 처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 * 제품위치검색 범위 수정 (화면:위치검색SPAN관리)
	 * 야드관리 > 2열연 코일야드[신] > 기준관리 > 제품저장영역별검색순서조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 김환진
	 * @작성일 : 2019/12/05
	 */
	public GridData updYdLocSrchRngCoilJ(GridData gdReq) throws JDTOException {						
		String mthdNm = "제품위치검색 범위 수정- 화면:위치검색SPAN관리[CCoilJspFaEJB.updYdLocSrchRngCoilJ]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updYdLocSrchRngCoilJ", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
	
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("범위수정 처리가 완료 됐습니다.");	
			}
			
			commUtils.printLog(logId, mthdNm, "F-");			
			return gdRes;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}		
	} 
	
	/**
	 * 소재위치검색 범위 수정 (화면:위치검색SPAN관리)
	 * 야드관리 > 2열연 코일야드[신] > 기준관리 > 소재저장영역별검색순서조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 김환진
	 * @작성일 : 2019/12/05
	 */
	public GridData updYdLocSrchRngCoilH(GridData gdReq) throws JDTOException {						
		String mthdNm = "소재위치검색 범위 수정- 화면:위치검색SPAN관리[CCoilJspFaEJB.updYdLocSrchRngCoilH]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updYdLocSrchRngCoilH", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
					
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("범위수정 처리가 완료 됐습니다.");	
			}
			
			commUtils.printLog(logId, mthdNm, "F-");			
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}		
	} 
	
	/**
	 * 제품위치검색 테이블 UPDATE/INSERT (화면:위치검색SPAN관리)
	 * 야드관리 > 2열연 코일야드[신] > 기준관리 > 제품저장영역별검색순서조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 김환진
	 * @작성일 : 2019/12/05
	 */
	public GridData updYdLocSrchBedCoilJ(GridData gdReq) throws JDTOException {						
		String mthdNm	= "제품위치검색 테이블 UPDATE/INSERT - 화면:위치검색SPAN관리[CCoilJspFaEJB.updYdLocSrchBedCoilJ]";
		String logId	= commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updYdLocSrchBedCoilJ", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("저장영역별검색순서 처리가 완료 됐습니다.");	
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}
	
	/**
	 * 소재위치검색 테이블 UPDATE/INSERT (화면:위치검색SPAN관리)
	 * 야드관리 > 2열연 코일야드[신] > 기준관리 > 소재저장영역별검색순서조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 김환진
	 * @작성일 : 2019/12/05
	 */
	public GridData updYdLocSrchBedCoilH(GridData gdReq) throws JDTOException {						
		String mthdNm	= "소재위치검색 테이블 UPDATE/INSERT - 화면:위치검색SPAN관리[CCoilJspFaEJB.updYdLocSrchBedCoilH]";
		String logId	= commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updYdLocSrchBedCoilH", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("저장영역별검색순서 처리가 완료 됐습니다.");	
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}
	
	/**
	 * 제품위치검색 테이블 삭제 (화면:위치검색SPAN관리)
	 * 야드관리 > 2열연 코일야드[신] > 기준관리 > 제품저장영역별검색순서조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 김환진
	 * @작성일 : 2019/12/05
	 */
	public GridData delYdLocSrchBedCoilJ(GridData gdReq) throws JDTOException {						
		String mthdNm = "제품위치검색 범위 삭제- 화면:위치검색SPAN관리[CCoilJspFaEJB.delYdLocSrchBedCoilJ]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);				
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("delYdLocSrchBedCoilJ", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("범위 삭제 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}			
	} 
	
	/**
	 * 제품위치검색 테이블 삭제 (화면:헤더삭제)
	 * 야드관리 > 2열연 코일야드[신] > 기준관리 > 제품저장영역별검색순서조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 김환진
	 * @작성일 : 2020/02/25
	 */
	public GridData deldYdLocsrchrngJ(GridData gdReq) throws JDTOException {						
		String mthdNm = "제품위치검색 범위 삭제- 화면:헤더삭제[CCoilJspFaEJB.deldYdLocsrchrngJ]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);				
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("deldYdLocsrchrngJ", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("범위 삭제 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}			
	} 
	
	/**
	 * 소재위치검색 테이블 삭제 (화면:위치검색SPAN관리)
	 * 야드관리 > 2열연 코일야드[신] > 기준관리 > 소재저장영역별검색순서조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 김환진
	 * @작성일 : 2019/12/05
	 */
	public GridData delYdLocSrchBedCoilH(GridData gdReq) throws JDTOException {						
		String mthdNm = "소재위치검색 범위 삭제- 화면:위치검색SPAN관리[CCoilJspFaEJB.delYdLocSrchBedCoilH]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);				
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("delYdLocSrchBedCoilH", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("범위 삭제 처리가 완료 됐습니다.");
			} 	
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}			
	} 
	
	/**
	 * 소재위치검색 테이블 삭제 (화면:헤더삭제)
	 * 야드관리 > 2열연 코일야드[신] > 기준관리 > 소재저장영역별검색순서조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 김환진
	 * @작성일 : 2020/02/25
	 */
	public GridData deldYdLocsrchrngH(GridData gdReq) throws JDTOException {						
		String mthdNm = "소재위치검색 범위 삭제- 화면:헤더삭제[CCoilJspFaEJB.deldYdLocsrchrngH]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);				
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("deldYdLocsrchrngH", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("범위 삭제 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}			
	} 
	
	/**
	 * 제품위치검색베범위 등록
	 * 야드관리 > 2열연 코일야드[신] > 기준관리 > 제품저장영역별검색순서조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 김환진
	 * @작성일 : 2019/12/05
	 */
	public GridData insLocSrchRngJ(GridData gdReq) throws JDTOException {
		String mthdNm = "제품위치검색베드범위 등록[CCoilJspFaEJB.insLocSrchRngJ]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("insLocSrchRngJ", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}

			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("베드범위 등록 처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}  
	
	/**
	 * 소재위치검색베범위 등록
	 * 야드관리 > 2열연 코일야드[신] > 기준관리 > 소재저장영역별검색순서조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 김환진
	 * @작성일 : 2019/12/05
	 */
	public GridData insLocSrchRngH(GridData gdReq) throws JDTOException {
		String mthdNm = "소재위치검색베드범위 등록[CCoilJspFaEJB.insLocSrchRngH]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("insLocSrchRngH", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);
				
				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
				
			}

			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("베드범위 등록 처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}


	/**
	 * 제품준비스케줄수정 - 이현진
	 * 야드관리 > 2열연 코일야드[신] > 출하이송관리 > 코일이송LOT편성 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updYdPrepSch(GridData gdReq) throws JDTOException {
		String mthdNm	= "준비스케줄수정 [CCoilJspFaEJB.updYdPrepSch";
		String logId	= commUtils.getLogId();	

		try{	
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
					
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updYdPrepSch", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
	
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
				
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
	
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("수정 처리가 완료 됐습니다.");	
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	

	
	/** 
	 * 차량예정정보 전송 백업
	 * 야드관리 > 2열연 코일야드[신] > 출하이송관리 > 제품차량예정정보 백업전송
	 * 강정선 2019.12.09
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regCarUdExplainInfo(GridData gdReq) throws DAOException {
		String mthdNm = "차량예정정보백업[CCoilJspFaEJB.regCarUdExplainInfo]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("regCarUdExplainInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("차량예정정보백업 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 * 이송작업관리 - 이송대상재를 준비스케줄에 등록 -자동
	 * 김환진 2019.10.17
	 * 이송대상재를 준비스케줄에 등록 - 크레인설비 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 */
	public GridData insYdPrepSchNCrnH(GridData gdReq) throws JDTOException {

		String mthdNm = "이송작업관리 - 이송대상재를 준비스케줄에 등록 -자동[CCoilJspFaEJB.insYdPrepSchNCrnH]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

	

			JDTORecord jrInRec = CmUtil.genJDTORecord(gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);	
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("insYdPrepSchNCrnH", new Class[] { JDTORecord.class }, new Object[] { jrInRec });
			
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
	
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			 	
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
	
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("등록 처리가 완료 됐습니다.");	
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 이송대상재를 준비스케줄에 등록 - 수동, 크레인설비 등록
	 * 김환진 2019.10.17
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 */
	public GridData insYdPrepSchNCrnByManualH(GridData gdReq) throws JDTOException {
		String mthdNm = "이송작업관리 - 이송대상재를 준비스케줄에 등록 - 수동[insYdPrepSchNCrnByManualH]";
		String logId  = commUtils.getLogId();		
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			//JDTORecord jrRec = CmUtil.genJDTORecord(gdReq);
			//JDTORecord [] jrInRec = commUtils.genJDTORecordSet(gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("insYdPrepSchNCrnByManualH", new Class[] { GridData.class }, new Object[] { gdReq });			
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
	
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("등록 처리가 완료 됐습니다.");	
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 * 이송상차Backup처리 화면 : 초기화
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData initMvCarSchMgt(GridData gdReq) throws DAOException {
		String mthdNm =  "이송상차Backup처리 화면 - 초기화[CCoilJspFaEJB.initMvCarSchMgt]";
		String logId = commUtils.getLogId();
		 
		try{

			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			commUtils.printLog(logId, mthdNm, "F+");
			
			String modifier  = commUtils.trim(gdReq.getParam("userid")); //수정자
			
			EJBConnector ejbConn 	= null;
			JDTORecord outRecord  	= commUtils.getParam(logId, mthdNm, modifier);
			JDTORecord jrRst 		= JDTORecordFactory.getInstance().create();
			JDTORecord jrRtn 		= JDTORecordFactory.getInstance().create();
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String sYdCarProgStat;

			for (int ii = 0; ii < rowCnt; ii++) {
				
				outRecord.setField("TC_CODE"        	, "TSYDJ004"); 
				outRecord.setField("TRN_EQP_CD"			, commUtils.getValue(gdReq, "TRN_EQP_CD", ii));
				outRecord.setField("TRN_WRK_FULLVOID_GP", "E");
				outRecord.setField("BACKUP_YN"			, "Y");
				sYdCarProgStat = commUtils.getValue(gdReq, "YD_CAR_PROG_STAT", ii);
				if ("1".equals(sYdCarProgStat)||
					"2".equals(sYdCarProgStat)||
					"3".equals(sYdCarProgStat)||
					"4".equals(sYdCarProgStat)||
					"5".equals(sYdCarProgStat)) {
					outRecord.setField("WLOC_CD"		, commUtils.getValue(gdReq, "SPOS_WLOC_CD", ii));
				} else {
					outRecord.setField("WLOC_CD"		, commUtils.getValue(gdReq, "ARR_WLOC_CD", ii));
				}
					
				ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
				jrRtn 	= (JDTORecord)ejbConn.trx("initCarSch", new Class[] { JDTORecord.class }, new Object[] { outRecord });
				jrRst 	= commUtils.addSndData(jrRst, jrRtn);
				
			}
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			 
			commUtils.printLog(logId, mthdNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 * 상차완료Backup처리 화면 : 초기화
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData mkUdCarSch(GridData gdReq) throws DAOException {
		String mthdNm = "하차백업생성[CCoilJspFaEJB.mkUdCarSch]";
		String logId  = commUtils.getLogId();
		 
		try{

			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			commUtils.printLog(logId, mthdNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("mkUdCarSch", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			 
			commUtils.printLog(logId, mthdNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 구내운송차량출발
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqTsStart(GridData gdReq) throws DAOException {
		String mthdNm = "구내운송차량출발 [CCoilJspFaEJB.reqTsStart]";
		String logId  = commUtils.getLogId();
		
		try {

			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			commUtils.printLog(logId, mthdNm, "F+");
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("JMS_TC_CD"			, "TSYDJ004" );
			jrParam.setField("TRN_EQP_CD"			, gdReq.getParam("TRN_EQP_CD") );
			jrParam.setField("SPOS_WLOC_CD"			, "" );
			jrParam.setField("SPOS_YD_PNT_CD"		, "" );
			jrParam.setField("ARR_WLOC_CD"			, gdReq.getParam("SPOS_WLOC_CD") );
			jrParam.setField("ARR_YD_PNT_CD"		, "" );
			jrParam.setField("TRN_WRK_FULLVOID_GP"	, "E" );
//			jrParam.setField("YD_WO_CNCL_YN"		, "N" );
//			jrParam.setField("L3_HMI"				, "Y" );   //백업화면 기동 여부
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("rcvTSYDJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, mthdNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}	

	
	/**
	 * 이송차량 실적처리 팝업 - 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtMvCarStatSet(GridData gdReq) throws DAOException {
		String mthdNm =  "이송차량 실적처리 팝업 - 등록[CCoilJspFaEJB.trtMvCarStatSet]";
		String logId = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
 
			commUtils.printLog(logId, mthdNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtMvCarStatSet", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, mthdNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}		
	
	
	/**
	 * 이송작업재료등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCarFtMvMtl(GridData gdReq) throws DAOException {
		String mthdNm =  "이송작업재료등록[CCoilJspFaEJB.updCarFtMvMtl]";
		String logId = commUtils.getLogId();
		
		try {

			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			commUtils.printLog(logId, mthdNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			
			ejbConn.trx("updCarFtMvMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, mthdNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}
	
	
	/**
	 * 이송작업재료삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delCarFtMvMtl(GridData gdReq) throws DAOException {
		String mthdNm =  "이송작업재료삭제[CCoilJspFaEJB.delCarFtMvMtl]";
		String logId = commUtils.getLogId();
		
		try {

			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			commUtils.printLog(logId, mthdNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			
			JDTORecord jrRst =  (JDTORecord)ejbConn.trx("delCarFtMvMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, mthdNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}
	

	/**
	 * 이송작업재료위치변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData chgCarFtMvMtl(GridData gdReq) throws DAOException {
		String mthdNm =  "이송작업재료위치변경[CCoilJspFaEJB.chgCarFtMvMtl]";
		String logId = commUtils.getLogId();
		
		try {

			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			commUtils.printLog(logId, mthdNm, "F+");
			
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			
			JDTORecord jrRst =  (JDTORecord)ejbConn.trx("chgCarFtMvMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, mthdNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}			
	
	
	
	/**
	 *  차량동간이적(도착백업) 송신 --[차량동간이적(도착)] 전문을 송신한다
	 * 염용선 2020-02-13
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */

	public GridData sendY5YDL018(GridData gdReq) throws JDTOException {
		String mthdNm = "차량동간이적(도착) 송신 [CCoilJspFaEJB.sendY5YDL018]" ;
		String logId = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

            EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("sendY5YDL018", new Class[] { GridData.class }, new Object[] { gdReq });
						
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnMsg:"+ rtnMsg, "FL");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "FL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			if (!"1".equals(rtnCd)) {		
				m_ctx.setRollbackOnly();
			} 	

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			//조회
						
			gdRes.setStatus(rtnCd);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("차량동간이적(도착백업)처리가 완료 됐습니다.");
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/** 
	 * 크레인작업 응답BackUp
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procCrnSchReqBackUp(GridData gdReq) throws DAOException {
		String mthdNm = "크레인작업 응답BackUp[CCoilJspFaEJB.procCrnSchReqBackUp]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procCrnSchReqBackUp", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("응답백업 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/** 
	 * 결로재 보급(임시 테스트용)
	 * 야드관리 > 2열연 소재 코일야드[신] > 산적LOT관리 > 결로HOT코일이적
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procConHotCoilInOut(GridData gdReq) throws DAOException {
		String mthdNm = "결로재보급/추출[CCoilJspFaEJB.procConHotCoilInOut]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			String sModifier  = commUtils.nvl(gdReq.getParam("YD_USER_ID"), "");
			
			JDTORecord jrParam  = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setResultMsg(mthdNm);
			jrParam.setResultCode(logId);
			jrParam.setField("IN_OUT_GP", commUtils.nvl(gdReq.getParam("IN_OUT_GP"), "1"));
			jrParam.setField("YD_BAY_GP", commUtils.trim(gdReq.getParam("CON_YD_BAY_GP")));
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procConHotCoilInOut", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);
				/*
				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
				*/
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("결로재 보급/추출 처리가 완료 되었습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/** 
	 * 2열연 기준관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updYdRuleMgt(GridData gdReq) throws DAOException {
		String mthdNm = "2열연기준관리-수정[CCoilJspFaEJB.updYdRuleMgt]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updYdRuleMgt", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("수정이 완료되었습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 * 2열연 기준관리
	 * 야드관리 > 2열연 코일야드[신] > 기준관리 > 2열연기준관리 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData insYdRuleMgt(GridData gdReq) throws JDTOException {
		String mthdNm = "2열연기준관리-등록[CCoilJspFaEJB.insYdRuleMgt]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("insYdRuleMgt", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);
				
				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
				
			}

			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRet.setMessage("기준관리 등록 처리가 완료 됐습니다.");
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}
	
	/**
	 * 2열연 설비기준관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updEqpOprnStat(GridData gdReq) throws DAOException {
		String mthdNm = "설비상태 변경[CCoilJspFaEJB.updEqpOprnStat]";
		String logId  = commUtils.getLogId();

		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updEqpOprnStat", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {

				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("설비상태를 변경하였습니다.");
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 * [A] 오퍼레이션명 : 이송지시 취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updFtmvWrkCancel(GridData gdReq) throws DAOException {
		String mthdNm =  "이송지시 취소[CCoilJspFaEJB.updFtmvWrkCancel]";
		String logId  = commUtils.getLogId();
		
		try {
			
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			commUtils.printLog(logId, mthdNm, "F+");
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//이송지시 취소(1) - YDPTJ007 전문 저송
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updFtmvWrkCancel", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + "updFtmvWrkCancel >> rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				
				//이송지시 취소(2) - 크레인스케줄,작업예약 ID 취소 처리
				jrRtn = (JDTORecord)ejbConn.trx("updFtmvWrkCancel2", new Class[] { GridData.class }, new Object[] { gdReq });
				 rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
				 rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
				commUtils.printLog(logId, mthdNm + "updFtmvWrkCancel2 >> rtnCd:"+ rtnCd, "SL");
	
				//전송할 Data가 있으면 전송 처리
				// ROLLBACK 시 전문 발생
				if (!"0".equals(rtnCd)) {
					
					jrRtn.setResultCode(logId);
					jrRtn.setResultMsg(mthdNm);
	
					EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
				}
				
				if (!"1".equals(rtnCd)) {
					gdRet.setMessage(rtnMsg);		
					m_ctx.setRollbackOnly();
				} else {
					gdRet.setMessage("이송지시 취소 작업이 완료되었습니다.");	
				}
			
			}
			commUtils.printLog(logId, mthdNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}	
	
	
	/**
	 *      [A] 오퍼레이션명 : 권하위치변경가능 위치
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData
	 *      @return GridData
	 *      @throws DAOException
     */			
	public GridData getDownLocChange(GridData gdReq) throws DAOException {
		String mthdNm = "권하위치변경가능 위치 - [CCoilJspFaEJB.getDownLocChange]";
		String logId  = commUtils.getLogId();
		
		try {
			
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			commUtils.printLog(logId, mthdNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("getDownLocChange", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}

	}	
	
	/**
	 * 지포장 추출
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procGPackOutReq(GridData gdReq) throws DAOException {
		String mthdNm = "지포장추출[CCoilJspFaEJB.procGPackOutReq]";
		String logId  = commUtils.getLogId();

		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procGPackOutReq", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {

				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("지포장 추출요구를 하였습니다.");
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	
	/**
	 * 대차 정보 수정
	 * 염용선
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updTcInfo(GridData gdReq) throws DAOException {
		String mthdNm = "대차 정보 수정[CCoilJspFaEJB.updTcInfo]";
		String logId  = commUtils.getLogId();

		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updTcInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {

				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			} else {
				
				EJBConnector ejbConnE = new EJBConnector("default", "CCoilJspSeEJB", this);
				JDTORecord jrRtnE = (JDTORecord)ejbConnE.trx("procTcarStsSetTcarE", new Class[] { GridData.class }, new Object[] { gdReq });
				 rtnCd	 		= commUtils.nvl(jrRtnE.getFieldString("RTN_CD"), "0");
				 rtnMsg	 		= commUtils.nvl(jrRtnE.getFieldString("RTN_MSG"), "");
				commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
				if (!"0".equals(rtnCd)) {
					
					jrRtn.setResultCode(logId);
					jrRtn.setResultMsg(mthdNm);

					EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
					gdRet.setMessage("대차정보 수정을 완료 하였습니다.");
				}else{
					gdRet.setMessage(rtnMsg);
				}
				
				
				
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 반송지시등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updRetnTgMgt(GridData gdReq) throws DAOException {
		String mthdNm = "반송지시등록[CCoilJspFaEJB.updRetnTgMgt]";
		String logId  = commUtils.getLogId();

		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updRetnTgMgt", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {

				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage(rtnMsg);
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 차량작업관리 - 차량동간이적POINT SPAN 범위 설정
	 * 야드관리 > 2열연 코일야드[신] > 출하이송관리 > 차량작업관리
	 * 강정선 2019.11.08
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCarSpanRngApp008(GridData gdReq) throws DAOException {
		String mthdNm = "차량포인트SPAN범위설정[CCoilJspFaEJB.updCarSpanRngApp008]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCarSpanRngApp008", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			

			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("범위설정 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 * 코일소재야드 tracking 팝업 조회보급등록
	 * * SPM/HFL입측관리 > 보급 > 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData inscoilYdLineWr(GridData gdReq) throws DAOException {
		String mthdNm = "소재입측 보급[CCoilJspFaEJB.inscoilYdLineWr]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");		
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);	
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("inscoilYdLineWr", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd  = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			if (!"1".equals(rtnCd)) {		
				m_ctx.setRollbackOnly();
				gdRes.setMessage(rtnMsg);
			}else{
				gdRes.setMessage("보급처리가 완료 됐습니다.");
			}

			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}  
	
	/**
	 * 코일소재야드 tracking 팝업 조회보급등록
	 * * SPM/HFL입측관리 > 추출
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData inscoilGdsYdLineWr(GridData gdReq) throws DAOException {
		String mthdNm = "추출 등록[CCoilJspFaEJB.inscoilGdsYdLineWr]";
		String logId  = commUtils.getLogId();
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");		
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID
			
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);	
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);			
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("inscoilGdsYdLineWr", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd  = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			}
			if (!"1".equals(rtnCd)) {		
				m_ctx.setRollbackOnly();
				gdRes.setMessage(rtnMsg);
			}else{
				gdRes.setMessage("추출처리가 완료 됐습니다.");
			}

			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}  	
	
	/**
	 * 소재위치별적치현황조회 - BED 활성상태 변경
	 * 야드관리 > 2열연 소재 코일야드[신] > 야드현황관리 > 위치별적치현황조회
	 * 강정선 2020.06.01
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updBedActStat(GridData gdReq) throws DAOException {
		String mthdNm = "BED활성상태변경[CCoilJspFaEJB.updBedActStat]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updBedActStat", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("BED상태 수정 처리가 완료 됐습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 이적대상재조회(야드간이적) - 이적지시등록
	 * 야드관리 > 2열연 소재 코일야드[신] > 산적LOT관리 > 이적대상재조회
	 * 강정선 2020.06.05
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData insUnitMvYdLocGp(GridData gdReq) throws DAOException {
		String mthdNm = "이적지시등록[CCoilJspFaEJB.insUnitMvYdLocGp]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("insUnitMvYdLocGp", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("이적지시등록이 완료 되었습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 * 공냉재 이적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procAirclMtlMvstk(GridData gdReq) throws DAOException {
		String mthdNm = "공냉재이적지시등록[CCoilJspFaEJB.procAirclMtlMvstk]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procAirclMtlMvstk", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("공냉재이적등록이 완료 되었습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
		
	/**
	 * 코일이송재료LIST - 긴급작업
	 * 강정선 2020.06.19
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updUgntWork(GridData gdReq) throws DAOException {
		String mthdNm = "긴급작업[CCoilJspFaEJB.updUgntWork]";
		String logId  = commUtils.getLogId();

		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updUgntWork", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {

				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("긴급작업등록이 완료 되었습니다.");
			}

			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 * 코일이송재료LIST - 스케줄기동
	 * 강정선 2020.06.19
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqCarLdSchRun(GridData gdReq) throws DAOException {
		String mthdNm = "스케줄기동[CCoilJspFaEJB.reqCarLdSchRun]";
		String logId  = commUtils.getLogId();

		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("reqCarLdSchRun", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {

				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("스케줄기동이 완료 되었습니다.");
			}

			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 코일제품차량작업 관리- 포인트 변경
	 * 야드관리 > 2열연 제품 코일야드[신] > 출하관리 > 배차차량작업관리
	 * 이묘원 2020.06.18
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  gdReq
	 * @return gdRet
	 * @throws JDTOException
	 */
	public GridData changeCarLoc(GridData gdReq) throws JDTOException {
		String mthdNm = "차량입동위치변경[CCoilJspFaEJBSBean.changeCarLoc]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("changeCarLoc", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage("차량입동포인트 변경이 완료 되었습니다.");
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 적치위치변경
	 * 야드관리 > 2열연 소재/제품 코일야드[신] > 기준관리 > 소재/제품 적치위치변경  등록
	 * 김호연 2020.06.23
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStrlocChgSet(GridData gdReq) throws DAOException {
		String mthdNm = "적치위치변경  등록[CCoilJspFaEJB.updStrlocChgSet]";
		String logId  = commUtils.getLogId();
		
		try{
			
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updStrlocChgSet", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);
				
				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("적치위치변경이 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 결로저장위치 ON/OFF
	 * 야드관리 > 2열연 제품 코일야드[신] > 기준관리 > 저장위치좌표설정  
	 * 강정선 2020.06.25
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updYdRuleCondYn(GridData gdReq) throws DAOException {
		String mthdNm = "결로저장위치 ON/OFF[CCoilJspFaEJB.updYdRuleCondYn]";
		String logId  = commUtils.getLogId();

		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updYdRuleCondYn", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {

				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage(rtnMsg);
			}

			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 결로저장위치 ON/OFF
	 * 야드관리 > 2열연 제품 코일야드[신] > 기준관리 > 저장위치좌표설정  
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updYdRuleCondYnNew(GridData gdReq) throws DAOException {
		String mthdNm = "결로저장위치 ON/OFF[CCoilJspFaEJB.updYdRuleCondYn]";
		String logId  = commUtils.getLogId();

		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updYdRuleCondYnNew", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {

				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			}else{
				gdRes.setMessage(rtnMsg);
			}

			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 입동지시 SMS 전송
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procBayInWoSmsSend(GridData gdReq) throws DAOException {
		String mthdNm = "입동지시 SMS 전송[CCoilJspFaEJB.procBayInWoSmsSend]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn		= (JDTORecord)ejbConn.trx("procBayInWoSmsSend", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd			= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg			= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage(rtnMsg);	
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 설비보급스케쥴 기동/금지
	 * 야드관리 > 2열연 소재 코일야드[신] > 설비입측관리 > SPM/HFL입측관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updLineInSchProhExn(GridData gdReq) throws DAOException {
		String mthdNm = "설비보급스케쥴 기동/금지[CCoilJspFaEJB.updLineInSchProhExn]";
		String logId  = commUtils.getLogId();

		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updLineInSchProhExn", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생 안함
			if (!"0".equals(rtnCd)) {

				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes.setMessage(rtnMsg);
			
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 지포장 이적등록
	 * 야드관리 > 2열연 소재 코일야드[신] > 설비입측관리 > 지포장보급관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regGFUnitMvStkH(GridData gdReq) throws DAOException {
		String mthdNm = "지포장 이적등록[CCoilJspFaEJB.regGFUnitMvStkH]";
		String logId  = commUtils.getLogId();

		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("regGFUnitMvStkH", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생 안함
			if (!"0".equals(rtnCd)) {

				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes.setMessage(rtnMsg);
			
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
		
	/**
	 * 입고스케줄 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procChgRcptSch(GridData gdReq) throws DAOException {
		String mthdNm = "입고스케줄 변경[CCoilJspFaEJB.procChgRcptSch]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn		= (JDTORecord)ejbConn.trx("procChgRcptSch", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("입고스케줄 변경처리가 완료 됐습니다.");	
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	
	
	/**
	 * 결속장 스케쥴 기동/금지
	 * 야드관리 > 2열연 소재 코일야드[신] > 설비입측관리 > SPM/HFL입측관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updLineInSupMtdGp(GridData gdReq) throws DAOException {
		String mthdNm = "결속장 기동/금지[CCoilJspFaEJB.updLineInSupMtdGp]";
		String logId  = commUtils.getLogId();

		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updLineInSupMtdGp", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생 안함
			if (!"0".equals(rtnCd)) {

				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes.setMessage(rtnMsg);
			
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 * 소재 결로HOT코일이적
	 * 야드관리 > 2열연 소재 코일야드[신] > 산적LOT관리 > 결로HOT코일이적
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCondenMvStkReg(GridData gdReq) throws DAOException {
		String mthdNm = "결로HOT코일이적[CCoilJspFaEJB.updCondenMvStkReg]";
		String logId  = commUtils.getLogId();

		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCondenMvStkReg", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생 안함
			if (!"0".equals(rtnCd)) {

				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}

			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes.setMessage(rtnMsg);
			
			if (!"1".equals(rtnCd)) {
				m_ctx.setRollbackOnly();
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	/** 
	 * 장기공냉재 자동이적 처리
	 * 야드관리 > 2열연 소재 코일야드[신] > jsp
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procHotCoilMove(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "장기공냉재 자동이적 처리[CCoilJspFaEJB.procHotCoilMove]"+ rcvMsg.getResultMsg();
		String logId  = commUtils.getLogId();
		
		try {
			commUtils.printLog(logId, mthdNm, "F+");
			
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); 
			
			JDTORecord jrParam  = commUtils.getParam(logId, mthdNm, sModifier);
			JDTORecord jrRtn    = commUtils.getParam(logId, mthdNm, sModifier);

			String rtnCd	 = "";
			String rtnMsg	 = "";
			
			commUtils.printLog(logId, mthdNm + "sModifier"+ sModifier, "SL");
			
			jrParam.setField("MODIFIER" , sModifier);
			jrParam.setField("YD_BAY_GP", "A");
			EJBConnector ejbConn0 = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn0.trx("procHotCoilMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm +"A"+ "동 rtnCd:"+ rtnCd, "SL");
			
			jrParam.setField("MODIFIER" , sModifier);
			jrParam.setField("YD_BAY_GP", 'B');
			EJBConnector ejbConn1 = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn1.trx("procHotCoilMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm +'B'+ "동 rtnCd:"+ rtnCd, "SL");
			
			jrParam.setField("MODIFIER" , sModifier);
			jrParam.setField("YD_BAY_GP", "C");
			EJBConnector ejbConn2 = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn2.trx("procHotCoilMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm +"C"+ "동 rtnCd:"+ rtnCd, "SL");
			
			jrParam.setField("MODIFIER" , sModifier);
			jrParam.setField("YD_BAY_GP", "D");
			EJBConnector ejbConn3 = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn3.trx("procHotCoilMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm +"D"+ "동 rtnCd:"+ rtnCd, "SL");
			
			jrParam.setField("MODIFIER" , sModifier);
			jrParam.setField("YD_BAY_GP", "E");
			EJBConnector ejbConn4 = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn4.trx("procHotCoilMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm +"E"+ "동 rtnCd:"+ rtnCd, "SL");
			 
			jrParam.setField("MODIFIER" , sModifier);
			jrParam.setField("YD_BAY_GP", "F");
			EJBConnector ejbConn5 = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn5.trx("procHotCoilMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm +"F"+ "동 rtnCd:"+ rtnCd, "SL");
			 
			
			jrParam.setField("MODIFIER" , sModifier);
			jrParam.setField("YD_BAY_GP", "G");
			EJBConnector ejbConn6 = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn6.trx("procHotCoilMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm +"G"+ "동 rtnCd:"+ rtnCd, "SL");
			 
			
			jrParam.setField("MODIFIER" , sModifier);
			jrParam.setField("YD_BAY_GP", "H");
			EJBConnector ejbConn7 = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn7.trx("procHotCoilMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm +"H"+ "동 rtnCd:"+ rtnCd, "SL");
			 
			
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);
				/*
				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
				*/
			} 
			
			//화면 메시지
			if (!"1".equals(rtnCd)) { 		
				m_ctx.setRollbackOnly();
			} else {
				commUtils.printLog(logId, mthdNm +"공냉재자동이적처리가 완료 되었습니다.", "SL"); 
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**	
	 * 공냉재입고자동 처리
	 * 야드관리 > 2열연 소재 코일야드[신] > jsp
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procJicMove(JDTORecord jrRtn) throws DAOException {
		String mthdNm = "공냉재입고자동 처리[CCoilJspFaEJB.procJicMove]"+ jrRtn.getResultMsg();;
		String logId  = commUtils.getLogId();
		
		try {
			 commUtils.printLog(logId, mthdNm, "F+");
			 
			 String sModifier = commUtils.trim(jrRtn.getFieldString("MODIFIER")); 
			
			JDTORecord jrParam  = commUtils.getParam(logId, mthdNm, sModifier); 
			
			commUtils.printLog(logId, mthdNm + "sModifier"+ sModifier, "SL");
			
			String rtnCd	 = "";
			String rtnMsg	 = "";
			
			jrParam.setField("MODIFIER" , sModifier);
			jrParam.setField("YD_BAY_GP", "A");
			EJBConnector ejbConn0 = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn0.trx("procJicMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + "A"+"동 rtnCd:"+ rtnCd, "SL"); 
			
			jrParam.setField("MODIFIER" , sModifier);
			jrParam.setField("YD_BAY_GP", "B");
			EJBConnector ejbConn1 = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn1.trx("procJicMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + "B"+"동 rtnCd:"+ rtnCd, "SL"); 
			
			jrParam.setField("MODIFIER" , sModifier);
			jrParam.setField("YD_BAY_GP", "C");
			EJBConnector ejbConn2 = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn2.trx("procJicMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + "C"+"동 rtnCd:"+ rtnCd, "SL"); 
			
			jrParam.setField("MODIFIER" , sModifier);
			jrParam.setField("YD_BAY_GP", "D");
			EJBConnector ejbConn3 = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn3.trx("procJicMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + "D"+"동 rtnCd:"+ rtnCd, "SL"); 
			
			jrParam.setField("MODIFIER" , sModifier);
			jrParam.setField("YD_BAY_GP", "E");
			EJBConnector ejbConn4 = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn4.trx("procJicMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + "E"+"동 rtnCd:"+ rtnCd, "SL"); 
			
			jrParam.setField("MODIFIER" , sModifier);
			jrParam.setField("YD_BAY_GP", "F");
			EJBConnector ejbConn5 = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn5.trx("procJicMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + "F"+"동 rtnCd:"+ rtnCd, "SL"); 
			
			jrParam.setField("MODIFIER" , sModifier);
			jrParam.setField("YD_BAY_GP", "G");
			EJBConnector ejbConn6 = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn6.trx("procJicMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + "G"+"동 rtnCd:"+ rtnCd, "SL"); 
			
			jrParam.setField("MODIFIER" , sModifier);
			jrParam.setField("YD_BAY_GP", "H");
			EJBConnector ejbConn7 = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn7.trx("procJicMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + "H"+"동 rtnCd:"+ rtnCd, "SL"); 
			 
			
			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);
				/*
				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
				*/
			} 
			
			//화면 메시지
			if (!"1".equals(rtnCd)) { 		
				m_ctx.setRollbackOnly();
			} else {
				commUtils.printLog(logId, mthdNm +"공냉재자동입고처리가 완료 되었습니다.", "SL"); 
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}		
	
	/**	
	 * 보급존 자동이적(테스트용)
	 * 야드관리 > 2열연 소재 코일야드[신] > 기준관리 > 2열연기준관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procSupplyZoneMove(GridData gdReq) throws DAOException {
		String mthdNm = "보급존 자동이적[CCoilJspFaEJB.procSupplyZoneMove]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			String sModifier  = commUtils.nvl(gdReq.getParam("YD_USER_ID"), "");
			
			JDTORecord jrParam  = commUtils.getParam(logId, mthdNm, sModifier);
			JDTORecord jrRtn    = commUtils.getParam(logId, mthdNm, sModifier);

			String rtnCd	 = "";
			String rtnMsg	 = "";
			
			//jrParam.setField("MODIFIER" , "SupMove");
			EJBConnector ejbConn1 = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn1.trx("procSupplyZoneMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + "rtnCd:"+ rtnCd +"/"+rtnMsg, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);
				/*
				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
				*/
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("보급존 자동이적 처리완료.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}		

	/**	
	 * 결로방지용 HOT COIL 반납장으로 자동이적
	 * 야드관리 > 2열연 소재 코일야드[신] > 산적LOT관리 > 결로HOT코일이적
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procHotCoilRetnMove(GridData gdReq) throws DAOException {
		String mthdNm = "결로재HOTCOIL 반납장 자동이적[CCoilJspFaEJB.procHotCoilRetnMove]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			String sModifier  = commUtils.nvl(gdReq.getParam("YD_USER_ID"), "");
			
			JDTORecord jrParam  = commUtils.getParam(logId, mthdNm, sModifier);
			JDTORecord jrRtn    = commUtils.getParam(logId, mthdNm, sModifier);

			jrParam.setField("YD_BAY_GP", commUtils.nvl(gdReq.getParam("YD_BAY_GP"), ""));
			
			String rtnCd	 = "";
			String rtnMsg	 = "";
			
			//jrParam.setField("MODIFIER" , "SupMove");
			EJBConnector ejbConn1 = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn1.trx("procHotCoilRetnMove", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + "rtnCd:"+ rtnCd +"/"+rtnMsg, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);
				/*
				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
				*/
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("결로HOT코일 반납장 자동이적 등록하였습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 공냉재자동이적 ON/OFF
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updAutoAriClMoveMgt(GridData gdReq) throws DAOException {
		String mthdNm	= "공냉재자동이적 ON/OFF[CCoilJspFaEJB.updAutoAriClMoveMgt]";
		String logId	= commUtils.getLogId();	
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updAutoAriClMoveMgt", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				String sMsg = "ON";
				if( "N".equals(gdReq.getParam("MODE") ) ) {
					sMsg = "OFF";
				}
				gdRet.setMessage("공냉재자동이적 시스템 "+ sMsg +" 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * C-HOOK 자동추출 시간설정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCHookAutoOutMgt(GridData gdReq) throws DAOException {
		String mthdNm	= "C-HOOK 자동추출 시간설정[CCoilJspFaEJB.updCHookAutoOutMgt]";
		String logId	= commUtils.getLogId();	
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCHookAutoOutMgt", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {

				gdRet.setMessage("C-HOOK 자동추출 시간변경 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**	
	 * C-HOOK 자동추출 스케쥴생성
	 * 야드관리 > 2열연 소재 코일야드[신] > 기준관리 > 2열연기준관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procCHookAutoOut(GridData gdReq) throws DAOException {
		String mthdNm = "C-HOOK 자동추출 스케쥴생성[CCoilJspFaEJB.procCHookAutoOut]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			String sModifier  = commUtils.nvl(gdReq.getParam("YD_USER_ID"), "");
			
			JDTORecord jrParam  = commUtils.getParam(logId, mthdNm, sModifier);
			JDTORecord jrRtn    = commUtils.getParam(logId, mthdNm, sModifier);

			String rtnCd	 = "";
			String rtnMsg	 = "";
			
			//jrParam.setField("MODIFIER" , "SupMove");
			EJBConnector ejbConn1 = new EJBConnector("default", "CCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn1.trx("procCHookAutoOut", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + "rtnCd:"+ rtnCd +"/"+rtnMsg, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("C-HOOK 자동추출 스케쥴생성 완료.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} 
	
	
	/** 
	 * 출하분산코일 기준관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updYdRuleDspr(GridData gdReq) throws DAOException {
		String mthdNm = "출하분산코일-수정[CCoilJspFaEJB.updYdRuleDspr]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updYdRuleDspr", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("수정이 완료되었습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	
	/** 
	 * 출하코일 분산관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procDmCoilDsprMgt(GridData gdReq) throws DAOException {
		String mthdNm = "출하코일 분산관리[CCoilJspFaEJB.procDmCoilDsprMgt]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			String userid		= commUtils.trim(gdReq.getParam("YD_USER_ID"));
			JDTORecord jrParam	= commUtils.getParam(logId, mthdNm, userid);
			
			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procDmCoilDsprMgt", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
	
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 스크랩 비우기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procClearScrap(GridData gdReq) throws DAOException {
		String mthdNm =  "스크랩현황조회-스크랩비우기[CCoilJspFaEJB.procClearScrap]";
		String logId  = commUtils.getLogId();
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn		= (JDTORecord)ejbConn.trx("procClearScrap", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("스크랩 비우기 처리가 완료 됐습니다.");	
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 스크랩 생성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procCreateScrap(GridData gdReq) throws DAOException {
		String mthdNm =  "스크랩현황조회-스크랩생성[CCoilJspFaEJB.procCreateScrap]";
		String logId  = commUtils.getLogId();
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn		= (JDTORecord)ejbConn.trx("procCreateScrap", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("스크랩 생성 처리가 완료 됐습니다.");	
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	// 
	/**
	 *      스크랩 차량 진입여부 변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData
	 *      @return GridData
	 *      @throws DAOException
     */	
	public GridData updScrpCar(GridData gdReq) throws DAOException {
		String mthdNm = "스크랩 차량 진입여부 변경[CCoilJspFaEJBupdScrpCar]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn		= (JDTORecord)ejbConn.trx("updScrpCar", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			// 조회
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("스크랩 차량 진입여부 변경 처리가 완료 됐습니다.");	
			}
			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 * 기준관리 - 세부항목수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updYdRuleScrap(GridData gdReq) throws DAOException {
		String mthdNm = "스크랩기준 수정[CCoilJspFaEJB.updYdRuleScrap]";
		String logId    = commUtils.getLogId();
		
		try {

			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn	 = (JDTORecord)ejbConn.trx("updYdRuleScrap", new Class[] { GridData.class }, new Object[] { gdReq });

			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			// 조회
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("자동이적/수동이적 변경처리가  완료 됐습니다.");	
			}
			
			commUtils.printLog(logId, mthdNm, "F-");

			//조회결과
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	

	/**
	 *      [A] 오퍼레이션명 : Scrap이적작업 예약 등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData updScrapMvStkWrkBook(GridData gdReq) throws DAOException {
		String mthdNm = "Scrap이적작업 예약 등록[CCoilJspFaEJB.updScrapMvStkWrkBook]";
		String logId  = commUtils.getLogId();

		try {
			
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updScrapMvStkWrkBook", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			//ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			// 조회
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			if (!"1".equals(rtnCd)) {
				gdRes.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRes.setMessage("스크랩 차량 진입여부 변경 처리가 완료 됐습니다.");	
			}

			commUtils.printLog(logId, mthdNm, "F-");

			//조회결과
			return gdRes;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/** 
	 * 제품이송하차 입동제한 기준관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updGdsFtmvCarudStdMgt(GridData gdReq) throws DAOException {
		String mthdNm = "제품이송하차 입동제한 기준관리[CCoilJspFaEJB.updGdsFtmvCarudStdMgt]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updGdsFtmvCarudStdMgt", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("수정이 완료되었습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 * 공냉재 HFL재 변경 이적지시
	 * 야드관리 > 2열연 소재 코일야드[신] > 산적LOT관리 > 공냉재이적대상재조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procAirClHflMoveReg(GridData gdReq) throws DAOException {
		String mthdNm = "공냉재 HFL재 변경 이적지시[CCoilJspFaEJB.procAirClHflMoveReg]";
		String logId  = commUtils.getLogId();

		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn1 = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn1.trx("procAirClHflMoveReg", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + "rtnCd:"+ rtnCd +"/"+rtnMsg, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {

				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("공냉재 이적지시 완료 되었습니다.");
			}
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 * 화면 도움말 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData setPageHelpInfo(GridData gdReq) throws DAOException {
		String mthdNm =  "화면 도움말 등록[CCoilJspFaEJB.setPageHelpInfo]";
		String logId = commUtils.getLogId();
		
		try {

			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			ejbConn.trx("setPageHelpInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}	// end of insEqpTrblReg
	
	/**
	 * 화면 도움말 - 버튼등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData setPageHelpBtnInfo(GridData gdReq) throws DAOException {
		String mthdNm =  "화면 도움말 - 버튼등록[CCoilJspFaEJB.setPageHelpBtnInfo]";
		String logId = commUtils.getLogId();
		
		try {

			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			ejbConn.trx("setPageHelpBtnInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}	// end of setPageHelpBtnInfo
	
	
	/**
	 * 화면 도움말 - 작업방법(버튼상세) 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData setPageHelpBtnDtlInfo(GridData gdReq) throws DAOException {
		String mthdNm =  "화면 도움말 - 작업방법(버튼상세) 등록[CCoilJspFaEJB.setPageHelpBtnDtlInfo]";
		String logId = commUtils.getLogId();
		
		try {

			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			ejbConn.trx("setPageHelpBtnDtlInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}	// end of setPageHelpBtnDtlInfo
	
	/**
	 * 화면 도움말 - 신규 문서번호 채번
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public JDTORecord getPageHelpDocMaxDocSeq(JDTORecord inRecord) throws DAOException {
		String mthdNm =  "화면 도움말 - 신규 문서번호 채번[CCoilJspFaEJB.getPageHelpDocMaxDocSeq]";
		String logId = commUtils.getLogId();
		JDTORecord outRecord = null;
		try {

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("getPageHelpDocMaxDocSeq", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			
			return outRecord;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}	// end of getPageHelpDocMaxDocSeq
	
	
	/**
	 * 화면 도움말 - 첨부문서 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public JDTORecord setPageHelpDoc(JDTORecord inRecord) throws DAOException {
		String mthdNm =  "화면 도움말 - 첨부문서 등록[CCoilJspFaEJB.setPageHelpDoc]";
		String logId = commUtils.getLogId();
		JDTORecord outRecord = null;
		try {
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("setPageHelpDoc", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			
			return outRecord;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}	// end of setPageHelpDoc
	
	/**
	 * 화면 도움말 - 첨부문서 삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public JDTORecord delPageHelpDoc(JDTORecord inRecord) throws DAOException {
		String mthdNm =  "화면 도움말 - 첨부문서 등록[CCoilJspFaEJB.setPageHelpDoc]";
		String logId = commUtils.getLogId();
		JDTORecord outRecord = null;
		try {
			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("delPageHelpDoc", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			
			return outRecord;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
		
	}	// end of setPageHelpDoc	
	
	/**
	 * 수입 검색 시작열 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updLocSrchCvRule(GridData gdReq) throws DAOException {
		String mthdNm	= "수입 검색 시작열 변경[CCoilJspFaEJB.updLocSrchCvRule]";
		String logId	= commUtils.getLogId();	
		
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updLocSrchCvRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);
				m_ctx.setRollbackOnly();
			} else {

				gdRet.setMessage("수입 검색 시작열이 수정 되었습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *  정정설비 난방코일 보급 ON/OFF
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updSpmWarmOnOff(GridData gdReq) throws DAOException {
		String mthdNm	= "정정설비 난방코일 보급 ON/OFF[CCoilJspFaEJB.updSpmWarmOnOff]";
		String logId	= commUtils.getLogId();	
		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");

			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updSpmWarmOnOff", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 		= commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 		= commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				String sMsg = "ON";
				if( "N".equals(gdReq.getParam("MODE") ) ) {
					sMsg = "OFF";
				}
				gdRet.setMessage("난방코일 보급 "+ sMsg +" 처리가 완료 됐습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 야드반납공냉재 진도코드변경
	 * exbuilder 페이지 처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */ 
	public GridData updCoilYdAirProcSet(GridData gdReq) throws DAOException {
		String mthdNm = "야드반납공냉재 진도코드변경[CCoilJspFaEJB.updCoilYdAirProcSet]";
		String logId  = commUtils.getLogId();

		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updCoilYdAirProcSet", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			if (!"1".equals(rtnCd)) {		
				m_ctx.setRollbackOnly();
			}else{ 	
			  EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
			  sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			gdRes.setMessage(rtnMsg);
			commUtils.printLog(logId, mthdNm, "F-");
			//this.getSelectData(gdReq);
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}		

	} 
	/**
	 * 공냉재자동처리 사용유무 샛팅
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */ 
	public GridData updateGongSupSetH(GridData gdReq) throws DAOException {
		String mthdNm = "공냉재자동처리 사용유무[CCoilJspFaEJB.updateGongSupSetH]";
		String logId  = commUtils.getLogId();

		try{
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdReq.setNavigateValue(mthdNm); //상위 Method 명
			gdReq.setIPAddress(logId); //Logging 을 위한 ID

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updateGongSupSetH", new Class[] { GridData.class }, new Object[] { gdReq });
			
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");
			
			if (!"1".equals(rtnCd)) {		
				m_ctx.setRollbackOnly();
			}
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			gdRes.setMessage(rtnMsg);
			commUtils.printLog(logId, mthdNm, "F-");
			//this.getSelectData(gdReq);
			return gdRes;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}		

	} 
	
	/** 
	 * 2열연 기준관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updateGongSupBaySetH(GridData gdReq) throws DAOException {
		String mthdNm = "2열연기준관리-수정[CCoilJspFaEJB.updateGongSupBaySetH]";
		String logId  = commUtils.getLogId();
		
		try {
			mthdNm = mthdNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, mthdNm, "F+");
			
			gdReq.setNavigateValue(mthdNm); // 상위 Method 명
			gdReq.setIPAddress(logId);      // Logging 을 위한 ID

			EJBConnector ejbConn	= new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updateGongSupBaySetH", new Class[] { GridData.class }, new Object[] { gdReq });
			String rtnCd	 = commUtils.nvl(jrRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	 = commUtils.nvl(jrRtn.getFieldString("RTN_MSG"), "");
			commUtils.printLog(logId, mthdNm + " rtnCd:"+ rtnCd, "SL");

			// ROLLBACK 시 전문 발생
			if (!"0".equals(rtnCd)) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(mthdNm);

				EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			//화면 메시지
			if (!"1".equals(rtnCd)) {
				gdRet.setMessage(rtnMsg);		
				m_ctx.setRollbackOnly();
			} else {
				gdRet.setMessage("수정이 완료되었습니다.");	
			}			
			commUtils.printLog(logId, mthdNm, "F-");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
}
