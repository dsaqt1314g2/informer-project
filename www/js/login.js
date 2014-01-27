function getLogin() {
	
	//definimos las vriables del login
	var username = $('#username').val();
	var userpass = $('#userpass').val();
	var url = API_BASE_URL + "users/"+username;
	var urlredirect = WWW_URL;
	
	//realizamos un intento de GET de mis propios datos si me he logeado bien los consigo sino me devolvera error no autorizado.
	$.ajax({
		url: url,
		type: 'GET',
		crossDomain: true,
		dataType: 'json',
		headers: {
			"Accept" : "application/vnd.informer.api.user+json",
			//"Access-Control-Allow-Origin" : "*"
		},
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa(username+':'+userpass));
	    },
	    success: function (data, status, jqxhr) {
			document.cookie="username="+username;
			document.cookie="userpass="+userpass;
			document.cookie="role="+data.isModerador.toString();
			window.location = urlredirect +"/post_viewer.html";
			console.log(data);
		},
	    error: function (jqXHR, textStatus) {
	    	window.location = urlredirect +"/login.html";
			console.log(textStatus);
		}
	});
//	.done(function (data, status, jqxhr) {
//		//$.cookie("username", username);
//		//$.cookie("userpass", userpass);
//		document.cookie="username="+username;
//		document.cookie="userpass="+userpass;
//		window.location = "http://localhost/informer-project/post_viewer.html";
//		console.log(data);
//	})
//    .fail(function (jqXHR, textStatus) {
//    	//window.location = "http://localhost/informer-project/login.html";
//		console.log(textStatus);
//	});
}

function logOut() {
	document.cookie = 'username=;expires=Thu, 01 Jan 1970 00:00:01 GMT;';
	document.cookie = 'userpass=;expires=Thu, 01 Jan 1970 00:00:01 GMT;';
	document.cookie = 'role=;expires=Thu, 01 Jan 1970 00:00:01 GMT;';
	window.location = urlredirect+"index.html";
}

$("document").ready(function() {
	var username=getCookie("username");
	var userpass=getCookie("userpass");
	var role=getCookie("role");
	if (username!= "" && userpass!= "") {
		if (role == "true")
			$('#contenedor-barra').load('barra_moderador.html');
		else
			$('#contenedor-barra').load('barra_conectado.html');  
	}
    else
    	$('#contenedor-barra').load('barra.html');
	
	Nombre = username;
	Contrasena = userpass;    	
    console.log(Nombre+":"+Contrasena);
});

function getCookie(cname)
{
var name = cname + "=";
var ca = document.cookie.split(';');
for(var i=0; i<ca.length; i++) 
  {
  var c = ca[i].trim();
  if (c.indexOf(name)==0) return c.substring(name.length,c.length);
  }
return "";
}	


function isLogged() {
	var username=getCookie("username");
    var userpass=getCookie("userpass");
	if (username!="" && userpass!= "")	{
		var htmlString = '<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown">';
		htmlString += "<h5 style='color: #999999;'>"+username+"</h5>";
		htmlString += '<b class="caret"></b></a><ul class="dropdown-menu">';
		htmlString += '<li><a href="#" onClick="logOut()">Salir</a></li>';
		htmlString += '</ul></li>';
		document.getElementById("my-user-informer").innerHTML=htmlString;
		console.log(Nombre+":"+Contrasena);
	}
}