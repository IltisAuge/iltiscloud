<%@page import="java.util.List"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.UUID"%>
<%@page import="com.mongodb.BasicDBObject"%>
<%@page import="de.iltisauge.iltiscloud.api.server.ITemplateServer"%>
<%@page import="de.iltisauge.iltiscloud.api.server.ServerType"%>
<%@page import="de.iltisauge.iltiscloud.panel.IltisCloud"%>
<%@page import="de.iltisauge.iltiscloud.api.server.IServer"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
if (request.getSession().getAttribute("uniqueId") == null) { %>
	<script type="text/javascript">
		window.location.href="login.jsp";
	</script>
<%
}
String action = null;
if ((action = request.getParameter("action")) != null && action.equals("get")) {
	final String serverIdString = request.getParameter("serverId");
	if (serverIdString == null) {
    	final List<IServer> servers = IltisCloud.getAPI().getServerManager().getServers();
    	Collections.sort(servers, new Comparator<IServer>() {
    		
    		public int compare(IServer o1, IServer o2) {
    			return o1.getName().compareTo(o2.getName());
    		}
    	});
    	for (IServer server : servers) {
    		System.out.println("server=" + server.getName()); %>
        	<tr id="<%= server.getServerId().toString() %>">
                <td><%= server.getName() %></td>
                <td><%= server.getServerType().equals(ServerType.TEMPLATE) && server instanceof ITemplateServer ? (ServerType.TEMPLATE.name() + ((ITemplateServer) server).getTemplateName()) : ServerType.STATIC.name() %></td>
                <td><%= server.getServerId().toString() %></td>
                <td><%= server.getAddress().toString() %></td>
                <td><%= server.getWrapper().getName() %></td>
                <td class="text-<%= server.isRunning() ? "success" : "danger"%> word-<%= server.isRunning() ? "online" : "offline" %>"></td>
                <td>-</td>
                <td><a class="btn btn-success m-0 word-edit" href="server.jsp?serverId=<%= server.getServerId().toString() %>"></a></td>
            </tr>
    	<%
    	}
    	return;
	}
	final UUID serverId = UUID.fromString(serverIdString);
	final IServer server = IltisCloud.getAPI().getServerManager().getServer(serverId);
	final BasicDBObject obj = new BasicDBObject();
	obj.put("name", server.getName());
	out.print(obj.toJson());
	return;
}
%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>IltisCloud - Server</title>
		<link rel="stylesheet" href="compiled/css/wrappers.css">
        <script src="https://code.jquery.com/jquery-3.6.0.js" integrity="sha256-H+K7U5CnXl1h5ywQfKtSj8PCmoN9aaq30gDh27Xc0jk=" crossorigin="anonymous"></script>
        <script src="js/tools.js"></script>
        <script src="js/translations.js"></script>
		<script src="js/ClientToMaster.js"></script>
        <script src="js/pageLoad.js"></script>
	</head>
	<body>
        <div id="page-header" style="opacity: 0;"></div>
        <div id="page-body" style="opacity: 0;">
            <div>
                <div class="page-title">
                    <p class="page-headline" id="servers-headline"></p>
                    <hr>
                </div>
                <table class="table">
                    <thead>
                        <tr>
                            <th class="word-name"></th>
                            <th class="word-type"></th>
                            <th class="word-uniqueid"></th>
                            <th class="word-address"></th>
                            <th class="word-wrapper"></th>
                            <th class="word-status"></th>
                            <th class="word-players"></th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody id="servers-tbody">
                    	<%
                    	final List<IServer> servers = IltisCloud.getAPI().getServerManager().getServers();
                    	Collections.sort(servers, new Comparator<IServer>() {
                    		
                    		public int compare(IServer o1, IServer o2) {
                    			return o1.getName().compareTo(o2.getName());
                    		}
                    	});
                    	for (IServer server : servers) {
                    		System.out.println("server=" + server.getName()); %>
	                    	<tr id="<%= server.getServerId().toString() %>">
	                            <td><%= server.getName() %></td>
	                            <td><%= server.getServerType().equals(ServerType.TEMPLATE) && server instanceof ITemplateServer ? (ServerType.TEMPLATE.name() + ((ITemplateServer) server).getTemplateName()) : ServerType.STATIC.name() %></td>
	                            <td><%= server.getServerId().toString() %></td>
	                            <td><%= server.getAddress().toString() %></td>
	                            <td><%= server.getWrapper().getName() %></td>
	                            <td class="text-<%= server.isRunning() ? "success" : "danger"%> word-<%= server.isRunning() ? "online" : "offline" %>"></td>
	                            <td>-</td>
	                            <td><a class="btn btn-success m-0 word-edit" href="server.jsp?serverId=<%= server.getServerId().toString() %>"></a></td>
	                        </tr>
                    	<%
                    	}
                    	%>
                    </tbody>
                </table>
                <td><a class="btn btn-success" id="create-server" href="createserver.jsp"></a></td>
            </div>
        </div>
        <script type="text/javascript">
            $(document).ready(function() {
                setTimeout(function() {
                    document.title = getTranslation("page.servers.title");
                    $('#servers-headline').html(getTranslation("page.servers.headline"));
                    $('#create-server').html(getTranslation("page.servers.createserver"));
                    setTimeout(function() {
                        $('#navbar-link-servers').attr("href", "#");
                    }, 50);
                }, 100);
                var _onMessageFromMaster = onMessageFromMaster;
                onMessageFromMaster = function(event) {
                	_onMessageFromMaster(event);
                	var json = JSON.parse(event.data);
                	console.log(json);
                	if (json.action == "CREATE" || json.action == "DELETE") {
                		$.get("servers.jsp?action=get", function(data, status) {
                			//data = JSON.parse(data);
                    		console.log("data=" + data);
                    		$('#servers-tbody').empty();
                        	//var element = $("<tr id=" + json.serverId + "><td>" + data.name + "</td></tr>");
                            $('#servers-tbody').append(data);
                            var edit_translation = getTranslation("word.edit");
                            $('.word-edit').each(function(i, obj) {
                                $(obj).html(edit_translation);
                            });
                            var online_translation = getTranslation("word.online");
                            $('.word-online').each(function(i, obj) {
                                $(obj).html(online_translation);
                            });
                            var offline_translation = getTranslation("word.offline");
                            $('.word-offline').each(function(i, obj) {
                                $(obj).html(offline_translation);
                            });
                    	});
                	}
                };
            });
        </script>
	</body>
</html>
