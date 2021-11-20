package service.core;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.LinkedList;

@WebService
public interface QuoterService {
    @WebMethod
    LinkedList<Quotation> getQuotations(ClientInfo info);
}
