var pagpublica = 0;

$(document).ready(function() {

	var offset = 0;
	var length = 5;
	var url = API_BASE_URL + "salas/visible/";	
	GetSalasMias(url, offset, length);
	

});

function GetSalasMias(url, offset, length) {

	var type = 3; // Salas publicas
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
									+ btoa('alicia:alicia'));
						},
						headers : {
							"Accept" : "application/vnd.informer.api.sala.collection+json",
						},
					})
			.done(
					function(data, status, jqxhr) {
						console.log(data);

						var Stringhtml = "<table class='table'><tr><th>ID</th><th>Propietario</th><th>Nombre Sala</th><th>Invitar Amigos</th><th>Abandonar</th><th>Ultima Actulizacion</th></tr>";

						$.each(data.salas,function(i, s) {
								Stringhtml += "<tr>";
								Stringhtml += "<td>"
								+ s.identificador
								+ "</td><td>"
								+ s.username
								+ "</td><td>"
								+ s.nombre_sala
								+ "</td><td><input type='button' value='Invitar' OnClick='Invitar("
								+ s.identificador
								+ ")'></td><td><input type='button' value='Abandonar' OnClick='Abandonar("
								+ s.identificador
								+ ")'></td><td>"
								+ s.last_update + "</td>";
								Stringhtml += "</tr>";
						});
						
						Stringhtml += "</table>";
						$("#tabla_salas").html(Stringhtml);
						
						console.log(data.count);
						var numpag = Math.ceil(data.count/length);
						console.log(numpag);
						var Stringpaginacion= "";
						if(pagpublica==0)
							{
							Stringpaginacion = "<ul class='pagination'><li class='active'><a href='#publica0' OnClick='Paginacion(0,1)'>0 <span class='sr-only'>(current)</span></a></li>";
							var i = 1;
							while(i<numpag)
								{
									Stringpaginacion += "<li><a href='#publica"+i+"' OnClick='Paginacion("+i+",1)'>"+i+" </a></li>";
									i++;
								}
							console.log(numpag);
							if(numpag>1){
								Stringpaginacion += "<li><a href='#' OnClick='Paginacion(1,1)'>&raquo;</a></li>";	
							}
							Stringpaginacion +="</ul>";
							}
						
						else
							{
							
							Stringpaginacion = "<ul class='pagination'>";							
							Stringpaginacion += "<li><a href='#Prev' OnClick='Paginacion("+(pagpublica-1)+",1)'>&laquo;</a></li>";
							console.log(Stringpaginacion);
							var i = 0;
							while(i<numpag)
								{
									if(i==pagpublica)
										{
										Stringpaginacion += "<li class='active'><a href='#publica"+i+"' OnClick='Paginacion("+i+",1)'>"+i+" <span class='sr-only'>(current)</span></a></li>";
										}
									else
										{
										Stringpaginacion += "<li><a href='#publica"+i+"' OnClick='Paginacion("+i+",1)'>"+i+" </a></li>";
										}
									
									i++;
								}
							if(numpag>pagpublica+1)
								Stringpaginacion += "<li><a href='#' OnClick='Paginacion("+pagpublica+1+",1)'>&raquo;</a></li></ul>";
							
							}
						$("#paginacion").html(Stringpaginacion);
						
					}).fail(function(jqXHR, textStatus) {
				console.log(textStatus + " " + url);
				var Stringhtml = "Error";
				$("#tabla_salas").html(Stringhtml);
			});
}

function Abandonar(id) {

	var url = API_BASE_URL + "salas/" + id + "/abandonar";

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
						var Stringhtml = "<div class='alert alert-success'>Has abandonado la Sala: "
								+ id + ".<p>" + data + "</div>";
						$("#post-container").html(Stringhtml);
					})
			.fail(
					function(data, status, jqXHR, textStatus) {
						console.log(data);
						var Stringhtml = "<div class='alert alert-danger'>Error interno, Usted no esta en esta sala.</div>";
						$("#post-container").html(Stringhtml);
					});
}

function Paginacion(pag) {
	var offset = 0;
	var url = API_BASE_URL + "salas/visible/";
	
		pagpublica = pag;		
		if(pag>0)
			{
			offset = (pag)*5;
			}
		
		GetInvitaciones(url, offset, 5);	
	
	
}
