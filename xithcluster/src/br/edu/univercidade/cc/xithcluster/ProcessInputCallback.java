package br.edu.univercidade.cc.xithcluster;

import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.events.KeyPressedEvent;

public interface ProcessInputCallback {
	
	void keyPressed(KeyPressedEvent e, Key key);
	
}
