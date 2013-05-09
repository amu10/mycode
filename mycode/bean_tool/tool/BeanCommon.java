package com.dinfo.tool;

import java.util.HashMap;
import java.util.Map;

public class BeanCommon {

	public static  Map<String, String> createJavaCode(String javaName,
			Map<String, String> map) {
		Map<String, String> code = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		sb.append("package com.dinfo.tool.bean;\n\n");
		sb.append("public class " + javaName + "{ \n");
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String fieldname = entry.getKey();
			String fieldtype = entry.getValue();
			String filed = "\tprivate  " + fieldtype + "   " + fieldname
					+ " ;\n";
			sb.append("\n");
			sb.append(filed);
		}
		sb.append("\n\n");
		for (Map.Entry<String, String> entry : map.entrySet()) {
			sb.append(createMethod(entry, 0));
			sb.append(createMethod(entry, 1));
		}
		sb.append("}");
		String java = javaName + ".java";
		code.put(java, sb.toString());
		return code;
	}

	public  static String createMethod(Map.Entry<String, String> entry, int type) {
		StringBuilder method = new StringBuilder();
		if (0 == type) {
			String fieldname = entry.getKey();
			String methodname = "get"
					+ entry.getKey().substring(0, 1).toUpperCase()
					+ entry.getKey().substring(1);
			method.append("\tpublic  " + entry.getValue() + "     "
					+ methodname + "()");
			method.append("\t{\n");
			method.append("\t\t return " + fieldname + ";\n");
			method.append("\t}\n\n");
		} else {
			String fieldname = entry.getKey();
			String fieldtype = entry.getValue();
			String methodname = "set"
					+ entry.getKey().substring(0, 1).toUpperCase()
					+ entry.getKey().substring(1);
			method.append("\tpublic  void  " + methodname + "(" + fieldtype
					+ "   " + fieldname + ")");
			method.append("\t{\n");
			method.append("\t\t this." + fieldname + "=" + fieldname + ";\n");
			method.append("\t}\n\n");
		}

		return method.toString();
	}

}
