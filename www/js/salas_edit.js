
$(document).ready(function() {
	
	Pintar();

});

function Pintar() {
		
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
									+ btoa('alicia:alicia'));
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
								visibilidad="Visible";
							else if(s.visibilidad==1)
								visibilidad="Priv.-Visible";
							else
								visibilidad="Priv.-Oculta";
							
							Stringhtml += "<tr>";
							Stringhtml += "<td>"
								+ s.identificador
								+ "</td><td>"+ s.nombre_sala+ "</td><td>"+visibilidad+ "</td><td>"+ s.username+ "</td><td><div id='editar"+s.identificador+"'><input type='button' value='Editar' OnClick='editar("
								+ s.identificador+ ")'></div>"								
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
									+ btoa('alicia:alicia'));
						},
					})
			.done(
					function(data, status, jqxhr) {
						console.log(status);
						console.log(data);
						var Stringhtml = "<div class='alert alert-success'>Te has unido correctamente a la Sala con ID: "
								+ id + ".<p>" + data + "</div>";						
						$("#tabla_publica").html(Stringhtml);
						setTimeout(Pintar(),60000);							 
					})
			.fail(
					function(data, status, jqXHR, textStatus) {
						console.log(data);
						var Stringhtml = "<div class='alert alert-danger'>Error interno, Usted ya esta en esta sala.</div>";							
						$("#tabla_publica").html(Stringhtml);
						setTimeout(Pintar(),60000);					
						
					});
}
function PonerPass(id) {

	var div = "#ponerpass" + id;
	var Stringhtml = "<input type='password' class='form-control' id='userpass"
			+ id
			+ "' placeholder='Password' required=''><button class='btn btn-default'  OnClick='UnirsePrivado("+ id + ")'>Identificarme</button>";
	$(div).html(Stringhtml);
}
