<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" session="true"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Registrame</title>
</head>
<body>
	<link rel="stylesheet" type="text/css" href="bootstrap/css/login.css">
	<script src="http://netdna.bootstrapcdn.com/bootstrap/3.0.1/js/bootstrap.min.js"></script>
	<link rel="stylesheet" href="http://netdna.bootstrapcdn.com/bootstrap/3.0.1/css/bootstrap.min.css">
	<link rel="stylesheet" href="http://netdna.bootstrapcdn.com/bootstrap/3.0.1/css/bootstrap-theme.min.css">

	<div class="account-wall-personalizado" style="margin-right: auto; margin-left: auto; width: 480px;">
		<div class="well fondo" style="background-image: url(bootstrap/img/registro.png);">
			<img class="profile-img" src="bootstrap/img/social-network.jpg"> <br>
			<legend>
				<a href="#"><i class="glyphicon glyphicon-globe"></i></a> Reg&iacute;strate!
			</legend>
			<form id="registro" action="RegisterServlet" method="POST" class="form">
				<input type="hidden" name="action" value="formularioREG" />
				<table>
					<tr>
						<td class="tabla-registro">
							<input class="form-control" name="username" placeholder="Usuario" type="text" required autofocus />
						</td>
						<td class="tabla-registro">
							<input class="form-control" style="padding-bottom: 4px;" name="password" placeholder="Contrase&ntilde;a" type="password" required />

						</td>
					</tr>
					<tr>
						<td class="tabla-registro">
							<input class="form-control" name="correo" placeholder="Correo universitario" type="text" required />
						</td>
						<td>
							<label id="correo-tail">@estudiant.upc.es</label>
						</td>
					</tr>
					<tr>
						<td class="tabla-registro">
							<input class="form-control" name="reenteremail" placeholder="Correo universitario" type="text" required />
						</td>
						<td>
							<label id="correo-tail">@estudiant.upc.es</label>
						</td>
					</tr>
					<tr>
						<td colspan=2 class="tabla-registro-especial">
							<select name="universidad" class="form-control">
								<option value="0">Escola d'Enginyeria de Telecomunicaci&oacute; i Aeroespacial de Castelldefels (UPC)</option>
							</select>
						</td>
					</tr>
				</table>
				<br>
				<legend>
					<i class="glyphicon glyphicon-star"></i> Informaci&oacute;n adicional
				</legend>
				<table>
					<tr>
						<td style="padding: 0px 10px;">
							<label class="radio-inline">
								<input type="radio" name="sex" id="inlineCheckbox1" value="male" checked />
								Hombre
							</label>
						</td>
						<td style="padding: 0px 10px;">
							<label class="radio-inline">
								<input type="radio" name="sex" id="inlineCheckbox2" value="female" />
								Mujer
							</label>
						</td>
						<td>
							<select name="dia" class="form-control" style="width: 75px;">
								<option value="0" selected>D&iacute;a</option>
								<option value="1">1</option>
								<option value="2">2</option>
								<option value="3">3</option>
								<option value="4">4</option>
								<option value="5">5</option>
								<option value="6">6</option>
								<option value="7">7</option>
								<option value="8">8</option>
								<option value="9">9</option>
								<option value="10">10</option>
								<option value="11">11</option>
								<option value="12">12</option>
								<option value="13">13</option>
								<option value="14">14</option>
								<option value="15">15</option>
								<option value="16">16</option>
								<option value="17">17</option>
								<option value="18">18</option>
								<option value="19">19</option>
								<option value="20">20</option>
								<option value="21">21</option>
								<option value="22">22</option>
								<option value="23">23</option>
								<option value="24">24</option>
								<option value="25">25</option>
								<option value="26">26</option>
								<option value="27">27</option>
								<option value="28">28</option>
								<option value="29">29</option>
								<option value="30">30</option>
								<option value="31">31</option>
							</select>
						</td>
						<td>
							<select name="mes" class="form-control" style="width: 115px;">
								<option value="0" selected>Mes</option>
								<option value="1">Enero</option>
								<option value="2">Febrero</option>
								<option value="3">Marzo</option>
								<option value="4">Abril</option>
								<option value="5">Mayo</option>
								<option value="6">Junio</option>
								<option value="7">Julio</option>
								<option value="8">Agosto</option>
								<option value="9">Septiembre</option>
								<option value="10">Octubre</option>
								<option value="11">Noviembre</option>
								<option value="12">Diciembre</option>
							</select>
						</td>
						<td>
							<select name="ano" class="form-control" style="width: 80px;">
								<option value="0" selected>A&ntilde;o</option>
								<!-- <option value="2014">2014</option>
								<option value="2013">2013</option>
								<option value="2012">2012</option>
								<option value="2011">2011</option>
								<option value="2010">2010</option>
								<option value="2009">2009</option>
								<option value="2008">2008</option>
								<option value="2007">2007</option>
								<option value="2006">2006</option>
								<option value="2005">2005</option>
								<option value="2004">2004</option>
								<option value="2003">2003</option>
								<option value="2002">2002</option>
								<option value="2001">2001</option>
								<option value="2000">2000</option>
								<option value="1999">1999</option>
								<option value="1998">1998</option>-->
								<option value="1997">1997</option>
								<option value="1996">1996</option>
								<option value="1995">1995</option>
								<option value="1994">1994</option>
								<option value="1993">1993</option>
								<option value="1992">1992</option>
								<option value="1991">1991</option>
								<option value="1990">1990</option>
								<option value="1989">1989</option>
								<option value="1988">1988</option>
								<option value="1987">1987</option>
								<option value="1986">1986</option>
								<option value="1985">1985</option>
								<option value="1984">1984</option>
								<option value="1983">1983</option>
								<option value="1982">1982</option>
								<option value="1981">1981</option>
								<option value="1980">1980</option>
								<option value="1979">1979</option>
								<option value="1978">1978</option>
								<option value="1977">1977</option>
								<option value="1976">1976</option>
								<option value="1975">1975</option>
								<option value="1974">1974</option>
								<option value="1973">1973</option>
								<option value="1972">1972</option>
								<option value="1971">1971</option>
								<option value="1970">1970</option>
								<option value="1969">1969</option>
								<option value="1968">1968</option>
								<option value="1967">1967</option>
								<option value="1966">1966</option>
								<option value="1965">1965</option>
								<option value="1964">1964</option>
								<option value="1963">1963</option>
								<option value="1962">1962</option>
								<option value="1961">1961</option>
								<option value="1960">1960</option>
								<option value="1959">1959</option>
								<option value="1958">1958</option>
								<option value="1957">1957</option>
								<option value="1956">1956</option>
								<option value="1955">1955</option>
								<option value="1954">1954</option>
								<option value="1953">1953</option>
								<option value="1952">1952</option>
								<option value="1951">1951</option>
								<option value="1950">1950</option>
								<option value="1949">1949</option>
								<option value="1948">1948</option>
								<option value="1947">1947</option>
								<option value="1946">1946</option>
								<option value="1945">1945</option>
								<option value="1944">1944</option>
								<option value="1943">1943</option>
								<option value="1942">1942</option>
								<option value="1941">1941</option>
								<option value="1940">1940</option>
							</select>
						</td>
					</tr>
					<tr>
						<td colspan=6 style="padding: 9px 0px 0px 0px;">
							<select name="civil" class="form-control">
								<option value="0">Soltero</option>
								<option value="1">Sin lazos</option>
								<option value="2">A falta de mimos</option>
								<option value="3">Follamig@</option>
								<option value="4">Relaci&oacute;n abierta</option>
								<option value="5">Relaci&oacute;n a distancia</option>
								<option value="6">En una relaci&oacute;n</option>
								<option value="7">Comprometid@</option>
							</select>
						</td>
					</tr>
				</table>
				<br>
				<button class="btn btn-lg btn-primary btn-block" style="width: 440px;" type="submit">Reg&iacute;strate</button>
			</form>
		</div>
	</div>
</body>
</html>