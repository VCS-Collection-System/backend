package com.redhat.service.cmd;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import com.redhat.service.SpringContextConfig;
import com.redhat.vcs.model.VaccineCardDocument;

import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessReplayCommand implements Command {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessReplayCommand.class);

    private static final String PROCESS_DEF = "vax_card_review_workflow";
    private static final String VCS_EMAIL_ENABLED = "vcs.email.enable";

    private final boolean enabled =
            System.getenv("PROCESS_REPLAY_CMD_ENABLED") != null && Boolean
                    .parseBoolean(System.getenv("PROCESS_REPLAY_CMD_ENABLED"));

    @Override
    public ExecutionResults execute(CommandContext ctx) {
        if (!enabled) {
            LOG.warn("{} is disabled.", this.getClass().getName());
            return new ExecutionResults();
        }

        if (ctx.getData("cutOfDateTime") == null) {
            throw new RuntimeException("cutOfDateTime is missing");
        }

        Boolean dryRun = isDryRun(ctx);
        LocalDateTime startDateTime = LocalDateTime.parse((String) ctx.getData("startDateTime"));
        LocalDateTime endDateTime = LocalDateTime.parse((String) ctx.getData("endDateTime"));
        List<Long> processInstanceList = (List) ctx.getData("processInstanceList");

        ProcessService processService = SpringContextConfig.getBean(ProcessService.class);
        RuntimeDataService runtimeDataService = SpringContextConfig.getBean(RuntimeDataService.class);

        EntityManagerFactory emf = getEmf(ctx);

        List<String> restarted = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Disable email notifications
        System.setProperty(VCS_EMAIL_ENABLED, "false");
        LOG.info(">>> Email enabled {}", System.getProperty(VCS_EMAIL_ENABLED));

        Collection<ProcessInstanceDesc> processInstances
                = runtimeDataService.getProcessInstancesByProcessDefinition(
                        PROCESS_DEF, Arrays.asList(ProcessInstance.STATE_ACTIVE), null);

        for (ProcessInstanceDesc p : processInstances) {
            Long pid = p.getId();

            Map<String, Object> params = processService.getProcessInstanceVariables(p.getDeploymentId(), pid);
            VaccineCardDocument document = (VaccineCardDocument) params.get("document");

            WorkflowProcessInstanceImpl pi = (WorkflowProcessInstanceImpl) processService.getProcessInstance(pid);

            LocalDateTime piStartDate = LocalDateTime.ofInstant(pi.getStartDate().toInstant(), ZoneId.systemDefault());

            if (dateOutsideRange(startDateTime, endDateTime, piStartDate)) {
                continue;
            }

            if (processInList(processInstanceList, pi)) {
                continue;
            }

            if (document == null) {
                errors.add("PID: " + pid +". Document is null");
                continue;
            }

            EntityManager em = emf.createEntityManager();

            try {
                if (!params.containsKey("document")) {
                    throw new RuntimeException("Document id is missing");
                }

                if (!dryRun) {
                    // remove doc entry from cs_document_task_mapping
                    em.getTransaction().begin();
                    deleteTaskDocumentMapping(em, document.getId(), dryRun);

                    processService.abortProcessInstance(pid);
                    em.getTransaction().commit();
                }

                restarted.add(" PID: " + pid + ", Document ID: " + document.getId());
                LOG.info(">>> Process {} successfully restarted", pid);
            } catch(Exception e) {
                errors.add("PID: " + pid + ". " + e.getMessage() + ", Document Id: " + document.getId());
                LOG.error(">>> Error while restarting Process " + e +".", e);
                em.getTransaction().rollback();
            }
        }

        genReport(restarted, errors);

        // Enable email notifications and send report
        System.setProperty(VCS_EMAIL_ENABLED, "true");
        LOG.info(">>> Email enabled {}", System.getProperty(VCS_EMAIL_ENABLED));

        return new ExecutionResults();
    }

    /**
     * Determines if the specified process instance is in the supplied list, doing some basic safety checks on said list.
     * @param processInstanceList List of process instances we have
     * @param pi Process instance to look for
     * @return Whether or not process instance is in list
     */
    boolean processInList(List<Long> processInstanceList, WorkflowProcessInstanceImpl pi) {
        if (processInstanceList == null) {
            return false;
        }
        
        for (Long possibleMatch : processInstanceList) {
            if(possibleMatch.equals(pi.getId())) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Determines if process instance's start time is outside the date range of the context
     * @param startDateTime Start time of context
     * @param endDateTime End time of context
     * @param piStartDate Start time of process instance
     * @return Whether or not the process instance time is in the range
     */
    boolean dateOutsideRange(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDateTime piStartDate) {
        return piStartDate.isBefore(startDateTime) || piStartDate.isAfter(endDateTime);
    }

    /**
     * Determines if we're in dry run mode or not.
     * @param ctx Commend context with needed information.
     * @return Whether or not we're in dry run
     */
    boolean isDryRun(CommandContext ctx) {
        return ctx.getData("dryRun") != null ? (Boolean) ctx.getData("dryRun") : true;
    }

    private void genReport(List<String> restarted, List<String> errors ) {
        LOG.info("### ProcessReplayCommand\n");
        LOG.info("Total: " + restarted.size() + errors.size());
        LOG.info(", Success: " + restarted.size());
        LOG.info(", Failures: " + errors.size());
        LOG.info("\n");

        LOG.info("## Success: " + restarted.size());
        restarted.forEach(e -> LOG.info("\t-" + e ));

        LOG.info("## Failures: " + errors.size());
        errors.forEach(e -> LOG.info("\t-" + e ));
    }

    private EntityManagerFactory getEmf(CommandContext ctx) {
        String emfName = (String) ctx.getData("EmfName");
        if (emfName == null) {
            emfName = "org.jbpm.domain";
        }
        return EntityManagerFactoryManager.get().getOrCreate(emfName);
    }

    private void deleteTaskDocumentMapping(EntityManager em, Long documentId, boolean dryRun) {
        if (!dryRun) {
           Query q = em.createQuery("delete from DocumentTaskMapping d where d.documentId=:id");
           q.setParameter("id", documentId);
           q.executeUpdate();
           LOG.info(">>>> DocumentTaskMapping with document_id {} deleted", documentId);
        }
    }
}
