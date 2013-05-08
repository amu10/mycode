package com.song.annotation.analy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;

import com.dinfo.tengxun.solr.annotation.ExcludeField;
import com.dinfo.tengxun.solr.annotation.SolrClass;
import com.dinfo.tengxun.solr.annotation.SolrField;
import com.dinfo.tengxun.solr.bean.WeiSolr;
import com.dinfo.tengxun.util.FunctionUtil;

public class AnnotationAnaly {
	
	
	public SolrInputDocument analySolrAnnotation(Object o){
SolrInputDocument record = new SolrInputDocument();
		
		Class clazz = null;
		clazz = o.getClass();
		Map<String,String> mapField =  analySolrField(o);
		Set<String> fieldKey = mapField.keySet();
		List<String> exField = analyExcludeField(o);
		Method[] methods =    clazz.getDeclaredMethods();
		for(Method method :methods){
			String  methodname = method.getName();
			Class type = method.getReturnType();
			if(methodname.startsWith("get")){
				try {
				Object target=	MethodUtils.invokeMethod(o, methodname, null);
				String fieldname = methodname.substring(3).toLowerCase();
				if(target!=null&&!StringUtils.isBlank(target.toString())&&!exField.contains(fieldname)){
					fieldname = fieldKey.contains(fieldname) ? mapField.get(fieldname):fieldname;
					if(FunctionUtil.isTypeofString(type)){
						record.addField(fieldname, target.toString());
					}else if(FunctionUtil.isTypeofCollecton(type)){
						Collection<Object>	c = (Collection<Object> )target;
						for(Object item : c){
							record.addField(fieldname, item.toString());
						}
					}else if(FunctionUtil.isTypeofMap(type)){
						Map	map = (Map)target;
						for(Object  key :map.keySet()){
							record.addField(key.toString(), map.get(key.toString()).toString());
						}
					}else{
						record.addField(fieldname, target);
					}
					
				}
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return record;
	}
	
	public Map<String,String> analySolrField(Object o ){
		Map<String,String> fieldMap = new HashMap<String, String>();
		Field[] fields = o.getClass().getDeclaredFields();
		for(Field field :fields){
			
			if(field.isAnnotationPresent(SolrField.class)){
				String fieldname = field.getName();
				SolrField solrField  = field.getAnnotation(SolrField.class);
				String value = solrField.value();
				fieldMap.put(fieldname, value);
			}
		}
		return fieldMap;
	}
	public List<String> analyExcludeField(Object o){
		List<String> exculdeFields = new ArrayList<String>();
		Field[] fields = o.getClass().getDeclaredFields();
		for(Field field :fields){
			if(field.isAnnotationPresent(ExcludeField.class)){
					exculdeFields.add(field.getName());
			}
		}
		
		return exculdeFields;
	}
	
	public static void main(String[] argv){
		
		WeiSolr solr = new WeiSolr();
		solr.setId("123");
		solr.setPkeyword(3);
		solr.setPopenid("abvnd");
		new AnnotationAnaly().analySolrAnnotation(solr);
		//new AnnotationAnaly().analySolrAnnotation(solr);
		
	}
}
