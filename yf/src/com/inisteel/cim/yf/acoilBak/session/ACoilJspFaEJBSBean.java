/**
 * @(#)ACoilJspFaEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      박판열연 COIL 야드 화면관리 Facade EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.yf.acoilBak.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yf.acoilBak.YfCommUtils;
import com.inisteel.cim.yf.acoilBak.YfConstant;
import com.inisteel.cim.yf.acoilBak.YfQueryIFOld;
import com.inisteel.cim.yf.acoilBak.dao.YfCommDAO;

/**
 *      [A] 클래스명 : 박판열연 COIL 야드 화면관리 Facade EJB
 *
 * @ejb.bean name="ACoilJspBakFaEJB" jndi-name="ACoilJspBakFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class ACoilJspFaEJBSBean extends BaseSessionBean implements YfQueryIFOld
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
		String methodNm =  "조회[ACoilJspBakFaEJB.getSelectData(GridData)]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try 
		{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
			                    + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
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
		String methodNm =  "조회[ACoilJspBakFaEJB.getSelectData(JDTORecord)]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);;
		
		try 
		{
			methodNm = methodNm + " < " + commUtils.trim(recPara.getFieldString("jsp_page_nm")) 
			                    + "("   + commUtils.trim(recPara.getFieldString("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(recPara.getFieldString("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", recPara);

			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
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
	 * 그리드의 선택된 행에 대해서 단순 업데이틀 수행
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	
	public GridData updateGridData(GridData gdReq) throws DAOException{
		String methodNm = "업데이트[AcoilJspFaEJB.updateGridData(GridData)]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try{
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm"))
								+ "("   + commUtils.trim(gdReq.getParam("jsp_page_id"))
								+ ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));
			commUtils.printLog(logId , methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default" , "ACoilJspBakSeEJB", this);
			ejbConn.trx("updateGridData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");
			
			return gdReq;
		}catch(DAOException e){
			throw e;
		}catch(Exception e){
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}
	
	/**
	 * 저장영역별검색순서조회 - 저장 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStrAreaSrchSeq(GridData gdReq) throws DAOException {
		String methodNm =  "저장영역별검색순서조회 - 저장[ACoilJspBakFaEJB.updStrAreaSrchSeq]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//저장영역별검색순서 저장
			ejbConn.trx("updStrAreaSrchSeq", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRtn = gdReq;
			
			if(!"Y".equals(gdReq.getParam("autoSaveChk"))){
				gdRtn = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	//end of updStrAreaSrchSeq
	
	/**
	 * ZONE별검색순서조회 - 저장 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStrZoneList(GridData gdReq) throws DAOException {
		String methodNm =  "ZONE별검색순서조회 - 저장[ACoilJspBakFaEJB.updStrZoneList]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//저장영역별검색순서 저장
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updStrZoneList", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRtn = gdReq;
			
			if(!"Y".equals(gdReq.getParam("autoSaveChk"))){
				gdRtn = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			}
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	//end of updStrZoneList
	
	/**
	 * ZONE별버전관리 - 저장 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStrZoneList1(GridData gdReq) throws DAOException {
		String methodNm =  "ZONE별버전관리조회 - 저장[ACoilJspBakFaEJB.updStrZoneList]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//저장영역별검색순서 저장
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updStrZoneList1", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRtn = gdReq;
			
			
			gdRtn = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	//end of updStrZoneList
	
	
	/**
	 * ZONE별검색순서조회 - 삭제 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delZoneList(GridData gdReq) throws DAOException {
		String methodNm =  "ZONE별검색순서조회 - 삭제[ACoilJspBakFaEJB.delZoneList]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//저장영역별검색순서 저장
			ejbConn.trx("delZoneList", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRtn = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	//end of updStrZoneList
	
	/**
	 * ZONE별 버전삭제 - 삭제 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData delZoneVerId(GridData gdReq) throws DAOException {
		String methodNm =  "ZONE별 버전 삭제 - 삭제[ACoilJspBakFaEJB.delZoneVerId]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//저장영역별검색순서 저장
			ejbConn.trx("delZoneVerId", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRtn = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	//end of updStrZoneList
	/**
	 * 선택 존 적용 - 삭제후저장 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData applyZoneVer(GridData gdReq) throws DAOException {
		String methodNm =  "선택 존 적용  - 삭제후저장[ACoilJspBakFaEJB.ApplyZoneVer]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//저장영역별검색순서 저장
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("applyZoneVer", new Class[] { GridData.class }, new Object[] { gdReq });
			
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			GridData gdRtn = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	//end of updStrZoneList
	
	
	/**
	 * 현재 ZONE 복사 - 복사 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData zoneCopy(GridData gdReq) throws DAOException {
		String methodNm =  " 현재 ZONE 복사 - 복사[ACoilJspBakFaEJB.zoneCopy]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//저장영역별검색순서 저장
			ejbConn.trx("zoneCopy", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRtn = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	//end of updStrZoneList
	
	/**
	 * 버전 추가 - 저장 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData insAddVerion(GridData gdReq) throws DAOException {
		String methodNm =  " 버전추가 저장 -저장 [ACoilJspBakFaEJB.insAddVerion]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			
			ejbConn.trx("insAddVerion", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRtn = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	//end of updStrZoneList
	

	
	/**
	 * 버전 타이틀 - 저장 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	
	public GridData updNewTitle(GridData gdReq) throws DAOException {
		String methodNm =  " 버전타이틀 -저장 [ACoilJspBakFaEJB.updNewTitle]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			
			ejbConn.trx("updNewTitle", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRtn = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	//end of updStrZoneList
	
	
	/**
	 * 크레인스케줄 기준 변경 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSchRuleMgt(GridData gdReq) throws DAOException {
		String methodNm =  "크레인스케줄 기준 변경[ACoilJspBakFaEJB.updSchRuleMgt]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
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
	 * 야드현황조회 존별 최대 적치율 - 저장 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updSchColor(GridData gdReq) throws DAOException {
		String methodNm =  "야드현황조회 존별 최대 적치율 - 저장[ACoilJspBakFaEJB.updZonMaxStcokRt]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//저장영역별검색순서 저장
			ejbConn.trx("updSchColor", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
	 * [A] 오퍼레이션명 : 크레인작업관리-크레인변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCraneChange(GridData gdReq) throws DAOException {
		String methodNm =  "크레인작업요구현황조회-크레인변경[ACoilJspBakFaEJB.updCraneChange]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCraneChange", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
//			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		String methodNm =  "크레인작업요구현황조회-순위변경[ACoilJspBakFaEJB.updPriorChange]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {	
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";		
			commUtils.printLog(logId, methodNm, "F+", gdReq);	
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updPriorChange", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
//			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		String methodNm =  "크레인작업요구현황조회-긴급작업[ACoilJspBakFaEJB.updPriorWrkChange]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updPriorWrkChange", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		String methodNm =  "크레인작업요구현황조회-권하위치변경[ACoilJspBakFaEJB.updDownLocChange]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updDownLocChange", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		String methodNm =  "크레인작업요구현황조회-작업취소[ACoilJspBakFaEJB.updCraneWrkCancel]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCraneWrkCancel", new Class[] { GridData.class }, new Object[] { gdReq });
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

	
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		String methodNm =  "크레인작업요구현황조회-스케줄취소[ACoilJspBakFaEJB.updCraneSchCancel]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCraneSchCancel", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		String methodNm =  "크레인작업요구현황조회-스케줄재전송[ACoilJspBakFaEJB.reSndCrnSch]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("reSndCrnSch", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	 *      [A] 오퍼레이션명 : 권하위치변경가능 위치
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData
	 *      @return GridData
	 *      @throws DAOException
     */			
	public GridData getDownLocChange(GridData gdReq) throws DAOException {
		String methodNm = "권하위치변경가능 위치 - [ACoilJspBakFaEJB.getDownLocChange]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
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

	}
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업요구현황조회 - 권상권하처리 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData updbtCrnStsSetPp(GridData gdReq) throws DAOException {
		String methodNm = "크레인상태설정[ACoilJspBakFaEJB.updbtCrnStsSetPp]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updbtCrnStsSetPp", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
	
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	 * 코일제품차량작업 관리- 입동순서변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData procCoilYdGdsBayInWoSeqChangCoil(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "차량작업 포인트 현황 - 입동순서변경[ACoilJspBakFaEJB.procCoilYdGdsBayInWoSeqChangCoil]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1); 
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn =  new EJBConnector("default", "ACoilJspBakSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procCoilYdGdsBayInWoSeqChangCoil", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	 * 코일제품차량작업 관리- 포인트 개폐
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return gdRet
	 * @throws JDTOException
	 */
	public GridData procCoilYdGdsPntUnitCLCoil(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "차량작업 포인트 현황 - 포인트 개폐[ACoilJspBakFaEJB.procCoilYdGdsPntUnitCLCoil]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
			
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procCoilYdGdsPntUnitCLCoil", new Class[] { GridData.class }, new Object[] { gdReq });
			
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		String methodNm =  "차량입동위치변경[ACoilJspBakFaEJB.changeCarLoc]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
			
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("changeCarLoc", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	 * 코일제품차량작업 관리- 출하차량도착
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData CarArrivalNEW(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "차량작업 포인트 현황 - 출하차량도착[CarArrivalNEW]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("CarArrivalNEW", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	 * 코일제품차량작업 관리- 출발처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData updCarStart(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "배차내역 - 출발처리[updCarStart]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCarStart", new Class[] { GridData.class }, new Object[] { gdReq });
			
			
			//조회
			//GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	 * 차량작업관리 - 전체입동제한
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData procAllCarPntYnReg(GridData gdReq) throws DAOException {
		String methodNm = "전체입동제한[ACoilJspBakFaEJB.procAllCarPntYnReg]";
		String logId  = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+");

			EJBConnector ejbConn	= new EJBConnector("default", "ACoilJspBakSeEJB", this);
			ejbConn.trx("procAllCarPntYnReg", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			commUtils.printLog(logId, methodNm, "F-");
			
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 제품이송우선순위 변경 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilCarMovYn(GridData gdReq) throws DAOException {
		//LOG
		String methodNm			= "차량Point작업현황 - 제품이송우선순위 변경 [ACoilJspBakFaEJB.getCoilCarMovYn]";
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		String sRTN_CD	="";
		String sRTN_MSG ="";
		String szMethodName = "[CoilGdsJspFaEjbBean.getCoilCarMovYn]";
		String logId 			= gdReq.getIPAddress();
		String szLogMsg = ""; 
		
		try{
			szLogMsg = "JSP-FACADE [ " + szMethodName +"] 시작";
			commUtils.printLog(logId, szLogMsg, "SL");	
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			outRecord 	= (JDTORecord)ejbConn.trx("getCoilCarMovYn", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			szLogMsg = "JSP-FACADE [ " + szMethodName +"] 끝";
			commUtils.printLog(logId, szLogMsg, "SL");	
			
			gdRet.setMessage(sRTN_MSG);
			return gdRet;
			
		} catch(DAOException e) {
			throw e;			
		}catch(Exception e){
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	} //end of getCoilCarMovYn
	
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

			for (int ii = 0; ii < rowCnt; ii++) {
				inRecord2 	= JDTORecordFactory.getInstance().create();
				inRecord2.setField("YD_GP"				, commUtils.getValue(gdReq, "YD_GP", ii));
				inRecord2.setField("CMBN_CARLD_YN"		, commUtils.getValue(gdReq, "CMBN_CARLD_YN", ii));
				inRecord2.setField("WORK_GP"			, commUtils.getValue(gdReq, "WORK_GP", ii));
				inRecord2.setField("TEL_NO"				, commUtils.getValue(gdReq, "TEL_NUMBER", ii));
				inRecord2.setField("TRANS_ORD_DT"		, commUtils.getValue(gdReq, "TRANS_ORD_DT", ii));
				inRecord2.setField("TRANS_ORD_SEQNO"	, commUtils.getValue(gdReq, "TRANS_ORD_SEQNO", ii));
				inRecord2.setField("CAR_NO"				, commUtils.getValue(gdReq, "CAR_NO", ii));
				inRecord2.setField("CARD_NO"			, commUtils.getValue(gdReq, "CARD_NO", ii));
				inRecord2.setField("WAIT_ARR_DDTT"		, commUtils.getValue(gdReq, "WAIT_ARR_DDTT", ii));
				inRecord2.setField("WAIT_ARR_GP"		, commUtils.getValue(gdReq, "WAIT_ARR_GP", ii));
				
				ejbConn = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
				jrRst =	(JDTORecord)ejbConn.trx("rcvDMYDR061", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });
	
			}
			
			szMsg = "[JSP Facade] 차량작업관리 대기장도착처리 전송처리 ===> 끝";
			commUtils.printLog(logId, szMsg, "SL");	
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			 
		}catch(Exception e){

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			
		}
		return gdRes;
	}  //end of getStandByYdArrive

	/**
	 * YF-RULE 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData modiYfRule(GridData gdReq) throws DAOException {
		String methodNm =  "YF-RULE 수정[ACoilJspBakFaEJB.modiYfRule]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			

			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("modiYfRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	 * <pre>
	 * 		배차차량 작업관리 - 초기화
	 *       * 크레인스케쥴이 선행되어야 함
	 * 
	 *       1. 구내운송차량 
	 *         - 공차(상차)일 경우 
	 *           : 차량스케쥴, 이적재료, 작업예약 삭제처리
	 *       2. 출하차량
	 *         -
	 * </pre>
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData updCarWrMgt(GridData gdReq) throws DAOException {
		String methodNm =  "배차차량작업관리 - 초기화[ACoilJspBakFaEJB.updCarWrMgt]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		 
		try{

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			String modifier  = commUtils.trim(gdReq.getParam("userid")); //수정자
			
			EJBConnector ejbConn 	= null;
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			JDTORecord jrRtn 		= JDTORecordFactory.getInstance().create();
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				
				if("구내운송".equals(commUtils.getValue(gdReq, "YD_CAR_USE_NM", ii))) {

					//위치정보 송신
					sndL2Msg = JDTORecordFactory.getInstance().create();
					sndL2Msg.setResultCode(logId);		//Log ID
					sndL2Msg.setResultMsg(methodNm);	//Log Method Name
					sndL2Msg.setField("YD_INFO_SYNC_CD",		"3");	//야드정보동기화코드
					sndL2Msg.setField("YD_STK_COL_GP",			commUtils.getValue(gdReq, "YD_STK_COL_GP"	, ii));
					sndL2Msg.setField("YD_STK_BED_NO",			"01");
					sndL2Msg.setField("YD_CAR_ARRSTRT_STAT",	"S");	//A:도착, S:출발
					sndL2Msg.setField("YD_CAR_USE_GP",			"L");	//L:구내운송, G:출하차량
					sndL2Msg.setField("YD_EQP_WRK_STAT",		commUtils.getValue(gdReq, "ITEM_CNT"	, ii).equals("0") ? "U" : "L");	//U:공차, L:영차
					sndL2Msg.setField("TRN_EQP_CD",				commUtils.getValue(gdReq, "CAR_NO"			, ii));	//운송장비코드
					sndL2Msg.setField("YD_CAR_AIM_YD_GP",		"1");
					
					//전송 Data 생성
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L001_CarInfo", sndL2Msg));
					
					//TSYDJ004
					sndL2Msg.setField("TC_CODE"        	, "TSYDJ004"); 
					sndL2Msg.setField("TRN_EQP_CD"			, commUtils.getValue(gdReq, "CAR_NO", ii));
					sndL2Msg.setField("TRN_WRK_FULLVOID_GP", "E");
					sndL2Msg.setField("BACKUP_YN"			, "Y");
					sndL2Msg.setField("WLOC_CD"			, commUtils.getValue(gdReq, "WLOC_CD", ii));
						
					ejbConn = new EJBConnector("default", "YfCommCarMvSeEJB", this);
					sndL2Msg 	= (JDTORecord)ejbConn.trx("initCarSch", new Class[] { JDTORecord.class }, new Object[] { sndL2Msg });
					
					jrRtn = commUtils.addSndData(jrRtn,sndL2Msg);

				} else {
					//DMYDR060 송신
					//위치정보 송신
					sndL2Msg = JDTORecordFactory.getInstance().create();
					sndL2Msg.setResultCode(logId);		//Log ID
					sndL2Msg.setResultMsg(methodNm);	//Log Method Name
					sndL2Msg.setField("YD_INFO_SYNC_CD",	"3");	//야드정보동기화코드
					sndL2Msg.setField("YD_STK_COL_GP",		commUtils.getValue(gdReq, "YD_STK_COL_GP"	, ii));
					sndL2Msg.setField("YD_STK_BED_NO",			"01");
					sndL2Msg.setField("YD_CAR_ARRSTRT_STAT",	"S");	//A:도착, S:출발
					sndL2Msg.setField("YD_CAR_USE_GP",			"G");	//L:구내운송, G:출하차량
					sndL2Msg.setField("YD_EQP_WRK_STAT",		commUtils.getValue(gdReq, "ITEM_CNT"	, ii).equals("0") ? "U" : "L");	//U:공차, L:영차
					sndL2Msg.setField("TRN_EQP_CD",				commUtils.getValue(gdReq, "CAR_NO"			, ii));	//운송장비코드
					sndL2Msg.setField("YD_CAR_AIM_YD_GP",		"1");
					
					//전송 Data 생성
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L001_CarInfo", sndL2Msg));
					
					sndL2Msg.setField("TC_CODE"        , "DMYDR060"); 
					sndL2Msg.setField("CURR_PROG_CD"	, "N"		);
					sndL2Msg.setField("YD_GP"			, commUtils.getValue(gdReq, "YD_GP"				, ii));
					sndL2Msg.setField("TRANS_ORD_DT"	, commUtils.getValue(gdReq, "TRANS_ORD_DT"		, ii));
					sndL2Msg.setField("TRANS_ORD_SEQNO", commUtils.getValue(gdReq, "TRANS_ORD_SEQNO"	, ii));
					sndL2Msg.setField("CAR_NO"			, commUtils.getValue(gdReq, "CAR_NO"			, ii));
					sndL2Msg.setField("CARD_NO"		, commUtils.getValue(gdReq, "CARD_NO"			, ii));
					
					ejbConn = new EJBConnector("default", "YfCommBakSeEJB", this);
					sndL2Msg 	= (JDTORecord)ejbConn.trx("receiveCancel", new Class[] { JDTORecord.class }, new Object[] { sndL2Msg });
					
					jrRtn = commUtils.addSndData(jrRtn,sndL2Msg);
				}
				
				// 크레인스케쥴은 삭제되었는데 작업예약이 남아 있을 경우
				// 동일하게 취소처리 시작한다.
			}
			
			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
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
	 * 하차작업등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regCarUdWrk(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "하차작업등록[ACoilJspBakFaEJB.regCarUdWrk]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("regCarUdWrk", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	 * 차량예정정보 전송
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData regCarUdExplainInfo(GridData gdReq) throws JDTOException {
		//		LOG
		String methodNm =  "차량예정정보 전송[ACoilJspBakFaEJB.regCarUdExplainInfo]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		EJBConnector ejbConn = null;
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("regCarUdExplainInfo", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		String methodNm = "크레인상태설정[ACoilJspBakSeEJB.procCrnWrkBookStart]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("procCrnWrkBookStart", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		String methodNm =  "작업예약삭제[ACoilJspBakSeEJB.delWrkBook]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		JDTORecord sndRecord = null;
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			sndRecord = (JDTORecord)ejbConn.trx("delWrkBook", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (sndRecord != null) {
				sndRecord.setResultCode(logId);
				sndRecord.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	 * 야드설비정비등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData insEqpTrblReg(GridData gdReq) throws DAOException {
		String methodNm =  "야드설비정비등록[ACoilJspBakFaEJB.insEqpTrblReg]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("insEqpTrblReg", new Class[] { GridData.class }, new Object[] { gdReq });
						
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//야드설비정비이력관리 조회
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
	 * CTS수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procYfCTSModify(GridData gdReq) throws DAOException {
		String methodNm =  "CTS수정[ACoilJspBakFaEJB.procYfCTSModify]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			//야드설비정비상태 변경
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("procYfCTSModify", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
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
		
	}	// end of procYfCTSModify
	
	/**
	 * 야드설비정비상태 변경
	 *  - 사용안함 2020. 02. 17
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData destinationSaddle(GridData gdReq) throws DAOException {
		String methodNm =  "SADDLE 작업[ACoilJspBakFaEJB.destinationSaddle]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			//야드설비정비상태 변경
			JDTORecord jrRst = (JDTORecord) ejbConn.trx("destinationSaddle", new Class[] { GridData.class }, new Object[] { gdReq });
			
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });			
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of destinationSaddle
	
	/**
	 *  상차정보조회 - 상차위치 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData carLiftPosSet(GridData gdReq) throws DAOException {
		String methodNm =  "상차위치변경 [ACoilJspBakFaEJB.carLiftPosSet]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
		
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
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
	 *  분기컨베이어 - Trun Table 고장처리,복구처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData trunTableBR(GridData gdReq) throws DAOException {
		String methodNm =  "Trun Table 고장처리,복구처리 - 저장[ACoilJspBakFaEJB.trunTableBR]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		GridData gdRet = null;
		try {
			gdRet = OperateGridData.cloneResponseGridData(gdReq);
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			//코일이 적치된 동과 적치상태 체크 --------------------------------------------------------------------------
			jrParam.setField("STL_NO", gdReq.getParam("STL_NO"));
			JDTORecordSet rsResult = commDao.select(jrParam, checkDcCnvyrByStl, logId, methodNm, "컨베이어 코일체크");
			
			if(rsResult.size() > 0) {
				gdRet.setMessage(rsResult.getRecord(0).getFieldString("MSG"));
			}
	
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
					
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("trunTableBR", new Class[] { GridData.class }, new Object[] { gdReq });
			
			
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		
	}	//end of updStrZoneList
	
	/**
	 *  분기컨베이어 -B동LineOff영순위변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData updLineOffSeq(GridData gdReq) throws DAOException {
		String methodNm =  "B동LineOff영순위변경 - [ACoilJspBakFaEJB.updLineOffSeq]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
					
			ejbConn.trx("updLineOffSeq", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	//end of updStrZoneList	
	
	/**
	 *  분기컨베이어 - 난방코일처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData upheatingCoilYN(GridData gdReq) throws DAOException {
		String methodNm =  "heatingCoilYN 난방코일처리 - 저장[ACoilJspBakFaEJB.upheatingCoilYN]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
					
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("upheatingCoilYN", new Class[] { GridData.class }, new Object[] { gdReq });
	
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	//end of updStrZoneList	
	
	
	/**
	 * 분기/확장 Conv - Line-Off 요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqLineOff(GridData gdReq) throws DAOException{
		String methodNm =  "분기/확장 Conv - Line-Off 요구[ACoilJspBakFaEJB.reqLineOff]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		GridData gdRet = null;
		try {

			gdRet = OperateGridData.cloneResponseGridData(gdReq);
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);
			jrParam.setResultMsg(methodNm);
			JDTORecord jrRst = null;
			
	
			//코일이 적치된 동과 적치상태 체크 --------------------------------------------------------------------------
			jrParam.setField("STL_NO", gdReq.getParam("STL_NO"));
			JDTORecordSet rsResult = commDao.select(jrParam, checkDcCnvyrByStl, logId, methodNm, "컨베이어 코일체크");
			
			if(rsResult.size() > 0) {
				gdRet.setMessage(rsResult.getRecord(0).getFieldString("MSG"));
			}
			
			if("".equals(gdRet.getMessage())){
				//분기 컨베이어 Line Off
				jrParam.setField("JMS_TC_CD"	, "F1YFL041" );
				jrParam.setField("STL_NO"		, gdReq.getParam("STL_NO") );
				
				// LINE OFF시 동까지만 전달해준다. Take In은 무조건 C동, 고장처리도 동일하게 C동
				jrParam.setField("LOCATION"		, gdReq.getParam("LOCATION") );

				// BIZ_GP ( I: Take In, T : Take Out, L : Line Off, H : Hot Coil Line Off
				jrParam.setField("BIZ_GP"		, gdReq.getParam("BIZ_GP") );
				jrParam.setField("BACK_YN"		, "Y" );   //백업화면 기동 여부

				EJBConnector ejbConn = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvF1YFL041", new Class[] { JDTORecord.class }, new Object[] { jrParam });	
				
				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);

					EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
				
			}

			commUtils.printLog(logId, methodNm, "F-");

			return gdRet;
			
		}catch(DAOException e) {
			throw e;
		}catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
   }		
	/**
	 * HFL/SPM - TakeIn 요구 (5)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqTakeIn(GridData gdReq) throws DAOException {
		String methodNm =  "HFL/SPM - TakeIn 요구[ACoilJspBakFaEJB.reqTakeIn]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
			String sRTN_MSG = "";
			
			//코일이 적치된 동과 적치상태 체크 --------------------------------------------------------------------------
			jrParam.setField("STL_NO", gdReq.getParam("COIL_NO"));
			JDTORecordSet rsResult = commDao.select(jrParam, getListCoilLoc, logId, methodNm, "코일이 적치된 동과 적치상태 체크 "); 
			if(rsResult.size() > 0) {
			
				String sBAY_GP 				= rsResult.getRecord(0).getFieldString("BAY_GP");
				String sYD_STK_LYR_STAT 	= rsResult.getRecord(0).getFieldString("YD_STK_LYR_STAT");
				
				if( YfConstant.EQP_WORK_ID_L3_HFL.equals(gdReq.getParam("WORKID"))) {
					//HFL B동에 적치된 코일만 가능
					if(!( YfConstant.BAY_GP_B.equals(sBAY_GP) && YfConstant.STACK_LAYER_STAT_C.equals(sYD_STK_LYR_STAT))) {
						sRTN_MSG = "Take In 불가능 코일입니다!! 동구분:"+sBAY_GP+",적치상태:"+sYD_STK_LYR_STAT;
					}
				} else if(YfConstant.EQP_WORK_ID_L3_SPM.equals(gdReq.getParam("WORKID"))) {
					//SPM D동에 적치된 코일만 가능
					if(!( (YfConstant.BAY_GP_D.equals(sBAY_GP) || YfConstant.BAY_GP_E.equals(sBAY_GP)) && YfConstant.STACK_LAYER_STAT_C.equals(sYD_STK_LYR_STAT))) {
						sRTN_MSG = "Take In 불가능 코일입니다!! 동구분:"+sBAY_GP+",적치상태:"+sYD_STK_LYR_STAT;
					}
				} else if(YfConstant.EQP_WORK_ID_L3_EQL.equals(gdReq.getParam("WORKID"))) {
					//EQL E동에 적치된 코일만 가능
					if(!( (YfConstant.BAY_GP_E.equals(sBAY_GP)||YfConstant.BAY_GP_F.equals(sBAY_GP)) && YfConstant.STACK_LAYER_STAT_C.equals(sYD_STK_LYR_STAT))) {
						sRTN_MSG = "Take In 불가능 코일입니다!! 동구분:"+sBAY_GP+",적치상태:"+sYD_STK_LYR_STAT;
					}
				} 
				
			} else {
				sRTN_MSG = "야드(적치단)에 없는 Coil번호 입니다!!";
				gdRet.setMessage(sRTN_MSG);
			}
			//---------------------------------------------------------------------------------------------------
			
			if(!"".equals(sRTN_MSG)){
				gdRet.setMessage(sRTN_MSG);
			}
			else
			{
				
				if( YfConstant.EQP_WORK_ID_L3_HFL.equals(gdReq.getParam("WORKID"))
						|| YfConstant.EQP_WORK_ID_L3_SPM.equals(gdReq.getParam("WORKID"))
						|| YfConstant.EQP_WORK_ID_L3_EQL.equals(gdReq.getParam("WORKID"))
				) {
					// "YardId"		 	야드 구분
					// "WorkId"		 	공정코드 S:SPM,H:HFL,D:결속대   // 2SPM  처리
					// "ProcessId"	 	1 보급, 2 보급취소, 3 추출, 4 Take-Out, 5 Take-In
					// "CoilNo"		 	'S' Scrap  'H' A열연  'K' B열연
					// "Position"		조업에서 수신한 위치정보를 야드 적치열에 대한  위치로 변환해야됨
					// "TakeOutProcess" Take-Out시  1:결번,2:임시보류처리(잠시 내려놨다가 Take-In할 Coil)
					
					jrParam.setField("JMS_TC_CD"	, YfConstant.POYMJ004 ); // JMS :: COIL 정정 / HFL 작업요구
					jrParam.setField("YardId"		, gdReq.getParam("YD_GP") ); // 1 : 박판열연
					jrParam.setField("WorkId"		, getEqpWorkId(gdReq.getParam("WORKID")) );
					jrParam.setField("ProcessId"	, gdReq.getParam("PROCESSID") ); //5
					jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
//					jrParam.setField("TAKE_IN_GP"   , gdReq.getParam("TAKE_IN_GP"));
					jrParam.setField("Position"		, gdReq.getParam("POSITION"));
					jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
					
					EJBConnector ejbConn = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
					jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				} 
				
				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);
	
					EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
				
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
	 * HFL/SPM - TakeOut 요구 (4)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqTakeOut(GridData gdReq) throws DAOException {
		String methodNm =  "HFL/SPM - TakeOut 요구[ACoilJspBakFaEJB.reqTakeOut]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
	
			if( YfConstant.EQP_WORK_ID_L3_HFL.equals(gdReq.getParam("WORKID"))
					|| YfConstant.EQP_WORK_ID_L3_SPM.equals(gdReq.getParam("WORKID"))
					|| YfConstant.EQP_WORK_ID_L3_EQL.equals(gdReq.getParam("WORKID"))
			) {
				//HFL, SPM 
				jrParam.setField("JMS_TC_CD"	, YfConstant.POYMJ004 ); // JMS :: COIL 정정 / HFL 작업요구
				jrParam.setField("YardId"		, gdReq.getParam("YD_GP") );
				jrParam.setField("WorkId"		, getEqpWorkId(gdReq.getParam("WORKID")) );
				jrParam.setField("ProcessId"	, gdReq.getParam("PROCESSID") ); //4
				jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
				jrParam.setField("Position"		, gdReq.getParam("POSITION")); //소스확인결과 if문에서 5,6,7 인지만 확인 하고 사용 안하는 파라메터
//				jrParam.setField("TakeOutProcess", "2"); // moveHFL_SPM_Takeip.jsp 내 하드코딩 ( 2019.09.16 [확인사항] )
				jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
				
				EJBConnector ejbConn = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			}
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		String methodNm =  "HFL/SPM/EQL - 보급 요구[ACoilJspBakFaEJB.reqSupply]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
			String sRTN_MSG = "";
			
			//코일이 적치된 동과 적치상태 체크 --------------------------------------------------------------------------
			jrParam.setField("STL_NO", gdReq.getParam("COIL_NO"));
			JDTORecordSet rsResult = commDao.select(jrParam, getListCoilLoc, logId, methodNm, "코일이 적치된 동과 적치상태 체크 "); 
			if(rsResult.size() > 0) {
			
				String sBAY_GP 				= rsResult.getRecord(0).getFieldString("BAY_GP");
				String sYD_STK_LYR_STAT 	= rsResult.getRecord(0).getFieldString("YD_STK_LYR_STAT");
				
				if(YfConstant.EQP_WORK_ID_L3_HFL.equals(gdReq.getParam("WORKID"))) {
					if(!(YfConstant.BAY_GP_B.equals(sBAY_GP) && YfConstant.STACK_LAYER_STAT_C.equals(sYD_STK_LYR_STAT))) {
						sRTN_MSG = "보급 불가능 코일입니다!! 동구분:"+sBAY_GP+",적치상태:"+sYD_STK_LYR_STAT;
					}
				} else if(YfConstant.EQP_WORK_ID_L3_SPM.equals(gdReq.getParam("WORKID"))) {
					if(!((YfConstant.BAY_GP_D.equals(sBAY_GP) || YfConstant.BAY_GP_E.equals(sBAY_GP)) && YfConstant.STACK_LAYER_STAT_C.equals(sYD_STK_LYR_STAT))) {
						sRTN_MSG = "보급 불가능 코일입니다!! 동구분:"+sBAY_GP+",적치상태:"+sYD_STK_LYR_STAT;
					}
				} else if(YfConstant.EQP_WORK_ID_L3_EQL.equals(gdReq.getParam("WORKID"))) {
					if(!((YfConstant.BAY_GP_E.equals(sBAY_GP) || YfConstant.BAY_GP_F.equals(sBAY_GP)) && YfConstant.STACK_LAYER_STAT_C.equals(sYD_STK_LYR_STAT))) {
						sRTN_MSG = "보급 불가능 코일입니다!! 동구분:"+sBAY_GP+",적치상태:"+sYD_STK_LYR_STAT;
					}
				}
				
			} else {
				sRTN_MSG = "야드(적치단)에 없는 Coil번호 입니다!!";
				gdRet.setMessage(sRTN_MSG);
			}
			//---------------------------------------------------------------------------------------------------
			
			if(!"".equals(sRTN_MSG)){
				gdRet.setMessage(sRTN_MSG);
			}
			else
			{
				
				if( YfConstant.EQP_WORK_ID_L3_HFL.equals(gdReq.getParam("WORKID"))
						|| YfConstant.EQP_WORK_ID_L3_SPM.equals(gdReq.getParam("WORKID"))
						|| YfConstant.EQP_WORK_ID_L3_EQL.equals(gdReq.getParam("WORKID"))
				) {
					
					//HFL, SPM, EQL
					jrParam.setField("JMS_TC_CD"	, YfConstant.POYMJ004 ); // JMS :: COIL 정정 / HFL 작업요구
					jrParam.setField("YardId"		, gdReq.getParam("YD_GP") );
					jrParam.setField("WorkId"		, getEqpWorkId(gdReq.getParam("WORKID")) );
					jrParam.setField("ProcessId"	, gdReq.getParam("PROCESSID") ); //1
					jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
					jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
					
					EJBConnector ejbConn = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
					jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				}
				
				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);
	
					EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
			
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
	 * HFL/SPM/EQL - 보급취소 요구 (2)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqCancelSupply(GridData gdReq) throws DAOException {
		String methodNm =  "HFL/SPM/EQL - 보급취소 요구[ACoilJspBakFaEJB.reqCancelSupply]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			 
			if( YfConstant.EQP_WORK_ID_L3_HFL.equals(gdReq.getParam("WORKID"))
					|| YfConstant.EQP_WORK_ID_L3_SPM.equals(gdReq.getParam("WORKID"))
					|| YfConstant.EQP_WORK_ID_L3_EQL.equals(gdReq.getParam("WORKID"))
			) {
				 
				//HFL, SPM 
				jrParam.setField("JMS_TC_CD"	, YfConstant.POYMJ004 ); // JMS :: COIL 정정 / HFL 작업요구
				jrParam.setField("YardId"		, gdReq.getParam("YD_GP") );
				jrParam.setField("WorkId"		, getEqpWorkId(gdReq.getParam("WORKID")) ); //F:HFL, K:SPM, Q:EQL
				jrParam.setField("ProcessId"	, gdReq.getParam("PROCESSID") ); //2
				jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
				jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
				
				EJBConnector ejbConn = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			}
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
//				sndConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
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
		String methodNm =  "HFL/SPM/EQL - 추출 요구[ACoilJspBakFaEJB.reqExtraction]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
	
			if( YfConstant.EQP_WORK_ID_L3_HFL.equals(gdReq.getParam("WORKID"))
					|| YfConstant.EQP_WORK_ID_L3_SPM.equals(gdReq.getParam("WORKID"))
					|| YfConstant.EQP_WORK_ID_L3_EQL.equals(gdReq.getParam("WORKID"))
			) {
				
				//HFL, SPM , EQL
				jrParam.setField("JMS_TC_CD"	, YfConstant.POYMJ004 );
				jrParam.setField("YardId"		, gdReq.getParam("YD_GP") );
				jrParam.setField("WorkId"		, getEqpWorkId(gdReq.getParam("WORKID")) ); //F:HFL, K:SPM, Q:SPM2
				jrParam.setField("ProcessId"	, gdReq.getParam("PROCESSID") ); //3
				jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
				jrParam.setField("Position"		, gdReq.getParam("POSITION"));
				jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
				
				EJBConnector ejbConn = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
				jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			}
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		String methodNm =  "HFL/SPM/EQL - 정정실적 요구[ACoilJspBakFaEJB.reqWorkResult]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrRst = null;
	
			jrParam.setField("JMS_TC_CD"	, YfConstant.POYMJ001 );
			jrParam.setField("YardID"		, gdReq.getParam("YD_GP") );
//			jrParam.setField("WORKID"		, gdReq.getParam("WORKID") ); //F:HFL, K:SPM, Q:EQL
			jrParam.setField("ProcessID"	, gdReq.getParam("PROCESSID") ); //07:HFL, 02:SPM,SPM2
			jrParam.setField("CoilNo"		, gdReq.getParam("COIL_NO"));
			jrParam.setField("L3_HMI"		, "Y" );   //백업화면 기동 여부
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilRcvL3SeEJB", this);
			jrRst = (JDTORecord)ejbConn.trx("rcvPOYMJ001", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	
	private String getEqpWorkId(String sEqp){
		String rtnWrokId = "";
		if( YfConstant.EQP_WORK_ID_L3_HFL.equals(sEqp)){
			rtnWrokId = YfConstant.EQP_WORK_ID_HFL;
		}
		else if( YfConstant.EQP_WORK_ID_L3_SPM.equals(sEqp)){
			rtnWrokId = YfConstant.EQP_WORK_ID_SPM;
		}
		else if( YfConstant.EQP_WORK_ID_L3_EQL.equals(sEqp)){
			rtnWrokId = YfConstant.EQP_WORK_ID_EQL;
		}
		
		return rtnWrokId;
	}
	/**
	 * 스크랩비우기
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData procClearScrap(GridData gdReq) throws DAOException {
		String methodNm =  "스크랩현황조회-스크랩비우기[ACoilJspBakFaEJB.procClearScrap]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//정정검사메시지 - 수정
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procClearScrap", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		String methodNm =  "스크랩현황조회-스크랩생성[ACoilJspBakFaEJB.procCreateScrap]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//정정검사메시지 - 수정
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("procCreateScrap", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	 *      [A] 오퍼레이션명 : 이적작업 예약 등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData updblMvStkWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "이적작업 예약 등록[ACoilJspBakFaEJB.updblMvStkWrkBook]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);

		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			//크레인변경
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updblMvStkWrkBook", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	 * 차량동간이적 (이적지시 )
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updColUnitCarMvstkRegNew(GridData gdReq) throws DAOException {
		String methodNm =  "차량동간이적[ACoilJspBakFaEJB.updColUnitCarMvstkRegNew]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updColUnitCarMvstkRegNew", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		
	}	// end of updIfTestData

	/**
	 *      [A] 오퍼레이션명 : 화면에서 수동으로 F1YFL018 - [차량동간이적(도착)] 전문을 송신한다
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData
	 *      @return GridData
	 *      @throws DAOException
     */			
	public GridData sendF1YFL018(GridData gdReq) throws DAOException {
		String methodNm = "차량동간이적(도착) 송신 sendF1YFL018[ACoilJspBakFaEJB.sendF1YFL018]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			JDTORecord jrF1YFL018 = JDTORecordFactory.getInstance().create();
			
			jrF1YFL018.setField("JMS_TC_CD"		, "F1YFL018"); // 차량동간이적 도착(F1YFL018)
			jrF1YFL018.setField("PT_LOAD_LOC"	, gdReq.getParam("PT_LOAD_LOC"));	// 상차도 위치
			jrF1YFL018.setField("CAR_NO"		, gdReq.getParam("CAR_NO"));		// 차량번호
			jrF1YFL018.setField("CAR_UPDN_GP"	, gdReq.getParam("CAR_UPDN_GP"));	// 차량상하차구분 ? 1:상차, 2:하차
			jrF1YFL018.setField("MODIFIER"		, gdReq.getParam("MODIFIER"));		// 차량번호
			
			commUtils.printLog(logId, methodNm, jrF1YFL018.toString());
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("rcvF1YFL018", new Class[] { JDTORecord.class }, new Object[] { jrF1YFL018 });	// L2 작업지시 전송
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			
				commUtils.printLog(logId, methodNm, "F-");
			}
			
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}	// end of sendF1YFL018
	
	/**
	 * 코일공통상세조회-정정검사메시지 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updShearInspectMsg(GridData gdReq) throws DAOException {
		String methodNm =  "코일공통상세조회-정정검사메시지 수정[ACoilJspBakFaEJB.updShearInspectMsg]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//정정검사메시지 - 수정
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updShearInspectMsg", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			//GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	 * 위치별 적치현황조회 - Bed상태 수정 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updBedActStat(GridData gdReq) throws DAOException {
		String methodNm =  "위치별 적치현황조회 - Bed상태 수정[ACoilJspBakFaEJB.updBedActStat]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updBedActStat", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		
	}	//end of updBedActStat
	
	/**
	 * 대차스케줄관리 - 대차초기화
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData initTcarSchMgt(GridData gdReq) throws DAOException {
		String methodNm =  "대차스케줄관리 대차초기화[ACoilJspBakFaEJB.initTcarSchMgt]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//대차초기화
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("initTcarSchMgt", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		String methodNm = "대차상태설정[ACoilJspBakFaEJB.trtTcarStatSet]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);

		try { 
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";

			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);

			//대차상태설정
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtTcarStatSet", new Class[] { GridData.class }, new Object[] { gdReq });
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	 * 대차이동구간변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData updTCarYdGpMgt(GridData gdReq) throws JDTOException {
		String methodNm =  "대차이동구간변경[ACoilJspBakFaEJB.updTCarYdGpMgt]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
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
	 * 대차작업현황조회 - 최대적치매수 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStackMaxQnty(GridData gdReq) throws DAOException {
		String methodNm =  "대차작업현황조회-최대적치매수 수정[ACoilJspBakFaEJB.updStackMaxQnty]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//최대적치매수 - 수정
			ejbConn.trx("updStackMaxQnty", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
//			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
 
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
	 * [A] 오퍼레이션명 : 대차작업현황조회-순위변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData modPriorChange(GridData gdReq) throws DAOException {
		String methodNm =  "대차작업현황조회-순위변경[ACoilJspBakFaEJB.modPriorChange]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//크레인변경
			ejbConn.trx("modPriorChange", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		String methodNm =  "대차작업현황조회-작업예약삭제[ACoilJspBakFaEJB.delWrkBookDel]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		JDTORecord sndRecord = null;
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			sndRecord = (JDTORecord)ejbConn.trx("delWrkBookDel", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (sndRecord != null) {
				sndRecord.setResultCode(logId);
				sndRecord.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	 * [A] 오퍼레이션명 : 이송지시 취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updFtmvWrkCancel(GridData gdReq) throws DAOException {
		String methodNm =  "이송지시 취소[ACoilJspBakFaEJB.updFtmvWrkCancel]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//이송지시 취소(1) - YDPTJ007 전문 저송
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updFtmvWrkCancel", new Class[] { GridData.class }, new Object[] { gdReq });
	
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
			}
			
			//이송지시 취소(2) - 크레인스케줄,작업예약 ID 취소 처리
			jrRst = (JDTORecord)ejbConn.trx("updFtmvWrkCancel2", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		String methodNm =  "긴급작업[ACoilJspBakFaEJB.updUgntWrk]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {
			
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
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
		String methodNm =  "상차대상순위별조회 - SCH기동[ACoilJspBakFaEJB.reqCarldSchSt]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("reqCarldSchSt", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	 * 상차완료Backup처리 화면 : 초기화
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData initMvCarSchMgt(GridData gdReq) throws DAOException {
		String methodNm =  "상차완료Backup처리 화면 - 초기화[ACoilJspBakFaEJB.initMvCarSchMgt]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		 
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
					
				ejbConn = new EJBConnector("default", "YfCommCarMvSeEJB", this);
				jrRtn 	= (JDTORecord)ejbConn.trx("initCarSch", new Class[] { JDTORecord.class }, new Object[] { outRecord });
				jrRst 	= commUtils.addSndData(jrRst, jrRtn);
					
			}
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		String methodNm =  "하차백업생성[ACoilJspBakFaEJB.mkUdCarSch]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		 
		try{

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("mkUdCarSch", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	}  //end of mkUdCarSch
	
	/**
	 * 구내운송차량출발
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqTsStart(GridData gdReq) throws DAOException {
		String methodNm =  "구내운송차량출발 [ACoilJspBakFaEJB.reqTsStart]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
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
			
			
			EJBConnector ejbConn = new EJBConnector("default", "YfCommCarMvSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("rcvTSYDJ004", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		String methodNm =  "이송차량 실적처리 팝업 - 등록[ACoilJspBakFaEJB.trtMvCarStatSet2]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtMvCarStatSet2", new Class[] { GridData.class }, new Object[] { gdReq });

			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		String methodNm =  "이송작업재료등록[ACoilJspBakFaEJB.updCarFtMvMtl]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
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
		String methodNm =  "이송작업재료삭제[ACoilJspBakFaEJB.delCarFtMvMtl]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
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
		String methodNm =  "이송작업재료위치변경[ACoilJspBakFaEJB.chgCarFtMvMtl]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
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
	 * 산적위치수정 - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStkLoc(GridData gdReq) throws DAOException {
		String methodNm =  "산적위치수정-수정[ACoilJspBakFaEJB.updStkLoc]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//산적위치수정 - 수정
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updStkLoc", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		String methodNm =  "산적위치수정-수정[ACoilJspBakFaEJB.delStkLoc]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {
	
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//산적위치수정 - 삭제
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("delStkLoc", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
	
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
		String methodNm =  "산적위치수정-수정[ACoilJspBakFaEJB.updStkLoc]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//산적위치수정 - 전문백업
			JDTORecord jrRtn = (JDTORecord)ejbConn.trx("updStkLocBackUp", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//조회
			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });

			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	 * 크레인스케줄 고도화기준 변경 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updAdvSchRuleMgt(GridData gdReq) throws DAOException {
		String methodNm =  "크레인스케줄 고도화기준 변경[ACoilJspBakFaEJB.updAdvSchRuleMgt]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
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
	 * GridData - 테이블 Data조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getTableData(GridData gdReq) throws DAOException 
	{
		String methodNm =  "테이블 Data조회[ACoilJspBakFaEJB.getSelectData(GridData)]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);;
		
		try 
		{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
			                    + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			GridData gdRet = (GridData)ejbConn.trx("getTableData", new Class[] { GridData.class }, new Object[] { gdReq });

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
	 * GridData - 테이블 Data 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updTableData(GridData gdReq) throws DAOException 
	{
		String methodNm =  "테이블 Data 수정[ACoilJspBakFaEJB.updTableData(GridData)]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);;
		
		try 
		{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
			                    + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			ejbConn.trx("updTableData", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
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
	 * GridData - 테이블 Data 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData executeQuery(GridData gdReq) throws DAOException 
	{
		String methodNm =  "테이블 Data 수정[ACoilJspBakFaEJB.executeQuery(GridData)]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);;
		
		try 
		{
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
			                    + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
			                    + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			ejbConn.trx("executeQuery", new Class[] { GridData.class }, new Object[] { gdReq });

			commUtils.printLog(logId, methodNm, "F-");
			GridData gdRet = OperateGridData.cloneResponseGridData(gdReq);
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
	 * CTS 작업우선동순위작업 - 동 변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData setCTSAreaBaySeq(GridData gdReq) throws DAOException {
		String methodNm =  "CTS 작업우선동순위작업- 동 적용[ACoilJspBakFaEJB.setCTSAreaBaySeq]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("setCTSAreaBaySeq", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	}// end of setCTSAreaBaySeq

	/**
	 * 기준관리 - 세부항목수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updYfRule(GridData gdReq) throws DAOException {
		String methodNm =  "기준관리 - 수정[ACoilJspBakFaEJB.updYfRule]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			ejbConn.trx("updYfRule", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
		
	}	// end of updYfRule
	
	/**
	 * 설비상태 (변경 설비기준조회 )
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updEqpOprnStat(GridData gdReq) throws DAOException {
		String methodNm =  "설비상태 변경[ACoilJspBakFaEJB.updEqpOprnStat]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
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
	 * 적치열 활성상태 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updYdStkColActiveStat(GridData gdReq) throws DAOException {
		String methodNm =  "적치열 활성상태 수정[ACoilJspBakFaEJB.updYdStkColActiveStat]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
					            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
					            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			ejbConn.trx("updYdStkColActiveStat", new Class[] { GridData.class }, new Object[] { gdReq });

			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updYdStkColActiveStat
	
	/**
	 * 적치열 저장품종류 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updStockItem(GridData gdReq) throws DAOException {
		String methodNm =  "적치열 저장품종류 수정[ACoilJspBakFaEJB.updStockItem]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
					            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
					            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			ejbConn.trx("updStockItem", new Class[] { GridData.class }, new Object[] { gdReq });

			GridData gdRet = (GridData)ejbConn.trx("getSelectData", new Class[] { GridData.class }, new Object[] { gdReq });
			
			commUtils.printLog(logId, methodNm, "F-");

			//조회결과
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}	// end of updYdStkColActiveStat
	
	/**
	 * 야드및설비 정보수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCoilYdStkPosSet(GridData gdReq) throws DAOException {
		String methodNm =  "야드및설비 열정보수정[ACoilJspBakFaEJB.updCoilYdStkPosSet]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
					            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
					            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCoilYdStkPosSet", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
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
		
	}	// end of updCoilYdStkPosSet
	
	/**
	 * 야드및설비 정보수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updCoilYdStkPosSetBed(GridData gdReq) throws DAOException {
		String methodNm =  "야드및설비 베드정보수정[ACoilJspBakFaEJB.updCoilYdStkPosSetBed]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) 
					            + "("   + commUtils.trim(gdReq.getParam("jsp_page_id")) 
					            + ")_"  + commUtils.trim(gdReq.getParam("jsp_page_func_nm"));

			commUtils.printLog(logId, methodNm, "F+", gdReq);

			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCoilYdStkPosSetBed", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
	 * 야드현황조회 존별 최대 적치율 - 저장 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData updZonMaxStcokRt(GridData gdReq) throws DAOException {
		String methodNm =  "야드현황조회 존별 최대 적치율 - 저장[ACoilJspBakFaEJB.updZonMaxStcokRt]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			
			//저장영역별검색순서 저장
			ejbConn.trx("updZonMaxStcokRt", new Class[] { GridData.class }, new Object[] { gdReq });
			
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
	 * 출하검수등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData updateCarExamination(GridData gdReq) throws DAOException {
		String methodNm =  "검수이상제품조회-출하검수등록 - [ACoilJspBakFaEJB.updateCarExamination]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		 
		EJBConnector ejbConn 	= null;
		JDTORecord outRecord  	= JDTORecordFactory.getInstance().create();	
		try{
			 
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			

			commUtils.printLog(logId, methodNm, "F+");	
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				outRecord 	= JDTORecordFactory.getInstance().create();
				outRecord.setField("userid"                     , commUtils.trim(gdReq.getParam("userid")));
				outRecord.setField("TRANS_ORD_NO"				, commUtils.getValue(gdReq, "TRANS_ORD_NO", ii));
				outRecord.setField("LABEL_YN"					, commUtils.getValue(gdReq, "LABEL_YN", ii));
				outRecord.setField("STL_NO"						, commUtils.getValue(gdReq, "STL_NO", ii));
				outRecord.setField("YD_AB_CD"					, commUtils.getValue(gdReq, "YD_AB_CD", ii));
				outRecord.setField("YD_AB_CD2"					, commUtils.getValue(gdReq, "YD_AB_CD2", ii));
				outRecord.setField("YD_CAR_UPP_LOC_CD"			, commUtils.getValue(gdReq, "YD_CAR_UPP_LOC_CD", ii));
				
				ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
				JDTORecord jrRst = (JDTORecord)	 ejbConn.trx("updateCarExamination", new Class[] { JDTORecord.class }, new Object[] { outRecord });
				
				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg("updateCarExamination");

					EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRst });
				}
			}
			commUtils.printLog(logId, methodNm, "F-");
			
			return  gdReq ;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}  //end of updateCarExamination
	
	
	/**
	 * 출하검수등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData abLabelPrintingSerchInfo(GridData gdReq) throws DAOException {
		String methodNm =  "검수이상제품조회-라벨출력 - [ACoilJspBakFaEJB.abLabelPrintingSerchInfo]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		 
		EJBConnector ejbConn 	= null;
		try{
			 
			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			commUtils.printLog(logId, methodNm, "F+");	
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			java.util.List rtnList = null;
			for (int i = 0; i < rowCnt; i++) {
				ejbConn = new EJBConnector("hsteelApp", "JNDIABSendRollingInfo", this);
				rtnList = (java.util.List)ejbConn.trx("abLabelPrintingInfo",
							new Class[]{
								String.class, String.class, String.class, String.class, String.class, String.class 
							}, 
							new Object[]{
								"R",
								commUtils.getValue(gdReq, "HR_PLNT_GPHR_PLNT_GP", i),
								commUtils.getValue(gdReq, "PROC_GP", i),
								commUtils.trim(gdReq.getParam("LABEL_ISSUE_LOC")),
								commUtils.getValue(gdReq, "STL_NO", i),
								"M" 
							});
				
				break;
			}
			
			
			commUtils.printLog(logId, methodNm, "처리결과 :: " + rtnList.toString());
			//조회
			commUtils.printLog(logId, methodNm, "F-");
			
			return  gdReq ;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}  //end of updateCarExamination
	
	/**
	 * 지포장 - 보급(1), 추출(3) 요구 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData reqPapWrapInOut(GridData gdReq) throws DAOException {
		String methodNm =  "지포장 - 보급,추출 요구[ACoilJspBakFaEJB.reqPapWrapInOut]";
		String logId = commUtils.getLogId(YfConstant.YD_GP_1);
		
		try {

			methodNm = methodNm + " < " + commUtils.trim(gdReq.getParam("jsp_page_nm")) + "(" + commUtils.trim(gdReq.getParam("jsp_page_id")) + ")";
			
			commUtils.printLog(logId, methodNm, "F+", gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "ACoilJspBakSeEJB", this);
			JDTORecord jrRst = (JDTORecord)ejbConn.trx("reqPapWrapInOut", new Class[] { GridData.class }, new Object[] { gdReq });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YfCommBakSeEJB", this);
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
}
