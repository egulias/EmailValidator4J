package emailvalidator4j.validator;

import emailvalidator4j.ValidationStrategy;
import emailvalidator4j.parser.Email;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

public class MXRecord implements ValidationStrategy {

    @Override
    public Boolean isValid(String email, Email parser) {
        String hostName = parser.getDomainPart();
        return this.findMXEntries(hostName).size() > 0;
    }

    private Set<String> findMXEntries(String hostName) {
        Set<String> results = new TreeSet<>();
        String type = "MX";
        Attributes dnsEntries = this.getEntriesForType(hostName, type);
        if(dnsEntries.size() == 0) {
            return results;
        }

        try {
            NamingEnumeration<?> dnsEntryIterator = dnsEntries.get(type).getAll();
            while(dnsEntryIterator.hasMoreElements()) {
                results.add(dnsEntryIterator.next().toString());
            }
        } catch(NamingException e) {
            return results;
        }
        return results;
    }

    private Attributes getEntriesForType(String hostName, String type) {
        Hashtable<String, String> envProps = new Hashtable<>();
        envProps.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
        envProps.put(Context.PROVIDER_URL, "dns://8.8.8.8/");

        try {
            DirContext dnsContext = new InitialDirContext(envProps);
            return dnsContext.getAttributes(hostName, new String[]{type});
        } catch(NamingException e) {
            return new BasicAttributes();
        }
    }
}
