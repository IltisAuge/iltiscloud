<%@page import="de.iltisauge.iltiscloud.api.server.IServer"%>
<%@page import="de.iltisauge.iltiscloud.panel.IltisCloud"%>
<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="utf-8"%>

<%
final String serverIdString = request.getParameter("serverId");
final UUID serverId = serverIdString == null ? null : UUID.fromString(serverIdString);
final String command = request.getParameter("command");
final IServer server = serverId == null ? null : IltisCloud.getAPI().getServerManager().getServer(serverId);
System.out.println("Execute command '" + command + "' on " + (server == null ? "null" : server.getName()));
if (command == null || server == null) {
	System.out.println("return");
	return;
}
System.out.println("Execute command '" + command + "' on " + server.getName());
server.executeCommand(command);
%>