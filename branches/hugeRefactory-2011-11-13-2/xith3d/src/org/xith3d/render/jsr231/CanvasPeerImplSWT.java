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
package org.xith3d.render.jsr231;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.jagatoo.input.InputSystem;
import org.jagatoo.input.impl.swt.SWTInputDeviceFactory;
import org.jagatoo.logging.ProfileTimer;
import org.xith3d.picking.PickRequest;
import org.xith3d.render.RenderPass;
import org.xith3d.render.config.DisplayMode;
import org.xith3d.render.config.FSAA;
import org.xith3d.render.config.OpenGLLayer;
import org.xith3d.render.config.DisplayMode.FullscreenMode;
import org.xith3d.scenegraph.View;
import org.xith3d.utility.logging.X3DLog;
import org.xith3d.utility.platform.EnvironmentCapabilities;

/**
 * The CanvasPeer implementation for the official Java OpenGL Bindings (JOGL)
 * and SWT.
 * 
 * @author David Yazel [JOGL]
 * @author Marvin Froehlich (aka Qudus)
 * @author Amos Wenger (aka BlueSky)
 * @author Kevin Finley (aka horati)
 */
public class CanvasPeerImplSWT extends CanvasPeerImplBase
{
    private Display   display          = null;
    private GLContext context          = null;
    private GLCanvas  glCanvas         = null;
    private GL        gl               = null;
    private Composite owner            = null;
    //private boolean   fullscreen       = false;
    private boolean   destroyed        = false;
    private boolean   earlyLocation    = false;
    private boolean   earlySize        = false;
    private volatile boolean initialized   = false;
    
    private View view;
    private List<RenderPass> renderPasses;
    private boolean layeredMode;
    private long frameId;
    private long nanoTime;
    private long nanoStep;
    private PickRequest pickRequest = null;
    private Object pickResult = null;
    
    private boolean isClearOnlyMode = false;
    private boolean isRendering = false;
    
    private boolean activeInputReceiver = true;
    
    /**
     * This is only used if earlyShellAccess happened.
     */
    private URL iconURL = null;
    /**
     * This is only used if earlyShellAccess happened.
     */
    private String shellTitle = null;
    
    private Runnable renderRunnable = new Runnable()
    {
        public void run()
        {
            if ( !destroyed && !glCanvas.isDisposed() )
            {
                activeInputReceiver = ( !owner.isDisposed() && glCanvas.isFocusControl() );
                
                isRendering = true;
                
                EnvironmentCapabilities capabilities = EnvironmentCapabilities.getInstance();
                
                glCanvas.setCurrent();
                
                if ( capabilities.isIllegalMakeCurrentRequired() )
                {
                    context.makeCurrent();
                }
                gl = context.getGL();
                if ( swapIntervalChanged )
                {
                    swapIntervalChanged = false;
                    if ( capabilities.isVSyncSwitchingAllowed( OpenGLLayer.JOGL_SWT ) )
                    {
                        gl.setSwapInterval( getSwapInterval() );
                    }
                }
                if ( capabilities.isIllegalMakeCurrentRequired() )
                {
                    context.release();
                }
                
                display();
                
                glCanvas.swapBuffers();
                
                isRendering = false;
            }
        }
    };
    
    private int left = -1;
    private int top = -1;
    private int width;
    private int height;
    
    private SWTInputDeviceFactory inputDeviceFactory = null;
    
    public SWTInputDeviceFactory getInputDeviceFactory( InputSystem inputSystem )
    {
        if ( inputDeviceFactory == null )
        {
            inputDeviceFactory = new SWTInputDeviceFactory( this, inputSystem.getEventQueue() );
        }
        
        return ( inputDeviceFactory );
    }
    
    public final GLCanvas getDrawable()
    {
        ensureSWT();
        
        return ( glCanvas );
    }
    
    /**
     * {@inheritDoc}
     */
    public void refreshCursor( org.jagatoo.input.devices.Mouse mouse )
    {
        if ( !owner.isDisposed() )
        {
            if ( !mouse.isAbsolute() || ( getCursor() == null ) )
            {
                glCanvas.setCursor( null );
            }
            else
            {
                glCanvas.setCursor( new Cursor( display, SWT.CURSOR_ARROW ) );
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean receivesInputEvents()
    {
        /*
        display.syncExec( new Runnable()
        {
            public void run()
            {
                if ( !owner.isDisposed() && glCanvas.isFocusControl() )    
                    activeInputReceiver = true;
                else
                    activeInputReceiver = false;
            }   
        } );
        */
        
        return ( activeInputReceiver );
    }
    
    public CanvasPeerImplSWT( Object owner, DisplayMode displayMode, FullscreenMode fullscreen, boolean vsync, FSAA fsaa, int depthBufferSize )
    {
        super( displayMode, fullscreen, vsync, fsaa, depthBufferSize );
        
        if ( ( owner == null ) || !( owner instanceof Composite ) )
        {
            String message = "To construct an SWT Canvas, you must provide an owner of type org.eclipse.swt.Composite. " + "At present, this means that you need to run as an Eclipse plug-in or as an Eclipse Rich Client Platform " + "application.  In the future, we will provide XithRCP to simplify this process; however, it is fairly " + "easy to create a default RCP application using the Eclipse wizard 'New plug-in project' and selecting " + "'Yes' when asked 'Do you want to create a rich client application' on the 2nd page.";
            throw new IllegalArgumentException( message );
        }
        
        try
        {
            System.setProperty( "org.xith3d.render.jsr231.displayGLInfos", String.valueOf( false ) );
        }
        catch ( SecurityException ignore )
        {
            // Ignore a SecurityException for Applet deployment
        }
        
        this.owner = (Composite)owner;
        //this.fullscreen = fullscreen;
        this.width = getDisplayMode().getWidth();
        this.height = getDisplayMode().getHeight();
        
        setVSyncEnabled( vsync );
    }
    
    protected synchronized void initSWT()
    {
        /*
         * ensureSWT() is unsynchronized for speed.  Due to threading issues, it is possible that
         * we were !initialized during execution of ensureSWT() so check it again. 
         */
        if ( !initialized )
        {
            Runnable initializationRunnable = new Runnable()
            {
                public void run()
                {
                    if ( iconURL != null )
                    {
                        try
                        {
                            setIcon( iconURL );
                        }
                        catch ( IOException e )
                        {
                            e.printStackTrace();
                        }
                        iconURL = null;
                    }
                    if ( shellTitle != null )
                    {
                        setTitle( shellTitle );
                        shellTitle = null;
                    }
                    if ( earlyLocation )
                    {
                        setLocation( left, top );
                    }
                    if ( earlySize )
                    {
                        setSize( width, height );
                    }
                    GLData data = new GLData();
                    data.doubleBuffer = true;
                    data.stencilSize = 8;
                    //data.alphaSize = 8;
                    data.depthSize = getDepthBufferSize();
                    if ( getFSAA() != FSAA.OFF )
                    {
                        data.sampleBuffers = 1;
                        data.samples = getFSAA().getIntValue();
                    }
                    
                    glCanvas = new GLCanvas( owner, SWT.NONE, data );
                    glCanvas.addDisposeListener( new DisposeListener()
                    {
                        private boolean executed = false;
                        
                        public synchronized void widgetDisposed( DisposeEvent event )
                        {
                            /*
                             * There were rare cases of context being destroyed more than once.
                             */
                            if ( !executed )
                            {
                                context.destroy();
                                context = null;
                                executed = true;
                            }
                        }
                    } );
                    glCanvas.addControlListener( new ControlListener()
                    {
                        public void controlMoved( ControlEvent e )
                        {
                            // nothing to do
                        }
                        
                        public void controlResized( ControlEvent e )
                        {
                            final Point size = glCanvas.getSize();
                            setSize( size.x, size.y );
                        }
                    } );
                    glCanvas.setCurrent();
                    glCanvas.setFocus();
                    
                    context = GLDrawableFactory.getFactory().createExternalGLContext();
                    gl = context.getGL();
                    
                    initialized = true;
                    earlyLocation = false;
                    earlySize = false;
                    shellTitle = null;
                    iconURL = null;
                    init();
                }
            };
            
            display = owner.getDisplay();
            
            if ( display.getThread() == Thread.currentThread() )
            {
                initializationRunnable.run();
            }
            else
            {
                display.syncExec( initializationRunnable );
            }
        }
    }
    
    protected final void ensureSWT()
    {
        if ( destroyed )
        {
            throw new SWTException( "Canvas has already been destroyed" );
        }
        if ( !initialized )
        {
            initSWT();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public OpenGLLayer getType()
    {
        return ( OpenGLLayer.JOGL_SWT  );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setVSyncEnabled( boolean vsync )
    {
        super.setVSyncEnabled( vsync );
        
        setSwapInterval( vsync ? 1 : 0 );
    }
    
    @Override
    public final GL getGL()
    {
        ensureSWT();
        
        return ( gl );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Composite getWindow()
    {
        return ( owner );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final GLCanvas getComponent()
    {
        ensureSWT();
        
        return ( glCanvas );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setIcon( URL iconResource ) throws IOException
    {
        if ( owner == null )
        {
            this.iconURL = iconResource;
        }
        else if ( ( owner.getStyle() & SWT.ICON ) == SWT.ICON )
        {
            // FIXME: file/socket stream loading should never occur inside the rendering loop
            owner.getShell().setImage( new Image( display, iconResource.openStream() ) );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void setTitle( String title )
    {
        if ( owner == null )
        {
            this.shellTitle = title;
        }
        else if ( ( owner.getStyle() & SWT.TITLE ) == SWT.TITLE )
        {
            owner.getShell().setText( title );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String getTitle()
    {
        final boolean hasTitle = ( owner != null ) && ( ( owner.getStyle() & SWT.TITLE ) == SWT.TITLE );
        
        return ( hasTitle ? owner.getShell().getText() : null );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean setLocation( int x, int y )
    {
        if ( ( left == x ) && ( top == y ) )
        {
            return ( false );
        }
        
        if ( owner == null )
        {
            earlyLocation = true;
        }
        else
        {
            owner.setLocation( x, y );
        }
        
        this.left = x;
        this.top = y;
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int getLeft()
    {
        return ( left );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int getTop()
    {
        return ( top );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean setSize( int width, int height )
    {
        if ( ( this.width == width ) && ( this.height == height ) )
        {
            return ( false );
        }
        
        if ( owner == null )
        {
            earlySize = true;
        }
        else
        {
            owner.setSize( width, height );
        }
        
        this.width = width;
        this.height = height;
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int getWidth()
    {
        return ( width );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int getHeight()
    {
        return ( height );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean setDisplayModeImpl( DisplayMode displayMode )
    {
        //final boolean result = !displayMode.equals( getDisplayMode() );
        final boolean result = true;
        
        /*
        if ( result )
            displayModeChanged = true;
        */
        
        setSize( displayMode.getWidth(), displayMode.getHeight() );
        
        return ( result );
    }
    
    @Override
    protected void setAutoSwapBufferMode( boolean mode )
    {
        // FIXME
        //glCanvas.setAutoSwapBufferMode( mode );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isRendering()
    {
        return ( isRendering );
    }
    
    private final void display()
    {
        if ( isClearOnlyMode )
        {
            super.clear();
            return;
        }
        
        pickResult = doRender( view, renderPasses, layeredMode, frameId, nanoTime, nanoStep, pickRequest );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeThreadChanged()
    {
    }
    
    private final void doRender()
    {
        /*
         * Reset the ShapeAtomPeer's TransformGroup-id indicator to setup the
         * modelview matrix at least once per frame.
         */
        ShapeAtomPeer.reset();
        
        ensureSWT();
        
        if ( destroyed || display.isDisposed() )
        {
            /*
             * We have been called after the SWT framework has already
             * closed resources that we would otherwise need; therefore,
             * we cannot render anything without generating exceptions
             * so don't even try. 
             */
        }
        else
        {
            if ( Thread.currentThread() == display.getThread() )
            {
                renderRunnable.run();
            }
            else
            {
                display.syncExec( renderRunnable );
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object initRenderingImpl( View view, List< RenderPass > renderPasses, boolean layeredMode, long frameId, long nanoTime, long nanoStep, PickRequest pickRequest )
    {
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "CanvasPeerImpl::initRendering" );
        
        this.isClearOnlyMode = false;
        
        this.view = view;
        this.renderPasses = renderPasses;
        this.layeredMode = layeredMode;
        this.frameId = frameId;
        this.nanoTime = nanoTime;
        this.nanoStep = nanoStep;
        this.pickRequest = pickRequest;
        this.pickResult = null;
        
        doRender();
        
        Object result = this.pickResult;
        this.pickResult = null;
        
        ProfileTimer.endProfile();
        
        return ( result );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear()
    {
        isClearOnlyMode = true;
        doRender();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void destroy()
    {
        super.destroy();
        
        destroyed = true;
        // FIXME: NVIDIA drivers don't seem to like this
        //glCanvas.getContext().destroy();
        
        if ( ( display != null ) && ( !display.isDisposed() ) )
        {
            display.syncExec( new Runnable()
            {
                public void run()
                {
                    owner.dispose();
                }
            } );
            owner = null;
            display = null;
            glCanvas = null;
            gl = null;
        }
    }
}
