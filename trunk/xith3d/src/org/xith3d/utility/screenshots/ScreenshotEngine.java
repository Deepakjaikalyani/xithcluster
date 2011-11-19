/**
 * Copyright (c) 2003-2010, Xith3D Project Group all rights reserved.
 * 
 * Portions based on the Java3D interface, Copyright by Sun Microsystems.
 * Many thanks to the developers of Java3D and Sun Microsystems for their
 * innovation and design.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * Neither the name of the 'Xith3D Project Group' nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) A
 * RISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE
 */
package org.xith3d.utility.screenshots;

import java.io.File;

/**
 * A class implementing this interface is able to produce
 * screenshots of the current scene thread sefely.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface ScreenshotEngine
{
    /**
     * Takes a screenshot of the current rendering.
     * 
     * @param file the file to save the screenshot to
     * @param alpha with alpha channel?
     */
    public void takeScreenshot( File file, boolean alpha );
    
    /**
     * Takes a screenshot of the current rendering.
     * 
     * @param filenameBase the filenameBase to save the screenshot to (e.g. "screens/shot")
     *                     The current date and ".png" are appended.
     * @param alpha with alpha channel?
     * @return the file where the screenshot has been saved
     */
    public File takeScreenshot( String filenameBase, boolean alpha );
    
    /**
     * Takes a screenshot of the current rendering of the first added Canvas3D.
     * 
     * @param alpha with alpha channel?
     * @return the file where the screenshot has been saved
     */
    public File takeScreenshot( boolean alpha );
}
