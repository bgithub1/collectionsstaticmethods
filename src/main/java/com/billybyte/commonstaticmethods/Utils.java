package com.billybyte.commonstaticmethods;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.thoughtworks.xstream.XStream;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class Utils {
	/*
	 * "   &quot;
		'   &apos;
		<   &lt;
		>   &gt;
		&   &amp;
	 */
	private static String[][] xmlCharsToEscape = {
		{"&","&amp;"},
		{"<","&lt;"}, 
		{">","&gt;"}, 
		{"\"","&quot;"},
	};
	
	
	
	
	/**
	 * Convert all characters that need to be escaped for valid xml
	 * @param stringWithEscapedChars - String that contains <,>,\,&, etc
	 * @return String with xmlEscapes for above characters
	 */
	public static String xmlEscapeConvert(String stringWithEscapedChars){
		String ret = stringWithEscapedChars;
		for(String[] escArr :xmlCharsToEscape){
//			if(ret.contains(escArr[0])){
//				Utils.prtObMess(Utils.class, "xmlEscapeConvert: about to convert char "+escArr[0]);
//			}
			ret = ret.replace(escArr[0], escArr[1]);
		}
		return ret;
	}
	
	public static <T> Set<T>  getSetFromArray(T ... array){
		return new HashSet<T>(getListFromArray(array));
	}
	
	public static <T> List<T>  getListFromArray(T[] array){
		List<T> tList = Arrays.asList(array);
		return tList;
	}
	
	public static boolean regexMatch(String regexExpression,String keyToSearch){
		Pattern pattern =Pattern.compile(regexExpression);
		Matcher matcher = 	pattern.matcher(keyToSearch);
		return matcher.find();
	}
	
	public static 		List<String> getTokensFromTemplate(String template){
		return Utils.getRegexMatches("@[\\w\\-]+@", template);
	}



	public static String ErMs(Object o,String mess){
		if(Class.class.isAssignableFrom(o.getClass())){
			return ((Class<?>)o).getName()+" "+ mess;
		}else{
			return o.getClass().getName()+" "+ mess;
			
		}
	}
	
	public static IllegalArgumentException IllArg(Object o, String mess){
		return new IllegalArgumentException(ErMs(o,mess));
	}
	
	public static IllegalStateException IllState(Object o, String mess){
		return new IllegalStateException(ErMs(o,mess));
	}

	public static IllegalStateException IllState(Throwable cause){
		return new IllegalStateException(cause);
	}

	public static List<String> getRegexMatches(String regexExpression,String stringToSearch){
//		List<String> ret = new ArrayList<String>();
		Pattern pattern =Pattern.compile(regexExpression);
		Matcher matcher = pattern.matcher(stringToSearch);
		return getRegexMatches(matcher);

	}
	
	public static List<String> getRegexMatches(Matcher matcher){
		List<String> ret = new ArrayList<String>();
		HashSet<String> foundTokens = new HashSet<String>();
		while (matcher.find()) {
			String token = matcher.group();
			if(foundTokens.contains(token)){
				continue;
			}
			ret.add(token);
			foundTokens.add(token);
//			System.out.format("I found the text \"%s\" starting at " +
//		       "index %d and ending at index %d.%n",
//		        token, matcher.start(), matcher.end());
		}
		return ret;
		
	}
	
	/**
	 * 
	 * @param sourceClass - class name to print on message
	 * @param s - message to print
	 */
	public static void prtObMess(Class<?> sourceClass,String s){
		prt(sourceClass.getName()+" : "+s);
	}
	public static void prt(String s){
		System.out.println(s);
	}
	public static void prtNoNewLine(String s){
		System.out.print(s);
	}
	
	public static void prt(int i){
		System.out.println(new Integer(i).toString());
	}
	
	public  static void prtErrWithCsvLineData(Class<?> sourceClass,String errMess,String[] csvDataLine){
		Utils.prtObErrMess(sourceClass, errMess + " for line"+ Arrays.toString(csvDataLine));
	}

	public static IllegalStateException illStateWithCsvLineData(Class<?> sourceClass,String errMess,String[] csvDataLine){
		throw Utils.IllState(sourceClass, errMess + " for line"+ Arrays.toString(csvDataLine));
	}
	
	public static  IllegalArgumentException illArgWithCsvLineData(Class<?> sourceClass,String errMess,String[] csvDataLine){
		throw Utils.IllArg(sourceClass, errMess + " for line"+ Arrays.toString(csvDataLine));
	}
	
	public static IllegalStateException illStateWithExceptList(Class<?> sourceClass,List<Exception> exceptList){
		String s = "";
		int len = exceptList.size();
		for(int i = 0;i<len;++i){
			s += exceptList.get(i);
			if(i<len-1) s +="\n";
		}
		return IllState(sourceClass, s);
	}


	public static void prt(long i){
		System.out.println(new Long(i).toString());
	}
	
	public static void prt(double d){
		System.out.println(new Double(d).toString());
	}

	
	public static void prtErr(String s){
		System.err.println(s);
	}

	public static void prtObErrMess(Class<?> sourceClass,String s){
		if(LoggingUtils.PRINT_ERROR_TO_LOG.get()){
			LoggingUtils.logError(sourceClass, s);
		}else{
			System.err.println(sourceClass.getName()+" " + s);
		}
	}

	
	public static void sleep(long millsToSleep){
		try {
			Thread.sleep(millsToSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new IllegalStateException(Dates.class.getName());
		}
	}
	
	public static boolean  copyFile(String srFile, String dtFile){
		InputStream in;
		try{
			File f2 = new File(dtFile);
			if(srFile.toLowerCase().contains("ftp://") || srFile.contains("http://")  || srFile.contains("https://")){
				in = getInputStreamFromHttpFtp(srFile);
				
			}else{
				File f1 = new File(srFile);
				in = new FileInputStream(f1);
			}
			
			
			//For Append the file.
//			OutputStream out = new FileOutputStream(f2,true);

			//For Overwrite the file.
			OutputStream out = new FileOutputStream(f2);
			return copyFile(in,out);
//			
//			byte[] buf = new byte[1024];
//			int len;
//			while ((len = in.read(buf)) > 0){
//				out.write(buf, 0, len);
//			}
//			in.close();
//			out.close();
//			return true;
		}
		catch(FileNotFoundException ex){
			throw new IllegalStateException(ex.getMessage() + " in the specified directory.");
			
		}
//		catch(IOException e){
//			throw new IllegalStateException(e.getMessage());			
//		}
	}


	
	public static boolean  copyFile(InputStream in, OutputStream out){
		byte[] buf = new byte[1024];
		int len;
		try {
			while ((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			return true;
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage());			
		}
	}

	public static boolean  copyFile(InputStream in, String dtFile){
		byte[] buf = new byte[1024];
		int len;
		try {
			File f2 = new File(dtFile);
			OutputStream out = new FileOutputStream(f2);

			while ((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			return true;
		} catch (IOException e) {
			throw Utils.IllState(e);			
		}
	}

	
	public static void clipBoardSet(Object o){
		String clipValue = o.toString();
		StringSelection data = new StringSelection(clipValue);
		 Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		 clipboard.setContents(data,data);

	}
	
	public static InputStream getInputStreamAsResource(Class<?> clazz,String fileName){
		InputStream stream = clazz.getResourceAsStream(fileName);
		return stream;
	}

	public static void makeDirectory(String path){
		File file = new File(path);
		if (!file.exists()) {
			if (file.mkdir()) {
				return;
			} else {
				throw IllState(Utils.class,"directory already exists!");
			}
		}

	}

	
	public static String getPathOfResource(Class<?> clazz,String fileName){
		URL url = getUrlAsResource(clazz, fileName);
		if(url==null)return null;
		return url.getPath();
	}
	
	public static URL getUrlAsResource(Class<?> clazz,String fileName){
		URL url = clazz.getResource(fileName);
		return url;
	}

	public static InputStreamReader getInputStreamReaderAsResource(Class<?> clazz,String fileName){
		return new InputStreamReader(getInputStreamAsResource(clazz,fileName));
	}
	
	public static BufferedReader getBufferedReaderAsResource(Class<?> clazz,String fileName){
		return new BufferedReader(getInputStreamReaderAsResource(clazz,fileName));
	}
	
	public static BufferedReader getBufferedReaderFromInputStreamReader(InputStreamReader isReader){
		return new BufferedReader(isReader);
	}
	
	public static InputStream getInputStreamFromHttpFtp(String urlString){
		
		URL url=null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			throw Utils.IllArg(Utils.class, e.getMessage());
//			Utils.prtObErrMess(Utils.class, " readHttp: Can't find "+urlString);
//			Utils.prtObErrMess(Utils.class, e.getMessage());
//			return null;
		}

		InputStream iS=null;
		try {
			iS = url.openStream();
			return iS;
		} catch (IOException e) {
			throw Utils.IllArg(Utils.class, e.getMessage());
		}

	}	
	
	public static List<LsEntry> getSftpLs(
			String user,
			String pw,
			String address,
			String folderPath){
		
		try {
			JSch jsch = new JSch();

			Session session = jsch.getSession(user, address);
			Properties config = new java.util.Properties(); 

			config.put("StrictHostKeyChecking", "no");

			session.setConfig(config);
			session.setPassword(pw);
			session.connect();
			Channel channel = session.openChannel( "sftp" );
			channel.connect();

			ChannelSftp sftpChannel = (ChannelSftp) channel;
			try {
				Vector<LsEntry> lsEntries = sftpChannel.ls(folderPath);
				List<LsEntry> ret = new ArrayList<ChannelSftp.LsEntry>();
				for(LsEntry lsEntry:lsEntries){
					ret.add(lsEntry);
				}
				return ret;
			} catch (SftpException e) {
				e.printStackTrace();
				return null;
			}
			
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static TreeMap<Integer, String> getSftpLsEntriesByRegexFileName(
			String user,
			String pw,
			String ftpIp,
			String regexToMatch){
		List<LsEntry> lsEntries = Utils.getSftpLs(user, pw, ftpIp, ".");
		Pattern  p = Pattern.compile(regexToMatch);
		TreeMap<Integer, String> ret = new TreeMap<Integer, String>();
		for(LsEntry lsEntry : lsEntries){
			String fName = lsEntry.getFilename();
			List<String> matches = RegexMethods.getRegexMatches(p, fName);
			if(matches==null || matches.size()<1){
				continue;
			}
			SftpATTRS att = lsEntry.getAttrs();
			if(att.isDir())continue;
			ret.put(att.getATime(), lsEntry.getFilename());
		}
		return ret;
	}

	
	public static List<String[]> getCsvViaSftp(
			String user,
			String pw,
			String address,
			String fileName){
		
		try {
			
			//user:TGROSS

			//pw:6MbAWV2F

			//address:38.98.129.85:22

			//filename:20121212ADVPOS.CSV

//			String user = "TGROSS";
//			String pw = "6MbAWV2F"; 
//			String address = "38.98.129.85";
//			String fileName = "20121212ADVPOS.CSV";

			JSch jsch = new JSch();

			Session session = jsch.getSession(user, address);
			Properties config = new java.util.Properties(); 

			config.put("StrictHostKeyChecking", "no");

			session.setConfig(config);
			session.setPassword(pw);
			session.connect();
			Channel channel = session.openChannel( "sftp" );
			channel.connect();

			ChannelSftp sftpChannel = (ChannelSftp) channel;

			InputStream in = sftpChannel.get(fileName);

			List<String[]> data = getCSVData(new InputStreamReader(in));
			sftpChannel.exit();
			session.disconnect();
			return data;
//			Scanner s = new java.util.Scanner(in); //.useDelimiter("\\A");
//
//			while(s.hasNext()){
//				String line = s.next();
//				System.out.println(line);
//			}

		} catch (JSchException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<String[]> getCsvViaFtp(
			String user,
			String pw,
			String address,
			String fileName){
		
		try {
			
			URL url = new URL("ftp://"+user+":"+pw+"@"+address+"/"+fileName);
			
			URLConnection conn = url.openConnection();
			
			InputStream is = conn.getInputStream();
			
			CSVReader reader = new CSVReader(new InputStreamReader(is));
			
			List<String[]> csvLines = Utils.getCSVData(reader);
			
			return csvLines;
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	
	
	public static CSVReader getCSV(String filename)  {
//		String filename="http://www.barchart.com/historicaldata.php?sym=IBM&view=historicalfiles&txtDate=10/28/10#";
		Reader bufferedReader=null;
		CSVReader reader;

		if(filename.substring(0,3).compareTo("ftp")==0 || filename.substring(0,4).compareTo("http")==0 ){
			try{
				URL url = 
				    new URL(filename);
				URLConnection con = url.openConnection();
				bufferedReader = 
					new BufferedReader(new InputStreamReader(con.getInputStream()));
			}catch(Exception e){
				String s = stackTraceAsString(e);
				Utils.prtObErrMess(Utils.class,s);
				Utils.prtObErrMess(Utils.class,"ftp connection failed to :"+filename);	
				return null;
			}
		}else try{
			bufferedReader = new FileReader(filename);
		}catch(Exception e){
			String s = stackTraceAsString(e);
			Utils.prtObErrMess(Utils.class,s);
			Utils.prtObErrMess(Utils.class,"CSV input file not found:" + filename);	
//			Utils.prt(e.getMessage());
//			e.printStackTrace();
			
			return null;
			
		}
		reader = new CSVReader(bufferedReader);
		return reader;
	}
	
	/**
	 * Search for a column header and return the column index (0 based)
	 * @param columnHeaderValueToGet
	 * @param csvHeader
	 * @return column index or -1 if not found
	 */
	public static int getCsvColumnIndex(String columnHeaderValueToGet,String[] csvHeader){
		if(csvHeader==null || csvHeader.length<1)return -1;
		for(int i=0;i<csvHeader.length;i++){
			String headerValue = csvHeader[i];
			if(headerValue.compareTo(columnHeaderValueToGet)==0){
				return i;
			}
		}
		return -1;
	}
	
	public static ArrayList<String[]> getCSVData(InputStreamReader inputStreamReader){
		CSVReader reader = new CSVReader(new BufferedReader(inputStreamReader));
		return getCSVData(reader);
	}
	
	
	public static ArrayList<String[]> getCSVData(CSVReader reader){
		String [] nextline;
		ArrayList<String[]> retList = new ArrayList<String[]>();

		try {
				while((nextline = reader.readNext())!=null){
					retList.add(nextline);
				}
				return retList;
			} catch (IOException e) {
			
				e.printStackTrace();
				return null;
			}
	}
	
	public static ArrayList<String[]> getCSVData(Class<?> classInPackageOfFile, String csvFileName){
		if(classInPackageOfFile==null)return getCSVData(csvFileName);
		BufferedReader bf = getBufferedReaderAsResource(classInPackageOfFile, csvFileName);
		CSVReader reader = new CSVReader(bf);
		return getCSVData(reader);
	}
	
	public static long getFileLength(String filePath){
		File f=null;
		try {
			f = new File(filePath);
			if(!f.exists())return 0;
		} catch (Exception e) {
			return 0;
		}
		
		return f.length();
	}

	public static ArrayList<String[]> getCSVDataWithProgress(String csvFileName){
		CSVReader reader = getCSV(csvFileName);
		String [] nextline;
		ArrayList<String[]> retList = new ArrayList<String[]>();
		long lineCount=0;
		try {
				while((nextline = reader.readNext())!=null){
					retList.add(nextline);
					lineCount++;
					
					if(lineCount % 100 == 0){
						Utils.prtObMess(Utils.class, " processed  "+lineCount+" lines in file: "+csvFileName);
					}
				}
				return retList;
			} catch (IOException e) {
			
				e.printStackTrace();
				return null;
			}
	}

	
	public static ArrayList<String[]> getCSVData(String csvFileName){
		CSVReader reader = getCSV(csvFileName);
		String [] nextline;
		ArrayList<String[]> retList = new ArrayList<String[]>();
		try {
				while((nextline = reader.readNext())!=null){
					retList.add(nextline);
				}
				return retList;
			} catch (IOException e) {
			
				e.printStackTrace();
				return null;
			}
	}
	
	public static URLConnection getUrlConnection(String urlString) throws IOException{
		URL url = 
			    new URL(urlString);
			URLConnection con = url.openConnection();
			return con;
	}
	public static Writer getWriter(String filename){
		Writer bufferedWriter=null;

		if(filename.substring(0,3).compareTo("ftp")==0 || filename.substring(0,4).compareTo("http")==0 ){
			try{
				
				URLConnection con = getUrlConnection(filename);
				con.setDoOutput(true);
				bufferedWriter = 
					new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
			}catch(Exception e){
//				e.printStackTrace();
//				System.out.println("ftp connection failed to :"+filename);	
				String s = stackTraceAsString(e);
				prtObErrMess(Utils.class, s);
				prtObErrMess(Utils.class,"ftp connection failed to :"+filename);
				return null;
			}
		}else try{
			bufferedWriter = new FileWriter(filename);
		}catch(Exception e){
//			System.out.println("CSV input file not found:" + filename);	
			String s = stackTraceAsString(e);
			prtObErrMess(Utils.class, s);
			prtObErrMess(Utils.class, "CSV input file not found:" + filename);
			return null;
			
		}

		return bufferedWriter;
	}
	
	public static CSVWriter getCSVWriter(String filename){
		CSVWriter writer ;
		Writer bufferedWriter=null;

		if(filename.substring(0,3).compareTo("ftp")==0 || filename.substring(0,4).compareTo("http")==0 ){
			try{
				URL url = 
				    new URL(filename);
				URLConnection con = url.openConnection();
				bufferedWriter = 
					new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
			}catch(Exception e){
//				e.printStackTrace();
//				System.out.println("ftp connection failed to :"+filename);	
				String s = stackTraceAsString(e);
				prtObErrMess(Utils.class, s);
				prtObErrMess(Utils.class,"ftp connection failed to :"+filename);
				return null;
			}
		}else try{
			bufferedWriter = new FileWriter(filename);
		}catch(Exception e){
//			System.out.println("CSV input file not found:" + filename);	
			String s = stackTraceAsString(e);
			prtObErrMess(Utils.class, s);
			prtObErrMess(Utils.class, "CSV input file not found:" + filename);
			return null;
			
		}
		writer = new CSVWriter(bufferedWriter);
		return writer;

	}
	
	public static void writeCSVData(List<String[]> outData,String outFileName) throws IOException{
		CSVWriter writer = getCSVWriter(outFileName);
		writer.writeAll(outData);
		writer.flush();
		writer.close();
	}
	
	/**
	 * Writes the data from a list of String arrays to a csv file
	 * @param outData
	 * @param outFileName
	 * @throws IOException
	 */
	public static void writeCleanCSVData(List<String[]> outData,String outFileName) throws IOException{
		
		File outputFile = new File(outFileName);
		
		FileWriter writer = new FileWriter(outputFile);
		
		String eol = System.getProperty("line.separator");
		
		try {
			for (String[] line : outData) {
				if (line.length == 0)
					continue;
				if (line.length == 1) {
					writer.append(line[0]);
					writer.append(eol);
					continue;
				} else {
					for (int i = 0; i <= line.length - 2; i++) {
						writer.append(line[i]);
						writer.append(',');
					}
					writer.append(line[line.length - 1]);
					writer.append(eol);
				}

			}
		} finally {
			writer.flush();
			writer.close();
		}
		
	}
	
	public static void writeLineData(List<String> lines, String outputPath) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputPath)));
		for(String s:lines){
			bw.write(s);
			bw.newLine();
		}
		bw.close();
	}
	
	/**
	 * 
	 * @param classInPackageOfFile - This a class which resides in the same package as the
	 * 			file that you want to read.  The file that you are reading should have been created
	 * 			by an Eclipse editor, in the source package where this class resides.  The
	 * 			getBufferedReaderAsResource method will find the same file - now conveniently located
	 * 			in the associated bin directory ( Eclipse copied it automatically when it did build
	 * 			of the project).
	 * @param lineDataFilePath - name of the file that you created using the Eclipse editor.
	 * @return ArrayList<String> of lines from the file.
	 */
	public static ArrayList<String> getLineData(Class<?> classInPackageOfFile, String lineDataFilePath){
		Reader bf =null;
		if(classInPackageOfFile!=null){
			bf = getBufferedReaderAsResource(classInPackageOfFile, lineDataFilePath);
		}else{
			try {
				bf = new FileReader( lineDataFilePath);
			} catch (FileNotFoundException e) {
				return null;
			}
		}
		
		CSVReader reader = new CSVReader(bf);
		
		String [] nextline;
		ArrayList<String> retList = new ArrayList<String>();

		try {
				while((nextline = reader.readNext())!=null){
					retList.add(nextline[0]);
				}
				return retList;
			} catch (IOException e) {
			
				e.printStackTrace();
				return null;
			}
	}


	public static String createPackagePath(String pathPrefix, Class<?> clazz){
		return pathPrefix+clazz.getPackage().getName().replace(".", "/")+"/";
		
	}

//	public static void addClassesToXstream(XStream xs,String packageName){
//		   ArrayList<Class<?>>  classes = GetClasses.getArrayListOfClasses(packageName);
//		   for(Class<?> clazz:classes){
//			   xs.processAnnotations(clazz);
//		   }
//
//	}
	
	public static List<String> readHttp(String urlString){
		
		URL url=null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			throw Utils.IllArg(Utils.class, e.getMessage());
//			Utils.prtObErrMess(Utils.class, " readHttp: Can't find "+urlString);
//			Utils.prtObErrMess(Utils.class, e.getMessage());
//			return null;
		}

		InputStream iS=null;
		try {
			iS = url.openStream();
		} catch (IOException e) {
			throw Utils.IllArg(Utils.class, e.getMessage());
//			Utils.prtObErrMess(Utils.class, " readHttp: Can't find "+urlString);
//			Utils.prtObErrMess(Utils.class, e.getMessage());
		}
		ArrayList<String> retList = new ArrayList<String>();
		DataInputStream stream = new DataInputStream( new BufferedInputStream(iS));
		String line=null;
		try {
			while ((line = stream.readLine()) != null) {
				retList.add(line);
			}
		} catch (Exception e) {
			throw Utils.IllArg(Utils.class, e.getMessage());
//			Utils.prtObErrMess(Utils.class, " readHttp: Can't find "+urlString);
//			Utils.prtObErrMess(Utils.class, e.getMessage());
		}
		
		return retList;

	}
	
	public static File[] getFilesFromRegex(String rootDirectory,String regexExpression){
        try {
        	File dir;
        	if(rootDirectory==null){
        		dir = new File("./");
        	}else{
        		dir = new File(rootDirectory);
        	}
            
            FileFilter fileFilter = new RegexFileFilter(regexExpression);
            File[] files = dir.listFiles(fileFilter);
            return files;
        } catch (Exception e) {
            Utils.prtObErrMess(Utils.class, e.getMessage());
            return null;
        }


	}
	
	public static XStream newXStreamInstance(){
		return new XStream();
	}
	
	public static <E>List<E> flattenMap(Map<?, ?> m,Class<E> rootClass){
		List<E> ret = new ArrayList<E>();
		List<?> oList = new ArrayList(m.values());
		if(oList.size()<1)return null;
		Object o = oList.get(0);
		if(Map.class.isAssignableFrom(o.getClass())){
			for(Object inner:m.values()){
				ret.addAll(flattenMap((Map<?, ?>)inner,rootClass));
			}
		}else{
			for(Object v:m.values()){
				if(rootClass.isAssignableFrom(v.getClass())){
					E e = (E)v;
					ret.add(e);
				}
			}
		}
		return ret;
	}
	
	
	@SuppressWarnings("rawtypes")
	public static void treeMapNDeepAdd(TreeMap mapToUpdate,Object valueToUpdate,String ... keyProperiesOfValueToUpdate){
		Object[] keys = new Object[keyProperiesOfValueToUpdate.length];
		for(int i = 0;i<keyProperiesOfValueToUpdate.length;i++){
			keys[i] = Reflection.getFieldByFieldName(valueToUpdate, keyProperiesOfValueToUpdate[i], null);
		}
		treeMapNDeepAdd(keyProperiesOfValueToUpdate, mapToUpdate, keys);
	}
	
	/**
	 * populate any treemap, of any dimentions or depth, using the passed keys:
	 * example:

	 * @param valueToUpdate
	 * @param mapToUpdate
	 * @param keys
	 */
	@SuppressWarnings("rawtypes")
	public static void treeMapNDeepAdd(Object valueToUpdate,@SuppressWarnings("rawtypes") TreeMap mapToUpdate,Object ... keys){
		@SuppressWarnings("rawtypes")
		TreeMap currMap = mapToUpdate;
		@SuppressWarnings("rawtypes")
		ArrayList al=null;
		for(int i=0;i<keys.length;i++){
			if(!currMap.containsKey(keys[i])){
				if(i==keys.length-1){
					al = new ArrayList();
					currMap.put(keys[i], al);
				}else{
					currMap.put(keys[i], new TreeMap());
				}
			}
			if(i==keys.length-1){
				al = (ArrayList)currMap.get(keys[i]);
			}else{
				currMap = (TreeMap)currMap.get(keys[i]);
			}
	
			
		}
		al.add(valueToUpdate);
		
		
	}
	
	public static  <T,C>  T getXmlDataFromResource(Class<T> returnClass,
			Class<C> classOfPackage, String xmlDataPath){
		String path = xmlDataPath;
		if(classOfPackage!=null){
			path = getPathOfResource(classOfPackage, xmlDataPath);
		}
		return getXmlData(returnClass,null,path);
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * This will process annotations in the class specified by
	 *   classOfPackage.
	 * @param returnClass
	 * @param classOfPackage
	 * @param xmlDataPath  - Full Path of xml data 
	 * 		THIS IS NOT THE FILENAME ONLY AS IN OTHER GET DATA METHODS
	 * 			WHICH SPECIFY A CLASS, AND FIND THE FILE AS A RESOURCE.
	 * 
	 * 		FOR THAT, SEE the method getXmlData.
	 * @return
	 */
	public static  <T,C>  T getFromXml(Class<T> returnClass,
			Class<C> classOfPackage, String xmlDataPath){
		
		return getXmlData(returnClass, classOfPackage, xmlDataPath);
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Get xml data from either a resource file, or a regular file with
	 * 		the full path of the file specified int the xmlDataPath argument
	 * @param returnClass
	 * @param classInPackageWhereFileIsLocated - any class in the same package
	 * 		as where the file (as a resource is located)  If this is null,
	 * 		then the normal file lookup will occur.
	 * @param xmlDataPath - if classInPackageWhereFileIsLocated is not null,
	 * 		then just the file name with no other path info, unless the file is
	 * 		in a subfolder in the package of theclassInPackageWhereFileIsLocated.
	 * 		Otherwise, a full file path.
	 * @return T
	 */
	public static  <T,C>  T getXmlData(
			Class<T> returnClass,
			Class<C> classInPackageWhereFileIsLocated, 
			String xmlDataPath){
		
		XStream xs = new XStream();
		InputStream is;
		if(classInPackageWhereFileIsLocated!=null){
			is = getInputStreamAsResource(classInPackageWhereFileIsLocated, xmlDataPath);
		}else{
			try {
				is = new FileInputStream(new File(xmlDataPath));
			} catch (FileNotFoundException e) {
				throw IllState(Utils.class, e.getMessage());
			}
		}
		//read the file 
		Object o = xs.fromXML(is);
		if(returnClass.isAssignableFrom(o.getClass())){
			return (T)o;
		}else{
			return null;
		}

	}

	
	public static void writeToXml(Object o, String filePath) throws IOException{
		XStream xs = new XStream();
		FileWriter fw = new FileWriter(new File(filePath));
		xs.toXML(o, fw);
	}

	
	public static void deleteFile(String fileName){
	    File f = new File(fileName);

	    // Make sure the file or directory exists and isn't write protected
	    if (!f.exists())
	      throw new IllegalArgumentException(new FileNotFoundException(
	          "Delete: no such file or directory: " + fileName));

	    if (!f.canWrite())
	      throw new IllegalArgumentException("Delete: write protected: "
	          + fileName);

	    // If it is a directory, make sure it is empty
	    if (f.isDirectory()) {
	      String[] files = f.list();
	      if (files.length > 0)
	        throw new IllegalArgumentException(
	            "Delete: directory not empty: " + fileName);
	    }

	    // Attempt to delete it
	    boolean success = f.delete();

	    if (!success)
	      throw new IllegalArgumentException("Delete: deletion failed");
	 }

	public static <E>List<E> fromArray( E ... eArray){
		ArrayList<E> ret = new ArrayList<E>();
		for(E e:eArray ){
			ret.add(e);
		}
		return ret;
	}

	public static <E> List<E> findClosest(Collection<E> values, E value){
		TreeSet<E> eSet = new TreeSet<E>(values);
		E closestUp = eSet.headSet(value)==null?null:eSet.headSet(value).first();
		E closestDown = eSet.tailSet(value)==null?null:eSet.tailSet(value).last();
		List<E> ret = new ArrayList<E>();
		if(closestUp!=null)ret.add(closestUp);
		if(closestDown!=null)ret.add(closestDown);
		return ret;
	}
	
	public static List<String> httpPost(
			String urlString,
			String postPath,
			Map<String,String> keyValuePairs){
	    List<String> ret = new ArrayList<String>();
		try {
			// first strip away any ending "/" character from urlString
			//   and any beginning "/" from postPath
			String firstPart;
			if(urlString.substring(urlString.length()-1).compareTo("/")==0){
				firstPart = urlString.substring(0,urlString.length()-1);
			}else{
				firstPart = urlString;
			}
			String secondPart;
			if(postPath.substring(0).compareTo("/")==0){
				secondPart = postPath.substring(1,postPath.length()-1);
			}else{
				secondPart = postPath.substring(0,postPath.length()-1);
			}
			String completePath = firstPart+"/"+secondPart;
			URL url = new URL(completePath);
		    // Construct data
			String data=null;
			for(Entry<String, String> entry:keyValuePairs.entrySet()){
				String key = entry.getKey();
				String value = entry.getValue();
				if(data==null){
				    data = URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
				}else{
				    data += "&" + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
				}
			}

		    // Send data
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data);
		    wr.flush();

		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    while ((line = rd.readLine()) != null) {
		        ret.add(line);
		    }
		    wr.close();
		    rd.close();
		} catch (Exception e) {
			return null;
		}	
		return ret;
	}
	
	public static <T> T getSerializedData(Class<T> classOfResult,String serialFilePath){
		T data =null;
		try {
			// opening a flow Input of the file filename
			FileInputStream flinpstr = new FileInputStream(serialFilePath);
			// creation a "Flow object " with the flow file
			ObjectInputStream objinstr= new ObjectInputStream(flinpstr);
			try {	
				// deserialization : reading of subject since the flow Input
				data = (T) objinstr.readObject(); 
				if(!classOfResult.isAssignableFrom(data.getClass())){
					throw IllState(Utils.class, "data from file "+serialFilePath+ " of type "+data.getClass().getName()+" is not assignable from class "+classOfResult);
				}
			} finally {
				// we farm the flow
				try {
					objinstr.close();
				} finally {
					flinpstr.close();
				}
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		
		return data;
	}
	
	public static String padRight(String s, int n) {
	     return String.format("%1$-" + n + "s", s);  
	}

	public static String padLeft(String s, int n) {
	    return String.format("%1$#" + n + "s", s);  
	}

	public static void runShScript(String shScriptPath){
		List<String> lines = Utils.getLineData(null, shScriptPath);
		String cmd="";
		for(String line:lines){
			cmd = cmd+line;
		}
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

	}
	
	public static String convertStreamToString(java.io.InputStream is) {
	    try {
	        return new java.util.Scanner(is).useDelimiter("\\A").next();
	    } catch (java.util.NoSuchElementException e) {
	        return "";
	    }
	}
	
	public static double[] bdListToDoubleArray(List<BigDecimal> bdList){
		double[] ret = new double[bdList.size()];
		for(int i = 0;i<bdList.size();i++){
			ret[i] = bdList.get(i).doubleValue();
		}
		return ret;
	}

	public static double[] doubleObjectifiedToDoubleArray(Double[] bigDoubleArray){
		double[] ret = new double[bigDoubleArray.length];
		for(int i = 0;i<bigDoubleArray.length;i++){
			ret[i] = bigDoubleArray[i];
		}
		return ret;
	}
	
	public static boolean xOr(boolean x, boolean y) {
	    return ( ( x || y ) && ! ( x && y ) );
	}
	
	
	
	/**
	 * 
	 * @param classOfReturn - class of object in a bean that  you are returning
	 * @param beanXmlspath - if classInPkgOfBeanXmlAsResource ==null, then the full path
	 * 			to the beanXml file.  If classInPkgOfBeanXmlAsResource!=null, then just
	 * 			the file name of the beanXml file.
	 * @param classInPkgOfBeanXmlAsResource - class name (e.g. com.billybyte.myclass) which
	 * 		is in the same package as the beanXml file.
	 * @param beanName - name of the bean that you want to return
	 * @return
	 */
	public static <T> T springGetBean(
			Class<T> classOfReturn,
			String beanXmlspath,
			String classNameOfClassInPkgOfBeanXmlAsResource,
			String beanName){
		
		ApplicationContext context = springApplicationContext(beanXmlspath, classNameOfClassInPkgOfBeanXmlAsResource);
		Object o = context.getBean(beanName);
		return classOfReturn.cast(o);
	}

	public static <T> T springGetBean(
			Class<T> classOfReturn,
			String beansXmlpath,
			String beanName){
		
		ApplicationContext context = new  FileSystemXmlApplicationContext(beansXmlpath);

		Object o = context.getBean(beanName);
		return classOfReturn.cast(o);
	}

	public static <T> T springGetBean(
			Class<T> classOfReturn,
			String beanXmlspath,
			Class<?> classInPkgOfBeanXmlAsResource,
			String beanName){
		
		ApplicationContext context = springApplicationContext(beanXmlspath, classInPkgOfBeanXmlAsResource);
		Object o = context.getBean(beanName);
		return classOfReturn.cast(o);
	}

	
	public static Map<String, Object> springGetAllBeans(
			String beansXmlpath,
			String classNameOfClassInPkgOfBeanXmlAsResource){
		if(classNameOfClassInPkgOfBeanXmlAsResource==null){
			return springGetAllBeans(beansXmlpath);
		}
		ApplicationContext context = 
				springApplicationContext(beansXmlpath, 
						classNameOfClassInPkgOfBeanXmlAsResource);
		Map<String, Object> ret = new HashMap<String, Object>();
		for(String beanName : context.getBeanDefinitionNames()){
			ret.put(beanName, context.getBean(beanName));
		}
		return ret;
	}

	public static Map<String, Object> springGetAllBeans(
			String beansXmlpath){
		ApplicationContext context = 
				springApplicationContext(beansXmlpath);
		Map<String, Object> ret = new HashMap<String, Object>();
		for(String beanName : context.getBeanDefinitionNames()){
			ret.put(beanName, context.getBean(beanName));
		}
		return ret;
	
	}
	
	
	public static Map<String, Object> springGetAllBeans(
			String beansXmlpath,
			Class<?> classInPkgOfBeanXmlAsResource){
		if(classInPkgOfBeanXmlAsResource==null){
			return springGetAllBeans(beansXmlpath);
		}

		ApplicationContext context = 
				springApplicationContext(beansXmlpath, 
						classInPkgOfBeanXmlAsResource);
		Map<String, Object> ret = new HashMap<String, Object>();
		for(String beanName : context.getBeanDefinitionNames()){
			ret.put(beanName, context.getBean(beanName));
		}
		return ret;
	}

	public static ApplicationContext springApplicationContext(
			String beansXmlpath){
		Utils.prtObMess(Utils.class, "beanspath = "+beansXmlpath);
		ApplicationContext context = new  FileSystemXmlApplicationContext(beansXmlpath);
		return context;
		
	}
	
	public static ApplicationContext springApplicationContext(
			String beansXmlpath,
			String classNameOfClassInPkgOfBeanXmlAsResource){
		if(classNameOfClassInPkgOfBeanXmlAsResource==null){
			ApplicationContext context = new  FileSystemXmlApplicationContext(beansXmlpath);
			return context;
		}
		Class<?> classInPkgOfBeanXmlAsResource;
		try {
			classInPkgOfBeanXmlAsResource = Class.forName(classNameOfClassInPkgOfBeanXmlAsResource);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		return springApplicationContext(beansXmlpath,classInPkgOfBeanXmlAsResource);
//		String path = classInPkgOfBeanXmlAsResource.getPackage().getName().replace(".","/")+"/"+ beanXmlspath;
//		ApplicationContext context = new  ClassPathXmlApplicationContext(
//				path);
//		return context;
	}

	public static ApplicationContext springApplicationContext(
			String beansXmlpath,
			Class<?> classInPkgOfBeanXmlAsResource){
		if(classInPkgOfBeanXmlAsResource==null){
			return springApplicationContext(beansXmlpath);
		}
		String path = classInPkgOfBeanXmlAsResource.getPackage().getName().replace(".","/")+"/"+ beansXmlpath;
		ApplicationContext context = new  ClassPathXmlApplicationContext(
				path);
		return context;
	}

	public static String stackTraceAsString(Throwable e){
	    final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    e.printStackTrace(printWriter);
	    String s =  result.toString();
	    return s;
	}
	
	public static String stackTraceGenerate(){
		StackTraceElement[] steArr = Thread.currentThread().getStackTrace();
		String ret = "";
		for(StackTraceElement ste : steArr){
			ret += ste.toString()+"\n";
		}
		return ret;
	}
	
	/**
	 * Get pairs of args from an arg line that looks like the following:
	 *    "p11  p12" "p21  p22"  "p31 p32"
	 * @param args String[]
	 * @return
	 */
	public static Map<String, String> getArgPairsSeparatedBySpaces(String[] args){
		String p = "(\\S|(?<=\\\\) )+";
		Map<String, String> ret = new HashMap<String, String>();
		for(String arg:args){
			// list of pairs of args that are separated by separatorString
			List<String> pair = RegexMethods.getRegexMatches(p, arg);
			ret.put(pair.get(0),pair.get(1));
		}
		return ret;
	}

	public static Map<String,String> getArgPairsSeparatedByChar(String[] args,String separator){
		Map<String, String> argPairs = new HashMap<String, String>();
		if(args!=null){
			// find pairs separated by the = sign
			for(String argPair : args){
				String[] pair = argPair.split("=");
				if(pair.length>1){
					argPairs.put(pair[0],pair[1]);
				}
			}
		}
		return argPairs;
	}
	
	/**
	 * Download and expand a zip file from zipfilePath into directory unZipFolderPath
	 *   and return all the names of the unzipped files in a List<String>
	 * @param zipfilePath
	 * @param unZipFolderPath
	 * @return
	 */
	public static List<String> getZipFile(String zipfilePath,String unZipFolderPath){
		List<String> ret = new ArrayList<String>();
		// use a calendar to create a unique temp file name
		Calendar c = Calendar.getInstance();
		String tempZipFileName = "temp"+c.get(Calendar.YEAR)+(c.get(Calendar.MONTH)+1)+
				c.get(Calendar.DAY_OF_MONTH)+"_"+c.get(Calendar.HOUR_OF_DAY)+
				c.get(Calendar.MINUTE)+c.get(Calendar.SECOND)+c.get(Calendar.MILLISECOND)+".zip";
		try {
			// get the file as inputstream
			InputStream in = zipfilePath.contains("http") || zipfilePath.contains("ftp") ?
					getInputStreamFromHttpFtp(zipfilePath)  :
						new FileInputStream(zipfilePath);
	        // create a full temp zipfile name
			String tempZipFilePath = addSlash(unZipFolderPath)+tempZipFileName;
			// copy the zip file to the temp file
			Utils.copyFile(in,
					tempZipFilePath);
			// create  ZipFile
			ZipFile zp = new ZipFile(tempZipFilePath);
			Enumeration<? extends ZipEntry> enumer = zp.entries();
			while(enumer.hasMoreElements()){
				ZipEntry entry = enumer.nextElement();
				if(entry.isDirectory()) {
		           // Assume directories are stored parents first then children.
		           System.err.println("Extracting directory: " + entry.getName());
		           // This is not robust, just for demonstration purposes.
		           (new File(entry.getName())).mkdir();
		           continue;
		         }
				// this copy unzips the file and puts it in unZipFolderPath
				String unZippedFileDest = addSlash(unZipFolderPath)+entry.getName();
				Utils.copyFile(zp.getInputStream(entry),
		        		 addSlash(unZippedFileDest));
		         // save file name of unzipped file
		         ret.add(unZippedFileDest);  
			}
			
		} catch (IOException e) {
			throw Utils.IllState(e);
		}
		return ret;
	}
	private static String addSlash(String folder){
		String ret = folder;
		String endChar = folder.substring(folder.length()-1,folder.length()); 
		if(endChar.compareTo("/")!=0){
			ret = ret+"/";
		}
		return ret;
	}
	
}
