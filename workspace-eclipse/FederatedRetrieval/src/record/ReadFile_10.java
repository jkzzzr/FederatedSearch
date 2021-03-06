package record;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.zip.GZIPInputStream;

import org.w3c.dom.css.ElementCSSInlineStyle;

import test.Readline;
import tools.tools;

public class ReadFile_10 implements Ireadfile{

	/**
	 * 记录硬盘中的ClueWeb09_English_2文件夹下面包含哪些en0000的文件夹
	 */
	private HashMap<String,String> HMap_floder_en = new HashMap<String,String>();
	public static void main(String[] args) {
		

	}
	public void run(){
	}
	/**
	 * 读取ClueWeb09_English_1一级的目录，将这个目录中包含的en0000存储在HMap_floder_en中
	 * 初始化HMap_floder_en对象
	 */
	public void init_floder_en(){
		String input_Prefix = "/media/clueweb09_1of2/ClueWeb09_English_";
		
		//将en0000与外层的文件夹对应起来ClueWeb09_English_1
		//记录每个ClueWeb09_English_1下面存储的en0000文件夹有哪些
		HashMap<String, String> hMap = new HashMap<>();
		for (int i1 = 1; i1 <= 10; i1++){
			File file = new File(input_Prefix + i1 +"/");
		//	System.out.println(file.isDirectory() +"\t"+file.getAbsolutePath());
			File [] flordNames = file.listFiles();
			for (File string: flordNames){
				hMap.put(string.getName(), "ClueWeb09_English_" + i1);
			}
		}
		HMap_floder_en = hMap;
	}
	/**
	 * 读取en0000一级的目录，将这个目录包含的warc.gz文件存储在数组中返回
	 * @param output_prefix 示例：/home/Lee/音乐/result/en0000
	 */
	public String[] read_en(String flodername,String inputPath_prefix){
		//进入这个目录,同时创建输出目录
		String inputFilePath = inputPath_prefix + "/" + flodername + "/";
		
		
		File file = new File(inputFilePath);
		
		
		File [] files = file.listFiles();
		String [] sub_filename = new String[files.length];
		int index = 0;
		for (File temp_file: files){
			if (!temp_file.getName().contains("warc.gz")){
				continue;
			}
			String filename = temp_file.getName();
			filename = filename.substring(0, 2);
			sub_filename[index++] = filename;
		}
		return sub_filename;
		
	}
/*	*//**
	 * 
	 * @param flodername	示例：en0000
	 * @param inputPath_prefix	示例：media/clueweb09_1of2/ClueWeb09_English_1/en0000
	 * runn("/media/clueweb09_1of2/ClueWeb09_English_1/en0000/00.warc.gz", "/home/Lee/音乐/result/en0000/00.warc", treeSet.get("00"));
			
	 * @throws Exception 
	 *//*
	public void filterAndWrite(String inputPath, String outputPath) throws Exception{
		File file2 = new File(outputPath);
		if (!file2.exists()){
			file2.createNewFile();
		}

		GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(inputPath));
		
		BufferedReader bReader = new BufferedReader(new InputStreamReader(gzipInputStream, "UTF-8"));
//		BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
		BufferedWriter bWriter = new BufferedWriter(new FileWriter(outputPath));
		
	
		String templine = "";
		//去掉每个文件，最开头的一个说明小段落
		Index2 index = new Index2();
		index.x = 0;
		index.path = inputPath;
		for (int tempx = 1;tempx <=18;tempx++){
			templine = bReader.readLine();
			index.incream(templine);
		}
		
		while (true){
			templine = bReader.readLine();
			if (templine == null){
				break;
			}
			index.incream(templine);
			
			if (templine.startsWith("WARC/1.0")){
				LINEInfo lineInfo = new LINEInfo();
				lineInfo.skip = index.x;
				
				//WARC-Type: response
				//WARC-Date: 2012-02-10T21:51:20Z
				for (int tempx = 1;tempx <=2;tempx++){
					templine = bReader.readLine();
					index.incream(templine);
				}
				//WARC-TREC-ID: clueweb12-0000tw-00-00013
				templine = bReader.readLine();
				index.incream(templine);
				if (templine == null){
					break;
				}
				if (templine.startsWith("WARC-TREC-ID: ")){
					String extractDocid = "";
					try{
						extractDocid = templine.replaceAll("WARC-TREC-ID: ", "");
					}catch(Exception e){
						extractDocid = templine.substring(templine.indexOf("clueweb"));
					}
					lineInfo.docid = extractDocid;
				}
				
				
				//WARC-Target-URI:
				templine = bReader.readLine();
				index.incream(templine);
				if (templine == null){
					break;
				}
				if (templine.contains("WARC-Target-URI:")){
					String extractURI = "";
					try{
						extractURI = templine.replaceAll("WARC-Target-URI: ", "");
					}catch(Exception e){
						extractURI = templine.substring(templine.indexOf("http"));
					}
					lineInfo.URI = extractURI;
				}
				//WARC-Payload-Digest: sha1:YZUOJNSUMFG3JVUKM6LBHMRMMHWLVNQ4
				//WARC-IP-Address: 100.42.59.15
				//WARC-Record-ID: <urn:uuid:74edc71e-a881-4942-81fc-a40db4bf1fb9>
				//Content-Type: application/http; msgtype=response
				for (int tempx = 1;tempx <=5;tempx++){
					templine = bReader.readLine();
					index.incream(templine);
				}
				
				
				long needskip = 0;
				boolean flag_skip = false;
				//Content-Length: 71726
				templine = bReader.readLine();
				index.incream(templine);
				if (templine.contains("Content-Length:")){
					flag_skip = true;
					String extractLength = "";
					extractLength = templine.replaceAll("Content-Length: ", "");
					needskip = Long.parseLong(extractLength);
				}
				//HTTP/1.1 200 OK
				//Date: Fri, 10 Feb 2012 21:51:22 GMT
				//Server: Apache/2.2.21 (Unix) mod_ssl/2.2....
				//X-Powered-By: PHP/5.2.17
				//X-Pingback: http://cheapcosthealthinsurance.com/xmlrpc.php
				//Link: <http://cheapcosthealthinsurance.com/?p=711>; rel=shortlink
				//Connection: close
				//Content-Type: text/html; charset=UTF-
				for (int tempx = 1;tempx <=20;tempx++){
					String tempt = bReader.readLine();
					index.incream(tempt);
					if (tempt.isEmpty()){
						count ++;
				//		System.out.println("break");
						break;
					}
				}
				
				if (flag_skip){
					String tempt = bReader.readLine();
					index.incream(tempt);
					if(tempt.isEmpty()){
						
					}else {
						needskip -= tempt.length() + 1;
					}
					bReader.skip(needskip);
					index.x += needskip;
					flag_skip = false;
				}
				
				
				
				if (lineInfo.docid.length()<2){
			//		tools.run(templine+"\t"+lineInfo.toString());
				}else {
					bWriter.write(lineInfo.toString());
					bWriter.newLine();
					bWriter.flush();
				}
			}
			
	}
		bReader.close();
		bWriter.flush();
		bWriter.close();
	}*/
	/**
	 * 
	 * @param flodername	示例：en0000
	 * @param inputPath_prefix	示例：media/clueweb09_1of2/ClueWeb09_English_1/en0000
	 * runn("/media/clueweb09_1of2/ClueWeb09_English_1/en0000/00.warc.gz", "/home/Lee/音乐/result/en0000/00.warc", treeSet.get("00"));
			
	 * @throws Exception 
	 */
	public void filterAndWrite(String inputPath, String outputPath) throws Exception{
		File file2 = new File(outputPath);
		if (!file2.exists()){
			file2.createNewFile();
		}

		GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(inputPath));
		Readline readline = new Readline(inputPath);
		readline.setIs(gzipInputStream);
	//	BufferedReader bReader = new BufferedReader(new InputStreamReader(gzipInputStream, "UTF-8"));
//		BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
		BufferedWriter bWriter = new BufferedWriter(new FileWriter(outputPath));
		
	
		String templine = "";
		//去掉整个文件，最开头的一个说明小段落
		Index2 index = new Index2();
		index.x = 0;
		index.path = inputPath;
		for (int tempx = 1;tempx <=6;tempx++){
			templine = readline.readLine();
			index.incream(readline);
		}
		
		while (true){
			templine = readline.readLine();
			if (templine == null){
				break;
			}
			index.incream(readline);
			if (templine.startsWith("WARC/1.0")){
				LINEInfo lineInfo = new LINEInfo();
				lineInfo.skip = (index.x -= readline.getReadLineByteCount());

				long needskip = 0;
				boolean hasID = false;
				boolean hasURI = false;
				for (int tempx = 1;tempx <=60;tempx++){
					templine = readline.readLine();
					index.incream(readline);
					if (templine == null){
						break;
					}
				
					if (templine.startsWith("WARC-TREC-ID: ")){
						hasID = true;
						String extractDocid = "";
						try{
							extractDocid = templine.replaceAll("WARC-TREC-ID: ", "");
						}catch(Exception e){
							extractDocid = templine.substring(templine.indexOf("clueweb"));
						}
						lineInfo.docid = extractDocid;
					}
					if (templine.contains("WARC-Target-URI:")){
						hasURI = true;
						String extractURI = "";
						try{
							extractURI = templine.replaceAll("WARC-Target-URI: ", "");
						}catch(Exception e){
							extractURI = templine.substring(templine.indexOf("http"));
						}
						lineInfo.URI = extractURI;
					}
				
					if (templine.contains("Content-Length:")){
						try{
						String extractLength = "";
						extractLength = templine.replaceAll("Content-Length: ", "");
						needskip = Long.parseLong(extractLength);
						needskip=(Long)Math.round(needskip*1.0);
						}catch (Exception e) {
							//就是没有找到Content-Length嘛，也没什么大不了的，就是继续一句一句的循环呗，就不跳了
							tools.run("@ReadFile_10\n\tContent-Length err\n\tID是：" + lineInfo.docid+
									"\n\t出错的语句是：" + templine);
						}
						if (hasID&&hasURI){
							readline.skip(needskip);
							index.x += needskip;
						}else {
							tools.run("@ReadFile_10\n\tContent-Length err没有ID和URI就找到了Content-Length\n\t"
									+ "\n\t文档ID为：" + lineInfo.docid 
									+ "\n\t文档URI为：" + lineInfo.URI);
						}
						break;
					}
					
				}
				
				
				bWriter.write(lineInfo.toString());
				bWriter.newLine();
				bWriter.flush();
			}
			
	}
//		bReader.close();
		bWriter.flush();
		bWriter.close();
		gzipInputStream.close();
	}
	class Index2{
		long x = 0;
		String path = "";
		void incream(String line ){
			GZIPInputStream gzipInputStream;
			try {
				gzipInputStream = new GZIPInputStream(new FileInputStream(path));
				BufferedReader bReader = new BufferedReader(new InputStreamReader(gzipInputStream, "UTF-8"));
				bReader.skip(x);
				String skipString = bReader.readLine();
				if (!skipString.equals(line)){
					System.out.println("err:"+line);
					System.out.println("\t:"+skipString);
					Thread.sleep(500);
				}else {
				}
				x+=skipString.length()+1;
				bReader.close();
				gzipInputStream.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		void incream(Readline readline ){
			this.x +=readline.getReadLineByteCount();
		}
	}
}
