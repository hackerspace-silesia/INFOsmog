package ninja.hudy.infosmog.update;

import ninja.hudy.infosmog.exception.UpdateException;
import ninja.hudy.infosmog.model.Configuration;
import ninja.hudy.infosmog.repository.ConfigurationRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Configurator {
    private static Logger log = Logger.getLogger(Configurator.class);

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private ScratchUpdater scratchUpdater;

    @Transactional
    public void configure() throws UpdateException {
        log.info("Start configuration");
        try {
            Map<Integer, Method> updaters = getUpdaters();
            Integer max = getLastID(updaters);

            List<Configuration> configurations = configurationRepository.findAll();

            Configuration configuration;

            if (configurations.isEmpty()) {
                scratchUpdater.createFromScratch();

                configuration = new Configuration();
                configuration.setDescription("Created from scratch");
            } else {
                log.info("Upgrade to " + max + " version");
                configuration = configurations.get(0);
                Integer currentVersion = configuration.getVersion();
                executeUpdaters(currentVersion + 1, max, updaters);
                configuration.setDescription(getDescription(updaters.get(max)));
            }

            configuration.setVersion(max);
            configurationRepository.saveAndFlush(configuration);
        } catch (Exception e) {
            throw new UpdateException(e);
        }
    }

    public void assignKnownStations() throws UpdateException {
        try {
            scratchUpdater.assignKnownStations();
        } catch (Exception e) {
            throw new UpdateException(e);
        }
    }

    private Integer getLastID(Map<Integer, Method> updaters) {
        if (updaters.isEmpty()) {
            return 0;
        }

        return updaters.keySet().stream().max((id1, id2) -> id1.compareTo(id2)).get();
    }

    private String getDescription(Method method) {
        Update update = method.getAnnotation(Update.class);
        return update.description();
    }

    private void executeUpdaters(Integer from, Integer to, Map<Integer, Method> updaters) throws InvocationTargetException, IllegalAccessException, UpdateException {
        for (int i = from; i <= to; ++i) {
            log.info("Execute " + i + " updater");
            if (!updaters.containsKey(i)) {
                throw new UpdateException("Missing updater with id: " + i);
            }
            updaters.get(i).invoke(this);
        }
    }

    private Map<Integer, Method> getUpdaters() throws UpdateException {
        Map<Integer, Method> updaters = new HashMap<>();

        Method[] methods = this.getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Update.class)) {
                Update update = method.getAnnotation(Update.class);
                if (updaters.containsKey(update.id())) {
                    throw new UpdateException("Multiple updaters with id: " + update.id());
                }

                updaters.put(update.id(), method);
            }
        }

        return updaters;
    }

}
