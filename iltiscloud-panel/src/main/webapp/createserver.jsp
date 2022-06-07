<%@page import="de.iltisauge.iltiscloud.api.server.ServerPlatform"%>
<%@page import="javax.print.attribute.standard.Severity"%>
<%@page import="de.iltisauge.iltiscloud.api.server.ServerType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
if (request.getSession().getAttribute("uniqueId") == null) { %>
	<script type="text/javascript">
		window.location.href="login.jsp";
	</script>
<%
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
                    <p class="page-headline" id="createserver-headline"></p>
                    <hr>
                </div>
                <form id="create-server-form" target="editserver.jsp?action=create" method="post">
                    <div class="attributes-container" style="display: flex; flex-direction: column;">
                        <div>
                            <span class="word-name"></span>: <input class="ipt" id="name-input" type="text" name="name">
                        </div>
                        <div>
                            <span class="word-type"></span>:
                            <select id="servertype-select" class="ipt" name="server-type">
	                            <%
	                            for (ServerType type : ServerType.values()) {
	                            %>
		                            <script type="text/javascript">
		                                var element = $('<option value=<%= type.name() %> selected=selected><%= type.name() %></option>');
		                                $('#servertype-select').append(element);
	                                </script>
		                        <%
	                            }
	                            %>
                            </select>
                        </div>
                        <div>
                            <span class="word-platform"></span>:
                            <select id="serverplatform-select" class="ipt" name="server-platform">
                            <%
	                            for (ServerPlatform platform : ServerPlatform.values()) {
	                            %>
		                            <script type="text/javascript">
		                                var element = $('<option value=<%= platform.name() %> selected=selected><%= platform.name() %></option>');
		                                $('#serverplatform-select').append(element);
	                                </script>
		                        <%
	                            }
	                            %>
                            </select>
                        </div>
                         <div>
                            <span class="word-ipaddress"></span>: <input class="ipt" id="ipaddress-input" type="text" name="ip-address">
                        </div>
                         <div>
                            <span class="word-port"></span>: <input class="ipt" id="port-input" type="number" min="0" max="65535" name="port">
                        </div>
                         <div>
                            <span class="word-maxram"></span>: <input class="ipt" id="max-ram-input" type="number" min="0" name="max-ram">
                        </div>
                        <div>
                            <span class="word-wrapper"></span>:
                            <select class="ipt" name="wrapper-id">
                                <option value="37e40b64-c460-4f4a-9e7a-a39fa949815a" selected="selected">Wrapper-01</option>
                                <option value="e9e43c41-fb00-4e75-b2a1-c7cafe34baa1">Wrapper-02</option>
                            </select>
                        </div>
                        <div class="d-flex" style="justify-content: flex-end;">
                            <input class="btn btn-success" id="submit-btn" type="submit">
                        </div>
                    </div>
                </form>
            </div>
        </div>
        <script type="text/javascript">
            $(document).ready(function() {
                setTimeout(function() {
                    document.title = getTranslation("page.createserver.title");
                    $('#createserver-headline').html(getTranslation("page.createserver.headline"));
                    $('#name-input').attr("placeholder", getTranslation("word.name"));
                    $('#ipaddress-input').attr("placeholder", getTranslation("word.ipaddress"));
                    $('#port-input').attr("placeholder", getTranslation("word.port"));
                    $('#max-ram-input').attr("placeholder", getTranslation("word.maxram"));
                    $('#submit-btn').attr("value", getTranslation("page.createserver.submitbtn.value"));
                }, 100);
                var create_server_form = $('#create-server-form');
                create_server_form.submit(function(e) {
                	e.preventDefault();
                	let serialized = { };
                	create_server_form.serializeArray().map(function(v) {
                        serialized[v.name] = v.value;
                    });
                	serialized["serverId"] = randomUUID();
                	console.log(serialized);
                	$.post("editserver.jsp?action=create", serialized);
                	setInterval(function() {
                        window.location.href = "servers.jsp";
                	}, 100);
                });
            });
        </script>
	</body>
</html>
