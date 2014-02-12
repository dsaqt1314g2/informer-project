var autorizacion = getCookie("username") +":"+getCookie("userpass");

var loaded = 0;
var offset = 0;
var length = 100;

var usuarioAnterior = "";
var minombre;
var fecha = Number(new Date());
var salaid = -1;
var lastIdentificador = 0;


$(document).ready(function(){
	getListMensajes();
});

function getListMensajes() {
	if (salaid == -1) {
		salaid = getCookie("salaid");
		minombre = getCookie("username");
	}
	var url = API_BASE_URL+"salas/"+salaid+"/mensajes?l="+length;
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
			"Accept" : "application/vnd.informer.api.mensaje.collection+json",
		},
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa(autorizacion));
	    },
	})
	.done(function (data, status, jqxhr) {
		var htmlString = "<div class='chat_container' id='chat_container'>";
		//if (loaded > 0) htmlString += document.getElementById('post-container').innerHTML;
		var started = 0;
	    $.each(data.mensajes, function(i,m){
	    	if (usuarioAnterior != m.username) {
	    		if (started != 0) htmlString += '</div><br>'
	    		else started=1;
		    	if (started == 1) {
		   			if (minombre == m.username) htmlString += '<div id="mensaje'+m.identificador+'" class="mensaje mio">';
		   			else htmlString += '<div id="mensaje'+m.identificador+'" class=mensaje>';
		   		}
	   			htmlString += '<div class="contenedor"><div class="username">'+m.username+'</div>';
	   			htmlString += '<div class="time">'+(new Date(m.last_update)).getHours()+':'+(new Date(m.last_update)).getMinutes()+':'+(new Date(m.last_update)).getSeconds()+'</div></div>';
	   		} else
	   			htmlString += '<br>';
	   		htmlString += m.contenido //+ '('+m.identificador+')'
	   		usuarioAnterior = m.username;
	   		lastIdentificador = m.identificador;
	    });
	    htmlString += '<span id="last-message"></span></div>'
	    htmlString += '<br>'

	    htmlString += "</div>";
		$('#res_get_list_mensajes').html(htmlString);
		var el = document.getElementById('chat_container');
		el.scrollTop = 99999999999999999;
		//console.log(posts);
	})
    .fail(function (jqXHR, textStatus) {
    	var htmlString = "<div class='chat_container' id='chat_container'>";
	    htmlString += '<span id="last-message"></span></div>'
	    htmlString += '<br>'
	    htmlString += "</div>";
		$('#res_get_list_mensajes').html(htmlString);
		var el = document.getElementById('chat_container');
		el.scrollTop = 99999999999999999;
		console.log(textStatus);
	});
}


function postMensaje() {
	var asdasdasd = Number(new Date());
	var url = API_BASE_URL+"salas/"+salaid+"/mensajes?f="+fecha;
	console.log(url);
	var url = API_BASE_URL+"salas/"+salaid+"/mensajes";
	var contenido = $('#mi-mensaje-txtarea').val();
	var mensaje ='{"contenido": "'+ contenido+'"}';
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		data: mensaje,
		headers : {
			"Content-Type" : "application/vnd.informer.api.mensaje+json",
			"Accept" : "application/vnd.informer.api.mensaje+json",
		},
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa(autorizacion));
	    },
	})
    .done(function (data, status, jqxhr) {
    	document.getElementById("mi-mensaje-txtarea").value = "";
		var el = document.getElementById('chat_container');
		var m = data;
		var htmlString = "";
		if (usuarioAnterior != m.username) {
			document.getElementById('last-message').id = 'last-message-old';
			if (minombre == m.username) htmlString += '<div id="mensaje'+m.identificador+'" class="mensaje mio">';
	   		else htmlString += '<div id="mensaje'+m.identificador+'" class=mensaje>';
	   		htmlString += '<div class="contenedor"><div class="username">'+m.username+'</div>';
	   		htmlString += '<div class="time">'+(new Date(m.last_update)).getHours()+':'+(new Date(m.last_update)).getMinutes()+':'+(new Date(m.last_update)).getSeconds()+'</div></div>';
	   		htmlString += m.contenido+'<br>';
	    	htmlString += '<span id="last-message"></span></div>'
	    	htmlString += '<br>'
			el.innerHTML = el.innerHTML + htmlString;
			usuarioAnterior = m.username;
		} else {
			document.getElementById('last-message').innerHTML += "<br>"+m.contenido;
		}
		lastIdentificador = m.identificador;
		el.scrollTop = 99999999999999999;
		console.log(data);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus+" "+url);
		console.log(mensaje);
	});	
}

function checkUpdates() {
	var url = API_BASE_URL+"salas/"+salaid+"/mensajes?id="+lastIdentificador;
	fecha = Number(new Date());
	//console.log(url);
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
			"Content-Type" : "application/vnd.informer.api.mensaje.collection+json",
		},
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa(autorizacion));
	    },
	})
    .done(function (data, status, jqxhr) {
    	document.getElementById("mi-mensaje-txtarea").value = "";
		var el = document.getElementById('chat_container');
		var m = data;
		var htmlString = "";
		started = 0;
		$.each(data.mensajes, function(i,m){
	    	if (usuarioAnterior != m.username) {
	    		if (started != 0) htmlString += '</div><br>'
	    		else {
	    			document.getElementById('last-message').id = 'last-message-old';
	    			started=1;
	    		}
		    	if (started == 1) {
		   			if (minombre == m.username) htmlString += '<div id="mensaje'+m.identificador+'" class="mensaje mio">';
		   			else htmlString += '<div id="mensaje'+m.identificador+'" class=mensaje>';
		   		}
	   			htmlString += '<div class="contenedor"><div class="username">'+m.username+'</div>';
	   			htmlString += '<div class="time">'+(new Date(m.last_update)).getHours()+':'+(new Date(m.last_update)).getMinutes()+':'+(new Date(m.last_update)).getSeconds()+'</div></div>';
	   		} else
	   			htmlString += '<br>';
	   		if (started == 0) document.getElementById('last-message').innerHTML += "<br>"+m.contenido;
	   		else htmlString += m.contenido;
	   		usuarioAnterior = m.username;
	   		lastIdentificador = m.identificador;
	    });
	    if (started == 1) {
	    	htmlString += '<span id="last-message"></span></div>'
	    	htmlString += '<br>'
	    	el.innerHTML = el.innerHTML + htmlString;
	    }
		el.scrollTop = 99999999999999999;
		//console.log(data);
	})
    .fail(function (jqXHR, textStatus) {
		//console.log(textStatus+" "+url);
	});	
}

var t=setInterval(checkUpdates,1000);

