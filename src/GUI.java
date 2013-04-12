
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.BoxLayout;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;


public class GUI extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */

Harmonizer h;
	/**
	 * Create the frame.
	 */
	public GUI(final Harmonizer h) {
		this.h = h;
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 200);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		
		String[] tuneChoices = { "London Bridges", "Low Octave Tune", "Select own file..."};
		JComboBox<String> comboBox = new JComboBox<String>(tuneChoices);
		
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				 JComboBox cb = (JComboBox)e.getSource();
				String selected = (String) cb.getSelectedItem();
				
				
				if(cb.getSelectedIndex() == cb.getItemCount()-1){
					
					
					openFileChooser();
		
				}
				else{
					h.setOriginalTune((String) cb.getSelectedItem(), null);
				}
				
			}
		});
		
		
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.gridwidth = 4;
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 0;
		gbc_comboBox.gridy = 1;
		contentPane.add(comboBox, gbc_comboBox);
		
		JButton btnNewButton = new JButton("Play Original");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				h.playOriginal();
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridx = 10;
		gbc_btnNewButton.gridy = 0;
		contentPane.add(btnNewButton, gbc_btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Play Harmony");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
		
				h.justPlayHarmony();
			}
		});
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_1.gridx = 10;
		gbc_btnNewButton_1.gridy = 1;
		contentPane.add(btnNewButton_1, gbc_btnNewButton_1);
		
		JButton btnPlayHarmonized = new JButton("Play Harmonized");
		btnPlayHarmonized.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				h.playHarmonizedTune();
			}
		});
		GridBagConstraints gbc_btnPlayHarmonized = new GridBagConstraints();
		gbc_btnPlayHarmonized.insets = new Insets(0, 0, 5, 0);
		gbc_btnPlayHarmonized.gridx = 10;
		gbc_btnPlayHarmonized.gridy = 2;
		contentPane.add(btnPlayHarmonized, gbc_btnPlayHarmonized);
		
	
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				h.stopSong();
			}
		});
		GridBagConstraints gbc_btnStop = new GridBagConstraints();
		gbc_btnStop.insets = new Insets(0, 0, 5, 0);
		gbc_btnStop.gridx = 10;
		gbc_btnStop.gridy = 3;
		contentPane.add(btnStop, gbc_btnStop);
		
		setVisible(true);

	}
	
	
	public void openFileChooser(){
		File midiFile = null;
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
		    midiFile = fc.getSelectedFile();
		}
		
		h.setOriginalTune(midiFile.getName(), midiFile);
	}

}
