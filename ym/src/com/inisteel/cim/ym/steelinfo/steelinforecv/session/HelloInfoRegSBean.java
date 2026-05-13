package com.inisteel.cim.ym.steelinfo.steelinforecv.session;

import jspeed.base.ejb.EJBConnector;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;


import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.util.CmUtil;

import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;

/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="HelloInfoRegEJB" jndi-name="JNDIHelloInfoReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class HelloInfoRegSBean extends BaseSessionBean {
	
	public void ejbCreate() {
	}
	private YmComm ymComm = new YmComm();
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 	    
	public String sayHello(String name){
	    return "Hello..." + name;
	}
	
	/**
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData getList(GridData inDto) throws DAOException {
		 
		// ERROR CHECK
		int intRtnVal = 0;
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			
			
			
			JDTORecord    recPara   = JDTORecordFactory.getInstance().create();	 
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
					
			YdStockDAO ydStockDao = new YdStockDAO();
		
			String sStockGbn 	= StringHelper.evl(inRecord.getFieldString("P_STOCK_GBN"),"S");
			String sSearchGbn 	= StringHelper.evl(inRecord.getFieldString("P_SEARCH_GBN"),"2");
			
			int iStockGbn = 1;
			
			if("A".equals(sStockGbn)||"D".equals(sStockGbn)||"S".equals(sStockGbn)){
				iStockGbn = 1;
			}else if("K".equals(sStockGbn)){
				iStockGbn = 2;
			}
			recPara.setField("STOCK_GBN",       	sStockGbn); 
			recPara.setField("SEARCH_GBN",       	sSearchGbn); 
			recPara.setField("V_PAGE_CNT",          inRecord.getField("PAGE_NO"));
			recPara.setField("V_ROW_CNT",           inRecord.getField("PAGE_SIZE"));
			recPara.setField("V_PAGE_CNT",          inRecord.getField("PAGE_NO"));			
			recPara.setField("V_ROW_CNT",           inRecord.getField("PAGE_SIZE"));
			
			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet,iStockGbn);
		
			gdRes = CmUtil.genGridData(inDto , outRecSet);
			 
		}catch(Exception e){
		
		} 
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		return gdRes;
	}
	

}