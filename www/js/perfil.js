var autorizacion = getCookie("username") +":"+getCookie("userpass");
var loaded = 0;
var offset = 0;
var length = 15;


$(document).ready(function() {	
	
	Pintar();
});

function Pintar() {
		
	var url = API_BASE_URL + "/users/"+getCookie("username");
	
	GetUsuario(url, offset, length);	
	GetNotificaciones(getCookie("username"));	
	GetPost(getCookie("username"),0,15);
	
}
function GetNotificaciones(username) {
	
	var url = API_BASE_URL + "/user/"+username+"/notifications";

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
							"Accept" : "application/vnd.dsa.informer.notification+json",
						},
					})
			.done(
					function(data, status, jqxhr) {
						
						$("#data6").html(data.numamigos); 
						$("#data7").html(data.numpost); 
						$("#data8").html(data.numcomment); 
						$("#data9").html(data.numlikes);		
						$("#data10").html(data.numdislikes);
						$("#data11").html(data.participacion);
						
					}).fail(function(jqXHR, textStatus) {
						console.log("aki llega bien pero ta mal");
						return(false);
			});
	
}
function GetUsuario(url) {

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
							"Accept" : "application/vnd.informer.api.user+json",
						},
					})
			.done(
					function(data, status, jqxhr) {
						console.log(data);
						var html =''+data.correo+'     |     '+data.username;
						$("#data1").html(html); 
						$("#imageperfil").html('<img style="max-width: 200px; max-height: 250px" src='+data.foto+' class="">'); 						
						var estado = "Soltero";
						if(data.genero)
							sexo = "Hombre";
						if(data.estado_civil==1)
							estado="Me acaban de dejar";
						if(data.estado_civil==2)
							estado="Abierto a Sugerencias";
						if(data.estado_civil==3)
							estado="Con relacion";
						$("#data2").html(sexo); 
						$("#data4").html(estado); 
						$("#data3").html(data.fecha_nacimiento); 
						$("#data5").html(data.lugar_de_residencia); 	
						$("#data23").html(data.last_Update); 
						
					}).fail(function(jqXHR, textStatus) {
				console.log(textStatus + " " + url);
				var Stringhtml = "Error";
				$("#todo").html(Stringhtml);
			});
}
function GetPost(username,offset,length){

		var url = API_BASE_URL+"posts/novedades/"+username+"?o="+offset+"&l="+length;
		$.ajax({
			url : url,
			type : 'GET',
			crossDomain : true,
			dataType : 'json',
			headers : {
				"Accept" : "application/vnd.informer.api.post.collection+json",
			},
			beforeSend: function (request)
		    {
		        request.withCredentials = true;
		        request.setRequestHeader("Authorization", "Basic "+ btoa(autorizacion));
		    },
		})
		.done(function (data, status, jqxhr) {
			console.log(data);
			
			var html ='';
			$.each(data.posts, function(i, s) {				
				html +='<li class="list-child"><a href="#Mostrarpost('+i+')"';
			
				
				html +='class=""><div>  |  '+s.asunto+'   @Anonimo :   '+s.contenido+'  |</div> </a><div style="text-align:right;" class="glyphicon glyphicon-thumbs-up" > .'+s.calificaciones_positivas+'  |  </div>';
				html +='<div style="text-align:right;" class="glyphicon glyphicon-thumbs-down">  .'+s.calificaciones_negativas+'</div>';
				
				//Esto raul nose por que peta....
//				html +="<div class='post-container' style='display:none;' id='post-oculto"+i+"'>";				
//				
//				html += '<span id="post'+s.identificador+'">';  
//				html += '<div class="panel panel-primary">';  
//				html += '<div class="panel-heading"><h3 class="panel-title"><div class="post-autor"> anonymous ('+s.identificador+')</div><div class="post-asunto">'+s.asunto+'</div></h3></div>';  
//				html += '<div class="panel-body">'; 
//				html += '<div class="post-contenido">'+s.contenido+'</div>';
//				html += '<div class="post-date">Publicado el '+ (new Date(s.publicacion_date)).toLocaleDateString()+' a las '+(new Date(p.publicacion_date)).toLocaleTimeString()+'</div>';
//				if (s.liked == 2)
//					html += '<div class="post-calificaciones_positivas" id="neutro_like'+s.identificador+'"><a href="javascript:void(0);" onClick="processNeutro('+s.identificador+',1)">Ya no me gusta ('+s.calificaciones_positivas+')</a></div>';
//				else
//					html += '<div class="post-calificaciones_positivas" id="like'+s.identificador+'"><a href="javascript:void(0);" onClick="processLike('+s.identificador+',2)">Me gusta ('+s.calificaciones_positivas+')</a></div>';
//				if (s.liked == 1)
//					html += '<div class="post-calificaciones_negativas" id="neutro_dislike'+s.identificador+'"><a href="javascript:void(0);" onClick="processNeutro('+s.identificador+',3)">Ya no es una puta mierda ('+s.calificaciones_negativas+')</a></div>';
//				else
//					html += '<div class="post-calificaciones_negativas" id="dislike'+s.identificador+'"><a href="javascript:void(0);" onClick="processDislike('+s.identificador+',4)">Esto es una puta mierda ('+s.calificaciones_negativas+')</a></div>';
//				html += '<div class="post-denuncia"><a href="javascript:void(0);" id="denuncia'+s.identificador+'" onClick="processDenuncia('+s.identificador+')">Denunciar</a></div>';
//				if (s.numcomentarios == 1)
//					html += '<div class="post-numcomentarios" id="div-num-comentarios'+s.identificador+'"><a href="javascript:void(0);" id="num-comentarios'+s.identificador+'" onClick="processComentarios('+s.identificador+',0)">'+s.numcomentarios+' comentario</a></div>';
//				else
//					html += '<div class="post-numcomentarios" id="div-num-comentarios'+s.identificador+'"><a href="javascript:void(0);" id="num-comentarios'+s.identificador+'" onClick="processComentarios('+s.identificador+',0)">'+s.numcomentarios+' comentarios</a></div>';
//				html += '<div class="post-comentarios-container" id="comentarios-container'+s.identificador+'"></div><br>';
//				html += '<div class="post-mi-comentario-container" id="mi-comentario-container'+s.identificador+'">';
//				html += '			     <textarea class="mi-comentario-txtarea" id="mi-comentario'+s.identificador+'" maxlength=255 spellcheck="false" placeholder="Escribe un comentario..." onkeyup="$(this).css("height","auto");$(this).height(this.scrollHeight);" onkeydown="if (event.keyCode == 13) postComentario('+s.identificador+');"></textarea>';
//				html += '<select id="mi-comentario-visibilidad'+s.identificador+'" style="height: 25px; width: 120px;"><option value="0">Anónimo</option><option value="1">Sólo amigos</option><option value="2">Público</option></select>';
//				html += '			   </div></div></div></span>';
				
				html +='</div></li>';				
					});		
			
			
			$("#data12").html(html); 	
			
		})
	    .fail(function (jqXHR, textStatus) {
			console.log(textStatus);
		});
	}
function Mostrarpost(i) {
	
	var contenedor = document.getElementById("post-oculto"+i+"");
	
	if(contenedor.style.visibility == "visible")
		{
		contenedor.style.visibility = "hidden";
		contenedor.style.display = "none";
		}
	else
		{
		contenedor.style.visibility = "visible";
		contenedor.style.display = "block";
		}
	
}

