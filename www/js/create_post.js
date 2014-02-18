function processIdea() {
	var url = API_BASE_URL + "posts";
	var visibilidad = $('#mi-post-visibilidad').text();
	if (visibilidad == "") {

	}
	var contenido = $('#mi-genialidad').val();
	var resumen = "Sin asunto";// $('#mi-resumen').val();
	var post = '{"visibilidad": "' + visibilidad + '", "contenido": "' + contenido + '", "asunto":"' + resumen + '"}';
	console.log(post);
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		data : post,
		headers : {
			"Content-Type" : "application/vnd.informer.api.post+json",
			"Accept" : "application/vnd.informer.api.post+json",
		},
		beforeSend : function(request) {
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
		},
	}).done(function(data, status, jqxhr) {
		// document.getElementById("mi-resumen").value = '';
		document.getElementById("mi-genialidad").value = '';
		console.log(data);
		setTimeout(function() {
			//window.location = WWW_URL + "/post_viewer.html";
			location.reload();
		}, redirecttimeout);
	}).fail(function(jqXHR, textStatus) {
		// console.log(textStatus + " " + url);
		// console.log(post);
	});
}

function cambiarMiVisibilidad(visibilidad) {
	$("#mi-post-visibilidad").html(visibilidad);
	if (visibilidad == 0) {
		$("#mi-post-imagen").attr("src", "img/anonymous.jpg");
		$("#mi-post-imagen").attr("style", "");
		$("#mi-post-user").html("An&oacute;nimo");
	} else if (visibilidad == 1) {
		$("#mi-post-imagen").attr("src", "img/anonymous.jpg");
		$("#mi-post-imagen").attr("style", "opacity:0.6;filter:alpha(opacity=60);");
		$("#mi-post-user").html(getCookie("username"));
	} else {
		$("#mi-post-imagen").attr("src", "");
		$("#mi-post-user").html(getCookie("username"));
	}
}

function startTime() {
	var today = new Date();
	var h = today.getHours();
	var m = today.getMinutes();
	var s = today.getSeconds();
	// add a zero in front of numbers<10
	m = checkTime(m);
	s = checkTime(s);
	document.getElementById('mi-post-fecha').innerHTML = h + ":" + m + ":" + s;
	t = setTimeout(function() {
		startTime()
	}, 500);
}

function checkTime(i) {
	if (i < 10) {
		i = "0" + i;
	}
	return i;
}

