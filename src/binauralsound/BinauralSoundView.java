/* 
 * The MIT License
 *
 * Copyright 2015 Mark Longstaff-Tyrrell.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package binauralsound;

import wave.WaveBuffer;
import java.awt.Color;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import wave.WavePlayer;
import wave.WaveWriter;

/**
 * The application's main frame.
 */
public class BinauralSoundView extends FrameView implements HRTFUpdateListener
{

    HRTFManager hrtfManager;

    public BinauralSoundView(SingleFrameApplication app)
    {
        super(app);

        initComponents();

        // debug
//        jTextField1.setText("/home/mark/Development/Current/Software/BinauralSound/IRC_1002.zip");
//        jTextField2.setText("/home/mark/konnichiwa.wav");
        jTextField1.setText("");
        jTextField2.setText("");

        leftHRTFView.setColour(Color.blue);
        rightHRTFView.setColour(Color.red);

        leftLabel.setText("Left HRIR");
        rightLabel.setText("Right HRIR");
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = BinauralSoundApp.getApplication().getMainFrame();
            aboutBox = new BinauralSoundAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        BinauralSoundApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        startButton = new javax.swing.JButton();
        soundTestButton = new javax.swing.JButton();
        hrirFileBrowse = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        sampleFileBrowse = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        leftLabel = new javax.swing.JLabel();
        rightLabel = new javax.swing.JLabel();
        rightHRTFView = new binauralsound.HRTFView();
        leftHRTFView = new binauralsound.HRTFView();
        generateButton = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();

        mainPanel.setName("mainPanel"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(binauralsound.BinauralSoundApp.class).getContext().getResourceMap(BinauralSoundView.class);
        startButton.setText(resourceMap.getString("playButton.text")); // NOI18N
        startButton.setName("playButton"); // NOI18N
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        soundTestButton.setText(resourceMap.getString("testButton.text")); // NOI18N
        soundTestButton.setName("testButton"); // NOI18N
        soundTestButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                soundTestButtonActionPerformed(evt);
            }
        });

        hrirFileBrowse.setText(resourceMap.getString("browseHRTF.text")); // NOI18N
        hrirFileBrowse.setName("browseHRTF"); // NOI18N
        hrirFileBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hrirFileBrowseActionPerformed(evt);
            }
        });

        jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
        jTextField1.setName("jTextField1"); // NOI18N

        sampleFileBrowse.setText(resourceMap.getString("sampleFileBrowse.text")); // NOI18N
        sampleFileBrowse.setName("sampleFileBrowse"); // NOI18N
        sampleFileBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sampleFileBrowseActionPerformed(evt);
            }
        });

        jTextField2.setText(resourceMap.getString("jTextField2.text")); // NOI18N
        jTextField2.setName("jTextField2"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        leftLabel.setText(resourceMap.getString("leftLabel.text")); // NOI18N
        leftLabel.setName("leftLabel"); // NOI18N

        rightLabel.setText(resourceMap.getString("rightLabel.text")); // NOI18N
        rightLabel.setName("rightLabel"); // NOI18N

        rightHRTFView.setName("rightHRTFView"); // NOI18N

        javax.swing.GroupLayout rightHRTFViewLayout = new javax.swing.GroupLayout(rightHRTFView);
        rightHRTFView.setLayout(rightHRTFViewLayout);
        rightHRTFViewLayout.setHorizontalGroup(
            rightHRTFViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 608, Short.MAX_VALUE)
        );
        rightHRTFViewLayout.setVerticalGroup(
            rightHRTFViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 74, Short.MAX_VALUE)
        );

        leftHRTFView.setName("leftHRTFView"); // NOI18N

        javax.swing.GroupLayout leftHRTFViewLayout = new javax.swing.GroupLayout(leftHRTFView);
        leftHRTFView.setLayout(leftHRTFViewLayout);
        leftHRTFViewLayout.setHorizontalGroup(
            leftHRTFViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 608, Short.MAX_VALUE)
        );
        leftHRTFViewLayout.setVerticalGroup(
            leftHRTFViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 87, Short.MAX_VALUE)
        );

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(binauralsound.BinauralSoundApp.class).getContext().getActionMap(BinauralSoundView.class, this);
        generateButton.setAction(actionMap.get("generateFiles")); // NOI18N
        generateButton.setText(resourceMap.getString("generateButton.text")); // NOI18N
        generateButton.setName("generateButton"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(leftHRTFView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rightHRTFView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(hrirFileBrowse)
                            .addComponent(sampleFileBrowse)))
                    .addComponent(leftLabel)
                    .addComponent(rightLabel)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(startButton)
                        .addGap(163, 163, 163)
                        .addComponent(generateButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 156, Short.MAX_VALUE)
                        .addComponent(soundTestButton)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hrirFileBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sampleFileBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(leftLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(leftHRTFView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(rightLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rightHRTFView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startButton)
                    .addComponent(soundTestButton)
                    .addComponent(generateButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_startButtonActionPerformed
    {//GEN-HEADEREND:event_startButtonActionPerformed
        
        WavePlayer wp = new WavePlayer(jTextField2.getText());

        wp.setHrtfUpdateListener(this);

        hrtfManager = new HRTFManager();
        
        if(hrtfManager.loadZip(jTextField1.getText())==true)
        {
            startButton.setEnabled(false);
            soundTestButton.setEnabled(false);
            generateButton.setEnabled(false);
            hrirFileBrowse.setEnabled(false);
            sampleFileBrowse.setEnabled(false);

            wp.setHrtfManager(hrtfManager);
            new Thread(wp).start();
        }
        else
            System.out.println("Couldn't open HRIR file " + jTextField1.getText());

        //System.out.println("Event action: "+evt.getActionCommand());

    }//GEN-LAST:event_startButtonActionPerformed

    private void soundTestButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_soundTestButtonActionPerformed
    {//GEN-HEADEREND:event_soundTestButtonActionPerformed
        WavePlayer wp = new WavePlayer(WavePlayer.TEST_MODE);
        new Thread(wp).start();
    }//GEN-LAST:event_soundTestButtonActionPerformed

    // load ZIP file
    private void hrirFileBrowseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_hrirFileBrowseActionPerformed
    {//GEN-HEADEREND:event_hrirFileBrowseActionPerformed
        selectFile(jTextField1, "zip", "ZIP files", "Select HRIR ZIP file pack");
    }//GEN-LAST:event_hrirFileBrowseActionPerformed

    // load sample file
    private void sampleFileBrowseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_sampleFileBrowseActionPerformed
    {//GEN-HEADEREND:event_sampleFileBrowseActionPerformed
        selectFile(jTextField2, "wav", "WAV files", "Select WAV file to transform");
    }//GEN-LAST:event_sampleFileBrowseActionPerformed

    private void selectFile(JTextField textField, String extension, String description, String dialogTitle)
    {
        final JFileChooser fc = new JFileChooser();

        JFrame frame=new JFrame();

        fc.addChoosableFileFilter(new ExtensionFilter(extension,description));
        fc.setAcceptAllFileFilterUsed(false);
        fc.setMultiSelectionEnabled(false);
        fc.setDialogTitle(dialogTitle);
        fc.setCurrentDirectory(new File(textField.getText()));

        int returnVal = fc.showDialog(frame,"Open");

        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = fc.getSelectedFile();
            if(file!=null)
            {
                textField.setText(file.getAbsolutePath());
            }
        }
    }

    public void finished()
    {
        startButton.setEnabled(true);
        soundTestButton.setEnabled(true);
        generateButton.setEnabled(true);
        hrirFileBrowse.setEnabled(true);
        sampleFileBrowse.setEnabled(true);

        MessageBox mb = new MessageBox("Information","Finished",MessageBox.DIALOG_OK);
        mb.show();
    }

    public void update(WaveBuffer hrtf, String name)
    {
        leftLabel.setText("Left HRIR: "+name+" degrees");
        rightLabel.setText("Right HRIR: "+name+" degrees");

        leftHRTFView.dataReady(hrtf.leftChannel);
        rightHRTFView.dataReady(hrtf.rightChannel);
    }

    @Action
    public void generateFiles()
    {
        final JFileChooser fc = new JFileChooser();

        JFrame frame=new JFrame();

        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle("Select output directory");

        int returnVal = fc.showDialog(frame,"Generate files");


        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = fc.getSelectedFile();

            if(file!=null)
            {
                WaveWriter ww = new WaveWriter(jTextField2.getText(), file.getAbsolutePath());

                ww.setHrtfUpdateListener(this);

                hrtfManager = new HRTFManager();

                if(hrtfManager.loadZip(jTextField1.getText())==true)
                {
                    startButton.setEnabled(false);
                    soundTestButton.setEnabled(false);
                    generateButton.setEnabled(false);
                    hrirFileBrowse.setEnabled(false);
                    sampleFileBrowse.setEnabled(false);

                    ww.setHrtfManager(hrtfManager);
                    new Thread(ww).start();
                }
                else
                    System.out.println("Couldn't open HRIR file " + jTextField1.getText());
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton generateButton;
    private javax.swing.JButton hrirFileBrowse;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private binauralsound.HRTFView leftHRTFView;
    private javax.swing.JLabel leftLabel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private binauralsound.HRTFView rightHRTFView;
    private javax.swing.JLabel rightLabel;
    private javax.swing.JButton sampleFileBrowse;
    private javax.swing.JButton soundTestButton;
    private javax.swing.JButton startButton;
    // End of variables declaration//GEN-END:variables

    private JDialog aboutBox;
}
