import xyz.srnyx.gradlegalaxy.utility.setupAnnoyingAPI
import xyz.srnyx.gradlegalaxy.utility.spigotAPI


plugins {
    java
    id("xyz.srnyx.gradle-galaxy") version "1.1.3"
    id("io.github.goooler.shadow") version "8.1.7"
}

setupAnnoyingAPI("723fb94dbb", "xyz.srnyx", "2.0.0", "A combination of LifeSteal and Death Swap")
spigotAPI("1.8.8")
