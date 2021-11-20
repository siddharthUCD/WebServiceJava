import service.core.ClientInfo;
import service.core.Quotation;
import service.core.QuoterService;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;
import java.text.NumberFormat;
import java.util.LinkedList;

public class Client {
    public static void main(String[] args) {
        try {
            String host = "localhost";
            int port = 9000;

            // More Advanced flag-based configuration
            // [ copy this from the ws-quote example client ]
            System.out.println("The client will start in 16 Secs...");
            Thread.sleep(16000);

            //Initializing with the URL of Broker Service
            URL wsdlUrl = new
                    URL("http://" + host + ":" + port + "/BrokerService?wsdl");

            //Initializing with the Service Name of Broker Service
            QName serviceName =
                    new QName("http://core.service/", "BrokerService");

            //Creation of Service
            Service service = Service.create(wsdlUrl, serviceName);

            //Initializing with the Broker Port
            QName portName = new QName("http://core.service/", "BrokerPort");
            QuoterService quotationService =
                    service.getPort(portName, QuoterService.class);

            //Iterating through the Client Information
            for (ClientInfo info : clients) {
                displayProfile(info);

                //List to accumulate all the quotations for one Client
                LinkedList<Quotation> quotations = quotationService.getQuotations(info);

                //Displaying the quotations under each client
                if(quotations.size() > 0)
                    for (Quotation quotation:
                            quotations) {
                        displayQuotation(quotation);
                    }

                System.out.println("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Display the client info nicely.
     *
     */
    public static void displayProfile(ClientInfo info) {
        System.out.println("|=================================================================================================================|");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println(
                "| Name: " + String.format("%1$-29s", info.name) +
                        " | Gender: " + String.format("%1$-27s", (info.gender==ClientInfo.MALE?"Male":"Female")) +
                        " | Age: " + String.format("%1$-30s", info.age)+" |");
        System.out.println(
                "| License Number: " + String.format("%1$-19s", info.licenseNumber) +
                        " | No Claims: " + String.format("%1$-24s", info.noClaims+" years") +
                        " | Penalty Points: " + String.format("%1$-19s", info.points)+" |");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println("|=================================================================================================================|");
    }

    /**
     * Display a quotation nicely - note that the assumption is that the quotation will follow
     * immediately after the profile (so the top of the quotation box is missing).
     *
     */
    public static void displayQuotation(Quotation quotation) {
        System.out.println(
                "| Company: " + String.format("%1$-26s", quotation.company) +
                        " | Reference: " + String.format("%1$-24s", quotation.reference) +
                        " | Price: " + String.format("%1$-28s", NumberFormat.getCurrencyInstance().format(quotation.price))+" |");
        System.out.println("|=================================================================================================================|");
    }

    /**
     * Test Data
     */
    public static final ClientInfo[] clients = {
            new ClientInfo("Niki Collier", ClientInfo.FEMALE, 43, 0, 5, "PQR254/1"),
            new ClientInfo("Old Geeza", ClientInfo.MALE, 65, 0, 2, "ABC123/4"),
            new ClientInfo("Hannah Montana", ClientInfo.FEMALE, 16, 10, 0, "HMA304/9"),
            new ClientInfo("Rem Collier", ClientInfo.MALE, 44, 5, 3, "COL123/3"),
            new ClientInfo("Jim Quinn", ClientInfo.MALE, 55, 4, 7, "QUN987/4"),
            new ClientInfo("Donald Duck", ClientInfo.MALE, 35, 5, 2, "XYZ567/9")
    };

}
