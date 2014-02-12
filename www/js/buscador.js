var autorizacion = getCookie("username") +":"+getCookie("userpass");
var loaded = 0;
var offset = 0;
var length = 15;
var urlredirect = WWW_URL;


$(document).ready(function() {		
	Pintar();
});
function getUrlVars()
{
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}

function Pintar() {
		
	var perfil = getUrlVars().user;
	if (perfil == null) perfil = getCookie("busqueda");
	console.log(perfil);
	
	var url = API_BASE_URL + "users/search?o=0&l=30";
	$('#listStuff').load('veramigos.html');
	GetBusqueda(url,perfil);	
		
}
function GetBusqueda(url,perfil) {

	var datauser = '{"username": "'+perfil+'"}';
	var objInstanceName=new jsNotifications({
		autoCloseTime : 5,
		showAlerts: true,
		title: 'Informer'
	});
	console.log(url);
	console.log(perfil);
	$
			.ajax(
					{
						url : url,
						type : 'POST',
						crossDomain : true,
						dataType : 'json',
						data: datauser,
						beforeSend : function(request) {
							request.withCredentials = true;
							request.setRequestHeader("Authorization", "Basic "
									+ btoa(autorizacion));
						},
						headers : {
							"Accept" : "application/vnd.informer.api.user.collection+json",
							"Content-Type" : "application/vnd.informer.api.user.collection+json",
						},
					})
			.done(
					function(data, status, jqxhr) {
						console.log(data);
						
						var html= '<table class="table table-striped custab" style="text-align:center;"><thead><tr style="text-align:center;">';
						//html +='<th>ID</th>';
						html +='<th>Foto</th><th>Nombre</th><th>Usuario</th><th>&Uacute;ltima conexi&oacute;n</th><th class="text-center">Acci&oacute;n</th></tr></thead>';
						
						$.each(data.users, function(i, s) {	
							
							html +='<td><a align="left"><div class="container" style="max-width: 75px; max-height: 50px" id="imageperfil">';
							html +='<img style="text-align:center;max-width: 75px; max-height: 50px" src="'+s.foto+'" class=""></div></a></td>';
							html +='<td>'+s.name+'</td>';
							html +='<td>'+s.username+'</td>';
							html +='<td>'+(new Date(s.last_Update)).toLocaleDateString()+'</td>';
							html +='<td class="text-center"><a class="btn btn-info btn-xs" href="perfil.html?user='+s.username+'" ><span class="glyphicon glyphicon-edit">';
							html +='</span> Ver Perfil </a> <a href="#eliminar" OnClick="SolicitarAmistad(\''+s.username+'\')" class="btn btn-success btn-xs" ><span class="glyphicon glyphicon-ok">';
							html +='</span> Enviar Solicitud</a></td></tr>';	
						});					
				
				$("#tabladeamigos").html(html); 	
						
					}).fail(function(jqXHR, textStatus) {
				console.log(textStatus + " " + url);
				
				$("#tabladeamigos").html(Error);
				
			});
}

function SolicitarAmistad(username) {
	
	var objInstanceName=new jsNotifications({
		autoCloseTime : 5,
		showAlerts: true,
		title: 'Informer'
	});
	

	var url = API_BASE_URL + "users/solicitud/"+username;
	console.log(url);
	$
			.ajax(
					{
						url : url,
						type : 'GET',
						crossDomain : true,
						dataType : 'json',
						beforeSend : function(request) {
							request.withCredentials = true;
							request.setRequestHeader("Authorization", "Basic "
									+ btoa(autorizacion));
						},
						headers : {
							"Accept" : "application/vnd.informer.api.user+json",
						},
					})
			.done(
					function(data, status, jqxhr) {
						console.log(data);	
						objInstanceName.show('ok','Se ha enviado la solicitud.');
						//setTimeout(function(){Pintar();},redirecttimeout);	
								
						
						
					}).fail(function(jqXHR, textStatus) {
						console.log(textStatus);
						objInstanceName.show('error','Se ha producido un erro al enviar la solicitud o usted ya es amigo.');
						//setTimeout(function(){Pintar();},redirecttimeout);	
							
						
			});
	
	
}

