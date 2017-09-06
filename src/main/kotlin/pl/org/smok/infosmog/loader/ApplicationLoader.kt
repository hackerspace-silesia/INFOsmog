package pl.org.smok.infosmog.loader

import pl.org.smok.infosmog.scheduler.ReaderScheduler
import pl.org.smok.infosmog.update.Configurator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class ApplicationLoader : CommandLineRunner {
    @Autowired
    lateinit var configurator: Configurator;

    @Autowired
    lateinit var readerScheduler: ReaderScheduler;

    @Throws(Exception::class)
    override fun run(vararg strings: String) {
        configurator.configure()
        readerScheduler.read()
        configurator.assignKnownStations()
    }


}
