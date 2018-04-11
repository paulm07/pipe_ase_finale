package analysis;

import formula.parser.SetIteratorInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maks on 6/8/2016.
 */
public class SetDefinitionInfo {
  private String mFunctionName;
  private String mSetVariable;
  private String mPreCondition;
  private String mPostCondition;
  private String mTarget;
  private String mReceiveSequence;
  private String mSetVariableType;
  private String mSource;
  private List<SetIteratorInfo> mNestedSources = new ArrayList<>(3);

  public String getFunctionName() {
    return mFunctionName;
  }

  public String getSetVariable() {
    return mSetVariable;
  }

  public String getPreCondition() {
    return mPreCondition;
  }

  public String getPostCondition() {
    return mPostCondition;
  }

  public String getTarget() {
    return mTarget;
  }
  public String getSource() {
    return mSource;
  }

  public String getReceiveSequence() {
    return mReceiveSequence;
  }

  public String getSetVariableType() {
    return mSetVariableType;
  }

  public void setFunctionName(final String pFunctionName) {
    mFunctionName = pFunctionName;
  }

  public void setSetVariable(final String pSetVariable) {
    mSetVariable = pSetVariable;
  }

  public void setPreCondition(final String pPreCondition) {
    mPreCondition = pPreCondition;
  }

  public void setPostCondition(final String pPostCondition) {
    mPostCondition = pPostCondition;
  }

  public void setTarget(final String pTarget) {
    mTarget = pTarget;
  }

  public void setReceiveSequence(final String pReceiveSequence) {
    mReceiveSequence = pReceiveSequence;
  }

  public void setSetVariableType(final String pSetVariableType) {
    mSetVariableType = pSetVariableType;
  }

  public void setSource(final String pSource) {
    mSource = pSource;
  }

  public List<SetIteratorInfo> getNestedSources() {
    return mNestedSources;
  }

  public void addNestedSources(final List<SetIteratorInfo> pNestedSources) {
    mNestedSources.addAll(pNestedSources);
  }
}
