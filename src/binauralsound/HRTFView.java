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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.text.DecimalFormat;

public class HRTFView extends javax.swing.JComponent 
{
  //protected DecimalFormat tflz, tf;

  private final static int DATA_LENGTH=400;

  double[] m_waveData;
  
  int m_ptr;

  Color colour;

  public HRTFView()
  {
      m_ptr=0;
      m_waveData = new double[DATA_LENGTH];
      colour = Color.red;
  }

  public void setColour(Color colour)
  {
      this.colour = colour;
  }

    public void dataReady(double[] data)
    {

//        double max=0, min=0;

        // resample data
        for(int n=0;n<DATA_LENGTH;n++)
        {
            m_waveData[n]=data[n*(data.length/DATA_LENGTH)];
/*
            if(m_waveData[n]>max)
                max=m_waveData[n];

            if(m_waveData[n]<min)
                min=m_waveData[n];
*/
        }

  //      System.out.println("Max="+max+" Min="+min);

        repaint();
    }
    
  public void paint(Graphics g)
  {
        Dimension size = getSize();

        g.setColor(Color.black);
        g.fillRect(0,0,size.width,size.height);

        if(m_waveData==null)
            return;

        int n;
        
        double scale=(double)size.height*2;
        int offset = size.height/2;
        int ptr,oldptr=0;
        double xScale = (double)DATA_LENGTH/(double)size.width;

        g.setColor(this.colour);
        for(n=1;n<size.width;n++)
        {
            // find offset into data
            ptr = (int)(xScale*(double)n);
            g.drawLine(n-1,offset-(int)(scale*m_waveData[oldptr]),n,offset-(int)(scale*m_waveData[ptr]));
            oldptr=ptr;
        }
  }

/*
  public Dimension getPreferredSize()
  {
    return new Dimension(100, 30);
  }
*/
  public Dimension getMinimumSize()
  {
    return new Dimension(50, 10);
  }
}