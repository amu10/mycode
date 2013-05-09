package com.dinfo.util;

import java.util.HashMap;
import java.util.Map;

public class TestTool {
	
	public static void main(String[] argv){
		Map<String, String> code = new HashMap<String,String>();
		code.put("dd", "dd");
		ToolUtil.writeJava(code);
		
	}

}
