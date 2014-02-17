var autorizacion = getCookie("username") + ":" + getCookie("userpass");
var loaded = 0;
var offset = 0;
var length = 15;
var urlredirect = WWW_URL;

function getUrlVars() {
	var vars = [], hash;
	var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
	for (var i = 0; i < hashes.length; i++) {
		hash = hashes[i].split('=');
		vars.push(hash[0]);
		vars[hash[0]] = hash[1];
	}
	return vars;
}

function PintarBusqueda() {
	var perfil = getUrlVars().user;
	if (perfil == null)
		perfil = getCookie("busqueda");
	console.log(perfil);
	var url = API_BASE_URL + "users/search?o=0&l=30";
	$('#listStuff').load('frames/veramigos.html');
	GetBusqueda(url, perfil);
}

function GetBusqueda(url, perfil) {
	var datauser = '{"username": "' + perfil + '"}';
	var objInstanceName = new jsNotifications({
		autoCloseTime : 5,
		showAlerts : true,
		title : 'Informer'
	});
	console.log(url);
	console.log(perfil);
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		data : datauser,
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
		headers : {
			"Accept" : "application/vnd.informer.api.user.collection+json",
			"Content-Type" : "application/vnd.informer.api.user.collection+json",
		},
	}).done(function(data, status, jqxhr) {
		console.log(data);
		var html = "";
		$.each(data.users, function(i, s) {
			html += '<tr><td style="vertical-align:middle;"><img style="text-align:center;max-width: 50px; max-height: 50px" src="' + s.foto + '" /></td>';
			html += '<td style="vertical-align:middle;">' + s.username + '</td>';
			html += '<td style="vertical-align:middle;">' + (new Date(s.last_Update)).toLocaleDateString() + '</td>';
			html += '<td style="vertical-align:middle;"><a class="btn btn-info btn-xs" href="perfil.html?user=' + s.username + '" ><span class="glyphicon glyphicon-edit">';
			html += '</span> Ver Perfil</a></td></tr>';
		});
		if (html == "") html = "<tr><td colspan=5 style='text-align: center;'><h3>No se han encontrado perfiles :(</h3></td></tr>";
		$("#listaAmigosBuscados").html(html);
		$('#buscarAmigos').modal('show');
	}).fail(function(jqXHR, textStatus) {
		console.log(textStatus + " " + url);
		$("#listaAmigosBuscados").html(Error);
	});
}

function SolicitarAmistad(username) {
	var objInstanceName = new jsNotifications({
		autoCloseTime : 5,
		showAlerts : true,
		title : 'Informer'
	});
	var url = API_BASE_URL + "users/solicitud/" + username;
	console.log(url);
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
		headers : {
			"Accept" : "application/vnd.informer.api.user+json",
		},
	}).done(function(data, status, jqxhr) {
		console.log(data);
		objInstanceName.show('ok', 'Se ha enviado la solicitud.');
		// setTimeout(function(){Pintar();},redirecttimeout);

	}).fail(function(jqXHR, textStatus) {
		console.log(textStatus);
		objInstanceName.show('error', 'Se ha producido un erro al enviar la solicitud o usted ya es amigo.');
		// setTimeout(function(){Pintar();},redirecttimeout);
	});
}
