package fr.lewon.dofus.bot.gui.config

import fr.lewon.dofus.bot.gui.MainFrame
import fr.lewon.dofus.bot.util.filemanagers.DTBConfigManager
import fr.lewon.dofus.bot.util.io.ScreenUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.ui.ConsoleLogger
import fr.lewon.dofus.bot.util.ui.LogLevel
import net.miginfocom.swing.MigLayout
import javax.swing.*

object ConfigTab : JPanel(MigLayout()) {

    private val darkModeLabel = JLabel("Dark mode")
    private val darkModeCheckBox = JCheckBox()
    private val alwaysOnTopLabel = JLabel("Always on top")
    private val alwaysOnTopCheckBox = JCheckBox()
    private val logLevelLabel = JLabel("Log Debug")
    private val logLevelComboBox = JComboBox(LogLevel.values())
    private val globalTimeoutLabel = JLabel("Global Timeout")
    private val firstNameTextField = JTextField("25")
    private val locateCursorLabel = JLabel("Locate cursor")
    private val locateCursorButton = JButton("Locate")
    private val selectAccessLabel = JLabel("Select access location")
    private val accessSelectorPane = AccessSelectorPane()

    init {
        updateDarkMode(DTBConfigManager.config.darkMode)
        addLine(darkModeLabel, darkModeCheckBox)
        darkModeCheckBox.addItemListener { updateDarkMode(darkModeCheckBox.isSelected) }

        updateAlwaysOnTop(DTBConfigManager.config.alwaysOnTop)
        addLine(alwaysOnTopLabel, alwaysOnTopCheckBox)
        alwaysOnTopCheckBox.addItemListener { updateAlwaysOnTop(alwaysOnTopCheckBox.isSelected) }

        val logLevel = LogLevel.valueOf(DTBConfigManager.config.logLevel)
        updateLogLevel(logLevel)
        logLevelComboBox.selectedItem = logLevel
        addLine(logLevelLabel, logLevelComboBox)
        logLevelComboBox.addItemListener { updateLogLevel(logLevelComboBox.selectedItem as LogLevel) }

        locateCursorButton.addActionListener { locatePoint() }

        addLine(globalTimeoutLabel, firstNameTextField)
        addLine(locateCursorLabel, locateCursorButton)
        addLine(selectAccessLabel, accessSelectorPane)
    }

    private fun locatePoint() {
        val locatorFrame = CursorLocatorFrame("Click on position to register") {
            println("PointRelative(${it.x}f, ${it.y}f)")
            Thread {
                WaitUtil.sleep(1000)
                val color = ScreenUtil.getPixelColor(it)
                println("Color : ${color.red} ${color.green} ${color.blue}")
            }.start()
        }
        locatorFrame.isVisible = true
    }

    private fun updateDarkMode(darkMode: Boolean) {
        DTBConfigManager.editConfig { it.darkMode = darkMode }
        darkModeCheckBox.isSelected = darkMode
        if (darkMode) {
            MainFrame.darkMode()
        } else {
            MainFrame.lightMode()
        }
    }

    private fun updateAlwaysOnTop(alwaysOnTop: Boolean) {
        DTBConfigManager.editConfig { it.alwaysOnTop = alwaysOnTop }
        alwaysOnTopCheckBox.isSelected = alwaysOnTop
        MainFrame.updateAlwaysOnTop(alwaysOnTop)
    }

    private fun updateLogLevel(logLevel: LogLevel) {
        DTBConfigManager.editConfig { it.logLevel = logLevel.name }
        ConsoleLogger.minLogLevel = logLevel
    }

    private fun addLine(leftComponent: JComponent, rightComponent: JComponent, separator: Boolean = true) {
        add(leftComponent)
        add(rightComponent, "width 80, al right, wrap")
        if (separator) addSeparator()
    }

    private fun addSeparator() {
        add(JSeparator(JSeparator.HORIZONTAL), "span 2 1, width max, wrap")
    }

}