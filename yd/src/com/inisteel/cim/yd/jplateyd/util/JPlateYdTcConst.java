/*
 * @(#) 2후판정정야드 TC관련 상수정의
 *
 * @version			V1.00
 * @author			김현우
 * @date			2013.04.03
 *
 * @description		2후판정정야드 TC관련 상수정의
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.01  2013/04/03   김현우      김현우       신규작성 (기존 YdTcConst 복사해서 사용)
 */

package com.inisteel.cim.yd.jplateyd.util;

import java.util.HashMap;

import com.inisteel.cim.yd.common.util.tcconst.YdRcvTcDefMap;

public class JPlateYdTcConst {

	// Session Name
	private static final String szSessionName = JPlateYdTcConst.class.getName();

	private YdRcvTcDefMap ydRcvTcDefMap = new YdRcvTcDefMap();

	// TC Code Map
	public HashMap regTcMap 	= new HashMap();

	// 송수신 TCCode vs Method Map
	public HashMap rcvTcFaMap 	= new HashMap();
	public HashMap rcvTcOpMap 	= new HashMap();
	public HashMap rcvTcDescMap = new HashMap();
	public HashMap rcvTcYdMap 	= new HashMap();

	// Facade CAll을 위한 TCCode
	public final String FACADE_TCCODE ="FACADEPT";

	private JPlateYdUtils ydUtils 	= new JPlateYdUtils();

	public JPlateYdTcConst(){

		// TC Code Table Init
		tcRcvMethodInit();

	} // end of YdTcConst()

	/**
	 * TcCode에 대한 MethodName 정합성 Check
	 * @param String TC Code, String MethodName
	 * @return : true, false
	 */
	public boolean chkTcMethod(String szTcCode, String szChkMethodName){

//		String szMethodName="chkTcMethod";
		String szMapMethodName="";
		boolean bRtc=false;

		if (szTcCode==null || szChkMethodName==null) {
			return false;
		}

		szTcCode = szTcCode.trim();
		szTcCode = szTcCode.toUpperCase();

		szChkMethodName = szChkMethodName.trim();

		int nMapCnt = rcvTcOpMap.size();
		if (nMapCnt <= 0) {
			// Map is empty error
			return false;
		}

		szMapMethodName = (String)rcvTcOpMap.get(szTcCode);
		if (szChkMethodName.equals(szMapMethodName)) {
			bRtc = true;
		}

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
//		String szMethodName = "chkTcType";
		String szChkID = "";

		if( szTcCode==null || "".equals(szTcCode)) {	//	TC Code is null
			return -1;
		}

		szTcCode.trim().toUpperCase();

		//
		// Facade 송신  Call Check
		//
		if( szTcCode.equals(FACADE_TCCODE)) {
			return 3;
		}

		// Get Check ID
		szChkID = szTcCode.substring(4,5);
		
		if(szTcCode.substring(0,3).equals("M10")){
			return 1;
		}

		//
		// TC Type Check
		//
		if ("J".equals(szChkID)) {		// 내부 JMS MSG
			return 1;
		} else if("R".equals(szChkID)){	// Remote EAI MSG
			//출하http ->jms
		    if(szTcCode.substring(2,4).equals("DM")){
		    	return 1;
		    }else{
		    	return 2;
		    }
		} else if("L".equals(szChkID)) {	// L2 System EAI MSG
			return 3;
		} else {							// Unknown MSG
			return 0;
		}

	} // end of chkTcType()

	/**
	 * TC Code, Method Table Define
	 * @param
	 * @return
	 */
	public void tcRcvMethodInit(){
		//
		// TC Code, Method 등록
		//

		String szMsg = "";
		String szMethodName = "tcRcvMethodInit";

		String szTcCode = "";

		int nTcCnt = ydRcvTcDefMap.strTcMap.length;

		try {

			for(int i=0; i<nTcCnt; i++) {

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


			//
			// Debug Msg
			//
			//szMsg="[DEBUG] Tc mapping completed (TC Count="+nTestIndex+")";
			//ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//System.out.println(szMsg);

			//
			// Debug Msg
			// 현재 위치 체크
			//
			// System.out.println(System.getProperty("user.dir"));

		}catch (Exception e){
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);

			return;
		}

	} // end of tcRcvMethodInit();


	public static void main(String[] args){
		JPlateYdTcConst im = new JPlateYdTcConst();

		im.tcRcvMethodInit();

	} // end of testMain()

  //---------------------------------------------------------------------------
} // end of class YdTcConst
