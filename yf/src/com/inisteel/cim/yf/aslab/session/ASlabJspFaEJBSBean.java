/**
 * @(#)ASlabJspFaEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      박판열연 Slab 야드 화면관리 Facade EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용 
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 	      2019/11/20
 */
package com.inisteel.cim.yf.aslab.session;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yf.common.dao.YfCommDAO;
import com.inisteel.cim.yf.common.YfCommUtils;
import com.inisteel.cim.yf.common.YfConstant;

/**
 *      [A] 클래스명 : 박판열연 Slab 야드 화면관리 Facade EJB
 *
 * @ejb.bean name="ASlabJspFaEJB" jndi-name="ASlabJspFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class ASlabJspFaEJBSBean extends BaseSessionBean 
{
	private YfCommUtils	commUtils		= new YfCommUtils();
	private YfCommDAO	commDao			= new YfCommDAO();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException 
	{
		
	}
	
	/**
	 * GridData - 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getSelectData(GridData gdReq) throws DAOException 
	{
		String methodNm =  "조회[ASlabJspFaEJB.getSelectData]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try 
		{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
			                    + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;			
		}
		catch(DAOException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	

	/**
	 * 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSelectData(JDTORecord recPara) throws DAOException 
	{
		String methodNm =  "조회[ASlabJspFaEJB.getSelectData]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try 
		{
			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) 
			                    + "("   + commUtils.trim(recPara.getFieldString("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(recPara.getFieldString("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", recPara);

			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			JDTORecordSet recordSet = (JDTORecordSet)ejbConn.trx("getSelectData", new Class[] { JDTORecord.class }, new Object[] { recPara });

			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return recordSet;
		} 
		catch(DAOException e) 
		{
			throw e;
		} 
		catch(Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 위치별적치이동
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData updSlabLoc(GridData gdReq) throws DAOException 
	{
		String methodNm =  "위치별적치이동 변경[ASlabJspFaEJB.updSlabLoc]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try 
		{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updSlabLoc", new Class[] { GridData.class }, new Object[] { gdReq });
			
			gdReq = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);
				
				EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;
		} 
		catch(DAOException e) 
		{
			throw e;
		} 
		catch(Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 위치별적치이동(끼워넣기)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData inLaySlab(GridData gdReq) throws DAOException{
		String methodNm =  "위치별적치이동(끼워넣기) 변경[ASlabJspFaEJB.inLaySlab]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try 
		{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("inLaySlab", new Class[] { GridData.class }, new Object[] { gdReq });
			
			gdReq = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);
				
				EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			
			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;
		} 
		catch(DAOException e) 
		{
			throw e;
		} 
		catch(Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 위치별적치이동(덮어쓰기)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData coverSlab(GridData gdReq) throws DAOException{
		String methodNm =  "위치별적치이동(덮어쓰기) 변경[ASlabJspFaEJB.coverSlab]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try 
		{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("coverSlab", new Class[] { GridData.class }, new Object[] { gdReq });
			
			gdReq = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);
					
				EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			return gdReq;
		} 
		catch(DAOException e) 
		{
			throw e;
		} 
		catch(Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 차량 적치활성상태 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public GridData updCarActiveStat(GridData gdReq) throws DAOException {
		String methodNm =  "차량 적치활성상태 변경[ASlabJspFaEJB.updCarActiveStat]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			ejbConn.trx("updCarActiveStat", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
	 * 오퍼레이션명 : 
	 *
	 * 적치활성상태 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public GridData updActiveStat(GridData gdReq) throws DAOException {
		String methodNm =  "적치활성상태 변경[ASlabJspFaEJB.updActiveStat]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			ejbConn.trx("updActiveStat", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
	 * 박판열연 SLAB 벤딩표시,해제,보급 설정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updBendingStat(GridData gdReq) throws DAOException {
		String methodNm =  "박판열연 SLAB 벤딩표시,해제,보급 설정[ASlabJspFaEJB.updBendingStat]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try {
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updBendingStat", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updBendingStat
	
	/**
	 * 박판열연 SLAB 마킹표시,해제,보급 설정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updMarkingStat(GridData gdReq) throws DAOException {
		String methodNm =  "박판열연 SLAB Marking표시,해제,보급 설정[ASlabJspFaEJB.updMarkingStat]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try {
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			ejbConn.trx("updMarkingStat", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updMarkingStat
	
	/**
	 * 박판열연 SLAB 마킹표시,해제,보급 설정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSlabSupply(GridData gdReq) throws DAOException {
		String methodNm =  "박판열연 SLAB 보급[ASlabJspFaEJB.updSlabSupply]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try {
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updSlabSupply", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			commUtils.printLog(logId, methodNm, "F-");

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);
					
				EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updMarkingStat
	
	/**
	 * 박판열연 차량 사용여부
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updUseCarManage(GridData gdReq) throws DAOException {
		String methodNm =  "박판열연 SLAB 차량진입 가능여부수정[ASlabJspFaEJB.updUseCarManage]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updUseCarManage", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);
				
				EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			commUtils.printLog(logId, methodNm, "F-");
			
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updUseCarManage
	
	/**
	 * 박판열연 차량 회송
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData runTsRetHt(GridData gdReq) throws DAOException {
		String methodNm =  "박판열연 SLAB 차량 회송[ASlabJspFaEJB.runTsRetHt]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("runTsRetHt", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);
				
				EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of runTsRetHt
	
	/**
	 * 박판열연 차량 상차완료
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updPhaseComplete(GridData gdReq) throws DAOException {
		String methodNm =  "박판열연 SLAB 차량 상차완료[ASlabJspFaEJB.updPhaseComplete]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updPhaseComplete", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);
				
				EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}

			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			commUtils.printLog(logId, methodNm, "F-");
			
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updPhaseComplete
	
	/**
	 * 박판열연 차량 하차완료
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updPhaseOffComplete(GridData gdReq) throws DAOException {
		String methodNm =  "박판열연 SLAB 차량 하차완료[ASlabJspFaEJB.updPhaseOffComplete]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updPhaseOffComplete", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updPhaseOffComplete
	
	/**
	 * 박판열연 차량 상차취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updPhaseCancel(GridData gdReq) throws DAOException {
		String methodNm =  "박판열연 SLAB 차량 상차취소[ASlabJspFaEJB.updPhaseCancel]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			ejbConn.trx("updPhaseCancel", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updPhaseCancel
	
	/**
	 * 박판열연 차량 도착
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCarArrive(GridData gdReq) throws DAOException {
		String methodNm =  "박판열연 SLAB 차량 도착[ASlabJspFaEJB.updCarArrive]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			ejbConn.trx("updCarArrive", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCarArrive
	
	/**
	 * 박판열연 Slab 도착
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSlabArrive(GridData gdReq) throws DAOException {
		String methodNm =  "박판열연 SLAB 도착[ASlabJspFaEJB.updSlabArrive]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updSlabArrive", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updSlabArrive
	
	/**
	 * 박판열연 차량 초기화
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCarReset(GridData gdReq) throws DAOException {
		String methodNm =  "박판열연 SLAB 차량 초기화[ASlabJspFaEJB.updCarReset]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			ejbConn.trx("updCarReset", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCarReset
	
	/**
	 * 박판열연 Slab 초기화
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSlabReset(GridData gdReq) throws DAOException {
		String methodNm =  "박판열연 SLAB 초기화[ASlabJspFaEJB.updCarReset]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updSlabReset", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			commUtils.printLog(logId, methodNm, "F-");

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);
					
				EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCarReset
	
	/**
	 * 박판열연 차량 위치변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCarLocChange(GridData gdReq) throws DAOException {
		String methodNm =  "박판열연 SLAB 차량 위치변경[ASlabJspFaEJB.updCarLocChange]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCarLocChange", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updCarLocChange
	
////////////////////////////////////
//	김광철작업시작
//////////////////////////////////
	/**
	 * 박판Slab 진도코드 변경( 이송작업대기(E) -> Slab정정작업대기(A)  )
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * 
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * 
	 */
	public GridData updateSlabProgStat(GridData gdReq) throws DAOException {
		String methodNm =  "박판Slab상세조회 진도코드변경(E->A)[ASlabJspFaEJB.updateSlabProgStat]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		GridData gdRet = null;
		String message = "";
		JDTORecord jrRtn = null;
		JDTORecordSet rsResult = null;
		JDTORecord jrParam = null;
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));	
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			gdRet = OperateGridData.cloneResponseGridData(gdReq);
			/**************************
			 * 변경가능여부 체크
			 *  - 진도코드 및 이송지시 여부 확인
			 */
			/*
				SELECT NVL(M.RECORD_PROG_STAT,'') AS RECORD_PROG_STAT
				      ,M.SLAB_NO 
				      ,M.MSLAB_NO
				      ,NVL((
				            SELECT A.STL_NO
				              FROM USRPTA.TB_PT_STLFRTOMOVE A
				            WHERE A.TRANSWORD_SEQNO =(SELECT MAX(TRANSWORD_SEQNO)
				                                        FROM TB_PT_STLFRTOMOVE B
				                                      WHERE A.STL_NO=B.STL_NO
				                                      )
				              AND A.STL_NO=M.MSLAB_NO
				              AND A.FRTOMOVE_STAT_CD='3'
				              AND A.ARR_WLOC_CD='D2Y43'
				      ),'N') AS TRANSWORD_YN
				      ,M.CURR_PROG_CD
				FROM   TB_PT_SLABCOMM M
				WHERE  M.SLAB_NO = :V_STL_NO 
			 */
			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setField("STL_NO", gdReq.getParam("STL_NO"));
			rsResult = commDao.select(
						jrParam
						, "com.inisteel.cim.yf.aslab.dao.ASlabDAO.aslabCommDtlInqjl.getSlabcommRecordProgStat"
						, logId
						, methodNm
						, "박판Slab 진도 및 이송지시 여부 확인");
			
			if(rsResult.size() > 0) {
				String sCurprogStat = rsResult.getRecord(0).getFieldString("CURR_PROG_CD");
				String sTranswordYn = rsResult.getRecord(0).getFieldString("TRANSWORD_YN");
				
				// 진도가 E(이송지시대기) 이송지시여부가 아닐경우 오류처리
				if( !YfConstant.CURR_PROG_CD_SLAB_E.equals(sCurprogStat)){
					// 유형별 메시지 셋팅 
					message = "현재 진도코드가 이송작업대기(E)만 수정가능합니다.";
					commUtils.printLog(logId, methodNm, message);
//					throw new Exception("현재 진도코드가 이송작업대기(E)만 수정가능합니다.");	
				}
				// 이송지시 존재여부 확인( 존재시 재료번호(Salb) )
				if("N".equals(sTranswordYn)){
					message = "이송지시가 있는 경우에만 Slab정정작업대기(A) 상태로 변경 가능합니다.";
					commUtils.printLog(logId, methodNm, message);
//					throw new Exception("이송지시가 있는 경우에만 Slab정정작업대기(A) 상태로 변경 가능합니다.");
				}
				
			}else{
				message = "SLAB정보가 존재하지 않습니다.";
				commUtils.printLog(logId, methodNm, message);
//				throw new Exception("SLAB정보가 존재하지 않습니다.");
			}
			
			if("".equals(message)){
				// Slab 진도코드 변경(정정대기)
				EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
				jrRtn = (JDTORecord)ejbConn.trx("updateSlabProgStat", new Class[] { GridData.class }, new Object[] { gdReq });
				
				//전송할 Data가 있으면 전송 처리
				if (jrRtn != null) {
					jrRtn.setResultCode(logId);
					jrRtn.setResultMsg(methodNm);

					EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
				}					
			}

			gdRet.setMessage(message);
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of procCreateScrap
	
	/**
	 * 야드및설비  열 정보수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSlabYdStkPosSet(GridData gdReq) throws DAOException {
		String methodNm =  "야드및설비 열정보수정[ASlabJspFaEJB.updSlabYdStkPosSet]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
					            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
					            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updSlabYdStkPosSet", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
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
		
	}	// end of updSlabYdStkPosSet
	
	/**
	 * 야드및설비 베드정보수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSlabYdStkPosSetBed(GridData gdReq) throws DAOException {
		String methodNm =  "야드및설비 베드정보수정[ASlabJspFaEJB.updSlabYdStkPosSetBed]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
					            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
					            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updSlabYdStkPosSetBed", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommSeEJB", this);
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
		
	}	// end of updSlabYdStkPosSetBed
	
	/**
	 * 적치기준 변경 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStockRule(GridData gdReq) throws DAOException {
		String methodNm =  "적치기준 변경[ASlabJspFaEJB.updStockRule]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_0);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ASlabJspSeEJB", this);
			
			//적치기준 변경
			ejbConn.trx("updStockRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updStockRule
////////////////////////////////////
//	김광철작업종료
//////////////////////////////////
}

