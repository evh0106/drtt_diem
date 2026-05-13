package com.inisteel.cim.ym.steelinfo.steelinforecv.dao;

import java.sql.Types;
import java.util.List;
import jspeed.base.record.JDTORecord;

import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.common.exception.DAOException;

public class YdSlabMoveBayRankingDAO extends CommonDAO {
    
	
	public List getListData(String query) throws DAOException{	
		return super.findList(query);
	}
	
	public int updateData(String qeuryId, List updateData) {
		return super.updateData(qeuryId, updateData.toArray());
	}
	
	public int updateSlabMoveBayRanking(String queryID,
										String ORD_YEOJAE_GP,
										String SLAB_GP,
										String RANKING,
										String DEST_BAY,
										String register) {
									
		return super.updateData(queryID, new Object []{ORD_YEOJAE_GP,
														SLAB_GP,
														RANKING,
														DEST_BAY,
														register});
	}
	
	public int updateSlabMoveBayRankingNEW(String queryID,
										String RANKING,
										String DEST_BAY,
										String PT_DEST_BAY,
										String register,
										String ORD_YEOJAE_GP,
										String SLAB_GP 
										) {
		
		return super.updateData(queryID, new Object []{RANKING,
				DEST_BAY,
				PT_DEST_BAY,
				register,
				ORD_YEOJAE_GP,
				SLAB_GP});
		}
	
	public int updateSlabloadLotRanking(String queryID,
			String RANKING1,
			String RULE_ID,
			String SCH_CD) {
		
        return super.updateData(queryID, new Object []{RANKING1,
        		RULE_ID,
        		SCH_CD
				});
    }
	
	
	
	public int updateCoilAppearGP(String queryID,
			String RANKING6,
			String RULE_ID6,
			String SCH_CD6) {
		
        return super.updateData(queryID, new Object []{RANKING6,
        		RULE_ID6,
        		SCH_CD6
				});
    }
	
	public int updateCoilAppearGPA(String queryID,
			String RANKING6,
			String RULE_ID6,
			String SCH_CD6) {
		
        return super.updateData(queryID, new Object []{RANKING6,
        		RULE_ID6,
        		SCH_CD6
				});
    }
	
	
	
	public int updateACoilmovebayRanking_SPM(String queryID,
			String RANKING1,
			String RULE_ID,
			String SCH_CD) {
		
        return super.updateData(queryID, new Object []{RANKING1,
        		RULE_ID,
        		SCH_CD
				});
    }
		
	
	public int updateACoilmovebayRanking_HFL(String queryID,
			String RANKING2,
			String RULE_ID1,
			String SCH_CD1) {
		
        return super.updateData(queryID, new Object []{RANKING2,
        		RULE_ID1,
        		SCH_CD1
				});
    }
	
	
	
	
	
	
	public int updateCoilloadLotRanking(String queryID,
			String RANKING1,
			String RULE_ID1,
			String SCH_CD1) {
		
        return super.updateData(queryID, new Object []{RANKING1,
        		RULE_ID1,
        		SCH_CD1
				});
    }
	
	/**
	 * 
	 * @param queryID
	 * @param OBJ[]
	 * @return
	 */
	public int updateCoilloadLotRankingJip(String queryID, Object[] inData) {
		
		return super.updateData(queryID, inData);
	}
	
	
	
	
	
	public int updateCoilloadLotRankingA(String queryID,
			String RANKING1,
			String RULE_ID1,
			String SCH_CD1) {
		
        return super.updateData(queryID, new Object []{RANKING1,
        		RULE_ID1,
        		SCH_CD1
				});
    }
	
	
	
	
	
	public int updateCoillunloadBay(String queryID,
			String RANKING2,
			String RULE_ID2,
			String SCH_CD2) {
		
        return super.updateData(queryID, new Object []{RANKING2,
        		RULE_ID2,
        		SCH_CD2
				});
    }
	
	
	public int updateCoillunloadBayA(String queryID,
			String RANKING2,
			String RULE_ID2,
			String SCH_CD2) {
		
        return super.updateData(queryID, new Object []{RANKING2,
        		RULE_ID2,
        		SCH_CD2
				});
    }
	
	
	
	
	public int updateSlabloadLotRankingSF(String queryID,
			String RANKING2,
			String RULE_ID1,
			String SCH_CD1) {
		
        return super.updateData(queryID, new Object []{RANKING2,
        		RULE_ID1,
        		SCH_CD1
				});
    }
	
	

	public int updateCoilloadLotRanking_CM(String queryID,
			String RANKING3,
			String RULE_ID3,
			String SCH_CD3) {
		
        return super.updateData(queryID, new Object []{RANKING3,
        		RULE_ID3,
        		SCH_CD3
				});
    }
	
	

	public int updateCoilloadLotRankingA_CM(String queryID,
			String RANKING3,
			String RULE_ID3,
			String SCH_CD3) {
		
        return super.updateData(queryID, new Object []{RANKING3,
        		RULE_ID3,
        		SCH_CD3
				});
    }
	
	

	public int updateCoilNextProc(String queryID,
			String RANKING4,
			String RULE_ID4,
			String SCH_CD4) {
		
        return super.updateData(queryID, new Object []{RANKING4,
        		RULE_ID4,
        		SCH_CD4
				});
    }
	

	public int updateCoilNextProcA(String queryID,
			String RANKING4,
			String RULE_ID4,
			String SCH_CD4) {
		
        return super.updateData(queryID, new Object []{RANKING4,
        		RULE_ID4,
        		SCH_CD4
				});
    }
	

	public int updateCoilunloadWlocCD(String queryID,
			String RANKING5,
			String RULE_ID5,
			String SCH_CD5) {
		
        return super.updateData(queryID, new Object []{RANKING5,
        		RULE_ID5,
        		SCH_CD5
				});
    }

	public int updateCoilunloadWlocCDA(String queryID,
			String RANKING5,
			String RULE_ID5,
			String SCH_CD5) {
		
        return super.updateData(queryID, new Object []{RANKING5,
        		RULE_ID5,
        		SCH_CD5
				});
    }
	
	
	
	
	public int updateSlabloadLotRankingWLOC(String queryID,
			String RANKING3,
			String RULE_ID2,
			String SCH_CD2) {
		
        return super.updateData(queryID, new Object []{RANKING3,
        		RULE_ID2,
        		SCH_CD2
				});
    }
}
