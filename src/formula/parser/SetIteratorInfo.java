package formula.parser;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public final class SetIteratorInfo {
    private final String mVariable;
    private final String mVariableType;
    private final String mSource;

    public SetIteratorInfo(final String pVariable, final String pVariableType, final String pSource) {
        mVariable = pVariable;
        mSource = pSource;
        mVariableType = pVariableType;
    }

    public String getVariable() {
        return mVariable;
    }

    public String getVariableType() {
        return mVariableType;
    }

    public String getSource() {
        return mSource;
    }
}
