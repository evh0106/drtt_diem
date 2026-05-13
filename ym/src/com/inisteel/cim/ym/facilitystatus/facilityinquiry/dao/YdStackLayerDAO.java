package com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao;

import java.util.List;

import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.common.exception.DAOException;

import jspeed.base.log.LogService;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;

/**
 * 
 */
public class YdStackLayerDAO extends CommonDAO{
	private Logger log = null;
	
	public YdStackLayerDAO(){
		log = LogService.getInstance().getLogServiceContext().getLogger( "ym" );
	}	

	/**
	 * 폭정보를 리턴한다.
	 * @return
	 */
	public List getListW() {
	    return super.findList("ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.getListW"); 
	}

	/**
	 * 중량정보를 리턴한다.
	 * @return
	 */
	public List getListWT() {
	    return super.findList("ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.getListWT"); 
	}

	/**
	 * 선적번호를 리턴한다.
	 * @return
	 */
	public List getListShipNo() {
	    return super.findList("ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.getListShipNo"); 
	}

	/**
	 * 규격약호를 리턴한다.
	 * @return
	 */
	public List getListSpecCode() {
	    return super.findList("ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.getListSpecCode"); 
	}

	/**
	  * List에 입력된 순서대로 Query값에 Matching하여 데이타를 Insert한다.  
	  * @param listData 입력할값
	  * @return int 
	  * @throws DAOException
	  */		
		public int insertData(List listData) throws DAOException{
			/* 
			INSERT INTO TB_YM_STACKER
			    (STACK_COL_GP,
			    STACK_BED_GP,
			    STACK_BED_X_AXIS,
			    STACK_BED_Y_AXIS,
			    STACK_BED_Z_AXIS,
			    STACK_BED_QNTY_MAX,
			    STACK_BED_WT_MAX,
			    STACK_BED_HIGH_MAX,
			    STACK_BED_W_MAX,
			    STACK_BED_LEN_MAX,
			    STACK_BED_QNTY_CURR,
			    STACK_BED_WT_CURR,
			    STACK_BED_HIGH_CURR,
			    STACK_BED_W_CURR,
			    STACK_BED_CURR_LEN,
			    STACK_BED_ABLE_QNTY,
			    STACK_BED_ABLE_WT,
			    STACK_BED_ABLE_HIGH,
			    STACK_BED_ABLE_W,
			    STACK_BED_ABLE_LEN,
			    STACK_BED_STAT,
			    'tester',
			    sysdate,
			    null,
			    sysdate,
			    'N')
			VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
			*/
		
		 	if(listData == null) {
		 		log.println("Input(listData) is NULL");
				throw new DAOException("Input Data is NULL");
		 	}		 	
			String  queryCode = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.insertStackLayer";			
			return super.insertData(queryCode,listData.toArray());
		}
		
		
		
		
		/**
	     * List에 입력된 순서대로 Query값에 Matching하여 데이타를 Delete한다.  
	     * @param listData 입력할값
	     * @return int 
	     * @throws DAOException
	     */		
	   	public int deleteData(String queryId, List listData) throws DAOException{
	   		/*
	   		DELETE EMP_TEMP
	   		WHERE emp_no=? 
	   		*/
	   	
	   	 	if(listData == null) {
	   	 		log.println("Input(listData) is NULL");
	   			throw new DAOException("Input Data is NULL");
	   	 	}
	   	 	
	   		return super.deleteData(queryId, listData.toArray());
	   	}
	   	
	   	
	   	
	   	
	   	
	   	
	   	/**
	     * 야드 적치열 등록현황을 조회한다.
	     * 
	     * @param 야드구분, 동구분, 스판구분, 열구분
	     * @return List
	     * @throws 
	     */
		public List getExistStockID(String queryCode,Object[] objs) throws DAOException{
			return super.findList(queryCode, objs);	
	    }
		
		
		
		
		
		/**
	     * List에 입력된 순서대로 Query값에 Matching하여 데이타를 Update한다. 
	     * @param listData 입력할값
	     * @return int 
	     * @throws DAOException
	     */		
	   	public int updateData(String queryCode, List listData) throws DAOException{	   	 
	   		return super.updateData(queryCode,listData.toArray());
	   	}
		
		/**
	     * List에 입력된 순서대로 Query값에 Matching하여 데이타를 Update한다. 
	     * @param listData 입력할값
	     * @return int 
	     * @throws DAOException
	     */
		public JDTORecord requestFind(String queryCode) throws DAOException{
			return super.find(queryCode);          
	    }
		
		public JDTORecord requestgetData(String queryCode,Object[] objs) throws DAOException{
			return super.findByPrimaryKey(queryCode, objs);          
	    }

		public int requestinsertData(String queryCode, Object[] objs) throws DAOException{	
			return super.insertData(queryCode,objs);
		}

	   	public int requestupdateData(String queryCode, Object[] objs) throws DAOException{	   	 	
	   		return super.updateData(queryCode,objs);
	   	}		
	   	public int requestdeleteData(String queryCode, Object[] objs) throws DAOException{	   	 	
	   		return super.deleteData(queryCode,objs);
	   	}		
	   	
	   	public List getListData(String query, List whereData) throws DAOException{	
	   		return super.findList(query, whereData.toArray());
	   	}
		
	   	public List requestgetListData(String queryCode, Object[] objs) throws DAOException{	   	 	
	   		return super.findList(queryCode,objs);
	   	}				
}

