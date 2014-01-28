var autorizacion = getCookie("username") +":"+getCookie("userpass");
var pagina = "default";
var loaded = 0;
var offset = 0;
var length = 7;

function getListPosts() {
	pagina = "default";
	var url = API_BASE_URL+"posts?o="+offset+"&l="+length;
	var url = API_BASE_URL+"posts?o="+offset+"&l="+length;
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
		//var posts = $.parseJSON(jqxhr.responseText);
		var htmlString = "<div class='post-container' id='post-container'>";
		if (loaded > 0) htmlString += document.getElementById('post-container').innerHTML;
		htmlString = rellenarPosts(data, htmlString)
	    htmlString += "</div>";
		$('#res_get_list_posts').html(htmlString);
		//console.log(posts);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function rellenarPosts(data, htmlString) {
	$.each(data.posts, function(i,p){	
    	htmlString += '<span id="post'+p.identificador+'">';  
    	htmlString += '<div class="panel panel-primary">';  
    	htmlString += '<div class="panel-heading"><h3 class="panel-title"><div class="post-autor">'+p.username+'</div><div class="post-asunto">'+p.asunto+'</div></h3></div>';  
    	htmlString += '<div class="panel-body" style="background-color:#EDF8FF;">'; 
    	htmlString += '<div class="post-contenido"><table style="width:490px;"><tr><td>'+p.contenido+'</td></tr><tr><td style="text-align: right;">';
		htmlString += 'Publicado el '+ (new Date(p.publicacion_date)).toLocaleDateString()+' a las '+(new Date(p.publicacion_date)).toLocaleTimeString()+'</td></tr></table></div>';
		htmlString += '<table><tr><td>'
		if (p.liked == 2)
			htmlString += '<div class="post-calificaciones_positivas" id="neutro_like'+p.identificador+'"><a href="javascript:void(0);" onClick="processNeutro('+p.identificador+',1)">Ya no me gusta ('+p.calificaciones_positivas+')</a></div>';
		else
			htmlString += '<div class="post-calificaciones_positivas" id="like'+p.identificador+'"><a href="javascript:void(0);" onClick="processLike('+p.identificador+',2)">Me gusta ('+p.calificaciones_positivas+')</a></div>';
		htmlString += '</td><td>'
		if (p.liked == 1)
			htmlString += '<div class="post-calificaciones_negativas" id="neutro_dislike'+p.identificador+'"><a href="javascript:void(0);" onClick="processNeutro('+p.identificador+',3)">Ya no es una puta mierda ('+p.calificaciones_negativas+')</a></div>';
		else
			htmlString += '<div class="post-calificaciones_negativas" id="dislike'+p.identificador+'"><a href="javascript:void(0);" onClick="processDislike('+p.identificador+',4)">Esto es una puta mierda ('+p.calificaciones_negativas+')</a></div>';
		htmlString += '</td><td>'
		htmlString += '<div class="post-denuncia"><a href="javascript:void(0);" id="denuncia'+p.identificador+'" onClick="processDenuncia('+p.identificador+')">Denunciar</a></div>';
		htmlString += '</td></tr><tr><td colspan=2>'
		if (p.numcomentarios == 1)
			htmlString += '<div class="post-numcomentarios" id="div-num-comentarios'+p.identificador+'"><a href="javascript:void(0);" id="num-comentarios'+p.identificador+'" onClick="processComentarios('+p.identificador+',0)">'+p.numcomentarios+' comentario</a></div>';
		else
			htmlString += '<div class="post-numcomentarios" id="div-num-comentarios'+p.identificador+'"><a href="javascript:void(0);" id="num-comentarios'+p.identificador+'" onClick="processComentarios('+p.identificador+',0)">'+p.numcomentarios+' comentarios</a></div>';
		htmlString += '</td></tr></table>';
		htmlString += '<div id="comentarios-container'+p.identificador+'"></div>';
		htmlString += '<div class="post-mi-comentario-container" id="mi-comentario-container'+p.identificador+'">';
		htmlString += '			     <textarea class="mi-comentario-txtarea" id="mi-comentario'+p.identificador+'" maxlength=255 spellcheck="false" placeholder="Escribe un comentario..." onkeyup="$(this).css("height","auto");$(this).height(this.scrollHeight);" onkeydown="if (event.keyCode == 13) postComentario('+p.identificador+');"></textarea>';
/*			htmlString += '			   <div class="dropdown-menu pull-right">';
		htmlString += '				  <button class="btn dropdown-toggle sr-only" type="button" id="dropdownMenu1" data-toggle="dropdown">';
		htmlString += '				    Dropdown';
		htmlString += '				    <span class="caret"></span>';
		htmlString += '				  </button>';
		htmlString += '				  <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">';
		htmlString += '				    <li role="presentation"><a role="menuitem" tabindex="-1" href="#">Action</a></li>';
		htmlString += '				    <li role="presentation"><a role="menuitem" tabindex="-1" href="#">Another action</a></li>';
		htmlString += '				    <li role="presentation"><a role="menuitem" tabindex="-1" href="#">Something else here</a></li>';
		htmlString += '				    <li role="presentation" class="divider"></li>';
		htmlString += '				    <li role="presentation"><a role="menuitem" tabindex="-1" href="#">Separated link</a></li>';
		htmlString += '				  </ul>';
		htmlString += '				</div>';
*/			htmlString += '<select id="mi-comentario-visibilidad'+p.identificador+'" style="height: 25px; width: 120px;"><option value="0">Anónimo</option><option value="1">Sólo amigos</option><option value="2">Público</option></select>';
		htmlString += '			   </div></div></div></span>';
    });
	return htmlString;
}

function getRankingPosts(ranking) {
	pagina = ranking;
	var url;
	if (ranking==1) url = API_BASE_URL+"posts/ranking/likes?o="+offset+"&l="+length;
	else if (ranking==2) url = API_BASE_URL+"posts/ranking/dislikes?o="+offset+"&l="+length;
	else if (ranking==3) url = API_BASE_URL+"posts/ranking/coments?o="+offset+"&l="+length;
	else url = API_BASE_URL+"posts?o="+offset+"&l="+length;
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
		var posts = $.parseJSON(jqxhr.responseText);
		var htmlString = "<div class='post-container' id='post-container'>";
		if (loaded > 0) htmlString += document.getElementById('post-container').innerHTML;
		htmlString = rellenarPosts(data, htmlString)
	    htmlString += "</div>";
		$('#res_get_ranking_posts').html(htmlString);
		console.log(posts);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}


function getListPostsDenunciados() {
	pagina = "posts_denuncias";
	var url = API_BASE_URL+"posts/denuncias?o="+offset+"&l="+length;
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
		//var posts = $.parseJSON(jqxhr.responseText);
		var htmlString = "<div class='post-container' id='post-container'>";
		if (loaded > 0) htmlString += document.getElementById('post-container').innerHTML;
	    $.each(data.posts, function(i,p){	
	    	htmlString += '<span id="post'+p.identificador+'">';  
        	htmlString += '<div class="panel panel-info">';  
        	htmlString += '<div class="panel-heading"><h3 class="panel-title"><div class="post-autor">'+p.username+'</div><div class="post-asunto">'+p.asunto+'</div></h3></div>';  
        	htmlString += '<div class="panel-body" style="background-color:#F9FDFF">'; 
        	htmlString += '<div class="post-denunciado-contenido">'+p.contenido+'</div>';
			htmlString += '<div class="post-denunciado-date">Publicado el '+ (new Date(p.publicacion_date)).toLocaleDateString()+' a las '+(new Date(p.publicacion_date)).toLocaleTimeString()+'</div>';
			htmlString += '<table><tr><td>'
			if (p.liked == 2)
				htmlString += '<div class="post-calificaciones_positivas" id="neutro_like'+p.identificador+'"><a href="javascript:void(0);" onClick="processNeutro('+p.identificador+',1)">Ya no me gusta ('+p.calificaciones_positivas+')</a></div>';
			else
				htmlString += '<div class="post-calificaciones_positivas" id="like'+p.identificador+'"><a href="javascript:void(0);" onClick="processLike('+p.identificador+',2)">Me gusta ('+p.calificaciones_positivas+')</a></div>';
			htmlString += '</td><td>'
			if (p.liked == 1)
				htmlString += '<div class="post-calificaciones_negativas" id="neutro_dislike'+p.identificador+'"><a href="javascript:void(0);" onClick="processNeutro('+p.identificador+',3)">Ya no es una puta mierda ('+p.calificaciones_negativas+')</a></div>';
			else
				htmlString += '<div class="post-calificaciones_negativas" id="dislike'+p.identificador+'"><a href="javascript:void(0);" onClick="processDislike('+p.identificador+',4)">Esto es una puta mierda ('+p.calificaciones_negativas+')</a></div>';
			htmlString += '</td><td>'
			htmlString += '<div class="post-denuncia"></div>';
			htmlString += '</td></tr></table>'
			if (p.numcomentarios == 1)
				htmlString += '<div class="post-numcomentarios" id="div-num-comentarios'+p.identificador+'"><a href="javascript:void(0);" id="num-comentarios'+p.identificador+'" onClick="processComentarios('+p.identificador+',0)">'+p.numcomentarios+' comentario</a></div>';
			else
				htmlString += '<div class="post-numcomentarios" id="div-num-comentarios'+p.identificador+'"><a href="javascript:void(0);" id="num-comentarios'+p.identificador+'" onClick="processComentarios('+p.identificador+',0)">'+p.numcomentarios+' comentarios</a></div>';
			htmlString += '<div class="post-moderar"><a href="javascript:void(0);" class="validar-denuncia" onClick="processModerar('+p.identificador+')"><img src="img/valid.png"/>&nbsp;&nbsp;Validar</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0);" class="rechazar-denuncia" onClick="processEliminar('+p.identificador+')"><img src="img/error.png"/>&nbsp;&nbsp;Eliminar</a></div>';
			htmlString += '<div class="post-comentarios-container" id="comentarios-container'+p.identificador+'"></div><br>';
			htmlString += '<div class="post-mi-comentario-container" id="mi-comentario-container'+p.identificador+'">';
			htmlString += '			     <textarea class="mi-comentario-txtarea" id="mi-comentario'+p.identificador+'" maxlength=255 spellcheck="false" placeholder="Escribe un comentario..." onkeyup="$(this).css("height","auto");$(this).height(this.scrollHeight);" onkeydown="if (event.keyCode == 13) postComentario('+p.identificador+');"></textarea>';
			htmlString += '<select id="mi-comentario-visibilidad'+p.identificador+'" style="height: 25px; width: 120px;"><option value="0">Anónimo</option><option value="1">Sólo amigos</option><option value="2">Público</option></select>';
			htmlString += '			   </div></div></div></span>';
	    });
	    htmlString += "</div>";
		$('#res_get_list_posts').html(htmlString);
		//console.log(posts);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function getListComentariosDenunciados() {
	pagina = "comentarios_denuncias";
	var url = API_BASE_URL+"posts/0/comentarios/denuncias?o="+offset+"&l="+length;
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
			"Accept" : "application/vnd.informer.api.comentario.collection+json",
		},
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa(autorizacion));
	    },
	})
	.done(function (data, status, jqxhr) {
		//var posts = $.parseJSON(jqxhr.responseText);
		var htmlString = "<div class='post-container' id='post-container'>";
		if (loaded > 0) htmlString += document.getElementById('post-container').innerHTML;
	    $.each(data.comentarios, function(i,p){	
	    	htmlString += '<span id="comentario'+p.identificador+'">';  
        	htmlString += '<div class="panel panel-warning">';  
        	htmlString += '<div class="panel-heading"><h3 class="panel-title"><div class="post-autor">'+p.username+'</div><div class="post-asunto">Publicado el '+ (new Date(p.publicacion_date)).toLocaleDateString()+' a las '+(new Date(p.publicacion_date)).toLocaleTimeString()+'</div></h3></div>';  
        	htmlString += '<div class="panel-body" style="background-color:#FFFFE5">'; 
        	htmlString += '<div class="comentario-denunciado-contenido">'+p.contenido+'</div>';
			htmlString += '<div class=""></div>';
			htmlString += '<div class="post-moderar"><a href="javascript:void(0);" class="validar-denuncia" onClick="processModerarComentario('+p.id_post+','+p.identificador+')"><img src="img/valid.png"/>&nbsp;&nbsp;Validar</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0);" class="rechazar-denuncia" onClick="processEliminarComentario('+p.id_post+','+p.identificador+')"><img src="img/error.png"/>&nbsp;&nbsp;Eliminar</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0);" onClick="processVerContenidoPostComentarioDenunciado(\''+p.contenido_post+'\')"><img src="img/warning.png"/>&nbsp;&nbsp;Ver contenido del post</a></div>';
			htmlString += '<div class="post-comentarios-container" id="comentarios-container'+p.identificador+'"></div><br>';
			htmlString += '</div></div></span>';
	    });
	    htmlString += "</div>";
		$('#res_get_list_comentarios_denuncias').html(htmlString);
		console.log(data.comentarios);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function processVerContenidoPostComentarioDenunciado(contenido) {
	var objInstanceName=new jsNotifications({
		autoCloseTime : 60,
		showAlerts: true,
		title: 'Contenido del post'
	});
	objInstanceName.show('informer',contenido);
}


function postComentario(postid) {
	var url = API_BASE_URL+"posts/"+postid+"/comentarios";
	var visibilidad = $('#mi-comentario-visibilidad'+postid).val();
	var contenido = $('#mi-comentario'+postid).val();
	var comentario ='{"visibilidad": "'+ visibilidad+'", "contenido": "'+ contenido+'"}';
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		data: comentario,
		headers : {
			"Content-Type" : "application/vnd.informer.api.comentario+json",
			"Accept" : "application/vnd.informer.api.comentario+json",
		},
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa(autorizacion));
	    },
	})
    .done(function (data, status, jqxhr) {
		var txtArea = document.getElementById("mi-comentario"+postid);
		txtArea.value = '';
		processComentarios(postid,0);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus+" "+url);
	});	
}


function processComentarios(identificador, caso) {
	if (caso == 1) {
		$('#comentarios-container'+identificador).html("");
		var contenido = document.getElementById('num-comentarios'+identificador).innerHTML;
		$('#div-num-comentarios'+identificador).html('<a href="javascript:void(0);" id="num-comentarios'+identificador+'" onClick="processComentarios('+identificador+',0)">'+contenido+'</a>');
	}
	else {
		var url = API_BASE_URL+"posts/"+identificador+"/comentarios";
		$.ajax({
			url : url,
			type : 'GET',
			crossDomain : true,
			dataType : 'json',
			headers : {
				"Content-Type" : "application/vnd.informer.api.comentario.collection+json",
			},
			beforeSend: function (request)
		    {
		        request.withCredentials = true;
		        request.setRequestHeader("Authorization", "Basic "+ btoa(autorizacion));
		    },
		})
	    .done(function (data, status, jqxhr) {
			var htmlString = "";
			cont=0;
		    $.each(data.comentarios, function(i,c){	
		    	//htmlString += '<span class="comentario'+c.identificador+">";
	        	htmlString += '<div class="well" style="padding:2px 10px 2px 10px;width:480px;margin-left: auto; margin-bottom:10px;" id="comentario'+c.identificador+'">';
	        	htmlString += '<table style="width:460px"><tr><td>'
	        	htmlString += '<h4>'+c.username+'</h4></td><td style="text-align:right;"> ...el '+ (new Date(c.publicacion_date)).toLocaleDateString()+' a las '+(new Date(c.publicacion_date)).toLocaleTimeString()+'</td></tr><tr><td colspan=2>';
	        	htmlString += c.contenido+'</td></tr><tr><td colspan=2 style="text-align:right;">';
	        	htmlString += '<div class="comentario-denuncia"><a href="javascript:void(0);" id="denuncia'+c.identificador+'" onClick="processDenunciaComentario('+identificador+','+c.identificador+')">Denunciar</a></div></td></tr></table>';
	        	htmlString += '</div>';
	        	//htmlString += '</span>';
	        	cont = cont+1;
		    });
		    if (cont == 1) $('#div-num-comentarios'+identificador).html('<a href="javascript:void(0);" id="num-comentarios'+identificador+'" onClick="processComentarios('+identificador+',1)">'+cont+' comentario</a>');
		    else $('#div-num-comentarios'+identificador).html('<a href="javascript:void(0);" id="num-comentarios'+identificador+'" onClick="processComentarios('+identificador+',1)">'+cont+' comentarios</a>');
			$('#comentarios-container'+identificador).html(htmlString);
		})
	    .fail(function (jqXHR, textStatus) {
			console.log(textStatus+" "+url);
		});	
	}
}

function processDenunciaComentario(post,comentario) {
	var objInstanceName=new jsNotifications({
		autoCloseTime : 5,
		showAlerts: true,
		title: 'Informer'
	});
	var url = API_BASE_URL+"posts/"+post+"/comentarios/"+comentario+"/denunciar";
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa(autorizacion));
	    },
	})
    .done(function (data, status, jqxhr) {
   		objInstanceName.show('ok','Comentario denunciado correctamente');
   		$('#comentario'+comentario).remove();
		console.log(status);
	})
    .fail(function (jqXHR, textStatus) {
    	objInstanceName.show('error','Denuncia no realizada');
		console.log(textStatus+" "+url);
	});	
}

function processDenuncia(identificador) {
	var objInstanceName=new jsNotifications({
		autoCloseTime : 5,
		showAlerts: true,
		title: 'Informer'
	});
	var url = API_BASE_URL+"posts/"+identificador+"/denunciar"
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa(autorizacion));
	    },
	})
    .done(function (data, status, jqxhr) {
   		objInstanceName.show('ok','Post denunciado correctamente');
   		$('#post'+identificador).remove();
		console.log(status);
	})
    .fail(function (jqXHR, textStatus) {
    	objInstanceName.show('error','Denuncia no realizada');
		console.log(textStatus+" "+url);
	});	
}

function processLike(identificador, id) {
	var url = API_BASE_URL+"posts/"+identificador+"/like";
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa(autorizacion));
	    },
	})
    .done(function (data, status, jqxhr) {
    	if (id == 2)
    		$('#like'+identificador).html('<a href="javascript:void(0);" onClick="processNeutro('+identificador+',2)">Ya no me gusta ('+data.calificaciones_positivas+')</a>');
    	if (id == 1)
    		$('#neutro_like'+identificador).html('<a href="javascript:void(0);" onClick="processNeutro('+identificador+',1)">Ya no me gusta ('+data.calificaciones_positivas+')</a>');
    	$('#neutro_dislike'+identificador).html('<a href="javascript:void(0);" onClick="processDislike('+identificador+',3)">Esto es una puta mierda ('+data.calificaciones_negativas+')</a>');
    	$('#dislike'+identificador).html('<a href="javascript:void(0);" onClick="processDislike('+identificador+',4)">Esto es una puta mierda ('+data.calificaciones_negativas+')</a>');
		console.log(status);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus+" "+url);
	});	
}

function processNeutro(identificador, id) {
	var url = API_BASE_URL+"posts/"+identificador+"/neutro";
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa(autorizacion));
	    },
	})
    .done(function (data, status, jqxhr) {
    	if (id == 2)
    		$('#like'+identificador).html('<a href="javascript:void(0);" onClick="processLike('+identificador+',2)">Me gusta ('+data.calificaciones_positivas+')</a>');
    	if (id == 1)
    		$('#neutro_like'+identificador).html('<a href="javascript:void(0);" onClick="processLike('+identificador+',1)">Me gusta ('+data.calificaciones_positivas+')</a>');
    	if (id == 3)
    		$('#neutro_dislike'+identificador).html('<a href="javascript:void(0);" onClick="processDislike('+identificador+',3)">Esto es una puta mierda ('+data.calificaciones_negativas+')</a>');
    	if (id == 4)
    		$('#dislike'+identificador).html('<a href="javascript:void(0);" onClick="processDislike('+identificador+',4)">Esto es una puta mierda ('+data.calificaciones_negativas+')</a>');
		console.log(status);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus+" "+url);
	});	
}

function processDislike(identificador, id) {
	var url = API_BASE_URL+"posts/"+identificador+"/dislike";
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa(autorizacion));
	    },
	})
    .done(function (data, status, jqxhr) {
    	if (id == 3)
    		$('#neutro_dislike'+identificador).html('<a href="javascript:void(0);" onClick="processNeutro('+identificador+',3)">Ya no es una puta mierda ('+data.calificaciones_negativas+')</a>');
    	if (id == 4)
    		$('#dislike'+identificador).html('<a href="javascript:void(0);" onClick="processNeutro('+identificador+',4)">Ya no es una puta mierda ('+data.calificaciones_negativas+')</a>');
    	$('#like'+identificador).html('<a href="javascript:void(0);" onClick="processLike('+identificador+',2)">Me gusta ('+data.calificaciones_positivas+')</a>');
    	$('#neutro_like'+identificador).html('<a href="javascript:void(0);" onClick="processLike('+identificador+',1)">Me gusta ('+data.calificaciones_positivas+')</a>');
		console.log(status);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus+" "+url);
	});	
}


function processModerar(identificador) {
	var objInstanceName=new jsNotifications({
		autoCloseTime : 5,
		showAlerts: true,
		title: 'Informer'
	});
	var url = API_BASE_URL+"posts/"+identificador+"/moderar"
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa(autorizacion));
	    },
	})
    .done(function (data, status, jqxhr) {
   		objInstanceName.show('ok','Post aceptado');
   		$('#post'+identificador).remove();
		console.log(status);
	})
    .fail(function (jqXHR, textStatus) {
    	objInstanceName.show('error','Moderacion no realizada');
		console.log(textStatus+" "+url);
	});	
}

function processEliminar(identificador) {
	var objInstanceName=new jsNotifications({
		autoCloseTime : 5,
		showAlerts: true,
		title: 'Informer'
	});
	var url = API_BASE_URL+"posts/"+identificador+"/eliminar"
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa(autorizacion));
	    },
	})
    .done(function (data, status, jqxhr) {
   		objInstanceName.show('warning','Post eliminado');
   		$('#post'+identificador).remove();
		console.log(status);
	})
    .fail(function (jqXHR, textStatus) {
    	objInstanceName.show('error','Moderacion no realizada');
		console.log(textStatus+" "+url);
	});	
}

function processModerarComentario(postid, identificador) {
	var objInstanceName=new jsNotifications({
		autoCloseTime : 5,
		showAlerts: true,
		title: 'Informer'
	});
	var url = API_BASE_URL+"posts/"+postid+"/comentarios/"+identificador+"/moderar"
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa(autorizacion));
	    },
	})
    .done(function (data, status, jqxhr) {
   		objInstanceName.show('ok','Comentario aceptado');
   		$('#comentario'+identificador).remove();
		console.log(status);
	})
    .fail(function (jqXHR, textStatus) {
    	objInstanceName.show('error','Moderacion no realizada');
		console.log(textStatus+" "+url);
	});	
}

function processEliminarComentario(postid, identificador) {
	var objInstanceName=new jsNotifications({
		autoCloseTime : 5,
		showAlerts: true,
		title: 'Informer'
	});
	var url = API_BASE_URL+"posts/"+postid+"/comentarios/"+identificador+"/eliminar"
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa(autorizacion));
	    },
	})
    .done(function (data, status, jqxhr) {
   		objInstanceName.show('warning','Comentario eliminado');
   		$('#comentario'+identificador).remove();
		console.log(status);
	})
    .fail(function (jqXHR, textStatus) {
    	objInstanceName.show('error','Moderacion no realizada');
		console.log(textStatus+" "+url);
	});	
}


//extra

function getheight() {
    var myWidth = 0,
        myHeight = 0;
    if (typeof(window.innerWidth) == 'number') {
        //Non-IE
        myWidth = window.innerWidth;
        myHeight = window.innerHeight;
    } else if (document.documentElement && (document.documentElement.clientWidth || document.documentElement.clientHeight)) {
        //IE 6+ in 'standards compliant mode'
        myWidth = document.documentElement.clientWidth;
        myHeight = document.documentElement.clientHeight;
    } else if (document.body && (document.body.clientWidth || document.body.clientHeight)) {
        //IE 4 compatible
        myWidth = document.body.clientWidth;
        myHeight = document.body.clientHeight;
    }
    var scrolledtonum = window.pageYOffset + myHeight + 2;
    var heightofbody = document.body.offsetHeight;
    if (scrolledtonum >= heightofbody) {
    	loaded++;
    	offset += length;
        if (pagina == "default") getListPosts();
        else if (pagina == "1" || pagina == "2" ||pagina == "3") getRankingPosts(pagina);
        else if (pagina == "posts_denuncias") getListPostsDenunciados();
        else if (pagina == "comentarios_denuncias") getListComentariosDenunciados();
        else console.log(pagina);
    }
}

window.onscroll = getheight;