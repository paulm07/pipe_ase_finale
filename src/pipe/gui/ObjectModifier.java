package pipe.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;
import java.awt.CardLayout;
import javax.swing.JTabbedPane;

public class ObjectModifier extends JPanel {
	public ObjectModifier() {
		setBackground(Color.WHITE);
		setLayout(new CardLayout(0, 0));
		
		JLabel lblDefaultEditorLabel = new JLabel("SUIE Object Modifier");
		lblDefaultEditorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblDefaultEditorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblDefaultEditorLabel, "name_30463073190476");
		
		JLabel lblHeaderAccent = new JLabel("");
		lblHeaderAccent.setBackground(Color.DARK_GRAY);
		lblHeaderAccent.setOpaque(true);
		
		
		add(lblHeaderAccent, "name_30463091437946");
	}
}
