package pipe.gui.widgets;

import java.awt.*;

import javax.swing.*;

import formula.parser.FontUtil;


public class FormulaPanel extends JPanel {
    private static final String[][] stLabelsAndButtons = new String[][] {
        new String[] {"Connective Symbols", "\u2227", "AND", "\u2228", "OR", "\u00AC", "Not", "\u2192", "Implies", "\u2194", "Equivalence"},
        new String[] {"Relational Symbols", "=", "Equals", "\u2260", "Not Equal", ">", "Greater than", "<", "Less than",
            "\u2265", "Greater than or equal to", "\u2264", "Less than or equal to"},
        new String[] {"Algebraic Symbols", "+", "Addition", "-", "Subtract", "*", "Multiply", "/", "Division", "%", "Modulus", "^", "Power"},
        new String[] {"Integral Symbols", "\u222B", "Integral", "\u2202", "Partial derivative", "\u03c4", "Built-in symbol denoting change of time"},
        new String[] {"Predicate Logic Symbols", "\u2200", "For all", "\u2203", "There exists", "\u2204", "There doesn't exist", "\u22C5", "Dot operator"},
        new String[] {"Set Symbols", "\u2208", "Belongs", "\u2209", "Doesn't belong", "\u222A", "Union", "\u2216", "Diff",
                                    "|", "", ":", "", "\u2205", ""},
        new String[] {"Parentheses", "(", "", ")", "", "{", "", "}", "", "[", "", "]", ""},
        new String[] {"Constants", "\u212F", "Math constant E", "\u03C0", "PI"}
    };

    private static final String[] stFunctions = new String[] {"concat(str...)", "sqrt(d)", "random()", "random(bound)", "random(lower, upper)",
        "sin(x)", "cos(x)", "tan(x)", "log(base, x)", "pow(x, p)", "exp(x)"};

    public JTextArea m_textArea;
    public Font m_font;
    //private Transition myTransition;
    private String mInitialFormula;

    public FormulaPanel(String pInitialFormula){
        super();
        mInitialFormula = pInitialFormula;
        initialize();
    }

    public FormulaPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        initialize();
    }

    public FormulaPanel(LayoutManager layout){
        super(layout);
        initialize();
    }

    public FormulaPanel(LayoutManager layout, boolean isDoubleBuffered){
        super(layout, isDoubleBuffered);
        initialize();
    }


    protected void initialize() {
        m_font = FontUtil.loadDefaultTLFont();
        Dimension size = new Dimension(700, 600);
        int lineHeight = getFontMetrics(m_font).getHeight()+20;

        setMinimumSize(size);
        setPreferredSize(size);
        setSize(size);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        setLayout(boxLayout);

        m_textArea = new JTextArea(mInitialFormula);
        m_textArea.setFont(m_font);
        m_textArea.setLineWrap(true);
        m_textArea.setSize(new Dimension(650, lineHeight*5));

        JScrollPane textScrollPane = new JScrollPane(m_textArea);
        textScrollPane.setAlignmentX(LEFT_ALIGNMENT);
        textScrollPane.setPreferredSize(new Dimension(660, lineHeight+6));
        textScrollPane.setBorder(BorderFactory.createTitledBorder("Formula"));
        textScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(textScrollPane);
        add(Box.createRigidArea(new Dimension(0, 10)));

        for (int i = 0; i<stLabelsAndButtons.length; i++) {
            String[] labelAndButtons = stLabelsAndButtons[i];
            Box box = Box.createHorizontalBox();
            box.setAlignmentX(LEFT_ALIGNMENT);

            JLabel label = new JLabel(labelAndButtons[0]);
            box.add(label);
            box.add(Box.createHorizontalGlue());
            for (int k = 1; k < labelAndButtons.length; k += 2) {
                box.add(createButton(labelAndButtons[k], labelAndButtons[k + 1]));
            }
            add(Box.createRigidArea(new Dimension(0, 5)));
            add(box);
        }
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(createFunctionsComboBox());
    }

    private Box createFunctionsComboBox() {
        JComboBox<String> functions = new JComboBox<>(stFunctions);
        functions.setFont(m_font);
        functions.setFocusable(false);
        functions.setMaximumSize(new Dimension(100,50));
        functions.addActionListener(e -> buttonPressed(functions.getSelectedItem().toString()));

        Box functionsBox = Box.createHorizontalBox();
        functionsBox.setAlignmentX(LEFT_ALIGNMENT);
        functionsBox.add(new JLabel("Functions"));
        functionsBox.add(Box.createHorizontalGlue());
        functionsBox.add(functions);

        return functionsBox;
    }

    private JButton createButton(final String pCode, final String pTooltip) {
        JButton button = new JButton(pCode);
        button.setFont(m_font);
        button.setToolTipText(pTooltip);
        button.setFocusable(false);
        button.addActionListener(e -> buttonPressed(pCode));
        return button;
    }

    private void buttonPressed(final String pCode){
        m_textArea.replaceSelection(pCode);
    }
}
