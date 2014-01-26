var autorizacion = getCookie("username") +":"+getCookie("userpass");
var pagpublica = 0;
var pagprivada = 0;

$(document).ready(function() {
	
	Pintar();

});

function Pintar() {
	console.log("PIMN");
	
	var offset = 0;
	var length = 5;
	var url = API_BASE_URL + "salas/visible/";
	

	Gen_getSalasPublicas(url, offset, length);
	Gen_getSalasPrivadas(url, offset, length);
}

function Gen_getSalasPublicas(url, offset, length) {

	var type = 0; // Salas publicas
	url += type + "?o=" + offset + "&l=" + length;

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

						var Stringhtml = "<table class='table'><tr><th>ID</th><th>Propietario</th><th>Nombre Sala</th><th>Unirse</th><th>Ultima Actulizacion</th></tr>";

						$.each(data.salas,function(i, s) {
								Stringhtml += "<tr>";
								Stringhtml += "<td>"
								+ s.identificador
								+ "</td><td>"
								+ s.username
								+ "</td><td>"
								+ s.nombre_sala
								+ "</td><td><input type='button' value='Unirse' OnClick='Unirse("
								+ s.identificador
								+ ")'></td><td>"
								+ s.last_update + "</td>";
								Stringhtml += "</tr>";
						});
						
						Stringhtml += "</table>";
						$("#tabla_publica").html(Stringhtml);
						
						console.log(data.count);
						var numpag = Math.ceil(data.count/length);
						console.log(numpag);
						var Stringpaginacion= "";
						if(pagpublica==0)
							{
							Stringpaginacion = "<ul class='pagination'><li class='active'><a href='#publica0' OnClick='PaginaPublica(0,1)'>0 <span class='sr-only'>(current)</span></a></li>";
							var i = 1;
							while(i<numpag)
								{
									Stringpaginacion += "<li><a href='#publica"+i+"' OnClick='PaginaPublica("+i+",1)'>"+i+" </a></li>";
									i++;
								}
							console.log(numpag);
							if(numpag>1){
								Stringpaginacion += "<li><a href='#' OnClick='PaginaPublica(1,1)'>&raquo;</a></li>";	
							}
							Stringpaginacion +="</ul>";
							}
						
						else
							{
							
							Stringpaginacion = "<ul class='pagination'>";							
							Stringpaginacion += "<li><a href='#Prev' OnClick='PaginaPublica("+(pagpublica-1)+",1)'>&laquo;</a></li>";
							console.log(Stringpaginacion);
							var i = 0;
							while(i<numpag)
								{
									if(i==pagpublica)
										{
										Stringpaginacion += "<li class='active'><a href='#publica"+i+"' OnClick='PaginaPublica("+i+",1)'>"+i+" <span class='sr-only'>(current)</span></a></li>";
										}
									else
										{
										Stringpaginacion += "<li><a href='#publica"+i+"' OnClick='PaginaPublica("+i+",1)'>"+i+" </a></li>";
										}
									
									i++;
								}
							if(numpag>pagpublica+1)
								Stringpaginacion += "<li><a href='#' OnClick='PaginaPublica("+pagpublica+1+",1)'>&raquo;</a></li></ul>";
							
							}
						$("#paginacion1").html(Stringpaginacion);
						
					}).fail(function(jqXHR, textStatus) {
				console.log(textStatus + " " + url);
				var Stringhtml = "Error";
				$("#tabla_publica").html(Stringhtml);
			});
}

function Gen_getSalasPrivadas(url, offset, length) {

	var type = 1; // Salas privadas
	url += type + "?o=" + offset + "&l=" + length;

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

						var Stringhtml = "<table class='table'><tr><th>ID</th><th>Propietario</th><th>Nombre Sala</th><th>Unirse</th><th>Ultima Actulizacion</th></tr>";

						$
								.each(
										data.salas,
										function(i, s) {
											Stringhtml += "<tr>";
											Stringhtml += "<td>"
													+ s.identificador
													+ "</td><td>"
													+ s.username
													+ "</td><td>"
													+ s.nombre_sala
													+ "</td><td><div id='ponerpass"
													+ s.identificador
													+ "'><input type='button' value='Unirse' OnClick='PonerPass("
													+ s.identificador
													+ ")'></div></td><td>"
													+ s.last_update + "</td>";
											Stringhtml += "</tr>";
										});
						Stringhtml += "</table>";
						$("#tabla_privada").html(Stringhtml);
					}).fail(function(jqXHR, textStatus) {
				console.log(textStatus + " " + url);
				var Stringhtml = "Error";
				$("#tabla_privada").html(Stringhtml);
			});
}

function Unirse(id) {

	var url = API_BASE_URL + "salas/" + id + "/unirse";

	$
			.ajax(
					{
						url : url,
						type : 'GET',
						crossDomain : true,
						beforeSend : function(request) {
							request.withCredentials = true;
							request.setRequestHeader("Authorization", "Basic "
									+ btoa(autorizacion));
						},
					})
			.done(
					function(data, status, jqxhr) {
						console.log(status);
						console.log(data);
						var Stringhtml = "<div class='alert alert-success'>Te has unido correctamente a la Sala con ID: "
								+ id + ".<p>" + data + "</div>";						
						$("#tabla_publica").html(Stringhtml);
						setTimeout(function () {
							Pintar();
						},redirecttimeout);
					})
			.fail(
					function(data, status, jqXHR, textStatus) {
						console.log(data);
						var Stringhtml = "<div class='alert alert-danger'>Error interno, Usted ya esta en esta sala.</div>";							
						$("#tabla_publica").html(Stringhtml);
						setTimeout(function () {
							Pintar();
						},redirecttimeout);
					});
}
function PonerPass(id) {

	var div = "#ponerpass" + id;
	var Stringhtml = "<input type='password' class='form-control' id='userpass"
			+ id
			+ "' placeholder='Password' required=''><button class='btn btn-default'  OnClick='UnirsePrivado("+ id + ")'>Identificarme</button>";
	$(div).html(Stringhtml);
}

function PaginaPublica(pag,tabla) {
	var offset = 0;
	var url = API_BASE_URL + "salas/visible/";
	if(tabla==1)
		{
		pagpublica = pag;		
		if(pag>0)
			{
			offset = (pag)*5;
			}
		
		Gen_getSalasPublicas(url, offset, 5);	
		}
	else
		{
		pagprivada = pag;		
		if(pag>0)
			{
			offset = (pag)*5;
			}		
		Gen_getSalasPrivadas(url, offset, 5);	
		}
}

function UnirsePrivado(id) {

	var String = "#userpass"+id;
	var password = $(String).val();
	var url = API_BASE_URL + "salas/" + id + "/unirse?pass="+CryptoJS.MD5(password).toString();;

	$
			.ajax(
					{
						url : url,
						type : 'GET',
						crossDomain : true,
						beforeSend : function(request) {
							request.withCredentials = true;
							request.setRequestHeader("Authorization", "Basic "
									+ btoa(autorizacion));
						},
					})
			.done(
					function(data, status, jqxhr) {
						console.log(status);
						console.log(data);
						var Stringhtml = "<div class='alert alert-success'>Te has unido correctamente a la Sala con ID: "
								+ id + ".<p>" + data + "</div>";						
						$("#post-container").html(Stringhtml);
					})
			.fail(
					function(data, status, jqXHR, textStatus) {
						console.log(data);
						var div = "#ponerpass" + id;
						var Stringhtml = "<div class='form-group has-error'><label class='control-label' for='inputError'>Password Error</label><input type='password' class='form-control' id='userpass"
							+ id
							+ "' placeholder='Password' required=''></div><button class='btn btn-default'  OnClick='UnirsePrivado("+ id + ")'>Identificarme</button>";
						$(div).html(Stringhtml);	
					});
}