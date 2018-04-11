package pipe.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import pipe.dataLayer.Arc;
import pipe.dataLayer.DataType;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class TransitionModifier extends JPanel {
	private Arc currentArc;
	private JTextField txtName;
	private JTextField txtVariable;
	private JTextField txtWeight;

	private String originalDataType, originalName, originalVariableValue;
	private int originalWeight;
	
	public TransitionModifier() {
		setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 400, 300);
		add(tabbedPane);
		
		JPanel transitionEditorPanelContainer = new JPanel();
		tabbedPane.addTab("Transition Editor", null, transitionEditorPanelContainer, null);
		transitionEditorPanelContainer.setLayout(null);
		
		JPanel transitionEditorPanel = new JPanel();
		transitionEditorPanel.setBounds(34, 11, 319, 234);
		transitionEditorPanelContainer.add(transitionEditorPanel);
		GridBagLayout gbl_transitionEditorPanel = new GridBagLayout();
		gbl_transitionEditorPanel.columnWidths = new int[]{136, 46, 0};
		gbl_transitionEditorPanel.rowHeights = new int[]{14, 0, 0};
		gbl_transitionEditorPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_transitionEditorPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		transitionEditorPanel.setLayout(gbl_transitionEditorPanel);
		
		JLabel lblNewLabel = new JLabel("Name:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridheight = 2;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		transitionEditorPanel.add(lblNewLabel, gbc_lblNewLabel);
		
	}
	
	public TransitionModifier(Arc anArc) {
		setLayout(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 400, 300);
		add(tabbedPane);

		JPanel ArcEditor = new JPanel();
		tabbedPane.addTab("Transition Editor", null, ArcEditor, null);
		ArcEditor.setLayout(null);

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 27, 375, 2);
		ArcEditor.add(separator);

		JLabel lblName = new JLabel("Name:");
		lblName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblName.setBounds(10, 27, 57, 23);
		ArcEditor.add(lblName);

		JLabel lblVariable_1 = new JLabel("Variable:");
		lblVariable_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblVariable_1.setBounds(10, 61, 57, 23);
		ArcEditor.add(lblVariable_1);

		txtName = new JTextField();
		txtName.setBounds(70, 30, 315, 20);
		ArcEditor.add(txtName);
		txtName.setColumns(10);

		txtVariable = new JTextField();
		txtVariable.setColumns(10);
		txtVariable.setBounds(70, 64, 77, 20);
		ArcEditor.add(txtVariable);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 141, 375, 2);
		ArcEditor.add(separator_1);

		JLabel lblArcHeaderName = new JLabel("ARC_NAME");
		lblArcHeaderName.setFont(new Font("Monospaced", Font.PLAIN, 14));
		lblArcHeaderName.setHorizontalAlignment(SwingConstants.CENTER);
		lblArcHeaderName.setBounds(10, 2, 375, 24);
		ArcEditor.add(lblArcHeaderName);

		txtWeight = new JTextField();
		txtWeight.setText((String) null);
		txtWeight.setColumns(10);
		txtWeight.setBounds(70, 157, 315, 20);
		ArcEditor.add(txtWeight);

		JLabel lblWeight = new JLabel("Weight:");
		lblWeight.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblWeight.setBounds(10, 154, 57, 23);
		ArcEditor.add(lblWeight);

		JButton btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		btnOK.setBounds(40, 198, 144, 36);
		ArcEditor.add(btnOK);

		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//lblDataType.setText(originalDataType);
				txtName.setText(originalName);
				txtVariable.setText(originalVariableValue);
				txtWeight.setText("" + originalWeight);
			}
		});
		btnReset.setBounds(229, 198, 144, 36);
		ArcEditor.add(btnReset);

		// HANDLE DATA
		currentArc = anArc;
		setName(txtName);
		setVariable(txtVariable);
		setWeight(txtWeight);
		setHeaderName(lblArcHeaderName);
		// END HANDLE DATA

	}

	/**
	 * Changes Data Type label
	 * 
	 * @param aDataType
	 */
	private void setDataType(JLabel aDataType) {
		DataType d = currentArc.getDataType();
		if (d.getDef()) {
			Vector<String> types = d.getTypes();
			String s;
			if (d.getPow())
				s = "P(< ";
			else
				s = "< ";
			for (int j = 0; j < types.size(); j++) {
				s += types.get(j);
				if (j < types.size() - 1) {
					s += " ,";
				}
			}
			if (d.getPow())
				s += " >)";
			else
				s += " >";
			aDataType.setText(s);

			originalDataType = s;

		}
	}

	private void setHeaderName(JLabel aHeaderName) {
		aHeaderName.setText(currentArc.getName());
	}

	/**
	 * Changes Name text field
	 * 
	 * @param anArc
	 */
	private void setName(JTextField aNameField) {
		originalName = currentArc.getName();
		aNameField.setText(currentArc.getName());

	}

	/**
	 * Changes Variable text field
	 * 
	 * @param aVariableField
	 */
	private void setVariable(JTextField aVariableField) {
		originalVariableValue = currentArc.getVar();
		aVariableField.setText(currentArc.getVar());
	}

	private void setWeight(JTextField aWeightField) {
		originalWeight = currentArc.getWeight();
		aWeightField.setText("" + currentArc.getWeight());
	}
}
