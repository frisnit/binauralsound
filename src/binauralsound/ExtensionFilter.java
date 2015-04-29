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

import java.io.File;
import javax.swing.filechooser.*;


/**
 *
 * @author tyrelm01
 */
public class ExtensionFilter extends FileFilter
{

    String extension;
    String description;

    public ExtensionFilter(String extension, String description)
    {
        this.extension=extension.toLowerCase();
        this.description=description;
    }

    /*
     * Get the extension of a file.
     */
    public static String getExtension(File f)
    {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1)
        {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f)
    {
        if(f.isDirectory())
            return true;

        String ext = getExtension(f);

        if (ext != null)
        {
            if(extension.equals(ext))
                    return true;
            else
                return false;
        }

        return false;
    }

    //The description of this filter
    public String getDescription()
    {
        return description;
    }
}
