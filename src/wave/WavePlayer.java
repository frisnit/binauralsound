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
package wave;

import binauralsound.HRTFManager;
import binauralsound.HRTFUpdateListener;
import dsp.Complex;
import dsp.Oscillator;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author mark
 */
public class WavePlayer implements Runnable
{
    // modes
    public final static int TEST_MODE = 0;
    public final static int WAVE_MODE = 1;

    // audio format
    private final static int SAMPLE_RATE = 44100;
    private final static int BITS_PER_SAMPLE = 16;
    private final static int CHANNELS = 2;// stereo

    // derived constants
    private final static int BYTES_PER_SAMPLE = BITS_PER_SAMPLE/8;
    private final static int FRAME_SIZE = BYTES_PER_SAMPLE * CHANNELS;
    private final static int FRAME_RATE = SAMPLE_RATE;

    // system
    private final static int BUFFER_LENGTH = 44100;

    String filename;

    HRTFManager hrtfManager;

    Oscillator oscillator;
    int testToneCounter;

    int playMode;

    HRTFUpdateListener hrtfUpdateListener;

    public WavePlayer(int mode)
    {
        oscillator = new Oscillator(SAMPLE_RATE);
        oscillator.SetFrequency(1000);
        testToneCounter=0;
        hrtfUpdateListener=null;

        playMode = mode;
    }

    public WavePlayer(String filename)
    {
        oscillator = new Oscillator(SAMPLE_RATE);
        oscillator.SetFrequency(1000);
        testToneCounter=0;

        this.filename=filename;

        hrtfUpdateListener=null;

        playMode = this.WAVE_MODE;
    }

    public void setHrtfUpdateListener(HRTFUpdateListener hrtful)
    {
        hrtfUpdateListener=hrtful;
    }

    public void setHrtfManager(HRTFManager hrtfManager)
    {
        this.hrtfManager=hrtfManager;
    }

    public void run()
    {
        AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, SAMPLE_RATE, BITS_PER_SAMPLE, CHANNELS, FRAME_SIZE, FRAME_RATE, false);

        System.out.println("Play input audio format=" + audioFormat);

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        if(!AudioSystem.isLineSupported(info))
        {
            System.out.println( "Play.playAudioStream does not handle this type of audio on this system." );
            return;
        }

        try
        {
            SourceDataLine dataLine = (SourceDataLine) AudioSystem.getLine(info);
            dataLine.open(audioFormat);
            dataLine.start();

            System.out.println("Starting playback...");

            byte[] buffer = new byte[BUFFER_LENGTH * FRAME_SIZE];

            if(playMode==TEST_MODE)
                generateTestTone(dataLine,buffer);

            if(playMode==WAVE_MODE)
                convolveWave(dataLine,buffer);

            dataLine.drain();
            dataLine.stop();
            dataLine.close();

            System.out.println("Playback stopped.");
        }
        catch(LineUnavailableException e)
        {
            e.printStackTrace();
        }
    }

    private void convolveWave(SourceDataLine dataLine, byte[] buffer)
    {
        WaveBuffer wb = new WaveBuffer();

        File file = new File(filename);

        try
        {
           wb.load(file);
        }
        catch(UnsupportedAudioFileException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        if(wb.leftChannel==null)
            return;

/*
        Oscillator o = new Oscillator(44100);
        o.SetFrequency(1000);

        for(int n=0;n<wb.leftChannel.length;n++)
            wb.leftChannel[n]=o.GetValue().Re();
*/

        int max = hrtfManager.getNumberOfHrtfs();

        for(int h=0;h<max;h++)
//        for(int h=max/4;h<=max/4;h++)// just play hard left/hard right
        {

//            System.out.println("HRTF position " + hrtfManager.getHrtfName(h) + " degrees (" + h + " of " + max + ")");

            // play a sound from a point
            playHRTF(h,wb,buffer,dataLine);

            // play a sound from a point + 90 degrees
//            playHRTF((h+max/2)%max,wb,buffer,dataLine);
        }

        hrtfUpdateListener.finished();
    }

    private void playHRTF(int h, WaveBuffer wb, byte buffer[],SourceDataLine dataLine)
    {
        // send buffer to output
        int n=0;
        int ptr;
        int block=0;

        WaveBuffer hrtf = hrtfManager.getHrtf(h);

        if(hrtfUpdateListener!=null)
            hrtfUpdateListener.update(hrtf,hrtfManager.getHrtfName(h));

        do
        {
  //          int hrtfs = hrtfManager.getNumberOfHrtfs();

    //        System.out.println("HRTFS: "+hrtfs);

            // for each block
            for(ptr=0;ptr<buffer.length && n<(wb.leftChannel.length+hrtf.leftChannel.length); n++, ptr+=FRAME_SIZE)
            {
                short lvalue=0;
                short rvalue=0;

                // ignore right channel of sample (if any)

                double lResult = Convolver.convolve(hrtf.leftChannel,wb.leftChannel,n);
                double rResult = Convolver.convolve(hrtf.rightChannel,wb.leftChannel,n);

                lvalue = (short)(lResult*32768.0);
                rvalue = (short)(rResult*32768.0);

                // left channel, big endian
                buffer[ptr+1]=(byte)((lvalue>>8)&0xff);
                buffer[ptr]=(byte)(lvalue&0xff);

                // right channel, big endian
                buffer[ptr+3]=(byte)((rvalue>>8)&0xff);
                buffer[ptr+2]=(byte)(rvalue&0xff);
            }

/*
            if(ptr>=buffer.length)
                System.out.println("Buffer full");

            if(n>=wb.leftChannel.length)
                System.out.println("End of sample");
*/
            dataLine.write(buffer,0,ptr);

            block++;
//            System.out.println("Block " + String.valueOf(block));
        }
        while(n<wb.leftChannel.length);

        // block until finished
        dataLine.drain();

    }

    private void loadWave(SourceDataLine dataLine, byte[] buffer)
    {
        WaveBuffer wb = new WaveBuffer();
        
        File file = new File(filename);
        
        try
        {
           wb.load(file);        
        }
        catch(UnsupportedAudioFileException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        if(wb.leftChannel==null)
            return;
        
        // send buffer to output
        int n=0;
        int ptr;
        int block=0;
        do
        {
            for(ptr=0;ptr<buffer.length && n<wb.leftChannel.length; n++, ptr+=FRAME_SIZE)
            {
                short lvalue=0;
                short rvalue=0;
                
                lvalue = (short)(wb.leftChannel[n]*32767.0);

                if(wb.rightChannel!=null)
                    rvalue = (short)(wb.rightChannel[n]*32767.0);

                // left channel, big endian
                buffer[ptr+1]=(byte)((lvalue>>8)&0xff);
                buffer[ptr]=(byte)(lvalue&0xff);

//System.out.println(String.format("%08x %02x %02x",lvalue, buffer[ptr], buffer[ptr+1]));

                // right channel, big endian
                buffer[ptr+3]=(byte)((rvalue>>8)&0xff);
                buffer[ptr+2]=(byte)(rvalue&0xff);
            }

            if(ptr>=buffer.length)
                System.out.println("Buffer full");

            if(n>=wb.leftChannel.length)
                System.out.println("End of sample");

            block++;
            dataLine.write(buffer,0,BUFFER_LENGTH * FRAME_SIZE);
            System.out.println("Block " + String.valueOf(block));
        }
        while(n<wb.leftChannel.length);
        
    }

    private void generateTestTone(SourceDataLine dataLine, byte[] buffer)
    {
        for(int n=0;n<3;n++)
        {
            testTone(buffer);
            dataLine.write(buffer,0,BUFFER_LENGTH * FRAME_SIZE);
        }
    }

    // fill buffer with 1000Hz test tone
    // alternating between left and right channels
    private void testTone(byte[] buffer)
    {
        Complex c;
        int lvalue;
        int rvalue;

        for(int n=0;n<buffer.length;n+=FRAME_SIZE)
        {
            c = oscillator.GetValue();

            lvalue = (int)(c.Re()*32767);

            rvalue = 0;
            if(testToneCounter>(SAMPLE_RATE/2))
            {
                rvalue=lvalue;
                lvalue=0;
            }

            if(testToneCounter>SAMPLE_RATE)
                testToneCounter=0;

            // left channel, little endian
            buffer[n+1]=(byte)((lvalue>>8)&0xff);
            buffer[n]=(byte)(lvalue&0xff);

            // right channel, big endian
            buffer[n+3]=(byte)((rvalue>>8)&0xff);
            buffer[n+2]=(byte)(rvalue&0xff);

            testToneCounter++;
        }
    }

}
