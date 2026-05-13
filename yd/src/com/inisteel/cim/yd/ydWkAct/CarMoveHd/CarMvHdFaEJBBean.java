package com.inisteel.cim.yd.ydWkAct.CarMoveHd;

import java.util.List;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;

import jspeed.base.record.JDTORecord;
import jspeed.base.util.DateHelper;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;


/**
 * 차량이동처리 Facade Session EJB 
 *
 * @ejb.bean name="CarMvHdFaEJB" jndi-name="CarMvHdFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CarMvHdFaEJBBean extends BaseSessionBean {
	
	// Session Name 
	private String szSessionName=getClass().getName(); 
	
	private YdUtils ydUtils =new YdUtils();
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	private SlabYdCommDAO slabYdCommDao = new SlabYdCommDAO();
	private YdSlabUtils  slabUtils = new YdSlabUtils();
	private EJBConnector ydEjbCon = new EJBConnector("default", this);
	
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	
	/**
     * 오퍼레이션명 : 소재차량도착Point요구 
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvMatlCarArrPntReq(JDTORecord inRecord) throws DAOException
    {
	    //
	    // YD-UC-???? 소재차량도착Point요구
	    // TC : TSYDJ002, YDYDJ630
	    // 구내운송시스템으로부터 소재차량도착Point요구 수신

        String szMsg="";
        String szMethodName="rcvMatlCarArrPntReq";

        if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName))
        {
            szMsg= szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
        }

        try 
        {
        	
        	String szARR_WLOC_CD = ydDaoUtils.paraRecChkNull(inRecord, "WLOC_CD");
        	
        	if ("DJY21".equals(szARR_WLOC_CD)    // 소재ABCDE
       		 || "DJY22".equals(szARR_WLOC_CD)    // 소재FGH
       		 || "DJY1E".equals(szARR_WLOC_CD)) { // 제품
        		/**********************************
            	 * 2열연코일야드 신규모듈 적용여부 
            	 **********************************/
            	YdPlateCommDAO ydCommDao = new YdPlateCommDAO();
            	JDTORecord jrAppYn = ydCommDao.get2HrAppYn();
            	
            	String s2HrAppYn = StringHelper.evl(jrAppYn.getFieldString("CCOIL_EFF_YN"), "N");; //2열연 코일야드 적용여부
        		
        		ydUtils.putLog(szSessionName, szMethodName,"2열연코일야드 신규모듈 적용여부 : " + s2HrAppYn, YdConstant.DEBUG);
        		
        		if ("Y".equals(s2HrAppYn)) {
        			ydEjbCon.trx("CCommSeEJB", "rcvInterface", inRecord); //2열연 신규모듈		
        		} else {
        			ydEjbCon.trx("CarMvHdSeEJB", "procMatlCarArrPntReq", inRecord);
        		}
        	} else {
                if ("DWY22".equals(szARR_WLOC_CD)/*2후판*/ || "DKY21".equals(szARR_WLOC_CD))/*1후판 */     		
                { 
    		       		/**********************************
    		           	 * 후판 슬라브 신규모듈 적용여부 
    		           	 **********************************/
    		           	YdPlateCommDAO ydCommDao = new YdPlateCommDAO();
    		           	JDTORecord jrAppYn = ydCommDao.getPSlabAppYn();
    		           	
    		           	String pSlabAppYn = StringHelper.evl(jrAppYn.getFieldString("PSLAB_EFF_YN"), "N");; //2열연 코일야드 적용여부
    		       		
    		       		ydUtils.putLog(szSessionName, szMethodName,"후판슬라브야드 신규모듈 적용여부 : " + pSlabAppYn, YdConstant.DEBUG);
    		       		
    		       		if ("Y".equals(pSlabAppYn)) {
    		       			ydEjbCon.trx("PSlabYdCommEJB", "rcvInterface", inRecord); //후판Slab -신규모듈-		
    		       		} else {
        		  			ydEjbCon.trx("CarMvHdSeEJB", "procMatlCarArrPntReq", inRecord);
    		       		}
    		  	} else {
    		  			ydEjbCon.trx("CarMvHdSeEJB", "procMatlCarArrPntReq", inRecord);
    		  	}

        	}
        	
            /***************************************************************
             * 박판열연 신규모듈 적용 여부
             **************************************************************/
            String sASLAB_EFF_YN = "N";
            String sACOIL_EFF_YN = "N";

            if("D2Y43".equals(szARR_WLOC_CD) || "D2Y44".equals(szARR_WLOC_CD)|| "D2Y45".equals(szARR_WLOC_CD))
            {
                YdPlateCommDAO commDao = new YdPlateCommDAO();
                JDTORecord jrResult = commDao.getYfNewModuleEffYn();

                sASLAB_EFF_YN = StringHelper.evl(jrResult.getFieldString("ASLAB_EFF_YN"), "N");
                sACOIL_EFF_YN = StringHelper.evl(jrResult.getFieldString("ACOIL_EFF_YN"), "N");

                szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연SLAB야드신규적용:" + sASLAB_EFF_YN + " ,A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
            }

            if("D3Y43".equals(szARR_WLOC_CD) || "D3Y44".equals(szARR_WLOC_CD) || "D3Y41".equals(szARR_WLOC_CD) || "D3Y42".equals(szARR_WLOC_CD))
            {
                //B열연 신규모듈 호출
                ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
            }
            else if("D2Y43".equals(szARR_WLOC_CD) && "Y".equals(sASLAB_EFF_YN))
            {
            	//A열연 SLAB 신규모듈 호출
                ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
            }
            else if( ("D2Y44".equals(szARR_WLOC_CD)	|| "D2Y45".equals(szARR_WLOC_CD)) && "Y".equals(sACOIL_EFF_YN) )
            {
            	//A열연 COIL 신규모듈 호출
                ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
            }
            else
            {
                //기존모듈 호출
                ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
            }
        }
        catch (Exception e)
        {
            szMsg =szMethodName + "() " +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(szMsg);
        } // end of try catch

        szMsg="소재차량도착Point요구 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
    } // end of rcvMatlCarArrPntReq()
	
	
	/**
	 * 오퍼레이션명 : 소재차량도착Point요구(통합야드용)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws DAOException
	 */ 
	public GridData rcvMatlCarArrPntReq2(GridData inDto) throws DAOException {
		
		String szMethodName="rcvMatlCarArrPntReq2";		
		String szLogMsg = "";
		String szOperationName	= "소재차량도착Point요구(통합야드용)";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		Integer returnValue = null;
		
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);

			returnValue = (Integer) ejbConn.trx("procMatlCarArrPntReq2",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			//gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			if(returnValue.intValue() == 1) {
				gdRes.setMessage("정상적으로 동 지정이 완료되었습니다.");
			} else {
				gdRes.setMessage("0");		
				m_ctx.setRollbackOnly();
				return gdRes;
			}
		}catch(Exception e){			
			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
		}
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	/**
     * 오퍼레이션명 : 출발취소정보 수신
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvCarstartDelete(JDTORecord inRecord) throws DAOException
    {
	    //
	    // YD-UC-???? 출발취소정보
	    // TC : TSYDJ014
	    // 구내운송시스템으로부터  출발취소정보 수신

    	String szMsg="";
        String szMethodName="rcvCarstartDelete";

        if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName))
        {
            szMsg= szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
        }

        try
        {
            //수신 전문에서 TRN_EQP_CD 를 추출
            String szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(inRecord, "TRN_EQP_CD");
            String szSPOS_WLOC_CD = "";

            //차량스케줄을 운송장비코드로 조회 ym.tsinfo.getListSposYNchk_E
            //SPOS_WLOC_CD 로 판단.
            List sposChklist = ymCommonDAO.getInstance().getCommonList("ym.tsinfo.getListSposYNchk_E",new Object[]{szTRN_EQP_CD});
            
            if(sposChklist.size() > 0)
            {
                szSPOS_WLOC_CD = StringHelper.evl(((JDTORecord)sposChklist.get(0)).getFieldString("SPOS_WLOC_CD"),"");
            }

            
            /***************************************************************
             * 박판열연 신규모듈 적용 여부
             **************************************************************/
            String sASLAB_EFF_YN = "N";
            String sACOIL_EFF_YN = "N";

            if("D2Y43".equals(szSPOS_WLOC_CD) || "D2Y44".equals(szSPOS_WLOC_CD)|| "D2Y45".equals(szSPOS_WLOC_CD))
            {
                YdPlateCommDAO commDao = new YdPlateCommDAO();
                JDTORecord jrResult = commDao.getYfNewModuleEffYn();

                sASLAB_EFF_YN = StringHelper.evl(jrResult.getFieldString("ASLAB_EFF_YN"),"N");
                sACOIL_EFF_YN = StringHelper.evl(jrResult.getFieldString("ACOIL_EFF_YN"),"N");

                szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연SLAB야드신규적용:" + sASLAB_EFF_YN + " ,A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
            }

            if( "D3Y43".equals(szSPOS_WLOC_CD) || "D3Y44".equals(szSPOS_WLOC_CD) || "D3Y41".equals(szSPOS_WLOC_CD) || "D3Y42".equals(szSPOS_WLOC_CD) )
            {
                //B열연
                ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
            }
            else if( "D2Y43".equals(szSPOS_WLOC_CD) )
            {
            	//A열연 신규모듈 호출
                ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
            }
            else if( ("D2Y44".equals(szSPOS_WLOC_CD) || "D2Y45".equals(szSPOS_WLOC_CD)) && "Y".equals(sACOIL_EFF_YN) )
            {
            	//A열연 신규모듈 호출
                ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
            }
            
            else if("DJY21".equals(szSPOS_WLOC_CD)    // 소재ABCDE
               	 || "DJY22".equals(szSPOS_WLOC_CD)    // 소재FGH
               	 || "DJY1E".equals(szSPOS_WLOC_CD)) { // 제품
            	/**********************************
            	 * 2열연코일야드 신규모듈 적용여부 
            	 **********************************/
            	YdPlateCommDAO ydCommDao = new YdPlateCommDAO();
            	JDTORecord jrAppYn = ydCommDao.get2HrAppYn();
            	
            	String s2HrAppYn = StringHelper.evl(jrAppYn.getFieldString("CCOIL_EFF_YN"), "N");; //2열연 코일야드 적용여부

            	ydUtils.putLog(szSessionName, szMethodName,"2열연코일야드 신규모듈 적용여부 : " + s2HrAppYn, YdConstant.DEBUG);
            	
            	if ("Y".equals(s2HrAppYn)) {
           			ydEjbCon.trx("CCommSeEJB"  , "rcvInterface"   , inRecord); //2열연 신규모듈		
            	} else {
            		ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
            	}
            }
            else
            {
                //기존모듈 호출
                ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
            }

        } catch (Exception e) {
            szMsg =szMethodName + "() " +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(szMsg);

        } // end of try catch

        szMsg="차량출발취소처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

    } // end of rcvMatlCarArr()
	
	
    /**
     * 오퍼레이션명 : 소재차량도착
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvMatlCarArr(JDTORecord inRecord) throws DAOException
    {
	    // YD-UC-???? 소재차량도착
	    // TC : TSYDJ003
	    // 구내운송시스템으로부터 소재차량도착 수신

    	String szMsg="";
        String szMethodName="rcvMatlCarArr";
        ymCommonDAO dao = ymCommonDAO.getInstance();

        if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName))
        {
            szMsg= szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
        }

        try
        {
            //수신 전문에서 ARR_WLOC_CD 를 추출
            String szARR_WLOC_CD 			= ydDaoUtils.paraRecChkNull(inRecord, "ARR_WLOC_CD");
            String szTRN_WRK_FULLVOID_GP	= ydDaoUtils.paraRecChkNull(inRecord, "TRN_WRK_FULLVOID_GP");
            

            /******************************
             * AB열연
             ******************************/
            if("D3Y41".equals(szARR_WLOC_CD) || "D3Y42".equals(szARR_WLOC_CD) || "D3Y43".equals(szARR_WLOC_CD) || "D3Y44".equals(szARR_WLOC_CD))
            {
            	ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
            }
            else if("D2Y43".equals(szARR_WLOC_CD) || "D2Y44".equals(szARR_WLOC_CD) || "D2Y45".equals(szARR_WLOC_CD))
            {
                String sASLAB_EFF_YN = "N";
                String sACOIL_EFF_YN = "N";

                YdPlateCommDAO commDao = new YdPlateCommDAO();
                JDTORecord jrResult = commDao.getYfNewModuleEffYn();

                sASLAB_EFF_YN = StringHelper.evl(jrResult.getFieldString("ASLAB_EFF_YN"),"N");
                sACOIL_EFF_YN = StringHelper.evl(jrResult.getFieldString("ACOIL_EFF_YN"),"N");

                szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연SLAB야드신규적용:" + sASLAB_EFF_YN + " ,A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);

                if( "D2Y43".equals(szARR_WLOC_CD) && "Y".equals(sASLAB_EFF_YN) )
                {
                    //A열연 SLAB야드 신규모듈 적용
                    ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
                }
                else if( ("D2Y44".equals(szARR_WLOC_CD) || "D2Y45".equals(szARR_WLOC_CD)) && "Y".equals(sACOIL_EFF_YN) )
                {
                    //A열연 COIL야드 신규모듈 적용
                    ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
                }
                else
                {
                    //A열연 기존 방식 호출.
                    ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
                }
            }
            else
            {
                // AB열연 기존 방식 호출.
                ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
            }

            /******************************
             * 일관제철
             ******************************/
            //통합야드 처리 방식 변경
            if ("DJY25".equals(szARR_WLOC_CD)||"DYY15".equals(szARR_WLOC_CD)||"BSY01".equals(szARR_WLOC_CD)||"BSY02".equals(szARR_WLOC_CD)||"BSY03".equals(szARR_WLOC_CD)) {
            	//(비상야드추가)
                ydEjbCon.trx("CarMvHdSeEJB", "procMatlCarArrS", inRecord);
                
            } else if ("DJY21".equals(szARR_WLOC_CD)    // 소재ABCDE
        		    || "DJY22".equals(szARR_WLOC_CD)    // 소재FGH
        		    || "DJY1E".equals(szARR_WLOC_CD)) { // 제품
            	/**********************************
            	 * 2열연코일야드 신규모듈 적용여부 
            	 **********************************/
            	YdPlateCommDAO ydCommDao = new YdPlateCommDAO();
            	JDTORecord jrAppYn = ydCommDao.get2HrAppYn();
            	
            	String s2HrAppYn = StringHelper.evl(jrAppYn.getFieldString("CCOIL_EFF_YN"), "N");; //2열연 코일야드 적용여부
            	
            	ydUtils.putLog(szSessionName, szMethodName,"2열연코일야드 신규모듈 적용여부 : " + s2HrAppYn, YdConstant.DEBUG);

            	if ("Y".equals(s2HrAppYn)) {
           			ydEjbCon.trx("CCommSeEJB", "rcvInterface", inRecord); //2열연 신규모듈		
            	} else {
            		ydEjbCon.trx("CarMvHdSeEJB", "procMatlCarArr", inRecord);
            	}
            	
            } 
            else if ("DWY22".equals(szARR_WLOC_CD)/*2후판*/ || "DKY21".equals(szARR_WLOC_CD))/*1후판 */     		
            { 
	       		/**********************************
	           	 * 후판 슬라브 신규모듈 적용여부 
	           	 **********************************/
	           	YdPlateCommDAO ydCommDao = new YdPlateCommDAO();
	           	JDTORecord jrAppYn = ydCommDao.getPSlabAppYn();
	           	
	           	String pSlabAppYn = StringHelper.evl(jrAppYn.getFieldString("PSLAB_EFF_YN"), "N");; //2열연 코일야드 적용여부
	       		
	       		ydUtils.putLog(szSessionName, szMethodName,"후판슬라브야드 신규모듈 적용여부 : " + pSlabAppYn, YdConstant.DEBUG);
	       		
	       		if ("Y".equals(pSlabAppYn)) {
	       			ydEjbCon.trx("PSlabYdCommEJB", "rcvInterface", inRecord); //후판Slab -신규모듈-		
	       		} else {
	       			ydEjbCon.trx("CarMvHdSeEJB", "procMatlCarArr", inRecord);
	       		}
	  	    }
            else 
            {
            	ydEjbCon.trx("CarMvHdSeEJB", "procMatlCarArr", inRecord);
            }
            cSlabYdDisplayTc(inRecord);
            
        } catch (Exception e) {
            szMsg =szMethodName + "() " +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(szMsg);
            
        } // end of try catch

        szMsg="소재차량도착 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
    } // end of rcvMatlCarArr()
    
    
    /**
     * 오퍼레이션명 : 소재차량 대기장도착 (TSYDJ005)
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvMatlCarWaitLocArr(JDTORecord inRecord) throws DAOException {

    	String szMsg="";
        String szMethodName="rcvMatlCarWaitLocArr";

        if (!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)) {
            szMsg= szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
        }

        try  {
            //수신 전문에서 ARR_WLOC_CD 를 추출
            String szARR_WLOC_CD = ydDaoUtils.paraRecChkNull(inRecord, "ARR_WLOC_CD");
            String szTRN_WRK_FULLVOID_GP	= ydDaoUtils.paraRecChkNull(inRecord, "TRN_WRK_FULLVOID_GP");
            /******************************
             * 1열연 코일야드
             ******************************/
            if ("D3Y41".equals(szARR_WLOC_CD) || "D3Y42".equals(szARR_WLOC_CD) || "D3Y43".equals(szARR_WLOC_CD) || "D3Y44".equals(szARR_WLOC_CD)) {
            	ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
            } 
            
            /******************************
             * 2열연 코일야드
             ******************************/
            if ("DJY22".equals(szARR_WLOC_CD) || "DJY1E".equals(szARR_WLOC_CD)) {
           		ydEjbCon.trx("CCommSeEJB", "rcvInterface", inRecord);		
            } 
            
            /******************************
             * 박판열연 코일야드
             ******************************/
            if ("D2Y44".equals(szARR_WLOC_CD) || "D2Y45".equals(szARR_WLOC_CD)) {
            	ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);		
            }
            
            cSlabYdDisplayTc(inRecord);
            
        } catch (Exception e) {
            szMsg =szMethodName + "() " +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(szMsg);
            
        } // end of try catch

        szMsg="소재차량 대기장도착 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
    } // end of rcvMatlCarWaitLocArr()
	
	
	/**
	 * 오퍼레이션명 : 스케줄기준을 체크한 후 가능하면 크레인스케줄 메인을 호출하는 메소드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws DAOException
	 */ 
	public void rcvSchRuleNCallCrnSch(JDTORecord inRecord) throws DAOException  {
	// 
	// YD-UC-???? 스케줄기준체크/크레인호출
	// TC : 
	// 스케줄기준체크/크레인호출
	//
	//┏━┓
	//┃
	//┗━┛
		
		String szMsg="";
		String szMethodName				= "rcvSchRuleNCallCrnSch";
		String szOperationName			= "스케줄기준체크/크레인호출";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
		
		
		try {
			
			ydEjbCon.trx("CarMvHdSeEJB", "procSchRuleNCallCrnSch", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new DAOException(szMsg);

		} // end of try catch
		
		
		
		szMsg="["+szOperationName+"] 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvMatlCarArr()
	
	
	/**
	 * 오퍼레이션명 : 소재차량출발
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws DAOException
	 */ 
	public void rcvMatlCarLev_OLD(JDTORecord inRecord) throws DAOException  {
	// 
	// YD-UC-???? 소재차량출발
	// TC : TSYDJ004
	// 구내운송시스템으로부터 소재차량출발 수신
	//
	//┏━┓
	//┃ 
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvMatlCarLev";
		String szSPOS_WLOC_CD = null;					//발지개소코드
		String szARR_WLOC_CD = null;					//착지개소코드
		boolean isSPOS_WLOC_CDForAB	= false;			//발지개소코드가 AB열연인 지를 판단하는 변수
		boolean isARR_WLOC_CDForAB = false;				//착지개소코드가 AB열연인 지를 판단하는 변수
		String szTRN_WRK_FULLVOID_GP = null;
		String szYD_WO_CNCL_YN = null;
		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}

		
		try {
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(inRecord, "SPOS_WLOC_CD");
			szARR_WLOC_CD = ydDaoUtils.paraRecChkNull(inRecord, "ARR_WLOC_CD");
			szTRN_WRK_FULLVOID_GP = ydDaoUtils.paraRecChkNull(inRecord, "TRN_WRK_FULLVOID_GP");
			szYD_WO_CNCL_YN = ydDaoUtils.paraRecChkNull(inRecord, "YD_WO_CNCL_YN");	//취소전문 처리 프로세스 y: 취소가능
			
			if( szSPOS_WLOC_CD.equals("") || szARR_WLOC_CD.equals("") ) {
				szMsg = "[JSP Facade] 소재차량출발실적 수신 시 발지개소코드["+szSPOS_WLOC_CD+"]나 착지개소코드["+szARR_WLOC_CD+"]가 없습니다." ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			
			szMsg = "[JSP Facade] 소재차량출발실적 수신 시 발지개소코드["+szSPOS_WLOC_CD+"]나 착지개소코드["+szARR_WLOC_CD+"]" ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			isSPOS_WLOC_CDForAB = YdCommonUtils.getABLocationInfo(szSPOS_WLOC_CD);
			isARR_WLOC_CDForAB = YdCommonUtils.getABLocationInfo(szARR_WLOC_CD);
			
			szMsg = "[JSP Facade] 소재차량출발실적 수신 시 발지개소코드["+szSPOS_WLOC_CD+"]가 AB열연 " + (isSPOS_WLOC_CDForAB ? "입니다." : "이 아닙니다.") ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg = "[JSP Facade] 소재차량출발실적 수신 시 착지개소코드["+szARR_WLOC_CD+"]가 AB열연 " + (isARR_WLOC_CDForAB ? "입니다." : "이 아닙니다.") ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			/***************************************************************
			 * B열연 신규모듈 적용 여부 
			 **************************************************************/
			String sBSLAB_EFF_YN = "N";
			String sBCOIL_EFF_YN = "N";
			String sBCOIL_EFF_YN1 = "N";
			
//			if(szARR_WLOC_CD.equals("D3Y41") || szARR_WLOC_CD.equals("D3Y42") 
//					|| szARR_WLOC_CD.equals("D3Y43") || szARR_WLOC_CD.equals("D3Y44")
//					|| szSPOS_WLOC_CD.equals("D3Y43")) {
				
			if(szARR_WLOC_CD.equals("D3Y41") || szARR_WLOC_CD.equals("D3Y42") 
			|| szARR_WLOC_CD.equals("D3Y43") || szARR_WLOC_CD.equals("D3Y44")
//SJH			
			|| szSPOS_WLOC_CD.equals("D3Y41") || szSPOS_WLOC_CD.equals("D3Y42")
			
			|| szSPOS_WLOC_CD.equals("D3Y43")) {
				YdPlateCommDAO commDao = new YdPlateCommDAO();
				JDTORecord jrResult = commDao.getNewModuleEffYn();
				
				sBSLAB_EFF_YN = StringHelper.evl(jrResult.getFieldString("BSLAB_EFF_YN"),"N");
				sBCOIL_EFF_YN = StringHelper.evl(jrResult.getFieldString("BCOIL_EFF_YN"),"N");
				sBCOIL_EFF_YN1 = StringHelper.evl(jrResult.getFieldString("BCOIL_EFF_YN1"),"N");
				
				szMsg = "YdPlateCommDAO.getNewModuleEffYn()---[[[ B열연SLAB야드신규적용:" + sBSLAB_EFF_YN + " ,B열연COIL야드신규적용:" + sBCOIL_EFF_YN + " ]]]---"; 
				ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
			}				
			
			
			/***************************************************************
			 * 이송지시 취소기능 추가(코일야드)
			 **************************************************************/
			if(szYD_WO_CNCL_YN.equals("Y")&&
				(szSPOS_WLOC_CD.equals("D2Y44")||szSPOS_WLOC_CD.equals("D2Y45")
				||szSPOS_WLOC_CD.equals("D3Y41")||szSPOS_WLOC_CD.equals("D3Y42")
				||szSPOS_WLOC_CD.equals("DJY21")||szSPOS_WLOC_CD.equals("DJY22")||szSPOS_WLOC_CD.equals("DJY1E"))){
				
				szMsg = "[JSP Facade] 소재차량출발실적 수신 시 착지개소코드["+szSPOS_WLOC_CD+"]로 차량스케줄 취소작업" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if(szSPOS_WLOC_CD.equals("DJY21")||szSPOS_WLOC_CD.equals("DJY22")||szSPOS_WLOC_CD.equals("DJY1E")  ) {
					//C열연 취소 인 경우.					
					EJBConnector ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);          
			        ejbConn.trx("CarinfoResetC", new Class[]{ JDTORecord.class }, new Object[]{ inRecord } );
				 
				}else{
					
					if("Y".equals(sBCOIL_EFF_YN)) {
						//B열연 신규모듈 취소 인 경우.	 
						EJBConnector ejbConn = new EJBConnector("default", "YmCommCarMvSeEJB", this);          
				        ejbConn.trx("initCarSch", new Class[]{ JDTORecord.class }, new Object[]{ inRecord } );
					} else {
						//AB열연 취소 인 경우.	 
						EJBConnector ejbConn = new EJBConnector("default", "JNDITsInfoReg", this);          
				        ejbConn.trx("CarinfoReset", new Class[]{ JDTORecord.class }, new Object[]{ inRecord } );
					}
				}
				
				return;
			}
			
			
			/*
			 * AB열연 송신.
			 */
			//1. 일관제철 야드[발지개소코드]에서 AB열연[착지재소코드]으로  출발
			if( !isSPOS_WLOC_CDForAB && isARR_WLOC_CDForAB  ) {
				szMsg = "[JSP Facade] 소재차량출발실적 수신 시 야드모듈을 먼저 호출하고 AB열연 모듈을 호출처리 " ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				ydEjbCon.trx("CarMvHdSeEJB", "procMatlCarLev", inRecord);
				
				if( ((szARR_WLOC_CD.equals("D3Y43") || szARR_WLOC_CD.equals("D3Y44")) && sBSLAB_EFF_YN.equals("Y"))
						|| ((szARR_WLOC_CD.equals("D3Y41") || szARR_WLOC_CD.equals("D3Y42")) && sBCOIL_EFF_YN.equals("Y")) ) {
					//B열연 신규모듈 호출
					ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
					
				} else {
					//기존모듈 호출
					ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
				}
				
			}else{
				
				szMsg = "[JSP Facade] 소재차량출발실적 수신 시 AB열연 모듈을 먼저 호출하고 야드모듈을 호출처리 " ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if( ((szARR_WLOC_CD.equals("D3Y43") || szARR_WLOC_CD.equals("D3Y44")) && sBSLAB_EFF_YN.equals("Y"))
						|| ((szARR_WLOC_CD.equals("D3Y41") || szARR_WLOC_CD.equals("D3Y42")) && sBCOIL_EFF_YN.equals("Y")) ) {
					//B열연 신규모듈 호출
					ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
				} else if(szSPOS_WLOC_CD.equals("D3Y43") && sBSLAB_EFF_YN.equals("Y")) {
					//B열연 SLAB 신규모듈 호출
					ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
//SJH				                 	
				} else if((szSPOS_WLOC_CD.equals("D3Y41")||szSPOS_WLOC_CD.equals("D3Y42")) && sBCOIL_EFF_YN1.equals("Y")) {
					//B열연 SLAB 신규모듈 호출
					ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
				} else {
					//기존모듈 호출
					ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
				}
				
				ydEjbCon.trx("CarMvHdSeEJB", "procMatlCarLev", inRecord);
			}

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new DAOException(szMsg);

		} // end of try catch
		
		
		szMsg="소재차량출발 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvMatlCarLev()
	
	
	/**
     * 오퍼레이션명 : 소재차량출발
     *
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param inRecord
     * @return:
     * @throws DAOException
     */
    public void rcvMatlCarLev(JDTORecord inRecord) throws DAOException {
    	// YD-UC-???? 소재차량출발
    	// TC : TSYDJ004
    	// 구내운송시스템으로부터 소재차량출발 수신

        String	szMsg					= "";
        String	szMethodName			= "rcvMatlCarLev";
        String	szSPOS_WLOC_CD			= null;		//발지개소코드
        String	szARR_WLOC_CD			= null;		//착지개소코드
        boolean	isSPOS_WLOC_CDForAB		= false;	//발지개소코드가 AB열연인 지를 판단하는 변수
        boolean	isARR_WLOC_CDForAB		= false;	//착지개소코드가 AB열연인 지를 판단하는 변수
        String	szTRN_WRK_FULLVOID_GP	= null;
        String	szYD_WO_CNCL_YN			= null;		//야드지시취소여부
        
        if ( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)) {
            szMsg= szMethodName+"() 실행 실패";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            return;
        }

        try {
            szSPOS_WLOC_CD			= ydDaoUtils.paraRecChkNull(inRecord, "SPOS_WLOC_CD");
            szARR_WLOC_CD			= ydDaoUtils.paraRecChkNull(inRecord, "ARR_WLOC_CD");
            szTRN_WRK_FULLVOID_GP	= ydDaoUtils.paraRecChkNull(inRecord, "TRN_WRK_FULLVOID_GP");
            szYD_WO_CNCL_YN			= ydDaoUtils.paraRecChkNull(inRecord, "YD_WO_CNCL_YN"); //취소전문 처리 프로세스 y: 취소가능

            if( szSPOS_WLOC_CD.equals("") || szARR_WLOC_CD.equals("") ) {
                szMsg = "[JSP Facade] 소재차량출발실적 수신 시 발지개소코드["+szSPOS_WLOC_CD+"]나 착지개소코드["+szARR_WLOC_CD+"]가 없습니다." ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                
                return;
            }

            szMsg = "[JSP Facade] 소재차량출발실적(TSYDJ004) 수신 : 발지개소코드["+szSPOS_WLOC_CD+"] / 착지개소코드["+szARR_WLOC_CD+"]" ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            isSPOS_WLOC_CDForAB	= YdCommonUtils.getABLocationInfo(szSPOS_WLOC_CD);	//발지개소코드가 AB열연인지 확인
            isARR_WLOC_CDForAB	= YdCommonUtils.getABLocationInfo(szARR_WLOC_CD);	//착지개소코드가 AB열연인지 확인

            szMsg = "[JSP Facade] 소재차량출발실적(TSYDJ004) 수신 : 발지개소코드[" + szSPOS_WLOC_CD + "]가 AB열연 " + (isSPOS_WLOC_CDForAB ? " 입니다." : " 이 아닙니다.") ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            szMsg = "[JSP Facade] 소재차량출발실적(TSYDJ004) 수신 : 착지개소코드[" + szARR_WLOC_CD + "]가 AB열연 " + (isARR_WLOC_CDForAB ? " 입니다." : " 이 아닙니다.") ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


            /***************************************************************
             * 박판열연 신규모듈 적용 여부
             **************************************************************/
            String sASLAB_EFF_YN	= "N";		//박판SLAB 신규모듈여부
            String sACOIL_EFF_YN	= "N";		//박판COIL 신규모듈여부
            String sMODULE_YN		= "N";		//모듈적용여부

            if (
            	"D2Y43".equals(szARR_WLOC_CD)	||	//착지개소코드(박판연주-B Cast Slab Yard)
            	"D2Y44".equals(szARR_WLOC_CD)	||	//착지개소코드(박판열연-#1 제품/소재 Coil Yard)
            	"D2Y45".equals(szARR_WLOC_CD)	||	//착지개소코드(박판열연-#1 제품/소재 Coil Yard)
            	"D2Y43".equals(szSPOS_WLOC_CD)	||	//발지개소코드(박판연주-B Cast Slab Yard)
            	"D2Y44".equals(szSPOS_WLOC_CD)	||	//발지개소코드(박판열연-#1 제품/소재 Coil Yard)
            	"D2Y45".equals(szSPOS_WLOC_CD)		//발지개소코드(박판열연-#1 제품/소재 Coil Yard)
            ) {
            	//박판열연 신규모듈 적용여부 조회
                YdPlateCommDAO commDao	= new YdPlateCommDAO();
                JDTORecord jrResult		= commDao.getYfNewModuleEffYn();

                sASLAB_EFF_YN	= StringHelper.evl(jrResult.getFieldString("ASLAB_EFF_YN"),	"N");
                sACOIL_EFF_YN	= StringHelper.evl(jrResult.getFieldString("ACOIL_EFF_YN"),	"N");
                sMODULE_YN		= StringHelper.evl(jrResult.getFieldString("MODULE_YN"),	"N");

                szMsg = "YdPlateCommDAO.getYfNewModuleEffYn()---[[[ A열연SLAB야드신규적용:" + sASLAB_EFF_YN + " ,A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
            }
            
//            if ("N".equals(sMODULE_YN)) {
//            	ydUtils.putLog(szSessionName, szMethodName,"********************** 기존 모듈을 호출함 **********************", YdConstant.DEBUG);
//            	this.rcvMatlCarLev_OLD(inRecord);
//            	
//            	return;
//            }

        	/**********************************
        	 * 2열연코일야드 신규모듈 적용여부 
        	 **********************************/
        	YdPlateCommDAO ydCommDao = new YdPlateCommDAO();
        	JDTORecord jrAppYn = ydCommDao.get2HrAppYn();
        	
        	String s2HrAppYn = StringHelper.evl(jrAppYn.getFieldString("CCOIL_EFF_YN"), "N");; //2열연 코일야드 적용여부
        	
        	ydUtils.putLog(szSessionName, szMethodName,"2열연코일야드 신규모듈 적용여부 : " + s2HrAppYn, YdConstant.DEBUG);
        	
            /***************************************************************
             * 이송지시 취소기능 추가(코일야드)
             **************************************************************/
            if (
            	"Y".equals(szYD_WO_CNCL_YN)	&&			//야드지시취소여부(Y)
                (
                	"D2Y44".equals(szSPOS_WLOC_CD)	||	//발지개소코드(박판열연-#1 제품/소재 Coil Yard)
                	"D2Y45".equals(szSPOS_WLOC_CD)	||	//발지개소코드(박판열연-#2 제품/소재 Coil Yard)
                	"D3Y41".equals(szSPOS_WLOC_CD)	||	//발지개소코드(1열연-#1 제품/소재 Coil Yard)
                	"D3Y42".equals(szSPOS_WLOC_CD)	||	//발지개소코드(1열연-#2 제품/소재 Coil Yard)
                	"DJY21".equals(szSPOS_WLOC_CD)	||	//발지개소코드(2열연-정정 #1 Yard(소재통로 D,E동))
                	"DJY22".equals(szSPOS_WLOC_CD)	||	//발지개소코드(2열연-정정 #2 Yard(소재통로 H동))
                	"DJY1E".equals(szSPOS_WLOC_CD)		//발지개소코드(2열연-결번재 적치장)
                )
            ) {
                szMsg = "[JSP Facade] 소재차량출발실적 수신 : 착지개소코드[" + szSPOS_WLOC_CD + "]로 차량스케줄 취소작업" ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                if ("DJY21".equals(szSPOS_WLOC_CD) || "DJY22".equals(szSPOS_WLOC_CD) || "DJY1E".equals(szSPOS_WLOC_CD)) {
                	if ("Y".equals(s2HrAppYn)) {
                		EJBConnector ejbConn = new EJBConnector("default", "CCommSeEJB", this);
                        ejbConn.trx("rcvInterface", new Class[]{ JDTORecord.class }, new Object[]{ inRecord } );
                	} else {
                        //발지개소코드가 DJY21(2열연-정정 #1 Yard(소재통로 D,E동)), DJY22(2열연-정정 #2 Yard(소재통로 H동)), DJY1E(2열연-결번재 적치장)
                        EJBConnector ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
                        ejbConn.trx("CarinfoResetC", new Class[]{ JDTORecord.class }, new Object[]{ inRecord } );
                	}
                } else if( "D3Y41".equals(szSPOS_WLOC_CD) || "D3Y42".equals(szSPOS_WLOC_CD) ) {
                	//발지개소코드가 D3Y41(1열연-#1 제품/소재 Coil Yard), D3Y42(1열연-#2 제품/소재 Coil Yard)
                	EJBConnector ejbConn = new EJBConnector("default", "YmCommCarMvSeEJB", this);
                	ejbConn.trx("initCarSch", new Class[]{ JDTORecord.class }, new Object[]{ inRecord } );
                } else if( "D2Y44".equals(szSPOS_WLOC_CD) || "D2Y45".equals(szSPOS_WLOC_CD) ) {
                	//발지개소코드가 D2Y44(박판열연-#1 제품/소재 Coil Yard), D2Y45(박판열연-#2 제품/소재 Coil Yard)
                    if ("Y".equals(sACOIL_EFF_YN)) {
                        //박판열연 신규모듈인 경우.
                        EJBConnector ejbConn = new EJBConnector("default", "YfCommCarMvSeEJB", this);
                        ejbConn.trx("initCarSch", new Class[]{ JDTORecord.class }, new Object[]{ inRecord } );
                    } else {
                        //신규모듈이 아닌 기존 AB열연인 경우.
                        EJBConnector ejbConn = new EJBConnector("default", "JNDITsInfoReg", this);
                        ejbConn.trx("CarinfoReset", new Class[]{ JDTORecord.class }, new Object[]{ inRecord } );
                    }
                }

                return;	//취소 후 종료
            }

            /***************************************************************
             * 소재차량 출발 시작
             **************************************************************/
            //1. 일관제철 야드[발지개소코드]에서 AB열연[착지재소코드]으로  출발 (CA)
            if ( !isSPOS_WLOC_CDForAB && isARR_WLOC_CDForAB ) {
            	/***************************************************************
                 * 소재차량 출발 ...........일관제철야드(C) -> AB열연(A)
                 **************************************************************/
                szMsg = "[JSP Facade] ▩▩▩▩▩▩▩▩▩▩ 소재차량출발실적 수신 시 야드모듈을 먼저 호출하고 AB열연 모듈을 호출처리 ▩▩▩▩▩▩▩▩▩▩" ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                //발지처리
                if (("DJY21".equals(szSPOS_WLOC_CD)    // 소재ABCDE
               	  || "DJY22".equals(szSPOS_WLOC_CD)    // 소재FGH
               	  || "DJY1E".equals(szSPOS_WLOC_CD)) && "Y".equals(s2HrAppYn)) { // 제품
                	ydEjbCon.trx("CCommSeEJB"  , "rcvInterface"  , inRecord); //2열연 신규모듈
                } else {
                	ydEjbCon.trx("CarMvHdSeEJB", "procMatlCarLev", inRecord); //야드모듈
                }
                

                //착지처리
                if( "D3Y43".equals(szARR_WLOC_CD) || "D3Y44".equals(szARR_WLOC_CD) )
                {
                	//착지개소가 D3Y43(1열연-Slab Yard), D3Y44(1열연 스카핑 절단슬라브장)
                    ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
                }
                else if( "D3Y41".equals(szARR_WLOC_CD) || "D3Y42".equals(szARR_WLOC_CD) )
                {
                	//착지개소가 D3Y41(1열연-#1 제품/소재 Coil Yard), D3Y42(1열연-#2 제품/소재 Coil Yard)
                    ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
                }
                else if( "D2Y43".equals(szARR_WLOC_CD) && "Y".equals(sASLAB_EFF_YN) ) 
                {
                    //착지개소가 D2Y43(박판연주-B Cast Slab Yard) + 박판SLAB 신규모듈(Y)
                    ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
                }
                else if( ("D2Y44".equals(szARR_WLOC_CD) || "D2Y45".equals(szARR_WLOC_CD)) && "Y".equals(sACOIL_EFF_YN) )
                {
                	//착지개소가 D2Y44(박판열연-#1 제품/소재 Coil Yard), D2Y45(박판열연-#2 제품/소재 Coil Yard) + 박판COIL 신규모듈(Y)
                    ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
                }
                else
                {
                    //AB열연 기존모듈
                    ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
                }
            }
            //2. AB열연[발지개소코드]에서 일관제철 야드[착지재소코드]으로  출발 (AC)
            else if( isSPOS_WLOC_CDForAB && !isARR_WLOC_CDForAB )
            {
            	/***************************************************************
                 * 소재차량 출발 ...........AB열연(A) -> 일관제철야드(C) 
                 **************************************************************/
                szMsg = "[JSP Facade] ▩▩▩▩▩▩▩▩▩▩ 소재차량출발실적 수신 시 AB열연 모듈을 먼저 호출하고 야드모듈을 호출처리 ▩▩▩▩▩▩▩▩▩▩" ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                //발지처리
                if( "D3Y43".equals(szSPOS_WLOC_CD) )
                {
                	//발지개소가 D3Y43(1열연-Slab Yard)
                    ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
                }
                else if( "D3Y41".equals(szSPOS_WLOC_CD) || "D3Y42".equals(szSPOS_WLOC_CD) )
                {
                	//발지개소가 D3Y41(1열연-#1 제품/소재 Coil Yard), D3Y42(1열연-#2 제품/소재 Coil Yard)
                    ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
                }
                else if( "D2Y43".equals(szSPOS_WLOC_CD) && "Y".equals(sASLAB_EFF_YN))
                {
                	//발지개소가 D2Y43(박판연주-B Cast Slab Yard) + 박판SLAB 신규모듈(Y)
                    ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
                }
                else if( ("D2Y44".equals(szSPOS_WLOC_CD) || "D2Y45".equals(szSPOS_WLOC_CD)) && "Y".equals(sACOIL_EFF_YN) )
                {
                	//발지개소가 D2Y44(박판열연-#1 제품/소재 Coil Yard), D2Y45(박판열연-#2 제품/소재 Coil Yard) + 박판COIL 신규모듈(Y)
                    ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
                }
                else
                {
                    //신규모듈이 아닌 기존 AB열연 기존모듈
                    ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
                }
                
                //착지처리
                if (("DJY21".equals(szARR_WLOC_CD)    // 소재ABCDE
             	  || "DJY22".equals(szARR_WLOC_CD)    // 소재FGH
             	  || "DJY1E".equals(szARR_WLOC_CD)) && "Y".equals(s2HrAppYn)) { // 제품
                	ydEjbCon.trx("CCommSeEJB"  , "rcvInterface"  , inRecord); //2열연 신규모듈
                } else {
                	ydEjbCon.trx("CarMvHdSeEJB", "procMatlCarLev", inRecord); //야드모듈
                }
                
            }
            //3. AB열연[발지개소코드]에서  AB열연[착지재소코드]으로  출발 (AA)
            else if( isSPOS_WLOC_CDForAB && isARR_WLOC_CDForAB )
            {
            	/***************************************************************
                 * 소재차량 출발 ...........AB열연(A) -> AB열연(A) 
                 **************************************************************/
            	szMsg = "[JSP Facade] ▩▩▩▩▩▩▩▩▩▩소재차량출발실적 수신 시 착지개소코드에 맞춰 야드모듈을 호출처리(AB열연(A) -> AB열연(A))▩▩▩▩▩▩▩▩▩▩" ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                
                //발지처리를 위해 착지개소코드를 "ZZZZZ"로 임의로 셋팅하여(AA -> AC 변경한 효과) 발지처리할 모듈(YM 또는 YF)를 호출
                JDTORecord inRecord2 = JDTORecordFactory.getInstance().create();	//발지처리용 JDTORecord
            	inRecord2.setField("JMS_TC_CD", inRecord.getFieldString("JMS_TC_CD"));
            	inRecord2.setField("JMS_TC_CREATE_DDTT", inRecord.getFieldString("JMS_TC_CREATE_DDTT"));
            	inRecord2.setField("TRN_EQP_CD", inRecord.getFieldString("TRN_EQP_CD"));
            	inRecord2.setField("SPOS_WLOC_CD", inRecord.getFieldString("SPOS_WLOC_CD"));
            	inRecord2.setField("SPOS_YD_PNT_CD", inRecord.getFieldString("SPOS_YD_PNT_CD"));
            	
            	if("D2Y45".equals(inRecord.getFieldString("SPOS_WLOC_CD")) 
            			&& "D2Y45".equals(inRecord.getFieldString("ARR_WLOC_CD"))){
            		//inRecord2.setField("ARR_WLOC_CD", inRecord.getFieldString("ARR_WLOC_CD"));
            		inRecord2.setField("ARR_WLOC_CD", "ZZZZZ"); // 2025.08.25 RITM1067302  박판열연에서 1열연 회송 시 차량 스케줄 중복 생성 개선 요청
            	}else{
            		inRecord2.setField("ARR_WLOC_CD", "ZZZZZ");	//WORK_GP을 AA가 아닌 AC로 만들어서 발지만 닦아주기위해 강제로 셋팅
            	}
            	
            	inRecord2.setField("ARR_YD_PNT_CD", inRecord.getFieldString("ARR_YD_PNT_CD"));
            	inRecord2.setField("TRN_WRK_FULLVOID_GP", inRecord.getFieldString("TRN_WRK_FULLVOID_GP"));
            	inRecord2.setField("TRN_EQP_STK_CAPA", inRecord.getFieldString("TRN_EQP_STK_CAPA"));
            	inRecord2.setField("CARUD_PAP_LEV_TT", inRecord.getFieldString("CARUD_PAP_LEV_TT"));
            	inRecord2.setField("YD_WO_CNCL_YN", inRecord.getFieldString("YD_WO_CNCL_YN"));
            	inRecord2.setField("JMS_QUEUE_NAME", inRecord.getFieldString("JMS_QUEUE_NAME"));
            	inRecord2.setField("UNIQUE_ID", inRecord.getFieldString("UNIQUE_ID"));
            	inRecord2.setField("IF_TYPE", inRecord.getFieldString("IF_TYPE"));
                
            	if( "D2Y43".equals(szARR_WLOC_CD) ) 
                {
            		//YM, YF분기로 인해 YM측 발지처리
                	String	logId				= inRecord.getFieldString("UNIQUE_ID");
                	String	modifier			= "TSYDJ004";
                	String	szTRN_EQP_CD		= ydDaoUtils.paraRecChkNull(inRecord, "TRN_EQP_CD");		//운송장비코드
                	String	szSPOS_YD_PNT_CD	= ydDaoUtils.paraRecChkNull(inRecord, "SPOS_YD_PNT_CD");	//발지야드포인트코드
                	
                	EJBConnector ejbConn1 = new EJBConnector("default", "YmCommCarMvSeEJB", this);	
					ejbConn1.trx("clearSposCarInfo", new Class[] { String.class, String.class, String.class, String.class, String.class }
									              , new Object[] { szSPOS_WLOC_CD, szSPOS_YD_PNT_CD, szTRN_EQP_CD, logId, modifier });
            		
                    //착지개소가 D2Y43(박판연주-B Cast Slab Yard) + 박판SLAB 신규모듈(Y)
                    ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);
                }
            	else if( ("D3Y43".equals(szARR_WLOC_CD) || "D3Y44".equals(szARR_WLOC_CD)) )
                {
                	//YM, YF분기로 인해 YF측 발지처리
                	String	logId				= inRecord.getFieldString("UNIQUE_ID");
                	String	modifier			= "TSYDJ004";
                	String	szTRN_EQP_CD		= ydDaoUtils.paraRecChkNull(inRecord, "TRN_EQP_CD");		//운송장비코드
                	String	szSPOS_YD_PNT_CD	= ydDaoUtils.paraRecChkNull(inRecord, "SPOS_YD_PNT_CD");	//발지야드포인트코드
                	
                	EJBConnector ejbConn1 = new EJBConnector("default", "YfCommCarMvSeEJB", this);	
					ejbConn1.trx("clearSposCarInfo", new Class[] { String.class, String.class, String.class, String.class, String.class }
									              , new Object[] { szSPOS_WLOC_CD, szSPOS_YD_PNT_CD, szTRN_EQP_CD, logId, modifier });
                	
                	//착지개소가 D3Y43(1열연-Slab Yard), D3Y44(1열연 스카핑 절단슬라브장)
                    ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
                }
            	else if( ("D2Y44".equals(szARR_WLOC_CD) || "D2Y45".equals(szARR_WLOC_CD)) && "N".equals(sACOIL_EFF_YN) )
                {
                	//착지개소가 D2Y44(박판열연-#1 제품/소재 Coil Yard), D2Y45(박판열연-#2 제품/소재 Coil Yard) + 박판COIL 신규모듈(N)
                	ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
                }
            	else if( ("D2Y44".equals(szARR_WLOC_CD) || "D2Y45".equals(szARR_WLOC_CD)) && "Y".equals(sACOIL_EFF_YN) )
                {
                	//착지개소가 D2Y44(박판열연-#1 제품/소재 Coil Yard), D2Y45(박판열연-#2 제품/소재 Coil Yard) + 박판COIL 신규모듈(Y)
            		ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord2);	//YM 발지처리
            		
                    ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord);	//YF 착지처리
                }
                else if( ("D3Y41".equals(szARR_WLOC_CD) || "D3Y42".equals(szARR_WLOC_CD)) && "N".equals(sACOIL_EFF_YN) )
                {
                	//착지개소가 D3Y41(1열연-#1 제품/소재 Coil Yard), D3Y42(1열연-#2 제품/소재 Coil Yard) + 박판COIL 신규모듈(N)
                    ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
                }
                else if( ("D3Y41".equals(szARR_WLOC_CD) || "D3Y42".equals(szARR_WLOC_CD)) && "Y".equals(sACOIL_EFF_YN) )
                {
                	//착지개소가 D3Y41(1열연-#1 제품/소재 Coil Yard), D3Y42(1열연-#2 제품/소재 Coil Yard) + 박판COIL 신규모듈(Y)
                	ydEjbCon.trx("YfRcvFaEJB", "rcvInterface", inRecord2);	//YF 발지처리
                	
                    ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);	//YM 착지처리
                }
                else
                {
                	//신규모듈이 아닌 기존 AB열연 기존모듈
                	ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
                }
            }
            else if ( !isSPOS_WLOC_CDForAB && !isARR_WLOC_CDForAB ) {
            	/***************************************************************
                 * 소재차량 출발 ...........일관제철야드(C) -> 일관제철야드(C)
                 **************************************************************/
            	szMsg = "[JSP Facade] ▩▩▩▩▩▩▩▩▩▩소재차량출발실적 수신 시 착지개소코드에 맞춰 야드모듈을 호출처리(일관제철야드(C) -> 일관제철야드(C))▩▩▩▩▩▩▩▩▩▩" ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

                if ("DJY21".equals(szARR_WLOC_CD )    // 소재ABCDE
           		 || "DJY22".equals(szARR_WLOC_CD )    // 소재FGH
           		 || "DJY1E".equals(szARR_WLOC_CD )    // 제품
                ) { 
                	if ("Y".equals(s2HrAppYn)) {
                		ydEjbCon.trx("CCommSeEJB", "rcvInterface", inRecord); //2열연 신규모듈
                	} else {
                		ydEjbCon.trx("CarMvHdSeEJB", "procMatlCarLev", inRecord);
                	}
                } 
                else if ("DJY21".equals(szSPOS_WLOC_CD)    // 소재ABCDE
              		     || "DJY22".equals(szSPOS_WLOC_CD)    // 소재FGH
            	         || "DJY1E".equals(szSPOS_WLOC_CD)    // 제품
            	) 
                {
            		//2열연 출발 -> 2열연 아닌 타 일관제철야드로 갈 경우 차량스케줄 생성처리를 위해 기존모듈 착지처리 하도록 호출.
                	//발지처리
            		ydEjbCon.trx("CCommSeEJB", "rcvInterface", inRecord); //2열연 신규모듈
            		
            		//착지처리
            		inRecord.setField("SPOS_WLOC_CD", "DMY1P");
            		ydEjbCon.trx("CarMvHdSeEJB", "procMatlCarLev", inRecord);
                }
                else if ("DWY22".equals(szARR_WLOC_CD)/*2후판*/ 
                		|| "DKY21".equals(szARR_WLOC_CD)/*1후판 */
                		|| "DWY22".equals(szSPOS_WLOC_CD)/*2후판*/ 
                        || "DKY21".equals(szSPOS_WLOC_CD))/*1후판 */   		
	            { 
			       		/**********************************
			           	 * 후판 슬라브 신규모듈 적용여부 
			           	 **********************************/
			           	//YdPlateCommDAO ydCommDao = new YdPlateCommDAO();
			           	JDTORecord jrPSlabAppYn = ydCommDao.getPSlabAppYn();
			           	
			           	String pSlabAppYn = StringHelper.evl(jrPSlabAppYn.getFieldString("PSLAB_EFF_YN"), "N");; //2열연 코일야드 적용여부
			       		
			       		ydUtils.putLog(szSessionName, szMethodName,"후판슬라브야드 신규모듈 적용여부 : " + pSlabAppYn, YdConstant.DEBUG);
			       		
			       		if ("Y".equals(pSlabAppYn)) {
			       			ydEjbCon.trx("PSlabYdCommEJB", "rcvInterface", inRecord);	// 후판Slab -신규모듈-
			       		} else {
			       			ydEjbCon.trx("CarMvHdSeEJB", "procMatlCarLev", inRecord);	// 후판Slab -구모듈-
			       		}
			  	}
                else 
                {
            		ydEjbCon.trx("CarMvHdSeEJB", "procMatlCarLev", inRecord);	//야드모듈
            	}
                
            }
            cSlabYdDisplayTc(inRecord);
            	
            
        } catch (Exception e) {
            szMsg =szMethodName + "() " +e.getMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

            throw new DAOException(szMsg);

        } // end of try catch
        szMsg="소재차량출발 처리("+szMethodName+") 완료";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
    } // end of rcvMatlCarLev()

	
	/**
	 * 오퍼레이션명 : 외판슬라브출하차량도착실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws DAOException
	 */ 
	public void rcvOutplSlabDistCarArrWr(JDTORecord inRecord) throws DAOException  {
	// 
	// YD-UC-???? 외판슬라브출하차량도착실적
	// TC : DMYDR035
	// 출하관리시스템으로부터 외판슬라브출하차량도착실적 수신
	//
	//┏━┓
	//┃ 
	//┗━┛
		String szMsg = "";
		String szMethodName = "rcvOutplSlabDistCarArrWr";
		String szOperationName = "외판슬라브출하차량도착실적";

		if(!ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			szMsg = szMethodName + "() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		ydUtils.displayRecord(szOperationName, inRecord);
		
		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");			
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				// AB열연 수신
				ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
			}else{
				// 일관제철 수신
				ydEjbCon.trx("CarMvHdSeEJB", "procOutplSlabDistCarArrWr", inRecord);
			}
			
		} catch (Exception e) {			
			szMsg = szMethodName + "() " + e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch
		
		szMsg = "외판슬라브출하차량도착실적 처리(" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	 } // end of rcvOutplSlabDistCarArrWr()
	
	

	
	/**
	 * 오퍼레이션명 : 코일제품출하차량도착실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws DAOException
	 */ 
	public void rcvCoilGdsDistCarArrWr(JDTORecord inRecord) throws DAOException  {
	// 
	// YD-UC-???? 코일제품출하차량도착실적
	// TC : DMYDR036
	// 출하관리시스템으로부터 코일제품출하차량도착실적 수신
	//
	//┏━┓
	//┃
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCoilGdsDistCarArrWr";
		String szOperationName = "코일제품출하차량도착실적";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		ydUtils.displayRecord(szOperationName, inRecord);

		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");			
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				// AB열연 수신
				ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
			}else{
				// 일관제철 수신
				ydEjbCon.trx("CarMvHdSeEJB", "procCoilGdsDistCarArrWr", inRecord);
			}
		} catch (Exception e) {			
			szMsg = szMethodName + "() " + e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch

		
		szMsg="코일제품출하차량도착실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvCoilGdsDistCarArrWr()
	
	

	
	/**
	 * 오퍼레이션명 : 코일임가공차량도착실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws DAOException
	 */ 
	public void rcvCoilRentprocCarArrWr(JDTORecord inRecord) throws DAOException  {
	// 
	// YD-UC-???? 코일임가공차량도착실적
	// TC : DMYDR037
	// 출하관리시스템으로부터 코일임가공차량도착실적 수신
	//
	//┏━┓
	//┃  
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCoilRentprocCarArrWr";
		String szOperationName = "코일임가공차량도착실적";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		
		ydUtils.displayRecord(szOperationName, inRecord);
		
		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");			
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				// AB열연 수신
				ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
			}else{
				// 일관제철 수신
				ydEjbCon.trx("CarMvHdSeEJB", "procCoilRentprocCarArrWr", inRecord);
			}
		} catch (Exception e) {			
			szMsg = szMethodName + "() " + e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch

		
		szMsg="코일임가공차량도착실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvCoilRentprocCarArrWr()
	
	

	
	/**
	 * 오퍼레이션명 : 후판제품출하차량도착실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws DAOException
	 */ 
	public void rcvPlGdsDistCarArrWr(JDTORecord inRecord) throws DAOException  {
	// 
	// YD-UC-???? 후판제품출하차량도착실적
	// TC : DMYDR038
	// 출하관리시스템으로부터 후판제품출하차량도착실적 수신
	//
	//┏━┓
	//┃ 
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvPlGdsDistCarArrWr";
		String szOperationName = "후판제품출하차량도착실적";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");			
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				// AB열연 수신
				ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
			}else{
				// 일관제철 수신
				ydEjbCon.trx("CarMvHdSeEJB", "procPlGdsDistCarArrWr", inRecord);
			}
		} catch (Exception e) {			
			szMsg = szMethodName + "() " + e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch
		
		szMsg="후판제품출하차량도착실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvPlGdsDistCarArrWr()
	
	
	/**
	 * 오퍼레이션명 : 후판제품입고차량도착실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws DAOException
	 */ 
	public void rcvPlGdsRcptCarArrWr(JDTORecord inRecord) throws DAOException  {
	// 
	// YD-UC-???? 후판제품출하차량도착실적
	// TC : DMYDR038
	// 출하관리시스템으로부터 후판제품출하차량도착실적 수신
	//
	//┏━┓
	//┃ 
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvPlGdsRcptCarArrWr";
		String szOperationName = "후판제품입고차량도착실적";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");			
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				// AB열연 수신
				ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
			}else{
				
				//2020.1.6 신규모듈관련 분기 추가
				//후판제품 신규모듈 적용여부 조회
                if(PlateGdsYdUtil.isPlateNewMoudleApply("DM")){
                	// procPlGdsRcptCarArrWr4G
                	ydEjbCon.trx("PlateYdRcvFaEJB", "rcvInterface", inRecord);
                }
                else{
    				// 일관제철 수신
    				ydEjbCon.trx("CarMvHdSeEJB", "procPlGdsRcptCarArrWr", inRecord);
                }
			}
		} catch (Exception e) {			
			szMsg = szMethodName + "() " + e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch
		
		szMsg="후판제품입고차량도착실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvPlGdsRcptCarArrWr()
	

	
	/**
	 * 오퍼레이션명 : 외판슬라브출하차량출발실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws DAOException
	 */ 
	public void rcvOutplSlabDistCarLevWr(JDTORecord inRecord) throws DAOException  {
	// 
	// YD-UC-???? 외판슬라브출하차량출발실적
	// TC : DMYDR039
	// 출하관리시스템으로부터 외판슬라브출하차량출발실적 수신
	//
	//┏━┓
	//┃ 
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvOutplSlabDistCarLevWr";
		String szOperationName = "외판슬라브출하차량출발실적";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}

		ydUtils.displayRecord(szOperationName, inRecord);
		
		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");			
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				// AB열연 수신
				/***************************************************************
				 * B열연 신규모듈 적용 여부 
				 **************************************************************/
				String sBSLAB_EFF_YN = "N";
				String sBCOIL_EFF_YN = "N";

				YdPlateCommDAO commDao = new YdPlateCommDAO();
				JDTORecord jrResult = commDao.getNewModuleEffYn();
				
				sBSLAB_EFF_YN = StringHelper.evl(jrResult.getFieldString("BSLAB_EFF_YN"),"N");
				sBCOIL_EFF_YN = StringHelper.evl(jrResult.getFieldString("BCOIL_EFF_YN"),"N");
				
				szMsg = "YdPlateCommDAO.getNewModuleEffYn()---[[[ B열연SLAB야드신규적용:" + sBSLAB_EFF_YN + " ,B열연COIL야드신규적용:" + sBCOIL_EFF_YN + " ]]]---"; 
				ydUtils.putLog(szSessionName, szMethodName,szMsg, YdConstant.DEBUG);
				
				if(sYdGp.equals("2")&&sBSLAB_EFF_YN.equals("Y")) {
					//B열연  신규모듈 적용
					ydEjbCon.trx("YmCommEJB", "rcvInterface", inRecord);
				} else  {
					//기존모듈 호출
					ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
				}
			}else{
				// 일관제철 수신
				ydEjbCon.trx("CarMvHdSeEJB", "procOutplSlabDistCarLevWr", inRecord);
			}
		
		} catch (Exception e) {	
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch

		
		szMsg="외판슬라브출하차량출발실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvOutplSlabDistCarLevWr()
	
	

	
	/**
	 * 오퍼레이션명 : 코일제품출하차량출발실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws DAOException
	 */ 
	public void rcvCoilGdsdistCarLevWr(JDTORecord inRecord) throws DAOException  {
	// 
	// YD-UC-???? 코일제품출하차량출발실적
	// TC : DMYDR040
	// 출하관리시스템으로부터 코일제품출하차량출발실적 수신
	//
	//┏━┓
	//┃  
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCoilGdsdistCarLevWr";
		String szOperationName = "코일제품출하차량출발실적";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}

		ydUtils.displayRecord(szOperationName, inRecord);

		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");			
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				// AB열연 수신
				//ydEjbCon.trx("CarMvHdSeEJB", "procCoilGdsdistCarLevWrAB", inRecord);
				ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
			}else{
				// 코일제품출하차량출발실적
				ydEjbCon.trx("CarMvHdSeEJB", "procCoilGdsdistCarLevWr", inRecord);
			}
		} catch (Exception e) {	
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch
		
		szMsg="코일제품출하차량출발실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvCoilGdsdistCarLevWr()
	
	

	
	/**
	 * 오퍼레이션명 : 코일임가공차량출발실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws DAOException
	 */ 
	public void rcvCoilRenrprocCarLevWr(JDTORecord inRecord) throws DAOException  {
	// 
	// YD-UC-???? 코일임가공차량출발실적
	// TC : DMYDR041
	// 출하관리시스템으로부터 코일임가공차량출발실적 수신
	//
	//┏━┓
	//┃ 
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCoilRenrprocCarLevWr";
		String szOperationName = "코일임가공차량출발실적";
		
		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		ydUtils.displayRecord(szOperationName, inRecord);
		
		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");			
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				// AB열연 수신
				ydEjbCon.trx("CarMvHdSeEJB", "procCoilRenrprocCarLevWrAB", inRecord);
			}else{
				// 일관제철 수신
				ydEjbCon.trx("CarMvHdSeEJB", "procCoilRenrprocCarLevWr", inRecord);
			}
		} catch (Exception e) {	
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch

		
		szMsg="코일임가공차량출발실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvCoilRenrprocCarLevWr()
	
	

	
	/**
	 * 오퍼레이션명 : 후판제품출하차량출발실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws DAOException
	 */ 
	public void rcvPlGdsDistCarLevWr(JDTORecord inRecord) throws DAOException  {
	// 
	// YD-UC-???? 후판제품출하차량출발실적
	// TC : DMYDR042
	// 출하관리시스템으로부터 후판제품출하차량출발실적 수신
	//
	//┏━┓
	//┃  
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvPlGdsDistCarLevWr";
		String szOperationName = "후판제품출하차량출발실적";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}

		ydUtils.displayRecord(szOperationName, inRecord);
		
		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");			
			if(sYdGp.equals("0")||sYdGp.equals("1")||sYdGp.equals("2")||sYdGp.equals("3") ) {
				// AB열연 수신
				ydEjbCon.trx("JNDIInternal", "receiveInternal", inRecord);
			}else{
				// 일관제철 수신
				ydEjbCon.trx("CarMvHdSeEJB", "procPlGdsDistCarLevWr", inRecord);
			}
		} catch (Exception e) {	
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch

		
		szMsg="후판제품출하차량출발실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvPlGdsDistCarLevWr()
	
	/**
	 * 오퍼레이션명 : 후판제품연안해송도착실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws DAOException
	 */ 
	public void rcvPlGdsDistShipArrWr(JDTORecord inRecord) throws DAOException  {
	// 
	// YD-UC-???? 후판제품연안해송도착실적
	// TC : DMYDR043
	// 출하관리시스템으로부터 후판제품연안해송도착실적 수신
	//
	//┏━┓
	//┃  
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvPlGdsDistShipArrWr";
		String szOperationName = "후판제품연안해송도착실적";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}

		ydUtils.displayRecord(szOperationName, inRecord);
		
		try {
			ydEjbCon.trx("CarMvHdSeEJB", "procPlGdsDistShipArrWr", inRecord);
		} catch (Exception e) {	
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch

		
		szMsg="후판제품출하차량출발실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvPlGdsDistShipArrWr()
	
	/**
	 * 오퍼레이션명 : 차량입동지시요구
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @throws DAOException
	 */
	public void rcvCarBayInOrdReq(JDTORecord inRecord)throws DAOException  {
		// 
		// YD-UC-???? 차량입동지시요구
		// TC : YDYDJ633
		// 차량입동지시요구 수신
		//
		//┏━┓
		//┃  
		//┗━┛
			
		String szMsg="";
		String szMethodName="rcvCarBayInOrdReq";
		String szOperationName = "차량입동지시요구";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= "["+szOperationName+"](" + szMethodName+") 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}

		ydUtils.displayRecord(szOperationName, inRecord);
		
		try {
			ydEjbCon.trx("CarMvHdSeEJB", "procCarBayInOrdReq", inRecord);
		} catch (Exception e) {	
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch

		
		szMsg="["+szOperationName+"]("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
	}

	/**
	 * 오퍼레이션명 : 코일HYSCO출하차량도착실적
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws DAOException
	 */ 
	public void rcvCoilHYSCOprocCarArrWr(JDTORecord inRecord) throws DAOException  {
	// 
	// YD-UC-???? 코일HYSCO출하차량도착실적
	// TC : DMYDR036
	// 출하관리시스템으로부터 코일HYSCO출하차량도착실적 수신
	//
	//┏━┓
	//┃
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCoilHYSCOprocCarArrWr";
		String szOperationName = "코일HYSCO출하차량도착실적";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
		
		ydUtils.displayRecord(szOperationName, inRecord);

		try {
			String sYdGp = StringHelper.evl(inRecord.getFieldString("YD_GP"), "");			
				// 일관제철 수신
			ydEjbCon.trx("CarMvHdSeEJB", "procCoilHYSCOprocCarArrWr", inRecord);

		} catch (Exception e) {			
			szMsg = szMethodName + "() " + e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch

		
		szMsg="코일HYSCO출하차량도착실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvCoilHYSCOprocCarArrWr()
	
	
	/**
	 * 오퍼레이션명 : 코일제품연안해송도착실적(DMYDR045)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws DAOException
	 */ 
	public void rcvCoilGdsDistShipArrWr(JDTORecord inRecord) throws DAOException  {
	// 
	// YD-UC-???? 코일제품연안해송도착실적
	// TC : DMYDR045
	// 출하관리시스템으로부터 코일제품연안해송도착실적 수신
	//
	//┏━┓
	//┃  
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCoilGdsDistShipArrWr";
		String szOperationName = "코일제품연안해송도착실적";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}

		ydUtils.displayRecord(szOperationName, inRecord);
		
		try {
			ydEjbCon.trx("CarMvHdSeEJB", "procCoilGdsDistShipArrWr", inRecord);
		} catch (Exception e) {	
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch

		
		szMsg="코일제품연안해송도착실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvCoilGdsDistShipArrWr()
	
	
	
	/**
	 * 오퍼레이션명 : 스크랩 하차완료 수신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws DAOException
	 */ 
	public void rcvCarArrive(JDTORecord inRecord) throws DAOException  {
	// 
 	// TC : TSYDJ015
	// 구내운송시스템으로부터 스크랩 하차완료 수신
	//
	//┏━┓
	//┃
	//┗━┛
		
		String szMsg="";
		String szMethodName="rcvCarArrive";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	 
		try { 
			ydEjbCon.trx("CarMvHdSeEJB", "procCarArrive", inRecord); 
			
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new DAOException(szMsg);

		} // end of try catch
		 
		szMsg="스크랩 하차완료처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvCarArrive()
	
	/**
	 * 오퍼레이션명 : 연주슬라브야드 전광판 전문 송신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return: 
	 * @throws DAOException
	 */ 
	public void cSlabYdDisplayTc(JDTORecord inRecord) throws DAOException  {

		
		String szMsg="";
		String szMethodName="cSlabYdDisplayTc";


	 
		try {
			//수신 전문에서 ARR_WLOC_CD 를 추출
			String szJMS_TC_CD  			= ydDaoUtils.paraRecChkNull(inRecord, "JMS_TC_CD");
			String szSPOS_WLOC_CD 			= ydDaoUtils.paraRecChkNull(inRecord, "SPOS_WLOC_CD");
            String szARR_WLOC_CD 			= ydDaoUtils.paraRecChkNull(inRecord, "ARR_WLOC_CD");
            String szTRN_WRK_FULLVOID_GP	= ydDaoUtils.paraRecChkNull(inRecord, "TRN_WRK_FULLVOID_GP");
            String szSPOS_YD_PNT_CD         = ydDaoUtils.paraRecChkNull(inRecord, "SPOS_YD_PNT_CD");
            String szARR_YD_PNT_CD          = ydDaoUtils.paraRecChkNull(inRecord, "ARR_YD_PNT_CD");
         
            JDTORecord  jrParam         = JDTORecordFactory.getInstance().create();
        	JDTORecord jrRtn = null;
            
			szMsg = "[JSP Facade] 소재차량출발실적 수신 연주슬라브야드 전광판 송출대상 검사" ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
            szMsg = "[JSP Facade] 전문코드 ["+szJMS_TC_CD+"] 발지개소코드 ["+szSPOS_WLOC_CD+"] 착지개소코드 ["+szARR_WLOC_CD+"] ";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
            szMsg = "[JSP Facade] 운송작업영공구분 ["+szTRN_WRK_FULLVOID_GP+"] 발지 포인트코드 ["+szSPOS_YD_PNT_CD+"] 착지 포인트코드 ["+szARR_YD_PNT_CD+"]" ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
            
            if("DHY21".equals(szSPOS_WLOC_CD) && "TSYDJ004".equals(szJMS_TC_CD) && szSPOS_YD_PNT_CD.length() >=4 ){
            	String ydPnt            = szSPOS_YD_PNT_CD.substring(2);
    			if("04".equals(ydPnt))  jrRtn = slabUtils.addSndData(jrRtn,slabYdCommDao.getMsgL2("YDT1L001", jrParam));
    			else if  ("06".equals(ydPnt))  jrRtn = slabUtils.addSndData(jrRtn,slabYdCommDao.getMsgL2("YDT2L001", jrParam));
            	
            }
            else if ("DHY21".equals(szARR_WLOC_CD)){
            	if("TSYDJ003".equals(szJMS_TC_CD) && szARR_YD_PNT_CD.length() >=4){
            		String ydPnt            = szARR_YD_PNT_CD.substring(2);
        			if("04".equals(ydPnt))  jrRtn = slabUtils.addSndData(jrRtn,slabYdCommDao.getMsgL2("YDT1L001", jrParam));
        			else if  ("06".equals(ydPnt))  jrRtn = slabUtils.addSndData(jrRtn,slabYdCommDao.getMsgL2("YDT2L001", jrParam));
            		
            	}
            	else if("TSYDJ004".equals(szJMS_TC_CD) || "TSYDJ005".equals(szJMS_TC_CD)){
            		String szTrnEqpCd			= ydDaoUtils.paraRecChkNull(inRecord, "TRN_EQP_CD");
                	String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
                	
                	JDTORecordSet jrResultSet = JDTORecordFactory.getInstance().createRecordSet("YD");
                	JDTORecord jrResult =  JDTORecordFactory.getInstance().create();
                	
                	szMsg = "[JSP Facade] 운송장비코드로 차량스케줄 검사" ;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                	
                	jrParam.setField("TRN_EQP_CD", szTrnEqpCd);
        			jrResultSet = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschDaoTrnEqpCd", logId, szMethodName, "차량스케줄 정보 select");
        			
        			if(jrResultSet != null && jrResultSet.size() >0) {
        				//차량스케줄 정보 SET
        				jrResultSet.absolute(1);
        	        	jrResult = jrResultSet.getRecord();
        	        	
        	        	String ydPntCd1	  		= jrResult.getFieldString("YD_PNT_CD1");
        	        	String ydCarLdStopLoc	= jrResult.getFieldString("YD_CARLD_STOP_LOC");
        	        	String ydPntCd3	  		= jrResult.getFieldString("YD_PNT_CD3");
        	        	String ydCarUdStopLoc	= jrResult.getFieldString("YD_CARUD_STOP_LOC");
        	        	String ydCarProgStat    = jrResult.getFieldString("YD_CAR_PROG_STAT");
        	        	String ydPnt            = "";
        	        	
        	        	szMsg = "[JSP Facade] 운송장비코드 ["+szTrnEqpCd+"] 스케줄 검사 결과 상차포인트코드 ["+ydPntCd1+"] 상차정지위치 ["+ydCarLdStopLoc+"]" +
        	        			" 하차포인트코드 ["+ydPntCd3+"] 하차정지위치 ["+ydCarUdStopLoc+"] 차량상태 ["+ydCarProgStat+"]" ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        	        	
                        //차량상태가 상차출발일땐 상차포인트코드
                        if(YdConstant.YD_CARLD_LEV.equals(ydCarProgStat) && ydPntCd1.length()>=4) ydPnt = ydPntCd1.substring(2);
                        else if (YdConstant.YD_CARUD_LEV.equals(ydCarProgStat) && ydPntCd3.length()>=4) ydPnt = ydPntCd3.substring(2);

                        szMsg = "[JSP Facade] 포인트 검사 결과 +["+ydPnt+"]" ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        
                        if("04".equals(ydPnt)) jrRtn = slabUtils.addSndData(jrRtn,slabYdCommDao.getMsgL2("YDT1L001", jrParam));
                        else if ("06".equals(ydPnt)) jrRtn = slabUtils.addSndData(jrRtn,slabYdCommDao.getMsgL2("YDT2L001", jrParam));
        	        	
        			}
            	}
            }
            else{
              	 szMsg = "[JSP Facade] 전광판 송출 대상이 아님" ;
                   ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            }
            
            if (jrRtn != null) {
				//jrRst.setResultCode(logId);
        		jrRtn.setResultMsg(szMethodName);

				EJBConnector sndConn = new EJBConnector("default", "YdCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
            
            
            //2022.01.12 연주슬라브야드 전광판 전문송신 기능 추가 연주상차 or 연주 하차
            /*
            if("DHY21".equals(szSPOS_WLOC_CD) || "DHY21".equals(szARR_WLOC_CD) ){
            	JDTORecord  jrParam         = JDTORecordFactory.getInstance().create();
            	JDTORecord jrRtn = null;
            	
            	if("TSYDJ004".equals(szJMS_TC_CD)){
            		if("DHY21".equals(szSPOS_WLOC_CD) && szSPOS_YD_PNT_CD.length() >=4 ) {
            			if(szSPOS_YD_PNT_CD.substring(2) =="04")  jrRtn = slabUtils.addSndData(jrRtn,slabYdCommDao.getMsgL2("YDT1L001", jrParam));
            			else if  (szSPOS_YD_PNT_CD.substring(2) =="06")  jrRtn = slabUtils.addSndData(jrRtn,slabYdCommDao.getMsgL2("YDT2L001", jrParam));
            		}
            		else if ("DHY21".equals(szARR_YD_PNT_CD)){
            			szMsg = "[JSP Facade] 운송장비코드로 차량스케줄 검사" ;
                        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                        
                        
            		}
            		
            	}

            	
            	String szTrnEqpCd			= ydDaoUtils.paraRecChkNull(inRecord, "TRN_EQP_CD");
            	String logId = "<P" + DateHelper.format(new java.util.Date(System.currentTimeMillis()), "HHmmssSSS") + ">";
            	
            	JDTORecord  jrParam         = JDTORecordFactory.getInstance().create();
            	JDTORecordSet jrResultSet = JDTORecordFactory.getInstance().createRecordSet("YD");
            	JDTORecord jrResult =  JDTORecordFactory.getInstance().create();
            	
            	szMsg = "[JSP Facade] 운송장비코드로 차량스케줄 검사" ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            	
            	jrParam.setField("TRN_EQP_CD", szTrnEqpCd);
    			jrResultSet = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschDaoTrnEqpCd", logId, szMethodName, "차량스케줄 정보 select");
            	
    			if(jrResultSet != null && jrResultSet.size() >0) {
    				//차량스케줄 정보 SET
    				jrResultSet.absolute(1);
    	        	jrResult = jrResultSet.getRecord();
    	        	
    	        	String ydPntCd1	  		= jrResult.getFieldString("YD_PNT_CD1");
    	        	String ydCarLdStopLoc	= jrResult.getFieldString("YD_CARLD_STOP_LOC");
    	        	String ydPntCd3	  		= jrResult.getFieldString("YD_PNT_CD3");
    	        	String ydCarUdStopLoc	= jrResult.getFieldString("YD_CARUD_STOP_LOC");
    	        	String ydCarProgStat    = jrResult.getFieldString("YD_CAR_PROG_STAT");
    	        	String ydPnt            = "";
    	        	JDTORecord jrRtn = null;
    	        	
    	        	szMsg = "[JSP Facade] 운송장비코드 ["+szTrnEqpCd+"] 스케줄 검사 결과 상차포인트코드 ["+ydPntCd1+"] 상차정지위치 ["+ydCarLdStopLoc+"]" +
    	        			" 하차포인트코드 ["+ydPntCd3+"] 하차정지위치 ["+ydCarUdStopLoc+"] 차량상태 ["+ydCarProgStat+"]" ;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    	        	
                    //차량상태가 상차출발일땐 상차포인트코드
                    if(YdConstant.YD_CARLD_LEV.equals(ydCarProgStat) && ydPntCd1.length()>=4) ydPnt = ydPntCd1.substring(2);
                    else if (YdConstant.YD_CARLD_ARR.equals(ydCarProgStat) && ydCarLdStopLoc.length()>=6) ydPnt = ydCarLdStopLoc.substring(4);
                    else if (YdConstant.YD_CARUD_LEV.equals(ydCarProgStat) && ydPntCd3.length()>=4) ydPnt = ydPntCd3.substring(2);
                    else if (YdConstant.YD_CARUD_ARR.equals(ydCarProgStat) && ydCarUdStopLoc.length()>=6) ydPnt = ydCarUdStopLoc.substring(4);
                    
                    szMsg = "[JSP Facade] 포인트 검사 결과 +["+ydPnt+"]" ;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                    
                    if("04".equals(ydPnt)) jrRtn = slabUtils.addSndData(jrRtn,slabYdCommDao.getMsgL2("YDT1L001", jrParam));
                    else if ("06".equals(ydPnt)) jrRtn = slabUtils.addSndData(jrRtn,slabYdCommDao.getMsgL2("YDT2L001", jrParam));

    	        	if (jrRtn != null) {
    					//jrRst.setResultCode(logId);
    	        		jrRtn.setResultMsg(szMethodName);

    					EJBConnector sndConn = new EJBConnector("default", "YdCommEJB", this);
    					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
    				}
    	        	
    			}
            	
            	
            }
            else{
           	 szMsg = "[JSP Facade] 전광판 송출 대상이 아님" ;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
           }
           */
           
			
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new DAOException(szMsg);

		} // end of try catch
		 
		szMsg="전광판전문 송신 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of rcvCarArrive()
	
	
	//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
	//                                                
	//                     일관제철소정보관리시스템-야드관리
	//              작업실행관리-차량이동처리 Facade Session Bean
	//                          2008.09.30 YHWHman
	//                                                      
	//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

	
  //---------------------------------------------------------------------------
} // end of class

