var autorizacion = getCookie("username") + ":" + getCookie("userpass");
var pagpublica = 0;

function pintar() {
	var url = API_BASE_URL + "salas/visible/";
	GetSalasMias(url, 0, 5); // url, offset, length
}

function GetSalasMias(url, offset, length) {
	var type = 3; // Salas publicas
	url += type + "?o=" + offset + "&l=" + length;
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
			"Accept" : "application/vnd.informer.api.sala.collection+json",
		},
	}).done(function(data, status, jqxhr) {
		// console.log(data);
		var Stringhtml = rellenarSalas(data);
		$("#tabla_salas").html(Stringhtml);
		var numpag = Math.ceil(data.count / length);
		var Stringpaginacion = "";
		if (pagpublica == 0) {
			Stringpaginacion = "<ul class='pagination'><li class='active'><a href='#publica0' OnClick='Paginacion(0,1)'>1 <span class='sr-only'>(current)</span></a></li>";
			var i = 1;
			while (i < numpag) {
				Stringpaginacion += "<li><a href='#publica" + i + "' OnClick='Paginacion(" + i + ",1)'>" + (i + 1) + " </a></li>";
				i++;
			}
			// console.log(numpag);
			if (numpag > 1) {
				Stringpaginacion += "<li><a href='#' OnClick='Paginacion(1,1)'>&raquo;</a></li>";
			}
			Stringpaginacion += "</ul>";
		} else {
			Stringpaginacion = "<ul class='pagination'>";
			Stringpaginacion += "<li><a href='#Prev' OnClick='Paginacion(" + (pagpublica - 1) + ",1)'>&laquo;</a></li>";
			console.log(Stringpaginacion);
			var i = 0;
			while (i < numpag) {
				if (i == pagpublica) {
					Stringpaginacion += "<li class='active'><a href='#publica" + i + "' OnClick='Paginacion(" + i + ",1)'>" + (i + 1) + " <span class='sr-only'>(current)</span></a></li>";
				} else {
					Stringpaginacion += "<li><a href='#publica" + i + "' OnClick='Paginacion(" + i + ",1)'>" + (i + 1) + " </a></li>";
				}
				i++;
			}
			if (numpag > pagpublica + 1)
				Stringpaginacion += "<li><a href='#' OnClick='Paginacion(" + pagpublica + 1 + ",1)'>&raquo;</a></li></ul>";
		}
		$("#paginacion").html(Stringpaginacion);
	}).fail(function(jqXHR, textStatus) {
		// console.log(textStatus + " " + url);
		var Stringhtml = "Error en el servidor...";
		$("#tabla_salas").html(Stringhtml);
	});
}

function Invitar(sala) {
	username = getCookie("username");
	Getamigos(username, sala);
}

function Getamigos(username, sala) {
	// anadir ofset y length con paginacion, o un buscador...
	var url = API_BASE_URL + "users/" + username + "/amigos?o=0&l=20";
	//console.log(url);
	//console.log("Sala: " + sala);
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
		//console.log(data);
		var html = '';
		$.each(data.users, function(i, s) {
			html += '<tr id="amigo'+s.username+'"><td style="vertical-align: middle;"><img width=50px height=50px src="' + s.foto + '"/></td>';
			html += '<td style="vertical-align: middle;">' + s.name + '</td>';
			html += '<td style="vertical-align: middle;">' + s.username + '</td>';
			html += '<td style="vertical-align: middle;">' + (new Date(s.last_Update)).toLocaleDateString() + '</td>';
			html += '<td  style="vertical-align: middle;"><a class="btn btn-info btn-xs" href="#invitado" OnClick="InvitarSend(\'' + s.username + '\',' + sala + ')">';
			html += '<span class="glyphicon glyphicon-edit"></span> Invitar</a></td></tr>';
		});
		$("#listaAmigosInvitar").html(html);
		//console.log("Aqui llega 2");
	}).fail(function(jqXHR, textStatus) {
		//console.log(textStatus);
	});
}

function InvitarSend(friend, sala) {
	var url = API_BASE_URL + "salas/" + sala + "/invitar?username=" + friend;
	var objInstanceName = new jsNotifications({
		autoCloseTime : 5,
		showAlerts : true,
		title : 'Informer'
	});
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
			"Accept" : "application/vnd.informer.api.sala+json",
		},
	}).done(function(data, status, jqxhr) {
		objInstanceName.show('ok', 'Se ha enviado la solicitud.');
		$('#amigo'+friend).remove();
		console.log(data);
	}).fail(function(jqXHR, textStatus) {
		objInstanceName.show('error', JSON.parse(jqXHR.responseText).message);
	});
}

function Abandonar(id) {
	var objInstanceName = new jsNotifications({
		autoCloseTime : 5,
		showAlerts : true,
		title : 'Informer'
	});
	var url = API_BASE_URL + "salas/" + id + "/abandonar";
	//console.log(url);
	$.ajax({
		url : url,
		type : 'DELETE',
		crossDomain : true,
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		//console.log(status);
		//console.log(data);
		objInstanceName.show('ok', 'Sala abandonada');
		setTimeout(function() {
			pintar()
		}, redirecttimeout);
	}).fail(function(data, status, jqXHR, textStatus) {
		//console.log(data);
		objInstanceName.show('error', JSON.parse(jqXHR.responseText).message);
		setTimeout(function() {
			pintar()
		}, redirecttimeout);
	});
}

function Eliminar(id) {
	var objInstanceName = new jsNotifications({
		autoCloseTime : 5,
		showAlerts : true,
		title : 'Informer'
	});
	var url = API_BASE_URL + "salas/" + id;
	$.ajax({
		url : url,
		type : 'DELETE',
		crossDomain : true,
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		//console.log(status);
		//console.log(data);
		objInstanceName.show('ok', 'Sala Eliminada');
	}).fail(function(data, status, jqXHR, textStatus) {
		//console.log(data);
		objInstanceName.show('error', 'Error interno o Usted no es dueño de la sala');
	});
	pintar();
}

function Paginacion(pag) {
	var offset = 0;
	var url = API_BASE_URL + "salas/visible/";
	pagpublica = pag;
	if (pag > 0) {
		offset = (pag) * 5;
	}
	GetInvitaciones(url, offset, 5);
}

function abrirChat(identificador) {
	document.cookie = "salaid=" + identificador;
	window.location = WWW_URL + "/chat_viewer.html";
}

function rellenarSalas(data) {
	var Stringhtml = "";
	$.each(data.salas, function(i, s) {
		Stringhtml += "<tr>";
		//Stringhtml += "<td>"+s.identificador + "</td>;
		Stringhtml += "<td>" + s.username + "</td>"
		Stringhtml += "<td><a href='javascript:void(0);' onClick='abrirChat(" + s.identificador + ")'>" + s.nombre_sala	+ "</a></td>";
		Stringhtml += "<td><button type='button' data-toggle='modal' data-target='#invitarAmigosSala' data-keyboard='true' onClick='Invitar(" + s.identificador + ")'>Invitar</button></td>";
		if (s.username != getCookie("username")) {
			Stringhtml += "<td><input type='button' value='Abandonar' OnClick='Abandonar(" + s.identificador + ")'></td>";
		} else {
			Stringhtml += "<td><input type='button' value='Eliminar Sala' OnClick='Eliminar(" + s.identificador + ")'></td>";
		}
		Stringhtml += "<td>" + (new Date(s.last_update)).toLocaleDateString() + ' a las ' + (new Date(s.last_update)).toLocaleTimeString() + "</td>";
		Stringhtml += "</tr>";
	});
	if (Stringhtml == "")
		Stringhtml = "<tr><td colspan=6 style='text-align: center;'><h3>No hay salas</h3></td></tr>";
	return Stringhtml;
}
