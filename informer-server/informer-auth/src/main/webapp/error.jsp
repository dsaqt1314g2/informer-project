<%@ page session="true" import="java.util.*"%>
<script>
	setTimeout(function() {
		window.location = "http://147.83.7.156:8080/informer-auth/register.jsp";
	}, 2000);
</script>

Formato de datos incorrectos. Intentelo de nuevo luego.
<br>
<%
	String s = session.getAttribute("error").toString();
	out.print(s);
%>
<br>
Ser&aacute;s redireccionado en 2 segundos...
