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
                    <p class="page-headline" id="createwrapper-headline"></p>
                    <hr>
                </div>
                <form target="editwrapper.jsp?action='create'" method="post">
                    <div class="attributes-container" style="display: flex; flex-direction: column;">
                        <div>
                            <span class="word-ipaddress"></span>: <input class="ipt" id="ipaddress-input" type="text" name="ip-addresse">
                        </div>
                         <div>
                            <span class="word-port"></span>: <input class="ipt" id="port-input" type="number" min="0" max="65535" name="port">
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
                    document.title = getTranslation("page.createwrapper.title");
                    $('#createwrapper-headline').html(getTranslation("page.createwrapper.headline"));
                    $('#ipaddress-input').attr("placeholder", getTranslation("word.ipaddress"));
                    $('#port-input').attr("placeholder", getTranslation("word.port"));
                    $('#submit-btn').attr("value", getTranslation("page.createwrapper.submitbtn.value"));
                }, 100);
            });
        </script>
	</body>
</html>
