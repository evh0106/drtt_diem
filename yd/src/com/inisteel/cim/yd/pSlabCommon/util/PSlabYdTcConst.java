package com.inisteel.cim.yd.pSlabCommon.util;

import java.util.HashMap;

import com.inisteel.cim.yd.common.util.tcconst.YdRcvTcDefMap;

public class PSlabYdTcConst {
	
	
	// Session Name
	private String szSessionName = getClass().getName();
	private YdRcvTcDefMap ydRcvTcDefMap=new YdRcvTcDefMap();

	// TC Code Map
	public HashMap regTcMap =new HashMap();

	// 송수신 TCCode vs Method Map
	public HashMap rcvTcFaMap = new HashMap();
	public HashMap rcvTcOpMap = new HashMap();
	public HashMap rcvTcDescMap =new HashMap();
	public HashMap rcvTcYdMap 	=new HashMap();
	
	
	// Facade CAll을 위한 TCCode
	public final String FACADE_TCCODE ="FACADEPT";	
	
	
	
	public PSlabYdTcConst(){
		
		// TC Code Table Init
		tcRcvMethodInit();

	} // end of YdTcConst()

	
	
	
	
	/**
	 * TcCode에 대한 MethodName 정합성 Check
	 * @param String TC Code, String MethodName
	 * @return : true, false
	 */
	public boolean chkTcMethod(String szTcCode, String szChkMethodName){
		
		String szMethodName="chkTcMethod";
		String szMapMethodName="";
		boolean bRtc=false;
		
		if( (szTcCode==null) || (szChkMethodName==null) ){
			return false;
		}
		
		szTcCode =szTcCode.trim();
		szTcCode=szTcCode.toUpperCase();
		
		szChkMethodName =szChkMethodName.trim();
		

		int nMapCnt =rcvTcOpMap.size();
		if( nMapCnt <=0)
			// Map is empty error
			return false;
		
		szMapMethodName =(String)rcvTcOpMap.get(szTcCode);
		if( szChkMethodName.equals(szMapMethodName))
				bRtc =true;
		
		return bRtc;
		
	} // end of chkTcMethod()
	
	
	

	
	/**
	 * TC코드로 내부, 외부, Facade 송신을 구분해서 리턴한다
	 * @param  	inTcCode
	 * @return  1:내부JMS, 2:리모트 EAI, 3:L2 EAI, 9:Facade, 
	 *          0:Unknown, -1:Error
	 */
	public int chkTcType(String szTcCode)
	{
		String szMethodName = "chkTcType";
		
		
		String szChkID="";

	
		if( szTcCode==null || szTcCode.equals(""))	//	TC Code is null
			return -1;
		
		szTcCode.trim().toUpperCase();

		//
		// Facade 송신  Call Check
		//
		if( szTcCode.equals(FACADE_TCCODE))
			return 3;
		
		
		// Get Check ID
		szChkID=szTcCode.substring(4,5);

				
		//
		// TC Type Check
		//
		if("J".equals(szChkID) )		// 내부 JMS MSG
			return 1;
		else if("R".equals(szChkID)){	// Remote EAI MSG
			//출하http ->jms
		    if(szTcCode.substring(2,4).equals("DM")){
		    	return 1;
		    }else{
		    	return 2;
		    }
		}
		else if("L".equals(szChkID))	// L2 System EAI MSG
			return 3;
		else							// Unknown MSG
			return 0;
		
		
	} 
	
	
	
	
	
	/**
	 * TC Code, Method Table Define
	 * @param
	 * @return
	 */																																																																																																																																			
	public void tcRcvMethodInit(){
		//
		// TC Code, Method 등록
		//

		String szMsg="";
		String szMethodName="tcRcvMethodInit";
		
				
		String szTcCode="";
		String szFaName="";
		String szOpName="";
		String szDesc="";

		int nTcCnt=ydRcvTcDefMap.strTcMap.length;
		//System.out.println("TC Cnt="+nTcCnt);
		
		
		try{
			
			for(int i=0;i<nTcCnt;i++){
				
				// TC Map Reg
				szTcCode=ydRcvTcDefMap.strTcMap[i][0].trim().toUpperCase();
				regTcMap.put(""+i, szTcCode);
//				System.out.println("등록 TC Code=["+szTcCode+"]");
				
				
				// FaName Map Reg
				rcvTcFaMap.put(szTcCode, ydRcvTcDefMap.strTcMap[i][1]);
//				System.out.println("등록 FaName=["+ydRcvTcDefMap.strTcMap[i][1]+"]");
				
				
				// OpName Map Reg
				rcvTcOpMap.put(szTcCode, ydRcvTcDefMap.strTcMap[i][2]);
//				System.out.println("등록 OpName=["+ydRcvTcDefMap.strTcMap[i][2]+"]");
				
				
				// Desc Map Reg
				rcvTcDescMap.put(szTcCode, ydRcvTcDefMap.strTcMap[i][3]);
//				System.out.println("등록 Desc=["+ydRcvTcDefMap.strTcMap[i][3]+"]\n");
				
				// yd Map Reg
				if(szTcCode.substring(0,4).equals("YDYD")){
					rcvTcYdMap.put(szTcCode, ydRcvTcDefMap.strTcMap[i][4]);
				}

			} // end of for(i)
			
			
			

		}catch (Exception e){
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			//ydUtils.putLog(szSessionName, szMethodName, szMsg, PSlabYdConstant.ERROR);
			
			return;
		}
		
		
		
	}
	
	
	
	
	public static void main(String[] args){
		PSlabYdTcConst im =new PSlabYdTcConst();
		
		im.tcRcvMethodInit();
		
	} 
	
  //---------------------------------------------------------------------------
}
