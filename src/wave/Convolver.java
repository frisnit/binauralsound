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

/**
 *
 * @author mark
 */
public class Convolver
{

    // convolve the HRTF with the sample
    public static double convolve(double[] hrtf,double[] buffer,int offset)
    {
        double result = 0;
        double sum = 0;

        offset-=hrtf.length;

        // need to reverse the HRIR wrt the sound sample
        for(int n=hrtf.length-1;n>=0;n--,offset++)
        {
            if((offset>=0) && (offset<buffer.length))
                    result+=buffer[offset]*hrtf[n];

            sum+=hrtf[n];
        }

        if(result>1.0 || result<-1.0)
            System.out.println("Sum: "+ sum + " Overflow: "+result);

        return(result);
    }


}
