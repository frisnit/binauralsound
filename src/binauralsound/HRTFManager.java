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
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author mark
 */
public class HRTFManager
{
    private ArrayList<HRTFData> hrtfList;

    public HRTFManager()
    {
        hrtfList = new ArrayList<HRTFData>();
    }

    public WaveBuffer getHrtf(int index)
    {
        return hrtfList.get(index).getHRTF();
    }

    public String getHrtfName(int index)
    {
        return hrtfList.get(index).getName();
    }

    public int getNumberOfHrtfs()
    {
        return(hrtfList.size());
    }

    public boolean loadZip(String filename)
    {
        hrtfList.clear();

        // is it an IRCAM HRTF file?
        // load all the compensated files with the source placed horizontal to the listener
        if(loadHRTFs(filename, "^COMPENSATED/WAV/.*?_T([0-9]{3})_P000")>0)
//        if(loadHRTFs(filename, "^COMPENSATED/WAV/.*?_T([0-9]{3})_P([0-9]{3})")>0)
        {
            System.out.println("Loaded IRCAM file");
            return true;
        }
        else
        {
/*            if(loadHRTFs(filename, "^elev0/H0e([0-9]{3})a")>0)
            {
                System.out.println("Loaded MIT file");
                return true;
            }
            else
            {
*/                System.out.println("HRIR file not recognised");
//            }
        }
        return false;
    }


    private int loadHRTFs(String filename, String regex)
    {
        try
        {
            FileInputStream fis = new FileInputStream(filename);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ZipInputStream zis = new ZipInputStream(bis);

            ZipEntry entry;

            Pattern pattern = Pattern.compile(regex);


            while((entry = zis.getNextEntry()) != null)
            {
                Matcher matcher = pattern.matcher(entry.getName());
//COMPENSATED/WAV/IRC_1002_C/IRC_1002_C_R0195_T345_P000.wav
///home/mark/Development/Current/Software/BinauralSound/IRC_1002.zip

                if(matcher.find())
                {
                    // TODO: sort angles into order (they seem to be anyway)
                    int position = Integer.parseInt(matcher.group(1));
//                    int elevation = Integer.parseInt(matcher.group(2));

                    WaveBuffer wb = new WaveBuffer();
                    File file = new File(entry.getName());

                    try
                    {
//                        System.out.println("HRIR: number " + hrtfList.size() + " pos " + position + " elev " + elevation +  " name " + entry.getName());
                        wb.load(zis);
/*
                        for(int n=0;n<wb.leftChannel.length;n++)
                        {
                            if(n==0)
                            {
                                wb.leftChannel[n]=1.0;
                                wb.rightChannel[n]=1.0;
                            }
                            else
                            {
                                if(n==(wb.leftChannel.length-1))
                                {
                                    wb.leftChannel[n]=1.0;
                                    wb.rightChannel[n]=1.0;
                                }
                                else
                                {
                                    wb.leftChannel[n]=0.0;
                                    wb.rightChannel[n]=0.0;
                                }
                            }
                        }
*/
                        // normalise hrtf
                      //  this.normalise(wb.leftChannel);
                      //  this.normalise(wb.rightChannel);

                        hrtfList.add(new HRTFData(String.format("%03d", position),wb));
//                        hrtfList.add(new HRTFData(String.format("T%03d_P%03d", position,elevation),wb));

                    }
                    catch(UnsupportedAudioFileException e)
                    {
                        e.printStackTrace();
                    }
                }
//                else
//                {
//                    System.out.println("Found file: " + entry.getName());
//                }
            }
            zis.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return(hrtfList.size());
    }

    private void normalise(double[] wb)
    {
        double sum=0;

        for(int n=0;n<wb.length;n++)
            sum+=wb[n];

//        System.out.println("HRIR sum = " + sum);

        sum*=10.0;

        if(sum!=0.0)
        {
            for(int n=0;n<wb.length;n++)
                wb[n]/=sum;
        }

//        sum=0;
//        for(int n=0;n<wb.length;n++)
//            sum+=wb[n];
//
//        System.out.println("Normalised = "+sum);

    }
}

