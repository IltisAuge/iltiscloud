package de.iltisauge.iltiscloud.master.httpserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import de.iltisauge.iltiscloud.api.IltisCloud;
import de.iltisauge.iltiscloud.api.Startable;
import de.iltisauge.iltiscloud.api.logging.ConsoleLoggingFormatter;
import de.iltisauge.iltiscloud.api.logging.LoggingUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public class CloudHttpServer implements Startable {

	private final InetSocketAddress address;
	private boolean isRunning;
	@Setter
	private Date startedUpAt;
	private HttpServer httpServer;
	
	@Override
	public void startUp() {
		IltisCloud.setLogger(Logger.getLogger("CloudHttpServer"));
		LoggingUtil.setFormatter(new ConsoleLoggingFormatter(), IltisCloud.getLogger());
		IltisCloud.getLogger().log(Level.INFO, "Starting HttpServer on " + address + "...");
		try {
			httpServer = HttpServer.create(address, 0);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		httpServer.createContext("/download", new HttpHandler() {

			@Override
			public void handle(HttpExchange exchange) throws IOException {
				final Headers headers = exchange.getRequestHeaders();
				final String templateName = headers.getFirst("templateName");
				if (templateName == null) {
					return;
				}
				IltisCloud.getLogger().log(Level.INFO, "Template '" + templateName + "' has been requested.");
				exchange.setAttribute("Content-Type", "application/zip");
				final File templatesDir = new File(".", "templates");
				System.out.println("TemplatesDir=" + Arrays.asList(templatesDir.listFiles()).stream().map(x -> x.getAbsolutePath()).collect(Collectors.joining(", ")));
				templatesDir.mkdir();
				final File templateZip = new File(templatesDir, templateName + ".zip");
				if (!templateZip.exists()) {
					IltisCloud.getLogger().log(Level.WARNING, "Template '" + templateName + "' was not found.");
					return;
				}
				IltisCloud.getLogger().log(Level.INFO, "Template '" + templateName + "' was found.");
				/*final File tempZipFilesDir = new File(new File("."), "tempZipFiles");
				tempZipFilesDir.mkdir();
				final File zipLocation = new File(tempZipFilesDir, templateName);
				ZipUtil.pack(templateDir, zipLocation);*/
				final byte[] responseBytes = FileUtils.readFileToByteArray(templateZip);
				exchange.sendResponseHeaders(200, responseBytes.length);
				final OutputStream os = exchange.getResponseBody();
				os.write(responseBytes);
				IltisCloud.getLogger().log(Level.INFO, "Sending template '" + templateName + "' to requestor...");
			}
		});	
		httpServer.createContext("/upload", new HttpHandler() {

			@Override
			public void handle(HttpExchange exchange) throws IOException {
				String responseMessage = "No response";
				exchange.getResponseHeaders().set("Content-Type", "text/plain");
				try {
					final Headers headers = exchange.getRequestHeaders();
					final String templateName = headers.getFirst("templateName");
					IltisCloud.getLogger().log(Level.INFO, "Template '" + templateName + "' should be uploaded.");
					final File templatesDir = new File(".", "templates");
					templatesDir.mkdir();
					final File templateDir = new File(templatesDir, templateName);
					if (templateDir.exists()) {
						IltisCloud.getLogger().log(Level.INFO, "Deleting existing template folder '" + templateName + "'...");
						FileUtils.deleteDirectory(templateDir);
					}
					templateDir.mkdir();
					final InputStream inputStream = exchange.getRequestBody();
					File downloaded = new File(templateDir, templateName);
					Files.copy(inputStream, downloaded.toPath(), StandardCopyOption.REPLACE_EXISTING);
					downloaded.renameTo(new File(downloaded.getParentFile(), templateName + ".zip"));
					//downloaded = new File(templateDir, templateName + ".zip"); // reload
					//ZipUtil.unpack(downloaded, templateDir);
					//Files.delete(downloaded.toPath());
					IltisCloud.getLogger().log(Level.INFO, "Uploaded template '" + templateName + "'.");
				} catch (Exception exception) {
					exception.printStackTrace();
					responseMessage = ExceptionUtils.getStackTrace(exception);
					exchange.sendResponseHeaders(200, responseMessage.getBytes().length);
					exchange.getResponseBody().write(responseMessage.getBytes());
					return;
				}
				responseMessage = "Uploaded template successfully.";
				exchange.sendResponseHeaders(200, responseMessage.getBytes().length);
				final OutputStream os = exchange.getResponseBody();
				os.write(responseMessage.getBytes());
				os.close();
			}
		});
		httpServer.setExecutor(null); // creates a default executor
		httpServer.start();
		IltisCloud.getLogger().log(Level.INFO, "Started HttpServer on " + httpServer.getAddress() + ".");
	}
	
	public static void main(String[] args) {
		new CloudHttpServer(new InetSocketAddress(8000)).startUp();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void shutDown() {
		IltisCloud.getLogger().log(Level.SEVERE, "Shutting down HttpServer...");
		httpServer.stop(0);
		Thread.currentThread().stop();
		IltisCloud.getLogger().log(Level.SEVERE, "Shut down HttpServer.");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void kill() {
		IltisCloud.getLogger().log(Level.SEVERE, "Killing HttpServer...");
		Thread.currentThread().stop();
		IltisCloud.getLogger().log(Level.SEVERE, "Killed HttpServer.");
	}

	@Override
	public void setRunning(boolean value) {
		this.isRunning = value;
	}
}
