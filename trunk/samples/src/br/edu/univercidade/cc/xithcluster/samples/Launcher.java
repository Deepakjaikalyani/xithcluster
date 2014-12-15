package br.edu.univercidade.cc.xithcluster.samples;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openmali.vecmath2.Tuple3f;

import br.edu.univercidade.cc.xithcluster.SampleApplication;

public class Launcher extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private String[] commandLineArguments;
	private JPanel panel;
	private JComboBox<String> samplesComboBox;
	private JButton okButton;

	public Launcher(String[] commandLineArguments) {
		super("Launcher");
		this.commandLineArguments = commandLineArguments;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		panel = new JPanel();
		panel.add(new JLabel("Sample: "));
		samplesComboBox = new JComboBox<String>(new String[] {
				"Earth and Moon", "Hanoi Tower", "Justice League" });
		okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		panel.add(samplesComboBox);
		panel.add(okButton);
		getContentPane().add(panel);
		pack();
	}
	
	private <T extends SampleApplication> void runSample(T sample)
	{
		setVisible(false);
		sample.init(commandLineArguments);		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")) {
			if (samplesComboBox.getSelectedIndex() == 0)
			{
				runSample(new EarthAndMoonSample(new Tuple3f(0.0f, 0.0f, 10.0f), new Tuple3f(0.0f, 0.0f, 0.0f)));
			}
			else if (samplesComboBox.getSelectedIndex() == 1)
			{
				runSample(new HanoiTowerSample());
			}
			else if (samplesComboBox.getSelectedIndex() == 2)
			{
				runSample(new JusticeLeagueSample());
			}
			else
			{
				// FIXME: checking invariants
				throw new AssertionError("Unknown sample");
			}
		}
	}

	public static void main(String[] args) {
		Launcher launcher = new Launcher(args);
		launcher.setVisible(true);
	}

}
