package com.redhat.vcs_kjar.drools;

import java.util.List;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class DroolsUtil {
    
    public static void fireAllRules(List<?> inputs, String ruleFlowGroup) {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kContainer = kieServices.getKieClasspathContainer();
        KieBase kbase = kContainer.getKieBase();
        KieSession session = kbase.newKieSession();
        if (ruleFlowGroup !=null ) {
            session.getAgenda().getAgendaGroup(ruleFlowGroup).setFocus();
        }
        try {
            inputs.forEach(session::insert);
            session.fireAllRules();
        } finally {
            session.dispose();
        }
    }

}
