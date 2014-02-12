
function whais() {
	var htmlString = '<iframe id="iframe1" src="explicacion.html" frameborder="0"></iframe>';
	$('#target_website').html(htmlString);
	console.log("htmlString");
}

function why() {
	var htmlString = '<iframe id="iframe1" src="explicacion.html#why" frameborder="0"></iframe>';
	$('#target_website').html(htmlString);
	console.log("htmlString");
}

function register() {
	var htmlString = '<iframe id="iframe1" src="http://localhost:8080/informer-auth/register.jsp" frameborder="0"></iframe>';
	$('#target_website').html(htmlString);
	console.log("htmlString");
}

