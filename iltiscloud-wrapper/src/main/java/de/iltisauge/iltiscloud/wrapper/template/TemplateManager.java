package de.iltisauge.iltiscloud.wrapper.template;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.zeroturnaround.zip.ZipUtil;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import de.iltisauge.iltiscloud.api.IltisCloud;
import de.iltisauge.iltiscloud.api.logging.ConsoleLoggingFormatter;
import de.iltisauge.iltiscloud.api.logging.LoggingUtil;
import de.iltisauge.iltiscloud.api.utils.Util;

public class TemplateManager {

	private static int CONNECTION_TIMEOUT_MS = 2 * 1000;
	private final Map<String, File> templates = new HashMap<String, File>();
	
	public void loadTemplateFile() {
		final File templateFile = new File("templates.json");
		if (!templateFile.exists()) {
			try {
				templateFile.createNewFile();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		final BasicDBObject fileObject = BasicDBObject.parse(Util.readContentFromFile(templateFile));
		final BasicDBList templatesList = (BasicDBList) fileObject.get("templates");
		for (Object o : templatesList) {
			templates.put((String) o, null);
		}
	}
	
	public void downloadTemplates() {
		final List<String> copy = new ArrayList<String>(templates.keySet());
		int successes = 0;
		for (String name : copy) {
			final File template = downloadTemplate(name);
			if (template != null) {
				successes++;
			}
			templates.put(name, template);
		}
		IltisCloud.getLogger().log(Level.INFO, "Downloaded " + successes + " templates.");
	}

	public File downloadTemplate(String templateName) {
		try {
			IltisCloud.getLogger().log(Level.INFO, "Trying to download template '" + templateName + "'...");
			HttpResponse response = getResponse("/download", templateName);
			if (response == null) {
				IltisCloud.getLogger().log(Level.WARNING, "Template '" + templateName + "' was not found on CloudMaster.");
				return null;
			}
			final File templatesDir = new File(".", "templates");
			templatesDir.mkdir();
			final File templateDir = new File(templatesDir, templateName);
			if (templateDir.exists()) {
				IltisCloud.getLogger().log(Level.WARNING, "Deleting existing local template '" + templateName + "'...");
				FileUtils.deleteDirectory(templateDir);
			}
			templateDir.mkdir();
			IltisCloud.getLogger().log(Level.INFO, "Template '" + templateName + "' was found on CloudMaster. Installing...");
			File downloaded = new File(templateDir, templateName);
			final InputStream inputStream = response.getEntity().getContent();
			Files.copy(inputStream, downloaded.toPath(), StandardCopyOption.REPLACE_EXISTING);
			downloaded.renameTo(new File(downloaded.getParentFile(), templateName + ".zip"));
			downloaded = new File(templateDir, templateName + ".zip"); // reload
			ZipUtil.unpack(downloaded, templateDir);
			Files.delete(downloaded.toPath());
			IltisCloud.getLogger().log(Level.INFO, "Downloaded and installed template '" + templateName + "'.");
			return templateDir;
		} catch (IOException exception) {
			exception.printStackTrace();
			return null;
		}
	}
	
	public void uploadTemplate(String templateName) {
		try {
			final File templateDir = new File("templates", templateName);
			if (!templateDir.exists()) {
				IltisCloud.getLogger().log(Level.WARNING, "Template does not exist on this wrapper!");
				return;
			}
			final File tempZipFilesDir = new File(new File("."), "tempZipFiles");
			tempZipFilesDir.mkdir();
			final File zipLocation = new File(tempZipFilesDir, templateName);
			ZipUtil.pack(templateDir, zipLocation);
			final byte[] templateBytes = FileUtils.readFileToByteArray(zipLocation);
			final HttpPost httpPost = new HttpPost("/upload");
			final RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
					.setConnectTimeout(CONNECTION_TIMEOUT_MS).setSocketTimeout(CONNECTION_TIMEOUT_MS).build();
			httpPost.setConfig(requestConfig);
			httpPost.setHeader("templateName", templateName);
			httpPost.setEntity(new ByteArrayEntity(templateBytes));
			final HttpClient client = buildHttpClient();
			HttpResponse response = null;
			try {
				IltisCloud.getLogger().log(Level.INFO, "Uploading template '" + templateName + "'...");
				response = client.execute(new HttpHost("127.0.0.1", 8000), httpPost);
			} catch (SocketTimeoutException exception) {
				// No response
				IltisCloud.getLogger().log(Level.WARNING, "No response from HttpServer.");
				return;
			}
			final InputStream inputStream = response.getEntity().getContent();
			final byte[] responseBytes = inputStream.readAllBytes();
			final String responseMessage = new String(responseBytes);
			IltisCloud.getLogger().log(Level.INFO, "Response: " + responseMessage);
		} catch (IOException exception) {
			exception.printStackTrace();
			return;
		}
	}
	
	public File getTemplate(String name) {
		return templates.get(name);
	}

	public static void main(String[] args) throws IOException {
		IltisCloud.setLogger(Logger.getLogger("a"));
		LoggingUtil.setFormatter(new ConsoleLoggingFormatter(), IltisCloud.getLogger());
		final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		System.out.println("Type 'download <template>' to download a template.");
		System.out.println("Type 'upload <template>' to upload a template.");
		final TemplateManager templateDownloader = new TemplateManager();
		while ((line = reader.readLine()) != null) {
			if (line.equals("stop")) {
				System.exit(0);
				return;
			}
			final String[] split = line.split(" ");
			if (split[0].equalsIgnoreCase("download")) {
				final String templateName = split[1];
				templateDownloader.downloadTemplate(templateName);
			} else if (split[0].equalsIgnoreCase("upload")) {
				final String templateName = split[1];
				templateDownloader.uploadTemplate(templateName);
			}
		}
	}

	private static HttpResponse getResponse(String uri, String templateName) throws ClientProtocolException, IOException {
		final HttpPost httpPost = new HttpPost(uri);
		final RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
				.setConnectTimeout(CONNECTION_TIMEOUT_MS).setSocketTimeout(CONNECTION_TIMEOUT_MS).build();
		httpPost.setConfig(requestConfig);
		httpPost.setHeader("templateName", templateName);
		final HttpClient client = buildHttpClient();
		HttpResponse response = null;
		try {
			response = client.execute(new HttpHost("127.0.0.1", 8000), httpPost);
		} catch (SocketTimeoutException exception) {
			// No response
		}
		return response;
	}
	
	private static HttpClient buildHttpClient() {
		final HttpClientBuilder builder = HttpClientBuilder.create();
		builder.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
		return builder.build();
	}
}
