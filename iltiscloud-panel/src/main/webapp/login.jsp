<%@page import="de.iltisauge.iltiscloud.panel.IltisCloud"%>
<%@page import="de.iltisauge.iltiscloud.api.clouduser.ICloudUser"%>
<%@page import="de.iltisauge.iltiscloud.api.utils.HashUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%
String action = null;
if ((action = request.getParameter("action")) != null && action.equals("check")) {
	final String username = request.getParameter("user");
	final String password = request.getParameter("pw");
	final String passwordHash = HashUtil.sha512(password);
	final ICloudUser cloudUser = IltisCloud.getAPI().getCloudUserManager().getCloudUser(username);
	if (cloudUser == null || !cloudUser.getPasswordHash().equals(passwordHash)) {
		out.write("FAILED");
		return;
	}
	request.getSession().setAttribute("uniqueId", cloudUser.getUserId());
	out.write("SUCCESS");
	return;
}
%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<link rel="stylesheet" href="compiled/css/login.css">
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
        <script src="js/tools.js"></script>
        <script src="js/translations.js"></script>
		<script src="js/ClientToMaster.js"></script>
        <script src="js/pageLoad.js"></script>
	</head>
	<body>
		<div class="container">
			<div class="login-container">
				<div class="headline-container" id="login-headline"></div>
				<form id="login-form" method="post">
					<div class="credential-fields">
						<div class="credential-field">
							<p class="field-headline" id="login-username"></p>
							<input id="username-input" class="field-input" type="text"
								required="required" />
						</div>
						<div class="credential-field">
							<p class="field-headline" id="login-password"></p>
							<input id="password-input" class="field-input" type="password"
								required="required" />
						</div>
					</div>
					<div class="footer-container">
						<%
						final String error = request.getParameter("error");
						if (error == null) { %>
							<div id="error-container" class="error-container"></div>
						<%
						} else if (error.equals("1")) {
							request.getSession().removeAttribute("user");
						%>
							<div class="error-container" id="logged-out-text text-green"></div>
						<%
						}
						%>
						<input class="login-button" id="login-submit" type="submit">
						<div id="spinner-border" class="spinner-border" role="status"></div>
					</div>
				</form>
			</div>
		</div>
	</body>
	<script type="text/javascript">
        $(document).ready(function() {
            setTimeout(function() {
                document.title = getTranslation("page.login.title");
                $('#login-headline').html(getTranslation("page.login.headline"));
                $('#login-username').html(getTranslation("page.login.username"));
                $('#login-password').html(getTranslation("page.login.password"));
                $('#login-submit').attr("value", getTranslation("page.login.submit"));
                $('#logged-out-text').html(getTranslation("page.login.loggedout"));
            }, 100);
        });
        
		$('#spinner-border').hide();
		$('#login-form').submit(function(event) {
			event.preventDefault();
			createErrorMessage(" ", "white");
			$('#spinner-border').show();
			let username = $('#username-input').val();
			let password = $('#password-input').val();
			var request = $.get("login.jsp?action=check", { user: username, pw: password });
			request.done(function(data) {
				console.log("data=" + data);
				if (data.includes("FAILED")) {
					console.log("FAILED");
					$('#spinner-border').hide();
					$('#username-input').val('');
					$('#password-input').val('');
					createErrorMessage(getTranslation("page.login.invalidlogin"), "red");
				} else if (data.includes("SUCCESS")) {
					console.log("SUCCESS");
					window.location.href = "/index.jsp";
				}
			});
		});
		
		function createErrorMessage(message, c) {
			var element = $('#error-container');
			console.log("message=" + message);
			element.html(message);
			element.css("color", c);
		}
	</script>
</html>
