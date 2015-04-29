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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author mark
 * load a wave file and convert to double array (+/-1.0) for processing
 */

public class WaveBuffer
{
    public AudioFormat audioFormat;
    public double leftChannel[];
    public double rightChannel[];

    private String name;

    public WaveBuffer()
    {
        name = "null";
    }

    public void load(File file) throws UnsupportedAudioFileException, IOException
    {
        FileInputStream fis = new FileInputStream(file);
        load(fis);

        // get the name minus the file extension
        name = file.getName();
        int temp = name.lastIndexOf(".");

        if(temp>=0)
            name = name.substring(0, temp);

    }

    public void load(InputStream is) throws UnsupportedAudioFileException, IOException
    {
        
        InputStream bufferedIn = new BufferedInputStream(is);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
        this.audioFormat = audioInputStream.getFormat();
        audioFormat = audioInputStream.getFormat();

        System.out.println(audioFormat);

        // check audio format
        // flexible about signed/unsigned and 1/2 channels but nothing else
        if(
         (audioFormat.getEncoding()!=Encoding.PCM_SIGNED && audioFormat.getEncoding()!=Encoding.PCM_UNSIGNED)
         ||audioFormat.getChannels()>2
         ||(audioFormat.getSampleSizeInBits()!=8 && audioFormat.getSampleSizeInBits()!=16 && audioFormat.getSampleSizeInBits()!=24)
         ||audioFormat.getSampleRate()!=44100.0
         ||audioFormat.isBigEndian()==true)
        {
            UnsupportedAudioFileException e = new UnsupportedAudioFileException();
            throw(e);
        }

//        PCM_SIGNED 44100.0 Hz, 24 bit, stereo, 6 bytes/frame, little-endian

        readFile(audioInputStream);
    }

    private void readFile(AudioInputStream audioInputStream) throws IOException
    {
//Oscillator o = new Oscillator(44100);
//o.SetFrequency(5000);

        int sampleLength = (int)audioInputStream.getFrameLength();
        byte buffer[];
        int bufferLength = sampleLength * audioFormat.getFrameSize();

        double scale=32767.0;

        switch(audioFormat.getSampleSizeInBits())
        {
        case 8:
            scale=127.0;
            break;
        case 16:
            scale=32767.0;
            break;
        case 24:
            scale=8388607.0;
            break;
        }

        // read the file into the buffer
        buffer = new byte[bufferLength];
        int length = audioInputStream.read(buffer);

        // prepare the buffers
        if(audioFormat.getChannels()>=1)
            leftChannel = new double[sampleLength];

        if(audioFormat.getChannels()>=2)
            rightChannel = new double[sampleLength];

        // now convert the buffer to double
        int n;
        int ptr;

        int channelFrameSize = audioFormat.getFrameSize()/audioFormat.getChannels();

        for(n=0,ptr=0;n<sampleLength;n++,ptr+=audioFormat.getFrameSize())
        {
            int value;

  //                          Complex c = o.GetValue();


            if(audioFormat.getChannels()>=1)
            {
                value = readSample(buffer, ptr, audioFormat.getSampleSizeInBits());

//                if(audioFormat.getEncoding()==Encoding.PCM_UNSIGNED)
//                    value-=32767;

                leftChannel[n]=((double)value)/scale;


if(leftChannel[n]>1.0 || leftChannel[n]<-1.0)
    System.out.println("Left channel clip "+leftChannel[n]);


    //            leftChannel[n]*=c.Re();

            }

            if(audioFormat.getChannels()>=2)
            {
                value = readSample(buffer, ptr+channelFrameSize, audioFormat.getSampleSizeInBits());

//                if(audioFormat.getEncoding()==Encoding.PCM_UNSIGNED)
//                    value-=32767;

                rightChannel[n]=((double)value)/scale;

if(rightChannel[n]>1.0 || rightChannel[n]<-1.0)
    System.out.println("Right channel clip "+rightChannel[n]);
      //          rightChannel[n]*=c.Re();

            }
        }
    }

    private int readSample(byte[] buffer, int offset, int bits)
    {
        int result=0;

        switch(bits)
        {
        case 8:
            result = (int)buffer[offset];
            break;
        case 16:
            short svalue = (short)((buffer[offset+1]<<8)|(buffer[offset]&0xff));
            result = svalue;
            break;
        case 24:
//            int ivalue = (int)((buffer[offset+2]<<16)|(buffer[offset+1]<<8)|(buffer[offset]&0xff));
            int ivalue=0;
            
            if((buffer[offset+2]&0x80)==0x80)
            {
                ivalue=0xff;
                ivalue<<=8;
            }
            
            ivalue |= buffer[offset+2]&0xff;
            ivalue<<=8;
            ivalue |= buffer[offset+1]&0xff;
            ivalue<<=8;
            ivalue |= buffer[offset]&0xff;

            result = ivalue;

//System.out.println(String.format("%08x %08x %02x %02x %02x %d",ivalue,result,buffer[offset+2],buffer[offset+1],buffer[offset],ivalue));

            break;
        }

        return result;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

}
