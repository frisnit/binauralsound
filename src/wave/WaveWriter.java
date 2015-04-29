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
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.Control.Type;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author mark
 */
public class WaveWriter implements Runnable
{
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
    String outputPath;

    HRTFManager hrtfManager;

    HRTFUpdateListener hrtfUpdateListener;

    public WaveWriter(String filename, String outpath)
    {
        this.filename=filename;
        hrtfUpdateListener=null;
        outputPath = outpath;
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

        System.out.println("Audio format=" + audioFormat);

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

/*
        if(!AudioSystem.isLineSupported(info))
        {
            System.out.println( "Play.playAudioStream does not handle this type of audio on this system." );
            return;
        }
*/
        try
        {
            SourceDataLine dataLine = (SourceDataLine) AudioSystem.getLine(info);
            dataLine.open(audioFormat);
            dataLine.start();

            System.out.println("Starting playback...");

            byte[] buffer = new byte[BUFFER_LENGTH * FRAME_SIZE];

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

        int max = hrtfManager.getNumberOfHrtfs();

        for(int h=0;h<max;h++)
        {
            // play a sound from a point
            playHRTF(h,wb,buffer);
        }

        hrtfUpdateListener.finished();
    }

    // play the sample convolved with the current HRTF
    private void playHRTF(int h, WaveBuffer wb, byte buffer[])
    {
        // send buffer to output
        int n=0;
        int ptr;
        int block=0;

        WaveBuffer hrtf = hrtfManager.getHrtf(h);

        if(hrtfUpdateListener!=null)
            hrtfUpdateListener.update(hrtf,hrtfManager.getHrtfName(h));

        String path = outputPath + "/" + hrtfManager.getHrtfName(h) + "_" + wb.getName() + ".wav";
        try
        {
            DataOutputStream outFile = new DataOutputStream(new FileOutputStream(path));

            int wavLength = wb.leftChannel.length+hrtf.leftChannel.length;

            wavLength*=FRAME_SIZE;

//System.out.println("wavLength " + wavLength + " bytes");


            writeWavHeader(outFile, wavLength);

            do
            {
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

//System.out.println("Wrote " + ptr + " bytes");

                // write to disk
                outFile.write(buffer,0,ptr);

                block++;

            }
            while(n<wb.leftChannel.length);

            outFile.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(WaveWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
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


    public boolean writeWavHeader(DataOutputStream outFile, int wavLength)
    {
        try
        {
            // write the wav file per the wav file format
            outFile.writeBytes("RIFF");					// 00 - RIFF
            outFile.write(intToByteArray(wavLength + 32), 0, 4);	// 04 - how big is the rest of this file?
            outFile.writeBytes("WAVE");					// 08 - WAVE
            outFile.writeBytes("fmt ");					// 12 - fmt
            outFile.write(intToByteArray((int)16), 0, 4);	// 16 - size of this chunk
            outFile.write(shortToByteArray((short)1), 0, 2);	// 20 - what is the audio format? 1 for PCM = Pulse Code Modulation
            outFile.write(shortToByteArray((short)CHANNELS), 0, 2);	// 22 - mono or stereo? 1 or 2?  (or 5 or ???)
            outFile.write(intToByteArray((int)SAMPLE_RATE), 0, 4);	// 24 - samples per second (numbers per second)
            outFile.write(intToByteArray((int)FRAME_SIZE*FRAME_RATE), 0, 4);	// 28 - bytes per second
            outFile.write(shortToByteArray((short)FRAME_SIZE), 0, 2);	// 32 - # of bytes in one sample, for all channels
            outFile.write(shortToByteArray((short)BITS_PER_SAMPLE), 0, 2);// 34 - how many bits in a sample(number)?  usually 16 or 24
            outFile.writeBytes("data");					// 36 - data
            outFile.write(intToByteArray((int)wavLength), 0, 4);	// 40 - how big is this data chunk
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

// ===========================
// CONVERT JAVA TYPES TO BYTES
// ===========================
    // returns a byte array of length 4
    private static byte[] intToByteArray(int i)
    {
            byte[] b = new byte[4];
            b[0] = (byte) (i & 0x00FF);
            b[1] = (byte) ((i >> 8) & 0x000000FF);
            b[2] = (byte) ((i >> 16) & 0x000000FF);
            b[3] = (byte) ((i >> 24) & 0x000000FF);
            return b;
    }

    // convert a short to a byte array
    public static byte[] shortToByteArray(short data)
    {
            return new byte[]{(byte)(data & 0xff),(byte)((data >>> 8) & 0xff)};
    }
}
