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

package com.synthbot.jasiohost;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * The <code>ExampleHost</code> demonstrates how to use an
 * <code>AsioDriver</code> in order to read and write audio from a loaded ASIO
 * driver. A small GUI is presented, allowing the user to select any of the
 * available ASIO drivers on the system. The <i>Start</i> button loads the
 * driver and plays a 440Hz tone. The <i>Stop</i> button stops this process and
 * unloads the driver. The <i>Control Panel</i> button opens the driver's
 * control panel for any additional configuration.
 */
public class ExampleHost extends JFrame
{

    private static final long serialVersionUID = 1L;

    SimpleAudioPlayer audioPlayer;

    public ExampleHost()
    {
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

        buttonStart.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if (audioPlayer == null)
                {
                    audioPlayer = new SimpleAudioPlayer();
                    String driverName = comboBox.getSelectedItem().toString();

                    // "C:\\Temp\\8track_1min_helicopter.wav"
                    // "C:\\Temp\\8channel.wav"
                    String wavFile = "C:\\Temp\\___ambi_to_cube_mix171.wav";
                    try
                    {
                        audioPlayer.Initialise(driverName, wavFile);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        buttonStop.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if (audioPlayer != null)
                {
                    audioPlayer.Shutdown();
                    audioPlayer = null;
                }
            }
        });

        buttonControlPanel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                /*
                 * if (asioDriver != null &&
                 * asioDriver.getCurrentState().ordinal() >=
                 * AsioDriverState.INITIALIZED.ordinal()) {
                 * asioDriver.openControlPanel(); }
                 */
            }
        });

        buttonRewindBeginning.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if (audioPlayer != null)
                {
                    audioPlayer.Seek(0.0);
                }
            }
        });

        buttonBack1Sec.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if (audioPlayer != null)
                {
                    audioPlayer.Seek(audioPlayer.CurrentTime() - 1.0);
                }
            }
        });

        buttonForward1Sec.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if (audioPlayer != null)
                {
                    audioPlayer.Seek(audioPlayer.CurrentTime() + 1.0);
                }
            }
        });

        buttonPause.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if (audioPlayer != null)
                {
                    audioPlayer.Pause();
                }
            }
        });

        buttonResume.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if (audioPlayer != null)
                {
                    audioPlayer.Resume();
                }
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
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent event)
            {
                if (audioPlayer != null)
                {
                    audioPlayer.Shutdown();
                    audioPlayer = null;
                }
            }
        });

        this.setSize(320, 170);
        this.setResizable(false);
        this.setVisible(true);
    }

    public static void main(String[] args)
    {
        @SuppressWarnings("unused")
        ExampleHost host = new ExampleHost();
    }
}
