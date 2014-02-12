var autorizacion = getCookie("username") + ":" + getCookie("userpass");
var loaded = 0;
var offset = 0;
var length = 15;

$(document).ready(function () {

    Pintar();
});

function Pintar() {
    var url = API_BASE_URL + "/users/" + getCookie("username");
    GetUsuario(url, offset, length);
    GetNotificaciones(getCookie("username"));
    GetPost(getCookie("username"), 0, 15);

}

function GetNotificaciones(username) {

    var url = API_BASE_URL + "/user/" + username + "/notifications";

    $
        .ajax({
            url: url,
            type: 'GET',
            crossDomain: true,
            dataType: 'json',
            beforeSend: function (request) {
                request.withCredentials = true;
                request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
            },
            headers: {
                "Accept": "application/vnd.dsa.informer.notification+json",
            },
        })
        .done(
            function (data, status, jqxhr) {

                $("#data6").html(data.numamigos);
                $("#data7").html(data.numpost);
                $("#data8").html(data.numcomment);
                $("#data9").html(data.numlikes);
                $("#data10").html(data.numdislikes);
                $("#data11").html(data.participacion);

                $("#data40").html(data.n_i_sala);
                $("#data41").html(data.n_s_amistad);

                var string = '<td><span	class="glyphicon glyphicon-eye-open text-success"></span><a';
                string += ' href="#Amigos" OnClick="Amigos(\'' + username + '\')" class="">&nbsp;Ver Amigos</a>';
                $("#data51").html(string);

            }).fail(function (jqXHR, textStatus) {
            // console.log("aki llega bien pero ta mal");
            return (false);
        });

}

function GetUsuario(url) {
    $.ajax({
        url: url,
        type: 'GET',
        crossDomain: true,
        dataType: 'json',
        beforeSend: function (request) {
            request.withCredentials = true;
            request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
        },
        headers: {
            "Accept": "application/vnd.informer.api.user+json",
        },
    })
        .done(
            function (data, status, jqxhr) {
                console.log(data);
                $("#data1").html(data.username);
                $("#imageperfil").html(
                    '<img style="max-width: 200px; max-height: 250px" src=' + data.foto + '>');
                var estado = "Soltero";
                if (data.genero)
                    sexo = "Hombre";
                if (data.estado_civil == 1)
                    estado = "Me acaban de dejar";
                if (data.estado_civil == 2)
                    estado = "Abierto a Sugerencias";
                if (data.estado_civil == 3)
                    estado = "Con relacion";
                $("#data2").html(sexo);
                $("#data4").html(estado);
                $("#data3").html(
                    (new Date(data.fecha_nacimiento))
                    .toLocaleDateString());
                $("#data5").html(data.lugar_de_residencia);
                $("#data23").html(
                    (new Date(data.last_Update))
                    .toLocaleDateString() + ' a las ' + (new Date(data.last_Update))
                    .toLocaleTimeString());
            }).fail(function (jqXHR, textStatus) {
            console.log(textStatus + " " + url);
            var Stringhtml = "Error";
            $("#todo").html(Stringhtml);
        });
}

function GetPost(username, offset, length) {

    var url = API_BASE_URL + "posts/novedades/" + username + "?o=" + offset + "&l=" + length;
    $
        .ajax({
            url: url,
            type: 'GET',
            crossDomain: true,
            dataType: 'json',
            headers: {
                "Accept": "application/vnd.informer.api.post.collection+json",
            },
            beforeSend: function (request) {
                request.withCredentials = true;
                request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
            },
        })
        .done(
            function (data, status, jqxhr) {
                console.log(data);

                var html = '';
                $
                    .each(
                        data.posts,
                        function (i, s) {
                            html += '<li class="list-child"><a href="javascript:void(0);" onClick="Mostrarpost(' + i + ')"';

                            html += 'class=""><div>  |  ' + s.asunto + '   @' + s.username + ': ' + s.contenido + '  |</div> </a><div style="text-align:right;" class="glyphicon glyphicon-thumbs-up" >&nbsp;' + s.calificaciones_positivas + ' |&nbsp;</div>';
                            html += '<div style="text-align:right;" class="glyphicon glyphicon-thumbs-down">&nbsp;' + s.calificaciones_negativas + '</div>';

                            // Esto raul nose por que peta....
                            html += "<div class='post-container' style='display:none;' id='post-oculto" + i + "'>";

                            html += '<span id="post' + s.identificador + '">';
                            html += '<div class="panel panel-primary">';
                            html += '<div class="panel-heading"><h3 class="panel-title"><div class="post-autor">' + s.username + '</div><div class="post-asunto">' + s.asunto + '</div></h3></div>';
                            html += '<div class="panel-body" style="background-color:#EDF8FF;">';
                            html += '<div class="post-contenido"><table style="width:490px;"><tr><td>' + s.contenido + '</td></tr><tr><td style="text-align: right;">';
                            html += 'Publicado el ' + (new Date(
                                s.publicacion_date))
                                .toLocaleDateString() + ' a las ' + (new Date(
                                    s.publicacion_date))
                                .toLocaleTimeString() + '</td></tr></table></div>';
                            html += '<table><tr><td>'
                            if (s.liked == 2)
                                html += '<div class="post-calificaciones_positivas" id="neutro_like' + s.identificador + '"><a href="javascript:void(0);" onClick="processNeutro(' + s.identificador + ',1)">Ya no me gusta (' + s.calificaciones_positivas + ')</a></div>';
                            else
                                html += '<div class="post-calificaciones_positivas" id="like' + s.identificador + '"><a href="javascript:void(0);" onClick="processLike(' + s.identificador + ',2)">Me gusta (' + s.calificaciones_positivas + ')</a></div>';
                            html += '</td><td>'
                            if (s.liked == 1)
                                html += '<div class="post-calificaciones_negativas" id="neutro_dislike' + s.identificador + '"><a href="javascript:void(0);" onClick="processNeutro(' + s.identificador + ',3)">Ya no es una puta mierda (' + s.calificaciones_negativas + ')</a></div>';
                            else
                                html += '<div class="post-calificaciones_negativas" id="dislike' + s.identificador + '"><a href="javascript:void(0);" onClick="processDislike(' + s.identificador + ',4)">Esto es una puta mierda (' + s.calificaciones_negativas + ')</a></div>';
                            html += '</td><td>'
                            html += '<div class="post-denuncia"><a href="javascript:void(0);" id="denuncia' + s.identificador + '" onClick="processDenuncia(' + s.identificador + ')">Denunciar</a></div>';
                            html += '</td></tr><tr><td colspan=2>'
                            if (s.numcomentarios == 1)
                                html += '<div class="post-numcomentarios" id="div-num-comentarios' + s.identificador + '"><a href="javascript:void(0);" id="num-comentarios' + s.identificador + '" onClick="processComentarios(' + s.identificador + ',0)">' + s.numcomentarios + ' comentario</a></div>';
                            else
                                html += '<div class="post-numcomentarios" id="div-num-comentarios' + s.identificador + '"><a href="javascript:void(0);" id="num-comentarios' + s.identificador + '" onClick="processComentarios(' + s.identificador + ',0)">' + s.numcomentarios + ' comentarios</a></div>';
                            html += '</td></tr></table>';
                            html += '<div id="comentarios-container' + s.identificador + '"></div>';
                            html += '<div class="post-mi-comentario-container" id="mi-comentario-container' + s.identificador + '">';
                            html += '			     <textarea class="mi-comentario-txtarea" id="mi-comentario' + s.identificador + '" maxlength=255 spellcheck="false" placeholder="Escribe un comentario..." onkeyup="$(this).css("height","auto");$(this).height(this.scrollHeight);" onkeydown="if (event.keyCode == 13) postComentario(' + s.identificador + ');"></textarea>';
                            html += '<select id="mi-comentario-visibilidad' + s.identificador + '" style="height: 25px; width: 120px;"><option value="0">Anónimo</option><option value="1">Sólo amigos</option><option value="2">Público</option></select>';
                            html += '			   </div></div></div></span>';

                            html += '</div></li>';
                        });

                $("#data12").html(html);

            }).fail(function (jqXHR, textStatus) {
            console.log(textStatus);
        });
}

function Mostrarpost(i) {

    var contenedor = document.getElementById("post-oculto" + i + "");

    if (contenedor.style.visibility == "visible") {
        contenedor.style.visibility = "hidden";
        contenedor.style.display = "none";
    } else {
        contenedor.style.visibility = "visible";
        contenedor.style.display = "block";
    }

}

function PanelControl() {
    $('#listStuff').load('paneledit.html');
    GetUserDates(getCookie("username"));
}

function Amigos(username) {
    if (username == null)
        username = getCookie("username");
    $('#listStuff').load('veramigos.html');
    Getamigos(username);
}

function Solicitudes() {
    $('#listStuff').load('veramigos.html');
    GetSolicitudes(getCookie("username"));
}

function GetSolicitudes(username) {
    // añadir ofset y length con paginacion
    var url = API_BASE_URL + "users/solicitudes?o=0&l=20";
    console.log(url);
    $
        .ajax({
            url: url,
            type: 'GET',
            crossDomain: true,
            dataType: 'json',
            beforeSend: function (request) {
                request.withCredentials = true;
                request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
            },
            headers: {
                "Accept": "application/vnd.informer.api.user.collection+json",
            },
        })
        .done(
            function (data, status, jqxhr) {
                console.log(data);
                var html = "";
                $.each(data.users,function (i, s) {
                    html += '<tr><td>' + s.identificador + '</td>';
                    html += '<td class="text-center"><a align="left"><div class="container" style="max-width: 75px; max-height: 50px" id="imageperfil">';
                    html += '<img style="max-width: 75px; max-height: 50px text-align:left;" src="' + s.foto + '"></div></a></td>';
                    html += '<td class="text-center">' + s.name + '</td>';
                    html += '<td class="text-center">' + s.username + '</td>';
                    html += '<td class="text-center">' + s.last_Update + '</td>';
                    html += '<td class="text-center"><a class="btn btn-info btn-xs" href="#aceptar" OnClick="AceptarAmistad(\'' + s.username + '\')"><span class="glyphicon glyphicon-edit">';
                    html += '</span> Aceptar Solicitud </a> <a href="#eliminar" OnClick="EliminarAmistad(\'' + s.username + '\')" class="btn btn-danger btn-xs" ><span class="glyphicon glyphicon-remove">';
                    html += '</span> Rechazar Solicitud</a></td></tr>';
                });
                $("#perfiles-amigos").html(html);
            }).fail(function (jqXHR, textStatus) {
            console.log(textStatus);
            return (false);
        });

}

function Getamigos(username) {

    // añadir ofset y length con paginacion
    var url = API_BASE_URL + "users/" + username + "/amigos?o=0&l=20";
    console.log(url);
    $
        .ajax({
            url: url,
            type: 'GET',
            crossDomain: true,
            dataType: 'json',
            beforeSend: function (request) {
                request.withCredentials = true;
                request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
            },
            headers: {
                "Accept": "application/vnd.informer.api.user.collection+json",
            },
        })
        .done(
            function (data, status, jqxhr) {
                console.log(data);
                var html = "";
                $.each(data.users,function (i, s) {
                    html += '<tr><td class="text-center"><a align="left"><div class="container" style="max-width: 75px; max-height: 50px" id="imageperfil">';
                    html += '<img style="text-align:center;max-width: 75px; max-height: 50px" src="' + s.foto + '" class=""></div></a></td>';
                    html += '<td class="text-center">' + s.username + '</td>';
                    html += '<td class="text-center">' + (new Date(s.last_Update)).toLocaleDateString() + '</td>';
                    html += '<td class="text-center"><a class="btn btn-info btn-xs" href="perfil.html?user=' + s.username + '" ><span class="glyphicon glyphicon-edit">';
                    html += '</span> Ver Perfil </a> <a href="#eliminar" OnClick="EliminarAmistad(\'' + s.username + '\')" class="btn btn-danger btn-xs" ><span class="glyphicon glyphicon-remove">';
                    html += '</span> Eliminar Amistad</a></td></tr>';
                });
                $("#perfiles-amigos").html(html);
            }).fail(function (jqXHR, textStatus) {
            console.log(textStatus);
            return (false);
        });

}

function EliminarAmistad(username) {

    console.log("Aqui llega 3");
    var objInstanceName = new jsNotifications({
        autoCloseTime: 5,
        showAlerts: true,
        title: 'Informer'
    });

    var url = API_BASE_URL + "users/" + username + "/deletefriend";
    console.log(url);
    $.ajax({
        url: url,
        type: 'DELETE',
        crossDomain: true,
        dataType: 'json',
        beforeSend: function (request) {
            request.withCredentials = true;
            request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
        },
    }).done(function (data, status, jqxhr) {
        console.log(data);
        console.log(status);
        console.log("si lo hahehco bien");
        objInstanceName.show('ok', 'Se ha eliminado la amistad.');
        setTimeout(function () {
            Getamigos(getCookie("username"));
        }, redirecttimeout);

    }).fail(
        function (jqXHR, textStatus) {
            console.log(textStatus);
            console.log(jqXHR);
            console.log("no lo hahehco bien");
            objInstanceName.show('error',
                'No se ha podido eliminar la amistad contacte admin.');
            setTimeout(function () {
                Getamigos(getCookie("username"));
            }, redirecttimeout);

        });

}

function AceptarAmistad(username) {

    console.log("Aqui llega 3");
    var objInstanceName = new jsNotifications({
        autoCloseTime: 5,
        showAlerts: true,
        title: 'Informer'
    });

    var url = API_BASE_URL + "users/" + username + "/aceptfriend";
    console.log(url);
    $.ajax({
        url: url,
        type: 'DELETE',
        crossDomain: true,
        dataType: 'json',
        beforeSend: function (request) {
            request.withCredentials = true;
            request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
        },
    }).done(function (data, status, jqxhr) {
        console.log(data);
        console.log(status);
        console.log("si lo hahehco bien");
        objInstanceName.show('ok', 'Se ha aceptado la amistad.');
        setTimeout(function () {
            GetSolicitudes(getCookie("username"));
        }, redirecttimeout);

    }).fail(
        function (jqXHR, textStatus) {
            console.log(textStatus);
            console.log(jqXHR);
            console.log("no lo hahehco bien");
            objInstanceName.show('error',
                'No se ha podido aceptar la amistad contacte admin.');
            setTimeout(function () {
                GetSolicitudes(getCookie("username"));
            }, redirecttimeout);

        });

}

function GetUserDates(username) {
    var url = API_BASE_URL + "/users/" + username;
    $.ajax({
        url: url,
        type: 'GET',
        crossDomain: true,
        dataType: 'json',
        beforeSend: function (request) {
            request.withCredentials = true;
            request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
        },
        headers: {
            "Accept": "application/vnd.informer.api.user+json",
        },
    }).done(function (data, status, jqxhr) {
        console.log(data);
        $("#nombre").val(data.username);
        $("#correo").val(data.correo);
        $("#foto2").val(data.foto);
        if (data.genero)
            $("#SexoinlineCheckbox1").attr('checked', 'checked');
        else
            $("#SexoinlineCheckbox2").attr('checked', 'checked');
        $("#estado").val(data.estado_civil);
        var fecha = (new Date(data.fecha_nacimiento)).toLocaleString();
        $("#dia").val(fecha.split("/")[0]);
        $("#mes").val(fecha.split("/")[1]);
        $("#ano").val(fecha.split("/")[2].split(" ")[0]);
    }).fail(function (jqXHR, textStatus) {
        console.log(textStatus);
        return (false);
    });

}

function ActulizarUser() {

    var url = API_BASE_URL + "users/" + getCookie("username");
    var correo = $('#correo').val();
    var foto = $('#foto2').val();

    var estado_civil = $('#estado').val();
    var genero = $('.inlineCheckbox1').val();
    var universidad = $("#universidad").val();
    var sex = true;
    if (genero != "male")
        sex = false;
    var fecha = $('#ano').val() + "-" + $('#mes').val() + "-" + $('#dia').val()+"T00:00:00";

    var usuario = '{"correo": "' + correo + '",';
    usuario += '"estado_civil": ' + estado_civil + ',';
    usuario += '"fecha_nacimiento": "' + fecha + '",';
    usuario += '"genero": ' + sex + ',';
    //usuario += '"lugar_de_residencia": "Mi casa",';
    usuario += '"foto": "' + foto + '",';
    //usuario += '"participar_GPS": false,';
    usuario += '"uni_escuela": '+universidad+'}';
    console.log(usuario);
    $.ajax({
        url: url,
        type: 'PUT',
        crossDomain: true,
        dataType: 'json',
        data: usuario,
        beforeSend: function (request) {
            request.withCredentials = true;
            request.setRequestHeader("Authorization", "Basic " + btoa(autorizacion));
        },
        headers: {
            "Accept": "application/vnd.informer.api.user+json",
            "Content-Type": "application/vnd.informer.api.user+json",
        },
    }).done(function (data, status, jqxhr) {
        console.log(data);
        setTimeout(function () {
            Pintar()
        }, redirecttimeout);

    }).fail(function (jqXHR, textStatus) {
        console.log(textStatus);
        return (false);
    });
}