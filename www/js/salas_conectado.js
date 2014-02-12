var autorizacion = getCookie("username") +":"+getCookie("userpass");
var pagpublica = 0;

$(document).ready(function() {

	console.log(autorizacion);
	pintar();
	

});

function pintar() {
	var offset = 0;
	var length = 5;
	var url = API_BASE_URL + "salas/visible/";	
	GetSalasMias(url, offset, length);
}

function Invitar(sala) {
	username=getCookie("username");
	$('#tabla_salas').load('solicitudinvitacion.html');
	Getamigos(username, sala);
}

function Getamigos(username, sala) {
	
	//añadir ofset y length con paginacion
	var url = API_BASE_URL + "users/"+username+"/amigos?o=0&l=20";
	console.log(url);
	console.log("Sala: "+sala);
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
							"Accept" : "application/vnd.informer.api.user.collection+json",
						},
					})
			.done(
					function(data, status, jqxhr) {
						console.log(data);	
						console.log("Aqui llega 2");
						var html= '<table class="table table-striped custab" style="text-align:center;"><thead><tr style="text-align:center;">';
						//html +='<th>ID</th>';
						html +='<th>Foto</th><th>Nombre</th><th>Usuario</th><th>&Uacute;ltima conexi&oacute;n</th><th class="text-center">Acci&oacute;n</th></tr></thead>';
						
						$.each(data.users,function(i, s) {
							//html +='<tr><td>'+s.identificador+'</td>';
							html +='<td><a align="left"><div class="container" style="max-width: 75px; max-height: 50px" id="imageperfil">';
							html +='<img style="text-align:center;max-width: 75px; max-height: 50px" src="'+s.foto+'" class=""></div></a></td>';
							html +='<td>'+s.name+'</td>';
							html +='<td>'+s.username+'</td>';
							html +='<td>'+(new Date(s.last_Update)).toLocaleDateString()+'</td>';
							html +='<td class="text-center"><a class="btn btn-info btn-xs" href="#invitado" OnClick="InvitarSend(\''+s.username+'\','+sala+')"><span class="glyphicon glyphicon-edit">';
							html +='</span> Invitar</a></td></tr>';
						});
						html +=' </table>';

						$("#tabladeamigos").html(html);
	                
						
					}).fail(function(jqXHR, textStatus) {
						console.log(textStatus);
						return(false);
			});
	
	
	
}

function InvitarSend(friend, sala) {
	
	var url = API_BASE_URL + "salas/"+sala+"/invitar?username="+friend;
	var objInstanceName=new jsNotifications({
		autoCloseTime : 5,
		showAlerts: true,
		title: 'Informer'
	});


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
							"Accept" : "application/vnd.informer.api.sala+json",
						},
					})
			.done(
					function(data, status, jqxhr) {	
						
						objInstanceName.show('ok','Se ha enviado la solicitud.');
						setTimeout(function(){Invitar(sala);},redirecttimeout);	
								
						
					}).fail(function(jqXHR, textStatus) {
						objInstanceName.show('error','Se ha producido un erro al enviar la solicitud.');
						setTimeout(function(){Invitar(sala);},redirecttimeout);	
						
			});	
}

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
									+ btoa(autorizacion));
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
								+ "</td><td><a href='javascript:void(0);' onClick='abrirChat("+s.identificador+")'>"
								+ s.nombre_sala
								+ "</a></td><td><input type='button' value='Invitar' OnClick='Invitar("+ s.identificador+ ")'>";
								if(s.username!=getCookie("username")){
								Stringhtml +="</td><td><input type='button' value='Abandonar' OnClick='Abandonar("
								+ s.identificador
								+ ")'>";
								} else{
								Stringhtml +="</td><td><input type='button' value='Eliminar Sala' OnClick='Eliminar("
										+ s.identificador
										+ ")'>";
								}
								
								Stringhtml +="</td><td>"
								+ (new Date(s.last_update)).toLocaleDateString()+' a las '+(new Date(s.last_update)).toLocaleTimeString() + "</td>";
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
							Stringpaginacion = "<ul class='pagination'><li class='active'><a href='#publica0' OnClick='Paginacion(0,1)'>1 <span class='sr-only'>(current)</span></a></li>";
							var i = 1;
							while(i<numpag)
								{
									Stringpaginacion += "<li><a href='#publica"+i+"' OnClick='Paginacion("+i+",1)'>"+(i+1)+" </a></li>";
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
										Stringpaginacion += "<li class='active'><a href='#publica"+i+"' OnClick='Paginacion("+i+",1)'>"+(i+1)+" <span class='sr-only'>(current)</span></a></li>";
										}
									else
										{
										Stringpaginacion += "<li><a href='#publica"+i+"' OnClick='Paginacion("+i+",1)'>"+(i+1)+" </a></li>";
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
	var objInstanceName=new jsNotifications({
		autoCloseTime : 5,
		showAlerts: true,
		title: 'Informer'
	});
	var url = API_BASE_URL + "salas/" + id + "/abandonar";
	console.log(url);
	$
			.ajax(
					{
						url : url,
						type : 'DELETE',
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
						var Stringhtml = "<div class='alert alert-success'>Has abandonado la Sala: "
								+ id + ".<p>" + data + "</div>";
						$("#post-container").html(Stringhtml);
						objInstanceName.show('ok','Sala abandonada');
						setTimeout(function(){pintar()},redirecttimeout);
						
					})
			.fail(
					function(data, status, jqXHR, textStatus) {
						console.log(data);
						var Stringhtml = "<div class='alert alert-danger'>Error interno, Usted no esta en esta sala.</div>";
						$("#post-container").html(Stringhtml);
						objInstanceName.show('error','No se ha podido abandonar la sala.');
						setTimeout(function(){pintar()},redirecttimeout);	
					});
}

function Eliminar(id) {
	var objInstanceName=new jsNotifications({
		autoCloseTime : 5,
		showAlerts: true,
		title: 'Informer'
	});
	var url = API_BASE_URL + "salas/" + id;

	$
			.ajax(
					{
						url : url,
						type : 'DELETE',
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
						var Stringhtml = "<div class='alert alert-success'>Has borrado la Sala: "
								+ id + ".<p>" + data + "</div>";
						$("#post-container").html(Stringhtml);
						objInstanceName.show('ok','Sala Eliminada');
						pintar();
					})
			.fail(
					function(data, status, jqXHR, textStatus) {
						console.log(data);
						var Stringhtml = "<div class='alert alert-danger'>Error interno o Usted no es dueño de la sala.</div>";
						$("#post-container").html(Stringhtml);
						objInstanceName.show('error','Error interno o Usted no es dueño de la sala');
						pintar();
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

function abrirChat(identificador) {
	document.cookie = "salaid=" + identificador;
	window.location = WWW_URL + "/chat_viewer.html";
}
