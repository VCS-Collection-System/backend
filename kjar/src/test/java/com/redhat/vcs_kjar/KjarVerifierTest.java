package com.redhat.vcs_kjar;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KjarVerifierTest {

	private static final Logger LOG = LoggerFactory.getLogger(KjarVerifierTest.class);

    @Test
    public void verifyRulesAndProcesses() {
            // load up the knowledge base
	        KieServices ks = KieServices.Factory.get();
    	    KieContainer kContainer = ks.getKieClasspathContainer();
    	    Results results = kContainer.verify();
    	    List<Message> messages = results.getMessages();
    	    System.out.println("MESSAGE SIZE= " + messages.size());
    	    
    	    for (int x = 0; x < messages.size(); x++) {
				LOG.info("MESSAGE " + x + " = "+ messages.get(x));
    	    }
    	    if (messages.size() == 0){
				LOG.info("<<<< RULES AND PROCESSES COMPILE >>>>");
    	    }
            assertTrue(messages.size() == 0);
    }


}
