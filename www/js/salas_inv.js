var autorizacion = getCookie("username") + ":" + getCookie("userpass");
var pagpublica = 0;
var offset = 0;
var length = 5;

function startDoc() {
	var url = API_BASE_URL + "salas/invitaciones";
	GetInvitaciones(url, offset, length);
}

function GetInvitaciones(url, offset, length) {
	url += "?o=" + offset + "&l=" + length;
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
		var Stringhtml = "";
		$.each(data.salas, function(i, s) {
			Stringhtml += "<tr><td>" + s.username + "</td><td>" + s.nombre_sala + "</td><td><input type='button' value='Aceptar' OnClick='Aceptar(" + s.identificador
					+ ")'></td><td><input type='button' value='Rechazar' OnClick='Rechazar(" + s.identificador + ")'></td><td>" + (new Date(s.last_update)).toLocaleDateString() + ' a las ' + (new Date(s.last_update)).toLocaleTimeString()
					+ "</td></tr>";
		});
		if (Stringhtml == "") Stringhtml = "<tr><td colspan=5 style='text-align: center;'><h3>No hay invitaciones</h3></td></tr>";
		$("#tabla_invitaciones").html(Stringhtml);
		var numpag = Math.ceil(data.count / length);
		//console.log(numpag);
		var Stringpaginacion = "";
		if (pagpublica == 0) {
			Stringpaginacion = "<ul class='pagination'><li class='active'><a href='#publica0' OnClick='Paginacion(0)'>1 <span class='sr-only'>(current)</span></a></li>";
			var i = 1;
			while (i < numpag) {
				Stringpaginacion += "<li><a href='#publica" + i + "' OnClick='Paginacion(" + i + ")'>" + (i + 1) + " </a></li>";
				i++;
			}
			//console.log(numpag);
			if (numpag > 1) {
				Stringpaginacion += "<li><a href='#' OnClick='Paginacion(1)'>&raquo;</a></li>";
			}
			Stringpaginacion += "</ul>";
		}
		else {
			Stringpaginacion = "<ul class='pagination'>";
			Stringpaginacion += "<li><a href='#Prev' OnClick='Paginacion(" + (pagpublica - 1) + ")'>&laquo;</a></li>";
			//console.log(Stringpaginacion);
			var i = 0;
			while (i < numpag) {
				if (i == pagpublica) {
					Stringpaginacion += "<li class='active'><a href='#publica" + i + "' OnClick='Paginacion(" + i + ")'>" + (i + 1) + " <span class='sr-only'>(current)</span></a></li>";
				} else {
					Stringpaginacion += "<li><a href='#publica" + i + "' OnClick='Paginacion(" + i + ")'>" + (i + 1) + " </a></li>";
				}
				i++;
			}
			if (numpag > pagpublica + 1)
				Stringpaginacion += "<li><a href='#' OnClick='Paginacion(" + pagpublica + 1 + ")'>&raquo;</a></li></ul>";
		}
		$("#paginacion").html(Stringpaginacion);
	}).fail(function(jqXHR, textStatus) {
		console.log(textStatus + " " + url);
		var Stringhtml = "Error";
		$("#tabla_invitaciones").html(Stringhtml);
	});
}

function Aceptar(id) {
	var objInstanceName = new jsNotifications({
		autoCloseTime : 5,
		showAlerts : true,
		title : 'Informer'
	});
	var url = API_BASE_URL + "salas/" + id + "/aceptar";
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		//console.log(status);
		//console.log(data);
		objInstanceName.show('ok', 'Has aceptado la solicitud');
	}).fail(function(data, status, jqXHR, textStatus) {
		//console.log(data);
		objInstanceName.show('error', JSON.parse(jqXHR.responseText).message);
	});
	url = API_BASE_URL + "salas/invitaciones";
	GetInvitaciones(url, offset, length);
}
function Rechazar(id) {
	var objInstanceName = new jsNotifications({
		autoCloseTime : 5,
		showAlerts : true,
		title : 'Informer'
	});
	var url = API_BASE_URL + "salas/" + id + "/denegarinvitacion";
	$.ajax({
		url : url,
		type : 'DELETE',
		crossDomain : true,
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		objInstanceName.show('ok', 'Has rechazado la solicitud');
	}).fail(function(data, status, jqXHR, textStatus) {
		objInstanceName.show('error', JSON.parse(jqXHR.responseText).message);
	});
	url = API_BASE_URL + "salas/invitaciones";
	GetInvitaciones(url, offset, length);
}

function Paginacion(pag) {
	var offset = 0;
	var url = API_BASE_URL + "salas/invitaciones";
	pagpublica = pag;
	if (pag > 0) {
		offset = (pag) * 5;
	}
	GetInvitaciones(url, offset, 5);
}
