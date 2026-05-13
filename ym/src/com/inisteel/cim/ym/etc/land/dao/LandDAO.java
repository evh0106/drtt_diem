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
package com.inisteel.cim.ym.etc.land.dao;

import java.sql.Types;
import java.util.List;
import jspeed.base.record.JDTORecord;

import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.common.exception.DAOException;

public class LandDAO extends CommonDAO {
    
	/**
	* List에 입력된 순서대로 Query값에 Matching하여 데이타를 토지계획테이블에 Insert한다.  
	*
	* @param listData 입력할값
	*
	* @return int 
	* @throws DAOException
	*/		
	public int insertLandPlanInfo(List listData) throws DAOException
	{
             /* Query  
			
			INSERT INTO TB_TJ_LANDPLAN
			( 
			    sanction_step_no,
			    address,
			    place_no,
			    owner,
			    sanction_date,
			    land_class,
			    area,
			    share_r,
			    ground_gp,
			    cal_amt,
			    special_no,
			    owner_add,
			    acq_farm_fax,
			    reg_farm_fax,
			    check_no,
			    remark,
			    sanction_gp,
			    register,
			    reg_ddtt    
			)
			VALUES
			(
			    :sanction_step_no,
			    :address,
			    :place_no,
			    :owner,
			    :sanction_date,
			    :land_class,
			    :area,
			    :share_r,
			    :ground_gp,
			    :cal_amt,
			    :special_no,
			    :owner_add,
			    :acq_farm_fax,
			    :reg_farm_fax,
			    :check_no,
			    :remark,
			    :sanction_gp,
			    :register,
			    sysdate
			)
		*/
	 	if(listData == null) {
	 		throw new DAOException("Input Data is NULL");
	 	}
	 	
		String  queryCode = "ym.etc.land.LandDAO.insertLandPlanInfo";			
		return super.insertData(queryCode,listData.toArray());
	}
	
	/**
	* List에 입력된 순서대로 Query값에 Matching하여 데이타를 지장물계획테이블에 Insert한다.  
	*
	* @param listData 입력할값
	*
	* @return int 
	* @throws DAOException
	*/		
	public int insertProcPlanInfo(List listData) throws DAOException
	{
             /* Query  
			
			INSERT INTO TB_TJ_LANDUTILPLAN
			( 
				sanction_step_no,
				owner,
				proc_kind,
				sanction_date,
				special_no,
				owner_add,
				cal_amt,
				address,
				place_no,
				check_no,
				remark,
				qnty,
				proc_unit,
				register,
				reg_ddtt,
				del_amt,
				del_seq,
				sanction_gp
			)
			VALUES
			(
			    	:sanction_step_no,
				:owner,
				:proc_kind,
				:sanction_date,
				:special_no,
				:owner_add,
				:cal_amt,
				:address,
				:place_no,
				:check_no,
				:remark,
				:qnty,
				:proc_unit,
				:register,
				sysdate,
				:del_amt,
				LPAD(:del_seq , 3, '0'),
				:sanction_gp
			)
		*/
	 	if(listData == null) {
	 		throw new DAOException("Input Data is NULL");
	 	}
	 	
		String  queryCode = "ym.etc.land.LandDAO.insertProcPlanInfo";			
		return super.insertData(queryCode,listData.toArray());
	}
	
	public int insertProcHistInfo_02(List listData) throws DAOException
	{
             /* Query  
			
			INSERT INTO TB_TJ_LANDUTILHIST
			( 
				del_seq,
				sanction_step_no,
				owner,
				proc_kind,
				cal_amt
			)
			VALUES
			(
			    	LPAD(:del_seq , 3, '0'),
			    	:sanction_step_no,
				:owner,
				:proc_kind,
				:cal_amt
			)
		*/
	 	if(listData == null) {
	 		throw new DAOException("Input Data is NULL");
	 	}
	 	
		String  queryCode = "ym.etc.land.LandDAO.insertProcHistInfo_02";			
		return super.insertData(queryCode,listData.toArray());
	}
	/**
	* List에 입력된 순서대로 Query값에 Matching하여 데이타를 토지실적테이블에 Insert한다.  
	*
	* @param listData 입력할값
	*
	* @return int 
	* @throws DAOException
	*/		
	public int insertLandWrsltInfo(List listData) throws DAOException
	{
             /* Query  
			INSERT INTO TB_TJ_LANDWRSLT
			( 
			    sanction_step_no,
			    address,
			    place_no,
			    owner,
			    payer,
			    special_no,
			    area,
			    acq_farm_fax,
			    reg_farm_fax,
			    compen_money,
			    pay_date,
			    register,
			    reg_ddtt    
			)
			VALUES
			(
			    :sanction_step_no,
			    :address,
			    :place_no,
			    :owner,
			    :payer,
			    :special_no,
			    :area,
			    :acq_farm_fax,
			    :reg_farm_fax,
			    :compen_money,
			    :pay_date,
			    :register,
			    sysdate    
			)
		*/
	 	if(listData == null) {
	 		throw new DAOException("Input Data is NULL");
	 	}
	 	
		String  queryCode = "ym.etc.land.LandDAO.insertLandWrsltInfo";			
		return super.insertData(queryCode,listData.toArray());
	}
	
	/**
	* List에 입력된 순서대로 Query값에 Matching하여 데이타를 지장물실적테이블에 Insert한다.  
	*
	* @param listData 입력할값
	*
	* @return int 
	* @throws DAOException
	*/		
	public int insertProcWrsltInfo(List listData) throws DAOException
	{
		

             /* Query  
			INSERT INTO TB_TJ_LANDUTILWRSLT
			( 
			   	sanction_step_no,
				owner,
				proc_kind,
				seq_num,
				payer,
				special_no,
				payer_add,
				compen_money,
				pay_date,
				register,
				reg_ddtt
			)
			VALUES
			(
			   	:sanction_step_no,
				:owner,
				:proc_kind,
				:seq_num,
				:payer,
				:special_no,
				:payer_add,
				:compen_money,
				:pay_date,
				:register,
				sysdate
			        
			)
		*/
	 	if(listData == null) {
	 		throw new DAOException("Input Data is NULL");
	 	}
	 	
		String  queryCode = "ym.etc.land.LandDAO.insertProcWrsltInfo";			
		return super.insertData(queryCode,listData.toArray());
	}
	
	public int insertLandHistInfo(	String strPayer,						//지급자
					 		String strUserId,						//등록자
					 		String strSanctionStepNo,				//품의차수
						  	String strAddress,						//소재지
						  	String strPlaceNo,						//지번
						  	String strOwner,						//소유자
						  	String strComAmt) throws DAOException 	//지급금액
	{
             /* Query  
			INSERT INTO TB_TJ_LANDLOG
			(    
			    sanction_step_no,
			    address,
			    place_no,
			    owner,
			    won_sanction_step_no,
			    won_address,
			    won_place_no,
			    won_owner,
			    sanction_date,
			    land_class,
			    area,
			    share_r,
			    ground_gp,
			    cal_amt,
			    special_no,
			    owner_add,
			    acq_farm_fax,
			    reg_farm_fax,
			    check_no,
			    remark,
			    register,
			    reg_ddtt,
			    compen_money
			)    
			SELECT 
			    sanction_step_no,
			    address,
			    place_no,
			    :payer,
			    sanction_step_no,
			    address,
			    place_no,
			    owner,
			    sanction_date,
			    land_class,
			    area,
			    share_r,
			    ground_gp,
			    cal_amt,
			    special_no,
			    owner_add,
			    acq_farm_fax,
			    reg_farm_fax,
			    check_no,
			    remark,
			    :register,
			    sysdate,
			    :compen_money
			FROM TB_TJ_LANDPLAN   
			WHERE 	sanction_step_no 	= :sanction_step_no
			AND 	address 			= :address
			AND 	place_no 			= :place_no
			AND 	owner   			= :owner
		*/
	 	
		String  queryCode = "ym.etc.land.LandDAO.insertLandHistInfo";			
		Object[] params = {strPayer,strUserId,strComAmt,strSanctionStepNo,strAddress,strPlaceNo,strOwner};	
		return super.insertData(queryCode,params);
	}
						  						    	
	public int insertProcHistInfo(	String strPayer,						//지급자
							String strSeqNum,						//일련번호
					 		String strUserId,						//등록자
					 		String strSanctionStepNo,				//품의차수
						  	String strOwner,						//소유자
						  	String strProcKind,						//물건의종류
						  	String strComAmt	) throws DAOException 	//지급금액
	{
             /* Query  
			INSERT INTO TB_TJ_LANDUTILLOG
			(    
			    	sanction_step_no,
				owner,
				proc_kind,
				won_sanction_step_no,
				won_owner,
				won_proc_kind,
				seq_num,
				sanction_date,
				special_no,
				owner_add,
				cal_amt,
				address,
				place_no,
				check_no,
				remark,
				qnty,
				proc_unit,
				register,
				reg_ddtt,
				compen_money
			)    
			SELECT 
			    	sanction_step_no,
				:payer,
				proc_kind,
				sanction_step_no,
				owner,
				proc_kind,
				:seq_num,
				sanction_date,
				special_no,
				owner_add,
				cal_amt,
				address,
				place_no,
				check_no,
				remark,
				qnty,
				proc_unit,
				:register,
				sysdate,
				:compen_money
			FROM TB_TJ_LANDUTILPLAN   
			WHERE 	sanction_step_no 	= :sanction_step_no
			AND 	owner 			= :owner
			AND 	proc_kind 		= :proc_kind
		*/
	 	
		String  queryCode = "ym.etc.land.LandDAO.insertProcHistInfo";			
		Object[] params = {strPayer,strSeqNum,strUserId,strComAmt,strSanctionStepNo,strOwner,strProcKind};	
		return super.insertData(queryCode,params);
	}
	
	public int deletetLandPlanInfo(String strSanctionStepNo,				//품의차수
						  	String strAddress,						//소재지
						  	String strPlaceNo,						//지번
						  	String strOwner	) throws DAOException 	//소유자
	{
		/*
		DELETE TB_TJ_LANDPLAN
		WHERE 	sanction_step_no 	= :sanction_step_no
		AND 	address 			= :address
		AND 	place_no 			= :place_no
		AND 	owner   			= :owner
		*/
		String  queryCode = "ym.etc.land.LandDAO.deletetLandPlanInfo";			
		Object[] params = {strSanctionStepNo,strAddress,strPlaceNo,strOwner};	
		return super.deleteData(queryCode, params);
	}
	
		
	public JDTORecord getLandPlanInfo_01(String strSanctionStepNo,
		  							String  strAddress,
		  							String  strPlaceNo,
		  							String  strOwner)throws DAOException
	{	  
		/*
		SELECT * 
		FROM TB_TJ_LANDPLAN
		WHERE 	SANCTION_STEP_NO 	= :SANCTION_STEP_NO
		AND 	ADDRESS 			= :ADDRESS 
		AND 	PLACE_NO 			= :PLACE_NO 
		AND 	OWNER 				= :OWNER
		*/
		
		String  queryCode 	= "ym.etc.land.LandDAO.getLandPlanInfo_01";	
		Object[] params = {strSanctionStepNo,strAddress,strPlaceNo,strOwner };	
		
		return super.findByPrimaryKey(queryCode, params);
	} 
	
	public JDTORecord getLandWrsltInfo_01(String strSanctionStepNo,
		  							String  strAddress,
		  							String  strPlaceNo,
		  							String  strOwner)throws DAOException
	{	  
		/*
		SELECT * 
		FROM TB_TJ_LANDWRSLT
		WHERE 	SANCTION_STEP_NO 	= :SANCTION_STEP_NO
		AND 	ADDRESS 			= :ADDRESS 
		AND 	PLACE_NO 			= :PLACE_NO 
		AND 	OWNER 				= :OWNER
		*/
		
		String  queryCode 	= "ym.etc.land.LandDAO.getLandWrsltInfo_01";	
		Object[] params = {strSanctionStepNo,strAddress,strPlaceNo,strOwner };	
		
		return super.findByPrimaryKey(queryCode, params);
	} 
	
	public int updateLandPlanInfo_01(	String strSpecialNo, 				// 주민/사업자번호
								String strSanctionStepNo,			// 품의차수
  							   	String strAddress,					// 소재지
  							  	String strPlaceNo,					// 지번
  							  	String strOwner)throws DAOException	// 소유자
	{  							  	
		/* 
		Ver4.0--
		UPDATE TB_TJ_LANDPLAN
		SET 
			special_no   	= :special_no
		 	 
		WHERE 	SANCTION_STEP_NO 	= :SANCTION_STEP_NO
		AND 	ADDRESS 			= :ADDRESS 
		AND 	PLACE_NO 			= :PLACE_NO 
		AND 	OWNER 				= :OWNER
		*/
	 	String  queryCode = "ym.etc.land.LandDAO.updateLandPlanInfo_01";
		Object[] params = {strSpecialNo,strSanctionStepNo,strAddress,strPlaceNo,strOwner};	
		return super.updateData(queryCode,params);
	}
	
	public int updateLandPlanInfo_02(	String strDelYn, 					// 이력관리대상
								String strSanctionStepNo,			// 품의차수
  							   	String strAddress,					// 소재지
  							  	String strPlaceNo,					// 지번
  							  	String strOwner)throws DAOException	// 소유자
	{  							  	
		/* 
		Ver4.0--
		UPDATE TB_TJ_LANDPLAN
		SET 
			del_yn   	= :del_yn
		 	 
		WHERE 	SANCTION_STEP_NO 	= :SANCTION_STEP_NO
		AND 	ADDRESS 			= :ADDRESS 
		AND 	PLACE_NO 			= :PLACE_NO 
		AND 	OWNER 				= :OWNER
		*/
	 	String  queryCode = "ym.etc.land.LandDAO.updateLandPlanInfo_02";
		Object[] params = {strDelYn,strSanctionStepNo,strAddress,strPlaceNo,strOwner};	
		return super.updateData(queryCode,params);
	}
	
	public int updateLandPlanInfo_03()throws DAOException	
	{  							  	
		/* 
		Ver4.0--
		UPDATE TB_TJ_LANDPLAN
		SET del_yn   	= :del_yn
		WHERE del_yn IS NOT NULL
		*/
	 	String  queryCode = "ym.etc.land.LandDAO.updateLandPlanInfo_03";
		Object[] params = {""};	
		return super.updateData(queryCode,params);
	}
	
	public List getLandPlanList_01() throws DAOException{
		/*
		SELECT 
				*
		FROM TB_TJ_LANDPLAN
		WHERE del_yn  = :del_yn
		*/
		String  queryCode = "ym.etc.land.LandDAO.getLandPlanList_01";	
		Object[] params = {"Y"};	
		
		return super.findList(queryCode, params);
      }
      
      public List getLandPlanList_02(String strSanctionStepNo,				// 품의차수
  							 String strAddress,						// 소재지
  							 String strPlaceNo)throws DAOException	// 지번
  	{						 	
		/*
		SELECT 
				*
		FROM TB_TJ_LANDPLAN
		WHERE 	SANCTION_STEP_NO 	= :SANCTION_STEP_NO
		AND 	ADDRESS 			= :ADDRESS 
		AND 	PLACE_NO 			= :PLACE_NO 
		AND 	NVL(DEL_YN,'N')  <> 'Y'
		*/
		String  queryCode = "ym.etc.land.LandDAO.getLandPlanList_02";	
		Object[] params = {strSanctionStepNo,strAddress,strPlaceNo};	
		
		return super.findList(queryCode, params);
      }
	
	
	public JDTORecord getProcPlanInfo_01(String strSanctionStepNo,
		  							String  strOwner,
		  							String  strProcKind)throws DAOException
	{	  
		/*
		SELECT * 
		FROM TB_TJ_LANDUTILPLAN
		WHERE 	SANCTION_STEP_NO 	= :SANCTION_STEP_NO
		AND 	OWNER 				= :OWNER 
		AND 	PROC_KIND 			= :PROC_KIND 
		*/
		
		String  queryCode 	= "ym.etc.land.LandDAO.getProcPlanInfo_01";	
		Object[] params = {strSanctionStepNo,strOwner,strProcKind };	
		
		return super.findByPrimaryKey(queryCode, params);
	}     	
	
	public int updateProcPlanInfo_01(	String strSpecialNo, 					// 주민/사업자번호
								String strSanctionStepNo,				// 품의차수
  							   	String strOwner,						// 소유자
  							  	String strProcKind)throws DAOException	// 물건의종류
	{  							  	
		/* 
		Ver4.0--
		UPDATE TB_TJ_LANDUTILPLAN
		SET 
			special_no   	= :special_no
		 	 
		WHERE 	SANCTION_STEP_NO 	= :SANCTION_STEP_NO
		AND 	OWNER 				= :OWNER 
		AND 	PROC_KIND 			= :PROC_KIND 
		*/
	 	String  queryCode = "ym.etc.land.LandDAO.updateProcPlanInfo_01";
		Object[] params = {strSpecialNo,strSanctionStepNo,strOwner,strProcKind};	
		return super.updateData(queryCode,params);
	}
					  							  		
	public int updateProcPlanInfo_02(	String strDelYn, 						// 이력관리대상
								String strDelSeq, 						// 구분자
								String strSanctionStepNo,				// 품의차수
  							   	String strOwner,						// 소유자
  							  	String strProcKind)throws DAOException	// 물건의종류
	{  							  	
		/* 
		Ver4.0--
		UPDATE TB_TJ_LANDUTILPLAN
		SET 
			del_yn   	= :del_yn,
			del_seq   = LPAD(:del_seq , 3, '0')
		 	 
		WHERE 	SANCTION_STEP_NO 	= :SANCTION_STEP_NO
		AND 	OWNER 				= :OWNER 
		AND 	PROC_KIND 			= :PROC_KIND 
		*/
	 	String  queryCode = "ym.etc.land.LandDAO.updateProcPlanInfo_02";
		Object[] params = {strDelYn,strDelSeq,strSanctionStepNo,strOwner,strProcKind};	
		return super.updateData(queryCode,params);
	}
	
	public int updateProcPlanInfo_03(	String strCalAmt, 						// 산정금액
								String strSanctionStepNo,				// 품의차수
  							   	String strOwner,						// 소유자
  							  	String strProcKind)throws DAOException	// 물건의종류
	{  							  	
		/* 
		Ver4.0--
		UPDATE TB_TJ_LANDUTILPLAN
		SET 
			cal_amt   	= :cal_amt
		 	 
		WHERE 	SANCTION_STEP_NO 	= :SANCTION_STEP_NO
		AND 	OWNER 				= :OWNER 
		AND 	PROC_KIND 			= :PROC_KIND 
		*/
	 	String  queryCode = "ym.etc.land.LandDAO.updateProcPlanInfo_03";
		Object[] params = {strCalAmt,strSanctionStepNo,strOwner,strProcKind};	
		return super.updateData(queryCode,params);
	}
		
	public int updateProcPlanInfo_04()throws DAOException	
	{  							  	
		/* 
		Ver4.0--
		UPDATE TB_TJ_LANDUTILPLAN
		SET del_yn   	= :del_yn,
			del_seq   = :del_seq,
			del_amt	= :del_amt
		WHERE del_seq IS NOT NULL
		*/
	 	String  queryCode = "ym.etc.land.LandDAO.updateProcPlanInfo_04";
		Object[] params = {"","",""};	
		return super.updateData(queryCode,params);
	}
											      		
	public List getProcPlanList_02(String strSanctionStepNo,				// 품의차수
  							 String strProcKind,						// 물건의종류
  							 String strDelSeq)throws DAOException		// 구분자
  	{						 	
		/*
		SELECT 
				*
		FROM TB_TJ_LANDUTILPLAN
		WHERE 	SANCTION_STEP_NO 	= :SANCTION_STEP_NO
		AND 	PROC_KIND 			= :PROC_KIND 
		AND 	DEL_SEQ				= :DEL_SEQ 
		AND 	NVL(DEL_YN,'N')  <> 'Y' 
		*/
		String  queryCode = "ym.etc.land.LandDAO.getProcPlanList_02";	
		Object[] params = {strSanctionStepNo,strProcKind,strDelSeq};	
		
		return super.findList(queryCode, params);
      }
      
      public List getProcPlanList_03( String strDelSeq)throws DAOException		// 구분자
  	{						 	
		/*
		SELECT 
				B.SANCTION_STEP_NO,
				B.OWNER,
				B.PROC_KIND,
				B.CAL_AMT,
				A.CAL_AMT AS DEL_AMT
		FROM 	TB_TJ_LANDUTILHIST A,
		     		TB_TJ_LANDUTILPLAN B
		WHERE 	A.DEL_SEQ		= :DEL_SEQ 
		AND A.SANCTION_STEP_NO 	= B.SANCTION_STEP_NO
		AND A.OWNER 			= B.OWNER
		AND A.PROC_KIND 			= B.PROC_KIND
		*/
		String  queryCode = "ym.etc.land.LandDAO.getProcPlanList_03";	
		Object[] params = {strDelSeq};	
		
		return super.findList(queryCode, params);
      }
     
      public JDTORecord getProcPlanInfo_02(	String strPayer,						//지급자
									String strSanctionStepNo,				//품의차수
								  	String strOwner,						//소유자
								  	String strProcKind	) throws DAOException 	//물건의종류
  	{						 	
		/*
		SELECT 
				LPAD(NVL(MAX(SEQ_NUM),0) + 1 , 2, '0') AS SEQ_NUM
		FROM TB_TJ_LANDUTILLOG
		WHERE 	SANCTION_STEP_NO 		= :SANCTION_STEP_NO
		AND 	OWNER 					= :OWNER 
		AND 	PROC_KIND				= :PROC_KIND 
		AND 	WON_SANCTION_STEP_NO 	= :SANCTION_STEP_NO
		AND 	WON_OWNER 				= :OWNER 
		AND 	WON_PROC_KIND			= :PROC_KIND 
		*/
		String  queryCode 	= "ym.etc.land.LandDAO.getProcPlanInfo_02";	
		Object[] params = {strSanctionStepNo,strPayer,strProcKind,strSanctionStepNo,strOwner,strProcKind };	
		
		return super.findByPrimaryKey(queryCode, params);
	}     	
      
      public JDTORecord getProcPlanInfo_03(	String strSanctionStepNo,				//품의차수
								  	String strOwner,						//소유자
								  	String strProcKind	) throws DAOException 	//물건의종류
  	{						 	
		/*
		SELECT 
				LPAD(NVL(MAX(SEQ_NUM),0) + 1 , 2, '0') AS SEQ_NUM
		FROM TB_TJ_LANDUTILWRSLT
		WHERE 	SANCTION_STEP_NO 		= :SANCTION_STEP_NO
		AND 	OWNER 					= :OWNER 
		AND 	PROC_KIND				= :PROC_KIND 
		*/
		String  queryCode 	= "ym.etc.land.LandDAO.getProcPlanInfo_03";	
		Object[] params = {strSanctionStepNo,strOwner,strProcKind};	
		
		return super.findByPrimaryKey(queryCode, params);
	}     	
      
      public JDTORecord getProcPlanInfo_04(	String strSanctionStepNo,				//품의차수
								  	String strOwner,						//소유자
								  	String strProcKind	) throws DAOException 	//물건의종류
  	{						 	
		/*
		SELECT 
		    SUM(CAL_AMT) 		AS CAL_AMT, 
		    SUM(COMPEN_MONEY) 	AS COMPEN_MONEY
		FROM 	TB_TJ_LANDUTILPLAN A,
		     		TB_TJ_LANDUTILWRSLT B
		WHERE A.SANCTION_STEP_NO    	= B.SANCTION_STEP_NO(+)
		AND   A.OWNER               		= B.OWNER(+)
		AND   A.PROC_KIND           		= B.PROC_KIND(+)     
		AND   A.SANCTION_STEP_NO    	= :SANCTION_STEP_NO
		AND   A.OWNER               		= :OWNER
		AND   A.PROC_KIND           		= :PROC_KIND
		*/
		String  queryCode 	= "ym.etc.land.LandDAO.getProcPlanInfo_04";	
		Object[] params = {strSanctionStepNo,strOwner,strProcKind};	
		
		return super.findByPrimaryKey(queryCode, params);
	}     	
	
      public int deletetProcPlanInfo( String strSanctionStepNo,				//품의차수
						  	String strOwner,						//소유자
						  	String strProcKind	) throws DAOException 	//물건의종류
	{
		/*
		DELETE TB_TJ_LANDUTILPLAN
		WHERE 	SANCTION_STEP_NO 	= :SANCTION_STEP_NO
		AND 	OWNER 				= :OWNER 
		AND 	PROC_KIND 			= :PROC_KIND 
		*/
		String  queryCode = "ym.etc.land.LandDAO.deletetProcPlanInfo";			
		Object[] params = {strSanctionStepNo,strOwner,strProcKind};	
		return super.deleteData(queryCode, params);
	}
	
	public int deletetProcHistInfo( ) throws DAOException 
	{
		/*
		DELETE TB_TJ_LANDUTILHIST
		*/
		String  queryCode = "ym.etc.land.LandDAO.deletetProcHistInfo";			
		return super.deleteData(queryCode);
	}
	
	public List getProcPlanList_01() throws DAOException{
		/*
		SELECT 
				*
		FROM TB_TJ_LANDUTILPLAN
		WHERE del_yn  = :del_yn
		*/
		String  queryCode = "ym.etc.land.LandDAO.getProcPlanList_01";	
		Object[] params = {"Y"};	
		
		return super.findList(queryCode, params);
      }
	
	public JDTORecord requestgetData(String queryCode,Object[] objs) throws DAOException{
		return super.findByPrimaryKey(queryCode, objs);          
    	}
	
	public List getListData(String queryCode,Object[] objs) throws DAOException{
		return super.findList(queryCode, objs);	
    }
	
	public List getListData(String query, List whereData) throws DAOException{	
		return super.findList(query, whereData.toArray());
	}	
}

