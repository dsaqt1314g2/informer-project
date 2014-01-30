<%@ page session="true" import="java.util.*" %>

Formato de datos incorrectos. Intentelo de nuevo luego.<br>
<% String s = session.getAttribute("error").toString();
	out.print(s);
%>