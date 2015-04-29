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
package dsp;

/**
 *
 * @author tyrrem
 */
public class Oscillator
{
    int m_sampleRate;
    double m_frequency;
    double m_realOsc;
    double m_imagOsc;
    double m_omega;
    double m_dr;
    double m_di;
    
    /** Creates a new instance of Oscillator */
    public Oscillator(int samplerate)
    {
    //    System.out.println("Creating oscillator");

        m_sampleRate = samplerate;
	/* initial vector at phase 0 */
	m_realOsc = 1.0;
	m_imagOsc = 0.0;
        
        m_frequency=0;
        m_omega=0;
        
        Create();
    }
    
    public void SetFrequency(double f)
    {
       // String s=String.format("Oscillator frequency %dHz (%s)",(int)f,this.toString());
       // System.out.println(s);
        
    	if(f<=0)
            return;

	if(f == m_frequency)
            return;

	m_frequency=f;
	m_omega = m_frequency*2.0*Math.PI;

	Create();
    }
    
    private void Create()
    {
	/* dr,di are used to rotate the vector */
	m_dr = Math.cos(m_omega/(double)m_sampleRate);
	m_di = Math.sin(m_omega/(double)m_sampleRate);
    }
    
    public Complex GetValue()
    {
        Complex result;
        double new_real,new_imag,mag_y;

        result = new Complex(m_realOsc,m_imagOsc);

	// update oscillator
	new_real = m_dr * m_realOsc - m_di * m_imagOsc;
	new_imag = m_dr * m_imagOsc + m_di * m_realOsc;

	// stabilise
	mag_y = Math.sqrt(new_real*new_real + new_imag*new_imag);
	new_real = new_real / mag_y;
	new_imag = new_imag / mag_y;

	m_realOsc = new_real;
	m_imagOsc = new_imag;

        return(result);
    }
}
