<%@page import="java.util.UUID"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.List"%>
<%@page import="de.iltisauge.iltiscloud.api.permission.IGroup"%>
<%@page import="de.iltisauge.iltiscloud.api.clouduser.ICloudUser"%>
<%@page import="de.iltisauge.iltiscloud.panel.IltisCloud"%>
<%@page import="de.iltisauge.iltiscloud.api.utils.HashUtil"%>
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
final ICloudUser user = IltisCloud.getAPI().getCloudUserManager().getCloudUser((UUID) request.getSession().getAttribute("uniqueId"));
String action = null;
if ((action = request.getParameter("action")) != null && action.equals("save")) {
	final String username = request.getParameter("username");
	final String password = request.getParameter("password");
	final String passwordHash = HashUtil.sha512(password);
	final String group = request.getParameter("group");
	if (!user.getUsername().equals(username)) {
		user.setUsername(username);
	}
	if (!user.getPasswordHash().equals(passwordHash)) {
		user.setPasswordHash(passwordHash);
	}
	if (!user.getGroup().getName().equals(group)) {
		user.setGroup(group);
	}
	%>
	<%-- <script type="text/javascript">
		console.log("username=" + <%= username %>);
		console.log("password=" + <%= password %>);
		console.log("passwordHash=" + <%= passwordHash %>);
		console.log("group=" + <%= group %>);
	</script> --%>
	<%
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
                    <p class="page-headline" id="settings-headline"></p>
                    <hr>
                </div>
                <form id="settings-form" method="post" autocomplete="off">
                    <div class="attributes-container" style="display: flex; flex-direction: column;">
                        <div>
                            <span class="word-username"></span>: <input class="ipt" type="text" id="username-input" name="username">
                        </div>
                        <div>
                            <span class="word-password"></span>: <input class="ipt" type="password" name="password">
                        </div>
                        <div>
                            <span class="word-group"></span>:
                            <select class="ipt" id="group-select" name="group">
                            	<%
                            	final List<IGroup> groups = IltisCloud.getAPI().getPermissionManager().getGroups();
                            	Collections.sort(groups);
                            	for (IGroup group : groups) { %>
                            		<script type="text/javascript">
	                            		var element = $('<option value=<%= group.getName() %>><%= group.getDisplayName() %></option>');
	                                    if (<%= user.getGroup().getName().equals(group.getName()) %>) {
	                                        element.attr('selected', 'selected');
	                                    }
	                                    $('#group-select').append(element);
                                    </script>
                            	<%
                            	}
                            	%>
                            </select>
                        </div>
                        <div>
                            <span class="word-language"></span>:
                            <select class="ipt" id="language-select" name="language">
                                <script type="text/javascript">
                                    for (l in languages) {
                                        var element = $('<option value=' + l + '>' + getLanguage(l).displayname + '</option>');
                                        if (l == language.name) {
                                            element.attr('selected', 'selected');
                                        }
                                        $('#language-select').append(element);
                                    }
                                </script>
                            </select>
                        </div>
                        <div>
                            <span class="word-design"></span>:
                            <select class="ipt" name="design">
                                <option class="word-dark" value="dark" selected="selected"></option>
                                <option class="word-light" value="light"></option>
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
                    document.title = getTranslation("page.settings.title");
                    $('#settings-headline').html(getTranslation("page.settings.headline"));
                    $('#submit-btn').attr("value", getTranslation("word.savesettings"));
                    $('#username-input').attr("value", '<%= user.getUsername() %>');
                    setTimeout(function() {
                        $('#navbar-link-settings').attr("href", "#");
                    }, 50);
                }, 100);
                
                var settings_form = $('#settings-form');
                settings_form.submit(function(e) {
                    let serialized = { };
                    settings_form.serializeArray().map(function(v) {
                        serialized[v.name] = v.value;
                    });
                	$.post("settings.jsp?action=save", serialized);
                    let new_language = serialized["language"];
                    let new_design = serialized["design"];
                    console.log("newDesign=" + design);
                    if (language.name != new_language) {
                        console.log("Setting language to " + new_language);
                        language = getLanguage(new_language);
                        document.cookie = "language=" + new_language;
                    }
                    if (design.name != new_design) {
                        console.log("Setting design to " + new_design);
                        design = new_design;
                        document.cookie = "design=" + new_design;
                    }
                    location.reload();
                });
            });
        </script>
	</body>
</html>
