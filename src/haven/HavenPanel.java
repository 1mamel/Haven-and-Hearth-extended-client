package haven;

import java.awt.GraphicsConfiguration;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.*;
import java.util.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

public class HavenPanel extends GLCanvas implements Runnable {
    UI ui;
    boolean inited = false, rdr = false;
    int w, h;
    long fd = 20, fps = 0;
    int dth = 0, dtm = 0;
    public static int texhit = 0, texmiss = 0;
    Queue<InputEvent> events = new LinkedList<InputEvent>();
    public Coord mousepos = new Coord(0, 0);
    public Profile prof = new Profile(300);
    private Profile.Frame curf;
    private SyncFSM fsm = null;
    private static final GLCapabilities caps;
    static {
	caps = new GLCapabilities();
	caps.setDoubleBuffered(true);
	caps.setAlphaBits(8);
	caps.setRedBits(8);
	caps.setGreenBits(8);
	caps.setBlueBits(8);
    }
	
    public HavenPanel(int w, int h) {
	super(caps);
	setSize(this.w = w, this.h = h);
	initgl();
	setCursor(Toolkit.getDefaultToolkit().createCustomCursor(TexI.mkbuf(new Coord(1, 1)), new java.awt.Point(), ""));
    }
	
    private void initgl() {
	final Thread caller = Thread.currentThread();
	addGLEventListener(new GLEventListener() {
		public void display(GLAutoDrawable d) {
		    GL gl = d.getGL();
		    if(inited && rdr)
			redraw(gl);
		    TexGL.disposeall(gl);
		}
			
		public void init(GLAutoDrawable d) {
		    GL gl = d.getGL();
		    if(caller.getThreadGroup() instanceof haven.error.ErrorHandler) {
			haven.error.ErrorHandler h = (haven.error.ErrorHandler)caller.getThreadGroup();
			h.lsetprop("gl.vendor", gl.glGetString(gl.GL_VENDOR));
			h.lsetprop("gl.version", gl.glGetString(gl.GL_VERSION));
			h.lsetprop("gl.renderer", gl.glGetString(gl.GL_RENDERER));
			h.lsetprop("gl.exts", Arrays.asList(gl.glGetString(gl.GL_EXTENSIONS).split(" ")));
			h.lsetprop("gl.caps", d.getChosenGLCapabilities().toString());
		    }
		    gl.glColor3f(1, 1, 1);
		    gl.glPointSize(4);
		    gl.setSwapInterval(1);
		    gl.glEnable(GL.GL_BLEND);
		    //gl.glEnable(GL.GL_LINE_SMOOTH);
		    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		    GOut.checkerr(gl);
		}

		public void reshape(GLAutoDrawable d, int x, int y, int w, int h) {
		}
			
		public void displayChanged(GLAutoDrawable d, boolean cp1, boolean cp2) {}
	    });
    }
	
    public void init() {
	setFocusTraversalKeysEnabled(false);
	ui = new UI(new Coord(w, h), null);
	addKeyListener(new KeyAdapter() {
		public void keyTyped(KeyEvent e) {
		    checkfs();
		    synchronized(events) {
			events.add(e);
			events.notifyAll();
		    }
		}

		public void keyPressed(KeyEvent e) {
		    checkfs();
		    synchronized(events) {
			events.add(e);
			events.notifyAll();
		    }
		}
		public void keyReleased(KeyEvent e) {
		    checkfs();
		    synchronized(events) {
			events.add(e);
			events.notifyAll();
		    }
		}
	    });
	addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
		    checkfs();
		    synchronized(events) {
			events.add(e);
			events.notifyAll();
		    }
		}

		public void mouseReleased(MouseEvent e) {
		    checkfs();
		    synchronized(events) {
			events.add(e);
			events.notifyAll();
		    }
		}
	    });
	addMouseMotionListener(new MouseMotionListener() {
		public void mouseDragged(MouseEvent e) {
		    checkfs();
		    synchronized(events) {
			events.add(e);
		    }
		}

		public void mouseMoved(MouseEvent e) {
		    checkfs();
		    synchronized(events) {
			events.add(e);
		    }
		}
	    });
	addMouseWheelListener(new MouseWheelListener() {
		public void mouseWheelMoved(MouseWheelEvent e) {
		    checkfs();
		    synchronized(events) {
			events.add(e);
			events.notifyAll();
		    }
		}
	    });
	inited = true;
    }
	
    private class SyncFSM implements FSMan {
	private FSMan wrapped;
	private boolean tgt;
		
	private SyncFSM(FSMan wrapped) {
	    this.wrapped = wrapped;
	    tgt = wrapped.hasfs();
	}
		
	public void setfs() {
	    tgt = true;
	}
		
	public void setwnd() {
	    tgt = false;
	}
		
	public boolean hasfs() {
	    return(tgt);
	}
		
	private void check() {
	    synchronized(ui) {
		if(tgt && !wrapped.hasfs())
		    wrapped.setfs();
		if(!tgt && wrapped.hasfs())
		    wrapped.setwnd();
	    }
	}
    }

    private void checkfs() {
	if(fsm != null) {
	    fsm.check();
	}
    }

    public void setfsm(FSMan fsm) {
	this.fsm = new SyncFSM(fsm);
	ui.fsm = this.fsm;
    }
    
    UI newui(Session sess) {
	ui = new UI(new Coord(w, h), sess);
	ui.root.gprof = prof;
	ui.fsm = this.fsm;
	return(ui);
    }
	
    void redraw(GL gl) {
	ui.tooltip = null;
	GOut g = new GOut(gl, getContext(), new Coord(800, 600));

	gl.glMatrixMode(GL.GL_PROJECTION);
	gl.glLoadIdentity();
	gl.glOrtho(0, w, 0, h, -1, 1);
	TexRT.renderall(g);
	curf.tick("texrt");

	gl.glMatrixMode(GL.GL_PROJECTION);
	gl.glLoadIdentity();
	gl.glOrtho(0, w, h, 0, -1, 1);
	gl.glClearColor(0, 0, 0, 1);
	gl.glClear(GL.GL_COLOR_BUFFER_BIT);
	curf.tick("cls");
	synchronized(ui) {
	    ui.draw(g);
	}
	curf.tick("draw");

	if(Utils.getprop("haven.dbtext", "off").equals("on")) {
	    if(Resource.qdepth() > 0)
		g.atext(String.format("RQ depth: %d (%d)", Resource.qdepth(), Resource.numloaded()), new Coord(10, 470), 0, 1);
	    g.atext("FPS: " + fps, new Coord(10, 545), 0, 1);
	    g.atext("Texhit: " + dth, new Coord(10, 530), 0, 1);
	    g.atext("Texmiss: " + dtm, new Coord(10, 515), 0, 1);
	    Runtime rt = Runtime.getRuntime();
	    long free = rt.freeMemory(), total = rt.totalMemory();
	    g.atext(String.format("Mem: %,011d/%,011d/%,011d/%,011d", free, total - free, total, rt.maxMemory()), new Coord(10, 500), 0, 1);
	    g.atext(String.format("LCache: %d/%d", Layered.cache.size(), Layered.cache.cached()), new Coord(10, 485), 0, 1);
	}
        Object tooltip = ui.tooltip;
        if(tooltip == null)
            tooltip = ui.tooltipat(mousepos);
	Tex tt = null;
	if(tooltip != null) {
	    if(tooltip instanceof Text) {
		tt = ((Text)tooltip).tex();
	    } else if(tooltip instanceof Tex) {
		tt = (Tex)tooltip;
	    } else if(tooltip instanceof String) {
		if(((String)tooltip).length() > 0)
		    tt = (Text.render((String)tooltip)).tex();
	    }
	}
	if(tt != null) {
	    Coord sz = tt.sz();
	    Coord pos = mousepos.add(sz.inv());
	    g.chcolor(244, 247, 21, 192);
	    g.rect(pos.add(-3, -3), sz.add(6, 6));
	    g.chcolor(35, 35, 35, 192);
	    g.frect(pos.add(-2, -2), sz.add(4, 4));
	    g.chcolor();
	    g.image(tt, pos);
	}
	Resource curs = ui.root.getcurs(mousepos);
	if(!curs.loading) {
	    Coord dc = mousepos.add(curs.layer(Resource.negc).cc.inv());
	    g.image(curs.layer(Resource.imgc).tex(), dc);
	}
    }
	
    void dispatch() {
	synchronized(events) {
	    InputEvent e = null;
	    while((e = events.poll()) != null) {
		if(e instanceof MouseEvent) {
		    MouseEvent me = (MouseEvent)e;
		    if(me.getID() == MouseEvent.MOUSE_PRESSED) {
			ui.mousedown(me, new Coord(me.getX(), me.getY()), me.getButton());
		    } else if(me.getID() == MouseEvent.MOUSE_RELEASED) {
			ui.mouseup(me, new Coord(me.getX(), me.getY()), me.getButton());
		    } else if(me.getID() == MouseEvent.MOUSE_MOVED || me.getID() == MouseEvent.MOUSE_DRAGGED) {
			mousepos = new Coord(me.getX(), me.getY());
			ui.mousemove(me, mousepos);
		    } else if(me instanceof MouseWheelEvent) {
			ui.mousewheel(me, new Coord(me.getX(), me.getY()), ((MouseWheelEvent)me).getWheelRotation());
		    }
		} else if(e instanceof KeyEvent) {
		    KeyEvent ke = (KeyEvent)e;
		    if(ke.getID() == KeyEvent.KEY_PRESSED) {
			ui.keydown(ke);
		    } else if(ke.getID() == KeyEvent.KEY_RELEASED) {
			ui.keyup(ke);
		    } else if(ke.getID() == KeyEvent.KEY_TYPED) {
			ui.type(ke);
		    }
		}
		ui.lastevent = System.currentTimeMillis();
	    }
	}
    }
	
    public void uglyjoglhack() throws InterruptedException {
	try {
	    rdr = true;
	    display();
	} catch(GLException e) {
	    if(e.getCause() instanceof InterruptedException) {
		throw((InterruptedException)e.getCause());
	    } else {
		e.printStackTrace();
		throw(e);
	    }
	} finally {
	    rdr = false;
	}
    }
	
    public void run() {
	try {
	    long now, fthen, then;
	    int frames = 0;
	    fthen = System.currentTimeMillis();
	    while(true) {
		then = System.currentTimeMillis();
		curf = prof.new Frame();
		synchronized(ui) {
		    if(ui.sess != null)
			ui.sess.glob.oc.ctick();
		    dispatch();
		}
		curf.tick("dsp");
		uglyjoglhack();
		curf.tick("aux");
		frames++;
		now = System.currentTimeMillis();
		if(now - then < fd) {
		    synchronized(events) {
			events.wait(fd - (now - then));
		    }
		}
		curf.tick("wait");
		if(now - fthen > 1000) {
		    fps = frames;
		    frames = 0;
		    dth = texhit;
		    dtm = texmiss;
		    texhit = texmiss = 0;
		    fthen = now;
		}
		curf.fin();
		if(Thread.interrupted())
		    throw(new InterruptedException());
	    }
	} catch(InterruptedException e) {}
    }
	
    public GraphicsConfiguration getconf() {
	return(getGraphicsConfiguration());
    }
}
