var API_BASE_URL = "http://localhost:8080/informer-api/";


$(document).ready(function(){
	var offset = 1;
	var length = 10;
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
	        request.setRequestHeader("Authorization", "Basic "+ btoa('McD0n3ld:McD0n3ld'));
	    },
	})
	.done(function (data, status, jqxhr) {
		var posts = $.parseJSON(jqxhr.responseText);
		var htmlString = "<div id='post-container'>";
	    $.each(data.posts, function(i,p){	
	    	htmlString += '<span id="post'+p.identificador+'">';  
        	htmlString += '<div class="panel panel-primary">';  
        	htmlString += '<div class="panel-heading"><h3 class="panel-title"><div id="post-autor">'+p.username+' ('+p.identificador+')</div><div id="post-asunto">'+p.asunto+'</div></h3></div>';  
        	htmlString += '<div class="panel-body">'; 
        	htmlString += '<div class="post-contenido">'+p.contenido+'</div>';
			htmlString += '<div class="post-date">Publicado el '+ (new Date(p.publicacion_date)).toLocaleDateString()+' a las '+(new Date(p.publicacion_date)).toLocaleTimeString()+'</div>';
			if (p.liked == 2)
				htmlString += '<div class="post-calificaciones_positivas" id="neutro_like'+p.identificador+'"><a href="#" onClick="processNeutro('+p.identificador+',\'neutro_like\')">Ya no me gusta ('+p.calificaciones_positivas+')</a></div>';
			else
				htmlString += '<div class="post-calificaciones_positivas" id="like'+p.identificador+'"><a href="#" onClick="processLike('+p.identificador+',\'like\')">Me gusta ('+p.calificaciones_positivas+')</a></div>';
			if (p.liked == 1)
				htmlString += '<div class="post-calificaciones_negativas" id="neutro_dislike'+p.identificador+'"><a href="#" onClick="processNeutro('+p.identificador+',\'neutro_dislike\')">Ya no es una puta mierda ('+p.calificaciones_negativas+')</a></div>';
			else
				htmlString += '<div class="post-calificaciones_negativas" id="dislike'+p.identificador+'"><a href="#" onClick="processDislike('+p.identificador+',\'dislike\')">Esto es una puta mierda ('+p.calificaciones_negativas+')</a></div>';
			htmlString += '<div class="post-denuncia"><a href="#" id="denuncia'+p.identificador+'" onClick="processDenuncia('+p.identificador+')">Denunciar</a></div>';
			//htmlString += '<div class="post-denuncia"><a href="#" data="'+p.links[6].uri+'">Denunciar</a></div>';
			if (p.numcomentarios == 1)
				htmlString += '<div class="post-numcomentarios"><a href="#" id="comentarios'+p.identificador+'" onClick="processComentarios('+p.identificador+')">'+p.numcomentarios+' comentario</a></div>';
			else
				htmlString += '<div class="post-numcomentarios"><a href="#" id="comentarios'+p.identificador+'" onClick="processComentarios('+p.identificador+')">'+p.numcomentarios+' comentarios</a></div>';
			htmlString += '</div></div></span><br>';
	    });
	    htmlString += "</div>";
		$('#res_get_list_posts').html(htmlString);
		console.log(posts);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
});

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
	        request.setRequestHeader("Authorization", "Basic "+ btoa('McD0n3ld:McD0n3ld'));
	    },
	})
    .done(function (data, status, jqxhr) {
   		objInstanceName.show('ok','Post denunciado correctamente');
   		$('#post'+identificador).html("");
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
	        request.setRequestHeader("Authorization", "Basic "+ btoa('McD0n3ld:McD0n3ld'));
	    },
	})
    .done(function (data, status, jqxhr) {
    	console.log(id);
   		$('#'+id+''+identificador).html('<a href="#" onClick="processNeutro('+identificador+',\''+id+'\')">Ya no me gusta ('+data.calificaciones_positivas+')</a>');
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
	        request.setRequestHeader("Authorization", "Basic "+ btoa('McD0n3ld:McD0n3ld'));
	    },
	})
    .done(function (data, status, jqxhr) {
    	console.log(id);
    	if(id="neutro_dislike")
    		$('#'+id+''+identificador).html('<a href="#" onClick="processDislike('+identificador+',\''+id+'\')">Esto es una puta mierda ('+data.calificaciones_negativas+')</a>');
    	if (id="neutro_like")
    		$('#'+id+''+identificador).html('<a href="#" onClick="processLike('+identificador+',\''+id+'\')">Me gusta ('+data.calificaciones_positivas+')</a>');
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
	        request.setRequestHeader("Authorization", "Basic "+ btoa('McD0n3ld:McD0n3ld'));
	    },
	})
    .done(function (data, status, jqxhr) {
    	console.log(id);
   		$('#'+id+''+identificador).html('<a href="#" onClick="processNeutro('+identificador+',\''+id+'\')">Ya no es una puta mierda ('+data.calificaciones_negativas+')</a>');
		console.log(status);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus+" "+url);
	});	
}
