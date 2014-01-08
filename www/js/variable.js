var API_BASE_URL = "http://localhost:8080/informer-api/";
var AUTH_BASE_URL = "http://localhost:8080/informer-api/users/";
var user = "alicia";
var pass = "alicia";


$(document).ready(function() {
	var username=getCookie("username");
	var userpass=getCookie("userpass");
	if (username!="" && userpass!= "")	{
		var htmlString = '<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown">';
		htmlString += "<h5 style='color: #999999;'>Bienvenid@ "+username+"</h5>";
		htmlString += '<b class="caret"></b></a><ul class="dropdown-menu">';
		htmlString += '<li><a href="#">Salir</a></li>';
		htmlString += '</ul></li>';
		document.getElementById("my-user-informer").innerHTML=htmlString;
		document.cookie = 'username=;expires=Thu, 01 Jan 1970 00:00:01 GMT;';
		document.cookie = 'userpass=;expires=Thu, 01 Jan 1970 00:00:01 GMT;';
		console.log(username+":"+userpass);
	}
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
