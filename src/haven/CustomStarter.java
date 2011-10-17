package haven;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Created by IntelliJ IDEA.
 * Player: Vlad.Rassokhin@gmail.com
 * Date: 12.01.11
 * Time: 14:12
 */
public class CustomStarter {
    public static void main(final String args[]) {
        if (!CustomConfigProcessor.loadSettings()) {
            CustomConfigProcessor.setDefaultSettings();
            final JFrame configFrame = new JFrame("Screen Size");
            final Container contentPane = configFrame.getContentPane();
            final JPanel clientSettingsPanel = new JPanel(new GridBagLayout(), true);
            final JPanel ircSettingsPanel = new JPanel(new GridBagLayout(), true);
            final JButton startBtn = new JButton("Start!");
            final GridBagConstraints constraints;
            final JCheckBox ircOn = new JCheckBox("IRC Enabled", true);
            final FilteredTextField xField = new FilteredTextField();
            final FilteredTextField yField = new FilteredTextField();
            final FilteredTextField ircDefNickField = new FilteredTextField();
            final FilteredTextField ircAltNickField = new FilteredTextField();
            final JRadioButton typeStandard = new JRadioButton("Standard resolution:", true);
            final JRadioButton typeCustom = new JRadioButton("Custom resolution:", false);
            final JComboBox stdRes = new JComboBox(new Coord[]{
                    new Coord(800, 600),
                    new Coord(1024, 768),
                    new Coord(1280, 720),
                    new Coord(1280, 768),
                    new Coord(1280, 800)
            });

            configFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            stdRes.setSelectedIndex(0);
            stdRes.setEditable(false);

            xField.setNoLetters(true);
            yField.setNoLetters(true);
            xField.setColumns(4);
            yField.setColumns(4);
            xField.setText("800");
            yField.setText("600");
            xField.setEditable(false);
            yField.setEditable(false);

            ircDefNickField.setBadChars("@#$%^~&? ");
            ircAltNickField.setBadChars("@#$%^~&? ");
            ircDefNickField.setColumns(10);
            ircAltNickField.setColumns(10);
            ircDefNickField.setMaxCharacters(30);
            ircAltNickField.setMaxCharacters(30);

            contentPane.setLayout(new GridBagLayout());

            //	Adding client components
            constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.WEST;

            constraints.gridx = 0;
            constraints.gridy = 0;
            clientSettingsPanel.add(typeStandard, constraints);

            constraints.gridx = 1;
            constraints.gridwidth = 2;
            clientSettingsPanel.add(stdRes, constraints);

            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 1;
            clientSettingsPanel.add(typeCustom, constraints);

            constraints.gridx = 1;
            clientSettingsPanel.add(xField, constraints);

            constraints.gridx = 2;
            clientSettingsPanel.add(yField, constraints);

            //	Adding irc components
            constraints.gridx = 0;
            constraints.gridy = 0;
            ircSettingsPanel.add(new JLabel("Default IRC Nickname:"), constraints);

            constraints.gridy = 1;
            ircSettingsPanel.add(new JLabel("Alternate Nickname:"), constraints);

            constraints.gridx = 1;
            constraints.gridy = 0;
            ircSettingsPanel.add(ircDefNickField, constraints);

            constraints.gridy = 1;
            ircSettingsPanel.add(ircAltNickField, constraints);

            //	Adding panel components
            constraints.anchor = GridBagConstraints.NORTH;
            constraints.gridx = 0;
            constraints.gridy = 0;
            contentPane.add(clientSettingsPanel, constraints);

            constraints.gridx = 2;
            constraints.gridy = 0;
            contentPane.add(ircSettingsPanel, constraints);

            constraints.gridx = 2;
            constraints.gridy = 2;
            constraints.insets.top = 10;
            clientSettingsPanel.add(startBtn, constraints);

            constraints.gridx = 1;
            constraints.gridy = 3;
            constraints.insets.top = 0;
            clientSettingsPanel.add(ircOn, constraints);

            ircOn.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    CustomConfig.isIRCOn = ircOn.isSelected();
                }
            });

            typeStandard.addChangeListener(new ChangeListener() {
                public void stateChanged(final ChangeEvent e) {
                    if (!typeStandard.isSelected() && !typeCustom.isSelected()) {
                        typeStandard.setSelected(true);
                    }
                    if (typeStandard.isSelected()) {
                        stdRes.setEnabled(true);
                        typeCustom.setSelected(false);
                    } else {
                        stdRes.setEnabled(false);
                    }
                }
            });
            typeCustom.addChangeListener(new ChangeListener() {
                public void stateChanged(final ChangeEvent e) {
                    if (!typeStandard.isSelected() && !typeCustom.isSelected()) {
                        typeCustom.setSelected(true);
                    }
                    if (typeCustom.isSelected()) {
                        xField.setEnabled(true);
                        yField.setEnabled(true);
                        xField.setEditable(true);
                        yField.setEditable(true);
                        typeStandard.setSelected(false);
                        stdRes.setEnabled(false);
                    } else {
                        xField.setEditable(false);
                        yField.setEditable(false);
                        xField.setEnabled(false);
                        yField.setEnabled(false);
                        stdRes.setEnabled(true);
                    }
                }
            });
            xField.addFocusListener(new FocusListener() {
                public void focusGained(final FocusEvent e) {
                }

                public void focusLost(final FocusEvent e) {
                    try {
                        if (Integer.parseInt(xField.getText()) < 800) {
                            xField.setText("800");
                        }
                    } catch (NumberFormatException NFExcep) {
                        xField.setText("800");
                    }
                }
            });
            yField.addFocusListener(new FocusListener() {
                public void focusGained(final FocusEvent e) {
                }

                public void focusLost(final FocusEvent e) {
                    try {
                        if (Integer.parseInt(yField.getText()) < 600) {
                            yField.setText("600");
                        }
                    } catch (NumberFormatException NFExcep) {
                        yField.setText("600");
                    }
                }
            });
            ircDefNickField.addFocusListener(new FocusListener() {
                public void focusGained(final FocusEvent e) {
                }

                public void focusLost(final FocusEvent e) {
                    if (ircDefNickField.getText().trim().length() != 0) {
                        CustomConfig.ircDefNick = ircDefNickField.getText().trim();
                    }
                    if (ircAltNickField.getText().trim().length() == 0) {
                        CustomConfig.ircAltNick = ircDefNickField.getText().trim() + "|C";
                    }
                }
            });
            ircAltNickField.addFocusListener(new FocusListener() {
                public void focusGained(final FocusEvent e) {
                }

                public void focusLost(final FocusEvent e) {
                    if (ircAltNickField.getText().trim().length() != 0) {
                        if (ircDefNickField.getText().trim().length() == 0) {
                            ircDefNickField.setText(ircAltNickField.getText().trim());
                            CustomConfig.ircDefNick = ircDefNickField.getText();
                            ircAltNickField.setText(CustomConfig.ircDefNick + "|C");
                            return;
                        }
                        CustomConfig.ircAltNick = ircAltNickField.getText().trim();
                        return;
                    }
                    if (ircDefNickField.getText().trim().length() != 0) {
                        CustomConfig.ircAltNick = ircDefNickField.getText().trim() + "|C";
                    }
                }
            });
            startBtn.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    final int x = stdRes.isEnabled() ? ((Coord) stdRes.getSelectedItem()).x : Integer.parseInt(xField.getText());
                    final int y = stdRes.isEnabled() ? ((Coord) stdRes.getSelectedItem()).y : Integer.parseInt(yField.getText());
                    CustomConfig.setWindowSize(x, y);

                    new Thread() {
                        public void run() {
                            runGame(args);
                        }
                    }.start();

                    configFrame.dispose();
                }
            });
            configFrame.pack();
            final Toolkit toolkit = Toolkit.getDefaultToolkit();
            configFrame.setLocation((int) (toolkit.getScreenSize().getWidth() - configFrame.getWidth()) / 2,
                    (int) (toolkit.getScreenSize().getHeight() - configFrame.getHeight()) / 2);
            configFrame.setVisible(true);
        } else {
            runGame(args);
        }
    }

    public static void runGame(final String[] args) {
        CustomConfigProcessor.saveSettings();
        MainFrame.main(args);
    }
}
