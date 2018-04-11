package analysis.spin;

import analysis.TranslationConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public class SpinTranslationConfig implements TranslationConfiguration {
  @JsonProperty("groups")
  private List<List<String>> mGroups = new ArrayList<>();

  @JsonProperty("globals")
  private List<String> mGlobals = new ArrayList<>();

  @JsonProperty("number")
  private String mNumberRepresentation = "byte";

  @JsonProperty("string")
  private String mStringRepresentation = "mtype";

  @JsonProperty("atomicity")
  private String mAtomicSequence = "d_step";
  private boolean mHybrid;

  public static Builder builder() {
    return new Builder();
  }

  public List<List<String>> getGroups() {
    return mGroups;
  }

  public List<String> getGlobals() {
    return mGlobals;
  }

  public String getNumberRepresentation() {
    return mNumberRepresentation;
  }

  public String getStringRepresentation() {
    return mStringRepresentation;
  }

  public String getAtomicSequence() {
    return mAtomicSequence;
  }

  public boolean isHybrid() {
    return mHybrid;
  }

  public void setHybrid(boolean mHybrid) {
    this.mHybrid = mHybrid;
  }

  public static class Builder {
    private final SpinTranslationConfig instance;
    private Builder() {
      instance = new SpinTranslationConfig();
    }

    public Builder havingGroupOf(final List<String> pTransitionGroup) {
      instance.mGroups.add(pTransitionGroup);
      return this;
    }

    public Builder configureAsGlobals(final List<String> pPlaces) {
      instance.mGlobals.addAll(pPlaces);
      return this;
    }

    public Builder numberAs(final String pNumberRepresentation) {
      instance.mNumberRepresentation = pNumberRepresentation;
      return this;
    }

    public Builder stringAs(final String pStringRepresentation) {
      instance.mStringRepresentation = pStringRepresentation;
      return this;
    }

    public Builder withAtomicSequence(final String pAtomicSequence) {
      instance.mAtomicSequence = pAtomicSequence;
      return this;
    }

    public Builder isHybrid(final boolean pIsHybrid) {
      instance.setHybrid(pIsHybrid);
      return this;
    }

    public SpinTranslationConfig build() {
      return instance;
    }
  }
}
