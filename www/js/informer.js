var API_BASE_URL = "http://localhost:8080/better-api";
var READY = "FALSE";

$(document).ready(function(){
	validar();
}); // end document.ready


$("#button_get_sting").click(function(e){
	e.preventDefault();
	var id = $('#id').val();
	getSting(id);	
});
 
$("#button_delete_sting").click(function(e){
	e.preventDefault();
	var id = $('#id').val();
	deleteSting(id);
});
 
$("#button_post_sting").click(function(e){
	e.preventDefault();
	READY = "FALSE";
	validar();
	if (READY == "TRUE") {
		var content = $('#noimporta_contenido').val();
		var subject = $('#noimporta_subject').val();
		var username = $('#noimporta_username').val();
		var sting ='{"content": "'+ content+'", "subject": "'+ subject+'", "username": "'+ username+'"}';
		createSting(sting);
	}
});

$("#button_get_sting_before_update").click(function(e){
	e.preventDefault();
	var id = $('#id').val();
	getStingBeforeUpdate(id);	
});

$("#button_put_sting").click(function(e){
	e.preventDefault();
	var content = $('#noimporta_contenido').val();
	var subject = $('#noimporta_subject').val();
	var id = $('#id').val();
	var sting ='{"content": "'+ content+'", "subject": "'+ subject+'", "id": "'+ id +'"}';
	createSting(sting);
	updateSting(sting, stingid);
});

$("#button_get_list_sting").click(function(e){
	e.preventDefault();
	var offset = $('#offset').val();
	var length = $('#offset').val();;
	getListSting(offset, length);
}); 
 
function getSting(stingid) {
	//var url = "/stings/"+stingid;
	var url = "http://localhost:8080/better-api/stings/"+stingid;
	var username = $.cookie("username");
	var userpass = $.cookie("userpass");
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
			"Accept" : "application/vnd.beeter.api.sting+json",
			//"Access-Control-Allow-Origin" : "*"
		},
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa(username+':'+userpass));
	    },
	})
	.done(function (data, status, jqxhr) {
		var s = $.parseJSON(jqxhr.responseText);
		var htmlString = "<table class='table'>";
		htmlString += "<tr><th>ID</th><th>Autor</th><th>Subject</th><th>Contenido</th><th>Timestamp</th></tr>";
		htmlString += "<tr>";
        htmlString += '<td>'+s.stingId+'</td>';
		htmlString += '<td>'+s.author+'</td>';
		htmlString += '<td>'+s.subject+'</td>';
		htmlString += '<td>'+s.content+'</td>';
		htmlString += '<td>'+s.creationTimestamp+'</td>';
		htmlString += "</tr>";
		htmlString +="</table>";
		$('#res_get_sting').html(htmlString);
		console.log(s);
	})
    .fail(function (jqXHR, textStatus) {
		var htmlString = "GET STING <img src='img/error.png'/>";
		$('#res_get_sting').html(htmlString);
		console.log(textStatus);
	});
}
 
function deleteSting(stingid) {
	var url = API_BASE_URL + '/stings/'+stingid;
 
	$.ajax({
		url : url,
		type : 'DELETE',
		crossDomain : true,
		dataType : 'json',
		headers : {
			"Accept" : "application/vnd.beeter.api.sting+json",
			//"Access-Control-Allow-Origin" : "*"
		},
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa('alicia:alicia'));
	    },
	})
    .done(function (data, status, jqxhr) {
    	var htmlString = "DELETE STING <img src='img/valid.png'/>";
		$('#res_delete_sting').html(htmlString);
		console.log(status);
	})
    .fail(function (jqXHR, textStatus) {
    	var htmlString = "DELETE STING: "+textStatus+ "<img src='img/error.png'/>";
		$('#res_delete_sting').html(htmlString);
		console.log(textStatus);
	});
		
}
 
 
function createSting(sting) {
	var url = API_BASE_URL + '/stings';
 
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		data : sting,
		headers : {
			"Accept" : "application/vnd.beeter.api.sting+json",
			"Content-Type" : "application/vnd.beeter.api.sting+json",
			//"Access-Control-Allow-Origin" : "*"
		},
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa('alicia:alicia'));
	    },
	})
	.done(function (data, status, jqxhr) {
		var htmlString = "<br><div class='alert alert-success' id='error'>POST STING "+status+"</div>";
		$('#return').html(htmlString);
		console.log(status);
	})
    .fail(function (jqXHR, textStatus) {
    	var htmlString = "<br><div class='alert alert-danger' id='error'>POST STING "+textStatus+"</div>";
		$('#res_post_sting').html(htmlString);
		console.log(textStatus);
	});
}


function getStingBeforeUpdate(stingid) {
	//var url = "/stings/"+stingid;
	var url = "http://localhost:8080/better-api/stings/"+stingid;
 
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
			"Accept" : "application/vnd.beeter.api.sting+json",
			//"Access-Control-Allow-Origin" : "*"
		},
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa('alicia:alicia'));
	    },
	})
	.done(function (data, status, jqxhr) {
		var sting = $.parseJSON(jqxhr.responseText);
		document.getElementById('noimporta_subject').value=data.subject;
		document.getElementById('noimporta_contenido').value=data.content;
		document.getElementById('noimporta_username').value=data.author;
		$('#res_put_sting').html("");
		console.log(sting);
	})
    .fail(function (jqXHR, textStatus) {
    	var htmlString = "GET STING <img src='img/error.png'/>";
		$('#res_put_sting').html(htmlString);
		console.log(textStatus);
	});
}

function updateSting(sting, stingid) {
	var url = API_BASE_URL + '/stings/' + stingid;
 
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		data : sting,
		headers : {
			"Accept" : "application/vnd.beeter.api.sting+json",
			"Content-Type" : "application/vnd.beeter.api.sting+json",
			//"Access-Control-Allow-Origin" : "*"
		},
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa('alicia:alicia'));
	    },
	}) //http://richardshepherd.com/how-to-use-jquery-with-a-json-flickr-feed-to-display-photos/
	.done(function (data, status, jqxhr) {
		var htmlString = "PUT STING:<br>";
		htmlString += 'ID: '+data.stingId+'<br>';
		htmlString += 'Autor: '+data.author+'<br>';
		htmlString += 'Subject: '+data.subject+'<br>';
		htmlString += 'Content: '+data.content+'<br>';
		htmlString += 'Tiemstamp: '+data.creationTimestamp+'<br>';
		$('#res_put_sting').html(htmlString);
		console.log(status);
	})
    .fail(function (jqXHR, textStatus) {
    	var htmlString = "PUT STING: "+textStatus;
    	$('#res_put_sting').text(htmlString);
		console.log(textStatus);
	});
}


function getListSting(offset, length) {
	//var url = "/stings/"+stingid;
	var url = "http://localhost:8080/better-api/stings?offset="+offset+"&length="+length+"";
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
			"Accept" : "application/vnd.beeter.api.sting.collection+json",
			//"Access-Control-Allow-Origin" : "*"
		},
		beforeSend: function (request)
	    {
	        request.withCredentials = true;
	        request.setRequestHeader("Authorization", "Basic "+ btoa('alicia:alicia'));
	    },
	})
	.done(function (data, status, jqxhr) {
		var sting = $.parseJSON(jqxhr.responseText);
		var htmlString = "<table class='table'>";
		htmlString += "<tr><th>ID</th><th>Autor</th><th>Subject</th><th>Contenido</th><th>Timestamp</th></tr>";
		 // Start putting together the HTML string
	    
	    // Now start cycling through our array of Flickr photo details
	    $.each(data.stings, function(i,s){	        
	        // Here's where we piece together the HTML
	    	htmlString += "<tr>";
	        htmlString += '<td>'+s.stingId+'</td>';
			htmlString += '<td>'+s.author+'</td>';
			htmlString += '<td>'+s.subject+'</td>';
			htmlString += '<td>'+s.content+'</td>';
			htmlString += '<td>'+s.creationTimestamp+'</td>';
			htmlString += "</tr>";
	    
	    });
	    htmlString +="</table>";
		$('#res_get_list_sting').html(htmlString);
		console.log(sting);
	})
    .fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
 
}


function validar() {
	$('#contact-form').validate(
			 {
			  rules: {
			    username: {
			      minlength: 2,
			      required: true
			    },
			    contenido: {
			      minlength: 2,
			      required: true
			      //email: true
			    },
			    subject: {
			      minlength: 2,
			      required: true
			    }
			  },
			  highlight: function(element) {
				  READY = "FALSE";
			    $(element)
			    .closest('.control-group')
			    .removeClass('success')
			    .addClass('error');
			  },
			  success: function(element) {
				  READY = "TRUE";
			    element
			    .text('OK!').addClass('valid')	//printa OK, pero en el style.css tenemos que el bacground sea el icono de OK, y emascara el texto
			    .closest('.control-group').removeClass('error').addClass('success');
			  }
			 });
}
