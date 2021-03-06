<%@page import="de.iltisauge.iltiscloud.panel.IltisCloud"%>
<%@page import="java.util.UUID"%>
<%@page import="de.iltisauge.iltiscloud.api.clouduser.ICloudUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
if (request.getSession().getAttribute("uniqueId") == null) { %>
	<script type="text/javascript">
		window.location.href="login.jsp";
	</script> <%
	return;
}
final ICloudUser user = IltisCloud.getAPI().getCloudUserManager().getCloudUser((UUID) request.getSession().getAttribute("uniqueId"));
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <!-- Load style sheet depending on selected theme -->
        <link href="compiled/css/index.css" type="text/css" rel="stylesheet">
        <script src="https://code.jquery.com/jquery-3.6.0.js" integrity="sha256-H+K7U5CnXl1h5ywQfKtSj8PCmoN9aaq30gDh27Xc0jk=" crossorigin="anonymous"></script>
        <script src="js/tools.js"></script>
        <script src="js/translations.js"></script>
		<script src="js/ClientToMaster.js"></script>
        <script src="js/pageLoad.js"></script>
    </head>
    <body>
        <div id="page-header" style="opacity: 0;"></div>
        <div id="page-container">
            <div id="page-body" style="opacity: 0;">
                <div class="col-md-6">
                    <div class="page-title">
                        <p class="page-headline text-center" id="home-headline"></p>
                        <hr>
                    </div>
                    <img src="http://skin.heddo.eu/head/256/IltisAuge.png" style="margin-top: 50px;">
                    <div class="info-table">
                        <table class="table">
                            <thead>
                                <th class="word-name"></th>
                                <th class="word-status"></th>
                            </thead>
                            <tbody>
                                <tr><td>Master</td><td class="text-success word-online"></td></tr>
                                <tr><td>Wrapper-01</td><td class="text-success word-online"></td></tr>
                                <tr><td>Wrapper-02</td><td class="text-danger word-offline"></td></tr>
                                <tr><td>Server-01</td><td class="text-success word-online"></td></tr>
                                <tr><td>Server-02</td><td class="text-success word-online"></td></tr>
                                <tr><td>Server-03</td><td class="text-success word-online"></td></tr>
                                <tr><td>Server-04</td><td class="text-success word-online"></td></tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
        <script type="text/javascript">
            $(document).ready(function() {
                setTimeout(function() {
                    document.title = getTranslation("page.home.title");
                    $('#home-headline').html(getTranslation("page.home.headline", '<%= user.getUsername() %>'));
                    setTimeout(function() {
                        $('#navbar-link-home').attr("href", "#");
                    }, 50);
                }, 100);
            });
        </script>
    </body>
</html>