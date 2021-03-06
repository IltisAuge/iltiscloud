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
                    <p class="page-headline" id="wrappers-headline"></p>
                    <hr>
                </div>
                <table class="table">
                    <thead>
                        <tr>
                            <th class="word-name"></th>
                            <th class="word-uniqueid"></th>
                            <th class="word-address"></th>
                            <th class="word-status"></th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>Wrapper-01</td>
                            <td>37e40b64-c460-4f4a-9e7a-a39fa949815a</td>
                            <td>49.12.163.115:25565</td>
                            <td class="text-success word-online"></td>
                            <td><a class="btn btn-success m-0 word-edit" href="wrapper.jsp?uniqueId='37e40b64-c460-4f4a-9e7a-a39fa949815a'"></a></td>
                        </tr>
                        <tr>
                            <td>Wrapper-02</td>
                            <td>e9e43c41-fb00-4e75-b2a1-c7cafe34baa1</td>
                            <td>49.12.163.115:27004</td>
                            <td class="text-danger word-offline"></td>
                            <td><a class="btn btn-success m-0 word-edit" href="wrapper.jsp?uniqueId='e9e43c41-fb00-4e75-b2a1-c7cafe34baa1'"></a></td>
                        </tr>
                    </tbody>
                </table>
                <td><a class="btn btn-success" href="createwrapper.jsp" id="create-wrapper"></a></td>
            </div>
        </div>
        <script type="text/javascript">
            $(document).ready(function() {
                setTimeout(function() {
                    document.title = getTranslation("page.wrappers.title");
                    $('#wrappers-headline').html(getTranslation("page.wrappers.headline"));
                    $('#create-wrapper').html(getTranslation("page.wrappers.createwrapper"));
                    setTimeout(function() {
                        $('#navbar-link-wrappers').attr("href", "#");
                    }, 50);
                }, 100);
            });
        </script>
	</body>
</html>
