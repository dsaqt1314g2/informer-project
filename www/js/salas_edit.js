var autorizacion = getCookie("username") + ":" + getCookie("userpass");

function Pintar() {
	$("#tabla_mod").html('<button type="button" class="btn btn-success" OnClick="CrearSala()" >Crear Sala</button>');
	var url = API_BASE_URL + "salas/administrarsalas";
	GetMisSalas(url);
}

function GetMisSalas(url) {
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
		//console.log(data);
		var Stringhtml = "";
		var visibilidad = "";
		$.each(data.salas, function(i, s) {
			if (s.visibilidad == 0)
				visibilidad = "P&uacute;blica";
			else if (s.visibilidad == 1)
				visibilidad = "Privada (visible)";
			else
				visibilidad = "Privada (oculta)";
			Stringhtml += "<tr><td>" + s.nombre_sala + "</td><td>" + visibilidad + "</td><td>" + s.username + "</td><td><div id='editar" + s.identificador + "'><button type='button' data-toggle='modal' data-target='#tabla_modsala' data-keyboard='true' onClick='ModSala("
					+ s.identificador + ",\"" + s.nombre_sala + "\", "+s.visibilidad+")'>Editar</button></div>" + "</td><td>" +  (new Date(s.last_update)).toLocaleDateString() + ' a las ' + (new Date(s.last_update)).toLocaleTimeString() + "</td></tr>";
		});
		$("#tabla_publica").html(Stringhtml);
	}).fail(function(jqXHR, textStatus) {
		console.log(textStatus + " " + url);
		var Stringhtml = "Error";
		$("#tabla_publica").html(Stringhtml);
	});
}

function ModSala(id, nombre, visibilidad) {
	$("#nombre_salamodf").attr("value",""+nombre);
	$("#select01modf option:eq("+visibilidad+")").prop('selected', true);
	$("#bottonModificar").attr("onClick", "ActulizarSala("+id+")");
}

function ActulizarSala(id) {
	var objInstanceName = new jsNotifications({
		autoCloseTime : 5,
		showAlerts : true,
		title : 'Informer'
	});
	var url = API_BASE_URL + "salas/" + id;
	var nombre = $('#nombre_salamodf').val();
	var pass = $('#pass_salamodf').val();
	pass = CryptoJS.MD5(pass).toString();
	var visibilidad = $('#select01modf').val();
	var sala = '{"nombre_sala": "' + nombre + '","password": "' + pass + '","visibilidad": ' + visibilidad + '}';
	//console.log(sala);
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		data : sala,
		headers : {
			"Content-Type" : "application/vnd.informer.api.sala+json",
			"Accept" : "application/vnd.informer.api.sala+json",
		},
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		//console.log(data);
		$('#tabla_modsala').modal('hide');
		objInstanceName.show('ok', 'Modificaci&oacute;n realizada');
		setTimeout(function() {
			Pintar()
		}, redirecttimeout);
	}).fail(function(jqXHR, textStatus) {
		objInstanceName.show('error', JSON.parse(jqXHR.responseText).message);
	});
}

function Salanueva() {
	var url = API_BASE_URL + "salas";
	var nombre = $('#nombre_sala').val();
	var pass = $('#pass_sala').val();
	pass = CryptoJS.MD5(pass).toString();
	var visibilidad = $('#select01').val();
	var sala = '{"nombre_sala": "' + nombre + '",';
	sala += '"password": "' + pass + '",';
	sala += '"visibilidad": ' + visibilidad + '}';
	console.log(sala);
	//console.log(url);
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		data : sala,
		headers : {
			"Content-Type" : "application/vnd.informer.api.sala+json",
			"Accept" : "application/vnd.informer.api.sala+json",
		},
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		$('#tabla_crearsala').modal('hide');
		setTimeout(function() {
			Pintar()
		}, redirecttimeout);
	}).fail(function(jqXHR, textStatus) {
		var objInstanceName = new jsNotifications({
			autoCloseTime : 5,
			showAlerts : true,
			title : 'Informer'
		});
		objInstanceName.show('error', JSON.parse(jqXHR.responseText).message);
	});
}