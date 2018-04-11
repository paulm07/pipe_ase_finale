package formula;

import formula.absyntree.Sentence;
import formula.parser.ErrorMsg;
import formula.parser.Parse;
import org.apache.commons.lang.StringUtils;
import pipe.dataLayer.Transition;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:dalam004@fiu.edu">Dewan Moksedul Alam</a>
 * @author last modified by $Author: $
 * @version $Revision: $ $Date: $
 */
public class FormulaUtil {
  public static final String E = "\u212F";
  public static final String PI = "\u03C0";
  private static Pattern pattern = Pattern.compile("([\\u212F\\u03C0])");

  public static final Sentence parseFormula(final String pFormula, final ErrorMsg pErrorMsg) {
    Objects.requireNonNull(pFormula, "The given formula to parse is null");

    String replacedFormula = replaceConstants(pFormula);
    try {
      return new Parse(replacedFormula, pErrorMsg).absyn;
    } catch (Exception e) {
      if (!pErrorMsg.msgs.isEmpty()) {
        for(ErrorMsg.Msg msg : pErrorMsg.msgs) {
          System.out.println(String.format("Error at %d: %s", msg.pos, msg.msg));
        }
      }
      throw new RuntimeException(String.format("Error during parsing formula \"%s\"", replacedFormula), e);
    }
  }

  private static String replaceConstants(final String pFormula) {
    Matcher matcher = pattern.matcher(pFormula);
    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      String character = matcher.group(0);
      if (character.equals(E)) {
        matcher.appendReplacement(sb, "2.71828");
      }
      else if(character.equals(PI)) {
        matcher.appendReplacement(sb, "3.14159");
      }
    }
    matcher.appendTail(sb);

    return sb.toString();
  }

  public static final Sentence parseFormula(final Transition pTransition, final ErrorMsg pErrorMsg) {
    String formula = pTransition.getFormula();
    if (StringUtils.isBlank(formula)) {
      throw new IllegalArgumentException(String.format("Formula for transition %s is not defined", pTransition.getName()));
    }
    return parseFormula(formula, pErrorMsg);
  }

  public static void main(String[] args) {
    System.out.println(String.format("\"%s\"", replaceConstants("ASD\u212FFGH\u03c0\u03c0EWR")));
    System.out.println(String.format("\"%s\"",replaceConstants("ASDFGHEWR")));
  }
}
