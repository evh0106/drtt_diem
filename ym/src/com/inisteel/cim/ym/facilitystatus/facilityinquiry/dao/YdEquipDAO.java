/**
 * 
 * @(#)CraneSchRegSBean
 * 
 * @version    :
 * @author     : HanDong Data Systems
 * @date       : 2005. 7. 20
 *
 * @description :
 * 
 */
package com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao;

import java.util.List;

import jspeed.base.log.LogService;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.common.exception.DAOException;

public class YdEquipDAO extends CommonDAO{

	private Logger log = null;
	
	public YdEquipDAO(){
		log = LogService.getInstance().getLogServiceContext().getLogger( "ym" );
	}	

    /**
     * @param queryCode
     * @param yd_gp
     */
    public List getListTransCarRoute(String queryCode, String yd_gp) {
        return super.findList(queryCode, new Object[]{ yd_gp });
    }

    /**
     * @param string3
     * @param string2
     * @param string
     * @param yd_gp_1
     * @return
     */
    public JDTORecord readCTSStatusInfo() {
        String queryCode = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.selectCTSStatusInfo";
        return super.find(queryCode);
    }

    /**
     * @param queryCode
     * @param objects
     */
    public String readEquipGp(String ydGp, String reCraneNo) {
        String queryCode = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.convertEquipNo";
        JDTORecord equipGp = super.findByPrimaryKey(queryCode, new Object[]{ ydGp, reCraneNo });
        return StringHelper.evl(equipGp.getFieldString("EQUIPNO"), "");
    }
    
	/**
     * 해당 재료번호에 해당하는 재료의 변경이력정보를 가져와 LIST로 반환한다.
     * 
     * @param 야드구분, 동구분, 스판구분, 소재구분
     * @return List
     * @throws 
     */		
	public List getListData(String queryCode,Object[] objs) throws DAOException{
		return super.findList(queryCode, objs);	
    }

	/**
     * 해당 재료번호에 해당하는 상세내역을 가져와 JDTORecord로 데이터를 리턴한다.
     * 
     * @param 재료번호
	 * @param 쿼리코드
     * @return
     * @throws 
     */
	public JDTORecord getData(String queryCode,Object[] objs) throws DAOException{
		return super.findByPrimaryKey(queryCode, objs);          
    }

    /**
     * @param objects
     */
    public int modifyBackUpDataOfSch(Object[] objects) {
        String queryCode =  
            "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateBackUpDataOfSchAndLayer";
        return super.updateData(queryCode, objects);        
    }
    
    /**
     * YJK
     *
     * 설비 TABLE 에 수조탱크 입,배수 관련 정보를 UPDATE
     *
     * @param String	: 수조탱크번호
     * @param String	: 입수시작일자
     * @param String	: 입수종료일자
     * @param String	: 배수시작일자
     * @param String	: 배수종료일자
     *
     * @return int
     * @throws DAOException
     */			
	public int updateWtEquipInfo(String stackColGp,
								 String sWTInSdate,
								 String sWTInEdate,
								 String sWTOutSdate,
								 String sWTOutEdate) throws DAOException
	{	
		/*
		Ver1.0--
		UPDATE tb_ym_equip
		SET	
			waterin_start_ddtt 	= :waterin_start_ddtt,
			waterin_end_ddtt 	= :waterin_end_ddtt,
			waterout_start_ddtt = :waterout_start_ddtt,
			waterout_end_ddtt 	= :waterout_end_ddtt,
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate     
		WHERE equip_gp = :equip_gp
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateWtEquipInfo";	
		Object[] params = {sWTInSdate,sWTInEdate,sWTOutSdate,sWTOutEdate,stackColGp};	
		return super.updateData(queryCode,params);
	}
    
    
    /**
     * YJK
     *
     * 설비 TABLE 에 수조탱크 입,배수 관련 정보를 UPDATE
     *
     * @param String	: 수조탱크번호
     * @param String	: 입/배수 시작/종료 구분
     * @param String	: 일자
     *
     * @return int
     * @throws DAOException
     */			
	public int updateWtEquipStatInfo(String stackColGp,
								     String sGbn,
								     String sdate) throws DAOException
	{	
		/*
		Ver1.0--
		UPDATE tb_ym_equip
		*/
		
		Object[] params = {sdate,stackColGp};	
		StringBuffer sDsql = new StringBuffer();
		
		if("1S".equals(sGbn)){
			sDsql.append("\n SET waterin_start_ddtt 	= :waterin_start_ddtt, ");	
		}else if("1E".equals(sGbn)){	
			sDsql.append("\n SET waterin_end_ddtt 		= :waterin_end_ddtt, ");	
		}else if("2S".equals(sGbn)){	
			sDsql.append("\n SET waterout_start_ddtt 	= :waterout_start_ddtt, ");	
		}else if("2E".equals(sGbn)){		
			sDsql.append("\n SET waterout_end_ddtt 		= :waterout_end_ddtt, ");	
		}
		sDsql.append("\n 	 modifier   = 'SYSTEM', ");	
		sDsql.append("\n 	 mod_ddtt   = sysdate   ");	
		sDsql.append("\n WHERE equip_gp = :equip_gp ");	
		
		String  queryCode = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateWtEquipStatInfo";	

		return super.updateData(queryCode,sDsql.toString(),params);
	}
	
	/**
     * YJK
     *
     * 코일 공통 Table에 냉각완료구분 항목을 UPDATE
     *
     * @param String	: 저장품ID
     *
     * @return int
     * @throws DAOException
     */			
	public int updateCommonCoilInfo(String sStockId) throws DAOException
	{	
		/*
		Ver1.0--
		UPDATE tb_pm_coilcomm
		SET	
			cool_done_gp 	= 'Y'
		WHERE coil_no = :coil_no
		*/
 		String  queryCode = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateCommonCoilInfo";	
		Object[] params = {sStockId};	
		return super.updateData(queryCode,params);
	}
	
    public int updateTransCarRoute(String qeuryId, List updateData) {
        return super.updateData(qeuryId, updateData.toArray());
    }
    
    public List getListData(String query, List whereData) throws DAOException{	
    	return super.findList(query, whereData.toArray());
    }
    
    public int updateData(String qeuryId, List updateData) {
    	return super.updateData(qeuryId, updateData.toArray());
    }
    
    public JDTORecord getData(String query, List whereData) throws DAOException{
    	return super.findByPrimaryKey(query, whereData.toArray());          
    }
    
    public int insertData(String qeuryId, List editData) throws DAOException {
    	return super.insertData(qeuryId, editData.toArray());
    }
    
    public int deleteData(String qeuryId, List editData) throws DAOException {
    	return super.deleteData(qeuryId, editData.toArray());
    }
    //2007-04-24 PALLET가능 적치수량 UPDATE(MCH)
    public int UpdateStackMaxQnty(String equip_gp, String Maxequip, String register) throws DAOException {
		/*
		UPDATE TB_YM_EQUIP
		SET STACK_MAX_QNTY = ?,
		    MODIFIER = ?
			MOD_DDTT = SYSDATE
		WHERE EQUIP_GP LIKE ?
		*/    	
		String query ="ym.facilitystatus.facilityinquiry.YdEquipDAO.UpdateStackMaxQnty";
    	return super.deleteData(query, new Object[]{Maxequip,register,equip_gp});
    } 
    public int UpdateStackMaxQnty_Bslab(String equip_gp, String equip_gp1, String Maxequip, String register) throws DAOException {    	
		String query ="ym.facilitystatus.facilityinquiry.YdEquipDAO.UpdateStackMaxQnty_Bslab";
    	return super.deleteData(query, new Object[]{Maxequip,register,equip_gp,equip_gp1});
    } 
}

