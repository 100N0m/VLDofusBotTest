package fr.lewon.dofus.bot.util.filemanagers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.model.config.VldbConfig
import fr.lewon.dofus.bot.util.io.gamefiles.VldbFilesUtil
import fr.lewon.dofus.bot.util.manager.VldbManager
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets


object ConfigManager : VldbManager {

    lateinit var config: VldbConfig
    private lateinit var configFile: File

    override fun initManager() {
        configFile = File("${VldbFilesUtil.getVldbConfigDirectory()}/config")
        if (configFile.exists()) {
            config = ObjectMapper().readValue(configFile)
        } else {
            config = VldbConfig()
            saveConfig()
        }
    }

    fun editConfig(function: (VldbConfig) -> Unit) {
        function.invoke(config)
        saveConfig()
    }

    private fun saveConfig() {
        with(OutputStreamWriter(FileOutputStream(configFile, false), StandardCharsets.UTF_8)) {
            write(ObjectMapper().writeValueAsString(config))
            close()
        }
    }
}