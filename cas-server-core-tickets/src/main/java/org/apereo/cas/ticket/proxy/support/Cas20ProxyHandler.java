package org.apereo.cas.ticket.proxy.support;

import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.HttpBasedServiceCredential;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.UniqueTicketIdGenerator;
import org.apereo.cas.ticket.proxy.ProxyGrantingTicket;
import org.apereo.cas.ticket.proxy.ProxyHandler;
import org.apereo.cas.util.DefaultUniqueTicketIdGenerator;
import org.apereo.cas.util.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * Proxy Handler to handle the default callback functionality of CAS 2.0.
 * <p>
 * The default behavior as defined in the CAS 2 Specification is to callback the
 * URL provided and give it a pgtIou and a pgtId.
 * </p>
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class Cas20ProxyHandler implements ProxyHandler {
    private static final int BUFFER_LENGTH_ADDITIONAL_CHARGE = 15;

    /**
     * The proxy granting ticket identifier parameter.
     */
    private static final String PARAMETER_PROXY_GRANTING_TICKET_IOU = "pgtIou";

    /**
     * The Constant proxy granting ticket parameter.
     */
    private static final String PARAMETER_PROXY_GRANTING_TICKET_ID = "pgtId";

    /**
     * The Commons Logging instance.
     */
    private transient Logger logger = LoggerFactory.getLogger(getClass());

    private UniqueTicketIdGenerator uniqueTicketIdGenerator;

    private HttpClient httpClient;

    /**
     * Initializes the ticket id generator to
     * {@link DefaultUniqueTicketIdGenerator}.
     */
    public Cas20ProxyHandler() {
        this.uniqueTicketIdGenerator = new DefaultUniqueTicketIdGenerator();
    }

    @Override
    public String handle(final Credential credential, final TicketGrantingTicket proxyGrantingTicketId) {
        final HttpBasedServiceCredential serviceCredentials = (HttpBasedServiceCredential) credential;
        final String proxyIou = this.uniqueTicketIdGenerator.getNewTicketId(ProxyGrantingTicket.PROXY_GRANTING_TICKET_IOU_PREFIX);

        final URL callbackUrl = serviceCredentials.getCallbackUrl();
        final String serviceCredentialsAsString = callbackUrl.toExternalForm();
        final int bufferLength = serviceCredentialsAsString.length() + proxyIou.length()
                + proxyGrantingTicketId.getId().length() + BUFFER_LENGTH_ADDITIONAL_CHARGE;
        final StringBuilder stringBuffer = new StringBuilder(bufferLength);

        stringBuffer.append(serviceCredentialsAsString);

        if (callbackUrl.getQuery() != null) {
            stringBuffer.append('&');
        } else {
            stringBuffer.append('?');
        }

        stringBuffer.append(PARAMETER_PROXY_GRANTING_TICKET_IOU);
        stringBuffer.append('=');
        stringBuffer.append(proxyIou);
        stringBuffer.append('&');
        stringBuffer.append(PARAMETER_PROXY_GRANTING_TICKET_ID);
        stringBuffer.append('=');
        stringBuffer.append(proxyGrantingTicketId);

        if (this.httpClient.isValidEndPoint(stringBuffer.toString())) {
            logger.debug("Sent ProxyIou of {} for service: {}", proxyIou, serviceCredentials);
            return proxyIou;
        }

        logger.debug("Failed to send ProxyIou of {} for service: {}", proxyIou, serviceCredentials);
        return null;
    }
    
    public void setUniqueTicketIdGenerator(final UniqueTicketIdGenerator uniqueTicketIdGenerator) {
        this.uniqueTicketIdGenerator = uniqueTicketIdGenerator;
    }

    public void setHttpClient(final HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public boolean canHandle(final Credential credential) {
        return true;
    }
}
