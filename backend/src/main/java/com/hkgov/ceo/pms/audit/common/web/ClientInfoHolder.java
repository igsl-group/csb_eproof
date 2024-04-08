package com.hkgov.ceo.pms.audit.common.web;

/**
 * Makes the ClientInfo available to the thread.
 */
public class ClientInfoHolder {
    private ClientInfoHolder() {
    }

    private static final ThreadLocal<ClientInfo> CLIENT_INFO_HOLDER = new InheritableThreadLocal<>();

    public static void setClientInfo(final ClientInfo clientInfo) {
        CLIENT_INFO_HOLDER.set(clientInfo);
    }

    public static ClientInfo getClientInfo() {
        return CLIENT_INFO_HOLDER.get();
    }

    public static void clear() {
        CLIENT_INFO_HOLDER.remove();
    }
}
