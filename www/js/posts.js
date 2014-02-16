var autorizacion = getCookie("username") + ":" + getCookie("userpass");
var pagina = "default";
var loaded = 0;
var offset = 0;
var length = 7;
var length_c = 2;
var cont = 0;

function getListPosts() {
	pagina = "default";
	var url = API_BASE_URL + "posts?o=" + offset + "&l=" + length;
	var url = API_BASE_URL + "posts?o=" + offset + "&l=" + length;
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
			"Accept" : "application/vnd.informer.api.post.collection+json",
		},
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		var htmlString = "<div class='post-container' id='post-container'>";
		if (loaded > 0)
			htmlString += document.getElementById('post-container').innerHTML;
		htmlString = rellenarPosts(data, htmlString)
		htmlString += "</div>";
		$('#res_get_list_posts').html(htmlString);
		$.each(data.posts, function(i, p) {
			if (p.numcomentarios > 0)
				processComentarios(p.identificador, 0);
		});
		$(".btna").popover({
		    animate: false,
		    html: true,
		    placement: 'left',
		    template: '<div class="popover" onmouseover="$(this).mouseleave(function() {$(this).hide(); });"><div class="arrow"></div><div class="popover-inner"><h3 class="popover-title"></h3><div class="popover-content">An&oacute;nimo, S&oacute;lo amigos, P&uacute;blico</div></div></div>'
		}).click(function(e) {
		    e.preventDefault();
		}).mouseenter(function(e) {
		    $(this).popover('show');
		});
		$(".post-popover").popover({
		    animate: false,
		    html: true,
		    placement: 'bottom',
		    template: '<div class="popover" onmouseover="$(this).mouseleave(function() {$(this).hide(); });"><div class="arrow"></div><div class="popover-inner"><h3 class="popover-title"></h3><div class="popover-content">An&oacute;nimo, S&oacute;lo amigos, P&uacute;blico</div></div></div>'
		}).click(function(e) {
			$(this).popover('show');
		}).mouseenter(function(e) {
			$(this).popover('show');
		});
		//console.log(data);
		console.log(getCookie("role"));
		console.log(getCookie("username"));
		console.log(getCookie("userpass"));
	}).fail(function(jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function rellenarPosts(data, htmlString) {
	var mifoto = getCookie("imagen");
	$.each(data.posts, function(i, p) {
		htmlString += '<div class="post" id="post' + p.identificador + '">'
		htmlString += '  <div class="post-imagen"><img src="' + p.asunto + '" width=50px height=50px></img></div>'
		htmlString += '  <div class="post-usuario">'
		htmlString += '  ' + p.username + ''
		htmlString += '  </div>'
		if (getCookie("username") != p.username) htmlString += '  <div class="post-opciones"><a class="post-popover" rel="popover" data-toggle="popover" data-content="<a href=\'javascript:void(0);\' onClick=\'processDenuncia('+p.identificador+');\'>Denunciar</a>" data-html="true"><img src="img/options.png"/></a></div>'
		else htmlString += '  <div class="post-opciones"><a class="post-popover" rel="popover" data-toggle="popover" data-content="<a href=\'javascript:void(0);\' onClick=\'processPostVisibilidad(0,'+p.identificador+');\'>An&oacute;nimo</a><br><a href=\'javascript:void(0);\' onClick=\'processPostVisibilidad(1,'+p.identificador+');\'>S&oacute;lo amigos</a><br><a href=\'javascript:void(0);\' onClick=\'processPostVisibilidad(2,'+p.identificador+');\'>P&uacute;blico</a>" data-html="true"><img src="img/options.png"/></a></div>'
		htmlString += '  <div class="post-contenido">'
		htmlString += p.contenido
		htmlString += '  </div>'
		if (p.liked == 2)
			htmlString += '<div class="post-gusta" id="neutro_like' + p.identificador + '"><a href="javascript:void(0);" onClick="processNeutro(' + p.identificador + ',1)">Ya no me gusta</a></div>';
		else
			htmlString += '<div class="post-gusta" id="like' + p.identificador + '"><a href="javascript:void(0);" onClick="processLike(' + p.identificador + ',2)">Me gusta</a></div>';
		if (p.liked == 1)
			htmlString += '<div class="post-disgusta" id="neutro_dislike' + p.identificador + '"><a href="javascript:void(0);" onClick="processNeutro(' + p.identificador + ',3)">Ya no es una puta mierda</a></div>';
		else
			htmlString += '<div class="post-disgusta" id="dislike' + p.identificador + '"><a href="javascript:void(0);" onClick="processDislike(' + p.identificador + ',4)">Esto es una puta mierda</a></div>';
		htmlString += '  <div class="post-fecha">' + (new Date(p.publicacion_date)).toLocaleDateString() + ' a las ' + (new Date(p.publicacion_date)).toLocaleTimeString() + '</div>'
		htmlString += '  <div class="post-triangulos"></div>'
		htmlString += '  <div class="post-opiniones" id="post-opiniones'+p.identificador+'">A ' + p.calificaciones_positivas + ' personas les gusta y ' + p.calificaciones_negativas + ' piensan que es una puta mierda</div>'
		htmlString += '  <div id="comentarios-container' + p.identificador + '"></div>';
		htmlString += '  <div id="num-comentarios-container' + p.identificador + '" style="display: none;">' + p.numcomentarios + '</div>';
		htmlString += '  <div class="post-mi-comentario">'
		htmlString += '    <div class="post-comentario-mifoto" style="background-image:url(\''+mifoto+'\');background-size:50px 50px;">'
		htmlString += '<a href="javascript:void(0);" class="btna" rel="popover" data-toggle="popover" data-content="<a href=\'javascript:void(0);\' onClick=\'cambiarVisibilidad(0,'+p.identificador+');\'>An&oacute;nimo</a><br><a href=\'javascript:void(0);\' onClick=\'cambiarVisibilidad(1,'+p.identificador+');\'>S&oacute;lo amigos</a><br><a href=\'javascript:void(0);\' onClick=\'cambiarVisibilidad(2,'+p.identificador+');\'>P&uacute;blico</a>" data-html="true">'
		htmlString += '<img src="" width=50px height=50px id="foto-comentario'+p.identificador+'"></img>'
		htmlString += '</a>'
		htmlString += '</div>'
		htmlString += '    <div id="mi-comentario-visibilidad' + p.identificador + '" style="display:none;">0</div>'
		htmlString += '    <div class="post-comentario-contenido"><textarea class="mi-comentario-txtarea" id="mi-comentario' + p.identificador + '" maxlength=255 spellcheck="false" placeholder="';
		if (p.numcomentarios == 0)
			htmlString += 'Se el primero en escribir un comentario...';
		else
			htmlString += 'Escribe un comentario...';
		htmlString += '" onkeydown="if (event.keyCode == 13) postComentario(' + p.identificador + ');else {$(this).height( 0 ); $(this).height( this.scrollHeight );}"></textarea></div>'
		htmlString += '  </div>'
		htmlString += '</div>'
	});
	return htmlString;
}

function rellenarComentarios(data, htmlString, identificador) {
	cont = 0;
	var user = getCookie("username");
	$.each(data.comentarios, function(i, c) {
		htmlString += '<div class="post-comentarios" id="comentario' + c.identificador + '">'
		if (user != c.username) htmlString += '    <div class="post-comentario-mifoto"><img src="' + c.imagen_usuario + '" width=50px height=50px title="' + c.username + '"></img></div>'
		else {
			htmlString += '    <div class="post-comentario-mifoto">'
			htmlString += '<a href="javascript:void(0);" class="comentario-popover" rel="popover" data-toggle="popover" data-content="<a href=\'javascript:void(0);\' onClick=\'processComentarioVisibilidad(0,'+c.id_post+','+c.identificador+');\'>An&oacute;nimo</a><br><a href=\'javascript:void(0);\' onClick=\'processComentarioVisibilidad(1,'+c.id_post+','+c.identificador+');\'>S&oacute;lo amigos</a><br><a href=\'javascript:void(0);\' onClick=\'processComentarioVisibilidad(2,'+c.id_post+','+c.identificador+');\'>P&uacute;blico</a>" data-html="true">'
			htmlString += '<img src="' + c.imagen_usuario + '" width=50px height=50px title="' + c.username + '"></img>'
			htmlString += '</a></div>'
		}
		htmlString += '    <div class="post-comentario-contenido" title="Publicado el ' + (new Date(c.publicacion_date)).toLocaleDateString() + ' a las ' + (new Date(c.publicacion_date)).toLocaleTimeString() + '">' + c.contenido + '</div>';
		if (user != c.username) htmlString += '    <div class="post-comentario-denuncia"><a href="javascript:void(0);" id="denuncia' + c.identificador + '" onClick="processDenunciaComentario(' + identificador + ',' + c.identificador + ')">Denunciar</a></div>';
		htmlString += '</div>';
		cont = cont + 1;
	});
	return htmlString;
}

function cambiarVisibilidad(visibilidad, identificador) {
	$("#mi-comentario-visibilidad" + identificador).html(visibilidad);
	if (visibilidad==0) {
		$("#foto-comentario" + identificador).attr("src","img/anonymous.jpg");
		$("#foto-comentario" + identificador).attr("style","");
	}
	else if (visibilidad == 1) {
		$("#foto-comentario" + identificador).attr("src","img/anonymous.jpg");
		$("#foto-comentario" + identificador).attr("style","opacity:0.6;filter:alpha(opacity=60);");
	} else {
		$("#foto-comentario" + identificador).attr("src","");
	}
}

function getRankingPosts(ranking) {
	pagina = ranking;
	var url;
	if (ranking == 1)
		url = API_BASE_URL + "posts/ranking/likes?o=" + offset + "&l=" + length;
	else if (ranking == 2)
		url = API_BASE_URL + "posts/ranking/dislikes?o=" + offset + "&l=" + length;
	else if (ranking == 3)
		url = API_BASE_URL + "posts/ranking/coments?o=" + offset + "&l=" + length;
	else
		url = API_BASE_URL + "posts?o=" + offset + "&l=" + length;
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
			"Accept" : "application/vnd.informer.api.post.collection+json",
		},
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		var posts = $.parseJSON(jqxhr.responseText);
		var htmlString = "<div class='post-container' id='post-container'>";
		if (loaded > 0)
			htmlString += document.getElementById('post-container').innerHTML;
		htmlString = rellenarPosts(data, htmlString)
		htmlString += "</div>";
		$('#res_get_ranking_posts').html(htmlString);
		console.log(posts);
	}).fail(function(jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function getListPostsDenunciados() {
	pagina = "posts_denuncias";
	var url = API_BASE_URL + "posts/denuncias?o=" + offset + "&l=" + length;
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
			"Accept" : "application/vnd.informer.api.post.collection+json",
		},
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(
			function(data, status, jqxhr) {
				// var posts = $.parseJSON(jqxhr.responseText);
				var htmlString = "<div class='post-container' id='post-container'>";
				if (loaded > 0)
					htmlString += document.getElementById('post-container').innerHTML;
				$.each(data.posts, function(i, p) {
					htmlString += '<span id="post' + p.identificador + '">';
					htmlString += '<div class="panel panel-info">';
					htmlString += '<div class="panel-heading"><h3 class="panel-title"><div class="post-autor">' + p.username + '</div><div class="post-asunto">' + p.asunto + '</div></h3></div>';
					htmlString += '<div class="panel-body" style="background-color:#F9FDFF">';
					htmlString += '<div class="post-denunciado-contenido">' + p.contenido + '</div>';
					htmlString += '<div class="post-denunciado-date">Publicado el ' + (new Date(p.publicacion_date)).toLocaleDateString() + ' a las ' + (new Date(p.publicacion_date)).toLocaleTimeString() + '</div>';
					htmlString += '<table><tr><td>'
					if (p.liked == 2)
						htmlString += '<div class="post-calificaciones_positivas" id="neutro_like' + p.identificador + '"><a href="javascript:void(0);" onClick="processNeutro(' + p.identificador + ',1)">Ya no me gusta (' + p.calificaciones_positivas
								+ ')</a></div>';
					else
						htmlString += '<div class="post-calificaciones_positivas" id="like' + p.identificador + '"><a href="javascript:void(0);" onClick="processLike(' + p.identificador + ',2)">Me gusta (' + p.calificaciones_positivas
								+ ')</a></div>';
					htmlString += '</td><td>'
					if (p.liked == 1)
						htmlString += '<div class="post-calificaciones_negativas" id="neutro_dislike' + p.identificador + '"><a href="javascript:void(0);" onClick="processNeutro(' + p.identificador + ',3)">Ya no es una puta mierda ('
								+ p.calificaciones_negativas + ')</a></div>';
					else
						htmlString += '<div class="post-calificaciones_negativas" id="dislike' + p.identificador + '"><a href="javascript:void(0);" onClick="processDislike(' + p.identificador + ',4)">Esto es una puta mierda ('
								+ p.calificaciones_negativas + ')</a></div>';
					htmlString += '</td><td>'
					htmlString += '<div class="post-denuncia"></div>';
					htmlString += '</td></tr></table>'
					if (p.numcomentarios == 1)
						htmlString += '<div class="post-numcomentarios" id="div-num-comentarios' + p.identificador + '"><a href="javascript:void(0);" id="num-comentarios' + p.identificador + '" onClick="processComentarios(' + p.identificador
								+ ',0)">' + p.numcomentarios + ' comentario</a></div>';
					else
						htmlString += '<div class="post-numcomentarios" id="div-num-comentarios' + p.identificador + '"><a href="javascript:void(0);" id="num-comentarios' + p.identificador + '" onClick="processComentarios(' + p.identificador
								+ ',0)">' + p.numcomentarios + ' comentarios</a></div>';
					htmlString += '<div class="post-moderar"><a href="javascript:void(0);" class="validar-denuncia" onClick="processModerar(' + p.identificador
							+ ')"><img src="img/valid.png"/>&nbsp;&nbsp;Validar</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0);" class="rechazar-denuncia" onClick="processEliminar(' + p.identificador
							+ ')"><img src="img/error.png"/>&nbsp;&nbsp;Eliminar</a></div>';
					htmlString += '<div class="post-comentarios-container" id="comentarios-container' + p.identificador + '"></div><br>';
					htmlString += '<div class="post-mi-comentario-container" id="mi-comentario-container' + p.identificador + '">';
					htmlString += '			     <textarea class="mi-comentario-txtarea" id="mi-comentario' + p.identificador
							+ '" maxlength=255 spellcheck="false" placeholder="Escribe un comentario..." onkeyup="$(this).css("height","auto");$(this).height(this.scrollHeight);" onkeydown="if (event.keyCode == 13) postComentario(' + p.identificador
							+ ');"></textarea>';
					htmlString += '<select id="mi-comentario-visibilidad' + p.identificador
							+ '" style="height: 25px; width: 120px;"><option value="0">Anónimo</option><option value="1">Sólo amigos</option><option value="2">Público</option></select>';
					htmlString += '			   </div></div></div></span>';
				});
				htmlString += "</div>";
				$('#res_get_list_posts').html(htmlString);
				// console.log(posts);
			}).fail(function(jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function getListComentariosDenunciados() {
	pagina = "comentarios_denuncias";
	var url = API_BASE_URL + "posts/0/comentarios/denuncias?o=" + offset + "&l=" + length;
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
			"Accept" : "application/vnd.informer.api.comentario.collection+json",
		},
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(
			function(data, status, jqxhr) {
				// var posts = $.parseJSON(jqxhr.responseText);
				var htmlString = "<div class='post-container' id='post-container'>";
				if (loaded > 0)
					htmlString += document.getElementById('post-container').innerHTML;
				$.each(data.comentarios, function(i, p) {
					htmlString += '<span id="comentario' + p.identificador + '">';
					htmlString += '<div class="panel panel-warning">';
					htmlString += '<div class="panel-heading"><h3 class="panel-title"><div class="post-autor">' + p.username + '</div><div class="post-asunto">Publicado el ' + (new Date(p.publicacion_date)).toLocaleDateString() + ' a las '
							+ (new Date(p.publicacion_date)).toLocaleTimeString() + '</div></h3></div>';
					htmlString += '<div class="panel-body" style="background-color:#FFFFE5">';
					htmlString += '<div class="comentario-denunciado-contenido">' + p.contenido + '</div>';
					htmlString += '<div class=""></div>';
					htmlString += '<div class="post-moderar"><a href="javascript:void(0);" class="validar-denuncia" onClick="processModerarComentario(' + p.id_post + ',' + p.identificador
							+ ')"><img src="img/valid.png"/>&nbsp;&nbsp;Validar</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0);" class="rechazar-denuncia" onClick="processEliminarComentario(' + p.id_post + ',' + p.identificador
							+ ')"><img src="img/error.png"/>&nbsp;&nbsp;Eliminar</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0);" onClick="processVerContenidoPostComentarioDenunciado(\'' + p.contenido_post
							+ '\')"><img src="img/warning.png"/>&nbsp;&nbsp;Ver contenido del post</a></div>';
					htmlString += '<div class="post-comentarios-container" id="comentarios-container' + p.identificador + '"></div><br>';
					htmlString += '</div></div></span>';
				});
				htmlString += "</div>";
				$('#res_get_list_comentarios_denuncias').html(htmlString);
				console.log(data.comentarios);
			}).fail(function(jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function processVerContenidoPostComentarioDenunciado(contenido) {
	var objInstanceName = new jsNotifications({
		autoCloseTime : 60,
		showAlerts : true,
		title : 'Contenido del post'
	});
	objInstanceName.show('informer', contenido);
}

function postComentario(postid) {
	var url = API_BASE_URL + "posts/" + postid + "/comentarios";
	var visibilidad = $('#mi-comentario-visibilidad' + postid).text();
	var contenido = $('#mi-comentario' + postid).val();
	var comentario = '{"visibilidad": "' + visibilidad + '", "contenido": "' + contenido + '"}';
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		data : comentario,
		headers : {
			"Content-Type" : "application/vnd.informer.api.comentario+json",
			"Accept" : "application/vnd.informer.api.comentario+json",
		},
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		document.getElementById("mi-comentario" + postid).value = '';
		var numcomentarios = $('#num-comentarios-container' + postid).text();
		$('#num-comentarios-container' + postid).text(numcomentarios + 1);
		processComentarios(postid, (numcomentarios + 1));
	}).fail(function(jqXHR, textStatus) {
		console.log(textStatus + " " + url);
	});
}

function processComentarios(identificador, caso) {
	var url = API_BASE_URL + "posts/" + identificador + "/comentarios?l=" + length_c;
	if (caso != 0)
		url = API_BASE_URL + "posts/" + identificador + "/comentarios?o=" + 0 + "&l=" + caso;
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
			"Content-Type" : "application/vnd.informer.api.comentario.collection+json",
		},
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(
			function(data, status, jqxhr) {
				var htmlString = "";
				// if (caso != 0) htmlString =
				// $('#comentarios-container' + identificador).html();
				htmlString = rellenarComentarios(data, htmlString, identificador);
				var numcomentarios = $('#num-comentarios-container' + identificador).text();
				if (numcomentarios > 2 && caso == 0) {
					htmlString += '<div class="post-mas-comentarios" id="post-mas-comentarios' + identificador + '">'
					htmlString += '    <div class="post-comentario-mifoto"></div>'
					htmlString += '    <div class="post-comentario-contenido"><a href="javascript:void(0);" onCLick="processComentarios(' + identificador + ',' + numcomentarios + ');">Ver ' + (numcomentarios - length_c)
							+ ' comentarios m&aacute;s</a></div>';
					htmlString += '</div>';
				}
				$('#comentarios-container' + identificador).html(htmlString);
				if (caso != 0)
					$('#post-mas-comentarios' + identificador).remove();
				$(".comentario-popover").popover({
				    animate: true,
				    delay: { show: 1500, hide: 1500 },
				    trigger: 'hover',
				    html: true,
				    placement: 'left',
				    template: '<div class="popover" onmouseover="$(this).mouseleave(function() {$(this).hide(); });"><div class="arrow"></div><div class="popover-inner"><h3 class="popover-title"></h3><div class="popover-content">An&oacute;nimo, S&oacute;lo amigos, P&uacute;blico</div></div></div>'
				});
			}).fail(function(jqXHR, textStatus) {
		// console.log(textStatus + " " + url);
	});
}

function processDenunciaComentario(post, comentario) {
	var objInstanceName = new jsNotifications({
		autoCloseTime : 5,
		showAlerts : true,
		title : 'Informer'
	});
	var url = API_BASE_URL + "posts/" + post + "/comentarios/" + comentario + "/denunciar";
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		objInstanceName.show('ok', 'Comentario denunciado correctamente');
		$('#comentario' + comentario).remove();
		console.log(status);
	}).fail(function(jqXHR, textStatus) {
		objInstanceName.show('error', 'Denuncia no realizada');
		console.log(textStatus + " " + url);
	});
}

function processDenuncia(identificador) {
	var objInstanceName = new jsNotifications({
		autoCloseTime : 5,
		showAlerts : true,
		title : 'Informer'
	});
	var url = API_BASE_URL + "posts/" + identificador + "/denunciar"
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		objInstanceName.show('ok', 'Post denunciado correctamente');
		$('#post' + identificador).remove();
		console.log(status);
	}).fail(function(jqXHR, textStatus) {
		objInstanceName.show('error', 'Denuncia no realizada');
		console.log(textStatus + " " + url);
	});
}

function processLike(identificador, id) {
	var url = API_BASE_URL + "posts/" + identificador + "/like";
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		console.log(data);
		if (id == 2)
			$('#like' + identificador).html('<a href="javascript:void(0);" onClick="processNeutro(' + identificador + ',2)">Ya no me gusta</a>');
		if (id == 1)
			$('#neutro_like' + identificador).html('<a href="javascript:void(0);" onClick="processNeutro(' + identificador + ',1)">Ya no me gusta</a>');
		$('#neutro_dislike' + identificador).html('<a href="javascript:void(0);" onClick="processDislike(' + identificador + ',3)">Esto es una puta mierda</a>');
		$('#dislike' + identificador).html('<a href="javascript:void(0);" onClick="processDislike(' + identificador + ',4)">Esto es una puta mierda</a>');
		$('#post-opiniones'+identificador).html('A ' + data.calificaciones_positivas + ' personas les gusta y ' + data.calificaciones_negativas + ' piensan que es una puta mierda');
	}).fail(function(jqXHR, textStatus) {
		console.log(textStatus + " " + url);
	});
}

function processNeutro(identificador, id) {
	var url = API_BASE_URL + "posts/" + identificador + "/neutro";
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		if (id == 2)
			$('#like' + identificador).html('<a href="javascript:void(0);" onClick="processLike(' + identificador + ',2)">Me gusta</a>');
		if (id == 1)
			$('#neutro_like' + identificador).html('<a href="javascript:void(0);" onClick="processLike(' + identificador + ',1)">Me gusta</a>');
		if (id == 3)
			$('#neutro_dislike' + identificador).html('<a href="javascript:void(0);" onClick="processDislike(' + identificador + ',3)">Esto es una puta mierda</a>');
		if (id == 4)
			$('#dislike' + identificador).html('<a href="javascript:void(0);" onClick="processDislike(' + identificador + ',4)">Esto es una puta mierda</a>');
		$('#post-opiniones'+identificador).html('A ' + data.calificaciones_positivas + ' personas les gusta y ' + data.calificaciones_negativas + ' piensan que es una puta mierda');
		console.log(status);
	}).fail(function(jqXHR, textStatus) {
		console.log(textStatus + " " + url);
	});
}

function processDislike(identificador, id) {
	var url = API_BASE_URL + "posts/" + identificador + "/dislike";
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		if (id == 3)
			$('#neutro_dislike' + identificador).html('<a href="javascript:void(0);" onClick="processNeutro(' + identificador + ',3)">Ya no es una puta mierda</a>');
		if (id == 4)
			$('#dislike' + identificador).html('<a href="javascript:void(0);" onClick="processNeutro(' + identificador + ',4)">Ya no es una puta mierda</a>');
		$('#like' + identificador).html('<a href="javascript:void(0);" onClick="processLike(' + identificador + ',2)">Me gusta</a>');
		$('#neutro_like' + identificador).html('<a href="javascript:void(0);" onClick="processLike(' + identificador + ',1)">Me gusta</a>');
		$('#post-opiniones'+identificador).html('A ' + data.calificaciones_positivas + ' personas les gusta y ' + data.calificaciones_negativas + ' piensan que es una puta mierda');
		console.log(status);
	}).fail(function(jqXHR, textStatus) {
		console.log(textStatus + " " + url);
	});
}

function processModerar(identificador) {
	var objInstanceName = new jsNotifications({
		autoCloseTime : 5,
		showAlerts : true,
		title : 'Informer'
	});
	var url = API_BASE_URL + "posts/" + identificador + "/moderar"
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		objInstanceName.show('ok', 'Post aceptado');
		$('#post' + identificador).remove();
		console.log(status);
	}).fail(function(jqXHR, textStatus) {
		objInstanceName.show('error', 'Moderacion no realizada');
		console.log(textStatus + " " + url);
	});
}

function processEliminar(identificador) {
	var objInstanceName = new jsNotifications({
		autoCloseTime : 5,
		showAlerts : true,
		title : 'Informer'
	});
	var url = API_BASE_URL + "posts/" + identificador + "/eliminar"
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		objInstanceName.show('warning', 'Post eliminado');
		$('#post' + identificador).remove();
		console.log(status);
	}).fail(function(jqXHR, textStatus) {
		objInstanceName.show('error', 'Moderacion no realizada');
		console.log(textStatus + " " + url);
	});
}

function processModerarComentario(postid, identificador) {
	var objInstanceName = new jsNotifications({
		autoCloseTime : 5,
		showAlerts : true,
		title : 'Informer'
	});
	var url = API_BASE_URL + "posts/" + postid + "/comentarios/" + identificador + "/moderar"
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		objInstanceName.show('ok', 'Comentario aceptado');
		$('#comentario' + identificador).remove();
		console.log(status);
	}).fail(function(jqXHR, textStatus) {
		objInstanceName.show('error', 'Moderacion no realizada');
		console.log(textStatus + " " + url);
	});
}

function processPostVisibilidad(visibilidad, identificador) {
	var objInstanceName = new jsNotifications({
		autoCloseTime : 5,
		showAlerts : true,
		title : 'Informer'
	});
	var post = '{"visibilidad": "' + visibilidad + '"}';
	var url = API_BASE_URL + "posts/" + identificador
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		data : post,
		headers : {
			"Accept" : "application/vnd.informer.api.post+json",
			"Content-Type" : "application/vnd.informer.api.post+json",
		},
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		objInstanceName.show('ok', 'Visibilidad modificada');
		console.log(status);
	}).fail(function(jqXHR, textStatus) {
		objInstanceName.show('error', 'Visibiliad no modificada');
		console.log(textStatus + " " + url);
	});
}

function processComentarioVisibilidad(visibilidad, postid, identificador) {
	var objInstanceName = new jsNotifications({
		autoCloseTime : 5,
		showAlerts : true,
		title : 'Informer'
	});
	var post = '{"visibilidad": "' + visibilidad + '"}';
	var url = API_BASE_URL + "posts/" + postid+"/comentarios/"+identificador
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		data : post,
		headers : {
			"Accept" : "application/vnd.informer.api.comentario+json",
			"Content-Type" : "application/vnd.informer.api.comentario+json",
		},
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		objInstanceName.show('ok', 'Visibilidad modificada');
		console.log(status);
	}).fail(function(jqXHR, textStatus) {
		objInstanceName.show('error', 'Visibiliad no modificada');
		console.log(textStatus + " " + url);
	});
}

function processEliminarComentario(postid, identificador) {
	var objInstanceName = new jsNotifications({
		autoCloseTime : 5,
		showAlerts : true,
		title : 'Informer'
	});
	var url = API_BASE_URL + "posts/" + postid + "/comentarios/" + identificador + "/eliminar"
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		objInstanceName.show('warning', 'Comentario eliminado');
		$('#comentario' + identificador).remove();
		console.log(status);
	}).fail(function(jqXHR, textStatus) {
		objInstanceName.show('error', 'Moderacion no realizada');
		console.log(textStatus + " " + url);
	});
}

// extra

function getheight() {
	var myWidth = 0, myHeight = 0;
	if (typeof (window.innerWidth) == 'number') {
		// Non-IE
		myWidth = window.innerWidth;
		myHeight = window.innerHeight;
	} else if (document.documentElement && (document.documentElement.clientWidth || document.documentElement.clientHeight)) {
		// IE 6+ in 'standards compliant mode'
		myWidth = document.documentElement.clientWidth;
		myHeight = document.documentElement.clientHeight;
	} else if (document.body && (document.body.clientWidth || document.body.clientHeight)) {
		// IE 4 compatible
		myWidth = document.body.clientWidth;
		myHeight = document.body.clientHeight;
	}
	var scrolledtonum = window.pageYOffset + myHeight + 2;
	var heightofbody = document.body.offsetHeight;
	if (scrolledtonum >= heightofbody) {
		loaded++;
		offset += length;
		if (pagina == "default")
			getListPosts();
		else if (pagina == "1" || pagina == "2" || pagina == "3")
			getRankingPosts(pagina);
		else if (pagina == "posts_denuncias")
			getListPostsDenunciados();
		else if (pagina == "comentarios_denuncias")
			getListComentariosDenunciados();
		else
			console.log(pagina);
	}
}

window.onscroll = getheight;