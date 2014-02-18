var autorizacion = getCookie("username") + ":" + getCookie("userpass");
var loaded = 0;
var offset = 0;
var length = 15;

function START() {
	var username = getCookie("username");
	var perfil = getUrlVars();
	if (perfil == null)
		Pintar();
	else {
		perfil = getUrlVars().user;
		if (username == perfil)
			Pintar();
		else
			Pintar2();
	}
}

function Pintar() {
	var url = API_BASE_URL + "users/" + getCookie("username");
	GetUsuario(url, offset, length);
	GetNotificaciones(getCookie("username"), 0);
	GetPost(getCookie("username"), 0, 15);
	$("#solicitudAmistad").remove();
	$("#eliminarAmistad").remove();
}

function Pintar2() {
	$(".borrar").remove();
	var perfil = getUrlVars().user;
	$("#solicitud-amigos").attr("onClick","SolicitarAmistad('"+perfil+"',-1)");
	$("#eliminar-amigos").attr("onClick","EliminarAmistad('"+perfil+"',-1)");
	var url = API_BASE_URL + "users/" + perfil;
	GetUsuario_ajeno(url);
	GetNotificaciones(perfil, 1);
	GetPost(perfil, 0, 15);
}

function GetUsuario(url) {
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
		// console.log(data);
		$("#data1").html(data.username);
		$("#imageperfil").attr("src", data.foto);
		var estado = "Soltero";
		var sexo = "";
		if (data.genero)
			sexo = "Hombre";
		if (data.estado_civil == 1)
			estado = "Me acaban de dejar";
		if (data.estado_civil == 2)
			estado = "Abierto a Sugerencias";
		if (data.estado_civil == 3)
			estado = "Con relacion";
		$("#data2").html(sexo);
		$("#data4").html(estado);
		$("#data3").html((new Date(data.fecha_nacimiento)).toLocaleDateString());
		$("#data5").html(data.lugar_de_residencia);
		$("#data23").html((new Date(data.last_Update)).toLocaleDateString() + ' a las ' + (new Date(data.last_Update)).toLocaleTimeString());
	}).fail(function(jqXHR, textStatus) {
		var objInstanceName = new jsNotifications({
			autoCloseTime : 60,
			showAlerts : true,
			title : 'Contenido del post'
		});
		objInstanceName.show('error', "Error");
	});
}

function GetNotificaciones(username, caso) {
	var url = API_BASE_URL + "user/" + username + "/notifications";
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
			"Accept" : "application/vnd.dsa.informer.notification+json",
		},
	}).done(function(data, status, jqxhr) {
		$("#data6").html(data.numamigos);
		$("#data7").html(data.numpost);
		$("#data8").html(data.numcomment);
		$("#data9").html(data.numlikes);
		$("#data10").html(data.numdislikes);
		$("#data11").html(data.participacion);
		$("#data40").html(data.n_i_sala);
		$("#data41").html(data.n_s_amistad);
		if (caso == 0)
			$("#data51").attr("onClick", "Amigos(\'" + username + "\')");
		if (caso == 1)
			$("#data51").attr("onClick", "Amigos_ajeno(\'" + username + "\')");
	}).fail(function(jqXHR, textStatus) {
		// console.log("aki llega bien pero ta mal");
	});
}

function GetPost(username, offset, length) {
	var url = API_BASE_URL + "posts/novedades/" + username + "?o=" + offset + "&l=" + length;
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
			"Accept" : "application/vnd.informer.api.post.collection+json",
		},
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		var html = rellenarPosts(data, '');
		$("#data12").append(html);
		$.each(data.posts, function(i, p) {
			if (p.numcomentarios > 0)
				processComentarios(p.identificador, 0);
		});
		refreshPopovers();
		stop = 0;
	}).fail(function(jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function PanelControl() {
	$('#listStuff').load('frames/paneledit.html');
	GetUserDates(getCookie("username"));
}

function Solicitudes() {
	// $('#listStuff').load('frames/veramigos.html');
	GetSolicitudes(getCookie("username"));
}

function Amigos(username) {
	if (username == null)
		username = getCookie("username");
	$('#listStuff').load('frames/veramigos.html');
	Getamigos(username);
}

function GetSolicitudes(username) {
	// anadir ofset y length con paginacion
	var url = API_BASE_URL + "users/solicitudes?o=0&l=20";
	// console.log(url);
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
			"Accept" : "application/vnd.informer.api.user.collection+json",
		},
	}).done(function(data, status, jqxhr) {
		console.log(data);
		var html = "";
		$.each(data.users, function(i, s) {
			html += '<tr>';
			html += '<td style="vertical-align:middle;"><img style="max-width: 50px; max-height: 50px;" src="' + s.foto + '"></td>';
			html += '<td style="vertical-align:middle;">' + s.username + '</td>';
			html += '<td style="vertical-align:middle;">' + (new Date(s.last_Update)).toLocaleDateString() + '</td>';
			html += '<td style="vertical-align:middle;"><p class="btn btn-success btn-xs" onClick="AceptarAmistad(\'' + s.username + '\',0)"><span class="glyphicon glyphicon-ok">';
			html += '</span> Aceptar</p>&nbsp;&nbsp;<p OnClick="EliminarAmistad(\'' + s.username + '\',0)" class="btn btn-danger btn-xs" ><span class="glyphicon glyphicon-remove">';
			html += '</span> Rechazar</p></td></tr>';
		});
		if (html == "")
			html = "<tr><td colspan=4><h3>No hay solicitudes</h3></td></tr>";
		$("#perfiles-amigos-inv").html(html);
	}).fail(function(jqXHR, textStatus) {
		// console.log(textStatus);
	});
}

function Getamigos(username) {
	// anadir ofset y length con paginacion
	var url = API_BASE_URL + "users/" + username + "/amigos?o=0&l=20";
	// console.log(url);
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
			"Accept" : "application/vnd.informer.api.user.collection+json",
		},
	}).done(function(data, status, jqxhr) {
		// console.log(data);
		var html = "";
		$.each(data.users, function(i, s) {
			html += '<tr><td style="vertical-align:middle;"><img style="text-align:center;max-width: 50px; max-height: 50px" src="' + s.foto + '" /></td>';
			html += '<td style="vertical-align:middle;">' + s.username + '</td>';
			html += '<td style="vertical-align:middle;">' + (new Date(s.last_Update)).toLocaleDateString() + '</td>';
			html += '<td style="vertical-align:middle;"><a class="btn btn-info btn-xs" href="perfil.html?user=' + s.username + '" ><span class="glyphicon glyphicon-edit">';
			html += '</span> Ver Perfil</a>&nbsp;&nbsp;<a href="#eliminar" OnClick="EliminarAmistad(\'' + s.username + '\',1)" class="btn btn-danger btn-xs" ><span class="glyphicon glyphicon-remove">';
			html += '</span> Eliminar</a></td></tr>';
		});
		if (html == "")
			html = "<tr><td colspan=4 style='text-align: center;'><h3>No tienes amigos :(</h3></td></tr>";
		$("#perfiles-amigos").html(html);
	}).fail(function(jqXHR, textStatus) {
		// console.log(textStatus);
	});
}

function EliminarAmistad(username, caso) {
	var objInstanceName = new jsNotifications({
		autoCloseTime : 5,
		showAlerts : true,
		title : 'Informer'
	});
	var url = API_BASE_URL + "users/" + username + "/deletefriend";
	// console.log(url);
	$.ajax({
		url : url,
		type : 'DELETE',
		crossDomain : true,
		dataType : 'json',
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		objInstanceName.show('ok', 'Se ha eliminado la amistad.');
	}).fail(function(jqXHR, textStatus) {
		var status = jqXHR.status;
		if (status == 200)
			objInstanceName.show('ok', jqXHR.responseText);
		else
			objInstanceName.show('error', jqXHR.responseText);
	});
	setTimeout(function() {
		if (caso == 0)
			GetSolicitudes(getCookie("username"));
		else if (caso == 1)
			Getamigos(getCookie("username"));
		else if (caso == -1)
			location.reload();
	}, redirecttimeout);
}

function AceptarAmistad(username, caso) {
	// console.log("Aqui llega 3");
	var objInstanceName = new jsNotifications({
		autoCloseTime : 5,
		showAlerts : true,
		title : 'Informer'
	});
	var url = API_BASE_URL + "users/" + username + "/aceptfriend";
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		// console.log(data);
		objInstanceName.show('ok', 'Se ha aceptado la amistad.');
	}).fail(function(jqXHR, textStatus) {
		var status = jqXHR.status;
		if (status == 200)
			objInstanceName.show('ok', jqXHR.responseText);
		else
			objInstanceName.show('error', jqXHR.responseText);
	});
	setTimeout(function() {
		if (caso == 0)
			GetSolicitudes(getCookie("username"));
	}, redirecttimeout);
}

function GetUserDates(username) {
	var url = API_BASE_URL + "users/" + username;
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
		// console.log(data);
		$("#nombre").val(data.username);
		$("#correo").val(data.correo);
		$("#foto2").val(data.foto);
		if (data.genero)
			$("#SexoinlineCheckbox1").attr('checked', 'checked');
		else
			$("#SexoinlineCheckbox2").attr('checked', 'checked');
		$("#estado").val(data.estado_civil);
		var fecha = (new Date(data.fecha_nacimiento)).toLocaleString();
		$("#dia").val(fecha.split("/")[0]);
		$("#mes").val(fecha.split("/")[1]);
		$("#ano").val(fecha.split("/")[2].split(" ")[0]);
	}).fail(function(jqXHR, textStatus) {
		// console.log(textStatus);
	});

}

function ActulizarUser() {
	var url = API_BASE_URL + "users/" + getCookie("username");
	var correo = $('#correo').val();
	var foto = $('#foto2').val();

	var estado_civil = $('#estado').val();
	var genero = $('.inlineCheckbox1').val();
	var universidad = $("#universidad").val();
	var sex = true;
	if (genero != "male")
		sex = false;
	var fecha = $('#ano').val() + "-" + $('#mes').val() + "-" + $('#dia').val() + "T00:00:00";

	var usuario = '{"correo": "' + correo + '",';
	usuario += '"estado_civil": ' + estado_civil + ',';
	usuario += '"fecha_nacimiento": "' + fecha + '",';
	usuario += '"genero": ' + sex + ',';
	// usuario += '"lugar_de_residencia": "Mi casa",';
	usuario += '"foto": "' + foto + '",';
	// usuario += '"participar_GPS": false,';
	usuario += '"uni_escuela": ' + universidad + '}';
	//console.log(usuario);
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		data : usuario,
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
		headers : {
			"Accept" : "application/vnd.informer.api.user+json",
			"Content-Type" : "application/vnd.informer.api.user+json",
		},
	}).done(function(data, status, jqxhr) {
		document.cookie = "imagen=" + foto;
		setTimeout(function() {
			Pintar()
		}, redirecttimeout);

	}).fail(function(jqXHR, textStatus) {
		console.log(textStatus);
		return (false);
	});
}

function getUrlVars() {
	var perfil;
	if (perfil != null)
		return perfil;
	var vars = [], hash;
	var hashes;
	try {
		hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
		for (var i = 0; i < hashes.length; i++) {
			hash = hashes[i].split('=');
			vars.push(hash[0]);
			vars[hash[0]] = hash[1].split('#')[0];
		}
	} catch (err) {
		return null;
	}
	return vars;
}

function GetUsuario_ajeno(url) {
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
		if (data.isFriend == true)
			$("#solicitudAmistad").remove();
		else
			$("#eliminarAmistad").remove();
		$("#data1").html(data.username);
		$("#imageperfil").attr("src", data.foto);
		var sexo = "Mujer";
		if (data.genero)
			sexo = "Hombre";
		var estado = "Soltero";
		if (data.estado_civil == 1)
			estado = "Sin lazos";
		else if (data.estado_civil == 2)
			estado = "A falta de mimos";
		else if (data.estado_civil == 3)
			estado = "Follamig@";
		else if (data.estado_civil == 4)
			estado = "Relaci&oacute;n abierta";
		else if (data.estado_civil == 5)
			estado = "Relaci&oacute;n a distancia";
		else if (data.estado_civil == 6)
			estado = "En una relaci&oacute;n";
		else if (data.estado_civil == 7)
			estado = "Comprometid@";
		$("#data2").html(sexo);
		$("#data4").html(estado);
		$("#solicitud-amigos").attr("onClick", "SolicitarAmistad('" + data.username + "')")
		$("#data3").html((new Date(data.fecha_nacimiento)).getFullYear());
	}).fail(function(jqXHR, textStatus) {
		console.log(textStatus + " " + url);
		var Stringhtml = "Error";
		$("#todo").html(Stringhtml);
	});
}

function Amigos_ajeno(username) {
	if (username == null)
		username = getUrlVars().user;
	$('#listStuff').load('frames/veramigos.html');
	Getamigos(username);
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
		// setTimeout(function(){Pintar();},redirecttimeout);
	}).fail(function(jqXHR, textStatus) {
		//console.log(jqXHR);
		if (jqXHR.status == 200) {
			objInstanceName.show('ok', 'Se ha enviado la solicitud.');
			location.reload();
		}
		else
			objInstanceName.show('error', JSON.parse(jqXHR.responseText).message);
		// setTimeout(function(){Pintar();},redirecttimeout);
	});
}