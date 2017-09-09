package ninja.hudy.infosmog.scheduler;

import ninja.hudy.infosmog.reader.Reader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class ReaderScheduler {

    private static Logger log = Logger.getLogger(ReaderScheduler.class);
    private final ListableBeanFactory listableBeanFactory;

    @Autowired
    public ReaderScheduler(ListableBeanFactory listableBeanFactory) {
        this.listableBeanFactory = listableBeanFactory;
    }

    @CacheEvict(allEntries = true, value = "county")
    @Scheduled(cron = "0 */10 * * * *")
    public void read() {
        try {
            Map<String, Reader> readers = listableBeanFactory.getBeansOfType(Reader.class);

            readers.values().forEach(Reader::read);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
