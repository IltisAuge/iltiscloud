<%@page import="java.net.InetSocketAddress"%>
<%@page import="de.iltisauge.iltiscloud.api.utils.SocketAddressConverter"%>
<%@page import="javax.print.attribute.standard.Severity"%>
<%@page import="de.iltisauge.iltiscloud.common.server.TemplateServer"%>
<%@page import="de.iltisauge.iltiscloud.api.server.ServerType"%>
<%@page import="de.iltisauge.iltiscloud.panel.IltisCloud"%>
<%@page import="de.iltisauge.iltiscloud.api.server.IServer"%>
<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
if (request.getSession().getAttribute("uniqueId") == null) { %>
	<script type="text/javascript">
		window.location.href="login.jsp";
	</script>
<%
	return;
}
final String serverIdString = request.getParameter("serverId");
final UUID serverId = serverIdString == null ? null : UUID.fromString(serverIdString);
final IServer server = serverId == null ? null : IltisCloud.getAPI().getServerManager().getServer(serverId);
%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<link rel="stylesheet" href="compiled/css/server.css">
        <script src="https://code.jquery.com/jquery-3.6.0.js" integrity="sha256-H+K7U5CnXl1h5ywQfKtSj8PCmoN9aaq30gDh27Xc0jk=" crossorigin="anonymous"></script>
        <script src="js/tools.js"></script>
        <script src="js/translations.js"></script>
		<script src="js/ClientToMaster.js"></script>
		<script src="js/ClientToServer.js"></script>
        <script src="js/pageLoad.js"></script>
	</head>
	<body>
        <div id="page-header" style="opacity: 0;"></div>
        <div id="page-body" style="opacity: 0;">
            <div class="col-md-6">
                <div class="page-title">
                    <p class="page-headline" id="server-headline"></p>
                    <hr>
                </div>
                <%
                if (server != null) { %>
	            	<div style="display: flex;">
	                    <div class="server-info-container">
	                        <div><span class="word-name"></span>: <%= server.getName() %></div>
	                        <%
	                        boolean b = server instanceof TemplateServer;
	                        System.out.println("isTemplateInstance:" + b);
	                        TemplateServer ts = b ? (TemplateServer) server : null;
	                        String s = server.getServerType().equals(ServerType.TEMPLATE) && ts != null ? (server.getServerType().name() + "(" + ts.getTemplateName() + ")") : server.getServerType().name();
	                        %>
	                        <div><span class="word-type"></span>: <%= s %></div>
	                        <div><span class="word-uniqueid"></span>: <%= server.getServerId().toString() %></div>
	                        <div><span class="word-address"></span>: <%= server.getAddress().toString() %></div>
	                        <div><span class="word-wrapper"></span>: <%= server.getWrapper().getName() %></div>
	                        <div><span class="word-status"></span>: <span class='text-<%= server.isRunning() ? "success" : "danger"%> word-<%= server.isRunning() ? "online" : "offline" %>'></span></div>
	                        <div><span class="word-players"></span>: -</div>
	                        <div><span class="word-uptime"></span>: 
	                        	<script type="text/javascript">
	                        		var v = <%= server.getStartedUpAt() %>
	                        		if (v == null) {
	                        			document.write("-");
	                        		} else {
		                       			var timeString = msToTime(new Date().getTime() - v.getTime());
		                       			document.write(timeString);
	                        		}
	                        	</script>
	                        </div>
	                        <div class="server-actions-container d-flex flex-column" style="gap: 10px;">
	                            <a class="btn btn-success word-edit" href="editserver?serverId=<%= serverIdString %>"></a>
	                            <button class="btn btn-success word-start" onclick="startServer()"></button>
	                            <button class="btn btn-warning word-stop" onclick="stopServer()"></button>
	                            <button class="btn btn-danger word-kill" onclick="killServer()"></button>
	                        </div>
						</div>
	                    <div class="console-container d-flex flex-column">
	                        <div class="console-text-container" id="console-text-container">
	                        	<canvas id="console-canvas">
	                        	</canvas>
	                        </div>
	                        <div class="command-line-container">
	                            <form id="execute-cmd-form" method="post">
	                                <div class="inner-form-container d-flex">
	                                    <div>
	                                        <input class="ipt cmd-input" id="cmd-input" type="text" value="test">
	                                    </div>
	                                    <div>
	                                        <input class="submit-cmd-btn btn-success" id="submit-cmd-bnt" type="submit">
	                                    </div>
	                                </div>
	                            </form>
	                        </div>
	                    </div>
	                </div>
                <%
                }
                %>
            </div>
        </div>
	    <script src="js/Console.js"></script>
        <script type="text/javascript">
            $(document).ready(function() {
                setTimeout(function() {
                    <%
                    final String serverName = server == null ? "" : server.getName();
                    %>
                    document.title = getTranslation("page.server.title" + (<%= server == null %> ? ".notfound" : ""), '<%= serverName %>');
                    $('#server-headline').html(getTranslation("page.server.headline" + (<%= server == null %> ? ".notfound" : ""), '<%= serverName %>'));
                    $('#cmd-input').attr("placeholder", getTranslation("console.cmdinput.placeholder"));
                    $('#submit-cmd-btn').attr("value", getTranslation("console.submitbtn.value"));
                }, 100);
                <%
                if (server != null) {
	                final InetSocketAddress address = SocketAddressConverter.convertToInet(server.getAddress());
	                %>
	                console.log("connecting to server...");
	                connectToServer('192.168.2.105', <%= address.getPort() + 1 %>);

	                initConsole();
	                
	                var _onMessageFromServer = onMessageFromServer;
	                onMessageFromServer = function(event) {
	                	_onMessageFromServer(event);
	                	addCanvasText(event.data);
	                };
	            <%
                }
	            %>
	            
	            function startServer() {
	            	if (<%= server != null %>) {
	            		<%
	            		server.startUp();
	            		%>
	            	}
	            }
	            
	            function stopServer() {
	            	if (<%= server != null %>) {
	            		<%
	            		server.shutDown();
	            		%>
	            	}
	            }
	            
	            function killServer() {
	            	if (<%= server != null %>) {
	            		<%
	            		server.kill();
	            		%>
	            	}
	            }
	            
                var form = $('#execute-cmd-form');
                form.submit(function(e) {
                   	e.preventDefault();
    	           	 let serialized = { };
    	               form.serializeArray().map(function(v) {
    	                   serialized[v.name] = v.value;
    	               });
                   	console.log(serialized);
                   	var cmd = $('#cmd-input').val();
                   	$.post("executeServerCommand.jsp?serverId=<%= serverIdString %>&command=" + cmd);
                });
            });
        </script>
	</body>
</html>
