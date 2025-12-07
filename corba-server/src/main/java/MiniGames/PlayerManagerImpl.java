package MiniGames;

import org.omg.CORBA.*;
import org.omg.CORBA.Object;

import java.util.concurrent.ConcurrentHashMap;

public class PlayerManagerImpl extends PlayerManagerPOA {

    private ConcurrentHashMap<String, Boolean> onlinePlayers = new ConcurrentHashMap<>();

    @Override
    public boolean registerPlayer(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        onlinePlayers.put(name, true);
        System.out.println("Player registered: " + name);
        return true;
    }

    @Override
    public String getOnlinePlayers() {
        return String.join(",", onlinePlayers.keySet());
    }

    @Override
    public String requestGame(String requester, String opponentName, String gameType) {
        return "ERR:NotImplementedYet";
    }

    @Override
    public String getGameServerInfo(String gameType) {
        return "NotImplemented";
    }

    @Override
    public boolean _is_equivalent(Object other) {
        return false;
    }

    @Override
    public int _hash(int maximum) {
        return 0;
    }

    @Override
    public Object _duplicate() {
        return null;
    }

    @Override
    public void _release() {

    }

    @Override
    public Request _request(String operation) {
        return null;
    }

    @Override
    public Request _create_request(Context ctx, String operation, NVList arg_list, NamedValue result) {
        return null;
    }

    @Override
    public Request _create_request(Context ctx, String operation, NVList arg_list, NamedValue result, ExceptionList exclist, ContextList ctxlist) {
        return null;
    }

    @Override
    public Policy _get_policy(int policy_type) {
        return null;
    }

    @Override
    public DomainManager[] _get_domain_managers() {
        return new DomainManager[0];
    }

    @Override
    public Object _set_policy_override(Policy[] policies, SetOverrideType set_add) {
        return null;
    }
}