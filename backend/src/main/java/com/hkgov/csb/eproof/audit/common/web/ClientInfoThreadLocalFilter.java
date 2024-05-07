package com.hkgov.csb.eproof.audit.common.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * Creates a ClientInfo object and passes it to the {@link ClientInfoHolder}
 * <p>
 * If one provides an alternative IP Address Header (i.e. init-param "alternativeIpAddressHeader"), the client
 * IP address will be read from that instead.
 */
public class ClientInfoThreadLocalFilter extends GenericFilter {

    public static final String CONST_IP_ADDRESS_HEADER = "alternativeIpAddressHeader";

    public static final String CONST_SERVER_IP_ADDRESS_HEADER = "alternateServerAddrHeaderName";

    public static final String CONST_USE_SERVER_HOST_ADDRESS = "useServerHostAddress";

    private String alternateLocalAddrHeaderName;

    private boolean useServerHostAddress;

    private String alternateServerAddrHeaderName;

    @Override
    public void destroy() {
        // no operation
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        try {
            final var clientInfo =
                new ClientInfo((HttpServletRequest) request,
                    this.alternateServerAddrHeaderName,
                    this.alternateLocalAddrHeaderName,
                    this.useServerHostAddress);
            ClientInfoHolder.setClientInfo(clientInfo);
            filterChain.doFilter(request, response);
        } finally {
            ClientInfoHolder.clear();
        }
    }

    @Override
    public void init(final FilterConfig filterConfig) {
        this.alternateLocalAddrHeaderName = filterConfig.getInitParameter(CONST_IP_ADDRESS_HEADER);
        this.alternateServerAddrHeaderName = filterConfig.getInitParameter(CONST_SERVER_IP_ADDRESS_HEADER);
        var useServerHostAddr = filterConfig.getInitParameter(CONST_USE_SERVER_HOST_ADDRESS);
        if (useServerHostAddr != null && !useServerHostAddr.isEmpty()) {
            this.useServerHostAddress = Boolean.parseBoolean(useServerHostAddr);
        }
    }
}
