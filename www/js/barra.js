var urlredirect = WWW_URL;

function Buscar(){
	console.log("Aki llega al buscador");
	var busqueda = $('#buscar_barra').val();
	//window.location = urlredirect + "/buscador.html?user="+busqueda;
	window.location = urlredirect + "/buscador.html";
	
}