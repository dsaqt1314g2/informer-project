var autorizacion = getCookie("username") +":"+getCookie("userpass");

$(document).ready(function() {	
	Pintar();
});

function Pintar() {
		
	var contenedor = document.getElementById("tabla_crearsala");
	contenedor.style.visibility = "hidden";
	contenedor.style.display = "none";
	
	$("#tabla_mod").html('<button type="button" class="btn btn-success" OnClick="CrearSala()" >Crear Sala</button>');
		
	var url = API_BASE_URL + "salas/administrarsalas";
	GetMisSalas(url);
	
}

function GetMisSalas(url) {	
	
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
							"Accept" : "application/vnd.informer.api.sala.collection+json",
						},
					})
			.done(
					function(data, status, jqxhr) {
						console.log(data);

						var Stringhtml = "<table class='table'><tr><th>ID</th><th>Nombre Sala</th><th>Visibilidad</th><th>Propietario</th><th>Editar</th><th>Ultima Actulizacion</th></tr>";
						var visibilidad="";
						$.each(data.salas,function(i, s) {
							if(s.visibilidad==0)
								visibilidad="Publica - Visible";
							else if(s.visibilidad==1)
								visibilidad="Priv.- Visible";
							else
								visibilidad="Priv.- Oculta";
							var aux = '"'+s.nombre_sala+'"';
							Stringhtml += "<tr>";
							Stringhtml += "<td>"
								+ s.identificador
								+ "</td><td>"+ s.nombre_sala+ "</td><td>"+visibilidad+ "</td><td>"+ s.username+ "</td><td><div id='editar"+s.identificador+"'><input type='button' value='Editar'"
								+ "OnClick="+"'"+ "ModSala("+s.identificador+ ","+aux+" )'></div>"							
								+ "</td><td>"
								+ s.last_update + "</td>";
								Stringhtml += "</tr>";
						});
						
						Stringhtml += "</table>";
						$("#tabla_publica").html(Stringhtml);	
					}).fail(function(jqXHR, textStatus) {
				console.log(textStatus + " " + url);
				var Stringhtml = "Error";
				$("#tabla_publica").html(Stringhtml);
			});
}

function CrearSala(){
	
	var contenedor = document.getElementById("tabla_crearsala");
	contenedor.style.visibility = "visible";
	contenedor.style.display = "block";
	var contenedor2 = document.getElementById("tabla_mod");
	contenedor2.style.visibility = "hidden";
	contenedor2.style.display = "none";
	
}

function ModSala(id, nombre){
	
	var contenedor = document.getElementById("tabla_crearsala");
	contenedor.style.visibility = "hidden";
	contenedor.style.display = "none";
	var contenedor2 = document.getElementById("tabla_mod");
	contenedor2.style.visibility = "visible";
	contenedor2.style.display = "block";
	
	var String_html = '<div id="tabla_mod"><button type="button" class="btn btn-success" OnClick="CrearSala()" >Crear Sala</button></div>';
	String_html += '<form class="form-horizontal"><fieldset><legend>Modificar Sala</legend>';
	String_html += '<div class="control-group"><label class="control-label" for="input01">Nombre de la';
	String_html +='Sala.</label><div class="controls"><input type="text" value="'+nombre+'" class="input-xlarge" id="nombre_salamodf">';
	String_html +='<p class="help-block">Tiene que ser un Nombre Autodescriptivo y no muy largo.</p>';
	String_html +='</div><div class="control-group"><label class="control-label" for="input01">Password</label>';
	String_html +='<div class="controls"><input type="password" class="input-xlarge" id="pass_salamodf">';
	String_html +='<p class="help-block">Solo es necesario en caso de sala privada</p></div></div>';
	String_html +='<div class="control-group"><label class="control-label" for="select01">Tipo de Sala</label>';
	String_html +='<div class="controls"><select id="select01modf"><option value="0">Publica</option><option value="1">Privada-Visible</option>';
	String_html +='<option value="2">Privada-Oculta</option></select></div></div><div class="controls">';
	String_html +='<button type="button" class="btn btn-success" OnClick="ActulizarSala('+id+')" >Modificar</button></div>';
	String_html +='</fieldset></form>';
	
	$("#tabla_mod").html(String_html);

}

function ActulizarSala(id){
	
var url = API_BASE_URL + "salas/"+id;
	
	var nombre = $('#nombre_salamodf').val();
	var pass = $('#pass_salamodf').val();
	pass = CryptoJS.MD5(pass).toString();
	var visibilidad = $('#select01modf').val();

	
	var sala = '{"nombre_sala": "'+nombre+'",';
	sala +=  '"password": "'+pass+'",';
	sala +=  '"visibilidad": '+visibilidad+'}';
	
	console.log(sala);
	
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		data: sala,
		headers : {
			"Content-Type" : "application/vnd.informer.api.sala+json",
			"Accept" : "application/vnd.informer.api.sala+json",
		},
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa(autorizacion));
	    },
	})
    .done(function (data, status, jqxhr) {
    	console.log(status);
		console.log(data);
		var Stringhtml = "<div class='alert alert-success'>Has Modificado la Sala: "+data.nombre_sala+" con password:";
		Stringhtml += data.password +" visibilidad tipo :"+data.visibilidad ; 
		$("#tabla_publica").html(Stringhtml);
		setTimeout(function(){Pintar()},redirecttimeout);	
	})
    .fail(function (jqXHR, textStatus) {
    	console.log(textStatus);
    	console.log(jqXHR);
		var Stringhtml = "<div class='alert alert-danger'>Error interno, no se ha podido crear la sala.</div>";
		$("#tabla_publica").html(Stringhtml);
		setTimeout(function(){Pintar()},redirecttimeout);	
	});	

}

function Salanueva() {	
	
	var url = API_BASE_URL + "salas";
	
	var nombre = $('#nombre_sala').val();
	var pass = $('#pass_sala').val();
	pass = CryptoJS.MD5(pass).toString();
	var visibilidad = $('#select01').val();

	
	var sala = '{"nombre_sala": "'+nombre+'",';
	sala +=  '"password": "'+pass+'",';
	sala +=  '"username": "'+getCookie("username")+'",';
	sala +=  '"visibilidad": '+visibilidad+'}';
	
	console.log(sala);
	console.log(url);
	
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		data: sala,
		headers : {
			"Content-Type" : "application/vnd.informer.api.sala+json",
			"Accept" : "application/vnd.informer.api.sala+json",
		},
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa(autorizacion));
	    },
	})
    .done(function (data, status, jqxhr) {
    	console.log(status);
		console.log(data);
		var Stringhtml = "<div class='alert alert-success'>Has Creado la Sala: "+data.nombre_sala+" con password:";
		Stringhtml += data.password +" visibilidad tipo :"+data.visibilidad ; 
		$("#tabla_publica").html(Stringhtml);
		setTimeout(function(){Pintar()},redirecttimeout);	
	})
    .fail(function (jqXHR, textStatus) {
    	console.log(textStatus);
    	console.log(jqXHR);
		var Stringhtml = "<div class='alert alert-danger'>Error interno, no se ha podido crear la sala.</div>";
		$("#tabla_publica").html(Stringhtml);
		setTimeout(function(){Pintar()},redirecttimeout);	
	});	
}