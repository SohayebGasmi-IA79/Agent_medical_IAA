package com.multiagent.containers;

import com.multiagent.agents.AgentPatient;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

/**
 * Lance un Container dédié à l'agent Patient, qui ouvre l'IHM Swing.
 *
 * <p>À exécuter APRÈS le Main Container et le SpecialistsContainer,
 * dans un JVM dédié.</p>
 */
public class PatientContainerLauncher {

    public static void main(String[] args) {
        Runtime runtime = Runtime.instance();
        runtime.setCloseVM(false);

        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, MainContainerLauncher.MAIN_HOST);
        profile.setParameter(Profile.MAIN_PORT, String.valueOf(MainContainerLauncher.MAIN_PORT));
        profile.setParameter(Profile.CONTAINER_NAME, "PatientContainer");
        profile.setParameter(Profile.PLATFORM_ID, MainContainerLauncher.PLATFORM_ID);

        AgentContainer container = runtime.createAgentContainer(profile);

        try {
            AgentController patient = container.createNewAgent(
                    "Patient",
                    AgentPatient.class.getName(),
                    new Object[]{}
            );
            patient.start();
            System.out.println("[PatientContainer] Agent Patient démarré — ouverture de l'IHM Swing.");
        } catch (StaleProxyException e) {
            System.err.println("[PatientContainer] Impossible de démarrer l'agent Patient : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
