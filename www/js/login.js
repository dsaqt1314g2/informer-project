$("#button_login").click(function(e){
	e.preventDefault();
	var username = $('#username').val();
	var userpass = $('#userpass').val();
	getLogin(username, userpass);	
});

function getLogin(username, userpass) {
	var url = API_BASE_URL + "users/"+username;
 
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
			"Accept" : "application/vnd.informer.api.user+json",
			//"Access-Control-Allow-Origin" : "*"
		},
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa(username+':'+userpass));
	    },
	})
	.done(function (data, status, jqxhr) {
		//$.cookie("username", username);
		//$.cookie("userpass", userpass);
		document.cookie="username="+username;
		document.cookie="userpass="+userpass;
		window.location = "http://localhost/informer-project/main.html";
		console.log(data);
	})
    .fail(function (jqXHR, textStatus) {
    	//window.location = "http://localhost/informer-project/login.html";
		console.log(textStatus);
	});
}
