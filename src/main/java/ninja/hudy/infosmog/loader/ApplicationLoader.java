package ninja.hudy.infosmog.loader;

import ninja.hudy.infosmog.scheduler.ReaderScheduler;
import ninja.hudy.infosmog.update.Configurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationLoader implements CommandLineRunner {
    @Autowired
    private Configurator configurator;

    @Autowired
    private ReaderScheduler readerScheduler;

    @Override
    public void run(String... strings) throws Exception {
        configurator.configure();
        readerScheduler.read();
        configurator.assignKnownStations();
    }


}
