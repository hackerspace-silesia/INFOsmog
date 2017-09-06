package pl.org.smok.infosmog.scheduler

import pl.org.smok.infosmog.reader.Reader
import org.apache.log4j.Logger
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Controller
import java.util.function.Consumer

@Controller
class ReaderScheduler @Autowired
constructor(private val listableBeanFactory: ListableBeanFactory) {

    @CacheEvict(allEntries = true, value = "county")
    @Scheduled(cron = "0 */10 * * * *")
    fun read() {
        try {
            val readers = listableBeanFactory.getBeansOfType<Reader>(Reader::class.java)

            readers.values.forEach(Consumer<Reader> { it.read() })
        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }

    companion object {
        private val log = Logger.getLogger(ReaderScheduler.javaClass)
    }
}
