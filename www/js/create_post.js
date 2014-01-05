var API_BASE_URL = "http://localhost:8080/informer-api/";

function processIdea() {
	var url = API_BASE_URL+"posts";
	var visibilidad = $('#mi-post-visibilidad').val();
	var contenido = $('#mi-genialidad').val();
	var resumen = $('#mi-resumen').val();
	var post ='{"visibilidad": "'+ visibilidad+'", "contenido": "'+ contenido+'", "asunto":"'+resumen+'"}';
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		data: post,
		headers : {
			"Content-Type" : "application/vnd.informer.api.post+json",
			"Accept" : "application/vnd.informer.api.post+json",
		},
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa('McD0n3ld:McD0n3ld'));
	    },
	})
    .done(function (data, status, jqxhr) {
		document.getElementById("mi-resumen").value = '';
		document.getElementById("mi-genialidad").value = '';
		console.log(data);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus+" "+url);
		console.log(post);
	});	
}
