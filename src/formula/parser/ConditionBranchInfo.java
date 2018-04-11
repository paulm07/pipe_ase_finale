package formula.parser;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public final class ConditionBranchInfo {
  private final String mPrecondition;
  private final String mPostCondition;

  public ConditionBranchInfo(final String pPrecondition, final String pPostCondition) {
    mPrecondition = pPrecondition;
    mPostCondition = pPostCondition;
  }

  public String getPrecondition() {
    return mPrecondition;
  }

  public String getPostCondition() {
    return mPostCondition;
  }
}