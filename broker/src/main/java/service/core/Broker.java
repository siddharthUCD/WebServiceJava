package service.core;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * Implementation of the broker service that uses the RMI Registry.
 *
 * @author Rem
 * @modifier Siddharth
 *
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public class Broker {
    private static Set<String> URLs = new LinkedHashSet<String>();

    public static void main (String[] args) {
        try {
            //The class that needs to be exposed
            Endpoint endpoint = Endpoint.create(new Broker());
            HttpServer server = HttpServer.create(new InetSocketAddress(9000), 5);
            server.setExecutor(Executors.newFixedThreadPool(5));

            //Creation of the quotation context
            HttpContext context = server.createContext("/BrokerService");
            endpoint.publish(context);
            server.start();

            //Creating the Auldfellas Quotation service
            JmDNS jmDNS = JmDNS.create(InetAddress.getLocalHost());

            //Adding service listener
            jmDNS.addServiceListener("_http._tcp.local.", new WSDLServiceListener());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @WebMethod
    //Method to retrieve quotation from the services
    public LinkedList<Quotation> getQuotations(ClientInfo info) {
        //List to accumulate all the quotations for the current Client
        LinkedList<Quotation> quotations = new LinkedList<Quotation>();

        try {
            URL wsdlUrl;

            //Iterating through the URLs and retrieving quotations
            for (String url: URLs) {
                Quotation quotation = connectToService(url, info);
                if(quotation != null){
                    //Adding received quotation to the list
                    quotations.add(quotation);
                }
            }

            //Return list of quotation to client
            return quotations;
        } catch (Exception e) {
            e.printStackTrace();
            return quotations;
        }
    }

    public static class WSDLServiceListener implements ServiceListener {

        @Override
        public void serviceAdded(ServiceEvent serviceEvent) {

        }

        @Override
        public void serviceRemoved(ServiceEvent serviceEvent) {
        }

        @Override
        public void serviceResolved(ServiceEvent serviceEvent) {
            String path = serviceEvent.getInfo().getPropertyString("path");
            if(path != null)
                //Adding all the available URLs to a common set
                URLs.add(path);
        }
    }

    private static Quotation connectToService(String url, ClientInfo info){
        try{
            URL wsdlUrl = new URL(url);

            //Name of the target service
            QName serviceName =
                    new QName("http://core.service/", "QuoterService");

            //Creation of Retreival service
            Service service = Service.create(wsdlUrl, serviceName);

            //Initializing and receiving the Port Name
            QName portName = new QName("http://core.service/", "QuoterPort");
            BrokerService quotationService =
                    service.getPort(portName, BrokerService.class);

            //Call to retrieve quotations
            return quotationService.generateQuotation(info);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}