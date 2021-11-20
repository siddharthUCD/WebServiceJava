package service.core;


import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Endpoint;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Implementation of Quotation Service for Dodgy Drivers Insurance Company
 *
 * @author Rem
 *
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
public class Quoter extends AbstractQuotationService {
    public static void main(String[] args) {
        try {
            //The class that needs to be exposed
            Endpoint endpoint = Endpoint.create(new Quoter());

            //Initializing the server with the port 9003
            HttpServer server = HttpServer.create(new InetSocketAddress(9003), 5);
            server.setExecutor(Executors.newFixedThreadPool(5));

            //Creation of the quotation context
            HttpContext context = server.createContext("/quotation");
            endpoint.publish(context);
            server.start();

            //Creating the Auldfellas Quotation service
            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());
            ServiceInfo serviceInfo = ServiceInfo.create(
                    "_http._tcp.local.", "sqs", 1234, "path=http://localhost:9002/quotation?wsdl"
            );

            //Registering the service
            jmdns.registerService(serviceInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // All references are to be prefixed with an DD (e.g. DD001000)
    public static final String PREFIX = "DD";
    public static final String COMPANY = "Dodgy Drivers Corp.";

    /**
     * Quote generation:
     * 5% discount per penalty point (3 points required for qualification)
     * 50% penalty for <= 3 penalty points
     * 10% discount per year no claims
     */
    @WebMethod
    public Quotation generateQuotation(ClientInfo info) {
        // Create an initial quotation between 800 and 1000
        double price = generatePrice(800, 200);

        // 5% discount per penalty point (3 points required for qualification)
        int discount = (info.points > 3) ? 5*info.points:-50;

        // Add a no claims discount
        discount += getNoClaimsDiscount(info);

        // Generate the quotation and send it back
        return new Quotation(COMPANY, generateReference(PREFIX), (price * (100-discount)) / 100);
    }

    private int getNoClaimsDiscount(ClientInfo info) {
        return 10*info.noClaims;
    }

}