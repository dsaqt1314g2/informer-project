<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Registrame</title>
</head>
<body>
	<link rel="stylesheet" type="text/css" href="bootstrap/css/login.css">
	
	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.1/css/bootstrap.min.css">
	<!-- Optional theme -->
	<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.1/css/bootstrap-theme.min.css">
	<!-- Latest compiled and minified JavaScript -->
	<script src="//netdna.bootstrapcdn.com/bootstrap/3.0.1/js/bootstrap.min.js"></script>
	
	<div class="col-sm-6 col-md-4 col-md-offset-4">
        
            <div class="account-wall-personalizado">

				<div class="container">
				    <div class="row">
				        <div class="col-xs-12 col-sm-12 col-md-4 well well-sm">
				        <img class="profile-img" src="http://www.anaderwei.com/wp-content/uploads/2013/07/social-networking.jpg" alt="">
				            <legend><a href="http://www.jquery2dotnet.com"><i class="glyphicon glyphicon-globe"></i></a> Regístrate!</legend>
				            <form name="formularioREG" action="RegisterServlet" method="POST" class="form" role="form">
				            <input type="hidden" name="action" value="formularioREG"/>
				            <div class="row">
				                <div class="col-xs-6 col-md-6">
				                    <input class="form-control" name="username" placeholder="Usuario" type="text"
				                        required autofocus />
				                </div>
				                <div class="col-xs-6 col-md-6">
				                    <label class="radio-inline">
						                <input type="radio" name="sex" id="inlineCheckbox1" value="male" checked="true" />
						                Hombre
						            </label>
						            <label class="radio-inline">
						                <input type="radio" name="sex" id="inlineCheckbox2" value="female" />
						                Mujer
						            </label>
				                </div>
				            </div>
				            <br><input class="form-control" name="correo" placeholder="Correo universitario" type="email" />
				            <br><input class="form-control" name="reenteremail" placeholder="Introduce otra vez el correo" type="email" />
				            <br><input class="form-control" name="password" placeholder="Contraseña" type="password" />
				            <br><select name="universidad" class="form-control">
				                        <option value="1">Escola d'Enginyeria de Telecomunicació i Aeroespacial de Castelldefels (UPC)</option>
				                    </select>
				            <br><br><label for="">
				                Fecha de nacimiento</label>
				            <div class="row">
				                <div class="col-xs-4 col-md-4">
				                    <select name="dia" class="form-control">
				                        <option value="0" selected="1">Día</option><option value="1">1</option><option value="2">2</option><option value="3">3</option><option value="4">4</option><option value="5">5</option><option value="6">6</option><option value="7">7</option><option value="8">8</option><option value="9">9</option><option value="10">10</option><option value="11">11</option><option value="12">12</option><option value="13">13</option><option value="14">14</option><option value="15">15</option><option value="16">16</option><option value="17">17</option><option value="18">18</option><option value="19">19</option><option value="20">20</option><option value="21">21</option><option value="22">22</option><option value="23">23</option><option value="24">24</option><option value="25">25</option><option value="26">26</option><option value="27">27</option><option value="28">28</option><option value="29">29</option><option value="30">30</option><option value="31">31</option>
				                    </select>
				                    
				                </div>
				                <div class="col-xs-4 col-md-4">
				                    <select name="mes" class="form-control">
				                        <option value="0" selected="1">Mes</option><option value="1">Enero</option><option value="2">Febrero</option><option value="3">Marzo</option><option value="4">Abril</option><option value="5">Mayo</option><option value="6">Junio</option><option value="7">Julio</option><option value="8">Agosto</option><option value="9">Septiembre</option><option value="10">Octubre</option><option value="11">Noviembre</option><option value="12">Diciembre</option>
				                    </select>
				                </div>
				                <div class="col-xs-4 col-md-4">
				                    <select name="ano" class="form-control">
				                        <option value="0" selected="1">Año</option><option value="2014">2014</option><option value="2013">2013</option><option value="2012">2012</option><option value="2011">2011</option><option value="2010">2010</option><option value="2009">2009</option><option value="2008">2008</option><option value="2007">2007</option><option value="2006">2006</option><option value="2005">2005</option><option value="2004">2004</option><option value="2003">2003</option><option value="2002">2002</option><option value="2001">2001</option><option value="2000">2000</option><option value="1999">1999</option><option value="1998">1998</option><option value="1997">1997</option><option value="1996">1996</option><option value="1995">1995</option><option value="1994">1994</option><option value="1993">1993</option><option value="1992">1992</option><option value="1991">1991</option><option value="1990">1990</option><option value="1989">1989</option><option value="1988">1988</option><option value="1987">1987</option><option value="1986">1986</option><option value="1985">1985</option><option value="1984">1984</option><option value="1983">1983</option><option value="1982">1982</option><option value="1981">1981</option><option value="1980">1980</option><option value="1979">1979</option><option value="1978">1978</option><option value="1977">1977</option><option value="1976">1976</option><option value="1975">1975</option><option value="1974">1974</option><option value="1973">1973</option><option value="1972">1972</option><option value="1971">1971</option><option value="1970">1970</option><option value="1969">1969</option><option value="1968">1968</option><option value="1967">1967</option><option value="1966">1966</option><option value="1965">1965</option><option value="1964">1964</option><option value="1963">1963</option><option value="1962">1962</option><option value="1961">1961</option><option value="1960">1960</option><option value="1959">1959</option><option value="1958">1958</option><option value="1957">1957</option><option value="1956">1956</option><option value="1955">1955</option><option value="1954">1954</option><option value="1953">1953</option><option value="1952">1952</option><option value="1951">1951</option><option value="1950">1950</option><option value="1949">1949</option><option value="1948">1948</option><option value="1947">1947</option><option value="1946">1946</option><option value="1945">1945</option><option value="1944">1944</option><option value="1943">1943</option><option value="1942">1942</option><option value="1941">1941</option><option value="1940">1940</option>
				                    </select>
				                </div>
				            </div>
				            
				            <br><br>
				            <label for="">Estado actual</label><br>
				            <label class="radio-inline">
				                <input type="radio" name="civil" id="inlineCheckbox1" value="0" checked="true" />
				                Soltero
				            </label>
				            <label class="radio-inline">
				                <input type="radio" name="civil" id="inlineCheckbox2" value="6" />
				                En relación
				            </label>
				            <br>
				            <label class="radio-inline">
				                <input type="radio" name="civil" id="inlineCheckbox2" value="2" />
				                Me acaban de dejar
				            </label>
				            <label class="radio-inline">
				                <input type="radio" name="civil" id="inlineCheckbox2" value="3" />
				                Lo que surja
				            </label>
				            <br />
				            <br />
				            <button class="btn btn-lg btn-primary btn-block" type="submit">
				                Regístrate</button>
				            </form>
				        </div>
				    </div>
				</div>
            </div>
        </div>
 
</body>
</html>