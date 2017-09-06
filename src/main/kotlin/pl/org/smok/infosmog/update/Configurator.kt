package pl.org.smok.infosmog.update

import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pl.org.smok.infosmog.exception.UpdateException
import pl.org.smok.infosmog.model.Configuration
import pl.org.smok.infosmog.repository.ConfigurationRepository

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.HashMap

@Component
class Configurator {

    @Autowired
    lateinit var configurationRepository: ConfigurationRepository;

    @Autowired
    private val scratchUpdater: ScratchUpdater? = null

    @Transactional
    @Throws(UpdateException::class)
    fun configure() {
        log.info("Start configuration")
        try {
            val updaters = updaters
            val max = getLastID(updaters)

            val configurations = configurationRepository.findAll()

            val configuration: Configuration

            if (configurations.isEmpty()) {
                scratchUpdater!!.createFromScratch()

                configuration = Configuration()
                configuration.description = "Created from scratch"
            } else {
                log.info("Upgrade to $max version")
                configuration = configurations[0]
                val currentVersion = configuration.version
                executeUpdaters(currentVersion!! + 1, max, updaters)
                configuration.description = updaters[max]?.let { getDescription(it) }
            }

            configuration.version = max
            configurationRepository.saveAndFlush(configuration)
        } catch (e: Exception) {
            throw UpdateException(e)
        }

    }

    @Throws(UpdateException::class)
    fun assignKnownStations() {
        try {
            scratchUpdater!!.assignKnownStations()
        } catch (e: Exception) {
            throw UpdateException(e)
        }

    }

    private fun getLastID(updaters: Map<Int, Method>): Int {
        if (updaters.isEmpty()) {
            return 0
        }

        return updaters.keys.stream().max { id1, id2 -> id1!!.compareTo(id2!!) }.get()
    }

    private fun getDescription(method: Method): String {
        val update = method.getAnnotation<Update>(Update::class.java!!)
        return update.description
    }

    @Throws(InvocationTargetException::class, IllegalAccessException::class, UpdateException::class)
    private fun executeUpdaters(from: Int, to: Int, updaters: Map<Int, Method>) {
        for (i in from..to) {
            log.info("Execute $i updater")
            if (!updaters.containsKey(i)) {
                throw UpdateException("Missing updater with id: " + i)
            }
            updaters[i]?.invoke(this)
        }
    }

    private val updaters: Map<Int, Method>
        @Throws(UpdateException::class)
        get() {
            val updaters = HashMap<Int, Method>()

            val methods = this.javaClass.getMethods()
            for (method in methods) {
                if (method.isAnnotationPresent(Update::class.java)) {
                    val update = method.getAnnotation(Update::class.java!!)
                    if (updaters.containsKey(update.id)) {
                        throw UpdateException("Multiple updaters with id: " + update.id)
                    }
                    updaters.put(update.id, method)
                }
            }

            return updaters
        }

    companion object {
        private val log = Logger.getLogger(Configurator::class.java!!)
    }

}
