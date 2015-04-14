/**
 * 
 */
package cn.edu.zucc.zql.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.conn.Wire;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zql
 *
 * @version 创建时间：2015年3月13日
 */
public class Tools {

	
	public static String signature(JSONObject params){
		String original = Constant.appkey;
		List list = new ArrayList<String>();
		Iterator iterator = params.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if (key.equals("signature"))
				continue;
			list.add(key);
		}
		Collections.sort(list);
		for (int i = 0; i < list.size(); i++) {
			original = original + params.get(list.get(i));
		}
		original = original + Constant.appkey;
		System.out.println("original---" + original);
//		original = changeCharset(original, "UTF-8");
		return md5(original);
	}
	
	public static String signature(Map params) {
		String original = Constant.appkey;
		List list = new ArrayList<String>();
		Iterator iterator = params.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if (key.equals("signature"))
				continue;
			list.add(key);
		}
		Collections.sort(list);
		for (int i = 0; i < list.size(); i++) {
			original = original + params.get(list.get(i));
		}
		original = original + Constant.appkey;
		System.out.println("original---" + original);
//		original = changeCharset(original, "UTF-8");
		return md5(original);
	}

	public static String md5(String original) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = original.getBytes("UTF-8");
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getTimestamp() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String time = format.format(new Date());
		return time;
	}

	public static String changeCharset(String str, String string) {
		// TODO Auto-generated method stub
		 if (str != null) {
			   byte[] bs = str.getBytes();
			   try {
				return new String(bs, string);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  }
			  return null;
	}
	
	public static JSONObject getResponse(HttpResponse response) throws IOException {
		if (null == response) {
			return null;
		}
		InputStream inputStream = null;
		BufferedReader reader = null;
		if (response.getStatusLine().getStatusCode() == 200) {

			HttpEntity httpEntity = response.getEntity();
			try {
				 inputStream = httpEntity.getContent();
				 reader = new BufferedReader(
						new InputStreamReader(inputStream));
				String result = "";
				String line = "";
				while (null != (line = reader.readLine())) {
					result += line;

				}
				inputStream.close();
				reader.close();
				
				 System.out.println("result----------" + result);
				return JSONObject.parseObject(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally{
				inputStream.close();
				reader.close();
			}
		}
		return null;

	}
	
	
	public static boolean writeToFile(InputStream uploadedInputStream,
			String uploadedFileLocation) {

//		uploadedFileLocation = uploadedFileLocation.trim();
		OutputStream out = null;
		try {
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		finally{
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public static boolean getFile(HttpResponse response,String filename) throws IOException {
		if (null == response) {
			return false;
		}
		InputStream inputStream = null;
		if (response.getStatusLine().getStatusCode() == 200) {

			HttpEntity httpEntity = response.getEntity();
			
			String path = Constant.downloadPath;

			File file = new File(path);
			if(!file.exists()){
				file.mkdirs();
			}
			path = path + filename;
			System.out.println("path----------" + path);
			try {
				 inputStream = httpEntity.getContent();
				 if(writeToFile(inputStream, path)){
					inputStream.close();
					return true;
				 }
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally{
				inputStream.close();
			}
		}
		return false;
	}
	
	

	public static void main(String[] args) {
		// Map<String, String> map = new HashMap<String, String>();
		// map.put("b", "n");
		// map.put("ac", "mi");
		// map.put("ab", "ad");
		// System.out.println(signature(map));
		// System.out.println(JSON.toJSONString(null));
		// System.out.println(setCode(10, null));
		// JSONObject result = new JSONObject();
		// result.put("1", "1");
//		Client client = Client.create();
//		Constant.setIp("122.235.97.92");
//		WebResource webResource = client.resource(Constant.getBaseURI() + "/login/check");
//		JSONObject params = new JSONObject();
//		params.put("id", "1");
//		params.put("password", Tools.md5("admin"));
//		params.put("timestamp", Tools.getTimestamp());
//		params.put("signature", Tools.signature(params));
//		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, params);
//		System.out.println(response);
//		System.out.println("Response for delete request: " + response.getStatus());
//		Client client = Client.create();
////		Constant.setIp("122.235.97.92");
//		WebResource webResource = client.resource("http://122.235.97.92:8080/teaching-material-management/TMMapi/login/check");
//		JSONObject params = new JSONObject();
//		params.put("id", "1");
//		params.put("password", Tools.md5("admin"));
//		params.put("timestamp", Tools.getTimestamp());
//		params.put("signature", Tools.signature(params));
//		ClientResponse response = webResource.type("application/json").post(ClientResponse.class, params.toString());
//		System.out.println(response);
//		System.out.println("Response for delete request: " + response.getStatus());
//		System.out.println(md5("7SB8OGSStudent人人698D51A19D8A121CE581499D7B7016682015-03-26 10:29:09.07SB8OGS"));
	}

}
