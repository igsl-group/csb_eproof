package com.hkgov.csb.eproof.audit.common.web;

import com.hkgov.csb.eproof.exception.GenericException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.Serial;
import java.io.Serializable;
import java.net.Inet4Address;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.hkgov.csb.eproof.config.Constants.V4IP;
import static com.hkgov.csb.eproof.config.Constants.V6IP;
import static com.hkgov.csb.eproof.exception.ExceptionConstants.CLIENT_INFO_CREATION_EXCEPTION_CODE;
import static com.hkgov.csb.eproof.exception.ExceptionConstants.CLIENT_INFO_CREATION_EXCEPTION_MESSAGE;

/**
 * Captures information from the HttpServletRequest to log later.
 */
public class ClientInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 7492721606084356617L;

    private final HttpServletRequest request;

    /**
     * IP Address of the server (local).
     */
    private final String serverIpAddress;

    /**
     * IP Address of the client (Remote)
     */
    private final String clientIpAddress;

    private final String geoLocation;

    private final String userAgent;

    private final Map<String, String> headers = new HashMap<>();

    private final Locale locale;

    private ClientInfo() {
        this(null);
    }

    public ClientInfo(final HttpServletRequest request) {
        this(request, null, null, false);
    }

    public ClientInfo(final HttpServletRequest request,
                      final String alternateServerAddrHeaderName,
                      final String alternateLocalAddrHeaderName,
                      final boolean useServerHostAddress) {
        try {
            this.request = request;
            this.locale = request != null ? request.getLocale() : Locale.getDefault();
            if (request != null) {
                var headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    var headerName = headerNames.nextElement();
                    this.headers.put(headerName, request.getHeader(headerName));
                }
            }

            var serverIpAddress = request != null ? request.getLocalAddr() : null;
            var clientIpAddress = request != null ? request.getRemoteAddr() : null;

            if (request == null) {
                this.geoLocation = "unknown";
                this.userAgent = "unknown";
            } else {
                if (useServerHostAddress) {
                    serverIpAddress = Inet4Address.getLocalHost().getHostAddress();
                } else if (alternateServerAddrHeaderName != null && !alternateServerAddrHeaderName.isEmpty()) {
                    serverIpAddress = request.getHeader(alternateServerAddrHeaderName) != null
                            ? request.getHeader(alternateServerAddrHeaderName) : request.getLocalAddr();
                }

                if (alternateLocalAddrHeaderName != null && !alternateLocalAddrHeaderName.isEmpty()) {
                    clientIpAddress = request.getHeader(alternateLocalAddrHeaderName) != null ? request.getHeader
                            (alternateLocalAddrHeaderName) : request.getRemoteAddr();
                }
                var header = request.getHeader("user-agent");
                this.userAgent = header == null ? "unknown" : header;
                var geo = request.getParameter("geolocation");
                if (geo == null) {
                    geo = request.getHeader("geolocation");
                }
                this.geoLocation = geo == null ? "unknown" : geo;
            }

            this.serverIpAddress = serverIpAddress == null ? "unknown" : serverIpAddress;
            this.clientIpAddress = clientIpAddress == null ? "unknown" : clientIpAddress;

        } catch (final Exception e) {
            throw new GenericException(CLIENT_INFO_CREATION_EXCEPTION_CODE, CLIENT_INFO_CREATION_EXCEPTION_MESSAGE, e);
        }
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public String getServerIpAddress() {
        return this.serverIpAddress;
    }

    public String getClientIpAddress() {
        if (V6IP.equals(clientIpAddress)) {
            return V4IP;
        }
        return this.clientIpAddress;
    }

    public String getGeoLocation() {
        return geoLocation;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(this.headers);
    }

    public Locale getLocale() {
        return locale;
    }

    public String getRequestUrl() {
        return Optional.ofNullable(request)
                .map(HttpServletRequest::getRequestURI)
                .orElse(null);
    }

    public String getRequestMethod() {
        return Optional.ofNullable(request)
                .map(HttpServletRequest::getMethod)
                .orElse(null);
    }

    public String getRequestHeaders() {
        return Optional.ofNullable(request)
                .map(HttpServletRequest::getHeaderNames)
                .map(Collections::list)
                .flatMap(headers -> headers.stream()
                        .map(header -> header + ": " + request.getHeader(header))
                        .reduce((header1, header2) -> header1 + ", " + header2))
                .orElse(null);
    }

    public static ClientInfo empty() {
        return new ClientInfo();
    }
}
