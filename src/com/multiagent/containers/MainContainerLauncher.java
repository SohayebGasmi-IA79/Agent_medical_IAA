package com.multiagent.containers;

import com.multiagent.agents.AgentCoordinateur;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

/**
 * Lance le Main Container JADE (avec GUI RMA) et y déploie le Coordinateur.
 *
 * <p>À exécuter EN PREMIER dans un JVM dédié.</p>
 */
public class MainContainerLauncher {

    public static final String MAIN_HOST = "127.0.0.1";
    public static final int    MAIN_PORT = 1099;
    public static final String PLATFORM_ID = "medical-platform";

    public static void main(String[] args) {
        Runtime runtime = Runtime.instance();
        runtime.setCloseVM(true);

        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "true");
        profile.setParameter(Profile.LOCAL_HOST, MAIN_HOST);
        profile.setParameter(Profile.LOCAL_PORT, String.valueOf(MAIN_PORT));
        profile.setParameter(Profile.PLATFORM_ID, PLATFORM_ID);

        AgentContainer mainContainer = runtime.createMainContainer(profile);

        try {
            AgentController coordinateur = mainContainer.createNewAgent(
                    "Coordinateur",
                    AgentCoordinateur.class.getName(),
                    new Object[]{}
            );
            coordinateur.start();
            System.out.println("[MainContainer] Agent Coordinateur démarré.");
        } catch (StaleProxyException e) {
            System.err.println("[MainContainer] Impossible de démarrer le Coordinateur : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("[MainContainer] Plateforme JADE prête sur " + MAIN_HOST + ":" + MAIN_PORT);
    }
}
