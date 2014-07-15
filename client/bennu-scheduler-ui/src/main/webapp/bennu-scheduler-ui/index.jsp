<!DOCTYPE html>
<html lang="en" ng-app="bennuScheduler">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>Scheduler Management</title>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/js/jquery.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/portal.js"></script>
	<link href="${pageContext.request.contextPath}/bennu-scheduler-ui/js/libs/codemirror/codemirror.css" rel="stylesheet">
</head>
<body style="display: none">
	<div id="portal-container">
		<div ng-view>
		</div>
		<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/js/angular.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/js/angular-route.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-scheduler-ui/js/libs/moment/moment.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-scheduler-ui/js/libs/codemirror/codemirror.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-scheduler-ui/js/app.js"></script>
	</div>
</body>
</html>
