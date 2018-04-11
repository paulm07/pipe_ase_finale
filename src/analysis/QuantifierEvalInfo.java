package analysis;

import java.util.List;

/**
 * Created by Maks on 5/23/2016.
 */
public class QuantifierEvalInfo {
  public enum QuantifierType {
    FORALL, EXISTS, NEXISTS
  }

  private String mFunctionName;
  private String mDeciderVariable;
  private String mLoopVariable;
  private String mLoopVariableType;
  private String mSourceName;
  private QuantifierType mType;
  private String mCondition;
  private List<String> mNestedQuantifiers;

  public String getFunctionName() {
    return mFunctionName;
  }

  public String getDeciderVariable() {
    return mDeciderVariable;
  }

  public String getLoopVariableName() {
    return mLoopVariable;
  }

  public String getLoopVariableType() {
    return mLoopVariableType;
  }

  public String getSourceName() {
    return mSourceName;
  }

  public QuantifierType getType() {
    return mType;
  }

  public String getCondition() {
    return mCondition;
  }

  public List<String> getNestedQuantifiers() {
    return mNestedQuantifiers;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private QuantifierEvalInfo mInfo = new QuantifierEvalInfo();

    public Builder setFunctionName(String pFunctionName) {
      mInfo.mFunctionName = pFunctionName;
      return this;
    }

    public Builder setDeciderVariable(String pDeciderVariable) {
      mInfo.mDeciderVariable = pDeciderVariable;
      return this;
    }

    public Builder setLoopVariableName(String pLoopVariable) {
      mInfo.mLoopVariable = pLoopVariable;
      return this;
    }

    public Builder setSourceName(String pSourceName) {
      mInfo.mSourceName = pSourceName;
      return this;
    }

    public Builder setType(QuantifierType mType) {
      mInfo.mType = mType;
      return this;
    }

    public Builder setCondition(String mCondition) {
      mInfo.mCondition = mCondition;
      return this;
    }

    public QuantifierEvalInfo build() {
      return mInfo;
    }

    public Builder setNestedQuantifiers(final List<String> pNestedQuantifiers) {
      mInfo.mNestedQuantifiers = pNestedQuantifiers;
      return this;
    }

    public Builder setLoopVariableType(final String pLoopVariableType) {
      mInfo.mLoopVariableType = pLoopVariableType;
      return this;
    }
  }
}
