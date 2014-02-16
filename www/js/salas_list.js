var autorizacion = getCookie("username") + ":" + getCookie("userpass");
var pagpublica = 0;
var pagprivada = 0;

function pintar() {
	var offset = 0;
	var length = 5;
	var url = API_BASE_URL + "salas/visible/";
	Gen_getSalasPublicas(url, offset, length);
	Gen_getSalasPrivadas(url, offset, length);
}

function Gen_getSalasPublicas(url, offset, length) {
	var type = 0; // Salas publicas
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
		//console.log(data);
		var Stringhtml = "";
		$.each(data.salas, function(i, s) {
			Stringhtml += "<tr><td>" + s.username + "</td><td>" + s.nombre_sala + "</td><td><input type='button' value='Unirse' OnClick='Unirse(" + s.identificador + ")'></td><td>"+ (new Date(s.last_update)).toLocaleDateString() + ' a las ' + (new Date(s.last_update)).toLocaleTimeString() + "</td></tr>";
		});
		$("#tabla_publica").html(Stringhtml);
		$("#paginacion1").html(gen_paginacion(data, length, "publica",1));
	}).fail(function(jqXHR, textStatus) {
		//console.log(textStatus + " " + url);
		$("#tabla_publica").html(JSON.parse(jqXHR.responseText).message);
	});
}

function Gen_getSalasPrivadas(url, offset, length) {
	var type = 1; // Salas privadas
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
		//console.log(data);
		var Stringhtml = "";
		$.each(data.salas, function(i, s) {
			Stringhtml += "<tr><td>" + s.username + "</td><td>" + s.nombre_sala + "</td><td><div id='ponerpass" + s.identificador + "'><input type='button' value='Unirse' OnClick='PonerPass(" + s.identificador
					+ ")'></div></td><td>" + (new Date(s.last_update)).toLocaleDateString() + ' a las ' + (new Date(s.last_update)).toLocaleTimeString() + "</td></tr>";
		});
		$("#tabla_privada").html(Stringhtml);
		$("#paginacion2").html(gen_paginacion(data, length, "privada",0));
	}).fail(function(jqXHR, textStatus) {
		console.log(textStatus + " " + url);
		var Stringhtml = "Error";
		$("#tabla_privada").html(Stringhtml);
	});
}

function Unirse(id) {
	var objInstanceName = new jsNotifications({
		autoCloseTime : 5,
		showAlerts : true,
		title : 'Informer'
	});
	var url = API_BASE_URL + "salas/" + id + "/unirse";
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		console.log(status);
		console.log(data);
		objInstanceName.show('ok', 'Te has unido correctamente a la sala');
		// var Stringhtml = "<div class='alert alert-success'>Te has unido
		// correctamente a la Sala con ID: "+ id + ".<p>" + data + "</div>";
		// $("#tabla_publica").html(Stringhtml);
		setTimeout(function() {
			Pintar();
		}, redirecttimeout);
	}).fail(function(data, status, jqXHR, textStatus) {
		console.log(data);
		objInstanceName.show('error', 'Error interno, usted ya esta en esta sala.');
		// var Stringhtml = "<div class='alert alert-danger'>Error interno,
		// Usted ya esta en esta sala.</div>";
		// $("#tabla_publica").html(Stringhtml);
		setTimeout(function() {
			Pintar();
		}, redirecttimeout);
	});
}
function PonerPass(id) {
	var div = "#ponerpass" + id;
	var Stringhtml = "<input type='password' style='width:150px;' class='form-control' id='userpass" + id + "' placeholder='Contrase&ntilde;a' required='' onkeydown='if (event.keyCode == 13) UnirsePrivado(" + id + ");'>";
	$(div).html(Stringhtml);
}

function PaginaPublica(pag, tabla) {
	var offset = 0;
	var url = API_BASE_URL + "salas/visible/";
	if (tabla == 1) {
		pagpublica = pag;
		if (pag > 0) {
			offset = (pag) * 5;
		}
		Gen_getSalasPublicas(url, offset, 5);
	} else {
		pagprivada = pag;
		if (pag > 0) {
			offset = (pag) * 5;
		}
		Gen_getSalasPrivadas(url, offset, 5);
	}
}

function UnirsePrivado(id) {
	var String = "#userpass" + id;
	var password = $(String).val();
	var url = API_BASE_URL + "salas/" + id + "/unirse?pass=" + CryptoJS.MD5(password).toString();
	console.log(url);
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		console.log(status);
		console.log(data);
		var Stringhtml = "<div class='alert alert-success'>Te has unido correctamente a la Sala con ID: " + id + ".<p>" + data + "</div>";
		$("#post-container").html(Stringhtml);
		setTimeout(function() {
			Pintar();
		}, redirecttimeout);
	}).fail(
			function(data, status, jqXHR, textStatus) {
				console.log(data);
				var div = "#ponerpass" + id;
				var Stringhtml = "<input type='password' style='width:150px;border:1px solid red;box-shadow: 0 0 3px #CC0000;'class='form-control' id='userpass" + id
						+ "' placeholder='Contrase&ntilde;a incorrecta' required='' onkeydown='if (event.keyCode == 13) UnirsePrivado(" + id + ");'></div>";
				$(div).html(Stringhtml);
				setTimeout(function() {
					Pintar();
				}, redirecttimeout);
			});
}

function gen_paginacion(data, length, tipo, numtipo) {
	var numpag = Math.ceil(data.count / length);
	//console.log(numpag);
	var Stringpaginacion = "";
	if (pagprivada == 0) {
		Stringpaginacion = "<ul class='pagination'><li class='active'><a href='#"+tipo+"0' OnClick='PaginaPublica(0,"+numtipo+")'>1 <span class='sr-only'>(current)</span></a></li>";
		var i = 1;
		while (i < numpag) {
			Stringpaginacion += "<li><a href='#"+tipo+"" + i + "' OnClick='PaginaPublica(" + i + ","+numtipo+")'>" + (i + 1) + " </a></li>";
			i++;
		}
		//console.log(numpag);
		if (numpag > 1) {
			Stringpaginacion += "<li><a href='#' OnClick='PaginaPublica(1,"+numtipo+")'>&raquo;</a></li>";
		}
		Stringpaginacion += "</ul>";
	} else {
		Stringpaginacion = "<ul class='pagination'>";
		Stringpaginacion += "<li><a href='#Prev' OnClick='PaginaPublica(" + (pagprivada - 1) + ","+numtipo+")'>&laquo;</a></li>";
		//console.log(Stringpaginacion);
		var i = 0;
		while (i < numpag) {
			if (i == pagprivada) {
				Stringpaginacion += "<li class='active'><a href='#"+tipo+"" + i + "' OnClick='PaginaPublica(" + i + ","+numtipo+")'>" + (i + 1) + " <span class='sr-only'>(current)</span></a></li>";
			} else {
				Stringpaginacion += "<li><a href='#"+tipo+"" + i + "' OnClick='PaginaPublica(" + i + ","+numtipo+")'>" + (i + 1) + " </a></li>";
			}

			i++;
		}
		if (numpag > pagprivada + 1)
			Stringpaginacion += "<li><a href='#' OnClick='PaginaPublica(" + pagprivada + 1 + ","+numtipo+")'>&raquo;</a></li></ul>";
	}
	return Stringpaginacion;
}