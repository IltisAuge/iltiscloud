<%@page import="de.iltisauge.iltiscloud.api.server.ServerPlatform"%>
<%@page import="de.iltisauge.iltiscloud.api.server.ServerType"%>
<%@page import="de.iltisauge.iltiscloud.api.utils.SocketAddressConverter"%>
<%@page import="java.net.InetSocketAddress"%>
<%@page import="java.util.UUID"%>
<%@page import="de.iltisauge.iltiscloud.panel.IltisCloud"%>
<%@page import="de.iltisauge.iltiscloud.api.clouduser.ICloudUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
if (request.getSession().getAttribute("uniqueId") == null) { %>
	<script type="text/javascript">
		window.location.href="login.jsp";
	</script>
<%
}
final ICloudUser user = IltisCloud.getAPI().getCloudUserManager().getCloudUser((UUID) request.getSession().getAttribute("uniqueId"));
String action = null;
if ((action = request.getParameter("action")) != null) {
	if (action.equals("create")) {
		final UUID serverId = UUID.fromString(request.getParameter("serverId"));
		final InetSocketAddress address = new InetSocketAddress(request.getParameter("ip-address"), Integer.valueOf(request.getParameter("port")));
		final String name = request.getParameter("name");
		final ServerType serverType = ServerType.valueOf(request.getParameter("server-type"));
		final ServerPlatform serverPlatform = ServerPlatform.valueOf(request.getParameter("server-platform"));
		final Integer maxRam = Integer.valueOf(request.getParameter("max-ram"));
		final UUID wrapperId = UUID.fromString(request.getParameter("wrapper-id")); %>
		
		<script type="text/javascript">
			console.log("serverId=" + <%= serverId.toString() %>);
			console.log("address=" + <%= address.toString() %>);
			console.log("name=" + <%= name %>);
			console.log("serverType=" + <%= serverType.name() %>);
			console.log("serverPlatform=" + <%= serverPlatform.name() %>);
			console.log("maxRam=" + <%= maxRam %>);
			console.log("wrapperId=" + <%= wrapperId.toString() %>);
		</script>
		<%
		IltisCloud.getAPI().getServerManager().createServer(serverId, address, name, false, serverType, serverPlatform, maxRam, wrapperId);
		return;
	}
	return;
}
%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<link rel="stylesheet" href="compiled/css/style.css">
        <script src="https://code.jquery.com/jquery-3.6.0.js" integrity="sha256-H+K7U5CnXl1h5ywQfKtSj8PCmoN9aaq30gDh27Xc0jk=" crossorigin="anonymous"></script>
        <script src="js/tools.js"></script>
        <script src="js/translations.js"></script>
		<script src="js/ClientToMaster.js"></script>
        <script src="js/pageLoad.js"></script>
	</head>
	<body>
        <div id="page-header" style="opacity: 0;"></div>
        <div id="page-body" style="opacity: 0;">
            <div class="col-md-6">
                <div class="page-title">
                    <p class="page-headline" id="editserver-headline"></p>
                    <hr>
                </div>
                <form id="edit-server-form" target="editserver.jsp?action=update" method="post">
                    <div class="attributes-container" style="display: flex; flex-direction: column;">
                        <div>
                            <span class="word-name"></span>: <input class="ipt" id="name-input" type="text" name="name">
                        </div>
                        <div>
                            <span class="word-type"></span>:
                            <select class="ipt" name="type">
                                <option class="word-static" value="static" selected="selected"></option>
                                <option value="template-lobby">Template 'lobby'</option>
                                <option value="template-skywars">Template 'skywars'</option>
                            </select>
                        </div>
                         <div>
                            <span class="word-ipaddress"></span>: <input class="ipt" id="ipaddress-input" type="text" name="ip-addresse">
                        </div>
                         <div>
                            <span class="word-port"></span>: <input class="ipt" id="port-input" type="number" min="0" max="65535" name="port">
                        </div>
                         <div>
                            <span class="word-maxram"></span>: <input class="ipt" id="max-ram-input" type="number" min="0" name="max-ram">
                        </div>
                        <div>
                            <span class="word-wrapper"></span>:
                            <select class="ipt" name="type">
                                <option value="37e40b64-c460-4f4a-9e7a-a39fa949815a" selected="selected">Wrapper-01</option>
                                <option value="e9e43c41-fb00-4e75-b2a1-c7cafe34baa1">Wrapper-02</option>
                            </select>
                        </div>
                        <div class="d-flex" style="justify-content: flex-end;">
                            <input class="btn btn-success" id="save-settings-btn" type="submit">
                        </div>
                    </div>
                </form>
            </div>
        </div>
        <script type="text/javascript">
            $(document).ready(function() {
                setTimeout(function() {
                    document.title = getTranslation("page.editserver.title", "lobby-01");
                    $('#editserver-headline').html(getTranslation("page.editserver.headline", "lobby-01"));
                    $('#name-input').attr("placeholder", getTranslation("word.name"));
                    $('#ipaddress-input').attr("placeholder", getTranslation("word.ipaddress"));
                    $('#port-input').attr("placeholder", getTranslation("word.port"));
                    $('#max-ram-input').attr("placeholder", getTranslation("word.maxram"));
                    $('#save-settings-btn').attr("value", getTranslation("word.savesettings"));
                }, 100);
                $('#edit-server-form').submit(function(e) {
                	e.preventDefault();
                })
            });
        </script>
	</body>
</html>
