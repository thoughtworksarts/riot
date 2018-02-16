/*
 *  Copyright 2009,2010 Martin Roth (mhroth@gmail.com)
 *
 *  This file is part of JAsioHost.
 *
 *  JAsioHost is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JAsioHost is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with JAsioHost.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io.thoughtworksarts.riot.audio;

import com.synthbot.jasiohost.AsioDriver;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The <code>ExampleHost</code> demonstrates how to use an
 * <code>AsioDriver</code> in order to read and write audio from a loaded ASIO
 * driver. A small GUI is presented, allowing the user to select any of the
 * available ASIO drivers on the system. The <i>Start</i> button loads the
 * driver and plays a 440Hz tone. The <i>Stop</i> button stops this process and
 * unloads the driver. The <i>Control Panel</i> button opens the driver's
 * control panel for any additional configuration.
 */
public class ExampleHost extends JFrame {

    private static final long serialVersionUID = 1L;

    AudioPlayer audioPlayer;

    public ExampleHost() {
        super("JAsioHost Example");

        final JComboBox comboBox = new JComboBox(AsioDriver.getDriverNames().toArray());
        final JButton buttonStart = new JButton("Start Driver");
        final JButton buttonStop = new JButton("Stop Driver");
        final JButton buttonRewindBeginning = new JButton("BackToStart");
        final JButton buttonBack1Sec = new JButton("Back 1 sec");
        final JButton buttonForward1Sec = new JButton("Forward 1 sec");
        final JButton buttonPause = new JButton("Pause");
        final JButton buttonResume = new JButton("Resume");

        final JButton buttonControlPanel = new JButton("Control Panel");

        buttonStart.addActionListener((event) -> {
            if (audioPlayer == null) {
                audioPlayer = new AudioPlayer();
                String driverName = comboBox.getSelectedItem().toString();
                String wavFile = "src/main/resources/audio/audio.wav";
                try {
                    audioPlayer.initialise(driverName, wavFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buttonStop.addActionListener((event) -> {
            if (audioPlayer != null) {
                audioPlayer.shutdown();
                audioPlayer = null;
            }
        });

        buttonControlPanel.addActionListener((event) -> {
            /*
             * if (asioDriver != null &&
             * asioDriver.getCurrentState().ordinal() >=
             * AsioDriverState.INITIALIZED.ordinal()) {
             * asioDriver.openControlPanel(); }
             */
        });

        buttonRewindBeginning.addActionListener((event) -> {
            if (audioPlayer != null) {
                audioPlayer.seek(0.0);
            }
        });

        buttonBack1Sec.addActionListener((event) -> {
            if (audioPlayer != null) {
                audioPlayer.seek(audioPlayer.currentTime() - 1.0);
            }
        });

        buttonForward1Sec.addActionListener((event) -> {
            if (audioPlayer != null) {
                audioPlayer.seek(audioPlayer.currentTime() + 1.0);
            }
        });

        buttonPause.addActionListener((event) -> {
            if (audioPlayer != null) {
                audioPlayer.pause();
            }
        });

        buttonResume.addActionListener((event) -> {
            if (audioPlayer != null) {
                audioPlayer.resume();
            }
        });

        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        this.add(comboBox);
        panel.add(buttonStart);
        panel.add(buttonStop);
        panel.add(buttonControlPanel);

        this.add(panel);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(buttonRewindBeginning);
        panel.add(buttonBack1Sec);
        panel.add(buttonForward1Sec);

        this.add(panel);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(buttonPause);
        panel.add(buttonResume);

        this.add(panel);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                if (audioPlayer != null) {
                    audioPlayer.shutdown();
                    audioPlayer = null;
                }
            }
        });

        this.setSize(320, 170);
        this.setResizable(false);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        @SuppressWarnings("unused")
        ExampleHost host = new ExampleHost();
    }
}
