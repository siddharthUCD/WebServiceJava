package service.core;


import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.ws.Endpoint;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Implementation of the AuldFellas insurance quotation service.
 * 
 * @author Rem
 *
 */
@WebService
@SOAPBinding(style=Style.RPC, use=Use.LITERAL)
public class Quoter extends AbstractQuotationService {
	public static void main(String[] args) {
		try {
			//The class that needs to be exposed
			Endpoint endpoint = Endpoint.create(new Quoter());
			HttpServer server = HttpServer.create(new InetSocketAddress(9002), 5);
			server.setExecutor(Executors.newFixedThreadPool(5));

			//Creation of the quotation context
			HttpContext context = server.createContext("/quotation");
			endpoint.publish(context);
			server.start();

			//Creating the Auldfellas Quotation service
			JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());
			ServiceInfo serviceInfo = ServiceInfo.create(
					"_http._tcp.local.", "sqs", 1111, "path=http://localhost:9003/quotation?wsdl"
			);

			//Registering the service
			jmdns.registerService(serviceInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final String PREFIX = "GP";
	public static final String COMPANY = "Girl Power Inc.";

	/**
	 * Quote generation:
	 * 50% discount for being female
	 * 20% discount for no penalty points
	 * 15% discount for < 3 penalty points
	 * no discount for 3-5 penalty points
	 * 100% penalty for > 5 penalty points
	 * 5% discount per year no claims
	 */
	@WebMethod
	public Quotation generateQuotation(ClientInfo info) {
		// Create an initial quotation between 600 and 1000
		double price = generatePrice(600, 400);

		// Automatic 50% discount for being female
		int discount = (info.gender == ClientInfo.FEMALE) ? 50:0;

		// Add a points discount
		discount += getPointsDiscount(info);

		// Add a no claims discount
		discount += getNoClaimsDiscount(info);

		// Generate the quotation and send it back
		return new Quotation(COMPANY, generateReference(PREFIX), (price * (100-discount)) / 100);
	}

	private int getNoClaimsDiscount(ClientInfo info) {
		return 5*info.noClaims;
	}

	private int getPointsDiscount(ClientInfo info) {
		if (info.points == 0) return 20;
		if (info.points < 3) return 15;
		if (info.points < 6) return 0;
		return -100;

	}
}
