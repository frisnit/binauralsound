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

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author mark
 */
public class MessageBox
{
    static public final int DIALOG_YESNO    =0;
    static public final int DIALOG_OK       =1;
    static public final int DIALOG_OKCANCEL =2;

    static public final int SELECTION_OK       =0;
    static public final int SELECTION_CANCEL   =1;

    static public final int SELECTION_YES      =0;
    static public final int SELECTION_NO       =1;


    private String title;
    private String message;
    private int type;
    
    public MessageBox(String title, String message, int type)
    {
        this.message = message;
        this.type = type;
    }
    
    public int show()
    {
        JOptionPane pane = new JOptionPane(message);

        Object[] options;

        if(type==DIALOG_YESNO)
            options = new String[] { "Yes", "No" };
        else if(type==DIALOG_OKCANCEL)
            options = new String[] { "OK", "Cancel" };
        else
            options = new String[] { "OK" };

        pane.setOptions(options);
        JDialog dialog = pane.createDialog(new JFrame(), title);
        dialog.show();
        Object obj = pane.getValue();
        int result = -1;

        for (int k = 0; k < options.length; k++)
          if (options[k].equals(obj))
            result = k;

        return result;
    }

}
