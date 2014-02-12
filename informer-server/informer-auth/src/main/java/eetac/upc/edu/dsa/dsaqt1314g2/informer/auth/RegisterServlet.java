package eetac.upc.edu.dsa.dsaqt1314g2.informer.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Servlet implementation class RegisterServlet
 */
public class RegisterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private DataSource ds = null;
	private static final String IP_SERVER = "147.83.7.156";
	//private static final String IP_SERVER = "localhost";
	private static final int PUERTO_SERVER = 8080;

	@Override
	public void init() throws ServletException {
		super.init();
		ds = DataSourceSPA.getInstance().getDataSource();
	}

	public RegisterServlet() {

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	@SuppressWarnings("deprecation")
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String action = req.getParameter("action");
		HttpSession session = req.getSession(false);
		String error = "Sin error";
		if (action.equals("formularioREG")) {
			if (!req.getParameter("correo").equals(req.getParameter("reenteremail"))) {
				error = "Los correos no coinciden";
				String dir = "/error.jsp";
				ServletContext serc = getServletContext();
				RequestDispatcher rd = serc.getRequestDispatcher(dir);
				// if (!error.equals("Sin error"))
				session.setAttribute("error", error);
				rd.forward(req, res);
				return;
			}
			String username, pass, sexo, email, civil, name, universidad, fecha;
			username = req.getParameter("username");
			pass = req.getParameter("password");
			sexo = req.getParameter("sex");
			email = req.getParameter("correo") + "@estudiant.upc.es";
			civil = req.getParameter("civil");
			universidad = req.getParameter("universidad");
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date date = new Date();
				if (Integer.parseInt(req.getParameter("dia")) != 0 && Integer.parseInt(req.getParameter("mes")) != 0 && Integer.parseInt(req.getParameter("ano")) != 0) {
					date.setDate(Integer.parseInt(req.getParameter("dia")));
					date.setMonth(Integer.parseInt(req.getParameter("mes")));
					date.setYear(Integer.parseInt(req.getParameter("ano")));
				}
				fecha = sdf.format(date).replace(" ", "T");
				// System.out.println(fecha);
			} catch (Exception e) {
				error = "Aniversario incorrecto!";
				String dir = "/error.jsp";
				ServletContext serc = getServletContext();
				RequestDispatcher rd = serc.getRequestDispatcher(dir);
				// if (!error.equals("Sin error"))
				session.setAttribute("error", error);
				rd.forward(req, res);
				return;
			}
			try {
				name = email.split(".")[0];
			} catch (Exception e) {
				name = email;
			}
			Connection con;
			Statement stmt;
			int resu = -1;
			try {
				con = ds.getConnection();
				stmt = con.createStatement();
				try {
					String update = "SELECT username FROM users WHERE username='" + username + "';";
					ResultSet rs = stmt.executeQuery(update);
					if (rs.next())
						throw new Exception("El usuario ya existe");
					con.setAutoCommit(false);
					update = "INSERT INTO users VALUES('" + username + "',MD5('" + pass + "'),'" + name + "','" + email + "');";
					stmt.executeUpdate(update);
					update = "INSERT INTO user_roles VALUES('" + username + "','registered');";
					stmt.executeUpdate(update);
					resu = postUserInformerDB(username, name, email, sexo, civil, fecha, universidad);
					if (resu == 0) {
						error = "No se ha hecho post en informerdb";
						con.commit();
						Correo.sendCorreo(username,new BigInteger(24, new SecureRandom()).toString(32).toUpperCase(), email);
					} else
						con.rollback();
					stmt.close();
					con.close();
				} catch (Exception e) {
					try {
						con.rollback();
					} catch (SQLException e1) {
						error = e1.toString();
					}
					error = e.toString();
				}
			} catch (SQLException e2) {
				error = e2.toString();
			}
			String url;
			if (resu == 0)
				url = "/validar.jsp";
			else
				url = "/error.jsp";
			ServletContext sc = getServletContext();
			RequestDispatcher rd = sc.getRequestDispatcher(url);
			// if (!error.equals("Sin error"))
			session.setAttribute("error", error);
			rd.forward(req, res);
		} else {
			// System.out.println("problema");
			String url = "/register.jsp";
			ServletContext sc = getServletContext();
			RequestDispatcher rd = sc.getRequestDispatcher(url);
			rd.forward(req, res);
		}
	}

	@SuppressWarnings("unchecked")
	private int postUserInformerDB(String username, String name, String email, String sexo, String civil, String fecha, String universidad) {
		HttpHost targetHost = new HttpHost(IP_SERVER, PUERTO_SERVER, "http");
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials("administrador", "informer91"));

		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// Generate BASIC scheme object and add it to the local auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(targetHost, basicAuth);

		// Add AuthCache to the execution context
		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credsProvider);

		HttpPost httpPost = new HttpPost("http://" + IP_SERVER + ":" + PUERTO_SERVER + "/informer-api/users");
		httpPost.addHeader("Content-Type", "application/vnd.informer.api.user+json");
		httpPost.addHeader("Accept", "application/vnd.informer.api.user+json");

		JSONObject obj = new JSONObject();
		obj.put("username", username);
		obj.put("name", name);
		obj.put("correo", email);
		if (sexo.equals("male"))
			obj.put("genero", 1);
		else
			obj.put("genero", 0);
		obj.put("fecha_nacimiento", fecha);
		obj.put("estado_civil", civil);
		obj.put("uni_escuela", universidad);
		String user = obj.toJSONString();
		// System.out.println(username);
		// System.out.println(name);
		// System.out.println(email);
		// System.out.println(sexo);
		// System.out.println(fecha);
		// System.out.println(civil);
		// System.out.println(universidad);
		// System.out.println(user);
		try {
			httpPost.setEntity(new StringEntity(user));
			CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
			CloseableHttpResponse httpResponse;
			httpResponse = closeableHttpClient.execute(targetHost, httpPost, context);
			HttpEntity entity = httpResponse.getEntity();
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				System.out.println(line);
			}
			httpResponse.close();
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(sb.toString());
			jsonObject.get("username");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} catch (ParseException e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
}
