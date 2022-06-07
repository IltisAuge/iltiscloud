package de.iltisauge.iltiscloud.wrapper.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.logging.Level;

import de.iltisauge.iltiscloud.api.IltisCloud;
import de.iltisauge.java.js.communication.java.JavaServer;
import lombok.Getter;

@Getter
public abstract class ServerProcess {

	private final File directory;
	private final File startFile;
	private Process process;
	private BufferedReader reader;
	private BufferedWriter writer;
	private JavaServer javaServer;
	
	public ServerProcess(File directory, File startFile, JavaServer javaServer) {
		this.directory = directory;
		this.startFile = startFile;
		this.javaServer = javaServer;
	}
	
	public void start() {
		final String serverName = directory.getName();
		IltisCloud.getLogger().log(Level.INFO, "Starting server " + serverName + "...");
		//final ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", directory.getName());
		final ProcessBuilder processBuilder = new ProcessBuilder("start.bat");
		processBuilder.directory(directory).redirectErrorStream(true);
		try {
			process = processBuilder.start();
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						String line;
						while ((line = reader.readLine()) != null) {
							if (line.equals(System.lineSeparator()) || line.equals("") || line.equals(" ")) {
								continue;
							}
							log(line);
						}
						reader.close();
					} catch (IOException exception) {
						exception.printStackTrace();
					}
				}
			}).start();
			process.waitFor();
			IltisCloud.getLogger().log(Level.INFO, "ServerProcesses '" + serverName + "' has been terminated.");
			onProcessStopped();
			/*new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
						String line = null;
						while ((line = reader.readLine()) != null) {
							writer.write(line + '\n');
							writer.flush();
						}
						writer.close();
					} catch (IOException exception) {
						exception.printStackTrace();
					}
				}
			}).start();*/
		} catch (IOException | InterruptedException exception) {
			exception.printStackTrace();
		}
	}
	
	public abstract void onProcessStopped();
	
	public void log(String line) {
		IltisCloud.getLogger().log(Level.INFO, "[" + directory.getName() + "] " + line);
		// Send msg to panel
		javaServer.broadcast(line);
	}

	public void write(String msg) {
		try {
			writer.write(msg);
			writer.newLine();
			writer.flush();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}
