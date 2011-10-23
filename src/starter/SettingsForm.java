package starter;

import com.memetix.mst.language.Language;
import haven.Coord;
import haven.CustomConfig;
import org.japura.gui.CheckList;
import org.japura.gui.model.DefaultListCheckModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

/**
 * // TODO: write javadoc
 * Created by IntelliJ IDEA.
 * Date: 21.10.11
 * Time: 14:59
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public class SettingsForm {
    JPanel Settings;
    private JRadioButton standartResolutionRadioButton;
    private JRadioButton customResolutionRadioButton;
    private JComboBox standartResolutionComboList;
    private JSpinner customResolutionWidth;
    private JSpinner customResolutionHeight;
    private JPanel customResolutionPanel;
    private JTabbedPane tabbedPane1;
    private JPanel resolutionPanel;
    private JPanel panelAction;
    private JButton bExit;
    private JButton bRunGame;
    private JButton bSaveSettings;
    private JTextField tfIRCServer;
    private JTextField tfIRCNick;
    private JTextField tfIRCNickAlt;
    private JTextField tfIRCChannels;
    private JCheckBox cbIRCEnabled;
    private JCheckBox fullScreenCheckBox;
    private JCheckBox newMinimapCheckBox;
    private JCheckBox newChatCheckBox;
    private JCheckBox addTimestampInChatCheckBox;
    private JCheckBox showDowsingDirecionCheckBox;
    private JCheckBox alwaysShowHeartlingNamesCheckBox;
    private JCheckBox compressScreenshotsCheckBox;
    private JCheckBox excludeUIFromScreenshotCheckBox;
    private JCheckBox excludeNamesFromScreenshotCheckBox;
    private JCheckBox useOptimizedClaimHiglightingCheckBox;
    private JCheckBox XRayCheckBox;
    private JCheckBox nightVisionCheckBox;
    private JCheckBox showSmileysInChatCheckBox;
    private JCheckBox alwaysShowOtherKinCheckBox;
    private JCheckBox showItemQualityCheckBox;
    private JCheckBox fastMenuCheckBox;
    private JCheckBox hidingEnabledCheckBox;
    private CheckList hidingList;
    private JCheckBox soundEnabledCheckBox;
    private JCheckBox musicEnabledCheckBox;
    private JSlider soundSlider;
    private JSlider musicSlider;
    private JCheckBox translationEnabledCheckBox;
    private JComboBox translationLanguageComboBox;
    private JTextField translationAPIKeyTextBox;
    private JCheckBox wallsCheckBox;
    private ButtonGroup resolutionTypeButtonGroup = new ButtonGroup();
    private final DefaultListCheckModel listCheckModel = new DefaultListCheckModel();
    private CustomConfig myConfig = new CustomConfig();
    private static final Set<HidingObjectDescriptor> HODS = new HashSet<HidingObjectDescriptor>();
    private static final Coord[] STANDART_RESOLUTION = new Coord[]{
            new Coord(800, 600),
            new Coord(1024, 768),
            new Coord(1280, 720),
            new Coord(1280, 768),
            new Coord(1280, 800)
    };

    public SettingsForm(@NotNull final Callback callback) {
        bSaveSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                saveSettings();
                callback.saveConfig(myConfig);
            }
        });
        bRunGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                saveSettings();
                callback.saveConfig(myConfig);
                callback.startGame();
            }
        });
        bExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                callback.exit();
            }
        });


        resolutionTypeButtonGroup.add(standartResolutionRadioButton);
        resolutionTypeButtonGroup.add(customResolutionRadioButton);
        for (final Coord res : STANDART_RESOLUTION) {
            standartResolutionComboList.addItem(res);
        }
        customResolutionRadioButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                final JRadioButton radioButton = (JRadioButton) e.getSource();
                customResolutionPanel.setEnabled(radioButton.isSelected());
            }
        });

        for (final String[] cb : CustomConfig.CHECKBOXES_LIST) {
            HODS.add(new HidingObjectDescriptor(cb[0], cb[1]));
        }
        for (final HidingObjectDescriptor hod : HODS) {
            listCheckModel.addElement(hod);
        }
        hidingList.setModel(listCheckModel);
        translationLanguageComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, ((Language) value).name(), index, isSelected, cellHasFocus);    //To change body of overridden methods use File | Settings | File Templates.
            }
        });
    }

    private void createUIComponents() {
    }

    public void useConfig(final CustomConfig config) {
        myConfig = config;
        updateFromCurrentConfig();
    }

    public CustomConfig getConfig() {
        return myConfig;
    }

    private void saveSettings() {
        // Main
        {
            if (standartResolutionRadioButton.isSelected()) {
                myConfig.setWindowSize((Coord) standartResolutionComboList.getSelectedItem());
            } else {
                myConfig.setWindowSize((Integer) customResolutionWidth.getValue(), (Integer) customResolutionHeight.getValue());
                //TODO: check for value > minimum
            }
        }

        // Graphic
        {
            myConfig.setNew_minimap(newMinimapCheckBox.isSelected());
            myConfig.setNew_chat(newChatCheckBox.isSelected());
            myConfig.setUse_smileys(showSmileysInChatCheckBox.isSelected());
            myConfig.setAddChatTimestamp(addTimestampInChatCheckBox.isSelected());
            myConfig.setShowNames(alwaysShowHeartlingNamesCheckBox.isSelected());
            myConfig.setShowOtherNames(alwaysShowOtherKinCheckBox.isSelected());
            myConfig.setShowq(showItemQualityCheckBox.isSelected());
            myConfig.setSshot_compress(compressScreenshotsCheckBox.isSelected());
            myConfig.setSshot_noui(excludeUIFromScreenshotCheckBox.isSelected());
            myConfig.setSshot_nonames(excludeNamesFromScreenshotCheckBox.isSelected());
            myConfig.setNewclaim(useOptimizedClaimHiglightingCheckBox.isSelected());
            myConfig.setXray(XRayCheckBox.isSelected());
            myConfig.setHasNightVision(nightVisionCheckBox.isSelected());
            myConfig.setShowDirection(showDowsingDirecionCheckBox.isSelected());
            myConfig.setFastFlowerAnim(fastMenuCheckBox.isSelected());
        }

        // Audio
        {
            myConfig.setSoundOn(soundEnabledCheckBox.isSelected());
            myConfig.setSfxVol(soundSlider.getValue());
            myConfig.setMusicOn(musicEnabledCheckBox.isSelected());
            myConfig.setMusicVol(musicSlider.getValue());
        }

        // Hiding Objects
        {
            myConfig.setHideObjects(hidingEnabledCheckBox.isSelected());
            final Set<String> checked = new HashSet<String>();
            for (final Object o : listCheckModel.getCheckeds()) {
                final HidingObjectDescriptor hod = (HidingObjectDescriptor) o;
                checked.add(hod.value);
            }
            myConfig.setHidingObjects(checked);
        }

        // Translation
        {
            myConfig.getTranslator().turn(translationEnabledCheckBox.isSelected());
            myConfig.setTranslatorLanguage((Language) translationLanguageComboBox.getSelectedItem());
            myConfig.setTranslatorApiKey(translationAPIKeyTextBox.getText());
        }

        // IRC
        {
            // TODO
        }
    }


    private void updateFromCurrentConfig() {
        // Main
        {
            final Coord windowSize = myConfig.getWindowSize();
            boolean flag = false;
            for (int i = 0; i < standartResolutionComboList.getItemCount(); i++) {
                final Coord coord = (Coord) standartResolutionComboList.getItemAt(i);
                if (coord.equals(windowSize)) {
                    standartResolutionRadioButton.setSelected(true);
                    standartResolutionComboList.setSelectedIndex(i);
                    break;
                }
                flag = true;
            }
            if (!flag) {
                customResolutionRadioButton.setSelected(true);
                customResolutionPanel.setEnabled(true);
                customResolutionWidth.setValue(windowSize.getX());
                customResolutionHeight.setValue(windowSize.getY());
            } else {
                customResolutionRadioButton.setSelected(false);
                customResolutionPanel.setEnabled(false);
            }
        }

        // Graphic
        {
            newMinimapCheckBox.setSelected(myConfig.isNew_minimap());
            newChatCheckBox.setSelected(myConfig.isNew_chat());
            showSmileysInChatCheckBox.setSelected(myConfig.isUse_smileys());
            addTimestampInChatCheckBox.setSelected(myConfig.isAddChatTimestamp());
            alwaysShowHeartlingNamesCheckBox.setSelected(myConfig.isShowNames());
            alwaysShowOtherKinCheckBox.setSelected(myConfig.isShowOtherNames());
            showItemQualityCheckBox.setSelected(myConfig.isShowq());
            compressScreenshotsCheckBox.setSelected(myConfig.isScreenShotsCompressing());
            excludeUIFromScreenshotCheckBox.setSelected(myConfig.isScreenShotExcludeUI());
            excludeNamesFromScreenshotCheckBox.setSelected(myConfig.isScreenShotsExcludeNames());
            useOptimizedClaimHiglightingCheckBox.setSelected(myConfig.isNewclaim());
            XRayCheckBox.setSelected(myConfig.isXray());
            nightVisionCheckBox.setSelected(myConfig.isNightVision());
            showDowsingDirecionCheckBox.setSelected(myConfig.isShowDirection());
            fastMenuCheckBox.setSelected(myConfig.isFastFlowerAnim());
        }

        // Audio
        {
            soundEnabledCheckBox.setSelected(myConfig.isSoundOn());
            soundSlider.setValue(myConfig.getSfxVol());
            musicEnabledCheckBox.setSelected(myConfig.isMusicOn());
            musicSlider.setValue(myConfig.getMusicVol());
        }

        // Hiding Objects
        {
            hidingEnabledCheckBox.setSelected(myConfig.isHideObjects());
            listCheckModel.removeChecks();
            //TODO: optimize
            for (final String s : myConfig.getHidingObjects()) {
                for (int i = 0; i < listCheckModel.getSize(); i++) {
                    final HidingObjectDescriptor hod = (HidingObjectDescriptor) listCheckModel.getElementAt(i);
                    if (hod.value.equals(s)) {
                        listCheckModel.setCheck(hod);
                    }

                }
            }
        }

        // Translation
        {
            translationEnabledCheckBox.setSelected(myConfig.getTranslator().isWorking());
            for (final Language language : myConfig.getTranslator().getAvailableLanguages().values()) {
                translationLanguageComboBox.addItem(language);
            }
            translationLanguageComboBox.setSelectedItem(myConfig.getTranslatorLanguage());
            translationAPIKeyTextBox.setText(myConfig.getTranslatorApiKey());
        }

        // IRC
        {
            // TODO
        }
    }

    private static class HidingObjectDescriptor {
        @NotNull
        final String description;
        @NotNull
        final String value;

        private HidingObjectDescriptor(@NotNull final String description, @NotNull final String value) {
            this.description = description;
            this.value = value;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    public static interface Callback {
        void startGame();

        void exit();

        void saveConfig(@NotNull CustomConfig config);
    }

}
