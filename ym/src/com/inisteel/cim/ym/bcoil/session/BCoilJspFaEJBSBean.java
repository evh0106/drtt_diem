/**
 * @(#)BCoilJspFaEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      B열연 COIL 야드 화면관리 Facade EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.ym.bcoil.session; 

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.bcommon.util.YmConstant;
/**
 *      [A] 클래스명 : B열연 COIL 야드 화면관리 Facade EJB
 *
 * @ejb.bean name="BCoilJspFaEJB" jndi-name="BCoilJspFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class BCoilJspFaEJBSBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YmCommUtils commUtils = new YmCommUtils();
	private YmCommDAO commDao = new YmCommDAO();
	private YmComm YmComm = new YmComm();
	private String szSessionName = getClass().getName();
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	/**
	 * GridData - 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getSelectData(GridData gdReq) throws DAOException {
		String methodNm =  "조회[BCoilJspFaEJB.getSelectData]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
			                    + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of getSelectData	

	/**
	 * 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSelectData(JDTORecord recPara) throws DAOException {
		String methodNm =  "조회[BCoilJspFaEJB.getSelectData]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) 
			                    + "("   + commUtils.trim(recPara.getFieldString("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(recPara.getFieldString("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", recPara);

			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecordSet recordSet = (JDTORecordSet)ejbConn.trx("getSelectData", new Class[] { JDTORecord.class }, new Object[] { recPara });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return recordSet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of getSelectData	

	/**
	 * IFTest Layout 변경 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updIfTestData(GridData gdReq) throws DAOException {
		String methodNm =  "IFTest Layout 변경[BCoilJspFaEJB.updIfTestData]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//IFTest Layout 변경
			ejbConn.trx("updIfTestData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updIfTestData
	
	/**
	 * IFTest 전송 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData sndIfTest(GridData gdReq) throws DAOException {
		String methodNm =  "IFTest 전송[BCoilJspFaEJB.sndIfTest]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//IFTest 전송
			ejbConn.trx("sndIfTest", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of sndIfTest	
	
	/**
	 * IFTest EAI전송
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData sndIfTestEAI(GridData gdReq) throws DAOException {
		String methodNm =  "IFTest EAI전송[BCoilJspFaEJB.sndIfTestEAI]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//IFTest EAI전송
			GridData gdRet = (GridData) ejbConn.trx("sndIfTestEAI", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of sndIfTestEAI
	
	/**
	 * 설비상태 (변경 설비기준조회 )
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updEqpOprnStat(GridData gdReq) throws DAOException {
		String methodNm =  "설비상태 변경[BCoilJspFaEJB.updEqpOprnStat]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//설비상태 변경
			ejbConn.trx("updEqpOprnStat", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updIfTestData
	
	/**
	 * 야드설비정비등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData insEqpTrblReg(GridData gdReq) throws DAOException {
		String methodNm =  "야드설비정비등록[BCoilJspFaEJB.insEqpTrblReg]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//야드설비정비상태 변경
			ejbConn.trx("updEqpTrblReg", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//야드설비정비등록
			GridData gdRet = (GridData)ejbConn.trx("insEqpTrblReg", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of insEqpTrblReg
	
	
	/**
	 * 저장품 작업예약 호출
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData callLoadWbookInfo(GridData gdReq) throws DAOException {
		String methodNm =  "출하상차[BCoilJspFaEJB.callLoadWbookInfo]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//IFTest Layout 변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("callLoadWbookInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of callLoadWbookInfo
	
	/**
	 *  상차정보조회 - 상차위치 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData carLiftPosSet(GridData gdReq) throws DAOException {
		String methodNm =  "상차위치변경 [BCoilJspFaEJB.carLiftPosSet]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//상차위치 변경
			ejbConn.trx("carLiftPosSet", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//상차위치
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of carLiftPosSet
	
	/**
	 * 코일제품차량작업 관리- 포인트 개폐
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return gdRet
	 * @throws JDTOException
	 */
	public GridData procCoilYdGdsPntUnitCLCoil(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "차량작업 포인트 현황 - 포인트 개폐[BCoilJspFaEJB.procCoilYdGdsPntUnitCLCoil]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
	 
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procCoilYdGdsPntUnitCLCoil", new Class[] { GridData.class }, new Object[] { gdReq });
			
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
	}	//end of procCoilYdGdsPntUnitCLCoil 
	
	/**
	 * 코일제품차량작업 관리- 포인트 개폐
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return gdRet
	 * @throws JDTOException
	 */
	public GridData changeCarLoc(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "차량입동위치변경[BCoilJspFaEJBSBean.changeCarLoc]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
			
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("changeCarLoc", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
	}	//end of changeCarLoc 
	
	/**
	 * 코일제품차량작업 관리- 입동순서변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData procCoilYdGdsBayInWoSeqChangCoil(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "차량작업 포인트 현황 - 입동순서변경[BCoilJspFaEJB.procCoilYdGdsBayInWoSeqChangCoil]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
	 
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn =  new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procCoilYdGdsBayInWoSeqChangCoil", new Class[] { GridData.class }, new Object[] { gdReq });
			
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
	}	//end of procCoilYdGdsBayInWoSeqChangCoil 	
	/**
	 * 배차차량작업관리 - 차량입동요구
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData CarArrivalNEW(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "배차차량작업관리 - 차량입동요구[BCoilJspFaEJB.CarArrivalNEW]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("CarArrivalNEW", new Class[] { GridData.class }, new Object[] { gdReq });
			
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
	}	//end of CarArrivalNEW 	

	/**
	 * 차량 작업 관리 화면 :배차내역 초기화
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData updCarWrMgt(GridData gdReq) throws DAOException {
		String methodNm =  "배차차량작업관리 - 초기화[BCoilJspFaEJB.updCarWrMgt]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		 
		try{

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			String modifier  = commUtils.trim(gdReq.getParam("userid")); //수정자
			
			EJBConnector ejbConn 	= null;
			JDTORecord outRecord  	= commUtils.getParam(logId, methodNm, modifier);
			JDTORecord jrRst 		= JDTORecordFactory.getInstance().create();
			JDTORecord jrRtn 		= JDTORecordFactory.getInstance().create();
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			// PIDEV_S :병행가동용:PI_YD
//			String sPI_YD  = commUtils.nvl(gdReq.getParam("PI_YD"), "*");
//			String sApplyYnPI = commDao.ApplyYnPI("", "배차차량작업관리 - 초기화[BCoilJspFaEJB.updCarWrMgt]", "APPPI0", sPI_YD, "*");		

			for (int ii = 0; ii < rowCnt; ii++) {
				
				if("구내운송".equals(commUtils.getValue(gdReq, "YD_CAR_USE_NM", ii))) {

					outRecord.setField("TC_CODE"        	, "TSYDJ004"); 
					outRecord.setField("TRN_EQP_CD"			, commUtils.getValue(gdReq, "CAR_NO", ii));
					outRecord.setField("TRN_WRK_FULLVOID_GP", "E");
					outRecord.setField("BACKUP_YN"			, "Y");
					outRecord.setField("WLOC_CD"			, commUtils.getValue(gdReq, "WLOC_CD", ii));
						
					ejbConn = new EJBConnector("default", "YmCommCarMvSeEJB", this);
					jrRtn 	= (JDTORecord)ejbConn.trx("initCarSch", new Class[] { JDTORecord.class }, new Object[] { outRecord });
					jrRst 	= commUtils.addSndData(jrRst, jrRtn);
					
				} else {
					
//					if("Y".equals(sApplyYnPI)) {
						outRecord.setField("MQ_TC_CD"       , "M10LMYDJ1031"); 
						outRecord.setField("CURR_PROG_CD"	, "N"		);
						outRecord.setField("YD_GP"			, commUtils.getValue(gdReq, "YD_GP"				, ii));
						outRecord.setField("TRANS_ORD_DT"	, commUtils.getValue(gdReq, "TRANS_ORD_DT"		, ii));
						outRecord.setField("TRANS_ORD_SEQNO", commUtils.getValue(gdReq, "TRANS_ORD_SEQNO"	, ii));
						outRecord.setField("CAR_NO"			, commUtils.getValue(gdReq, "CAR_NO"			, ii));
						outRecord.setField("CARD_NO"		, commUtils.getValue(gdReq, "CARD_NO"			, ii));
						outRecord.setField("YD_CAR_SCH_ID"	, commUtils.getValue(gdReq, "YD_CAR_SCH_ID"		, ii));
						
						ejbConn = new EJBConnector("default", "YmCoilL3RcvPISeEJB", this);
						jrRtn 	= (JDTORecord)ejbConn.trx("receiveCancelPI", new Class[] { JDTORecord.class }, new Object[] { outRecord });
						jrRst 	= commUtils.addSndData(jrRst, jrRtn);
						
//					} else {
//						outRecord.setField("TC_CODE"        , "DMYDR060"); 
//						outRecord.setField("CURR_PROG_CD"	, "N"		);
//						outRecord.setField("YD_GP"			, commUtils.getValue(gdReq, "YD_GP"				, ii));
//						outRecord.setField("TRANS_ORD_DT"	, commUtils.getValue(gdReq, "TRANS_ORD_DT"		, ii));
//						outRecord.setField("TRANS_ORD_SEQNO", commUtils.getValue(gdReq, "TRANS_ORD_SEQNO"	, ii));
//						outRecord.setField("CAR_NO"			, commUtils.getValue(gdReq, "CAR_NO"			, ii));
//						outRecord.setField("CARD_NO"		, commUtils.getValue(gdReq, "CARD_NO"			, ii));
//						outRecord.setField("YD_CAR_SCH_ID"	, commUtils.getValue(gdReq, "YD_CAR_SCH_ID"		, ii));
//						
//						ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
//						jrRtn 	= (JDTORecord)ejbConn.trx("receiveCancel", new Class[] { JDTORecord.class }, new Object[] { outRecord });
//						jrRst 	= commUtils.addSndData(jrRst, jrRtn);
//						
//					}
				}
			}
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			 
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}  //end of updCarWrMgt
	
	/**
	 * 차량 작업 관리 화면 :대기장도착처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData getStandByYdArrive(GridData gdReq) throws DAOException {
		//		LOG
		String szMsg 			= "";
		String methodNm		= "getStandByYdArrive";
		String logId = gdReq.getIPAddress();
		GridData gdRes 			= null;
		EJBConnector ejbConn 	= null;
	 
 
		JDTORecord inRecord2  	= JDTORecordFactory.getInstance().create();	
		try{
			 
			
			szMsg = "[JSP Facade] 차량작업관리 대기장도착처리  전송처리 시작  ==>";
			commUtils.printLog(logId, szMsg, "SL");	
			JDTORecord jrRst = JDTORecordFactory.getInstance().create();	
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			// PIDEV_S :병행가동용:PI_YD
//			String sPI_YD  = commUtils.nvl(gdReq.getParam("PI_YD"), "*");
//			String sApplyYnPI = commDao.ApplyYnPI("", "배차차량작업관리 - 초기화[BCoilJspFaEJB.updCarWrMgt]", "APPPI0", sPI_YD, "*");		
			
			for (int ii = 0; ii < rowCnt; ii++) {
				inRecord2 	= JDTORecordFactory.getInstance().create();
				
//				if("Y".equals(sApplyYnPI)) {
					inRecord2.setField("YD_GP"				, commUtils.getValue(gdReq, "YD_GP", ii));
					inRecord2.setField("CMBN_CARLD_YN"		, commUtils.getValue(gdReq, "CMBN_CARLD_YN", ii));
					inRecord2.setField("WORK_GP"			, commUtils.getValue(gdReq, "WORK_GP", ii));
					inRecord2.setField("TEL_NO"				, commUtils.getValue(gdReq, "TEL_NUMBER", ii));
					inRecord2.setField("TRN_REQ_DATE"		, commUtils.getValue(gdReq, "TRANS_ORD_DT", ii));
					inRecord2.setField("TRN_REQ_SEQ"	    , commUtils.getValue(gdReq, "TRANS_ORD_SEQNO", ii));
					inRecord2.setField("CAR_NO"				, commUtils.getValue(gdReq, "CAR_NO", ii));
					inRecord2.setField("CARD_NO"			, commUtils.getValue(gdReq, "CARD_NO", ii));
					inRecord2.setField("WAIT_ARR_DDTT"		, commUtils.getValue(gdReq, "WAIT_ARR_DDTT", ii));
					inRecord2.setField("WAIT_ARR_GP"		, commUtils.getValue(gdReq, "WAIT_ARR_GP", ii));
					
					ejbConn = new EJBConnector("default", "YmCoilL3RcvPISeEJB", this);
					jrRst =	(JDTORecord)ejbConn.trx("rcvM10LMYDJ1041", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });
					
//				} else {
//					inRecord2.setField("YD_GP"				, commUtils.getValue(gdReq, "YD_GP", ii));
//					inRecord2.setField("CMBN_CARLD_YN"		, commUtils.getValue(gdReq, "CMBN_CARLD_YN", ii));
//					inRecord2.setField("WORK_GP"			, commUtils.getValue(gdReq, "WORK_GP", ii));
//					inRecord2.setField("TEL_NO"				, commUtils.getValue(gdReq, "TEL_NUMBER", ii));
//					inRecord2.setField("TRANS_ORD_DT"		, commUtils.getValue(gdReq, "TRANS_ORD_DT", ii));
//					inRecord2.setField("TRANS_ORD_SEQNO"	, commUtils.getValue(gdReq, "TRANS_ORD_SEQNO", ii));
//					inRecord2.setField("CAR_NO"				, commUtils.getValue(gdReq, "CAR_NO", ii));
//					inRecord2.setField("CARD_NO"			, commUtils.getValue(gdReq, "CARD_NO", ii));
//					inRecord2.setField("WAIT_ARR_DDTT"		, commUtils.getValue(gdReq, "WAIT_ARR_DDTT", ii));
//					inRecord2.setField("WAIT_ARR_GP"		, commUtils.getValue(gdReq, "WAIT_ARR_GP", ii));
//					
//					ejbConn = new EJBConnector("default", "YmCommL3RcvSeEJB", this);
//					jrRst =	(JDTORecord)ejbConn.trx("rcvDMYDR061", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });
//				}	
			}
			
			szMsg = "[JSP Facade] 차량작업관리 대기장도착처리 전송처리 ===> 끝";
			commUtils.printLog(logId, szMsg, "SL");	
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			 
		}catch(Exception e){

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			
		}
		return gdRes;
	}  //end of getStandByYdArrive
	
	
	/**
	 * 코일제품차량작업 관리- 출발처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData updCarStart(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "배차내역 - 출발처리[updCarStart]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCarStart", new Class[] { GridData.class }, new Object[] { gdReq });
			
			
			//조회
			//GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, commUtils.trim(jrRst.getFieldString("RTN_MSG")), "SL");
			gdRet.setMessage(commUtils.trim(jrRst.getFieldString("RTN_MSG")));
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
	}	//end of updCarStart 		
	
	/**
	 * 크레인스케줄 기준 변경 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSchRuleMgt(GridData gdReq) throws DAOException {
		String methodNm =  "크레인스케줄 기준 변경[BCoilJspFaEJB.updSchRuleMgt]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//IFTest Layout 변경
			ejbConn.trx("updSchRuleMgt", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	//end of updSchRuleMgt
	
	
	/**
	 * 크레인스케줄 고도화기준 변경 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updAdvSchRuleMgt(GridData gdReq) throws DAOException {
		String methodNm =  "크레인스케줄 고도화기준 변경[BCoilJspFaEJB.updAdvSchRuleMgt]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//IFTest Layout 변경
			ejbConn.trx("updAdvSchRuleMgt", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	//end of updAdvSchRuleMgt
	
	
	/**
	 * 차량예정정보 전송
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regCarUdExplainInfo(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "차량예정정보 전송[BCoilJspFaEJB.regCarUdExplainInfo]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("regCarUdExplainInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			gdRet.setMessage(commUtils.trim(jrRst.getFieldString("RTN_MSG")));
			
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
	}	//end of regCarUdExplainInfo
		
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인SCH 기동 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData procCrnWrkBookStart(GridData gdReq) throws DAOException {
		String methodNm = "크레인상태설정[BCoilJspSeEJB.procCrnWrkBookStart]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procCrnWrkBookStart", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	
	
	/**
	 * 크레인작업예약관리 - 작업예약삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delWrkBook(GridData gdReq) throws DAOException {
		String methodNm =  "작업예약삭제[BCoilJspSeEJB.delWrkBook]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		JDTORecord sndRecord = null;
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			sndRecord = (JDTORecord)ejbConn.trx("delWrkBook", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (sndRecord != null) {
				sndRecord.setResultCode(logId);
				sndRecord.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { sndRecord });
			}
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of delWrkBook		

	/**
	 * 구내운송차량출발
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqTsStart(GridData gdReq) throws DAOException {
		String methodNm =  "구내운송차량출발 [BCoilJspFaEJB.reqTsStart]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("JMS_TC_CD"			, "TSYDJ004" );
			jrParam.setField("TRN_EQP_CD"			, gdReq.getParam("TRN_EQP_CD") );
			jrParam.setField("SPOS_WLOC_CD"			, "" );
			jrParam.setField("SPOS_YD_PNT_CD"		, "" );
			jrParam.setField("ARR_WLOC_CD"			, gdReq.getParam("SPOS_WLOC_CD") );
			jrParam.setField("ARR_YD_PNT_CD"		, "" );
			jrParam.setField("TRN_WRK_FULLVOID_GP"	, "E" );
			jrParam.setField("YD_WO_CNCL_YN"		, "N" );
			jrParam.setField("L3_HMI"				, "Y" );   //백업화면 기동 여부
			
			
			EJBConnector ejbConn = new EJBConnector("default", "YmCommCarMvSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("rcvTSYDJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of reqTsStart	

	
	/**
	 * 이송차량 실적처리 팝업 - 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData trtMvCarStatSet2(GridData gdReq) throws DAOException {
		String methodNm =  "이송차량 실적처리 팝업 - 등록[BCoilJspFaEJB.trtMvCarStatSet2]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtMvCarStatSet2", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of trtMvCarStatSet2		
	/**
	 * 이송작업재료등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm =  "이송작업재료등록[BCoilJspFaEJB.updCarFtMvMtl]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			ejbConn.trx("updCarFtMvMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			//GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCarFtMvMtl
	
	/**
	 * 이송작업재료삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm =  "이송작업재료삭제[BCoilJspFaEJB.delCarFtMvMtl]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			JDTORecord jrRst =  (JDTORecord)ejbConn.trx("delCarFtMvMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			//GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of delCarFtMvMtl	

	/**
	 * 이송작업재료위치변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData chgCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm =  "이송작업재료위치변경[BCoilJspFaEJB.chgCarFtMvMtl]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			JDTORecord jrRst =  (JDTORecord)ejbConn.trx("chgCarFtMvMtl", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			//GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of chgCarFtMvMtl		
	
	/**
	 * 적치단 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet updStacklayerStat(JDTORecord recPara) throws DAOException {
	 
		String methodNm =  "적치단 수정[BCoilJspFaEJB.updStacklayerStat]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) 
			                    + "("   + commUtils.trim(recPara.getFieldString("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(recPara.getFieldString("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", recPara);

			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecordSet recordSet = (JDTORecordSet)ejbConn.trx("updStacklayerStat", new Class[] { JDTORecord.class }, new Object[] { recPara });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return recordSet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updStacklayerStat	
	
	/**
	 * 크레인 스케줄 삭제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet delCrnSch(JDTORecord recPara) throws DAOException {
		String methodNm =  "크레인 스케줄 삭제[BCoilJspFaEJB.delCrnSch]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) 
			                    + "("   + commUtils.trim(recPara.getFieldString("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(recPara.getFieldString("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", recPara);

			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecordSet recordSet = (JDTORecordSet)ejbConn.trx("delCrnSch", new Class[] { JDTORecord.class }, new Object[] { recPara });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return recordSet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of delCrnSch	
	
	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리-크레인변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCraneChange(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업요구현황조회-크레인변경[BCoilJspFaEJB.updCraneChange]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCraneChange", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
//			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCraneChange	
	
	
	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업요구현황조회-순위변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updPriorChange(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업요구현황조회-순위변경[BCoilJspFaEJB.updPriorChange]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updPriorChange", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
//			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updPriorChange	
	
	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업요구현황조회-긴급작업
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updPriorWrkChange(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업요구현황조회-긴급작업[BCoilJspFaEJB.updPriorWrkChange]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updPriorWrkChange", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updPriorChange		
	
	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업요구현황조회-권하위치변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updDownLocChange(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업요구현황조회-권하위치변경[BCoilJspFaEJB.updDownLocChange]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updDownLocChange", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updDownLocChange	
	
	
	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업요구현황조회-작업취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCraneWrkCancel(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업요구현황조회-작업취소[BCoilJspFaEJB.updCraneWrkCancel]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCraneWrkCancel", new Class[] { GridData.class }, new Object[] { gdReq });
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

	
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCraneWrkCancel		
	
	
	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업요구현황조회-스케줄취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCraneSchCancel(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업요구현황조회-스케줄취소[BCoilJspFaEJB.updCraneSchCancel]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCraneSchCancel", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCraneSchCancel		
	
	
	
	/**
	 * [A] 오퍼레이션명 : 크레인작업요구현황조회-스케줄재전송
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reSndCrnSch(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업요구현황조회-스케줄재전송[BCoilJspFaEJB.reSndCrnSch]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("reSndCrnSch", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of reSndCrnSch		
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업요구현황조회 - 권상권하처리 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData updbtCrnStsSetPp(GridData gdReq) throws DAOException {
		String methodNm = "크레인상태설정[BCoilJspFaEJB.updbtCrnStsSetPp]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updbtCrnStsSetPp", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
	
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
		
			commUtils.printLog(logId, methodNm, "F-", gdReq);

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 개소코드 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updWlocCd(GridData gdReq) throws DAOException {
		String methodNm =  "개소코드 변경[BCoilJspFaEJB.updWlocCd]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
			                    + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("updWlocCd", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updWlocCd	
	
	/**
	 * 사용여부 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updPtat(GridData gdReq) throws DAOException {
		String methodNm =  "사용여부 변경[BCoilJspFaEJB.updPtat]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
					            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
					            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updPtat", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updPtat	
	
	/**
	 * 적치기준 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStackRuleInfo(GridData gdReq) throws DAOException {
		String methodNm =  "적치기준 변경[BCoilJspFaEJB.updWlocCd]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
			                    + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("updStackRuleInfo", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updStackRuleInfo	
	
	/**
	 * 적치기준 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updateStackRuleInfo(GridData gdReq) throws DAOException {
		String methodNm =  "폭,외경기준 변경[BCoilJspFaEJB.updWlocCd]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
			                    + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("updateStackRuleInfo", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updateStackRuleInfo	
	
	/**
	 * 야드및설비 정보수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCoilYdStkPosSet(GridData gdReq) throws DAOException {
		String methodNm =  "야드및설비 열정보수정[BCoilJspFaEJB.updCoilYdStkPosSet]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
					            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
					            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCoilYdStkPosSet", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCoilYdStkPosSet
	
	/**
	 * 야드및설비 정보수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCoilYdStkPosSetBed(GridData gdReq) throws DAOException {
		String methodNm =  "야드및설비 베드정보수정[BCoilJspFaEJB.updCoilYdStkPosSetBed]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
					            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
					            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCoilYdStkPosSetBed", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCoilYdStkPosSetBed
	
	
	
	/**
	 * 산적위치수정 - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStkLoc(GridData gdReq) throws DAOException {
		String methodNm =  "산적위치수정-수정[BCoilJspFaEJB.updStkLoc]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//산적위치수정 - 수정
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updStkLoc", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updStkLoc	
	

	/**
	 * 산적위치수정 - 삭제수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delStkLoc(GridData gdReq) throws DAOException {
		String methodNm =  "산적위치수정-수정[BCoilJspFaEJB.delStkLoc]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {
	
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//산적위치수정 - 삭제
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("delStkLoc", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
	
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");
	
			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of delStkLoc	
	
	
	/**
	 * 산적위치수정 - 전문백업
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStkLocBackUp(GridData gdReq) throws DAOException {
		String methodNm =  "산적위치수정-수정[BCoilJspFaEJB.updStkLoc]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//산적위치수정 - 전문백업
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updStkLocBackUp", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updStkLoc	
	
	
	/**
	 * 저장영역별검색순서조회 - 저장 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStrAreaSrchSeq(GridData gdReq) throws DAOException {
		String methodNm =  "저장영역별검색순서조회 - 저장[BCoilJspFaEJB.updStrAreaSrchSeq]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//저장영역별검색순서 저장
			ejbConn.trx("updStrAreaSrchSeq", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	//end of updStrAreaSrchSeq
	
	/**
	 * 분기/확장 Conv - Line-Off 요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqLineOff(GridData gdReq) throws DAOException {
		String methodNm =  "분기/확장 Conv - Line-Off 요구[BCoilJspFaEJB.reqLineOff]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);
			jrParam.setResultMsg(methodNm);
			JDTORecord jrRst = null;
			
			
			JDTORecordSet rsChkResult;
			JDTORecord jrParamCoilChk = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParamCoilChk.setField("COIL_NO" , gdReq.getParam("COIL_NO"));
			rsChkResult = commDao.select(jrParamCoilChk, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilComByCurrProgCd", logId, methodNm, "TB_PT_COILCOMM 에 존재 하는지 확인");
			if(rsChkResult.size()<=0) {
				throw new Exception("존재하지 않는 COIL 번호 입니다. [" + gdReq.getParam("COIL_NO") + "]");	
			}
			
	
			if("D".equals(gdReq.getParam("PROC_GP"))) {
				
				//분기 컨베이어 Line Off
				jrParam.setField("JMS_TC_CD"	, "CF1PB12" );
				jrParam.setField("COILNo"		, gdReq.getParam("COIL_NO") );
				jrParam.setField("Location"		, gdReq.getParam("LOC_NO") );
				jrParam.setField("SKIDNo"		, gdReq.getParam("SKID_NO") );
				jrParam.setField("SPARE1"		, "SH" );
				jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
				
				EJBConnector ejbConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvCF1PB12", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
			} else if("X".equals(gdReq.getParam("PROC_GP"))) {
				
				//확장 컨베이어 Line Off
				
				String sSTACK_COL_GP = gdReq.getParam("LOC_NO");
				
				if(sSTACK_COL_GP.length() < 6 ) {
					throw new Exception("적치열 이상 [" + sSTACK_COL_GP + "]");	
				}
				
				jrParam.setField("JMS_TC_CD"	, "A7YML025" );
				jrParam.setField("YD_GP"		, gdReq.getParam("YD_GP") );
				jrParam.setField("BAY_GP"		, sSTACK_COL_GP.substring(1,2) );
				jrParam.setField("SECT_GP"		, sSTACK_COL_GP.substring(2,4) );
				jrParam.setField("COL_GP"		, sSTACK_COL_GP.substring(4,6) );
				jrParam.setField("STACK_BED_GP"	, gdReq.getParam("SKID_NO") );
				jrParam.setField("STOCK_ID"		, gdReq.getParam("COIL_NO") );
				jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
				
				EJBConnector ejbConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvA7YML025", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			}
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of reqLineOff	
	
	/**
	 * HFL/SPM/SPM2 - TakeIn 요구 (5)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqTakeIn(GridData gdReq) throws DAOException {
		String methodNm =  "HFL/SPM/SPM2 - TakeIn 요구[BCoilJspFaEJB.reqTakeIn]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
			String sRTN_MSG = "";
			
			//코일이 적치된 동과 적치상태 체크 --------------------------------------------------------------------------
			jrParam.setField("STOCK_ID", gdReq.getParam("COIL_NO"));
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getListCoilLoc", logId, methodNm, "코일이 적치된 동과 적치상태 체크 "); 
			if(rsResult.size() > 0) {
			
				String sBAY_GP 				= rsResult.getRecord(0).getFieldString("BAY_GP");
				String sSTACK_LAYER_STAT 	= rsResult.getRecord(0).getFieldString("STACK_LAYER_STAT");
				
				if("H".equals(gdReq.getParam("WORKID"))) {
					//HFL B동에 적치된 코일만 가능
					if(!("B".equals(sBAY_GP)&&"C".equals(sSTACK_LAYER_STAT))) {
						sRTN_MSG = "Take In 불가능 코일입니다!! 동구분:"+sBAY_GP+",적치상태:"+sSTACK_LAYER_STAT;
					}
				} else if("S".equals(gdReq.getParam("WORKID"))) {
					//SPM B동에 적치된 코일만 가능
					if(!("B".equals(sBAY_GP)&&"C".equals(sSTACK_LAYER_STAT))) {
						sRTN_MSG = "Take In 불가능 코일입니다!! 동구분:"+sBAY_GP+",적치상태:"+sSTACK_LAYER_STAT;
					}
				} else if("N".equals(gdReq.getParam("WORKID"))) {
					//SPM2 E동에 적치된 코일만 가능
					if(!("E".equals(sBAY_GP)&&"C".equals(sSTACK_LAYER_STAT))) {
						sRTN_MSG = "Take In 불가능 코일입니다!! 동구분:"+sBAY_GP+",적치상태:"+sSTACK_LAYER_STAT;
					}
				}
				
			} else {
				sRTN_MSG = "야드(적치단)에 없는 Coil번호 입니다!!";
			}
			//---------------------------------------------------------------------------------------------------
			
			if("".equals(sRTN_MSG)) {
				
				if("N".equals(gdReq.getParam("WORKID"))) { //SPM2
					
					//SPM2 Take In
					jrParam.setField("JMS_TC_CD"	, "POYMJ010" );
					jrParam.setField("YardId"		, gdReq.getParam("YD_GP") );
					jrParam.setField("WorkId"		, gdReq.getParam("WORKID") ); //H:HFL, S:SPM, N:SPM2
					jrParam.setField("ProcessId"	, gdReq.getParam("PROCESSID") ); //5
					jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
					jrParam.setField("TAKE_IN_GP"   , gdReq.getParam("TAKE_IN_GP"));
					jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
					
					EJBConnector ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
					jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ010", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					
				} else {
					
					//HFL, SPM Take In
					jrParam.setField("JMS_TC_CD"	, "POYMJ004" );
					jrParam.setField("YardId"		, gdReq.getParam("YD_GP") );
					jrParam.setField("WorkId"		, gdReq.getParam("WORKID") ); //H:HFL, S:SPM, N:SPM2
					jrParam.setField("ProcessId"	, gdReq.getParam("PROCESSID") ); //5
					jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
					jrParam.setField("TAKE_IN_GP"   , gdReq.getParam("TAKE_IN_GP"));
					jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
					
					EJBConnector ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
					jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				}
				
				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);
	
					EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
				
			} else {
				
				commUtils.printLog(logId, sRTN_MSG, "FL");
				gdRet.setMessage(sRTN_MSG);
			}

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of reqTakeIn	

	/**
	 * HFL/SPM/SPM2 - TakeOut 요구 (4)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqTakeOut(GridData gdReq) throws DAOException {
		String methodNm =  "HFL/SPM/SPM2 - TakeOut 요구[BCoilJspFaEJB.reqTakeOut]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
	
			//COIL 공통 존재여부 체크
			JDTORecordSet rsChkResult;
			JDTORecord jrParamCoilChk = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParamCoilChk.setField("COIL_NO" , gdReq.getParam("COIL_NO"));
			rsChkResult = commDao.select(jrParamCoilChk, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilComByCurrProgCd", logId, methodNm, "TB_PT_COILCOMM 에 존재 하는지 확인");
			if(rsChkResult.size()<=0) {
				
				String sWORKID = gdReq.getParam("WORKID");
				jrParamCoilChk.setField("STOCK_ID" , gdReq.getParam("COIL_NO"));
				
				//HFL인경우 스크랩재 존재 하지 않음 
				if("H".equals(sWORKID)){
					String sRTN_MSG = "존재하지 않는 COIL 번호 입니다. [" + gdReq.getParam("COIL_NO") + "]";
					commUtils.printLog(logId, sRTN_MSG, "FL");
					GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
					gdRet.setMessage(sRTN_MSG);	
					return gdRet;
				//SPM인경우 SCRAP존재여부 확인후 에러체크
				}else{ 
					//TB_PO_COILSHEARORD_SCRAP Table 존재여부 확인
					JDTORecordSet rsChkScrResult = commDao.select(jrParamCoilChk, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getScrInfo", logId, methodNm, "SCRAP존재여부 확인");
					
					if(rsChkScrResult.size() <= 0){
						String sRTN_MSG = "존재하지 않는 COIL 번호 입니다. [" + gdReq.getParam("COIL_NO") + "]";
						commUtils.printLog(logId, sRTN_MSG, "FL");
						GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
						gdRet.setMessage(sRTN_MSG);	
						return gdRet;
					}
					
				}
				
			}
			
			if("N".equals(gdReq.getParam("WORKID"))) {
				
				//SPM2 
				jrParam.setField("JMS_TC_CD"	, "POYMJ010" );
				jrParam.setField("YardId"		, gdReq.getParam("YD_GP") );
				jrParam.setField("WorkId"		, gdReq.getParam("WORKID") ); //H:HFL, S:SPM, N:SPM2
				jrParam.setField("ProcessId"	, gdReq.getParam("PROCESSID") ); //4
				jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
				jrParam.setField("Position"		, gdReq.getParam("POSITION"));
				jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
				
				EJBConnector ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ010", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
			} else {
				
				//HFL, SPM 
				jrParam.setField("JMS_TC_CD"	, "POYMJ004" );
				jrParam.setField("YardId"		, gdReq.getParam("YD_GP") );
				jrParam.setField("WorkId"		, gdReq.getParam("WORKID") ); //H:HFL, S:SPM, N:SPM2
				jrParam.setField("ProcessId"	, gdReq.getParam("PROCESSID") ); //4
				jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
				jrParam.setField("Position"		, gdReq.getParam("POSITION"));
				jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
				
				EJBConnector ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			}
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of reqTakeOut	
	
	/**
	 * HFL/SPM/SPM2 - 보급 요구 (1)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqSupply(GridData gdReq) throws DAOException {
		String methodNm =  "HFL/SPM/SPM2 - 보급 요구[BCoilJspFaEJB.reqSupply]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
			String sRTN_MSG = "";
			
			//코일이 적치된 동과 적치상태 체크 --------------------------------------------------------------------------
			jrParam.setField("STOCK_ID", gdReq.getParam("COIL_NO"));
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getListCoilLoc", logId, methodNm, "코일이 적치된 동과 적치상태 체크 "); 
			if(rsResult.size() > 0) {
			
				String sBAY_GP 				= rsResult.getRecord(0).getFieldString("BAY_GP");
				String sSTACK_LAYER_STAT 	= rsResult.getRecord(0).getFieldString("STACK_LAYER_STAT");
				
				if("H".equals(gdReq.getParam("WORKID"))) {
					//HFL A동에 적치된 코일만 가능
					if(!("A".equals(sBAY_GP)&&"C".equals(sSTACK_LAYER_STAT))) {
						sRTN_MSG = "보급 불가능 코일입니다!! 동구분:"+sBAY_GP+",적치상태:"+sSTACK_LAYER_STAT;
					}
				} else if("S".equals(gdReq.getParam("WORKID"))) {
					//SPM C,B동에 적치된 코일만 가능
					if(!(("C".equals(sBAY_GP)||"B".equals(sBAY_GP))&&"C".equals(sSTACK_LAYER_STAT))) {
						sRTN_MSG = "보급 불가능 코일입니다!! 동구분:"+sBAY_GP+",적치상태:"+sSTACK_LAYER_STAT;
					}
				} else if("N".equals(gdReq.getParam("WORKID"))) {
					//SPM2 D,E동에 적치된 코일만 가능
					if(!(("D".equals(sBAY_GP)||"E".equals(sBAY_GP))&&"C".equals(sSTACK_LAYER_STAT))) {
						sRTN_MSG = "보급 불가능 코일입니다!! 동구분:"+sBAY_GP+",적치상태:"+sSTACK_LAYER_STAT;
					}
				}
				
			} else {
				sRTN_MSG = "야드(적치단)에 없는 Coil번호 입니다!!";
			}
			//---------------------------------------------------------------------------------------------------
			
			if("".equals(sRTN_MSG)) {
				
				if("N".equals(gdReq.getParam("WORKID"))) {
					
					//SPM2 
					jrParam.setField("JMS_TC_CD"	, "POYMJ010" );
					jrParam.setField("YardId"		, gdReq.getParam("YD_GP") );
					jrParam.setField("WorkId"		, gdReq.getParam("WORKID") ); //H:HFL, S:SPM, N:SPM2
					jrParam.setField("ProcessId"	, gdReq.getParam("PROCESSID") ); //1
					jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
					jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
					
					EJBConnector ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
					jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ010", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					
				} else {
					
					//HFL, SPM
					jrParam.setField("JMS_TC_CD"	, "POYMJ004" );
					jrParam.setField("YardId"		, gdReq.getParam("YD_GP") );
					jrParam.setField("WorkId"		, gdReq.getParam("WORKID") ); //H:HFL, S:SPM, N:SPM2
					jrParam.setField("ProcessId"	, gdReq.getParam("PROCESSID") ); //1
					jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
					jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
					
					EJBConnector ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
					jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				}
				
				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);
	
					EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
			
			} else {
				
				commUtils.printLog(logId, sRTN_MSG, "FL");
				gdRet.setMessage(sRTN_MSG);
			}

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of reqSupply	

	/**
	 * HFL/SPM/SPM2 - 보급취소 요구 (2)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqCancelSupply(GridData gdReq) throws DAOException {
		String methodNm =  "HFL/SPM/SPM2 - 보급취소 요구[BCoilJspFaEJB.reqCancelSupply]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
	
			//COIL 공통 존재여부 체크
			JDTORecordSet rsChkResult;
			JDTORecord jrParamCoilChk = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParamCoilChk.setField("COIL_NO" , gdReq.getParam("COIL_NO"));
			rsChkResult = commDao.select(jrParamCoilChk, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilComByCurrProgCd", logId, methodNm, "TB_PT_COILCOMM 에 존재 하는지 확인");
			if(rsChkResult.size()<=0) {
				
				String sWORKID = gdReq.getParam("WORKID");
				jrParamCoilChk.setField("STOCK_ID" , gdReq.getParam("COIL_NO"));
				
				//HFL인경우 스크랩재 존재 하지 않음 
				if("H".equals(sWORKID)){
					String sRTN_MSG = "존재하지 않는 COIL 번호 입니다. [" + gdReq.getParam("COIL_NO") + "]";
					commUtils.printLog(logId, sRTN_MSG, "FL");
					GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
					gdRet.setMessage(sRTN_MSG);	
					return gdRet;
				//SPM인경우 SCRAP존재여부 확인후 에러체크
				}else{ 
					//TB_PO_COILSHEARORD_SCRAP Table 존재여부 확인
					JDTORecordSet rsChkScrResult = commDao.select(jrParamCoilChk, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getScrInfo", logId, methodNm, "SCRAP존재여부 확인");
					
					if(rsChkScrResult.size() <= 0){
						String sRTN_MSG = "존재하지 않는 COIL 번호 입니다. [" + gdReq.getParam("COIL_NO") + "]";
						commUtils.printLog(logId, sRTN_MSG, "FL");
						GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
						gdRet.setMessage(sRTN_MSG);	
						return gdRet;
					}
					
				}
				
			}
			
			if("N".equals(gdReq.getParam("WORKID"))) {
				
				//SPM2 
				jrParam.setField("JMS_TC_CD"	, "POYMJ010" );
				jrParam.setField("YardId"		, gdReq.getParam("YD_GP") );
				jrParam.setField("WorkId"		, gdReq.getParam("WORKID") ); //H:HFL, S:SPM, N:SPM2
				jrParam.setField("ProcessId"	, gdReq.getParam("PROCESSID") ); //2
				jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
				jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
				
				EJBConnector ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ010", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
			} else {
				
				//HFL, SPM 
				jrParam.setField("JMS_TC_CD"	, "POYMJ004" );
				jrParam.setField("YardId"		, gdReq.getParam("YD_GP") );
				jrParam.setField("WorkId"		, gdReq.getParam("WORKID") ); //H:HFL, S:SPM, N:SPM2
				jrParam.setField("ProcessId"	, gdReq.getParam("PROCESSID") ); //2
				jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
				jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
				
				EJBConnector ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			}
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of reqCancelSupply	
	
	/**
	 * HFL/SPM/SPM2 - 추출 요구 (3)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqExtraction(GridData gdReq) throws DAOException {
		String methodNm =  "HFL/SPM/SPM2 - 추출 요구[BCoilJspFaEJB.reqExtraction]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
	
			//COIL 공통 존재여부 체크
			JDTORecordSet rsChkResult;
			JDTORecord jrParamCoilChk = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParamCoilChk.setField("COIL_NO" , gdReq.getParam("COIL_NO"));
			rsChkResult = commDao.select(jrParamCoilChk, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilComByCurrProgCd", logId, methodNm, "TB_PT_COILCOMM 에 존재 하는지 확인");
			if(rsChkResult.size()<=0) {
				
				String sWORKID = gdReq.getParam("WORKID");
				jrParamCoilChk.setField("STOCK_ID" , gdReq.getParam("COIL_NO"));
				
				//HFL인경우 스크랩재 존재 하지 않음 
				if("H".equals(sWORKID)){
					String sRTN_MSG = "존재하지 않는 COIL 번호 입니다. [" + gdReq.getParam("COIL_NO") + "]";
					commUtils.printLog(logId, sRTN_MSG, "FL");
					GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
					gdRet.setMessage(sRTN_MSG);	
					return gdRet;
				//SPM인경우 SCRAP존재여부 확인후 에러체크
				}else{ 
					//TB_PO_COILSHEARORD_SCRAP Table 존재여부 확인
					JDTORecordSet rsChkScrResult = commDao.select(jrParamCoilChk, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getScrInfo", logId, methodNm, "SCRAP존재여부 확인");
					
					if(rsChkScrResult.size() <= 0){
						String sRTN_MSG = "존재하지 않는 COIL 번호 입니다. [" + gdReq.getParam("COIL_NO") + "]";
						commUtils.printLog(logId, sRTN_MSG, "FL");
						GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
						gdRet.setMessage(sRTN_MSG);	
						return gdRet;
					}
					
				}
				
			}
			
			
			if("N".equals(gdReq.getParam("WORKID"))) {
				
				//SPM2 
				jrParam.setField("JMS_TC_CD"	, "POYMJ010" );
				jrParam.setField("YardId"		, gdReq.getParam("YD_GP") );
				jrParam.setField("WorkId"		, gdReq.getParam("WORKID") ); //H:HFL, S:SPM, N:SPM2
				jrParam.setField("ProcessId"	, gdReq.getParam("PROCESSID") ); //3
				jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
				jrParam.setField("Position"		, gdReq.getParam("POSITION"));
				jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
				
				EJBConnector ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ010", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
			} else {
				
				//HFL, SPM 
				jrParam.setField("JMS_TC_CD"	, "POYMJ004" );
				jrParam.setField("YardId"		, gdReq.getParam("YD_GP") );
				jrParam.setField("WorkId"		, gdReq.getParam("WORKID") ); //H:HFL, S:SPM, N:SPM2
				jrParam.setField("ProcessId"	, gdReq.getParam("PROCESSID") ); //3
				jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
				jrParam.setField("Position"		, gdReq.getParam("POSITION"));
				jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
				
				EJBConnector ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			}
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of reqExtraction	

	/**
	 * HFL/SPM/SPM2 - 정정실적 요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqWorkResult(GridData gdReq) throws DAOException {
		String methodNm =  "HFL/SPM/SPM2 - 정정실적 요구[BCoilJspFaEJB.reqWorkResult]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
	
			jrParam.setField("JMS_TC_CD"	, "POYMJ001" );
			jrParam.setField("YardID"		, gdReq.getParam("YD_GP") );
//			jrParam.setField("WORKID"		, gdReq.getParam("WORKID") ); //H:HFL, S:SPM, N:SPM2
			jrParam.setField("ProcessID"	, gdReq.getParam("PROCESSID") ); //07:HFL, 02:SPM,SPM2
			jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
			jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
			jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ001", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of reqWorkResult	
	
	/**
	 * SPM/SPM2 - 스크랩백업 요구 (3)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqScrapBackup(GridData gdReq) throws DAOException {
		String methodNm =  "SPM/SPM2 - 스크랩백업 요구[BCoilJspFaEJB.reqScrapBackup]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
	
			if("N".equals(gdReq.getParam("WORKID"))) {
				
				//SPM2 
				jrParam.setField("JMS_TC_CD"	, "POYMJ010" );
				jrParam.setField("YardId"		, gdReq.getParam("YD_GP") );
				jrParam.setField("WorkId"		, gdReq.getParam("WORKID") ); //H:HFL, S:SPM, N:SPM2
				jrParam.setField("ProcessId"	, gdReq.getParam("PROCESSID") ); //3
				jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
				jrParam.setField("Position"		, gdReq.getParam("POSITION"));
				jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
				
				EJBConnector ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ010", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
			} else {
				
				//HFL, SPM 
				jrParam.setField("JMS_TC_CD"	, "POYMJ004" );
				jrParam.setField("YardId"		, gdReq.getParam("YD_GP") );
				jrParam.setField("WorkId"		, gdReq.getParam("WORKID") ); //H:HFL, S:SPM, N:SPM2
				jrParam.setField("ProcessId"	, gdReq.getParam("PROCESSID") ); //3
				jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
				jrParam.setField("Position"		, gdReq.getParam("POSITION"));
				jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
				
				EJBConnector ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			}
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of reqScrapBackup	
	
	/**
	 * 2HFL - 보급 요구 (1), 추출 요구 (3)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData req2HFLSupply(GridData gdReq) throws DAOException {
		String methodNm =  "2HFL - 보급 요구[BCoilJspFaEJB.req2HFLSupply]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
	
			//COIL 공통 존재여부 체크
            JDTORecordSet rsChkResult;
			JDTORecord jrParamCoilChk = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParamCoilChk.setField("COIL_NO" , gdReq.getParam("COIL_NO"));
			rsChkResult = commDao.select(jrParamCoilChk, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilComByCurrProgCd", logId, methodNm, "TB_PT_COILCOMM 에 존재 하는지 확인");
			if(rsChkResult.size()<=0) {
				throw new Exception("존재하지 않는 COIL 번호 입니다. [" + gdReq.getParam("COIL_NO") + "]");	
			}
			
			//HFL, SPM
			jrParam.setField("JMS_TC_CD"	, "POYMJ004" );
			jrParam.setField("YardId"		, gdReq.getParam("YD_GP") );
			jrParam.setField("WorkId"		, gdReq.getParam("WORKID") ); //D:2HFL(결속장)
			jrParam.setField("ProcessId"	, gdReq.getParam("PROCESSID") ); //1:보급, 3:추출
			jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
			jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
			
			//String sAPP016_YN = YmComm.BCoilApplyYn("APP016","3","1");   //log여부
			//if("Y".equals(sAPP016_YN)){
				EJBConnector ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ004_2HFL", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
			//} else {
			//	EJBConnector ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
			//	jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			//}
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of req2HFLSupply	
	
	/**
	 * 2HFL - 전체 추출 요구 (3)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData req2HFLExtractionAll(GridData gdReq) throws DAOException {
		String methodNm =  "2HFL - 전체 추출 요구[BCoilJspFaEJB.req2HFLExtractionAll]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
	
			//COIL 공통 존재여부 체크
            JDTORecordSet rsChkResult;
			JDTORecord jrParamCoilChk = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				String sCoilNo = commUtils.getValue(gdReq, "COIL_NO", ii);
				
				jrParamCoilChk.setField("COIL_NO" , sCoilNo);
				rsChkResult = commDao.select(jrParamCoilChk, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilComByCurrProgCd", logId, methodNm, "TB_PT_COILCOMM 에 존재 하는지 확인");
				if(rsChkResult.size()<=0) {
					throw new Exception("존재하지 않는 COIL 번호 입니다. [" + sCoilNo + "]");	
				}
				
				//HFL, SPM
				jrParam.setField("JMS_TC_CD"	, "POYMJ004" );
				jrParam.setField("YardId"		, gdReq.getParam("YD_GP") );
				jrParam.setField("WorkId"		, gdReq.getParam("WORKID") ); //D:2HFL(결속장)
				jrParam.setField("ProcessId"	, gdReq.getParam("PROCESSID") ); //1:보급, 3:추출
				jrParam.setField("CoilNo"		, sCoilNo);
				jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
				
				//String sAPP016_YN = YmComm.BCoilApplyYn("APP016","3","1");   //log여부
				//if("Y".equals(sAPP016_YN)){
					EJBConnector ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
					jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ004_2HFL", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					
				//} else {
				//	EJBConnector ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
				//	jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				//}
				
				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);

					EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}
	
	
	/**
	 * HFL/SPM/SPM2 - 회전 요구 (9)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqRotaion(GridData gdReq) throws DAOException {
		String methodNm =  "HFL/SPM/SPM2 - 회전 요구[BCoilJspFaEJB.reqRotaion]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
			
			jrParam.setField("YD_GP"			, gdReq.getParam("YD_GP") );
			jrParam.setField("COIL_NO"			, gdReq.getParam("COIL_NO"));

			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			jrRst = (JDTORecord)ejbConn.trx("reqRotaion", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
		
			gdRet.setMessage(commUtils.trim(jrRst.getFieldString("RTN_MSG")));
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of reqRotaion	
	/**
	 * 지포장 - 보급(1), 추출(3) 요구 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqPapWrapInOut(GridData gdReq) throws DAOException {
		String methodNm =  "지포장 - 보급,추출 요구[BCoilJspFaEJB.reqPapWrapInOut]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("reqPapWrapInOut", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of reqPapWrapInOut	
	
	/**
	 * 차량동간이적 (이적지시 )
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updColUnitCarMvstkRegNew(GridData gdReq) throws DAOException {
		String methodNm =  "차량동간이적[BCoilJspFaEJB.updColUnitCarMvstkRegNew]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updColUnitCarMvstkRegNew", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			
			//조회
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updIfTestData
	
	
	/**
	 *      [A] 오퍼레이션명 : 이적작업 예약 등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData updblMvStkWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "이적작업 예약 등록[BCoilJspFaEJB.updblMvStkWrkBook]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updblMvStkWrkBook", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-", gdReq);

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	} // updblMvStkWrkBook		
	
	
	/**
	 * 출하검수등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData updateCarExamination(GridData gdReq) throws DAOException {
		//		LOG
		String szMsg 			= "";
		String methodNm		= "updateCarExamination";
		String logId = gdReq.getIPAddress();
		 
		EJBConnector ejbConn 	= null;
	 
 
		JDTORecord outRecord  	= JDTORecordFactory.getInstance().create();	
		try{
			 
			szMsg = "[JSP Facade]"+methodNm+" 출하검수등록 ==>";
			commUtils.printLog(logId, szMsg, "SL");	
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			//먼저 이상코드 업데이트 사항은 업데이트 후 , 체크되어있는애들은 운송지시번호 단위로 검수처리
			for (int ii = 0; ii < rowCnt; ii++) {
				String ydCarUppLocCd = commUtils.getValue(gdReq, "NUM", ii);
				String ydAbCd = commUtils.getValue(gdReq, "YD_AB_CD", ii);
				String ydAbCd2 = commUtils.getValue(gdReq, "YD_AB_CD2", ii);
				String labelYn = commUtils.getValue(gdReq, "LABEL_YN", ii);
				String transOrdNo = commUtils.getValue(gdReq, "TRANS_ORD_NO", ii);
				String stlNo = commUtils.getValue(gdReq, "STL_NO", ii);
				
				//검수 완료 TC 전송 가능 유무 체크 
				String[] OrdArr = null;
				OrdArr = transOrdNo.split("-");
				String sTRANS_ORD_DATE =OrdArr[0];
				String sTRANS_ORD_SEQNO =OrdArr[1];
				
				outRecord.setField("YD_CAR_UPP_LOC_CD"		, ydCarUppLocCd);
				outRecord.setField("YD_AB_CD"				, ydAbCd);
				outRecord.setField("YD_AB_CD2"				, ydAbCd2);
				outRecord.setField("LABEL_YN"				, labelYn);
				outRecord.setField("MODIFIER"				, "YDPDA");  
				outRecord.setField("TRANS_ORD_DATE"				, sTRANS_ORD_DATE);
				outRecord.setField("TRANS_ORD_SEQNO"				, sTRANS_ORD_SEQNO);
				outRecord.setField("STL_NO"				, stlNo);
				
				outRecord.setField("QUERY_ID"				, "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateCarExaminationGoodsDetjlNEW2_m");
				
				szMsg = "[JSP Facade]"+methodNm+" 출하검수(PC) 지시번호 "+transOrdNo+"차량 이상코드: "+ydAbCd+" |이상코드 상세: "+ydAbCd2+" 업데이트";
				commUtils.printLog(logId, szMsg, "SL");	
		
				ejbConn= new EJBConnector("default", this);
				JDTORecordSet recordSet = (JDTORecordSet) ejbConn.trx("PlateJspSeEJB", "updGridData", outRecord);

			}

			
			for (int ii = 0; ii < rowCnt; ii++) {
				String checkGp =  commUtils.getValue(gdReq, "CHECK", ii);
				
				//체크되어있는 대상들만 운송지시번호 단위로 검수처리.
				if(!"1".equals(checkGp)) continue;
				outRecord 	= JDTORecordFactory.getInstance().create();
				outRecord.setField("userid"                     , commUtils.trim(gdReq.getParam("userid")));
				outRecord.setField("TRANS_ORD_NO"				, commUtils.getValue(gdReq, "TRANS_ORD_NO", ii));
				outRecord.setField("PI_YD"						, commUtils.getValue(gdReq, "PI_YD", ii));
				
				ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
				JDTORecord jrRst = (JDTORecord)	 ejbConn.trx(methodNm, new Class[] { JDTORecord.class }, new Object[] { outRecord });
				
				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);

					EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
			}
			//조회
			GridData gdRes = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
	
			szMsg = "[JSP Facade]"+methodNm+" 출하검수등록 ===> 끝";
			commUtils.printLog(logId, szMsg, "SL");	
	

 			 
			return  gdRes ;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}  //end of updateCarExamination
	
	/**
	 * 기준관리 - 세부항목수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updYmRule(GridData gdReq) throws DAOException {
		String methodNm =  "기준관리 - 수정[BCoilJspFaEJB.updYmRule]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			ejbConn.trx("updYmRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updYmRule
	
	/**
	 * 기준관리 - 등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regYmRule(GridData gdReq) throws DAOException {
		String methodNm =  "기준관리 - 등록[BCoilJspFaEJB.regYmRule]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			ejbConn.trx("regYmRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of regYmRule
	
	/**
	 * [A] 오퍼레이션명 : 이송지시 취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updFtmvWrkCancel(GridData gdReq) throws DAOException {
		String methodNm =  "이송지시 취소[BCoilJspFaEJB.updFtmvWrkCancel]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//이송지시 취소(1) - YDPTJ007 전문 저송
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updFtmvWrkCancel", new Class[] { GridData.class }, new Object[] { gdReq });
	
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//이송지시 취소(2) - 크레인스케줄,작업예약 ID 취소 처리
			jrRst = (JDTORecord)ejbConn.trx("updFtmvWrkCancel2", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updFtmvWrkCancel	
	
	/**
	 * [A] 오퍼레이션명 : 상차대상순위별조회 긴급작업
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updUgntWrk(GridData gdReq) throws DAOException {
		String methodNm =  "긴급작업[BCoilJspFaEJB.updUgntWrk]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//기존 긴급재 삭제 후 새로운 긴급재 편성
			ejbConn.trx("updUgntWrk", new Class[] { GridData.class }, new Object[] { gdReq });
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
	
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updUgntWrk
	
	/**
	 * 상차대상순위별조회 - SCH기동
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqCarldSchSt(GridData gdReq) throws DAOException {
		String methodNm =  "상차대상순위별조회 - SCH기동[BCoilJspFaEJB.reqCarldSchSt]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("reqCarldSchSt", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of CarldSchSt
	
	/**
	 * 하차작업등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regCarUdWrk(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "하차작업등록[BCoilJspFaEJB.regCarUdWrk]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("regCarUdWrk", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			gdRet.setMessage(commUtils.trim(jrRst.getFieldString("RTN_MSG")));
			
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
	}	//end of regCarUdWrk
	
	/**
	 * 하차작업등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCoilYdTcarClear(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "대차상태 초기화[BCoilJspFaEJB.updCoilYdTcarClear]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCoilYdTcarClear", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			gdRet.setMessage(commUtils.trim(jrRst.getFieldString("RTN_MSG")));
			
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
	}	//end of updCoilYdTcarClear
	/**
	 * 대차스케줄관리 - 대차초기화
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData initTcarSchMgt(GridData gdReq) throws DAOException {
		String methodNm =  "대차스케줄관리 대차초기화[BCoilJspFaEJB.initTcarSchMgt]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//대차초기화
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("initTcarSchMgt", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of initTcarSchMgt	
	/**
	 * 대차상태설정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException
	*/
	public GridData trtTcarStatSet(GridData gdReq) throws DAOException {
		String methodNm = "대차상태설정[BCoilJspFaEJB.trtTcarStatSet]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);

		try { 
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";

			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);

			//대차상태설정
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtTcarStatSet", new Class[] { GridData.class }, new Object[] { gdReq });
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}


			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of trtTcarStatSet
		
	/**
	 * 설비상태 (대차스케줄복원 )
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData TcarSchRollBack(GridData gdReq) throws DAOException {
		String methodNm =  "대차스케줄복원[BCoilJspFaEJB.TcarSchRollBack]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//스케줄복원
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("TcarSchRollBack", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of TcarSchRollBack		
	
	
	/**
	 * 대차이동구간변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData updTCarYdGpMgt(GridData gdReq) throws JDTOException {
		String methodNm =  "대차이동구간변경[BCoilJspFaEJB.updTCarYdGpMgt]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			ejbConn.trx("updTCarYdGpMgt", new Class[] { GridData.class }, new Object[] { gdReq });

			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");
			return gdRet;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}  //end of updTCarYdGpMg
	
	
	
	/**
	 * 위치별 적치현황조회 - Bed상태 수정 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updBedActStat(GridData gdReq) throws DAOException {
		String methodNm =  "위치별 적치현황조회 - Bed상태 수정[BCoilJspFaEJB.updBedActStat]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			GridData gdRet = null;
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updBedActStat", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	//end of updBedActStat
	
	/**
	 * 상차완료Backup처리 화면 : 초기화
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData initMvCarSchMgt(GridData gdReq) throws DAOException {
		String methodNm =  "상차완료Backup처리 화면 - 초기화[BCoilJspFaEJB.initMvCarSchMgt]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		 
		try{

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			String modifier  = commUtils.trim(gdReq.getParam("userid")); //수정자
			
			EJBConnector ejbConn 	= null;
			JDTORecord outRecord  	= commUtils.getParam(logId, methodNm, modifier);
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
				if( "1".equals(sYdCarProgStat)||
					"2".equals(sYdCarProgStat)||
					"3".equals(sYdCarProgStat)||
					"4".equals(sYdCarProgStat)||
					"5".equals(sYdCarProgStat)) {
					outRecord.setField("WLOC_CD"		, commUtils.getValue(gdReq, "SPOS_WLOC_CD", ii));
				} else {
					outRecord.setField("WLOC_CD"		, commUtils.getValue(gdReq, "ARR_WLOC_CD", ii));
				}
					
				ejbConn = new EJBConnector("default", "YmCommCarMvSeEJB", this);
				jrRtn 	= (JDTORecord)ejbConn.trx("initCarSch", new Class[] { JDTORecord.class }, new Object[] { outRecord });
				jrRst 	= commUtils.addSndData(jrRst, jrRtn);
					
			}
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			 
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}  //end of initMvCarSchMgt
	
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
		String methodNm =  "하차백업생성[BCoilJspFaEJB.mkUdCarSch]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		 
		try{

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("mkUdCarSch", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			 
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}  //end of initMvCarSchMgt
	
	/**
	 * [A] 오퍼레이션명 : 대차작업현황조회-순위변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData modPriorChange(GridData gdReq) throws DAOException {
		String methodNm =  "대차작업현황조회-순위변경[BCoilJspFaEJB.modPriorChange]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("modPriorChange", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of modPriorChange	
	
	
	/**
	 * [A] 오퍼레이션명 : 대차작업현황조회-작업예약삭제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delWrkBookDel(GridData gdReq) throws DAOException {
		String methodNm =  "대차작업현황조회-작업예약삭제[BCoilJspFaEJB.delWrkBookDel]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		JDTORecord sndRecord = null;
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			sndRecord = (JDTORecord)ejbConn.trx("delWrkBookDel", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (sndRecord != null) {
				sndRecord.setResultCode(logId);
				sndRecord.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { sndRecord });
			}
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of delWrkBookDel		
	
	
	/**
	 * 대차작업현황조회 - 최대적치매수 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStackMaxQnty(GridData gdReq) throws DAOException {
		String methodNm =  "대차작업현황조회-최대적치매수 수정[BCoilJspFaEJB.updStackMaxQnty]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//최대적치매수 - 수정
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updStackMaxQnty", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updStackMaxQnty	
	
	
	/**
	 * 대차작업현황조회 - 대차직보급 여부 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updDirYn(GridData gdReq) throws DAOException {
		String methodNm =  "대차작업현황조회-대차직보급여부 수정[BCoilJspFaEJB.updDirYn]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//대차직보급여부 - 수정
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updDirYn", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updDirYn	
	
	
	/**
	 * 코일공통상세조회-정정검사메시지 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updShearInspectMsg(GridData gdReq) throws DAOException {
		String methodNm =  "코일공통상세조회-정정검사메시지 수정[BCoilJspFaEJB.updShearInspectMsg]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//정정검사메시지 - 수정
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updShearInspectMsg", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updShearInspectMsg		
	
	
	/**
	 * 스크랩비우기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procClearScrap(GridData gdReq) throws DAOException {
		String methodNm =  "스크랩현황조회-스크랩비우기[BCoilJspFaEJB.procClearScrap]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//정정검사메시지 - 수정
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procClearScrap", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of procClearScrap			
	
	
	/**
	 * 스크랩생성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procCreateScrap(GridData gdReq) throws DAOException {
		String methodNm =  "스크랩현황조회-스크랩생성[BCoilJspFaEJB.procCreateScrap]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//정정검사메시지 - 수정
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procCreateScrap", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of procCreateScrap	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인 작업지시(스케쥴) 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData
	 *      @return GridData
	 *      @throws DAOException
     */			
	public GridData getSchAvdWoList(GridData gdReq) throws DAOException {
		String methodNm = "신고도화 크레인 스케쥴 결정여부 - [BCoilJspFaEJB.getSchAvdWoList]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("getSchAvdWoList", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");
			
			gdRet.setStatus("true");
			gdRet.setMessage("success");
			
			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}	// end of getSchAvdWoList
	
	/**
	 *      [A] 오퍼레이션명 : 화면에서 수동으로 크레인에 대해 작업지시를 내린다.
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData
	 *      @return GridData
	 *      @throws DAOException
     */			
	public GridData sendA7YML007(GridData gdReq) throws DAOException {
		String methodNm = "크레인 작업지시 생성 sendA7YML007[BCoilJspFaEJB.sendA7YML007]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
			JDTORecord jrA7YML007 = JDTORecordFactory.getInstance().create();
			
			jrA7YML007.setField("JMS_TC_CD", YmConstant.A7YML007);
			jrA7YML007.setField("YD_EQP_ID", gdReq.getParam("YD_EQP_ID"));	// 설비ID(크레인ID)
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("rcvA7YML007", new Class[] { JDTORecord.class }, new Object[] { jrA7YML007 });	// L2 작업지시 전송
			
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			
				commUtils.printLog(logId, methodNm, "F-");
			}
			
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}	// end of sendA7YML007
	
	
	/**
	 *      [A] 오퍼레이션명 : 화면에서 수동으로 A7YML018 - [차량동간이적(도착)] 전문을 송신한다
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData
	 *      @return GridData
	 *      @throws DAOException
     */			
	public GridData sendA7YML018(GridData gdReq) throws DAOException {
		String methodNm = "차량동간이적(도착) 송신 sendA7YML007[BCoilJspFaEJB.sendA7YML018]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
			JDTORecord jrA7YML018 = JDTORecordFactory.getInstance().create();
			
			jrA7YML018.setField("JMS_TC_CD"		, YmConstant.A7YML018);
			jrA7YML018.setField("PT_LOAD_LOC"	, gdReq.getParam("PT_LOAD_LOC"));	// 상차도 위치
			jrA7YML018.setField("CAR_NO"		, gdReq.getParam("CAR_NO"));		// 차량번호
			jrA7YML018.setField("CAR_UPDN_GP"	, gdReq.getParam("CAR_UPDN_GP"));	// 차량상하차구분 ? 1:상차, 2:하차
			jrA7YML018.setField("MODIFIER"		, gdReq.getParam("MODIFIER"));		// 차량번호
			
			commUtils.printLog(logId, methodNm, jrA7YML018.toString());
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("rcvA7YML018", new Class[] { JDTORecord.class }, new Object[] { jrA7YML018 });	// L2 작업지시 전송
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			
				commUtils.printLog(logId, methodNm, "F-");
			}
			
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}	// end of sendA7YML018
	
	//2018년 3월 8일 고도화 모니터링 화면 냉각코일자동이적 편성, 더미 자동이적 편성 기능추가
	/**
	 *      1열연 COIL 고도화 모니터링 화면 냉각코일자동이적, 더미 자동이적 편성 기능 추가
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData
	 *      @return GridData
	 *      @throws DAOException
     */	
	public GridData updYdAdvancemtRule(GridData gdReq) throws DAOException {
		String methodNm = "냉각코일자동이적/더미 자동이적 편성[BCoilJspFaEJB.updYdAdvancemtRule]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			
			//최대적치매수 - 수정
			//JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updYdAdvancemtRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
			ejbConn.trx("updYdAdvancemtRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			/*if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}*/
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //updYdAdvancemtRule
	
	
	
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
		String methodNm = "스크랩 차량 진입여부 변경[BCoilJspFaEJB.updScrpCar]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			ejbConn.trx("updScrpCar", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end updScrpCar 
	
	
	/**
	 * YM-RULE 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData modiYmRule(GridData gdReq) throws DAOException {
		String methodNm =  "YM-RULE 수정[BCoilJspFaEJB.modiYmRule]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			

			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("modiYmRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updDirYn
	
	/**
	 *      [A] 오퍼레이션명 : 권하위치변경가능 위치
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData
	 *      @return GridData
	 *      @throws DAOException
     */			
	public GridData getDownLocChange(GridData gdReq) throws DAOException {
		String methodNm = "권하위치변경가능 위치 - [BCoilJspFaEJB.getDownLocChange]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("getDownLocChange", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");
			
			gdRet.setStatus("true");
			gdRet.setMessage("success");
			
			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}	// end of getDownLocChange
	
	/**
	 *      [A] 오퍼레이션명 : 권하위치변경가능 위치 (사용자 지정 span 조회)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData
	 *      @return GridData
	 *      @throws DAOException
     */			
	public GridData getDownLocChangeByUser(GridData gdReq) throws DAOException {
		String methodNm = "권하위치변경가능 위치 (사용자 지정 span 조회) - [BCoilJspFaEJB.getDownLocChangeByUser]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("getDownLocChangeByUser", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");
			
			gdRet.setStatus("true");
			gdRet.setMessage("success");
			
			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}	// end of getDownLocChangeByUser	

	/**
	 *      [A] 오퍼레이션명 : 권하위치변경가능 위치 (사용자 지정 span 조회)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData
	 *      @return GridData
	 *      @throws DAOException
     */			
	public GridData getScrapDownLocChangeByUser(GridData gdReq) throws DAOException {
		String methodNm = "Scrap 권하위치변경가능 위치  (사용자 지정 Scrap 열 조회) - [BCoilJspFaEJB.getScrapDownLocChangeByUser]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("getScrapDownLocChangeByUser", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");
			
			gdRet.setStatus("true");
			gdRet.setMessage("success");
			
			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}	// end of getScrapDownLocChangeByUser	
	
	/**
	 *      [A] 오퍼레이션명 : 배차차량작업관리 입동지시/작업지연 SMS 전송
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData
	 *      @return GridData
	 *      @throws DAOException
     */			
	public GridData procBayInWoSmsSend(GridData gdReq) throws DAOException {
		String methodNm = "배차차량작업관리 입동지시/작업지연 SMS 전송 - [BCoilJspFaEJB.procBayInWoSmsSend]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procBayInWoSmsSend", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");
			
			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}	// end of procBayInWoSmsSend	
	
	/**
	 * 기준관리 화면 - 1열연 제품장 재고관리 알람 전송 backup
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData sndKakaoMsg(GridData gdReq) throws DAOException {
		String methodNm =  "기준관리 화면 - 1열연 제품장 재고관리 알람 전송 backup[BCoilJspFaEJB.sndKakaoMsg]";
		String logId = commUtils.getLogId(YmConstant.YD_GP_3);
		 
		try{

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			String modifier  = commUtils.trim(gdReq.getParam("userid")); //수정자
			
			EJBConnector ejbConn 	= null;
			JDTORecord jrRtn 		= JDTORecordFactory.getInstance().create();
			
			JDTORecord jrYMYMJ313 = commUtils.getParam(logId, methodNm, modifier);
			jrYMYMJ313.setResultCode(logId);	//Log ID
			jrYMYMJ313.setResultMsg(methodNm);	//Log Method Name
			jrYMYMJ313.setField("JMS_TC_CD" 		, "YMYMJ313");
			jrYMYMJ313.setField("GUBUN" 			, "CHK_TEST");

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
			
				jrYMYMJ313.setField("PHONE_NUM" 	, commUtils.getValue(gdReq, "DTL_ITM1", ii));
				
				ejbConn = new EJBConnector("default", "BCoilL3RcvSeEJB", this);
				jrRtn 	= (JDTORecord)ejbConn.trx("rcvYMYMJ313", new Class[] { JDTORecord.class }, new Object[] { jrYMYMJ313 });
			
			}
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			 
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}  //end of updCarWrMgt	
	
	
	/**
	 * [A] 오퍼레이션명 : 임가공 등록 처리  
	 *
	 * [B] Action위치 : 야드관리 > 임가공 > 입고등록 > 등록 버튼 실행 
	 *		
	 * PIDEV
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return GridData
	 * @throws DAOException 
	*/
	public GridData updcoilFromToResultjlNEW(GridData gdReq) throws DAOException {
		String methodNm = "임가공등록[BCoilJspFaEJB.updcoilFromToResultjlNEW]";
		
		String logId = commUtils.getLogId("임가공 야드");		
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updcoilFromToResultjlNEW", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
	
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
		
			commUtils.printLog(logId, methodNm, "F-", gdReq);

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	
	/**
	 *      [A] 오퍼레이션명    : 임가공 산적위치 수정 처리
	 *		
	 *		[B] Action 위치 : 야드관리 -> 임가공 -> 적치위치수정 -> 수정버튼 실행
	 *		
	 *		PIDEV 신규 메소드
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData headLocationModify(GridData gdReq) throws DAOException {
		
		String methodNm = "임가공 산적위치 수정[BCoilJspFaEJB.headLocationModify]";
		String logId = commUtils.getLogId("야드구분 => 임가공[5]");

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("headLocationModify", new Class[] { GridData.class }, new Object[] { gdReq });
		
			commUtils.printLog(logId, methodNm, "F-", gdReq);

			// 조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명    : 임가공 산적위치 삭제 처리
	 *		
	 *		[B] Action 위치 : 야드관리 -> 임가공 -> 적치위치수정 -> 삭제버튼 실행
	 *		
	 *		PIDEV 신규 메소드
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData headLocationDelete(GridData gdReq) throws DAOException {
		
		String methodNm = "임가공 삭제위치 수정[BCoilJspFaEJB.headLocationDelete]";
		String logId = commUtils.getLogId("야드구분 => 임가공[5]");

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilJspSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("headLocationDelete", new Class[] { GridData.class }, new Object[] { gdReq });
		
			commUtils.printLog(logId, methodNm, "F-", gdReq);

			// 조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
}

