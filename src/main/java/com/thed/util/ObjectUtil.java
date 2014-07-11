package com.thed.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ObjectUtil {
	public static final Map<String, String> RESULT_STATUS_MAP = new HashMap<String, String>(4);
	public static final Map<String, String> RELEASE_STATUS_MAP = new HashMap<String, String>(4);
	public static final Map<String, String> TC_PRIORITY_STATUS_MAP = new HashMap<String, String>(4);
	public static final Map<String, String> REQ_PRIORITY_STATUS_MAP = new HashMap<String, String>(4);
	public static final Map<String, String>	TEST_STEP_STATUS_MAP  = new HashMap<String, String>(4);
	private final static Log log = LogFactory.getLog(ObjectUtil.class);
	 
	public static Object cloneObject(Object object) {
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(object);
			oos.flush();
			ObjectInputStream in = new ObjectInputStream(
					new ByteArrayInputStream(bos.toByteArray()));
			return in.readObject();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			try {
				if (ois != null)
					ois.close();
				if (oos != null)
					oos.close();
			} catch (Exception ex) {
				log.fatal("Unable to clone Object", ex);
			}
		}
	}
	
	public static String urlFormat(String url){
    	if(!StringUtils.isBlank(url)){
    		if(url.endsWith("/")){
    			return url.substring(0, url.length()-1);
    		}
    	}
    	return url;
    }
	
	/**
	 * Used by testcase priority and execution result
	 * @param key
	 * @param map Static map containing the mapping of preference key and value
	 * @return
	 */
	public static String translatePreference(String key, Map<String, String> map){
		if(map != null && map.containsKey(key)){
			return map.get(key);
		}else{
			return key;
		}
	}
	
	/**
	 * Used by testcase Priority, requirement Priority 
	 * @param value
	 * @param map Static map containing the mapping of preference key and value
	 * @param defaultPriority used when any garbage priority imported from a file 
	 * @return
	 */
	public static String reverseTranslatePreference(String value, Map<String, String> map, String defaultPriority){
		String result = "";
		if(map != null && value != null){
			 Set<Entry<String, String>> entries = map.entrySet();
			 for(Entry<String, String> entry : entries){
				 if(entry.getValue().equals(value)){
					 result = entry.getKey();
				 }
			 }
		}else{
			result = defaultPriority;
			}
		return result;
		}
	
	public static void loadLOVs(){
		loadLOVs(false);
	}
	
	public static void loadLOVs(boolean force){
		if(force || RESULT_STATUS_MAP.size() < 1){
			populateHashMapFromPreference("testresult.testresultStatus.LOV", RESULT_STATUS_MAP);
		}
		if(force || TC_PRIORITY_STATUS_MAP.size() < 1){
			populateHashMapFromPreference("testcase.testcasePriority.LOV", TC_PRIORITY_STATUS_MAP);
		}
		if(force || REQ_PRIORITY_STATUS_MAP.size() < 1){
			populateHashMapFromPreference("requirement.requirementPriority.LOV", REQ_PRIORITY_STATUS_MAP);
		}
		if(force || RELEASE_STATUS_MAP.size() < 1){
			populateHashMapFromPreference("release.releaseStatus.LOV", RELEASE_STATUS_MAP);
		}
		if(force || TEST_STEP_STATUS_MAP.size()<1){
			populateHashMapFromPreference("testStepResult.testStepResultStatus.LOV", TEST_STEP_STATUS_MAP);
		}
	}
	
	/**
	 * It check if there is LOV in the picklist of custom field , it there is it put them in hasp map 
	 * if no value is found it return 
	 * @param prefKey
	 * @param map
	 */
	public static void populateHashMapFromPreference(String prefKey, Map<String, String> map){
		String[] nameValues = prefKey.split(";");
		synchronized (map) {
			for(String entry : nameValues){
				String []nameValue = entry.split("=");
				if(nameValue.length == 2)
					map.put(nameValue[0], nameValue[1]);
			}
		}
	}

    public static String htmlToText(String html) throws IOException{
        if(StringUtils.isBlank(html)) return html;
        final StringBuffer txt = new StringBuffer();
        HTMLEditorKit.ParserCallback callback =
                new HTMLEditorKit.ParserCallback () {
                    public void handleSimpleTag(HTML.Tag t, javax.swing.text.MutableAttributeSet a, int pos){
                        if(t == HTML.Tag.BR || t == HTML.Tag.P)
                            txt.append("\n");
                        if(t == HTML.Tag.LI)
                            txt.append("\n\t");
                    }
                    public void handleStartTag(HTML.Tag t, javax.swing.text.MutableAttributeSet a, int pos){
                        if(t == HTML.Tag.BR || t == HTML.Tag.P)
                            txt.append("\n");
                        if(t == HTML.Tag.LI)
                            txt.append("\n\t");
                    }
                    public void handleText(char[] data, int pos) {
                        txt.append(data);
                    }
                };
        Reader reader = new CharArrayReader(html.toCharArray());
        new ParserDelegator().parse(reader, callback, false);
        String textDetail = txt.toString();
        if(StringUtils.contains(textDetail, "&apos")) {
            textDetail = textDetail.replace("&apos", "'");
        }
        textDetail = StringEscapeUtils.unescapeHtml(textDetail);
        return textDetail;
    }
}