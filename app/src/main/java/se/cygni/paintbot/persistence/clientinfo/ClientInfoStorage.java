package se.cygni.paintbot.persistence.clientinfo;

import se.cygni.paintbot.api.request.ClientInfo;

public interface ClientInfoStorage {

    void storeClientInfo(ClientInfo clientInfo);

}
