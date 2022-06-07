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
		<link rel="stylesheet" href="compiled/css/wrapper.css">
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
                    <p class="page-headline" id="wrapper-headline"></p>
                    <hr>
                </div>
                <div style="display: flex;">
                    <div class="server-info-container">
                        <div><span class="word-name"></span>: Wrapper-01</div>
                        <div><span class="word-uniqueid"></span>: 37e40b64-c460-4f4a-9e7a-a39fa949815a</div>
                        <div><span class="word-address"></span>: 49.12.163.115:25565</div>
                        <div><span class="word-status"></span>: <span class="text-success word-online"></span></div>
                        <div><span class="word-uptime"></span>: 21h 12min 3s</div>
                        <div class="server-actions-container">
                            <button class="btn btn-warning word-stop" onclick="stopWrapper('37e40b64-c460-4f4a-9e7a-a39fa949815a')"></button>
                            <button class="btn btn-danger word-kill" onclick="killWrapper('37e40b64-c460-4f4a-9e7a-a39fa949815a')"></button>
                        </div>
                    </div>
                    <div class="console-container d-flex flex-column">
                        <div class="console-text-container">
                            <p>Text</p>
                            <p>Text</p>
                            <p>Text</p>
                            <p>Text</p>
                            <p>Text</p>
                            <p>Text</p>
                            <p>Text</p>
                            <p>Text</p>
                            <p>Text</p>
                            <p>Text</p>
                            <p>Text</p>
                            <p>Text</p>
                            <p>Text</p>
                            <p>Text</p>
                            <p>Text</p>
                        </div>
                        <div class="command-line-container">
                            <form target="sndwrappercmd.jsp" method="post">
                                <div class="inner-form-container d-flex">
                                    <div>
                                        <input class="ipt cmd-input" id="cmd-input" type="text">
                                    </div>
                                    <div>
                                        <input class="submit-cmd-bnt btn-success" id="submit-cmd-bnt" type="submit">
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script type="text/javascript">
            $(document).ready(function() {
                setTimeout(function() {
                    document.title = getTranslation("page.wrapper.title", "Wrapper-01");
                    $('#wrapper-headline').html(getTranslation("page.wrapper.headline", "Wrapper-01"));
                    $('#cmd-input').attr("placeholder", getTranslation("console.cmdinput.placeholder"));
                    $('#submit-cmd-bnt').attr("value", getTranslation("console.submitbtn.value"));
                }, 100);
            });
        </script>
	</body>
</html>
