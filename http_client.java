// John Esco 2019
/* Java program for HTTP client illustrating delay between Get and response */
import java.net.*; 
import java.io.*; 
import java.util.*;
  
public class http_client 
{ 
public static void main(String args[]) 
{ 
	// CL should accept one parameter URL
	if (args.length == 1) { 
		// String to store URL from CL args
		// string buffer to store key,val
		StringBuffer sbu = new StringBuffer();
		String url = args[0];
		// boolean for redirect
		boolean redirect = false;
		try {
			// Create URL object
			URL urlObject =  new URL(url);
			// obtain URLConnection object && cast to HTTPURLConnection
			HttpURLConnection httpConnect = (HttpURLConnection) urlObject.openConnection();
			// five sec timeout
			httpConnect.setReadTimeout(5000);
			// get HTTP response code
			int response = httpConnect.getResponseCode();
			// if http response code != 200
			if (response != HttpURLConnection.HTTP_OK) {
				// check for http response code 300 || 301
				if (response == HttpURLConnection.HTTP_MOVED_TEMP || 
					response == HttpURLConnection.HTTP_MOVED_PERM) {
						// set flag 
						redirect = true;
					}
			}		
			sbu.append("Printing HTTP header info from " + url + "\n");
			// if flagged get redirected URL
			if (redirect) {
				String urlRedirect = httpConnect.getHeaderField("Location");
				// open the new connection cast as Http 
				sbu.append("Redirected to: " + urlRedirect + "\n");
				httpConnect = (HttpURLConnection) new URL(urlRedirect).openConnection();	
			}	
			// map containing key,value pairs from getHeaderFields()
			Map<String, List<String>> fieldMap = httpConnect.getHeaderFields();
			// print key : val
			for (String key : fieldMap.keySet()) {
				// add key to string buffer
				sbu.append(key + ": [");
				// get list for current key
				List<String> headerVal = fieldMap.get(key);
				// iterator for current list
				Iterator<String> headerIt = headerVal.iterator();
				// append list contents
				while (headerIt.hasNext()){
					sbu.append(headerIt.next());
				}
				sbu.append("]\n");
			}
			sbu.append("\nURL Content... \n");
			// convert stringBuffer to byte array for inputStream
			byte[] headerSB = sbu.toString().getBytes();
			// input stream for parsed key,value 
			InputStream inputStreamHeader = new ByteArrayInputStream(headerSB);
			// bufferedInputStream
			BufferedInputStream readerHeader = new BufferedInputStream(inputStreamHeader);
			// input stream from http connection
			InputStream inputStreamHttp = httpConnect.getInputStream();
			// bufferedInputStream
			BufferedInputStream reader = new BufferedInputStream(inputStreamHttp);
			// bufferedOutputStream
			BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream("http_client_output"));
			// 4kb buffer
			byte[] buffer = new byte[4096];
			// set to -1 (EOF)
			int bytesRead = -1;
			// write the header input stream... 
			// reads buffer until -1 (EOF) is returned and writing
			while ((bytesRead = readerHeader.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
				output.flush();
			}
			// close header input stream
			readerHeader.close();
			// write the http connection input stream
			while ((bytesRead = reader.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
				output.flush();
			}
			// close I/O streams
			output.close();
			reader.close();
		} 
		// thrown to indicate malformed URL, no protocol or string not parsed
		catch (MalformedURLException e) {
			System.out.println("The provided URL is malformed: " + e.getMessage());
		}
		// fail or interuption of IO
		catch (IOException e) {
			System.out.println("I/O error: " +e.getMessage());
		}
		
	}
	else {
		System.out.println("http_client usage: java http_client 'URL'");
	}
} 

}
